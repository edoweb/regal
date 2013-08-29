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
package de.nrw.hbz.regal.sync.ingest;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.nrw.hbz.regal.api.helper.ObjectType;
import de.nrw.hbz.regal.sync.extern.DigitalEntity;
import de.nrw.hbz.regal.sync.extern.DigitalEntityBuilderInterface;
import de.nrw.hbz.regal.sync.extern.DigitalEntityRelation;
import de.nrw.hbz.regal.sync.extern.RelatedDigitalEntity;
import de.nrw.hbz.regal.sync.extern.StreamType;
import de.nrw.hbz.regal.sync.extern.XmlUtils;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class EdowebDigitalEntityBuilder implements
	DigitalEntityBuilderInterface {

    @SuppressWarnings({ "javadoc", "serial" })
    public class TypeNotFoundException extends RuntimeException {

	public TypeNotFoundException(String message) {
	    super(message);
	}

    }

    @SuppressWarnings({ "javadoc", "serial" })
    public class XPathException extends RuntimeException {
	public XPathException(Throwable cause) {
	    super(cause);
	}

	public XPathException(String message, Throwable cause) {
	    super(message, cause);
	}
    }

    final static Logger logger = LoggerFactory
	    .getLogger(EdowebDigitalEntityBuilder.class);
    Map<String, String> fileIds2Volume = new HashMap<String, String>();
    Map<String, String> groupIds2FileIds = new HashMap<String, String>();

    @Override
    public DigitalEntity build(String location, String pid) throws Exception {
	DigitalEntity dtlDe = buildSimpleBean(location, pid);
	dtlDe = prepareMetsStructure(dtlDe);
	dtlDe = addSiblings(dtlDe);
	dtlDe = addChildren(dtlDe);
	return dtlDe;
    }

    private DigitalEntity buildSimpleBean(String location, String pid) {
	DigitalEntity dtlDe = new DigitalEntity(location, pid);
	dtlDe.setXml(new File(dtlDe.getLocation() + File.separator + pid
		+ ".xml"));

	Element root = getXmlRepresentation(dtlDe);
	dtlDe.setLabel(getLabel(root));
	loadMetadataStreams(dtlDe, root);
	setType(dtlDe);
	loadDataStream(dtlDe, root);
	linkToParent(dtlDe);

	return dtlDe;
    }

    private void setType(DigitalEntity dtlBean) {
	Element root = XmlUtils.getDocument(dtlBean.getStream(
		StreamType.CONTROL).getFile());
	XPathFactory factory = XPathFactory.newInstance();
	XPath xpath = factory.newXPath();

	try {
	    XPathExpression expr = xpath.compile("//partition_c");
	    Object result = expr.evaluate(root, XPathConstants.NODESET);
	    NodeList nodes = (NodeList) result;
	    if (nodes.getLength() != 1) {
		throw new TypeNotFoundException("Found " + nodes.getLength()
			+ " types");
	    }

	    dtlBean.setType(nodes.item(0).getTextContent());
	    logger.info(dtlBean.getType());
	} catch (XPathExpressionException e) {
	    throw new XPathException(e);
	}
    }

    private DigitalEntity prepareMetsStructure(final DigitalEntity entity) {
	Element root = null;
	DigitalEntity dtlDe = entity;
	try {
	    root = XmlUtils.getDocument(entity.getStream(StreamType.STRUCT_MAP)
		    .getFile());
	} catch (Exception e) {
	    return dtlDe;
	}

	try {
	    dtlDe = createVolumes(entity, root);
	} catch (XPathExpressionException e) {
	    logger.warn(entity.getPid() + " no volumes found.");
	}

	try {
	    mapFileIdsToVolumes(entity, root);
	} catch (XPathExpressionException e) {
	    logger.warn(entity.getPid() + " no issus found.");
	}
	try {
	    root = XmlUtils.getDocument(entity.getStream(StreamType.FILE_SEC)
		    .getFile());
	} catch (Exception e) {
	    return dtlDe;
	}

	try {
	    mapGroupIdsToFileIds(entity, root);
	} catch (XPathExpressionException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return dtlDe;
    }

    private void mapGroupIdsToFileIds(DigitalEntity entity, Element root)
	    throws XPathExpressionException {
	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPath xpath = xpathFactory.newXPath();
	NodeList volumes = (NodeList) xpath.evaluate("/*/*/*/*/*", root,
		XPathConstants.NODESET);
	for (int i = 0; i < volumes.getLength(); i++) {
	    Element item = (Element) volumes.item(i);
	    String groupId = item.getAttribute("GROUPID");
	    String fileId = item.getAttribute("ID");
	    logger.debug(groupId + " to " + fileId);
	    groupIds2FileIds.put(groupId, fileId);
	}

    }

    private void mapFileIdsToVolumes(DigitalEntity entity, Element root)
	    throws XPathExpressionException {
	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPath xpath = xpathFactory.newXPath();
	NodeList volumes = (NodeList) xpath.evaluate("/*/*/*/*/*", root,
		XPathConstants.NODESET);
	for (int i = 0; i < volumes.getLength(); i++) {
	    Element item = (Element) volumes.item(i);
	    String volumeLabel = item.getAttribute("LABEL");
	    String volumePid = entity.getPid() + "-" + volumeLabel;

	    NodeList issues = (NodeList) xpath.evaluate("/*/*/*/*/*[@LABEL="
		    + volumeLabel + "]/*/*", root, XPathConstants.NODESET);
	    for (int j = 0; j < issues.getLength(); j++) {
		Element issue = (Element) issues.item(j);
		String fileId = issue.getAttribute("FILEID");
		logger.debug("Key: " + fileId + " Value: " + volumePid);
		fileIds2Volume.put(fileId, volumePid);
	    }

	}
    }

    private DigitalEntity createVolumes(DigitalEntity entity, Element root)
	    throws XPathExpressionException {
	DigitalEntity dtlDe = entity;
	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPath xpath = xpathFactory.newXPath();
	NodeList list = (NodeList) xpath.evaluate("/*/*/*/*/*", root,
		XPathConstants.NODESET);
	for (int i = 0; i < list.getLength(); i++) {
	    Element item = (Element) list.item(i);
	    String volumeLabel = item.getAttribute("LABEL");
	    String volumePid = dtlDe.getPid() + "-" + volumeLabel;
	    DigitalEntity volume = new DigitalEntity(entity.getLocation()
		    + File.separator + volumePid, volumePid);
	    volume.setLabel(volumeLabel);
	    volume.setParentPid(dtlDe.getPid());
	    volume.setUsageType(ObjectType.volume.toString());
	    dtlDe.addRelated(volume, DigitalEntityRelation.part_of.toString());
	}
	return dtlDe;
    }

    private void loadDataStream(DigitalEntity dtlDe, Element root) {
	Node streamRef = root.getElementsByTagName("stream_ref").item(0);
	String filename = ((Element) streamRef)
		.getElementsByTagName("file_name").item(0).getTextContent();
	File file = new File(dtlDe.getLocation() + File.separator
		+ dtlDe.getPid() + File.separator + filename);
	String mime = ((Element) streamRef).getElementsByTagName("mime_type")
		.item(0).getTextContent();
	String fileId = ((Element) streamRef).getElementsByTagName("file_id")
		.item(0).getTextContent();
	dtlDe.addStream(file, mime, StreamType.DATA, fileId);
    }

    private void loadMetadataStreams(DigitalEntity dtlDe, Element root) {
	setXmlStream(dtlDe, root.getElementsByTagName("control").item(0),
		StreamType.CONTROL);
	dtlDe.setUsageType(root.getElementsByTagName("usage_type").item(0)
		.getTextContent());
	NodeList list = root.getElementsByTagName("md");
	for (int i = 0; i < list.getLength(); i++) {
	    Node item = list.item(i);
	    String type = getItemType((Element) item);

	    if (type.compareTo("dc") == 0) {
		setXmlStream(dtlDe, item, StreamType.DC);
	    } else if (type.compareTo("preservation_md") == 0) {
		setXmlStream(dtlDe, item, StreamType.PREMIS);
	    } else if (type.compareTo("text_md") == 0) {
		setXmlStream(dtlDe, item, StreamType.TEXT);
	    } else if (type.compareTo("rights_md") == 0) {
		setXmlStream(dtlDe, item, StreamType.RIGHTS);
	    } else if (type.compareTo("jhove") == 0) {
		setXmlStream(dtlDe, item, StreamType.JHOVE);
	    } else if (type.compareTo("changehistory_md") == 0) {
		setXmlStream(dtlDe, item, StreamType.HIST);
	    } else if (type.compareTo("marc") == 0) {
		/*
		 * FIXME : Workaround for some bug.
		 */
		setMarcStream(dtlDe, item, StreamType.MARC);
	    } else if (type.compareTo("metsHdr") == 0) {
		setXmlStream(dtlDe, item, StreamType.METS_HDR);
	    } else if (type.compareTo("structMap") == 0) {
		setXmlStream(dtlDe, item, StreamType.STRUCT_MAP);
	    } else if (type.compareTo("fileSec") == 0) {
		setXmlStream(dtlDe, item, StreamType.FILE_SEC);
	    }

	}
    }

    private void setXmlStream(DigitalEntity dtlDe, Node item, StreamType type) {
	try {
	    File file = new File(dtlDe.getLocation() + File.separator + "."
		    + dtlDe.getPid() + "_" + type.toString() + ".xml");
	    File stream = XmlUtils.stringToFile(file, nodeToString(item));
	    dtlDe.addStream(stream, "application/xml", type);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /*
     * FIXME : Workaround for some bug.
     */
    private void setMarcStream(DigitalEntity dtlDe, Node item, StreamType type) {
	try {
	    File file = new File(dtlDe.getLocation() + File.separator + "."
		    + dtlDe.getPid() + "_" + type.toString() + ".xml");
	    File stream = XmlUtils.stringToFile(file, getMarc(item));
	    dtlDe.addStream(stream, "application/xml", type);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private String getMarc(Node item) {
	Element marc = (Element) ((Element) item)
		.getElementsByTagName("record").item(0);
	marc.setAttribute("xmlns", "http://www.loc.gov/MARC21/slim");
	String xmlStr = nodeToString(marc);
	/*
	 * FIXME : Workaround for some bug.
	 */
	xmlStr = xmlStr.replaceAll("nam  2200000 u 4500",
		"00000    a2200000   4500");
	return xmlStr;
    }

    private String getItemType(Element root) {
	return root.getElementsByTagName("type").item(0).getTextContent();
    }

    private String getLabel(Element root) {
	return root.getElementsByTagName("label").item(0).getTextContent();
    }

    private Element getXmlRepresentation(final DigitalEntity dtlDe) {
	File digitalEntityFile = new File(dtlDe.getLocation() + File.separator
		+ dtlDe.getPid() + ".xml");
	return XmlUtils.getDocument(digitalEntityFile);
    }

    private DigitalEntity addSiblings(final DigitalEntity entity) {
	DigitalEntity dtlDe = entity;
	Element root;
	try {
	    root = XmlUtils.getDocument(entity.getXml());
	    NodeList list = root.getElementsByTagName("relation");
	    for (int i = 0; i < list.getLength(); i++) {
		Node item = list.item(i);
		String relPid = ((Element) item).getElementsByTagName("pid")
			.item(0).getTextContent();
		String usageType = ((Element) item)
			.getElementsByTagName("usage_type").item(0)
			.getTextContent();
		String type = ((Element) item).getElementsByTagName("type")
			.item(0).getTextContent();
		if (type.compareTo(DigitalEntityRelation.manifestation
			.toString()) == 0) {
		    DigitalEntity b = buildSimpleBean(entity.getLocation(),
			    relPid);
		    dtlDe.addRelated(b, usageType);
		}
	    }
	} catch (Exception e) {

	}

	return dtlDe;
    }

    private DigitalEntity addChildren(final DigitalEntity entity) {
	DigitalEntity dtlDe = entity;
	try {
	    Element root = XmlUtils.getDocument(entity.getXml());
	    NodeList list = root.getElementsByTagName("relation");
	    for (int i = 0; i < list.getLength(); i++) {
		Node item = list.item(i);
		String relPid = ((Element) item).getElementsByTagName("pid")
			.item(0).getTextContent();
		String usageType = ((Element) item)
			.getElementsByTagName("usage_type").item(0)
			.getTextContent();
		String type = ((Element) item).getElementsByTagName("type")
			.item(0).getTextContent();
		if (type.compareTo(DigitalEntityRelation.include.toString()) == 0
			&& (usageType.compareTo(DigitalEntityRelation.ARCHIVE
				.toString()) != 0)) {
		    try {
			DigitalEntity b = build(entity.getLocation(), relPid);
			b.setUsageType(usageType);
			addToTree(entity, b);

		    } catch (Exception e) {
			// TODO
			e.printStackTrace();
		    }
		}
	    }
	} catch (Exception e) {

	}
	return dtlDe;
    }

    private void linkToParent(DigitalEntity dtlDe) {
	try {
	    Element root = XmlUtils.getDocument(dtlDe.getXml());
	    NodeList list = root.getElementsByTagName("relation");
	    for (int i = 0; i < list.getLength(); i++) {
		Node item = list.item(i);
		String relPid = ((Element) item).getElementsByTagName("pid")
			.item(0).getTextContent();
		String type = ((Element) item).getElementsByTagName("type")
			.item(0).getTextContent();
		if (type.compareTo(DigitalEntityRelation.part_of.toString()) == 0) {
		    dtlDe.setIsParent(false);
		    dtlDe.setParentPid(relPid);
		}
	    }
	} catch (Exception e) {

	}
    }

    private void addToTree(DigitalEntity dtlDe, DigitalEntity related) {

	DigitalEntity parent = findParent(dtlDe, related);
	parent.setIsParent(true);
	related.setParentPid(parent.getPid());
	parent.addRelated(related, DigitalEntityRelation.part_of.toString());

    }

    private DigitalEntity findParent(DigitalEntity dtlDe, DigitalEntity related) {

	if (!(related.getUsageType().compareTo("VIEW") == 0)
		&& !(related.getUsageType().compareTo("VIEW_MAIN") == 0))
	    return dtlDe;
	String groupId = related.getStream(StreamType.DATA).getFileId();
	String fileId = groupIds2FileIds.get(groupId);
	String volumeId = this.fileIds2Volume.get(fileId);

	try {
	    if (groupId == null)
		throw new NullPointerException(related.getPid()
			+ " stream is unkown!");
	    if (fileId == null)
		throw new NullPointerException("streamId: " + groupId
			+ " mets fileId does not exist!");
	    if (volumeId == null)
		throw new NullPointerException("fileId: " + fileId
			+ " mets volume does not exist!");
	} catch (NullPointerException e) {
	    logger.warn(related.getPid() + " (" + related.getLabel()
		    + "), child of " + dtlDe.getPid() + " : " + e.getMessage()
		    + " mets relation is broken!");
	    related.setUsageType(ObjectType.file.toString());
	    return dtlDe;
	}
	for (RelatedDigitalEntity entity : dtlDe.getRelated()) {
	    // logger.debug(entity.entity.getPid());
	    if (entity.entity.getPid().compareTo(volumeId) == 0) {
		logger.debug(related.getPid() + " (" + related.getLabel()
			+ "), child of " + entity.entity.getPid() + " ("
			+ entity.entity.getLabel() + ") child of "
			+ dtlDe.getPid() + " (" + dtlDe.getLabel() + ")");
		related.setUsageType(ObjectType.issue.toString());
		return entity.entity;
	    }
	}

	return dtlDe;
    }

    /**
     * Creates a plain xml string of the node and of all it's children. The xml
     * string has no XML declaration.
     * 
     * @param node
     *            a org.w3c.dom.Node
     * @return a plain string representation of the node it's children
     */
    private String nodeToString(Node node) {
	try {
	    TransformerFactory transFactory = TransformerFactory.newInstance();
	    Transformer transformer = transFactory.newTransformer();
	    StringWriter buffer = new StringWriter(1024);
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
		    "yes");

	    transformer
		    .transform(new DOMSource(node), new StreamResult(buffer));
	    String str = buffer.toString();
	    return str;
	} catch (Exception e) {
	    e.printStackTrace();
	} catch (Error error) {
	    error.printStackTrace();
	}
	return "";
    }

}
