package org.milyn.edisax;

import org.milyn.io.NullWriter;
import org.milyn.schema.edi_message_mapping_1_0.*;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

/**
 * Writes a {@link Edimap} to {@link Result}.
 * 
 * @author bardl
 */
public class EDIModelWriter {

    /**
     * Write segments in {@link Edimap} to {@link Result}.
     * @param edimap the {@link Edimap} to write.
     * @param result the {@link Result}.
     * @param encoding used in {@link Result}. 
     * @throws java.io.IOException is thrown when writer experience problems.
     * @throws EDIParseException is thrown when required segment content is missing.
     */
    public static void writeEDIModel(Edimap edimap, Result result, String encoding) throws IOException, EDIParseException {
        Writer writer = getWriter(result, encoding);
        writeEDIModel(edimap, writer);

    }

    /**
     * Write segments in {@link Edimap} to {@link Writer}.
     * @param edimap the {@link Edimap} to write.
     * @param writer the {@link Writer}.
     * @throws java.io.IOException is thrown when writer experience problems.
     * @throws EDIParseException is thrown when required segment content is missing.
     */
    public static void writeEDIModel(Edimap edimap, Writer writer) throws IOException, EDIParseException {

        try {
            writeSegments(writer, edimap.getSegments().getSegment(), edimap.getDelimiters());
        } finally {
            writer.close();
        }

    }

    /**
     * Write {@link Segment}s to {@link Writer} as long as all required segment content are present.
     * @param writer the {@link Writer} to write the {@link Segment} to.
     * @param segments the list {@link Segment}s to write.
     * @param delimiters the {@link Delimiters} containing delimiters set in {@link Edimap}.
     * @throws java.io.IOException is thrown when writer experience problems.
     * @throws EDIParseException is thrown when required segment content is missing.
     */
    private static void writeSegments(Writer writer, List<Segment> segments, Delimiters delimiters) throws IOException, EDIParseException {
        if (segments == null) {
            return;
        }

        for (Segment segment : segments) {
            writeSegment(writer, segment, delimiters);
        }
    }

    /**
     * Write {@link Segment} to {@link Writer} as long as all required segment content are present.
     * @param writer the {@link Writer} to write the {@link Segment} to.
     * @param segment the {@link Segment} to write.
     * @param delimiters the {@link Delimiters} containing delimiters set in {@link Edimap}.
     * @throws java.io.IOException is thrown when writer experience problems.
     * @throws EDIParseException is thrown when required segment content is missing.
     */
    private static void writeSegment(Writer writer, Segment segment, Delimiters delimiters) throws EDIParseException, IOException {
        StringBuilder segmentContent = new StringBuilder();
        int result = writeFields(segmentContent, segment.getField(), delimiters);

        if (result > 0) {
            writer.write(segment.getSegcode());
            writer.write(segmentContent.toString());
        } else {
            if (segment.getMinOccurs() > 0) {
                throw new EDIParseException("Mandatory segment [" + segment.getSegcode() + "] could not be written. The reason might be that a mandatory field, component or subcomponent is missing. Written result would be [" + segment.getSegcode() + segmentContent.toString() + "]");
            }
        }
        writer.write(delimiters.getSegment());
        writer.flush();
        writeSegments(writer, segment.getSegment(), delimiters);

    }

    /**
     * Write {@link Field}s to {@link StringBuilder} as long as all required {@link Component}s and
     * {@link SubComponent}s are present.
     * @param segmentContent the {@link StringBuilder} to write the {@link Field}s to.
     * @param fields the List of {@link Field}s to write.
     * @param delimiters the {@link Delimiters} containing delimiters set in {@link Edimap}.
     * @return If required {@link Field}, {@link Component}s or {@link SubComponent} is missing this method
     * will return -1, otherwise return the number of written {@link Field}s.
     */
    private static int writeFields(StringBuilder segmentContent, List<Field> fields, Delimiters delimiters) {
        int result = 0;
        int tmpResult;
        StringBuilder fieldContent;
        for (Field field : fields) {
            fieldContent = new StringBuilder();
            tmpResult = writeField(fieldContent, field, delimiters);

            if (tmpResult > 0) {
                segmentContent.append(fieldContent);
                result++;
            } else if ( tmpResult == -1 ) {
                return tmpResult;
            }
        }
        return result;
    }

