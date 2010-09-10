package org.mylin.ecore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
	
    public static final String PLUGIN_XML_ENTRY = "plugin.xml";

	private static final String MANIFEST = "META-INF/MANIFEST.MF";
	
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmm");


	protected DirectoryConverter() {
		// noop
	}

	/**
	 * Convert directory given as {@link InputStream} to the resulting archive
	 * 
	 * @param directoryInputStream
	 */
	public Archive createArchive(InputStream directoryStream, String pluginID)
			throws IOException {
		String qualifier = format.format(Calendar.getInstance().getTime());
		ZipInputStream zipInputStream = new ZipInputStream(directoryStream);
		UnEdifactSpecificationReader ediSpecificationReader = new UnEdifactSpecificationReader(
				zipInputStream, false);
		ECoreGenerator ecoreGen = new ECoreGenerator();
		Set<EPackage> packages = ecoreGen
				.generatePackages(ediSpecificationReader);
		ResourceSet rs = prepareResourceSet();

		// TODO Add qualifier
		Archive archive = new Archive(pluginID + "_1.0.0.v" + qualifier + ".jar");
		StringBuilder pluginXMLBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<?eclipse version=\"3.0\"?>\n" +
				"<plugin>\n" +
				"\t<extension point=\"org.eclipse.emf.ecore.dynamic_package\">\n");
		String pathPrefix = pluginID.replace(".", "/");

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		// Writing out ECORE files
		for (EPackage pkg : packages) {
			if (pkg.getName().startsWith("common")) {
				serializePackage(rs, archive, pluginXMLBuilder, pathPrefix,
						out, pkg);
			}
		}

		for (EPackage pkg : packages) {
			if (!pkg.getName().startsWith("common")) {
				serializePackage(rs, archive, pluginXMLBuilder, pathPrefix,
						out, pkg);
			}
		}

		pluginXMLBuilder.append("\t</extension>\n</plugin>");
		// Add the generated mapping model to the archive...
		archive.addEntry(PLUGIN_XML_ENTRY,
				pluginXMLBuilder.toString());

		// Add the model set URN to the archive...
		archive.addEntry(EDIUtils.EDI_MAPPING_MODEL_URN, pluginID);
		
		archive.addEntry(MANIFEST, generateManifest(pluginID, qualifier));
		
		return archive;
	}

	private String generateManifest(String pluginID, String qualfier) {
		StringBuilder result = new StringBuilder();
		result.append("Manifest-Version: 1.0\n");
		result.append("Bundle-ManifestVersion: 2\n");
		result.append("Bundle-Name: " + pluginID + "\n");
		result.append("Bundle-SymbolicName: " + pluginID + ";singleton:=true\n");
		result.append("Bundle-Version: 1.0.0.v" + qualfier + "\n");
		result.append("Bundle-ClassPath: .\n");
		result.append("Bundle-ActivationPolicy: lazy\n");
		return result.toString();
	}

	private void serializePackage(ResourceSet rs, Archive archive,
			StringBuilder pluginBuilder, String pathPrefix,
			ByteArrayOutputStream out, EPackage pkg) throws IOException {
		String message = pkg.getName();
		String ecoreEntryPath = pathPrefix + "/" + message + ".ecore";

		out.reset();
		Resource resource = rs.createResource(URI.createFileURI(message + ".ecore"));
		resource.getContents().add(pkg);
		resource.save(out, null);

		// Add the generated mapping model to the archive...
		archive.addEntry(ecoreEntryPath, out.toByteArray());

		// Add this messages archive entry to the mapping model list file...
		pluginBuilder.append("\t\t<resource \n\t\t\tlocation=\"");
		pluginBuilder.append(ecoreEntryPath);
		pluginBuilder.append("\" \n\t\t\turi=\"");
		pluginBuilder.append(pkg.getNsURI());
		pluginBuilder.append("\">\n\t\t</resource>\n");
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
