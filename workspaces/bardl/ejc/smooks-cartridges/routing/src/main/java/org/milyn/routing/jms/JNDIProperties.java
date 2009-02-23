package org.milyn.routing.jms;


public class JNDIProperties
{
    private String contextFactory;

    private String providerUrl;

    private String namingFactoryUrlPkgs;

	public String getContextFactory()
	{
		return contextFactory;
	}

	public void setContextFactory( String contextFactory )
	{
		this.contextFactory = contextFactory;
	}

	public String getProviderUrl()
	{
		return providerUrl;
	}

	public void setProviderUrl( String providerUrl )
	{
		this.providerUrl = providerUrl;
	}

	public String getNamingFactoryUrlPkgs()
	{
		return namingFactoryUrlPkgs;
	}

	public void setNamingFactoryUrlPkgs( String namingFactoryUrl )
	{
		this.namingFactoryUrlPkgs = namingFactoryUrl;
	}

}
