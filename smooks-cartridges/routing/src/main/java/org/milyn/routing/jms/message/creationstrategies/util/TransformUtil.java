/*
 * Milyn - Copyright (C) 2006
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (version 2.1) as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.milyn.routing.jms.message.creationstrategies.util;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.delivery.sax.SAXElement;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * Utility methods for transforming from Element (SAX and DOM)
 * objects to Strings.
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>				
 *
 */
public class TransformUtil
{
	private static Logger log = Logger.getLogger( TransformUtil.class );
	
	private TransformUtil() { }
	
	/**
	 * Transforms the DOM Elmement to a String representation
	 * 
	 * @param element	- the DOM element
	 * @return String	- the String representation of the element
	 * @throws SmooksException	- if a transformation exception occurs
	 */
	public static String transformElement( final Element element ) throws SmooksException
	{
		try
		{
			final Transformer transformer = TransformerFactory.newInstance().newTransformer();
			final StringWriter sw = new StringWriter();
			final StreamResult result = new StreamResult( sw );
			transformer.transform( new DOMSource( element ), result );
			return sw.toString();
		} 
		catch (TransformerConfigurationException e)
		{
			final String errorMsg = "TransformerException while trying to transform DOM Element to String";
			log.error( errorMsg, e );
			throw new SmooksConfigurationException( errorMsg, e );
		} 
		catch (TransformerFactoryConfigurationError e)
		{
			final String errorMsg = "TransformerFactoryConfigurationException while trying to transform DOM Element to String";
			log.error( errorMsg, e );
			throw new SmooksConfigurationException( errorMsg, e );
		} 
		catch (TransformerException e)
		{
			final String errorMsg = "TransformerException while trying to transform DOM Element to String";
			log.error( errorMsg, e );
			throw new SmooksConfigurationException( errorMsg, e );
		}
	}
	
	/**
	 * TODO: impl this correctly.
	 * 
	 * @param element
	 * @return
	 * @throws SmooksException
	 */
	public static String transformElement( final SAXElement element ) throws SmooksException
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "<" ).append( element.getName().getLocalPart());
		Attributes attributes = element.getAttributes();
		if ( attributes != null )
			for( int i=0 ; i < attributes.getLength(); i++ )
			{
				String name = attributes.getLocalName( i );
				String value = attributes.getValue( i );
				sb.append(" ").append(name).append("=").append(value);
			}
		sb.append( ">" );
		sb.append( "</" ).append( element.getName().getLocalPart() ).append(">");
		
		return sb.toString();
	}


}
