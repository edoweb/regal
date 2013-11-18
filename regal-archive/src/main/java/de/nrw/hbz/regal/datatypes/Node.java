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

import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
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

    /**
     * Dublin Core Metadata
     */
    public DCBean dublinCoreData = new DCBean();

    private String metadataFile;
    private String uploadFile;
    private String fileLabel;
    private Vector<Link> relsExt = new Vector<Link>();
    private Vector<Transformer> cms = new Vector<Transformer>();
    private String label = null;
    private String type = null;
    private String pid = null;
    private String state = null;
    private String mimeType = null;
    private String namespace = null;
    private String contentType = null;
    private Date lastModified = null;

    private BigInteger fileSize;

    private String checksum;

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
    public Node addTransformer(Transformer cm) {
	cms.add(cm);
	return this;
    }

    /**
     * @param id
     *            the Transformer-Id
     * @return this
     */
    public Node removeTransformer(String id) {
	Iterator<Transformer> it = cms.iterator();
	while (it.hasNext()) {
	    Transformer t = it.next();
	    if (t.getId().compareTo(id) == 0) {
		System.out.println("REMOVE: " + id);
		it.remove();
	    }

	}
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
    public List<Transformer> getContentModels() {

	return cms;
    }

    /**
     * @return the state
     */
    public String getState() {
	return state;
    }

    /**
     * @return the node's pid with namespace
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

    @Override
    public int hashCode() {
	return dublinCoreData.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	return dublinCoreData.equals(obj);
    }

    /**
     * @return a dublin core java object representation
     */
    public DCBean getBean() {
	return dublinCoreData;
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
		// System.out.println("REMOVE: " + this.pid + " <"
		// + rel.getPredicate() + "> " + rel.getObject());
		removed.add(rel);
	    } else {
		// System.out.println("ADD: " + this.pid + " <"
		// + rel.getPredicate() + "> " + rel.getObject());
		newRels.add(rel);
	    }
	}
	this.setRelsExt(newRels);
	return removed;
    }

    /**
     * @return a label for the upload data
     */
    public String getFileLabel() {
	return fileLabel;
    }

    /**
     * @param label
     *            a label for the upload data
     * @return this
     */
    public Node setFileLabel(String label) {
	fileLabel = label;
	return this;
    }

    /**
     * @param dc
     *            dublin core data in one bag
     * @return this
     */
    public Node setDcBean(DCBean dc) {
	this.dublinCoreData = dc;
	return this;
    }

    /**
     * @return returns the fileSize
     */
    public BigInteger getFileSize() {
	return fileSize;
    }

    /**
     * @param sizeInByte
     *            sets the filesize
     */
    public void setFileSize(BigInteger sizeInByte) {
	fileSize = sizeInByte;
    }

    /**
     * @return the checksum of the data
     */
    public String getChecksum() {
	return checksum;
    }

    /**
     * @param checksum
     *            sets a checksum for the data
     */
    public void setChecksum(String checksum) {
	this.checksum = checksum;
    }

}
