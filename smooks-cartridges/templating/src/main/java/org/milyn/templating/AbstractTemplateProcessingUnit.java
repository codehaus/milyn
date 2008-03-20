package org.milyn.templating;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.SmooksException;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.Filter;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.delivery.dom.serialize.ContextObjectSerializationUnit;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.nio.charset.Charset;

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

    private Log logger = LogFactory.getLog(getClass());
    private static boolean legactVisitBeforeParamWarn = false;

    protected enum Action {
        REPLACE,
        ADDTO,
        INSERT_BEFORE,
        INSERT_AFTER,
        BIND_TO,
    }

    private boolean applyTemplateBefore;

    private Action action;

    private Charset encoding;

    private String bindId;

    public void setConfiguration(SmooksResourceConfiguration config) throws SmooksConfigurationException {
        try {
            loadTemplate(config);
        } catch (Exception e) {
            throw new SmooksConfigurationException("Error loading Templating resource: " + config, e);
        }
        String visitBefore = config.getStringParameter("visitBefore");
        if(visitBefore != null) {
            if(!legactVisitBeforeParamWarn) {
                logger.warn("Templating <param> 'visitBefore' deprecated.  Use 'applyTemplateBefore'.");
                legactVisitBeforeParamWarn = true;
            }
            this.applyTemplateBefore = visitBefore.equalsIgnoreCase("true");
        }
    }
	
	protected abstract void loadTemplate(SmooksResourceConfiguration config) throws IOException, TransformerConfigurationException;

    @ConfigParam(defaultVal = "false")
    public void setApplyTemplateBefore(boolean applyTemplateBefore) {
        this.applyTemplateBefore = applyTemplateBefore;
    }

    public boolean applyTemplateBefore() {
        return applyTemplateBefore;
    }

    @ConfigParam(name = "action", defaultVal = "replace", choice = {"replace", "addto", "insertbefore", "insertafter", "bindto"}, decoder = ActionDecoder.class)
    public void setAction(Action action) {
        this.action = action;
    }

    protected Action getAction() {
        return action;
    }

    public Charset getEncoding() {
        return encoding;
    }

    @ConfigParam(defaultVal = "UTF-8")
    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public String getBindId() {
        return bindId;
    }

    @ConfigParam(use = ConfigParam.Use.OPTIONAL)
    public void setBindId(String bindId) {
        this.bindId = bindId;
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
        } else if(action == Action.BIND_TO) {
            if(bindId == null) {
                throw new SmooksConfigurationException("'bindto' templating action configurations must also specify a 'bindId' configuration for the Id under which the result is bound to the ExecutionContext");
            } else if(node.getNodeType() == Node.TEXT_NODE) {
                ExecutionContext context = Filter.getCurrentExecutionContext();
                context.setAttribute(bindId, node.getTextContent());
            } else if(node.getNodeType() == Node.ELEMENT_NODE && ContextObjectSerializationUnit.isContextObjectElement((Element) node)) {
                String contextKey = ContextObjectSerializationUnit.getContextKey((Element) node);
                ExecutionContext context = Filter.getCurrentExecutionContext();
                
                context.setAttribute(bindId, context.getAttribute(contextKey));
            } else {
                throw new SmooksException("Unsupported 'bindTo' templating action.  The bind data must be attached to a DOM Text node, or already bound to a <context-object> element.");
            }
        } else if(action == Action.REPLACE) {
            // Don't perform any "replace" actions here!
        }
	}

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException {
        if(applyTemplateBefore) {
            visit(element, executionContext);
        }
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException {
        if(!applyTemplateBefore) {
            visit(element, executionContext);
        }
    }

    protected abstract void visit(Element element, ExecutionContext executionContext) throws SmooksException;

    public static class ActionDecoder implements DataDecoder {
        public Object decode(String data) throws DataDecodeException {
            if("addto".equals(data)) {
                return Action.ADDTO;
            } else if("insertbefore".equals(data)) {
                return Action.INSERT_BEFORE;
            } else if("insertafter".equals(data)) {
                return Action.INSERT_AFTER;
            } else if("bindto".equals(data)) {
                return Action.BIND_TO;
            } else {
                return Action.REPLACE;
            }
        }
    }
}
