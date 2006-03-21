/*
	Milyn - Copyright (C) 2003

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

package org.milyn;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;

import org.milyn.container.standalone.StandaloneContainerContext;
import org.milyn.container.standalone.StandaloneContainerRequest;
import org.milyn.container.standalone.StandaloneContainerSession;
import org.milyn.delivery.SmooksXML;
import org.milyn.device.ident.UnknownDeviceException;
import org.w3c.dom.Node;

import sun.io.CharToByteConverter;

/**
 * Smooks standalone execution class.
 * <p/>
 * Allows {@link org.milyn.delivery.SmooksXML} to be executed in a "non-container" type environemt e.g.
 * from the commandline.  See {@link org.milyn.report.SmooksReportGenerator} as an example of a class
 * using SmooksStandalone.
 * <p/>
 * This class effectively turns the file system into an execution "container" for
 * {@link org.milyn.delivery.SmooksXML}.  It requires a "baseDir" as a construction parameter,  
 * and expects the baseDir file and directory structure to be similar to that of the {@link org.milyn.SmooksServletFilter}
 * WEB-INF folder (the WEB-INF folder is the {@link org.milyn.SmooksServletFilter} equivalent to this classes baseDir folder).
 * @author tfennelly
 */
public class SmooksStandalone {

	private StandaloneContainerContext context;
	private StandaloneContainerSession session;
	private StandaloneContainerRequest request;
	private String contentEncoding;
	
	/**
	 * Public constructor.
	 * <p/>
	 * Uses the system character encoding for parsing content streams. 
	 * @param baseDir Directory in which the Smooks Standalone Deployment 
	 * is located. 
	 * @param browserName The initial browser to be emulated by this Stanadlone
	 * Smooks instance.  See {@link #setBrowser(String)}.
	 * @throws UnknownDeviceException Thrown when the named device/browser is not known i.e. typically means
	 * the deviceName is not mapped into any profiles in device-profiles.xml.
	 */
	public SmooksStandalone(File baseDir, String browserName) throws UnknownDeviceException {
		context = new StandaloneContainerContext(baseDir);
		setBrowser(browserName);
	}
	
	/**
	 * Public constructor.
	 * @param baseDir Directory in which the Smooks Standalone Deployment 
	 * is located. 
	 * @param browserName The initial browser to be emulated by this Stanadlone
	 * Smooks instance.  See {@link #setBrowser(String)}.
	 * @param contentEncoding Character encoding to be used when parsing content.  Null 
	 * defaults to "ISO-8859-1".
	 * @throws UnknownDeviceException Thrown when the named device/browser is not known i.e. typically means
	 * the deviceName is not mapped into any profiles in device-profiles.xml.
	 */
	public SmooksStandalone(File baseDir, String browserName, String contentEncoding) throws UnknownDeviceException {
		this(baseDir, browserName);
		contentEncoding = (contentEncoding == null)?"ISO-8859-1":contentEncoding;
		try {
			CharToByteConverter.getConverter(contentEncoding);
		} catch (UnsupportedEncodingException e) {
			IllegalArgumentException argE = new IllegalArgumentException("Invalid 'contentEncoding' arg [" + contentEncoding + "].  This encoding is not supported.");
			argE.initCause(e);
			throw argE;
		}
		this.contentEncoding = contentEncoding;
	}
	
	/**
	 * Set the browser to be emulated by this Stanadlone Smooks instance.
	 * <p/>
	 * Must be a browser for which there exists a profile set i.e. has profile
	 * configurations set the device-profiles.xml file.
	 * <p/>
	 * Creates a new {@link StandaloneContainerSession} associated with the browserName.
	 * @param browserName The browser name.
	 * @throws UnknownDeviceException Thrown when the named device/browser is not known i.e. typically means
	 * the deviceName is not mapped into any profiles in device-profiles.xml.
	 */
	public void setBrowser(String browserName) throws UnknownDeviceException {
		if(browserName == null || browserName.trim().equals("")) {
			throw new IllegalArgumentException("null or empty 'browserName' arg in method call.");
		}
		session = new StandaloneContainerSession(browserName, context);
	}
	
	/**
	 * Process the content at the specified URI for the current browser.
	 * <p/>
	 * Calls {@link #process(InputStream)} after opening an {@link InputStream}
	 * to the specified {@link URI}.
	 * @param requestURI URI of the content to be processed.
	 * @return The Smooks processed content DOM {@link Node}.
	 * @throws IOException Is a:<br/>
	 * - {@link MalformedURLException} If a protocol handler for the URL could not be found when
	 * constructing a {@link URL} instance from the supplied {@link URI}, or if 
	 * some other error occurred while constructing the URL.<br/>
	 * - {@link IOException} If unable to read content.
	 * @throws SmooksException Excepting processing content stream.
	 */
	public Node process(URI requestURI) throws IOException, SmooksException {
		return process(requestURI, context.getResourceLocator().getResource(requestURI.toString()));
	}

