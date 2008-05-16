package org.milyn.javabean.bcm.asm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassSearcher implements ClassVisitor {

	private final String methodName;

	private final String[] methodDescriptions;

	private final boolean[] result;

	/**
	 * @param methodName
	 * @param methodDescription
	 */
	public ClassSearcher(String methodName, String[] parameterDescription) {
		this.methodName = methodName;
		this.methodDescriptions = parameterDescription;

		result = new boolean[parameterDescription.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = false;
		}
	}

	public void visit(int version, int access, String name,
			String signature, String superName, String[] interfaces) {
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return null;
	}

	public void visitAttribute(Attribute attr) {
	}

	public void visitEnd() {
	}

	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		return null;
	}

	public void visitInnerClass(String name, String outerName,
			String innerName, int access) {
	}

	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {

		if(access == ACC_PUBLIC && methodName.equals(name)) {
			for (int i = 0; i < methodDescriptions.length; i++) {
				String methodDescription = methodDescriptions[i];

				if(methodDescription.equals(desc)) {
					result[i] = true;
				}
			}

		}

		return null;
	}

	public void visitOuterClass(String owner, String name, String desc) {
	}

	public void visitSource(String source, String debug) {
	}

	/**
	 * @return the result
	 */
	public boolean[] getResult() {
		return result;
	}

}
