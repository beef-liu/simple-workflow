package simpleworkflow.core.error;

import simpleworkflow.core.WorkflowException;

public class WorkflowStateInstanceNotFoundException extends WorkflowException {
	private static final long serialVersionUID = 2561076187638290409L;

	public WorkflowStateInstanceNotFoundException() {
        super();
    }

    public WorkflowStateInstanceNotFoundException(String msg) {
        super(msg);
    }

    public WorkflowStateInstanceNotFoundException(Throwable e) {
        super(e);
    }

    public WorkflowStateInstanceNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
    
	public WorkflowStateInstanceNotFoundException(int errorCode) {
        super(errorCode);
    }

    public WorkflowStateInstanceNotFoundException(int errorCode, String msg) {
        super(errorCode, msg);
    }

    public WorkflowStateInstanceNotFoundException(int errorCode, Throwable e) {
        super(errorCode, e);
    }

    public WorkflowStateInstanceNotFoundException(int errorCode, String msg, Throwable e) {
        super(errorCode, msg, e);
    }
	
}
