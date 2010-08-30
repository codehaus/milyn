package org.milyn.edisax.model.internal;

import java.util.List;

public interface IField extends IValueNode, ContainerNode {

	public abstract List<Component> getComponents();

	public abstract boolean isRequired();

	public abstract boolean isTruncatable();

}