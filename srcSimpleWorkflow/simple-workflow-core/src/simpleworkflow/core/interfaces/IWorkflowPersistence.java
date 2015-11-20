package simpleworkflow.core.interfaces;

import simpleworkflow.core.WorkflowEnums;
import simpleworkflow.core.meta.Workflow;
import simpleworkflow.core.persistence.WorkflowPersistenceException;
import simpleworkflow.core.persistence.data.WfInstance;
import simpleworkflow.core.persistence.data.WfStateInstance;
import simpleworkflow.core.persistence.data.WfTraceInstance;
import simpleworkflow.core.persistence.data.WfTraceRecord;

import java.util.List;

/**
 * @author XingGu_Liu
 */
public interface IWorkflowPersistence {

    public void init(String configFilePath,
                     IClassFinder classFinderForWorkflowCoreData, IClassFinder classFinderForStateData);

    public void destroy();

    public IPersistenceTransaction createTransaction();

    public IWorkflowQueryService getWorkflowQueryService();

    public IWorkflowModifyService getWorkflowModifyService();

    /**
     * Generate new id for workflow id or state id, etc.
     * @return
     * @throws WorkflowPersistenceException
     */
    public String newDataId() throws WorkflowPersistenceException;



    public static interface IWorkflowQueryService {
        public Workflow getMetaWorkflow(String name, long version) throws WorkflowPersistenceException;
        public Workflow getMetaWorkflowOfLatestVersion(String name) throws WorkflowPersistenceException;

        /**
         * Find Workflows sorted by version in descending
         * @param name workflow name
         * @return
         * @throws WorkflowPersistenceException
         */
        public List<Workflow> findMetaWorkflows(String name) throws WorkflowPersistenceException;

        public WfInstance getWorkflowInstance(String id) throws WorkflowPersistenceException;

        public WfTraceInstance getWorkflowTraceInstance(String id) throws WorkflowPersistenceException;

        public WfStateInstance getStateInstance(String stateId) throws WorkflowPersistenceException;

        public WfStateInstance getCurrentStateInstance(String workflowId) throws WorkflowPersistenceException;

        /**
         * Find WorkflowInstances sorted by create_time in descending
         * @param workflowName It will be ignored in finding if it is null.
         * @param startTime results are >= start time(UTC)
         * @param endTime results are < end time(UTC)
         * @return
         */
        public List<WfInstance> findWorkflowInstatncesCreatedInTimeSpan(
                String workflowName, long startTime, long endTime);

        /**
         * Find WorkflowInstances sorted by create_time in descending
         * @param workflowName It will be ignored in finding if it is null.
         * @param startTime results are >= start time(UTC)
         * @param endTime results are < end time(UTC)
         * @param workflowStatus
         * @return
         */
        public List<WfInstance> findWorkflowInstatncesCreatedInTimeSpan(
                String workflowName, long startTime, long endTime,
                WorkflowEnums.WorkflowStatus workflowStatus);

        /**
         * Find WorkflowInstances sorted by update_time in descending
         * @param workflowName It will be ignored in finding if it is null.
         * @param startTime results are >= start time(UTC)
         * @param endTime results are < end time(UTC)
         * @return
         */
        public List<WfInstance> findWorkflowInstatncesUpdatedInTimeSpan(
                String workflowName, long startTime, long endTime);

        /**
         * Find WorkflowInstances sorted by update_time in descending
         * @param workflowName It will be ignored in finding if it is null.
         * @param startTime results are >= start time(UTC)
         * @param endTime results are < end time(UTC)
         * @param workflowStatus
         * @return
         */
        public List<WfInstance> findWorkflowInstatncesUpdatedInTimeSpan(
                String workflowName, long startTime, long endTime,
                WorkflowEnums.WorkflowStatus workflowStatus);
    }

    public static interface IWorkflowModifyService {
        public void setMetaWorkflow(IPersistenceTransaction trans, Workflow data) throws WorkflowPersistenceException;

        public void setWorkflowInstance(IPersistenceTransaction trans, WfInstance data) throws WorkflowPersistenceException;

        public void setWorkflowTraceInstance(IPersistenceTransaction trans, WfTraceInstance data) throws WorkflowPersistenceException;

        public void addWorkflowTraceRecord(IPersistenceTransaction trans, WfTraceRecord data) throws WorkflowPersistenceException;

        public void setStateInstance(IPersistenceTransaction trans, WfStateInstance data) throws WorkflowPersistenceException;

        public void setCurrentStateInstance(IPersistenceTransaction trans, WfStateInstance data) throws WorkflowPersistenceException;

        public void setCurrentState(IPersistenceTransaction trans, String stateId) throws WorkflowPersistenceException;
    }

}