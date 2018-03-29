package simpleworkflow.core.interfaces;

import simpleworkflow.core.error.WorkflowPersistenceException;
import simpleworkflow.core.meta.Workflow;
import simpleworkflow.core.persistence.data.WfInstance;
import simpleworkflow.core.persistence.data.WfStateInstance;
import simpleworkflow.core.persistence.data.WfTraceInstance;
import simpleworkflow.core.persistence.data.WfTraceRecord;

import java.util.List;

/**
 * @author XingGu_Liu
 */
public interface IWorkflowPersistence {

    public static enum QueryFilterOfTimeSpan {
    	UpdateTime, CreateTime, UpdateTimeOrCreateTime
    }
    
    public static enum QueryFilterOfWorkflowStatus {
        Running,
        Terminated,
        Completed,
        All
    };    
	
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
         * @param name name of workflow
         * @return
         * @throws WorkflowPersistenceException
         */
        public List<Workflow> findMetaWorkflows(String name) throws WorkflowPersistenceException;

        /**
         * get workflow instance by workflow id
         * @param id id of workflow
         * @return
         * @throws WorkflowPersistenceException
         */
        public WfInstance getWorkflowInstance(String id) throws WorkflowPersistenceException;

        /**
         * get workflow trace instance which contains all of trace records by workflow id
         * @param id id of workflow
         * @return
         * @throws WorkflowPersistenceException
         */
        public WfTraceInstance getWorkflowTraceInstance(String id) throws WorkflowPersistenceException;

        /**
         * get state instance by stateId
         * @param stateId id of state instance
         * @return
         * @throws WorkflowPersistenceException
         */
        public WfStateInstance getStateInstance(String stateId) throws WorkflowPersistenceException;

        public WfStateInstance getCurrentStateInstance(String workflowId) throws WorkflowPersistenceException;

        /**
         * Find all state instances in order of trace records
         * @param workflowId
         * @return
         * @throws WorkflowPersistenceException
         */
        public List<WfStateInstance> findStateInstancesInTraceOrder(String workflowId) throws WorkflowPersistenceException;
        
        /**
         * Find WorkflowInstances sorted by create_time in descending
         * @param workflowName It will be ignored in finding if it is null.
         * @param startTime results are >= start time(UTC)
         * @param endTime results are < end time(UTC)
         * @return
         */
        
        /**
         * Find WorkflowInstances. 
         * Sorted by update_time in descending if filterOfTimeSpan is UpdateTimeOrCreateTime or UpdateTime, otherwise sorted by create_time. 
         * @param workflow_name It will be ignored if it is null.
         * @param create_user It will be ignored if it is null.
         * @param update_user It will be ignored if it is null.
         * @param filterOfTimeSpan
         * @param filterOfWorkflowStatus
         * @param startTime
         * @param endTime
         * @param current_state_name It will be ignored if it is null.
         * @return
         * @throws WorkflowPersistenceException
         */
        public List<WfInstance> findWorkflowInstatncesInTimeSpan(
        		long rowOffset, long rowPageSize, 
                String workflow_name,
                String create_user, String update_user,
                QueryFilterOfTimeSpan filterOfTimeSpan,
                QueryFilterOfWorkflowStatus filterOfWorkflowStatus,
                long startTime, long endTime,
                String current_state_name
                ) throws WorkflowPersistenceException;

        /**
         * Query count with logic same as findWorkflowInstatncesInTimeSpan
         * @param rowOffset
         * @param rowPageSize
         * @param workflow_name
         * @param create_user
         * @param update_user
         * @param filterOfTimeSpan
         * @param filterOfWorkflowStatus
         * @param startTime
         * @param endTime
         * @param current_state_name
         * @return
         * @throws WorkflowPersistenceException
         */
        public long countWorkflowInstatncesInTimeSpan(
                String workflow_name,
                String create_user, String update_user,
                QueryFilterOfTimeSpan filterOfTimeSpan,
                QueryFilterOfWorkflowStatus filterOfWorkflowStatus,
                long startTime, long endTime,
                String current_state_name
                ) throws WorkflowPersistenceException;
    }

    public static interface IWorkflowModifyService {
        /**
         * save workflow meta data
         * @param trans
         * @param data workflow meta data
         * @throws WorkflowPersistenceException
         */
        public void setMetaWorkflow(IPersistenceTransaction trans, Workflow data) throws WorkflowPersistenceException;

        /**
         * save workflow instance
         * @param trans
         * @param data workflow instance
         * @throws WorkflowPersistenceException
         */
        public void setWorkflowInstance(IPersistenceTransaction trans, WfInstance data) throws WorkflowPersistenceException;

        /**
         * add a trace record
         * @param trans
         * @param data workflow trace record
         * @throws WorkflowPersistenceException
         */
        public void addWorkflowTraceRecord(IPersistenceTransaction trans, WfTraceRecord data) throws WorkflowPersistenceException;

        /**
         * Save state instance
         * @param trans
         * @param data state instance
         * @throws WorkflowPersistenceException
         */
        public void setStateInstance(IPersistenceTransaction trans, WfStateInstance data) throws WorkflowPersistenceException;


        /**
         * update current_state_name, current_state_id of workflow instance
         * @param trans
         * @param workflowId
         * @param stateName
         * @param stateId
         * @throws WorkflowPersistenceException
         */
        public void setWorkflowCurrentState(
                IPersistenceTransaction trans,
                String workflowId,
                String stateName,
                String stateId,
                int workflowStatus,
                String updateUser,
                long updateTime
                ) throws WorkflowPersistenceException;
    }

}
