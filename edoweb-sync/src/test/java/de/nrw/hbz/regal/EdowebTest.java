package de.nrw.hbz.regal;
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
import java.net.URISyntaxException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.nrw.hbz.regal.sync.EdowebMain;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class EdowebTest {
    String namespace;
    String password;
    String user;
    String fedoraUrl;
    String oaitimestamp;
    String pidlist;
    String oaiHost;
    String oaiSet;
    String fromScratch;
    String pidFule;
    String downloadHost;
    String localcache;

    /*
     * namespace=ubm password=schnasse user=jan
     * fedoraUrl=http://localhost:8080/fedora oaitimestamp=oaitimestamp-test
     * pidlist=pidl.txt
     * pidreporter.server=http://ubm.opus.hbz-nrw.de/oai2/oai2.php
     * pidreporter.set=has-source-swb:false pidreporter.harvestFromScratch=true
     * pidreporter.pidFile=pids.txt piddownloader.server=
     * "http://ubm.opus.hbz-nrw.de/oai2/oai2.php?verb=GetRecord&metadataPrefix=XMetaDissPlus&identifier=oai:ubm.opus.hbz-nrw.de:"
     * piddownloader.downloadLocation=/tmp/opus
     */
    @Before
    public void setUp() throws IOException {
	Properties properties = new Properties();
	properties.load(Thread.currentThread().getContextClassLoader()
		.getResourceAsStream("test.properties"));
	namespace = properties.getProperty("namespace");
	password = properties.getProperty("password");
	user = properties.getProperty("user");
	fedoraUrl = properties.getProperty("fedoraUrl");
	oaitimestamp = properties.getProperty("oaitimestamp");
	pidlist = properties.getProperty("pidlist");
	oaiHost = properties.getProperty("pidreporter.server");
	oaiSet = properties.getProperty("pidreporter.set");
	fromScratch = "true";
	downloadHost = properties.getProperty("piddownloader.server");
	localcache = properties.getProperty("piddownloader.downloadLocation");
    }

    @Test
    public void mainTest() throws URISyntaxException {

	pidlist = Thread.currentThread().getContextClassLoader()
		.getResource(pidlist).toString().substring(5);
	EdowebMain.main(new String[] { "--mode", "PIDL", "--user", user,
		"--password", password, "--dtl", downloadHost, "-cache",
		localcache, "--oai", oaiHost, "--set", oaiSet, "--timestamp",
		oaitimestamp, "--fedoraBase", fedoraUrl, "--host",
		"http://localhost", "-list", pidlist, "-namespace", "test" });
	EdowebMain.main(new String[] { "--mode", "DELE", "--user", user,
		"--password", password, "--dtl", downloadHost, "-cache",
		localcache, "--oai", oaiHost, "--set", oaiSet, "--timestamp",
		oaitimestamp, "--fedoraBase", fedoraUrl, "--host",
		"http://localhost", "-list", pidlist, "-namespace", "test" });
	File timestamp = new File(oaitimestamp);
	timestamp.deleteOnExit();

	File cache = new File(localcache);
	cache.deleteOnExit();

    }

}
