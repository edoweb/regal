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
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.nrw.hbz.regal.sync.ingest.DigitoolDownloader;
import de.nrw.hbz.regal.sync.ingest.DownloaderInterface;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class TestDigitoolDownloader {
    Properties properties = new Properties();

    private final String piddownloaderServer;
    private final String piddownloaderDownloadLocation;

    public TestDigitoolDownloader() throws IOException {

	properties = new Properties();
	properties.load(getClass().getResourceAsStream(
		"/testEdowebDownloader.properties"));

	piddownloaderServer = properties.getProperty("piddownloader.server");
	piddownloaderDownloadLocation = properties
		.getProperty("piddownloader.downloadLocation");
	File dir = new File(piddownloaderDownloadLocation);
	dir.mkdirs();
    }

    @Before
    public void setUp() {

    }

    @Test
    public void downloadPid() throws IOException {
	FileUtils.deleteDirectory(new File(piddownloaderDownloadLocation
		+ File.separator + "3025500"));
	DownloaderInterface downloader = new DigitoolDownloader();
	downloader.init(piddownloaderServer, piddownloaderDownloadLocation);
	downloader.download("3025500");
	Assert.assertTrue(new File(piddownloaderDownloadLocation
		+ File.separator + "3025500").exists());
	FileUtils.deleteDirectory(new File(piddownloaderDownloadLocation
		+ File.separator + "3025500"));
	downloader.download("3237400");
    }

    @After
    public void tearDown() {

    }
}
