package simpleworkflow.core.meta;

/**
 * @author XingGu_Liu
 */
public class Application extends MetaBaseData {

    /**
     * The format should be decided by the implementation of workflow engine.
     * It could simply be a block of code of java script,
     * or could be some information for constructing IXXXXApplication, such like class name, method name, source file path, etc.
     *
     */
    private String _run_scheme;

    public String getRun_scheme() {
        return _run_scheme;
    }

    public void setRun_scheme(String run_scheme) {
        _run_scheme = run_scheme;
    }
}
