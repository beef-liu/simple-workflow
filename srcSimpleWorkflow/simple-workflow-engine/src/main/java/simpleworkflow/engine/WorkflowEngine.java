package simpleworkflow.engine;

import MetoXML.Base.XmlDocument;
import MetoXML.Base.XmlNode;
import MetoXML.Base.XmlParseException;
import MetoXML.Util.ClassFinder;
import MetoXML.XmlDeserializer;
import MetoXML.XmlReader;
import MetoXML.XmlSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import simpleworkflow.core.interfaces.IApplicationLoader;
import simpleworkflow.core.interfaces.IClassFinder;
import simpleworkflow.core.interfaces.IWorkflowPersistence;

import java.beans.IntrospectionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author XingGu_Liu
 */
public class WorkflowEngine extends AbstractWorkflowEngine {
    private final static Log logger = LogFactory.getLog(WorkflowEngine.class);

    //private MyXmlSerializer _xmlSerForWorkflowCoreData;
    private MyXmlSerializer _xmlSerForStateData;

    @Override
    public void init(IWorkflowPersistence workflowPersistence,
            IApplicationLoader applicationLoader,
            final IClassFinder classFinderForWorkflowCoreData,
            final IClassFinder classFinderForStateData) {
        super.init(workflowPersistence, applicationLoader,
                classFinderForWorkflowCoreData, classFinderForStateData);
        
        /*
        _xmlSerForWorkflowCoreData = new MyXmlSerializer(new ClassFinder() {
            @Override
            public Class<?> findClass(String s) throws ClassNotFoundException {
                return classFinderForWorkflowCoreData.findClass(s);
            }
        });
        */
        
        _xmlSerForStateData = new MyXmlSerializer(new ClassFinder() {
            @Override
            public Class<?> findClass(String s) throws ClassNotFoundException {
                return classFinderForStateData.findClass(s);
            }
        });
    }
    

    @Override
    public String getEngineName() {
        return WorkflowEngine.class.getName();
    }
    
    @Override
    protected void logInfo(String msg) {
        logger.info(msg);
    }


    @Override
    protected void logError(String msg, Throwable e) {
        logger.error(msg, e);
    }


    @Override
    protected String objectToString(Object obj) {
        try {
            return _xmlSerForStateData.objToXml(obj);
        } catch (Throwable e) {
            logError(null, e);
            return null;
        }
    }


    @Override
    protected Object stringToObject(String str) {
        try {
            return _xmlSerForStateData.xmlToObj(str);
        } catch (Throwable e) {
            logError(null, e);
            return null;
        }
    }
    

    protected static class MyXmlSerializer {
        private final ClassFinder _classFinder;
        public MyXmlSerializer(ClassFinder classFinder) {
            _classFinder = classFinder;
        }

        public String objToXml(Object data) throws InvocationTargetException, IOException, IntrospectionException, IllegalAccessException {
            if(data == null) {
                return "";
            } else {
                return XmlSerializer.objectToString(data, data.getClass());
            }
        }

        public Object xmlToObj(String xml) throws IOException, XmlParseException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
            if(xml == null || xml.length() == 0) {
                return null;
            }

            XmlReader xmlReader = new XmlReader();
            XmlDocument xmlDoc = xmlReader.ReadXml(
                    new ByteArrayInputStream(xml.getBytes(XmlDeserializer.DefaultCharset)));
            XmlNode rootNode = xmlDoc.getRootNode();
            String className = rootNode.getName();

            Class<?> dataClass = _classFinder.findClass(className);

            XmlDeserializer xmlDes = new XmlDeserializer();
            return xmlDes.ConvertXmlNodeToObject(rootNode, dataClass, _classFinder);
        }

        public Object xmlToObj(String xml, Class<?> cls) throws IllegalAccessException, IOException, XmlParseException, InstantiationException, NoSuchMethodException, InvocationTargetException {
            return XmlDeserializer.stringToObject(xml, cls, _classFinder);
        }
    }
    
}
