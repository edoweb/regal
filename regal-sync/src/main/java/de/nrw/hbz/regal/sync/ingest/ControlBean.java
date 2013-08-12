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

import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.nrw.hbz.regal.sync.extern.DigitalEntity;
import de.nrw.hbz.regal.sync.extern.XmlUtils;

/**
 * Class ControlBean
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 12.07.2011
 * 
 */
@SuppressWarnings("javadoc")
public class ControlBean {
    DigitalEntity dtlBean = null;
    DigitoolControl2RdfMap map = new DigitoolControl2RdfMap();

    Vector<String> label = new Vector<String>();
    Vector<String> note = new Vector<String>();
    Vector<String> ingestId = new Vector<String>();
    Vector<String> ingestName = new Vector<String>();
    Vector<String> entityType = new Vector<String>();
    Vector<String> entityGroup = new Vector<String>();
    Vector<String> usageType = new Vector<String>();
    Vector<String> preservationLevel = new Vector<String>();
    Vector<String> partitionA = new Vector<String>();
    Vector<String> partitionB = new Vector<String>();
    Vector<String> partitionC = new Vector<String>();
    Vector<String> status = new Vector<String>();
    Vector<String> creationDate = new Vector<String>();
    Vector<String> creator = new Vector<String>();
    Vector<String> modificationDate = new Vector<String>();
    Vector<String> modifiedBy = new Vector<String>();
    Vector<String> adminUnit = new Vector<String>();

