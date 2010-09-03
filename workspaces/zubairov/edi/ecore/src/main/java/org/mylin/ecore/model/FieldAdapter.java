package org.mylin.ecore.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.milyn.edisax.model.internal.IComponent;
import org.milyn.edisax.model.internal.IField;
import org.mylin.ecore.EMFHelper;

/**
 * Adapt {@link EReference} or {@link EAttribute} to {@link IField}
 * 
 * @author zubairov
 *
 */
public class FieldAdapter extends ValueNodeAdapter implements IField {

	private ArrayList<IComponent> components;

	public FieldAdapter(EStructuralFeature feature) {
		super(feature);
	}

	@SuppressWarnings("unchecked")
	public List<IComponent> getComponents() {
		if (feature instanceof EReference) {
			EReference ref = (EReference) feature;
			EClass clazz = ref.getEReferenceType();
			if (components == null) {
				components = new ArrayList<IComponent>();
				EList<EStructuralFeature> features = clazz.getEStructuralFeatures();
				for (EStructuralFeature compFeature : features) {
					components.add(new ComponentAdapter((EAttribute) compFeature));
				}
			}
			return components;
		} 
		return Collections.EMPTY_LIST;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRequired() {
		return Boolean.valueOf(EMFHelper.getAnnotationValue(feature, "required"));
	}

}
