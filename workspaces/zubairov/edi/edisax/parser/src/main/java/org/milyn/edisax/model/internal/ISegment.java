package org.milyn.edisax.model.internal;

import java.util.List;

public interface ISegment extends ISegmentGroup {

	public abstract List<IField> getFields();

	public abstract boolean isTruncatable();

	public abstract boolean isIgnoreUnmappedFields();

	public abstract String getDescription();

}