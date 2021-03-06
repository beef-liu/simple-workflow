package simpleworkflow.engine.application.param;

import simpleworkflow.core.interfaces.IPersistenceTransaction;
import simpleworkflow.core.interfaces.IWorkflowEngine;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;

/**
 * It is the type of parameter of method of application of State.initApp and State.accessibleCheck
 * workflowId could be null when the state is the 1st state of the workflow.
 * @author XingGu_Liu
 */
public class StateAppParams extends StateAppBaseParams {
    private Object _stateData;

    public StateAppParams() {
    }

    public StateAppParams(
            IWorkflowEngine engine, IPersistenceTransaction transaction, 
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData) {
    	super(engine, transaction, user, workflowMeta, workflowId, stateMeta, stateId);
        _stateData = stateData;
    }

    public Object getStateData() {
        return _stateData;
    }

    public void setStateData(Object stateData) {
        _stateData = stateData;
    }
}
