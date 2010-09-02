package org.mylin.ecore.model;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.milyn.edisax.model.internal.IComponent;
import org.milyn.edisax.model.internal.SubComponent;

/**
 * Adapts {@link EStructuralFeature} to {@link IComponent}
 * 
 * @author zubairov
 *
 */
public class ComponentAdapter extends ValueNodeAdapter implements IComponent {

	private final EAttribute attribute;

	public ComponentAdapter(EAttribute attr) {
		super(attr);
		this.attribute = attr;
	}

	@SuppressWarnings("unchecked")
	public List<SubComponent> getSubComponents() {
		// TODO Clarify why we don't have subcomponents
		return Collections.EMPTY_LIST;
	}

	public boolean isRequired() {
		return Boolean.valueOf(getAnnotationValue(attribute, "required"));
	}

	public boolean isTruncatable() {
		throw new UnsupportedOperationException("TODO Implement");
	}
}
