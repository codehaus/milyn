package org.mylin.ecore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.milyn.ect.EdiSpecificationReader;
import org.milyn.ect.formats.unedifact.UnEdifactSpecificationReader;

public class ECoreGenerationTest extends TestCase {

	public void testECoreGeneration() throws Exception {
		InputStream inputStream = getClass().getResourceAsStream("/D99A.zip");
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);

		EdiSpecificationReader ediSpecificationReader = new UnEdifactSpecificationReader(
				zipInputStream, false);
		ECoreGenerator generator = new ECoreGenerator();
		Set<EPackage> packages = generator
				.generatePackages(ediSpecificationReader);
		save(packages);
	}

	private void save(Collection<EPackage> packages) throws IOException {
		ResourceSetImpl resourceSet = new ResourceSetImpl();
		resourceSet
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new EcoreResourceFactoryImpl());
		// First write the common package
		for (EPackage pkg : packages) {
			String pkgName = pkg.getName();
			if (pkgName.startsWith("common")) {
				Resource resource = resourceSet.createResource(URI
						.createFileURI(pkgName + ".ecore"));
				resource.getContents().add(pkg);
				resource.save(null);
			}
		}
		for (EPackage pkg : packages) {
			String pkgName = pkg.getName();
			if (pkgName.startsWith("CUSCAR")) {
				Resource resource = resourceSet.createResource(URI
						.createFileURI(pkgName + ".ecore"));
				resource.getContents().add(pkg);
				resource.save(null);
			}
		}
	}

}
