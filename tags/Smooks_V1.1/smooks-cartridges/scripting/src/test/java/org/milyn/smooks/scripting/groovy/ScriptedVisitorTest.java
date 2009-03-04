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
package org.milyn.smooks.scripting.groovy;

import junit.framework.TestCase;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.io.StreamUtils;
import org.milyn.payload.StringResult;
import org.milyn.payload.StringSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 */
public class ScriptedVisitorTest extends TestCase {

    public void test_templated_01() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-01.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource("<a><b><c/></b></a>"), result);
        assertEquals("<a><b><xxx newElementAttribute=\"1234\"></xxx></b></a>", result.getResult());
    }

    public void test_templated_ext_01() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-ext-01.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource("<a><b><c/></b></a>"), result);
        assertEquals("<a><b><xxx newElementAttribute=\"1234\"></xxx></b></a>", result.getResult());
    }

    public void test_templated_02() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-02.xml"));
        StringResult result = new StringResult();

        try {
            smooks.filter(new StringSource("<a><b><c/></b></a>"), result);
            fail("Expected SmooksException.");
        } catch(SmooksException e) {
            assertEquals("Unable to filter InputStream for target profile [org.milyn.profile.Profile#default_profile].", e.getMessage());
        }
    }

    public void test_templated_ext_02() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-ext-02.xml"));
        StringResult result = new StringResult();

        try {
            smooks.filter(new StringSource("<a><b><c/></b></a>"), result);
            fail("Expected SmooksException.");
        } catch(SmooksException e) {
            assertEquals("Unable to filter InputStream for target profile [org.milyn.profile.Profile#default_profile].", e.getMessage());
        }
    }

    public void test_templated_03() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-03.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource("<a><b><c/></b></a>"), result);
        assertEquals("<a><b><xxx newElementAttribute=\"1234\"></xxx></b></a>", result.getResult());
    }

    public void test_templated_ext_03() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-ext-03.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource("<a><b><c/></b></a>"), result);
        assertEquals("<a><b><xxx newElementAttribute=\"1234\"></xxx></b></a>", result.getResult());
    }

    public void test_templated_04() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-04.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource("<a><b><c/></b></a>"), result);
        assertEquals("<a><b><c><car make=\"Holden\" name=\"HSV Maloo\" year=\"2006\"><country>Australia</country><record type=\"speed\">Production Pickup Truck with speed of 271kph</record></car><car make=\"Peel\" name=\"P50\" year=\"1962\"><country>Isle of Man</country><record type=\"size\">Smallest Street-Legal Car at 99cm wide and 59 kg in weight</record></car><car make=\"Bugatti\" name=\"Royale\" year=\"1931\"><country>France</country><record type=\"price\">Most Valuable Car at $15 million</record></car></c></b></a>", result.getResult());
    }

    public void test_templated_ext_04() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-ext-04.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource("<a><b><c/></b></a>"), result);
        assertEquals("<a><b><c><car make=\"Holden\" name=\"HSV Maloo\" year=\"2006\"><country>Australia</country><record type=\"speed\">Production Pickup Truck with speed of 271kph</record></car><car make=\"Peel\" name=\"P50\" year=\"1962\"><country>Isle of Man</country><record type=\"size\">Smallest Street-Legal Car at 99cm wide and 59 kg in weight</record></car><car make=\"Bugatti\" name=\"Royale\" year=\"1931\"><country>France</country><record type=\"price\">Most Valuable Car at $15 million</record></car></c></b></a>", result.getResult());
    }

    public void test_templated_05() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-05.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource(shoppingList), result);
        assertEquals(
                "<shopping>\n" +
                "    <category type=\"groceries\">\n" +
                "        <item>Luxury Chocolate</item>\n" +
                "        <item>Luxury Coffee</item>\n" +
                "    </category>\n" +
                "    <category type=\"supplies\">\n" +
                "        <item>Paper</item>\n" +
                "        <item quantity=\"6\" when=\"Urgent\">Pens</item>\n" +
                "    </category>\n" +
                "    <category type=\"present\">\n" +
                "        \n" +
                "    <item>Mum's Birthday</item><item when=\"Oct 15\">Monica's Birthday</item></category>\n" +
                "</shopping>",
                result.getResult());
    }

    public void test_templated_ext_05() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-ext-05.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource(shoppingList), result);
        assertEquals(
                "<shopping>\n" +
                "    <category type=\"groceries\">\n" +
                "        <item>Luxury Chocolate</item>\n" +
                "        <item>Luxury Coffee</item>\n" +
                "    </category>\n" +
                "    <category type=\"supplies\">\n" +
                "        <item>Paper</item>\n" +
                "        <item quantity=\"6\" when=\"Urgent\">Pens</item>\n" +
                "    </category>\n" +
                "    <category type=\"present\">\n" +
                "        \n" +
                "    <item>Mum's Birthday</item><item when=\"Oct 15\">Monica's Birthday</item></category>\n" +
                "</shopping>",
                result.getResult());
    }

    public void test_templated_ext_06() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-ext-06.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource("<a><b><c/></b></a>"), result);
        assertEquals("<a><b><newElX newElementAttribute=\"1234\"></newElX></b></a>", result.getResult());
    }

    public void test_templated_ext_07() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-ext-07.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource("<a><b><c/></b></a>"), result);
    }

    public void test_templated_ext_08() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-ext-08.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource("<a><b><c/></b></a>"), result);
        assertEquals("<a><b><xxx/></b></a>", result.getResult());
    }

    public void test_templated_ext_09() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-ext-09.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource(shoppingList), result);
        assertTrue(StreamUtils.compareCharStreams(
                "<shopping>\n" +
                "    <category type=\"groceries\">\n" +
                "        <item>Luxury Chocolate</item>\n" +
                "        <item>Luxury Coffee</item>\n" +
                "    </category>\n" +
                "    <category type=\"supplies\">\n" +
                "        <item>Paper</item>\n" +
                "        <item quantity=\"6\" when=\"Urgent\">Pens</item>\n" +
                "    </category>\n" +
                "    <category type=\"present\">\n" +
                "        <item>Mum's Birthday</item>\n" +
                "        <item when=\"Oct 15\">Monica's Birthday</item>\n" +
                "    </category>\n" +
                "</shopping>",
                result.getResult()));
    }

    public void test_templated_ext_10() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-ext-10.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource(shoppingList), result);
        assertTrue(StreamUtils.compareCharStreams(
                "<shopping>\n" +
                "    <category type=\"groceries\"><item>Chocolate</item><item>Coffee</item></category>\n" +
                "    <category type=\"supplies\"><item>Paper</item><item quantity=\"6\">Pens</item></category>\n" +
                "    <category type=\"present\"><item when=\"Aug 10\">Kathryn's Birthday</item></category>\n" +
                "</shopping>",
                result.getResult()));
    }

    public void test_templated_ext_11() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("scripted-ext-11.xml"));
        StringResult result = new StringResult();

        smooks.filter(new StringSource(shoppingList), result);
        assertEquals("<category type=\"supplies\"><item>Paper</item><item quantity=\"6\">Pens</item></category>", result.getResult());
    }

    private static String shoppingList =
            "<shopping>\n" +
            "    <category type=\"groceries\">\n" +
            "        <item>Chocolate</item>\n" +
            "        <item>Coffee</item>\n" +
            "    </category>\n" +
            "    <category type=\"supplies\">\n" +
            "        <item>Paper</item>\n" +
            "        <item quantity=\"4\">Pens</item>\n" +
            "    </category>\n" +
            "    <category type=\"present\">\n" +
            "        <item when=\"Aug 10\">Kathryn's Birthday</item>\n" +
            "    </category>\n" +
            "</shopping>";
}