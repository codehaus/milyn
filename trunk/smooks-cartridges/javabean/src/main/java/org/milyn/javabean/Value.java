package org.milyn.javabean;

import org.milyn.assertion.AssertArgument;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.VisitorAppender;
import org.milyn.delivery.VisitorConfigMap;
import org.milyn.javabean.ext.SelectorPropertyResolver;

/**
 *
 * @author maurice_zeijen
 *
 */
public class Value implements VisitorAppender {

	private String beanId;

	private String dataSelector;

	private String targetNamespace;

	private String defaultValue;

	private DataDecoder decoder;

	public Value(String beanId, String data) {
		AssertArgument.isNotNullAndNotEmpty(beanId, "beanId");
		AssertArgument.isNotNullAndNotEmpty(data, "dataSelector");

		this.beanId = beanId;
		this.dataSelector = data;
	}

	public Value setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;

		return this;
	}

	public Value setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;

		return this;
	}


	public Value setDecoder(DataDecoder dataDecoder) {
		this.decoder = dataDecoder;

		return this;
	}

	public void addVisitors(VisitorConfigMap visitorMap) {

		ValueBinder binder = new ValueBinder(beanId);
		SmooksResourceConfiguration populatorConfig = new SmooksResourceConfiguration(dataSelector);

		SelectorPropertyResolver.resolveSelectorTokens(populatorConfig);

		binder.setDecoder(decoder);
		binder.setDefaultValue(defaultValue);
		binder.setValueAttributeName(populatorConfig.getStringParameter(BeanInstancePopulator.VALUE_ATTRIBUTE_NAME));

		visitorMap.addVisitor(binder, populatorConfig.getSelector(), targetNamespace, true);
	}

}
