package simpleworkflow.core.interfaces;

import simpleworkflow.core.persistence.WorkflowPersistenceException;

/**
 * @author XingGu_Liu
 */
public interface IPersistenceTransaction {

    public void rollback() throws WorkflowPersistenceException;

    public void commit() throws WorkflowPersistenceException;

    public void close() throws WorkflowPersistenceException;
}
