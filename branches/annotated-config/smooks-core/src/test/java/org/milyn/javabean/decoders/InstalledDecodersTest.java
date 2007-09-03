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
import org.milyn.javabean.DataDecoder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Calendar;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class InstalledDecodersTest extends TestCase {

    public void test() {
        assertTrue(DataDecoder.Factory.create(String.class) instanceof StringDecoder);
        assertTrue(DataDecoder.Factory.create(Integer.class) instanceof IntegerDecoder);
        assertTrue(DataDecoder.Factory.create(int.class) instanceof IntegerDecoder);
        assertTrue(DataDecoder.Factory.create(Float.class) instanceof FloatDecoder);
        assertTrue(DataDecoder.Factory.create(float.class) instanceof FloatDecoder);
        assertTrue(DataDecoder.Factory.create(Double.class) instanceof DoubleDecoder);
        assertTrue(DataDecoder.Factory.create(double.class) instanceof DoubleDecoder);
        assertTrue(DataDecoder.Factory.create(Character.class) instanceof CharacterDecoder);
        assertTrue(DataDecoder.Factory.create(char.class) instanceof CharacterDecoder);
        assertTrue(DataDecoder.Factory.create(BigDecimal.class) instanceof BigDecimalDecoder);
        assertTrue(DataDecoder.Factory.create(Date.class) instanceof DateDecoder);
        assertTrue(DataDecoder.Factory.create(Calendar.class) instanceof CalendarDecoder);
        assertNull(DataDecoder.Factory.create(getClass()));
    }
}
