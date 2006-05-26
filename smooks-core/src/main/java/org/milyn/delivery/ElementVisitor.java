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

package org.milyn.delivery;

import org.milyn.container.ContainerRequest;
import org.w3c.dom.Element;

/**
 * Element <b>Visitor</b> (GoF) interface.
 * <p/>
 * {@link org.milyn.delivery.SmooksXML} filters (analyses/transforms) XML/XHTML/HTML markup 
 * by "visting" the DOM {@link org.w3c.dom.Element} nodes through a series of iterations over 
 * the source XML DOM.  
 * <p/>
 * This interface defines the methods for this "visiting" process. 
 * Implementations of this interface (via {@link org.milyn.delivery.assemble.AssemblyUnit}
 * and {@link org.milyn.delivery.process.ProcessingUnit}) provide a means of hooking analysis
 * and transformation logic into the {@link org.milyn.delivery.SmooksXML} filtering process
 * via implementation of the {@link #visit(Element, ContainerRequest)} method.
 * @author tfennelly
 */
public interface ElementVisitor extends ContentDeliveryUnit {
	/**
	 * Visit the supplied element.
	 * <p/>
	 * Implementations of this method should contain logic to perform an 
	 * operation on (transformation and/or analysis) the supplied DOM element.
	 * @param element The DOM element being visited.
	 * @param containerRequest Request relative instance.
	 */
	public abstract void visit(Element element, ContainerRequest containerRequest);
	
	/**
	 * Call the {@link #visit(Element, ContainerRequest)} method of this ElementVisitor 
	 * before iterating over the associated elements child content.
	 * <p/>
	 * This method simply controls when the {@link #visit(Element, ContainerRequest)} method is
	 * called on the target element relative to Smooks iterating over the target elements child 
	 * content i.e. before (true) or after (false).
	 * @return <code>true</code> if the {@link #visit(Element, ContainerRequest)} method 
	 * should be called before iterating over the target element's child content, <code>false</code> 
	 * if the {@link #visit(Element, ContainerRequest)} method should be called after iterating 
	 * over the target element's child content.
	 */
	public abstract boolean visitBefore();
}