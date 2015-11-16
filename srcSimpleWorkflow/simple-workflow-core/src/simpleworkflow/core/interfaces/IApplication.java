package simpleworkflow.core.interfaces;

/**
 * @author XingGu_Liu
 */
public interface IApplication {

    public void init(String workflow_id);

    public void run();

    public void destroy();

}
