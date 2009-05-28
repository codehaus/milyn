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

import org.milyn.assertion.AssertArgument;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.Writer;

/**
 * Element details as described by the SAX even model API.
 * <p/>
 * {@link org.milyn.delivery.sax.SAXVisitor} implementations will be passed
 * an instance of this class for each of the event methods of
 * {@link org.milyn.delivery.sax.SAXVisitor} implementations.
 * <p/>
 * <h3 id="element_cache_object">Element Cache Object</h3>
 * This class supports the concept of a "cache" object which can be get and set through
 * the {@link #getCache()} and {@link #setCache(Object)} methods.  The cache object can be used by
 * {@link org.milyn.delivery.sax.SAXVisitor} implementations to store information
 * between calls to the {@link org.milyn.delivery.sax.SAXVisitor} event methods.
 * <p/>
 * Obviously we could have implemented this cache as a straightforward {@link java.util.Map},
 * but that forces you to do silly things in situations where you wish to (for example)
 * store a {@link java.util.List}, or a single Object reference of some other type.
 * <p/>
 * <h3 id="element-writing">Element Writing/Serialization</h3>
 * Each SAXElement instance has a {@link Writer writer} set on it.
 * {@link org.milyn.delivery.sax.SAXVisitor} implementations can take care of
 * serializing the elements at which they are targeted themselves.  Alternatively, they
 * can use the {@link org.milyn.delivery.sax.WriterUtil} class.
 * <p/>
 * {@link org.milyn.delivery.sax.SAXVisitor} implementations can also control the serialization
 * of their "child elements" by {@link #setWriter(java.io.Writer, SAXVisitor) setting the writter}
 * on the SAXElement instance they receive.  This works because Smooks passes the
 * writer instance that's set on a SAXElement instance to all of the SAXElement
 * instances created for child elements.
 * <p/>
 * Only one {@link SAXVisitor} can have access to the {@link java.io.Writer writer}
 * for any individual {@link SAXElement}.  The first visitor to request access to
 * the writer via the {@link SAXElement#getWriter(SAXVisitor)} method "owns" the writer
 * for that element.  Any other visitors requesting access to get or change the writer
 * will result in a {@link SAXWriterAccessException} being thrown.  In this situation,
 * you need to restructure the offending Smooks configuration and eliminate one of the
 * visitors attempting to gain access to the writer.  If developing a new visitor,
 * you probably need to change the visitor to also implement the {@link SAXVisitBefore}
 * interface and use that event method to acquire ownership of the element writer
 * through a call to {@link SAXElement#getWriter(SAXVisitor)}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SAXElement {

    private QName name;
    private Attributes attributes;
    private Object cache;
    private SAXElement parent;
    private Writer writer;

    /**
     * Public constructor.
     *
     * @param namespaceURI The Namespace URI, or the empty string if the
     *                     element has no Namespace URI or if Namespace
     *                     processing is not being performed.
     * @param localName    The local name (without prefix), or the
     *                     empty string if Namespace processing is not being
     *                     performed.
     * @param qName        The qualified name (with prefix), or the
     *                     empty string if qualified names are not available.
     * @param attributes   The attributes attached to the element.  If
     *                     there are no attributes, it shall be an empty
     *                     Attributes object.
     * @param parent       Parent element, or null if the element is the document root element.
     */
    public SAXElement(String namespaceURI, String localName, String qName, Attributes attributes, SAXElement parent) {
        this.name = toQName(namespaceURI, localName, qName);
        this.attributes = copyAttributes(attributes);
        this.parent = parent;
    }

    /**
     * Public constructor.
     *
     * @param name       The element {@link QName}.
     * @param attributes The attributes attached to the element.  If
     *                   there are no attributes, it shall be an empty
     *                   Attributes object.
     * @param parent     Parent element, or null if the element is the document root element.
     */
    public SAXElement(QName name, Attributes attributes, SAXElement parent) {
        this.name = name;
        this.attributes = copyAttributes(attributes);
        this.parent = parent;
    }

    /**
     * Create a {@link QName} instance from the supplied element naming parameters.
     *
     * @param namespaceURI The Namespace URI, or the empty string if the
     *                     element has no Namespace URI or if Namespace
     *                     processing is not being performed.
     * @param localName    The local name (without prefix), or the
     *                     empty string if Namespace processing is not being
     *                     performed.
     * @param qName        The qualified name (with prefix), or the
     *                     empty string if qualified names are not available.
     * @return A {@link QName} instance representing the element named by the supplied parameters.
     */
    public static QName toQName(String namespaceURI, String localName, String qName) {
        if (namespaceURI != null) {
            int colonIndex;

            if (namespaceURI.length() != 0 && qName != null && (colonIndex = qName.indexOf(':')) != -1) {
                String prefix = qName.substring(0, colonIndex);
                String qNameLocalName = qName.substring(colonIndex + 1);

                return new QName(namespaceURI.intern(), qNameLocalName, prefix);
            } else if (localName != null && localName.length() != 0) {
                return new QName(namespaceURI, localName);
            } else if (qName != null && qName.length() != 0) {
                return new QName(namespaceURI, qName);
            } else {
                thowInvalidNameException(namespaceURI, localName, qName);
            }
        } else if (localName != null && localName.length() != 0) {
            return new QName(localName);
        } else {
            thowInvalidNameException(namespaceURI, localName, qName);
        }

        return null;
    }

    /**
     * Create a copy of the attributes.
     * <p/>
     * This needs to be done because some SAX parsers reuse the same {@link Attributes} instance
     * across SAX events.
     *
     * @param attributes The attributes to copy.
     * @return The new {@link Attributes} instance with a copy of the attributes.
     */
    private Attributes copyAttributes(Attributes attributes) {
        AttributesImpl attributesCopy = new AttributesImpl();
        attributesCopy.setAttributes(attributes);
        return attributesCopy;
    }

    private static void thowInvalidNameException(String namespaceURI, String localName, String qName) {
        throw new IllegalArgumentException("Invalid SAXELement name paramaters: namespaceURI='" + namespaceURI + "', localName='" + localName + "', qName='" + qName + "'.");
    }

    /**
     * Get the writer to which this element should be writen to.
     * <p/>
     * See <a href="#element-writing">element writing</a>.
     *
     * @param visitor The visitor requesting access to element writer.
     * @return The element writer.
     * @throws SAXWriterAccessException Invalid access request for the element writer. See <a href="#element-writing">element writing</a>.
     */
    public Writer getWriter(SAXVisitor visitor) throws SAXWriterAccessException {
        // This implementation doesn't actually enforce the "one writer per element" rule.  It's enforced from
        // within the SAXHandler.
        return writer;
    }

    /**
     * Set the writer to which this element should be writen to.
     * <p/>
     * See <a href="#element-writing">element writing</a>.
     *
     * @param writer  The element writer.
     * @param visitor The visitor requesting to set the element writer.
     * @throws SAXWriterAccessException Invalid access request for the element writer. See <a href="#element-writing">element writing</a>.
     */
    public void setWriter(Writer writer, SAXVisitor visitor) throws SAXWriterAccessException {
        // This implementation doesn't actually enforce the "one writer per element" rule.  It's enforced from
        // within the SAXHandler.
        this.writer = writer;
    }

    /**
     * Get the element naming details.
     *
     * @return Element naming details.
     */
    public QName getName() {
        return name;
    }

    /**
     * Set the naming details for the Element.
     *
     * @param name The element naming details.
     */
    public void setName(QName name) {
        AssertArgument.isNotNull(name, "name");
        this.name = name;
    }

    /**
     * Get the element attributes.
     *
     * @return Element attributes.
     */
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * Set the element attributes.
     *
     * @param attributes The element attributes.
     */
    public void setAttributes(Attributes attributes) {
        AssertArgument.isNotNull(attributes, "attributes");
        this.attributes = attributes;
    }

    /**
     * Get the <a href="#element_cache_object">element cache object</a>.
     *
     * @return The element cache Object.
     */
    public Object getCache() {
        return cache;
    }

    /**
     * Set the <a href="#element_cache_object">element cache object</a>.
     *
     * @param cache The element cache Object.
     */
    public void setCache(Object cache) {
        this.cache = cache;
    }

    /**
     * Get parent element.
     *
     * @return Parent element, or null if it's the documnent root.
     */
    public SAXElement getParent() {
        return parent;
    }

    /**
     * Set parent element.
     *
     * @param parent Parent element, or null if it's the documnent root.
     */
    public void setParent(SAXElement parent) {
        this.parent = parent;
    }

    public String toString() {
        return getName().toString();
    }

    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * Create a DOM {@link Element} instance from this {@link SAXElement}
     * instance.
     * @param document The document to use to create the DOM Element.
     * @return The DOM Element.
     */
    public Element toDOMElement(Document document) {
        Element element;

        if(name.getNamespaceURI() != null) {
            if(name.getPrefix().length() != 0) {
                element = document.createElementNS(name.getNamespaceURI(), name.getPrefix() + ":" + name.getLocalPart());
            } else {
                element = document.createElementNS(name.getNamespaceURI(), name.getLocalPart());
            }
        } else {
            element = document.createElement(name.getLocalPart());
        }

        int attributeCount = attributes.getLength();
        for(int i = 0; i < attributeCount; i++) {
            String namespace = attributes.getURI(i);
            String value = attributes.getValue(i);

            if(namespace != null) {
                String qName = attributes.getQName(i);
                Attr attribute = document.createAttributeNS(namespace, qName);

                attribute.setValue(value);
                element.setAttributeNode(attribute);
            } else {
                String localName = attributes.getLocalName(i);
                Attr attribute = document.createAttribute(localName);

                attribute.setValue(value);
                element.setAttributeNode(attribute);
            }
        }
        
        return element;
    }
}