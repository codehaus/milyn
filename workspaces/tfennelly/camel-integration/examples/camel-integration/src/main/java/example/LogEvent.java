package example;

public class LogEvent
{
	private String transactionId;
	private String customerId;
	private String nationality;

	public LogEvent()
	{
	}
	
	public LogEvent(String transactionId, String customerId, String nationality)
	{
		this.transactionId = transactionId;
		this.customerId = customerId;
		this.nationality = nationality;
	}

	public String getTransactionId()
	{
		return transactionId;
	}

	public void setTransactionId(String transactionId)
	{
		this.transactionId = transactionId;
	}

	public String getCustomerId()
	{
		return customerId;
	}

	public void setCustomerId(String customerId)
	{
		this.customerId = customerId;
	}

	public String getNationality()
	{
		return nationality;
	}

	public void setNationality(String nationality)
	{
		this.nationality = nationality;
	}
	
	public String toString()
	{
		return "LogEvent [CustomerId=" + customerId + ", nationality=" + nationality + ", transactionId=" + transactionId + "]";
	}

}
