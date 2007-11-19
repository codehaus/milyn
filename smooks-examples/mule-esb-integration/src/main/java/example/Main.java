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
package example;

import org.apache.log4j.Logger;

/**
 * Simple example main class.
 * @author <a href="mailto:daniel.bevenius@gmail.com">daniel.bevenius@gmail.com</a>
 */
public class Main implements SmooksMule {
	
	private Logger log = Logger.getLogger( Main.class );

	public void performService(String s) 
	{
		log.debug( "Input to Service : " + s);
		//... service impl here
	}
}
