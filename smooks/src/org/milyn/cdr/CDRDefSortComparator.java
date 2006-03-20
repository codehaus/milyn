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

package org.milyn.cdr;

import java.util.Comparator;

import org.milyn.device.UAContext;


/**
 * Sort Comparator for {@link org.milyn.cdr.CDRDef} Objects based on their "specificity".
 * <p/>
 * Before reading this be sure to read the {@link org.milyn.cdr.CDRDef} class Javadoc.
 * <p/>
 * As Smooks applies {@link org.milyn.delivery.ContentDeliveryUnit}s ({@link org.milyn.delivery.assemble.AssemblyUnit}s, {@link org.milyn.delivery.trans.TransUnit}s and
 * {@link org.milyn.delivery.serialize.SerializationUnit}s) it may discover that in a given case more than 1 {@link org.milyn.delivery.ContentDeliveryUnit}
 * can be applied.  How does Smooks decide on the order in which these {@link org.milyn.delivery.ContentDeliveryUnit}s are to be applied to the content?
 * <p/>
 * At the moment, Smooks uses this class to calculate a "specificity" rating for each {@link org.milyn.delivery.ContentDeliveryUnit} based on its 
 * {@link org.milyn.cdr.CDRDef &lt;cdres&gt;} configuration and sorts the {@link org.milyn.delivery.ContentDeliveryUnit}s in decreasing order of specificity.
 * <p/>
 * The following outlines how this specificity value is calculated at present.  This
 * "algorithm" wasn't arrived at through any real scientific process so it could
 * be very questionable:
 * <!-- Just cut-n-paste from the code -->
 * <pre>
	// Check the 'uatarget' attribute specificity by adding the specificity
	// of all its uatarget expressions.
	{@link org.milyn.cdr.UATargetExpression}[] uaTargets = cdrDef.{@link org.milyn.cdr.CDRDef#getUaTargetExpressions() getUaTargetExpressions()};
	for(int i = 0; i < uaTargets.length; i++) {
		specificity += uaTargets[i].{@link org.milyn.cdr.UATargetExpression#getSpecificity(UAContext) getSpecificity(deviceContext)};
	}
	
	// Check the 'selector' attribute value.
	if(cdrDef.{@link org.milyn.cdr.CDRDef#isXmlDef() isXmlDef()}) {
		specificity += 1;
	} else if(cdrDef.{@link org.milyn.cdr.CDRDef#getSelector() getselector()}.equals("*")) {
		specificity += 10;
	} else {
		// Explicit selector listed
		specificity += 100;
	}
		
	// Check the 'namespace' attribute.
	if(cdrDef.{@link org.milyn.cdr.CDRDef#getNamespaceURI() getNamespaceURI()} != null) {
		specificity += 10;
	}</pre>  
 * For more details on this please refer to the code in this class.
 * 
 * @author tfennelly
 */

public class CDRDefSortComparator implements Comparator {

	/**
	 * Browser/device context.
	 */
	private UAContext deviceContext;

	/**
	 * Private constructor.
	 * @param deviceContext Device context.
	 */
	public CDRDefSortComparator(UAContext deviceContext) {
		this.deviceContext = deviceContext;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object unitDefObj1, Object unitDefObj2) {
		CDRDef unitDef1 = (CDRDef)unitDefObj1;
		CDRDef unitDef2 = (CDRDef)unitDefObj2;

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
	 * Get the specificity of the CDRDef.
	 * <p/>
	 * The "specificity" is evaluated based on the selector and uatarget values.
	 * Equal precedence is given to both attribute values.  
	 * @param cdrDef
	 * @return
	 */
	private double getSpecificity(CDRDef cdrDef) {
		double specificity = 0;
		
		// If the following code is modified, please update the class Javadoc.

		// Check the 'uatarget' attribute specificity by adding the specificity
		// of all its uatarget expressions.
		UATargetExpression[] uaTargets = cdrDef.getUaTargetExpressions();
		for(int i = 0; i < uaTargets.length; i++) {
			specificity += uaTargets[i].getSpecificity(deviceContext);
		}
		
		// Check the 'selector' attribute value.
		if(cdrDef.isXmlDef()) {
			specificity += 1;
		} else if(cdrDef.getSelector().equals("*")) {
			specificity += 10;
		} else {
			// Explicit selector listed
			specificity += 100;
		}
		
		// Check the 'namespace' attribute.
		if(cdrDef.getNamespaceURI() != null) {
			specificity += 10;
		}
		
		return specificity;
	}
	
	/**
	 * Is the uaContext device listed in the supplied list.
	 * @param list CDRDef device list.
	 * @return True if the device common name is in the supplied list, otherwise false.
	 */
	private boolean containsDevice(String[] list) {
		for(int i = 0; i < list.length; i++) {
			if(list[i].equals(deviceContext.getCommonName())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Is there an "*" in the supplied list.
	 * @param list CDRDef device list.
	 * @return True if an "*" is in the supplied list, otherwise false.
	 */
	private boolean containsAstrix(String[] list) {
		for(int i = 0; i < list.length; i++) {
			if(list[i].equals("*")) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Is there a matching profile in the supplied device list.
	 * @param list CDRDef device list.
	 * @return True if there's a matching profile in the supplied device list, otherwise false.
	 */
	private boolean containsMatchingProfile(String[] list) {
		for(int i = 0; i < list.length; i++) {
			if(deviceContext.getProfileSet().isMember(list[i])) {
				return true;
			}
		}

		return false;
	}
}