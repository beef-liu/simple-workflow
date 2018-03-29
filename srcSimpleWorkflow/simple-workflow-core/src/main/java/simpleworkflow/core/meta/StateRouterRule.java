package simpleworkflow.core.meta;

/**
 * @author XingGu_Liu
 */
public class StateRouterRule extends MetaBaseData {

    /**
     * Return value should be type of Boolean
     */
    private Application _boolCondition;

    /**
     * the state name where go to
     */
    private String _toState;


    public Application getBoolCondition() {
        return _boolCondition;
    }

    public void setBoolCondition(Application boolCondition) {
        _boolCondition = boolCondition;
    }

    public String getToState() {
        return _toState;
    }

    public void setToState(String toState) {
        _toState = toState;
    }
}
