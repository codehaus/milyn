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
package org.milyn.cdr;

import junit.framework.TestCase;
import org.milyn.delivery.ContentDeliveryUnit;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.cdr.annotation.Configurator;
import org.milyn.cdr.annotation.Config;
import org.milyn.javabean.decoders.StringDecoder;
import org.milyn.javabean.decoders.IntegerDecoder;

/**
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ConfiguratorTest extends TestCase {

    public void test_paramaterSetting_allok() {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration();
        MyContentDeliveryUnit1 cdu = new MyContentDeliveryUnit1();

        config.setParameter("paramA", "A-Val");
        config.setParameter("param-b", "B-Val");
        config.setParameter("paramC", "8");

        Configurator.configure(cdu, config);
        assertEquals("A-Val", cdu.paramA);
        assertEquals("B-Val", cdu.paramB);
        assertEquals(8, cdu.paramC);
    }

    public void test_paramaterSetting_missing_required() {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration();
        MyContentDeliveryUnit1 cdu = new MyContentDeliveryUnit1();

        config.setParameter("paramA", "A-Val");
        config.setParameter("param-b", "B-Val");

        // Don't add the required paramC config

        try {
            Configurator.configure(cdu, config);
            fail(" Expected SmooksConfigurationException");
        } catch(SmooksConfigurationException e) {
            assertTrue(e.getMessage().startsWith("<param> 'paramC' not specified on resource configuration"));
        }
    }

    public void test_paramaterSetting_optional_default() {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration();
        MyContentDeliveryUnit2 cdu = new MyContentDeliveryUnit2();

        config.setParameter("paramA", "A-Val");
        config.setParameter("param-b", "B-Val");

        // Don't add the optional paramC config

        Configurator.configure(cdu, config);
        assertEquals("A-Val", cdu.paramA);
        assertEquals("B-Val", cdu.paramB);
        assertEquals(9, cdu.paramC);
    }

    public void test_paramaterSetting_Config_annotation() {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration();
        MyContentDeliveryUnit3 cdu = new MyContentDeliveryUnit3();

        Configurator.configure(cdu, config);
        assertNotNull(cdu.config);
    }

    public void test_paramaterSetting_Config_setConfiguration_on_private_inner_class() {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration();
        MyContentDeliveryUnit4 cdu = new MyContentDeliveryUnit4();

        try {
            Configurator.configure(cdu, config);
            fail("Expected SmooksConfigurationException");
        } catch(SmooksConfigurationException e) {
            assertEquals("Error invoking 'setConfiguration' method on class 'org.milyn.cdr.ConfiguratorTest$MyContentDeliveryUnit4'.  This class must be public.  Alternatively, use the @Config annotation on a class field.", e.getMessage());
        }
    }

    public void test_paramaterSetting_Config_setConfiguration_on_top_level_class() {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration();
        MyContentDeliveryUnit5 cdu = new MyContentDeliveryUnit5();

        Configurator.configure(cdu, config);
        assertNotNull(cdu.config);
    }

    public void test_paramaterSetting_Config_choice() {
        SmooksResourceConfiguration config;
        MyContentDeliveryUnit6 cdu = new MyContentDeliveryUnit6();

        // Check that valid values are accepted....
        config = new SmooksResourceConfiguration();
        config.setParameter("paramA", "A");
        Configurator.configure(cdu, config);

        config = new SmooksResourceConfiguration();
        config.setParameter("paramA", "B");
        Configurator.configure(cdu, config);

        config = new SmooksResourceConfiguration();
        config.setParameter("paramA", "C");
        Configurator.configure(cdu, config);

        // Check that invalid values are accepted....
        config = new SmooksResourceConfiguration();
        config.setParameter("paramA", "X");
        try {
            Configurator.configure(cdu, config);
            fail("Expected SmooksConfigurationException");
        } catch(SmooksConfigurationException e) {
            assertEquals("Value 'X' for paramater 'paramA' is invalid.  Valid choices for this paramater are: [A, B, C]", e.getMessage());
        }
    }

    private class MyContentDeliveryUnit1 implements ContentDeliveryUnit {

        @ConfigParam
        private String paramA;

        @ConfigParam(name="param-b")
        private String paramB;

        @ConfigParam
        private int paramC;
    }

    private class MyContentDeliveryUnit2 implements ContentDeliveryUnit {

        @ConfigParam(decoder=StringDecoder.class)
        private String paramA;

        @ConfigParam(name="param-b", decoder=StringDecoder.class)
        private String paramB;

        @ConfigParam(decoder=IntegerDecoder.class, use=ConfigParam.Use.OPTIONAL, defaultVal ="9")
        private int paramC;
    }

    private class MyContentDeliveryUnit3 implements ContentDeliveryUnit {

        @Config
        private SmooksResourceConfiguration config;
    }

    private class MyContentDeliveryUnit4 implements ContentDeliveryUnit {

        private SmooksResourceConfiguration config;

        public void setConfiguration(SmooksResourceConfiguration resourceConfig) throws SmooksConfigurationException {
            this.config = resourceConfig;
        }
    }

    private class MyContentDeliveryUnit6 implements ContentDeliveryUnit {

        @ConfigParam(choice = {"A", "B", "C"})
        private String paramA;
    }
}
