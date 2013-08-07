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

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class TestUpdateResource {

    Properties properties;
    Client client;
    String apiUrl;

    @Before
    public void setUp() {
	try {
	    properties = new Properties();
	    properties.load(getClass().getResourceAsStream("/test.properties"));

	    ClientConfig cc = new DefaultClientConfig();
	    cc.getProperties()
		    .put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
	    cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
		    true);
	    client = Client.create(cc);
	    client.addFilter(new HTTPBasicAuthFilter(properties
		    .getProperty("user"), properties.getProperty("password")));
	    apiUrl = properties.getProperty("apiUrl");
	    cleanUp();

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {

	}

    }

    // @After
    public void tearDown() throws IOException {
	cleanUp();
    }

    @Test
    public void testUpdate() throws FileNotFoundException, IOException {
	String monuri = createMonograph();
	String voluri = createJournal();
	String arturi = moveMonographAsArticleToJournal();

    }

    private String moveMonographAsArticleToJournal() {
	WebResource mRes = client.resource(apiUrl + "/resource/test:123");
	CreateObjectBean input = new CreateObjectBean();
	input.setType("article");
	input.setParentPid("test:234");
	String response = mRes.accept("application/json")
		.type("application/json").put(String.class, input);
	System.out.println("Request PUT " + mRes.toString() + "\nResponse: "
		+ response);
	return client.toString();
    }

    private String createJournal() {
	WebResource mRes = client.resource(apiUrl + "/resource/test:234");
	CreateObjectBean input = new CreateObjectBean();
	input.setType("journal");
	String response = mRes.accept("application/json")
		.type("application/json").put(String.class, input);
	System.out.println("Request PUT " + mRes.toString() + "\nResponse: "
		+ response);
	return client.toString();
    }

    private String createMonograph() {
	WebResource mRes = client.resource(apiUrl + "/resource/test:123");
	CreateObjectBean input = new CreateObjectBean();
	input.setType("monograph");
	String response = mRes.accept("application/json")
		.type("application/json").put(String.class, input);
	System.out.println("Request PUT " + mRes.toString() + "\nResponse: "
		+ response);
	return client.toString();
    }

    public void cleanUp() {
	try {
	    WebResource deleteNs = client.resource(apiUrl
		    + "/utils/deleteNamespace/test");
	    String response = deleteNs.delete(String.class);
	    System.out.println(response);
	} catch (Exception e) {

	}
    }
}