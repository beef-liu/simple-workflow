package simpleworkflow.core;

/**
 * @author XingGu_Liu
 */
public class WorkflowException extends Exception {
	private static final long serialVersionUID = 440222858039086151L;
	
	public final static int ERROR_CODE_UNKNOWN_ERROR = -1;
	
	protected int _errorCode = ERROR_CODE_UNKNOWN_ERROR;

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

	public WorkflowException(int errorCode) {
        super();
        
        _errorCode = errorCode;
    }

    public WorkflowException(int errorCode, String msg) {
        super(msg);
        
        _errorCode = errorCode;
    }

    public WorkflowException(int errorCode, Throwable e) {
        super(e);
        
        _errorCode = errorCode;
    }

    public WorkflowException(int errorCode, String msg, Throwable e) {
        super(msg, e);
        
        _errorCode = errorCode;
    }
    
    
	public int getErrorCode() {
		return _errorCode;
	}

	public void setErrorCode(int errorCode) {
		_errorCode = errorCode;
	}

}
