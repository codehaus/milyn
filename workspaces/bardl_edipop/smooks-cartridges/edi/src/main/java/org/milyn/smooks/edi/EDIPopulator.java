package org.milyn.smooks.edi;

import org.milyn.cdr.Parameter;
import org.milyn.cdr.ParameterAccessor;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.annotation.AppContext;
import org.milyn.cdr.annotation.Config;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.container.ApplicationContext;
import org.milyn.delivery.ConfigurationExpander;
import org.milyn.delivery.Filter;
import org.milyn.delivery.annotation.Initialize;
import org.milyn.delivery.dom.VisitPhase;
import org.milyn.edisax.EdifactModel;
import org.milyn.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * EDIPopulator is used to create and bind {@link org.milyn.smooks.edi.EDIBindingGroupPopulator} and
 * {@link org.milyn.smooks.edi.EDIBindingGroupHandler} to the SmooksResourceConfiguration.
 * @see org.milyn.smooks.edi.EDIBindingGroupPopulator
 * @see org.milyn.smooks.edi.EDIBindingGroupHandler
 * @author bardl 
 */
public class EDIPopulator implements ConfigurationExpander {

    @ConfigParam(use= ConfigParam.Use.OPTIONAL)
    private String ediPath;

    @ConfigParam(use= ConfigParam.Use.OPTIONAL)
    private String binding;

    @ConfigParam(use= ConfigParam.Use.OPTIONAL)
    private String ediModel;

    @ConfigParam(use= ConfigParam.Use.OPTIONAL)
    private String modelId;

    @ConfigParam(use= ConfigParam.Use.OPTIONAL)
    private String newSegment;

    @AppContext
    private ApplicationContext appContext;

    @Config
    private SmooksResourceConfiguration config;

    private static EdifactModel edifactModel;

    @Initialize
    public void intitialize() throws SmooksConfigurationException {
        SmooksResourceConfiguration config = new SmooksResourceConfiguration(ParameterAccessor.GLOBAL_PARAMETERS);
        config.setParameter(Filter.DEFAULT_SERIALIZATION_ON, Boolean.toString(false));
        appContext.getStore().registerResource(config);                     
    }

    public List<SmooksResourceConfiguration> expandConfigurations() throws SmooksConfigurationException {
        List<SmooksResourceConfiguration> resources = new ArrayList<SmooksResourceConfiguration>();

        buildSegmentBatch(resources);
        buildBindingConfigs(resources);

        return resources;
    }

    private void buildSegmentBatch(List<SmooksResourceConfiguration> resources) {

        SmooksResourceConfiguration configuration = new SmooksResourceConfiguration(config.getSelector(), EDIBindingGroupHandler.class.getName());
        configuration.setParameter("bindingGroupId", getSegmentBatchId());
        if (newSegment != null) {
            configuration.setParameter("newSegment", newSegment);
        }

        resources.add(configuration);
    }

    private String getSegmentBatchId() {
        String result = config.getSelector();

        //Append newSegment to handle multiple SegmentBatches with same selector.
        if (newSegment != null) {
            result += newSegment;
        }

        return result;
    }

    private void buildBindingConfigs(List<SmooksResourceConfiguration> resources) {
        Parameter bindingsParam = config.getParameter("bindings");

        if (bindingsParam != null) {
            Element bindingsParamElement = bindingsParam.getXml();

            if(bindingsParamElement != null) {
                NodeList bindings = bindingsParamElement.getElementsByTagName("binding");

                try {
                    for (int i = 0; bindings != null && i < bindings.getLength(); i++) {
                    	Element node = (Element)bindings.item(i);

                    	resources.add(buildInstancePopulatorConfig(node));

                    }
                } catch (IOException e) {
                    throw new SmooksConfigurationException("Failed to read binding configuration for " + config, e);
                }
            } else {
                //logger.error("Sorry, the Javabean populator bindings must be available as XML DOM.  Please configure using XML.");
            }
        }
    }

    private SmooksResourceConfiguration buildInstancePopulatorConfig(Element bindingConfig) throws IOException, SmooksConfigurationException {
        SmooksResourceConfiguration resourceConfig;
        String selector;

        // Make sure there's both 'selector' and 'property' attributes...
        selector = getSelectorAttr(bindingConfig);
        String ediPath = getEdiPathAttr(bindingConfig);
        String selectorProperty = getSelectorProperty(selector);

        // Construct the configuraton...
        resourceConfig = new SmooksResourceConfiguration(selectorProperty, EDIBindingGroupPopulator.class.getName());
        resourceConfig.setParameter(VisitPhase.class.getSimpleName(), config.getStringParameter(VisitPhase.class.getSimpleName(), VisitPhase.PROCESSING.toString()));
        resourceConfig.setParameter("ediPath", ediPath);
        resourceConfig.setParameter("modelId", modelId);
        resourceConfig.setParameter("bindingGroupId", getSegmentBatchId());

        resourceConfig.setTargetProfile(config.getTargetProfile());

        return resourceConfig;
    }

    private String getEdiPathAttr(Element bindingConfig) {
        String selector = DomUtils.getAttributeValue(bindingConfig, "ediPath");

        if (selector == null) {
            selector = config.getSelector();
        }

        return selector;
    }

    private String getSelectorProperty(String selector) {
        StringBuffer selectorProp = new StringBuffer();
        String[] selectorTokens = SmooksResourceConfiguration.parseSelector(selector);

        for (String selectorToken : selectorTokens) {
            if (!selectorToken.trim().startsWith("@")) {
                selectorProp.append(selectorToken).append(" ");
            }
        }

        return selectorProp.toString().trim();
    }

    private String getSelectorAttr(Element bindingConfig) {
    	String selector = DomUtils.getAttributeValue(bindingConfig, "selector");

        if (selector == null) {
            selector = config.getSelector();
        }

        return selector;
    }
}
