package example;

import java.io.File;

import org.apache.camel.builder.RouteBuilder;
import org.milyn.smooks.camel.dataformat.SmooksDataFormat2;

public class ExampleRouteBuilder extends RouteBuilder
{
	public ExampleRouteBuilder()
	{
	}
	
	@Override
	public void configure() throws Exception
	{
		//Starting with Camel 2.5 the path can be specified as file:. 
		//See https://issues.apache.org/activemq/browse/CAMEL-3063 for more information.
		from("file://" + getWorkingDir() + "?fileName=input-message.edi&noop=true")
		.log("Before unmarshal with SmooksDataFormat2:")
		.log("${body}")
		.unmarshal(new SmooksDataFormat2("smooks-config.xml", "org.milyn.payload.StringResult"))
		.log("After unmarshal with SmooksDataFormat2:")
		.log("${body}");
	}
	
	private File getWorkingDir()
	{
		String userDir = System.getProperty("user.dir");
		File workingDir = new File(userDir);
		return workingDir;
	}

}
