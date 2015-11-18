package simpleworkflow.core.interfaces;

import simpleworkflow.core.WorkflowException;
import simpleworkflow.core.meta.Application;

/**
 * @author XingGu_Liu
 */
public interface IApplicationLoader {

    IApplication createApplication(Application appMeta) throws WorkflowException;

}
