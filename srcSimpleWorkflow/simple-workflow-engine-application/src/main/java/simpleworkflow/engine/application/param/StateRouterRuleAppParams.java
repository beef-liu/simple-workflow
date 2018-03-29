package simpleworkflow.engine.application.param;

import simpleworkflow.core.interfaces.IPersistenceTransaction;
import simpleworkflow.core.interfaces.IWorkflowEngine;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;

/**
 * It is the type of parameter of method of application of StateRouterRule.boolCondition
 * @author XingGu_Liu
 */
public class StateRouterRuleAppParams extends StateEventAppParams {
    private String _routerRuleName;
    private String _toState;

    public StateRouterRuleAppParams() {
    }

    public StateRouterRuleAppParams(
            IWorkflowEngine engine, IPersistenceTransaction transaction, 
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, Object eventData,
            String routerRuleName, String toState
    ) {
        super(engine, transaction, user, workflowMeta, workflowId, stateMeta, stateId, stateData, eventName, eventData);

        _routerRuleName = routerRuleName;
        _toState = toState;
    }

    public String getRouterRuleName() {
        return _routerRuleName;
    }

    public void setRouterRuleName(String routerRuleName) {
        _routerRuleName = routerRuleName;
    }

	public String getToState() {
		return _toState;
	}

	public void setToState(String toState) {
		_toState = toState;
	}
    
    
}
