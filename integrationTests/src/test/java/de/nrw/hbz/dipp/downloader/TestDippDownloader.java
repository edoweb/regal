package de.nrw.hbz.dipp.downloader;

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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class TestDippDownloader
{
	Properties properties = new Properties();
	/*
	 * 1637992 4676380 2258539 1638892 4628526
	 */
	String pid = "dipp:1";// "3237397";//
	private final String piddownloaderServer;
	private final String piddownloaderDownloadLocation;

	public TestDippDownloader()
	{
		try
		{
			properties = new Properties();
			properties.load(getClass().getResourceAsStream(
					"/testDippDownloader.properties"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		piddownloaderServer = properties.getProperty("piddownloader.server");
		piddownloaderDownloadLocation = properties
				.getProperty("piddownloader.downloadLocation");
	}

	@Before
	public void setUp()
	{
		try
		{

			FileUtils.deleteDirectory(new File(piddownloaderDownloadLocation
					+ File.separator + pid));

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void downloadPid()
	{

		DippDownloader downloader = new DippDownloader(piddownloaderServer,
				piddownloaderDownloadLocation);

		try
		{
			downloader.download(pid);
			Assert.assertTrue(new File(piddownloaderDownloadLocation
					+ File.separator + URLEncoder.encode(pid)).exists());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	@After
	public void tearDown()
	{

	}
}
