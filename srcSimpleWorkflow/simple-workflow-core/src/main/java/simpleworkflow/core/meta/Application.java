package simpleworkflow.core.meta;

/**
 * @author XingGu_Liu
 */
public class Application extends MetaBaseData {

    /**
     * It could simply be a block of code of java script, or a java class.
     * Details about how functions is interpreted by workflow engine.
     * Generally the return value is type of Boolean or type of stateData.
     */
    private String _run_scheme;

    public String getRun_scheme() {
        return _run_scheme;
    }

    public void setRun_scheme(String run_scheme) {
        _run_scheme = run_scheme;
    }
}
