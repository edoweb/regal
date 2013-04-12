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

package de.nrw.hbz.edoweb2.sync.mapper;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.nrw.hbz.edoweb2.sync.extern.DigitalEntity;
import de.nrw.hbz.edoweb2.sync.util.XMLUtils;

/**
 * Class DCQBean
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 24.06.2011
 * 
 */
@SuppressWarnings("javadoc")
public class DCQBean
{
	DigitalEntity dtlBean = null;
	DigitoolQDc2RdfMap map = new DigitoolQDc2RdfMap();

	Vector<String> medium = new Vector<String>();
	Vector<String> extent = new Vector<String>();

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
	Vector<String> isPartOf = new Vector<String>();
	Vector<String> anAbstract = new Vector<String>();
	Vector<String> alternative = new Vector<String>();

	public DCQBean(DigitalEntity dtlBean) throws Exception
	{
		this.dtlBean = dtlBean;

		Element root = XMLUtils.getDocument(dtlBean.getDc());

		String tagName = DigitoolQDc2RdfMap.xmlMedium;
		NodeList nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addMedium(element.getTextContent());
		}

		tagName = DigitoolDc2RdfMap.xmlDcContributer;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addContributer(element.getTextContent());
		}

		tagName = DigitoolDc2RdfMap.xmlDcCoverage;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addCoverage(element.getTextContent());
		}

		tagName = DigitoolDc2RdfMap.xmlDcCreator;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addCreator(element.getTextContent());
		}
		tagName = DigitoolDc2RdfMap.xmlDcDate;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addDate(element.getTextContent());
		}
		tagName = DigitoolDc2RdfMap.xmlDcDescription;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addDescription(element.getTextContent());
		}
		tagName = DigitoolDc2RdfMap.xmlDcFormat;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addFormat(element.getTextContent());
		}
		tagName = DigitoolDc2RdfMap.xmlDcIdentifier;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			String curId = element.getTextContent();
			if (curId.contains("HT"))
			{
				if (curId.startsWith("HBZ"))
				{
					addIdentifier(curId.substring(3));
				}
				else
				{
					addIdentifier(curId);
				}
			}
			else
			{
				addIdentifier(curId);
			}
		}
		tagName = DigitoolDc2RdfMap.xmlDcLanguage;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addLanguage(element.getTextContent());
		}
		tagName = DigitoolDc2RdfMap.xmlDcPublisher;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addPublisher(element.getTextContent());
		}
		tagName = DigitoolDc2RdfMap.xmlDcRelation;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addRelation(element.getTextContent());
		}
		tagName = DigitoolDc2RdfMap.xmlDcRights;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addRights(element.getTextContent());
		}
		tagName = DigitoolDc2RdfMap.xmlDcSource;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addSource(element.getTextContent());
		}
		tagName = DigitoolDc2RdfMap.xmlDcSubject;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addSubject(element.getTextContent());
		}
		tagName = DigitoolDc2RdfMap.xmlDcTitle;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addTitle(element.getTextContent());
		}
		tagName = DigitoolDc2RdfMap.xmlDcType;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addType(element.getTextContent());
		}

		tagName = DigitoolQDc2RdfMap.xmlIsPartOf;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addIsPartOf(element.getTextContent());
		}

		tagName = DigitoolQDc2RdfMap.xmlAbstract;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addAbstract(element.getTextContent());
		}

		tagName = DigitoolQDc2RdfMap.xmlAlternative;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addAlternative(element.getTextContent());
		}

	}

	public Vector<String> getExtent()
	{
		return extent;
	}

	public Vector<String> getMedium()
	{
		return medium;
	}

	public void setMedium(Vector<String> medium)
	{
		this.medium = medium;
	}

	public boolean addMedium(String e)
	{
		return medium.add(e);
	}

	public String getFirstMedium()
	{
		Vector<String> elements = getMedium();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getContributer()
	{
		return contributer;
	}

	public void setContributer(Vector<String> contributer)
	{
		this.contributer = contributer;
	}

	public boolean addContributer(String e)
	{
		return contributer.add(e);
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

	public void setCoverage(Vector<String> coverage)
	{
		this.coverage = coverage;
	}

	public boolean addCoverage(String e)
	{
		return coverage.add(e);
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

	public void setCreator(Vector<String> creator)
	{
		this.creator = creator;
	}

	public boolean addCreator(String e)
	{
		return creator.add(e);
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

	public void setDate(Vector<String> date)
	{
		this.date = date;
	}

	public boolean addDate(String e)
	{
		return date.add(e);
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

	public void setDescription(Vector<String> description)
	{
		this.description = description;
	}

	public boolean addDescription(String e)
	{
		return description.add(e);
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

	public void setFormat(Vector<String> format)
	{
		this.format = format;
	}

	public boolean addFormat(String e)
	{
		return format.add(e);
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

	public void setIdentifier(Vector<String> identifier)
	{
		this.identifier = identifier;
	}

	public boolean addIdentifier(String e)
	{
		return identifier.add(e);
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

	public void setLanguage(Vector<String> language)
	{
		this.language = language;
	}

	public boolean addLanguage(String e)
	{
		return language.add(e);
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

	public void setPublisher(Vector<String> publisher)
	{
		this.publisher = publisher;
	}

	public boolean addPublisher(String e)
	{
		return publisher.add(e);
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

	public void setRelation(Vector<String> relation)
	{
		this.relation = relation;
	}

	public boolean addRelation(String e)
	{
		return relation.add(e);
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

	public void setRights(Vector<String> rights)
	{
		this.rights = rights;
	}

	public boolean addRights(String e)
	{
		return rights.add(e);
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

	public void setSource(Vector<String> source)
	{
		this.source = source;
	}

	public boolean addSource(String e)
	{
		return source.add(e);
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

	public void setSubject(Vector<String> subject)
	{
		this.subject = subject;
	}

	public boolean addSubject(String e)
	{
		return subject.add(e);
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

	public void setTitle(Vector<String> title)
	{
		this.title = title;
	}

	public boolean addTitle(String e)
	{
		return title.add(e);
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

	public void setType(Vector<String> type)
	{
		this.type = type;
	}

	public boolean addType(String e)
	{
		return type.add(e);
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

	public Vector<String> getIsPartOf()
	{
		return isPartOf;
	}

	public void setIsPartOf(Vector<String> isPartOf)
	{
		this.isPartOf = isPartOf;
	}

	public boolean addIsPartOf(String str)
	{
		return isPartOf.add(str);
	}

	public String getFirstIsPartOf()
	{
		Vector<String> elements = getIsPartOf();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getAbstract()
	{
		return anAbstract;
	}

	public void setAbstract(Vector<String> anAbstract)
	{
		this.anAbstract = anAbstract;
	}

	public boolean addAbstract(String e)
	{
		return anAbstract.add(e);
	}

	public String getFirstAbstract()
	{
		Vector<String> elements = getAbstract();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getAlternative()
	{
		return alternative;
	}

	public void setAlternative(Vector<String> alternative)
	{
		this.alternative = alternative;
	}

	public boolean addAlternative(String e)
	{
		return alternative.add(e);
	}

	public String getFirstAlternative()
	{
		Vector<String> elements = getAlternative();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}
}
