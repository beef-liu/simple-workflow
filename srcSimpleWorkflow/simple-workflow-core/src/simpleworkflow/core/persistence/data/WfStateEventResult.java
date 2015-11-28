package simpleworkflow.core.persistence.data;

/**
 * @author XingGu_Liu
 */
public class WfStateEventResult {
    private String _to_workflow_name;
    private String _to_workflow_id;
    private int _to_workflow_status;

    private String _to_state_name;
    private String _to_state_id;

    
    public String getTo_workflow_name() {
        return _to_workflow_name;
    }

    public void setTo_workflow_name(String to_workflow_name) {
        _to_workflow_name = to_workflow_name;
    }

    public String getTo_workflow_id() {
        return _to_workflow_id;
    }

    public void setTo_workflow_id(String to_workflow_id) {
        _to_workflow_id = to_workflow_id;
    }

    public String getTo_state_name() {
        return _to_state_name;
    }

    public void setTo_state_name(String to_state_name) {
        _to_state_name = to_state_name;
    }

    public String getTo_state_id() {
        return _to_state_id;
    }

    public void setTo_state_id(String to_state_id) {
        _to_state_id = to_state_id;
    }

	public int getTo_workflow_status() {
		return _to_workflow_status;
	}

	public void setTo_workflow_status(int to_workflow_status) {
		_to_workflow_status = to_workflow_status;
	}

    
    
}
