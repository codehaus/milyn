package example;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.milyn.io.StreamUtils;

public class ExampleRouteBuilderTest extends CamelTestSupport
{
	
	@Override
	public RouteBuilder createRouteBuilder()
	{
		return new ExampleRouteBuilder(System.out);
	}
	
	@Test
	public void route() throws Exception
	{
        byte[] payload = StreamUtils.readStream(getClass().getResourceAsStream("/input-message.xml"));
		template.sendBody("direct:input",  payload);
		Thread.sleep(1000);
	}
	
}
