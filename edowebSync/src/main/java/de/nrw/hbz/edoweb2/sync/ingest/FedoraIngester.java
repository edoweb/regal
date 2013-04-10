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
package de.nrw.hbz.edoweb2.sync.ingest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.Vector;

import javax.ws.rs.core.MediaType;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;

import de.nrw.hbz.edoweb2.api.DCBeanAnnotated;
import de.nrw.hbz.edoweb2.api.ObjectType;
import de.nrw.hbz.edoweb2.api.TypeType;
import de.nrw.hbz.edoweb2.datatypes.ContentModel;
import de.nrw.hbz.edoweb2.sync.extern.DigitalEntity;
import de.nrw.hbz.edoweb2.sync.mapper.ControlBean;
import de.nrw.hbz.edoweb2.sync.mapper.DCBean;

/**
 * Class FedoraIngester
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
public class FedoraIngester implements IngestInterface
{
	final static Logger logger = LoggerFactory.getLogger(FedoraIngester.class);

	final static String namespace = "edoweb";

	String user = null;
	String password = null;
	String host = null;
	Client webclient = null;

	public FedoraIngester(String usr, String pwd, String host)
	{
		user = usr;
		password = pwd;
		this.host = host;
		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(MultiPartWriter.class);
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		cc.getProperties().put(
				DefaultApacheHttpClientConfig.PROPERTY_CHUNKED_ENCODING_SIZE,
				1024);
		webclient = Client.create(cc);
		webclient.addFilter(new HTTPBasicAuthFilter(user, password));
	}

	@Override
	public ContentModel createContentModel()
	{
		// TODO implement
		return null;
	}

	@Override
	public void ingest(DigitalEntity dtlBean)
	{
		logger.info(dtlBean.getPid() + " " + "Start ingest: " + namespace + ":"
				+ dtlBean.getPid());

		String partitionC = null;
		String pid = null;
		pid = dtlBean.getPid();
		try
		{
			ControlBean control = new ControlBean(dtlBean);
			partitionC = control.getPartitionC().firstElement();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{

			if (partitionC.compareTo("EJO01") == 0)
			{
				if (dtlBean.isParent())
				{
					logger.info(pid + ": start ingesting eJournal");
					ingestEJournalComplete(dtlBean);
					logger.info(pid + ": end ingesting eJournal");
				}
				else
				{
					logger.info(pid + ": start ingesting eJournal volume");
					updateEJournalPart(dtlBean);
					logger.info(pid + ": end ingesting eJournal volume");
				}
			}
			else if (partitionC.compareTo("WPD01") == 0)
			{

				logger.info(pid + ": start updating monograph (wpd01)");
				updateMonographs(dtlBean);
				logger.info(pid + ": end updating monograph (wpd01)");
			}
			else if (partitionC.compareTo("WPD02") == 0)
			{

				logger.info(pid + ": start updating monograph (wpd02)");
				updateMonographs(dtlBean);
				logger.info(pid + ": end updating monograph (wpd02)");
			}
			else if (partitionC.compareTo("WSC01") == 0)
			{
				if (dtlBean.isParent())
				{
					logger.info(pid + ": start ingesting webpage (wsc01)");
					ingestWebpageComplete(dtlBean);
					logger.info(pid + ": end ingesting webpage (wsc01)");
				}
				else
				{
					logger.info(pid
							+ ": start ingesting webpage version (wsc01)");
					updateWebpagePart(dtlBean);
					logger.info(pid + ": end ingesting webpage version (wsc01)");
				}
			}
			else if (partitionC.compareTo("WSI01") == 0)
			{
				logger.info(pid + ": start updating webpage (wsi01)");
				updateSingleWebpage(dtlBean);
				logger.info(pid + ": end updating webpage (wsi01)");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.info(dtlBean.getPid() + " " + e.getMessage());
		}
		try
		{
			index(dtlBean);
			oaiProvide(dtlBean);
			lobidify(dtlBean);

		}

		catch (Exception e)
		{
			logger.error(dtlBean.getPid() + " " + e.getMessage());
		}

		logger.info(dtlBean.getPid() + " " + "Thanx and goodbye!\n");
	}

	@Override
	public void update(DigitalEntity dtlBean)
	{
		logger.info(dtlBean.getPid() + " " + "Start update: " + namespace + ":"
				+ dtlBean.getPid());

		String partitionC = null;
		String pid = null;
		pid = dtlBean.getPid();
		try
		{
			ControlBean control = new ControlBean(dtlBean);
			partitionC = control.getPartitionC().firstElement();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{

			if (partitionC.compareTo("EJO01") == 0)
			{
				if (dtlBean.isParent())
				{
					logger.info(pid + ": start updating eJournal");
					updateEJournalParent(dtlBean);
					logger.info(pid + ": end updating eJournal");
				}
				else
				{
					logger.info(pid + ": start updating eJournal volume");
					updateEJournalPart(dtlBean);
					logger.info(pid + ": end updating eJournal volume");
				}
			}
			else if (partitionC.compareTo("WPD01") == 0)
			{
				logger.info(pid + ": start updating monograph (wpd01)");
				updateMonographs(dtlBean);
				logger.info(pid + ": end updating monograph (wpd01)");
			}
			else if (partitionC.compareTo("WPD02") == 0)
			{

				logger.info(pid + ": start updating monograph (wpd02)");
				updateMonographs(dtlBean);
				logger.info(pid + ": end updating monograph (wpd02)");
			}
			else if (partitionC.compareTo("WSC01") == 0)
			{
				if (dtlBean.isParent())
				{
					logger.info(pid + ": start updating webpage (wsc01)");
					updateWebpageParent(dtlBean);
					logger.info(pid + ": end updating webpage (wsc01)");
				}
				else
				{
					logger.info(pid
							+ ": start updating webpage version (wsc01)");
					updateWebpagePart(dtlBean);
					logger.info(pid + ": end updating webpage version (wsc01)");
				}
			}
			else if (partitionC.compareTo("WSI01") == 0)
			{
				logger.info(pid + ": start updating webpage (wsi01)");
				updateSingleWebpage(dtlBean);
				logger.info(pid + ": end updating webpage (wsi01)");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.info(dtlBean.getPid() + " " + e.getMessage());
		}
		try
		{
			index(dtlBean);
			oaiProvide(dtlBean);
			lobidify(dtlBean);
		}
		catch (Exception e)
		{
			logger.error(dtlBean.getPid() + " " + e.getMessage());
		}

	}

	@Override
	public void delete(String p)
	{
		String pid = namespace + ":" + p;

		WebResource delete = webclient.resource(host
				+ ":8080/edoweb2-api/utils/delete/" + pid);
		try
		{
			delete.delete();
		}
		catch (UniformInterfaceException e)
		{
			logger.info(pid + " Can't delete!");
		}

	}

	private void updateEJournalPart(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		logger.info(pid + " " + "Found eJournal volume.");
		String resource = host + ":8080/edoweb2-api/ejournal/" + namespace
				+ ":" + dtlBean.getParentPid() + "/volume/" + pid;
		createObject(resource, dtlBean, "application/pdf");
		logger.info(pid + " " + "updated.\n");
	}

	private void updateWebpagePart(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		logger.info(pid + " Found webpage version.");
		String resource = host + ":8080/edoweb2-api/webpage/" + namespace + ":"
				+ dtlBean.getParentPid() + "/version/" + pid;
		createObject(resource, dtlBean, "application/zip");
		logger.info(pid + " " + "updated.\n");
	}

	private void updateMonographs(DigitalEntity dtlBean)
	{

		String pid = namespace + ":" + dtlBean.getPid();
		logger.info(pid + " Found monograph.");
		String resource = host + ":8080/edoweb2-api/monograph/" + pid;
		createObject(resource, dtlBean, "application/pdf");
		logger.info(pid + " " + "updated.\n");
	}

	private void updateEJournalParent(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{
			logger.info(pid + " Found ejournal.");
			String ejournal = host + ":8080/edoweb2-api/ejournal/" + pid;
			String ejournalDC = ejournal.toString() + "/dc";
			createResource(ejournal, dtlBean);
			updateDC(ejournalDC, dtlBean);
			Vector<DigitalEntity> viewMainLinks = dtlBean.getViewMainLinks();
			int numOfVols = viewMainLinks.size();
			logger.info(pid + " " + "Found " + numOfVols + " volumes.");
			logger.info(pid + " " + "Will not update volumes.");
			logger.info(pid + " " + "updated.\n");
		}
		catch (Exception e)
		{
			logger.error(pid + " " + e.getMessage());
		}

	}

	private void updateWebpageParent(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{
			logger.info(pid + " Found webpage.");
			String webpage = host + ":8080/edoweb2-api/webpage/" + pid;
			createResource(webpage, dtlBean);
			String webpageDC = webpage + "/dc";
			updateDC(webpageDC, dtlBean);
			Vector<DigitalEntity> viewLinks = dtlBean.getViewLinks();
			int numOfVersions = viewLinks.size();
			logger.info(pid + " " + "Found " + numOfVersions + " versions.");
			logger.info(pid + " " + "Will not update versions.");
			logger.info(pid + " " + "updated.\n");
		}
		catch (Exception e)
		{
			logger.info(pid + " " + e.getMessage());
		}

	}

	private void updateSingleWebpage(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{
			logger.info(pid + " Found webpage.");
			String webpage = host + ":8080/edoweb2-api/webpage/" + pid;
			createResource(webpage, dtlBean);
			updateDC(webpage + "/dc", dtlBean);

			for (DigitalEntity b : dtlBean.getArchiveLinks())
			{
				String versionPid = namespace + ":" + b.getPid();
				if (b.getStreamMime().compareTo("application/zip") == 0)
				{
					createObject(webpage + "/version/" + versionPid, b,
							"application/zip");
					break;
				}
			}
			logger.info(pid + " " + "updated.\n");
		}
		catch (Exception e)
		{
			logger.error(pid + " " + e.getMessage());
		}

	}

	private void ingestEJournalComplete(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{
			logger.info(pid + " Found ejournal.");
			String ejournal = host + ":8080/edoweb2-api/ejournal/" + pid;
			String ejournalDC = ejournal + "/dc";
			createResource(ejournal, dtlBean);
			updateDC(ejournalDC, dtlBean);
			Vector<DigitalEntity> viewMainLinks = dtlBean.getViewMainLinks();
			int numOfVols = viewMainLinks.size();
			int count = 1;
			logger.info(pid + " Found " + numOfVols + " volumes.");
			for (DigitalEntity b : viewMainLinks)
			{
				logger.info("Part: " + (count++) + "/" + numOfVols);
				updateEJournalPart(b);
			}
			logger.info(pid + " " + "and all volumes updated.\n");
		}
		catch (Exception e)
		{
			logger.error(pid + " " + e.getMessage());
		}
	}

	private void ingestWebpageComplete(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{
			logger.info(pid + " Found webpage.");
			String webpage = host + ":8080/edoweb2-api/webpage/" + pid;
			String webpageDC = webpage + "/dc";
			createResource(webpage, dtlBean);
			updateDC(webpageDC, dtlBean);
			Vector<DigitalEntity> viewLinks = dtlBean.getViewLinks();
			int numOfVersions = viewLinks.size();
			logger.info(pid + " Found " + numOfVersions + " versions.");
			int count = 1;
			for (DigitalEntity b : viewLinks)
			{
				logger.info("Part: " + (count++) + "/" + numOfVersions);
				updateWebpagePart(b);
			}
			logger.info(pid + " " + "and all versions updated.\n");
		}
		catch (Exception e)
		{
			logger.info(pid + " " + e.getMessage());
		}

	}

	private void createObject(String resource, DigitalEntity dtlBean,
			String expectedMime)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		String dc = resource + "/dc";
		String data = resource + "/data";

		createResource(resource, dtlBean);

		if (dtlBean.getStreamMime().compareTo(expectedMime) != 0)
		{
			DigitalEntity fulltextObject = null;
			for (DigitalEntity view : dtlBean.getViewMainLinks())
			{
				if (view.getStreamMime().compareTo(expectedMime) == 0)
				{
					fulltextObject = view;
					break;
				}
			}
			if (fulltextObject != null)
			{
				updateData(data, fulltextObject);
			}
			else
			{
				logger.warn(pid + " found no valid data.");
			}
		}
		else
		{
			updateData(data, dtlBean);
		}

		updateDC(dc, dtlBean);
	}

	private void createResource(String endpoint, DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource resource = webclient.resource(endpoint);
		try
		{
			resource.put(String.class);
		}
		catch (UniformInterfaceException e)
		{
			logger.info(pid + " already exists - will be updated!");
		}
	}

	private void updateDC(String endpoint, DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource webpageDC = webclient.resource(endpoint);

		DCBeanAnnotated dc = new DCBeanAnnotated();

		try
		{
			dc.add(marc2dc(dtlBean));
			dc.addDescription(dtlBean.getLabel());
			dc.addType(TypeType.contentType + ":" + ObjectType.webpage);
			webpageDC.post(dc);

		}
		catch (UniformInterfaceException e)
		{
			logger.info(pid + " " + e.getMessage());
		}
		catch (Exception e)
		{
			logger.debug(pid + " " + e.getMessage());
		}
	}

	private void updateData(String endpoint, DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource data = webclient.resource(endpoint);

		try
		{
			logger.info(pid + " Updata data: " + dtlBean.getStreamMime());
			MultiPart multiPart = new MultiPart();
			multiPart.bodyPart(new StreamDataBodyPart("InputStream",
					new FileInputStream(dtlBean.getStream()), dtlBean
							.getStream().getName()));
			multiPart.bodyPart(new BodyPart(dtlBean.getStreamMime(),
					MediaType.TEXT_PLAIN_TYPE));
			data.type("multipart/mixed").post(multiPart);

		}
		catch (UniformInterfaceException e)
		{
			logger.error(pid + " " + e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			logger.error(pid + " " + "FileNotFound "
					+ dtlBean.getStream().getAbsolutePath());
		}
		catch (Exception e)
		{
			logger.error(pid + " " + e.getMessage());
		}

	}

	private void lobidify(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource lobid = webclient.resource(host
				+ ":8080/edoweb2-api/utils/lobidify/" + namespace + ":"
				+ dtlBean.getPid());
		try

		{
			lobid.type("text/plain").post();
		}
		catch (UniformInterfaceException e)
		{
			logger.warn(pid + " fetching lobid-data failed");
		}
	}

	private void index(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{

			WebResource index = webclient.resource(host
					+ ":8080/edoweb2-api/utils/index/" + pid);
			index.post();
			logger.info(pid + ": got indexed!");
		}
		catch (UniformInterfaceException e)
		{
			logger.warn(pid + " " + "Not indexed! " + e.getMessage());
		}
	}

	private void oaiProvide(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource oaiSet = webclient.resource(host
				+ ":8080/edoweb2-api/utils/makeOaiSet/" + namespace + ":"
				+ dtlBean.getPid());
		try
		{
			oaiSet.post();
		}
		catch (UniformInterfaceException e)
		{
			logger.warn(pid + " " + "Not oai provided! " + e.getMessage());
		}
	}

	private DCBeanAnnotated marc2dc(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{
			StringWriter str = new StringWriter();
			TransformerFactory tFactory = TransformerFactory.newInstance();

			// String xslFile = ClassLoader.getSystemResource(
			// "MARC21slim2OAIDC.xsl").getPath();

			// TODO jar path
			Transformer transformer = tFactory
					.newTransformer(new StreamSource(ClassLoader
							.getSystemResourceAsStream("MARC21slim2OAIDC.xsl")));
			transformer.transform(new StreamSource(dtlBean.getMarcFile()),
					new StreamResult(str));

			String xmlStr = str.getBuffer().toString();
			// logger.info(xmlStr);
			DCBeanAnnotated dc = new DCBeanAnnotated(new DCBean(xmlStr));
			return dc;

		}
		catch (Throwable t)
		{
			logger.warn(pid + " " + t.getMessage());
		}
		return null;
	}
}
