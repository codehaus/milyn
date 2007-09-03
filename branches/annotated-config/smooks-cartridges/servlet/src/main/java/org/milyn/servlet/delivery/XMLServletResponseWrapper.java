package org.milyn.servlet.delivery;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.dom.SmooksDOMFilter;
import org.milyn.xml.Parser;
import org.milyn.servlet.http.HeaderAction;
import org.milyn.servlet.parse.HTMLSAXParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * XML/XHTML/HTML Servlet Response Wrapper.
 * <p/>
 * This class hooks the {@link org.milyn.delivery.dom.SmooksDOMFilter} class into a
 * Servlet response filter chain via the {@link org.milyn.servlet.SmooksServletFilter}.  
 * <p/>
 * <b style="color: red">This is the default ServletResponse wrapper.</b>
 * @author tfennelly
 */
public class XMLServletResponseWrapper extends ServletResponseWrapper {

	/**
	 * Default parser configuration.
	 */
	private static SmooksResourceConfiguration defaultSAXParserConfig;
	/**
	 * Request specific SAX parser configuration.  If not specified
	 * (See {@link Parser}), {@link #defaultSAXParserConfig} is used.
	 */
	private SmooksResourceConfiguration requestSAXParserConfig;
	/**
	 * Logger.
	 */
	private static Log logger = LogFactory.getLog(XMLServletResponseWrapper.class);
	/**
	 * ServletOutputStream instance - if getOutputStream is called by filter delegate.
	 */
	private SmooksServletOutputStream stream;
	/**
	 * PrintWriter instance - if getWriter is called by filter delegate.
	 */
	private PrintWriter printWriter;
	/**
	 * Smooks delivery instance.
	 */
	private SmooksDOMFilter smooks;
	/**
	 * The CharArrayWriter capturing the content.
	 */
	private CharArrayWriter charArrayWriter = new CharArrayWriter();
	/**
	 * List of 'add' HeaderAction instances.
	 */
	private List addHeaderActions = new Vector();
	/**
	 * List of 'remove' HeaderAction instances.
	 */
	private List removeHeaderActions = new Vector();
	/**
	 * Key for access a request bound DOM passed from a down-stream
	 * Servlet or Phase.
	 */
	public static final String SOURCE_DOCUMENT = XMLServletResponseWrapper.class.toString();
	
	/**
	 * Constructor.
	 * @param executionContext Container Request.
	 * @param originalResponse Original servlet response.
	 */
	public XMLServletResponseWrapper(ExecutionContext executionContext, HttpServletResponse originalResponse) {
		super(executionContext, originalResponse);
		smooks = new SmooksDOMFilter(executionContext);
		requestSAXParserConfig = Parser.getSAXParserConfiguration(executionContext.getDeliveryConfig());
		initHeaderActions(executionContext.getDeliveryConfig().getObjects("http-response-header"));
	}
	
	static {
		defaultSAXParserConfig = new SmooksResourceConfiguration("org.xml.sax.driver", HTMLSAXParser.class.getName());
	}
	
