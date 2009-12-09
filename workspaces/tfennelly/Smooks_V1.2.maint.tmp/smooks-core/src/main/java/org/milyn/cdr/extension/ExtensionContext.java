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
package org.milyn.cdr.extension;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.XMLConfigDigester;
import org.milyn.container.ExecutionContext;
import org.milyn.expression.ExpressionEvaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Context object used by Smooks configuration extension visitors.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ExtensionContext {

    private static final String EXEC_CONTEXT_KEY = ExtensionContext.class.getName() + "#EXEC_CONTEXT_KEY";

    private XMLConfigDigester xmlConfigDigester;
    private String defaultSelector;
    private String defaultNamespace;
    private String defaultProfile;
    private ExpressionEvaluator defaultConditionEvaluator;
    
    private Stack<SmooksResourceConfiguration> resourceStack = new Stack<SmooksResourceConfiguration>();
    private List<SmooksResourceConfiguration> resources = new ArrayList<SmooksResourceConfiguration>();

    /**
     * Public constructor.
     * @param xmlConfigDigester The base XMLConfigDigester.
     * @param defaultSelector The default selector.
     * @param defaultNamespace The default namespace.
     * @param defaultProfile The default profile.
     * @param defaultConditionEvaluator The default condition evaluator.
     */
    public ExtensionContext(XMLConfigDigester xmlConfigDigester, String defaultSelector, String defaultNamespace, String defaultProfile, ExpressionEvaluator defaultConditionEvaluator) {
        this.xmlConfigDigester = xmlConfigDigester;
        this.defaultSelector = defaultSelector;
        this.defaultNamespace = defaultNamespace;
        this.defaultProfile = defaultProfile;
        this.defaultConditionEvaluator = defaultConditionEvaluator;
    }

    /**
     * Set the {@link ExtensionContext} on the {@link org.milyn.container.ExecutionContext}.
     * @param extensionContext Extension Context.
     * @param executionContext Execution Context.
     */
    public static void setExtensionContext(ExtensionContext extensionContext, ExecutionContext executionContext) {
        executionContext.setAttribute(EXEC_CONTEXT_KEY, extensionContext);
    }

    /**
     * Get the {@link ExtensionContext} from the {@link org.milyn.container.ExecutionContext}.
     * @param executionContext Execution Context.
     * @return Extension Context.
     */
    public static ExtensionContext getExtensionContext(ExecutionContext executionContext) {
        return (ExtensionContext) executionContext.getAttribute(EXEC_CONTEXT_KEY);
    }

    /**
     * Add a resource configuration to the list of resources for this Extension Context.
     * <p/>
     * The resource gets added to the {@link #getResourceStack() resourceStack} and the
     * basic list of {@link #getResources() resources}.
     *
     * @param resource The resource to be added.
     */
    public void addResource(SmooksResourceConfiguration resource) {
        resourceStack.push(resource);
        resources.add(resource);
    }

    /**
     * Get the resource stack.
     * @return The resource stack.
     * @see #addResource(org.milyn.cdr.SmooksResourceConfiguration) 
     */
    public Stack<SmooksResourceConfiguration> getResourceStack() {
        return resourceStack;
    }

    /**
     * Get the resource list.
     * @return The resource list.
     * @see #addResource(org.milyn.cdr.SmooksResourceConfiguration)
     */
    public List<SmooksResourceConfiguration> getResources() {
        return resources;
    }

    public XMLConfigDigester getXmlConfigDigester() {
        return xmlConfigDigester;
    }

    public String getDefaultSelector() {
        return defaultSelector;
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    public String getDefaultProfile() {
        return defaultProfile;
    }

    public ExpressionEvaluator getDefaultConditionEvaluator() {
        return defaultConditionEvaluator;
    }

	public SmooksResourceConfiguration getResourceByName(String name) {
		for(int i = resourceStack.size() - 1; i >= 0; i--) {
			SmooksResourceConfiguration config = resourceStack.get(i);
			String resourceName = config.getResource();
			if(name.equals(resourceName)) {
				return config;
			}
		}
		
		return null;
	}
}
