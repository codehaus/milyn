package org.mylin.ecore;

import java.util.Map;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.milyn.edisax.model.internal.Component;
import org.milyn.edisax.model.internal.Description;
import org.milyn.edisax.model.internal.Edimap;
import org.milyn.edisax.model.internal.Field;
import org.milyn.edisax.model.internal.IEdimap;
import org.milyn.edisax.model.internal.IField;
import org.milyn.edisax.model.internal.IMappingNode;
import org.milyn.edisax.model.internal.ISegmentGroup;
import org.milyn.edisax.model.internal.IValueNode;
import org.milyn.edisax.model.internal.Segment;
import org.milyn.edisax.model.internal.SegmentGroup;

/**
 * Utility class that convert EDI model to ECore model elements
 * 
 * @author zubairov
 * 
 */
public class ECoreConversionUtils {

	/**
	 * Supported data types for conversion
	 * 
	 */
	private static final EDataType ETYPES[] = { EcorePackage.Literals.ESTRING,
			EcorePackage.Literals.ELONG, EcorePackage.Literals.EBIG_DECIMAL,
			EcorePackage.Literals.EFLOAT };
	
	public static final String ANNOTATION_TYPE = "smooks-mapping-data";
	
	public static final String SEGMENT_TYPE = "segment";
	
	public static final String SEGMENT_GROUP_TYPE = "group";

	private static final String FIELD_TYPE = "field";

	/**
	 * Converting {@link Segment} to {@link EClass}
	 * 
	 * @param segment
	 * @return
	 */
	public static EClass segmentToEClass(Segment segment) {
		EClass clazz = segmentGroupToEClass(segment);
		annotate(clazz, "segcode", segment.getSegcode());
		annotate(clazz, "segcodePattern", segment.getSegcodePattern()
				.toString());
		annotate(clazz, "truncatable", String.valueOf(segment.isTruncatable()));
		annotate(clazz, "ignoreUnmappedFields",
				String.valueOf(segment.isIgnoreUnmappedFields()));
		annotate(clazz, "description", segment.getDescription());
		annotate(clazz, "type", SEGMENT_TYPE);
		return clazz;
	}

	/**
	 * This method transforms {@link Edimap} to {@link EPackage} where classes
	 * related to this {@link Edimap} will be stored
	 * 
	 * @param mappingModel
	 * @return
	 */
	public static EPackage mappingModelToEPackage(IEdimap mappingModel) {
		final EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		Description desc = mappingModel.getDescription();
		pkg.setName(desc.getName());
		pkg.setNsPrefix(desc.getName().toLowerCase());
		pkg.setNsURI("http://smooks.org/UNEDI/" 
				+ desc.getVersion().replace(':', '_') + "/" + desc.getName());
		if (mappingModel.getSrc() != null) {
			annotate(pkg, "src", mappingModel.getSrc().toASCIIString());
		}
		annotate(pkg, "description.name", mappingModel.getDescription()
				.getName());
		annotate(pkg, "description.version", mappingModel.getDescription()
				.getVersion());
		annotate(pkg, "delimeters.segment", mappingModel.getDelimiters()
				.getSegment());
		annotate(pkg, "delimeters.component", mappingModel.getDelimiters()
				.getComponent());
		annotate(pkg, "delimeters.field", mappingModel.getDelimiters()
				.getField());
		annotate(pkg, "delimeters.fieldRepeat", mappingModel.getDelimiters()
				.getFieldRepeat());
		annotate(pkg, "delimeters.escape", mappingModel.getDelimiters()
				.getEscape());
		annotate(pkg, "delimeters.ignoreCLRF",
				String.valueOf(mappingModel.getDelimiters().ignoreCRLF()));
		return pkg;
	}

	/**
	 * Converts {@link Segment} to {@link EReference}
	 * 
	 * @param segment
	 * @param refClass
	 * @return
	 */
	public static EReference segmentToEReference(Segment segment,
			EClass refClass) {
		EReference reference = segmentGroupToEReference(segment, refClass);
		annotate(reference, "type", SEGMENT_TYPE);
		return reference;
	}

	/**
	 * Converting {@link SegmentGroup} to {@link EClass}
	 * 
	 * @param grp
	 * @return
	 */
	public static EClass segmentGroupToEClass(ISegmentGroup grp) {
		EClass clazz = EcoreFactory.eINSTANCE.createEClass();
		clazz.setName(grp.getXmltag());
		addMappingInformation(clazz, grp);
		return clazz;
	}

	private static void addMappingInformation(EModelElement element,
			IMappingNode node) {
		if (node.getDocumentation() != null) {
			annotate(element, "documentation", node.getDocumentation());
		}
		if (node.getXmltag() != null) {
			annotate(element, "xmlTag", node.getXmltag());
		}
	}

	/**
	 * Annotate given {@link EModelElement} with smooks anntation with given key
	 * and value
	 * 
	 * @param element
	 * @param key
	 * @param value
	 */
	private static void annotate(EModelElement element, String key, String value) {
		EAnnotation annotation = element.getEAnnotation(ANNOTATION_TYPE);
		if (annotation == null) {
			annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(ANNOTATION_TYPE);
			element.getEAnnotations().add(annotation);
		}
		annotation.getDetails().put(key, value);
	}