    /**
     * Write {@link Field} to {@link StringBuilder} as long as all required {@link Component}s and
     * {@link SubComponent}s are present.
     * @param segmentContent the {@link StringBuilder} to write the {@link Field} to.
     * @param field the {@link Field} to write.
     * @param delimiters the {@link Delimiters} containing delimiters set in {@link Edimap}.
     * @return If required {@link Field} is missing this method will return -1, otherwise return nr of written {@link Field}.
     */
    private static int writeField(StringBuilder segmentContent, Field field, Delimiters delimiters) {
        int result = 0;
        if (field.getValue() == null) {
            if (field.isRequired()) {
                return -1;
            }
            segmentContent.append(delimiters.getField());
        } else {
            segmentContent.append(delimiters.getField());
            segmentContent.append(field.getValue());
            result++;
        }
        Component component;
        List<Component> components = field.getComponent();
        for (int i = 0; i < components.size(); i++) {
            component = components.get(i);
            result = writeComponent(segmentContent, component, delimiters, result, i==0);
            if ( result == -1) {
                return result;
            }
        }
        return result;
    }

    /**
     * Write {@link Component} to {@link StringBuilder} as long as all required {@link SubComponent}s are present.
     * @param segmentContent the {@link StringBuilder} to write the {@link Component} to.
     * @param component the {@link Component} to write.
     * @param delimiters the {@link Delimiters} containing delimiters set in {@link Edimap}.
     * @param foundValues the number of values written to segmentContent so far.
     * @param firstComponent true if this is the first {@link Component} in {@link Field}.
     * @return If required {@link Component} is missing this method will return -1, otherwise return nr of written {@link Component}.
     */
    private static int writeComponent(StringBuilder segmentContent, Component component, Delimiters delimiters, int foundValues, boolean firstComponent) {

        if (component.getValue() == null) {
            if (component.isRequired()) {
                return -1;
            }
            segmentContent.append(delimiters.getComponent());
        } else {
            if ( ! firstComponent ) {
                segmentContent.append(delimiters.getComponent());
            }
            segmentContent.append(component.getValue());
            foundValues++;
        }

        SubComponent subComponent;
        List<SubComponent> subComponents = component.getSubComponent();
        for (int i = 0; i < subComponents.size(); i++) {
            subComponent = subComponents.get(i);
            foundValues = writeSubComponent(segmentContent, subComponent, delimiters, foundValues, i==0);

            if (foundValues == -1) {
                return foundValues;
            }
        }
        return foundValues;
    }

    /**
     * Write {@link SubComponent} to {@link StringBuilder}.
     * @param segmentContent the {@link StringBuilder} to write the {@link SubComponent} to.
     * @param subComponent the {@link SubComponent} to write.
     * @param delimiters the {@link Delimiters} containing delimiters set in {@link Edimap}.
     * @param foundValues the number of values written to segmentContent so far.
     * @param firstSubComponent true if this is the first {@link SubComponent} in {@link Component}. 
     * @return If required {@link SubComponent} is missing this method will return -1, otherwise return nr of written {@link SubComponent}.
     */
    private static int writeSubComponent(StringBuilder segmentContent, SubComponent subComponent, Delimiters delimiters, int foundValues, boolean firstSubComponent) {
        if (subComponent.getValue() == null) {
            if (subComponent.isRequired()) {
                return -1;
            }
            if ( ! firstSubComponent ) {
                segmentContent.append(delimiters.getSubComponent());
            }
        }
        segmentContent.append(subComponent.getValue());
        foundValues++;

        return foundValues;
    }

    /**
     * Returns the {@link Writer} contained in {@link Result}.
     * @param result the {@link Result} containing the {@link Writer}.
     * @param encoding the encoding.
     * @return the {@link Writer} contained in the {@link Result}.
     * @throws IOException is thrown when
     */
    private static Writer getWriter(Result result, String encoding) throws IOException {
        if(!(result instanceof StreamResult)) {
            return new NullWriter();
        }
        StreamResult streamResult = (StreamResult) result;
        if(streamResult.getWriter() != null) {
            return streamResult.getWriter();
        } else if(streamResult.getOutputStream() != null) {
            try {
                return new OutputStreamWriter(streamResult.getOutputStream(), encoding);
            } catch(UnsupportedEncodingException e) {
                throw new IOException("Unable to encode output stream with encoding [" + encoding + "].");
            }
        } else {
            throw new IOException("Invalid " + StreamResult.class.getName() + ".  No OutputStream or Writer instance.");
        }
    }
}
