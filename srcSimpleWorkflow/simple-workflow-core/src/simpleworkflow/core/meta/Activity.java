package simpleworkflow.core.meta;

/**
 * @author XingGu_Liu
 */
public class Activity extends MetaBaseData {

    private boolean _subflowFlag = false;

    private String _subflowName;

    private Application _application;

    public boolean isSubflowFlag() {
        return _subflowFlag;
    }

    public void setSubflowFlag(boolean subflowFlag) {
        _subflowFlag = subflowFlag;
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
