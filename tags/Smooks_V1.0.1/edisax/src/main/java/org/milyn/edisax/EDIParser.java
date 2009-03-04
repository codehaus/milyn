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

package org.milyn.edisax;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.milyn.assertion.AssertArgument;
import org.milyn.io.StreamUtils;
import org.milyn.schema.ediMessageMapping10.ComponentDocument.Component;
import org.milyn.schema.ediMessageMapping10.DelimitersDocument.Delimiters;
import org.milyn.schema.ediMessageMapping10.EdimapDocument;
import org.milyn.schema.ediMessageMapping10.EdimapDocument.Edimap;
import org.milyn.schema.ediMessageMapping10.FieldDocument.Field;
import org.milyn.schema.ediMessageMapping10.SegmentDocument.Segment;
import org.milyn.schema.ediMessageMapping10.SubComponentDocument.SubComponent;
import org.milyn.xml.XmlUtil;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;

/**
 * EDI Parser.
 * <p/>
 * Generates a stream of SAX events from an EDI message stream based on the supplied
 * {@link #setMappingModel(Edimap) mapping model}.
 * 
 * <h3>Usage</h3>
 * <pre>
 * 	InputStream ediInputStream = ....
 * 	InputStream <a href="http://www.milyn.org/schema/edi-message-mapping-1.0.xsd">edi2SaxMappingConfig</a> = ....
 * 	{@link org.xml.sax.ContentHandler} contentHandler = ....
 * 		
 * 	EDIParser parser = new EDIParser();
 * 		
 * 	parser.setContentHandler(contentHandler);
 * 	parser.{@link #setMappingModel(Edimap) setMappingModel}(EDIParser.{@link #parseMappingModel(InputStream) parseMappingModel}(<a href="http://www.milyn.org/schema/edi-message-mapping-1.0.xsd">edi2SaxMappingConfig</a>));
 * 	parser.parse(new InputSource(ediInputStream));
 * 	etc... 
 * </pre>
 *
 * <h3>Mapping Model</h3>
 * The EDI to SAX Event mapping is performed based on an "Mapping Model" supplied to
 * the parser.  This model must be based on the 
 * <a href="http://www.milyn.org/schema/edi-message-mapping-1.0.xsd">edi-message-mapping-1.0.xsd</a>
 * schema.
 * <p/>
 * From this schema you can see that segment groups are supported (nested segments), including groups within groups,
 * repeating segments and repeating segment groups.  Be sure to review the 
 * <a href="http://www.milyn.org/schema/edi-message-mapping-1.0.xsd">schema</a>.
 *
 * <h3>Example (Input EDI, EDI to XML Mapping and Output SAX Events)</h3>
 * The following illustration attempts to create a visualisation of the mapping process.  The "input-message.edi" file
 * specifies the EDI input, "edi-to-xml-order-mapping.xml" describes how to map that EDI message to SAX events and
 * "expected.xml" illustrates the XML that would result from applying the mapping.
 * <p/>
 * <img src="doc-files/edi-mapping.png" />
 * <p/>
 * So the above illustration attempts to highlight the following:
 * <ol>
 * 	<li>How the message delimiters (segment, field, component and sub-component) are specified in the mapping.  In particular, how special 
 * 		characters like the linefeed character are specified using XML Character References.</li>
 * 	<li>How segment groups (nested segments) are specified.  In this case the first 2 segments are part of a group.</li>
 * 	<li>How the actual field, component and sub-component values are specified and mapped to the target SAX events (to generate the XML).</li>
 * </ol>
 * 
 * <h3>Segment Cardinality</h3>
 * What's not shown above is how the &lt;medi:segment&gt; element supports the 2 optional attributes "minOccurs" and
 * "maxOccurs" (default value of 1 in both cases).  These attributes can be used to control the optional and required
 * characteristics of a segment.  A maxOccurs value of -1 indicates that the segment can repeat any number of times
 * in that location of the EDI message (unbounded).
 *
 * <h3>Required Values</h3>
 * &lt;field&gt;, &lt;component&gt; and &lt;sub-component&gt; configurations support a "required" attribute, which
 * flags that &lt;field&gt;, &lt;component&gt; or &lt;sub-component&gt; as requiring a value.
 * <p/>
 * By default, values are not required (fields, components and sub-components).
 *
 * <h3>Truncation</h3>
 * &lt;segment&gt;, &lt;field&gt; and &lt;component&gt; configurations support a "truncatable" attribute.  For a
 * segment, this means that parser errors will not be generated when that segment does not specify trailing
 * fields that are not "required" (see "required" attribute above). Likewise for fields/components and
 * components/sub-components.
 * <p/>
 * By default, segments, fields, and components are not truncatable.
 *
 * @author tfennelly
 */
