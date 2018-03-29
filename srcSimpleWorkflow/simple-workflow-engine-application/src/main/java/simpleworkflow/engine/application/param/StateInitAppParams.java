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
public class StateInitAppParams extends StateAppBaseParams {

	private String _fromStateId;
	
    private Object _inData;

    public StateInitAppParams() {
    }

    public StateInitAppParams(
            IWorkflowEngine engine, IPersistenceTransaction transaction, 
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId,
            String fromStateId,
            Object inData) {
    	super(engine, transaction, user, workflowMeta, workflowId, stateMeta, stateId);
    	
    	_fromStateId = fromStateId;
        _inData = inData;
    }
    

	public String getFromStateId() {
		return _fromStateId;
	}

	public void setFromStateId(String fromStateId) {
		_fromStateId = fromStateId;
	}

	public Object getInData() {
        return _inData;
    }

    public void setInData(Object inData) {
        _inData = inData;
    }
}
