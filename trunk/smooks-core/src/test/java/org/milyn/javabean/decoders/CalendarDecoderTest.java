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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import junit.framework.TestCase;

import org.milyn.cdr.SmooksResourceConfiguration;

/**
 * Tests for the Calendar and Date decoders.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 * @author <a href="mailto:daniel.bevenius@gmail.com">daniel.bevenius@gmail.com</a>
 */
public class CalendarDecoderTest extends TestCase {

	public void test_CalendarDecoder() {
	    SmooksResourceConfiguration config = new SmooksResourceConfiguration();
	    config.setParameter(CalendarDecoder.FORMAT, "EEE MMM dd HH:mm:ss z yyyy");

	    CalendarDecoder decoder = new CalendarDecoder();
	    config.setParameter(CalendarDecoder.LOCALE_LANGUAGE_CODE, "en");
	    config.setParameter(CalendarDecoder.LOCALE_COUNTRY_CODE, "IE");
	    decoder.setConfiguration(config);

	    Calendar cal_a = (Calendar) decoder.decode("Wed Nov 15 13:45:28 EST 2006");
	    assertEquals(1163616328000L, cal_a.getTimeInMillis());
	    assertEquals("Eastern Standard Time", cal_a.getTimeZone().getDisplayName( new Locale("en", "IE") ) );
	    Calendar cal_b = (Calendar) decoder.decode("Wed Nov 15 13:45:28 EST 2006");
	    assertNotSame(cal_a, cal_b);
	}

	public void test_CalendarDecoder_with_swedish_local() throws ParseException {
		final String dateFormat = "EEE MMM dd HH:mm:ss z yyyy";
		final String dateString = "sö mar 02 15:25:07 CET 2008";

	    SmooksResourceConfiguration config = new SmooksResourceConfiguration();
	    CalendarDecoder decoder = new CalendarDecoder();

	    config.setParameter(CalendarDecoder.FORMAT, dateFormat );
	    config.setParameter(CalendarDecoder.LOCALE_LANGUAGE_CODE, "sv");
	    config.setParameter(CalendarDecoder.LOCALE_COUNTRY_CODE, "SE");
	    decoder.setConfiguration(config);

	    Calendar cal_a = (Calendar) decoder.decode( dateString );
	    assertEquals("Europe/Stockholm", cal_a.getTimeZone().getID() );
	    assertEquals("Centraleuropeisk tid", cal_a.getTimeZone().getDisplayName( new Locale("sv", "SE") ) );

	    Calendar cal_b = (Calendar) decoder.decode( dateString );
	    assertNotSame(cal_a, cal_b);
	}

	public void setUp() {
		Locale.setDefault( new Locale("de", "DE") );
		//Locale.setDefault( new Locale("ga", "IE") );
	}

}

