package org.milyn.javabean.decoders;

import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.DataDecodeException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksConfigurationException;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;

/**
 * {@link java.util.Calendar} data decoder.
 * <p/>
 * Decodes the supplied string into a {@link java.util.Calendar} value
 * based on the supplied "{@link java.text.SimpleDateFormat format}" parameter.
 * <p/>
 * This decoder is synchronized on its underlying {@link SimpleDateFormat} instance.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CalendarDecoder implements DataDecoder {

    /**
     * Date format configuration key.
     */
    public static final String FORMAT = "format";
    private String format;
    private SimpleDateFormat decoder;

    public void setConfiguration(SmooksResourceConfiguration resourceConfig) throws SmooksConfigurationException {
        format = resourceConfig.getStringParameter(FORMAT);
        if (format == null) {
            throw new SmooksConfigurationException("Date Decoder must specify a 'format' parameter.");
        }
        decoder = new SimpleDateFormat(format.trim());
    }

    public Object decode(String data) throws DataDecodeException {
        if (decoder == null) {
            throw new IllegalStateException("Calendar decoder not initialised.  A decoder for this type (" + getClass().getName() + ") must be explicitly configured (unlike the primitive type decoders) with a date 'format'. See Javadoc.");
        }
        try {
            // Must be sync'd - DateFormat is not synchronized.
            synchronized(decoder) {
                decoder.parse(data.trim());
                return decoder.getCalendar().clone();
            }
        } catch (ParseException e) {
            throw new DataDecodeException("Error decoding Date data value '" + data + "' with decode format '" + format + "'.", e);
        }
    }
}
