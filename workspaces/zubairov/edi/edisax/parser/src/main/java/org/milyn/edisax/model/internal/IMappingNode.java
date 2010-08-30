package org.milyn.edisax.model.internal;

public interface IMappingNode {

	public abstract String getXmltag();

	public abstract String getNodeTypeRef();

	public abstract String getDocumentation();

	public abstract IMappingNode getParent();

	public abstract String getJavaName();

}