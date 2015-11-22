package simpleworkflow.engine.persistence.sql.util;

import simpleworkflow.core.interfaces.IPersistenceTransaction;
import simpleworkflow.core.persistence.WorkflowPersistenceException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author XingGu_Liu
 */
public class SQLPersistenceTransaction implements IPersistenceTransaction {
    private final Connection _conn;

    public SQLPersistenceTransaction(Connection conn) {
        _conn = conn;
    }

    public Connection getConnection() {
        return _conn;
    }

    @Override
    public void rollback() throws WorkflowPersistenceException {
        try {
            _conn.rollback();
        } catch (SQLException e) {
            throw new WorkflowPersistenceException(e);
        }
    }

    @Override
    public void commit() throws WorkflowPersistenceException {
        try {
            _conn.commit();
        } catch (SQLException e) {
            throw new WorkflowPersistenceException(e);
        }
    }

    @Override
    public void close() throws WorkflowPersistenceException {
        try {
            _conn.close();
        } catch (SQLException e) {
            throw new WorkflowPersistenceException(e);
        }
    }
}
