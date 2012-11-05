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
package de.nrw.hbz.edoweb2.fedora;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.nrw.hbz.edoweb2.datatypes.ContentModel;
import de.nrw.hbz.edoweb2.datatypes.Node;
import de.nrw.hbz.edoweb2.fedora.FedoraFacade;

/**
 * Class TestUsersInit
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 09.11.2010
 * 
 */
public class FedoraFacadeIntegrationTest
{

	Properties properties = null;
	FedoraFacade facade = null;
	Node object = null;

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

		System.out.println(XmlSchemaCollection.class
				.getResource("XmlSchemaCollection.class"));

		facade = new FedoraFacade(properties.getProperty("fedoraUrl"),
				properties.getProperty("user"),
				properties.getProperty("password"));

		object = new Node().setNamespace("test").setPID("test:234")
				.addCreator("Jan Schnasse").setLabel("Ein Testobjekt")
				.addTitle("Ein Testtitel");

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

		object.addContentModel(cm);

		URL url = this.getClass().getResource("/test.pdf");
		object.setUploadData(url.getPath(), "test", "application/pdf");

	}

	@Test
	public void createNode()
	{
		try
		{
			if (facade.nodeExists(object.getPID()))
				facade.deleteNode(object.getPID());
			facade.createNode(object);
			Assert.assertTrue(facade.nodeExists(object.getPID()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void readNode()
	{
		try
		{

			if (facade.nodeExists(object.getPID()))
				facade.deleteNode(object.getPID());

			facade.createNode(object);
			Node node = facade.readNode(object.getPID());

			Assert.assertEquals(0,
					node.getNodeType().compareTo(object.getNodeType()));
			Assert.assertEquals(0, "test:234".compareTo(node.getPID()));
			// System.out.println(node.getNamespace());
			Assert.assertEquals(0, "test".compareTo(node.getNamespace()));
			Assert.assertEquals(0,
					"Jan Schnasse".compareTo(node.getFirstCreator()));
			Assert.assertEquals(0, "Ein Testobjekt".compareTo(node.getLabel()));
			Assert.assertEquals(0,
					"Ein Testtitel".compareTo(node.getFirstTitle()));
			Assert.assertEquals(0, "test".compareTo(node.getFileName()));
			Assert.assertEquals(0,
					"application/pdf".compareTo(node.getMimeType()));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void updateNode()
	{
		try
		{
			if (facade.nodeExists(object.getPID()))
				facade.deleteNode(object.getPID());

			facade.createNode(object);
			Vector<String> newTitle = new Vector<String>();
			newTitle.add("Neuer Titel");
			object.setTitle(newTitle);
			URL url = this.getClass().getResource("/logback.xml");
			object.setUploadData(url.getPath(), "test", "text/xml");
			facade.updateNode(object);
			Node readObject = facade.readNode(object.getPID());
			System.out.println("DataUrl:" + readObject.getDataUrl().toString());
			Assert.assertEquals(0,
					"Neuer Titel".compareTo(readObject.getFirstTitle()));

		}
		catch (RemoteException e)
		{
			Assert.fail(e.getMessage());
		}
		catch (UnsupportedEncodingException e)
		{
			Assert.fail(e.getMessage());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void findObjects()
	{
		List<String> result = facade.findPids("test:*",
				FedoraFacade.TYPE_SIMPLE);
		for (String pid : result)
			facade.deleteNode(pid);
		Assert.assertEquals(0,
				facade.findPids("test:*", FedoraFacade.TYPE_SIMPLE).size());
		if (!facade.nodeExists(object.getPID()))
			try
			{
				facade.createNode(object);
			}
			catch (RemoteException e)
			{

				e.printStackTrace();
			}
			catch (IOException e)
			{

				e.printStackTrace();
			}
		result = facade.findPids("test:*", FedoraFacade.TYPE_SIMPLE);
		Assert.assertEquals(4, result.size());
	}

	@Test
	public void deleteNode()
	{
		if (!facade.nodeExists(object.getPID()))
			try
			{
				facade.createNode(object);
			}
			catch (RemoteException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		facade.deleteNode(object.getPID());
		Assert.assertFalse(facade.nodeExists(object.getPID()));
	}

	@After
	public void tearDown()
	{
		List<String> result = facade.findPids("test:*",
				FedoraFacade.TYPE_SIMPLE);
		for (String pid : result)
			facade.deleteNode(pid);

		// result = facade.findPids("dtl:*", FedoraFacade.TYPE_SIMPLE);
		// for (String pid : result)
		// facade.deleteNode(pid);
		//
		// result = facade.findPids("default:*", FedoraFacade.TYPE_SIMPLE);
		// for (String pid : result)
		// facade.deleteNode(pid);
		//
		// result = facade.findPids("edoweb:*", FedoraFacade.TYPE_SIMPLE);
		// for (String pid : result)
		// facade.deleteNode(pid);

	}

}
