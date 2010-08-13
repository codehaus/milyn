/*
 * Milyn - Copyright (C) 2006 - 2010
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
package org.milyn.smooks.camel.converters;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.milyn.payload.JavaSourceWithoutEventStream;

/**
 * Unit test for {@link SourceConverter}.
 * </p>
 * 
 * @author Daniel Bevenius
 *
 */
public class SourceConverterTest
{
	
	@Test
	public void toJavaSourceWithoutEventStream()
	{
		JavaSourceWithoutEventStream javaSource = SourceConverter.toJavaSourceWithoutEventStream("dummyPayload");
		assertFalse(javaSource.isEventStreamRequired());
		Map<String, Object> beans = javaSource.getBeans();
		String payload = (String) beans.get("string");
		assertEquals("dummyPayload", payload);
	}

}
