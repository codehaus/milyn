package example;

import java.io.PrintStream;

import org.apache.camel.builder.RouteBuilder;
import org.milyn.smooks.camel.SmooksProcessor;

public class ExampleRouteBuilder extends RouteBuilder
{
	private PrintStream outputStream;
	
	public ExampleRouteBuilder()
	{
		outputStream = System.out;
	}
	
	public ExampleRouteBuilder(PrintStream outputStream)
	{
		this.outputStream = outputStream;
	}

	@Override
	public void configure() throws Exception
	{
		// Set up the route for the initial input-message.xml
		from("direct:input").process(new SmooksProcessor("/smooks-config.xml", this));
		
		// Set up routes for endpoints defined in smooks-config.xml
		from("direct:ireland").process(new LogProcessor("ie", outputStream));
		from("direct:greatbritain").process(new LogProcessor("gb", outputStream));
	}

}
