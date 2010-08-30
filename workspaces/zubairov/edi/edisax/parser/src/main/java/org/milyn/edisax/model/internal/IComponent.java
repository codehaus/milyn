package org.milyn.edisax.model.internal;

import java.util.List;

public interface IComponent extends IValueNode, ContainerNode {

	public abstract List<SubComponent> getSubComponents();

	public abstract boolean isRequired();

	public abstract boolean isTruncatable();

}