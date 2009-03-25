/*
 * Milyn - Copyright (C) 2006
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (version 2.1) as published
 * by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.javabean.decoders;

import junit.framework.TestCase;

import java.util.Locale;
import java.util.Properties;

/**
 * Unit test for LocaleAwareDateDedoderTest
 *
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class LocaleAwareDateDecoderTest extends TestCase
{
	private LocaleAwareDateDecoder decoder;

	public void test_locale_default() {
	    final Locale locale = decoder.getLocale( null , null );
	    assertEquals(Locale.getDefault(),locale);
	}

	public void test_locale_languageCode() {
	    final Locale locale = decoder.getLocale( "sv", null );
	    assertEquals(new Locale("sv"), locale);
	}

	public void test_locale_languageCode_and_coutryCode() {
	    final Locale locale = decoder.getLocale( "sv", "SE" );
	    assertEquals(new Locale("sv", "SE"), locale);
	}

	public void test_non_installed_locale()
	{
	    final Locale locale = decoder.getLocale( "xx", "XX" );
        Properties config = new Properties();
	    config.setProperty(CalendarDecoder.LOCALE_LANGUAGE_CODE, locale.getLanguage() );
	    config.setProperty(CalendarDecoder.LOCALE_COUNTRY_CODE, locale.getCountry() );
	    decoder.setConfiguration( config );

		decoder.getLocale( locale.getLanguage(), locale.getCountry() );
	}

	public void setUp()
	{
	    decoder = new LocaleAwareDateDecoder() {};

	}

}
