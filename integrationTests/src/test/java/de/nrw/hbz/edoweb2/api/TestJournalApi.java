package de.nrw.hbz.edoweb2.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class TestJournalApi
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
	public void testJournal() throws FileNotFoundException, IOException
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
					+ "/edowebAdmin/delete/edoweb:123");
			WebResource journals = c.resource(properties.getProperty("apiUrl")
					+ "/ejournal/");
			WebResource aJournal = c.resource(journals.toString()
					+ "edoweb:123");

			WebResource aJournalMetadata = c.resource(aJournal.toString()
					+ "/metadata");
			WebResource aJournalDc = c.resource(aJournal.toString() + "/dc");

			// --------------Clean up--------------------
			{
				try
				{
					String response = delete.delete(String.class);
					waitWorkaround();
					System.out.println(response);
					ObjectList list = journals.get(ObjectList.class);
					Assert.assertTrue(list.getList().isEmpty());
				}
				catch (UniformInterfaceException e)
				{
					System.out.println(e.getResponse().getEntity(String.class));
				}

			}

			String request = "content";
			String response = "";
			try
			{
				response = aJournal.put(String.class, request);
				System.out.println(response);
			}
			catch (UniformInterfaceException e)
			{
				System.out.println(e.getResponse().getEntity(String.class));
			}

			byte[] metadata = IOUtils.toByteArray(Thread.currentThread()
					.getContextClassLoader().getResourceAsStream("test.ttl"));
			aJournalMetadata.type("text/plain").post(metadata);

			try
			{
				DCBeanAnnotated dc = aJournalDc.get(DCBeanAnnotated.class);

				Vector<String> v = new Vector<String>();
				v.add("Test");
				dc.setCreator(v);
				aJournalDc.post(DCBeanAnnotated.class, dc);
				waitWorkaround();
				dc = aJournalDc.get(DCBeanAnnotated.class);
				Assert.assertEquals("Test", dc.getCreator().get(0));
				dc = aJournalDc.get(DCBeanAnnotated.class);

			}
			catch (Exception e)
			{

			}

			WebResource aJournalVolumes = c.resource(aJournal.toString()
					+ "/volume/");
			WebResource aJournalVolume = c.resource(aJournal.toString()
					+ "/volume/edoweb:345");
			WebResource aJournalVolumeMetadata = c.resource(aJournalVolume
					.toString() + "/metadata");
			WebResource aJournalVolumeDc = c.resource(aJournalVolume.toString()
					+ "/dc");
			WebResource aJournalVolumeData = c.resource(aJournalVolume
					.toString() + "/data");

			response = aJournalVolume.put(String.class);
			System.out.println(response);

			metadata = IOUtils.toByteArray(Thread.currentThread()
					.getContextClassLoader().getResourceAsStream("test.ttl"));
			aJournalVolumeMetadata.type("text/plain").post(metadata);

			byte[] data = IOUtils.toByteArray(Thread.currentThread()
					.getContextClassLoader().getResourceAsStream("test.pdf"));
			aJournalVolumeData.type("application/pdf").post(data);

			DCBeanAnnotated dc = aJournalVolumeDc.get(DCBeanAnnotated.class);
			Vector<String> v = new Vector<String>();
			v.add("TestVolume");
			dc.setCreator(v);
			aJournalVolumeDc.post(dc);
			waitWorkaround();
			dc = aJournalVolumeDc.get(DCBeanAnnotated.class);
			Assert.assertEquals("TestVolume", dc.getCreator().get(0));

			response = delete.delete(String.class);
			waitWorkaround();
			System.out.println(response);

		}
		catch (UniformInterfaceException e)
		{

			e.printStackTrace();
			System.out.println(e.getResponse().getEntity(String.class));
		}
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