public class EDIParser implements XMLReader {

    private ContentHandler contentHandler;
    private int depth = 0;
    private static Attributes EMPTY_ATTRIBS = new AttributesImpl();
    private Edimap mappingModel;
    private Delimiters delimiters;
    private BufferedSegmentReader segmentReader;

    /**
     * Parse the supplied mapping model config stream and return the generated EdiMap.
     * <p/>
     * Can be used to set the mapping model to be used during the parsing operation.
     * See {@link #setMappingModel(Edimap)}.
     * @param mappingConfigStream Config stream.  Must conform with the
     * <a href="http://www.milyn.org/schema/edi-message-mapping-1.0.xsd">edi-message-mapping-1.0.xsd</a>
     * schema.
     * @return The Edimap for the mapping model.
     * @throws IOException Error reading the model stream.
     * @throws SAXException Invalid model.
     */
    public static Edimap parseMappingModel(InputStream mappingConfigStream) throws IOException, SAXException {
        AssertArgument.isNotNull(mappingConfigStream, "mappingConfigStream");
        try {
            return parseMappingModel(new InputStreamReader(mappingConfigStream));
        } finally {
            mappingConfigStream.close();
        }
    }

    /**
     * Parse the supplied mapping model config stream and return the generated EdiMap.
     * <p/>
     * Can be used to set the mapping model to be used during the parsing operation.
     * See {@link #setMappingModel(Edimap)}.
     * @param mappingConfigStream Config stream.  Must conform with the
     * <a href="http://www.milyn.org/schema/edi-message-mapping-1.0.xsd">edi-message-mapping-1.0.xsd</a>
     * schema.
     * @return The Edimap for the mapping model.
     * @throws IOException Error reading the model stream.
     * @throws SAXException Invalid model.
     */
    public static Edimap parseMappingModel(Reader mappingConfigStream) throws IOException, SAXException {
    	AssertArgument.isNotNull(mappingConfigStream, "mappingConfigStream");
    	
    	Edimap mappingModel = null;
    	String mappingConfig;
    	
    	try {
    		mappingConfig = StreamUtils.readStream(mappingConfigStream);
    	} finally {
    		mappingConfigStream.close();
    	}
    	
    	assertMappingConfigValid(new StringReader(mappingConfig));
    	try {
			mappingModel = EdimapDocument.Factory.parse(new StringReader(mappingConfig)).getEdimap();
		} catch (XmlException e) {
			new SAXException("EDI Mapping Model parse failure.", e);
		}
		
		// Rewrite any entities used in the delimiter definitions.  Can use entity/character resfs
		// to define special characters e.g. CR or LF.
		Delimiters delimiters = mappingModel.getDelimiters();
		delimiters.setSegment(XmlUtil.removeEntities(delimiters.getSegment()));
		delimiters.setField(XmlUtil.removeEntities(delimiters.getField()));
		delimiters.setComponent(XmlUtil.removeEntities(delimiters.getComponent()));
		delimiters.setSubComponent(XmlUtil.removeEntities(delimiters.getSubComponent()));
		
		return mappingModel;
    }
    
    /**
     * Assert that the supplied mapping configuration is valid.
	 * @param mappingConfigStream
     * @throws IOException Failed to read the schema.
     * @throws SAXException Invalid configuration.
	 */
	protected static void assertMappingConfigValid(Reader mappingConfigStream) throws IOException, SAXException {
    	AssertArgument.isNotNull(mappingConfigStream, "mapping");
		
   		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
   		Schema schema = factory.newSchema(new StreamSource(EDIParser.class.getResourceAsStream("/schema/edi-message-mapping-1.0.xsd")));
		Validator validator = schema.newValidator();
		
		validator.validate(new StreamSource(mappingConfigStream));
	}

