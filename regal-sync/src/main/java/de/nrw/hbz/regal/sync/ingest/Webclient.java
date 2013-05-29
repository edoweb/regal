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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;

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

import de.nrw.hbz.regal.api.CreateObjectBean;
import de.nrw.hbz.regal.api.DCBeanAnnotated;
import de.nrw.hbz.regal.api.ObjectType;
import de.nrw.hbz.regal.sync.extern.DigitalEntity;

/**
 * Webclient collects typical api-calls and make them available in the
 * regal-sync module
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class Webclient
{
	final static Logger logger = LoggerFactory.getLogger(Webclient.class);

	String namespace = null;
	String endpoint = null;
	String host = null;
	Client webclient = null;

	/**
	 * @param namespace
	 *            The namespace is used to prefix pids for resources
	 * @param user
	 *            a valid user to authenticate to the webapi
	 * @param password
	 *            a password for the webapi
	 * @param host
	 *            the host of the api. it is assumed that the regal-api is
	 *            available under host:8080/api
	 */
	public Webclient(String namespace, String user, String password, String host)
	{
		this.host = host;
		this.namespace = namespace;
		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(MultiPartWriter.class);
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		cc.getProperties().put(
				DefaultApacheHttpClientConfig.PROPERTY_CHUNKED_ENCODING_SIZE,
				1024);
		webclient = Client.create(cc);
		webclient.addFilter(new HTTPBasicAuthFilter(user, password));
		endpoint = host + ":8080/api";
	}

	/**
	 * Metadata performs typical metadata related api-actions like update the dc
	 * stream enrich with catalogdata. Add the object to the searchindex and
	 * provide it on the oai interface.
	 * 
	 * @param dtlBean
	 *            A DigitalEntity to operate on
	 */
	public void metadata(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		String resource = endpoint + "/resources/" + pid;
		try
		{
			updateDC(resource + "/dc", dtlBean);
		}
		catch (Exception e)
		{
			logger.error(dtlBean.getPid() + " " + e.getMessage());
		}
		try
		{
			lobidify(dtlBean);
		}
		catch (Exception e)
		{
			logger.error(dtlBean.getPid() + " " + e.getMessage());
		}
		try
		{
			index(dtlBean);
		}
		catch (Exception e)
		{
			logger.error(dtlBean.getPid() + " " + e.getMessage());
		}
		try
		{
			oaiProvide(dtlBean);
		}
		catch (Exception e)
		{
			logger.error(dtlBean.getPid() + " " + e.getMessage());
		}

	}

	/**
	 * Metadata performs typical metadata related api-actions like update the dc
	 * stream enrich with catalogdata. Add the object to the searchindex and
	 * provide it on the oai interface.
	 * 
	 * @param dtlBean
	 *            A DigitalEntity to operate on
	 */
	public void metadata(DigitalEntity dtlBean, String metadata)
	{
		metadata(dtlBean);
		String pid = namespace + ":" + dtlBean.getPid();
		String resource = endpoint + "/resources/" + pid;
		String m = "";
		try
		{
			logger.info("Metadata: " + metadata);
			m = readMetadata(resource + "/metadata", dtlBean);

		}
		catch (Exception e)
		{
			logger.error(dtlBean.getPid() + " " + e.getMessage());
		}
		try
		{
			String merge = mergeMetadata(m, metadata);
			logger.info("MERGE: " + metadata);
			updateMetadata(resource + "/metadata", merge);
		}
		catch (Exception e)
		{
			logger.error(dtlBean.getPid() + " " + e.getMessage());
		}

	}

	private String mergeMetadata(String m, String metadata)
	{
		return m + "\n" + metadata;
	}

	/**
	 * @param dtlBean
	 *            A DigitalEntity to operate on.
	 * @param expectedMime
	 *            The expected mimetype of the main datastream
	 * @param type
	 *            The Object type
	 */
	public void createObject(DigitalEntity dtlBean, String expectedMime,
			ObjectType type)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		String resource = endpoint + "/resources/" + pid;
		String data = resource + "/data";

		createResource(type, dtlBean);

		if (dtlBean.getStreamMime() != null
				&& dtlBean.getStreamMime().compareTo(expectedMime) != 0)
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
			if (fulltextObject == null)
			{
				for (DigitalEntity view : dtlBean.getViewLinks())
				{

					if (view.getStreamMime().compareTo(expectedMime) == 0)
					{
						fulltextObject = view;
						break;
					}
				}
			}
			if (fulltextObject != null)
			{
				updateData(data, fulltextObject);
			}
			else
			{
				logger.warn(pid + " found no valid data.");
				logger.info(pid + " expected " + expectedMime + " , found "
						+ dtlBean.getStreamMime());
			}
		}
		else
		{
			updateData(data, dtlBean);
		}
		updateLabel(resource, dtlBean);
	}

	/**
	 * @param type
	 *            The ObjectType .
	 * @param dtlBean
	 *            The DigitalEntity to operate on
	 */
	public void createResource(ObjectType type, DigitalEntity dtlBean)
	{

		String pid = namespace + ":" + dtlBean.getPid();
		String ppid = dtlBean.getParentPid();

		String parentPid = namespace + ":" + ppid;
		String resourceUrl = endpoint + "/resources/" + pid;
		WebResource resource = webclient.resource(resourceUrl);
		CreateObjectBean input = new CreateObjectBean();
		input.setType(type.toString());
		logger.debug(pid + " type: " + input.getType());
		if (ppid != null && !ppid.isEmpty())
		{
			logger.debug("Parent: " + dtlBean.getParentPid());
			input.setParentPid(parentPid);

		}

		try
		{
			resource.put(input);
		}
		catch (UniformInterfaceException e)
		{
			logger.info(pid + " " + e.getMessage());
		}
	}

	private void updateDC(String url, DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource webpageDC = webclient.resource(url);

		DCBeanAnnotated dc = new DCBeanAnnotated();

		try
		{

			if (dtlBean.getMarcFile() != null)
				dc.add(marc2dc(dtlBean));
			else if (dtlBean.getDc() != null)
			{
				dc.add(new DCBeanAnnotated(dtlBean.getDc()));
			}
			else
			{
				logger.warn(pid
						+ " not able to create dublin core data. No Marc or DC metadata found.");
			}

			dc.addDescription(dtlBean.getLabel());
			webpageDC.put(dc);

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

	private String readMetadata(String url, DigitalEntity dtlBean)
	{
		WebResource metadataRes = webclient.resource(url);
		return metadataRes.get(String.class);
	}

	private void updateMetadata(String url, String metadata)
	{
		WebResource metadataRes = webclient.resource(url);
		metadataRes.put(metadata);
	}

	private void updateLabel(String url, DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource webpageDC = webclient.resource(url + "/dc");

		DCBeanAnnotated dc = new DCBeanAnnotated();

		try
		{
			dc.addTitle("Version of: " + pid);
			dc.addDescription(dtlBean.getLabel());
			webpageDC.put(dc);

		}
		catch (UniformInterfaceException e)
		{
			logger.info(pid + " " + e.getMessage());
			// e.printStackTrace();
		}
		catch (Exception e)
		{
			logger.debug(pid + " " + e.getMessage());
			// e.printStackTrace();
		}
	}

	private void updateData(String url, DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource data = webclient.resource(url);

		try
		{
			logger.info(pid + " Update data: " + dtlBean.getStreamMime());
			MultiPart multiPart = new MultiPart();
			multiPart.bodyPart(new StreamDataBodyPart("InputStream",
					new FileInputStream(dtlBean.getFirstStream()), dtlBean
							.getFirstStream().getName()));
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
					+ dtlBean.getFirstStream().getAbsolutePath());
		}
		catch (Exception e)
		{
			logger.error(pid + " " + e.getMessage());
		}

	}

	private void lobidify(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource lobid = webclient.resource(endpoint + "/utils/lobidify/"
				+ namespace + ":" + dtlBean.getPid());
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

			WebResource index = webclient.resource(endpoint + "/utils/index/"
					+ pid);
			index.post();
			logger.info(pid + ": got indexed!");
		}
		catch (UniformInterfaceException e)
		{
			logger.warn(pid + " " + "Not indexed! "
					+ e.getResponse().getEntity(String.class));
		}
		catch (Exception e)
		{
			logger.warn(pid + " " + "Not indexed! " + e.getMessage());
		}
	}

	private void oaiProvide(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource oaiSet = webclient.resource(endpoint + "/utils/makeOaiSet/"
				+ namespace + ":" + dtlBean.getPid());
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
			Transformer transformer = tFactory
					.newTransformer(new StreamSource(ClassLoader
							.getSystemResourceAsStream("MARC21slim2OAIDC.xsl")));
			transformer.transform(new StreamSource(dtlBean.getMarcFile()),
					new StreamResult(str));
			String xmlStr = str.getBuffer().toString();
			DCBeanAnnotated dc = new DCBeanAnnotated(xmlStr);
			return dc;

		}
		catch (Throwable t)
		{
			logger.warn(pid + " " + t.getCause().getMessage());
		}
		return null;
	}

	/**
	 * 
	 * @param p
	 *            A pid to delete
	 */
	public void delete(String p)
	{
		String pid = namespace + ":" + p;

		WebResource delete = webclient.resource(endpoint + "/resources/" + pid);
		try
		{
			delete.delete();
		}
		catch (UniformInterfaceException e)
		{
			logger.info(pid + " Can't delete!" + e.getMessage());
		}
	}
}
