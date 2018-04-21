package simpleworkflow.engine.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataCopyUtil {
    private final static Map<String, Map<String, PropertyDescriptor>> _cache = new ConcurrentHashMap<String, Map<String, PropertyDescriptor>>();

    /**
     *
     * @param srcData could be Map<String, Object>
     * @param destData could be Map<String, Object>
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static void copyData(Object srcData, Object destData) throws InstantiationException, IllegalAccessException, IntrospectionException, IllegalArgumentException, InvocationTargetException {
        if(Map.class.isAssignableFrom(srcData.getClass())) {
            if(Map.class.isAssignableFrom(destData.getClass())) {
                //map to map
                ((Map<String, Object>) destData).putAll(((Map<String, Object>) srcData));
            } else {
                //map to bean
                copyDataWithMapToBean((Map<String, Object>) srcData, destData);
            }
        } else {
            if(Map.class.isAssignableFrom(destData.getClass())) {
                //bean to map
                copyDataWithBeanToMap(srcData, (Map<String, Object>) destData);
            } else {
                //bean to bean
                copyDataWithBeanToBean(srcData, destData);
            }
        }
    }

    /**
     *
     * @param srcData could be Map<String, Object>
     * @param destDataClass could be HashMap.class
     * @param <T>
     * @return
     * @throws InvocationTargetException
     * @throws IntrospectionException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> T createAndCopyData(Object srcData, Class<T> destDataClass) throws InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException {
        T destData = destDataClass.newInstance();

        copyData(srcData, destData);

        return destData;
    }

    public static <T> List<T> createAndCopyDataList(List<?> srcDataList, Class<T> destDataClass) throws IntrospectionException, InstantiationException, IllegalAccessException, InvocationTargetException {
        List<T> destDataList = srcDataList.getClass().newInstance();

        for(Object srcData : srcDataList) {
            T destData = createAndCopyData(srcData, destDataClass);

            destDataList.add(destData);
        }

        return destDataList;
    }

    private static void copyDataWithBeanToBean(Object srcData, Object destData) throws InstantiationException, IllegalAccessException, IntrospectionException, IllegalArgumentException, InvocationTargetException {
        Map<String, PropertyDescriptor> srcPropMap = getPropertyDescriptorMap(srcData.getClass());
        Map<String, PropertyDescriptor> destPropMap = getPropertyDescriptorMap(destData.getClass());
        for(Map.Entry<String, PropertyDescriptor> srcPdEntry : srcPropMap.entrySet()) {
            final String propName = srcPdEntry.getKey();
            final PropertyDescriptor srcPd = srcPdEntry.getValue();
            final Method srcReadMethod = srcPd.getReadMethod();

            if(srcReadMethod == null || srcPd.getWriteMethod() == null) {
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

    private static void copyDataWithMapToBean(Map<String, Object> srcData, Object destData) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Map<String, PropertyDescriptor> destPropMap = getPropertyDescriptorMap(destData.getClass());
        for(Map.Entry<String, Object> entry : srcData.entrySet()) {
            final String key = entry.getKey();
            final Object val = entry.getValue();

            final PropertyDescriptor destPd = destPropMap.get(key);
            if(destPd == null) {
                continue;
            }

            final Method destWriteMethod = destPd.getWriteMethod();
            if(destWriteMethod == null) {
                continue;
            }

            destWriteMethod.invoke(destData, val);
        }
    }

    private static void copyDataWithBeanToMap(Object srcData, Map<String, Object> destData) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Class<?> dataCls = srcData.getClass();

        if(Map.class.isAssignableFrom(dataCls)) {
            destData.putAll((Map<String, Object>) srcData);
            return;
        }

        Map<String, PropertyDescriptor> srcPropMap = getPropertyDescriptorMap(dataCls);
        for(Map.Entry<String, PropertyDescriptor> srcPdEntry : srcPropMap.entrySet()) {
            final String propName = srcPdEntry.getKey();
            final PropertyDescriptor srcPd = srcPdEntry.getValue();
            final Method srcReadMethod = srcPd.getReadMethod();

            if(srcReadMethod == null || srcPd.getWriteMethod() == null) {
                continue;
            }

            final Object val = srcReadMethod.invoke(srcData, (Object[]) null);
            if(val != null) {
                destData.put(propName, val);
            }
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
