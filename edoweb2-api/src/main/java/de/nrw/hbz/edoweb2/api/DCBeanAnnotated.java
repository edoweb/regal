/*
 * Copyright 2012 hbz NRW (http://www.hbz-nrw.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.nrw.hbz.edoweb2.api;

import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

import de.nrw.hbz.edoweb2.datatypes.DCBean;
import de.nrw.hbz.edoweb2.datatypes.Node;

/**
 * Class DCBean
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@XmlRootElement
public class DCBeanAnnotated
{
	Vector<String> contributer = new Vector<String>();
	Vector<String> coverage = new Vector<String>();
	Vector<String> creator = new Vector<String>();
	Vector<String> date = new Vector<String>();
	Vector<String> description = new Vector<String>();
	Vector<String> format = new Vector<String>();
	Vector<String> identifier = new Vector<String>();
	Vector<String> language = new Vector<String>();
	Vector<String> publisher = new Vector<String>();
	Vector<String> relation = new Vector<String>();
	Vector<String> rights = new Vector<String>();
	Vector<String> source = new Vector<String>();
	Vector<String> subject = new Vector<String>();
	Vector<String> title = new Vector<String>();
	Vector<String> type = new Vector<String>();

	public DCBeanAnnotated()
	{

	}

	public DCBeanAnnotated(DCBean node)
	{
		contributer = node.getContributer();
		coverage = node.getCoverage();
		creator = node.getCreator();
		date = node.getDate();
		description = node.getDescription();
		format = node.getFormat();
		identifier = node.getIdentifier();
		language = node.getLanguage();
		publisher = node.getPublisher();
		relation = node.getRelation();
		rights = node.getRights();
		source = node.getSource();
		subject = node.getSubject();
		title = node.getTitle();
		type = node.getType();
	}

	public DCBeanAnnotated(Node node)
	{
		contributer = node.getContributer();
		coverage = node.getCoverage();
		creator = node.getCreator();
		date = node.getDate();
		description = node.getDescription();
		format = node.getFormat();
		identifier = node.getIdentifier();
		language = node.getLanguage();
		publisher = node.getPublisher();
		relation = node.getRelation();
		rights = node.getRights();
		source = node.getSource();
		subject = node.getSubject();
		title = node.getTitle();
		type = node.getType();
	}

	public Vector<String> getContributer()
	{
		return contributer;
	}

	public DCBeanAnnotated setContributer(Vector<String> contributer)
	{
		this.contributer = contributer;
		return this;
	}

	public DCBeanAnnotated addContributer(String e)
	{
		contributer.add(e);
		return this;
	}

	public String getFirstContributer()
	{
		Vector<String> elements = getContributer();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getCoverage()
	{
		return coverage;
	}

	public DCBeanAnnotated setCoverage(Vector<String> coverage)
	{
		this.coverage = coverage;
		return this;
	}

	public DCBeanAnnotated addCoverage(String e)
	{
		coverage.add(e);
		return this;
	}

	public String getFirstCoverage()
	{
		Vector<String> elements = getCoverage();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getCreator()
	{
		return creator;
	}

	public DCBeanAnnotated setCreator(Vector<String> creator)
	{
		this.creator = creator;
		return this;
	}

	public DCBeanAnnotated addCreator(String e)
	{
		creator.add(e);
		return this;
	}

	public String getFirstCreator()
	{
		Vector<String> elements = getCreator();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getDate()
	{
		return date;
	}

	public DCBeanAnnotated setDate(Vector<String> date)
	{
		this.date = date;
		return this;
	}

	public DCBeanAnnotated addDate(String e)
	{
		date.add(e);
		return this;
	}

	public String getFirstDate()
	{
		Vector<String> elements = getDate();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getDescription()
	{
		return description;
	}

	public DCBeanAnnotated setDescription(Vector<String> description)
	{
		this.description = description;
		return this;
	}

	public DCBeanAnnotated addDescription(String e)
	{
		description.add(e);
		return this;
	}

	public String getFirstDescription()
	{
		Vector<String> elements = getDescription();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getFormat()
	{
		return format;
	}

	public DCBeanAnnotated setFormat(Vector<String> format)
	{
		this.format = format;
		return this;
	}

	public DCBeanAnnotated addFormat(String e)
	{
		format.add(e);
		return this;
	}

	public String getFirstFormat()
	{
		Vector<String> elements = getFormat();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getIdentifier()
	{
		return identifier;
	}

	public DCBeanAnnotated setIdentifier(Vector<String> identifier)
	{
		this.identifier = identifier;
		return this;
	}

	public DCBeanAnnotated addIdentifier(String e)
	{
		identifier.add(e);
		return this;
	}

	public String getFirstIdentifier()
	{
		Vector<String> elements = getIdentifier();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getLanguage()
	{
		return language;
	}

	public DCBeanAnnotated setLanguage(Vector<String> language)
	{
		this.language = language;
		return this;
	}

	public DCBeanAnnotated addLanguage(String e)
	{
		language.add(e);
		return this;
	}

	public String getFirstLanguage()
	{
		Vector<String> elements = getLanguage();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getPublisher()
	{
		return publisher;
	}

	public DCBeanAnnotated setPublisher(Vector<String> publisher)
	{
		this.publisher = publisher;
		return this;
	}

	public DCBeanAnnotated addPublisher(String e)
	{
		publisher.add(e);
		return this;
	}

	public String getFirstPublisher()
	{
		Vector<String> elements = getPublisher();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getRelation()
	{
		return relation;
	}

	public DCBeanAnnotated setRelation(Vector<String> relation)
	{
		this.relation = relation;
		return this;
	}

	public DCBeanAnnotated addRelation(String e)
	{
		relation.add(e);
		return this;
	}

	public String getFirstRelation()
	{
		Vector<String> elements = getRelation();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getRights()
	{
		return rights;
	}

	public DCBeanAnnotated setRights(Vector<String> rights)
	{
		this.rights = rights;
		return this;
	}

	public DCBeanAnnotated addRights(String e)
	{
		rights.add(e);
		return this;
	}

	public String getFirstRights()
	{
		Vector<String> elements = getRights();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getSource()
	{
		return source;
	}

	public DCBeanAnnotated setSource(Vector<String> source)
	{
		this.source = source;
		return this;
	}

	public DCBeanAnnotated addSource(String e)
	{
		source.add(e);
		return this;
	}

	public String getFirstSource()
	{
		Vector<String> elements = getSource();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getSubject()
	{
		return subject;
	}

	public DCBeanAnnotated setSubject(Vector<String> subject)
	{
		this.subject = subject;
		return this;
	}

	public DCBeanAnnotated addSubject(String e)
	{
		subject.add(e);
		return this;
	}

	public String getFirstSubject()
	{
		Vector<String> elements = getSubject();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getTitle()
	{
		return title;
	}

	public DCBeanAnnotated setTitle(Vector<String> title)
	{
		this.title = title;
		return this;
	}

	public DCBeanAnnotated addTitle(String e)
	{
		title.add(e);
		return this;
	}

	public String getFirstTitle()
	{
		Vector<String> elements = getTitle();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getType()
	{
		return type;
	}

	public DCBeanAnnotated setType(Vector<String> type)
	{
		this.type = type;
		return this;
	}

	public DCBeanAnnotated addType(String e)
	{
		type.add(e);
		return this;
	}

	public String getFirstType()
	{
		Vector<String> elements = getType();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

}
