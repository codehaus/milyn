package org.milyn.javabean.asm;

import org.milyn.javabean.invoker.SetMethodInvoker;

public class AsmTestArrayPropertySetMethodInvocator implements
		SetMethodInvoker {


	public void set(Object obj, Object arg) {
		((AsmTestObj)obj).setTestIntArray((int[])arg);
	}

}
