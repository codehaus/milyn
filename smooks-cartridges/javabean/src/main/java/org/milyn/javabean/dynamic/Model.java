/*
	Milyn - Copyright (C) 2006 - 2010

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/
package org.milyn.javabean.dynamic;

import java.util.ArrayList;
import java.util.List;

import org.milyn.assertion.AssertArgument;

/**
 * Model container.
 * <p/>
 * Contains the {@link #getModelRoot() modelRoot} object instance, as well
 * as {@link #getModelMetadata() modelMetadata} associated with the
 * objects wired into the object graph routed on the {@link #getModelRoot() modelRoot}.
 * The {@link #getModelMetadata() modelMetadata} can contain information for, among other
 * things, serializing the object graph routed at {@link #getModelRoot() modelRoot}.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Model<T> {
	
	private T modelRoot;
	private List<BeanMetadata> modelMetadata;

	/**
	 * Public constructor.
	 * @param modelRoot The model root object.
	 */
	public Model(T modelRoot) {
		this.modelRoot = modelRoot;
		this.modelMetadata = new ArrayList<BeanMetadata>();
	}

	/**
	 * Public constructor.
	 * @param modelRoot The model root object.
	 * @param modelMetadata Model metadata.
	 */
	public Model(T modelRoot, List<BeanMetadata> modelMetadata) {
		this.modelRoot = modelRoot;
		this.modelMetadata = modelMetadata;
	}

	/**
	 * Get the model root object instance.
	 * @return the model root object instance.
	 */
	public T getModelRoot() {
		return modelRoot;
	}

	/**
	 * Get the model metadata.
	 * <p/>
	 * A {@link BeanMetadata} list containing metadata about objects
	 * wired into the object graph, routed at the {@link #getModelRoot() model root}.
	 * 
	 * @return Model metadata.
	 */
	public List<BeanMetadata> getModelMetadata() {
		return modelMetadata;
	}

	/**
	 * Get the model metadata.
	 * <p/>
	 * A {@link BeanMetadata} list containing metadata about objects
	 * wired into the object graph, routed at the {@link #getModelRoot() model root}.
	 * 
	 * @return Model metadata.
	 */
	public BeanMetadata getBeanMetadata(Object beanInstance) {
		AssertArgument.isNotNull(beanInstance, "beanInstance");		
		for(BeanMetadata beanMetadata: modelMetadata) {
			if(beanMetadata.getBean() == beanInstance) {
				return beanMetadata;
			}
		}		
		return null;
	}
}
