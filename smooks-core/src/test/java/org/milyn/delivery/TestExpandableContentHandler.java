package org.milyn.delivery;

import org.junit.Test;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.delivery.dom.serialize.DefaultSerializationUnit;
import org.milyn.delivery.dom.DOMElementVisitor;
import org.milyn.container.ExecutionContext;
import org.w3c.dom.Element;

import java.util.List;
import java.util.ArrayList;

import junit.framework.JUnit4TestAdapter;

/**
 * @author
 */
public class TestExpandableContentHandler implements DOMElementVisitor, ConfigurationExpander {

    public void setConfiguration(SmooksResourceConfiguration resourceConfig) throws SmooksConfigurationException {
    }

    public List<SmooksResourceConfiguration> expandConfigurations() {

        List<SmooksResourceConfiguration> expansionConfigs = new ArrayList<SmooksResourceConfiguration>();

        expansionConfigs.add(new SmooksResourceConfiguration("a", Assembly1.class.getName()));
        expansionConfigs.add(new SmooksResourceConfiguration("b", Processing1.class.getName()));        
        expansionConfigs.add(new SmooksResourceConfiguration("c", DefaultSerializationUnit.class.getName()));

        return expansionConfigs;
    }

    public void visitBefore(Element element, ExecutionContext executionContext) {
    }

    public void visitAfter(Element element, ExecutionContext executionContext) {
    }
    
    @Test
    public void test_dummy()
    {
    	
    }
    
    public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter( TestExpandableContentHandler.class );
	}
    
}
