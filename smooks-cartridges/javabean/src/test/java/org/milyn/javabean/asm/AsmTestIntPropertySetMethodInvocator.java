package org.milyn.javabean.asm;

import org.milyn.javabean.invoker.SetMethodInvoker;

public class AsmTestIntPropertySetMethodInvocator implements
		SetMethodInvoker {


	public void set(Object obj, Object arg) {
		((AsmTestObj)obj).setTestInt((Integer)arg);
	}

}
