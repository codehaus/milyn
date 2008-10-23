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

package org.milyn.edisax;

import java.util.List;
import java.util.ArrayList;

/**
 * EDIUtils contain different helper-methods for handling edifact.
 *
 * @author bardl
 */
public class EDIUtils {

    /**
     * Splits a String by delimiter as long as delimiter does not follow an escape sequence.
     * The split method follows the same behavior as the method splitPreserveAllTokens(String, String)
     * in {@link org.apache.commons.lang.StringUtils}.
     *
     * @param value the string to split.
     * @param delimiter the delimiter sequence
     * @param escape the escape sequence
     * @return an array of split edi-sequences.  
     */
    public static String[] split(String value, String delimiter, String escape) {

        if (value == null || value.length() == 0) {
            return new String[0];
        }

        List<String> tokens = new ArrayList<String>();

        int escapeIndex = 0;
        int delimiterIndex = 0;
        boolean foundEscape = false;
        StringBuilder escapeContent = new StringBuilder();
        StringBuilder delimiterContent = new StringBuilder();
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char tmp = value.charAt(i);

            // If character equals current escape character or start of a new escape sequence.
            if (escape != null && ( tmp == escape.charAt(0) || tmp == escape.charAt(escapeIndex) )) {

                // If starting from the beginning of a new escape sequence.
                if (tmp == escape.charAt(0)) {
                    token.append(escapeContent);
                    escapeIndex = 0;
                    escapeContent = new StringBuilder();
                }

                // If we haven't found the whole escape seguence.
                if ( escapeIndex < escape.length() -1 ) {
                    escapeIndex++;
                    if (foundEscape) {
                        token.append(escapeContent);
                        escapeContent = new StringBuilder();
                    }
                    foundEscape = false;

                    // If we have found the whole escape seguence.
                } else {
                    if (foundEscape) {
                        token.append(escapeContent);
                    }
                    foundEscape = true;
                    escapeIndex = 0;
                }

                escapeContent.append(tmp);

                // If character equals current delimiter or start of a new delimiter sequence.
            } else if (tmp == delimiter.charAt(delimiterIndex) || tmp == delimiter.charAt(0)) {

                // If starting from the beginning of a new delimiter sequence.
                if ( tmp == delimiter.charAt(0) ) {
                    token.append(delimiterContent);
                    delimiterIndex = 0;
                    delimiterContent = new StringBuilder();
                }

                delimiterContent.append(tmp);
                // If we haven't found the whole delimiter sequence.
                if ( delimiterIndex < delimiter.length() -1 ) {
                    delimiterIndex++;
                    // If we have found the whole delimiter sequence.
                } else {
                    if (foundEscape) {
                        token.append(delimiterContent);
                        escapeContent = new StringBuilder();
                    } else {
                        tokens.add(token.toString());
                        token = new StringBuilder();
                    }
                    delimiterIndex = 0;
                    delimiterContent = new StringBuilder();
                }
                // If Character doesn't match current delimiter or escape character.
            } else {
                // Append and reset escape sequence if it exists.
                token.append(escapeContent);
                foundEscape = false;
                escapeContent = new StringBuilder();

                // Append and reset delimiter sequence if it exists.
                token.append(delimiterContent);
                delimiterContent = new StringBuilder();

                // Append the current character.
                token.append(value.charAt(i));
            }
        }

        tokens.add(token.toString());

        return tokens.toArray(new String[tokens.size()]);
    }

    public static void main(String[] args) {
        String[] test = EDIUtils.split("ATS+hep:iee+hai??+kai=haikai+slut", "+", "?");
        String[] expected = new String[]{"ATS", "hep:iee", "hai?+kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel1");
        }

        test = EDIUtils.split("ATS+hep:iee+hai?#?#+kai=haikai+slut", "+", "?#");
        expected = new String[]{"ATS", "hep:iee", "hai?#+kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel2");
        }

        test = EDIUtils.split("ATS+#hep:iee+#hai?#?#+#kai=haikai+#slut", "+#", "?#");
        expected = new String[]{"ATS", "hep:iee", "hai?#+#kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel3");
        }

        test = EDIUtils.split("ATS+#hep:iee+#hai??+#kai=haikai+#slut", "+#", "?");
        expected = new String[]{"ATS", "hep:iee", "hai?+#kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel4");
        }

        test = EDIUtils.split("ATS+#hep:iee+#hai??+#kai=haikai+#slut", "+#", null);
        expected = new String[]{"ATS", "hep:iee", "hai??", "kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel5");
        }

        // Test restarting escape sequence within escape sequence.
        test = EDIUtils.split("ATS+hep:iee+hai??#+kai=haikai+slut", "+", "?#");
        expected = new String[]{"ATS", "hep:iee", "hai?+kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel6");
        }

        // Test restarting delimiter sequence within delimiter sequence.
        test = EDIUtils.split("ATS++#hep:iee+#hai?+#kai=haikai+#slut", "+#", "?");
        expected = new String[]{"ATS+", "hep:iee", "hai+#kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel7");
        }

        System.out.println("");
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
