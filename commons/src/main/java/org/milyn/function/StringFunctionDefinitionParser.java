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
package org.milyn.function;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts a StringFunction definition into a function list.
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 */
public class StringFunctionDefinitionParser {

    public static final TrimFunction TRIM_FUNCTION = new TrimFunction();
    public static final LeftTrimFunction LEFT_TRIM_FUNCTION = new LeftTrimFunction();
    public static final RightTrimFunction RIGHT_TRIM_FUNCTION = new RightTrimFunction();
    public static final UpperCaseFunction UPPER_CASE_FUNCTION = new UpperCaseFunction();
    public static final LowerCaseFunction LOWER_CASE_FUNCTION = new LowerCaseFunction();
    public static final CapitalizeFunction CAPITALIZE_FUNCTION = new CapitalizeFunction();
    public static final CapitalizeFirstFunction CAPITALIZE_FIRST_FUNCTION = new CapitalizeFirstFunction();
    public static final UncapitalizeFirstFunction UNCAPITALIZE_FIRST_FUNCTION = new UncapitalizeFirstFunction();

    private StringFunctionDefinitionParser() {
    }

    public static List<StringFunction> parse(String definition) {
        List<StringFunction> functions = new ArrayList<StringFunction>();

        String[] functionsDef = StringUtils.split(definition, '.');

        for(String functionDef : functionsDef) {
            if(functionDef.equals("trim")) {
                functions.add(TRIM_FUNCTION);
            }else if(functionDef.equals("ltrim")) {
                functions.add(LEFT_TRIM_FUNCTION);
            }else if(functionDef.equals("rtrim")) {
                functions.add(RIGHT_TRIM_FUNCTION);
            } else if(functionDef.equals("upper_case")) {
                functions.add(UPPER_CASE_FUNCTION);
            } else if(functionDef.equals("lower_case")) {
                functions.add(LOWER_CASE_FUNCTION);
            }  else if(functionDef.equals("capitalize")) {
                functions.add(CAPITALIZE_FUNCTION);
            }  else if(functionDef.equals("cap_first")) {
                functions.add(CAPITALIZE_FIRST_FUNCTION);
            }  else if(functionDef.equals("uncap_first")) {
                functions.add(UNCAPITALIZE_FIRST_FUNCTION);
            }
        }

        return functions;
    }

}
