package org.milyn.javabean.asm;

import org.milyn.javabean.invocator.PropertySetMethodInvocator;

public class AsmTestStringPropertySetMethodInvocator implements
		PropertySetMethodInvocator {


	public void set(Object obj, Object arg) {
		((AsmTestObj)obj).setTestString((String)arg);
	}

}
