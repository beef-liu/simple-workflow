package simpleworkflow.engine;

import org.apache.log4j.Logger;
import simpleworkflow.core.WorkflowAccessNoAuthException;
import simpleworkflow.core.WorkflowEnums;
import simpleworkflow.core.WorkflowException;
import simpleworkflow.core.interfaces.IApplication;
import simpleworkflow.core.interfaces.IApplicationLoader;
import simpleworkflow.core.interfaces.IWorkflowEngine;
import simpleworkflow.core.interfaces.IWorkflowPersistence;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;
import simpleworkflow.core.persistence.WorkflowPersistenceException;
import simpleworkflow.core.persistence.data.WfInstance;
import simpleworkflow.core.persistence.data.WfStateEventResult;
import simpleworkflow.core.persistence.data.WfStateInstance;
import simpleworkflow.engine.util.WorkFlowUtil;

/**
 * @author XingGu_Liu
 */
public class WorkflowEngine implements IWorkflowEngine {
    private final static Logger logger = Logger.getLogger(WorkflowEngine.class);

    private IWorkflowPersistence _workflowPersistence;
    private IApplicationLoader _applicationLoader;

    @Override
    public void init(IWorkflowPersistence workflowPersistence, IApplicationLoader applicationLoader) {
        logger.info("WorkflowEngine init()");

        _workflowPersistence = workflowPersistence;
        _applicationLoader = applicationLoader;
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
    public WfInstance createWorkflow(
            String user, String workflowName,
            String parentWorkflowId, String parentWorkflowStateId, String parentWorkflowStateEvent) throws WorkflowException {
        long createTime = System.currentTimeMillis();

        String workflowId = _workflowPersistence.newDataId();
        long version = newWorkflowVersion();


        //get meta
        Workflow workflowMeta = _workflowPersistence.getWorkflowQueryService()
                .getMetaWorkflowOfLatestVersion(workflowName);

        //Get state meta
        String startStateName = workflowMeta.getStartState();
        State startStateMeta = WorkFlowUtil.getMetaState(workflowMeta, startStateName);

        //check state access authority
        checkRoleAccessAuthorization(workflowMeta, startStateMeta,
                workflowId, null, null, user);

        //init workflow instance
        WfInstance workflowInst = new WfInstance();
        workflowInst.setCreate_time(createTime);
        workflowInst.setWorkflow_status(WorkflowEnums.WorkflowStatus.Running.ordinal());
        workflowInst.setWorkflow_id(workflowId);
        if(parentWorkflowId != null && parentWorkflowId.length() > 0) {
            workflowInst.setParent_flow_id(parentWorkflowId);
            workflowInst.setParent_flow_state_id(parentWorkflowStateId);
            workflowInst.setParent_flow_state_event(parentWorkflowStateEvent);
        }
        workflowInst.setWorkflow_name(workflowName);
        workflowInst.setWorkflow_version(version);

        //init state



        return null;
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

    private WfStateInstance createStateInstance(WfInstance workflowInstance, State stateMeta, String user) throws WorkflowPersistenceException {
        WfStateInstance state = new WfStateInstance();

        state.setWorkflow_name(workflowInstance.getWorkflow_name());
        state.setCreate_time(workflowInstance.getCreate_time());
        state.setCreate_user(user);
        state.setState_id(_workflowPersistence.newDataId());
        state.setIn_state_data();

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

    protected Object executeApplication(
            Workflow workflowMeta, State stateMeta,
            String workflowId, String stateId,
            Object stateData,
            String user
    ) throws WorkflowException {
        IApplication app = _applicationLoader.createApplication(stateMeta.getAccessibleJudge());

        try {
            app.init(this, workflowMeta, stateMeta, workflowId, stateId, stateData, user);

            return _applicationLoader.executeApplication(stateMeta.getAccessibleJudge(), app);
        } finally {
            app.destroy();
        }
    }

    protected long newWorkflowVersion() {
        return System.currentTimeMillis();
    }

}
