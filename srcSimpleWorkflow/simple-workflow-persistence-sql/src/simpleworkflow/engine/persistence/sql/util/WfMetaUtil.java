package simpleworkflow.engine.persistence.sql.util;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;
import simpleworkflow.engine.persistence.sql.data.WfMeta;
import MetoXML.XmlDeserializer;
import MetoXML.XmlSerializer;
import MetoXML.Base.XmlParseException;
import MetoXML.Util.ClassFinder;

/**
 * @author XingGu_Liu
 */
public class WfMetaUtil {

    public static Workflow toWorkflow(WfMeta wfMeta, ClassFinder classFinder)
            throws IllegalAccessException, IOException, XmlParseException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        if(wfMeta == null) {
            return null;
        }

        Workflow workflow = new Workflow();

        workflow.setName(wfMeta.getName());
        workflow.setVersion(wfMeta.getVersion());
        workflow.setDescription(wfMeta.getDescription());
        workflow.setAuthor(wfMeta.getAuthor());
        workflow.setEngineName(wfMeta.getEngineName());
        workflow.setStartState(wfMeta.getStartState());

        workflow.setStates((List<State>) XmlDeserializer.stringToObject(
                wfMeta.getStates(), ArrayList.class, classFinder));

        return workflow;
    }

    public static WfMeta toWfMeta(Workflow workflow)
            throws InvocationTargetException, IOException, IntrospectionException, IllegalAccessException {
        if(workflow == null) {
            return null;
        }

        WfMeta wfMeta = new WfMeta();
        wfMeta.setName(workflow.getName());
        wfMeta.setVersion(workflow.getVersion());
        wfMeta.setDescription(workflow.getDescription());
        wfMeta.setAuthor(workflow.getAuthor());
        wfMeta.setEngineName(workflow.getEngineName());
        wfMeta.setStartState(workflow.getStartState());

        wfMeta.setStates(XmlSerializer.objectToString(workflow.getStates(), ArrayList.class));

        return wfMeta;
    }

}
