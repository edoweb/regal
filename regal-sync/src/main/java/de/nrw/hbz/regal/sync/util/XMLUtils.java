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
package de.nrw.hbz.regal.sync.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class XMLUtils
{

	/**
	 * converts a string to an dom element
	 * 
	 * @param xmlString
	 *            the xmlstring
	 * @return the root element
	 * @throws NullPointerException
	 *             if string is empty or null
	 */
	public static Element getDocument(String xmlString)
			throws NullPointerException
	{
		if (xmlString.isEmpty() || xmlString == null)
			throw new NullPointerException("XMLUtils: XMLString is null!");
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder;
			factory.setNamespaceAware(true);
			factory.setExpandEntityReferences(false);
			docBuilder = factory.newDocumentBuilder();

			Document doc;

			doc = docBuilder.parse(new BufferedInputStream(
					new ByteArrayInputStream(xmlString.getBytes())));
			Element root = doc.getDocumentElement();
			root.normalize();
			return root;

		}
		catch (FileNotFoundException e)
		{

			e.printStackTrace();
		}
		catch (SAXException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{

			e.printStackTrace();
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		return null;

	}
}
