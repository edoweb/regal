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
package de.nrw.hbz.regal.sync;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.nrw.hbz.regal.sync.ingest.OpusDownloader;

/**
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class TestOpusDownloader {
    Properties properties = new Properties();
    /*
     * 1637992 4676380 2258539 1638892 4628526
     */
    String pid = "1";// "3237397";//
    private final String piddownloaderServer;
    private final String piddownloaderDownloadLocation;

    public TestOpusDownloader() throws IOException {
	properties = new Properties();
	properties.load(getClass().getResourceAsStream(
		"/testOpusDownloader.properties"));
	piddownloaderServer = properties.getProperty("piddownloader.server");
	piddownloaderDownloadLocation = properties
		.getProperty("piddownloader.downloadLocation");
    }

    @Before
    public void setUp() throws IOException {
	FileUtils.deleteDirectory(new File(piddownloaderDownloadLocation));
    }

    @Test
    public void downloadPid() throws IOException {

	OpusDownloader downloader = new OpusDownloader();
	downloader.init(piddownloaderServer, piddownloaderDownloadLocation);
	downloader.download(pid);
	File file = new File(piddownloaderDownloadLocation + File.separator
		+ pid);
	Assert.assertTrue(file.exists());
	FileUtils.deleteDirectory(file);
	Assert.assertTrue(!file.exists());

    }

}
