package simpleworkflow.core.persistence;

import simpleworkflow.core.WorkflowException;

/**
 * @author XingGu_Liu
 */
public class WorkflowPersistenceException extends WorkflowException {

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
