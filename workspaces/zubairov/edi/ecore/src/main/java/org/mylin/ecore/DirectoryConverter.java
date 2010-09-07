package org.mylin.ecore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipInputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.milyn.archive.Archive;
import org.milyn.ect.formats.unedifact.UnEdifactSpecificationReader;
import org.milyn.edisax.util.EDIUtils;

/**
 * Class that converts an UN/EDIFACT directory ZIP file to the JAR file with
 * ECORE models inside it
 * 
 * @author zubairov
 * 
 */
public class DirectoryConverter {

	/**
	 * Singleton instance for convinience
	 */
	public static final DirectoryConverter INSTANCE = new DirectoryConverter();
	
    public static final String ECORE_MAPPING_MODEL_ZIP_LIST_FILE = "META-INF/services/org/smooks/edi/mapping-model-ecore.lst";


	protected DirectoryConverter() {
		// noop
	}

	/**
	 * Convert directory given as {@link InputStream} to the resulting archive
	 * 
	 * @param directoryInputStream
	 */
	public Archive createArchive(InputStream directoryStream, String urn)
			throws IOException {
		ZipInputStream zipInputStream = new ZipInputStream(directoryStream);
		UnEdifactSpecificationReader ediSpecificationReader = new UnEdifactSpecificationReader(
				zipInputStream, false);
		ECoreGenerator ecoreGen = new ECoreGenerator();
		Set<EPackage> packages = ecoreGen
				.generatePackages(ediSpecificationReader);
		ResourceSet rs = prepareResourceSet();

		
		Archive archive = new Archive();
		StringBuilder modelListBuilder = new StringBuilder();
		StringBuilder ecoreListBuilder = new StringBuilder();
		String pathPrefix = urn.replace(".", "_").replace(":", "/");

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		// Writing out ECORE files
		for (EPackage pkg : packages) {
			if (pkg.getName().startsWith("common")) {
				serializePackage(rs, archive, ecoreListBuilder, pathPrefix,
						out, pkg);
			}
		}

		for (EPackage pkg : packages) {
			if (!pkg.getName().startsWith("common")) {
				serializePackage(rs, archive, ecoreListBuilder, pathPrefix,
						out, pkg);
			}
		}

		// Add the generated mapping model to the archive...
		archive.addEntry(ECORE_MAPPING_MODEL_ZIP_LIST_FILE,
				ecoreListBuilder.toString());

		// Add the model set URN to the archive...
		archive.addEntry(EDIUtils.EDI_MAPPING_MODEL_URN, urn);

		// Add an entry for the interchange properties...
		Properties interchangeProperties = ediSpecificationReader
				.getInterchangeProperties();
		ByteArrayOutputStream propertiesOutStream = new ByteArrayOutputStream();
		try {
			interchangeProperties.store(propertiesOutStream,
					"UN/EDIFACT Interchange Properties");
			propertiesOutStream.flush();
			archive.addEntry(
					EDIUtils.EDI_MAPPING_MODEL_INTERCHANGE_PROPERTIES_FILE,
					propertiesOutStream.toByteArray());
		} finally {
			propertiesOutStream.close();
		}

		return archive;
	}

	private void serializePackage(ResourceSet rs, Archive archive,
			StringBuilder modelListBuilder, String pathPrefix,
			ByteArrayOutputStream out, EPackage pkg) throws IOException {
		String message = pkg.getName();
		String ecoreEntryPath = pathPrefix + "/" + message + ".ecore";

		out.reset();
		Resource resource = rs.createResource(URI.createURI(pkg.getNsURI()));
		resource.getContents().add(pkg);
		resource.save(out, null);

		// Add the generated mapping model to the archive...
		archive.addEntry(ecoreEntryPath, out.toByteArray());

		// Add this messages archive entry to the mapping model list file...
		modelListBuilder.append("/" + ecoreEntryPath);
		modelListBuilder.append("!" + pkg.getNsURI());
		modelListBuilder.append("\n");
	}

	private ResourceSet prepareResourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		/*
		 * Register XML Factory implementation using DEFAULT_EXTENSION
		 */
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new EcoreResourceFactoryImpl());
		
		return resourceSet;
	}
}
