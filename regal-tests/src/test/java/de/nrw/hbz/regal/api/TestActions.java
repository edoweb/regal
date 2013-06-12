package de.nrw.hbz.regal.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.nrw.hbz.regal.api.helper.Actions;
import de.nrw.hbz.regal.api.helper.ObjectType;
import de.nrw.hbz.regal.datatypes.Node;

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
			Actions actions = new Actions();
			try
			{
				actions.deleteNamespace("test");
			}
			catch (Exception e)
			{

			}
			try
			{

				actions.deleteNamespace("testCM");
			}
			catch (Exception e)
			{

			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	// @Test
	public void testFindByType() throws IOException
	{
		// TODO implement
		Actions actions = new Actions();
		for (String result : actions
				.findByType(ObjectType.monograph.toString()))
		{
			Node node = actions.readNode(result);
			String type = node.getContentType();

			if (type == null || type.isEmpty())
				Assert.fail();
			else if (ObjectType.monograph.toString().compareTo(type) != 0)
			{
				Assert.fail();
			}
		}

		for (String result : actions.findByType(ObjectType.journal.toString()))
		{
			Node node = actions.readNode(result);
			String type = node.getContentType();

			if (type == null || type.isEmpty())
				Assert.fail();
			else if (ObjectType.journal.toString().compareTo(type) != 0)
			{
				Assert.fail();
			}
		}

		for (String result : actions.findByType(ObjectType.volume.toString()))
		{
			Node node = actions.readNode(result);
			String type = node.getContentType();

			if (type == null || type.isEmpty())
				Assert.fail();
			else if (ObjectType.volume.toString().compareTo(type) != 0)
			{
				Assert.fail();
			}
		}

		for (String result : actions.findByType(ObjectType.webpage.toString()))
		{
			Node node = actions.readNode(result);
			String type = node.getContentType();

			if (type == null || type.isEmpty())
				Assert.fail();
			else if (ObjectType.webpage.toString().compareTo(type) != 0)
			{
				Assert.fail();
			}
		}

	}

	// @Test
	public void testCreation()
	{
		try
		{
			Resource resources = new Resource();
			CreateObjectBean input = new CreateObjectBean();
			input.setType("monograph");
			resources.create("123", "test", input);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	// @Test
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

		String str = "<http://lobid.org/resource/HT015456932> <http://purl.org/vocab/frbr/core#exemplar> <http://lobid.org/item/HT015456932%3AElektronische+Publikation> .";
		str = Pattern.compile("http://lobid.org/resource/HT015456932")
				.matcher(str)
				.replaceAll(Matcher.quoteReplacement("edoweb:1234"));
		System.out.println(str);
	}

	@Test
	public void html() throws IOException
	{
		Actions actions = new Actions();
		String str = actions.getReM("edoweb:2470307", "text/html");
		System.out.println(str);
	}

	@After
	public void tearDown() throws IOException
	{
		Actions actions = new Actions();
		try
		{
			actions.deleteNamespace("test");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		try
		{
			actions.deleteNamespace("testCM");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
