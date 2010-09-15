package org.mylin.ecore;

import static org.mylin.ecore.ECoreConversionUtils.toJavaName;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.milyn.ect.formats.unedifact.UnEdifactSpecificationReader;

public class ECoreGenerationTest extends TestCase {

	private static final ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;

	public void testECoreGeneration() throws Exception {
		InputStream inputStream = getClass().getResourceAsStream("/D99A.zip");
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);

		UnEdifactSpecificationReader ediSpecificationReader = new UnEdifactSpecificationReader(
				zipInputStream, false);
		ECoreGenerator generator = new ECoreGenerator();
		Set<EPackage> packages = generator
				.generatePackages(ediSpecificationReader);
		save(packages);
		for (EPackage pkg : packages) {
			validatePackage(pkg);
			if ("cuscar".equals(pkg.getName())) {
				checkCUSCAR(pkg);
			}
		}
	}

	private void checkCUSCAR(EPackage pkg) {
		EClass clazz = (EClass) pkg.getEClassifier("CUSCAR");
		assertNotNull(clazz);
		assertEquals(13, clazz.getEStructuralFeatures().size());
		assertEquals(13, clazz.getEAllContainments().size());
		assertEquals("CUSCAR", metadata.getName(clazz));
	}

	private void validatePackage(EPackage pkg) {
		assertNotNull(pkg.getName() + " has document root",
				metadata.getDocumentRoot(pkg));
		EList<EClassifier> classifiers = pkg.getEClassifiers();
		Set<String> names = new HashSet<String>();
		for (EClassifier classifier : classifiers) {
			if (classifier instanceof EClass) {
				EClass clazz = (EClass) classifier;
				String location = pkg.getName() + "#" + clazz.getName();
				if (!"DocumentRoot".equals(clazz.getName())) {
					String metadataName = metadata.getName(clazz);
					boolean same = clazz.getName().equals(metadataName)
							|| clazz.getName().equals(
									toJavaName(metadataName, true));
					assertTrue(
							location + " metadata missmatch " + clazz.getName()
									+ "<>" + metadataName, same);
					assertTrue(location + " duplicate",
							names.add(clazz.getName()));
				}
			}
		}
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
			if (pkgName.startsWith("cuscar")) {
				Resource resource = resourceSet.createResource(URI
						.createFileURI(pkgName + ".ecore"));
				resource.getContents().add(pkg);
				resource.save(null);
			}
		}
	}

}
