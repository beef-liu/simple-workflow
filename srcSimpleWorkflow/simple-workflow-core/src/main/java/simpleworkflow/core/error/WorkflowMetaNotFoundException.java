package simpleworkflow.core.error;

import simpleworkflow.core.WorkflowException;

public class WorkflowMetaNotFoundException extends WorkflowException {
	private static final long serialVersionUID = -4427495666877570926L;
	
	public WorkflowMetaNotFoundException() {
        super();
    }

    public WorkflowMetaNotFoundException(String msg) {
        super(msg);
    }

    public WorkflowMetaNotFoundException(Throwable e) {
        super(e);
    }

    public WorkflowMetaNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
    
	public WorkflowMetaNotFoundException(int errorCode) {
        super(errorCode);
    }

    public WorkflowMetaNotFoundException(int errorCode, String msg) {
        super(errorCode, msg);
    }

    public WorkflowMetaNotFoundException(int errorCode, Throwable e) {
        super(errorCode, e);
    }

    public WorkflowMetaNotFoundException(int errorCode, String msg, Throwable e) {
        super(errorCode, msg, e);
    }

}
