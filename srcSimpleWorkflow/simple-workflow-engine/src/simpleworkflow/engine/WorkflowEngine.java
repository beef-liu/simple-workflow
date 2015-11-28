package simpleworkflow.engine;

import MetoXML.Base.XmlDocument;
import MetoXML.Base.XmlNode;
import MetoXML.Base.XmlParseException;
import MetoXML.Util.ClassFinder;
import MetoXML.XmlDeserializer;
import MetoXML.XmlReader;
import MetoXML.XmlSerializer;
import org.apache.log4j.Logger;
import simpleworkflow.core.WorkflowAccessNoAuthException;
import simpleworkflow.core.WorkflowEnums;
import simpleworkflow.core.WorkflowException;
import simpleworkflow.core.interfaces.*;
import simpleworkflow.core.meta.*;
import simpleworkflow.core.persistence.WorkflowPersistenceException;
import simpleworkflow.core.persistence.data.WfInstance;
import simpleworkflow.core.persistence.data.WfStateEventResult;
import simpleworkflow.core.persistence.data.WfStateInstance;
import simpleworkflow.core.persistence.data.WfTraceRecord;
import simpleworkflow.engine.application.param.StateEventAppParams;
import simpleworkflow.engine.application.param.StateInitAppParams;
import simpleworkflow.engine.application.param.StateRouterRuleAppParams;
import simpleworkflow.engine.util.DataCopyUtil;
import simpleworkflow.engine.util.WorkflowUtil;

import java.beans.IntrospectionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author XingGu_Liu
 */
public class WorkflowEngine implements IWorkflowEngine {
    private final static Logger logger = Logger.getLogger(WorkflowEngine.class);

    private IWorkflowPersistence _workflowPersistence;
    private IApplicationLoader _applicationLoader;

    private ClassFinder _classFinderForWorkflowCoreData;
    private IClassFinder _iClassFinderForWorkflowCoreData;
    private ClassFinder _classFinderForStateData;
    private IClassFinder _iClassFinderForStateData;

    private MyXmlSerializer _xmlSerForWorkflowCoreData;
    private MyXmlSerializer _xmlSerForStateData;


    @Override
    public void init(
            IWorkflowPersistence workflowPersistence, IApplicationLoader applicationLoader,
            IClassFinder classFinderForWorkflowCoreData, IClassFinder classFinderForStateData) {
        logger.info("WorkflowEngine init()");

        _workflowPersistence = workflowPersistence;
        _applicationLoader = applicationLoader;

        {
            _iClassFinderForWorkflowCoreData = classFinderForWorkflowCoreData;
            _classFinderForWorkflowCoreData = new ClassFinder() {
                @Override
                public Class<?> findClass(String s) throws ClassNotFoundException {
                    return _iClassFinderForWorkflowCoreData.findClass(s);
                }
            };
        }

        {
            _iClassFinderForStateData = classFinderForStateData;
            _classFinderForStateData = new ClassFinder() {
                @Override
                public Class<?> findClass(String s) throws ClassNotFoundException {
                    return _iClassFinderForStateData.findClass(s);
                }
            };
        }

        _xmlSerForWorkflowCoreData = new MyXmlSerializer(_classFinderForWorkflowCoreData);
        _xmlSerForStateData = new MyXmlSerializer(_classFinderForStateData);
    }

    @Override
    public void destroy() {
        logger.info("WorkflowEngine destroy()");
    }

    @Override
    public String getEngineName() {
        return WorkflowEngine.class.getName();
    }

    @Override
    public IWorkflowPersistence.IWorkflowQueryService getWorkflowQueryService() {
        return _workflowPersistence.getWorkflowQueryService();
    }

    @Override
    public WfInstance createWorkflow(String user, String workflowName, Object inData) throws WorkflowException {
        return createSubWorkflow(user, workflowName, inData, null, null, null);
    }

