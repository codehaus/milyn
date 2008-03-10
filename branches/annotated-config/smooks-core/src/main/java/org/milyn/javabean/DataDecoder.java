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
package org.milyn.javabean;

import org.milyn.delivery.ContentDeliveryUnit;
import org.milyn.javabean.decoders.StringDecoder;
import org.milyn.util.ClassUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Data decoder.
 * <p/>
 * A data decoder converts data (encoded in a String) to an Object of some form, determined by
 * the decoder implementation.
 * <p/>
 * There are a number of pre-installed decoders in the {@link org.milyn.javabean.decoders}
 * package.  Smooks will attempt to automatically load a data type decoder from the
 * {@link org.milyn.javabean.decoders} package, but only after it has attempted to load
 * the decoder from the {@link org.milyn.container.ExecutionContext context} configuration,
 * using a selector key of "decoder:<u>type</u>", where "type" is the type alias used on the
 * property binding configuration (see the "OrderDateLong" definition in the sample
 * in org.milyn.javabean.BeanPopulator in the Javabeans Cartridge.
 * <p/>
 * If one of the decoders in {@link org.milyn.javabean.decoders} is not what's needed, simply
 * implement a new decoder using this interface.  If you want the decoder to be automatically
 * picked up from the type alias specified on the binding configuration (e.g in the same way
 * as the decoders for types "Long", "Integer" etc are picked up) simply package the new decoder
 * in the {@link org.milyn.javabean.decoders} package and follow the class naming convention of
 * "<i>type</i>Decoder".
 * <p/>
 * Some type decoders will however need to be configured as a Smooks resource
 * because they will require configuration of one sort or another.  For an example of this,
 * look at the "OrderDateLong" decoder definition in the sample
 * in org.milyn.javabean.BeanPopulator in the Javabeans Cartridge.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface DataDecoder extends ContentDeliveryUnit {
    
    /**
     * Decode the supplied String data into a new Object data instance.
     *
     * @param data Data to be decoded.
     * @return Decoded data Object.
     * @throws DataDecodeException Error decoding data.
     */
    public Object decode(String data) throws DataDecodeException;

    /**
     * Factory method for constructing decoders defined in the "decoders" package.
     */
    public static class Factory {

        private static Log logger = LogFactory.getLog(DataDecoder.class);

        private static volatile Map<Class, Class<? extends DataDecoder>> installedDecoders;

        public static DataDecoder create(Class targetType) throws DataDecodeException {
            if(installedDecoders == null) {
                loadInstalledDecoders();
            }

            Class<? extends DataDecoder> decoderType = installedDecoders.get(targetType);
            if(decoderType != null) {
                return newInstance(decoderType);
            }

            return null;
        }

        private synchronized static void loadInstalledDecoders() throws DataDecodeException {
            if(installedDecoders == null) {
                List<Class> decoders = ClassUtil.findInstancesOf(DataDecoder.class);

                if(decoders.isEmpty()) {
                    throw new DataDecodeException("Failed to find installed DataDecoders on clasaspath.");
                }

                installedDecoders = new HashMap<Class, Class<? extends DataDecoder>>();
                for (Class decoder : decoders) {
                    DecodeType decoodeType = (DecodeType) decoder.getAnnotation(DecodeType.class);
                    if(decoodeType != null) {
                        Class[] types = decoodeType.value();

                        for (Class type : types) {
                            if(installedDecoders.containsKey(type)) {
                                logger.warn("More than one DataDecoder for type '" + type.getName() + "' is installed on the classpath.  You must manually configure decoding of this type, where required.");
                                installedDecoders.put(type, null); // We don't remove, because we need to maintain a record of this!
                            } else {
                                installedDecoders.put(type, decoder);
                            }
                        }
                    }
                }
            }
        }

        /**
         * Attempt to construct a decoder instance from it's type alias based on the
         * packaging and naming convention used in the {@link org.milyn.javabean.decoders} package.
         *
         * @param typeAlias Decoder alias used to construct an instance by prefixing the
         *              alias with the "decoders" package and suffixing it with the word "Decoder".
         * @return The DateDecoder instance, or null if no such instance is available.
         * @throws DataDecodeException Failed to load alias decoder.
         */
        public static DataDecoder create(String typeAlias) throws DataDecodeException {
            String className = StringDecoder.class.getPackage().getName() + "." + typeAlias + "Decoder";

            try {
                Class decoderType = ClassUtil.forName(className, DataDecoder.class);
                return newInstance(decoderType);
            } catch (ClassNotFoundException e) {
                throw new DataDecodeException("DataDecoder Class '" + className + "' is not available on the classpath.");
            }
        }

        private static DataDecoder newInstance(Class decoderType) throws DataDecodeException {
            try {
                return (DataDecoder) decoderType.newInstance();
            } catch (ClassCastException e) {
                throw new DataDecodeException("Class '" + decoderType.getName() + "' is not a valid DataDecoder.  It doesn't implement " + DataDecoder.class.getName());
            } catch (IllegalAccessException e) {
                throw new DataDecodeException("Failed to load DataDecoder Class '" + decoderType.getName() + "'.", e);
            } catch (InstantiationException e) {
                throw new DataDecodeException("Failed to load DataDecoder Class '" + decoderType.getName() + "'.", e);
            }
        }
    }
}