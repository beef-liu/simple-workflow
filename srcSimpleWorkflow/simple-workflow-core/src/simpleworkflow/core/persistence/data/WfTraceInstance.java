package simpleworkflow.core.persistence.data;

import java.util.List;

/**
 * @author XingGu_Liu
 */
public class WfTraceInstance {
    private String _workflow_id;

    private List<WfTraceRecord> _trace_records;

    public String getWorkflow_id() {
        return _workflow_id;
    }

    public void setWorkflow_id(String workflow_id) {
        _workflow_id = workflow_id;
    }

    public List<WfTraceRecord> getTrace_records() {
        return _trace_records;
    }

    public void setTrace_records(List<WfTraceRecord> trace_records) {
        _trace_records = trace_records;
    }
}
