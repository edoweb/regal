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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.regal.api.CreateObjectBean;
import de.nrw.hbz.regal.api.DCBeanAnnotated;
import de.nrw.hbz.regal.datatypes.ContentModel;
import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.exceptions.ArchiveException;
import de.nrw.hbz.regal.fedora.CopyUtils;
import de.nrw.hbz.regal.fedora.FedoraFactory;
import de.nrw.hbz.regal.fedora.FedoraInterface;
import de.nrw.hbz.regal.fedora.FedoraVocabulary;
import de.nrw.hbz.regal.fedora.RdfUtils;

/**
 * Actions provide a single class to access the archive. All endpoints are using
 * this class.
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class Actions {

    final static Logger logger = LoggerFactory.getLogger(Actions.class);

    Services services = null;
    Representations representations = null;
    private FedoraInterface fedora = null;
    private String fedoraExtern = null;
    private String server = null;

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
     * @param pids
     *            The pids that must be deleted
     * @return A short message.
     */
    public String deleteAll(List<String> pids) {
	if (pids == null || pids.isEmpty())
	    throw new HttpArchiveException(304, "Nothing to delete!");
	logger.info("Delete All");
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
    public Vector<String> findByType(String type) {

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
	logger.info(query);
	InputStream stream = fedora.findTriples(query, FedoraVocabulary.SPO,
		FedoraVocabulary.N3);
	return RdfUtils.getFedoraObjects(stream);
    }

    /**
     * @param pid
     *            The pid to read the data from
     * @return the data part of the pid
     * @throws URISyntaxException
     *             if the data url is not wellformed
     */
    public Response readData(String pid) throws URISyntaxException {

	Node node = null;

	node = fedora.readNode(pid);

	if (node != null) {
	    return Response.temporaryRedirect(
		    new java.net.URI(fedoraExtern + "/objects/" + pid
			    + "/datastreams/data/content")).build();

	}
	return null;

    }

    /**
     * @param pid
     *            The pid to read the dublin core stream from.
     * @return A DCBeanAnnotated java object.
     */
    public DCBeanAnnotated readDC(String pid) {

	logger.info("Read DC");

	Node node = fedora.readNode(pid);
	if (node != null)
	    return new DCBeanAnnotated(node);

	return null;

    }

    /**
     * @param pid
     *            the pid of the object
     * @return n-triple metadata
     * @throws URISyntaxException
     *             if the fedora link is misconfigured
     * @throws MalformedURLException
     *             if the fedora link is misconfigured
     * @throws IOException
     *             if reading fails
     */
    public String readMetadata(String pid) throws URISyntaxException,
	    MalformedURLException, IOException {

	String result = null;
	Node node = fedora.readNode(pid);
	InputStream is = null;
	if (node != null) {

	    URL url = new URL(fedoraExtern + "/objects/" + pid
		    + "/datastreams/metadata/content");
	    URLConnection connection = url.openConnection();
	    try {
		is = connection.getInputStream();
		CopyUtils.copy(is, result);
		return result;
	    } catch (IOException ioe) {
		if (connection instanceof HttpURLConnection) {
		    HttpURLConnection httpConn = (HttpURLConnection) connection;
		    int statusCode = httpConn.getResponseCode();
		    if (statusCode != 200) {
			throw new HttpArchiveException(statusCode,
				httpConn.getResponseMessage(), ioe);
		    }
		}
	    }
	}
	throw new HttpArchiveException(404, "Datastream does not exist!");
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

	logger.info("Update DC");

	content.trim();
	Node node = fedora.readNode(pid);
	node.setContributer(content.getContributer());
	node.setCoverage(content.getCoverage());
	node.setCreator(content.getCreator());
	node.setDate(content.getDate());
	node.setDescription(content.getDescription());
	node.setFormat(content.getFormat());
	node.setIdentifier(content.getIdentifier());
	node.setLanguage(content.getLanguage());
	node.setPublisher(content.getPublisher());
	node.setDescription(content.getDescription());
	node.setRights(content.getRights());
	node.setSource(content.getSource());
	node.setSubject(content.getSubject());
	node.setTitle(content.getTitle());
	node.setType(content.getType());
	fedora.updateNode(node);

	return pid + " dc successfully updated!";

    }

    /**
     * @param pid
     *            The pid that must be updated
     * @param content
     *            The metadata as rdf string
     * @return a short message
     * @throws IOException
     *             if the metadata can not be cached
     */
    public String updateMetadata(String pid, String content) throws IOException {

	if (content == null || content.isEmpty()) {
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

    }

    /**
     * @param node
     *            read metadata from the Node to the repository
     * @return a message
     */
    public String updateMetadata(Node node) {
	String pid = node.getPID();
	if (node != null) {
	    fedora.updateNode(node);
	}
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
	// long start = System.nanoTime();
	fedora.updateNode(node);
	// long elapsed = System.nanoTime() - start;
	// System.out.println("update node duration: " + elapsed);

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
     * @return a list of all objects
     */
    public List<String> getAll() {
	String query = "* <" + REL_IS_NODE_TYPE + "> \"" + TYPE_OBJECT + "\"";
	InputStream stream = fedora.findTriples(query, FedoraVocabulary.SPO,
		FedoraVocabulary.N3);
	return RdfUtils.getFedoraSubject(stream);
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

	    fedora.updateContentModel(ContentModelFactory
		    .createHeadModel(namespace));

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

	    fedora.updateContentModel(ContentModelFactory
		    .createPdfModel(namespace));

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
	List<String> objects = fedora.findNodes(namespace + ":*");
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
     *            the plain pid
     * @param namespace
     *            namespace of the pid
     * @return a epicur yml
     * @throws URISyntaxException
     *             if redirect coded wrong
     */
    public Response getEpicur(String pid, String namespace)
	    throws URISyntaxException {

	return Response.temporaryRedirect(
		new java.net.URI(fedoraExtern + "/objects/" + namespace + ":"
			+ pid + "/methods/" + namespace
			+ "CM:headServiceDefinition/epicur")).build();

    }

    /**
     * @param pid
     *            the plain pid
     * @param namespace
     *            namespace of the pid
     * @return oai dc yml
     * @throws URISyntaxException
     *             if coded wrong
     */
    public Response getOAI_DC(String pid, String namespace)
	    throws URISyntaxException {

	return Response.temporaryRedirect(
		new java.net.URI(fedoraExtern + "/objects/" + namespace + ":"
			+ pid + "/methods/" + namespace
			+ "CM:headServiceDefinition/oai_dc")).build();

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
	String pid = namespace + ":" + rawPid;
	Node node = null;

	if (fedora.nodeExists(pid)) {
	    node = fedora.readNode(pid);
	} else {
	    node = new Node();
	    node.setNamespace(namespace).setPID(pid);
	    node.setContentType(input.getType());
	    for (ContentModel model : models) {
		node.addContentModel(model);
	    }
	    fedora.createNode(node);
	}
	node.setType(TYPE_OBJECT);
	node.setContentType(input.getType());

	String parentPid = input.getParentPid();
	// remove link from old parent
	fedora.unlinkParent(node);
	// link node to new parent
	fedora.linkToParent(node, parentPid);
	// link new parent to node
	fedora.linkParentToNode(parentPid, node.getPID());

	fedora.updateNode(node);
	return node;
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
     * @param pid
     *            the pid of the object
     * @param namespace
     *            the namespace
     * @return a epicur display for the pid
     */
    public String epicur(String pid, String namespace) {
	View view = getView(pid);
	return services.epicur(pid, namespace, view);
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

	try {
	    URL metadata = new URL(fedoraExtern + "/objects/" + pid
		    + "/datastreams/metadata/content");
	    Date lastModified = getLastModified(pid);
	    List<String> parents = findObject(pid, IS_PART_OF);
	    List<String> children = findObject(pid, HAS_PART);
	    return representations.getReM(pid, format, metadata, lastModified,
		    fedoraExtern, parents, children);
	} catch (MalformedURLException e) {
	    throw new InternalUrlException(e);
	}
    }

    /**
     * @param pid
     *            The pid of an existing object.
     * @return the view of the object
     */
    public View getView(String pid) {
	return representations.getView(pid, oaidc(pid), fedoraExtern);
    }

    /**
     * @return all objects in a html list
     */
    public String getAllAsHtml() {
	return representations.getAllAsHtml(getAll());
    }

    /**
     * @param type
     *            the type to be displaye
     * @return html listing of all objects
     */
    public String getAllOfTypeAsHtml(String type) {
	List<String> list = findByType(type);
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

    @SuppressWarnings({ "javadoc", "serial" })
    public class InternalUrlException extends RuntimeException {

	public InternalUrlException(Throwable e) {
	    super(e);
	}
    }

    @SuppressWarnings("unused")
    private String getCacheUri(String pid) throws UnsupportedEncodingException {
	String cacheUri = null;
	String pidWithoutNamespace = pid.substring(pid.indexOf(':') + 1);
	String namespace = pid.substring(0, pid.indexOf(':'));

	if (pid.contains("edoweb") || pid.contains("ellinet")) {
	    if (pid.length() <= 17) {
		cacheUri = this.server + "/" + namespace + "base/"
			+ pidWithoutNamespace;

	    }
	}
	if (pid.contains("dipp")) {
	    cacheUri = this.server
		    + "/"
		    + namespace
		    + "base/"
		    + URLEncoder.encode(URLEncoder.encode(pid, "utf-8"),
			    "utf-8");

	}
	if (pid.contains("ubm")) {
	    cacheUri = this.server + "/" + namespace + "base/"
		    + pidWithoutNamespace;

	}
	if (pid.contains("fhdd")) {
	    cacheUri = this.server + "/" + namespace + "base/"
		    + pidWithoutNamespace;

	}
	if (pid.contains("kola")) {
	    cacheUri = this.server + "/" + namespace + "base/"
		    + pidWithoutNamespace;

	}
	return cacheUri;
    }
}
