package org.milyn.smooks.mule;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.milyn.Smooks;
import org.milyn.container.standalone.StandaloneExecutionContext;
import org.mule.umo.transformer.TransformerException;
import org.xml.sax.SAXException;

/**
 *  SmooksTransformer indended to be used with the Mule ESB
 * 	<p>
 * 	Usage:
 * 	</p> 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>				
 *
 */
public class SmooksTransformer extends org.mule.transformers.AbstractTransformer
{
	private static final long serialVersionUID = 1L;
	
	private Logger log = Logger.getLogger( SmooksTransformer.class );
	
	/**
	 * Smooks instance
	 */
	private Smooks smooks;
	/**
	 * Filename for smooks configuration. Default is smooks-config.xml
	 */
    private String smooksConfigFile = "smooks-config.xml";

	@Override
	protected Object doTransform( Object message, String encoding ) throws TransformerException
	{
		smooks = getSmooks();
        StandaloneExecutionContext executionContext = smooks.createExecutionContext();
        
        byte[] bytes = getBytesFromMessageObject( message );
	        
        CharArrayWriter outputWriter = new CharArrayWriter();
        smooks.filter(new StreamSource(new ByteArrayInputStream(bytes), encoding), new StreamResult(outputWriter), executionContext);
        return outputWriter.toString();
	}
	
	public final Smooks getSmooks() throws TransformerException
	{
		try
		{
			if ( smooks == null )
				smooks = new Smooks( smooksConfigFile );
		} catch (IOException e)
		{
			log.error( "IOException while trying to get smooks instance: ", e);
			throw new TransformerException( this, e );
		} catch (SAXException e)
		{
			log.error( "SAXException while trying to get smooks instance: ", e);
			throw new TransformerException( this, e );
		}
		return smooks;
	}

	public String getSmooksConfigFile()
	{
		return smooksConfigFile;
	}

	public void setSmooksConfigFile( final String smooksResFile )
	{
		this.smooksConfigFile = smooksResFile;
	}
	
	byte[] getBytesFromMessageObject( final Object object )
	{
		if ( object instanceof String )
			return ( (String) object).getBytes();
		else if ( object instanceof byte[] )
			return (byte[]) object;
		else
			return null;
	}
	
	
}