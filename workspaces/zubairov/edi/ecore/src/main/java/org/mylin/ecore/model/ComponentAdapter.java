package org.mylin.ecore.model;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.milyn.edisax.model.internal.IComponent;
import org.milyn.edisax.model.internal.IMappingNode;
import org.milyn.edisax.model.internal.SubComponent;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;

/**
 * Adapts {@link EStructuralFeature} to {@link IComponent}
 * 
 * @author zubairov
 *
 */
public class ComponentAdapter extends ModelAdapter implements IComponent {

	private final EAttribute attribute;

	public ComponentAdapter(EAttribute attr) {
		this.attribute = attr;
	}

	public String getDataType() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public DataDecoder getDecoder() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public Class<?> getTypeClass() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public List<Entry<String, String>> getTypeParameters() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public String getDataTypeParametersString() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public Integer getMinLength() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public Integer getMaxLength() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public void isValidForType(String value) throws DataDecodeException {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public String getXmltag() {
		return getAnnotationValue(attribute, "xmlTag");
	}

	public String getNodeTypeRef() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public String getDocumentation() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public IMappingNode getParent() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public String getJavaName() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	@SuppressWarnings("unchecked")
	public List<SubComponent> getSubComponents() {
		// TODO Clarify why we don't have subcomponents
		return Collections.EMPTY_LIST;
	}

	public boolean isRequired() {
		return Boolean.valueOf(getAnnotationValue(attribute, "required"));
	}

	public boolean isTruncatable() {
		throw new UnsupportedOperationException("TODO Implement");
	}
}
