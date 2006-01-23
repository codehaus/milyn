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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.milyn.cdr.CDRDef;
import org.milyn.cdr.CDRDefSortComparator;
import org.milyn.cdr.cdrar.CDRArchiveEntry;
import org.milyn.cdr.cdrar.CDRArchiveEntryNotFoundException;
import org.milyn.container.ContainerContext;
import org.milyn.delivery.assemble.AssemblyUnit;
import org.milyn.delivery.serialize.SerializationUnit;
import org.milyn.delivery.trans.TransSet;
import org.milyn.delivery.trans.TransUnit;
import org.milyn.device.UAContext;
import org.milyn.dtd.DTDStore;
import org.milyn.dtd.DTDStore.DTDObjectContainer;
import org.milyn.ioc.BeanFactory;
import org.milyn.logging.SmooksLogger;


/**
 * Useragent content delivery configuration.
 * @author tfennelly
 */
public class ContentDeliveryConfigImpl implements ContentDeliveryConfig {
	
	/**
	 * Context key for the table of loaded ContentDeliveryConfig instances.
	 */
	private static final String DELIVERY_CONFIG_TABLE_CTX_KEY = ContentDeliveryConfig.class.getName() + "#configTable";
	/**
	 * Associated device context.
	 */
	private UAContext deviceContext;
	/**
	 * Container context.
	 */
	private ContainerContext containerContext;
	/**
	 * XML selector content spec definition prefix
	 */
	private static final String ELCSPEC_PREFIX = "elcspec:";
	/**
	 * Table of CDRDef instances keyed by selector value. Each table entry
	 * contains a List of CDRDef instances.
	 */
	private Hashtable cdrDefTable = new Hashtable();
	/**
	 * Table of AssemblyUnit instances keyed by selector. Each table entry
	 * contains a single AssemblyUnit instances.
	 */
	private Hashtable assemblyUnitTable = new Hashtable();
	/**
	 * Table of TransSet instances keyed by selector. Each table entry
	 * contains a TransSet instances.
	 */
	private Hashtable transSetTable = new Hashtable();
	/**
	 * Table of SerializationUnit instances keyed by selector. Each table entry
	 * contains a single SerializationUnit instances.
	 */
	private Hashtable serializationUnitTable = new Hashtable();
	/**
	 * Table of Object instance lists keyed by selector. Each table entry
	 * contains a List of Objects.
	 */
	private Hashtable objectsTable = new Hashtable();
	/**
	 * DTD for the associated device.
	 */
	private DTDObjectContainer dtd;
	
	/**
	 * Private (hidden) constructor. 
	 * @param containerContext
	 */
	private ContentDeliveryConfigImpl(UAContext deviceContext, ContainerContext containerContext) {
		this.deviceContext = deviceContext;
		this.containerContext = containerContext;
	}
	
	/**
	 * Get the ContentDeliveryConfigImpl instance for the named table.
	 * @param deviceContext The device context for the associated device.
	 * @param containerContext Container context.
	 * @return The ContentDeliveryConfig instance for the named table.
	 */
	public static ContentDeliveryConfig getInstance(UAContext deviceContext, ContainerContext containerContext) {
		ContentDeliveryConfigImpl table = null;
		Hashtable configTable;
		
		if(deviceContext == null) {
			throw new IllegalArgumentException("null 'deviceContext' arg passed in method call.");
		} else if(containerContext == null) {
			throw new IllegalArgumentException("null 'containerContext' arg passed in method call.");
		}

		// Get the delivery config table from container context.
		configTable = (Hashtable)containerContext.getAttribute(DELIVERY_CONFIG_TABLE_CTX_KEY);
		if(configTable == null) {
			configTable = new Hashtable();
			containerContext.setAttribute(DELIVERY_CONFIG_TABLE_CTX_KEY, configTable);
		}
		// Get the delivery config instance for this UAContext
		table = (ContentDeliveryConfigImpl)configTable.get(deviceContext);
		if(table == null) {
			table = new ContentDeliveryConfigImpl(deviceContext, containerContext);
			table.load();
			configTable.put(deviceContext, table);
		}
		
		return table;
	}

