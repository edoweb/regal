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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class XmlUtils {

    @SuppressWarnings({ "javadoc", "serial" })
    public static class XPathException extends RuntimeException {

	public XPathException(Throwable cause) {
	    super(cause);
	}
    }

    @SuppressWarnings({ "javadoc", "serial" })
    public static class ReadException extends RuntimeException {
	public ReadException(String message) {
	    super(message);
	}

	public ReadException(Throwable cause) {
	    super(cause);
	}
    }

    @SuppressWarnings({ "javadoc", "serial" })
    public static class StreamNotClosedException extends RuntimeException {
	public StreamNotClosedException(String message) {
	    super(message);
	}

	public StreamNotClosedException(Throwable cause) {
	    super(cause);
	}
    }

    final static Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    /**
     * @param digitalEntityFile
     *            the xml file
     * @return the root element as org.w3c.dom.Element
     * @throws XmlException
     *             RuntimeException if something goes wrong
     */
    public static Element getDocument(File digitalEntityFile) {
	try {
	    return getDocument(new FileInputStream(digitalEntityFile));
	} catch (FileNotFoundException e) {
	    throw new XmlException(e);
	}
    }

    /**
     * @param xmlString
     *            a xml string
     * @return the root element as org.w3c.dom.Element
     * @throws XmlException
     *             RuntimeException if something goes wrong
     */
    public static Element getDocument(String xmlString) {
	return getDocument(new ByteArrayInputStream(xmlString.getBytes()));

    }

    /**
     * @param file
     *            file to store the string in
     * @param str
     *            the string will be stored in file
     * @return a file containing the string
     */
    public static File stringToFile(File file, String str) {
	FileOutputStream writer = null;
	try {
	    file.createNewFile();

	    writer = new FileOutputStream(file);
	    writer.write(str.replace("\n", " ").replace("  ", " ")
		    .getBytes("utf-8"));
	} catch (IOException e) {
	    throw new ReadException(e);
	} finally {
	    if (writer != null)
		try {
		    writer.flush();
		    writer.close();
		} catch (IOException ignored) {
		    throw new StreamNotClosedException(ignored);
		}
	}
	str = null;
	return file;
    }

    /**
     * @param file
     *            the contents of this file will be converted to a string
     * @return a string with the content of the file
     */
    public static String fileToString(File file) {
	if (file == null || !file.exists()) {
	    throw new ReadException("");
	}
	byte[] buffer = new byte[(int) file.length()];
	BufferedInputStream f = null;
	try {
	    f = new BufferedInputStream(new FileInputStream(file));
	    f.read(buffer);
	} catch (IOException e) {
	    throw new ReadException(e);
	} finally {
	    if (f != null)
		try {
		    f.close();
		} catch (IOException ignored) {
		    throw new StreamNotClosedException(ignored);
		}
	}
	return new String(buffer);
    }

    /**
     * @param xPathStr
     *            a xpath expression
     * @param root
     *            the xpath is applied to this element
     * @param nscontext
     *            a NamespaceContext
     * @return a list of elements
     */
    public static List<Element> getElements(String xPathStr, Element root,
	    NamespaceContext nscontext) {
	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPath xpath = xpathFactory.newXPath();
	if (nscontext != null)
	    xpath.setNamespaceContext(nscontext);
	NodeList elements;
	try {
	    elements = (NodeList) xpath.evaluate(xPathStr, root,
		    XPathConstants.NODESET);

	    List<Element> result = new Vector<Element>();
	    for (int i = 0; i < elements.getLength(); i++) {
		try {
		    Element element = (Element) elements.item(i);
		    result.add(element);
		} catch (ClassCastException e) {
		    logger.warn(e.getMessage());
		}
	    }
	    return result;
	} catch (XPathExpressionException e1) {
	    throw new XPathException(e1);
	}

    }

    /**
     * @param inputStream
     *            the xml stream
     * @return the root element as org.w3c.dom.Element
     * @throws XmlException
     *             RuntimeException if something goes wrong
     */
    public static Element getDocument(InputStream inputStream) {
	try {
	    DocumentBuilderFactory factory = DocumentBuilderFactory
		    .newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder docBuilder = factory.newDocumentBuilder();

	    Document doc = docBuilder
		    .parse(new BufferedInputStream(inputStream));
	    Element root = doc.getDocumentElement();
	    root.normalize();
	    return root;
	} catch (FileNotFoundException e) {
	    throw new XmlException(e);
	} catch (SAXException e) {
	    throw new XmlException(e);
	} catch (IOException e) {
	    throw new XmlException(e);
	} catch (ParserConfigurationException e) {
	    throw new XmlException(e);
	}

    }
}
