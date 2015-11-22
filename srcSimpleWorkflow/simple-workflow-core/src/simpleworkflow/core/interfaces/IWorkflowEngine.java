package simpleworkflow.core.interfaces;

import simpleworkflow.core.WorkflowException;
import simpleworkflow.core.meta.Workflow;
import simpleworkflow.core.persistence.data.WfInstance;
import simpleworkflow.core.persistence.data.WfStateEventResult;

/**
 * @author XingGu_Liu
 */
public interface IWorkflowEngine {

    /**
     *
     * @param workflowPersistence
     * @param applicationLoader
     * @param classFinderForWorkflowCoreData this classFinder should be capable to find class of all class under simpleworkflow.core
     * @param classFinderForStateData this classFinder should be capable to find class of all stateData and applications and others which referenced by them.
     */
    public void init(IWorkflowPersistence workflowPersistence, IApplicationLoader applicationLoader,
                     IClassFinder classFinderForWorkflowCoreData, IClassFinder classFinderForStateData);

    public void destroy();

    public String getEngineName();

    public IWorkflowPersistence.IWorkflowQueryService getWorkflowQueryService();

    /**
     * Create a workflow
     * @param user
     * @param workflowName
     * @param inData input data for the start state
     * @return
     * @throws WorkflowException
     */
    public WfInstance createWorkflow(
            String user, String workflowName, Object inData
            ) throws WorkflowException;

    /**
     * Create a sub workflow which is invoked from activity of parent flow.
     * If parentWorkflowId is null, then it is not a sub workflow just same as the method 'createWorkflow'.
     * @param user
     * @param workflowName
     * @param inData input data for the start state
     * @param parentWorkflowId
     * @param parentWorkflowStateId
     * @param parentWorkflowEventName
     * @return
     * @throws WorkflowException
     */
    public WfInstance createSubWorkflow(
            String user, String workflowName, Object inData,
            String parentWorkflowId, String parentWorkflowStateId, String parentWorkflowEventName
            ) throws WorkflowException;


    /**
     * Trigger an event of current state.
     * @param user
     * @param workflowId
     * @param eventName
     * @param eventData
     * @return
     * @throws WorkflowException
     */
    public WfStateEventResult triggerStateEvent(
            String user, String workflowId,
            String eventName, Object eventData
    ) throws WorkflowException;

    public void updateMetaWorkflow(Workflow data) throws WorkflowException;

    /**
     *
     * @param workflowId
     * @return
     */
    public Object getWorkflowCurrentStateData(String workflowId) throws WorkflowException;


}
