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
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Vector;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;

import de.nrw.hbz.regal.api.helper.ObjectType;
import de.nrw.hbz.regal.exceptions.ArchiveException;
import de.nrw.hbz.regal.fedora.CopyUtils;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class TestResource {

    Properties properties;
    Client c;
    String apiUrl;

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

	apiUrl = properties.getProperty("apiUrl");
	cleanUp();
    }

    @Test
    public void delete() throws FileNotFoundException, IOException,
	    URISyntaxException {
	for (ObjectType type : ObjectType.values()) {
	    delete(type);
	    deleteWS(type);
	}
    }

    public void deleteWS(ObjectType type) throws FileNotFoundException,
	    IOException {
	String pid = "test:d20c7a72-bb6c-40aa-bf12-ad38f7e0cb9c";

	createWS(pid, type);
	deleteWS(pid);

    }

    public void createWS(ObjectType type) throws FileNotFoundException,
	    IOException {
	String pid = "test:d20c7a72-bb6c-40aa-bf12-ad38f7e0cb9c";
	createWS(pid, type);
    }

    public void delete(ObjectType type) throws FileNotFoundException,
	    IOException, URISyntaxException {
	String namespace = "test";
	String pid = "d20c7a72-bb6c-40aa-bf12-ad38f7e0cb9c";
	create(pid, namespace, type);
	delete(pid, namespace);

    }

    public void create(ObjectType type) throws FileNotFoundException,
	    IOException {
	String namespace = "test";
	String pid = "d20c7a72-bb6c-40aa-bf12-ad38f7e0cb9c";
	create(pid, namespace, type);
    }

    private void delete(String pid, String namespace) throws IOException,
	    URISyntaxException {
	Resource resource = new Resource();
	resource.delete(pid, namespace);
	resource = new Resource();
	try {
	    resource.getReMAsJson(pid, namespace);
	    Assert.fail();
	} catch (ArchiveException e) {

	}
    }

    private void deleteWS(String pid) {
	WebResource resource = c.resource(properties.getProperty("apiUrl")
		+ "/resource/" + pid);
	resource.delete();
	try {
	    resource.get(String.class);
	    Assert.fail();
	} catch (UniformInterfaceException e) {

	}
    }

    private void create(String pid, String namespace, ObjectType type)
	    throws IOException {
	createResource(pid, namespace, type);
	uploadData(pid, namespace);
	uploadMetadata(pid, namespace);
	uploadDublinCore(pid, namespace);
	testDublinCore(pid, namespace);
    }

    private void testDublinCore(String pid, String namespace)
	    throws IOException {
	Resource resource = new Resource();
	DCBeanAnnotated dc = resource.readDC(pid, namespace);
	Assert.assertEquals("Test", dc.getCreator().get(0));
    }

    private void uploadDublinCore(String pid, String namespace)
	    throws IOException {
	Resource resource = new Resource();
	DCBeanAnnotated dc = new DCBeanAnnotated();
	dc.addCreator("Test");
	resource.updateDC(pid, namespace, dc);
    }

    private void uploadMetadata(String pid, String namespace)
	    throws IOException {
	Resource resource = new Resource();
	String content = CopyUtils.copyToString(Thread.currentThread()
		.getContextClassLoader().getResourceAsStream("test.nt"),
		"utf-8");
	resource.updateMetadata(pid, namespace, content);

    }

    private void uploadData(String pid, String namespace) throws IOException {
	// Resource Resource = new Resource();
	// MultiPart multiPart = new MultiPart();
	// multiPart.bodyPart(new StreamDataBodyPart("InputStream", Thread
	// .currentThread().getContextClassLoader()
	// .getResourceAsStream("test.pdf"), "test.pdf"));
	// multiPart.bodyPart(new BodyPart("application/pdf",
	// MediaType.TEXT_PLAIN_TYPE));
	// Resource.updateResourceData(pid, namespace, multiPart);

    }

    private void createResource(String pid, String namespace, ObjectType type)
	    throws IOException {
	Resource resource = new Resource();
	resource.create(pid, namespace, new CreateObjectBean(type));
    }

    private void createWS(String pid, ObjectType type) throws IOException {

	createResource(pid, type);
	uploadData(pid, type);
	uploadMetadata(pid, type);
	uploadDublinCore(pid, type);

	testDublinCore(pid, type);
    }

    private void testDublinCore(String pid, ObjectType type) {
	WebResource dc = c.resource(apiUrl + "/resource/" + pid + "/dc");
	DCBeanAnnotated content = dc.get(DCBeanAnnotated.class);
	Assert.assertEquals("Test", content.getCreator().get(0));
    }

    private void uploadDublinCore(String pid, ObjectType type) {
	WebResource dc = c.resource(apiUrl + "/resource/" + pid + "/dc");
	DCBeanAnnotated content = dc.get(DCBeanAnnotated.class);
	Vector<String> v = new Vector<String>();
	v.add("Test");
	content.setCreator(v);
	dc.accept("application/json").type("application/json").put(content);
    }

    private void uploadMetadata(String pid, ObjectType type) throws IOException {
	WebResource metadata = c.resource(apiUrl + "/resource/" + pid
		+ "/metadata");
	byte[] content = IOUtils.toByteArray(Thread.currentThread()
		.getContextClassLoader().getResourceAsStream("test.nt"));
	metadata.type("text/plain").post(content);
    }

    private void uploadData(String pid, ObjectType type) {
	WebResource data = c.resource(apiUrl + "/resource/" + pid + "/data");
	MultiPart multiPart = new MultiPart();
	multiPart.bodyPart(new StreamDataBodyPart("InputStream", Thread
		.currentThread().getContextClassLoader()
		.getResourceAsStream("test.pdf"), "test.pdf"));
	multiPart.bodyPart(new BodyPart("application/pdf",
		MediaType.TEXT_PLAIN_TYPE));
	data.type("multipart/mixed").post(multiPart);
    }

    private WebResource createResource(String pid, ObjectType type) {
	WebResource dc = c.resource(apiUrl + "/resource/" + pid);

	String response = dc.put(String.class, new CreateObjectBean(type));
	System.out.println(response);
	return dc;
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