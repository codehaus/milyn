/**
 * 
 */
package org.milyn.javabean.runtime.info;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class ArrayRuntimeInfo extends ObjectRuntimeInfo {

    /**
     * If the bean classification is an ARRAY_COLLECTION, this member specifies the
     * actual array type.
     */
    private Class<?> arrayType;
    

    public Class<?> getArrayType() {
        return arrayType;
    }

    public void setArrayType(Class<?> arrayType) {
        this.arrayType = arrayType;
    }
}
