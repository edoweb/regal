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
package de.nrw.hbz.regal.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;

/**
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class TestJournalApi {

    Properties properties;
    Client c;
    String apiUrl;

    @Before
    public void setUp() throws IOException {

	properties = new Properties();
	properties.load(getClass().getResourceAsStream("/test.properties"));
	apiUrl = properties.getProperty("apiUrl");
	cleanUp();
	// ----------------Init------------------
	ClientConfig cc = new DefaultClientConfig();
	cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
	cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
	c = Client.create(cc);
	c.addFilter(new HTTPBasicAuthFilter(properties.getProperty("user"),
		properties.getProperty("password")));
    }

    @Test
    public void testJournal() throws FileNotFoundException, IOException {

	WebResource journals = c.resource(apiUrl + "/journal/");
	WebResource aJournal = c.resource(journals.toString() + "test:123");
	WebResource aJournalMetadata = c.resource(aJournal.toString()
		+ "/metadata");
	WebResource aJournalDc = c.resource(aJournal.toString() + "/dc");

	createJournal(aJournal);
	uploadMetadata(aJournalMetadata);
	uploadDublinCore(aJournalDc);
	testDublinCore(aJournalDc);

	c.resource(aJournal.toString() + "/volume/");
	WebResource aJournalVolume = c.resource(aJournal.toString()
		+ "/volume/test:345");
	WebResource aJournalVolumeMetadata = c.resource(aJournalVolume
		.toString() + "/metadata");
	WebResource aJournalVolumeDc = c.resource(aJournalVolume.toString()
		+ "/dc");
	WebResource aJournalVolumeData = c.resource(aJournalVolume.toString()
		+ "/data");

	createVolume(aJournalVolume);
	uploadData(aJournalVolumeData);
	uploadMetadata(aJournalVolumeMetadata);
	uploadDublinCore(aJournalVolumeDc);
	testDublinCore(aJournalVolumeDc);

    }

    private void uploadData(WebResource aJournalVolumeData) {
	MultiPart multiPart = new MultiPart();
	multiPart.bodyPart(new StreamDataBodyPart("InputStream", Thread
		.currentThread().getContextClassLoader()
		.getResourceAsStream("test.pdf"), "test.pdf"));
	multiPart.bodyPart(new BodyPart("application/pdf",
		MediaType.TEXT_PLAIN_TYPE));
	aJournalVolumeData.type("multipart/mixed").post(multiPart);
    }

    private void createVolume(WebResource aJournalVolume) {
	String response = aJournalVolume.put(String.class);
	System.out.println(response);
    }

    private void testDublinCore(WebResource aJournalDc) {
	DCBeanAnnotated dc = aJournalDc.get(DCBeanAnnotated.class);
	Assert.assertEquals("Test", dc.getCreator().get(0));
	dc = aJournalDc.get(DCBeanAnnotated.class);
    }

    private void uploadDublinCore(WebResource aJournalDc) {
	DCBeanAnnotated dc = aJournalDc.get(DCBeanAnnotated.class);

	Vector<String> v = new Vector<String>();
	v.add("Test");
	dc.setCreator(v);
	aJournalDc.post(dc);
    }

    private void uploadMetadata(WebResource aJournalMetadata)
	    throws IOException {
	byte[] metadata = IOUtils.toByteArray(Thread.currentThread()
		.getContextClassLoader().getResourceAsStream("test.nt"));
	aJournalMetadata.type("text/plain").put(metadata);
    }

    private void createJournal(WebResource aJournal) {
	String response = aJournal.put(String.class);
	System.out.println(response);
    }

    @After
    public void tearDown() {
	cleanUp();
    }

    public void cleanUp() {
	try {
	    WebResource deleteNs = c.resource(apiUrl
		    + "/utils/deleteNamespace/test");
	    String response = deleteNs.delete(String.class);
	    System.out.println(response);
	} catch (Exception e) {

	}

    }
}