	/**
	 * Convert {@link SegmentGroup} into {@link EReference} to the given
	 * {@link EClass}
	 * 
	 * @param grp
	 * @param refClass
	 * @return
	 */
	public static EReference segmentGroupToEReference(SegmentGroup grp,
			EClass refClass) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName(grp.getXmltag());
		reference.setEType(refClass);
		reference.setLowerBound(grp.getMinOccurs());
		reference.setUpperBound(grp.getMaxOccurs());
		addMappingInformation(reference, grp);
		annotate(reference, "minOccurs", String.valueOf(grp.getMinOccurs()));
		annotate(reference, "maxOccurs", String.valueOf(grp.getMaxOccurs()));
		annotate(reference, "type", SEGMENT_GROUP_TYPE);
		return reference;
	}

	/**
	 * Converting a {@link Field} to {@link EAttribute} Works only for
	 * {@link Field} where {@link Field#getComponents()} is empty
	 * 
	 * @param field
	 * @return
	 */
	public static EAttribute fieldToEAttribute(IField field) {
		if (!field.getComponents().isEmpty()) {
			throw new IllegalArgumentException(
					"Can't convert field with components to "
							+ "EAttribute, use fieldToEReference");
		}
		EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
		attr.setName(field.getXmltag());
		attr.setLowerBound(field.isRequired() ? 1 : 0);
		attr.setUpperBound(1);
		if (field.getTypeClass() != null) {
			attr.setEType(toEType(field.getTypeClass()));
		} else {
			System.err.println("WARN: Field " + field.getXmltag()
					+ " has no type! Setting it's type to String");
			attr.setEType(EcorePackage.Literals.ESTRING);
		}
		addMappingInformation(attr, field);
		annotate(attr, "trunkable", String.valueOf(field.isTruncatable()));
		annotate(attr, "type", FIELD_TYPE);
		return attr;
	}

	/**
	 * This method creates a new {@link EReference} to the {@link Field} that
	 * contains multiple {@link Component}.
	 * 
	 * For that purpose new {@link EClass} will be created and
	 * {@link EReference} will refer to it
	 * 
	 * @param field
	 * @param classes
	 * @return
	 */
	public static EReference fieldToEReference(IField field,
			Map<String, EClass> classes) {
		EClass newClass = fieldToEClass(field);
		if (!classes.containsKey(newClass.getName())) {
			classes.put(newClass.getName(), newClass);
		} else {
			System.err.println("WARN: Class for field " + newClass.getName()
					+ " is duplicated");
			newClass = classes.get(newClass.getName());
		}
		for (Component component : field.getComponents()) {
			EStructuralFeature attribute = componentToEAttribute(component);
			if (newClass.getEStructuralFeature(attribute.getName()) == null) {
				newClass.getEStructuralFeatures().add(attribute);
			} else {
				System.err.println("WARN: Field " + newClass.getName()
						+ " contains duplicate attribute "
						+ attribute.getName());
			}
		}
		EReference result = EcoreFactory.eINSTANCE.createEReference();
		result.setName(field.getXmltag());
		result.setLowerBound(field.isRequired() ? 1 : 0);
		result.setUpperBound(1);
		result.setEType(newClass);
		annotate(result, "type", FIELD_TYPE);
		addMappingInformation(result, field);
		return result;
	}

	/**
	 * Converts {@link Component} to {@link EAttribute}
	 * 
	 * @param component
	 * @return
	 */
	private static EStructuralFeature componentToEAttribute(Component component) {
		if (!component.getSubComponents().isEmpty()) {
			throw new IllegalArgumentException(
					"Sub-components are not supported yet for component "
							+ component.getXmltag());
		}
		EAttribute result = EcoreFactory.eINSTANCE.createEAttribute();
		result.setName(component.getXmltag());
		result.setLowerBound(component.isRequired() ? 1 : 0);
		result.setUpperBound(1);
		result.setEType(toEType(component.getTypeClass()));
		addMappingInformation(result, component);
		return result;
	}

	private static EClassifier toEType(Class<?> typeClass) {
		for (EDataType type : ETYPES) {
			if (type.getInstanceClass() == typeClass) {
				return type;
			}
		}
		throw new IllegalArgumentException("Type for type class " + typeClass
				+ " is not supported");
	}

	/**
	 * Creating a new {@link EClass} based on the information from {@link Field}
	 * used in case we have a complex {@link Field} and we need to create a
	 * class for it.
	 * 
	 * @param field
	 * @return
	 */
	private static EClass fieldToEClass(IField field) {
		String classifierName = field.getNodeTypeRef() + field.getXmltag();
		EClass newClass = EcoreFactory.eINSTANCE.createEClass();
		newClass.setName(classifierName);
		addMappingInformation(newClass, field);
		annotate(newClass, "type", FIELD_TYPE);
		annotateValueNode(newClass, field);
		return newClass;
	}

	private static void annotateValueNode(EModelElement element,
			IValueNode valueNode) {
		annotate(element, "datatype", valueNode.getDataType());
		annotate(element, "maxLength", String.valueOf(valueNode.getMaxLength()));
		annotate(element, "minLength", String.valueOf(valueNode.getMinLength()));
		if (valueNode.getDecoder() != null) {
			annotate(element, "decoder", valueNode.getDecoder().getClass()
					.getCanonicalName());
		}
	}

}
