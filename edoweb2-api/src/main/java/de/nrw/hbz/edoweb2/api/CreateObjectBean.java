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
}
