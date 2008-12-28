/*
 * Milyn - Copyright (C) 2006
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (version 2.1) as published
 * by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details:
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.milyn.validation;

import java.io.IOException;

import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.annotation.VisitAfterIf;
import org.milyn.delivery.annotation.VisitBeforeIf;
import org.milyn.delivery.dom.DOMVisitAfter;
import org.milyn.delivery.dom.DOMVisitBefore;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.w3c.dom.Element;

/**
 * AbstractValidator is a just a convenient way of creating a Validator in Smooks.
 * It contains implementations of visitBefore, visitAfter for both SAX and DOM. These methods simpy delegate
 * to the abstract method {@link #validate(String, ExecutionContext)} which subclasses can implement.
 *
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 */
@VisitBeforeIf(condition = "parameters.containsKey('executeBefore') && parameters.executeBefore.value == 'true'")
@VisitAfterIf(condition = "!parameters.containsKey('executeBefore') || parameters.executeBefore.value != 'true'")
public abstract class AbstractValidator implements SAXVisitBefore, SAXVisitAfter, DOMVisitBefore, DOMVisitAfter
{
    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException
    {
        validate(element.toString(), executionContext);
    }

    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException
    {
        validate(element.toString(), executionContext);
    }

    public void visitBefore(Element element, ExecutionContext executionContext) throws SmooksException
    {
        validate(element.getTextContent(), executionContext);
    }

    public void visitAfter(Element element, ExecutionContext executionContext) throws SmooksException
    {
        validate(element.getTextContent(), executionContext);
    }

    public abstract void validate(final String text, final ExecutionContext executionContext) throws SmooksException;

}
