package simpleworkflow.core.meta;

/**
 * @author XingGu_Liu
 */
public class Activity extends MetaBaseData {

    /**
     * It will be ignored when subflowFlag == true.
     * Return value should be type of stateData
     */
    private Application _application;

    /**
     * Current flow will go into a subflow after application being executed if subflow == true
     */
    private boolean _subflow = false;

    private String _subflowName;



    public boolean isSubflow() {
        return _subflow;
    }

    public void setSubflow(boolean subflow) {
        _subflow = subflow;
    }

    public String getSubflowName() {
        return _subflowName;
    }

    public void setSubflowName(String subflowName) {
        _subflowName = subflowName;
    }

    public Application getApplication() {
        return _application;
    }

    public void setApplication(Application application) {
        _application = application;
    }
}
