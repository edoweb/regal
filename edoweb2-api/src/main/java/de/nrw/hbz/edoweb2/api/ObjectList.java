package de.nrw.hbz.edoweb2.api;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ObjectList
{
	List<String> list = null;

	public ObjectList()
	{
		list = new Vector<String>();
	}

	public ObjectList(Vector<String> v)
	{
		list = v;
	}

	public List<String> getList()
	{
		return list;
	}

	public void setList(List<String> list)
	{
		this.list = list;
	}

}
