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
package de.nrw.hbz.regal.api.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The OAISetBuilder creates OAISets from rdf statements
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
class OaiSetBuilder {
    final static Logger logger = LoggerFactory.getLogger(OaiSetBuilder.class);

    /**
     * @param subject
     *            the subject of rdf triple
     * @param predicate
     *            the predicate of rdf triple
     * @param object
     *            the object of rdf triple
     * @return a OAISet associated with the statement made by the triple
     */
    public OaiSet getSet(String subject, String predicate, String object) {
	String name = null;
	String spec = null;
	String pid = null;

	if (predicate.compareTo("http://purl.org/dc/terms/subject") == 0) {

	    if (object.startsWith("http://dewey.info/class/")) {
		String ddc = object.subSequence(object.length() - 4,
			object.length() - 1).toString();
		System.out.println("Found rdf ddc: " + ddc);

		name = ddcmap(ddc);
		spec = "ddc:" + ddc;
		pid = "oai:" + ddc;

	    }
	} else if (predicate.compareTo("http://hbz-nrw.de/regal#contentType") == 0) {
	    String docType = object;
	    name = docmap(docType);
	    spec = "contentType:" + docType;

	}

	return new OaiSet(name, spec, pid);
    }

    private String ddcmap(String number) {
	if (number == null || number.length() != 3)
	    logger.info("Didn't found ddc name for ddc:" + number);
	String name = "";
	try {
	    URL url = new URL("http://dewey.info/class/" + number
		    + "/2009-08/about.en");
	    HttpClient httpClient = new HttpClient();

	    HttpMethod method = new GetMethod(url.toString());
	    httpClient.executeMethod(method);
	    InputStream stream = method.getResponseBodyAsStream();
	    DocumentBuilderFactory factory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder docBuilder;
	    factory.setNamespaceAware(true);
	    factory.setExpandEntityReferences(false);
	    docBuilder = factory.newDocumentBuilder();

	    Document doc;

	    doc = docBuilder.parse(stream);
	    Element root = doc.getDocumentElement();
	    root.normalize();
	    try {
		name = root.getElementsByTagName("skos:prefLabel").item(0)
			.getTextContent();
		logger.info("Found ddc name: " + name);
	    } catch (Exception e) {
		logger.info("Didn't found ddc name for ddc:" + number);
	    }
	} catch (MalformedURLException e) {
	    logger.error(e.getMessage());
	} catch (HttpException e) {
	    logger.error(e.getMessage());
	} catch (IOException e) {
	    logger.error(e.getMessage());
	} catch (ParserConfigurationException e) {
	    logger.error(e.getMessage());
	} catch (SAXException e) {
	    logger.error(e.getMessage());
	}

	return name;
    }

    private String docmap(String type) {
	if (type.compareTo("report") == 0) {
	    return "Monograph";
	}
	if (type.compareTo("webpage") == 0) {
	    return "Webpage";
	}
	if (type.compareTo("ejournal") == 0) {
	    return "EJournal";
	}
	return "";
    }
}
