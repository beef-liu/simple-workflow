package simpleworkflow.engine.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XingGu_Liu
 */
public class DataCopyUtil {
    private final static Map<String, Map<String, PropertyDescriptor>> _cache = new ConcurrentHashMap<String, Map<String, PropertyDescriptor>>();

    public static void copyData(Object srcData, Object destData) throws InstantiationException, IllegalAccessException, IntrospectionException, IllegalArgumentException, InvocationTargetException {
        Map<String, PropertyDescriptor> srcPropMap = getPropertyDescriptorMap(srcData.getClass());
        Map<String, PropertyDescriptor> destPropMap = getPropertyDescriptorMap(destData.getClass());
        for(Map.Entry<String, PropertyDescriptor> srcPdEntry : srcPropMap.entrySet()) {
            final String propName = srcPdEntry.getKey();
            final PropertyDescriptor srcPd = srcPdEntry.getValue();
            final Method srcReadMethod = srcPd.getReadMethod();

            if(srcReadMethod == null) {
                continue;
            }

            final PropertyDescriptor destPd = destPropMap.get(propName);
            if(destPd == null) {
                continue;
            }

            final Method destWriteMethod = destPd.getWriteMethod();
            if(destWriteMethod == null) {
                continue;
            }

            destWriteMethod.invoke(
                    destData,
                    srcReadMethod.invoke(srcData, (Object[]) null)
            );
        }
    }

    private static Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> cls) throws IntrospectionException {
        final String key = cls.getName();
        Map<String, PropertyDescriptor> pdMap = _cache.get(key);

        if(pdMap == null) {
            pdMap = new HashMap<String, PropertyDescriptor>();

            PropertyDescriptor[] pdArray = Introspector.getBeanInfo(cls).getPropertyDescriptors();
            for(PropertyDescriptor pd : pdArray) {
                pdMap.put(pd.getName(), pd);
            }

            _cache.put(key, pdMap);
        }

        return pdMap;
    }

}
