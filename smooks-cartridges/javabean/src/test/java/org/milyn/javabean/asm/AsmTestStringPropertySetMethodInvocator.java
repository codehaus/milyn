package org.milyn.javabean.asm;

import org.milyn.javabean.invoker.SetMethodInvoker;

public class AsmTestStringPropertySetMethodInvocator implements
		SetMethodInvoker {


	public void set(Object obj, Object arg) {
		((AsmTestObj)obj).setTestString((String)arg);
	}

}
