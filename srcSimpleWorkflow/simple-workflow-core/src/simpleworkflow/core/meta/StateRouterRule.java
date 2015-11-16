package simpleworkflow.core.meta;

/**
 * @author XingGu_Liu
 */
public class StateRouterRule extends MetaBaseData {

    private BoolCondition _boolCondition;

    /**
     * the state where go to
     */
    private String _destination;

    public BoolCondition getBoolCondition() {
        return _boolCondition;
    }

    public void setBoolCondition(BoolCondition boolCondition) {
        _boolCondition = boolCondition;
    }

    public String getDestination() {
        return _destination;
    }

    public void setDestination(String destination) {
        _destination = destination;
    }
}
