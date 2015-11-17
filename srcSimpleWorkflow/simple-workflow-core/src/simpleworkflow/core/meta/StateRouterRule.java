package simpleworkflow.core.meta;

/**
 * @author XingGu_Liu
 */
public class StateRouterRule extends MetaBaseData {

    /**
     * Application whose return value is boolean type
     */
    private Application _boolCondition;

    /**
     * the state where go to
     */
    private String _destination;


    public Application getBoolCondition() {
        return _boolCondition;
    }

    public void setBoolCondition(Application boolCondition) {
        _boolCondition = boolCondition;
    }

    public String getDestination() {
        return _destination;
    }

    public void setDestination(String destination) {
        _destination = destination;
    }
}
