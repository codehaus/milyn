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

package org.milyn.rules.regex;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for {@link RegexCache}.
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class RegexCacheTest
{
    private RegexCache instance = RegexCache.getInstance();

    @Test
    public void getRegexForAlias()
    {
        String regex = instance.getRegexForAlias("email");
        assertNotNull(regex);
    }

    @Test (expected = IllegalArgumentException.class)
    public void getRegexForAliasNull()
    {
        instance.getRegexForAlias(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void getNull() throws InterruptedException
    {
        instance.get(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addAliasesFromFile()
    {
        instance.addAliasesFromFile(null);
    }

}
