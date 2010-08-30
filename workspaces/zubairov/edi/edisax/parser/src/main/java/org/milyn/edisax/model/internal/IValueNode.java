package org.milyn.edisax.model.internal;

import java.util.List;
import java.util.Map;

import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;

public interface IValueNode extends IMappingNode {

	public abstract String getDataType();

	public abstract DataDecoder getDecoder();

	public abstract Class<?> getTypeClass();

	public abstract List<Map.Entry<String, String>> getTypeParameters();

	public abstract String getDataTypeParametersString();

	public abstract Integer getMinLength();

	public abstract Integer getMaxLength();

	public abstract void isValidForType(String value)
			throws DataDecodeException;

}