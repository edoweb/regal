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
 * Class PremisBean
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 11.07.2011
 * 
 */
public class PremisBean
{

	DigitalEntity dtlBean = null;
	DigitoolPremis2RdfMap map = new DigitoolPremis2RdfMap();

	Vector<String> urns = new Vector<String>();

	public PremisBean(DigitalEntity dtlBean) throws Exception
	{
		this.dtlBean = dtlBean;

		Element root;

		root = XMLUtils.getDocument(dtlBean.getPreservation());

		String tagName = DigitoolPremis2RdfMap.xmlUrn;
		NodeList nodes = root.getElementsByTagName(tagName);
		for (int j = 0; j < nodes.getLength(); j++)
		{
			Element element = ((Element) nodes.item(j));
			urns.add(element.getTextContent());
		}

	}

	public Vector<String> getUrn()
	{
		return urns;
	}

	public void setUrn(Vector<String> urn)
	{
		this.urns = urn;
	}

	public boolean addSubject(String e)
	{
		return urns.add(e);
	}

	public String getFirstSubject()
	{
		Vector<String> elements = getUrn();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}
}
