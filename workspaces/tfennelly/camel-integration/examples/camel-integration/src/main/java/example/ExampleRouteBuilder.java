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
		from("file://" + getWorkingDir() + "?fileName=input-message.xml&noop=true").routeId("inputFileRoute") 
		// Might be able to specify the input directory like shown below if 
		// https://issues.apache.org/activemq/browse/CAMEL-3063
		// is excepted.
		//from("file://./?fileName=input-message.xml&noop=true").routeId("inputFileRoute") 
		//from("file://.?fileName=input-message.xml&noop=true").routeId("inputFileRoute") 
		//from("file:.?fileName=input-message.xml&noop=true").routeId("inputFileRoute") 
		.log("Read file [${file:name}]")
		.to("smooks://smooks-config.xml");
		
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
