package simpleworkflow.core.interfaces.application;

import simpleworkflow.core.interfaces.IApplication;
import simpleworkflow.core.interfaces.IWorkflowEngine;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;

/**
 * @author XingGu_Liu
 */
public interface IStateAuthCheckApp extends IApplication {

    public Boolean checkStateAccessible(
            IWorkflowEngine engine,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData
    );

    public Boolean checkStateEventAccessible(
            IWorkflowEngine engine,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName
    );

}
