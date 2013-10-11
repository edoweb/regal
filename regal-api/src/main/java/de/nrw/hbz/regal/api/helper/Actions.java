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

import static de.nrw.hbz.regal.datatypes.Vocabulary.REL_CONTENT_TYPE;
import static de.nrw.hbz.regal.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.regal.datatypes.Vocabulary.TYPE_OBJECT;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.HAS_PART;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.IS_PART_OF;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.ws.rs.core.Response;

import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.regal.api.CreateObjectBean;
import de.nrw.hbz.regal.api.DCBeanAnnotated;
import de.nrw.hbz.regal.datatypes.ContentModel;
import de.nrw.hbz.regal.datatypes.DCBean;
import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.exceptions.ArchiveException;
import de.nrw.hbz.regal.fedora.CopyUtils;
import de.nrw.hbz.regal.fedora.FedoraFactory;
import de.nrw.hbz.regal.fedora.FedoraInterface;
import de.nrw.hbz.regal.fedora.FedoraVocabulary;
import de.nrw.hbz.regal.fedora.RdfException;
import de.nrw.hbz.regal.fedora.RdfUtils;
import de.nrw.hbz.regal.fedora.UrlConnectionException;

/**
 * Actions provide a single class to access the archive. All endpoints are using
 * this class.
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class Actions {

    @SuppressWarnings({ "serial" })
    private class UrnException extends RuntimeException {
	public UrnException(String arg0) {
	    super(arg0);
	}

	public UrnException(Throwable arg0) {
	    super(arg0);
	}

    }

    @SuppressWarnings({ "serial" })
    private class UpdateNodeException extends RuntimeException {
	public UpdateNodeException(Throwable cause) {
	    super(cause);
	}
    }

    final static Logger logger = LoggerFactory.getLogger(Actions.class);
    Services services = null;
    Representations representations = null;
    private FedoraInterface fedora = null;
    private String fedoraExtern = null;
    private String server = null;
    private String urnbase = null;

    // String namespace = null;

    /**
     * @throws IOException
     *             if properties can not be loaded.
     */
    public Actions() throws IOException {
	Properties properties = new Properties();
	properties.load(getClass().getResourceAsStream("/api.properties"));
	fedoraExtern = properties.getProperty("fedoraExtern");
	server = properties.getProperty("serverName");
	urnbase = properties.getProperty("urnbase");
	fedora = FedoraFactory.getFedoraImpl(
		properties.getProperty("fedoraIntern"),
		properties.getProperty("user"),
		properties.getProperty("password"));
	properties.load(getClass().getResourceAsStream(
		"/externalLinks.properties"));
	services = new Services(fedora, server);
	representations = new Representations(fedora, server);
    }

    /**
     * @return the host name
     */
    public String getServer() {
	return server;
    }

    /**
     * @param pids
     *            The pids that must be deleted
     * @return A short message.
     */
    public String deleteAll(List<String> pids) {
	if (pids == null || pids.isEmpty()) {
	    return "Nothing to delete!";
	}
	StringBuffer msg = new StringBuffer();
	for (String pid : pids) {

	    try {
		msg.append(delete(pid) + "\n");
	    } catch (Exception e) {
		logger.warn(pid + " " + e.getMessage());
	    }
	}

	return msg.toString();
    }

    /**
     * @param pid
     *            The pid that must be deleted
     * @return A short Message
     */
    public String delete(String pid) {

	String msg = "";
	fedora.deleteComplexObject(pid);

	try {
	    outdex(pid);
	} catch (Exception e) {
	    msg = e.getMessage();
	}

	return pid + " successfully deleted! " + msg;
    }

    /**
     * @param pid
     *            the pid of the object
     * @return a message
     */
    public String deleteMetadata(String pid) {
	fedora.deleteDatastream(pid, "metadata");
	return pid + ": metadata - datastream successfully deleted! ";
    }

    /**
     * @param pid
     *            the pid og the object
     * @return a message
     */
    public String deleteData(String pid) {
	fedora.deleteDatastream(pid, "data");
	return pid + ": data - datastream successfully deleted! ";
    }

    // -------------------------------------------------------

    /**
     * @param type
     *            The objectTyp
     * @return A list of pids with type {@type}
     */
    public List<String> findByType(String type) {
	String query = "* <" + REL_CONTENT_TYPE + "> \"" + type + "\"";
	InputStream in = fedora.findTriples(query, FedoraVocabulary.SPO,
		FedoraVocabulary.N3);
	return RdfUtils.getFedoraSubject(in);
    }

    /**
     * 
     * @param pid
     *            The pid
     * @param pred
     *            the predicate
     * @return A list of objects that are referenced by pid/predicate
     *         combination.
     */
    public List<String> findObject(String pid, String pred) {
	String query = "<info:fedora/" + pid + "> <" + pred + "> *";
	logger.debug(query);
	InputStream stream = fedora.findTriples(query, FedoraVocabulary.SPO,
		FedoraVocabulary.N3);
	return RdfUtils.getFedoraObjects(stream);
    }

    /**
     * @param pid
     *            The pid to read the data from
     * @return the data part of the pid
     */
    public Response readData(String pid) {
	try {
	    logger.debug("Redirect to " + fedoraExtern + "/objects/" + pid
		    + "/datastreams/data/content");
	    return Response.temporaryRedirect(
		    new java.net.URI(fedoraExtern + "/objects/" + pid
			    + "/datastreams/data/content")).build();
	} catch (URISyntaxException e) {
	    throw new HttpArchiveException(500, "Wrong setup!");
	}
    }

    /**
     * @param pid
     *            The pid to read the dublin core stream from.
     * @return A DCBeanAnnotated java object.
     */
    public DCBeanAnnotated readDC(String pid) {
	Node node = fedora.readNode(pid);
	if (node != null)
	    return new DCBeanAnnotated(node);
	return null;
    }

    /**
     * @param pid
     *            the pid of the object
     * @return n-triple metadata
     */
    public String readMetadata(String pid) {
	String metadataAdress = fedoraExtern + "/objects/" + pid
		+ "/datastreams/metadata/content";
	try {
	    return RdfUtils.readRdfToString(new URL(metadataAdress),
		    RDFFormat.NTRIPLES, RDFFormat.NTRIPLES, "text/plain");
	} catch (MalformedURLException e) {
	    throw new HttpArchiveException(500, "Wrong Metadata adress: "
		    + metadataAdress);
	} catch (RdfException e) {
	    throw new HttpArchiveException(500, e);
	} catch (UrlConnectionException e) {
	    throw new HttpArchiveException(404, e);
	}
    }

    /**
     * @param pid
     *            the pid that must be updated
     * @param content
     *            the file content as byte array
     * @param mimeType
     *            the mimetype of the file
     * @param name
     *            the name of the file
     * @return A short message
     * @throws IOException
     *             if data can not be written to a tmp file
     */
    public String updateData(String pid, InputStream content, String mimeType,
	    String name) throws IOException {
	if (content == null) {
	    throw new HttpArchiveException(406, pid
		    + " you've tried to upload an empty stream."
		    + " This action is not supported. Use HTTP DELETE instead.");
	}
	File tmp = File.createTempFile(name, "tmp");
	tmp.deleteOnExit();
	CopyUtils.copy(content, tmp);
	Node node = fedora.readNode(pid);
	if (node != null) {
	    node.setFileLabel(name);
	    node.setUploadData(tmp.getAbsolutePath(), mimeType);
	    fedora.updateNode(node);
	}
	return pid + " data successfully updated!";
    }

    /**
     * @param pid
     *            The pid that must be updated
     * @param content
     *            A dublin core object
     * @return a short message
     */
    public String updateDC(String pid, DCBeanAnnotated content) {
	content.trim();
	Node node = fedora.readNode(pid);
	DCBean dc = node.getBean();
	dc.setContributer(content.getContributer());
	dc.setCoverage(content.getCoverage());
	dc.setCreator(content.getCreator());
	dc.setDate(content.getDate());
	dc.setDescription(content.getDescription());
	dc.setFormat(content.getFormat());
	dc.setIdentifier(content.getIdentifier());
	dc.setLanguage(content.getLanguage());
	dc.setPublisher(content.getPublisher());
	dc.setDescription(content.getDescription());
	dc.setRights(content.getRights());
	dc.setSource(content.getSource());
	dc.setSubject(content.getSubject());
	dc.setTitle(content.getTitle());
	dc.setType(content.getType());
	node.setDcBean(dc);
	fedora.updateNode(node);
	return pid + " dc successfully updated!";
    }

    /**
     * @param pid
     *            The pid that must be updated
     * @param content
     *            The metadata as rdf string
     * @return a short message
     */
    public String updateMetadata(String pid, String content) {
	try {
	    if (content == null) {
		throw new HttpArchiveException(406, pid
			+ "You've tried to upload an empty string."
			+ " This action is not supported."
			+ " Use HTTP DELETE instead.");
	    }
	    File file = CopyUtils.copyStringToFile(content);
	    Node node = fedora.readNode(pid);
	    if (node != null) {
		node.setMetadataFile(file.getAbsolutePath());
		fedora.updateNode(node);
	    }
	    return pid + " metadata successfully updated!";
	} catch (IOException e) {
	    throw new UpdateNodeException(e);
	}
    }

    /**
     * @param node
     *            read metadata from the Node to the repository
     * @return a message
     */
    public String updateMetadata(Node node) {
	fedora.updateNode(node);
	String pid = node.getPID();
	return pid + " metadata successfully updated!";
    }

    /**
     * @param pid
     *            The pid to which links must be added
     * @param links
     *            list of links
     * @return a short message
     */
    public String addLinks(String pid, List<Link> links) {
	Node node = fedora.readNode(pid);
	for (Link link : links) {
	    node.addRelation(link);
	}
	fedora.updateNode(node);
	return pid + " " + links + " links successfully added.";
    }

    /**
     * @param pid
     *            The pid to which links must be added uses: Vector<Link> v =
     *            new Vector<Link>(); v.add(link); return addLinks(pid, v);
     * @param link
     *            a link
     * @return a short message
     */
    public String addLink(String pid, Link link) {
	Vector<Link> v = new Vector<Link>();
	v.add(link);
	return addLinks(pid, v);
    }

    /**
     * Initialises all content models for one namespace
     * 
     * @param namespace
     *            a namespace
     * @return a message
     */
    public String contentModelsInit(String namespace) {
	try {
	    fedora.updateContentModel(ContentModelFactory.createHeadModel(
		    namespace, server));
	    fedora.updateContentModel(ContentModelFactory
		    .createEJournalModel(namespace));
	    fedora.updateContentModel(ContentModelFactory
		    .createMonographModel(namespace));
	    fedora.updateContentModel(ContentModelFactory
		    .createWebpageModel(namespace));
	    fedora.updateContentModel(ContentModelFactory
		    .createVersionModel(namespace));
	    fedora.updateContentModel(ContentModelFactory
		    .createVolumeModel(namespace));
	    fedora.updateContentModel(ContentModelFactory.createPdfModel(
		    namespace, server));
	    return "Success!";
	} catch (ArchiveException e) {
	    throw new HttpArchiveException(500, e);
	}
    }

    /**
     * @param namespace
     *            the namespace will be deleted
     * @return a message
     */
    public String deleteNamespace(String namespace) {
	List<String> objects = null;
	try {
	    objects = fedora.findNodes(namespace + ":*");
	} catch (Exception e) {

	}
	return deleteAll(objects);
    }

    /**
     * @param pid
     *            the pid with pdf data
     * @param namespace
     *            the namespace
     * @return the result of a pdfbox call
     * @throws URISyntaxException
     *             if redirect is wrongly configured
     */
    public Response getFulltext(String pid, String namespace)
	    throws URISyntaxException {
	return Response.temporaryRedirect(
		new java.net.URI(fedoraExtern + "/objects/" + namespace + ":"
			+ pid + "/methods/" + namespace
			+ "CM:pdfServiceDefinition/pdfbox")).build();
    }

    /**
     * @param pid
     *            the pid
     * @return the last modified date
     */
    public Date getLastModified(String pid) {
	Node node = fedora.readNode(pid);
	return node.getLastModified();
    }

    /**
     * @param input
     *            the input defines the contenttype and a optional parent
     * @param rawPid
     *            the pid without namespace
     * @param namespace
     *            the namespace
     * @param models
     *            content models for the resource
     * @return the Node representing the resource
     */
    public Node createResource(CreateObjectBean input, String rawPid,
	    String namespace, List<ContentModel> models) {
	logger.info("create " + input.getType());
	Node node = createNodeIfNotExists(rawPid, namespace, input, models);
	setNodeType(input, node);
	linkWithParent(input, node);
	return node;
    }

    private Node createNodeIfNotExists(String rawPid, String namespace,
	    CreateObjectBean input, List<ContentModel> models) {
	String pid = namespace + ":" + rawPid;
	Node node = null;
	if (fedora.nodeExists(pid)) {
	    node = fedora.readNode(pid);
	} else {
	    node = new Node();
	    node.setNamespace(namespace).setPID(pid);
	    node.setContentType(input.getType());
	    if (models != null)
		for (ContentModel model : models) {
		    node.addContentModel(model);
		}
	    fedora.createNode(node);
	}
	return node;
    }

    private void setNodeType(CreateObjectBean input, Node node) {
	node.setType(TYPE_OBJECT);
	node.setContentType(input.getType());
    }

    private void linkWithParent(CreateObjectBean input, Node node) {
	String parentPid = input.getParentPid();
	fedora.unlinkParent(node);
	fedora.linkToParent(node, parentPid);
	fedora.linkParentToNode(parentPid, node.getPID());
	fedora.updateNode(node);
    }

    /**
     * @param pid
     *            adds lobidmetadata (if avaiable) to the node and updates the
     *            repository
     * @return a message
     */
    public String lobidify(String pid) {
	Node node = fedora.readNode(pid);
	node = services.lobidify(node);
	return updateMetadata(node);
    }

    /**
     * Returns an existing urn. Throws UrnException if found 0 urn or more than
     * 1 urns.
     * 
     * @param pid
     *            the pid of an object
     * @param namespace
     *            the namespace
     * @return the urn
     */
    public String getUrn(String pid, String namespace) {
	try {

	    String metadataAdress = fedoraExtern + "/objects/" + namespace
		    + ":" + pid + "/datastreams/metadata/content";
	    URL url = new URL(metadataAdress);
	    List<String> urns = RdfUtils.findRdfObjects(namespace + ":" + pid,
		    "http://geni-orca.renci.org/owl/topology.owl#hasURN", url,
		    RDFFormat.NTRIPLES, "text/plain");
	    if (urns == null || urns.isEmpty()) {
		throw new UrnException("Found no urn!");
	    }
	    if (urns.size() != 1) {
		throw new UrnException("Found " + urns.size() + " urns. "
			+ urns + "\n Expected exactly one urn.");
	    }
	    return urns.get(0);

	} catch (Exception e) {
	    throw new UrnException(e);
	}

    }

    /**
     * @param pid
     *            the pid of the object
     * @param namespace
     *            the namespace
     * @return a epicur display for the pid
     */
    public String epicur(String pid, String namespace) {
	String url = urnbase + namespace + ":" + pid;
	return services.epicur(url, getUrn(pid, namespace));
    }

    /**
     * @param pid
     *            The pid of an object
     * @return The metadata a oaidc-xml
     */
    public String oaidc(String pid) {
	return services.oaidc(pid);
    }

    /**
     * @param node
     *            the node with pdf data
     * @return the plain text content of the pdf
     */
    public String pdfbox(Node node) {
	return services.pdfbox(node, fedoraExtern);
    }

    /**
     * @param pid
     *            the pid of a node that must be published on the oai interface
     * @return A short message.
     */
    public String makeOAISet(String pid) {
	return services.makeOAISet(pid, fedoraExtern);
    }

    /**
     * @param pid
     *            The pid to remove from index
     * @return A short message
     */
    public String outdex(String pid) {
	return services.outdex(pid);
    }

    /**
     * @param p
     *            The pid that must be indexed
     * @param namespace
     *            the namespace of the pid
     * @return a short message.
     */
    public String index(String p, String namespace) {
	return services.index(p, namespace);
    }

    /**
     * @param node
     *            the node with pdf data
     * @return the plain text content of the pdf
     */
    public String itext(Node node) {
	return services.itext(node, fedoraExtern);
    }

    /**
     * @param pid
     *            the pid
     * @param format
     *            application/rdf+xml text/plain application/json
     * @return a oai_ore resource map
     */
    public String getReM(String pid, String format) {
	List<String> parents = findObject(pid, IS_PART_OF);
	List<String> children = findObject(pid, HAS_PART);
	return representations.getReM(pid, format, fedoraExtern, parents,
		children);
    }

    /**
     * @param type
     *            a contentType
     * @return all objects of contentType type
     */
    public List<String> getAll(String type) {

	if (type == null || type.isEmpty())
	    return getAll();
	else
	    return findByType(type);
    }

    /**
     * @return a list of all objects
     */
    public List<String> getAll() {

	String query = "* <" + REL_IS_NODE_TYPE + "> \"" + TYPE_OBJECT + "\"";
	InputStream stream = fedora.findTriples(query, FedoraVocabulary.SPO,
		FedoraVocabulary.N3);
	return RdfUtils.getFedoraSubject(stream);
    }

    /**
     * @param type
     *            the type to be displaye
     * @return html listing of all objects
     */
    public String getAllAsHtml(String type) {
	List<String> list = null;
	if (type == null || type.isEmpty())
	    list = getAll();
	else
	    list = findByType(type);
	return representations.getAllOfTypeAsHtml(list, type);
    }

    /**
     * @param pid
     *            the pid to read from
     * @return the parentPid and contentType as json
     */
    public CreateObjectBean getRegalJson(String pid) {
	return representations.getRegalJson(pid);
    }

    /**
     * @param pid
     *            the will be read to the node
     * @return a Node containing the data from the repository
     */
    public Node readNode(String pid) {
	return fedora.readNode(pid);
    }

    Services getServices() {
	return services;
    }

    /**
     * @param node
     *            a node with a pdf data stream
     * @return a URL to a PDF/A Conversion
     */
    public String pdfa(Node node) {
	return services.pdfa(node, fedoraExtern);
    }

    /**
     * Generates a urn
     * 
     * @param pid
     *            usually the pid of an object
     * @param namespace
     *            usually the namespace
     * @param snid
     *            the urn subnamespace id
     * @return the urn
     */
    public String addUrn(String pid, String namespace, String snid) {
	String subject = namespace + ":" + pid;
	String urn = services.generateUrn(subject, snid);
	String hasUrn = "http://geni-orca.renci.org/owl/topology.owl#hasURN";
	// String sameAs = "http://www.w3.org/2002/07/owl#sameAs";
	String metadata = readMetadata(subject);
	metadata = RdfUtils.replaceTriple(subject, hasUrn, urn, true, metadata);
	updateMetadata(namespace + ":" + pid, metadata);
	return "Update " + subject + " metadata " + metadata;
    }
}
