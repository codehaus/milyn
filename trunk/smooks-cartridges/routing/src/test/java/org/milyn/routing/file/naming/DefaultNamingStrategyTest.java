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

package org.milyn.routing.file.naming;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for class DefaultNamingStrategy
 *
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class DefaultNamingStrategyTest
{
	private String prefix = "prefix-";
	private final String suffix = ".suffix";

	DefaultNamingStrategy strategy = new DefaultNamingStrategy();

	@Test
	public void generateFileName()
	{
		String generateFileName = strategy.generateFileName( prefix, suffix );
		System.out.println(generateFileName);

		assertTrue( generateFileName.startsWith( prefix ) );
		assertTrue( generateFileName.endsWith( suffix ) );
	}

}