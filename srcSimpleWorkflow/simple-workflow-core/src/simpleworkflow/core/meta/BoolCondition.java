package simpleworkflow.core.meta;

/**
 * @author XingGu_Liu
 */
public class BoolCondition extends MetaBaseData {

    /**
     * JavaScript. e.g. "return true;"
     */
    private String _boolExpression;

    public String getBoolExpression() {
        return _boolExpression;
    }

    public void setBoolExpression(String boolExpression) {
        _boolExpression = boolExpression;
    }
}
