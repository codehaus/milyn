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

package org.milyn.cdr;

import java.util.Comparator;

import org.milyn.device.UAContext;


/**
 * Sort Comparator for {@link org.milyn.cdr.SmooksResourceConfiguration} Objects based on their "specificity".
 * <p/>
 * Before reading this be sure to read the {@link org.milyn.cdr.SmooksResourceConfiguration} class Javadoc.
 * <p/>
 * As Smooks applies {@link org.milyn.delivery.ContentDeliveryUnit}s ({@link org.milyn.delivery.assemble.AssemblyUnit}s, {@link org.milyn.delivery.process.ProcessingUnit}s and
 * {@link org.milyn.delivery.serialize.SerializationUnit}s) it may discover that in a given case more than 1 {@link org.milyn.delivery.ContentDeliveryUnit}
 * can be applied.  How does Smooks decide on the order in which these {@link org.milyn.delivery.ContentDeliveryUnit}s are to be applied to the content?
 * <p/>
 * At the moment, Smooks uses this class to calculate a "specificity" rating for each Content Delivery Resource based on its 
 * {@link org.milyn.cdr.SmooksResourceConfiguration &lt;smooks-resource&gt;} configuration, and sorts them in decreasing order of specificity.
 * <p/>
 * The following outlines how this specificity value is calculated at present.
 * <!-- Just cut-n-paste from the code -->
 * <pre>
	// Check the 'useragent' attribute specificity by adding the specificity
	// of all its useragent expressions.
	{@link org.milyn.cdr.UseragentExpression}[] useragents = resourceConfig.{@link org.milyn.cdr.SmooksResourceConfiguration#getUseragentExpressions() getUseragentExpressions()};
	for(int i = 0; i < useragents.length; i++) {
		specificity += useragents[i].{@link org.milyn.cdr.UseragentExpression#getSpecificity(UAContext) getSpecificity(deviceContext)};
	}
	
	// Check the 'selector' attribute value.
	if(resourceConfig.{@link org.milyn.cdr.SmooksResourceConfiguration#isXmlDef() isXmlDef()}) {
		specificity += 1;
	} else if(resourceConfig.{@link org.milyn.cdr.SmooksResourceConfiguration#getSelector() getselector()}.equals("*")) {
		specificity += 10;
	} else {
		// Explicit selector listed
		specificity += 100;
	}
		
	// Check the 'namespace' attribute.
	if(resourceConfig.{@link org.milyn.cdr.SmooksResourceConfiguration#getNamespaceURI() getNamespaceURI()} != null) {
		specificity += 10;
	}</pre>  
 * For more details on this please refer to the code in this class.
 * 
 * @author tfennelly
 */

public class SmooksResourceConfigurationSortComparator implements Comparator {

	/**
	 * Browser/device context.
	 */
	private UAContext deviceContext;

	/**
	 * Private constructor.
	 * @param deviceContext Device context.
	 */
	public SmooksResourceConfigurationSortComparator(UAContext deviceContext) {
		this.deviceContext = deviceContext;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object unitDefObj1, Object unitDefObj2) {
		SmooksResourceConfiguration unitDef1 = (SmooksResourceConfiguration)unitDefObj1;
		SmooksResourceConfiguration unitDef2 = (SmooksResourceConfiguration)unitDefObj2;

		if(unitDef1 == unitDef2) {
			// This should never happen.
			return 0;
		}
		
		double unitDef1Specificity = getSpecificity(unitDef1);
		double unitDef2Specificity = getSpecificity(unitDef2);				
		
		// They are ordered as follow (most specific first). 
		if(unitDef1Specificity > unitDef2Specificity) {
			return -1;
		} else if(unitDef1Specificity < unitDef2Specificity) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the specificity of the SmooksResourceConfiguration.
	 * <p/>
	 * The "specificity" is evaluated based on the selector and useragent values.
	 * Equal precedence is given to both attribute values.  
	 * @param resourceConfig
	 * @return
	 */
	private double getSpecificity(SmooksResourceConfiguration resourceConfig) {
		double specificity = 0;
		
		// If the following code is modified, please update the class Javadoc.

		// Check the 'useragent' attribute specificity by adding the specificity
		// of all its useragent expressions.
		UseragentExpression[] useragents = resourceConfig.getUseragentExpressions();
		for(int i = 0; i < useragents.length; i++) {
			specificity += useragents[i].getSpecificity(deviceContext);
		}
		
		// Check the 'selector' attribute value.
		if(resourceConfig.isXmlDef()) {
			specificity += 1;
		} else if(resourceConfig.getSelector().equals("*")) {
			specificity += 10;
		} else {
			// Explicit selector listed
			specificity += 100;
		}
		
		// Check the 'namespace' attribute.
		if(resourceConfig.getNamespaceURI() != null) {
			specificity += 10;
		}
		
		return specificity;
	}
}