	/**
	 * Set the EDI mapping model to be used in all subsequent parse operations.
	 * <p/>
	 * The model can be generated through a call to {@link #parseMappingModel(InputStream)}.
	 * 
	 * @param mappingModel The mapping model.
	 */
	public void setMappingModel(Edimap mappingModel) {
    	AssertArgument.isNotNull(mappingModel, "mappingModel");
    	this.mappingModel = mappingModel;
    	delimiters = mappingModel.getDelimiters();
    }
    
    /**
     * Parse an EDI InputSource.
     */
    public void parse(InputSource ediInputSource) throws IOException, SAXException {
        if(contentHandler == null) {
            throw new IllegalStateException("'contentHandler' not set.  Cannot parse EDI stream.");
        }
        if(mappingModel == null) {
            throw new IllegalStateException("'mappingModel' not set.  Cannot parse EDI stream.");
        }
        
        // Create a reader for reading the EDI segments...
        segmentReader = new BufferedSegmentReader(ediInputSource, delimiters);
        
        // Fire the startDocument event, as well as the startElement event...
        contentHandler.startDocument();
        startElement(mappingModel.getSegments().getXmltag(), false);

        // Work through all the segments in the model.  Move to the first segment before starting...
        if(segmentReader.moveToNextSegment()) {
        	mapSegments(mappingModel.getSegments().getSegmentArray());

    		// If we reach the end of the mapping model and we still have more EDI segments in the message.... 
    		if(segmentReader.hasCurrentSegment()) {
    			throw new EDIParseException(mappingModel, "Reached end of mapping model but there are more EDI segments in the incoming message.  Read " + segmentReader.getCurrentSegmentNumber() + " segment(s).");
    		}
        }

        // Fire the endDocument event, as well as the endElement event...
        endElement(mappingModel.getSegments().getXmltag(), true);
        contentHandler.endDocument();
    }

    /**
     * Map a list of EDI Segments to SAX events.
     * <p/>
     * Reads the segments from the input stream and maps them based on the supplied list of expected segments.
	 * @param expectedSegments The list of expected segments.
     * @throws IOException Error reading an EDI segment from the input stream.
     * @throws SAXException EDI processing exception.
	 */
	private void mapSegments(Segment[] expectedSegments) throws IOException, SAXException {
		int segmentMappingIndex = 0; // The current index within the supplied segment list.
		int segmentProcessingCount = 0; // The number of times the current segment definition from the supplied segment list has been applied to message segments on the incomming EDI message.
		
		if(expectedSegments.length == 0) {
			return;
		}
		
		while(segmentMappingIndex < expectedSegments.length && segmentReader.hasCurrentSegment()) {
			Segment expectedSegment = expectedSegments[segmentMappingIndex];
			int minOccurs = expectedSegment.getMinOccurs();
			int maxOccurs = expectedSegment.getMaxOccurs();
	
			// A negative max value indicates an unbound max....
			if(maxOccurs < 0) {
				maxOccurs = Integer.MAX_VALUE;
			}
			// Make sure min is not greater than max...
			if(minOccurs > maxOccurs) {
				maxOccurs = minOccurs;
			}
			
			String[] currentSegmentFields = segmentReader.getCurrentSegmentFields();
			
			// If the current segment being read from the incomming message doesn't match the expected
			// segment code....
			if(!currentSegmentFields[0].equals(expectedSegment.getSegcode())) {
				// If we haven't read the minimum number of instances of the current "expected" segment, raise an error...
				if(segmentProcessingCount < minOccurs) {
					throw new EDIParseException(mappingModel, "Must be a minimum of " + minOccurs + " instances of segment [" + expectedSegment.getSegcode() + "].  Currently at segment number " + segmentReader.getCurrentSegmentNumber() + ".");
				} else {
					// Otherwise, move to the next "expected" segment and start the loop again...
					segmentMappingIndex++;
					segmentProcessingCount = 0;
					continue;
				}
			}

			// Make sure we haven't encountered a message with too many instances of the current expected segment...
			if(segmentProcessingCount >= maxOccurs) {
				throw new EDIParseException(mappingModel, "Maximum of " + maxOccurs + " instances of segment [" + expectedSegment.getSegcode() + "] exceeded.  Currently at segment number " + segmentReader.getCurrentSegmentNumber() + ".");
			}
			
			// The current read message segment appears to match that expected according to the mapping model.
			// Proceed to process the segment fields and the segments sub-segments...
			mapSegment(currentSegmentFields, expectedSegment);
			
			// Increment the count on the number of times the current "expected" mapping config has been applied...
			segmentProcessingCount++;

			while(segmentProcessingCount < minOccurs && !segmentReader.hasCurrentSegment()) {
				throw new EDIParseException(mappingModel, "Reached end of EDI message stream but there must be a minimum of " + minOccurs + " instances of segment [" + expectedSegment.getSegcode() + "].  Currently at segment number " + segmentReader.getCurrentSegmentNumber() + ".");
			}
		}
	}