    @Override
    public WfInstance createSubWorkflow(
            String user, String workflowName, Object inData,
            String parentWorkflowId, String parentWorkflowStateId, String parentWorkflowEventName) throws WorkflowException {
        long beginTime = System.currentTimeMillis();
        try {
            //get meta
            Workflow workflowMeta = _workflowPersistence.getWorkflowQueryService()
                    .getMetaWorkflowOfLatestVersion(workflowName);

            //Get state meta
            String startStateName = workflowMeta.getStartState();
            State startStateMeta = WorkflowUtil.getMetaState(workflowMeta, startStateName);

            //init workflow instance ---
            WfInstance workflowInst = new WfInstance();
            workflowInst.setWorkflow_id(_workflowPersistence.newDataId());
            workflowInst.setWorkflow_name(workflowName);
            workflowInst.setWorkflow_version(newWorkflowVersion());
            workflowInst.setWorkflow_status(WorkflowEnums.WorkflowStatus.Running.ordinal());
            workflowInst.setCurrent_state_name(startStateName);

            long curTime = System.currentTimeMillis();
            workflowInst.setCreate_time(curTime);
            workflowInst.setUpdate_time(curTime);

            //parent flow
            if(!isEmpty(parentWorkflowId)) {
                workflowInst.setParent_flow_id(parentWorkflowId);
                workflowInst.setParent_flow_state_id(parentWorkflowStateId);
                workflowInst.setParent_flow_state_event(parentWorkflowEventName);
            }

            //init state (create instance)
            InitedStateInstance initedStateInst = initStateInstance(
                    user, workflowMeta, workflowInst.getWorkflow_id(),
                    startStateMeta, inData
                    );

            //save into persistence
            IPersistenceTransaction trans = _workflowPersistence.createTransaction();
            try {
                //===>save instance of workFlow and state
                saveWorkflowStateOnFlowCreated(trans, workflowInst, initedStateInst.stateInstance);

                trans.commit();
            } catch (Throwable e) {
                trans.rollback();
                throw e;
            } finally {
                trans.close();
            }

            logger.info("createWorkflow() succeeded."
                    + " cost(ms):" + (System.currentTimeMillis() - beginTime)
                    + " workflowName:" + workflowName
                    + " workflowId:" + workflowInst.getWorkflow_id()
                    + " stateName:" + startStateName
                    + " stateId:" + initedStateInst.stateInstance.getState_id()
            );
            return workflowInst;
        } catch(Throwable e) {
            logger.error(
                    "createWorkflow() failed."
                            + " cost(ms):" + (System.currentTimeMillis() - beginTime)
                            + " workflowName:" + workflowName,
                    e);
            throw new WorkflowException(e);
        }
    }

