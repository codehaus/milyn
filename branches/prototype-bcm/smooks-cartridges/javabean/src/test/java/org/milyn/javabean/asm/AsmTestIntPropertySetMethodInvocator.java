package org.milyn.javabean.asm;

import org.milyn.javabean.invocator.PropertySetMethodInvocator;

public class AsmTestIntPropertySetMethodInvocator implements
		PropertySetMethodInvocator {


	public void set(Object obj, Object arg) {
		((AsmTestObj)obj).setTestInt((Integer)arg);
	}

}
