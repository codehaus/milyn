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
package org.milyn.cdr.annotation;

import org.milyn.delivery.ContentDeliveryUnit;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.assertion.AssertArgument;
import org.milyn.javabean.DataDecoder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Utility class for processing configuration annotations on a
 * {@link ContentDeliveryUnit} instance and applying resource configurations from the
 * supplied {@link SmooksResourceConfiguration}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Configurator {

    /**
     * Configure the supplied {@link ContentDeliveryUnit} instance using the supplied
     * {@link SmooksResourceConfiguration}.
     * @param instance The instance to be configured.
     * @param config The configuration.
     * @return The configured ContentDeliveryUnit instance.
     * @throws SmooksConfigurationException Invalid field annotations.
     */
    public static <U extends ContentDeliveryUnit> U configure(U instance, SmooksResourceConfiguration config) throws SmooksConfigurationException {
        AssertArgument.isNotNull(instance, "instance");
        AssertArgument.isNotNull(config, "config");

        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            ConfigParam configParamAnnotation = field.getAnnotation(ConfigParam.class);
            if(configParamAnnotation != null) {
                applyConfigParam(configParamAnnotation, field, instance, config);
            }
            Config configAnnotation = field.getAnnotation(Config.class);
            if(configAnnotation != null) {
                if(configParamAnnotation != null) {
                    throw new SmooksConfigurationException("Invalid Smooks configuration annotations on Field '" + getLongFieldName(field) + "'.  Field should not specify both @ConfigParam and @Config annotations.");
                }
                applyConfig(field, instance, config);
            }
        }

        setConfiguration(instance, config);

        return instance;
    }

    private static void applyConfigParam(ConfigParam configParam, Field field, ContentDeliveryUnit instance, SmooksResourceConfiguration config) throws SmooksConfigurationException {
        String name = configParam.name();
        String paramValue;

        if(ConfigParam.NULL.equals(name)) {
            // "name" not defined.  Use the field name...
            name = field.getName();
        } 
        paramValue = config.getStringParameter(name);

        if(paramValue == null && configParam.use() == ConfigParam.Use.OPTIONAL) {
            paramValue = configParam.defaultVal();
            if(ConfigParam.NULL.equals(paramValue)) {
                paramValue = null;
            }
        }

        if(paramValue != null) {
            String[] choices = configParam.choice();
            Class<? extends DataDecoder> decoderClass;
            DataDecoder decoder;

            assertValidChoice(choices, name, paramValue);

            decoderClass = configParam.decoder();
            if(decoderClass.isAssignableFrom(DataDecoder.class)) {
                // No decoder specified via annotation.  Infer from the field type...
                decoder = DataDecoder.Factory.create(field.getType());
                if(decoder == null) {
                    throw new SmooksConfigurationException("ContentDeliveryUnit class field '" + getLongFieldName(field) + "' must define a decoder through it's @ConfigParam annotation.  Unable to automatically determine DataDecoder from field type.");
                }
            } else {
                // Decoder specified on annotation...
                try {
                    decoder = decoderClass.newInstance();
                } catch (InstantiationException e) {
                    throw new SmooksConfigurationException("Failed to create DataDecoder instance from class '" + decoderClass.getName() + "'.  Make sure the DataDecoder implementation has a public default constructor.", e);
                } catch (IllegalAccessException e) {
                    throw new SmooksConfigurationException("Failed to create DataDecoder instance from class '" + decoderClass.getName() + "'.  Make sure the DataDecoder implementation has a public default constructor.", e);
                }
            }

            try {
                setField(field, instance, decoder.decode(paramValue));
            } catch (IllegalAccessException e) {
                throw new SmooksConfigurationException("Failed to set paramater configuration value on '" + getLongFieldName(field) + "'.", e);
            }
        } else if(configParam.use() == ConfigParam.Use.REQUIRED) {
            throw new SmooksConfigurationException("<param> '" + name + "' not specified on resource configuration:\n" + config);
        }
    }

    private static void assertValidChoice(String[] choices, String name, String paramValue) throws SmooksConfigurationException {
        if(choices == null || choices.length == 0) {
            throw new RuntimeException("Unexpected annotation default choice value.  Should not be null or empty.  Code may have changed incompatibly.");
        } else if(choices.length == 1 && ConfigParam.NULL.equals(choices[0])) {
            // A choice wasn't specified on the paramater config.
            return;
        } else {
            // A choice was specified. Check it against the value...
            for (String choice : choices) {
                if(paramValue.equals(choice)) {
                    return;
                }
            }
        }

        throw new SmooksConfigurationException("Value '" + paramValue + "' for paramater '" + name + "' is invalid.  Valid choices for this paramater are: " + Arrays.asList(choices));
    }

    private static void applyConfig(Field field, ContentDeliveryUnit instance, SmooksResourceConfiguration config) {
        try {
            setField(field, instance, config);
        } catch (IllegalAccessException e) {
            throw new SmooksConfigurationException("Failed to set paramater configuration value on '" + getLongFieldName(field) + "'.", e);
        }
    }

    private static void setConfiguration(ContentDeliveryUnit instance, SmooksResourceConfiguration config) {
        try {
            Method setConfigurationMethod = instance.getClass().getMethod("setConfiguration", SmooksResourceConfiguration.class);
            
            setConfigurationMethod.invoke(instance, config);
        } catch (NoSuchMethodException e) {
            // That's fine
        } catch (IllegalAccessException e) {
            throw new SmooksConfigurationException("Error invoking 'setConfiguration' method on class '" + instance.getClass().getName() + "'.  This class must be public.  Alternatively, use the @Config annotation on a class field.", e);
        } catch (InvocationTargetException e) {
            if(e.getTargetException() instanceof SmooksConfigurationException) {
                throw (SmooksConfigurationException)e.getTargetException();
            } else {
                Throwable cause = e.getTargetException();
                throw new SmooksConfigurationException("Error invoking 'setConfiguration' method on class '" + instance.getClass().getName() + "'.", (cause != null?cause:e));
            }
        }
    }

    private static String getLongFieldName(Field field) {
        return field.getDeclaringClass().getName() + "#" + field.getName();
    }

    private static void setField(Field field, Object instance, Object value) throws IllegalAccessException {
        boolean isAccessible = field.isAccessible();

        if(!isAccessible) {
            field.setAccessible(true);
        }
        field.set(instance, value);
        field.setAccessible(isAccessible);
    }
}
