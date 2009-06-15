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

package org.milyn.smooks.edi;

/**
 * Smooks EDI Reader.
 * <p/>
 * Hooks the Milyn {@link org.milyn.edisax.EDIParser} into the <a href="http://milyn.codehaus.org/Smooks" target="new">Smooks</a> framework.
 * This adds EDI processing support to Smooks.
 *
 * <h3>Configuration</h3>
 * <pre>
 * &lt;smooks-resource useragent="<i>&lt;profile&gt;</i>" selector="org.xml.sax.driver" path="org.milyn.smooks.edi.SmooksEDIReader" &gt;
 *
 *  &lt;!--
 *      (Mandatory) {@link org.milyn.edisax.EDIParser Mapping Model}.  Can be a URI specifiying the location of the model (see {@link org.milyn.resource.URIResourceLocator}),
 *      or can be the model itself (inlined).
 *  --&gt;
 *  &lt;param name="<b>mapping-model</b>"&gt;[{@link java.net.URI} | <i>inlined model</i>]&lt;/param&gt;
 *
 * &lt;/smooks-resource&gt;
 * </pre>
 *
 * @author tfennelly
 * @deprecated Please use {@link EDIReader} instead.
 */
@Deprecated
public class SmooksEDIReader extends EDIReader {
}
