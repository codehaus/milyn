package org.mylin.ecore;

import java.util.Collection;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.mylin.ecore.ECoreBindingHandler.MessageHanlder;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * {@link ContentHandler} that bind XML SAX events to ECORE model
 * 
 * @author zubairov
 * 
 */
public class ECoreBindingHandler extends DefaultHandler implements
		ContentHandler {

	/**
	 * Interface that will be notified about new messages
	 * 
	 * @author zubairov
	 * 
	 */
	public interface MessageHanlder {

		/**
		 * This method will be called by {@link ECoreBindingHandler} as soon as
		 * handling of one EDI message will be completed
		 * 
		 * @param message
		 */
		public void messageElement(EObject message);

	}

	private Stack<Object> objStack = new Stack<Object>();

	private Stack<EStructuralFeature> featureStack = new Stack<EStructuralFeature>();

	private EClass root;

	private MessageHanlder handler;

	private static final Log log = LogFactory.getLog(ECoreBindingHandler.class);

	/**
	 * Constructor
	 * 
	 * @param pkg
	 */
	public ECoreBindingHandler(EPackage pkg, MessageHanlder handler) {
		root = (EClass) pkg.getEClassifier(pkg.getName().toUpperCase());
		this.handler = handler;
	}

	@Override
	public void startElement(String namespace, String localName, String qName,
			Attributes attrs) throws SAXException {
		if (localName.equalsIgnoreCase(root.getName())) {
			EObject rootInstance = newInstance(root);
			log.debug("Created root class " + root.getName());
			objStack.push(rootInstance);
		} else if (!objStack.isEmpty()) {
			matchAndCreateNewInstance(localName);
		}
	}

	private void matchAndCreateNewInstance(String localName)
			throws SAXException {
		Object obj = objStack.peek();
		if (!(obj instanceof EObject)) {
			throw new SAXException("Unexpected new element " + localName
					+ " where text node is expected " + ". Stack state: "
					+ objStack);
		}
		EObject top = (EObject) obj;
		EList<EStructuralFeature> features = top.eClass()
				.getEStructuralFeatures();
		boolean found = false;
		for (EStructuralFeature feature : features) {
			String xmlTag = EMFHelper.getAnnotationValue(feature, "xmlTag");
			if (xmlTag.equals(localName)) {
				found = true;
				featureStack.push(feature);
				log.debug("Pushing feature " + feature.getName()
						+ " to feature stack");
				if (feature instanceof EReference) {
					// Reference
					EReference ref = (EReference) feature;
					EObject newObject = newInstance(ref.getEReferenceType());
					objStack.push(newObject);
					log.debug("Pushing object of class "
							+ newObject.eClass().getName() + " to object stack");
				} else {
					// Attribute
					EAttribute attr = (EAttribute) feature;
					Object newValue = attr.getDefaultValue();
					objStack.push(newValue);
					log.debug("Pushing value " + newValue + " to object stack");
				}
				break;
			}
		}
		if (!found) {
			throw new SAXException("Can't find any suitable child for tag "
					+ localName + " in " + top.eClass().getName()
					+ ". Stack state: " + objStack);
		}
	}

	@Override
	public void characters(char[] chars, int start, int length)
			throws SAXException {
		if (!objStack.isEmpty()) {
			String content = new String(chars, start, length);
			if (!StringUtils.isBlank(content)) {
				EStructuralFeature topFeature = featureStack.peek();
				if (topFeature instanceof EAttribute) {
					EAttribute attr = (EAttribute) topFeature;
					EDataType type = attr.getEAttributeType();
					EPackage pkg = type.getEPackage();
					EFactory factory = pkg.getEFactoryInstance();
					Object newObject = factory.createFromString(type, content);
					objStack.pop();
					objStack.push(newObject);
					log.debug("Replacing object from top of the stack with "
							+ newObject);
				} else {
					throw new SAXException(
							"Unexpected not-empty content found however feature "
									+ "stack contains a reference on top. Stack: "
									+ featureStack);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void endElement(String namespace, String localName, String qName)
			throws SAXException {
		if (localName.equalsIgnoreCase(root.getName())) {
			log.debug("Reached the end of the mapping");
			objStack.pop();
		} else if (!objStack.isEmpty()) {
			Object subject = objStack.pop();
			EStructuralFeature feature = featureStack.pop();
			if (log.isDebugEnabled()) {
				log.debug("Poping objects from the object stack " + subject
						+ " and from the feature stack " + feature);
			}
			Object obj = objStack.peek();
			if (obj instanceof EObject) {
				EObject top = (EObject) obj;
				if (feature instanceof EAttribute) {
					// For attribute just set it
					top.eSet(feature, subject);
				} else {
					// For reference need to check how many of them
					EReference ref = (EReference) feature;
					if (ref.getUpperBound() == 1) {
						top.eSet(feature, subject);
					} else {
						((Collection<Object>) top.eGet(feature)).add(subject);
					}
				}
			} else {
				throw new SAXException("Unexpected close tag " + localName
						+ ". Stack state: " + objStack);
			}
		}
	}

	/**
	 * Creates a new instance of given {@link EClass}
	 * 
	 * @param clazz
	 * @return
	 */
	private EObject newInstance(EClass clazz) {
		EPackage pkg = clazz.getEPackage();
		EFactory factory = pkg.getEFactoryInstance();
		return factory.create(clazz);
	}
}
