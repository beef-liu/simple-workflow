package simpleworkflow.core.interfaces;

import simpleworkflow.core.WorkflowException;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;

/**
 * @author XingGu_Liu
 */
public interface IApplication {

    public void init(
            IWorkflowEngine engine,
            Workflow workflowMeta, State stateMeta,
            String workflowId, String stateId,
            Object stateData,
            String user
            ) throws WorkflowException;

    public void destroy();

}
