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
	}

	@Test
	public void mainTest() throws IOException
	{
		System.out
				.println("de.nrw.hbz.edoweb2.sync.TestEdoweb2Fedora.java : Please comment out to run this test!");
		Main main = new Main();
		pidreporterPidFile = getClass().getResource("/pidlist.txt").getPath();

		main.run("PIDL", user, password, piddownloaderServer,
				piddownloaderDownloadLocation, pidreporterServer,
				pidreporterSet, pidreporterTimestampFile, fedoraUrl,
				"http://localhost", pidreporterPidFile);

	}
}
