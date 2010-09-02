package org.mylin.ecore.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.milyn.edisax.model.internal.IField;
import org.milyn.edisax.model.internal.ISegment;
import org.milyn.edisax.model.internal.ISegmentGroup;

/**
 * Adapter that adapts {@link EReference} to {@link ISegment}
 * 
 * @author zubairov
 *
 */
public class SegmentAdapter extends SegmentGroupAdapter implements ISegment {

	private List<IField> fields;

	public SegmentAdapter(EReference reference) {
		super(reference);
	}

	public List<IField> getFields() {
		if (fields == null) {
			fields = new ArrayList<IField>();
			EList<EStructuralFeature> features = clazz.getEStructuralFeatures();
			for (EStructuralFeature feature : features) {
				fields.add(new FieldAdapter(feature));
			}
		}
		return fields;
	}

	public boolean isTruncatable() {
		return Boolean.valueOf(getAnnotationValue(clazz, "truncable"));
	}

	public boolean isIgnoreUnmappedFields() {
		return Boolean.valueOf(getAnnotationValue(clazz, "ignoreUnmappedFields"));
	}

	public String getDescription() {
		throw new UnsupportedOperationException("TODO Implement");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ISegmentGroup> getSegments() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSegcode() {
		return getAnnotationValue(clazz, "segcode");
	}

	/**
	 * {@inheritDoc}
	 */
	public Pattern getSegcodePattern() {
		if (pattern == null) {
			pattern = Pattern.compile(getAnnotationValue(clazz, "segcodePattern"));
		}
		return pattern;
	}


}
