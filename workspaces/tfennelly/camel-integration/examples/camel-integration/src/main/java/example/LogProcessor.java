package example;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogProcessor implements Processor
{
    private final Log log = LogFactory.getLog(getClass());
	private final String string;

	public LogProcessor(String string)
	{
		this.string = string;
	}

	public void process(Exchange exchange) throws Exception
	{
		LogEvent logEvent = (LogEvent) exchange.getIn().getBody();
		log.info("Logging event [" + string + "]" + logEvent);
	}

}
