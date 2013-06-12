package de.nrw.hbz.regal.api.helper;

public class OaiSet
{
	String name = null;
	String spec = null;
	String pid = null;

	public OaiSet(String name, String spec, String pid)
	{
		super();
		this.name = name;
		this.spec = spec;
		this.pid = pid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSpec()
	{
		return spec;
	}

	public void setSpec(String spec)
	{
		this.spec = spec;
	}

	public String getPid()
	{
		return pid;
	}

	protected void setPid(String pid)
	{
		this.pid = pid;
	}

}
