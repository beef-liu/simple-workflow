package simpleworkflow.core.interfaces;

import simpleworkflow.core.WorkflowException;
import simpleworkflow.core.meta.Application;

/**
 * @author XingGu_Liu
 */
public interface IApplicationLoader {

    /**
     * appMeta and appParam are interpreted by workflow engine.
     * @param appMeta
     * @param appParams
     * @return stateData or Boolean
     * @throws WorkflowException
     */
    Object executeApplication(Application appMeta, Object appParams) throws WorkflowException;

}
