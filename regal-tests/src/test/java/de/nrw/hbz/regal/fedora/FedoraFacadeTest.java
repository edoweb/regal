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
package de.nrw.hbz.regal.fedora;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.nrw.hbz.regal.api.helper.ContentModelFactory;
import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.datatypes.Vocabulary;

/**
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class FedoraFacadeTest {

    FedoraInterface facade = null;
    Node object = null;

    @Before
    public void setUp() throws IOException {
	Properties properties = new Properties();
	properties.load(getClass().getResourceAsStream("/test.properties"));

	// System.out.println(XmlSchemaCollection.class
	// .getResource("XmlSchemaCollection.class"));

	facade = FedoraFactory.getFedoraImpl(
		properties.getProperty("fedoraUrl"),
		properties.getProperty("user"),
		properties.getProperty("password"));

	object = new Node().setNamespace("test").setPID("test:234")
		.setLabel("Ein Testobjekt").setFileLabel("test")
		.setType(Vocabulary.TYPE_OBJECT);
	object.dublinCoreData.addTitle("Ein Testtitel");
	object.dublinCoreData.addCreator("Jan Schnasse");

	// object.addContentModel(ContentModelFactory.createMonographModel("test"));
	// object.addContentModel(ContentModelFactory.createHeadModel("test"));
	object.addContentModel(ContentModelFactory.createPdfModel("test",
		"http://localhost"));

	URL url = this.getClass().getResource("/test.pdf");
	object.setUploadData(url.getPath(), "application/pdf");

	List<String> result = facade
		.findPids("test:*", FedoraVocabulary.SIMPLE);
	for (String pid : result)
	    facade.deleteNode(pid);
	result = facade.findPids("testCM:*", FedoraVocabulary.SIMPLE);
	for (String pid : result)
	    facade.deleteNode(pid);
    }

    @Test
    public void createNode() {
	facade.createNode(object);
	Assert.assertTrue(facade.nodeExists(object.getPID()));
    }

    @Test(expected = FedoraFacade.NodeNotFoundException.class)
    public void testNodeNotFoundException() {
	facade.readNode(object.getPID());
    }

    @Test
    public void readNode() {

	facade.createNode(object);
	Node node = facade.readNode(object.getPID());

	Assert.assertEquals(0,
		node.getNodeType().compareTo(object.getNodeType()));
	Assert.assertEquals("test:234", node.getPID());
	Assert.assertEquals("test", node.getNamespace());
	Assert.assertEquals("Jan Schnasse",
		node.dublinCoreData.getFirstCreator());
	Assert.assertEquals("Ein Testobjekt", node.getLabel());
	Assert.assertEquals("test", node.getFileLabel());
	Assert.assertEquals("Ein Testtitel",
		node.dublinCoreData.getFirstTitle());
	Assert.assertEquals("application/pdf", node.getMimeType());

    }

    @Test
    public void updateNode() {
	facade.createNode(object);
	Vector<String> newTitle = new Vector<String>();
	newTitle.add("Neuer Titel");
	object.dublinCoreData.setTitle(newTitle);
	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("logback-test.xml");
	object.setUploadData(url.getPath(), "text/xml");
	facade.updateNode(object);
	Node readObject = facade.readNode(object.getPID());
	// System.out.println("DataUrl:" + readObject.getDataUrl().toString());
	Assert.assertEquals("Neuer Titel",
		readObject.dublinCoreData.getFirstTitle());
	Assert.assertEquals("logback-test.xml", readObject.getFileLabel());

    }

    @Test
    public void findObjects() {
	List<String> result = facade
		.findPids("test:*", FedoraVocabulary.SIMPLE);
	for (String pid : result)
	    facade.deleteNode(pid);
	Assert.assertEquals(0,
		facade.findPids("test:*", FedoraVocabulary.SIMPLE).size());
	if (!facade.nodeExists(object.getPID()))

	    facade.createNode(object);

	result = facade.findPids("test:*", FedoraVocabulary.SIMPLE);
	Assert.assertEquals(1, result.size());
    }

    @Test
    public void deleteNode() {
	if (!facade.nodeExists(object.getPID()))

	    facade.createNode(object);

	facade.deleteNode(object.getPID());
	Assert.assertFalse(facade.nodeExists(object.getPID()));
    }

    @Test
    public void makeContentModel() {
	facade.createNode(object);
	String namespace = "test";
	facade.updateContentModel(ContentModelFactory.createHeadModel(
		namespace, "http://localhost"));
	facade.updateContentModel(ContentModelFactory
		.createEJournalModel(namespace));
	facade.updateContentModel(ContentModelFactory
		.createMonographModel(namespace));
	facade.updateContentModel(ContentModelFactory
		.createWebpageModel(namespace));
	facade.updateContentModel(ContentModelFactory
		.createVersionModel(namespace));
	facade.updateContentModel(ContentModelFactory
		.createVolumeModel(namespace));
	facade.updateContentModel(ContentModelFactory.createPdfModel(namespace,
		"http://localhost"));

	// TODO Assertion

    }

    @Test
    public void nodeExists() {
	Assert.assertTrue(!facade.nodeExists(object.getPID()));
	facade.createNode(object);
	Assert.assertTrue(facade.nodeExists(object.getPID()));
    }

    @Test
    public void createHierarchy() {
	Node node = new Node();
	node.setPID(facade.getPid("test"));
	Node parent = facade.createRootObject("test");
	node = facade.createNode(parent, node);
	for (Link link : node.getRelsExt()) {
	    if (link.getPredicate().equals(FedoraVocabulary.IS_PART_OF)) {
		System.out.println(link.getObject());
		Assert.assertTrue(link.getObject().equals(
			"info:fedora/" + parent.getPID()));
	    }
	}
	for (Link link : parent.getRelsExt()) {
	    if (link.getPredicate().equals(FedoraVocabulary.HAS_PART)) {
		System.out.println(link.getObject());
		Assert.assertTrue(link.getObject().equals(
			"info:fedora/" + node.getPID()));
	    }
	}
    }

    @After
    public void tearDown() {
	List<String> result = facade
		.findPids("test:*", FedoraVocabulary.SIMPLE);
	for (String pid : result)
	    facade.deleteNode(pid);
	result = facade.findPids("testCM:*", FedoraVocabulary.SIMPLE);
	for (String pid : result)
	    facade.deleteNode(pid);
    }

}
