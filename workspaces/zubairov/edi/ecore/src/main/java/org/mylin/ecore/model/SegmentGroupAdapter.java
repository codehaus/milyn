package org.mylin.ecore.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.milyn.edisax.model.internal.IMappingNode;
import org.milyn.edisax.model.internal.ISegmentGroup;
import org.mylin.ecore.ECoreConversionUtils;

/**
 * Adapter class that adapts {@link EClass} to {@link ISegmentGroup}
 * 
 * @author zubairov
 * 
 */
public class SegmentGroupAdapter extends ModelAdapter implements ISegmentGroup {

	protected final EClass clazz;
	protected final EReference ref;
	private List<ISegmentGroup> segments;
	private Pattern pattern;

	public SegmentGroupAdapter(EClass clazz) {
		this.clazz = clazz;
		this.ref = null;
	}

	public SegmentGroupAdapter(EReference reference) {
		this.ref = reference;
		this.clazz = reference.getEReferenceType();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getXmltag() {
		return getAnnotationValue(clazz, "xmlTag");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getNodeTypeRef() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDocumentation() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	/**
	 * {@inheritDoc}
	 */
	public IMappingNode getParent() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getJavaName() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ISegmentGroup> getSegments() {
		if (segments == null) {
			segments = new ArrayList<ISegmentGroup>();
			EList<EStructuralFeature> features = clazz.getEStructuralFeatures();
			for (EStructuralFeature feature : features) {
				if (feature instanceof EReference) {
					String type = getAnnotationValue(feature, "type");
					if (ECoreConversionUtils.SEGMENT_TYPE.equals(type)) {
						segments.add(new SegmentAdapter((EReference)feature));
					} else if (ECoreConversionUtils.SEGMENT_GROUP_TYPE.equals(type)) {
						segments.add(new SegmentGroupAdapter((EReference) feature));
					} else {
						throw new UnsupportedOperationException("Not supported Reference type " + type);
					}
				} else {
					throw new UnsupportedOperationException(
							"Type not supported " + feature);
				}
			}
		}
		return segments;
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
			pattern = Pattern.compile(getAnnotationValue(clazz,
					"segcodePattern"));
		}
		return pattern;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMinOccurs() {
		if (ref == null) {
			throw new NullPointerException("Reference value missing");
		}
		return Integer.parseInt(getAnnotationValue(ref, "minOccurs"));
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMaxOccurs() {
		if (ref == null) {
			throw new NullPointerException("Reference value missing");
		}
		return Integer.parseInt(getAnnotationValue(ref, "maxOccurs"));
	}

}
