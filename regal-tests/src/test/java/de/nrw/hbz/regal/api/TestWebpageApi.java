package de.nrw.hbz.regal.api;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestWebpageApi
{
	@Before
	@After
	public void cleanUp() throws IOException
	{
		Utils utils = new Utils();
		try
		{
			utils.deleteNamespace("test");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		try
		{
			utils.deleteNamespace("textCM");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testResources() throws IOException
	{
		Resources resources = new Resources();

		CreateObjectBean input = new CreateObjectBean();
		input.type = "webpage";

		resources.create("1234", "test", input);
		input.type = "version";
		input.parentPid = "test:1234";
		resources.create("4567", "test", input);

	}
}