	/**
	 * Process the content at the specified {@link InputStream} for the current browser.
	 * <p/>
	 * The difference between this method and {@link #process(URI)} is simply that this implementation
	 * uses the supplied stream rather than attempting to open another stream from the requestURI
	 * parameter.  This makes unit testing of this class easier.
	 * <p/>
	 * The content of the buffer returned is totally dependent on the configured
	 * {@link org.milyn.delivery.trans.TransUnit} and {@link org.milyn.delivery.serialize.SerializationUnit}
	 * implementations. 
	 * @param requestURI URI of the content to be processed.
	 * @param stream Stream to be processed.  Will be closed before returning.
	 * @return The Smooks processed content DOM {@link Node}.
	 * @throws SmooksException Excepting processing content stream.
	 */
	public Node process(URI requestURI, InputStream stream) throws SmooksException {
		if(requestURI == null) {
			throw new IllegalArgumentException("null 'requestURI' arg in method call.");
		}
		if(stream == null) {
			throw new IllegalArgumentException("null 'stream' arg in method call.");
		}
		Node node;
		SmooksXML smooks;
		
		request = new StandaloneContainerRequest(requestURI, new LinkedHashMap(), session);
		smooks = new SmooksXML(request);
		if(contentEncoding == null) {
			node = smooks.applyTransform(new InputStreamReader(stream));
		} else {
			try {
				node = smooks.applyTransform(new InputStreamReader(stream, contentEncoding));
			} catch (UnsupportedEncodingException e) {
				Error error = new Error("Unexpected exception.  Encoding has already been validated as being supported.");
				error.initCause(e);
				throw error;
			}
		}
		
		return node;
	}

	/**
	 * Process the content at the specified {@link InputStream} for the current browser
	 * and serialise into a String buffer.  See {@link #process(URI, InputStream)}.
	 * <p/>
	 * The content of the buffer returned is totally dependent on the configured
	 * {@link org.milyn.delivery.trans.TransUnit} and {@link org.milyn.delivery.serialize.SerializationUnit}
	 * implementations. 
	 * @param stream Stream to be processed.  Will be closed before returning.
	 * @return The Smooks processed content buffer.
	 * @throws IOException Exception using or closing the supplied InputStream.
	 * @throws SmooksException Excepting processing content stream.
	 */
	public String processAndSerialize(URI requestURI, InputStream stream) throws IOException, SmooksException {
		String responseBuf = null;
		CharArrayWriter writer = new CharArrayWriter();
		try {
			Node node;

			node = process(requestURI, stream);
			serialize(node, writer);
			responseBuf = writer.toString();
		} finally {
			stream.close();
			writer.close();
		}
		
		return responseBuf;
	}

	/**
	 * Serialise the supplied node based on the current browsers serialisation
	 * configuration.
	 * @param node Node to be serialised.
	 * @param writer Serialisation output writer.
	 * @throws IOException Unable to write to output writer.
	 * @throws SmooksException Unable to serialise due to bad Smooks environment.  Check cause.
	 */
	public void serialize(Node node, Writer writer) throws IOException, SmooksException {
		SmooksXML smooks;
		StandaloneContainerRequest serRequest = getLastRequest();
		
		if(node == null) {
			throw new IllegalArgumentException("null 'node' arg in method call.");
		}
		if(writer == null) {
			throw new IllegalArgumentException("null 'writer' arg in method call.");
		}
		
		if(serRequest == null) {
			// Create a "bogus" request - fine for serialization.  This is an unexpected
			// usecase but one we can still handle!!  We'd expect that this method
			// only be called after calling the process method.
			serRequest = new StandaloneContainerRequest(URI.create("http://x.com"), new LinkedHashMap(), session);
		}
		smooks = new SmooksXML(request);
		smooks.serialize(node, writer);
	}

	/**
	 * Get the current {@link StandaloneContainerSession} associated with the
	 * current browser.
	 * <p/>
	 * See {@link #setBrowser(String)}.
	 * @return
	 */
	public StandaloneContainerSession getSession() {
		return session;
	}

	/**
	 * Get the last request {@link #processAndSerialize(URI, InputStream) processed} on this
	 * instance.
	 * @return The last request {@link #processAndSerialize(URI, InputStream) processed} on this
	 * instance, or null if none have yet been processed.
	 */
	public StandaloneContainerRequest getLastRequest() {
		return request;
	}
}
