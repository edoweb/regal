package de.nrw.hbz.regal.sync;

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
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.Before;

/**
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class TestEdoweb2Fedora {

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

    public TestEdoweb2Fedora() {
	try {
	    properties = new Properties();
	    properties.load(getClass().getResourceAsStream("/test.properties"));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
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

    @Before
    public void cleanUp() throws IOException {
	File dir = new File("/tmp/edoweb/test");
	if (dir.exists())
	    FileUtils.deleteDirectory(dir);
	dir.mkdirs();
    }

    @SuppressWarnings("static-access")
    // @Test
    public void mainTest() throws IOException {
	Main main = new Main();
	pidreporterPidFile = getClass().getResource("/pidlist.txt").getPath();
	main.main(new String[] { "--mode", "DELE", "--user", user,
		"--password", password, "--dtl", piddownloaderServer, "-cache",
		piddownloaderDownloadLocation, "--oai", pidreporterServer,
		"--set", pidreporterSet, "--timestamp",
		pidreporterTimestampFile, "--fedoraBase", fedoraUrl, "--host",
		"http://localhost", "-list", pidreporterPidFile, "-namespace",
		"test" });
	main.main(new String[] { "--mode", "PIDL", "--user", user,
		"--password", password, "--dtl", piddownloaderServer, "-cache",
		piddownloaderDownloadLocation, "--oai", pidreporterServer,
		"--set", pidreporterSet, "--timestamp",
		pidreporterTimestampFile, "--fedoraBase", fedoraUrl, "--host",
		"http://localhost", "-list", pidreporterPidFile, "-namespace",
		"test" });

	// main.main(new String[] { "--mode", "UPDT", "--user", user,
	// "--password", password, "--dtl", piddownloaderServer, "-cache",
	// piddownloaderDownloadLocation, "--oai", pidreporterServer,
	// "--set", pidreporterSet, "--timestamp",
	// pidreporterTimestampFile, "--fedoraBase", fedoraUrl, "--host",
	// "http://localhost", "-list", pidreporterPidFile, "-namespace",
	// "test" });
	//

    }
}
