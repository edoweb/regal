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
package de.nrw.hbz.ellinet.sync.ingest;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import de.nrw.hbz.edoweb2.api.DCBeanAnnotated;
import de.nrw.hbz.edoweb2.api.ObjectType;
import de.nrw.hbz.edoweb2.api.TypeType;
import de.nrw.hbz.edoweb2.api.UploadDataBean;
import de.nrw.hbz.edoweb2.datatypes.ContentModel;
import de.nrw.hbz.edoweb2.sync.extern.DigitalEntity;
import de.nrw.hbz.edoweb2.sync.ingest.IngestInterface;
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
public class EllinetIngester implements IngestInterface
{
	final static Logger logger = LoggerFactory.getLogger(EllinetIngester.class);

	final static String ellinetNamespace = "ellinet";

	String user = null;
	String password = null;
	String host = null;

	public EllinetIngester(String usr, String pwd, String host)
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
		logger.info("Start ingest: " + ellinetNamespace + ":"
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

			if (partitionC.compareTo("HSS00DZM") == 0)
			{

				logger.info(pid + ": start ingesting ellinetObject");
				updateMonographs(dtlBean);
				logger.info(pid + ": end ingesting eJournal");

			}

			else
			{
				logger.info("Unknown type: " + partitionC
						+ ". No further actions performed.");
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
					+ ":8080/edoweb2-api/edowebAdmin/index/" + ellinetNamespace
					+ ":" + dtlBean.getPid());
			index.post();
			logger.info(pid + ": got indexed!");
			// WebResource oaiSet = c.resource(host
			// + ":8080/edoweb2-api/edowebAdmin/makeOaiSet/"
			// + ellinetNamespace + ":" + dtlBean.getPid());
			// oaiSet.post();
			logger.info(pid + ": got set! Thanx and goodbye!\n");
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

	}

	@Override
	public void update(DigitalEntity dtlBean)
	{
		ingest(dtlBean);
	}

	private void updateMonographs(DigitalEntity dtlBean)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));

		WebResource monograph = c.resource(host
				+ ":8080/edoweb2-api/monograph/" + ellinetNamespace + ":"
				+ dtlBean.getPid());
		try
		{
			String request = "content";
			monograph.put(String.class, request);
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
			// WebResource monographMetadata = c
			// .resource(monograph.toString() + "/metadata");

			UploadDataBean data = new UploadDataBean();
			try
			{
				String protocol = "file";
				String host = "";
				String path = dtlBean.getStream().getAbsolutePath();
				String fragment = "";
				data.path = new URI(protocol, host, path, fragment);
				data.mime = "application/pdf";
				monographData.post(data);
			}
			catch (URISyntaxException e)
			{

				e.printStackTrace();
			}

			DCBeanAnnotated dc = new DCBeanAnnotated();

			try
			{
				dc.add(new DCBeanAnnotated(new DCBean(dtlBean)));
				dc.addDescription(dtlBean.getLabel());
				dc.addType(TypeType.contentType + ":" + ObjectType.monograph);
				monographDC.post(dc);
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

	@Override
	public void delete(String pid)
	{
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(user, password));

		WebResource delete = c.resource(host
				+ ":8080/edoweb2-api/edowebAdmin/delete/" + ellinetNamespace
				+ ":" + pid);
		delete.delete();
	}
}
