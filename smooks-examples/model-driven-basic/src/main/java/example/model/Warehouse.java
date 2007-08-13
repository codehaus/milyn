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
package example.model;

import java.util.Map;
import java.util.HashMap;

/**
 * Warehouse bean.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Warehouse {

    private int id = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return warehouses.get(id);
    }

    // Enrich the model.  This data could come from a database, .properties file etc
    private static Map<Integer, String> warehouses = new HashMap<Integer, String>();
    static {
        warehouses.put(-1, "$$-Unset-$$");
        warehouses.put(1, "Dublin");
        warehouses.put(2, "Belfast");
        warehouses.put(3, "Cork");
    }
}
