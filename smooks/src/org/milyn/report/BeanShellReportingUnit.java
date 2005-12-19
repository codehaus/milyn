/*
	Milyn - Copyright (C) 2003

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

package org.milyn.report;

import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerRequest;
import org.milyn.css.CSSAccessor;
import org.milyn.logging.SmooksLogger;
import org.w3c.dom.Element;

import bsh.BshMethod;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.Primitive;

/**
 * BeanShell report unit.
 * <p/>
 * Uses the <a href="http://www.beanshell.org/">BeanShell</a> Java scripting 
 * framework for triggering node reports.  This allows the code to be added as a
 * &lt;param /&gt; element in the cdr.
 * <p/>
 * Example:<br/>
 * <pre>
 * 	
 * </pre>
 * @author tfennelly
 */
public class BeanShellReportingUnit extends AbstractReportingUnit {
	
	private CDRDef cdrDef;
	private BshMethod method;

	public BeanShellReportingUnit(CDRDef cdrDef) {
		super(cdrDef);
		this.cdrDef = cdrDef;
		Interpreter bsh = new Interpreter();
		
		try {
			StringBuffer methodString = new StringBuffer();
			
			// Construct the method signature.
			methodString.append("public boolean report(").append(Element.class.getName()).append(" element, ");
			methodString.append(ContainerRequest.class.getName()).append(" request, ");
			methodString.append(CSSAccessor.class.getName()).append(" cssAccessor, ");
			methodString.append(CDRDef.class.getName()).append(" cdrDef) {");

			// Add the method code from the cdrl
			methodString.append(cdrDef.getStringParameter("code"));
			
			// close the method.
			methodString.append("return false; }");

			// Evaluate the script.
			bsh.eval(methodString.toString());
			
			// Get a reference to the "report" method.
			NameSpace namespace = bsh.getNameSpace();
			method = namespace.getMethod("report", new Class[] {Element.class, ContainerRequest.class, CSSAccessor.class, CDRDef.class});
		} catch (Exception e) {
			SmooksLogger.getLog().error("Failed to construct BeanShell method instance for report script.", e);
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
			CSSAccessor cssAccessor = CSSAccessor.getInstance(request);
			Object retVal = method.invoke(new Object[] {element, request, cssAccessor, cdrDef}, new Interpreter());
			if(retVal instanceof Primitive) {
				Primitive prim = (Primitive)retVal;
				if(!prim.isNumber() && prim.booleanValue()) {
					report(element, request);
				}
			}
		} catch (Exception e) {
			SmooksLogger.getLog().error("Error executing BeanShell method instance for report script.", e);
		}
	}
}
