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

package org.milyn.db;

import static org.junit.Assert.*;

import org.junit.Test;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.container.MockApplicationContext;
import org.milyn.delivery.ContentHandler;
import org.milyn.delivery.JavaContentHandlerFactory;

/**
 * Unit test for {@link JndiDataSource}.
 *
 * @author <a href="mailto:danielbevenius@gmail.com">Daniel Bevenius</a>
 */
public class JndiDataSourceTest
{
    @Test public void test() throws SmooksConfigurationException, InstantiationException
    {
        final MockApplicationContext appContext = new MockApplicationContext();
        final SmooksResourceConfiguration config = createConfig("test", "dbsource");

        final JavaContentHandlerFactory factory = new JavaContentHandlerFactory();
        Configurator.configure(factory, config, appContext);

        final ContentHandler<?> handler = factory.create(config);
        assertNotNull(handler);
    }

    private SmooksResourceConfiguration createConfig(final String resourceName, final String datasource)
    {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration( "element", MockJndiDataSource.class.getName() );
        config.setParameter( "resourceName", resourceName );
        config.setParameter( "datasource", datasource );
        config.setParameter( "autoCommit", "false" );
        return config;
    }

    public static class MockJndiDataSource extends JndiDataSource
    {
        @Override
        public void intitialize()
        {
            // NoOp
        }
    }

}