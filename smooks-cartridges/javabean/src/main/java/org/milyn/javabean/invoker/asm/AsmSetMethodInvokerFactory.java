/**
 *
 */
package org.milyn.javabean.invoker.asm;

import static org.milyn.javabean.bcm.asm.TypeUtil.TYPE_BOOLEAN_OBJ;
import static org.milyn.javabean.bcm.asm.TypeUtil.TYPE_BYTE_OBJ;
import static org.milyn.javabean.bcm.asm.TypeUtil.TYPE_CHARACTER_OBJ;
import static org.milyn.javabean.bcm.asm.TypeUtil.TYPE_DOUBLE_OBJ;
import static org.milyn.javabean.bcm.asm.TypeUtil.TYPE_FLOAT_OBJ;
import static org.milyn.javabean.bcm.asm.TypeUtil.TYPE_INTEGER_OBJ;
import static org.milyn.javabean.bcm.asm.TypeUtil.TYPE_LONG_OBJ;
import static org.milyn.javabean.bcm.asm.TypeUtil.TYPE_SHORT_OBJ;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_5;

import java.io.IOException;

import org.milyn.container.ApplicationContext;
import org.milyn.javabean.bcm.BcmClassLoader;
import org.milyn.javabean.bcm.BcmUtils;
import org.milyn.javabean.bcm.asm.ClassSearcher;
import org.milyn.javabean.invoker.SetMethodInvoker;
import org.milyn.javabean.invoker.SetMethodInvokerFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class AsmSetMethodInvokerFactory implements
		SetMethodInvokerFactory {


	private BcmClassLoader classLoader;

	private boolean initialized = false;

	/* (non-Javadoc)
	 * @see org.milyn.javabean.invoker.SetMethodInvokerFactory#initialize(org.milyn.container.ApplicationContext)
	 */
	public void initialize(ApplicationContext applicationContext) {

		classLoader = BcmUtils.getClassloader(applicationContext);

		initialized = true;
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.invoker.SetMethodInvokerFactory#create(java.lang.String, java.lang.Class, java.lang.Class)
	 */
	public SetMethodInvoker create(Class<?> beanClass, String setterName,Class<?> setterParamType) {

		if(!initialized) {
			throw new IllegalStateException("Factory not initizialed. Call the #initialize(ApplicationContext) first.");
		}

		Class<?> clazz;
		try {

			 clazz = getClass(setterName, beanClass, setterParamType);
		} catch (RuntimeException e) {
			String methodDescription = getMethodDescription(beanClass, setterName, setterParamType);
			throw new RuntimeException("Could not get the class the SetterMethodInvocator object for the " + methodDescription + ")" + ".", e);
		}
    	try {

    		return (SetMethodInvoker) clazz.newInstance();

		} catch (InstantiationException e) {
			throw new RuntimeException("Could not create the SetterMethodInvocator object", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Could not create the SetterMethodInvocator object", e);
		}
	}

	/**
	 * @param setterName
	 * @param beanClass
	 * @param setterParamType
	 * @return
	 */
	private String getMethodDescription(Class<?> beanClass, String setterName,
			Class<?> setterParamType) {
		String methodDescription = beanClass.getName() + "#" + setterName + "(" + setterParamType.getName();
		return methodDescription;
	}

	public Class<?> getClass(String setterName,
			Class<?> beanClass, Class<?> setterParamType) {

		if(!initialized) {
			throw new IllegalStateException("Factory not initizialed. Call the #initialize(ApplicationContext) first.");
		}

		// smi = SetterMethodInvocator
		Class<?> smiClass = null;

		String smiClassName = "org.milyn.javabean.invoker.asm._generated." + safeClassName(beanClass.getName() + "_" + setterName + "_" + setterParamType.getName());

		smiClass = classLoader.load(smiClassName);

		if(smiClass == null) {
			smiClass = generateClass(smiClassName, setterName, beanClass, setterParamType);
		}

		return smiClass;
	}

	private Class<?> generateClass(String smiClassName , String setterName,
			Class<?> beanClass, Class<?> setterParamType) {

		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		String bcSMIClassname = getBcClassName(smiClassName);

		cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, bcSMIClassname, null, "java/lang/Object", new String[] { Type.getInternalName(SetMethodInvoker.class) });

		createConstructor(cw, bcSMIClassname);

		createSetMethod(cw, setterName, beanClass, setterParamType,	bcSMIClassname);

		cw.visitEnd();

		byte[] byteCode =  cw.toByteArray();

		return classLoader.load(smiClassName, byteCode);
	}
	/**
	 * @param cw
	 * @param bcSMIClassname
	 */
	private void createConstructor(ClassWriter cw, String bcSMIClassname) {

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(5, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		mv.visitInsn(RETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "L"+ bcSMIClassname +";", null, l0, l1, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

	}

	/**
	 * @param setterName
	 * @param beanClass
	 * @param setterParamType
	 * @param cw
	 * @param bcSMIClassname
	 */
	private void createSetMethod(ClassWriter cw, String setterName, Class<?> beanClass,
			Class<?> setterParamType, String bcSMIClassname) {

		MethodVisitor mv;

		String beanInternalType = Type.getInternalName(beanClass);

		mv = cw.visitMethod(ACC_PUBLIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(10, l0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST, beanInternalType);
		mv.visitVarInsn(ALOAD, 2);

		createTargetBeanInvocation(mv, setterName, beanClass, setterParamType);

		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(11, l1);
		mv.visitInsn(RETURN);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLocalVariable("this", "L"+ bcSMIClassname +";", null, l0, l2, 0);
		mv.visitLocalVariable("obj", "Ljava/lang/Object;", null, l0, l2, 1);
		mv.visitLocalVariable("arg", "Ljava/lang/Object;", null, l0, l2, 2);
		mv.visitMaxs(2, 3);
		mv.visitEnd();
	}

	private void createTargetBeanInvocation(MethodVisitor mv, String setterName, Class<?> beanClass, Class<?> setterParamType) {

		Type paramType = Type.getType(setterParamType);

		mv.visitTypeInsn(CHECKCAST, paramType.getInternalName());

		if(setterParamType.getName().startsWith("java.lang")) {

    		if(paramType.equals(TYPE_INTEGER_OBJ)) {

    			paramType = autobox(mv, setterName, beanClass, paramType, Type.INT_TYPE);

			}

    		if(paramType.equals(TYPE_LONG_OBJ)) {

    			paramType = autobox(mv, setterName, beanClass, paramType, Type.LONG_TYPE);

			}

    		if(paramType.equals(TYPE_BOOLEAN_OBJ)) {

    			paramType = autobox(mv, setterName, beanClass, paramType, Type.BOOLEAN_TYPE);

			}

    		if(paramType.equals(TYPE_FLOAT_OBJ)) {

    			paramType = autobox(mv, setterName, beanClass, paramType, Type.FLOAT_TYPE);

			}

    		if(paramType.equals(TYPE_DOUBLE_OBJ)) {

    			paramType = autobox(mv, setterName, beanClass, paramType, Type.DOUBLE_TYPE);

			}

    		if(paramType.equals(TYPE_CHARACTER_OBJ)) {
    			paramType = autobox(mv, setterName, beanClass, paramType, Type.CHAR_TYPE);
    		}

    		if(paramType.equals(TYPE_BYTE_OBJ)) {

    			paramType = autobox(mv, setterName, beanClass, paramType, Type.BYTE_TYPE);

	    	}

    		if(paramType.equals(TYPE_SHORT_OBJ)) {

    			paramType = autobox(mv, setterName, beanClass, paramType, Type.SHORT_TYPE);

			}


		}


		mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(beanClass), setterName, "("+ paramType.getDescriptor() +")V");

	}

	private Type autobox(MethodVisitor mv, String setterName, Class<?> beanClass, Type paramType, Type typePrimative) {

		try {

			String methodDescriptionParam = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { paramType });
			String methodDescriptionPrimative = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { typePrimative });

			ClassSearcher cs = new ClassSearcher(setterName, new String[] { methodDescriptionParam, methodDescriptionPrimative});
			ClassReader cr = new ClassReader(beanClass.getName());
			cr.accept(cs, 0);

			boolean[] result = cs.getResult();
			if(!result[0] && result[1]) {

				mv.visitMethodInsn(INVOKEVIRTUAL, paramType.getInternalName(), typePrimative.getClassName() + "Value", "()" + typePrimative.getDescriptor());

				return typePrimative;
			}

		} catch (IOException e) {
			throw new RuntimeException("Could not parse class '" + beanClass.getName() + "'", e);
		}


		return paramType;
	}

	private String getBcClassName(String name) {
		return name.replace('.', '/');
	}

	private String safeClassName(String name) {
		return name.replace('.', '_')
				.replace('[', '_')
				.replace(';', '_');
	}

}
