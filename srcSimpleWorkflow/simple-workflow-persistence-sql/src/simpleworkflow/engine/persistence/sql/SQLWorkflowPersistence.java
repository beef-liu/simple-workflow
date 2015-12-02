package simpleworkflow.engine.persistence.sql;

import MetoXML.Base.XmlParseException;
import MetoXML.Util.ClassFinder;

import com.salama.service.clouddata.util.dao.QueryDataDao;
import com.salama.service.clouddata.util.dao.UpdateDataDao;
import com.salama.util.db.JDBCUtil;

import simpleworkflow.core.WorkflowEnums;
import simpleworkflow.core.interfaces.IClassFinder;
import simpleworkflow.core.interfaces.IPersistenceTransaction;
import simpleworkflow.core.interfaces.IWorkflowPersistence;
import simpleworkflow.core.meta.Workflow;
import simpleworkflow.core.persistence.WorkflowPersistenceException;
import simpleworkflow.core.persistence.data.WfInstance;
import simpleworkflow.core.persistence.data.WfStateInstance;
import simpleworkflow.core.persistence.data.WfTraceInstance;
import simpleworkflow.core.persistence.data.WfTraceRecord;
import simpleworkflow.engine.persistence.sql.data.WfMeta;
import simpleworkflow.engine.persistence.sql.util.HexUtil;
import simpleworkflow.engine.persistence.sql.util.SQLPersistenceTransaction;
import simpleworkflow.engine.persistence.sql.util.WfMetaUtil;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author XingGu_Liu
 */
public class SQLWorkflowPersistence implements IWorkflowPersistence {

    private final byte _serverNum;
    private final Random _rand;
    private final AtomicInteger _seq;

    private final IDBSource _dbSource;
    private ClassFinder _classFinderForWorkflowCoreData;
    private IClassFinder _iClassFinderForWorkflowCoreData;
//    private ClassFinder _classFinderForStateData;
//    private IClassFinder _iClassFinderForStateData;

    private final WorkflowQueryService _workflowQueryService;
    private final WorkflowQueryDao _workflowQueryDao;
    private final WorkflowModifyService _workflowModifyService;
    private final WorkflowModifyDao _workflowModifyDao;

    public static interface IDBSource {
        public Connection getConnection();
    }

    public SQLWorkflowPersistence(
            byte serverNum,
            IDBSource dbSource,
            IClassFinder classFinderForWorkflowCoreData
            ) {
        _serverNum = serverNum;
        _rand = new Random(System.currentTimeMillis());
        _seq = new AtomicInteger(Integer.MIN_VALUE);

        _dbSource = dbSource;

        {
            _iClassFinderForWorkflowCoreData = classFinderForWorkflowCoreData;
            _classFinderForWorkflowCoreData = new ClassFinder() {
                @Override
                public Class<?> findClass(String s) throws ClassNotFoundException {
                    return _iClassFinderForWorkflowCoreData.findClass(s);
                }
            };
        }

        /*
        {
            _iClassFinderForStateData = classFinderForStateData;
            _classFinderForStateData = new ClassFinder() {
                @Override
                public Class<?> findClass(String s) throws ClassNotFoundException {
                    return _iClassFinderForStateData.findClass(s);
                }
            };
        }
        */

        _workflowQueryService = new WorkflowQueryService();
        _workflowQueryDao = new WorkflowQueryDao();
        _workflowModifyService = new WorkflowModifyService();
        _workflowModifyDao = new WorkflowModifyDao();
    }

    @Override
    public IPersistenceTransaction createTransaction() {
        return new SQLPersistenceTransaction(_dbSource.getConnection());
    }

    @Override
    public IWorkflowQueryService getWorkflowQueryService() {
        return _workflowQueryService;
    }

    @Override
    public IWorkflowModifyService getWorkflowModifyService() {
        return _workflowModifyService;
    }

    @Override
    public String newDataId() throws WorkflowPersistenceException {
        final ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.clear();

        buffer.order(ByteOrder.BIG_ENDIAN);

        //UTC(8 byte) ----->
        buffer.putLong(System.currentTimeMillis());

        //4 byte ----->
        int randNum = (_rand.nextInt() & 0x00ffffff)
                | ( (_serverNum) << 24);
        buffer.putInt(randNum);

        //4 byte ----->
        buffer.putInt(_seq.incrementAndGet());


        return HexUtil.toHexString(buffer.array(), 0, 16);
    }

