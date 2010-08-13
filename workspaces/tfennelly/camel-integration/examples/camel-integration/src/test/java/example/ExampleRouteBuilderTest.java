package example;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.milyn.io.StreamUtils;
import org.milyn.smooks.camel.dataformat.StreamInDomOutMapper;

public class ExampleRouteBuilderTest extends CamelTestSupport
{
	@Override
	protected JndiRegistry createRegistry() throws Exception
	{
		JndiRegistry jndiRegistry = super.createRegistry();
		jndiRegistry.bind("smooksMapper", new StreamInDomOutMapper());
		return jndiRegistry;
	}
	
	@Override
	public RouteBuilder createRouteBuilder()
	{
		return new ExampleRouteBuilder(System.out);
	}
	
	@Test
	public void route() throws Exception
	{
        byte[] payload = StreamUtils.readStream(getClass().getResourceAsStream("/input-message.xml"));
		
		MockEndpoint irelandMockQueue = getMockEndpoint("jms:queue:ireland");
		irelandMockQueue.setExpectedMessageCount(1);
		
		template.sendBody("direct:input",  payload);
		
		irelandMockQueue.assertIsSatisfied(1000);
	}
	
	@Override 
	protected CamelContext createCamelContext() throws Exception 
	{
		CamelContext context = super.createCamelContext();
		context.addComponent("jms", context.getComponent("mock")); 
		context.setTracing(true);
		return context;
	}
	
}
