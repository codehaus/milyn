/**
 *
 */
package org.milyn.javabean.invoker.javassist;

import static org.milyn.javabean.bcm.javassist.JavassistUtils.NO_ARGS;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;

import org.milyn.container.ApplicationContext;
import org.milyn.javabean.bcm.BcmClassLoader;
import org.milyn.javabean.bcm.BcmUtils;
import org.milyn.javabean.bcm.javassist.JavaPoolUtils;
import org.milyn.javabean.invoker.SetMethodInvoker;
import org.milyn.javabean.invoker.SetMethodInvokerFactory;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class JavassistSetMethodInvokerFactory implements
		SetMethodInvokerFactory {


	private ClassPool classPool;

	private BcmClassLoader classLoader;

	private CtClass smiInterface;

	private CtClass ctObject;

	private boolean initialized = false;


	/* (non-Javadoc)
	 * @see org.milyn.javabean.invoker.SetterMethodInvocatorFactory#initialize(org.milyn.container.ApplicationContext)
	 */
	public void initialize(ApplicationContext applicationContext) {

    	classPool = JavaPoolUtils.getClassPool(applicationContext);
    	classLoader = BcmUtils.getClassloader(applicationContext);

		try {

			// smi = SetterMethodInvocator
			smiInterface = classPool.get(SetMethodInvoker.class.getName());

			ctObject = classPool.get(Object.class.getName());

		} catch (NotFoundException e) {
			throw new RuntimeException("Could not get one of the CtClass's from the ClassPool", e);
		}

		initialized = true;
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.invoker.SetterMethodInvocatorFactory#create(org.milyn.container.ApplicationContext, java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public SetMethodInvoker create(Class<?> beanClass, String setterName, Class<?> setterParamType) {
		if(!initialized) {
			throw new IllegalStateException("Factory not initizialed. Call the #initialize(ApplicationContext) first.");
		}

    	try {

			return (SetMethodInvoker) getClass(beanClass, setterName, setterParamType).newInstance();

		} catch (InstantiationException e) {
			throw new RuntimeException("Could not create the SetterMethodInvocator object", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Could not create the SetterMethodInvocator object", e);
		}

    }


	/**
	 * Gets the setMethodInvoker class with the specified parameter
	 *
	 * @param beanClass
	 * @param setterName
	 * @param setterParamType
	 * @return
	 */
	private Class<?> getClass(Class<?> beanClass, String setterName, Class<?> setterParamType) {
		if(!initialized) {
			throw new IllegalStateException("Factory not initizialed. Call the #initialize(ApplicationContext) first.");
		}

		// smi = SetterMethodInvocator
		Class<?> smiClass = null;

		String smiClassName = "org.milyn.javabean.invoker.javassist._generated." + safeClassName(beanClass.getName() + "_" + setterName + "_" + setterParamType.getName());

		smiClass = classLoader.load(smiClassName);

		if(smiClass == null) {
			smiClass = createClass(beanClass, setterName, setterParamType, smiClassName);
		}

		return smiClass;
	}

	/**
	 * @param beanClass
	 * @param setterName
	 * @param setterParamType
	 * @param smiClass
	 * @param smiClassName
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 * @throws IOException
	 */
	private Class<?> createClass(Class<?> beanClass, String setterName,	Class<?> setterParamType, String smiClassName) {
		try {

			CtClass ctSetterParamType;
			CtClass ctBean;

			try {

				ctBean = classPool.get(beanClass.getName());

				ctSetterParamType = classPool.get(setterParamType.getName());

			} catch (NotFoundException e) {
				throw new RuntimeException("Could not get one of the CtClass's from the ClassPool", e);
			}

			CtClass smiImpl = classPool.makeClass(smiClassName);
			smiImpl.addInterface(smiInterface);

			createConstructor(smiImpl);

			createSetMethod(smiImpl, beanClass, setterName, smiClassName, ctSetterParamType, ctBean);

			byte[] byteCode = smiImpl.toBytecode();

			return classLoader.load(smiImpl.getName(), byteCode);

		} catch (CannotCompileException e) {
			throw new RuntimeException("Could not create the SetterMethodInvocator class", e);
		} catch (NotFoundException e) {
			throw new RuntimeException("Could not create the SetterMethodInvocator class", e);
		} catch (IOException e) {
			throw new RuntimeException("Could not create the SetterMethodInvocator class", e);
		}
	}

	/**
	 * @param smiImpl
	 * @throws CannotCompileException
	 */
	private void createConstructor(CtClass smiImpl)
			throws CannotCompileException {
		// add public default constructor method to class
		CtConstructor cons = new CtConstructor(NO_ARGS, smiImpl);
		cons.setBody(";");
		smiImpl.addConstructor(cons);
	}

	/**
	 * @param beanClass
	 * @param setterName
	 * @param smiClassName
	 * @param ctSetterParamType
	 * @param ctBean
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	private void createSetMethod(CtClass smiImpl, Class<?> beanClass, String setterName,
			String smiClassName, CtClass ctSetterParamType, CtClass ctBean)
			throws CannotCompileException, NotFoundException {

		String methodStr = "((" + beanClass.getName() + ")$1)." + setterName + "(" + parameterStr(setterName, ctBean, "$2", ctSetterParamType) + ");";

		CtMethod setMethod = CtNewMethod.make(CtPrimitiveType.voidType, "set", new CtClass[] { ctObject, ctObject }, new CtClass[0], methodStr, smiImpl);

		smiImpl.addMethod(setMethod);

	}

	private String safeClassName(String name) {

		return name.replace('.', '_')
				.replace('[', '_')
				.replace(';', '_');

	}

    private String parameterStr(String setterName, CtClass ctBeanClass, String parameterName, CtClass setterParamType) throws NotFoundException {


    	if(setterParamType.getName().startsWith("java.lang")) {
    		ClassPool cp = ctBeanClass.getClassPool();

    		CtClass ctByte = cp.get(Byte.class.getName());
    		if(setterParamType.equals(Byte.class.getName())) {

	    		return autobox(setterName, ctBeanClass, parameterName, ctByte, CtClass.byteType);

	    	}
    		CtClass ctShort = cp.get(Short.class.getName());
    		if(setterParamType.equals(ctShort)) {

	    		return autobox(setterName, ctBeanClass, parameterName, ctShort, CtClass.shortType);

			}
    		CtClass ctInteger = cp.get(Integer.class.getName());
    		if(setterParamType.equals(ctInteger)) {

	    		return autobox(setterName, ctBeanClass, parameterName, ctInteger, CtClass.intType);

			}
    		CtClass ctLong = cp.get(Long.class.getName());
    		if(setterParamType.equals(ctLong)) {

	    		return autobox(setterName, ctBeanClass, parameterName, ctLong, CtClass.longType);

			}
    		CtClass ctFloat = cp.get(Float.class.getName());
    		if(setterParamType.equals(ctFloat)) {

	    		return autobox(setterName, ctBeanClass, parameterName, ctFloat, CtClass.floatType);

			}
    		CtClass ctDouble = cp.get(Double.class.getName());
    		if(setterParamType.equals(ctDouble)) {

	    		return autobox(setterName, ctBeanClass, parameterName, ctDouble, CtClass.doubleType);

			}
    		CtClass ctBoolean = cp.get(Boolean.class.getName());
    		if(setterParamType.equals(ctBoolean)) {

	    		return autobox(setterName, ctBeanClass, parameterName, ctBoolean, CtClass.booleanType);

			}
    		CtClass ctCharacter = cp.get(Character.class.getName());
    		if(setterParamType.equals(ctCharacter)) {

	    		return autobox(setterName, ctBeanClass, parameterName, ctCharacter, CtClass.charType);
    		}
    	}

		if(setterParamType.isArray()) {

			return "(" + setterParamType.getComponentType().getName() + "[])" + parameterName;
		} else {
			return "(" + setterParamType.getName() + ")" + parameterName;
		}


    }

    private String autobox(String setterName, CtClass beanClass, String parameterName, CtClass paramType, CtClass paramPrimativeType) throws NotFoundException {

    	if(isMethodPresent(setterName, beanClass, paramType)) {
			return "("+ paramType.getSimpleName() +") " + parameterName;
		} else if (isMethodPresent(setterName, beanClass, paramPrimativeType)){
			return "(("+ paramType.getSimpleName() +") " + parameterName + ")." + paramPrimativeType.getSimpleName() + "Value()";
		} else if (isMethodPresent(setterName, beanClass, ctObject)){
			return parameterName;
		} else {

			throw new RuntimeException("Could not find the method '" + setterName + "' on the bean '" + beanClass
					+ "' with a '" + paramType.getSimpleName() + "','" + paramPrimativeType.getSimpleName() + "' or a 'Object' parameter.");

		}
    }

    private boolean isMethodPresent(String methodName, CtClass beanClass, CtClass setterParamType) throws NotFoundException {

    	CtMethod[] methods = beanClass.getMethods();

    	for(CtMethod method : methods) {

    		if(methodName.equals(method.getName())) {
	    		CtClass[] parameterTypes = method.getParameterTypes();

	    		if(parameterTypes.length == 1 && parameterTypes[0].equals(setterParamType)) {
	    			return true;
	    		}
    		}

    	}
    	return false;
    }


}
