package simpleworkflow.core.interfaces.application;

import simpleworkflow.core.interfaces.IApplication;
import simpleworkflow.core.interfaces.IWorkflowEngine;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;

/**
 * @author XingGu_Liu
 */
public interface IStateRouterRuleApp extends IApplication {

    public Boolean handleBoolCondition(
            IWorkflowEngine engine,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, String routerRuleName
    );

}
