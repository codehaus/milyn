package org.mylin.ecore.model;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.edisax.model.internal.Description;
import org.milyn.edisax.model.internal.IEdimap;
import org.milyn.edisax.model.internal.ISegmentGroup;
import org.milyn.edisax.model.internal.Import;

/**
 * Adapter that adapt {@link EPackage} to {@link IEdimap}
 * 
 * @author zubairov
 *
 */
public class EdimapAdapter extends ModelAdapter implements IEdimap {

	private EPackage pkg;
	
	private Description description;

	private ISegmentGroup segments;

	public EdimapAdapter(EPackage pkg) {
		this.pkg = pkg;
	}

	public URI getSrc() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	@SuppressWarnings("unchecked")
	public List<Import> getImports() {
		return Collections.EMPTY_LIST;
	}

	public Description getDescription() {
		if (description == null) {
			description = new Description();
			description.setName(getAnnotationValue(pkg, "description.name"));
			description.setVersion(getAnnotationValue(pkg, "description.version"));
		}
		return description;
	}

	public Delimiters getDelimiters() {
		throw new UnsupportedOperationException("TODO Implement");
	}

	public ISegmentGroup getSegments() {
		if (segments == null) {
			segments = new SegmentGroupAdapter((EClass) pkg.getEClassifier(pkg.getName() + "Root"));
		}
		return segments;
	}
	
}
