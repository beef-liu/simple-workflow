package simpleworkflow.engine;

import java.beans.IntrospectionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import simpleworkflow.core.WorkflowAccessNoAuthException;
import simpleworkflow.core.WorkflowEnums;
import simpleworkflow.core.WorkflowException;
import simpleworkflow.core.error.WorkflowEventNotFoundException;
import simpleworkflow.core.error.WorkflowInstanceNotFoundException;
import simpleworkflow.core.error.WorkflowMetaNotFoundException;
import simpleworkflow.core.error.WorkflowPersistenceException;
import simpleworkflow.core.interfaces.IApplicationLoader;
import simpleworkflow.core.interfaces.IClassFinder;
import simpleworkflow.core.interfaces.IPersistenceTransaction;
import simpleworkflow.core.interfaces.IWorkflowEngine;
import simpleworkflow.core.interfaces.IWorkflowPersistence;
import simpleworkflow.core.meta.Activity;
import simpleworkflow.core.meta.Application;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.StateRouterRule;
import simpleworkflow.core.meta.Transition;
import simpleworkflow.core.meta.Workflow;
import simpleworkflow.core.persistence.data.WfInstance;
import simpleworkflow.core.persistence.data.WfStateEventResult;
import simpleworkflow.core.persistence.data.WfStateInstance;
import simpleworkflow.core.persistence.data.WfTraceRecord;
import simpleworkflow.engine.application.param.StateEventAppParams;
import simpleworkflow.engine.application.param.StateInitAppParams;
import simpleworkflow.engine.application.param.StateRouterRuleAppParams;
import simpleworkflow.engine.util.WorkflowUtil;
import MetoXML.XmlDeserializer;
import MetoXML.XmlReader;
import MetoXML.XmlSerializer;
import MetoXML.Base.XmlDocument;
import MetoXML.Base.XmlNode;
import MetoXML.Base.XmlParseException;
import MetoXML.Util.ClassFinder;

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

    	IPersistenceTransaction trans = _workflowPersistence.createTransaction();
    	try {
    		WfInstance workflowInst = createSubWorkflowImp(trans, user, workflowName, inData, parentWorkflowId, parentWorkflowStateId, parentWorkflowEventName);
            
            logger.info("createWorkflow() succeeded."
                    + " cost(ms):" + (System.currentTimeMillis() - beginTime)
                    + " workflowName:" + workflowName
                    + " workflowId:" + workflowInst.getWorkflow_id()
                    + " stateName:" + workflowInst.getCurrent_state_name()
                    + " stateId:" + workflowInst.getCurrent_state_id()
            );
            
            trans.commit();
            return workflowInst;
        } catch(Throwable e) {
        	trans.rollback();
        	
            logger.error(
                    "createWorkflow() failed."
                            + " cost(ms):" + (System.currentTimeMillis() - beginTime)
                            + " workflowName:" + workflowName,
                    e);
            
            throw wrapWorkflowException(e);
    	} finally {
    		trans.close();
    	}
    }
    
    private WfInstance createSubWorkflowImp(
    		IPersistenceTransaction trans,
            String user, String workflowName, Object inData,
            String parentWorkflowId, String parentWorkflowStateId, String parentWorkflowEventName) throws WorkflowException, IOException, IntrospectionException, IllegalAccessException, InvocationTargetException {
        //get meta
        Workflow workflowMeta = _workflowPersistence.getWorkflowQueryService()
                .getMetaWorkflowOfLatestVersion(workflowName);
        if(workflowMeta == null) {
        	throw new WorkflowMetaNotFoundException("Workflow not found. workflowName:" + workflowName);
        }

        //Get state meta
        String startStateName = workflowMeta.getStartState();
        State startStateMeta = WorkflowUtil.getMetaState(workflowMeta, startStateName);
        if(startStateMeta == null) {
        	throw new WorkflowMetaNotFoundException("State not found. stateName:" + startStateName);
        }

        //init workflow instance ---
        WfInstance workflowInst = new WfInstance();
        workflowInst.setWorkflow_id(_workflowPersistence.newDataId());
        workflowInst.setWorkflow_name(workflowName);
        workflowInst.setWorkflow_version(workflowMeta.getVersion());
        workflowInst.setWorkflow_status(WorkflowEnums.WorkflowStatus.Running.ordinal());
        workflowInst.setCurrent_state_name(startStateName);

        long curTime = System.currentTimeMillis();
        workflowInst.setCreate_user(user);
        workflowInst.setCreate_time(curTime);
//        workflowInst.setUpdate_user(user);
//        workflowInst.setUpdate_time(curTime);

        //parent flow
        if(!isEmpty(parentWorkflowId)) {
            workflowInst.setParent_flow_id(parentWorkflowId);
            workflowInst.setParent_flow_state_id(parentWorkflowStateId);
            workflowInst.setParent_flow_state_event(parentWorkflowEventName);
        }

        //save into persistence
        InitedStateInstance initedStateInst = initStateInstance(
        		trans,
                user, workflowMeta, workflowInst.getWorkflow_id(),
                startStateMeta,
                inData,
                parentWorkflowStateId
                );
    	
        //===>save instance of workFlow and state
        saveWorkflowStateOnFlowCreated(trans, workflowInst, initedStateInst.stateInstance);

        return workflowInst;
    }

    @Override
    public WfStateEventResult triggerStateEvent(
            String user, String workflowId,
            String eventName, Object eventData) throws WorkflowException {
    	long beginTime = System.currentTimeMillis();
        try {
            //get workFlowInstance
            WfInstance workflowInst = _workflowPersistence.getWorkflowQueryService().getWorkflowInstance(workflowId);
            if(workflowInst == null) {
            	throw new WorkflowInstanceNotFoundException("Workflow instance not found. workflowId:" + workflowId);
            }
            
            WfStateInstance currentStateInst = _workflowPersistence.getWorkflowQueryService()
                    .getStateInstance(workflowInst.getCurrent_state_id());
            if(currentStateInst == null) {
            	throw new WorkflowInstanceNotFoundException("State instance not found. stateId:" + workflowInst.getCurrent_state_id());
            }

            //get meta
            Workflow workflowMeta = _workflowPersistence.getWorkflowQueryService()
                    .getMetaWorkflow(workflowInst.getWorkflow_name(), workflowInst.getWorkflow_version());
            if(workflowMeta == null) {
            	throw new WorkflowMetaNotFoundException("Workflow not found. workflowName:" + workflowInst.getWorkflow_name());
            }

            //Get state meta
            State currentStateMeta = WorkflowUtil.getMetaState(
                    workflowMeta, currentStateInst.getState_name());
            if(currentStateMeta == null) {
            	throw new WorkflowMetaNotFoundException("State not found. stateName:" + currentStateInst.getState_name());
            }

            //stateData
            Object stateData = _xmlSerForStateData.xmlToObj(
                    currentStateInst.getState_data()
            );

            //get activity
            Transition transition = WorkflowUtil.getTransitionByEvent(currentStateMeta, eventName);
            if(transition == null) {
            	throw new WorkflowEventNotFoundException("Event not found. eventName:" + eventName);
            }
            
            Activity activity = transition.getActivity();

        	IPersistenceTransaction trans = _workflowPersistence.createTransaction();
        	try {
                //check state event authority
                checkStateEventAccessAuthorization(trans, user, workflowMeta, workflowId,
                        currentStateMeta, currentStateInst.getState_id(), stateData,
                        eventName, eventData,
                        transition);

                //---> execute application of activity
                if(activity.getApplication() != null
                        && !isEmpty(activity.getApplication().getRun_scheme())) {
                    stateData = executeApplicationOfStateEventActivity(
                    		trans,
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
                currentStateInst.setUpdate_time(System.currentTimeMillis());

                //if subflow or not
                WfStateEventResult eventResult;
                WfInstance subflowInst = null;
                if(activity.isSubflow()) {
                    //===> create subflow
                    subflowInst = createSubWorkflowImp(
                    		trans,
                            user, activity.getSubflowName(), stateData,
                            workflowId, currentStateInst.getState_id(), eventName);

                    //current state
                    currentStateInst.setTriggered_subflow_id(subflowInst.getWorkflow_id());

                    //save current state
                    //===> save current state
                    _workflowPersistence.getWorkflowModifyService().setStateInstance(
                            trans, currentStateInst);

                    
                    eventResult = new WfStateEventResult();

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
                } else {
                    logger.info("triggerStateEvent() succeeded." + " cost(ms):" + (System.currentTimeMillis() - beginTime)
                            + " workflowName:" + workflowMeta.getName()
                            + " workflowId:" + workflowId
                            + " stateId:" + currentStateInst.getState_id()
                            + " event:" + eventName
                    );
                    eventResult = handleStateRouter(
                    		trans,
                            user, workflowMeta, workflowInst,
                            currentStateMeta, currentStateInst, stateData,
                            eventName, eventData,
                            transition
                    );
                }
                
                
                trans.commit();
                return eventResult;
        	} catch(Throwable e) {
            	trans.rollback();
            	throw e;
            } finally {
            	trans.close();
        	}
        } catch(Throwable e) {
            logger.error(
                    "triggerStateEvent() failed"
                            + " cost(ms):" + (System.currentTimeMillis() - beginTime)
                            + " workflowId:" + workflowId
                            + " event:" + eventName
                    ,
                    e);
            throw wrapWorkflowException(e);
        }
    }

    private WfStateEventResult handleStateRouter(
    		IPersistenceTransaction trans,
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
            		trans,
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
        		trans,
                user, workflowMeta, workflowInst.getWorkflow_id(),
                nextStateMeta, 
                stateData,
                stateInst.getState_id()
                );

        stateInst.setTo_state_name(nextStateName);
        stateInst.setTo_state_id(nextStateInst.stateInstance.getState_id());

        if(nextStateMeta.getStateType() == WorkflowEnums.StateTypes.Terminated.ordinal()) {
            workflowInst.setWorkflow_status(WorkflowEnums.WorkflowStatus.Terminated.ordinal());
        } else if(nextStateMeta.getStateType() == WorkflowEnums.StateTypes.Completed.ordinal()) {
            workflowInst.setWorkflow_status(WorkflowEnums.WorkflowStatus.Completed.ordinal());
        }

        saveWorkflowStateOnMoveToNextState(
        		trans,
        		user,
                workflowInst, stateInst, nextStateInst.stateInstance);

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

            return handleStateRouter(
            		trans,
            		user, parentFlowMeta, parentFlowInst,
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
    public long updateMetaWorkflow(Workflow data) throws WorkflowException {
    	data.setEngineName(getEngineName());
        data.setVersion(newWorkflowVersion());

        IPersistenceTransaction trans = _workflowPersistence.createTransaction();
        try {
            _workflowPersistence.getWorkflowModifyService()
                    .setMetaWorkflow(trans, data);

            trans.commit();
            return data.getVersion();
        } catch (WorkflowPersistenceException e) {
            trans.rollback();
            throw e;
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
        	throw wrapWorkflowException(e);
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
            String user,
            WfInstance workflowInst,
            WfStateInstance currentStateInst, WfStateInstance nextStateInst
    ) throws WorkflowPersistenceException, IOException, IntrospectionException, IllegalAccessException, InvocationTargetException {
        workflowInst.setCurrent_state_id(nextStateInst.getState_id());
        workflowInst.setCurrent_state_name(nextStateInst.getState_name());
        //workflowInst.setCurrent_state_data(_xmlSerForStateData.objToXml(stateData));
        workflowInst.setUpdate_user(user);
        workflowInst.setUpdate_time(System.currentTimeMillis());

        //===> workflow
        _workflowPersistence.getWorkflowModifyService().setWorkflowCurrentState(trans,
                workflowInst.getWorkflow_id(),
                workflowInst.getCurrent_state_name(), workflowInst.getCurrent_state_id(),
                workflowInst.getWorkflow_status(),
                workflowInst.getUpdate_user(),
                workflowInst.getUpdate_time()
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
    		IPersistenceTransaction transaction,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, Object inData,
            String fromStateId
    ) throws WorkflowException, IOException, IntrospectionException, IllegalAccessException, InvocationTargetException {
        //check state access authority
        checkStateAccessAuthorization(
        		transaction,
                user, workflowMeta, workflowId, 
                stateMeta, null,
                fromStateId,
                inData);

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
        			transaction,
                    user, workflowMeta, workflowId,
                    stateMeta, state.getState_id(),
                    fromStateId,
                    inData,
                    stateMeta.getInitApp());

            state.setState_data(_xmlSerForStateData.objToXml(stateData));
            initedState.newStateData = stateData;
        } else {
            state.setState_data(state.getIn_data());
            initedState.newStateData = inData;
        }

        return initedState;
    }

    private void checkStateAccessAuthorization(
    		IPersistenceTransaction transaction,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, 
            String fromStateId,
            Object inData
    ) throws WorkflowException {
        if(stateMeta.getAccessibleCheck() != null
                && !isEmpty(stateMeta.getAccessibleCheck().getRun_scheme())) {
            Boolean accessible = (Boolean) executeApplicationOfStateAuthCheck(
            		transaction,
                    user, workflowMeta, workflowId, 
                    stateMeta, stateId,
                    fromStateId,
                    inData,
                    stateMeta.getAccessibleCheck()
            );
            if(!accessible) {
                throw new WorkflowAccessNoAuthException();
            }
        }
    }

    private void checkStateEventAccessAuthorization(
    		IPersistenceTransaction transaction,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, Object eventData,
            Transition transition
    ) throws WorkflowException {
        if(transition.getEvent().getAccessibleCheck() != null
                && !isEmpty(transition.getEvent().getAccessibleCheck().getRun_scheme())) {
            Boolean accessible = (Boolean) executeApplicationOfStateEventAuthCheck(
            		transaction,
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
    		IPersistenceTransaction transaction,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, 
            String fromStateId,
            Object inData,
            Application application
    ) throws WorkflowException {
        final StateInitAppParams appParams = new StateInitAppParams(
                this, transaction, 
                user,
                workflowMeta, workflowId,
                stateMeta, stateId,
                fromStateId,
                inData
        );
        return (Boolean) _applicationLoader.executeApplication(application, appParams);
    }

    private Boolean executeApplicationOfStateEventAuthCheck(
    		IPersistenceTransaction transaction,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, Object eventData,
            Application application
    ) throws WorkflowException {
        final StateEventAppParams appParams = new StateEventAppParams(
                this, transaction, 
                user,
                workflowMeta, workflowId,
                stateMeta, stateId, stateData,
                eventName, eventData
        );
        return (Boolean) _applicationLoader.executeApplication(application, appParams);
    }

    private Object executeApplicationOfStateInit(
    		IPersistenceTransaction transaction,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, 
            String fromStateId,
            Object inData,
            Application application
    ) throws WorkflowException {
        final StateInitAppParams appParams = new StateInitAppParams(
                this, transaction, 
                user,
                workflowMeta, workflowId,
                stateMeta, stateId,
                fromStateId,
                inData
        );
        return _applicationLoader.executeApplication(application, appParams);
    }

    private Boolean executeApplicationOfStateRouterRule(
    		IPersistenceTransaction transaction,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, Object eventData,
            String routerRuleName, String toState,
            Application application
    ) throws WorkflowException {
        final StateRouterRuleAppParams appParams = new StateRouterRuleAppParams(
                this, transaction, 
                user,
                workflowMeta, workflowId,
                stateMeta, stateId, stateData,
                eventName, eventData,
                routerRuleName, toState
        );
        return (Boolean) _applicationLoader.executeApplication(application, appParams);
    }

    private Object executeApplicationOfStateEventActivity(
    		IPersistenceTransaction transaction,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, Object eventData,
            Application application
    ) throws WorkflowException {
        final StateEventAppParams appParams = new StateEventAppParams(
                this, transaction,
                user,
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
    
    private static WorkflowException wrapWorkflowException(Throwable e) {
        if(WorkflowException.class.isAssignableFrom(e.getClass())) {
        	return (WorkflowException) e;
        } else {
            return new WorkflowException(e);
        }
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
