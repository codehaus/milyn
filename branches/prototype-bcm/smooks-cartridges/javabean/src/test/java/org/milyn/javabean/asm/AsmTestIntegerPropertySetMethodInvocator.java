package org.milyn.javabean.asm;

import org.milyn.javabean.invocator.PropertySetMethodInvocator;

public class AsmTestIntegerPropertySetMethodInvocator implements
		PropertySetMethodInvocator {


	public void set(Object obj, Object arg) {
		((AsmTestObj)obj).setTestInteger((Integer)arg);
	}

}
