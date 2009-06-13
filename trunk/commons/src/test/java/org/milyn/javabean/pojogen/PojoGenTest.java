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
package org.milyn.javabean.pojogen;

import junit.framework.TestCase;

import java.io.StringWriter;
import java.io.IOException;
import java.util.List;

import org.milyn.io.StreamUtils;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class PojoGenTest extends TestCase {

    public void test_01() throws IOException {
        JavaClass aClass = new JavaClass("com.acme", "AClass");
        JavaClass bClass = new JavaClass("com.acme", "BClass");

        aClass.addProperty(new JavaNamedType(new JavaType(int.class), "primVar"));
        aClass.addProperty(new JavaNamedType(new JavaType(Double.class), "doubleVar"));
        aClass.addProperty(new JavaNamedType(new JavaType(BBBClass.class), "objVar"));
        aClass.addProperty(new JavaNamedType(new JavaType(List.class, BBBClass.class), "genericVar"));

        // Wire AClass into BClass...
        bClass.addProperty(new JavaNamedType(new JavaType(BBBClass.class), "bbbVar"));
        bClass.addProperty(new JavaNamedType(new JavaType(aClass.getSkeletonClass()), "aClassVar"));

        StringWriter aWriter = new StringWriter();
        StringWriter bWriter = new StringWriter();

        aClass.writeClass(aWriter);
        String aS = aWriter.toString();
//        System.out.println(aS);
        assertEquals(StreamUtils.trimLines(AClass_Expected), StreamUtils.trimLines(aS));

        bClass.writeClass(bWriter);
        String bS = bWriter.toString();
//        System.out.println(bS);
        assertEquals(StreamUtils.trimLines(BClass_Expected), StreamUtils.trimLines(bS));
    }

    private static String AClass_Expected = "/**\n" +
            " * This class was generated by Smooks EJC (http://www.smooks.org).\n" +
            " */\n" +
            "package com.acme;\n" +
            "\n" +
            "import java.lang.Double;    \n" +
            "import org.milyn.javabean.pojogen.BBBClass;    \n" +
            "import java.util.List;    \n" +
            "\n" +
            "public class AClass {\n" +
            "\n" +
            "    private int primVar;\n" +
            "    private Double doubleVar;\n" +
            "    private BBBClass objVar;\n" +
            "    private List<BBBClass> genericVar;\n" +
            "\n" +
            "    public int getPrimVar() {\n" +
            "        return primVar;\n" +
            "    }\n" +
            "\n" +
            "    public void setPrimVar(int primVar) {\n" +
            "        this.primVar = primVar;\n" +
            "    }\n" +
            "\n" +
            "    public Double getDoubleVar() {\n" +
            "        return doubleVar;\n" +
            "    }\n" +
            "\n" +
            "    public void setDoubleVar(Double doubleVar) {\n" +
            "        this.doubleVar = doubleVar;\n" +
            "    }\n" +
            "\n" +
            "    public BBBClass getObjVar() {\n" +
            "        return objVar;\n" +
            "    }\n" +
            "\n" +
            "    public void setObjVar(BBBClass objVar) {\n" +
            "        this.objVar = objVar;\n" +
            "    }\n" +
            "\n" +
            "    public List<BBBClass> getGenericVar() {\n" +
            "        return genericVar;\n" +
            "    }\n" +
            "\n" +
            "    public void setGenericVar(List<BBBClass> genericVar) {\n" +
            "        this.genericVar = genericVar;\n" +
            "    }\n" +
            "}";

    private static String BClass_Expected = "/**\n" +
            " * This class was generated by Smooks EJC (http://www.smooks.org).\n" +
            " */\n" +
            "package com.acme;\n" +
            "\n" +
            "import org.milyn.javabean.pojogen.BBBClass;    \n" +
            "import com.acme.AClass;    \n" +
            "\n" +
            "public class BClass {\n" +
            "\n" +
            "    private BBBClass bbbVar;\n" +
            "    private AClass aClassVar;\n" +
            "\n" +
            "    public BBBClass getBbbVar() {\n" +
            "        return bbbVar;\n" +
            "    }\n" +
            "\n" +
            "    public void setBbbVar(BBBClass bbbVar) {\n" +
            "        this.bbbVar = bbbVar;\n" +
            "    }\n" +
            "\n" +
            "    public AClass getAClassVar() {\n" +
            "        return aClassVar;\n" +
            "    }\n" +
            "\n" +
            "    public void setAClassVar(AClass aClassVar) {\n" +
            "        this.aClassVar = aClassVar;\n" +
            "    }\n" +
            "}";
}