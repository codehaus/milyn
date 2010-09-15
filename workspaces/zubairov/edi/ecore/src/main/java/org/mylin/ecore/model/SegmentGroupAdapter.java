package org.mylin.ecore.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.milyn.edisax.model.internal.IMappingNode;
import org.milyn.edisax.model.internal.ISegmentGroup;
import org.mylin.ecore.EMFHelper;
import org.mylin.ecore.SmooksMetadata;

/**
 * Adapter class that adapts {@link EClass} to {@link ISegmentGroup}
 * 
 * @author zubairov
 * 
 */
public class SegmentGroupAdapter implements ISegmentGroup {

	protected final EClass clazz;
	protected final EReference ref;
	private List<ISegmentGroup> segments;
	protected Pattern pattern;
	private static final ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;

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
		// We need to prefer xmlTag from reference to the xmlTag from class definition
		// because in both common definitions xml and in mapping xml
		// we have XML tags, but reference should be winning
		if (ref != null) {
			return metadata.getName(ref);
		}
		return metadata.getName(clazz);
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
					String type = EMFHelper.getAnnotationValue(feature, "type");
					if (SmooksMetadata.SEGMENT_TYPE.equals(type)) {
						segments.add(new SegmentAdapter((EReference)feature));
					} else if (SmooksMetadata.SEGMENT_GROUP_TYPE.equals(type)) {
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
	public Pattern getSegcodePattern() {
		if (pattern == null) {
			if (getSegments().isEmpty()) {
				throw new IllegalArgumentException("Segments list of segment group empty : " + clazz);
			}
			pattern = getSegments().get(0).getSegcodePattern();
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
		return Integer.parseInt(EMFHelper.getAnnotationValue(ref, "minOccurs"));
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMaxOccurs() {
		if (ref == null) {
			throw new NullPointerException("Reference value missing");
		}
		return Integer.parseInt(EMFHelper.getAnnotationValue(ref, "maxOccurs"));
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSegcode() {
		if (getSegments().isEmpty()) {
			throw new IllegalArgumentException("Segments list of segment group empty : " + clazz);
		}
		return getSegments().get(0).getSegcode();
	}

	public String getNamespace() {
		if (ref != null) {
			return ExtendedMetaData.INSTANCE.getNamespace(ref);
		}
		return ExtendedMetaData.INSTANCE.getNamespace(clazz);
	}

}
