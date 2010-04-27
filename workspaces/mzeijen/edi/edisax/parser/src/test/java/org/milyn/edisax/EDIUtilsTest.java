package org.milyn.edisax;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.milyn.edisax.model.EDIConfigDigester;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Segment;
import org.milyn.io.StreamUtils;

/**
 * @author bardl
 */
public class EDIUtilsTest extends TestCase {

	public void test_with_escape() throws IOException, SAXException {


        String[] test = EDIUtils.split("ATS+hep:iee+hai??+kai=haikai+slut", "+", "?");
        String[] expected = new String[]{"ATS", "hep:iee", "hai?+kai=haikai", "slut"};
        assertTrue("Result is [" + output(test) + "] should be [" + output(expected) + "] ", equal(test, expected));

        test = EDIUtils.split("ATS+hep:iee+hai?#?#+kai=haikai+slut", "+", "?#");
        expected = new String[]{"ATS", "hep:iee", "hai?#+kai=haikai", "slut"};
        assertTrue("Result is [" + output(test) + "] should be [" + output(expected) + "] ", equal(test, expected));

        test = EDIUtils.split("ATS+#hep:iee+#hai?#?#+#kai=haikai+#slut", "+#", "?#");
        expected = new String[]{"ATS", "hep:iee", "hai?#+#kai=haikai", "slut"};
        assertTrue("Result is [" + output(test) + "] should be [" + output(expected) + "] ", equal(test, expected));

        test = EDIUtils.split("ATS+#hep:iee+#hai??+#kai=haikai+#slut", "+#", "?");
        expected = new String[]{"ATS", "hep:iee", "hai?+#kai=haikai", "slut"};
        assertTrue("Result is [" + output(test) + "] should be [" + output(expected) + "] ", equal(test, expected));

        test = EDIUtils.split("ATS+#hep:iee+#hai??+#kai=haikai+#slut", "+#", null);
        expected = new String[]{"ATS", "hep:iee", "hai??", "kai=haikai", "slut"};
        assertTrue("Result is [" + output(test) + "] should be [" + output(expected) + "] ", equal(test, expected));

        // Test restarting escape sequence within escape sequence.
        test = EDIUtils.split("ATS+hep:iee+hai??#+kai=haikai+slut", "+", "?#");
        expected = new String[]{"ATS", "hep:iee", "hai?+kai=haikai", "slut"};
        assertTrue("Result is [" + output(test) + "] should be [" + output(expected) + "] ", equal(test, expected));

        // Test restarting delimiter sequence within delimiter sequence.
        test = EDIUtils.split("ATS++#hep:iee+#hai?+#kai=haikai+#slut", "+#", "?");
        expected = new String[]{"ATS+", "hep:iee", "hai+#kai=haikai", "slut"};
        assertTrue("Result is [" + output(test) + "] should be [" + output(expected) + "] ", equal(test, expected));

    }

	public void test_without_escape() {
        String[] result = EDIUtils.split(null, "*", null);        
        assertTrue("Result is [" + output(result) + "] should be [null] ", result == null);

        result = EDIUtils.split("", null, null);
        String[] expected = new String[0];
        assertTrue("Result is [" + output(result) + "] should be [" + output(expected) + "] ", equal(result, expected));

        result = EDIUtils.split("abc def", null, null);
        expected = new String[]{"abc", "def"};
        assertTrue("Result is [" + output(result) + "] should be [" + output(expected) + "] ", equal(result, expected));

        result = EDIUtils.split("abc def", " ", null);
        expected = new String[]{"abc", "def"};
        assertTrue("Result is [" + output(result) + "] should be [" + output(expected) + "] ", equal(result, expected));

        result = EDIUtils.split("abc  def", " ", null);
        expected = new String[]{"abc", "", "def"};
        assertTrue("Result is [" + output(result) + "] should be [" + output(expected) + "] ", equal(result, expected));

        result = EDIUtils.split("ab:cd:ef", ":", null);
        expected = new String[]{"ab", "cd", "ef"};
        assertTrue("Result is [" + output(result) + "] should be [" + output(expected) + "] ", equal(result, expected));

        result = EDIUtils.split("ab:cd:ef:", ":", null);
        expected = new String[]{"ab", "cd", "ef", ""};
        assertTrue("Result is [" + output(result) + "] should be [" + output(expected) + "] ", equal(result, expected));

        result = EDIUtils.split("ab:cd:ef::", ":", null);
        expected = new String[]{"ab", "cd", "ef", "", ""};
        assertTrue("Result is [" + output(result) + "] should be [" + output(expected) + "] ", equal(result, expected));

        result = EDIUtils.split(":cd:ef", ":", null);
        expected = new String[]{"", "cd", "ef"};
        assertTrue("Result is [" + output(result) + "] should be [" + output(expected) + "] ", equal(result, expected));

        result = EDIUtils.split("::cd:ef", ":", null);
        expected = new String[]{"", "", "cd", "ef"};
        assertTrue("Result is [" + output(result) + "] should be [" + output(expected) + "] ", equal(result, expected));

        result = EDIUtils.split(":cd:ef:", ":", null);
        expected = new String[]{"", "cd", "ef", ""};
        assertTrue("Result is [" + output(result) + "] should be [" + output(expected) + "] ", equal(result, expected));


	}

    private String output(String[] value) {
        if (value == null) {
            return null;
        }

        String result = "{";
        String str;
        for (int i = 0; i < value.length; i++) {
            str = value[i];
            result += "\"" + str + "\"";
            if (i != value.length -1) {
                result += ", ";
            }
        }
        result += "}";
        return result;
    }

    private static boolean equal(String[] test, String[] expected) {
        if (test.length != expected.length) {
            return false;
        }

        for (int i = 0; i < test.length; i++) {
            if (!test[i].equals(expected[i])) {
                return false;
            }
        }
        return true;
    }
}
