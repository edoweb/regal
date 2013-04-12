package de.nrw.hbz.edoweb2.api;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateObjectBean
{
	String type = null;
	String parentPid = null;

	public CreateObjectBean()
	{

	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getParentPid()
	{
		return parentPid;
	}

	public void setParentPid(String parentPid)
	{
		this.parentPid = parentPid;
	}

}
