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
package org.milyn.ejc;

import java.util.List;
import java.util.Map;

/**
 * ValueNodeInfo contains information about values from a ValueNode. The values contained here are xmltag and
 * typeParameters.
 * 
 * @author bardl.
 */
public class ValueNodeInfo {
    private String xmlElementName;
    private List<Map.Entry<String,String>> decoderConfigs;

    public ValueNodeInfo(String xmlElementName, List<Map.Entry<String,String>> decoderConfigs) {
        this.xmlElementName = xmlElementName;
        this.decoderConfigs = decoderConfigs;
    }

    public String getXmlElementName() {
        return xmlElementName;
    }

    public List<Map.Entry<String,String>> getDecoderConfigs() {
        return decoderConfigs;
    }

}