    @Override
    public WfStateEventResult triggerStateEvent(
            String user, String workflowId,
            String eventName, Object eventData) throws WorkflowException {
        long beginTime = System.currentTimeMillis();
        try {
            long updateTime = System.currentTimeMillis();

            //get workFlowInstance
            WfInstance workflowInst = _workflowPersistence.getWorkflowQueryService().getWorkflowInstance(workflowId);
            WfStateInstance currentStateInst = _workflowPersistence.getWorkflowQueryService()
                    .getStateInstance(workflowInst.getCurrent_state_id());

            //get meta
            Workflow workflowMeta = _workflowPersistence.getWorkflowQueryService()
                    .getMetaWorkflow(workflowInst.getWorkflow_name(), workflowInst.getWorkflow_version());

            //Get state meta
            State currentStateMeta = WorkflowUtil.getMetaState(
                    workflowMeta, currentStateInst.getState_name());

            //stateData
            Object stateData = _xmlSerForStateData.xmlToObj(
                    currentStateInst.getState_data()
            );

            //get activity
            Transition transition = WorkflowUtil.getTransitionByEvent(currentStateMeta, eventName);
            Activity activity = transition.getActivity();

            //check state event authority
            checkStateEventAccessAuthorization(user, workflowMeta, workflowId,
                    currentStateMeta, currentStateInst.getState_id(), stateData,
                    eventName, eventData,
                    transition);

            //---> execute application of activity
            if(activity.getApplication() != null
                    && !isEmpty(activity.getApplication().getRun_scheme())) {
                stateData = executeApplicationOfStateEventActivity(
                        user, workflowMeta, workflowId,
                        currentStateMeta, currentStateInst.getState_id(), stateData,
                        eventName, eventData,
                        activity.getApplication());

                currentStateInst.setState_data(_xmlSerForStateData.objToXml(stateData));
            }

            //current state
            currentStateInst.setTriggered_event(eventName);
            currentStateInst.setTriggered_event_data(_xmlSerForStateData.objToXml(eventData));
            currentStateInst.setUpdate_user(user);
            currentStateInst.setUpdate_time(updateTime);

            //if subflow or not
            WfInstance subflowInst = null;
            if(activity.isSubflow()) {
                //===> create subflow
                subflowInst = createSubWorkflow(
                        user, activity.getSubflowName(), stateData,
                        workflowId, currentStateInst.getState_id(), eventName);

                //current state
                currentStateInst.setTriggered_subflow_id(subflowInst.getWorkflow_id());

                //save current state
                IPersistenceTransaction trans = _workflowPersistence.createTransaction();
                try {
                    //===> save current state
                    _workflowPersistence.getWorkflowModifyService().setStateInstance(
                            trans, currentStateInst);
                    trans.commit();
                } catch (Throwable e) {
                    trans.rollback();
                    throw e;
                } finally {
                    trans.close();
                }

                WfStateEventResult eventResult = new WfStateEventResult();

                eventResult.setTo_workflow_name(subflowInst.getWorkflow_name());
                eventResult.setTo_workflow_id(subflowInst.getWorkflow_id());
                eventResult.setTo_workflow_status(subflowInst.getWorkflow_status());
                eventResult.setTo_state_name(subflowInst.getCurrent_state_name());
                eventResult.setTo_state_id(subflowInst.getCurrent_state_id());

                logger.info("triggerStateEvent() succeeded." + " cost(ms):" + (System.currentTimeMillis() - beginTime)
                        + " workflowName:" + workflowMeta.getName()
                        + " workflowId:" + workflowId
                        + " stateId:" + currentStateInst.getState_id()
                        + " event:" + eventName
                        + " subflowName:" + subflowInst.getWorkflow_name()
                        + " subflowId:" + subflowInst.getWorkflow_id()
                );
                return eventResult;
            } else {
                logger.info("triggerStateEvent() succeeded." + " cost(ms):" + (System.currentTimeMillis() - beginTime)
                        + " workflowName:" + workflowMeta.getName()
                        + " workflowId:" + workflowId
                        + " stateId:" + currentStateInst.getState_id()
                        + " event:" + eventName
                );
                return handleStateRouter(
                        user, workflowMeta, workflowInst,
                        currentStateMeta, currentStateInst, stateData,
                        eventName, eventData,
                        transition
                );
            }
        } catch(Throwable e) {
            logger.error(
                    "triggerStateEvent() failed"
                            + " cost(ms):" + (System.currentTimeMillis() - beginTime)
                            + " workflowId:" + workflowId
                            + " event:" + eventName
                    ,
                    e);
            throw new WorkflowException(e);
        }
    }

