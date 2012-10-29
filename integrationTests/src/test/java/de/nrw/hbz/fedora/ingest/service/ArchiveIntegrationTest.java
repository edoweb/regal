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
package de.nrw.hbz.fedora.ingest.service;

import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.TYPE_OBJECT;

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

import de.nrw.hbz.edoweb2.archive.ArchiveFactory;
import de.nrw.hbz.edoweb2.archive.ArchiveInterface;
import de.nrw.hbz.edoweb2.datatypes.ComplexObject;
import de.nrw.hbz.edoweb2.datatypes.ComplexObjectNode;
import de.nrw.hbz.edoweb2.datatypes.ContentModel;
import de.nrw.hbz.edoweb2.datatypes.Link;
import de.nrw.hbz.edoweb2.datatypes.Node;
import de.nrw.hbz.edoweb2.datatypes.Vocabulary;
import de.nrw.hbz.edoweb2.fedora.FedoraFacade;

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
				properties.getProperty("password"),
				properties.getProperty("sesameStore"));

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
			Node node1 = archive.createNode(myObject.getPID());

			Node node2 = new Node();
			node2.addTitle("NEUER KNOTEN").setLabel("Cooler neuer Knoten");
			archive.createNode(myObject, node2);

			Thread.sleep(10000);

			InputStream stream = archive.findTriples(
					"<http://127.0.0.1:8080/fedora/objects/"
							+ myObject.getPID() + "> <"
							+ Vocabulary.REL_IS_RELATED + "> *",
					FedoraFacade.TYPE_SPO, FedoraFacade.FORMAT_N3);
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
			// List<String> objects = facade.findNodes("test:*");
			//
			// for (String pid : objects)
			// {
			// System.out.println("Still exists " + pid);
			// }
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
		List<String> objects = archive.findNodes("test:*");
		for (String pid : objects)
		{
			archive.deleteNode(pid);
		}
		objects = archive.findNodes("changeme:*");
		for (String pid : objects)
		{
			archive.deleteNode(pid);
		}
	}
	//
	// @Test
	// public void readObject()
	// {
	// try
	// {
	//
	// if (facade.nodeExists(object.getPID()))
	// facade.deleteNode(object.getPID());
	// System.out.println("TEST Object: " + object.getFileName());
	// facade.createNode(object);
	// readObject = facade.readNode(object.getPID());
	//
	// System.out.println("Test Read: " + readObject.getFileName());
	// Assert.assertEquals(0, "test:234".compareTo(object.getPID()));
	// Assert.assertEquals(0,
	// "Jan Schnasse".compareTo(object.getCreator()[0]));
	// Assert.assertEquals(0,
	// "Ein Testobjekt".compareTo(object.getLabel()));
	// Assert.assertEquals(0,
	// "Ein Testtitel".compareTo(object.getTitle()[0]));
	// Assert.assertEquals(0, "test".compareTo(readObject.getFileName()));
	// Assert.assertEquals(0,
	// "application/pdf".compareTo(readObject.getMimeType()));
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// Assert.fail(e.getMessage());
	// }
	// }
	//
	// @Test
	// public void updateObject()
	// {
	// try
	// {
	// if (facade.nodeExists(object.getPID()))
	// facade.deleteNode(object.getPID());
	//
	// facade.createNode(object);
	// object.setTitle(new String[] { "Neuer Titel" });
	// URL url = this.getClass().getResource("/logback.xml");
	// object.setUploadData(url.getPath(), "test", "text/xml");
	// facade.updateNode(object);
	// readObject = facade.readNode(object.getPID());
	// Assert.assertEquals(0,
	// "Neuer Titel".compareTo(readObject.getTitle()[0]));
	//
	// }
	// catch (RemoteException e)
	// {
	// Assert.fail(e.getMessage());
	// }
	// catch (UnsupportedEncodingException e)
	// {
	// Assert.fail(e.getMessage());
	// }
	// catch (IOException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// @Test
	// public void deleteObject()
	// {
	// if (!facade.nodeExists(object.getPID()))
	// try
	// {
	// facade.createNode(object);
	// }
	// catch (RemoteException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// catch (IOException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// facade.deleteNode(object.getPID());
	// Assert.assertFalse(facade.nodeExists(object.getPID()));
	// }
	//

}

