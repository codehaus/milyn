/**
 * 
 */
package org.milyn.javabean.runtime.info;

import java.util.HashMap;
import java.util.Map;

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.container.ApplicationContext;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ObjectRuntimeInfo {
	
	private static final String CONTEXT_KEY = ObjectRuntimeInfo.class.getName() + "#CONTEXT_KEY";

    /**
     * The basic type that's created and populated for the associated bean.
     */
    private Class<?> populateType;

    /**
     * The bean classification.
     * <p/>
     * We maintain this classification enum because it helps us avoid performing
     * instanceof checks, which are cheap when the instance being checked is
     * an instanceof, but is expensive if it's not.
     */
    private Classification classification;
    
    public static void recordRuntimeInfo(String beanId, ObjectRuntimeInfo objectRuntimeInfo, ApplicationContext appContext) {
        Map<String, ObjectRuntimeInfo> runtimeInfoMap = getRuntimeInfoMap(appContext);
        ObjectRuntimeInfo existingBeanConfig = runtimeInfoMap.get(beanId);

        if(existingBeanConfig != null && !objectRuntimeInfo.equals(existingBeanConfig)) {
            throw new SmooksConfigurationException("Multiple configurations present with beanId='" + beanId + "', but the bean runtime infos are not equal i.e bean classes etc are different.  Use a different beanId and the 'setOnMethod' config if needed.");
        }

        runtimeInfoMap.put(beanId, objectRuntimeInfo);
    }

    public static ObjectRuntimeInfo getRuntimeInfo(String beanId, ApplicationContext appContext) {
        Map<String, ObjectRuntimeInfo> runtimeInfoMap = getRuntimeInfoMap(appContext);

        return runtimeInfoMap.get(beanId);
    }

    @SuppressWarnings("unchecked")
	private static Map<String, ObjectRuntimeInfo> getRuntimeInfoMap(ApplicationContext appContext) {
        Map<String, ObjectRuntimeInfo> runtimeInfoMap = (Map<String, ObjectRuntimeInfo>) appContext.getAttribute(CONTEXT_KEY);

        if(runtimeInfoMap == null) {
            runtimeInfoMap = new HashMap<String, ObjectRuntimeInfo>();
            appContext.setAttribute(CONTEXT_KEY, runtimeInfoMap);
        }

        return runtimeInfoMap;
    }

    public Class<?> getPopulateType() {
        return populateType;
    }

    public void setPopulateType(Class<?> populateType) {
        this.populateType = populateType;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

   
    @Override
	public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof ObjectRuntimeInfo)) {
            return false;
        }

        ObjectRuntimeInfo beanInfo = (ObjectRuntimeInfo) obj;
        if(beanInfo.getClassification() != getClassification()) {
            return false;
        }
        if(beanInfo.getPopulateType() != getPopulateType()) {
            return false;
        }

        return true;
    }
	  
}
