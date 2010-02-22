/*
	Milyn - Copyright (C) 2006 - 2010

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
package org.milyn.smooks.camel.result;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;
import org.milyn.javabean.context.BeanContext;
import org.milyn.payload.JavaResult;

/**
 * {@link JavaResult} mapper.
 * <p/>
 * If there are multiple beans, they will be mapped onto the message body as a {@link Map}, keyed by 
 * their {@link BeanContext} beanId.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class JavaResultMapper implements ResultMapper {
	
	private List<String> beansToMapList;
	
	/**
	 * Public default constructor.
	 * <p/>
	 * Map all beans from the bean context.
	 */
	public JavaResultMapper(String...beansToMap) {
		if(beansToMap == null || beansToMap.length == 0) {
			// map all beans
			beansToMapList = null;
		} else {
			beansToMapList = Arrays.asList(beansToMap);
		}
	}

	/* (non-Javadoc)
	 * @see org.milyn.smooks.camel.result.ResultMapper#createResult()
	 */
	public Result createResult() {
		return new JavaResult();
	}

	/* (non-Javadoc)
	 * @see org.milyn.smooks.camel.result.ResultMapper#mapResult(javax.xml.transform.Result, org.apache.camel.Exchange)
	 */
	public Exchange mapResult(Result result, Exchange exchange) {
		Message outMessage = exchange.getOut();
		
		outMessage = new JavaMessage();
		exchange.setOut(outMessage);
		
		// And map the beans...
		mapBeans((JavaResult) result, outMessage);

		return exchange;
	}

	private void mapBeans(JavaResult result, Message outMessage) {
		Map<String, Object> resultBeans = result.getResultMap();
		
		if(beansToMapList == null) {
			if(resultBeans.size() == 1) {
				// map single bean...
				outMessage.setBody(resultBeans.values().iterator().next());				
			} else {
				// map all beans in a Map...
				outMessage.setBody(new HashMap<String, Object>(resultBeans));				
			}
		} else {
			if(beansToMapList.size() == 1) {
				Object bean = resultBeans.get(beansToMapList.get(0));
				if(bean != null) {
					outMessage.setBody(bean);
				}
			} else {
				// map requested beans in a Map...
				Map<String, Object> outBeans = new HashMap<String, Object>();
				outMessage.setBody(outBeans);
				for(String beanId : beansToMapList) {
					outBeans.put(beanId, resultBeans.get(beanId));
				}
			}
		}
	}
	
	private class JavaMessage extends DefaultMessage {

		/* (non-Javadoc)
		 * @see org.apache.camel.impl.MessageSupport#getBody(java.lang.Class)
		 */
		@Override
		public <T> T getBody(Class<T> type) {
			Object body = getBody();
			
			if(type.isInstance(body)) {
				return type.cast(body);
			}
			
			// Return the first instance of that type.  What if there are more?
			if(body instanceof Map<?, ?>) {
				Collection<?> beans = ((Map<?, ?>)body).values();
				for(Object bean : beans) {
					if(type.isInstance(bean)) {
						return type.cast(bean);
					}
				}
			}
			
			return null;
		}		
	}
}
