/**
 * 
 */
package org.milyn.javabean.runtime.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.util.ClassUtil;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ObjectRuntimeInfoFactory {
	
	/**
     * Resolve the Javabean runtime class.
     * <p/>
     * Also performs some checks on the bean.
     *
     * @param beanClass The beanClass name.
     * @return The bean runtime class instance.
     */
    public static ObjectRuntimeInfo createBeanRuntime(String beanClass) {
        
        Class<?> clazz;

        // If it's an array, we use a List and extract an array from it on the
        // visitAfter event....
        if(beanClass.endsWith("[]")) {
        	ArrayRuntimeInfo arrayRuntimeInfo = new ArrayRuntimeInfo();
        	arrayRuntimeInfo.setClassification(Classification.ARRAY_COLLECTION);
            String arrayTypeName = beanClass.substring(0, beanClass.length() - 2);
            try {
            	arrayRuntimeInfo.setArrayType(ClassUtil.forName(arrayTypeName, ObjectRuntimeInfoFactory.class));
            } catch (ClassNotFoundException e) {
                throw new SmooksConfigurationException("Invalid Smooks bean configuration.  Bean class " + arrayTypeName + " not on classpath.");
            }
            arrayRuntimeInfo.setPopulateType(ArrayList.class);

            return arrayRuntimeInfo;
        }
        
        ObjectRuntimeInfo objectBeanInfo;
        
        try {
            clazz = ClassUtil.forName(beanClass, ObjectRuntimeInfoFactory.class);
        } catch (ClassNotFoundException e) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  Bean class " + beanClass + " not on classpath.");
        }

        // We maintain a targetType enum because it helps us avoid performing
        // instanceof checks, which are cheap when the instance being checked is
        // an instanceof, but is expensive if it's not....
        if(Map.class.isAssignableFrom(clazz)) {
            MapRuntimeInfo mapRuntimeInfo = new MapRuntimeInfo();
        	
            mapRuntimeInfo.setClassification(Classification.MAP_COLLECTION);
        	
            objectBeanInfo = mapRuntimeInfo;
        } else if(Collection.class.isAssignableFrom(clazz)) {
        	CollectionRuntimeInfo collectionRuntimeInfo = new CollectionRuntimeInfo();
        	
        	collectionRuntimeInfo.setClassification(Classification.COLLECTION_COLLECTION);
        	
        	objectBeanInfo = collectionRuntimeInfo;
        } else {
        	objectBeanInfo = new ObjectRuntimeInfo();
        	
            objectBeanInfo.setClassification(Classification.NON_COLLECTION);
        }
        
        objectBeanInfo.setPopulateType(clazz);

        // check for a default constructor.
        try {
            clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new SmooksConfigurationException("Invalid Smooks bean configuration.  Bean class " + beanClass + " doesn't have a public default constructor.");
        }
        
        return objectBeanInfo;
    }
}
