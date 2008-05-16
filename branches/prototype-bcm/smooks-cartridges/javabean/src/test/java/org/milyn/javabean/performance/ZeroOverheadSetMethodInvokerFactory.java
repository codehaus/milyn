/*
	Milyn - Copyright (C) 2006

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
package org.milyn.javabean.performance;

import org.milyn.container.ApplicationContext;
import org.milyn.javabean.invoker.SetMethodInvoker;
import org.milyn.javabean.invoker.SetMethodInvokerFactory;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ZeroOverheadSetMethodInvokerFactory implements SetMethodInvokerFactory {
    public void initialize(ApplicationContext applicationContext) {
    }

    public SetMethodInvoker create(String setterName, Class<?> beanClass, Class<?> setterParamType) {
        return new ZeroOverheadPropertySetMethodInvocator();
    }

    public class ZeroOverheadPropertySetMethodInvocator implements SetMethodInvoker {
        public void set(Object obj, Object arg) {
            // Do nothing.... zero overhead....
        }
    }
}
