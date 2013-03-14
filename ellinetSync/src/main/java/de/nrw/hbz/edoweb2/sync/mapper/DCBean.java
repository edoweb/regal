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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.nrw.hbz.edoweb2.sync.extern.DigitalEntity;
import de.nrw.hbz.edoweb2.sync.util.XMLUtils;

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
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 21.06.2011
 * 
 */
public class DCBean extends de.nrw.hbz.edoweb2.datatypes.DCBean
{
	DigitalEntity dtlBean = null;
	DigitoolDc2RdfMap map = new DigitoolDc2RdfMap();

	public DCBean(String dcXMLString) throws Exception
	{
		parse(dcXMLString);
	}

	public DCBean(DigitalEntity dtlBean) throws Exception
	{
		this.dtlBean = dtlBean;

		parse(dtlBean.getDc());
	}

	private void parse(String str)
	{
		if (str == null || str.isEmpty())
			return;
		Element root = XMLUtils.getDocument(str);

		String tagName = DigitoolDc2RdfMap.xmlDcContributer;
		NodeList nodes = root.getElementsByTagName(tagName);
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

	}

	public DigitoolDc2RdfMap getMap()
	{
		return map;
	}

	public void setMap(DigitoolDc2RdfMap map)
	{
		this.map = map;
	}

}
