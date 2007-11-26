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
	
	private CalendarDecoder decoder;
	private String languageCode;
	private String countryCode;
	
	public void test_CalendarDecoder() {
	    SmooksResourceConfiguration config = new SmooksResourceConfiguration();
	    config.setParameter(CalendarDecoder.FORMAT, "EEE MMM dd HH:mm:ss z yyyy");
	    decoder.setConfiguration(config);

	    Calendar cal_a = (Calendar) decoder.decode("Wed Nov 15 13:45:28 EST 2006");
	    assertEquals(1163616328000L, cal_a.getTimeInMillis());
	    assertEquals("Eastern Standard Time", cal_a.getTimeZone().getDisplayName());
	    Calendar cal_b = (Calendar) decoder.decode("Wed Nov 15 13:45:28 EST 2006");
	    assertNotSame(cal_a, cal_b);
	}

	public void test_locale_default() {
	    final Locale locale = decoder.getLocale( null , null );
	    assertEquals(Locale.getDefault(),locale);
	}

	public void test_locale_languageCode() {
	    final Locale locale = decoder.getLocale( languageCode, null );
	    assertEquals(new Locale(languageCode), locale);
	}

	public void test_locale_languageCode_and_coutryCode() {
	    final Locale locale = decoder.getLocale( languageCode, countryCode );
	    assertEquals(new Locale(languageCode, countryCode), locale);
	}

	public void test_CalendarDecoder_with_locale() {
	    SmooksResourceConfiguration config;

	    config = new SmooksResourceConfiguration();
	    config.setParameter(CalendarDecoder.FORMAT, "EEE MMM dd HH:mm:ss yyyy");
	    config.setParameter(CalendarDecoder.LOCALE_LANGUAGE_CODE, "se");
	    config.setParameter(CalendarDecoder.LOCALE_COUNTRY_CODE, "SE");
	    decoder.setConfiguration(config);

	    Calendar cal_a = (Calendar) decoder.decode("Wed Nov 15 13:45:28 2006");
	    assertEquals("Central European Time", cal_a.getTimeZone().getDisplayName());
	    Calendar cal_b = (Calendar) decoder.decode("Wed Nov 15 13:45:28 2006");
	    assertNotSame(cal_a, cal_b);
	}
	
	public void setUp() {
	    decoder = new CalendarDecoder();
	    languageCode = "sv";
	    countryCode = "SE";
	}

}

