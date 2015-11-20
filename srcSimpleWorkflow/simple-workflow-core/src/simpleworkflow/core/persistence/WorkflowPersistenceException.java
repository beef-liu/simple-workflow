package simpleworkflow.core.persistence;

import simpleworkflow.core.WorkflowException;

/**
 * @author XingGu_Liu
 */
public class WorkflowPersistenceException extends WorkflowException {

    /**
	 * 
	 */
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
}
