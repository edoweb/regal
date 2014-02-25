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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.nrw.hbz.regal.api.CreateObjectBean;
import de.nrw.hbz.regal.api.DCBeanAnnotated;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.datatypes.Transformer;
import de.nrw.hbz.regal.exceptions.ArchiveException;
import de.nrw.hbz.regal.fedora.CopyUtils;
import de.nrw.hbz.regal.fedora.RdfUtils;
import de.nrw.hbz.regal.fedora.XmlUtils;

/**
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class TestActions {

    Actions actions;

    @Before
    public void setUp() throws IOException {
	actions = Actions.getInstance();
	cleanUp();
    }

    private void cleanUp() {
	actions.deleteByQuery("test:*");
	actions.deleteByQuery("CM:test*");
    }

    @Test
    public void testFindByType() throws IOException, InterruptedException {
	createTestObject("123");
	List<String> list = actions.list("monograph", "test", 0, 10, "es");
	Assert.assertTrue(list.get(0).equals("test:123"));
	Node node = actions.readNode(list.get(0));
	String type = node.getContentType();
	Assert.assertTrue(type.equals("monograph"));
    }

    public void createTestObject(String pid) throws IOException {
	// actions.contentModelsInit("test");
	CreateObjectBean input = new CreateObjectBean();
	input.setType("monograph");
	actions.createResource(input, pid, "test");
	DCBeanAnnotated dc = new DCBeanAnnotated();
	dc.addIdentifier("HT015702837");
	actions.updateDC("test:" + pid, dc);
	actions.updateData("test:" + pid, Thread.currentThread()
		.getContextClassLoader().getResourceAsStream("test.pdf"),
		"application/pdf", "TestFile");
	actions.updateMetadata("test:" + pid, CopyUtils.copyToString(
		Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("test.nt"), "utf-8"));
    }

    @Test
    public void create() throws IOException, InterruptedException {
	createTestObject("123");
	Thread.sleep(10000);
	List<String> pids = actions.list("monograph", "test", 0, 10, "repo");
	Assert.assertEquals(1, pids.size());
	System.out.println(pids);
    }

    @Test(expected = HttpArchiveException.class)
    public void deleteMetadata() throws IOException {
	createTestObject("123");
	actions.readMetadata("test:123");
	actions.deleteMetadata("123", "test");
	actions.deleteData("test:123");
	actions.readMetadata("test:123");
    }

    @Test
    public void epicurAddAndReplace() throws IOException, URISyntaxException {
	createTestObject("123");

	String assumed = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<epicur xmlns=\"urn:nbn:de:1111-2004033116\" xsi:schemaLocation=\"urn:nbn:de:1111-2004033116 http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd\">\n"
		+ "\t<administrative_data>\n"
		+ "\t\t<delivery>\n"
		+ "\t\t\t<update_status type=\""
		+ "urn_new"
		+ "\"></update_status>\n"
		+ "\t\t\t<transfer type=\"oai\"></transfer>\n"
		+ "\t\t</delivery>\n"
		+ "\t</administrative_data>\n"
		+ "<record>\n"
		+ "\t<identifier scheme=\"urn:nbn:de\">"
		+ "urn:nbn:de:test-test:1236"
		+ "</identifier>\n"
		+ "\t<resource>\n"
		+ "\t\t<identifier origin=\"original\" role=\"primary\" scheme=\"url\" type=\"frontpage\">"
		+ actions.getUrnbase()
		+ "test:123"
		+ "</identifier>\n"
		+ "\t\t<format scheme=\"imt\">text/html</format>\n"
		+ "\t</resource>" + "</record>\n" + "</epicur> ";
	String assumed2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<epicur xmlns=\"urn:nbn:de:1111-2004033116\" xsi:schemaLocation=\"urn:nbn:de:1111-2004033116 http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd\">\n"
		+ "\t<administrative_data>\n"
		+ "\t\t<delivery>\n"
		+ "\t\t\t<update_status type=\""
		+ "urn_new"
		+ "\"></update_status>\n"
		+ "\t\t\t<transfer type=\"oai\"></transfer>\n"
		+ "\t\t</delivery>\n"
		+ "\t</administrative_data>\n"
		+ "<record>\n"
		+ "\t<identifier scheme=\"urn:nbn:de\">"
		+ "urn:nbn:de:hbz:929:01-test:1234"
		+ "</identifier>\n"
		+ "\t<resource>\n"
		+ "\t\t<identifier origin=\"original\" role=\"primary\" scheme=\"url\" type=\"frontpage\">"
		+ actions.getUrnbase()
		+ "test:123"
		+ "</identifier>\n"
		+ "\t\t<format scheme=\"imt\">text/html</format>\n"
		+ "\t</resource>" + "</record>\n" + "</epicur> ";
	Services services = actions.getServices();
	Assert.assertEquals("urn:nbn:de:test-1231",
		services.generateUrn("123", "test"));
	actions.addUrn("123", "test", "test");
	String response = actions.epicur("test:123");
	Assert.assertEquals(assumed, response);
	actions.replaceUrn("123", "test", "quatsch");
	actions.replaceUrn("123", "test", "hbz:929:01");
	response = actions.epicur("test:123");
	Assert.assertEquals(assumed2, response);
	response = actions.readMetadata("test:123");
    }

    @Test(expected = ArchiveException.class)
    public void epicurAddAndAdd() throws IOException, URISyntaxException {
	createTestObject("123");

	String assumed = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<epicur xmlns=\"urn:nbn:de:1111-2004033116\" xsi:schemaLocation=\"urn:nbn:de:1111-2004033116 http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd\">\n"
		+ "\t<administrative_data>\n"
		+ "\t\t<delivery>\n"
		+ "\t\t\t<update_status type=\""
		+ "urn_new"
		+ "\"></update_status>\n"
		+ "\t\t\t<transfer type=\"oai\"></transfer>\n"
		+ "\t\t</delivery>\n"
		+ "\t</administrative_data>\n"
		+ "<record>\n"
		+ "\t<identifier scheme=\"urn:nbn:de\">"
		+ "urn:nbn:de:test-test:1236"
		+ "</identifier>\n"
		+ "\t<resource>\n"
		+ "\t\t<identifier origin=\"original\" role=\"primary\" scheme=\"url\" type=\"frontpage\">"
		+ actions.getUrnbase()
		+ "test:123"
		+ "</identifier>\n"
		+ "\t\t<format scheme=\"imt\">text/html</format>\n"
		+ "\t</resource>" + "</record>\n" + "</epicur> ";

	Services services = actions.getServices();
	Assert.assertEquals("urn:nbn:de:test-1231",
		services.generateUrn("123", "test"));
	actions.addUrn("123", "test", "test");
	String response = actions.epicur("test:123");
	Assert.assertEquals(assumed, response);
	actions.addUrn("123", "test", "quatsch");
    }

    @Test
    public void pdfa() throws IOException, URISyntaxException {

	createTestObject("123");
	Node node = actions.readNode("test:123");
	// The pdfA conversion needs a public address
	if (actions.getServer().contains("localhost"))
	    return;
	String response = actions.pdfa(node);
	Assert.assertNotNull(response);
	System.out.println(response);
    }

    @Test
    public void pdfbox() throws IOException, URISyntaxException {
	createTestObject("123");
	Node node = actions.readNode("test:123");
	String response = actions.pdfbox(node);
	Assert.assertNotNull(response);
	Assert.assertEquals("test\n", response);
	System.out.println(response);
    }

    @Test
    public void itext() throws IOException, URISyntaxException {
	createTestObject("123");
	Node node = actions.readNode("test:123");
	String response = actions.itext(node);
	Assert.assertNotNull(response);
	Assert.assertEquals("test", response);
	System.out.println(response);
    }

    @Test(expected = HttpArchiveException.class)
    public void invalidMetadata() throws IOException {
	createTestObject("123");
	actions.updateMetadata(
		"test:123",
		CopyUtils.copyToString(
			Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("invalid.nt"), "utf-8"));
    }

    @Test
    public void oaiOre() throws IOException {
	createTestObject("123");
	RdfUtils.validate(actions.oaiore("test:123", "text/plain"));
	System.out.println(actions.oaiore("test:123", "text/plain"));
    }

    @Test
    public void oaidc() throws IOException {
	createTestObject("123");
	actions.addUrn("123", "test", "test");
	String schemaDecl = ""; // "<!DOCTYPE oai_dc PUBLIC \"http://www.openarchives.org/OAI/2.0/oai_dc.xsd\" \"\">\n";
	URL schema = new URL("http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
	String xmlString = schemaDecl + actions.oaidc("test:123");
	XmlUtils.validate(
		new ByteArrayInputStream(xmlString.getBytes("UTF-8")),
		schema.openStream());
	System.out.println(actions.oaidc("test:123"));
    }

    @Test
    public void html() throws IOException {
	createTestObject("123");
	actions.oaiore("test:123", "text/html");
    }

    @Test
    public void createTransformer() throws IOException {
	createTestObject("123");
	List<Transformer> transformers = new Vector<Transformer>();
	transformers.add(new Transformer("testepicur", "epicur", actions
		.getServer() + "/resource/(pid).epicur"));
	transformers.add(new Transformer("testoaidc", "oaidc", actions
		.getServer() + "/resource/(pid).oaidc"));
	transformers.add(new Transformer("testpdfa", "pdfa", actions
		.getServer() + "/resource/(pid).pdfa"));
	actions.contentModelsInit(transformers);
    }

    @Test
    public void removeNodesTransformer() throws InterruptedException,
	    IOException {
	createTestObject("123");
	actions.addTransformer("123", "test", "testepicur");
	actions.addTransformer("123", "test", "testoaidc");
	actions.addTransformer("123", "test", "testpdfa");
	Node node = actions.readNode("test:123");
	node.removeTransformer("testepicur");

	List<Transformer> ts = node.getContentModels();
	Assert.assertEquals(2, ts.size());
	for (Transformer t : ts) {
	    Assert.assertFalse(t.getId().equals("testepicur"));
	}

	List<String> transformer = new Vector<String>();
	for (int i = 0; i < ts.size(); i++) {
	    transformer.add(ts.get(i).getId());
	}
	CreateObjectBean input = new CreateObjectBean();
	input.setTransformer(transformer);
	input.setType(node.getContentType());
	input.setParentPid(null);
	actions.createResource(input, "123", "test");
	node = actions.readNode(node.getPID());

	HashMap<String, String> map = new HashMap<String, String>();
	map.put("testoaidc", "testoaidc");
	map.put("testpdfa", "testpdfa");
	ts = node.getContentModels();
	Assert.assertEquals(2, ts.size());
	for (Transformer t : ts) {
	    Assert.assertTrue(map.containsKey(t.getId()));
	}
	for (Transformer t : ts) {
	    Assert.assertFalse(t.getId().equals("testepicur"));
	}
    }

    @Test
    public void readNodesTransformer() throws IOException {
	createTestObject("123");
	actions.addTransformer("123", "test", "testepicur");
	actions.addTransformer("123", "test", "testoaidc");
	actions.addTransformer("123", "test", "testpdfa");

	Node node = actions.readNode("test:123");
	List<Transformer> ts = node.getContentModels();
	HashMap<String, String> map = new HashMap<String, String>();
	map.put("testepicur", "testepicur");
	map.put("testoaidc", "testoaidc");
	map.put("testpdfa", "testpdfa");
	Assert.assertEquals(3, ts.size());
	for (Transformer t : ts) {
	    System.out.println(t.getId());
	    Assert.assertTrue(map.containsKey(t.getId()));
	}
    }

    @Test
    public void addUrnIfNoMetadataExists() throws IOException,
	    InterruptedException {
	createTestObject("123");
	actions.deleteMetadata("123", "test");
	Thread.sleep(10000);
	actions.addUrn("123", "test", "hbz:test:902");
    }

    @Test
    public void alephConversion() throws IOException, InterruptedException {
	createTestObject("123");
	Node node = actions.readNode("test:123");
	System.out.println("Start-\"" + actions.aleph(node) + "\"-End");
    }

    @After
    public void tearDown() throws IOException {
	// cleanUp();
    }
}
