package example;

import java.io.PrintStream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class LogProcessor implements Processor
{
	private final String string;
	private final PrintStream outputStream;

	public LogProcessor(String string, PrintStream outputStream)
	{
		this.string = string;
		this.outputStream = outputStream;
	}

	public void process(Exchange exchange) throws Exception
	{
		LogEvent logEvent = (LogEvent) exchange.getIn().getBody();
		outputStream.println("Logging event [" + string + "]" + logEvent);
	}

}
