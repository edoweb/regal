package de.nrw.hbz.edoweb2.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

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
		Actions actions = new Actions();
		for (String result : actions
				.findByType(ObjectType.monograph.toString()))
		{
			System.out.println(result);
		}

	}
}
