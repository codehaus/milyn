package org.milyn.javabean.decoders;

import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.DataDecodeException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksConfigurationException;

/**
 * Double decoder.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DoubleDecoder implements DataDecoder {

    public void setConfiguration(SmooksResourceConfiguration resourceConfig) throws SmooksConfigurationException {
    }

    public Object decode(String data) throws DataDecodeException {
        try {
            return Double.parseDouble(data);
        } catch(NumberFormatException e) {
            throw new DataDecodeException("Failed to decode Double value '" + data + "'.", e);
        }
    }
}
