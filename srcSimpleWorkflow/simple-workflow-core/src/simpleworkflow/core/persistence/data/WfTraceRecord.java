package simpleworkflow.core.persistence.data;

/**
 * @author XingGu_Liu
 */
public class WfTraceRecord {
    private String _workflow_id;

    private long _trace_seq;

    private String _parent_workflow_id;

    private String _state_id;

    public String getWorkflow_id() {
        return _workflow_id;
    }

    public void setWorkflow_id(String workflow_id) {
        _workflow_id = workflow_id;
    }

    public String getParent_workflow_id() {
        return _parent_workflow_id;
    }

    public long getTrace_seq() {
        return _trace_seq;
    }

    public void setTrace_seq(long trace_seq) {
        _trace_seq = trace_seq;
    }

    public void setParent_workflow_id(String parent_workflow_id) {
        _parent_workflow_id = parent_workflow_id;
    }

    public String getState_id() {
        return _state_id;
    }

    public void setState_id(String state_id) {
        _state_id = state_id;
    }
}
