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
package de.nrw.hbz.regal;

import static de.nrw.hbz.regal.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.regal.datatypes.Vocabulary.TYPE_OBJECT;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.nrw.hbz.regal.ArchiveFactory;
import de.nrw.hbz.regal.ArchiveInterface;
import de.nrw.hbz.regal.datatypes.ComplexObject;
import de.nrw.hbz.regal.datatypes.ComplexObjectNode;
import de.nrw.hbz.regal.datatypes.ContentModel;
import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.fedora.FedoraVocabulary;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class ArchiveIntegrationTest
{

	ArchiveInterface archive = null;

	Properties properties = null;
	Node rootObject = null;
	ComplexObject object = null;

	@Before
	public void setUp()
	{

		try
		{
			properties = new Properties();
			properties.load(getClass().getResourceAsStream("/test.properties"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		archive = ArchiveFactory.getArchiveImpl(
				properties.getProperty("fedoraUrl"),
				properties.getProperty("user"),
				properties.getProperty("password"));

		rootObject = new Node();
		rootObject.setNodeType(TYPE_OBJECT);
		Link link = new Link();
		link.setPredicate(REL_IS_NODE_TYPE);
		link.setObject(TYPE_OBJECT, true);
		rootObject.addRelation(link);
		rootObject.setNamespace("test").setPID("test:234")
				.addCreator("Jan Schnasse")
				.setLabel("Ein komplexes Testobjekt").addTitle("Ein Testtitel");

		ContentModel cm = new ContentModel();

		cm.addPrescribedDs("DC", "http://www.openarchives.org/OAI/2.0/oai_dc/",
				"text/xml");
		cm.addPrescribedDs("RELS-EXT",
				"info:fedora/fedora-system:FedoraRELSExt-1.0",
				"application/rdf+xml");

		cm.setContentModelPID("test:HBZNodeModel");
		cm.setServiceDefinitionPID("test:HBZNodeServiceDefinition");
		cm.setServiceDeploymentPID("test:HBZNodeServiceDeployment");
		cm.addMethod(
				"listParents",
				"http://localhost:8080/AdditionalServices/services/HBZNodeServices/ListParents?pid=(pid)");
		cm.addMethod(
				"dc",
				"http://localhost:8080/AdditionalServices/services/HBZNodeServices/ListDCStream?pid=(pid)");
		cm.addMethod(
				"listChildren",
				"http://localhost:8181/AdditionalServices/services/HBZNodeServices/ListChildren?pid=(pid)");

		rootObject.addContentModel(cm);

		object = new ComplexObject(rootObject);
		object.addChild(new ComplexObjectNode(new Node().addCreator(
				"Der kleine Jan").setLabel("Ein Kindobjekt")));

		List<String> objects = archive.findNodes("test:*");
		for (String pid : objects)
		{
			archive.deleteNode(pid);
		}

		objects = archive.findNodes("testCM:*");
		for (String pid : objects)
		{
			archive.deleteNode(pid);
		}

	}

	@Test
	public void createObject()
	{
		try
		{
			archive.createComplexObject(object);
			Assert.assertTrue(archive.nodeExists(object.getRoot().getPID()));
			Assert.assertEquals(5, archive.findNodes("test:*").size());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void readObject()
	{
		try
		{
			archive.createComplexObject(object);
			Node obj = archive.readObject(object.getRoot().getPID());

			// System.out.println(obj.getPID());
			Assert.assertEquals(0,
					object.getRoot().getNodeType().compareTo(obj.getNodeType()));
			Assert.assertEquals(0, "test:234".compareTo(obj.getPID()));
			// System.out.println(obj.getNamespace());
			Assert.assertEquals(0, "test".compareTo(obj.getNamespace()));
			// System.out.println(obj.getCreator()[0]);
			Assert.assertEquals(0,
					"Jan Schnasse".compareTo(obj.getFirstCreator()));
			// System.out.println(obj.getLabel());
			Assert.assertEquals(0,
					"Ein komplexes Testobjekt".compareTo(obj.getLabel()));
			// System.out.println(obj.getTitle()[0]);
			Assert.assertEquals(0,
					"Ein Testtitel".compareTo(obj.getFirstTitle()));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void updateObject()
	{
		try
		{

			Node myObject = archive.createComplexObject(object);
			// Node node1 =
			archive.createNode(myObject.getPID());

			Node node2 = new Node();
			node2.addTitle("NEUER KNOTEN").setLabel("Cooler neuer Knoten");
			archive.createNode(myObject, node2);

			Thread.sleep(10000);

			InputStream stream = archive.findTriples(
					"<http://127.0.0.1:8080/fedora/objects/"
							+ myObject.getPID() + "> <"
							+ FedoraVocabulary.HAS_PART + "> *",
					FedoraVocabulary.SPO, FedoraVocabulary.N3);
			StringWriter writer = new StringWriter();
			IOUtils.copy(stream, writer, "utf-8");
			String theString = writer.toString();
			System.out.println(theString);

			myObject = archive.readObject(myObject.getPID());

			Assert.assertEquals(
					0,
					object.getRoot().getNodeType()
							.compareTo(myObject.getNodeType()));

			URL data = this.getClass().getResource("/test.pdf");
			URL metadata = this.getClass().getResource("/test.ttl");

			ComplexObject complexObject = archive.readComplexObject(object
					.getRoot().getPID());
			complexObject.getRoot().setUploadData(data.getPath(), "test",
					"application/pdf");
			complexObject.getRoot().setMetadataFile(metadata.getPath());

			archive.updateComplexObject(complexObject);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void deleteObject()
	{
		try
		{
			archive.createComplexObject(object);
			Thread.sleep(10000);
			archive.deleteComplexObject(object.getRoot().getPID());

			Assert.assertFalse(archive.nodeExists(object.getRoot().getPID()));
			Assert.assertEquals(3, archive.findNodes("test:*").size());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@After
	public void tearDown()
	{
		// List<String> objects = archive.findNodes("test:*");
		// for (String pid : objects)
		// {
		// archive.deleteNode(pid);
		// }
		//
		// objects = archive.findNodes("testCM:*");
		// for (String pid : objects)
		// {
		// archive.deleteNode(pid);
		// }

	}
}