/*
 * // PropertyLoader properties = null;
 * 
 * // public HBZFedoraIngestTest() // { // // // Properties properties = null;
 * // // try // { // properties = new Properties(); //
 * properties.load(getClass().getResourceAsStream("/IngestService.properties"));
 * // } catch (FileNotFoundException e) // { // e.printStackTrace(); // } catch
 * (IOException e) // { // e.printStackTrace(); // } // Session session =
 * Session.getInstance(); // System.out.println(properties); //
 * session.setUser(properties.getProperty("fedoraUser")); //
 * session.setPassword(properties.getProperty("fedoraPassword")); // ingester =
 * new HBZFedoraIngesterSecured(); // }
 * 
 * @Test public void test() { // TODO Make a test }
 * 
 * public void delete(String ns) { Vector<String> objects =
 * ingester.findObjects(ns + ":*"); System.out.println("Lösche " +
 * objects.size() + " Objekte!"); for (String pid : objects) {
 * ingester.deleteNode(pid); System.out.println("DELETED: " + pid); } }
 * 
 * // @Test public void deletePids() { Vector<String> objects = new
 * Vector<String>();
 * 
 * objects.add("ellinet:EllinetObjectModel");
 * objects.add("ellinet:EllinetObjectServiceDefinition");
 * objects.add("ellinet:EllinetObjectServiceDeployment");
 * 
 * for (String pid : objects) { ingester.deleteNode(pid);
 * System.out.println("DELETED: " + pid); } }
 * 
 * // @Test public void deleteObject() {
 * ingester.deleteObject("ellinet:3276527"); ingester.deleteObject("ddc630");
 * ingester.deleteObject("book"); ingester.deleteObject("report");
 * ingester.deleteObject("conferenceObject");
 * ingester.deleteObject("doctoralThesis"); ingester.deleteObject("other");
 * ingester.deleteObject("open_access"); ingester.deleteObject("all"); }
 * 
 * // @Test public void deleteAll() { // delete("test"); // delete("changeme");
 * // delete("ellinet"); delete("oai"); delete("model"); }
 * 
 * // @Test public void createTree() { try { String namespace = "test"; String[]
 * pids = ingester.getPids(namespace, 17); HBZTree tree;
 * 
 * // START Create Nodes tree = new HBZTree(namespace, namespace + ":3949870");
 * 
 * HBZObject object = tree.getRoot();
 * 
 * HBZContentModel ellinetObjectModel = new HBZContentModel();
 * ellinetObjectModel.addPrescribedDs("RELS-EXT",
 * "info:fedora/fedora-system:FedoraRELSExt-1.0", "application/rdf+xml");
 * ellinetObjectModel.setContentModelPID(namespace + ":EllinetObjectModel");
 * ellinetObjectModel.setServiceDefinitionPID(namespace +
 * ":EllinetObjectServiceDefinition");
 * ellinetObjectModel.setServiceDeploymentPID(namespace +
 * ":EllinetObjectServiceDeployment"); ellinetObjectModel.addMethod("oai_ore",
 * "http://localhost:8080/Fedora2ORESrv/(pid)");
 * object.addContentModel(ellinetObjectModel);
 * 
 * HBZTreeNode hochschulschrift = new HBZTreeNode(new HBZConcept(),
 * object.getPID() + "_1");
 * 
 * HBZTreeNode hochschulschriftDocument = new HBZTreeNode( new HBZRealisation(),
 * object.getPID() + "_2");
 * 
 * HBZTreeNode hochschulschriftDocumentRepresentation = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_3");
 * 
 * HBZTreeNode originalObject = new HBZTreeNode(new HBZConcept(),
 * object.getPID() + "_4");
 * 
 * HBZTreeNode originalObjectViewPDF = new HBZTreeNode( new HBZRealisation(),
 * object.getPID() + "_5");
 * 
 * HBZTreeNode originalObjectViewPDFPDF = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_6");
 * 
 * HBZTreeNode originalObjectViewPDFXML = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_7");
 * 
 * HBZTreeNode originalObjectViewOCR = new HBZTreeNode( new HBZRealisation(),
 * object.getPID() + "_8");
 * 
 * HBZTreeNode originalObjectViewOCRTXT = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_9");
 * 
 * HBZTreeNode originalObjectViewOCRXML = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_10");
 * 
 * HBZTreeNode originalObjectViewINDEX = new HBZTreeNode( new HBZRealisation(),
 * object.getPID() + "_11");
 * 
 * HBZTreeNode originalObjectViewINDEXHTML = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_12");
 * 
 * HBZTreeNode originalObjectViewINDEXXML = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_13");
 * 
 * HBZTreeNode originalObjectViewTHUMB = new HBZTreeNode( new HBZRealisation(),
 * object.getPID() + "_14");
 * 
 * HBZTreeNode originalObjectViewTHUMBJPEG = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_15");
 * 
 * HBZTreeNode originalObjectViewTHUMBXML = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_16");
 * 
 * // END Create Nodes
 * 
 * // START Tree Layout tree.addChild(hochschulschrift);
 * 
 * hochschulschrift.addChild(hochschulschriftDocument); hochschulschriftDocument
 * .addChild(hochschulschriftDocumentRepresentation);
 * tree.addChild(originalObject);
 * 
 * originalObject.addChild(originalObjectViewPDF); originalObjectViewPDF
 * .addChild(hochschulschriftDocumentRepresentation);//
 * originalObjectViewPDFPDF);
 * originalObjectViewPDF.addChild(originalObjectViewPDFXML);
 * 
 * originalObject.addChild(originalObjectViewOCR);
 * originalObjectViewOCR.addChild(originalObjectViewOCRTXT);
 * originalObjectViewOCR.addChild(originalObjectViewOCRXML);
 * 
 * originalObject.addChild(originalObjectViewINDEX);
 * originalObjectViewINDEX.addChild(originalObjectViewINDEXHTML);
 * originalObjectViewINDEX.addChild(originalObjectViewINDEXXML);
 * 
 * originalObject.addChild(originalObjectViewTHUMB);
 * originalObjectViewTHUMB.addChild(originalObjectViewTHUMBJPEG);
 * originalObjectViewTHUMB.addChild(originalObjectViewTHUMBXML); // END Tree
 * Layout
 * 
 * // START setTitles object.addTitle("Ellinet Object - " + pids[0]);
 * hochschulschrift.getMe().addTitle( "Ellinet Object - Hochschulschrift - " +
 * pids[1]); hochschulschriftDocument.getMe() .addTitle(
 * "Ellinet Object - Hochschulschrift - Document - " + pids[2]);
 * hochschulschriftDocumentRepresentation.getMe().addTitle(
 * "Ellinet Object - Hochschulschrift - Document - PDF - " + pids[3]);
 * originalObject.getMe().addTitle( "Ellinet Object - DTL Object - " + pids[4]);
 * originalObjectViewPDF.getMe().addTitle(
 * "Ellinet Object - DTL Object - PDF View - " + pids[5]);
 * originalObjectViewPDFPDF.getMe() .addTitle(
 * "Ellinet Object - DTL Object - PDF View - PDF - " + pids[6]);
 * originalObjectViewPDFXML.getMe() .addTitle(
 * "Ellinet Object - DTL Object - PDF View - XML - " + pids[7]);
 * originalObjectViewOCR.getMe().addTitle(
 * "Ellinet Object - DTL Object - OCR View - " + pids[8]);
 * originalObjectViewOCRTXT.getMe() .addTitle(
 * "Ellinet Object - DTL Object - OCR View - TXT - " + pids[9]);
 * originalObjectViewOCRXML.getMe().addTitle(
 * "Ellinet Object - DTL Object - OCR View - XML - " + pids[10]);
 * originalObjectViewINDEX.getMe().addTitle(
 * "Ellinet Object - DTL Object - Index View - " + pids[11]);
 * originalObjectViewINDEXHTML.getMe().addTitle(
 * "Ellinet Object - DTL Object - Index View - HTML - " + pids[12]);
 * originalObjectViewINDEXXML.getMe().addTitle(
 * "Ellinet Object - DTL Object - Index View - XML - " + pids[13]);
 * originalObjectViewTHUMB.getMe().addTitle(
 * "Ellinet Object - DTL Object - Thumb View - " + pids[14]);
 * originalObjectViewTHUMBJPEG.getMe().addTitle(
 * "Ellinet Object - DTL Object - Thumb View - JPEG - " + pids[15]);
 * originalObjectViewTHUMBXML.getMe().addTitle(
 * "Ellinet Object - DTL Object - Thumb View - XML - " + pids[16]);
 * 
 * System.out.println(object.getTitle()[0]);
 * System.out.println(hochschulschrift.getMe().getTitle()[0]);
 * System.out.println(hochschulschriftDocument.getMe().getTitle()[0]);
 * System.out.println(hochschulschriftDocumentRepresentation.getMe()
 * .getTitle()[0]); System.out.println(originalObject.getMe().getTitle()[0]);
 * System.out.println(originalObjectViewPDF.getMe().getTitle()[0]);
 * System.out.println(originalObjectViewPDFPDF.getMe().getTitle()[0]);
 * System.out.println(originalObjectViewPDFXML.getMe().getTitle()[0]);
 * System.out.println(originalObjectViewOCR.getMe().getTitle()[0]);
 * System.out.println(originalObjectViewOCRTXT.getMe().getTitle()[0]);
 * System.out.println(originalObjectViewOCRXML.getMe().getTitle()[0]);
 * System.out.println(originalObjectViewINDEX.getMe().getTitle()[0]); System.out
 * .println(originalObjectViewINDEXHTML.getMe().getTitle()[0]); System.out
 * .println(originalObjectViewINDEXXML.getMe().getTitle()[0]);
 * System.out.println(originalObjectViewTHUMB.getMe().getTitle()[0]); System.out
 * .println(originalObjectViewTHUMBJPEG.getMe().getTitle()[0]); System.out
 * .println(originalObjectViewTHUMBXML.getMe().getTitle()[0]); // END setTitles
 * 
 * // CREATE TREE ingester.createTree(tree.getBean());
 * 
 * // UPLOAD DATA // if (dtlBean.getStream() != null) // { //
 * System.out.println(hochschulschriftDocumentRepresentation.getMe().getPID());
 * // HBZRepresentation representation = //
 * (HBZRepresentation)hochschulschriftDocumentRepresentation.getMe(); //
 * representation=ingester.readRepresentation(representation.getPID()); //
 * representation.addCreator("Digitool2Fedora Software"); //
 * representation.addDate(new Date().toString()); // //
 * representation.addIdentifier(dtlBean.getPid()); //
 * representation.addRights("open_access"); //
 * representation.addSubject("Ellinet, klio, zbmed, Digitool"); //
 * representation.setDatastreamId("id_" + // dtlBean.getStream().getName()); //
 * representation.setDatastreamType("application/pdf"); //
 * System.out.println(dtlBean.getStream().getAbsolutePath()); //
 * representation.setUploadData(dtlBean.getStream().getAbsolutePath(), //
 * representation.getDatastreamId(), "application/pdf"); // // HBZLink link =
 * new HBZLink(); // link.setPredicate(HBZNode.REL_ORIGINAL_OBJECT_ID); //
 * link.setObject(dtlBean.getPid()); // representation.addRelation(link); // //
 * link = new HBZLink(); //
 * link.setPredicate(HBZNode.REL_ORIGINAL_DATASTREAM_ID); //
 * link.setObject(dtlBean.getStream().getName()); //
 * representation.addRelation(link); // // link = new HBZLink(); //
 * link.setPredicate(HBZNode.REL_FULLTEXT_URL); //
 * link.setObject("info:fedora/"+ //
 * representation.getPID()+"/datastreams/"+representation
 * .getDatastreamId()+"/content"); // // representation.addRelation(link); // //
 * link = new HBZLink(); // link.setPredicate(HBZNode.REL_REPRESENTATION_TYPE);
 * // link.setObject(HBZNode.TYPE_FULLTEXT); //
 * representation.addRelation(link); //
 * ingester.updateRepresentation(representation.getPID(), // representation); //
 * // // editDtlViewRepresentation(originalObjectViewPDFPDF, dtlBean); // //
 * editDtlViewRepresentation(originalObjectViewPDFXML, dtlBean); // // // }
 * 
 * // // if (dtlBean.getArchiveLink() != null) // { //
 * editOcrRealisation(originalObjectViewOCR, dtlBean); //
 * editDtlOcrRepresentation(originalObjectViewOCRTXT, dtlBean); //
 * editDtlOcrRepresentation(originalObjectViewOCRXML, dtlBean); // } // // if
 * (dtlBean.getIndexLink() != null) // { //
 * editIndexRealisation(originalObjectViewOCR, dtlBean); //
 * editDtlIndexRepresentation(originalObjectViewINDEXHTML, dtlBean); //
 * editDtlIndexRepresentation(originalObjectViewINDEXXML, dtlBean); // } // //
 * if (dtlBean.getThumbnailLink() != null) // { //
 * editThumbRealisation(originalObjectViewOCR, dtlBean); //
 * editDtlThumbRepresentation(originalObjectViewTHUMBJPEG, dtlBean); //
 * editDtlThumbRepresentation(originalObjectViewTHUMBXML, dtlBean); // ; // }
 * 
 * } catch (RemoteException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); }
 * 
 * }
 * 
 * // @Test public void createEllinetContentModel() { HBZObject object =
 * ingester.createObject("test"); HBZContentModel ellinetObjectModel = new
 * HBZContentModel(); ellinetObjectModel.addPrescribedDs("RELS-EXT",
 * "info:fedora/fedora-system:FedoraRELSExt-1.0", "application/rdf+xml");
 * ellinetObjectModel.setContentModelPID("ellinet:EllinetObjectModel");
 * ellinetObjectModel
 * .setServiceDefinitionPID("ellinet:EllinetObjectServiceDefinition");
 * ellinetObjectModel
 * .setServiceDeploymentPID("ellinet:EllinetObjectServiceDeployment");
 * ellinetObjectModel.addMethod("oai_ore",
 * "http://localhost:8080/Fedora2ORESrv/(pid)");
 * object.addContentModel(ellinetObjectModel);
 * 
 * ingester.updateNode(object.getPID(), object); }
 * 
 * // @Test public void updateReReadTest() throws InterruptedException { //
 * CreateObject HBZObject object = ingester.createObject("test");
 * System.out.println("Create Object " + object.getPID() + ", " +
 * object.getPID());
 * 
 * // Modify Object object.addCreator("Digitool2Fedora Software");
 * object.addDate(new Date().toString());
 * object.addDescription("Ein aus Digitool überführtes Objekt.");
 * object.addTitle("EllinetObject (" + 1234 + ")");
 * object.addType("EllinetObjekt"); object.addIdentifier("1234");
 * object.addRights("open_access");
 * object.addSubject("Ellinet, klio, zbmed, Digitool");
 * 
 * HBZLink link = new HBZLink(); link.setPredicate("hbz:test:predicate");
 * link.setObject("TEST1 test", true); link.setLiteral(true);
 * object.addRelation(link); // object.addRelation(link);
 * ingester.updateNode(object.getPID(), object); Thread.sleep(10000); //
 * CreateConcept HBZConcept concept = ingester.createConcept(object.getPID());
 * // concept = ingester.readConcept(concept.getPID());
 * System.out.println("\tCreate Concept " + concept.getPID() + ", " +
 * concept.getPID());
 * 
 * // Modify Concept concept.addCreator("Digitool2Fedora Software");
 * concept.addDate(new Date().toString());
 * concept.addDescription("Ein aus Digitool überführtes Objekt.");
 * concept.addTitle("EllinetObject (" + 1234 + ")");
 * concept.addType("EllinetObjekt"); concept.addIdentifier("1234");
 * concept.addRights("open_access");
 * concept.addSubject("Ellinet, klio, zbmed, Digitool");
 * 
 * link = new HBZLink(); // link.setPredicate("hbz:test:predicate"); //
 * link.setObject("test test",true); // link.setLiteral(true); //
 * concept.addRelation(link);
 * 
 * link.setPredicate("hbz:test:predicate"); link.setObject("uri", true);
 * link.setLiteral(false); concept.addRelation(link);
 * 
 * // link.setPredicate("hbz:test:predicate"); // link.setObject("test",true);
 * // link.setLiteral(false); // concept.addRelation(link);
 * 
 * ingester.updateNode(concept.getPID(), concept); // ! Local Copy of Object is
 * deprecated ! // object = ingester.readObject(object.getPID());
 * Thread.sleep(10000);
 * 
 * HBZRealisation realisation = ingester.createRealisation(concept .getPID());
 * // realisation = ingester.readRealisation(realisation.getPID());
 * System.out.println("\tCreate Realisation " + realisation.getPID() + ", " +
 * realisation.getPID());
 * 
 * realisation.addCreator("Digitool2Fedora Software"); realisation.addDate(new
 * Date().toString()); realisation.addDescription("DocumentRealisation");
 * realisation.addTitle("DocumentRealisation ()");
 * realisation.addType("DocumentRealisation"); //
 * realisation.addIdentifier(dtlBean.getPid());
 * realisation.addRights("open_access");
 * realisation.addSubject("Ellinet, klio, zbmed, Digitool");
 * 
 * link = new HBZLink(); link.setPredicate("hbz:test:predicate");
 * link.setObject("test realisation", true); link.setLiteral(true);
 * realisation.addRelation(link); System.out.println("UPDATE REALISATION");
 * ingester.updateNode(realisation.getPID(), realisation);
 * 
 * Thread.sleep(10000); }
 * 
 * // @Test public final void testDublinCore() throws InterruptedException {
 * 
 * try {
 * 
 * } catch (Exception e) { e.printStackTrace(); } catch (Error e) {
 * e.printStackTrace(); } System.out.println("END"); }
 * 
 * public final void testCreateRepresentation() throws InterruptedException {
 * try { this.deleteAll();
 * System.out.println("CREATE TREE 1---------------------------");
 * 
 * // CREATE OBJECT---------------------------------- HBZObject object =
 * ingester.createObject("test"); System.out.println("Create Object " +
 * object.getLabel() + ", " + object.getPID()); Thread.sleep(10000);
 * 
 * // CREATE CONCEPT--------------------------------- HBZConcept concept =
 * ingester.createConcept(object.getPID()); object =
 * ingester.readObject(object.getPID());
 * 
 * System.out.println("\tCreate Concept " + concept.getLabel() + ", " +
 * concept.getPID()); Thread.sleep(10000); // CREATE
 * CONCEPT--------------------------------- HBZConcept concept2 =
 * ingester.createConcept(concept.getPID()); concept =
 * ingester.readConcept(concept.getPID());
 * 
 * System.out.println("\t\tCreate Concept " + concept2.getLabel() + ", " +
 * concept2.getPID()); Thread.sleep(10000);
 * 
 * // CREATE REALISATION HBZRealisation realisation =
 * ingester.createRealisation(concept2 .getPID()); concept2 =
 * ingester.readConcept(concept2.getPID());
 * System.out.println("\t\t\tCreate Realisation " + realisation.getLabel() +
 * ", " + realisation.getPID()); Thread.sleep(10000); // CREATE REPRESENTATION
 * HBZRepresentation representation = ingester
 * .createRepresentation(realisation.getPID()); realisation =
 * ingester.readRealisation(realisation.getPID());
 * 
 * representation.setUploadData("/home/jan/test.pdf", "TestFile",
 * "application/pdf"); ingester.updateNode(representation.getPID(),
 * representation); representation.addTitle("The BeOS File System");
 * representation
 * .addSubject("The BeOs File System is one of the first systems that allows user\n"
 * + "to define additional datastreams related to a file and index them.");
 * 
 * System.out.println("\t\t\tCreate Representation " + representation.getLabel()
 * + ", " + representation.getPID());
 * 
 * Thread.sleep(10000); // CREATE REPRESENTATION HBZRepresentation
 * representation2 = ingester .createRepresentation(realisation.getPID());
 * realisation = ingester.readRealisation(realisation.getPID());
 * 
 * representation2.setUploadData("/home/jan/test.pdf", "TestFile",
 * "application/pdf"); ingester.updateNode(representation2.getPID(),
 * representation2);
 * 
 * System.out.println("\t\t\tCreate Representation " +
 * representation2.getLabel() + ", " + representation2.getPID()); } catch
 * (RemoteException e) { e.printStackTrace(); } catch (Exception e) {
 * e.printStackTrace(); } catch (Error e) { e.printStackTrace(); }
 * System.out.println("END"); }
 * 
 * // @Test public final void testCreateObject() throws InterruptedException {
 * try { delete("test*");
 * System.out.println("CREATE TREE 1---------------------------");
 * 
 * // CREATE OBJECT---------------------------------- HBZObject object =
 * ingester.createObject("test"); System.out.println("Create Object " +
 * object.getLabel() + ", " + object.getPID()); Thread.sleep(10000);
 * 
 * // CREATE CONCEPT--------------------------------- HBZConcept concept =
 * ingester.createConcept(object.getPID()); object =
 * ingester.readObject(object.getPID());
 * 
 * System.out.println("\tCreate Concept " + concept.getLabel() + ", " +
 * concept.getPID()); Thread.sleep(10000); // CREATE
 * CONCEPT--------------------------------- HBZConcept concept2 =
 * ingester.createConcept(concept.getPID()); concept =
 * ingester.readConcept(concept.getPID());
 * 
 * System.out.println("\t\tCreate Concept " + concept2.getLabel() + ", " +
 * concept2.getPID()); Thread.sleep(10000);
 * 
 * // CREATE REALISATION HBZRealisation realisation =
 * ingester.createRealisation(concept2 .getPID()); concept2 =
 * ingester.readConcept(concept2.getPID());
 * System.out.println("\t\t\tCreate Realisation " + realisation.getLabel() +
 * ", " + realisation.getPID()); Thread.sleep(10000); // CREATE REPRESENTATION
 * HBZRepresentation representation = ingester
 * .createRepresentation(realisation.getPID()); realisation =
 * ingester.readRealisation(realisation.getPID());
 * 
 * representation .setUploadData(
 * "/home/jan/development/fedoraIngest/IngestService/res/Morgan_Kauffman_Practical_File_System_Design.pdf"
 * , "Test File", "application/pdf");
 * ingester.updateNode(representation.getPID(), representation);
 * representation.addTitle("The BeOS File System"); representation
 * .addSubject("The BeOs File System is one of the first systems that allows user\n"
 * + "to define additional datastreams related to a file and index them.");
 * 
 * System.out.println("\t\t\tCreate Representation " + representation.getLabel()
 * + ", " + representation.getPID());
 * 
 * Thread.sleep(10000); // CREATE REPRESENTATION HBZRepresentation
 * representation2 = ingester .createRepresentation(realisation.getPID());
 * realisation = ingester.readRealisation(realisation.getPID());
 * 
 * representation.setUploadData("/home/jan/test.pdf", "Test File",
 * "application/pdf"); ingester.updateNode(representation2.getPID(),
 * representation2);
 * 
 * System.out.println("\t\t\tCreate Representation " +
 * representation2.getLabel() + ", " + representation2.getPID());
 * 
 * // CREATE CONCEPT--------------------------------- HBZConcept concept3 =
 * ingester.createConcept(object.getPID()); object =
 * ingester.readObject(object.getPID()); System.out.println("\tCreate Concept "
 * + concept3.getLabel() + ", " + concept3.getPID()); Thread.sleep(10000); //
 * CREATE REALISATION HBZRealisation realisation2 =
 * ingester.createRealisation(concept3 .getPID()); concept3 =
 * ingester.readConcept(concept3.getPID());
 * 
 * System.out.println("\t\t\tCreate Realisation " + realisation2.getLabel() +
 * ", " + realisation2.getPID()); Thread.sleep(10000);
 * 
 * // CREATE REPRESENTATION HBZRepresentation representation3 = ingester
 * .createRepresentation(realisation2.getPID());
 * 
 * realisation2 = ingester.readRealisation(realisation2.getPID());
 * 
 * System.out.println("\t\t\tCreate Representation " +
 * representation3.getLabel() + ", " + representation3.getPID());
 * System.out.println("CREATE END---------------------------");
 * 
 * // PRINT TREE 1--------------------------------------- System.out
 * .println("// PRINT TREE 1---------------------------------------");
 * printTree(object);
 * 
 * // CREATE TREE 2---------------------------------------------- HBZObject
 * object2 = ingester.createObject("test");
 * 
 * System.out.println("Create Object " + object2.getLabel() + ", " +
 * object2.getPID()); Thread.sleep(10000); // CREATE
 * CONCEPT--------------------------------- HBZConcept concept1Object2 =
 * ingester.createConcept(object2 .getPID()); object2 =
 * ingester.readObject(object2.getPID());
 * 
 * System.out.println("\tCreate Concept " + concept1Object2.getLabel() + ", " +
 * concept1Object2.getPID()); Thread.sleep(10000);
 * 
 * concept1Object2.addRelation(concept2);
 * 
 * ingester.updateNode(concept1Object2.getPID(), concept1Object2);
 * 
 * ingester.updateNode(concept2.getPID(), concept2);
 * 
 * // PRINT TREE 1--------------------------------------- System.out
 * .println("// PRINT TREE 1---------------------------------------");
 * printTree(object);
 * 
 * // PRINT TREE 2--------------------------------------- System.out
 * .println("// PRINT TREE 2---------------------------------------");
 * printTree(object2);
 * 
 * // MODIFY TREE System.out.println("MODIFY---------------------------");
 * 
 * String pid2 = ingester.deleteConcept(concept3.getPID());
 * System.out.println("DELETION OF concept3 " + pid2);
 * 
 * Thread.sleep(10000);
 * 
 * String pid3 = ingester.deleteRealisation(realisation2.getPID());
 * System.out.println("DELETION OF realisation2 " + pid3);
 * 
 * Thread.sleep(10000);
 * 
 * String pid4 = ingester.deleteRepresentation(representation3 .getPID());
 * System.out.println("DELETION OF representation3 " + pid4);
 * 
 * Thread.sleep(10000);
 * 
 * // PRINT TREE 1--------------------------------------- System.out
 * .println("// PRINT TREE 1---------------------------------------"); object =
 * ingester.readObject(object.getPID());
 * 
 * printTree(object);
 * 
 * // PRINT TREE 2--------------------------------------- System.out
 * .println("// PRINT TREE 2---------------------------------------"); object2 =
 * ingester.readObject(object2.getPID());
 * 
 * printTree(object2);
 * 
 * System.out.println("DELETE---------------------------");
 * 
 * // DELETE OBJECT 1-------------------------------------
 * System.out.println("Wait 10sec for Triple Store...."); Thread.sleep(10000);
 * System.out .println("Triple Store is hopefully sync now. Proceed..."); String
 * oldPid = ingester.deleteObject(object.getPID());
 * System.out.println("Delete Object " + oldPid);
 * 
 * // QUERY FOR DELETED OBJECTS-------------------------------
 * hasBeenDeleted(object); hasBeenDeleted(concept); hasBeenDeleted(concept2);
 * hasBeenDeleted(concept3); hasBeenDeleted(realisation);
 * hasBeenDeleted(realisation2); hasBeenDeleted(representation);
 * hasBeenDeleted(representation2); hasBeenDeleted(representation3);
 * 
 * // DELETE OBJECT 2-------------------------------------
 * System.out.println("Wait 10sec for Triple Store...."); Thread.sleep(10000);
 * System.out .println("Triple Store is hopefully sync now. Proceed...");
 * 
 * oldPid = ingester.deleteObject(object2.getPID());
 * System.out.println("Delete Object " + oldPid);
 * 
 * // QUERY FOR DELETED OBJECTS-------------------------------
 * hasBeenDeleted(object2); hasBeenDeleted(concept1Object2);
 * hasBeenDeleted(concept3); hasBeenDeleted(realisation);
 * hasBeenDeleted(realisation2); hasBeenDeleted(representation);
 * hasBeenDeleted(representation2);
 * 
 * } catch (RemoteException e) { e.printStackTrace(); } }
 * 
 * // @Test public void testUserManagement() { try { //
 * -------------------------------------------------- //
 * -------------------------------------------------- //
 * -------------------------------------------------- //
 * -------------------------------------------------- //
 * -------------------------------------------------- // // user: anouar // pwd:
 * anouar // role: testManager //
 * 
 * System.out.println("// BEGIN SESSION ANOUAR"); Session session =
 * Session.getInstance(); session.setUser("anouar");
 * session.setPassword("anouar"); ingester = new HBZFedoraIngesterSecured();
 * 
 * HBZObject object2 = ingester.createObject("test");
 * System.out.println("Create Object " + object2.getLabel() + ", " +
 * object2.getPID()); Thread.sleep(10000);
 * 
 * HBZConcept concept2 = ingester.createConcept(object2.getPID()); object2 =
 * ingester.readObject(object2.getPID());
 * 
 * System.out.println("\tCreate Concept " + concept2.getLabel() + ", " +
 * concept2.getPID());
 * 
 * Thread.sleep(10000);
 * 
 * System.out.println("// END SESSION ANOUAR");
 * 
 * // // user: jan // pwd: jan // role: administrator //
 * 
 * System.out.println("// BEGIN SESSION JAN"); session = Session.getInstance();
 * session.setUser("jan"); session.setPassword("jan"); ingester = new
 * HBZFedoraIngesterSecured();
 * 
 * HBZObject object = ingester.createObject("test");
 * System.out.println("Create Object " + object.getLabel() + ", " +
 * object.getPID()); Thread.sleep(10000);
 * 
 * HBZConcept concept = ingester.createConcept(object.getPID()); object =
 * ingester.readObject(object.getPID());
 * 
 * System.out.println("\tCreate Concept " + concept.getLabel() + ", " +
 * concept.getPID());
 * 
 * Thread.sleep(10000); System.out.println("// END SESSION JAN");
 * 
 * // -------------------------------------------------- //
 * -------------------------------------------------- //
 * -------------------------------------------------- //
 * -------------------------------------------------- //
 * -------------------------------------------------- // // user: andres // pwd:
 * andres // role: testManager // System.out.println("// BEGIN SESSION ANDRES");
 * session = Session.getInstance(); session.setUser("andres");
 * session.setPassword("andres"); ingester = new HBZFedoraIngesterSecured();
 * 
 * HBZObject object3 = ingester.createObject("test");
 * System.out.println("Create Object " + object3.getLabel() + ", " +
 * object3.getPID()); Thread.sleep(10000);
 * 
 * HBZConcept concept3 = ingester.createConcept(object3.getPID()); object3 =
 * ingester.readObject(object3.getPID());
 * 
 * System.out.println("\tCreate Concept " + concept3.getLabel() + ", " +
 * concept3.getPID());
 * 
 * System.out .println("Das Folgende Sollte nicht klappen: READ--------------");
 * 
 * object2 = ingester.readObject(object2.getPID());
 * 
 * System.out
 * .println("Letztereres Sollte nicht geklappt haben  READ--------------");
 * 
 * object2.addCreator("Dr.Quast");
 * object2.addDescription("Das Objekt gehört jetzt mir");
 * 
 * System.out
 * .println("Das Folgende Sollte nicht klappen: UPDATE--------------");
 * 
 * ingester.updateNode(object2.getPID(), object2);
 * 
 * System.out
 * .println("Letztereres Sollte nicht geklappt haben  UPDATE--------------");
 * 
 * System.out.println("// END SESSION ANDRES");
 * 
 * // -------------------------------------------------- //
 * -------------------------------------------------- //
 * -------------------------------------------------- //
 * -------------------------------------------------- //
 * -------------------------------------------------- // // user: user // pwd:
 * user // role: userManager HBZFedoraIngesterSecured ingester = null;
 * 
 * // PropertyLoader properties = null;
 * 
 * // public HBZFedoraIngestTest() // { // // // Properties properties = null;
 * // // try // { // properties = new Properties(); //
 * properties.load(getClass().getResourceAsStream("/IngestService.properties"));
 * // } catch (FileNotFoundException e) // { // e.printStackTrace(); // } catch
 * (IOException e) // { // e.printStackTrace(); // } // Session session =
 * Session.getInstance(); // System.out.println(properties); //
 * session.setUser(properties.getProperty("fedoraUser")); //
 * session.setPassword(properties.getProperty("fedoraPassword")); // ingester =
 * new HBZFedoraIngesterSecured(); // }
 * 
 * @Test public void test() { // TODO Make a test }
 * 
 * public void delete(String ns) { Vector<String> objects =
 * ingester.findObjects(ns + ":*"); System.out.println("Lösche " +
 * objects.size() + " Objekte!"); for (String pid : objects) {
 * ingester.deleteNode(pid); System.out.println("DELETED: " + pid); } }
 * 
 * // @Test public void deletePids() { Vector<String> objects = new
 * Vector<String>();
 * 
 * objects.add("ellinet:EllinetObjectModel");
 * objects.add("ellinet:EllinetObjectServiceDefinition");
 * objects.add("ellinet:EllinetObjectServiceDeployment");
 * 
 * for (String pid : objects) { ingester.deleteNode(pid);
 * System.out.println("DELETED: " + pid); } }
 * 
 * // @Test public void deleteObject() {
 * ingester.deleteObject("ellinet:3276527"); ingester.deleteObject("ddc630");
 * ingester.deleteObject("book"); ingester.deleteObject("report");
 * ingester.deleteObject("conferenceObject");
 * ingester.deleteObject("doctoralThesis"); ingester.deleteObject("other");
 * ingester.deleteObject("open_access"); ingester.deleteObject("all"); }
 * 
 * // @Test public void deleteAll() { // delete("test"); // delete("changeme");
 * // delete("ellinet"); delete("oai"); delete("model"); }
 * 
 * // @Test public void createTree() { try { String namespace = "test"; String[]
 * pids = ingester.getPids(namespace, 17); HBZTree tree;
 * 
 * // START Create Nodes tree = new HBZTree(namespace, namespace + ":3949870");
 * 
 * HBZObject object = tree.getRoot();
 * 
 * HBZContentModel ellinetObjectModel = new HBZContentModel();
 * ellinetObjectModel.addPrescribedDs("RELS-EXT",
 * "info:fedora/fedora-system:FedoraRELSExt-1.0", "application/rdf+xml");
 * ellinetObjectModel.setContentModelPID(namespace + ":EllinetObjectModel");
 * ellinetObjectModel.setServiceDefinitionPID(namespace +
 * ":EllinetObjectServiceDefinition");
 * ellinetObjectModel.setServiceDeploymentPID(namespace +
 * ":EllinetObjectServiceDeployment"); ellinetObjectModel.addMethod("oai_ore",
 * "http://localhost:8080/Fedora2ORESrv/(pid)");
 * object.addContentModel(ellinetObjectModel);
 * 
 * HBZTreeNode hochschulschrift = new HBZTreeNode(new HBZConcept(),
 * object.getPID() + "_1");
 * 
 * HBZTreeNode hochschulschriftDocument = new HBZTreeNode( new HBZRealisation(),
 * object.getPID() + "_2");
 * 
 * HBZTreeNode hochschulschriftDocumentRepresentation = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_3");
 * 
 * HBZTreeNode originalObject = new HBZTreeNode(new HBZConcept(),
 * object.getPID() + "_4");
 * 
 * HBZTreeNode originalObjectViewPDF = new HBZTreeNode( new HBZRealisation(),
 * object.getPID() + "_5");
 * 
 * HBZTreeNode originalObjectViewPDFPDF = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_6");
 * 
 * HBZTreeNode originalObjectViewPDFXML = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_7");
 * 
 * HBZTreeNode originalObjectViewOCR = new HBZTreeNode( new HBZRealisation(),
 * object.getPID() + "_8");
 * 
 * HBZTreeNode originalObjectViewOCRTXT = new HBZTreeNode( new
 * HBZRepresentation(), object.getPID() + "_9"); //
 * System.out.println("// BEGIN SESSION USER"); session = Session.getInstance();
 * session.setUser("user"); session.setPassword("user"); ingester = new
 * HBZFedoraIngesterSecured();
 * 
 * HBZObject object4 = ingester.createObject("test");
 * System.out.println("Create Object " + object4.getLabel() + ", " +
 * object4.getPID()); Thread.sleep(10000);
 * 
 * HBZConcept concept4 = ingester.createConcept(object4.getPID()); object4 =
 * ingester.readObject(object4.getPID());
 * 
 * System.out.println("\tCreate Concept " + concept4.getLabel() + ", " +
 * concept4.getPID()); Vector<String> objects = ingester.findObjects("test:*");
 * for (String pid : objects) { ingester.deleteNode(pid);
 * System.out.println("DELETED: " + pid); }
 * System.out.println("// END SESSION USER");
 * 
 * } catch (InterruptedException e) {
 * 
 * e.printStackTrace(); } catch (RemoteException e) {
 * 
 * e.printStackTrace(); } }
 * 
 * private void hasBeenDeleted(HBZNode node) { try {
 * ingester.readNode(node.getPID()); } catch (Exception e) {
 * System.out.println("DELETE SUCCESSFUL: " + node.getPID()); return; }
 * System.out.println("DELETE FAILED: " + node.getPID()); }
 * 
 * private void printTree(HBZObject object) {
 * System.out.println("PRINT---------------------------"); try { object =
 * ingester.readObject(object.getPID());
 * 
 * System.out.println("Object Label " + object.getLabel() + "\nSubject: " +
 * object.getPID()); printDC(object, "\t"); if (object.getRelsExt() != null) {
 * HBZLink[] externalRels = object.getRelsExt(); if (externalRels != null) { for
 * (HBZLink link : externalRels) { System.out.println("\tP O " +
 * link.getPredicate() + ", " + link.getObject()); } for (HBZLink link :
 * externalRels) { printHBZLink(link); } } }
 * 
 * } catch (RemoteException e1) { e1.printStackTrace(); }
 * System.out.println("PRINT END---------------------------");
 * 
 * }
 * 
 * private void printHBZLink(HBZLink link) {
 * 
 * if (link.getPredicate().compareTo(HBZNode.REL_HAS_CONCEPT) == 0) {
 * printNode(link.getObject()); } else if
 * (link.getPredicate().compareTo(HBZNode.REL_HAS_REALISATION) == 0) {
 * printNode(link.getObject()); } else if (link.getPredicate()
 * .compareTo(HBZNode.REL_HAS_REPRESENTATION) == 0) {
 * printNode(link.getObject()); }
 * 
 * }
 * 
 * private void printNode(String pred) { String pid = pred2pid(pred); HBZNode
 * node; try { node = ingester.readNode(pid);
 * 
 * System.out.println("Object Label " + node.getLabel() + "\nSubject: " +
 * node.getPID()); HBZLink[] externalRels = node.getRelsExt(); printDC(node,
 * "\t"); for (HBZLink link : externalRels) { System.out.println("\tP O " +
 * link.getPredicate() + ", " + link.getObject()); } for (HBZLink link :
 * externalRels) { printHBZLink(link); }
 * 
 * } catch (RemoteException e) { e.printStackTrace(); } }
 * 
 * private String pred2pid(String pred) { String pid =
 * pred.substring(pred.indexOf('/') + 1); return pid; }
 * 
 * private void printDC(HBZNode node, String formater) { String[] contributer =
 * null; String[] coverage = null; String[] creator = null; String[] date =
 * null; String[] description = null; String[] format = null; String[]
 * identifier = null; // String[] label = null; String[] language = null;
 * String[] publisher = null; String[] rights = null; String[] source = null;
 * String[] subject = null; String[] title = null; String[] type = null;
 * 
 * if ((contributer = node.getContributer()) != null) { for (int i = 0; i <
 * contributer.length; i++) { String scontributer = contributer[i];
 * System.out.println(formater + "contributer" + scontributer); } } if
 * ((coverage = node.getCoverage()) != null) { for (int i = 0; i <
 * coverage.length; i++) { String scoverage = coverage[i];
 * System.out.println(formater + "coverage: " + scoverage); } } if ((creator =
 * node.getCreator()) != null) { for (int i = 0; i < creator.length; i++) {
 * String screator = creator[i]; System.out.println(formater + "creator: " +
 * screator); } } if ((date = node.getDate()) != null) { for (int i = 0; i <
 * date.length; i++) { String sdate = date[i]; System.out.println(formater +
 * "date: " + sdate); } } if ((description = node.getDescription()) != null) {
 * for (int i = 0; i < description.length; i++) { String sdescription =
 * description[i]; System.out.println(formater + "description: " +
 * sdescription); } } if ((format = node.getFormat()) != null) { for (int i = 0;
 * i < format.length; i++) { String sformat = format[i];
 * System.out.println(formater + "format: " + sformat); } } if ((identifier =
 * node.getIdentifier()) != null) { for (int i = 0; i < identifier.length; i++)
 * { String sidentifier = identifier[i]; System.out.println(formater +
 * "identifier: " + sidentifier); } } // if ((label = node.getLabel()) != null)
 * // { // for (int i = 0; i < label.length; i++) // { // String slabel =
 * label[i]; // System.out.println(formater + "label: " + slabel); // } // } if
 * ((language = node.getLanguage()) != null) { for (int i = 0; i <
 * language.length; i++) { String slanguage = language[i];
 * System.out.println(formater + "language: " + slanguage); } } if ((publisher =
 * node.getPublisher()) != null) { for (int i = 0; i < publisher.length; i++) {
 * String spublisher = publisher[i]; System.out.println(formater + "publisher: "
 * + spublisher); } } if ((rights = node.getRights()) != null) { for (int i = 0;
 * i < rights.length; i++) { String srights = rights[i];
 * System.out.println(formater + "rights: " + srights); } } if ((source =
 * node.getSource()) != null) { for (int i = 0; i < source.length; i++) { String
 * ssource = source[i]; System.out.println(formater + "source: " + ssource); } }
 * if ((subject = node.getSubject()) != null) { for (int i = 0; i <
 * subject.length; i++) { String ssubject = subject[i];
 * System.out.println(formater + "subject: " + ssubject); } } if ((title =
 * node.getTitle()) != null) { for (int i = 0; i < title.length; i++) { String
 * stitle = title[i]; System.out.println(formater + "title: " + stitle); } } if
 * ((type = node.getType()) != null) { for (int i = 0; i < type.length; i++) {
 * String stype = type[i]; System.out.println(formater + "type: " + stype); } }
 * }
 */