    private class WorkflowQueryService implements IWorkflowQueryService {

        @Override
        public Workflow getMetaWorkflow(String name, long version) throws WorkflowPersistenceException {
            try {
                Connection conn = _dbSource.getConnection();
                try {
                    return _workflowQueryDao.getMetaWorkflow(conn, name, version);
                } finally {
                    conn.close();
                }
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }


        @Override
        public Workflow getMetaWorkflowOfLatestVersion(String name) throws WorkflowPersistenceException {
            try {
                Connection conn = _dbSource.getConnection();
                try {
                    return _workflowQueryDao.getMetaWorkflowOfLatestVersion(conn, name);
                } finally {
                    conn.close();
                }
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public List<Workflow> findMetaWorkflows(String name) throws WorkflowPersistenceException {
            try {
                Connection conn = _dbSource.getConnection();
                try {
                    return _workflowQueryDao.findMetaWorkflows(conn, name);
                } finally {
                    conn.close();
                }
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public WfInstance getWorkflowInstance(String id) throws WorkflowPersistenceException {
            try {
                Connection conn = _dbSource.getConnection();
                try {
                    return _workflowQueryDao.getWorkflowInstance(conn, id);
                } finally {
                    conn.close();
                }
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public WfTraceInstance getWorkflowTraceInstance(String id) throws WorkflowPersistenceException {
            try {
                Connection conn = _dbSource.getConnection();
                try {
                    return _workflowQueryDao.getWorkflowTraceInstance(conn, id);
                } finally {
                    conn.close();
                }
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public WfStateInstance getStateInstance(String stateId) throws WorkflowPersistenceException {
            try {
                Connection conn = _dbSource.getConnection();
                try {
                    return _workflowQueryDao.getStateInstance(conn, stateId);
                } finally {
                    conn.close();
                }
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public WfStateInstance getCurrentStateInstance(String workflowId) throws WorkflowPersistenceException {
            try {
                Connection conn = _dbSource.getConnection();
                try {
                    return _workflowQueryDao.getCurrentStateInstance(conn, workflowId);
                } finally {
                    conn.close();
                }
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public List<WfInstance> findWorkflowInstatncesCreatedInTimeSpan(
                String workflowName, long startTime, long endTime)
                throws WorkflowPersistenceException {
            try {
                Connection conn = _dbSource.getConnection();
                try {
                    return _workflowQueryDao.findWorkflowInstatncesUpdatedInTimeSpan(conn,
                            false,
                            workflowName, startTime, endTime,
                            Integer.MIN_VALUE
                            );
                } finally {
                    conn.close();
                }
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public List<WfInstance> findWorkflowInstatncesCreatedInTimeSpan(
                String workflowName, long startTime, long endTime,
                WorkflowEnums.WorkflowStatus workflowStatus) throws WorkflowPersistenceException {
            try {
                Connection conn = _dbSource.getConnection();
                try {
                    return _workflowQueryDao.findWorkflowInstatncesUpdatedInTimeSpan(conn,
                            false,
                            workflowName, startTime, endTime,
                            workflowStatus.ordinal()
                    );
                } finally {
                    conn.close();
                }
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public List<WfInstance> findWorkflowInstatncesUpdatedInTimeSpan(
                String workflowName, long startTime, long endTime) throws WorkflowPersistenceException {
            try {
                Connection conn = _dbSource.getConnection();
                try {
                    return _workflowQueryDao.findWorkflowInstatncesUpdatedInTimeSpan(conn,
                            true,
                            workflowName, startTime, endTime,
                            Integer.MIN_VALUE
                    );
                } finally {
                    conn.close();
                }
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public List<WfInstance> findWorkflowInstatncesUpdatedInTimeSpan(
                String workflowName, long startTime, long endTime,
                WorkflowEnums.WorkflowStatus workflowStatus) throws WorkflowPersistenceException {
            try {
                Connection conn = _dbSource.getConnection();
                try {
                    return _workflowQueryDao.findWorkflowInstatncesUpdatedInTimeSpan(conn,
                            true,
                            workflowName, startTime, endTime,
                            workflowStatus.ordinal()
                    );
                } finally {
                    conn.close();
                }
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }
    }

    private class WorkflowModifyService implements IWorkflowModifyService {

        @Override
        public void setMetaWorkflow(IPersistenceTransaction trans, Workflow data) throws WorkflowPersistenceException {
            try {
                _workflowModifyDao.setMetaWorkflow(
                        ((SQLPersistenceTransaction)trans).getConnection(),
                        data
                );
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public void setWorkflowInstance(IPersistenceTransaction trans, WfInstance data) throws WorkflowPersistenceException {
            try {
                _workflowModifyDao.setWorkflowInstance(
                        ((SQLPersistenceTransaction)trans).getConnection(),
                        data
                );
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public void addWorkflowTraceRecord(IPersistenceTransaction trans, WfTraceRecord data) throws WorkflowPersistenceException {
            try {
                _workflowModifyDao.addWorkflowTraceRecord(
                        ((SQLPersistenceTransaction)trans).getConnection(),
                        data
                );
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public void setStateInstance(IPersistenceTransaction trans, WfStateInstance data) throws WorkflowPersistenceException {
            try {
                _workflowModifyDao.setStateInstance(
                        ((SQLPersistenceTransaction)trans).getConnection(),
                        data
                );
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }

        @Override
        public void setWorkflowCurrentState(
                IPersistenceTransaction trans,
                String workflowId, String stateName, String stateId,
                int workflowStatus, 
                String updateUser, long updateTime
                ) throws WorkflowPersistenceException {
            try {
                _workflowModifyDao.setWorkflowCurrentState(
                        ((SQLPersistenceTransaction)trans).getConnection(),
                        workflowId, stateName, stateId, workflowStatus,
                        updateUser, updateTime
                );
            } catch (Throwable e) {
                throw new WorkflowPersistenceException(e);
            }
        }
    }

    private class WorkflowQueryDao {

        private final static String SQL_FIND_META_WORKFLOW_BY_PK =
                "select * from WfMeta"
                + " where name = ? and version = ?";
        private Workflow getMetaWorkflow(
                Connection conn,
                String name, long version) throws SQLException, InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException, NoSuchMethodException, IOException, XmlParseException {
            PreparedStatement stmt = conn.prepareStatement(SQL_FIND_META_WORKFLOW_BY_PK);
            try {
                int index = 1;

                stmt.setString(index++, name);
                stmt.setLong(index++, version);

                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                    return WfMetaUtil.toWorkflow(
                            (WfMeta) JDBCUtil.ResultSetToData(rs, WfMeta.class, true),
                            _classFinderForWorkflowCoreData
                    );
                } else {
                    return null;
                }
            } finally {
                stmt.close();
            }
        }

        private final static String SQL_FIND_META_WORKFLOW_OF_LATEST =
                "select * from WfMeta"
                + " where name = ? "
                + " order by version desc"
                ;
        private Workflow getMetaWorkflowOfLatestVersion(Connection conn, String name) throws SQLException, InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException, NoSuchMethodException, IOException, XmlParseException {
            PreparedStatement stmt = conn.prepareStatement(SQL_FIND_META_WORKFLOW_OF_LATEST,
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            try {
                int index = 1;

                stmt.setString(index++, name);

                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                    return WfMetaUtil.toWorkflow(
                            (WfMeta) JDBCUtil.ResultSetToData(rs, WfMeta.class, true),
                            _classFinderForWorkflowCoreData
                    );
                } else {
                    return null;
                }
            } finally {
                stmt.close();
            }
        }

        private final static String SQL_FIND_META_WORKFLOWS_BY_NAME =
                "select * from WfMeta"
                + " where name = ? "
                + " order by version desc";
        private List<Workflow> findMetaWorkflows(Connection conn, String name) throws InvocationTargetException, SQLException, IntrospectionException, InstantiationException, IllegalAccessException, NoSuchMethodException, IOException, XmlParseException {
            PreparedStatement stmt = conn.prepareStatement(SQL_FIND_META_WORKFLOWS_BY_NAME,
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            try {
                int index = 1;

                stmt.setString(index++, name);

                ResultSet rs = stmt.executeQuery();

                List<Workflow> list = new ArrayList<Workflow>();
                while(rs.next()) {
                    Workflow data = WfMetaUtil.toWorkflow(
                            (WfMeta) JDBCUtil.ResultSetToData(rs, WfMeta.class, true),
                            _classFinderForWorkflowCoreData
                    );
                    list.add(data);
                }

                return list;
            } finally {
                stmt.close();
            }
        }


        private final static String SQL_FIND_WORKFLOW_INSTANCE_BY_ID=
                "select * from WfInstance"
                + " where workflow_id = ? ";
        public WfInstance getWorkflowInstance(Connection conn, String workflowId)
                throws SQLException, InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException {
            PreparedStatement stmt = conn.prepareStatement(SQL_FIND_WORKFLOW_INSTANCE_BY_ID);
            try {
                int index = 1;

                stmt.setString(index++, workflowId);

                ResultSet rs = stmt.executeQuery();

                if(rs.next()) {
                    return (WfInstance) JDBCUtil.ResultSetToData(rs, WfInstance.class, true);
                } else {
                	return null;
                }
            } finally {
                stmt.close();
            }
        }

        private final static String SQL_FIND_WORKFLOW_TRACE_BY_ID=
                "select * from WfTraceRecord"
                + " where workflow_id = ? "
                + " order by trace_seq";
        public WfTraceInstance getWorkflowTraceInstance(Connection conn, String workflowId) throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException {
            PreparedStatement stmt = conn.prepareStatement(SQL_FIND_WORKFLOW_TRACE_BY_ID,
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            try {
                int index = 1;

                stmt.setString(index++, workflowId);

                List<WfTraceRecord> traceRecordList = QueryDataDao.findData(stmt, WfTraceRecord.class);

                WfTraceInstance trace = new WfTraceInstance();
                trace.setWorkflow_id(workflowId);
                trace.setTrace_records(traceRecordList);

                return trace;
            } finally {
                stmt.close();
            }
        }

        private final static String SQL_FIND_STATE_INSTANCE_BY_ID=
                "select * from WfStateInstance"
                + " where state_id = ? ";
        public WfStateInstance getStateInstance(Connection conn, String stateId) throws InvocationTargetException, SQLException, IntrospectionException, InstantiationException, IllegalAccessException {
            PreparedStatement stmt = conn.prepareStatement(SQL_FIND_STATE_INSTANCE_BY_ID);
            try {
                int index = 1;

                stmt.setString(index++, stateId);

                ResultSet rs = stmt.executeQuery();
                
                if(rs.next()) {
                    return (WfStateInstance) JDBCUtil.ResultSetToData(
                            rs, WfStateInstance.class, true);
                } else {
                	return null;
                }
            } finally {
                stmt.close();
            }
        }

        private final static String SQL_FIND_CURRENT_STATE_INSTANCE_BY_WORKFLOW_ID
                = "select st.* "
                + " from WfStateInstance st"
                + " join WfInstance wf"
                + "   on wf.workflow_id = ?"
                + "      and st.state_id = wf.current_state_id"
                ;
        public WfStateInstance getCurrentStateInstance(Connection conn, String workflowId) throws InvocationTargetException, SQLException, IntrospectionException, InstantiationException, IllegalAccessException {
            PreparedStatement stmt = conn.prepareStatement(
                    SQL_FIND_CURRENT_STATE_INSTANCE_BY_WORKFLOW_ID);
            try {
                int index = 1;

                stmt.setString(index++, workflowId);

                ResultSet rs = stmt.executeQuery();

                if(rs.next()) {
                    return (WfStateInstance) JDBCUtil.ResultSetToData(
                            rs, WfStateInstance.class, true);
                } else {
                	return null;
                }
            } finally {
                stmt.close();
            }
        }

        /**
         *
         * @param conn
         * @param workflowName ignored if it is null or empty
         * @param startTime
         * @param endTime
         * @param workflowStatus ignored if it equals Integer.MIN_VALUE
         * @return
         */
        public List<WfInstance> findWorkflowInstatncesUpdatedInTimeSpan(
                Connection conn,
                boolean isUseUpdateTime,
                String workflowName, long startTime, long endTime,
                int workflowStatus
        ) throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException {
            List<Object> params = new ArrayList<Object>();
            StringBuilder sqlWhere = new StringBuilder();

            //status
            if(workflowStatus != Integer.MIN_VALUE) {
                sqlWhere.append(" workflow_status = ?");
                params.add(workflowStatus);
            }

            //update_time
            if(sqlWhere.length() > 0) {
                sqlWhere.append(" and ");
            }
            if(isUseUpdateTime) {
                sqlWhere.append(" update_time >= ? and update_time < ?");
            } else {
                sqlWhere.append(" create_time >= ? and create_time < ?");
            }

            params.add(startTime);
            params.add(endTime);

            //name
            if(workflowName != null || workflowName.length() > 0) {
                sqlWhere.append(" and ");
                sqlWhere.append(" workflow_name = ?");
                params.add(workflowName);
            }

            String sql = "select * from WfInstance where "
                    .concat(sqlWhere.toString())
                    .concat(" order by update_time");


            PreparedStatement stmt = conn.prepareStatement(sql,
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            try {
                int index = 1;

                for (Object param : params) {
                    if(param.getClass() == String.class) {
                        stmt.setString(index++, (String)param);
                    } else if(param.getClass() == Integer.class) {
                        stmt.setInt(index++, (Integer) param);
                    } else if(param.getClass() == Long.class) {
                        stmt.setLong(index++, (Long) param);
                    } else {
                        stmt.setObject(index++, param);
                    }
                }

                return QueryDataDao.findData(stmt, WfInstance.class);
            } finally {
                stmt.close();
            }

        }

    }

    private final static String[] WfMeta_PKs = new String[]{
            "name", "version"
    };
    private final static String[] WfInstance_PKs = new String[]{
            "workflow_id"
    };
    private final static String[] WfStateInstance_PKs = new String[]{
            "state_id"
    };

    private class WorkflowModifyDao {
        public int setMetaWorkflow(Connection conn, Workflow data) throws IOException, IntrospectionException, IllegalAccessException, InvocationTargetException, SQLException {
            WfMeta wfMeta = WfMetaUtil.toWfMeta(data);

            try {
                return UpdateDataDao.insertData(conn, "WfMeta", wfMeta);
            } catch (SQLException e) {
                if(SQLIntegrityConstraintViolationException.class.isAssignableFrom(e.getClass())) {
                    return UpdateDataDao.updateData(conn, "WfMeta", wfMeta, WfMeta_PKs);
                } else {
                    throw e;
                }
            }
        }

        public int setWorkflowInstance(Connection conn, WfInstance data
        ) throws SQLException {
            try {
                return UpdateDataDao.insertData(conn, "WfInstance", data);
            } catch (SQLException e) {
                if(SQLIntegrityConstraintViolationException.class.isAssignableFrom(e.getClass())) {
                    return UpdateDataDao.updateData(conn, "WfInstance", data, WfInstance_PKs);
                } else {
                    throw e;
                }
            }
        }

        public int addWorkflowTraceRecord(Connection conn, WfTraceRecord data
        ) throws SQLException {
            return UpdateDataDao.insertData(conn, "WfTraceRecord", data);
        }

        public int setStateInstance(Connection conn, WfStateInstance data) throws SQLException {
            try {
                return UpdateDataDao.insertData(conn, "WfStateInstance", data);
            } catch (SQLException e) {
                if(SQLIntegrityConstraintViolationException.class.isAssignableFrom(e.getClass())) {
                    return UpdateDataDao.updateData(conn, "WfStateInstance", data, WfStateInstance_PKs);
                } else {
                    throw e;
                }
            }
        }

        private final static String SQL_UPDATE_CURRENT_STATE
                = " update WfInstance set "
                + " current_state_name = ?"
                + " , current_state_id = ?"
                + " , workflow_status = ?"
                + " , update_user = ?"
                + " , update_time = ?"
                + " where workflow_id = ?";
        public int setWorkflowCurrentState(
                Connection conn,
                String workflowId, 
                String stateName, String stateId,
                int workflowStatus,
                String updateUser, long updateTime
        ) throws SQLException {
            PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_CURRENT_STATE);
            try {
                int index = 1;

                stmt.setString(index++, stateName);
                stmt.setString(index++, stateId);
                stmt.setInt(index++, workflowStatus);
                stmt.setString(index++, updateUser);
                stmt.setLong(index++, updateTime);
                
                stmt.setString(index++, workflowId);

                return stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        }
    }


}
