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

package org.milyn.tinak;

/**
 * Select Tag implementation.
 * <p/>
 * The <code>select</code> tag selects the first enclosed/nested <code>ua</code> tag
 * which matches successfully.  It causes a sequence of <code>ua</code> tags to behave
 * in the same way as an if-elseif-elseif sequence.
 * @author Tom Fennelly
 */

public class SelectTag extends ContainerTag {
	/*
	 * Typing implementation.
	 * 
	 * The ua tag checks has the select tag already had content appended
	 * to it before evaluating it's own content.
	 */
}
