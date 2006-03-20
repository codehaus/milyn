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

package org.milyn.smooks.nav;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.CDRDef;
import org.milyn.container.ContainerContext;
import org.milyn.container.ContainerRequest;
import org.milyn.delivery.trans.AbstractTransUnit;
import org.milyn.dom.DomUtils;
import org.milyn.smooks.assembly.SPutAU;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * snav Transformation Unit
 * @author tfennelly
 */
public class SnavTU extends AbstractTransUnit {

	/**
	 * Logger.
	 */
	private Log logger = LogFactory.getLog(SPutAU.class);
	
	/**
	 * Public Constructor.
	 * @param unitDef
	 */
	public SnavTU(CDRDef unitDef) {
		super(unitDef);
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.trans.TransUnit#visitBefore()
	 */
	public boolean visitBefore() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.milyn.delivery.ElementVisitor#visit(org.w3c.dom.Element, org.milyn.container.ContainerRequest)
	 */
	public void visit(Element snav, ContainerRequest request) {
		Element snavblock = (Element)XmlUtil.getNode(snav, "snavblock");
		
		if(snavblock != null) {
			List slinks = DomUtils.copyNodeList(XmlUtil.getNodeList(snav, "slinks/slink"));
			NodeList anchors = XmlUtil.getNodeList(snav, "snavsrc/a");
			Hashtable patterns = getPatterns(request.getContext());

			if(anchors == null || anchors.getLength() == 0) {
				anchors = XmlUtil.getNodeList(snav, "snavsrc/A");
			}
			
			// Process:
			// 1. Iterate over each of the anchors in snavsrc and:
			// 		a. 	Find the first slink element whose pattern returns a match on the anchor href.
			// 		b. 	Clone the snavblock.
			// 		c. 	Iterate over the cloned snavblock and replace "${XXXX}" attribute values with 
			//    		values from attribute "XXXX" in the matched slink (found in step a).
			// 		d.	Insert the contents of the cloned snavblock before snav.
			// 2. Remove snav.
			if(anchors != null && anchors.getLength() > 0) {
				int anchorCount = anchors.getLength();
				SRepeat[] srepeats = getSRepeats(snav);
				int anchorAddedCount = 0;
				boolean slinksRequired = DomUtils.getBooleanAttrib(snav, "req-slinks");				
				
				for(int i = 0; i < anchorCount; i++) {
					Element anchor = (Element)anchors.item(i);
					String href = anchor.getAttribute("href");
					Element slink = getSlink(slinks, getPattern(href, patterns));
					Element snavblockClone = (Element)snavblock.cloneNode(true);
					Element srepeat = getSRepeat(srepeats, anchorAddedCount);
					boolean addNav = false;
					
					if(slink != null) {
						applySlink(slink, snavblockClone, anchor);
						addNav = true;
					} else if(!slinksRequired) {
						Element snavblockAnchor = (Element)XmlUtil.getNode(snavblockClone, "//a");
						if(snavblockAnchor == null) {
							snavblockAnchor = (Element)XmlUtil.getNode(snavblockClone, "//A");
						}
						if(snavblockAnchor != null) {
							snavblockAnchor.getParentNode().replaceChild(anchor, snavblockAnchor);
						}
						addNav = true;
					}
					if(addNav) {
						if(srepeat != null) {
							DomUtils.insertBefore(srepeat.getChildNodes(), snav);
						}
						DomUtils.insertBefore(snavblockClone.getChildNodes(), snav);
						anchorAddedCount++;
					}
				}
			} else {
				logger.warn("No anchors in snavsrc element.");
			}
		} else {
			logger.warn("No snavblock element.");
		}
		snav.getParentNode().removeChild(snav);
	}

	/**
	 * Get the srepeat for the specified index.
	 * @param srepeats The srepeats list for this snav.
	 * @param index The index.
	 * @return The srepeat for the specified index, or null if none specified.
	 */
	private Element getSRepeat(SRepeat[] srepeats, int index) {
		if(index > 0) {
			for(int i = 0; i < srepeats.length; i++) {
				SRepeat srepeat = srepeats[i];
				int position = index + 1;
				int mod = position % srepeat.frequency;
				
				if(mod == 1 || srepeat.frequency == 1) {
					return srepeat.getSRepeat();
				} 
			}
		}
		return null;
	}

	/**
	 * Get the list of srepeat elements from the snav.
	 * @param snav The snav element.
	 * @return The list of srepeats, empty if there are none.
	 */
	private SRepeat[] getSRepeats(Element snav) {
		Vector srepeatsVec = new Vector();
		NodeList srepeatNodes = XmlUtil.getNodeList(snav, "srepeat");
		SRepeat[] srepeats = null;
		
		if(srepeatNodes != null) {
			int srepeatCount = srepeatNodes.getLength();
		
			for(int i = 0; i < srepeatCount; i++) {
				srepeatsVec.add(new SRepeat((Element)srepeatNodes.item(i)));
			}
		}
		srepeats = new SRepeat[srepeatsVec.size()];
		if(srepeats.length > 0) {
			srepeatsVec.toArray(srepeats);
			Arrays.sort(srepeats, new SRepeatSortComparator());
		}
		
		return srepeats;
	}

	/**
	 * Iterate over the cloned snavblock and replace "${XXXX}" attribute and text 
	 * values with values from attribute "XXXX" in the matched slink (found in step a).
	 * @param slink The matched slink to be used in the attribute replacement.
	 * @param snavblockClone The cloned snavblock to which the slink will be applied.
	 * @param anchor Nav href.
	 */
	private void applySlink(Element slink, Element snavblockClone, Element anchor) {
		// If it's an anchor, copy the href.
		if(snavblockClone.getTagName().equalsIgnoreCase("a")) {
			String href = anchor.getAttribute("href");
			if(href != null) {
				snavblockClone.setAttribute("href", href);
			}
		}
		
		// Iterate over the element attributes
		NamedNodeMap attributes = snavblockClone.getAttributes();
		for(int i = 0; i < attributes.getLength(); i++) {
			Attr attrib = (Attr)attributes.item(i);
			String attribVal;
			if(applySlink(slink, attrib, anchor)) {
				attributes.removeNamedItem(attrib.getName());
				i--;
			}
		}
		
		// Iterate over the element child elements and text and make recursive call.
		NodeList children = snavblockClone.getChildNodes();
		int childCount = children.getLength();
		for(int i = 0; i < childCount; i++) {
			Node child = children.item(i);
			short nodeType = child.getNodeType();
			if(nodeType == Node.ELEMENT_NODE) {
				applySlink(slink, (Element)child, anchor);
			} else if(nodeType == Node.TEXT_NODE) {
				applySlink(slink, child, anchor);
			}
		}		
	}

	/**
	 * Apply the sink to the supplied node.
	 * <p/>
	 * If the slink doesn't define the ${XXXX} attribute, try the source anchor 
	 * for the atribute.  
	 * @param slink slink to apply.
	 * @param node Node to apply slink to.
	 * @param anchor Nav href.
	 * @return True if the node should be removed, otherwise false.
	 */
	private boolean applySlink(Element slink, Node node, Element anchor) {
		String attribVal = node.getNodeValue();
		
		if(attribVal.startsWith("${") && attribVal.endsWith("}") && attribVal.length() > 3) {
			String slinkAttrib = attribVal.substring(2, attribVal.length() - 1);
			String newAttribVal = slink.getAttribute(slinkAttrib);

			if(newAttribVal == null || newAttribVal.equals("")) {
				newAttribVal = anchor.getAttribute(slinkAttrib);
			}			
			if(newAttribVal != null && !newAttribVal.equals("")) {
				node.setNodeValue(newAttribVal);
			} else if(node.getNodeType() == Node.ATTRIBUTE_NODE) {
				// Remove attributes for which no ${XXXX} value was found on 
				// the slink or source anchor.
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Get the slink match for supplied pattern.
	 * @param slinks slink element to search.
	 * @param pattern Pattern to be used in matching processes.
	 * @return slink Element instance, or null if no match found.
	 */
	private Element getSlink(List slinks, Pattern pattern) {
		if(slinks != null && pattern != null) {
			if(!slinks.isEmpty()) {
				for(int i = 0; i < slinks.size(); i++) {
					Element slink = (Element)slinks.get(i);
					Matcher matcher = pattern.matcher(slink.getAttribute("pattern"));
					if(matcher.matches()) {
						return slink;
					}
				}
			}
		}
		
		return null;
	}

	private static final String SLINK_CTXT_LOOKUP = SnavTU.class.getName() + "slinkPatterns";
	/**
	 * Get the regexp patterns from the container context.
	 * <p/>
	 * Creates and adds the table to the context if not already present.
	 * @param context Container context.
	 * @return Patterns table.
	 */
	private Hashtable getPatterns(ContainerContext context) {
		Hashtable patterns = (Hashtable)context.getAttribute(SLINK_CTXT_LOOKUP);
		
		if(patterns == null) {
			synchronized(context) {
				patterns = (Hashtable)context.getAttribute(SLINK_CTXT_LOOKUP);
				if(patterns == null) {
					patterns = new Hashtable();
					context.setAttribute(SLINK_CTXT_LOOKUP, patterns);
				}
			}
		}
		
		return patterns;
	}
	
	/**
	 * Get the regexp pattern from the supplied patterns table.
	 * <p/>
	 * Creates (compiles) and adds the Pattern instance to the patterns table if not already 
	 * present.
	 * @param patternString Patterns string.
	 * @param patterns Patterns lookup table.
	 * @return Compiled Pattern instance for the supplied patternString.
	 */
	private Pattern getPattern(String patternString, Hashtable patterns) {
		Pattern pattern = (Pattern)patterns.get(patternString);
		
		if(pattern == null) {
			synchronized(patterns) {
				pattern = (Pattern)patterns.get(patternString);
				if(pattern == null) {
					pattern = Pattern.compile(patternString);
					patterns.put(patternString, pattern);
				}
			}
		}
		
		return pattern;
	}

	/**
	 * srepeat container class.
	 * <p/>
	 * Just saves on reading and parsing the frequency attribute.
	 * @author tfennelly
	 */
	private class SRepeat {
		int frequency = 1;
		Element srepeat;
		
		private SRepeat(Element srepeat) {
			this.srepeat = srepeat;
			try {
				frequency = Integer.parseInt(srepeat.getAttribute("freq"));
				frequency = Math.max(frequency, 1);
			} catch(Exception e) {
				frequency = 1;
			}
			removeComments(srepeat);
		}
		
		private Element getSRepeat() {
			return (Element)srepeat.cloneNode(true);
		}
		
		private void removeComments(Element element) {
			NodeList children = element.getChildNodes();
			int childCount = children.getLength();
			Document ownerDoc = element.getOwnerDocument();
			
			for(int i = 0; i < childCount; i++) {
				Node child = children.item(i);
				int nodeType = child.getNodeType();
				if(nodeType == Node.COMMENT_NODE) {
					Comment comment = (Comment)child;
					element.replaceChild(ownerDoc.createTextNode(comment.getData()), comment);
				} else if(nodeType == Node.ELEMENT_NODE) {
					removeComments((Element)child);
				}
			}
		}
	}
	
	/**
	 * Sort the srepeat elements, least frequent first.
	 * @author tfennelly
	 */
	private class SRepeatSortComparator implements Comparator {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			return ((SRepeat)o2).frequency - ((SRepeat)o1).frequency;
		}		
	}
}



