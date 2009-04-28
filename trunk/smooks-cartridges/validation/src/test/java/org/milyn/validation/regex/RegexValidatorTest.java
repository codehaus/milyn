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

package org.milyn.validation.regex;

import static org.junit.Assert.*;

import org.junit.Test;
import org.milyn.SmooksException;

/**
 * Test for {@link RegexValidator}.
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class RegexValidatorTest
{
    @Test
    public void regularExpression()
    {
        //RegexValidator validator = new RegexValidator("[0-9]*");
        //validator.validate("9933");
    }
    /*

    @Test (expected = IllegalArgumentException.class)
    public void regularExpressionNullText()
    {
        RegexValidator validator = new RegexValidator("[0-9]*");
        validator.validate(null);
    }

    @Test
    public void phoneNumberSweden()
    {
        RegexValidator validator = new RegexValidator("phoneNumberSE", null);
        validator.validate("08-7549922");
    }

    @Test
    public void phoneNumberUnitedStates()
    {
        RegexValidator validator = new RegexValidator("phoneNumberUS", null);
        validator.validate("2405525009");
        validator.validate("1(240) 652-5009");
        validator.validate("240/752-5009 ext.55");
        validator.validate("240/752-5009 ext.55");

        assertFalse(valid("(2405525009", validator));
        assertFalse(valid("2 (240) 652-5009", validator));
    }

    @Test
    public void phoneNumberIndia()
    {
        RegexValidator validator = new RegexValidator("phoneNumberIN", null);
        validator.validate("0493 - 3227341");
        validator.validate("0493 3227341");
        validator.validate("493 3227341");

        assertFalse(valid("93 0227341", validator));
        assertFalse(valid("493 322734111", validator));
        assertFalse(valid("493 -- 3227341", validator));

    }

    @Test
    public void phoneNumberAustralia()
    {
        RegexValidator validator = new RegexValidator("phoneNumberAU", null);
        validator.validate("0732105432");
        validator.validate("1300333444");
        validator.validate("131313");

        assertFalse(valid("32105432", validator));
        assertFalse(valid("13000456", validator));
    }

    @Test
    public void phoneNumberUnitedKingdom()
    {
        RegexValidator validator = new RegexValidator("phoneNumberGB", null);
        validator.validate("+447222555555");
        validator.validate("+44 7222 555 555");
        validator.validate("(0722) 5555555 #2222");

        assertFalse(valid("(+447222)555555", validator));
        assertFalse(valid("+44(7222)555555", validator));
        assertFalse(valid("(0722) 5555555 #22", validator));
    }

    @Test
    public void phoneNumberItaly()
    {
        RegexValidator validator = new RegexValidator("phoneNumberIT", null);
        validator.validate("02-343536");
        validator.validate("02/343536");
        validator.validate("02 343536");

        assertFalse(valid("02a343536", validator));
        assertFalse(valid("02+343536", validator));
    }

    @Test
    public void phoneNumberNetherlands()
    {
        RegexValidator validator = new RegexValidator("phoneNumberNL", null);
        validator.validate("06 12345678");
        validator.validate("010-1234560");
        validator.validate("0111-101234");

        assertFalse(valid("05-43021212", validator));
        assertFalse(valid("123-4567890", validator));
        assertFalse(valid("1234567890", validator));
    }

    @Test
    public void email()
    {
        RegexValidator validator = new RegexValidator("email", null);
        validator.validate("daniel.bevenius@gmail.com");
        validator.validate("daniel@gmail.se");
        validator.validate("daniel.bevenius@gmail.uk.com");
        validator.validate("d.b@gmail.uk.com");
        validator.validate("db@gl.se");
        validator.validate("db@gl.s");

        assertFalse(valid("@gmail.com", validator));
    }

    @Test
    public void dateMMddyyyyTime()
    {
        RegexValidator validator = new RegexValidator("dateMMddyyyy", null);
        validator.validate("12/30/2002");
        validator.validate("12/30/2002 9:35 pm");
        validator.validate("12/30/2002 19:35:02");

        assertFalse(valid("18/22/2003", validator));
        assertFalse(valid("8/12/99", validator));
        assertFalse(valid("8/22/2003 25:00", validator));
    }

    @Test
    public void dateyyyyMMdd()
    {
        RegexValidator validator = new RegexValidator("dateyyyyMMdd", null);
        validator.validate("2002-01-31");
        validator.validate("1997-04-30");
        validator.validate("2004-01-01");

        assertFalse(valid("2002-01-32", validator));
        assertFalse(valid("2003-02-29", validator));
        assertFalse(valid("04-01-01", validator));
    }

    @Test
    public void time()
    {
        RegexValidator validator = new RegexValidator("time", null);
        validator.validate("1 AM");
        validator.validate("1 PM");
        validator.validate("23:00:00");
        validator.validate("23:00");
        validator.validate("5:29:59 PM");

        assertFalse(valid("13 PM", validator));
        assertFalse(valid("13:60:00", validator));
        assertFalse(valid("00:00:00 AM", validator));
    }

    private boolean valid(final String regex, final RegexValidator validator)
    {
        try
        {
            validator.validate(regex);
        }
        catch(final SmooksException e)
        {
            Throwable cause = e.getCause();
            if (cause instanceof RegexException)
            {
                return false;
            }
            throw e;
        }
        return true;
    }
    */

}
