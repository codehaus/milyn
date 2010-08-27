package org.mylin.ecore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.milyn.ect.EdiSpecificationReader;
import org.milyn.ect.formats.unedifact.UnEdifactSpecificationReader;
import org.milyn.edisax.model.internal.Component;
import org.milyn.edisax.model.internal.Description;
import org.milyn.edisax.model.internal.Edimap;
import org.milyn.edisax.model.internal.Field;
import org.milyn.edisax.model.internal.Segment;
import org.milyn.edisax.model.internal.SegmentGroup;

public class ECoreGenerationTest extends TestCase {

	public void testECoreGeneration() throws Exception {
		InputStream inputStream = getClass().getResourceAsStream("D08A.zip");
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);

		EdiSpecificationReader ediSpecificationReader = new UnEdifactSpecificationReader(
				zipInputStream, false);
		ECoreGenerator generator = new ECoreGenerator();
		Set<EPackage> packages = generator
				.generatePackages(ediSpecificationReader);
		// Map<String, EClass> commonClasses = new HashMap<String, EClass>();
		// EPackage commonPkg = createCommonPackage(
		// ediSpecificationReader
		// .getMappingModel("__modelset_definitions"),
		// commonClasses);
		// EPackage pkg = processModel(
		// ediSpecificationReader.getMappingModel("CUSCAR"), commonClasses);
		save(packages);
	}

	/**
	 * Creating common package with common definitions
	 * 
	 * @param mappingModel
	 * @param commonClasses
	 */
	private EPackage createCommonPackage(Edimap mappingModel,
			final Map<String, EClass> commonClasses) {
		final EPackage result = EcoreFactory.eINSTANCE.createEPackage();
		result.setName("commonClasses");
		List<SegmentGroup> segments = mappingModel.getSegments().getSegments();
		CollectionUtils.forAllDo(segments, new Closure() {

			public void execute(Object arg0) {
				Segment segment = (Segment) arg0;
				EClass clazz = EcoreFactory.eINSTANCE.createEClass();
				clazz.setName(segment.getXmltag());
				result.getEClassifiers().add(clazz);
				commonClasses.put(segment.getSegcode(), clazz);
				processFields(segment.getFields(), clazz, result);
			}
		});
		return result;
	}

	private EPackage processModel(Edimap mappingModel,
			final Map<String, EClass> commonClasses) {
		final EPackage result = EcoreFactory.eINSTANCE.createEPackage();
		Description desc = mappingModel.getDescription();
		result.setName(desc.getName());
		result.setNsPrefix(desc.getName().toLowerCase());
		result.setNsURI("http://smooks.org/UNEDI/"
				+ desc.getVersion().replace(':', '_'));
		SegmentGroup root = mappingModel.getSegments();
		final EClass messageRoot = EcoreFactory.eINSTANCE.createEClass();
		messageRoot.setName(desc.getName());
		result.getEClassifiers().add(messageRoot);

		processSegments(root.getSegments(), commonClasses, result, messageRoot);
		return result;
	}

	private void processSegments(List<SegmentGroup> segments,
			final Map<String, EClass> commonClasses, final EPackage result,
			final EClass parent) {
		CollectionUtils.forAllDo(segments, new Closure() {

			public void execute(Object arg0) {
				if (arg0 instanceof Segment) {
					Segment segment = (Segment) arg0;
					processSegment(commonClasses, parent, segment);
				} else if (arg0 instanceof SegmentGroup) {
					SegmentGroup grp = (SegmentGroup) arg0;
					processSegmentGroup(commonClasses, parent, grp, result);
				}
			}

		});
	}

	/**
	 * Create an reference to the root class for each simple segment
	 * 
	 * @param commonClasses
	 * @param parent
	 * @param arg0
	 */
	private void processSegment(final Map<String, EClass> commonClasses,
			final EClass parent, Segment segment) {
		EClass refClass = commonClasses.get(getLocalPart(segment));
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName(segment.getXmltag());
		reference.setEType(refClass);
		reference.setLowerBound(segment.getMinOccurs());
		reference.setUpperBound(segment.getMaxOccurs());
		parent.getEStructuralFeatures().add(reference);
	}

	private void processFields(List<Field> fields, final EClass parent,
			final EPackage pkg) {
		CollectionUtils.forAllDo(fields, new Closure() {

			public void execute(Object arg0) {
				if (arg0 instanceof Field) {
					Field field = (Field) arg0;
					if (field.getComponents().isEmpty()) {
						EAttribute attr = EcoreFactory.eINSTANCE
								.createEAttribute();
						attr.setName(field.getXmltag());
						// TODO Set type here
						if (field.isRequired()) {
							attr.setLowerBound(1);
						} else {
							attr.setLowerBound(0);
						}
						attr.setUpperBound(1);
						parent.getEStructuralFeatures().add(attr);
					} else {
						// We have a complex field --> need to define a new
						// class
						String classifierName = field.getNodeTypeRef()
								+ field.getXmltag();
						EClass newClass = EcoreFactory.eINSTANCE.createEClass();
						newClass.setName(classifierName);
						pkg.getEClassifiers().add(newClass);
						for (Component component : field.getComponents()) {
							if (component.getSubComponents().isEmpty()) {
								EAttribute attr = EcoreFactory.eINSTANCE
										.createEAttribute();
								attr.setName(component.getXmltag());
								if (component.isRequired()) {

								}
							}
						}
					}
				}
			}
		});
	}

	/**
	 * Creating a new class and instance reference on parent class
	 * 
	 * @param commonClasses
	 * @param parent
	 * @param grp
	 */
	private void processSegmentGroup(Map<String, EClass> commonClasses,
			EClass parent, SegmentGroup grp, EPackage pkg) {
		EClass refClass = EcoreFactory.eINSTANCE.createEClass();
		refClass.setName(grp.getXmltag());
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName(grp.getXmltag());
		reference.setEType(refClass);
		reference.setLowerBound(grp.getMinOccurs());
		reference.setUpperBound(grp.getMaxOccurs());
		parent.getEStructuralFeatures().add(reference);
		pkg.getEClassifiers().add(refClass);
		processSegments(grp.getSegments(), commonClasses, pkg, refClass);
	}

	private String getLocalPart(Segment segment) {
		// TODO Fix this hack
		return segment.getNodeTypeRef().substring(3);
	}

	private void save(Collection<EPackage> packages) throws IOException {
		ResourceSetImpl resourceSet = new ResourceSetImpl();
		resourceSet
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new EcoreResourceFactoryImpl());
		Resource resource = resourceSet.createResource(URI
				.createFileURI("./test.ecore"));
		resource.getContents().addAll(packages);
		resource.save(null);
	}

}