	/**
	 * Map a single segment based on the current set of segment fields read from input and the segment mapping
	 * config that these fields should map to.
	 * @param currentSegmentFields Current set of segment fields read from input.
	 * @param expectedSegment The segment mapping config that the currentSegmentFields should map to.
     * @throws IOException Error reading an EDI segment from the input stream.  This will happen as the segment
     * reader tries to move to the next segment after performing this mapping.
     * @throws SAXException EDI processing exception.
	 */
	private void mapSegment(String[] currentSegmentFields, Segment expectedSegment) throws IOException, SAXException {
        startElement(expectedSegment.getXmltag(), true);

        mapFields(currentSegmentFields, expectedSegment);
		if(segmentReader.moveToNextSegment()) {
			mapSegments(expectedSegment.getSegmentArray());
		}
		
        endElement(expectedSegment.getXmltag(), true);
	}

	/**
	 * Map the individual field values based on the supplied expected field configs.
	 * @param currentSegmentFields Segment fields from the input message.
	 * @param segment List of expected field mapping configurations that the currentSegmentFields
	 * are expected to map to.
     * @throws SAXException EDI processing exception.
	 */
	private void mapFields(String[] currentSegmentFields, Segment segment) throws SAXException {
        String segmentCode = segment.getSegcode();
        Field[] expectedFields = segment.getFieldArray();

        // Make sure all required fields are present in the incoming message...
        assertFieldsOK(currentSegmentFields, segment);

		// Iterate over the fields and map them...
        int numFields = currentSegmentFields.length - 1; // It's "currentSegmentFields.length - 1" because we don't want to include the segment code.
		for(int i = 0; i < numFields; i++) {
			String fieldMessageVal = currentSegmentFields[i + 1]; // +1 to skip the segment code
			Field expectedField = expectedFields[i];
			
			mapField(fieldMessageVal, expectedField, i, segmentCode);
		}
	}

    /**
	 * Map an individual segment field.
	 * @param fieldMessageVal The field message value.
	 * @param expectedField The mapping config to which the field value is expected to map.
	 * @param fieldIndex The field index within its segment (base 0).
	 * @param segmentCode The segment code within which the field exists.
     * @throws SAXException EDI processing exception.
	 */
	private void mapField(String fieldMessageVal, Field expectedField, int fieldIndex, String segmentCode) throws SAXException {
		Component[] expectedComponents = expectedField.getComponentArray();

        startElement(expectedField.getXmltag(), true);

		// If there are components defined on this field...
		if(expectedComponents.length != 0) {
			String[] currentFieldComponents = StringUtils.splitPreserveAllTokens(fieldMessageVal, mappingModel.getDelimiters().getComponent());

            assertComponentsOK(expectedField, fieldIndex, segmentCode, expectedComponents, currentFieldComponents);

            // Iterate over the field components and map them...
			for(int i = 0; i < currentFieldComponents.length; i++) {
				String componentMessageVal = currentFieldComponents[i];
				Component expectedComponent = expectedComponents[i];

				mapComponent(componentMessageVal, expectedComponent, fieldIndex, i, segmentCode, expectedField.getXmltag());
			}
	        endElement(expectedField.getXmltag(), true);
		} else {
            if(expectedField.isSetRequired() && fieldMessageVal.length() == 0) {
                throw new EDIParseException(mappingModel, "Segment [" + segmentCode + "], field " + (fieldIndex + 1) + " (" + expectedField.getXmltag() + ") expected to contain a value.  Currently at segment number " + segmentReader.getCurrentSegmentNumber() + ".");
            }

            contentHandler.characters(fieldMessageVal.toCharArray(), 0, fieldMessageVal.length());
	        endElement(expectedField.getXmltag(), false);
		}
	}

