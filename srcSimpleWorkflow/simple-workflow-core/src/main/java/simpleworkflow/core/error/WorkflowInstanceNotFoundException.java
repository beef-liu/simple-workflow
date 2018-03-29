package simpleworkflow.core.error;

import simpleworkflow.core.WorkflowException;

public class WorkflowInstanceNotFoundException extends WorkflowException {

	private static final long serialVersionUID = 7949118403059271129L;
	
	public WorkflowInstanceNotFoundException() {
        super();
    }

    public WorkflowInstanceNotFoundException(String msg) {
        super(msg);
    }

    public WorkflowInstanceNotFoundException(Throwable e) {
        super(e);
    }

    public WorkflowInstanceNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
    
	public WorkflowInstanceNotFoundException(int errorCode) {
        super(errorCode);
    }

    public WorkflowInstanceNotFoundException(int errorCode, String msg) {
        super(errorCode, msg);
    }

    public WorkflowInstanceNotFoundException(int errorCode, Throwable e) {
        super(errorCode, e);
    }

    public WorkflowInstanceNotFoundException(int errorCode, String msg, Throwable e) {
        super(errorCode, msg, e);
    }
	

}