	/**
	 * Build the ContentDeliveryConfigImpl for the specified device.
	 * <p/>
	 * Creates the buildTable instance and populates it with the TransUnit matrix
	 * for the specified device.
	 * @param deviceContext The associated device context.
	 */
	private void load() {
		List cdrDefsList = Arrays.asList(containerContext.getCdrarStore().getCDRDefs(deviceContext));

		// Build and sort the cdrDefTable table - non-transforming elements.
		buildCDRDefTable(cdrDefsList);
		sortCDRDefs(cdrDefTable);
		
		// If there's a DTD for this device, get it and add it to the DTDStore.
		List dtdCDRDefs = (List)cdrDefTable.get("dtd");
		if(dtdCDRDefs != null && dtdCDRDefs.size() > 0) {
			try {
				CDRDef dtdCDRDef = (CDRDef)dtdCDRDefs.get(0);
				byte[] dtdDataBytes = containerContext.getCdrarStore().getEntry(dtdCDRDef.getPath()).getEntryBytes();
				DTDStore.addDTD(deviceContext, new ByteArrayInputStream(dtdDataBytes));
				// Initialise the DTD reference for this config table.
				dtd = DTDStore.getDTDObject(deviceContext);
			} catch (CDRArchiveEntryNotFoundException e) {
				IllegalStateException state = new IllegalStateException("Error getting DTD CDRArchiveEntry.");
				state.initCause(e);
				throw state;
			}
		}

		// Expand the CDRDef table and resort
		expandCDRDefTable();
		sortCDRDefs(cdrDefTable);
		
		// Extract the ContentDeliveryUnits and build the tables
		extractContentDeliveryUnits();
	}

	/**
	 * Build the basic CDRDef table from the list.
	 * @param cdrDefsList List of CDRDefs.
	 */
	private void buildCDRDefTable(List cdrDefsList) {
		Iterator iterator = cdrDefsList.iterator();
		
		while(iterator.hasNext()) {
			CDRDef unitDef = (CDRDef)iterator.next();
			
			Vector selectorUnits = (Vector)cdrDefTable.get(unitDef.getSelector());
			
			if(selectorUnits == null) {
				selectorUnits = new Vector();
				cdrDefTable.put(unitDef.getSelector(), selectorUnits);
			}
			
			// Add to the cdres on the cdrDefTable
			selectorUnits.addElement(unitDef);
		}
	}
	
	/**
	 * Expand the CDRDef table.
	 * <p/>
	 * Expand the XmlDef entries to the target elements etc.
	 */
	private void expandCDRDefTable() {
		class ExpansionCDRDefStrategy implements CDRDefStrategy {
			private UAContext deviceContext;
			private ExpansionCDRDefStrategy(UAContext deviceContext) {
				this.deviceContext = deviceContext;
			}
			public void applyStrategy(String elementName, CDRDef cdrDef) {
				// Expand XmlDef entries.
				if(cdrDef.isXmlDef()) {
					String selector = cdrDef.getSelector();
					String[] elements = getDTDElements(cdrDef.getSelector().substring(CDRDef.XML_DEF_PREFIX.length()));
					for(int i = 0; i < elements.length; i++) {
						addObject(elements[i], cdrDef, cdrDefTable);
					}
				}
				
				// Add code to expand other expandable entry types here.
			}
		}
		CDRDefTableIterator tableIterator = new CDRDefTableIterator(new ExpansionCDRDefStrategy(deviceContext));
		tableIterator.iterate();
	}

