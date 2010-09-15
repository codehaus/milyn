package org.mylin.ecore;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

import junit.framework.TestCase;

import org.milyn.archive.Archive;

public class DirectoryConverterTest extends TestCase {

	public void testConversion() throws Exception {
		Archive archive = DirectoryConverter.INSTANCE.createArchive(getClass()
				.getResourceAsStream("/D99A.zip"),
				"org.milyn.edi.unedifact.mappings.d99a");
		archive.toFileSystem(new File("./target/test"));
		archive.toOutputStream(new ZipOutputStream(new FileOutputStream(new File("target/" + archive.getArchiveName()))));
	}
}
