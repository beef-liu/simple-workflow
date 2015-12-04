package simpleworkflow.core.error;

import simpleworkflow.core.WorkflowException;

public class WorkflowEventNotFoundException extends WorkflowException {

	private static final long serialVersionUID = 1766019570256179926L;

	public WorkflowEventNotFoundException() {
        super();
    }

    public WorkflowEventNotFoundException(String msg) {
        super(msg);
    }

    public WorkflowEventNotFoundException(Throwable e) {
        super(e);
    }

    public WorkflowEventNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
    
	public WorkflowEventNotFoundException(int errorCode) {
        super(errorCode);
    }

    public WorkflowEventNotFoundException(int errorCode, String msg) {
        super(errorCode, msg);
    }

    public WorkflowEventNotFoundException(int errorCode, Throwable e) {
        super(errorCode, e);
    }

    public WorkflowEventNotFoundException(int errorCode, String msg, Throwable e) {
        super(errorCode, msg, e);
    }
	
}
