package simpleworkflow.core.persistence;

/**
 * @author XingGu_Liu
 */
public class WorkflowInstance {
    private String _id;

    private String _name;

    /**
     * This workflow is a subflow if parent_flow_id is not 0;
     */
    private String _parent_flow_id = null;

    private String _parent_flow_state_id = null;

    /**
     * If this workflow is a subflow, then parent_flow_activity_name is the activity name where sub flow was created from.
     */
    private String _parent_flow_activity_name = null;

    private int _workflow_status;

    private String _current_state_id;

    private long _creation_time;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getParent_flow_id() {
        return _parent_flow_id;
    }

    public void setParent_flow_id(String parent_flow_id) {
        _parent_flow_id = parent_flow_id;
    }

    public String getParent_flow_state_id() {
        return _parent_flow_state_id;
    }

    public void setParent_flow_state_id(String parent_flow_state_id) {
        _parent_flow_state_id = parent_flow_state_id;
    }

    public String getParent_flow_activity_name() {
        return _parent_flow_activity_name;
    }

    public void setParent_flow_activity_name(String parent_flow_activity_name) {
        _parent_flow_activity_name = parent_flow_activity_name;
    }

    public int getWorkflow_status() {
        return _workflow_status;
    }

    public void setWorkflow_status(int workflow_status) {
        _workflow_status = workflow_status;
    }

    public String getCurrent_state_id() {
        return _current_state_id;
    }

    public void setCurrent_state_id(String current_state_id) {
        _current_state_id = current_state_id;
    }

    public long getCreation_time() {
        return _creation_time;
    }

    public void setCreation_time(long creation_time) {
        _creation_time = creation_time;
    }
}
