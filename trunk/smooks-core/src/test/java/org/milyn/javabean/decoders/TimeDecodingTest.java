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
package org.milyn.javabean.decoders;

import junit.framework.TestCase;
import org.milyn.cdr.SmooksResourceConfiguration;

import java.util.Calendar;
import java.util.Date;

/**
 * Tests for the Calendar and Date decoders.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TimeDecodingTest extends TestCase {

    public void test_CalendarDecoder() {
        CalendarDecoder decoder = new CalendarDecoder();
        SmooksResourceConfiguration config;

        config = new SmooksResourceConfiguration();
        config.setParameter(CalendarDecoder.FORMAT, "EEE MMM dd HH:mm:ss z yyyy");
        decoder.setConfiguration(config);

        Calendar cal_a = (Calendar) decoder.decode("Wed Nov 15 13:45:28 EST 2006");
        assertEquals(1163616328000L, cal_a.getTimeInMillis());
        assertEquals("Eastern Standard Time", cal_a.getTimeZone().getDisplayName());
        Calendar cal_b = (Calendar) decoder.decode("Wed Nov 15 13:45:28 EST 2006");
        assertNotSame(cal_a, cal_b);
    }

    public void test_DateDecoder() {
        DateDecoder decoder = new DateDecoder();
        SmooksResourceConfiguration config;

        config = new SmooksResourceConfiguration();
        config.setParameter(CalendarDecoder.FORMAT, "EEE MMM dd HH:mm:ss z yyyy");
        decoder.setConfiguration(config);

        Date date_a = (Date) decoder.decode("Wed Nov 15 13:45:28 EST 2006");
        assertEquals(1163616328000L, date_a.getTime());
        Date date_b = (Date) decoder.decode("Wed Nov 15 13:45:28 EST 2006");
        assertNotSame(date_a, date_b);
    }
}