	/**
	 * Iterate over the table cdres instances and sort the CDRDefs
	 * on each element.  Ordered by specificity.
	 */
	private void sortCDRDefs(Hashtable table) {
		if(!table.isEmpty()) {
			Iterator tableEntrySet = table.entrySet().iterator();
			
			while(tableEntrySet.hasNext()) {
				Map.Entry entry = (Map.Entry)tableEntrySet.next();
				List markupElCDRDefs = (List)entry.getValue();
				CDRDef[] cdrDefs = (CDRDef[])markupElCDRDefs.toArray(new CDRDef[markupElCDRDefs.size()]);
				CDRDefSortComparator sortComparator = new CDRDefSortComparator(deviceContext);

				Arrays.sort(cdrDefs, sortComparator);
				entry.setValue(new Vector(Arrays.asList(cdrDefs)));
			}
		}
	}

	/**
	 * Extract the ContentDeliveryUnits from the CDRDef table and add them to  
	 * their respective tables.
	 */
	private void extractContentDeliveryUnits() {
		CDRDefStrategy cduStrategy = new ContentDeliveryExtractionStrategy();
		CDRDefTableIterator tableIterator = new CDRDefTableIterator(cduStrategy);
		tableIterator.iterate();
	}
	
	/**
	 * Get the DTD elements for specific device context.
	 * @param string DTD spec string e.g. "elcspec:empty"
	 * @return List of element names.
	 */
	private String[] getDTDElements(String string) {
		String tmpString = string.toLowerCase();
		
		if(tmpString.startsWith(ELCSPEC_PREFIX)) {
			tmpString = tmpString.substring(ELCSPEC_PREFIX.length());
			if(tmpString.equals("empty")) {
				return dtd.getEmptyElements();
			} else if(tmpString.equals("not-empty")) {
				return dtd.getNonEmptyElements();
			} else if(tmpString.equals("any")) {
				return dtd.getAnyElements();
			} else if(tmpString.equals("not-any")) {
				return dtd.getNonAnyElements();
			} else if(tmpString.equals("mixed")) {
				return dtd.getMixedElements();
			} else if(tmpString.equals("not-mixed")) {
				return dtd.getNonMixedElements();
			} else if(tmpString.equals("pcdata")) {
				return dtd.getPCDataElements();
			} else if(tmpString.equals("not-pcdata")) {
				return dtd.getNonPCDataElements();
			}
		}
		
		throw new IllegalStateException("Unsupported DTD spec definition [" + string + "]");
	}

	/**
	 * Add the Object for the specified element to the supplied table.
	 * @param element The element to which the Object is to be added.
	 * @param object The Object to be added.
	 * @param table The table to be added to.
	 */
	private void addObject(String element, Object object, Hashtable table) {
		List markupElCDRs = (List)table.get(element);
		
		if(markupElCDRs == null) {
			markupElCDRs = new Vector();
			table.put(element, markupElCDRs);
		}
		
		if(!markupElCDRs.contains(object)) {
			markupElCDRs.add(object);
		}
	}

	/**
	 * Get the TransUnit configure instances, for the named tag, for the useragent 
	 * associated with this table.
	 * @param tag The tag name for which the TransUnits are being requested.
	 * @return TransSet for the specified tag name, or null if none is specified.  
	 */
	public TransSet getTransSet(String tag) {
		return (TransSet)transSetTable.get(tag);
	}
	
	/**
	 * Get the list of {@link CDRDef}s for the specified selector definition.
	 * @param selector "selector" attribute value from the .cdrl file in the .cdrar.
	 * @return List of CDRDef instances, or null.
	 */
	public List getCDRDefs(String selector) {
		return (List)cdrDefTable.get(selector.toLowerCase());
	}

	private static final Vector EMPTY_LIST = new Vector();

