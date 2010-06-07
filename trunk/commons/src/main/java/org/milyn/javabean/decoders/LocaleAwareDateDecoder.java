package org.milyn.javabean.decoders;

import org.milyn.config.Configurable;
import org.milyn.cdr.SmooksConfigurationException;

import java.text.*;
import java.util.*;

/**
 * LocaleAwareDateDecoder is a decoder 'helper' that can be subclassed by Date decoders to enable
 * them to use locale specific date formats.
 * <p/>
 * Usage (on Java Binding value config using the {@link org.milyn.javabean.decoders.DateDecoder}):
 * <pre>
 * &lt;jb:value property="date" decoder="Date" data="order/@date"&gt;
 *     &lt;-- Format: Defaults to "yyyy-MM-dd'T'HH:mm:ss" (SOAP) --&gt;
 *     &lt;jb:decodeParam name="format"&gt;EEE MMM dd HH:mm:ss z yyyy&lt;/jb:decodeParam&gt;
 *     &lt;-- Locale: Defaults to machine Locale --&gt;
 *     &lt;jb:decodeParam name="locale"&gt;sv-SE&lt;/jb:decodeParam&gt;
 *     &lt;-- Verify Locale: Default false --&gt;
 *     &lt;jb:decodeParam name="verify-locale"&gt;true&lt;/jb:decodeParam&gt;
 * &lt;/jb:value&gt;
 * </pre>
 *
 * @author <a href="mailto:daniel.bevenius@gmail.com">daniel.bevenius@gmail.com</a>
 */
public abstract class LocaleAwareDateDecoder implements Configurable
{
    /**
     * Date format configuration key.
     */
    public static final String FORMAT = "format";

    /**
     * Default date format string.
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Locale.  Hyphen separated ISO Language Code and Country Code e.g. "en-IE".
     */
    public static final String LOCALE = "locale";

    /**
     * ISO Language Code. Lower case two-letter code defined by ISO-639
     */
    public static final String LOCALE_LANGUAGE_CODE = "locale-language";

    /**
     * ISO Country Code. Upper case two-letter code defined by ISO-3166
     */
    public static final String LOCALE_COUNTRY_CODE = "locale-country";

    /**
     * True or false(default).
     * Whether or not a check should be performed to verify that
     * the specified locale is installed. This operation can take some
     * time and should be turned off in a production evironment
     */
    public static final String VERIFY_LOCALE = "verify-locale";

    private boolean verifyLocale;

    protected String format;

    private Properties configuration;
    
    /*
     * 	Need to initialize a default decoder as not calls can be make
     * 	directly to decode without calling setConfigurtion.
     */
    protected SimpleDateFormat decoder = new SimpleDateFormat( DEFAULT_DATE_FORMAT );

    public void setConfiguration(Properties resourceConfig) throws SmooksConfigurationException {
        format = resourceConfig.getProperty(FORMAT, DEFAULT_DATE_FORMAT);
        if (format == null) {
            throw new SmooksConfigurationException("Decoder must specify a 'format' parameter.");
        }

        final String locale = resourceConfig.getProperty(LOCALE);
        final String languageCode;
        final String countryCode;

        if(locale != null) {
            String[] localTokens = locale.split("-");

            languageCode = localTokens[0];
            if(localTokens.length == 2) {
                countryCode = localTokens[1];
            } else {
                countryCode = null;
            }
        } else {
            languageCode = resourceConfig.getProperty(LOCALE_LANGUAGE_CODE);
            countryCode = resourceConfig.getProperty(LOCALE_COUNTRY_CODE);
        }

        verifyLocale = Boolean.parseBoolean(resourceConfig.getProperty(VERIFY_LOCALE, "false"));

        decoder = new SimpleDateFormat(format.trim(), getLocale( languageCode, countryCode ));
        this.configuration = resourceConfig;
    }

    public Properties getConfiguration() {
        return configuration;
    }

    /**
     * Returns a Locale matching the passed in languageCode, and countryCode
     *
     * @param languageCode	lowercase two-letter ISO-639 code.
     * @param countryCode	uppercase two-letter ISO-3166 code.
     * @return Locale		matching the passed in languageCode and optionally the
     * 						countryCode. If languageCode is null the default Locale
     * 						will be returned.
     * @throws SmooksConfigurationException
     * 						if the Locale is not installed on the system
     */
    protected Locale getLocale(final String languageCode, final String countryCode ) {
    	Locale locale = null;
    	if ( languageCode == null )
    		locale = Locale.getDefault();
    	else if ( countryCode == null  )
    		locale = new Locale( languageCode.trim() );
    	else
    		locale =  new Locale( languageCode.trim(), countryCode.trim() );
    	if ( verifyLocale )
    		if ( !isLocalInstalled( locale ) )
    			throw new SmooksConfigurationException( "Locale " + locale + " is not available on this system.");
    	return locale;
    }

    protected boolean isLocalInstalled(final Locale locale )
    {
    	return Arrays.asList( Locale.getAvailableLocales() ).contains( locale );
    }
}
