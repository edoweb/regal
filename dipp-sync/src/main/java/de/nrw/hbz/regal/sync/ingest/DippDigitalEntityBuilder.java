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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class DippDigitalEntityBuilder implements DigitalEntityBuilder
{
	final static Logger logger = LoggerFactory
			.getLogger(DippDigitalEntityBuilder.class);

	HashMap<String, String> map = new HashMap<String, String>();

	@Override
	public DigitalEntity build(String baseDir, String pid) throws Exception
	{
		// String dir = URLEncoder.encode(baseDir);
		if (!map.containsKey(pid))
		{
			map.put(pid, pid);
			return buildDigitalEntity(baseDir, pid);
		}
		throw new Exception(pid + " already visited!");
	}

	private DigitalEntity buildDigitalEntity(String baseDir, String pid)
	{
		DigitalEntity dtlDe = new DigitalEntity(baseDir);
		File dcFile = new File(baseDir + File.separator + "QDC.xml");
		File relsExtFile = new File(baseDir + File.separator + "RELS-EXT.xml");
		dtlDe.setPid(pid);
		try
		{
			String dcString = IOUtils.readStringFromStream(new FileInputStream(
					dcFile));
			dcString = dcString.replaceAll("<ns:", "<dc:");
			dcString = dcString.replaceAll("</ns:", "</dc:");
			dcString = dcString.replaceAll("xmlns:ns", "xmlns:dc");

			dtlDe.setDc(dcString);
		}
		catch (FileNotFoundException e)
		{
			logger.error(e.getMessage());
		}
		catch (IOException e)
		{
			logger.error(e.getMessage());
		}

		NodeList list = getDocument(dcFile).getElementsByTagName("ns:title");

		if (list != null && list.getLength() > 0)
		{
			dtlDe.setLabel(list.item(0).getTextContent());
		}

		list = getDocument(dcFile).getElementsByTagName("ns:type");
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

		buildRelated("rel:isPartOf", dtlDe, baseDir);
		buildRelated("rel:isConstituentOf", dtlDe, baseDir);
		buildRelated("rel:isMemberOf", dtlDe, baseDir);
		buildRelated("rel:isSubsetOf", dtlDe, baseDir);
		buildRelated("rel:isMemberOfCollection", dtlDe, baseDir);
		buildRelated("rel:isDerivationOf", dtlDe, baseDir);
		buildRelated("rel:isDependentOf", dtlDe, baseDir);

		buildRelated("rel:hasPart", dtlDe, baseDir);
		buildRelated("rel:hasConstituent", dtlDe, baseDir);
		buildRelated("rel:hasMember", dtlDe, baseDir);
		buildRelated("rel:hasSubset", dtlDe, baseDir);
		buildRelated("rel:hasCollectionMember", dtlDe, baseDir);
		buildRelated("rel:hasDerivation", dtlDe, baseDir);
		buildRelated("rel:hasDependent", dtlDe, baseDir);

		File content = new File(baseDir + File.separator + "content.zip");

		if (content.exists())
		{
			dtlDe.setStream(content);
			dtlDe.setStreamMime("application/zip");
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
			NodeList list = getDocument(relsExtFile).getElementsByTagName(
					relation);

			for (int i = 0; i < list.getLength(); i++)
			{
				try
				{
					Element n = (Element) list.item(i);
					String np = n.getAttribute("rdf:resource");
					String p = np.replace("info:fedora/", "");
					logger.info(dtlDe.getPid() + " " + relation + " " + p);
					logger.debug("BUILD-GRAPH: \"" + dtlDe.getPid() + "\"->\""
							+ p + "\" [label=\"" + relation + "\"]");
					dtlDe.addRelated(build(baseDir, p), "relation");
				}
				catch (Exception e)
				{
					logger.warn(e.getMessage());
				}
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
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
