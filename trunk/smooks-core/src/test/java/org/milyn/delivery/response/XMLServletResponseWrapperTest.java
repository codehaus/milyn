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

package org.milyn.delivery.response;

import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;

import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.container.ContainerRequest;
import org.milyn.container.MockContainerRequest;
import org.milyn.delivery.MockContentDeliveryConfig;
import org.milyn.delivery.http.HeaderAction;
import org.milyn.delivery.process.AbstractProcessingUnit;
import org.milyn.delivery.process.ProcessingSet;
import org.milyn.delivery.process.ProcessingUnit;
import org.milyn.dom.DomUtils;
import org.w3c.dom.Element;

import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockServletOutputStream;

import junit.framework.TestCase;

public class XMLServletResponseWrapperTest extends TestCase {

	private MockContainerRequest mockCR;
	private MockHttpServletResponse mockSR;
	
	protected void setUp() throws Exception {
		mockCR = new MockContainerRequest();
		mockSR = new MockHttpServletResponse() {
			public String getCharacterEncoding() {
				return "UTF-8";
			}
			public void setIntHeader(String arg0, int arg1) {
			}
		};
	}

	/*
	 * Test method for 'org.milyn.delivery.response.XMLServletResponseWrapper.XMLServletResponseWrapper(ContainerRequest, HttpServletResponse)'
	 */
	public void test_initHeaderActions() {
		// Make sure it constructs without configured header actions
		new XMLServletResponseWrapper(mockCR, mockSR);
		
		// Set the header actions.
		addHeaderAction("add", "header-x", "value-x", (MockContentDeliveryConfig) mockCR.deliveryConfig);
		addHeaderAction("remove", "header-y", "value-y", (MockContentDeliveryConfig) mockCR.deliveryConfig);
		
		// Now, construct it again - with header actions
		new XMLServletResponseWrapper(mockCR, mockSR);
	}

	public void test_deliverResponse_OutputStream() {
		addHeaderAction("add", "header-x", "value-x", (MockContentDeliveryConfig) mockCR.deliveryConfig);
		addHeaderAction("remove", "header-y", "value-y", (MockContentDeliveryConfig) mockCR.deliveryConfig);
		addTransUnit("w", new TestTU("x", true), (MockContentDeliveryConfig) mockCR.deliveryConfig);
		addTransUnit("y", new TestTU("z", false), (MockContentDeliveryConfig) mockCR.deliveryConfig);

		XMLServletResponseWrapper wrapper = new XMLServletResponseWrapper(mockCR, mockSR);
		MockServletOutputStream mockOS = new MockServletOutputStream();

		try {
			mockSR.setContentType("text/html; charset=ISO-88591");
			mockSR.setupOutputStream(mockOS);
			
			ServletOutputStream os = wrapper.getOutputStream();
			os.print("<w>");
			os.write("sometext ".getBytes());
			os.print(1);
			os.print(" ");
			os.print(true);
			os.print(" ");
			os.print('c');
			os.print(" ");
			os = wrapper.getOutputStream();
			os.print(1.1);
			os.print(" ");
			os.print(10L);
			os.print(" ");
			os.print("<y/></w>");
			os.close();
			
			wrapper.deliverResponse();
			wrapper.close();
			
			// expect "w" elements to be renamed to "x" and "y" elements to be renamed to "z". 
			assertEquals("Wrong SmooksXML delivery response.", "<x>sometext 1 true c 1.1 10 <z></z></x>", mockOS.getContents());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void test_deliverResponse_PrintWriter() {
		addHeaderAction("add", "Content-Type", "text/html", (MockContentDeliveryConfig) mockCR.deliveryConfig);
		addHeaderAction("remove", "Content-Length", "100", (MockContentDeliveryConfig) mockCR.deliveryConfig);

		XMLServletResponseWrapper wrapper = new XMLServletResponseWrapper(mockCR, mockSR);
		MockServletOutputStream mockOS = new MockServletOutputStream();

		try {
			mockSR.setContentType("text/html; charset=ISO-88591");
			mockSR.setupOutputStream(mockOS);
			
			PrintWriter pw = wrapper.getWriter();
			pw.write("<x>".toCharArray());
			pw.write("sometext".toCharArray(), 0, 8);
			pw.write("<a/>");
			pw = wrapper.getWriter();
			pw.write("</x>", 0, 4);
			
			wrapper.deliverResponse();
			wrapper.close();
			
			assertEquals("Wrong SmooksXML delivery response.", "<x>sometext<a></a></x>", mockOS.getContents());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class TestTU extends AbstractProcessingUnit {
		private static SmooksResourceConfiguration resourceConfig = new SmooksResourceConfiguration("X", "X", "X");
		private String newName;
		private boolean visitBefore;
		public TestTU(String newName, boolean visitBefore) {
			super(resourceConfig);
			this.newName = newName;
			this.visitBefore = visitBefore;
		}
		public void visit(Element element, ContainerRequest containerRequest) {
			DomUtils.renameElement(element, newName, true, true);
		}
		public boolean visitBefore() {
			return visitBefore;
		}
	}

    private void addHeaderAction(String action, String headerName, String headerValue, MockContentDeliveryConfig deliveryConfig) {
        SmooksResourceConfiguration resourceConfig = new SmooksResourceConfiguration("X", "X", "X");
        
        resourceConfig.setParameter("action", action);
        resourceConfig.setParameter("header-name", headerName);
        resourceConfig.setParameter("header-value", headerValue);
        
        deliveryConfig.addObject("http-response-header", new HeaderAction(resourceConfig));
    }

    private void addTransUnit(String targetElement, ProcessingUnit processingUnit, MockContentDeliveryConfig deliveryConfig) {
        ProcessingSet processingSet = (ProcessingSet)deliveryConfig.processingSets.get(targetElement);
        
        if(processingSet == null) {
            processingSet = new ProcessingSet();
            deliveryConfig.processingSets.put(targetElement, processingSet);
        }
        processingSet.addProcessingUnit(processingUnit, new SmooksResourceConfiguration(targetElement, processingUnit.getClass().getName()));
    }
}
