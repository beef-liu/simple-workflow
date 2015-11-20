package simpleworkflow.core.persistence.data;

/**
 * @author XingGu_Liu
 */
public class WfStateInstance {
    private String _workflow_id;

    private String _workflow_name;

    private String _state_id;

    private String _state_name;

    /**
     * StateData value when workflow just go to this state
     */
    private String _in_state_data = null;

    /**
     * StateData value after executing activity
     */
    private String _out_state_data = null;

    private String _triggered_event = null;
    private String _triggered_event_data = null;
    private String _triggered_subflow_id = null;

    /**
     * next state name
     */
    private String _to_state_name = null;

    private String _to_state_id = null;

    private String _create_user;

    private String _update_user;

    private long _create_time;

    private long _update_time;


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

    public String getState_id() {
        return _state_id;
    }

    public void setState_id(String state_id) {
        _state_id = state_id;
    }

    public String getState_name() {
        return _state_name;
    }

    public void setState_name(String state_name) {
        _state_name = state_name;
    }

    public String getIn_state_data() {
        return _in_state_data;
    }

    public void setIn_state_data(String in_state_data) {
        _in_state_data = in_state_data;
    }

    public String getOut_state_data() {
        return _out_state_data;
    }

    public void setOut_state_data(String out_state_data) {
        _out_state_data = out_state_data;
    }

    public String getTriggered_event() {
        return _triggered_event;
    }

    public void setTriggered_event(String triggered_event) {
        _triggered_event = triggered_event;
    }

    public String getTriggered_event_data() {
        return _triggered_event_data;
    }

    public void setTriggered_event_data(String triggered_event_data) {
        _triggered_event_data = triggered_event_data;
    }

    public String getTriggered_subflow_id() {
        return _triggered_subflow_id;
    }

    public void setTriggered_subflow_id(String triggered_subflow_id) {
        _triggered_subflow_id = triggered_subflow_id;
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

    public long getCreate_time() {
        return _create_time;
    }

    public void setCreate_time(long create_time) {
        _create_time = create_time;
    }

    public long getUpdate_time() {
        return _update_time;
    }

    public void setUpdate_time(long update_time) {
        _update_time = update_time;
    }
}
