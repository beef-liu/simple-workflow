package simpleworkflow.core.interfaces;

import simpleworkflow.core.WorkflowException;
import simpleworkflow.core.meta.Application;

/**
 * @author XingGu_Liu
 */
public interface IApplicationLoader {

    public IApplication createApplication(Application appMeta) throws WorkflowException;

    public Object executeApplication(Application appMeta, IApplication app) throws WorkflowException;

}
