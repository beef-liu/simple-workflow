package simpleworkflow.core;

/**
 * @author XingGu_Liu
 */
public class WorkflowAccessNoAuthException extends WorkflowException {

    public WorkflowAccessNoAuthException() {
        super();
    }

    public WorkflowAccessNoAuthException(String msg) {
        super(msg);
    }

    public WorkflowAccessNoAuthException(Throwable e) {
        super(e);
    }

    public WorkflowAccessNoAuthException(String msg, Throwable e) {
        super(msg, e);
    }
}
