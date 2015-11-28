package simpleworkflow.engine.application;

import MetoXML.XmlDeserializer;
import simpleworkflow.core.WorkflowException;
import simpleworkflow.core.interfaces.IApplicationLoader;
import simpleworkflow.core.interfaces.IClassFinder;
import simpleworkflow.core.meta.Application;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author XingGu_Liu
 */
public class ApplicationLoader implements IApplicationLoader {

    private final IClassFinder _classFinderForStateData;

    private final ScriptEngineManager _engineManager;

    public ApplicationLoader(IClassFinder classFinderForStateData) {
        _classFinderForStateData = classFinderForStateData;

        _engineManager = new ScriptEngineManager();
    }

    @Override
    public Object executeApplication(Application appMeta, Object appParams) throws WorkflowException {
        try {
            ApplicationRunScheme runScheme = ApplicationRunScheme.deserialize(appMeta.getRun_scheme()); 

            if(ApplicationRunScheme.RunSchemeAppTypes.Java.equalsIgnoreCase(runScheme.getAppType())) {
                return executeApplicationOfJava(runScheme, appParams);
            } else if(ApplicationRunScheme.RunSchemeAppTypes.JavaScript.equalsIgnoreCase(runScheme.getAppType())) {
                return executeApplicationOfJavaScript(runScheme, appParams);
            } else {
                throw new IllegalArgumentException("runScheme.getAppType() is illegal:" + runScheme.getAppType());
            }
        } catch (Throwable e) {
            throw new WorkflowException(e);
        }
    }

    protected Object executeApplicationOfJava(ApplicationRunScheme runScheme, Object appParams) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        String[] classAndMethod = parseClassAndMethod(runScheme);

        Class<?> cls = _classFinderForStateData.findClass(classAndMethod[0]);
        Method method = getMethod(cls, classAndMethod[1]);

        boolean isStatic = ((method.getModifiers() & Modifier.STATIC) != 0);
        if(isStatic) {
            return method.invoke(null, appParams);
        } else {
            Object app = cls.newInstance();
            return method.invoke(app, appParams);
        }
    }

    protected Object executeApplicationOfJavaScript(ApplicationRunScheme runScheme, Object appParams) throws ScriptException {
        ScriptEngine engine = _engineManager.getEngineByName("javascript");

        engine.put("appParams", appParams);

        return engine.eval(runScheme.getSource());
    }

    protected String[] parseClassAndMethod(ApplicationRunScheme runScheme) throws ClassNotFoundException {
        String classNameAndMethod = runScheme.getSource();

        int lastIndexOfDot = classNameAndMethod.lastIndexOf('.');
        String className = classNameAndMethod.substring(0, lastIndexOfDot).trim();

        String methodName = classNameAndMethod.substring(lastIndexOfDot + 1);
        int indexOfParentheses = methodName.indexOf('(');
        if(indexOfParentheses >= 0) {
            methodName = methodName.substring(0, indexOfParentheses);
        }
        methodName = methodName.trim();

        return new String[] {className, methodName};
    }

    protected static Method getMethod(Class<?> cls, String methodName) {
        Method[] methods = cls.getMethods();
        for (Method m: methods) {
            if(m.getName().equals(methodName)) {
                return m;
            }
        }

        return null;
    }
}
