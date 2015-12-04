package simpleworkflow.engine.application.param;

import simpleworkflow.core.interfaces.IPersistenceTransaction;
import simpleworkflow.core.interfaces.IWorkflowEngine;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;

public class StateAppBaseParams {

    private IWorkflowEngine _engine;
    
    private IPersistenceTransaction _transaction;

    private String _user;

    private Workflow _workflowMeta;

    private String _workflowId;

    private State _stateMeta;

    private String _stateId;
    
    public StateAppBaseParams() {
    }
    
    public StateAppBaseParams(
            IWorkflowEngine engine, IPersistenceTransaction transaction, 
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId) {
        _engine = engine;
        _transaction = transaction;
        
        _user = user;
        _workflowMeta = workflowMeta;
        _workflowId = workflowId;
        _stateMeta = stateMeta;
        _stateId = stateId;
    }
    

	public IWorkflowEngine getEngine() {
		return _engine;
	}

	public void setEngine(IWorkflowEngine engine) {
		_engine = engine;
	}

	public IPersistenceTransaction getTransaction() {
		return _transaction;
	}

	public void setTransaction(IPersistenceTransaction transaction) {
		_transaction = transaction;
	}

	public String getUser() {
		return _user;
	}

	public void setUser(String user) {
		_user = user;
	}

	public Workflow getWorkflowMeta() {
		return _workflowMeta;
	}

	public void setWorkflowMeta(Workflow workflowMeta) {
		_workflowMeta = workflowMeta;
	}

	public String getWorkflowId() {
		return _workflowId;
	}

	public void setWorkflowId(String workflowId) {
		_workflowId = workflowId;
	}

	public State getStateMeta() {
		return _stateMeta;
	}

	public void setStateMeta(State stateMeta) {
		_stateMeta = stateMeta;
	}

	public String getStateId() {
		return _stateId;
	}

	public void setStateId(String stateId) {
		_stateId = stateId;
	}
    
}
