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
package de.nrw.hbz.regal.sync.ingest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.helpers.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.nrw.hbz.regal.sync.extern.DigitalEntity;
import de.nrw.hbz.regal.sync.extern.DigitalEntityBuilder;
import de.nrw.hbz.regal.sync.extern.StreamType;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class DippDigitalEntityBuilder implements DigitalEntityBuilder
{
	final static Logger logger = LoggerFactory
			.getLogger(DippDigitalEntityBuilder.class);

	HashMap<String, DigitalEntity> map = new HashMap<String, DigitalEntity>();

	@Override
	public DigitalEntity build(String baseDir, String pid) throws Exception
	{

		if (!map.containsKey(pid))
		{
			DigitalEntity e = new DigitalEntity(baseDir);
			// store reference to e
			map.put(pid, e);
			// update Reference
			e = buildDigitalEntity(baseDir, pid, e);
			return e;
		}
		return map.get(pid);
	}

	private DigitalEntity buildDigitalEntity(String baseDir, String pid,
			DigitalEntity dtlDe)
	{
		// dtlDe = new DigitalEntity(baseDir);
		File dcFile = new File(baseDir + File.separator + "QDC.xml");
		if (!dcFile.exists())
		{
			dcFile = new File(baseDir + File.separator + "DC.xml");
		}
		File relsExtFile = new File(baseDir + File.separator + "RELS-EXT.xml");
		dtlDe.setPid(pid);

		try
		{

			FileInputStream fis = new FileInputStream(dcFile);
			String dcString = IOUtils.toString(fis, "UTF-8");
			dcString = dcString.replaceAll("<ns\\:", "<dc:");
			dcString = dcString.replaceAll("</ns\\:", "</dc:");
			dcString = dcString.replaceAll("xmlns\\:ns", "xmlns:dc");

			dtlDe.setDc(dcString);
			NodeList list = getDocument(dcString).getElementsByTagName(
					"dc:title");

			if (list != null && list.getLength() > 0)
			{
				dtlDe.setLabel(list.item(0).getTextContent());
			}

			list = getDocument(dcString).getElementsByTagName("dc:type");
			if (list != null && list.getLength() > 0)
			{
				for (int i = 0; i < list.getLength(); i++)
				{
					Element el = (Element) list.item(i);
					String type = el.getAttribute("xsi:type");
					if (type.compareTo("oai:pub-type") == 0)
					{
						dtlDe.setType(el.getTextContent());
					}

				}
			}

		}
		catch (FileNotFoundException e)
		{
			logger.debug(e.getMessage());
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}

		buildRelated("rel:isPartOf", dtlDe, baseDir);
		// buildRelated("rel:isConstituentOf", dtlDe, baseDir);
		buildRelated("rel:isMemberOf", dtlDe, baseDir);
		buildRelated("rel:isSubsetOf", dtlDe, baseDir);
		buildRelated("rel:isMemberOfCollection", dtlDe, baseDir);
		buildRelated("rel:isDerivationOf", dtlDe, baseDir);
		buildRelated("rel:isDependentOf", dtlDe, baseDir);

		buildRelated("rel:hasPart", dtlDe, baseDir);
		// buildRelated("rel:hasConstituent", dtlDe, baseDir);
		buildRelated("rel:hasMember", dtlDe, baseDir);
		buildRelated("rel:hasSubset", dtlDe, baseDir);
		buildRelated("rel:hasCollectionMember", dtlDe, baseDir);
		buildRelated("rel:hasDerivation", dtlDe, baseDir);
		buildRelated("rel:hasDependent", dtlDe, baseDir);

		File content = new File(baseDir + File.separator + "content.zip");

		if (content.exists())
		{
			dtlDe.addStream(content, "application/zip", StreamType.DATA);
		}
		else
		{

		}

		return dtlDe;

	}

	private void buildRelated(String relation, DigitalEntity dtlDe,
			String baseDir)
	{
		try
		{
			File relsExtFile = new File(baseDir + File.separator
					+ "RELS-EXT.xml");
			logger.debug("Parse file: " + relsExtFile.getAbsolutePath());
			NodeList list = getDocument(relsExtFile).getElementsByTagName(
					relation);

			logger.debug("found " + list.getLength() + " nodes with tagname "
					+ relation);
			for (int i = 0; i < list.getLength(); i++)
			{
				String p = null;
				try
				{
					Element n = (Element) list.item(i);
					logger.debug(n.getTagName());
					String np = n.getAttribute("rdf:resource");
					p = np.replace("info:fedora/", "");
					logger.debug("BUILD-GRAPH: \"" + dtlDe.getPid() + "\"->\""
							+ p + "\" [label=\"" + relation + "\"]");

					int end = baseDir.lastIndexOf(File.separator);
					String dir = baseDir.substring(0, end) + File.separator
							+ URLEncoder.encode(p);
					if (!p.contains("temp"))
						dtlDe.addRelated(build(dir, p), relation);
				}
				catch (Exception e)
				{
					logger.debug(e.getMessage());
				}
			}
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
	}

	private Element getDocument(File digitalEntityFile)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder;

			docBuilder = factory.newDocumentBuilder();

			Document doc;

			doc = docBuilder.parse(new BufferedInputStream(new FileInputStream(
					digitalEntityFile)));
			Element root = doc.getDocumentElement();
			root.normalize();
			return root;
		}
		catch (FileNotFoundException e)
		{

			logger.error(e.getMessage());
		}
		catch (SAXException e)
		{

			logger.error(e.getMessage());
		}
		catch (IOException e)
		{

			logger.error(e.getMessage());
		}
		catch (ParserConfigurationException e)
		{

			logger.error(e.getMessage());
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		return null;
	}

	private Element getDocument(String str)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder;

			docBuilder = factory.newDocumentBuilder();

			Document doc;

			doc = docBuilder.parse(new BufferedInputStream(
					new ByteArrayInputStream(str.getBytes("UTF-8"))));
			Element root = doc.getDocumentElement();
			root.normalize();
			return root;
		}
		catch (FileNotFoundException e)
		{

			logger.error(e.getMessage());
		}
		catch (SAXException e)
		{

			logger.error(e.getMessage());
		}
		catch (IOException e)
		{

			logger.error(e.getMessage());
		}
		catch (ParserConfigurationException e)
		{

			logger.error(e.getMessage());
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		return null;
	}
}
