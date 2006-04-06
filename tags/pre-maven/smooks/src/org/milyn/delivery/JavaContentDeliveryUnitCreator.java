/*
	Milyn - Copyright (C) 2003

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

package org.milyn.delivery;

import java.lang.reflect.Constructor;

import org.milyn.cdr.CDRStore;
import org.milyn.cdr.CDRDef;

/**
 * Java ContentDeliveryUnit instance creator.
 * <p/>
 * Java-based ContentDeliveryUnit implementations should contain a public 
 * constructor that takes a CDRDef instance as a parameter.
 * @see XslContentDeliveryUnitCreator 
 * @author tfennelly
 */
public class JavaContentDeliveryUnitCreator implements ContentDeliveryUnitCreator {

	/**
	 * Create a Java based ContentDeliveryUnit instance ie from a Java Class byte stream.
	 * <p/>
	 * @see XslContentDeliveryUnitCreator 
	 */
	public synchronized ContentDeliveryUnit create(CDRDef cdrDef, CDRStore cdrarStore) throws InstantiationException{
		ContentDeliveryUnit deliveryUnit = null;
		
		try {
			Class classRuntime = cdrarStore.getCdrarClassLoader().loadClass(cdrDef);
			Constructor constructor = classRuntime.getConstructor(new Class[] {CDRDef.class});
			
			deliveryUnit = (ContentDeliveryUnit)constructor.newInstance(new Object[] {cdrDef});
		} catch (NoSuchMethodException e) {
			IllegalStateException state = new IllegalStateException("Unable to load Java ContentDeliveryUnit implementation [" + cdrDef.getPath() + "]. Imlementation must provide a public constructor that takes a CDRDef arg.");
			state.initCause(e);
			throw state;
		} catch (Exception e) {
			InstantiationException instanceException = new InstantiationException("Java ContentDeliveryUnit class resource [" + cdrDef.getPath() + "] not loadable through " + cdrarStore.getCdrarClassLoader().getClass().getName() + ".");
			instanceException.initCause(e);
			throw instanceException;
		}
		
		return deliveryUnit;
	}
}
