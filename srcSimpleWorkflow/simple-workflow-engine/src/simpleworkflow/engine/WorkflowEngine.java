package simpleworkflow.engine;

import MetoXML.Base.XmlDocument;
import MetoXML.Base.XmlNode;
import MetoXML.Base.XmlParseException;
import MetoXML.Util.ClassFinder;
import MetoXML.XmlDeserializer;
import MetoXML.XmlReader;
import MetoXML.XmlSerializer;
import com.salama.reflect.PreScanClassFinder;
import org.apache.log4j.Logger;
import simpleworkflow.core.WorkflowAccessNoAuthException;
import simpleworkflow.core.WorkflowEnums;
import simpleworkflow.core.WorkflowException;
import simpleworkflow.core.interfaces.*;
import simpleworkflow.core.interfaces.application.IStateAuthCheckApp;
import simpleworkflow.core.interfaces.application.IStateInitApp;
import simpleworkflow.core.interfaces.application.IStateRouterRuleApp;
import simpleworkflow.core.interfaces.application.IStateTransitionApp;
import simpleworkflow.core.meta.Application;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;
import simpleworkflow.core.persistence.WorkflowPersistenceException;
import simpleworkflow.core.persistence.data.WfInstance;
import simpleworkflow.core.persistence.data.WfStateEventResult;
import simpleworkflow.core.persistence.data.WfStateInstance;
import simpleworkflow.core.persistence.data.WfTraceRecord;
import simpleworkflow.engine.util.WorkFlowUtil;

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
    public IWorkflowPersistence.IWorkflowQueryService getWorkflowQueryService() {
        return _workflowPersistence.getWorkflowQueryService();
    }

    @Override
    public WfInstance createWorkflow(String user, String workflowName, Object stateData) throws WorkflowException {
        return createWorkflow(user, workflowName, stateData, null, null, null);
    }

    @Override
    public WfInstance createWorkflow(
            String user, String workflowName, Object stateData,
            String parentWorkflowId, String parentWorkflowStateId, String parentWorkflowStateEvent) throws WorkflowException {
        try {
            //get meta
            Workflow workflowMeta = _workflowPersistence.getWorkflowQueryService()
                    .getMetaWorkflowOfLatestVersion(workflowName);

            //Get state meta
            String startStateName = workflowMeta.getStartState();
            State startStateMeta = WorkFlowUtil.getMetaState(workflowMeta, startStateName);

            //check state access authority
            checkRoleAccessAuthorization(workflowMeta, startStateMeta,
                    null, null, stateData, user);

            //initiate id and some parameters
            String workflowId = _workflowPersistence.newDataId();
            String stateId = _workflowPersistence.newDataId();
            long version = newWorkflowVersion();
            long createTime = System.currentTimeMillis();

            //init activity
            if(startStateMeta.getInitActivity() != null) {
                stateData = executeApplication(
                        workflowMeta, startStateMeta,
                        workflowId, stateId,
                        stateData, user);
            }

            //init workflow instance ---
            WfInstance workflowInst = new WfInstance();
            workflowInst.setWorkflow_id(workflowId);
            workflowInst.setWorkflow_name(workflowName);
            workflowInst.setWorkflow_version(version);
            workflowInst.setWorkflow_status(WorkflowEnums.WorkflowStatus.Running.ordinal());
            workflowInst.setCurrent_state_id(stateId);
            workflowInst.setCreate_time(createTime);

            //parent flow
            if(parentWorkflowId != null && parentWorkflowId.length() > 0) {
                workflowInst.setParent_flow_id(parentWorkflowId);
                workflowInst.setParent_flow_state_id(parentWorkflowStateId);
                workflowInst.setParent_flow_state_event(parentWorkflowStateEvent);
            }

            //init state
            WfStateInstance stateInst = createStateInstance(
                    workflowId, workflowName, startStateName, stateId,
                    createTime, user, stateData);

            //save instance of workFlow
            saveWorkFlowStateOnFlowCreated(workflowMeta, workflowInst, startStateMeta, stateInst);

            return workflowInst;
        } catch(Throwable e) {
            logger.error("createWorkflow() failed", e);
            throw new WorkflowException(e);
        }
    }

    @Override
    public WfStateEventResult triggerStateEvent(String user, String workflowId, String eventName) throws WorkflowException {
        return null;
    }

    @Override
    public void updateMetaWorkflow(Workflow data) throws WorkflowException {

    }

    @Override
    public State getMetaState(String workflowName, long workflowVersion, String stateName) throws WorkflowException {
        return null;
    }

    @Override
    public void updateState(WfStateInstance stateInstance) throws WorkflowException {

    }

    @Override
    public Object getStateData(String stateId) {
        return null;
    }

    @Override
    public void updateStateData(String stateId, Object stateData) {

    }

    private void saveWorkFlowStateOnFlowCreated(
            Workflow workflowMeta, WfInstance workflowInst,
            State currentStateMeta, WfStateInstance currentStateInst
    ) throws WorkflowPersistenceException {
        workflowInst.setCurrent_state_id(currentStateInst.getState_id());

        _workflowPersistence.getWorkflowModifyService().setWorkflowInstance(workflowInst);

        _workflowPersistence.getWorkflowModifyService().setStateInstance(currentStateInst);

        saveTrace(workflowInst, currentStateInst);
    }

    private void saveTrace(WfInstance workflowInst, WfStateInstance currentStateInst) throws WorkflowPersistenceException {
        WfTraceRecord traceRecord = new WfTraceRecord();
        traceRecord.setWorkflow_id(workflowInst.getWorkflow_id());
        traceRecord.setState_id(currentStateInst.getState_id());
        traceRecord.setParent_workflow_id(workflowInst.getParent_flow_id());
        traceRecord.setTrace_seq(newTraceSeq());

        _workflowPersistence.getWorkflowModifyService().addWorkflowTraceRecord(traceRecord);
    }

    private WfStateInstance createStateInstance(
            String workflowId, String workflowName, String stateName,
            String stateId,
            long createTime, String user, Object stateData
    ) throws WorkflowPersistenceException, IOException, IntrospectionException, IllegalAccessException, InvocationTargetException {
        WfStateInstance state = new WfStateInstance();

        state.setWorkflow_id(workflowId);
        state.setWorkflow_name(workflowName);

        state.setState_id(stateId);
        state.setState_name(stateName);

        if(stateData != null) {
            state.setIn_state_data(_xmlSerForStateData.objToXml(stateData));
        }

        state.setCreate_time(createTime);
        state.setCreate_user(user);

        return state;
    }

    protected void checkRoleAccessAuthorization(
            Workflow workflowMeta, State stateMeta,
            String workflowId, String stateId,
            Object stateData,
            String user
    ) throws WorkflowException {
        Boolean accessible = (Boolean) executeApplication(
                workflowMeta, stateMeta, workflowId, stateId, stateData, user);
        if(!accessible) {
            throw new WorkflowAccessNoAuthException();
        }
    }

    protected Boolean executeApplicationOfStateAuthCheck(
            String user,
            Workflow workflowMeta, State stateMeta,
            String workflowId, String stateId,
            Object stateData,
            Application application
    ) throws WorkflowException {
        IApplication app = _applicationLoader.createApplication(application);

        try {
            app.init();

            return ((IStateAuthCheckApp)app).checkStateAccessible(this, user,
                    workflowMeta, workflowId, stateMeta, stateId, stateData);
        } finally {
            app.destroy();
        }
    }

    protected Boolean executeApplicationOfStateEventAuthCheck(
            String user,
            Workflow workflowMeta, State stateMeta,
            String workflowId, String stateId,
            Object stateData, String eventName,
            Application application
    ) throws WorkflowException {
        IApplication app = _applicationLoader.createApplication(application);

        try {
            app.init();

            return ((IStateAuthCheckApp)app).checkStateEventAccessible(this, user,
                    workflowMeta, workflowId, stateMeta, stateId, stateData,
                    eventName);
        } finally {
            app.destroy();
        }
    }

    protected Object executeApplicationOfStateInit(
            String user,
            Workflow workflowMeta, State stateMeta,
            String workflowId, String stateId,
            Object stateData,
            Application application
    ) throws WorkflowException {
        IApplication app = _applicationLoader.createApplication(application);

        try {
            app.init();

            return ((IStateInitApp)app).handleStateInit(this, user,
                    workflowMeta, workflowId, stateMeta, stateId, stateData);
        } finally {
            app.destroy();
        }
    }

    protected Object executeApplicationOfStateTransition(
            String user,
            Workflow workflowMeta, State stateMeta,
            String workflowId, String stateId,
            Object stateData,
            String eventName, Object eventData,
            Application application
    ) throws WorkflowException {
        IApplication app = _applicationLoader.createApplication(application);

        try {
            app.init();

            return ((IStateTransitionApp)app).handleStateEvent(this, user,
                    workflowMeta, workflowId, stateMeta, stateId, stateData,
                    eventName, eventData);
        } finally {
            app.destroy();
        }
    }

    protected Boolean executeApplicationOfStateTransition(
            String user,
            Workflow workflowMeta, State stateMeta,
            String workflowId, String stateId,
            Object stateData,
            String eventName, String routerRuleName,
            Application application
    ) throws WorkflowException {
        IApplication app = _applicationLoader.createApplication(application);

        try {
            app.init();

            return ((IStateRouterRuleApp)app).handleBoolCondition(this, user,
                    workflowMeta, workflowId, stateMeta, stateId, stateData,
                    eventName, routerRuleName);
        } finally {
            app.destroy();
        }
    }

    protected long newWorkflowVersion() {
        return System.currentTimeMillis();
    }

    protected long newTraceSeq() {
        return System.currentTimeMillis();
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
            return XmlDeserializer.stringToObject(xml, dataClass, _classFinder);
        }
    }


}
