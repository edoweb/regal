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
package de.nrw.hbz.regal.sync.extern;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class XmlUtils {
    /**
     * @param digitalEntityFile
     *            the xml file
     * @return the root element as org.w3c.dom.Element
     * @throws ParserConfigurationException
     *             comes from factory.newDocumentBuilder()
     * @throws IOException
     *             comes from docBuilder.parse()
     * @throws SAXException
     *             comes from docBuilder.parse()
     * @throws FileNotFoundException
     *             comes from FileInputStream
     */
    public static Element getDocument(File digitalEntityFile)
	    throws ParserConfigurationException, FileNotFoundException,
	    SAXException, IOException {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setNamespaceAware(true);
	DocumentBuilder docBuilder;

	docBuilder = factory.newDocumentBuilder();

	Document doc = docBuilder.parse(new BufferedInputStream(
		new FileInputStream(digitalEntityFile)));
	Element root = doc.getDocumentElement();
	root.normalize();
	return root;
    }

    /**
     * @param xmlString
     *            a xml string
     * @return the root element as org.w3c.dom.Element
     */
    public static Element getDocument(String xmlString) {
	try {
	    DocumentBuilderFactory factory = DocumentBuilderFactory
		    .newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder docBuilder;

	    docBuilder = factory.newDocumentBuilder();

	    Document doc = docBuilder.parse(new BufferedInputStream(
		    new ByteArrayInputStream(xmlString.getBytes())));
	    Element root = doc.getDocumentElement();
	    root.normalize();
	    return root;
	} catch (FileNotFoundException e) {

	    e.printStackTrace();
	} catch (SAXException e) {

	    e.printStackTrace();
	} catch (IOException e) {

	    e.printStackTrace();
	} catch (ParserConfigurationException e) {

	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * @param file
     *            file to store the string in
     * @param str
     *            the string will be stored in file
     * @return a file containing the string
     * @throws Exception
     *             if something goes wrong
     */
    public static File stringToFile(File file, String str) throws Exception {
	file.createNewFile();
	FileOutputStream writer = null;
	try {
	    writer = new FileOutputStream(file);
	    writer.write(str.replace("\n", " ").replace("  ", " ")
		    .getBytes("utf-8"));
	} catch (IOException e) {

	    e.printStackTrace();
	} finally {
	    if (writer != null)
		try {
		    writer.flush();
		    writer.close();
		} catch (IOException ignored) {
		}
	}
	str = null;
	return file;
    }

    /**
     * @param file
     *            the contents of this file will be converted to a string
     * @return a string with the content of the file
     * @throws Exception
     *             if something goes wrong
     */
    public static String fileToString(File file) throws Exception {
	if (file == null || !file.exists()) {
	    System.out.println("NO MARC METADATA");
	    return "";
	}
	byte[] buffer = new byte[(int) file.length()];
	BufferedInputStream f = null;
	try {
	    f = new BufferedInputStream(new FileInputStream(file));
	    f.read(buffer);
	} finally {
	    if (f != null)
		try {
		    f.close();
		} catch (IOException ignored) {
		}
	}
	return new String(buffer);
    }

    /**
     * @param xPathStr
     *            a xpath expression
     * @param root
     *            the xpath is applied to this element
     * @return a list of elements
     * @throws XPathExpressionException
     */
    public static List<Element> getElements(String xPathStr, Element root,
	    NamespaceContext nscontext) throws XPathExpressionException {
	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPath xpath = xpathFactory.newXPath();
	xpath.setNamespaceContext(nscontext);
	NodeList elements = (NodeList) xpath.evaluate(xPathStr, root,
		XPathConstants.NODESET);

	List<Element> result = new Vector<Element>();
	for (int i = 0; i < elements.getLength(); i++) {
	    try {
		Element element = (Element) elements.item(i);
		result.add(element);
	    } catch (ClassCastException e) {
		e.printStackTrace();
	    }
	}
	return result;
    }
}