    private WfStateEventResult handleStateRouter(
            String user,
            Workflow workflowMeta, WfInstance workflowInst,
            State stateMeta, WfStateInstance stateInst, Object stateData,
            String eventName, Object eventData,
            Transition transition
    ) throws WorkflowException, IOException, IntrospectionException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, XmlParseException, InstantiationException, NoSuchMethodException {
        //router state
        String nextStateName = null;
        for (StateRouterRule routerRule : transition.getStateRouter().getRouterRules()) {
            Boolean isRightRouter = executeApplicationOfStateRouterRule(
                    user, workflowMeta, workflowInst.getWorkflow_id(),
                    stateMeta, stateInst.getState_id(), stateData,
                    eventName, eventData,
                    routerRule.getName(), routerRule.getToState(),
                    routerRule.getBoolCondition()
            );
            if(isRightRouter) {
                nextStateName = routerRule.getToState();
                break;
            }
        }
        if(nextStateName == null) {
            throw new WorkflowException("No routerRule matched!");
        }

        //next state meta
        State nextStateMeta = WorkflowUtil.getMetaState(workflowMeta, nextStateName);
        InitedStateInstance nextStateInst = initStateInstance(
                user, workflowMeta, workflowInst.getWorkflow_id(),
                nextStateMeta, stateData);

        stateInst.setTo_state_name(nextStateName);
        stateInst.setTo_state_id(nextStateInst.stateInstance.getTo_state_id());

        if(nextStateMeta.getStateType() == WorkflowEnums.StateTypes.Terminated.ordinal()) {
            workflowInst.setWorkflow_status(WorkflowEnums.WorkflowStatus.Terminated.ordinal());
        } else if(nextStateMeta.getStateType() == WorkflowEnums.StateTypes.Completed.ordinal()) {
            workflowInst.setWorkflow_status(WorkflowEnums.WorkflowStatus.Completed.ordinal());
        }

        IPersistenceTransaction trans = _workflowPersistence.createTransaction();
        try {
            saveWorkflowStateOnMoveToNextState(trans,
                    workflowInst, stateInst, nextStateInst.stateInstance);
            trans.commit();
        } catch (Throwable e) {
            trans.rollback();
            throw new WorkflowException(e);
        } finally {
            trans.close();
        }

        //if this is subflow
        if((nextStateMeta.getStateType() == WorkflowEnums.StateTypes.Terminated.ordinal()
                || nextStateMeta.getStateType() == WorkflowEnums.StateTypes.Completed.ordinal())
                && !isEmpty(workflowInst.getParent_flow_id())
                ) {
            //return to parent workflow, and router
            WfInstance parentFlowInst = _workflowPersistence.getWorkflowQueryService()
                    .getWorkflowInstance(workflowInst.getParent_flow_id());
            Workflow parentFlowMeta = _workflowPersistence.getWorkflowQueryService()
                    .getMetaWorkflow(parentFlowInst.getWorkflow_name(), parentFlowInst.getWorkflow_version());
            WfStateInstance parentFlowStateInst = _workflowPersistence.getWorkflowQueryService()
                    .getStateInstance(workflowInst.getParent_flow_state_id());
            State parentStateMeta = WorkflowUtil.getMetaState(
                    parentFlowMeta, parentFlowStateInst.getState_name());
            Transition parentTransition = WorkflowUtil.getTransitionByEvent(
                    parentStateMeta, workflowInst.getParent_flow_state_event());

            return handleStateRouter(user, parentFlowMeta, parentFlowInst,
                    parentStateMeta, parentFlowStateInst, nextStateInst.newStateData,
                    workflowInst.getParent_flow_state_event(),
                    _xmlSerForStateData.xmlToObj(parentFlowStateInst.getTriggered_event_data()),
                    parentTransition
            );
        } else {
            WfStateEventResult eventResult = new WfStateEventResult();
            eventResult.setTo_workflow_name(workflowInst.getWorkflow_name());
            eventResult.setTo_workflow_id(workflowInst.getWorkflow_id());
            eventResult.setTo_workflow_status(workflowInst.getWorkflow_status());
            eventResult.setTo_state_name(nextStateMeta.getName());
            eventResult.setTo_state_id(nextStateInst.stateInstance.getState_id());

            return eventResult;
        }
    }

    @Override
    public void updateMetaWorkflow(Workflow data) throws WorkflowException {
        data.setVersion(newWorkflowVersion());

        IPersistenceTransaction trans = _workflowPersistence.createTransaction();
        try {
            _workflowPersistence.getWorkflowModifyService()
                    .setMetaWorkflow(trans, data);

            trans.commit();
        } catch (WorkflowPersistenceException e) {
            trans.rollback();
        } finally {
            trans.close();
        }
    }

    @Override
    public Object getWorkflowCurrentStateData(String workflowId) throws WorkflowException {
        try {
            WfStateInstance stateInst = _workflowPersistence.getWorkflowQueryService()
                    .getCurrentStateInstance(workflowId);
            return _xmlSerForStateData.xmlToObj(stateInst.getState_data());
        } catch (Throwable e) {
            throw new WorkflowException(e);
        }
    }


    private void saveWorkflowStateOnFlowCreated(
            IPersistenceTransaction trans,
            WfInstance workflowInst,
            WfStateInstance currentStateInst
    ) throws WorkflowPersistenceException, IOException, IntrospectionException, IllegalAccessException, InvocationTargetException {
        workflowInst.setCurrent_state_name(currentStateInst.getState_name());
        workflowInst.setCurrent_state_id(currentStateInst.getState_id());
        //workflowInst.setCurrent_state_data(_xmlSerForStateData.objToXml(stateData));

        _workflowPersistence.getWorkflowModifyService().setWorkflowInstance(trans, workflowInst);

        _workflowPersistence.getWorkflowModifyService().setStateInstance(trans, currentStateInst);

        saveTrace(trans, workflowInst, currentStateInst);
    }

