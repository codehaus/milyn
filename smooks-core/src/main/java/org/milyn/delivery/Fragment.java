/*
 * Milyn - Copyright (C) 2006 - 2010
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License (version 2.1) as published by the Free Software
 *  Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  See the GNU Lesser General Public License for more details:
 *  http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.delivery;

import org.milyn.assertion.AssertArgument;
import org.milyn.delivery.sax.SAXElement;
import org.w3c.dom.Element;

/**
 * Fragment.
 * <p/>
 * Wrapper class for a DOM or SAX Fragment.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Fragment {

    private Element domFragment;
    private SAXElement saxFragment;

    public Fragment(Element fragmentElement) {
        this.domFragment = fragmentElement;
    }

    public Fragment(SAXElement fragmentElement) {
        this.saxFragment = fragmentElement;
    }

    public Element getDOMElement() {
        return domFragment;
    }

    public SAXElement getSAXElement() {
        return saxFragment;
    }

    private boolean isDOMElement() {
        return (domFragment != null);
    }

    private boolean isSAXElement() {
        return (saxFragment != null);
    }

    public String getNamespaceURI() {
        if(isSAXElement()) {
            return saxFragment.getName().getNamespaceURI();
        } else if(isDOMElement()) {
            return domFragment.getNamespaceURI();
        }
        return null;
    }

    public String getPrefix() {
        if(isSAXElement()) {
            return saxFragment.getName().getPrefix();
        } else if(isDOMElement()) {
            return domFragment.getPrefix();
        }
        return null;
    }
}
