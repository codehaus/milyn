/*
	Milyn - Copyright (C) 2006

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software 
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
    
	See the GNU Lesser General Public License for more details:    
	http://www.gnu.org/licenses/lgpl.txt
*/

package org.milyn.smooks.beanshell;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.css.CSSAccessor;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.milyn.logging.SmooksLogger;
import org.w3c.dom.Element;

import bsh.BshMethod;
import bsh.Interpreter;
import bsh.NameSpace;

/**
 * BeanShell transformation unit.
 * <p/>
 * Uses the <a href="http://www.beanshell.org/">BeanShell</a> Java scripting 
 * framework for performing transformations.  This allows the code to be added as a
 * &lt;param /&gt; element in the .cdrl.
 * <p/>
 * <b>Note</b>: We recommend not using BeanShell scripting where performance is 
 * an issue.
 * <p/>
 * Example cdrl Configuration:<br/>
 * <pre>
	&lt;cdres selector="*" path="org/milyn/delivery/trans/BeanShellTransUnit.class"&gt;
		&lt;param name="visitBefore"&gt;true&lt;/param&gt; &lt;!-- default false --&gt;
		&lt;param name="code"&gt; &lt;!--
			org.milyn.magger.{@link org.milyn.magger.CSSProperty} fontVariant = {@link org.milyn.css.CSSAccessor cssAccessor}.getProperty(element, "font-variant");
			org.milyn.magger.CSSProperty textTransform = cssAccessor.getProperty({@link org.w3c.dom.Element element}, "text-transform");
			
			if(fontVariant == null || textTransform == null) {
				// Do something...
			}
			if(fontVariant.getValue().getStringValue().equals("small-caps")) {
				String textTransformVal = textTransform.getValue().getStringValue();
				if(textTransformVal.equals("uppercase") || textTransformVal.equals("lowercase")) {
					// Do something else....
				}
			}			
			// Other available script variables: {@link org.milyn.container.ContainerRequest request} and {@link org.milyn.cdr.CDRDef cdrDef}. 
			--&gt;
		&lt;/param&gt;
	&lt;/cdres&gt;
 * </pre>
 * @author tfennelly
 */
public class BeanShellTransUnit extends AbstractTransUnit {
	
	private CDRDef cdrDef;
	private boolean visitBefore = false;
	private BshMethod method;
	private boolean usesCssAccessor = false;
	private Interpreter bsh;

	public BeanShellTransUnit(CDRDef cdrDef) {
		super(cdrDef);
		this.cdrDef = cdrDef;
		this.visitBefore = cdrDef.getBoolParameter("visitBefore", false);
		
		bsh = new Interpreter();
		
		try {
			String code = cdrDef.getStringParameter("code", "");
			StringBuffer methodString = new StringBuffer();

			usesCssAccessor = (code.indexOf("cssAccessor") != -1);
			
			// Construct the method signature.
			methodString.append("public void visit(").append(Element.class.getName()).append(" element, ");
			methodString.append(ContainerRequest.class.getName()).append(" request, ");
			if(usesCssAccessor) {
				methodString.append(CSSAccessor.class.getName()).append(" cssAccessor, ");
			}
			methodString.append(CDRDef.class.getName()).append(" cdrDef) {");

			// Add the method code from the cdrl
			methodString.append(code);
			
			// close the method.
			methodString.append("}");

			// Evaluate the script.
			bsh.eval(methodString.toString());
			
			// Get a reference to the "visit" method.
			NameSpace namespace = bsh.getNameSpace();
			if(usesCssAccessor) {
				method = namespace.getMethod("visit", new Class[] {Element.class, ContainerRequest.class, CSSAccessor.class, CDRDef.class});
			} else {
				method = namespace.getMethod("visit", new Class[] {Element.class, ContainerRequest.class, CDRDef.class});
			}
		} catch (Exception e) {
			SmooksLogger.getLog().error("Failed to construct BeanShell method instance for 'visit' script.", e);
			IllegalStateException state = new IllegalStateException();
			state.initCause(e);
			throw state;
		}
		
		if(method == null) {
			throw new IllegalStateException("Failed to construct BeanShell method instance.  Check method signature.");
		}
	}

	public void visit(Element element, ContainerRequest request) {
		try {
			if(usesCssAccessor) {
				CSSAccessor cssAccessor = CSSAccessor.getInstance(request);
				method.invoke(new Object[] {element, request, cssAccessor, cdrDef}, bsh);
			} else {
				method.invoke(new Object[] {element, request, cdrDef}, bsh);
			}
		} catch (Exception e) {
			SmooksLogger.getLog().error("Error executing BeanShell method instance for 'visit' script.", e);
		}
	}

	public boolean visitBefore() {
		return visitBefore;
	}
}
