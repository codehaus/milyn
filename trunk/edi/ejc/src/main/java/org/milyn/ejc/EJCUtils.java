/*
	Milyn - Copyright (C) 2006 - 2010

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
package org.milyn.ejc;

//import org.milyn.ejc.classes.JType;
import org.milyn.javabean.pojogen.JType;

import java.util.HashSet;
import java.util.Collection;

/**
 * Utility class used when creating {@link org.milyn.ejc.ClassModel} and Binding file.
 */
public class EJCUtils {

    private static HashSet<String> reservedKeywords = new HashSet<String>();

    /**
     * Encodes a String into standard java class name convention. The following steps are performed
     * on the name:
     * 1. First character is set to upper case.
     * 2. Illegal characters like '-' and whitespace are removed.
     *
     * @param name the original class name.
     * @return the class name complying with standard java class name convention.
     * @throws IllegalNameException when class name is a reserved keyword in java. 
     */
    public static String encodeClassName(String name) throws IllegalNameException {
        String result = name;
        if (!name.toUpperCase().equals(name)) {
            result = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
        }
        result = EJCUtils.deleteWithPascalNotation(result, '-');
        result = EJCUtils.deleteWithPascalNotation(result, ' ');

        assertLegalName(result);

        return result;
    }

    /**
     * Encodes a String into standard java attribute name convention. The following steps are performed
     * on the name:
     * 1. First character is set to lower case.
     * 2. Illegal characters like '-' and whitespace are removed.
     * 3. If attributetype is a Collection a 's'-character is appended.
     *  
     * @param type the type of attribute.
     * @param name the original attribut name.
     * @return the attribute name complying with standard java attribute name convention.
     * @throws IllegalNameException when attribute name is a reserved keyword in java. 
     */
    public static String encodeAttributeName(JType type, String name) throws IllegalNameException {
        String result = name;
        if (!name.toUpperCase().equals(name)) {
            result = name.substring(0,1).toLowerCase() + name.substring(1, name.length());
        }
        result = EJCUtils.deleteWithPascalNotation(result, '-');
        result = EJCUtils.deleteWithPascalNotation(result, ' ');

        if(type != null && Collection.class.isAssignableFrom(type.getClass())) {
            result += "s";
        }

        if (reservedKeywords.contains(result)) {
            result = "_" + result;
        }

        return result;
    }

    /**
     * Removes all occurances of deleteChar and sets next character in value to uppercase.
     * @param value the String to perform deletion on.
     * @param deleteChar the character to delete.
     * @return the pascal notated String.
     */
    public static String deleteWithPascalNotation(String value, char deleteChar) {
        StringBuilder result = new StringBuilder();
        boolean matchPrevious = false;
        char currentChar;
        for (int i = 0; i < value.length(); i++) {
            currentChar = value.charAt(i);
            if (currentChar == deleteChar) {
                matchPrevious = true;
                continue;
            }
            if (matchPrevious) {
                currentChar = Character.toUpperCase(currentChar);
                matchPrevious = false;
            }
            result.append(currentChar);
        }
        return result.toString();
    }

    /**
     * Checks that the name is not a reserved word in java.
     * @param name the value to check.
     * @throws IllegalNameException when name is a reserved keyword in java.
     */
    private static void assertLegalName(String name) throws IllegalNameException {
        if (reservedKeywords.contains(name)) {
            throw new IllegalNameException("Illegal attribute- or class-name. The name [" + name + "] is a reserved keyword in java.");
        }
    }

    // Initialize reservedKeywords Set containing all keywords in java.
    static {        
        String[] words = new String[]{
            "abstract",
            "boolean",
            "break",
            "byte",
            "case",
            "catch",
            "char",
            "class",
            "const",
            "continue",
            "default",
            "do",
            "double",
            "else",
            "extends",
            "final",
            "finally",
            "float",
            "for",
            "goto",
            "if",
            "implements",
            "import",
            "instanceof",
            "int",
            "interface",
            "long",
            "native",
            "new",
            "package",
            "private",
            "protected",
            "public",
            "return",
            "short",
            "static",
            "strictfp",
            "super",
            "switch",
            "synchronized",
            "this",
            "throw",
            "throws",
            "transient",
            "try",
            "void",
            "volatile",
            "while",
            "true",
            "false",
            "null",
            "assert",
            "enum"
            };
        for (String w : words)
            reservedKeywords.add(w);
    }

}
