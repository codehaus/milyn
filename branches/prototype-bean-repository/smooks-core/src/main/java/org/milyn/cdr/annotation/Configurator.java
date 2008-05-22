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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ApplicationContext;
import org.milyn.delivery.ContentHandler;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.annotation.Uninitialize;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Utility class for processing configuration annotations on a
 * {@link org.milyn.delivery.ContentHandler} instance and applying resource configurations from the
 * supplied {@link SmooksResourceConfiguration}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Configurator {

    private static Log logger = LogFactory.getLog(Configurator.class);

    /**
     * Configure the supplied {@link org.milyn.delivery.ContentHandler} instance using the supplied
     * {@link SmooksResourceConfiguration} and {@link org.milyn.container.ApplicationContext} instances.
     * @param instance The instance to be configured.
     * @param config The configuration.
     * @param appContext Associated application context.
     * @return The configured ContentHandler instance.
     * @throws SmooksConfigurationException Invalid field annotations.
     */
    public static <U extends ContentHandler> U configure(U instance, SmooksResourceConfiguration config, ApplicationContext appContext) throws SmooksConfigurationException {
        AssertArgument.isNotNull(appContext, "appContext");

        // process the field annotations (@AppContext)...
        processFieldContextAnnotation(instance, appContext);

        // TODO: Add by-setter-method injection support for the app context

        return configure(instance, config);
    }

    /**
     * Configure the supplied {@link org.milyn.delivery.ContentHandler} instance using the supplied
     * {@link SmooksResourceConfiguration} isntance.
     * @param instance The instance to be configured.
     * @param config The configuration.
     * @return The configured ContentHandler instance.
     * @throws SmooksConfigurationException Invalid field annotations.
     */
    public static <U extends ContentHandler> U configure(U instance, SmooksResourceConfiguration config) throws SmooksConfigurationException {
        AssertArgument.isNotNull(instance, "instance");
        AssertArgument.isNotNull(config, "config");

        // process the field annotations (@ConfigParam and @Config)...
        processFieldConfigAnnotations(instance, config);

        // process the method annotations (@ConfigParam)...
        processMethodConfigAnnotations(instance, config);

        // reflectively call the "setConfiguration" method, if defined...
        setConfiguration(instance, config);

        // process the @Initialise annotations...
        initialise(instance);

        return instance;
    }

    private static <U extends ContentHandler> void processFieldContextAnnotation(U instance, ApplicationContext appContext) {
        Field[] fields = instance.getClass().getDeclaredFields();

        for (Field field : fields) {
            AppContext appContextAnnotation = field.getAnnotation(AppContext.class);
            if(appContextAnnotation != null) {
                try {
                    setField(field, instance, appContext);
                } catch (IllegalAccessException e) {
                    throw new SmooksConfigurationException("Failed to set ApplicationContext value on '" + getLongMemberName(field) + "'.", e);
                }
            }
        }
    }

    private static <U extends ContentHandler> void processFieldConfigAnnotations(U instance, SmooksResourceConfiguration config) {
        Class contentHandlerClass = instance.getClass();
        processFieldConfigAnnotations(contentHandlerClass, instance, config);
    }

    private static <U extends ContentHandler> void processFieldConfigAnnotations(Class contentHandlerClass, U instance, SmooksResourceConfiguration config) {
        Field[] fields = contentHandlerClass.getDeclaredFields();

        // Work back up the Inheritance tree first...
        Class superClass = contentHandlerClass.getSuperclass();
        if(superClass != null && ContentHandler.class.isAssignableFrom(superClass)) {
            processFieldConfigAnnotations(superClass, instance, config);
        }

        for (Field field : fields) {
            ConfigParam configParamAnnotation = field.getAnnotation(ConfigParam.class);
            if(configParamAnnotation != null) {
                applyConfigParam(configParamAnnotation, field, field.getType(), instance, config);
            }
            Config configAnnotation = field.getAnnotation(Config.class);
            if(configAnnotation != null) {
                if(configParamAnnotation != null) {
                    throw new SmooksConfigurationException("Invalid Smooks configuration annotations on Field '" + getLongMemberName(field) + "'.  Field should not specify both @ConfigParam and @Config annotations.");
                }
                applyConfig(field, instance, config);
            }
        }
    }

    private static <U extends ContentHandler> void processMethodConfigAnnotations(U instance, SmooksResourceConfiguration config) {
        Method[] methods = instance.getClass().getMethods();

        for (Method method : methods) {
            ConfigParam configParamAnnotation = method.getAnnotation(ConfigParam.class);
            if(configParamAnnotation != null) {
                Class params[] = method.getParameterTypes();

                if(params.length == 1) {
                    applyConfigParam(configParamAnnotation, method, params[0], instance, config);
                } else {
                    throw new SmooksConfigurationException("Method '" + getLongMemberName(method) + "' defines a @ConfigParam, yet it specifies more than a single paramater.");
                }
            }
        }
    }

    private static void applyConfigParam(ConfigParam configParam, Member member, Class type, ContentHandler instance, SmooksResourceConfiguration config) throws SmooksConfigurationException {
        String name = configParam.name();
        String paramValue;

        // Work out the property name, if not specified via the annotation....
        if(AnnotationConstants.NULL_STRING.equals(name)) {
            // "name" not defined.  Use the field/method name...
            if(member instanceof Method) {
                name = getPropertyName((Method)member);
                if(name == null) {
                    throw new SmooksConfigurationException("Unable to determine the property name associated with '" +
                            getLongMemberName(member)+ "'. " +
                            "Setter methods that specify the @ConfigParam annotation " +
                            "must either follow the Javabean naming convention ('setX' for propert 'x'), or specify the " +
                            "propery name via the 'name' parameter on the @ConfigParam annotation.");
                }
            } else {
                name = member.getName();
            }
        }
        paramValue = config.getStringParameter(name);

        if(paramValue == null) {
            paramValue = configParam.defaultVal();
            if(AnnotationConstants.NULL_STRING.equals(paramValue)) {
                // A null default was assigned...
                String[] choices = configParam.choice();
                assertValidChoice(choices, name, AnnotationConstants.NULL_STRING);
                setMember(member, instance, null);
                return;
            } else if(AnnotationConstants.UNASSIGNED.equals(paramValue)) {
                // No default was assigned...
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
                decoder = DataDecoder.Factory.create(type);
                if(decoder == null) {
                    throw new SmooksConfigurationException("ContentHandler class member '" + getLongMemberName(member) + "' must define a decoder through it's @ConfigParam annotation.  Unable to automatically determine DataDecoder from member type.");
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
                setMember(member, instance, decoder.decode(paramValue));
            } catch (DataDecodeException e) {
                throw new SmooksConfigurationException("Failed to set paramater configuration value on '" + getLongMemberName(member) + "'.", e);
            }
        } else if(configParam.use() == ConfigParam.Use.REQUIRED) {
            throw new SmooksConfigurationException("<param> '" + name + "' not specified on resource configuration:\n" + config);
        }
    }

    private static void assertValidChoice(String[] choices, String name, String paramValue) throws SmooksConfigurationException {
        if(choices == null || choices.length == 0) {
            throw new RuntimeException("Unexpected annotation default choice value.  Should not be null or empty.  Code may have changed incompatibly.");
        } else if(choices.length == 1 && AnnotationConstants.NULL_STRING.equals(choices[0])) {
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

    private static void applyConfig(Field field, ContentHandler instance, SmooksResourceConfiguration config) {
        try {
            setField(field, instance, config);
        } catch (IllegalAccessException e) {
            throw new SmooksConfigurationException("Failed to set paramater configuration value on '" + getLongMemberName(field) + "'.", e);
        }
    }

    private static void setConfiguration(ContentHandler instance, SmooksResourceConfiguration config) {
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

    private static String getLongMemberName(Member field) {
        return field.getDeclaringClass().getName() + "#" + field.getName();
    }

    private static void setMember(Member member, ContentHandler instance, Object value) {
        try {
            if(member instanceof Field) {
                setField((Field)member, instance, value);
            } else {
                try {
                    setMethod((Method)member, instance, value);
                } catch (InvocationTargetException e) {
                    throw new SmooksConfigurationException("Failed to set paramater configuration value on '" + getLongMemberName(member) + "'.", e.getTargetException());
                }
            }
        } catch (IllegalAccessException e) {
            throw new SmooksConfigurationException("Failed to set paramater configuration value on '" + getLongMemberName(member) + "'.", e);
        }
    }

    private static void setField(Field field, ContentHandler instance, Object value) throws IllegalAccessException {
        boolean isAccessible = field.isAccessible();

        if(!isAccessible) {
            field.setAccessible(true);
        }
        field.set(instance, value);
        field.setAccessible(isAccessible);
    }

    private static void setMethod(Method method, ContentHandler instance, Object value) throws IllegalAccessException, InvocationTargetException {
        method.invoke(instance, value);
    }

    private static <U extends ContentHandler> void initialise(U instance) {
        invoke(instance, Initialize.class);
    }

    public static <U extends ContentHandler> void uninitialise(U instance) {
        invoke(instance, Uninitialize.class);
    }

    private static <U extends ContentHandler> void invoke(U instance, Class<? extends Annotation> annotation) {
        Method[] methods = instance.getClass().getMethods();

        for (Method method : methods) {
            if(method.getAnnotation(annotation) != null) {
                if(method.getParameterTypes().length == 0) {
                    try {
                        method.invoke(instance);
                    } catch (IllegalAccessException e) {
                        throw new SmooksConfigurationException("Error invoking @" + annotation.getSimpleName() + " method '" + method.getName() + "' on class '" + instance.getClass().getName() + "'.", e);
                    } catch (InvocationTargetException e) {
                        throw new SmooksConfigurationException("Error invoking @" + annotation.getSimpleName() + " method '" + method.getName() + "' on class '" + instance.getClass().getName() + "'.", e.getTargetException());
                    }
                } else {
                    logger.warn("Method '" + getLongMemberName(method) + "' defines an @" + annotation.getSimpleName() + " annotation on a paramaterized method.  This is not allowed!");
                }
            }
        }
    }

    private static String getPropertyName(Method method) {
        if(!method.getName().startsWith("set")) {
            return null;
        }

        StringBuffer methodName = new StringBuffer(method.getName());

        if(methodName.length() < 4) {
            return null;
        }

        methodName.delete(0, 3);
        methodName.setCharAt(0, Character.toLowerCase(methodName.charAt(0)));

        return methodName.toString();
    }
}
