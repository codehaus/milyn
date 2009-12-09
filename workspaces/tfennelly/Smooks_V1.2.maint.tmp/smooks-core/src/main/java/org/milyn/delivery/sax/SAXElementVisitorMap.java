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
package org.milyn.delivery.sax;

import org.milyn.delivery.ContentHandler;
import org.milyn.delivery.ContentHandlerConfigMap;
import org.milyn.delivery.VisitLifecycleCleanable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * SAXElement visitor Map.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXElementVisitorMap {

	public boolean isBlank = false;
    public List<ContentHandlerConfigMap<SAXVisitBefore>> visitBefores;
    public List<ContentHandlerConfigMap<SAXVisitChildren>> childVisitors;
    public List<ContentHandlerConfigMap<SAXVisitAfter>> visitAfters;
    public List<ContentHandlerConfigMap<VisitLifecycleCleanable>> visitCleanables;
    
    public SAXElementVisitorMap(boolean isBlank) {
    	this.isBlank = isBlank;
    }
    
    public List<ContentHandlerConfigMap<SAXVisitBefore>> getVisitBefores() {
        return visitBefores;
    }

    public void setVisitBefores(List<ContentHandlerConfigMap<SAXVisitBefore>> visitBefores) {
        this.visitBefores = visitBefores;
    }

    public List<ContentHandlerConfigMap<SAXVisitChildren>> getChildVisitors() {
        return childVisitors;
    }

    public void setChildVisitors(List<ContentHandlerConfigMap<SAXVisitChildren>> childVisitors) {
        this.childVisitors = childVisitors;
    }

    public List<ContentHandlerConfigMap<SAXVisitAfter>> getVisitAfters() {
        return visitAfters;
    }
    
    public void addSelectorPathElementNames(Set<String> nameSet) {
    	addSelectorPathElementNames(visitBefores, nameSet);
    	addSelectorPathElementNames(visitAfters, nameSet);
    }

	private <T extends ContentHandler> void addSelectorPathElementNames(List<ContentHandlerConfigMap<T>> visitorConfigList, Set<String> nameSet) {
		if(visitorConfigList != null) {
			for(ContentHandlerConfigMap<T> visitorConfig : visitorConfigList) {
				String[] selectorTokens = visitorConfig.getResourceConfig().getContextualSelector();
				for(String selectorToken : selectorTokens) {
					nameSet.add(selectorToken);
				}
			}
		}
	}

	public void setVisitAfters(List<ContentHandlerConfigMap<SAXVisitAfter>> visitAfters) {
        this.visitAfters = visitAfters;
    }

    public List<ContentHandlerConfigMap<VisitLifecycleCleanable>> getVisitCleanables() {
        return visitCleanables;
    }

    public void setVisitCleanables(List<ContentHandlerConfigMap<VisitLifecycleCleanable>> visitCleanables) {
        this.visitCleanables = visitCleanables;
    }
    
    public SAXElementVisitorMap merge(SAXElementVisitorMap map) {
    	if(map == null) {
    		// No need to merge...
    		return this;
    	}    	
    	
    	SAXElementVisitorMap merge = new SAXElementVisitorMap(map.isBlank);
    	
        merge.visitBefores = new ArrayList<ContentHandlerConfigMap<SAXVisitBefore>>();
        merge.childVisitors = new ArrayList<ContentHandlerConfigMap<SAXVisitChildren>>();
        merge.visitAfters = new ArrayList<ContentHandlerConfigMap<SAXVisitAfter>>();
        merge.visitCleanables = new ArrayList<ContentHandlerConfigMap<VisitLifecycleCleanable>>();
        
        merge.visitBefores.addAll(visitBefores);
        merge.visitBefores.addAll(map.visitBefores);
        merge.childVisitors.addAll(childVisitors);
        merge.childVisitors.addAll(map.childVisitors);
        merge.visitAfters.addAll(visitAfters);
        merge.visitAfters.addAll(map.visitAfters);
        merge.visitCleanables.addAll(visitCleanables);
        merge.visitCleanables.addAll(map.visitCleanables);
    	
    	return merge;
    }
}