    /**
	 * Map an individual component.
	 * @param componentMessageVal Component message value read from EDI input.
	 * @param expectedComponent The mapping config to which the component value is expected to map.
	 * @param fieldIndex The field index within its segment (base 0) in which the component exists.
	 * @param componentIndex The component index within its field (base 0).
	 * @param segmentCode The segment code within which the component exists.
	 * @param field Field within which the component exists.
     * @throws SAXException EDI processing exception.
	 */
	private void mapComponent(String componentMessageVal, Component expectedComponent, int fieldIndex, int componentIndex, String segmentCode, String field) throws SAXException {
		SubComponent[] expectedSubComponents = expectedComponent.getSubComponentArray();

		startElement(expectedComponent.getXmltag(), true);

		if(expectedSubComponents.length != 0) {
			String[] currentComponentSubComponents = StringUtils.splitPreserveAllTokens(componentMessageVal, mappingModel.getDelimiters().getSubComponent());

            assertSubComponentsOK(expectedComponent, fieldIndex, componentIndex, segmentCode, field, expectedSubComponents, currentComponentSubComponents);

            for(int i = 0; i < currentComponentSubComponents.length; i++) {
                if(expectedSubComponents[i].isSetRequired() && currentComponentSubComponents[i].length() == 0) {
                    throw new EDIParseException(mappingModel, "Segment [" + segmentCode + "], field " + (fieldIndex + 1) + " (" + field + "), component " + (componentIndex + 1) + " (" + expectedComponent.getXmltag() + "), sub-component " + (i + 1) + " (" + expectedSubComponents[i].getXmltag() + ") expected to contain a value.  Currently at segment number " + segmentReader.getCurrentSegmentNumber() + ".");
                }

				startElement(expectedSubComponents[i].getXmltag(), true);
				contentHandler.characters(currentComponentSubComponents[i].toCharArray(), 0, currentComponentSubComponents[i].length());
				endElement(expectedSubComponents[i].getXmltag(), false);
			}
			endElement(expectedComponent.getXmltag(), true);
		} else {
            if(expectedComponent.isSetRequired() && componentMessageVal.length() == 0) {
                throw new EDIParseException(mappingModel, "Segment [" + segmentCode + "], field " + (fieldIndex + 1) + " (" + field + "), component " + (componentIndex + 1) + " (" + expectedComponent.getXmltag() + ") expected to contain a value.  Currently at segment number " + segmentReader.getCurrentSegmentNumber() + ".");
            }

			contentHandler.characters(componentMessageVal.toCharArray(), 0, componentMessageVal.length());
			endElement(expectedComponent.getXmltag(), false);
		}
	}

    private void assertFieldsOK(String[] currentSegmentFields, Segment segment) throws EDIParseException {
        Field[] expectedFields = segment.getFieldArray();
        int numFieldsExpected = expectedFields.length + 1; // It's "expectedFields.length + 1" because the segment code is included.

        if(currentSegmentFields.length != numFieldsExpected) {
            boolean throwException = false;

            // If we don't have all the fields we're expecting, check is the Segment truncatable
            // and are the missing fields required or not...
            if(segment.isSetTruncatable()) {
                int numFieldsMissing = numFieldsExpected - currentSegmentFields.length;
                for(int i = expectedFields.length - 1; i > (expectedFields.length - numFieldsMissing - 1); i--) {
                    if(expectedFields[i].isSetRequired()) {
                        throwException = true;
                        break;
                    }
                }
            } else {
                throwException = true;
            }

            if(throwException) {
                throw new EDIParseException(mappingModel, "Segment [" + segment.getSegcode() + "] expected to contain " + (numFieldsExpected - 1) + " fields.  Actually contains " + (currentSegmentFields.length - 1) + " fields (not including segment code).  Currently at segment number " + segmentReader.getCurrentSegmentNumber() + ".");
            }
        }
    }

