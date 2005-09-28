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

package org.milyn.cdres.css;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * Simple ValueManager implementation.
 * @author tfennelly
 */
class SimpleValueManager extends AbstractValueManager {

	private String propertyName;
	
	/**
	 * Public Constructor.
	 * @param propertyName The name of the property to be associated 
	 * with this manaher instance.
	 */
	public SimpleValueManager(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * Create a Batik property Value instance from the supplied lexical unit.
	 * <p/>
	 * Doesn't support Function types.
	 */
	public Value createValue(LexicalUnit lu, CSSEngine cssEngine) throws DOMException {
		switch (lu.getLexicalUnitType()) {
		case LexicalUnit.SAC_INHERIT:
		    return ValueConstants.INHERIT_VALUE;
		case LexicalUnit.SAC_INTEGER:
            return new FloatValue(CSSPrimitiveValue.CSS_NUMBER, lu.getIntegerValue());
		case LexicalUnit.SAC_REAL:
		case LexicalUnit.SAC_DIMENSION:
		case LexicalUnit.SAC_EM:
		case LexicalUnit.SAC_EX:
		case LexicalUnit.SAC_PIXEL:
		case LexicalUnit.SAC_INCH:
		case LexicalUnit.SAC_CENTIMETER:
		case LexicalUnit.SAC_MILLIMETER:
		case LexicalUnit.SAC_POINT:
		case LexicalUnit.SAC_PICA:
		case LexicalUnit.SAC_PERCENTAGE:
		case LexicalUnit.SAC_DEGREE:
		case LexicalUnit.SAC_GRADIAN:
		case LexicalUnit.SAC_RADIAN:
		case LexicalUnit.SAC_MILLISECOND:
		case LexicalUnit.SAC_SECOND:
		case LexicalUnit.SAC_HERTZ:
		case LexicalUnit.SAC_KILOHERTZ:
            return new FloatValue(CSSPrimitiveValue.CSS_NUMBER, lu.getFloatValue());
		case LexicalUnit.SAC_COUNTER_FUNCTION:
		case LexicalUnit.SAC_COUNTERS_FUNCTION:
		case LexicalUnit.SAC_RECT_FUNCTION:
		case LexicalUnit.SAC_FUNCTION:
		case LexicalUnit.SAC_RGBCOLOR:
			break;
		case LexicalUnit.SAC_URI:
		case LexicalUnit.SAC_ATTR:
		case LexicalUnit.SAC_IDENT:
		case LexicalUnit.SAC_STRING_VALUE:
		case LexicalUnit.SAC_UNICODERANGE: 
            return new StringValue(CSSPrimitiveValue.CSS_STRING, lu.getStringValue());
		default:
			break;
		}

		return new StringValue(CSSPrimitiveValue.CSS_STRING, lu.getStringValue());
	}

	public Value getDefaultValue() {
        return ValueConstants.NONE_VALUE;
	}

	public boolean isInheritedProperty() {
		return false;
	}

	public String getPropertyName() {
		return propertyName;
	}
}
