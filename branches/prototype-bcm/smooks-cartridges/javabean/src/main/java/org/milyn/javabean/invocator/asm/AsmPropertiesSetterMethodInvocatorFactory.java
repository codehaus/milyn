/**
 *
 */
package org.milyn.javabean.invocator.asm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_5;

import org.milyn.container.ApplicationContext;
import org.milyn.javabean.bcm.BcmClassLoader;
import org.milyn.javabean.bcm.BcmUtils;
import org.milyn.javabean.invocator.PropertySetMethodInvocator;
import org.milyn.javabean.invocator.PropertySetMethodInvocatorFactory;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
public class AsmPropertiesSetterMethodInvocatorFactory implements
		PropertySetMethodInvocatorFactory {


	private BcmClassLoader classLoader;

	private boolean initialized = false;

	/* (non-Javadoc)
	 * @see org.milyn.javabean.invocator.PropertySetMethodInvocatorFactory#initialize(org.milyn.container.ApplicationContext)
	 */
	public void initialize(ApplicationContext applicationContext) {

		classLoader = BcmUtils.getClassloader(applicationContext);

		initialized = true;
	}

	/* (non-Javadoc)
	 * @see org.milyn.javabean.invocator.PropertySetMethodInvocatorFactory#create(java.lang.String, java.lang.Class, java.lang.Class)
	 */
	public PropertySetMethodInvocator create(String setterName,
			Class<?> beanClass, Class<?> setterParamType) {

		if(!initialized) {
			throw new IllegalStateException("Factory not initizialed. Call the #initialize(ApplicationContext) first.");
		}

		// smi = SetterMethodInvocator
		Class<?> smiClass = null;

		String smiClassName = "org.milyn.javabean.invocator.asm._generated." + safeClassName(beanClass.getName() + "_" + setterName + "_" + setterParamType.getName());

		smiClass = classLoader.load(smiClassName);

		if(smiClass == null) {

			ClassWriter cw = new ClassWriter(0);
			MethodVisitor mv;

			String bcSMIClassname = getBcClassName(smiClassName);

			cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, bcSMIClassname, null, "java/lang/Object", new String[] { getBcClassName(PropertySetMethodInvocator.class) });

			createConstructor(cw, bcSMIClassname);

			createSetMethod(cw, setterName, beanClass, setterParamType,	bcSMIClassname);

			cw.visitEnd();

			byte[] byteCode =  cw.toByteArray();

			smiClass = classLoader.load(smiClassName, byteCode);
		}


    	try {
			return (PropertySetMethodInvocator) smiClass.newInstance();
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
	 * @param cw
	 * @param bcSMIClassname
	 */
	private void createSetMethod(ClassWriter cw, String setterName, Class<?> beanClass,
			Class<?> setterParamType, String bcSMIClassname) {

		MethodVisitor mv;
		String bcBeanClassName = getBcClassName(beanClass);
		String bcSetterParamClassName = getBcClassName(setterParamType);

		mv = cw.visitMethod(ACC_PUBLIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(10, l0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST, bcBeanClassName);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitTypeInsn(CHECKCAST, bcSetterParamClassName);
		mv.visitMethodInsn(INVOKEVIRTUAL, bcBeanClassName, setterName, "(L"+ bcSetterParamClassName +";)V");
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

	private String getBcClassName(Class<?> clazz) {
		return getBcClassName(clazz.getName());
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
