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

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import de.nrw.hbz.edoweb2.api.DCBeanAnnotated;
import de.nrw.hbz.edoweb2.api.ObjectType;
import de.nrw.hbz.edoweb2.api.UploadDataBean;
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

	final static String dtlNamespace = "dtl";
	final static String edowebNamespace = "edoweb";

	@Override
	public ContentModel createContentModel()
	{
		return null;
	}

	@Override
	public void ingest(DigitalEntity dtlBean)
	{
		logger.info("Start ingest: " + dtlNamespace + ":" + dtlBean.getPid());

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

		if (partitionC.compareTo("EJO01") == 0)
		{
			logger.debug(pid + ": is a eJournal");
			ingestEJournal(dtlBean);
		}
		else if (partitionC.compareTo("WPD01") == 0)
		{
			logger.debug(pid + ": is a Amtsdruckschrift old style");
			ingestReports(dtlBean);
		}
		else if (partitionC.compareTo("WPD02") == 0)
		{
			logger.info(pid
					+ ": is a Amtsdruckschrift new style - NOT IMPLEMENTED YET");
		}
		else if (partitionC.compareTo("WSC01") == 0)
		{
			logger.debug(pid + ": is a Webschnitt");
			ingestWebpage(dtlBean);
		}
		else if (partitionC.compareTo("WSI01") == 0)
		{
			logger.info(pid + ": is a Webside - NOT IMPLEMENTED YET");
		}

	}

	private void ingestReports(DigitalEntity dtlBean)
	{

		ingestReportsOriginalObject(dtlBean);
		ingestReportsNew(dtlBean);
	}

	private void ingestReportsOriginalObject(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter("fedoraAdmin", "fedoraAdmin1"));

		// WPD
		WebResource wpd = c.resource("http://localhost:8080/edoweb2-api/wpd/"
				+ dtlNamespace + ":" + dtlBean.getPid());

		String request = "content";
		String response = wpd.put(String.class, request);
		logger.debug(response);

		WebResource wpdDC = c.resource(wpd.toString() + "/dc");
		WebResource wpdData = c.resource(wpd.toString() + "/data");
		WebResource wpdMetadata = c.resource(wpd.toString() + "/metadata");

		// WPD - view_main
		WebResource wpdViewMain = c.resource(wpd.toString() + "/view_main");
		WebResource wpdViewMainDC = c.resource(wpdViewMain.toString() + "/dc");
		WebResource wpdViewMainData = c.resource(wpdViewMain.toString()
				+ "/data");
		WebResource wpdViewMainMetadata = c.resource(wpdViewMain.toString()
				+ "/metadata");

		// WPD - fulltext
		WebResource wpdFulltext = c.resource(wpd.toString() + "/fulltext");
		WebResource wpdFulltextDC = c.resource(wpdFulltext.toString() + "/dc");
		WebResource wpdFulltextData = c.resource(wpdFulltext.toString()
				+ "/data");
		WebResource wpdFulltextMetadata = c.resource(wpdFulltext.toString()
				+ "/metadata");

		wpdViewMainDC.post(new DCBeanAnnotated().addTitle("Main XML"));
		wpdFulltextDC.post(new DCBeanAnnotated().addTitle("Fulltext Data"));

		UploadDataBean data = new UploadDataBean();
		try
		{
			logger.debug("Upload: " + dtlBean.getMe().getAbsolutePath());
			data.path = new URI(dtlBean.getMe().getAbsolutePath());
			data.mime = "text/xml";
			wpdViewMainData.post(data);
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			logger.debug("Upload: " + dtlBean.getStream().getAbsolutePath());
			data.path = new URI(dtlBean.getStream().getAbsolutePath());
			data.mime = "application/pdf";
			wpdFulltextData.post(data);
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			if (dtlBean.getViewLinks() != null
					&& !dtlBean.getViewLinks().isEmpty())
			{
				// WPD - view
				WebResource wpdView = c.resource(wpd.toString() + "/view");
				WebResource wpdViewDC = c.resource(wpdView.toString() + "/dc");
				WebResource wpdViewData = c.resource(wpdView.toString()
						+ "/data");
				WebResource wpdViewMetadata = c.resource(wpdView.toString()
						+ "/metadata");
				// WPD - ocr
				WebResource wpdOcr = c.resource(wpd.toString() + "/ocr");
				WebResource wpdOcrDC = c.resource(wpdOcr.toString() + "/dc");
				WebResource wpdOcrData = c
						.resource(wpdOcr.toString() + "/data");
				WebResource wpdOcrMetadata = c.resource(wpdOcr.toString()
						+ "/metadata");

				logger.debug("Upload: "
						+ dtlBean.getViewLinks().get(0).getMe()
								.getAbsolutePath());
				data.path = new URI(dtlBean.getViewLinks().get(0).getMe()
						.getAbsolutePath());
				data.mime = "text/xml";
				wpdViewData.post(data);

				logger.debug("Upload: "
						+ dtlBean.getViewLinks().get(0).getStream()
								.getAbsolutePath());
				data.path = new URI(dtlBean.getViewLinks().get(0).getStream()
						.getAbsolutePath());
				data.mime = "text/text";
				wpdOcrData.post(data);

				wpdViewDC.post(new DCBeanAnnotated().addTitle("OCR XML"));
				wpdOcrDC.post(new DCBeanAnnotated().addTitle("OCR Data"));
			}
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			if (dtlBean.getIndexLinks() != null
					&& !dtlBean.getIndexLinks().isEmpty())
			{
				// WPD - index
				WebResource wpdIndex = c.resource(wpd.toString() + "/index");
				WebResource wpdIndexDC = c
						.resource(wpdIndex.toString() + "/dc");
				WebResource wpdIndexData = c.resource(wpdIndex.toString()
						+ "/data");
				WebResource wpdIndexMetadata = c.resource(wpdIndex.toString()
						+ "/metadata");

				// WPD - toc
				WebResource wpdToc = c.resource(wpd.toString() + "/toc");
				WebResource wpdTocDC = c.resource(wpdToc.toString() + "/dc");
				WebResource wpdTocData = c
						.resource(wpdToc.toString() + "/data");
				WebResource wpdTocMetadata = c.resource(wpdToc.toString()
						+ "/metadata");

				logger.debug("Upload: "
						+ dtlBean.getIndexLinks().get(0).getMe()
								.getAbsolutePath());
				data.path = new URI(dtlBean.getIndexLinks().get(0).getMe()
						.getAbsolutePath());
				data.mime = "text/xml";
				wpdIndexData.post(data);

				logger.debug("Upload: "
						+ dtlBean.getIndexLinks().get(0).getStream()
								.getAbsolutePath());
				data.path = new URI(dtlBean.getIndexLinks().get(0).getStream()
						.getAbsolutePath());
				data.mime = "text/html";
				wpdTocData.post(data);

				wpdIndexDC.post(new DCBeanAnnotated().addTitle("Index XML"));
				wpdTocDC.post(new DCBeanAnnotated().addTitle("Index Data"));

			}

		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try
		{
			DCBeanAnnotated dc = marc2dc(dtlBean);
			dc.addType(ObjectType.wpd.toString());

			wpdDC.post(DCBeanAnnotated.class, dc);

		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}

	}

	private void ingestReportsNew(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter("fedoraAdmin", "fedoraAdmin1"));

		WebResource report = c
				.resource("http://localhost:8080/edoweb2-api/report/"
						+ edowebNamespace + ":" + dtlBean.getPid());

		String request = "content";
		String response = report.put(String.class, request);
		logger.debug(response);

		WebResource reportDC = c.resource(report.toString() + "/dc");
		WebResource reportData = c.resource(report.toString() + "/data");
		WebResource reportMetadata = c
				.resource(report.toString() + "/metadata");
		UploadDataBean data = new UploadDataBean();
		try
		{
			data.path = new URI(dtlBean.getStream().getAbsolutePath());
			data.mime = "application/pdf";
			reportData.post(data);
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try
		{
			DCBeanAnnotated dc = marc2dc(dtlBean);
			dc.addType(ObjectType.report.toString());
			reportDC.post(DCBeanAnnotated.class, dc);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
	}

	private void ingestWebpage(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter("fedoraAdmin", "fedoraAdmin1"));

		WebResource webpage = c
				.resource("http://localhost:8080/edoweb2-api/webpage/"
						+ edowebNamespace + ":" + dtlBean.getPid());

		String request = "content";
		String response = webpage.put(String.class, request);
		logger.debug(response);

		WebResource webpageDC = c.resource(webpage.toString() + "/dc");
		WebResource webpageMetadata = c.resource(webpage.toString()
				+ "/metadata");

		String title = "";

		try
		{
			DCBeanAnnotated dc = marc2dc(dtlBean);
			dc.addType(ObjectType.webpage.toString());
			webpageDC.post(DCBeanAnnotated.class, dc);
			title = dc.getFirstTitle();
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		for (DigitalEntity b : dtlBean.getArchiveLinks())
		{
			String mimeType = b.getStreamMime();
			if (mimeType.compareTo("application/zip") != 0)
				continue;
			String version = b.getPid();
			WebResource webpageVersion = c.resource(webpage.toString()
					+ "/version/" + version);
			webpageVersion.put();
			WebResource webpageVersionDC = c.resource(webpageVersion.toString()
					+ "/dc");
			WebResource webpageVersionData = c.resource(webpageVersion
					.toString() + "/data");
			WebResource webpageVersionMetadata = c.resource(webpageVersion
					.toString() + "/metadata");

			UploadDataBean data = new UploadDataBean();

			try
			{
				data.path = new URI(b.getStream().getAbsolutePath());
				data.mime = mimeType;
				webpageVersionData.post(data);
			}
			catch (URISyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{
				DCBeanAnnotated dc = webpageVersionDC
						.get(DCBeanAnnotated.class);
				dc.addTitle("Version of: " + dtlBean.getPid() + " " + title);
				webpageVersionDC.post(DCBeanAnnotated.class, dc);
			}
			catch (Exception e)
			{
				logger.info(e.getMessage());
			}

		}

		// WebResource webpageCurrent = c.resource(webpage.toString() +
		// "/current/");
	}

	private void ingestEJournal(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter("fedoraAdmin", "fedoraAdmin1"));

		WebResource ejournal = c
				.resource("http://localhost:8080/edoweb2-api/ejournal/"
						+ edowebNamespace + ":" + dtlBean.getPid());

		String request = "content";
		String response = ejournal.put(String.class, request);
		logger.debug(response);

		WebResource ejournalDC = c.resource(ejournal.toString() + "/dc");
		WebResource ejournalMetadata = c.resource(ejournal.toString()
				+ "/metadata");
		try
		{
			DCBeanAnnotated dc = marc2dc(dtlBean);
			dc.addType(ObjectType.ejournal.toString());
			ejournalDC.post(DCBeanAnnotated.class, dc);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		for (DigitalEntity b : dtlBean.getViewMains())
		{
			String mimeType = b.getStreamMime();
			if (mimeType.compareTo("application/pdf") != 0)
				continue;
			String volName = b.getPid();
			WebResource ejournalVolume = c.resource(ejournal.toString()
					+ "/volume/" + volName);
			ejournalVolume.put();
			WebResource ejournalVolumeDC = c.resource(ejournalVolume.toString()
					+ "/dc");
			WebResource ejournalVolumeData = c.resource(ejournalVolume
					.toString() + "/data");
			WebResource ejournalVolumeMetadata = c.resource(ejournalVolume
					.toString() + "/metadata");

			UploadDataBean data = new UploadDataBean();

			try
			{
				data.path = new URI(b.getStream().getAbsolutePath());
				data.mime = mimeType;
				ejournalVolumeData.post(data);
			}
			catch (URISyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{
				DCBeanAnnotated dc = marc2dc(b);
				dc.addType(ObjectType.ejournalVolume.toString());
				ejournalVolumeDC.post(DCBeanAnnotated.class, dc);
			}
			catch (Exception e)
			{
				logger.debug(e.getMessage());
			}

		}

	}

	private DCBeanAnnotated marc2dc(DigitalEntity dtlBean)
	{
		try
		{
			StringWriter str = new StringWriter();
			TransformerFactory tFactory = TransformerFactory.newInstance();

			String xslFile = ClassLoader.getSystemResource(
					"MARC21slim2OAIDC.xsl").getPath();

			// TODO jar path
			Transformer transformer = tFactory
					.newTransformer(new StreamSource(ClassLoader
							.getSystemResourceAsStream("MARC21slim2OAIDC.xsl")));
			transformer.transform(new StreamSource(dtlBean.getMarcFile()),
					new StreamResult(str));

			String xmlStr = str.getBuffer().toString();
			logger.debug(xmlStr);
			DCBeanAnnotated dc = new DCBeanAnnotated(new DCBean(xmlStr));
			return dc;

		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		return null;
	}

	@Override
	public void update(DigitalEntity dtlBean)
	{
		// delete(dtlBean.getPid());
		ingest(dtlBean);
	}

	@Override
	public void delete(String pid)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter("fedoraAdmin", "fedoraAdmin1"));

		WebResource delete = c
				.resource("http://localhost:8080/edoweb2-api/edowebAdmin/deleteMirror/"
						+ pid);

		delete.delete();
	}

}
