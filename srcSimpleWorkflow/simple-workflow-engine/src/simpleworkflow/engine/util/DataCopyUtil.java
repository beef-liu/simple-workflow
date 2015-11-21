package simpleworkflow.engine.util;

import CollectionCommon.ITreeNode;
import MetoXML.AbstractReflectInfoCachedSerializer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author XingGu_Liu
 */
public class DataCopyUtil extends AbstractReflectInfoCachedSerializer {

    /**
     * copy all values of properties from srcData to destData
     * @param srcData
     * @param destData
     */
    public static void copyDataValue(Object srcData, Object destData) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        if(srcData == null || destData == null) {
            return;
        }

        PropertyDescriptor[] props = findPropertyDescriptorArray(srcData.getClass());
        for (PropertyDescriptor p : props) {
            if(p.getReadMethod() == null || p.getWriteMethod() == null) {
                continue;
            }

            p.getWriteMethod().invoke(
                    destData,
                    p.getReadMethod().invoke(srcData)
                    );
        }
    }


    @Override
    protected void ForwardToNode(ITreeNode iTreeNode, int i, boolean b) {
        //Do nothing
    }

    @Override
    protected void BackwardToNode(ITreeNode iTreeNode, int i) {
        //do nothing
    }
}
