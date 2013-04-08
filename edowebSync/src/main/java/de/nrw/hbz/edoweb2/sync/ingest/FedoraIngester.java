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
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import javax.ws.rs.core.MediaType;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
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

	public FedoraIngester(String usr, String pwd, String host)
	{
		user = usr;
		password = pwd;
		this.host = host;
	}

	@Override
	public ContentModel createContentModel()
	{
		return null;
	}

	@Override
	public void ingest(DigitalEntity dtlBean)
	{
		logger.info("Start ingest: " + namespace + ":" + dtlBean.getPid());

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
					ingestEJournalPart(dtlBean);
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
				updateMonographsNewStyle(dtlBean);
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
					ingestWebpagePart(dtlBean);
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
			logger.info(e.getMessage());
		}
		try
		{
			ClientConfig cc = new DefaultClientConfig();
			cc.getProperties()
					.put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
			cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
					true);
			Client c = Client.create(cc);
			c.addFilter(new HTTPBasicAuthFilter(user, password));
			try
			{
				WebResource index = c.resource(host
						+ ":8080/edoweb2-api/edowebAdmin/index/" + namespace
						+ ":" + dtlBean.getPid());
				index.post();
				logger.info(pid + ": got indexed!");
			}
			catch (UniformInterfaceException e)
			{
				logger.warn("Not indexed! "
						+ e.getResponse().getEntity(String.class));
			}
			try
			{

				WebResource oaiSet = c.resource(host
						+ ":8080/edoweb2-api/edowebAdmin/makeOaiSet/"
						+ namespace + ":" + dtlBean.getPid());
				oaiSet.post();
				logger.info(pid + ": is oai provided! ");
			}
			catch (UniformInterfaceException e)
			{
				logger.warn("Not oai provided! "
						+ e.getResponse().getEntity(String.class));
			}

			lobidify(dtlBean);

		}

		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		logger.info("Thanx and goodbye!\n");
	}

	private void ingestEJournalPart(DigitalEntity dtlBean)
	{
		updateEJournalPart(dtlBean);
	}

	private void ingestWebpagePart(DigitalEntity dtlBean)
	{
		updateWebpagePart(dtlBean);
	}

	@Override
	public void update(DigitalEntity dtlBean)
	{
		logger.info("Start update: " + namespace + ":" + dtlBean.getPid());

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
				updateMonographsNewStyle(dtlBean);
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
			logger.info(e.getMessage());
		}
		try
		{
			ClientConfig cc = new DefaultClientConfig();
			cc.getProperties()
					.put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
			cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
					true);
			Client c = Client.create(cc);
			c.addFilter(new HTTPBasicAuthFilter(user, password));

			WebResource index = c.resource(host
					+ ":8080/edoweb2-api/edowebAdmin/index/" + namespace + ":"
					+ dtlBean.getPid());
			index.post();
			logger.info(pid + ": got indexed!");
			WebResource oaiSet = c.resource(host
					+ ":8080/edoweb2-api/edowebAdmin/makeOaiSet/" + namespace
					+ ":" + dtlBean.getPid());
			oaiSet.post();
			logger.info(pid + ": got set! Thanx and goodbye!\n");
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

	}

	private void updateEJournalPart(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));
		String volName = namespace + ":" + dtlBean.getPid();
		logger.info("Update eJournal volume: " + volName);
		WebResource ejournalVolume = c.resource(host
				+ ":8080/edoweb2-api/ejournal/" + namespace + ":"
				+ dtlBean.getParentPid() + "/volume/" + volName);

		String mimeType = dtlBean.getStreamMime();
		if (mimeType.compareTo("application/pdf") != 0)
		{
			logger.info("Volume " + dtlBean.getPid() + " contains no pdf!");
			return;
		}

		try
		{
			ejournalVolume.put();
			logger.info("Created new volume!");
		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getResponse().getEntity(String.class));
		}
		catch (Exception e)
		{
			logger.info("Volume exists no new volume is created!");
		}
		WebResource ejournalVolumeDC = c.resource(ejournalVolume.toString()
				+ "/dc");
		WebResource ejournalVolumeData = c.resource(ejournalVolume.toString()
				+ "/data");

		try
		{
			logger.info(dtlBean.getStreamMime());
			byte[] data = IOUtils.toByteArray(new FileInputStream(dtlBean
					.getStream()));

			ejournalVolumeData.type(dtlBean.getStreamMime()).post(data);

		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getResponse().getEntity(String.class));
		}
		catch (FileNotFoundException e1)
		{
			logger.error("FileNotFound "
					+ dtlBean.getStream().getAbsolutePath());
		}
		catch (IOException e1)
		{
			logger.error("Problem " + dtlBean.getStream().getAbsolutePath());
		}

		DCBeanAnnotated dc = new DCBeanAnnotated();

		try
		{
			dc.addDescription(dtlBean.getLabel());
			dc.addTitle("Volume of: " + namespace + ":"
					+ dtlBean.getParentPid());
			dc.addType(TypeType.contentType + ":" + ObjectType.ejournalVolume);
			ejournalVolumeDC.post(dc);
		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getResponse().getEntity(String.class));
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}

	}

	private void updateWebpagePart(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));
		String versionName = namespace + ":" + dtlBean.getPid();
		logger.info("Update webpage version: " + versionName);
		WebResource webpageVersion = c.resource(host
				+ ":8080/edoweb2-api/webpage/" + namespace + ":"
				+ dtlBean.getParentPid() + "/version/" + versionName);

		String mimeType = dtlBean.getStreamMime();
		if (mimeType.compareTo("application/zip") != 0)
		{
			logger.info("Version " + dtlBean.getPid() + " contains no zip!");
			return;
		}

		try
		{
			webpageVersion.put();
			logger.info("Create new version!");
		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getResponse().getEntity(String.class));
		}
		catch (Exception e)
		{
			logger.info("Version exists no new version is created!");
		}
		WebResource ejournalVolumeDC = c.resource(webpageVersion.toString()
				+ "/dc");
		WebResource ejournalVolumeData = c.resource(webpageVersion.toString()
				+ "/data");

		try
		{
			logger.info(dtlBean.getStreamMime());
			byte[] data = IOUtils.toByteArray(new FileInputStream(dtlBean
					.getStream()));

			ejournalVolumeData.type(dtlBean.getStreamMime()).post(data);

		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getResponse().getEntity(String.class));
		}
		catch (FileNotFoundException e1)
		{
			logger.error("FileNotFound "
					+ dtlBean.getStream().getAbsolutePath());
		}
		catch (IOException e1)
		{
			logger.error("Problem " + dtlBean.getStream().getAbsolutePath());
		}

		DCBeanAnnotated dc = new DCBeanAnnotated();

		try
		{
			dc.addDescription(dtlBean.getLabel());
			dc.addTitle("Version of: " + namespace + ":"
					+ dtlBean.getParentPid());
			dc.addType(TypeType.contentType + ":" + ObjectType.webpageVersion);
			ejournalVolumeDC.post(dc);
		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getResponse().getEntity(String.class));
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
	}

	private DCBeanAnnotated marc2dc(DigitalEntity dtlBean)
	{
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
			t.printStackTrace();
		}
		return null;
	}

	private void updateMonographs(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));

		WebResource monograph = c.resource(host
				+ ":8080/edoweb2-api/monograph/" + namespace + ":"
				+ dtlBean.getPid());
		try
		{
			String request = "content";
			monograph.put(String.class, request);
		}
		catch (ClientHandlerException e)
		{
			logger.info(e.getMessage());
		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getResponse().getEntity(String.class));
		}
		catch (Exception e)
		{

			logger.info(e.getMessage());
		}

		try
		{

			WebResource monographDC = c.resource(monograph.toString() + "/dc");
			WebResource monographData = c.resource(monograph.toString()
					+ "/data");
			try
			{
				logger.info(dtlBean.getStreamMime());
				byte[] data = IOUtils.toByteArray(new FileInputStream(dtlBean
						.getStream()));
				monographData.type(dtlBean.getStreamMime()).post(data);

			}
			catch (UniformInterfaceException e)
			{
				logger.info(e.getResponse().getEntity(String.class));
			}
			catch (FileNotFoundException e1)
			{
				logger.error("FileNotFound "
						+ dtlBean.getStream().getAbsolutePath());
			}
			catch (IOException e1)
			{
				logger.error("Problem " + dtlBean.getStream().getAbsolutePath());
			}

			DCBeanAnnotated dc = new DCBeanAnnotated();

			try
			{
				dc.add(marc2dc(dtlBean));
				dc.addDescription(dtlBean.getLabel());
				dc.addType(TypeType.contentType + ":" + ObjectType.monograph);
				monographDC.post(dc);
			}
			catch (UniformInterfaceException e)
			{
				logger.info(e.getResponse().getEntity(String.class));
			}
			catch (Exception e)
			{
				logger.debug(e.getMessage());
			}
		}
		catch (UniformInterfaceException e)
		{
			logger.error(e.getMessage());
		}
	}

	private void updateMonographsNewStyle(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));

		WebResource monograph = c.resource(host
				+ ":8080/edoweb2-api/monograph/" + namespace + ":"
				+ dtlBean.getPid());
		try
		{
			String request = "content";
			monograph.put(String.class, request);
		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getResponse().getEntity(String.class));
		}
		catch (Exception e)
		{
			logger.info(e.getMessage());
		}
		try
		{

			WebResource monographDC = c.resource(monograph.toString() + "/dc");
			WebResource monographData = c.resource(monograph.toString()
					+ "/data");

			DigitalEntity fulltextObject = null;
			for (DigitalEntity view : dtlBean.getViewLinks())
			{
				logger.info("I have a view: " + view.getPid());
				if (view.getStreamMime().compareTo("application/pdf") == 0)
				{
					fulltextObject = view;
					break;
				}
			}
			if (fulltextObject != null)
			{
				try
				{
					logger.info(dtlBean.getStreamMime());
					byte[] data = IOUtils.toByteArray(new FileInputStream(
							fulltextObject.getStream()));
					monographData.type(fulltextObject.getStreamMime()).post(
							data);

				}
				catch (UniformInterfaceException e)
				{
					logger.info(e.getResponse().getEntity(String.class));
				}
				catch (FileNotFoundException e1)
				{
					logger.error("FileNotFound "
							+ dtlBean.getStream().getAbsolutePath());
				}
				catch (IOException e1)
				{
					logger.error("Problem "
							+ dtlBean.getStream().getAbsolutePath());
				}
			}

			DCBeanAnnotated dc = new DCBeanAnnotated();

			try
			{
				dc.add(marc2dc(dtlBean));
				dc.addDescription(dtlBean.getLabel());
				dc.addType(TypeType.contentType + ":" + ObjectType.monograph);
				monographDC.post(dc);
			}
			catch (UniformInterfaceException e)
			{
				logger.info(e.getResponse().getEntity(String.class));
			}
			catch (Exception e)
			{
				logger.debug(e.getMessage());
			}
		}
		catch (UniformInterfaceException e)
		{
			logger.error(e.getMessage());
		}
	}

	private void ingestEJournalComplete(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));

		WebResource ejournal = c.resource(host + ":8080/edoweb2-api/ejournal/"
				+ namespace + ":" + dtlBean.getPid());

		try
		{
			String request = "content";
			ejournal.put(String.class, request);
		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getResponse().getEntity(String.class));
		}
		catch (Exception e)
		{
			logger.info(e.getMessage());
		}
		try
		{

			WebResource ejournalDC = c.resource(ejournal.toString() + "/dc");
			DCBeanAnnotated dc = null;
			try
			{
				dc = ejournalDC.get(DCBeanAnnotated.class);
			}
			catch (UniformInterfaceException e)
			{
				logger.info(e.getResponse().getEntity(String.class));
			}
			catch (Exception e)
			{
				dc = new DCBeanAnnotated();
			}

			try
			{
				dc.add(marc2dc(dtlBean));
				dc.addType(TypeType.contentType + ":" + ObjectType.ejournal);
				ejournalDC.post(dc);
			}
			catch (UniformInterfaceException e)
			{
				logger.info(e.getResponse().getEntity(String.class));
			}
			catch (Exception e)
			{
				// logger.debug(e.getMessage());
			}
			Vector<DigitalEntity> viewMainLinks = dtlBean.getViewMainLinks();
			int numOfVols = viewMainLinks.size();
			int num = 1;
			logger.info("Found " + numOfVols + " volumes.");
			for (DigitalEntity b : viewMainLinks)
			{

				String mimeType = b.getStreamMime();
				if (mimeType.compareTo("application/pdf") != 0)
					continue;
				String volName = namespace + ":" + b.getPid();
				// if (b.getLabel() != null && !b.getLabel().isEmpty())
				// volName = urlEncode(num + "-" + b.getLabel());

				logger.info("Create eJournal volume: " + volName + " "
						+ (num++) + "/" + numOfVols);
				WebResource ejournalVolume = c.resource(ejournal.toString()
						+ "/volume/" + volName);
				try
				{
					ejournalVolume.put();
				}
				catch (UniformInterfaceException e)
				{
					logger.info(e.getResponse().getEntity(String.class));
				}
				catch (Exception e)
				{
					logger.info(e.getMessage());
				}
				WebResource ejournalVolumeDC = c.resource(ejournalVolume
						.toString() + "/dc");
				WebResource ejournalVolumeData = c.resource(ejournalVolume
						.toString() + "/data");

				try
				{
					logger.info(b.getStreamMime());
					byte[] data = IOUtils.toByteArray(new FileInputStream(b
							.getStream()));
					ejournalVolumeData.type(b.getStreamMime()).post(data);

				}
				catch (UniformInterfaceException e)
				{
					logger.info(e.getResponse().getEntity(String.class));
				}
				catch (FileNotFoundException e1)
				{
					logger.error("FileNotFound "
							+ dtlBean.getStream().getAbsolutePath());
				}
				catch (IOException e1)
				{
					logger.error("Problem "
							+ dtlBean.getStream().getAbsolutePath());
				}

				dc = null;
				try
				{
					dc = ejournalVolumeDC.get(DCBeanAnnotated.class);
				}
				catch (UniformInterfaceException e)
				{
					logger.info(e.getResponse().getEntity(String.class));
				}
				catch (Exception e)
				{
					dc = new DCBeanAnnotated();
				}

				try
				{
					dc.addDescription(b.getLabel());
					dc.addTitle("Version of: " + namespace + ":"
							+ dtlBean.getPid());
					dc.addType(TypeType.contentType + ":"
							+ ObjectType.ejournalVolume);
					ejournalVolumeDC.post(dc);
				}
				catch (UniformInterfaceException e)
				{
					logger.info(e.getResponse().getEntity(String.class));
				}
				catch (Exception e)
				{
					logger.debug(e.getMessage());
				}

			}
		}
		catch (UniformInterfaceException e)
		{
			logger.error(e.getMessage());
		}
	}

	private void updateEJournalParent(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));

		WebResource ejournal = c.resource(host + ":8080/edoweb2-api/ejournal/"
				+ namespace + ":" + dtlBean.getPid());

		try
		{
			String request = "content";
			ejournal.put(String.class, request);
		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getResponse().getEntity(String.class));
		}
		catch (Exception e)
		{
			logger.info(e.getMessage());
		}
		try
		{

			WebResource ejournalDC = c.resource(ejournal.toString() + "/dc");
			DCBeanAnnotated dc = new DCBeanAnnotated();

			try
			{
				dc.add(marc2dc(dtlBean));
				dc.addType(TypeType.contentType + ":"
						+ ObjectType.ejournal.toString());
				ejournalDC.post(dc);
			}
			catch (UniformInterfaceException e)
			{
				logger.info(e.getResponse().getEntity(String.class));
			}
			catch (Exception e)
			{
				// logger.debug(e.getMessage());
			}
			Vector<DigitalEntity> viewMainLinks = dtlBean.getViewMainLinks();
			int numOfVols = viewMainLinks.size();
			logger.info("Found " + numOfVols + " volumes.");
			logger.info("Will not update volumes.");

		}
		catch (UniformInterfaceException e)
		{
			logger.error(e.getMessage());
		}
	}

	private void ingestWebpageComplete(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(MultiPartWriter.class);
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		// Thanks to Tomasz Krzyżak
		// http://stackoverflow.com/questions/11584791/jersey-client-upload-progress
		URLConnectionClientHandler clientHandler = new URLConnectionClientHandler(
				new HttpURLConnectionFactory()
				{
					@Override
					public HttpURLConnection getHttpURLConnection(URL url)
							throws IOException
					{
						HttpURLConnection connection = (HttpURLConnection) url
								.openConnection();
						connection.setChunkedStreamingMode(1024);
						return connection;
					}
				});
		Client c = new Client(clientHandler, cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));

		WebResource webpage = c.resource(host + ":8080/edoweb2-api/webpage/"
				+ namespace + ":" + dtlBean.getPid());

		try
		{
			String response = "";
			try
			{
				String request = "content";
				response = webpage.put(String.class, request);
			}
			catch (UniformInterfaceException e)
			{
				logger.info(e.getResponse().getEntity(String.class));
			}
			catch (Exception e)
			{
				logger.info(e.getMessage());
			}
			WebResource webpageDC = c.resource(webpage.toString() + "/dc");

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
				logger.info(e.getResponse().getEntity(String.class));
			}
			catch (Exception e)
			{
				logger.debug(e.getMessage());
			}
			Vector<DigitalEntity> viewLinks = dtlBean.getViewLinks();
			int numOfVersions = viewLinks.size();
			int num = 1;
			logger.info("Found " + numOfVersions + " versions.");
			for (DigitalEntity b : viewLinks)
			{
				// long start = System.nanoTime();

				String mimeType = b.getStreamMime();
				if (mimeType.compareTo("application/zip") != 0)
					continue;
				String version = namespace + ":" + b.getPid();

				// if (b.getLabel() != null && !b.getLabel().isEmpty())
				// version = urlEncode(b.getLabel());
				logger.info("Create WebpageVersion volume: " + version + " "
						+ (num++) + "/" + numOfVersions);
				WebResource webpageVersion = c.resource(webpage.toString()
						+ "/version/" + version);
				WebResource webpageVersionDC = c.resource(webpageVersion
						.toString() + "/dc");
				WebResource webpageVersionData = c.resource(webpageVersion
						.toString() + "/data");

				try
				{
					// long versionstart = System.nanoTime();
					response = webpageVersion.put(String.class);
					logger.info(response);
					// long versionelapsedTime = System.nanoTime() -
					// versionstart;
					// logger.info("Create new version duration: "
					// + versionelapsedTime);
				}
				catch (UniformInterfaceException e)
				{
					logger.info(e.getResponse().getEntity(String.class));
				}
				catch (Exception e)
				{
					logger.info(e.getMessage());
				}
				finally
				{
					webpageVersion = null;
				}

				try
				{
					logger.info(b.getStreamMime());

					FormDataMultiPart formDataMultiPart = new FormDataMultiPart();

					formDataMultiPart.field("fileType", b.getStreamMime());
					FormDataBodyPart bodyPart = new FormDataBodyPart("file",
							new FileInputStream(b.getStream()),
							MediaType.MULTIPART_FORM_DATA_TYPE);
					formDataMultiPart.bodyPart(bodyPart);

					webpageVersionData.type(MediaType.MULTIPART_FORM_DATA)
							.post(formDataMultiPart);

					// webpageVersionData.type(b.getStreamMime()).post(data);

				}
				catch (UniformInterfaceException e)
				{
					logger.info(e.getResponse().getEntity(String.class));
				}
				catch (FileNotFoundException e1)
				{
					logger.error("FileNotFound "
							+ dtlBean.getStream().getAbsolutePath());
				}
				catch (IOException e1)
				{
					logger.error("Problem "
							+ dtlBean.getStream().getAbsolutePath());
				}

				dc = new DCBeanAnnotated();

				try
				{
					dc.addTitle("Version of: " + dtlBean.getPid());
					dc.addDescription(b.getLabel());
					dc.addType(TypeType.contentType + ":"
							+ ObjectType.webpageVersion);
					webpageVersionDC.post(dc);
				}
				catch (UniformInterfaceException e)
				{
					logger.info(e.getResponse().getEntity(String.class));
				}
				catch (Exception e)
				{
					logger.error(e.getMessage());
				}
				finally
				{
					webpageVersionDC = null;
				}
				// long elapsedTime = System.nanoTime() - start;
				// logger.info("Time: " + elapsedTime);
			}
		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getMessage());
		}

	}

	private void updateWebpageParent(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));

		WebResource webpage = c.resource(host + ":8080/edoweb2-api/webpage/"
				+ namespace + ":" + dtlBean.getPid());

		try
		{

			try
			{
				String request = "content";
				webpage.put(String.class, request);
			}
			catch (UniformInterfaceException e)
			{
				logger.info(e.getResponse().getEntity(String.class));
			}
			catch (Exception e)
			{
				logger.info(e.getMessage());
			}
			WebResource webpageDC = c.resource(webpage.toString() + "/dc");

			DCBeanAnnotated dc = new DCBeanAnnotated();

			try
			{
				dc.add(marc2dc(dtlBean));
				dc.addType(TypeType.contentType + ":"
						+ ObjectType.webpage.toString());
				dc.addDescription(dtlBean.getLabel());
				webpageDC.post(dc);
			}
			catch (UniformInterfaceException e)
			{
				logger.info(e.getResponse().getEntity(String.class));
			}
			catch (Exception e)
			{
				logger.debug(e.getMessage());
			}
			Vector<DigitalEntity> viewLinks = dtlBean.getViewLinks();
			int numOfVersions = viewLinks.size();

			logger.info("Found " + numOfVersions + " versions.");
			logger.info("Will not update versions.");

		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getMessage());
		}

	}

	private void updateSingleWebpage(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(MultiPartWriter.class);
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		cc.getProperties().put(
				DefaultApacheHttpClientConfig.PROPERTY_CHUNKED_ENCODING_SIZE,
				1024);

		Client c = ApacheHttpClient.create(cc);

		// Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));

		WebResource webpage = c.resource(host + ":8080/edoweb2-api/webpage/"
				+ namespace + ":" + dtlBean.getPid());

		String request = "content";
		try
		{
			webpage.put(String.class, request);
		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getResponse().getEntity(String.class));
		}
		catch (Exception e)
		{
			logger.info(e.getMessage());
		}
		try
		{

			WebResource webpageDC = c.resource(webpage.toString() + "/dc");

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
				logger.info(e.getResponse().getEntity(String.class));
			}
			catch (Exception e)
			{
				logger.debug(e.getMessage());
			}
			for (DigitalEntity b : dtlBean.getArchiveLinks())
			{
				logger.info(dtlBean.getPid() + ": has a Archive");
				String mimeType = b.getStreamMime();
				logger.debug(mimeType);
				if (mimeType.compareTo("application/zip") != 0)
					continue;
				String version = namespace + ":" + b.getPid();

				logger.info("Create webpage version: " + version);
				WebResource webpageVersion = c.resource(webpage.toString()
						+ "/version/" + version);
				try
				{
					webpageVersion.put(String.class);
				}
				catch (UniformInterfaceException e)
				{
					logger.info(e.getResponse().getEntity(String.class));
				}

				WebResource webpageVersionDC = c.resource(webpageVersion
						.toString() + "/dc");
				WebResource webpageVersionData = c.resource(webpageVersion
						.toString() + "/data");

				try
				{
					logger.info(b.getStreamMime());

					FormDataMultiPart formDataMultiPart = new FormDataMultiPart();

					// formDataMultiPart.field("fileType", b.getStreamMime());
					FormDataBodyPart bodyPart = new FormDataBodyPart("file",
							new FileInputStream(b.getStream()),
							MediaType.MULTIPART_FORM_DATA_TYPE);
					formDataMultiPart.bodyPart(bodyPart);

					webpageVersionData.type(MediaType.MULTIPART_FORM_DATA)
							.post(formDataMultiPart);

					// webpageVersionData.type(b.getStreamMime()).post(data);

				}
				catch (UniformInterfaceException e)
				{
					logger.warn(e.getResponse().getEntity(String.class));
				}
				catch (FileNotFoundException e1)
				{
					logger.error("FileNotFound "
							+ dtlBean.getStream().getAbsolutePath());
				}
				catch (IOException e1)
				{
					logger.error("Problem "
							+ dtlBean.getStream().getAbsolutePath());
				}
				dc = new DCBeanAnnotated();

				try
				{
					dc.addTitle("Version of: " + namespace + ":"
							+ dtlBean.getPid());
					dc.addDescription(b.getLabel());
					dc.addType(TypeType.contentType + ":"
							+ ObjectType.webpageVersion);
					webpageVersionDC.post(dc);
				}
				catch (UniformInterfaceException e)
				{
					logger.info(e.getResponse().getEntity(String.class));
				}
				catch (Exception e)
				{

					logger.debug(e.getMessage());
				}

			}
		}
		catch (UniformInterfaceException e)
		{
			logger.error(e.getMessage());
		}

	}

	@Override
	public void delete(String pid)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));

		WebResource delete = c.resource(host
				+ ":8080/edoweb2-api/edowebAdmin/delete/" + namespace + ":"
				+ pid);
		try
		{
			delete.delete();
		}
		catch (UniformInterfaceException e)
		{
			logger.info(e.getResponse().getEntity(String.class));
		}

	}

	private void lobidify(DigitalEntity dtlBean)
	{

		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));

		WebResource lobid = c.resource(host
				+ ":8080/edoweb2-api/edowebAdmin/lobidify/" + namespace + ":"
				+ dtlBean.getPid());
		try

		{
			lobid.type("text/plain").post();
		}
		catch (UniformInterfaceException e)
		{
			logger.warn("Fetching lobid-data failed: "
					+ e.getResponse().getEntity(String.class));
		}
	}

}
