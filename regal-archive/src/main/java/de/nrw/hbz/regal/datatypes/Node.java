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
package de.nrw.hbz.regal.datatypes;

import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * A Node of object graph. Nodes are used to model complex objects
 * 
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class Node {

    String metadataFile;
    String uploadFile;
    Vector<Link> relsExt = new Vector<Link>();
    Vector<ContentModel> cms = new Vector<ContentModel>();
    private String label = null;
    private String type = null;
    private String pid = null;
    private String state = null;
    private String mimeType = null;
    private String namespace = null;
    private String contentType = null;
    private Date lastModified = null;
    DCBean bean = new DCBean();

    /**
     * Creates a new Node.
     * 
     */
    public Node() {
    }

    /**
     * Creates a new node with a certain pid.
     * 
     * @param pid
     *            the ID of the node.
     */
    public Node(String pid) {
	setPID(pid);
    }

    /**
     * Adds a relation. If the relation is a IS_PART_OF relation, the node is
     * assumed to have a new parent. All relations are stored in the rels_ext
     * collection.
     * 
     * @param link
     *            the link: predicats, object.
     * @return this
     */
    public Node addRelation(Link link) {
	relsExt.add(link);
	return this;
    }

    /**
     * Nodes can be associated with content models. A ContentModel defines web
     * services which can operate on the persisted node.
     * 
     * @param cm
     *            the ContentModel
     * @return this
     */
    public Node addContentModel(ContentModel cm) {
	cms.add(cm);
	return this;
    }

    /**
     * Removes all relations pointing to a certain object using a certain
     * predicate.
     * 
     * @param pred
     *            the link or predicate
     * @param obj
     *            the object or namespaced node-pid
     */
    public void removeRelation(String pred, String obj) {
	Vector<Link> newRels = new Vector<Link>();

	for (Link link : relsExt) {
	    if (link.getPredicate().compareTo(pred) == 0
		    && link.getObject().compareTo(obj) == 0) {

	    } else {
		newRels.add(link);
	    }
	}
	setRelsExt(newRels);
    }

    /**
     * Set the nodes PID
     * 
     * @param pid
     *            the ID
     * @return this
     */
    public Node setPID(String pid) {
	this.pid = pid;
	return this;
    }

    /**
     * The relsExt defines all relations of the node.
     * 
     * @param links
     *            all relations of the node
     * @return this
     */
    private Node setRelsExt(List<Link> links) {
	// myNode.setRELSEXT(new RELSEXT_type0());
	relsExt = new Vector<Link>();
	for (Link link : links) {
	    addRelation(link);
	}

	return this;
    }

    /**
     * The nodes namespace
     * 
     * @param namespace
     *            a namespace
     * 
     * @return this
     */
    public Node setNamespace(String namespace) {
	this.namespace = namespace;
	return this;
    }

    /**
     * The type of the Node. Valid types are dependend to the context.
     * 
     * @param str
     *            the type
     * @return this
     */
    public Node setType(String str) {
	this.type = str;

	return this;
    }

    /**
     * A state
     * 
     * @param str
     *            the state
     * @return this
     */
    public Node setState(String str) {
	this.state = str;
	return this;
    }

    /**
     * A Node can be associated to a file and it's containing data.
     * 
     * @param localLocation
     *            file location
     * @param mimeType
     *            mime type of the data
     * @return this
     */
    public Node setUploadData(String localLocation, String mimeType) {
	uploadFile = localLocation;
	setMimeType(mimeType);
	return this;

    }

    /**
     * @param mimeType
     *            the mimeType of the data
     * @return this
     */
    public Node setMimeType(String mimeType) {
	this.mimeType = mimeType;
	return this;
    }

    /**
     * The metadata file
     * 
     * @return the absolute path to file
     */
    public String getMetadataFile() {
	return metadataFile;
    }

    /**
     * @param metadataFile
     *            The absolutepath to the metadatafile
     */
    public void setMetadataFile(String metadataFile) {
	this.metadataFile = metadataFile;
    }

    /**
     * @return The mime type of the data
     */
    public String getMimeType() {
	return mimeType;
    }

    /**
     * @return the data file
     */
    public String getUploadFile() {
	return uploadFile;
    }

    /**
     * @return all content Models
     */
    public List<ContentModel> getContentModels() {

	return cms;
    }

    /**
     * @return the state
     */
    public String getState() {
	return state;
    }

    // /**
    // * @return all parents of the node
    // */
    // public Vector<String> getParents() {
    // return parentObjects;
    // }

    /**
     * @return the node's pid
     */
    public String getPID() {
	return pid;
    }

    /**
     * @return all relations
     */
    public List<Link> getRelsExt() {
	return relsExt;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
	return namespace;
    }

    /**
     * @return the node's type
     */
    public String getNodeType() {
	return type;
    }

    /**
     * @return the label
     */
    public String getLabel() {
	return label;
    }

    /**
     * @param str
     *            a label for the node
     * @return this
     */
    public Node setLabel(String str) {
	label = str;
	return this;
    }

    /**
     * @param e
     *            dc:contributer
     * @return this
     */
    public Node addContributer(String e) {
	bean.addContributer(e);
	return this;
    }

    /**
     * @param e
     *            dc:coverage
     * @return this
     */
    public Node addCoverage(String e) {
	bean.addCoverage(e);
	return this;
    }

    /**
     * @param e
     *            dc:creator
     * @return this
     */
    public Node addCreator(String e) {
	bean.addCreator(e);
	return this;
    }

    /**
     * @param e
     *            dc:date
     * @return this
     */
    public Node addDate(String e) {
	bean.addDate(e);
	return this;
    }

    /**
     * @param e
     *            dc:description
     * @return this
     */
    public Node addDescription(String e) {
	bean.addDescription(e);
	return this;
    }

    /**
     * @return dc:format
     */
    public Vector<String> getFormat() {
	return bean.getFormat();
    }

    /**
     * @param e
     *            dc:format
     * @return this
     */
    public Node addFormat(String e) {
	bean.addFormat(e);
	return this;
    }

    /**
     * @param e
     *            dc:identifier
     * @return this
     */
    public Node addIdentifier(String e) {
	bean.addIdentifier(e);
	return this;
    }

    /**
     * @param e
     *            dc:language
     * @return this
     */
    public Node addLanguage(String e) {
	bean.addLanguage(e);
	return this;
    }

    /**
     * @param e
     *            dc:publisher
     * @return this
     */
    public Node addPublisher(String e) {
	bean.addPublisher(e);
	return this;
    }

    /**
     * @param e
     *            dc:relation
     * @return this
     */
    public Node addDCRelation(String e) {
	bean.addRelation(e);
	return this;
    }

    /**
     * @param e
     *            dc:rights
     * @return this
     */
    public Node addRights(String e) {
	bean.addRights(e);
	return this;
    }

    /**
     * @param e
     *            dc:source
     * @return this
     */
    public Node addSource(String e) {
	bean.addSource(e);
	return this;
    }

    /**
     * @param e
     *            dc:subject
     * @return this
     */
    public Node addSubject(String e) {
	bean.addSubject(e);
	return this;
    }

    /**
     * @param e
     *            dc:title
     * @return this
     */
    public Node addTitle(String e) {
	bean.addTitle(e);
	return this;
    }

    /**
     * @param e
     *            dc:type
     * @return this
     */
    public Node addType(String e) {
	bean.addType(e);
	return this;
    }

    /**
     * @return dc:title
     */
    public Vector<String> getTitle() {
	return bean.getTitle();
    }

    /**
     * @return dc:contributer
     */
    public Vector<String> getContributer() {
	return bean.getContributer();
    }

    /**
     * @return dc:coverage
     */
    public Vector<String> getCoverage() {
	return bean.getCoverage();
    }

    /**
     * @return dc:description
     */
    public Vector<String> getDescription() {
	return bean.getDescription();
    }

    /**
     * @return dc:date
     */
    public Vector<String> getDate() {
	return bean.getDate();
    }

    /**
     * @return dc:creator
     */
    public Vector<String> getCreator() {
	return bean.getCreator();
    }

    /**
     * @return dc:subject
     */
    public String getFirstSubject() {
	return bean.getFirstSubject();
    }

    /**
     * @return dc:contributer
     */
    public String getFirstContributer() {
	return bean.getFirstContributer();
    }

    /**
     * @return dc:coverage
     */
    public String getFirstCoverage() {
	return bean.getFirstCoverage();
    }

    /**
     * @return dc:creator
     */
    public String getFirstCreator() {
	return bean.getFirstCreator();
    }

    /**
     * @return dc:date
     */
    public String getFirstDate() {
	return bean.getFirstDate();
    }

    /**
     * @return dc:description
     */
    public String getFirstDescription() {
	return bean.getFirstDescription();
    }

    /**
     * @return dc:format
     */
    public String getFirstFormat() {
	return bean.getFirstFormat();
    }

    /**
     * @return dc:itentifier
     */
    public String getFirstIdentifier() {
	return bean.getFirstIdentifier();
    }

    /**
     * @return dc:language
     */
    public String getFirstLanguage() {
	return bean.getFirstLanguage();
    }

    /**
     * @return dc:publisher
     */
    public String getFirstPublisher() {
	return bean.getFirstPublisher();
    }

    /**
     * @return dc:relation
     */
    public String getFirstRelation() {
	return bean.getFirstRelation();
    }

    /**
     * @return dc:title
     */
    public String getFirstTitle() {
	return bean.getFirstTitle();
    }

    /**
     * @return dc:type
     */
    public String getFirstType() {
	return bean.getFirstType();
    }

    /**
     * @param contributer
     *            dc:contributer
     */
    public void setContributer(Vector<String> contributer) {
	bean.setContributer(contributer);
    }

    /**
     * @param coverage
     *            dc:coverage
     */
    public void setCoverage(Vector<String> coverage) {
	bean.setCoverage(coverage);
    }

    /**
     * @param creator
     *            dc:creator
     */
    public void setCreator(Vector<String> creator) {
	bean.setCreator(creator);
    }

    /**
     * @param date
     *            dc:date
     */
    public void setDate(Vector<String> date) {
	bean.setDate(date);
    }

    /**
     * @param description
     *            dc:description
     */
    public void setDescription(Vector<String> description) {
	bean.setDescription(description);
    }

    /**
     * @param format
     *            dc:format
     */
    public void setFormat(Vector<String> format) {
	bean.setFormat(format);
    }

    /**
     * @param identifier
     *            dc:identifier
     */
    public void setIdentifier(Vector<String> identifier) {
	bean.setIdentifier(identifier);
    }

    /**
     * @param language
     *            dc:language
     */
    public void setLanguage(Vector<String> language) {
	bean.setLanguage(language);
    }

    /**
     * @param publisher
     *            dc:publisher
     */
    public void setPublisher(Vector<String> publisher) {
	bean.setPublisher(publisher);
    }

    /**
     * @param relation
     *            dc:relation
     */
    public void setDCRelation(Vector<String> relation) {
	bean.setRelation(relation);
    }

    /**
     * @param rights
     *            dc:rights
     */
    public void setRights(Vector<String> rights) {
	bean.setRights(rights);
    }

    /**
     * @param source
     *            dc:source
     */
    public void setSource(Vector<String> source) {
	bean.setSource(source);
    }

    /**
     * @param subject
     *            dc:subject
     */
    public void setSubject(Vector<String> subject) {
	bean.setSubject(subject);
    }

    /**
     * @param title
     *            dc:title
     */
    public void setTitle(Vector<String> title) {
	bean.setTitle(title);
    }

    /**
     * @param type
     *            dc:type
     */
    public void setType(Vector<String> type) {
	bean.setType(type);
    }

    @Override
    public int hashCode() {
	return bean.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	return bean.equals(obj);
    }

    /**
     * @return a dublin core java object representation
     */
    public DCBean getBean() {
	return bean;
    }

    /**
     * The content type can be used to specify certain content related
     * characteristics. e.g. type is more about the abstract role within the
     * graph.
     * 
     * 
     * @param type
     *            the actual content type
     */
    public void setContentType(String type) {
	contentType = type;
    }

    /**
     * @return the content type
     */
    public String getContentType() {
	return contentType;
    }

    /**
     * @return the last date the object has been modified
     */
    public Date getLastModified() {
	return lastModified;
    }

    /**
     * @param lastModified
     *            the last date the object has been modified
     */
    public void setLastModified(Date lastModified) {
	this.lastModified = lastModified;
    }

    /**
     * @return dc:identifier
     */
    public Vector<String> getIdentifier() {
	return bean.getIdentifier();
    }

    /**
     * @return dc:language
     */
    public Vector<String> getLanguage() {
	return bean.getLanguage();
    }

    /**
     * @return dc:publisher
     */
    public Vector<String> getPublisher() {
	return bean.getPublisher();
    }

    /**
     * @return dc:rights
     */
    public Vector<String> getRights() {
	return bean.getRights();
    }

    /**
     * @return dc:source
     */
    public Vector<String> getSource() {
	return bean.getSource();
    }

    /**
     * @return dc:subject
     */
    public Vector<String> getSubject() {
	return bean.getSubject();
    }

    /**
     * @return dc:type
     */
    public Vector<String> getType() {
	return bean.getType();
    }

    /**
     * @return dc:Relation
     */
    public Vector<String> getDCRelation() {
	return bean.getRelation();
    }

    /**
     * @param predicate
     *            all links with this predicate will be removed
     * @return the removed statements
     */
    public List<Link> removeRelations(String predicate) {
	Vector<Link> newRels = new Vector<Link>();
	Vector<Link> removed = new Vector<Link>();
	for (Link rel : relsExt) {
	    if (rel.getPredicate().compareTo(predicate) == 0) {
		System.out.println("REMOVE: " + this.pid + " <"
			+ rel.getPredicate() + "> " + rel.getObject());
		removed.add(rel);
	    } else {
		System.out.println("ADD: " + this.pid + " <"
			+ rel.getPredicate() + "> " + rel.getObject());
		newRels.add(rel);
	    }
	}
	this.setRelsExt(newRels);
	return removed;
    }

}
