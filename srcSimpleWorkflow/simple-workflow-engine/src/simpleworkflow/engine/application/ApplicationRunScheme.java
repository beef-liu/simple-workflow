package simpleworkflow.engine.application;

/**
 * @author XingGu_Liu
 */
public class ApplicationRunScheme {
    public static class RunSchemeAppTypes {
        public final static String Java = "java";
        public final static String JavaScript = "javascript";
    }

    private String appType = "java";

    /**
     * Be Class name and method name when appType is 'java', e.g. "com.a.b.TestApp.test1()"
     * Be script code when appType is 'javascript', the appParams will be put into ScriptEngineContext,
     * just write 'appParams.getXXX()' to get parameters which you needs.
     */
    private String source = "";


    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
