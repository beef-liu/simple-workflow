package simpleworkflow.core.persistence.data;

/**
 * @author XingGu_Liu
 */
public class WfInstance {
    private String _workflow_id;

    private String _workflow_name;

    private long _workflow_version;

    private int _workflow_status;

    private String _current_state_name;
    private String _current_state_id;

    /**
     * This workflow is a subflow if parent_flow_id is not 0;
     */
    private String _parent_flow_id = null;

    private String _parent_flow_state_id = null;

    /**
     * If this workflow is a subflow, then _parent_flow_state_event is the event name where sub flow was created from.
     */
    private String _parent_flow_state_event = null;

    private String _create_user;
    
    private String _update_user;
    
    private long _update_time;

    private long _create_time;




    public String getWorkflow_id() {
        return _workflow_id;
    }

    public void setWorkflow_id(String workflow_id) {
        _workflow_id = workflow_id;
    }

    public String getWorkflow_name() {
        return _workflow_name;
    }

    public void setWorkflow_name(String workflow_name) {
        _workflow_name = workflow_name;
    }

    public long getWorkflow_version() {
        return _workflow_version;
    }

    public void setWorkflow_version(long workflow_version) {
        _workflow_version = workflow_version;
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

    public String getParent_flow_state_event() {
        return _parent_flow_state_event;
    }

    public void setParent_flow_state_event(String parent_flow_state_event) {
        _parent_flow_state_event = parent_flow_state_event;
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

    public String getCurrent_state_name() {
        return _current_state_name;
    }

    public void setCurrent_state_name(String current_state_name) {
        _current_state_name = current_state_name;
    }

    public String getCreate_user() {
		return _create_user;
	}

	public void setCreate_user(String create_user) {
		_create_user = create_user;
	}

	public String getUpdate_user() {
		return _update_user;
	}

	public void setUpdate_user(String update_user) {
		_update_user = update_user;
	}

	public long getUpdate_time() {
        return _update_time;
    }

    public void setUpdate_time(long update_time) {
        _update_time = update_time;
    }

    public long getCreate_time() {
        return _create_time;
    }

    public void setCreate_time(long create_time) {
        _create_time = create_time;
    }
}
