package de.nrw.hbz.regal.sync.extern;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

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

public class DippDigitalEntityBuilder implements DigitalEntityBuilder
{
	final static Logger logger = LoggerFactory
			.getLogger(DippDigitalEntityBuilder.class);

	@Override
	public DigitalEntity build(String baseDir, String pid) throws Exception
	{
		// String dir = URLEncoder.encode(baseDir);
		return buildDigitalEntity(baseDir, pid);
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
		list = getDocument(relsExtFile).getElementsByTagName(
				"rel:isMemberOfCollection");

		if (list != null && list.getLength() > 0)
		{
			dtlDe.setParentPid(list.item(0).getTextContent()
					.replace("info:fedora/", ""));
		}
		File content = new File(baseDir + File.separator + "content.zip");

		if (content.exists())
		{
			dtlDe.setStream(content);
			dtlDe.setStreamMime("application/zip");
		}

		try
		{
			// logger.debug(pid + " search for members!");

			Element root = getDocument(relsExtFile);
			NodeList constituents = root
					.getElementsByTagName("rel:hasCollectionMember");
			for (int i = 0; i < constituents.getLength(); i++)
			{
				Element c = (Element) constituents.item(i);
				String cPid = c.getAttribute("rdf:resource").replace(
						"info:fedora/", "");
				if (cPid.contains("temp"))
				{
					// logger.debug(cPid + " skip temporary object.");

				}
				else
				{
					String cDir = baseDir + File.separator
							+ URLEncoder.encode(cPid);

					dtlDe.addViewLink(buildDigitalEntity(cDir, cPid));
				}

			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return dtlDe;

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
