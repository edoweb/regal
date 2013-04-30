package de.nrw.hbz.edoweb2.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TestActions
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
	public void testFindByType() throws IOException
	{
		// TODO implement
		Actions actions = new Actions();
		for (String result : actions
				.findByType(ObjectType.monograph.toString()))
		{
			System.out.println(result);
		}

	}

	@Test
	public void epicur() throws IOException
	{
		Actions actions = new Actions();
		Assert.assertEquals("urn:nbn:de:edoweb-12340",
				actions.generateUrn("1234", "edoweb"));
		Assert.assertEquals("urn:nbn:de:edoweb-12357",
				actions.generateUrn("1235", "edoweb"));
		Assert.assertEquals("urn:nbn:de:edoweb-123476",
				actions.generateUrn("12347", "edoweb"));
		System.out.println(actions.epicur("1234", "edoweb"));
	}
}
