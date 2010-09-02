package org.mylin.ecore.model;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.milyn.edisax.model.internal.IMappingNode;
import org.milyn.edisax.model.internal.IValueNode;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;

/**
 * Adapter that adapt {@link EStructuralFeature} to {@link IValueNode}
 * 
 * @author zubairov
 *
 */
public class ValueNodeAdapter extends ModelAdapter implements IValueNode {

	protected EStructuralFeature feature;
	
	private DataDecoder decoder;

	public ValueNodeAdapter(EStructuralFeature feature) {
		this.feature = feature;
	}

	public String getDataType() {
		return getAnnotationValue(feature, "datatype");
	}

	public DataDecoder getDecoder() {
	    if (decoder == null) {
	    	decoder = DataDecoder.Factory.create(getDataType());
	    }
	    return decoder;
	}

	public Integer getMinLength() {
		return Integer.parseInt(getAnnotationValue(feature, "minLength"));
	}

	public Integer getMaxLength() {
		return Integer.parseInt(getAnnotationValue(feature, "maxLength"));
	}

	public void isValidForType(String value) throws DataDecodeException {
	    getDecoder().decode(value);
	}

	public String getXmltag() {
		return getAnnotationValue(feature, "xmlTag");
	}

	public String getNodeTypeRef() {
		return getAnnotationValue(feature, "nodeTypeRef");
	}

	public String getDocumentation() {
		return getAnnotationValue(feature, "documentation");
	}

	public IMappingNode getParent() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public String getJavaName() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public boolean isTruncatable() {
		return Boolean.valueOf(getAnnotationValue(feature, "truncable"));
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

}