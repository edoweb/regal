package de.nrw.hbz.edoweb2.api;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MessageBean
{
	String message = null;

	public MessageBean()
	{

	}

	public MessageBean(String msg)
	{
		message = msg;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

}
