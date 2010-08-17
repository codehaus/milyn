package example;

import java.io.File;

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
		from("file://" + getWorkingDir() + "?fileName=input-message.xml&noop=true")
		.to("smooks://file:./smooks-config.xml");
		
		// Set up routes for endpoints defined in smooks-config.xml
		from("direct:ireland").process(new LogProcessor("ie")).to("jms:queue:ireland");
		from("direct:greatbritain").process(new LogProcessor("gb")).to("jms:queue:greatbritian");
	}
	
	private File getWorkingDir()
	{
		String userDir = System.getProperty("user.dir");
		File workingDir = new File(userDir);
		return workingDir;
	}

}
