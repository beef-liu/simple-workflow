package simpleworkflow.core.error;

import simpleworkflow.core.WorkflowException;

/**
 * @author XingGu_Liu
 * 
 * Error occurred in storing data into persistence
 */
public class WorkflowPersistenceException extends WorkflowException {
	private static final long serialVersionUID = -6974935464705100305L;

	public WorkflowPersistenceException() {
        super();
    }

    public WorkflowPersistenceException(String msg) {
        super(msg);
    }

    public WorkflowPersistenceException(Throwable e) {
        super(e);
    }

    public WorkflowPersistenceException(String msg, Throwable e) {
        super(msg, e);
    }
    
	public WorkflowPersistenceException(int errorCode) {
        super(errorCode);
    }

    public WorkflowPersistenceException(int errorCode, String msg) {
        super(errorCode, msg);
    }

    public WorkflowPersistenceException(int errorCode, Throwable e) {
        super(errorCode, e);
    }

    public WorkflowPersistenceException(int errorCode, String msg, Throwable e) {
        super(errorCode, msg, e);
    }
    
}
