package simpleworkflow.core.meta;

/**
 * @author XingGu_Liu
 */
public class Application extends MetaBaseData {

    /**
     * Information for constructing IApplication, such like class name, source file path, etc.
     * Its format should be defined by the implementation of workflow engine.
     */
    private String _run_scheme;

    public String getRun_scheme() {
        return _run_scheme;
    }

    public void setRun_scheme(String run_scheme) {
        _run_scheme = run_scheme;
    }
}