	/**
	 * Initialise the 'add' and 'remove' header actions.
	 * <p/>
	 * Expand the header actions into 2 seperate lists for add and remove.
	 * @param headerActions
	 */
	private void initHeaderActions(List headerActions) {
		if(headerActions == null) {
			return;
		}
		
		for(int i = 0; i < headerActions.size(); i++) {
			HeaderAction action = (HeaderAction)headerActions.get(i);
			
			if(action.getAction() == HeaderAction.ACTION_ADD) {
				addHeaderActions.add(action);
			} else {
				removeHeaderActions.add(action);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getOutputStream()
	 */
	public ServletOutputStream getOutputStream() throws IOException {
		if(printWriter != null) {
			throw new IllegalStateException("Call to 'getOutputStream()' after call to 'getWriter()'.");
		}
		if(stream != null) {
			return stream;
		}
		stream = new SmooksServletOutputStream(this, charArrayWriter);
		return stream;
	}		
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getWriter()
	 */
	public PrintWriter getWriter() throws IOException {
		if(stream != null) {
			throw new IllegalStateException("Call to 'getWriter()' after call to 'getOutputStream()'.");
		}
		if(printWriter != null) {
			return printWriter;
		}
		printWriter = new SmooksPrintWriter(charArrayWriter, this);
		return printWriter;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
	 */
	public void addHeader(String name, String value) {
		if(assertAddHeader(name)) {
			super.addHeader(name, value);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
	 */
	public void setDateHeader(String name, long value) {
		if(assertAddHeader(name)) {
			super.setDateHeader(name, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
	 */
	public void setHeader(String name, String value) {
		if(assertAddHeader(name)) {
			super.setHeader(name, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
	 */
	public void setIntHeader(String name, int value) {
		if(assertAddHeader(name)) {
			super.setIntHeader(name, value);
		}
	}
	
	/**
	 * Assert whether or not it's OK to add the header - is it absent
	 * from the list of removeHeaders. 
	 * @param name The name of the header
	 * @return True if it's OK to add the named header to the response, otherwise false.
	 */
	private boolean assertAddHeader(String name) {
		int removeCount = removeHeaderActions.size();
		
		for(int i = 0; i < removeCount; i++){
			if(removeHeaderActions.get(i).equals(name)) {
				return false;
			}
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.milyn.delivery.response.ServletResponseWrapper#deliverResponse()
	 */
	public void deliverResponse() throws IOException {
		ServletOutputStream targetOutputStream;
		OutputStreamWriter writer;
		HtmlByteArrayOutputStream outStream = new HtmlByteArrayOutputStream(1024 * 10);
		char[] content;

		modifyResponseHeaders();
		
		targetOutputStream = getResponse().getOutputStream();
		writer = new OutputStreamWriter(outStream, getCharacterEncoding());
		content = charArrayWriter.toCharArray();
		
		try {
			Node deliveryNode;
			Document sourceDoc = (Document)getContainerRequest().getAttribute(XMLServletResponseWrapper.SOURCE_DOCUMENT);
			
			if(sourceDoc == null) {
				Reader inputReader = new CharArrayReader(content);
                Parser parser;
                
                if(requestSAXParserConfig != null) {
                	parser = new Parser(getContainerRequest(), requestSAXParserConfig);
                } else {
                	parser = new Parser(getContainerRequest(), defaultSAXParserConfig);
                }

                try {
                    sourceDoc = parser.parse(inputReader);
                } catch (SAXException e) {
                    throw new SmooksException("Unable to filter InputStream for target useragent [" + getContainerRequest().getTargetProfiles().getBaseProfile() + "].", e);
                } 

				logger.info("Filtering content stream from down-stream Servlet/Phase.");
                deliveryNode = smooks.filter(sourceDoc);
			} else {
				logger.info("Filtering W3C DOM from down-stream Servlet/Phase.");
				deliveryNode = smooks.filter(sourceDoc);
			}
			smooks.serialize(deliveryNode, writer);
			writer.flush();
			super.setIntHeader("Content-Length", outStream.size());
			outStream.writeToTarget(targetOutputStream);
		} catch (SmooksException e) {
			IOException ioE = new IOException("Unable to deliver response.");
			PrintWriter printWriter = new PrintWriter(writer);
			String contentString = new String(content);
			
			ioE.initCause(e);
			printWriter.println("<pre>");
			ioE.printStackTrace(printWriter);
			printWriter.println("</pre>");
			printWriter.println("<hr/>");
			printWriter.println("<pre>");
			contentString = contentString.replaceAll("<", "&lt;");
			contentString = contentString.replaceAll(">", "&gt;");
			printWriter.write(contentString);
			printWriter.println("</pre>");
			
			throw ioE;
		} finally {
			targetOutputStream.flush();
		}
	}

	/**
	 * Modify the response headers.
	 */
	private void modifyResponseHeaders() {
		ServletResponse response = getResponse();
		
		// Apply the 'add' header actions.
		for(int i = 0; i < addHeaderActions.size(); i++) {
			HeaderAction action = (HeaderAction)addHeaderActions.get(i);
			
			if(action.getHeaderName().equals("Content-Type")) {
				response.setContentType(action.getHeaderValue());
			} else if(action.getHeaderName().equals("Content-Length")) {
				try {
					response.setContentLength(Integer.parseInt(action.getHeaderValue()));
				} catch(NumberFormatException fe) {
					// Ignore
				}
			} else {
				super.setHeader(action.getHeaderName(), action.getHeaderValue());
			}
		}
	}

	/**
	 * Ensure all resources etc are closed
	 */
	public void close() {
		if(printWriter != null) {
			printWriter.close();
		}
		if(stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				logger.warn("Response wrapper stream close error.", e);
			}
		}
	}


	/**
	 * Smooks servlet output stream.
	 * <p/>
	 * Performs the filtering of the content from a binary source.
	 * @author tfennelly
	 */
	class SmooksServletOutputStream extends ServletOutputStream {

		/**
		 * Content buffer writer.
		 */
		CharArrayWriter charArrayWriter;
		/**
		 * Content buffer print writer.
		 */
		PrintWriter printWriter;
		/**
		 * Wrapped response.
		 */
		ServletResponseWrapper responseWrapper;
		/**
		 * Charset for binary decoding.
		 */
		String charSet; 
		
		/**
		 * Constructor.
		 * @param responseWrapper Response wrapper instance.
		 */
		private SmooksServletOutputStream(XMLServletResponseWrapper responseWrapper, CharArrayWriter charArrayWriter) {
			this.responseWrapper = responseWrapper;
			this.charArrayWriter = charArrayWriter;
			printWriter = new PrintWriter(charArrayWriter);
			charSet = responseWrapper.getCharacterEncoding();
		}		
		
		/* (non-Javadoc)
		 * @see java.io.OutputStream#close()
		 */
		public void close() throws IOException {
			charArrayWriter.close();
			printWriter.close();
			super.close();
		}
		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int b) throws IOException {
			printWriter.write(b);
		}		
		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[], int, int)
		 */
		public void write(byte[] b, int off, int len) throws IOException {
			printWriter.write(new String(b, off, len, charSet));
		}
		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[])
		 */
		public void write(byte[] b) throws IOException {
			printWriter.write(new String(b, charSet));
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#print(boolean)
		 */
		public void print(boolean arg0) throws IOException {
			printWriter.print(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#print(char)
		 */
		public void print(char arg0) throws IOException {
			printWriter.print(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#print(double)
		 */
		public void print(double arg0) throws IOException {
			printWriter.print(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#print(float)
		 */
		public void print(float arg0) throws IOException {
			printWriter.print(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#print(int)
		 */
		public void print(int arg0) throws IOException {
			printWriter.print(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#print(long)
		 */
		public void print(long arg0) throws IOException {
			printWriter.print(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#print(java.lang.String)
		 */
		public void print(String arg0) throws IOException {
			printWriter.print(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#println()
		 */
		public void println() throws IOException {
			printWriter.println();
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#println(boolean)
		 */
		public void println(boolean arg0) throws IOException {
			printWriter.println(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#println(char)
		 */
		public void println(char arg0) throws IOException {
			printWriter.println(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#println(double)
		 */
		public void println(double arg0) throws IOException {
			printWriter.println(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#println(float)
		 */
		public void println(float arg0) throws IOException {
			printWriter.println(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#println(int)
		 */
		public void println(int arg0) throws IOException {
			printWriter.println(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#println(long)
		 */
		public void println(long arg0) throws IOException {
			printWriter.println(arg0);
		}
		/* (non-Javadoc)
		 * @see javax.servlet.ServletOutputStream#println(java.lang.String)
		 */
		public void println(String arg0) throws IOException {
			printWriter.println(arg0);
		}
	}
	
	/**
	 * Smooks servlet PrintWriter.
	 * <p/>
	 * Performs the filtering of the content from a character source.
	 * @author tfennelly
	 */
	class SmooksPrintWriter extends PrintWriter {

		/**
		 * The CharArrayWriter capturing the content.
		 */
		CharArrayWriter charArrayWriter;
		/**
		 * Wrapped response.
		 */
		ServletResponseWrapper responseWrapper;

		/**
		 * Constructor.
		 * @param charArrayWriter Character buffer for catching the content.
		 * @param responseWrapper Response wrapper instance.
		 * @throws IOException
		 */
		private SmooksPrintWriter(CharArrayWriter charArrayWriter, ServletResponseWrapper responseWrapper) throws IOException {
			super(charArrayWriter);
			this.charArrayWriter = charArrayWriter;
			this.responseWrapper = responseWrapper;
		}
		
		/* (non-Javadoc)
		 * @see java.io.Writer#close()
		 */
		public void close() {
			super.close();
			charArrayWriter.close();
		}
	}

	private class HtmlByteArrayOutputStream extends ByteArrayOutputStream {
		public HtmlByteArrayOutputStream(int size) {
			super(size);
		}
		private void writeToTarget(OutputStream outStream) throws IOException {
			outStream.write(buf, 0, count);
		}
	}
}