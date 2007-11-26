package org.milyn.javabean.decoders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.DecodeType;

/**
 * {@link java.util.Calendar} data decoder.
 * <p/>
 * Decodes the supplied string into a {@link java.util.Calendar} value
 * based on the supplied "{@link java.text.SimpleDateFormat format}" parameter.
 * Optionally a locale-language and language-country can be used which 
 * will be used as the "{@link java.util.Locale}".
 * <p/>
 * This decoder is synchronized on its underlying {@link SimpleDateFormat} instance.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 * @author Pavel Kadlec
 * @author <a href="mailto:daniel.bevenius@gmail.com">daniel.bevenius@gmail.com</a>
 */
@DecodeType(Calendar.class)
public class CalendarDecoder implements DataDecoder {

    /**
     * Date format configuration key.
     */
    public static final String FORMAT = "format";
    public static final String LOCALE_LANGUAGE_CODE = "locale-language";
    public static final String LOCALE_COUNTRY_CODE = "locale-country";
    
    private String format;
    private SimpleDateFormat decoder;

    public void setConfiguration(SmooksResourceConfiguration resourceConfig) throws SmooksConfigurationException {
        format = resourceConfig.getStringParameter(FORMAT);
        if (format == null) {
            throw new SmooksConfigurationException("Date Decoder must specify a 'format' parameter.");
        }
        final String languageCode = resourceConfig.getStringParameter(LOCALE_LANGUAGE_CODE);
        final String countryCode = resourceConfig.getStringParameter(LOCALE_COUNTRY_CODE);
        decoder = new SimpleDateFormat(format.trim(), getLocale( languageCode, countryCode ));
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
    
    /*
     * Returns a Locale matching the passed in languageCode, and coutryCode
     * 
     * @param languageCode	lowercase two-letter ISO-639 code.
     * @param countryCode	uppercase two-letter ISO-3166 code.
     * @return Locale		matching the passed in languageCode and optionally the
     * 						countryCode. If languageCode is null the default Locale 
     * 						will be returned.
     */
    protected Locale getLocale(final String languageCode, final String countryCode ) {
    	if ( languageCode == null )
    		return Locale.getDefault();
    	else if ( countryCode == null  )
    		return new Locale( languageCode.trim() );
    	else 
    		return new Locale( languageCode.trim(), countryCode.trim() );
    }
}
