package simpleworkflow.engine.application.param;

import simpleworkflow.core.interfaces.IWorkflowEngine;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;

/**
 * It is the type of parameter of method of application of State.initApp and State.accessibleCheck
 * workflowId could be null when the state is the 1st state of the workflow.
 * @author XingGu_Liu
 */
public class StateInitAppParams {
    private IWorkflowEngine _engine;

    private String _user;

    private Workflow _workflowMeta;

    private String _workflowId;

    private State _stateMeta;

    private String _stateId;

    private Object _stateData;

    public StateInitAppParams() {
    }

    public StateInitAppParams(
            IWorkflowEngine engine, String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData) {
        _engine = engine;
        _user = user;
        _workflowMeta = workflowMeta;
        _workflowId = workflowId;
        _stateMeta = stateMeta;
        _stateId = stateId;
        _stateData = stateData;
    }

    public IWorkflowEngine getEngine() {
        return _engine;
    }

    public void setEngine(IWorkflowEngine engine) {
        _engine = engine;
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

    public Object getStateData() {
        return _stateData;
    }

    public void setStateData(Object stateData) {
        _stateData = stateData;
    }
}
