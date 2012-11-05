package de.nrw.hbz.edoweb2.api;

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
	public void testJournal()
	{
		// ----------------Init------------------
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		c.addFilter(new HTTPBasicAuthFilter(properties.getProperty("user"),
				properties.getProperty("password")));

		WebResource journals = c.resource(properties.getProperty("apiUrl")
				+ "/ejournal/");
		WebResource aJournal = c.resource(journals.toString() + "edoweb:123");

		WebResource aJournalMetadata = c.resource(aJournal.toString()
				+ "/metadata");
		WebResource aJournalDc = c.resource(aJournal.toString() + "/dc");

		// --------------Clean up--------------------
		{
			String response = journals.delete(String.class);
			System.out.println(response);
			ObjectList list = journals.get(ObjectList.class);
			Assert.assertTrue(list.getList().isEmpty());
		}

		String request = "content";
		String response = aJournal.put(String.class, request);

		System.out.println(response);
		ObjectList list = journals.get(ObjectList.class);
		Assert.assertEquals("edoweb:123", list.getList().get(0));

		try
		{
			UploadDataBean metadata = new UploadDataBean();
			metadata.path = new URI(getClass().getResource("/test.ttl")
					.getPath());
			metadata.mime = "text/turtle";
			aJournalMetadata.post(metadata);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		try
		{
			DCBeanAnnotated dc = aJournalDc.get(DCBeanAnnotated.class);
			Vector<String> v = new Vector<String>();
			v.add("Test");
			dc.setCreator(v);
			aJournalDc.post(DCBeanAnnotated.class, dc);

			dc = aJournalDc.get(DCBeanAnnotated.class);
			Assert.assertEquals("Test", dc.getCreator().get(0));
		}
		catch (Exception e)
		{

		}

		WebResource aJournalVolumes = c.resource(aJournal.toString()
				+ "/volume/");
		WebResource aJournalVolume = c.resource(aJournal.toString()
				+ "/volume/2012-01");
		WebResource aJournalVolumeMetadata = c.resource(aJournalVolume
				.toString() + "/metadata");
		WebResource aJournalVolumeDc = c.resource(aJournalVolume.toString()
				+ "/dc");
		WebResource aJournalVolumeData = c.resource(aJournalVolume.toString()
				+ "/data");

		response = aJournalVolume.put(String.class);
		System.out.println(response);
		list = aJournalVolumes.get(ObjectList.class);
		Assert.assertEquals("2012-01", list.getList().get(0));

		try
		{
			UploadDataBean metadata = new UploadDataBean();
			metadata.path = new URI(getClass().getResource("/test.ttl")
					.getPath());
			metadata.mime = "text/turtle";
			aJournalVolumeMetadata.post(metadata);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		try
		{
			UploadDataBean data = new UploadDataBean();
			data.path = new URI(getClass().getResource("/test.pdf").getPath());
			data.mime = "application/pdf";
			aJournalVolumeData.post(data);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		try
		{
			DCBeanAnnotated dc = aJournalVolumeDc.get(DCBeanAnnotated.class);
			Vector<String> v = new Vector<String>();
			v.add("TestVolume");
			dc.setCreator(v);
			aJournalVolumeDc.post(DCBeanAnnotated.class, dc);

			dc = aJournalVolumeDc.get(DCBeanAnnotated.class);
			Assert.assertEquals("TestVolume", dc.getCreator().get(0));
		}
		catch (Exception e)
		{

		}

		response = journals.delete(String.class);
		System.out.println(response);
		list = journals.get(ObjectList.class);
		Assert.assertTrue(list.getList().isEmpty());
	}

	@After
	public void tearDown()
	{

	}

}
