package de.nrw.hbz.regal.api;

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
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class TestReportApi {

    Properties properties;
    Client c;

    @Before
    public void setUp() throws IOException {
	properties = new Properties();
	properties.load(getClass().getResourceAsStream("/test.properties"));
	ClientConfig cc = new DefaultClientConfig();
	cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
	cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
	c = Client.create(cc);
	c.addFilter(new HTTPBasicAuthFilter(properties.getProperty("user"),
		properties.getProperty("password")));

	cleanUp();
    }

    @Test
    public void testReport() throws FileNotFoundException, IOException {
	WebResource monographs = c.resource(properties.getProperty("apiUrl")
		+ "/monograph/");
	WebResource myReport = c.resource(monographs.toString()
		+ "test:d20c7a72-bb6c-40aa-bf12-ad38f7e0cb9c");
	WebResource myReportData = c.resource(myReport + "/data");
	WebResource myReportMetadata = c.resource(myReport + "/metadata");
	WebResource myReportDc = c.resource(myReport + "/dc");

	createResource(myReport);
	uploadData(myReportData);
	uploadMetadata(myReportMetadata);
	uploadDublinCore(myReportDc);

	testDublinCore(myReportDc);
    }

    private void testDublinCore(WebResource myReportDc) {
	DCBeanAnnotated dc = myReportDc.get(DCBeanAnnotated.class);
	Assert.assertEquals("Test", dc.getCreator().get(0));
    }

    private void uploadDublinCore(WebResource myReportDc) {
	DCBeanAnnotated dc = myReportDc.get(DCBeanAnnotated.class);
	Vector<String> v = new Vector<String>();
	v.add("Test");
	dc.setCreator(v);
	myReportDc.accept("application/json").type("application/json").post(dc);
    }

    private void uploadMetadata(WebResource myReportMetadata)
	    throws IOException {
	byte[] metadata = IOUtils.toByteArray(Thread.currentThread()
		.getContextClassLoader().getResourceAsStream("test.nt"));
	myReportMetadata.type("text/plain").post(metadata);
    }

    private void uploadData(WebResource myReportData) {
	MultiPart multiPart = new MultiPart();
	multiPart.bodyPart(new StreamDataBodyPart("InputStream", Thread
		.currentThread().getContextClassLoader()
		.getResourceAsStream("test.pdf"), "test.pdf"));
	multiPart.bodyPart(new BodyPart("application/pdf",
		MediaType.TEXT_PLAIN_TYPE));
	myReportData.type("multipart/mixed").post(multiPart);
    }

    private WebResource createResource(WebResource myReport) {
	WebResource myReportDc = c.resource(myReport.toString() + "/dc");
	String response = myReport.put(String.class);
	System.out.println(response);
	return myReportDc;
    }

    @After
    public void tearDown() {
	cleanUp();
    }

    public void cleanUp() {
	try {
	    WebResource deleteNs = c.resource(properties.getProperty("apiUrl")
		    + "/utils/deleteNamespace/test");
	    deleteNs.delete();
	} catch (Exception e) {

	}
    }
}