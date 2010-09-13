package org.mylin.ecore;

import java.io.PrintWriter;

import junit.framework.TestCase;

import org.mylin.ecore.model.envelope.UNEdifact;

/**
 * EDIWriter test
 * 
 * @author zubairov
 * 
 */
public class EDIWriterTest extends TestCase {

	public void testWriting() throws Exception {
		UNEdifact envelope = EDILoader.INSTANCE.load(getClass()
				.getResourceAsStream("/99a_cuscar.edi"));
		assertNotNull(envelope);
		PrintWriter out = new PrintWriter(System.err);
		try {
			EDIWriter.INSTANCE.write(out, envelope);
		} finally {
			out.flush();
		}
	}

}