	/**
	 * Get a list {@link Object}s from the supplied {@link CDRDef} selector value.
	 * <p/>
	 * Uses {@link org.milyn.cdr.CDRStore#getObject(CDRDef)} to construct the object.
	 * @param selector selector attribute value from the .cdrl file in the .cdrar.
	 * @return List of Object instances.  An empty list is returned where no 
	 * selectors exist.
	 */
	public List getObjects(String selector) {
		Vector objects;
		
		selector = selector.toLowerCase();
		objects = (Vector)objectsTable.get(selector);
		if(objects == null) {
			List unitDefs = (List)cdrDefTable.get(selector);

			if(unitDefs != null && unitDefs.size() > 0) {
				objects = new Vector(unitDefs.size());
				for(int i = 0; i < unitDefs.size(); i++) {
					CDRDef unitDef = (CDRDef)unitDefs.get(i);
					objects.add(containerContext.getCdrarStore().getObject(unitDef));
				}
			} else {
				objects = EMPTY_LIST;
			}
			
			objectsTable.put(selector, objects);
		}
		
		return objects;
	}
	
	/**
	 * Get the AssemblyUnits table for this delivery context.
	 * <p/>
	 * The table is keyed by element name and the values are 
	 * {@link AssemblyUnit} instances.
	 * @return The AssemblyUnits table for this delivery context.
	 */
	public Hashtable getAssemblyUnits() {
		return assemblyUnitTable;
	}
	
