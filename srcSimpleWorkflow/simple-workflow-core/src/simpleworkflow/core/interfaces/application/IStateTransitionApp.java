package simpleworkflow.core.interfaces.application;

import simpleworkflow.core.interfaces.IApplication;
import simpleworkflow.core.interfaces.IWorkflowEngine;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;

/**
 * @author XingGu_Liu
 */
public interface IStateTransitionApp extends IApplication {

    /**
     *
     * @param engine
     * @param user
     * @param workflowMeta
     * @param workflowId
     * @param stateMeta
     * @param stateId
     * @param stateData
     * @param eventName
     * @param eventData
     * @return stateData
     */
    public Object handleStateEvent(
            IWorkflowEngine engine,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, Object eventData
    );

}
