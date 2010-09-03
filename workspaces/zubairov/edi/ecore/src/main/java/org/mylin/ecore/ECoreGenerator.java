package org.mylin.ecore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.milyn.ect.EdiSpecificationReader;
import org.milyn.edisax.model.internal.Edimap;
import org.milyn.edisax.model.internal.Field;
import org.milyn.edisax.model.internal.IEdimap;
import org.milyn.edisax.model.internal.IField;
import org.milyn.edisax.model.internal.ISegmentGroup;
import org.milyn.edisax.model.internal.Segment;
import org.milyn.edisax.model.internal.SegmentGroup;

/**
 * This class is responsible for generating ECore model based on the UN EDI
 * Model
 * 
 * @author zubairov
 * 
 */
public class ECoreGenerator {

	private static final String COMMON_MAPPING_MODEL_NAME = "__modelset_definitions";

	private static final String COMMON_PACKAGE_NAME = "commonDefinitions";

	/**
	 * This method will convert information available in
	 * {@link EdiSpecificationReader} into the set of {@link EPackage} packages.
	 * 
	 * Set will contain one package with common definitions and one package per
	 * each {@link Edimap} that is using common classes
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public Set<EPackage> generatePackages(EdiSpecificationReader reader)
			throws IOException {
		Set<EPackage> result = new HashSet<EPackage>();

		// Creating common package
		Map<String, EClass> commonClasses = new HashMap<String, EClass>();
		IEdimap commonModel = reader.getMappingModel(COMMON_MAPPING_MODEL_NAME);
		EPackage commonPackage = EcoreFactory.eINSTANCE.createEPackage();
		commonPackage.setName(COMMON_PACKAGE_NAME);
		commonPackage.setNsPrefix("common");
		commonPackage.setNsURI("http://www.smooks.org/CommonPackage");
		Collection<EClass> clzz = createCommonClasses(commonModel,
				commonClasses);
		commonPackage.getEClassifiers().addAll(clzz);
		result.add(commonPackage);

		// Processing individual packages
		Set<String> messageNames = reader.getMessageNames();
		for (String messageName : messageNames) {
			if (!COMMON_MAPPING_MODEL_NAME.equals(messageName)) {
				IEdimap mappingModel = reader.getMappingModel(messageName);
				EPackage pkg = ECoreConversionUtils
						.mappingModelToEPackage(mappingModel);
				pkg.getEClassifiers().addAll(
						createMappingClases(mappingModel.getSegments(),
								commonClasses));
				if (!result.add(pkg)) {
					throw new RuntimeException("WARN: Duplicated package "
							+ pkg.getName());
				}
			}
		}
		return result;
	}

	/**
	 * Creating mapping classes
	 * 
	 * @param root
	 * @param commonClasses
	 * @return
	 */
	private Set<EClass> createMappingClases(ISegmentGroup root,
			Map<String, EClass> commonClasses) {
		Set<EClass> result = new HashSet<EClass>();
		EClass rootClass = ECoreConversionUtils.segmentGroupToEClass(root);
		// We need to change the name of the Root class so it is not 
		// the same as name of the package
		rootClass.setName(rootClass.getName().toUpperCase());
		result.add(rootClass);
		processSegments(root.getSegments(), commonClasses, result, rootClass);
		return result;
	}

	/**
	 * Process segments
	 * 
	 * @param segments
	 * @param commonClasses
	 * @param result
	 * @param rootClass
	 */
	private void processSegments(List<ISegmentGroup> segments,
			final Map<String, EClass> commonClasses, final Set<EClass> result,
			final EClass parent) {
		for (ISegmentGroup arg0 : segments) {
			if (arg0 instanceof Segment) {
				Segment segment = (Segment) arg0;
				EClass refClass = commonClasses.get(getLocalPart(segment));
				EReference segmentRef = ECoreConversionUtils
						.segmentToEReference(segment, refClass);
				if (parent.getEStructuralFeature(segmentRef.getName()) == null) {
					parent.getEStructuralFeatures().add(segmentRef);
				} else {
					System.err.println("WARN: Class " + refClass.getName()
							+ " already contains a refernce "
							+ segmentRef.getName());
				}
			} else if (arg0 instanceof SegmentGroup) {
				SegmentGroup grp = (SegmentGroup) arg0;
				EClass refClass = ECoreConversionUtils
						.segmentGroupToEClass(grp);
				EReference reference = ECoreConversionUtils
						.segmentGroupToEReference(grp, refClass);
				if (parent.getEStructuralFeature(reference.getName()) == null) {
					parent.getEStructuralFeatures().add(reference);
				} else {
					System.err.println("WARN: Class " + refClass.getName()
							+ " already contains a refernce "
							+ reference.getName());
				}
				if (!result.add(refClass)) {
					throw new RuntimeException("Reference class "
							+ refClass.getName() + " is duplicated in package");
				}
				processSegments(grp.getSegments(), commonClasses, result,
						refClass);
			}
		}
	}

	/**
	 * This method converting classes for common mapping model
	 * 
	 * @param commonModel
	 * @param commonClasses
	 * @param commonPackage
	 */
	private Collection<EClass> createCommonClasses(IEdimap commonModel,
			final Map<String, EClass> commonClasses) {
		Map<String, EClass> result = new HashMap<String, EClass>();
		for (ISegmentGroup grp : commonModel.getSegments().getSegments()) {
			// No segment groups are allowed in common part
			Segment segment = (Segment) grp;
			EClass clazz = ECoreConversionUtils.segmentToEClass(segment);
			if (!segment.getFields().isEmpty()) {
				commonClasses.put(segment.getSegcode(), clazz);
				Collection<EStructuralFeature> fields = processFields(
						segment.getFields(), result);
				clazz.getEStructuralFeatures().addAll(fields);
			}
			result.put(clazz.getName(), clazz);
		}
		return result.values();
	}

	/**
	 * Here we transform {@link Field} to {@link EStructuralFeature} which is
	 * either {@link EAttribute} or {@link EReference}
	 * 
	 * In case of {@link EReference} we would need to add a new {@link EClass}
	 * to the result EClass set
	 * 
	 * @param fields
	 * @param result
	 */
	private Collection<EStructuralFeature> processFields(List<IField> fields,
			Map<String, EClass> classes) {
		// We need to preserve order therefore we are going
		// to use separate list and set for controlling duplicates
		List<EStructuralFeature> result = new ArrayList<EStructuralFeature>();
		Set<String> names = new HashSet<String>();
		for (IField field : fields) {
			if (field.getComponents().isEmpty()) {
				// We have a simple field without components
				EAttribute attribute = ECoreConversionUtils.fieldToEAttribute(field);
				if (!names.contains(attribute.getName())) {
					result.add(attribute);
					names.add(attribute.getName());
				} else {
					System.err.println("WARN: Duplicate attribute " + attribute.getName());
				}
			} else {
				// We have a complex field --> need to define a new
				// class
				EReference reference = ECoreConversionUtils.fieldToEReference(field,
						classes);
				if (!names.contains(reference.getName())) {
					result.add(reference);
					names.add(reference.getName());
				} else {
					System.err.println("WARN: Duplicate reference " + reference.getName());
				}
			}
		}
		return result;
	}

	/**
	 * Just cut out a local part from the fully qualified name
	 * 
	 * @param segment
	 * @return
	 */
	private String getLocalPart(Segment segment) {
		// TODO Fix this hack
		return segment.getNodeTypeRef().substring(3);
	}

}
