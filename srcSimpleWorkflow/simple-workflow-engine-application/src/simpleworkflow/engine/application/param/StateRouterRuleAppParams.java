package simpleworkflow.engine.application.param;

import simpleworkflow.core.interfaces.IWorkflowEngine;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;

/**
 * It is the type of parameter of method of application of StateRouterRule.boolCondition
 * @author XingGu_Liu
 */
public class StateRouterRuleAppParams extends StateEventAppParams {
    private String _routerRuleName;

    public StateRouterRuleAppParams() {
    }

    public StateRouterRuleAppParams(
            IWorkflowEngine engine, String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, Object eventData,
            String routerRuleName
    ) {
        super(engine, user, workflowMeta, workflowId, stateMeta, stateId, stateData, eventName, eventData);

        _routerRuleName = routerRuleName;
    }

    public String getRouterRuleName() {
        return _routerRuleName;
    }

    public void setRouterRuleName(String routerRuleName) {
        _routerRuleName = routerRuleName;
    }
}
