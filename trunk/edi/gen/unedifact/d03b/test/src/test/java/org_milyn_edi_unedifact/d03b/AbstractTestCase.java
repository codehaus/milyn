package org_milyn_edi_unedifact.d03b;

import junit.framework.TestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.milyn.Smooks;
import org.milyn.edi.unedifact.d03b.D03BInterchangeFactory;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser;
import org.milyn.ejc.util.MessageBuilder;
import org.milyn.io.StreamUtils;
import org.milyn.payload.StringResult;
import org.milyn.smooks.edi.unedifact.model.UNEdifactInterchange;
import org.milyn.smooks.edi.unedifact.model.r41.UNB41;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactInterchange41;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactMessage41;
import org.milyn.smooks.edi.unedifact.model.r41.types.MessageIdentifier;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public abstract class AbstractTestCase extends TestCase {

    private static D03BInterchangeFactory factory;
    private static MessageBuilder messageBuilder = new MessageBuilder("org.milyn", UNEdifactInterchangeParser.defaultUNEdifactDelimiters.getField());

    static {
        try {
            factory = D03BInterchangeFactory.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testXMLSerialize(String messageInFile, String expectedResFile) throws IOException, SAXException {
        testXMLSerialize(messageInFile, expectedResFile, false);
    }

    public void testXMLSerialize(String messageInFile, String expectedResFile, boolean dumpResult) throws IOException, SAXException {
        Smooks smooks = new Smooks(AbstractTestCase.class.getResourceAsStream("smooks-config.xml"));
        StringResult result = new StringResult();

        smooks.filterSource(new StreamSource(getClass().getResourceAsStream(messageInFile)), result);

        if(dumpResult) {
            System.out.println(result);
        }

        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream(expectedResFile)), new StringReader(result.toString()));
    }

    public void testJavaBinding(String messageInFile, boolean dumpResult) throws IOException, SAXException {
        // Deserialize the a UN/EDIFACT interchange stream to Java...
        UNEdifactInterchange interchange = factory.fromUNEdifact(getClass().getResourceAsStream(messageInFile));

        // Serialize it back to EDI....
        StringWriter writer = new StringWriter();
        factory.toUNEdifact(interchange, writer);

        if(dumpResult) {
            System.out.println(writer.toString());
        }

        // We expect the result to be the same as the input...
        String expected = StreamUtils.readStreamAsString(getClass().getResourceAsStream(messageInFile));
        assertEquals(StreamUtils.normalizeLines(expected, false), StreamUtils.normalizeLines(writer.toString(), false));
    }

    public void test_Interchange(boolean dump, Class<?>... messageTypes) throws IOException {
        UNEdifactInterchange41 interchange41 = buildInterchange(messageTypes);
        test_Interchange(dump, interchange41);
    }

    public void test_Interchange(boolean dump, UNEdifactInterchange41 interchange41) throws IOException {
        StringWriter writer = new StringWriter();

        // serialize it...
        factory.toUNEdifact(interchange41, writer);

        String messageV1 = writer.toString();

        // reconstruct from the serialized form...
        interchange41 = (UNEdifactInterchange41) factory.fromUNEdifact(new InputSource(new ByteArrayInputStream(messageV1.getBytes(Charset.forName("UTF-8")))));

        // serialize again...
        writer.getBuffer().setLength(0);
        factory.toUNEdifact(interchange41, writer);

        String messageV2 = writer.toString();

        assertEquals(messageV1, messageV2);

        if(dump) {
            System.out.println(messageV1);
        }
    }

    public UNEdifactInterchange41 buildInterchange(Class<?>... messageTypes) {
        return buildInterchange("D", "03B", messageTypes);
    }

    public UNEdifactInterchange41 buildInterchange(String versionNum, String releaseNum, Class<?>[] messageTypes) {
        UNEdifactInterchange41 interchange41 = messageBuilder.buildMessage(UNEdifactInterchange41.class);
        UNB41 unb = interchange41.getInterchangeHeader();
        List<UNEdifactMessage41> messages = interchange41.getMessages();

        unb.getSyntaxIdentifier().setId("UNOW"); // UNOW is UTF-8.... as encoded above
        unb.getSyntaxIdentifier().setCodedCharacterEncoding("UNOW"); // UNOW is UTF-8.... as encoded above
        messages.clear();

        for(Class<?> messageType : messageTypes) {
            UNEdifactMessage41 message41 = messageBuilder.buildMessage(UNEdifactMessage41.class);
            Object messageInstance = messageBuilder.buildMessage(messageType);

            MessageIdentifier messageIdentifier = message41.getMessageHeader().getMessageIdentifier();
            messageIdentifier.setControllingAgencyCode("UN");
            messageIdentifier.setId(messageType.getSimpleName().toUpperCase());
            messageIdentifier.setVersionNum(versionNum);
            messageIdentifier.setReleaseNum(releaseNum);
            message41.setMessage(messageInstance);
            messages.add(message41);
        }

        return interchange41;
    }
}