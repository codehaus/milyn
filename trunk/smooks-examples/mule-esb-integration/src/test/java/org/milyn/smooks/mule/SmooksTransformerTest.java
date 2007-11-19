package org.milyn.smooks.mule;

import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.milyn.Smooks;
import org.milyn.io.StreamUtils;
import org.mule.umo.transformer.TransformerException;

/**
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>				
 *
 */
public class SmooksTransformerTest extends TestCase
{
	private Logger log = Logger.getLogger( SmooksTransformerTest.class );
	
	private final String smooksConfigFileName = "smooks-config.xml";
	
	private SmooksTransformer smooksTransformer = new SmooksTransformer();
	
	public void test_getSmooksConfigFile() throws TransformerException
	{
		smooksTransformer.setSmooksConfigFile( smooksConfigFileName );
		assertEquals( smooksConfigFileName, smooksTransformer.getSmooksConfigFile() );
	}
	
	public void test_getSmooks() throws TransformerException
	{
		smooksTransformer.setSmooksConfigFile( smooksConfigFileName );
		Smooks smooks = smooksTransformer.getSmooks();
		assertNotNull( smooks );
		Smooks smooks2 = smooksTransformer.getSmooks();
		assertSame( smooks, smooks2 );
	}
	
	public void test_doTransformation() throws TransformerException
	{
		smooksTransformer.setSmooksConfigFile( smooksConfigFileName );
		byte[] inputMessage = readInputMessage();
		Object transformedObject = smooksTransformer.doTransform( inputMessage, "UTF-8" );
		log.debug( transformedObject );
		assertNotNull ( transformedObject );
	}
	
	public void test_getBytesFromMessageObject() throws TransformerException
	{
		smooksTransformer.setSmooksConfigFile( smooksConfigFileName );
		byte[] inputMessage = readInputMessage();
		String inputAsString = new String( inputMessage );
		byte[] bytesFromMessageObject = smooksTransformer.getBytesFromMessageObject( inputAsString );
		assertEquals( new String( inputMessage ), new String ( bytesFromMessageObject ) );
	}
	
	private static byte[] readInputMessage() {
        try {
            return StreamUtils.readStream(new FileInputStream("input-message.xml"));
        } catch (IOException e) {
            e.printStackTrace();
            return "<no-message/>".getBytes();
        }
    }

}