	/**
	 * Get the SerializationUnit table for this delivery context.
	 * <p/>
	 * The table is keyed by element name and the values are 
	 * {@link SerializationUnit} instances.
	 * @return The SerializationUnit table for this delivery context.
	 */
	public Hashtable getSerailizationUnits() {
		return serializationUnitTable;
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ContentDeliveryConfig#getDTD()
	 */
	public DTDObjectContainer getDTD() {
		return dtd;
	}
	
	/**
	 * ContentDeliveryUnit extraction strategy.
	 * @author tfennelly
	 */
	private final class ContentDeliveryExtractionStrategy implements CDRDefStrategy {
		
		public void applyStrategy(String elementName, CDRDef cdrDef) {
			ContentDeliveryUnitCreator creator;
			CDRArchiveEntry cdrarEntry;

			try {
				// Try it as a Java class before trying anything else.  This is to
				// accomodate specification of the class in the standard 
				// Java form e.g. java.lang.String Vs java/lang/String.class
				creator = BeanFactory.getContentDeliveryUnitCreator("class");
				if(addCDU(elementName, cdrDef, creator)) {
					// Job done - it's a CDU and we've added it!
					return;
				}
			} catch (UnsupportedContentDeliveryUnitTypeException e) {
				throw new IllegalStateException("No ContentDeliveryUnitCreator configured (IoC) for type 'class' (Java).");
			} catch (Exception e) {
				// Ignore it again - not a CDU - continue on, may be a different type...
			}					
			
			try {
				String type = getExtension(cdrDef.getPath());
				if(type == null || type.trim().equals("")) {
					return;
				}
				creator = BeanFactory.getContentDeliveryUnitCreator(type);
			} catch (UnsupportedContentDeliveryUnitTypeException e) {
				// Just ignore it - something else will use it
				return;
			}					
			
			try {
				addCDU(elementName, cdrDef, creator);
			} catch (InstantiationException e) {
				SmooksLogger.getLog().error("ContentDeliveryUnit creation failure.", e);
			}
		}

		/**
		 * Add a {@link ContentDeliveryUnit} for the specified element and configuration.
		 * @param elementName Element name against which to associate the CDU.
		 * @param cdrDef Configuration.
		 * @param creator CDU Creator class.
		 * @throws InstantiationException 
		 */
		private boolean addCDU(String elementName, CDRDef cdrDef, ContentDeliveryUnitCreator creator) throws InstantiationException {
			ContentDeliveryUnit contentDeliveryUnit;

			// Create the ContentDeliveryUnit.
			contentDeliveryUnit = creator.create(cdrDef, containerContext.getCdrarStore());
			
			if(contentDeliveryUnit instanceof AssemblyUnit) {
				addAssemblyUnit(elementName, (AssemblyUnit)contentDeliveryUnit);				
			} else if(contentDeliveryUnit instanceof TransUnit) {
				addTransUnit(elementName, (TransUnit)contentDeliveryUnit);
			} else if(contentDeliveryUnit instanceof SerializationUnit) {
				addSerializationUnit(elementName, (SerializationUnit)contentDeliveryUnit);
			} else {
				// It's not a CDU type we know of!  Leave for now - whatever's using it
				// can instantiate it itself.
				return false;
			}
			
			return true;
		}

		/**
		 * Get the file extension from the resource path.
		 * @param path Resource path.
		 * @return File extension, or null if the resource path has no file extension.
		 */
		private String getExtension(String path) {
			if(path != null) {
				File resFile = new File(path);
				String resName = resFile.getName();
				
				if(resName != null && !resName.trim().equals("")) {
					int extensionIndex = resName.lastIndexOf('.');
					if(extensionIndex != -1 && (extensionIndex + 1 < resName.length())) {
						return resName.substring(extensionIndex + 1);
					}
				}
			}
			
			return null;
		}

		/**
		 * Add AssemblyUnit.
		 * @param elementName Element to which the AssemblyUnit is to be applied.
		 * @param assemblyUnit AssemblyUnit to be added.
		 */
		private void addAssemblyUnit(String elementName, AssemblyUnit assemblyUnit) {
			Vector elAssemblyUnits = (Vector)assemblyUnitTable.get(elementName);
			
			if(elAssemblyUnits == null) {
				elAssemblyUnits = new Vector();
				assemblyUnitTable.put(elementName, elAssemblyUnits);
			}
			elAssemblyUnits.add(assemblyUnit);
		}

		/**
		 * Add TransUnit.
		 * @param elementName Element to which the TransUnit is to be applied.
		 * @param transUnit TransUnit to be added.
		 */
		private void addTransUnit(String elementName, TransUnit transUnit) {
			TransSet transSet = (TransSet)transSetTable.get(elementName);
			
			if(transSet == null) {
				transSet = new TransSet();
				transSetTable.put(elementName, transSet);
			}
			transSet.addTransUnit(transUnit);
		}

		/**
		 * Add SerializationUnit.
		 * @param elementName Element to which the SerializationUnit is to be applied.
		 * @param serializationUnit SerializationUnit to be added.
		 */
		private void addSerializationUnit(String elementName, SerializationUnit serializationUnit) {
			if(!serializationUnitTable.containsKey(elementName)) {
				serializationUnitTable.put(elementName, serializationUnit);
			}
		}
	}

	/**
	 * Iterate over the CDRDef table applying the constructor 
	 * supplied CDRDefStrategy.
	 * @author tfennelly
	 */
	private class CDRDefTableIterator {
		
		/**
		 * Iteration strategy.
		 */
		private CDRDefStrategy strategy;
		
		/**
		 * Private constructor.
		 * @param strategy Strategy algorithm implementation.
		 */
		private CDRDefTableIterator(CDRDefStrategy strategy) {
			this.strategy = strategy;
		}
		
		/**
		 * Iterate over the table applying the strategy.
		 */
		private void iterate() {
			if(!cdrDefTable.isEmpty()) {
				Hashtable tableClone = (Hashtable)cdrDefTable.clone();
				Iterator iterator = tableClone.entrySet().iterator();				
				
				while(iterator.hasNext()) {
					Map.Entry entry = (Map.Entry)iterator.next();
					String elementName = (String)entry.getKey();
					List cdrDefList = (List)entry.getValue();

					for(int i = 0; i < cdrDefList.size(); i++) {
						CDRDef cdrDef = (CDRDef)cdrDefList.get(i);
						
						strategy.applyStrategy(elementName, cdrDef);
					}
				}
			}			
		}
	}
	
	/**
	 * Unitdef iteration strategy interface.
	 * @author tfennelly
	 */
	private interface CDRDefStrategy {
		/**
		 * Apply the strategy algorithm.
		 * @param elementName The element name the CDRDef
		 * @param unitDef
		 */
		public void applyStrategy(String elementName, CDRDef unitDef);
	}
}
