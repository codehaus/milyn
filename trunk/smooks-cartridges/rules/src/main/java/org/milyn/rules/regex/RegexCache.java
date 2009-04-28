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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.assertion.AssertArgument;
import org.milyn.cache.Computable;
import org.milyn.cache.Memoizer;

/**
 * Singleton that holds compiled regular expressions.
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public final class RegexCache
{
    /**
     * Logger.
     */
	private static Log log = LogFactory.getLog(RegexCache.class);

    /**
     * The singleton instance.
     */
    private static final RegexCache instance = new RegexCache();

    /**
     * The actual cache where the key is the reqex string and the value
     * is the compiled regular expression.
     */
    private Memoizer<String, Pattern> cache = new Memoizer<String, Pattern>(new RegexComputable());

    /**
     * Mapping of aliases to regular expressions.
     */
    private ConcurrentMap<String, String> aliases;

    /**
     * Private sole constructor
     */
    private RegexCache()
    {
        aliases = loadAliases("/regex-default.properties");
    }

    /**
     * Gets the singleton instance.
     *
     * @return RegexCache The singleton instance.
     */
    public static RegexCache getInstance()
    {
        return instance;
    }

    /**
     * Get the compiled pattern from the cache.
     *
     * @param regex The regular expression
     * @return Pattern The compiled Pattern
     *
     * @throws InterruptedException
     */
    public Pattern get(final String regex) throws InterruptedException
    {
        AssertArgument.isNotNull(regex, "regex");
        return cache.compute(regex);
    }

    /**
     * Will try to lookup the passed-in alias and return the regex
     * for it.
     *
     * @param alias The alias to lookup.
     * @return {@code String} The regex that maps to the alias.
     */
    public String getRegexForAlias(final String alias)
    {
        AssertArgument.isNotNull(alias, "alias");
        return aliases.get(alias);
    }

    /**
     * Will add the aliases found in the passed-in file to the
     * list of aliases.
     *
     * @param fileName The file from to load the aliases to be added.
     */
    public void addAliasesFromFile(final String fileName)
    {
        AssertArgument.isNotNull(fileName, "fileName");
        aliases.putAll(loadAliases(fileName));
    }

    /**
     * Will try to load the alias to regex mappings from the passed-in properties file.
     *
     * @param fileName The file containing alias to regex mappings.
     * @return {@code ConcurrentHashMap} Map containing the aliases as its keys and regular expressions as its values.
     */
    private ConcurrentMap<String, String> loadAliases(final String fileName)
    {
        AssertArgument.isNotNullAndNotEmpty(fileName, "fileName");
        final InputStream in = getClass().getResourceAsStream(fileName);
        if (in == null)
        {
            throw new SmooksException("Could not located alias file '" + fileName + "'. Please make sure that it exists on the classpath");
        }

        log.debug("Going to read aliases from file '" + fileName + "'");
        try
        {
            final ConcurrentHashMap<String, String> regexes = new ConcurrentHashMap<String, String>();
            final Properties properties = new Properties();
            properties.load(in);
            for (Entry<Object, Object> entry : properties.entrySet())
            {
                final String alias = (String) entry.getKey();
                final String regex = (String) entry.getValue();
                regexes.put(alias, regex);
            }
            return regexes;
        }
        catch (final IOException e)
        {
            throw new SmooksException("IOException while trying to load input stream to file '" + fileName + "'", e);
        }
        finally
        {
            close(in, fileName);
        }
    }

    private class RegexComputable implements Computable<String, Pattern>
    {
        public Pattern compute(final String regex) throws InterruptedException
        {
            return Pattern.compile(regex);
        }
    }

    private void close(final InputStream in, final String fileName)
    {
        try
        {
            in.close();
        }
        catch (final IOException e)
        {
            log.error("IOException while trying to close input stream to file '" + fileName + "'", e);
        }
    }
}
