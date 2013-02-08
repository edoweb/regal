package de.nrw.hbz.edoweb2.api;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TypeObjectDictionary
{
	public String type;
	public List<String> uris;

	public TypeObjectDictionary()
	{
		uris = new Vector<String>();
	}

	public TypeObjectDictionary(String type, List<String> uris)
	{
		this.type = type;
		this.uris = uris;
	}
}
