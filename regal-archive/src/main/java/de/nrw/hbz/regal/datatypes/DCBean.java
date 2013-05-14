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
package de.nrw.hbz.regal.datatypes;

import java.util.Vector;

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
@SuppressWarnings("javadoc")
public class DCBean
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

	public Vector<String> getContributer()
	{
		return contributer;
	}

	public DCBean setContributer(Vector<String> contributer)
	{
		this.contributer = contributer;
		return this;
	}

	public DCBean addContributer(String e)
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

	public DCBean setCoverage(Vector<String> coverage)
	{
		this.coverage = coverage;
		return this;
	}

	public DCBean addCoverage(String e)
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

	public DCBean setCreator(Vector<String> creator)
	{
		this.creator = creator;
		return this;
	}

	public DCBean addCreator(String e)
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

	public DCBean setDate(Vector<String> date)
	{
		this.date = date;
		return this;
	}

	public DCBean addDate(String e)
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

	public DCBean setDescription(Vector<String> description)
	{
		this.description = description;
		return this;
	}

	public DCBean addDescription(String e)
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

	public DCBean setFormat(Vector<String> format)
	{
		this.format = format;
		return this;
	}

	public DCBean addFormat(String e)
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

	public DCBean setIdentifier(Vector<String> identifier)
	{
		this.identifier = identifier;
		return this;
	}

	public DCBean addIdentifier(String e)
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

	public DCBean setLanguage(Vector<String> language)
	{
		this.language = language;
		return this;
	}

	public DCBean addLanguage(String e)
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

	public DCBean setPublisher(Vector<String> publisher)
	{
		this.publisher = publisher;
		return this;
	}

	public DCBean addPublisher(String e)
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

	public DCBean setRelation(Vector<String> relation)
	{
		this.relation = relation;
		return this;
	}

	public DCBean addRelation(String e)
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

	public DCBean setRights(Vector<String> rights)
	{
		this.rights = rights;
		return this;
	}

	public DCBean addRights(String e)
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

	public DCBean setSource(Vector<String> source)
	{
		this.source = source;
		return this;
	}

	public DCBean addSource(String e)
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

	public DCBean setSubject(Vector<String> subject)
	{
		this.subject = subject;
		return this;
	}

	public DCBean addSubject(String e)
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

	public DCBean setTitle(Vector<String> title)
	{
		this.title = title;
		return this;
	}

	public DCBean addTitle(String e)
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

	public DCBean setType(Vector<String> type)
	{
		this.type = type;
		return this;
	}

	public DCBean addType(String e)
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
