package simpleworkflow.core;

/**
 * @author XingGu_Liu
 */
public class WorkflowException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 440222858039086151L;

	public WorkflowException() {
        super();
    }

    public WorkflowException(String msg) {
        super(msg);
    }

    public WorkflowException(Throwable e) {
        super(e);
    }

    public WorkflowException(String msg, Throwable e) {
        super(msg, e);
    }

}
