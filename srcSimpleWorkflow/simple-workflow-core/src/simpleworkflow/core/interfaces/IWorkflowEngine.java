package simpleworkflow.core.interfaces;

import simpleworkflow.core.WorkflowException;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;
import simpleworkflow.core.persistence.data.WfInstance;
import simpleworkflow.core.persistence.data.WfStateEventResult;
import simpleworkflow.core.persistence.data.WfStateInstance;

/**
 * @author XingGu_Liu
 */
public interface IWorkflowEngine {

    public void init(IWorkflowPersistence workflowPersistence, IApplicationLoader applicationLoader,
                     IClassFinder classFinderForWorkflowCoreData, IClassFinder classFinderForStateData);

    public void destroy();

    public IWorkflowPersistence.IWorkflowQueryService getWorkflowQueryService();

    /**
     * Create a workflow
     * @param user
     * @param workflowName
     * @return
     * @throws WorkflowException
     */
    public WfInstance createWorkflow(
            String user, String workflowName,
            Object stateData
            ) throws WorkflowException;

    /**
     * Create a workflow which is invoked from activity of parent flow
     * @param user
     * @param workflowName
     * @param parentWorkflowId
     * @param parentWorkflowStateId
     * @param parentWorkflowStateEvent
     * @return
     * @throws WorkflowException
     */
    public WfInstance createWorkflow(
            String user, String workflowName,
            Object stateData,
            String parentWorkflowId, String parentWorkflowStateId,
            String parentWorkflowStateEvent) throws WorkflowException;

    public WfStateEventResult triggerStateEvent(
            String user, String workflowId,
            String eventName
    ) throws WorkflowException;

    public void updateMetaWorkflow(Workflow data) throws WorkflowException;

    public State getMetaState(String workflowName, long workflowVersion, String stateName) throws WorkflowException;

    public void updateState(WfStateInstance stateInstance) throws WorkflowException;

    public Object getStateData(String stateId);

    public void updateStateData(String stateId, Object stateData);

}