    private void saveWorkflowStateOnMoveToNextState(
            IPersistenceTransaction trans,
            WfInstance workflowInst,
            WfStateInstance currentStateInst, WfStateInstance nextStateInst
    ) throws WorkflowPersistenceException, IOException, IntrospectionException, IllegalAccessException, InvocationTargetException {
        workflowInst.setCurrent_state_id(nextStateInst.getState_id());
        workflowInst.setCurrent_state_name(nextStateInst.getState_name());
        //workflowInst.setCurrent_state_data(_xmlSerForStateData.objToXml(stateData));

        //===> workflow
        _workflowPersistence.getWorkflowModifyService().setWorkflowCurrentState(trans,
                workflowInst.getWorkflow_id(),
                workflowInst.getCurrent_state_name(), workflowInst.getCurrent_state_id()
                );

        //===> state
        _workflowPersistence.getWorkflowModifyService()
                .setStateInstance(trans, currentStateInst);
        _workflowPersistence.getWorkflowModifyService()
                .setStateInstance(trans, nextStateInst);

        //===> trace
        saveTrace(trans, workflowInst, nextStateInst);
    }

    private void saveTrace(IPersistenceTransaction trans, WfInstance workflowInst, WfStateInstance currentStateInst) throws WorkflowPersistenceException {
        WfTraceRecord traceRecord = new WfTraceRecord();
        traceRecord.setWorkflow_id(workflowInst.getWorkflow_id());
        traceRecord.setState_id(currentStateInst.getState_id());
        traceRecord.setParent_workflow_id(workflowInst.getParent_flow_id());
        traceRecord.setTrace_seq(newTraceSeq());
        traceRecord.setCreate_time(System.currentTimeMillis());

        _workflowPersistence.getWorkflowModifyService()
                .addWorkflowTraceRecord(trans, traceRecord);
    }

    private InitedStateInstance initStateInstance(
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, Object inData
    ) throws WorkflowException, IOException, IntrospectionException, IllegalAccessException, InvocationTargetException {
        //check state access authority
        checkStateAccessAuthorization(
                user, workflowMeta, null, stateMeta, null, inData);

        WfStateInstance state = new WfStateInstance();

        state.setWorkflow_id(workflowId);
        state.setWorkflow_name(workflowMeta.getName());

        state.setState_id(_workflowPersistence.newDataId());
        state.setState_name(stateMeta.getName());

        state.setIn_data(_xmlSerForStateData.objToXml(inData));

        long curTime = System.currentTimeMillis();
        state.setCreate_time(curTime);
        state.setCreate_user(user);
        state.setUpdate_time(curTime);
        state.setUpdate_user(user);

        InitedStateInstance initedState = new InitedStateInstance();
        initedState.stateInstance = state;

        //init application
        if(stateMeta.getInitApp() != null
                && !isEmpty(stateMeta.getInitApp().getRun_scheme())) {
            Object stateData = executeApplicationOfStateInit(
                    user, workflowMeta, null,
                    stateMeta, null, inData,
                    stateMeta.getInitApp());

            state.setState_data(_xmlSerForStateData.objToXml(stateData));

            initedState.newStateData = stateData;
        }

        return initedState;
    }

    private void checkStateAccessAuthorization(
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object inData
    ) throws WorkflowException {
        if(stateMeta.getAccessibleCheck() != null
                && !isEmpty(stateMeta.getAccessibleCheck().getRun_scheme())) {
            Boolean accessible = (Boolean) executeApplicationOfStateAuthCheck(
                    user, workflowMeta, workflowId, stateMeta, stateId, inData,
                    stateMeta.getAccessibleCheck()
            );
            if(!accessible) {
                throw new WorkflowAccessNoAuthException();
            }
        }
    }

