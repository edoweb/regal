package de.nrw.hbz.edoweb2.sync;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import java.text.ParseException;
//import java.util.Date;
//import de.nrw.hbz.dtl2fedora.util.ISO8601DateParser;

/**
 * Class TestDigitool2Fedora
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 09.06.2011
 * 
 */
public class TestEdoweb2Fedora
{

	Properties properties = null;
	private final String pidreporterServer;
	private final String pidreporterSet;
	private String pidreporterPidFile = null;
	private final String pidreporterTimestampFile;
	private final String piddownloaderServer;
	private final String piddownloaderDownloadLocation;
	private final String user;
	private final String password;
	private final String fedoraUrl;

	public TestEdoweb2Fedora()
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

		user = properties.getProperty("user");
		password = properties.getProperty("password");
		fedoraUrl = properties.getProperty("fedoraUrl");
		pidreporterServer = properties.getProperty("pidreporter.server");
		pidreporterSet = properties.getProperty("pidreporter.set");

		pidreporterTimestampFile = properties
				.getProperty("pidreporter.timestampFile");
		piddownloaderServer = properties.getProperty("piddownloader.server");
		piddownloaderDownloadLocation = properties
				.getProperty("piddownloader.downloadLocation");

		// provide a prepared pidList
		// pidreporterPidFile = properties.getProperty("pidreporter.pidFile");

	}

	/*
	 * 1637992 4676380 2258539 1638892 4628526 2238484
	 */
	@Before
	public void setUp()
	{
		pidreporterPidFile = getClass().getResource("/pidlist.txt").getPath();
		Main main = new Main();

		try
		{
			main.run("DELE", user, password, piddownloaderServer,
					piddownloaderDownloadLocation, pidreporterServer,
					pidreporterSet, pidreporterTimestampFile, fedoraUrl,
					pidreporterPidFile);
		}
		catch (Exception e)
		{

		}
		// try
		// {
		//
		// FileUtils.deleteDirectory(new File(piddownloaderDownloadLocation));
		//
		// }
		// catch (IOException e)
		// {
		// e.printStackTrace();
		// }
		// // System.out
		// // .println(piddownloaderDownloadLocation + " has been deleted!");
		// if (!new File(piddownloaderDownloadLocation).exists())
		// {
		// // boolean success =
		// (new File(piddownloaderDownloadLocation)).mkdirs();
		// // if (success)
		// // {
		// // // System.out.println(piddownloaderDownloadLocation
		// // // + " has been created!");
		// // }
		// }

	}

	@Test
	public void mainTest() throws IOException
	{
		Main main = new Main();

		main.run("PIDL", user, password, piddownloaderServer,
				piddownloaderDownloadLocation, pidreporterServer,
				pidreporterSet, pidreporterTimestampFile, fedoraUrl,
				pidreporterPidFile);

	}

	@After
	public void tearDown()
	{
		// Main main = new Main();
		//
		// main.run("DELE", user, password, piddownloaderServer,
		// piddownloaderDownloadLocation, pidreporterServer,
		// pidreporterSet, pidreporterTimestampFile, fedoraUrl,
		// pidreporterPidFile);
		//
		// try
		// {
		// FileUtils
		// .deleteDirectory(new File("piddownloaderDownloadLocation"));
		// }
		// catch (IOException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}
	// 1637996
	// 1638000
	// 1638004
	// 1638008
	// 1638012
	// 1638016
	// 1638020
	// 1638024
	// 1638028
	// 1638032
	// 1638036
	// 1637994
	// 1637998
	// 1638002
	// 1638006
	// 1638010
	// 1638014
	// 1638018
	// 1638022
	// 1638026
	// 1638030
	// 1638034
	// 1638038
}
