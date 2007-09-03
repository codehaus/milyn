package org.milyn.templating;

import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.xml.DomUtils;
import org.milyn.container.ExecutionContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Abstract template processing unit.
 * <p/>
 * Defines abstract methods for loading the template in question, as well as convienience methods for
 * processing the template action against the templating result (replace, addto, insertbefore and insertafter).
 * <p/>
 * See implementations.
 * @author tfennelly
 */
public abstract class AbstractTemplateProcessingUnit implements DOMElementVisitor {

    protected enum Action {
        REPLACE,
        ADDTO,
        INSERT_BEFORE,
        INSERT_AFTER,
    }
    
    private Log logger;
    private boolean visitBefore = false;
    private Action action = Action.REPLACE;

    public void setConfiguration(SmooksResourceConfiguration config) throws SmooksConfigurationException {
        logger = LogFactory.getLog(getClass());
        visitBefore = config.getBoolParameter("visitBefore", false);
        setAction(config);
        try {
            loadTemplate(config);
        } catch (IOException e) {
            logger.error("Error loading Templating resource: " + config, e);
            throw new SmooksConfigurationException("Unable to read template.", e);
        } catch (TransformerConfigurationException e) {
            logger.error("Error loading Templating resource: " + config, e);
            throw new SmooksConfigurationException("Unable to configure template engine.", e);
        }
    }
	
	protected abstract void loadTemplate(SmooksResourceConfiguration config) throws IOException, TransformerConfigurationException;
	
    private void setAction(SmooksResourceConfiguration config) {
        String actionParam = config.getStringParameter("action");
        
        if("addto".equals(actionParam)) {
            action = Action.ADDTO;
        } else if("insertbefore".equals(actionParam)) {
            action = Action.INSERT_BEFORE;
        } else if("insertafter".equals(actionParam)) {
            action = Action.INSERT_AFTER;
        } else {
            action = Action.REPLACE;
        }
    }

    protected Action getAction() {
        return action;
    }

    protected void processTemplateAction(Element element, Node templatingResult) {
		// REPLACE needs to be handled explicitly...
		if(action == Action.REPLACE) {
            DomUtils.replaceNode(templatingResult, element);
        } else {
    		_processTemplateAction(element, templatingResult, action);
        }
	}

	protected void processTemplateAction(Element element, NodeList templatingResultNodeList) {
		// If we're at the root element
		if(element.getParentNode() instanceof Document) {
			int count = templatingResultNodeList.getLength();

			// Iterate over the NodeList and filter the action using the
			// first element node we encounter as the transformation result...
			for(int i = 0; i < count; i++) {
				Node node = templatingResultNodeList.item(0);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					processTemplateAction(element, node);
					break;
				}
			}
		} else if(action == Action.REPLACE) {
			// When we're not at the root element, REPLACE needs to be handled explicitly
			// by performing a series of insert-befores, followed by a remove of the
			// target element...
			processTemplateAction(element, templatingResultNodeList, Action.INSERT_BEFORE);
			element.getParentNode().removeChild(element);
        } else {
			processTemplateAction(element, templatingResultNodeList, action);
        }
	}

	private void processTemplateAction(Element element, NodeList templatingResultNodeList, Action action) {
		int count = templatingResultNodeList.getLength();
		
		// Iterate over the NodeList and filter each Node against the action.
		for(int i = 0; i < count; i++) {
			// We iterate over the list in this way because the nodes are auto removed from the
			// the list as they are added/inserted elsewhere.
			_processTemplateAction(element, templatingResultNodeList.item(0), action);
		}
	}

	private void _processTemplateAction(Element element, Node node, Action action) {
        Node parent = element.getParentNode();

        // Can't insert before or after the root element...
        if(parent instanceof Document && (action == Action.INSERT_BEFORE || action == Action.INSERT_AFTER)) {
            logger.warn("Insert before/after root element not allowed.  Consider using the replace action!!");
            return;
        }
        
        if(action == Action.ADDTO) {
            element.appendChild(node);
        } else if(action == Action.INSERT_BEFORE) {
            DomUtils.insertBefore(node, element);
        } else if(action == Action.INSERT_AFTER) {
            Node nextSibling = element.getNextSibling();
            
            if(nextSibling == null) {
                // "element" is the last child of "parent" so just add to "parent".
                parent.appendChild(node);
            } else {
                // insert before the "nextSibling" - Node doesn't have an "insertAfter" operation!
                DomUtils.insertBefore(node, nextSibling);
            }
        } else if(action == Action.REPLACE) {
            // Don't perform any "replace" actions here!
        }
	}

    public void visitBefore(Element element, ExecutionContext executionContext) {
        if(visitBefore) {
            visit(element, executionContext);
        }
    }

    public void visitAfter(Element element, ExecutionContext executionContext) {
        if(!visitBefore) {
            visit(element, executionContext);
        }
    }

    protected abstract void visit(Element element, ExecutionContext executionContext);
}
