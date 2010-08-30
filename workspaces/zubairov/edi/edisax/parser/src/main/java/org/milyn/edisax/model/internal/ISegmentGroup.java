package org.milyn.edisax.model.internal;

import java.util.List;
import java.util.regex.Pattern;

public interface ISegmentGroup extends IMappingNode {

	public abstract List<ISegmentGroup> getSegments();

	public abstract String getSegcode();

	public abstract Pattern getSegcodePattern();

	public abstract int getMinOccurs();

	public abstract int getMaxOccurs();

}