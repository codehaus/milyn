package org.milyn.javabean.asm;

import org.milyn.javabean.invocator.SetMethodInvoker;

public class AsmTestIntegerPropertySetMethodInvocator implements
		SetMethodInvoker {


	public void set(Object obj, Object arg) {
		((AsmTestObj)obj).setTestInteger((Integer)arg);
	}

}
