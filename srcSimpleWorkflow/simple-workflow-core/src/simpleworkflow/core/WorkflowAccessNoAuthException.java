package simpleworkflow.core;

/**
 * @author XingGu_Liu
 */
public class WorkflowAccessNoAuthException extends WorkflowException {
	private static final long serialVersionUID = 3550298100794691113L;

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
    
	public WorkflowAccessNoAuthException(int errorCode) {
        super(errorCode);
    }

    public WorkflowAccessNoAuthException(int errorCode, String msg) {
        super(errorCode, msg);
    }

    public WorkflowAccessNoAuthException(int errorCode, Throwable e) {
        super(errorCode, e);
    }

    public WorkflowAccessNoAuthException(int errorCode, String msg, Throwable e) {
        super(errorCode, msg, e);
    }
}