    private void assertComponentsOK(Field expectedField, int fieldIndex, String segmentCode, Component[] expectedComponents, String[] currentFieldComponents) throws EDIParseException {
        if (currentFieldComponents.length != expectedComponents.length) {
            boolean throwException = false;

            if (expectedField.isSetTruncatable()) {
                int numComponentsMissing = expectedComponents.length - currentFieldComponents.length;
                for (int i = expectedComponents.length - 1; i > (expectedComponents.length - numComponentsMissing - 1); i--)
                {
                    if (expectedComponents[i].isSetRequired()) {
                        throwException = true;
                        break;
                    }
                }
            } else {
                throwException = true;
            }

            if (throwException) {
                throw new EDIParseException(mappingModel, "Segment [" + segmentCode + "], field " + (fieldIndex + 1) + " (" + expectedField.getXmltag() + ") expected to contain " + expectedComponents.length + " components.  Actually contains " + currentFieldComponents.length + " components.  Currently at segment number " + segmentReader.getCurrentSegmentNumber() + ".");
            }
        }
    }

    private void assertSubComponentsOK(Component expectedComponent, int fieldIndex, int componentIndex, String segmentCode, String field, SubComponent[] expectedSubComponents, String[] currentComponentSubComponents) throws EDIParseException {
        if (currentComponentSubComponents.length != expectedSubComponents.length) {
            boolean throwException = false;

            if (expectedComponent.isSetTruncatable()) {
                int numSubComponentsMissing = expectedSubComponents.length - currentComponentSubComponents.length;
                for (int i = expectedSubComponents.length - 1; i > (expectedSubComponents.length - numSubComponentsMissing - 1); i--)
                {
                    if (expectedSubComponents[i].isSetRequired()) {
                        throwException = true;
                        break;
                    }
                }
            } else {
                throwException = true;
            }

            if (throwException) {
                throw new EDIParseException(mappingModel, "Segment [" + segmentCode + "], field " + (fieldIndex + 1) + " (" + field + "), component " + (componentIndex + 1) + " (" + expectedComponent.getXmltag() + ") expected to contain " + expectedSubComponents.length + " sub-components.  Actually contains " + currentComponentSubComponents.length + " sub-components.  Currently at segment number " + segmentReader.getCurrentSegmentNumber() + ".");
            }
        }
    }

    private void startElement(String elementName, boolean indent) throws SAXException {
        if(indent) {
            indent();
        }
        contentHandler.startElement(null, elementName, "", EMPTY_ATTRIBS);
        depth++;
    }

    private void endElement(String elementName, boolean indent) throws SAXException {
        depth--;
        if(indent) {
            indent();
        }
        contentHandler.endElement(null, elementName, "");
    }

    // HACK :-) it's hardly going to be deeper than this!!
    private static final char[] indentChars = (new String("\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t").toCharArray());
    private void indent() throws SAXException {
        contentHandler.characters(indentChars, 0, depth + 1);
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    /****************************************************************************
     *
     * The following methods are currently unimplemnted...
     *
     ****************************************************************************/

    public void parse(String systemId) throws IOException, SAXException {
        throw new UnsupportedOperationException("Operation not supports by this reader.");
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        return false;
    }

    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    public DTDHandler getDTDHandler() {
        return null;
    }

    public void setDTDHandler(DTDHandler arg0) {
    }

    public EntityResolver getEntityResolver() {
        return null;
    }

    public void setEntityResolver(EntityResolver arg0) {
    }

    public ErrorHandler getErrorHandler() {
        return null;
    }

    public void setErrorHandler(ErrorHandler arg0) {
    }

    public Object getProperty(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        return null;
    }

    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
    }
}