package org.mylin.ecore.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.milyn.edisax.model.internal.IComponent;
import org.milyn.edisax.model.internal.IField;
import org.milyn.edisax.model.internal.IMappingNode;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;

/**
 * Adapt {@link EReference} or {@link EAttribute} to {@link IField}
 * 
 * @author zubairov
 *
 */
public class FieldAdapter extends ModelAdapter implements IField {


	private EStructuralFeature feature;
	
	private ArrayList<IComponent> components;

	public FieldAdapter(EStructuralFeature feature) {
		this.feature = feature;
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
		throw new UnsupportedOperationException("TODO Implement");
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
	public List<IComponent> getComponents() {
		if (feature instanceof EReference) {
			if (components == null) {
				components = new ArrayList<IComponent>();
			}
			return components;
		} 
		return Collections.EMPTY_LIST;
	}

	public boolean isRequired() {
		return Boolean.valueOf(getAnnotationValue(feature, "required"));
	}

	public boolean isTruncatable() {
		throw new UnsupportedOperationException("TODO Implement");
	}

}
