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

import de.nrw.hbz.edoweb2.sync.extern.DigitalEntityBean;
import de.nrw.hbz.edoweb2.sync.util.XMLUtils;

/**
 * Class DCDBean
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
public class DCDBean
{

	DigitalEntityBean dtlBean = null;
	DigitoolDcD2RdfMap map = new DigitoolDcD2RdfMap();

	Vector<String> creatorPersonalName = new Vector<String>();
	Vector<String> contributorPersonalName = new Vector<String>();
	Vector<String> statementOfResponsibility = new Vector<String>();
	Vector<String> extent = new Vector<String>();
	Vector<String> publisherThesisNote = new Vector<String>();
	Vector<String> placeOfPublicationUniversity = new Vector<String>();
	Vector<String> placeOfPublication = new Vector<String>();
	Vector<String> furtherTitle = new Vector<String>();

	public DCDBean(DigitalEntityBean dtlBean) throws Exception
	{
		this.dtlBean = dtlBean;

		Element root;

		root = XMLUtils.getDocument(dtlBean.getDc());

		String tagName = DigitoolDcD2RdfMap.xmlCreatorPersonalName;
		NodeList nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addCreatorPersonalName(element.getTextContent());
		}

		tagName = DigitoolDcD2RdfMap.xmlStatementOfResponsibility;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addStatementOfResponsibility(element.getTextContent());
		}

		tagName = DigitoolDcD2RdfMap.xmlExtent;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addExtent(element.getTextContent());
		}
		tagName = DigitoolDcD2RdfMap.xmlPlaceOfPublicationUniversity;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addPlaceOfPublicationUniversity(element.getTextContent());
		}

		tagName = DigitoolDcD2RdfMap.xmlPublisherThesisNote;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addPublisherThesisNote(element.getTextContent());
		}

		tagName = DigitoolDcD2RdfMap.xmlPlaceOfPublication;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addPlaceOfPublication(element.getTextContent());
		}

		tagName = DigitoolDcD2RdfMap.xmlfurtherTitle;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addFurtherTitle(element.getTextContent());
		}

		tagName = DigitoolDcD2RdfMap.xmlContributorPersonalName;
		nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			addContributerPersonalName(element.getTextContent());
		}

	}

	public boolean addContributerPersonalName(String e)
	{
		return contributorPersonalName.add(e);
	}

	public DigitoolDcD2RdfMap getMap()
	{
		return map;
	}

	public void setMap(DigitoolDcD2RdfMap map)
	{
		this.map = map;
	}

	public Vector<String> getStatementOfResponsibility()
	{
		return this.statementOfResponsibility;
	}

	public void setStatementOfResponsibility(
			Vector<String> statementOfResponsibility)
	{
		this.statementOfResponsibility = statementOfResponsibility;
	}

	public boolean addStatementOfResponsibility(String e)
	{
		return statementOfResponsibility.add(e);
	}

	public String getFirstStatementOfResponsibility()
	{
		Vector<String> elements = getStatementOfResponsibility();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getExtent()
	{
		return this.extent;
	}

	public void setExtent(Vector<String> extent)
	{
		this.extent = extent;
	}

	public boolean addExtent(String e)
	{
		return extent.add(e);
	}

	public String getFirstExtent()
	{
		Vector<String> elements = getExtent();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getPublisherThesisNote()
	{
		return this.publisherThesisNote;
	}

	public void setPublisherThesisNote(Vector<String> publisherThesisNote)
	{
		this.publisherThesisNote = publisherThesisNote;
	}

	public boolean addPublisherThesisNote(String e)
	{
		return publisherThesisNote.add(e);
	}

	public String getFirstPublisherThesisNote()
	{
		Vector<String> elements = getPublisherThesisNote();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getPlaceOfPublicationUniversity()
	{
		return this.placeOfPublicationUniversity;
	}

	public void setPlaceOfPublicationUniversity(
			Vector<String> placeOfPublicationUniversity)
	{
		this.creatorPersonalName = placeOfPublicationUniversity;
	}

	public boolean addPlaceOfPublicationUniversity(String e)
	{
		return placeOfPublicationUniversity.add(e);
	}

	public boolean addFurtherTitle(String e)
	{
		return furtherTitle.add(e);
	}

	public String getFirstPlaceOfPublicationUniversity()
	{
		Vector<String> elements = getPlaceOfPublicationUniversity();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getCreatorPersonalName()
	{
		return this.creatorPersonalName;
	}

	public void setCreatorPersonalName(Vector<String> creatorPersonalName)
	{
		this.creatorPersonalName = creatorPersonalName;
	}

	public boolean addCreatorPersonalName(String e)
	{
		return creatorPersonalName.add(e);
	}

	public String getFirstCreatorPersonalName()
	{
		Vector<String> elements = getCreatorPersonalName();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getPlaceOfPublication()
	{
		return this.placeOfPublication;
	}

	public void setPlaceOfPublication(Vector<String> placeOfPublication)
	{
		this.placeOfPublication = placeOfPublication;
	}

	public boolean addPlaceOfPublication(String e)
	{
		return placeOfPublication.add(e);
	}

	public String getFirstPlaceOfPublication()
	{
		Vector<String> elements = getCreatorPersonalName();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getFurtherTitle()
	{
		return furtherTitle;
	}

	public void setFurtherTitle(Vector<String> furtherTitle)
	{
		this.furtherTitle = furtherTitle;
	}

	public String getFirstFurtherTitle()
	{
		Vector<String> elements = getFurtherTitle();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getContributerPersonalName()
	{
		return contributorPersonalName;
	}

	public void setContributerPersonalName(Vector<String> contributer)
	{
		this.contributorPersonalName = contributer;
	}

	public String getFirstContributerPersonalName()
	{
		Vector<String> elements = getContributerPersonalName();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

}
