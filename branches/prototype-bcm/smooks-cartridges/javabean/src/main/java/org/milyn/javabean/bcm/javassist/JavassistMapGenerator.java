/**
 * 
 */
package org.milyn.javabean.bcm.javassist;

import static org.milyn.javabean.bcm.javassist.JavassistUtils.NO_ARGS;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;

import org.milyn.container.ApplicationContext;
import org.milyn.javabean.BeanUtils;
import org.milyn.javabean.bcm.BcmClassLoader;
import org.milyn.javabean.bcm.BcmUtils;
import org.milyn.javabean.bcm.MapGenerator;
import org.milyn.javabean.bcm.OptimizedMap;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class JavassistMapGenerator implements MapGenerator {

	/**
	 * 
	 */
	private static final String MAP_NAME_PREFIX = "org.milyn.javabean.bcm.javassist._generated.map.";

	private ClassPool classPool;
	
	private BcmClassLoader classLoader;
	
	private CtClass ctOptimizedMap;
	
	private CtClass ctObject;
	
	private CtClass ctMap;
	
	boolean initialized = true;
	
	public void initialize(ApplicationContext applicationContext) {
		classPool = JavaPoolUtils.getClassPool(applicationContext);
		classLoader = BcmUtils.getClassloader(applicationContext);
		
		try {
			
			ctOptimizedMap = classPool.get(OptimizedMap.class.getName());
			
			ctObject = classPool.get(Object.class.getName());
			
			ctMap = classPool.get(Map.class.getName());
			
		} catch (NotFoundException e) {
			throw new RuntimeException("Could not get one of the CtClass's from the ClassPool", e);
		}
		
		initialized = true;
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.javabean.bcm.MapGenerator#generateMap(java.lang.Class, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, ?> generateMap(String name, List<String> keys) {
		
		if(!initialized) {
			throw new IllegalStateException("JavassistMapGenerator not initialized. Call #initialize(ApplicationContext) first.");
		}
		
		if(keys.isEmpty()) {
			return new HashMap<String, Object>();
		}
    	
		Class<?> mapClass = null;
		try {
			String mapClassName = MAP_NAME_PREFIX + name;
			
			mapClass = classLoader.load(mapClassName);
						
			if(mapClass == null) {
				CtClass impl = classPool.makeClass(mapClassName);
				impl.setSuperclass(ctOptimizedMap);
				
				addConstructor(impl);
	
				addFields(impl, keys);
				
				addVirtualClearMethod(impl, keys);
				
				addVirtualContainsKeyMethod(impl, keys);
				
				addVirtualContainsValueMethod(impl, keys);
				
				addVirtualGetMethod(impl, keys);
				
				addVirtualPutMethod(impl, keys);
				
				//addVirtualPutAllMethod(impl, keys);
				
				addVirtualRemoveMethod(impl, keys);
				
				addVirtualFieldsToMapMethod(impl, keys);
				
				byte[] byteCode = impl.toBytecode();
				
				mapClass = classLoader.load(impl.getName(), byteCode);

			}
		} catch (CannotCompileException e) {
			throw new RuntimeException("Could not create the Map class", e);
		} catch (IOException e) {
			throw new RuntimeException("Could not create the Map class", e);
		}
    	
    	try {
			return (Map) mapClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("Could not create the Map object", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Could not create the Map object", e);
		}
	}

	private void addConstructor(CtClass impl) throws CannotCompileException {
		// add public default constructor method to class
        CtConstructor cons = new CtConstructor(NO_ARGS, impl);
        cons.setBody(";");
        impl.addConstructor(cons);
	}
	
	private void addFields(CtClass impl, List<String> fieldNames) throws CannotCompileException {
		
		for(String fieldName : fieldNames) {
			addField(impl, fieldName);
		}
		
	}

	/**
	 * @param impl
	 * @param fieldName
	 */
	private void addField(CtClass impl, String fieldName) throws CannotCompileException {

		CtField field = CtField.make("private Object " + fieldName + " = NOT_SET;" , impl);
		
		impl.addField(field);
		
		addFieldGetter(impl, fieldName);
		addFieldSetter(impl, fieldName);
		
	}
	
	private void addFieldGetter(CtClass impl, String fieldName) throws CannotCompileException {
		
		String methodBody = "return " + fieldName + ";";
		
		CtMethod setMethod = CtNewMethod.make(ctObject, BeanUtils.toGetterName(fieldName), NO_ARGS, new CtClass[0], methodBody, impl);
		impl.addMethod(setMethod);
	}
	
	private void addFieldSetter(CtClass impl, String fieldName) throws CannotCompileException {
		
		String methodBody = fieldName + " = $1;";
		
		CtMethod setMethod = CtNewMethod.make(CtPrimitiveType.voidType, BeanUtils.toSetterName(fieldName), new CtClass[] { ctObject }, new CtClass[0], methodBody, impl);
		impl.addMethod(setMethod);
	}

	private void addVirtualClearMethod(CtClass impl, List<String> keys) throws CannotCompileException {
		
		StringBuilder methodBody = new StringBuilder();
		
		methodBody.append("{");
			
		for(String key : keys) {
			methodBody.append(key);
			methodBody.append("= NOT_SET;");
		}
		
		methodBody.append("}");
		
		CtMethod setMethod = CtNewMethod.make(CtPrimitiveType.voidType, "virtualClear", NO_ARGS, new CtClass[0], methodBody.toString(), impl);
		impl.addMethod(setMethod);
	}
	
	private void addVirtualContainsValueMethod(CtClass impl, List<String> keys) throws CannotCompileException {
		
		StringBuilder methodBody = new StringBuilder();
		
		methodBody.append("{");
			
		for(String key : keys) {
			
			methodBody.append("if(");
			methodBody.append(key);
			methodBody.append(".equals($1)) { return true; }");

		}
		
		methodBody.append("return false;");
		methodBody.append("}");
		
		CtMethod setMethod = CtNewMethod.make(CtPrimitiveType.booleanType, "virtualContainsValue", new CtClass[] { ctObject }, new CtClass[0], methodBody.toString(), impl);
		impl.addMethod(setMethod);
	}
	
	private void addVirtualContainsKeyMethod(CtClass impl, List<String> keys) throws CannotCompileException {
		
		StringBuilder methodBody = new StringBuilder();
		
		methodBody.append("{");	
		
		for(String key : keys) {
			
			methodBody.append("if(\"");
			methodBody.append(key);
			methodBody.append("\".equals($1)) { return true; }");

		}
		
		methodBody.append("return false;");
		methodBody.append("}");
		
		CtMethod setMethod = CtNewMethod.make(CtPrimitiveType.booleanType, "virtualContainsKey", new CtClass[] { ctObject }, new CtClass[0], methodBody.toString(), impl);
		impl.addMethod(setMethod);
	}
	
	private void addVirtualGetMethod(CtClass impl, List<String> keys) throws CannotCompileException {
		
		StringBuilder methodBody = new StringBuilder();
		
		methodBody.append("{");
		
		for(String key : keys) {
			
			methodBody.append("if(\"");
			methodBody.append(key);
			methodBody.append("\".equals($1)) { return ");
			methodBody.append(key);
			methodBody.append("; }");
		}
		
		methodBody.append("return NOT_FOUND;");
		methodBody.append("}");
		
		CtMethod setMethod = CtNewMethod.make(ctObject, "virtualGet", new CtClass[] { ctObject }, new CtClass[0], methodBody.toString(), impl);
		impl.addMethod(setMethod);
	}

	private void addVirtualPutMethod(CtClass impl, List<String> keys) throws CannotCompileException {
		
		StringBuilder methodBody = new StringBuilder();
		
		methodBody.append("{");
		
		for(String key : keys) {
			
			methodBody.append("if(\"");
			methodBody.append(key);
			methodBody.append("\".equals($1)) { ");
			methodBody.append("Object result = ");
			methodBody.append(key);
			methodBody.append(";");
			methodBody.append(key);
			methodBody.append(" = $2;");
			methodBody.append("return result; }");
		}
		
		methodBody.append("return NOT_FOUND;");
		methodBody.append("}");
		
		CtMethod setMethod = CtNewMethod.make(ctObject, "virtualPut", new CtClass[] { ctObject, ctObject }, new CtClass[0], methodBody.toString(), impl);
		impl.addMethod(setMethod);
	}
	
//	private void addVirtualPutAllMethod(CtClass impl, List<String> keys) throws CannotCompileException {
//		
//		StringBuilder methodBody = new StringBuilder();
//		
//		methodBody.append("{");
//		methodBody.append(AssertArgument.class.getName());
//		methodBody.append(".isNotNull($1, \"t\");");
//		
//		
//		for(String key : keys) {
//			
//			methodBody.append("if(\"");
//			methodBody.append(key);
//			methodBody.append("\".equals($1)) { ");
//			methodBody.append("Object result = ");
//			methodBody.append(key);
//			methodBody.append(";");
//			methodBody.append(key);
//			methodBody.append(" = $2;");
//			methodBody.append("return result; }");
//		}
//		
//		methodBody.append("return NOT_FOUND;");
//		methodBody.append("}");
//		
//		CtMethod setMethod = CtNewMethod.make(CtPrimitiveType.voidType, "virtualPutAll", new CtClass[] { ctMap }, new CtClass[0], methodBody.toString(), impl);
//		impl.addMethod(setMethod);
//	}
	
	private void addVirtualRemoveMethod(CtClass impl, List<String> keys) throws CannotCompileException {
		
		StringBuilder methodBody = new StringBuilder();
		methodBody.append("{");

		
		for(String key : keys) {

			methodBody.append("if(\"");
			methodBody.append(key);
			methodBody.append("\".equals($1)) { ");
			methodBody.append("Object result = ");
			methodBody.append(key);
			methodBody.append(";");
			methodBody.append(key);
			methodBody.append(" = NOT_SET;");
			methodBody.append("return result; }");
			
		}
		
		methodBody.append("return NOT_FOUND;");
		methodBody.append("}");
		
		CtMethod setMethod = CtNewMethod.make(ctObject, "virtualRemove", new CtClass[] { ctObject }, new CtClass[0], methodBody.toString(), impl);
		impl.addMethod(setMethod);
	}
	
	private void addVirtualFieldsToMapMethod(CtClass impl, List<String> keys) throws CannotCompileException {
		
		StringBuilder methodBody = new StringBuilder();
		methodBody.append("{");
		
		methodBody.append("java.util.Map result = new java.util.HashMap();" );
		
		for(String key : keys) {
			
			methodBody.append("result.put(");
			methodBody.append(key);
			methodBody.append(",");
			methodBody.append("\"");
			methodBody.append(key);
			methodBody.append("\");");
		}
		
		methodBody.append("return result;");
		methodBody.append("}");
		
		CtMethod setMethod = CtNewMethod.make(ctMap, "virtualFieldsToMap", NO_ARGS, new CtClass[0], methodBody.toString(), impl);
		impl.addMethod(setMethod);
	}
	
	
}
