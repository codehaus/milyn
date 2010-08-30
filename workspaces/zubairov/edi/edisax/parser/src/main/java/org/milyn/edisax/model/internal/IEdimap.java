package org.milyn.edisax.model.internal;

import java.net.URI;
import java.util.List;

public interface IEdimap {

	public abstract URI getSrc();

	public abstract List<Import> getImports();

	public abstract Description getDescription();

	public abstract Delimiters getDelimiters();

	public abstract ISegmentGroup getSegments();

}