    public ControlBean(DigitalEntity dtlBean) throws Exception {
	this.dtlBean = dtlBean;

	Element root = XmlUtils.getDocument(dtlBean.getControl());

	XPathFactory factory = XPathFactory.newInstance();
	XPath xpath = factory.newXPath();
	XPathExpression expr;

	Object result;
	NodeList nodes;
	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlLabel);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addLabel(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e16) {

	    e16.printStackTrace();
	} catch (DOMException e16) {

	    e16.printStackTrace();
	}

	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlNote);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addNote(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e15) {

	    e15.printStackTrace();
	} catch (DOMException e15) {

	    e15.printStackTrace();
	}

	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlIngestId);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addIngestId(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e14) {

	    e14.printStackTrace();
	} catch (DOMException e14) {

	    e14.printStackTrace();
	}

	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlIngestName);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addIngestName(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e13) {

	    e13.printStackTrace();
	} catch (DOMException e13) {

	    e13.printStackTrace();
	}

	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlEntityType);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addEntityType(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e12) {

	    e12.printStackTrace();
	} catch (DOMException e12) {

	    e12.printStackTrace();
	}

	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlEntityGroup);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addEntityGroup(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e11) {

	    e11.printStackTrace();
	} catch (DOMException e11) {

	    e11.printStackTrace();
	}
	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlUsageType);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addUsageType(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e10) {

	    e10.printStackTrace();
	} catch (DOMException e10) {

	    e10.printStackTrace();
	}
	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlPreservationLevel);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addPreservationLevel(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e9) {
	    e9.printStackTrace();
	} catch (DOMException e9) {
	    e9.printStackTrace();
	}

	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlPartitionA);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addPartitionA(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e8) {
	    e8.printStackTrace();
	} catch (DOMException e8) {
	    e8.printStackTrace();
	}
	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlPartitionB);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addPartitionB(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e7) {
	    e7.printStackTrace();
	} catch (DOMException e7) {
	    e7.printStackTrace();
	}
	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlPartitionC);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addPartitionC(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e6) {
	    e6.printStackTrace();
	} catch (DOMException e6) {
	    e6.printStackTrace();
	}
	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlStatus);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addStatus(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e5) {
	    e5.printStackTrace();
	} catch (DOMException e5) {
	    e5.printStackTrace();
	}
	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlCreationDate);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addCreationDate(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e4) {
	    e4.printStackTrace();
	} catch (DOMException e4) {

	    e4.printStackTrace();
	}
	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlCreator);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addCreator(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e3) {

	    e3.printStackTrace();
	} catch (DOMException e3) {

	    e3.printStackTrace();
	}

	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlModificationDate);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addModificationDate(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e2) {

	    e2.printStackTrace();
	} catch (DOMException e2) {

	    e2.printStackTrace();
	}

	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlModifiedBy);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addModifiedBy(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e1) {

	    e1.printStackTrace();
	} catch (DOMException e1) {

	    e1.printStackTrace();
	}

	try {
	    expr = xpath.compile(DigitoolControl2RdfMap.xmlAdminUnit);
	    result = expr.evaluate(root, XPathConstants.NODESET);
	    nodes = (NodeList) result;
	    for (int i = 0; i < nodes.getLength(); i++) {

		addAdminUnit(nodes.item(i).getTextContent());
	    }
	} catch (XPathExpressionException e) {

	    e.printStackTrace();
	} catch (DOMException e) {

	    e.printStackTrace();
	}

    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addLabel(String textContent) {

	label.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addNote(String textContent) {

	note.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addIngestId(String textContent) {

	ingestId.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addIngestName(String textContent) {

	ingestName.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addEntityType(String textContent) {

	entityType.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addEntityGroup(String textContent) {

	entityGroup.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addUsageType(String textContent) {

	usageType.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addPreservationLevel(String textContent) {

	preservationLevel.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addPartitionA(String textContent) {

	partitionA.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addPartitionB(String textContent) {

	partitionB.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addPartitionC(String textContent) {

	partitionC.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addStatus(String textContent) {

	status.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addCreationDate(String textContent) {

	creationDate.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addCreator(String textContent) {

	creator.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addModificationDate(String textContent) {

	modificationDate.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addModifiedBy(String textContent) {

	modifiedBy.add(textContent);
    }

    /**
     * <p>
     * <em>Title: </em>
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param textContent
     */
    private void addAdminUnit(String textContent) {

	adminUnit.add(textContent);
    }

    public Vector<String> getLabel() {
	return label;
    }

    public void setLabel(Vector<String> label) {
	this.label = label;
    }

    public Vector<String> getNote() {
	return note;
    }

    public void setNote(Vector<String> note) {
	this.note = note;
    }

    public Vector<String> getIngestId() {
	return ingestId;
    }

    public void setIngestId(Vector<String> ingestId) {
	this.ingestId = ingestId;
    }

    public Vector<String> getIngestName() {
	return ingestName;
    }

    public void setIngestName(Vector<String> ingestName) {
	this.ingestName = ingestName;
    }

    public Vector<String> getEntityType() {
	return entityType;
    }

    public void setEntityType(Vector<String> entityType) {
	this.entityType = entityType;
    }

    public Vector<String> getEntityGroup() {
	return entityGroup;
    }

    public void setEntityGroup(Vector<String> entityGroup) {
	this.entityGroup = entityGroup;
    }

    public Vector<String> getUsageType() {
	return usageType;
    }

    public void setUsageType(Vector<String> usageType) {
	this.usageType = usageType;
    }

    public Vector<String> getPreservationLevel() {
	return preservationLevel;
    }

    public void setPreservationLevel(Vector<String> preservationLevel) {
	this.preservationLevel = preservationLevel;
    }

    public Vector<String> getPartitionA() {
	return partitionA;
    }

    public void setPartitionA(Vector<String> partitionA) {
	this.partitionA = partitionA;
    }

    public Vector<String> getPartitionB() {
	return partitionB;
    }

    public void setPartitionB(Vector<String> partitionB) {
	this.partitionB = partitionB;
    }

    public Vector<String> getPartitionC() {
	return partitionC;
    }

    public void setPartitionC(Vector<String> partitionC) {
	this.partitionC = partitionC;
    }

    public Vector<String> getStatus() {
	return status;
    }

    public void setStatus(Vector<String> status) {
	this.status = status;
    }

    public Vector<String> getCreationDate() {
	return creationDate;
    }

    public void setCreationDate(Vector<String> creationDate) {
	this.creationDate = creationDate;
    }

    public Vector<String> getCreator() {
	return creator;
    }

    public void setCreator(Vector<String> creator) {
	this.creator = creator;
    }

    public Vector<String> getModificationDate() {
	return modificationDate;
    }

    public void setModificationDate(Vector<String> modificationDate) {
	this.modificationDate = modificationDate;
    }

    public Vector<String> getModifiedBy() {
	return modifiedBy;
    }

    public void setModifiedBy(Vector<String> modifiedBy) {
	this.modifiedBy = modifiedBy;
    }

    public Vector<String> getAdminUnit() {
	return adminUnit;
    }

    public void setAdminUnit(Vector<String> adminUnit) {
	this.adminUnit = adminUnit;
    }

    public String getFirstNote() {
	Vector<String> elements = getNote();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstLabel() {
	Vector<String> elements = getLabel();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstIngestId() {
	Vector<String> elements = getIngestId();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstIngestName() {
	Vector<String> elements = getIngestName();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstEntityType() {
	Vector<String> elements = getEntityType();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstEntityGroup() {
	Vector<String> elements = getEntityGroup();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstUsageType() {
	Vector<String> elements = getUsageType();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstPreservationLevel() {
	Vector<String> elements = getPreservationLevel();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstPartitionA() {
	Vector<String> elements = getPartitionA();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstPartitionB() {
	Vector<String> elements = getPartitionB();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstPartitionC() {
	Vector<String> elements = getPartitionC();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstStatus() {
	Vector<String> elements = getStatus();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstCreator() {
	Vector<String> elements = getCreator();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstModificationDate() {
	Vector<String> elements = getModificationDate();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstModifiedBy() {
	Vector<String> elements = getModifiedBy();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

    public String getFirstAdminUnit() {
	Vector<String> elements = getAdminUnit();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.elementAt(0);
    }

}
