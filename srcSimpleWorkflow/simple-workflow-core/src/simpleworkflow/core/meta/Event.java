package simpleworkflow.core.meta;

/**
 * @author XingGu_Liu
 */
public class Event extends MetaBaseData {
    private Application _accessibleCheck;

    public Application getAccessibleCheck() {
        return _accessibleCheck;
    }

    public void setAccessibleCheck(Application accessibleCheck) {
        _accessibleCheck = accessibleCheck;
    }
}
