package de.nrw.hbz.edoweb2.api;

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class TestReportApi
{

	Properties properties;

	@Before
	public void setUp()
	{
		try
		{
			properties = new Properties();
			properties.load(getClass().getResourceAsStream("/test.properties"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testReport() throws FileNotFoundException, IOException
	{
		try
		{
			// ----------------Init------------------
			ClientConfig cc = new DefaultClientConfig();
			cc.getProperties()
					.put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
			cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
					true);
			Client c = Client.create(cc);
			c.addFilter(new HTTPBasicAuthFilter(properties.getProperty("user"),
					properties.getProperty("password")));

			WebResource delete = c.resource(properties.getProperty("apiUrl")
					+ "/edowebAdmin/delete/test:123");
			WebResource monographs = c.resource(properties
					.getProperty("apiUrl") + "/monograph/");
			WebResource myReport = c.resource(monographs.toString()
					+ "test:123");
			WebResource myReportData = c
					.resource(myReport.toString() + "/data");
			WebResource myReportMetadata = c.resource(myReport.toString()
					+ "/metadata");
			WebResource myReportDc = c.resource(myReport.toString() + "/dc");
			WebResource deleteNs = c.resource(properties.getProperty("apiUrl")
					+ "/utils/deleteNamespace/test");

			// --------------Clean up--------------------
			{
				try
				{
					String response = deleteNs.delete(String.class);
					System.out.println(response);

				}
				catch (UniformInterfaceException e)
				{
					System.out.println(e.getMessage());
				}
			}

			String request = "content";
			String response = myReport.put(String.class, request);

			try
			{
				myReport.put(String.class, request);

			}
			catch (UniformInterfaceException e)
			{
				System.out.println(e.getResponse().getEntity(String.class));
			}

			System.out.println(response);

			MultiPart multiPart = new MultiPart();
			multiPart.bodyPart(new StreamDataBodyPart("InputStream", Thread
					.currentThread().getContextClassLoader()
					.getResourceAsStream("test.pdf"), "test.pdf"));
			multiPart.bodyPart(new BodyPart("application/pdf",
					MediaType.TEXT_PLAIN_TYPE));
			myReportData.type("multipart/mixed").post(multiPart);

			byte[] metadata = IOUtils.toByteArray(Thread.currentThread()
					.getContextClassLoader().getResourceAsStream("test.ttl"));
			myReportMetadata.type("text/plain").post(metadata);

			try
			{
				DCBeanAnnotated dc = myReportDc.get(DCBeanAnnotated.class);
				Vector<String> v = new Vector<String>();
				v.add("Test");
				dc.setCreator(v);
				myReportDc.post(DCBeanAnnotated.class, dc);

				dc = myReportDc.get(DCBeanAnnotated.class);
				Assert.assertEquals("Test", dc.getCreator().get(0));
			}
			catch (Exception e)
			{

			}

			// --------------Clean up--------------------
			{
				response = deleteNs.delete(String.class);
				System.out.println(response);

			}
		}
		catch (UniformInterfaceException e)
		{
			e.printStackTrace();
			System.out.println(e.getResponse().getEntity(String.class));
		}

	}
}