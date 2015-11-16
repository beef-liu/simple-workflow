package simpleworkflow.core.interfaces;

/**
 * @author XingGu_Liu
 */
public interface IApplication {

    public void init(long workflowId);

    public void run();

    public void destroy();

}