    private void checkStateEventAccessAuthorization(
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, Object eventData,
            Transition transition
    ) throws WorkflowException {
        if(transition.getEvent().getAccessibleCheck() != null
                && !isEmpty(transition.getEvent().getAccessibleCheck().getRun_scheme())) {
            Boolean accessible = (Boolean) executeApplicationOfStateEventAuthCheck(
                    user, workflowMeta, workflowId,
                    stateMeta, stateId, stateData,
                    eventName, eventData,
                    transition.getEvent().getAccessibleCheck()
            );
            if(!accessible) {
                throw new WorkflowAccessNoAuthException();
            }
        }
    }

    private Boolean executeApplicationOfStateAuthCheck(
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object inData,
            Application application
    ) throws WorkflowException {
        final StateInitAppParams appParams = new StateInitAppParams(
                this, user,
                workflowMeta, workflowId,
                stateMeta, stateId, inData
        );
        return (Boolean) _applicationLoader.executeApplication(application, appParams);
    }

    private Boolean executeApplicationOfStateEventAuthCheck(
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, Object eventData,
            Application application
    ) throws WorkflowException {
        final StateEventAppParams appParams = new StateEventAppParams(
                this, user,
                workflowMeta, workflowId,
                stateMeta, stateId, stateData,
                eventName, eventData
        );
        return (Boolean) _applicationLoader.executeApplication(application, appParams);
    }

    private Object executeApplicationOfStateInit(
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object inData,
            Application application
    ) throws WorkflowException {
        final StateInitAppParams appParams = new StateInitAppParams(
                this, user,
                workflowMeta, workflowId,
                stateMeta, stateId, inData
        );
        return _applicationLoader.executeApplication(application, appParams);
    }

    private Boolean executeApplicationOfStateRouterRule(
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, Object eventData,
            String routerRuleName, String toState,
            Application application
    ) throws WorkflowException {
        final StateRouterRuleAppParams appParams = new StateRouterRuleAppParams(
                this, user,
                workflowMeta, workflowId,
                stateMeta, stateId, stateData,
                eventName, eventData,
                routerRuleName, toState
        );
        return (Boolean) _applicationLoader.executeApplication(application, appParams);
    }

    private Object executeApplicationOfStateEventActivity(
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, Object eventData,
            Application application
    ) throws WorkflowException {
        final StateEventAppParams appParams = new StateEventAppParams(
                this, user,
                workflowMeta, workflowId,
                stateMeta, stateId, stateData,
                eventName, eventData
        );
        return _applicationLoader.executeApplication(application, appParams);
    }

    private long newWorkflowVersion() {
        return System.currentTimeMillis();
    }

    private long newTraceSeq() {
        return System.currentTimeMillis();
    }

    private static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    protected static class MyXmlSerializer {
        private final ClassFinder _classFinder;
        public MyXmlSerializer(ClassFinder classFinder) {
            _classFinder = classFinder;
        }

        public String objToXml(Object data) throws InvocationTargetException, IOException, IntrospectionException, IllegalAccessException {
            if(data == null) {
                return "";
            } else {
                return XmlSerializer.objectToString(data, data.getClass());
            }
        }

        public Object xmlToObj(String xml) throws IOException, XmlParseException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
            if(xml == null || xml.length() == 0) {
                return null;
            }

            XmlReader xmlReader = new XmlReader();
            XmlDocument xmlDoc = xmlReader.ReadXml(
                    new ByteArrayInputStream(xml.getBytes(XmlDeserializer.DefaultCharset)));
            XmlNode rootNode = xmlDoc.getRootNode();
            String className = rootNode.getName();

            Class<?> dataClass = _classFinder.findClass(className);

            XmlDeserializer xmlDes = new XmlDeserializer();
            return xmlDes.ConvertXmlNodeToObject(rootNode, dataClass, _classFinder);
        }

        public Object xmlToObj(String xml, Class<?> cls) throws IllegalAccessException, IOException, XmlParseException, InstantiationException, NoSuchMethodException, InvocationTargetException {
            return XmlDeserializer.stringToObject(xml, cls, _classFinder);
        }
    }

    private static class InitedStateInstance {
        public WfStateInstance stateInstance = null;
        public Object newStateData = null;
    }
}
