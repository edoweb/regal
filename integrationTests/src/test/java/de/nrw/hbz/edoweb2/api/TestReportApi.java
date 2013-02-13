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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

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
	public void testReport()
	{
		// ----------------Init------------------
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(properties.getProperty("user"),
				properties.getProperty("password")));

		WebResource monographs = c.resource(properties.getProperty("apiUrl")
				+ "/monograph/");
		WebResource myReport = c.resource(monographs.toString() + "test:123");
		WebResource myReportData = c.resource(myReport.toString() + "/data");
		WebResource myReportMetadata = c.resource(myReport.toString()
				+ "/metadata");
		WebResource myReportDc = c.resource(myReport.toString() + "/dc");

		// --------------Clean up--------------------
		{
			String response = monographs.delete(String.class);
			waitWorkaround();
			System.out.println(response);
			ObjectList list = monographs.get(ObjectList.class);
			Assert.assertTrue(list.getList().isEmpty());
		}

		String request = "content";
		String response = myReport.put(String.class, request);

		System.out.println(response);
		ObjectList list = monographs.get(ObjectList.class);
		Assert.assertEquals("test:123", list.getList().get(0));

		try
		{
			UploadDataBean data = new UploadDataBean();
			data.path = new URI(getClass().getResource("/test.pdf").getPath());
			data.mime = "application/pdf";
			myReportData.post(data);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		try
		{
			UploadDataBean metadata = new UploadDataBean();
			metadata.path = new URI(getClass().getResource("/test.ttl")
					.getPath());
			metadata.mime = "text/turtle";
			myReportMetadata.post(metadata);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		try
		{
			DCBeanAnnotated dc = myReportDc.get(DCBeanAnnotated.class);
			Vector<String> v = new Vector<String>();
			v.add("Test");
			dc.setCreator(v);
			myReportDc.post(DCBeanAnnotated.class, dc);
			waitWorkaround();
			dc = myReportDc.get(DCBeanAnnotated.class);
			Assert.assertEquals("Test", dc.getCreator().get(0));
		}
		catch (Exception e)
		{

		}

		response = monographs.delete(String.class);
		waitWorkaround();
		System.out.println(response);
		list = monographs.get(ObjectList.class);
		Assert.assertTrue(list.getList().isEmpty());
	}

	private void waitWorkaround()
	{
		/*
		 * Workaround START
		 */
		try
		{

			Thread.sleep(10000);

		}
		catch (InterruptedException e1)
		{

			e1.printStackTrace();
		}
		/*
		 * Workaround END
		 */
	}

	@After
	public void tearDown()
	{

	}
}