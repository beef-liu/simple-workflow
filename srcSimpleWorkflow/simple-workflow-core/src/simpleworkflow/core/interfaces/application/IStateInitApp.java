package simpleworkflow.core.interfaces.application;

import simpleworkflow.core.interfaces.IApplication;
import simpleworkflow.core.interfaces.IWorkflowEngine;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;

/**
 * @author XingGu_Liu
 */
public interface IStateInitApp extends IApplication {

    /**
     *
     * @param engine
     * @param user
     * @param workflowMeta
     * @param workflowId
     * @param stateMeta
     * @param stateId
     * @param stateData
     * @return stateData
     */
    public Object handleStateInit(
            IWorkflowEngine engine,
            String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData
    );

}
