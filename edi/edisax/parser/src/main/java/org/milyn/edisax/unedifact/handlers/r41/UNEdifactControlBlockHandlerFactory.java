/*
 * Milyn - Copyright (C) 2006 - 2010
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License (version 2.1) as published by the Free Software
 * Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */

package org.milyn.edisax.unedifact.handlers;

import org.milyn.edisax.interchange.ControlBlockHandler;
import org.milyn.edisax.interchange.ControlBlockHandlerFactory;
import org.milyn.xml.hierarchy.HierarchyChangeListener;
import org.xml.sax.SAXException;

/**
 * UN/EDIFACT control block handler factory.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UNEdifactControlBlockHandlerFactory implements ControlBlockHandlerFactory {

    private HierarchyChangeListener hierarchyChangeListener;

    public UNEdifactControlBlockHandlerFactory(HierarchyChangeListener hierarchyChangeListener) {
        this.hierarchyChangeListener = hierarchyChangeListener;
    }

    public ControlBlockHandler getControlBlockHandler(String segCode) throws SAXException {

        if(segCode.equals("UNH")) {
            return new UNHHandler(hierarchyChangeListener);
        } else if(segCode.equals("UNG")) {
            return new UNGHandler();
        } else if(segCode.equals("UNA")) {
            return new UNAHandler();
        } else if(segCode.equals("UNB")) {
            return new UNBHandler();
        } else if(segCode.charAt(0) == 'U') {
            return new GenericHandler();
        }

        throw new SAXException("Unknown/Unexpected UN/EDIFACT control block segment code '" + segCode + "'.");
    }
}
