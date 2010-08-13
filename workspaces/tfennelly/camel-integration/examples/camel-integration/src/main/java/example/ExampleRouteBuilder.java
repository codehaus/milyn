package example;

import org.apache.camel.builder.RouteBuilder;

public class ExampleRouteBuilder extends RouteBuilder
{
	
	public ExampleRouteBuilder()
	{
	}
	
	@Override
	public void configure() throws Exception
	{
		// Set up the route for the initial input-message.xml
		errorHandler(defaultErrorHandler().maximumRedeliveries(5).redeliverDelay(10000));
		from("direct:input").to("smooks://smooks-config.xml");
		
		// Set up routes for endpoints defined in smooks-config.xml
		from("direct:ireland").process(new LogProcessor("ie")).to("jms:queue:ireland");
		from("direct:greatbritain").process(new LogProcessor("gb")).to("jms:queue:greatbritian");
	}

}
