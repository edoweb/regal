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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import de.nrw.hbz.regal.api.CreateObjectBean;
import de.nrw.hbz.regal.api.DCBeanAnnotated;
import de.nrw.hbz.regal.datatypes.DCBean;
import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.datatypes.Transformer;
import de.nrw.hbz.regal.exceptions.ArchiveException;
import de.nrw.hbz.regal.fedora.CopyUtils;
import de.nrw.hbz.regal.fedora.FedoraFactory;
import de.nrw.hbz.regal.fedora.FedoraInterface;
import de.nrw.hbz.regal.fedora.FedoraVocabulary;
import de.nrw.hbz.regal.fedora.RdfException;
import de.nrw.hbz.regal.fedora.RdfUtils;
import de.nrw.hbz.regal.fedora.UrlConnectionException;
import de.nrw.hbz.regal.search.Search;

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
    private static Actions actions = null;

    private Services services = null;
    private Representations representations = null;
    private FedoraInterface fedora = null;

    private String fedoraExtern = null;
    private String server = null;
    private String urnbase = null;
    private Search search = null;
    private String escluster = null;

    // String namespace = null;

    /**
     * @throws IOException
     *             if properties can not be loaded.
     */
    private Actions() throws IOException {
	Properties properties = new Properties();
	properties.load(getClass().getResourceAsStream("/api.properties"));
	fedoraExtern = properties.getProperty("fedoraExtern");
	server = properties.getProperty("serverName");
	urnbase = properties.getProperty("urnbase");
	escluster = properties.getProperty("escluster");
	fedora = FedoraFactory.getFedoraImpl(
		properties.getProperty("fedoraIntern"),
		properties.getProperty("user"),
		properties.getProperty("password"));
	properties.load(getClass().getResourceAsStream(
		"/externalLinks.properties"));
	services = new Services(fedora, server);
	representations = new Representations(fedora, server);
	search = new Search(escluster);
    }

    /**
     * @return an instance of this Actions.class
     * @throws IOException
     *             if properties can't be read
     */
    public static Actions getInstance() throws IOException {
	if (actions == null)
	    actions = new Actions();
	return actions;
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
	Node node = readNode(pid);
	fedora.deleteComplexObject(pid);

	try {
	    removeFromIndex(node.getNamespace(), node.getContentType(), pid);
	} catch (Exception e) {
	    msg = e.getMessage();
	}

	return pid + " successfully deleted! \n" + msg + "\n";
    }

    /**
     * @param p
     *            the id part of a pid
     * @param namespace
     *            the namespace part of a pid
     * @return a message
     */
    public String deleteMetadata(String p, String namespace) {
	String pid = namespace + ":" + p;
	fedora.deleteDatastream(pid, "metadata");
	index(readNode(pid));
	return pid + ": metadata - datastream successfully deleted! ";
    }

    /**
     * @param pid
     *            the pid og the object
     * @return a message
     */
    public String deleteData(String pid) {
	fedora.deleteDatastream(pid, "data");
	index(readNode(pid));
	return pid + ": data - datastream successfully deleted! ";
    }

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
	index(node);
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
	index(node);
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
	    RdfUtils.validate(content);
	    File file = CopyUtils.copyStringToFile(content);
	    Node node = fedora.readNode(pid);
	    if (node != null) {
		node.setMetadataFile(file.getAbsolutePath());
		fedora.updateNode(node);
	    }
	    index(node);
	    return pid + " metadata successfully updated!";
	} catch (RdfException e) {
	    throw new HttpArchiveException(400);
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
	index(node);
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
	index(node);
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
     * 
     * @param cms
     *            a List of Transformers
     * @return a message
     */
    public String contentModelsInit(List<Transformer> cms) {
	try {
	    fedora.updateContentModels(cms);
	    return "Success!";
	} catch (ArchiveException e) {
	    throw new HttpArchiveException(500, e);
	}
    }

    /**
     * @param query
     *            a query to define objects that must be deleted
     * @return a message
     */
    public String deleteByQuery(String query) {
	List<String> objects = null;
	try {
	    objects = fedora.findNodes(query);
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
     * @return the Node representing the resource
     */
    public Node createResource(CreateObjectBean input, String rawPid,
	    String namespace) {
	logger.debug("create " + input.getType());
	Node node = createNodeIfNotExists(rawPid, namespace, input);
	setNodeType(input, node);
	linkWithParent(input, node);
	updateTransformer(input, node);
	fedora.updateNode(node);
	return node;
    }

    private Node createNodeIfNotExists(String rawPid, String namespace,
	    CreateObjectBean input) {
	String pid = namespace + ":" + rawPid;
	Node node = null;
	if (fedora.nodeExists(pid)) {
	    node = fedora.readNode(pid);
	} else {
	    node = new Node();
	    node.setNamespace(namespace).setPID(pid);
	    node.setContentType(input.getType());
	    fedora.createNode(node);
	    index(node);
	}
	return node;
    }

    private void setNodeType(CreateObjectBean input, Node node) {
	node.setType(TYPE_OBJECT);
	node.setContentType(input.getType());
	index(node);
    }

    private void linkWithParent(CreateObjectBean input, Node node) {
	String parentPid = input.getParentPid();
	fedora.unlinkParent(node);
	fedora.linkToParent(node, parentPid);
	fedora.linkParentToNode(parentPid, node.getPID());
	index(node);
    }

    private void updateTransformer(CreateObjectBean input, Node node) {
	String[] transformers = input.getTransformer();
	if (transformers != null && transformers.length != 0)
	    for (String t : transformers) {
		node.addTransformer(new Transformer(t));
	    }
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
	index(node);
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
     * @param index
     *            the elasticsearch index
     * @param type
     *            the type of the resource
     * @param pid
     *            The pid to remove from index
     * @return A short message
     */
    public String removeFromIndex(String index, String type, String pid) {
	search.deleteSync(index, type, pid);
	return pid + " removed from index " + index + "!";
    }

    /**
     * @param p
     *            The pid that must be indexed
     * @param namespace
     *            the namespace of the pid
     * @param type
     *            the type of the resource
     * @return a short message.
     */
    public String index(String p, String namespace, String type) {
	String viewAsString = getReM(namespace + ":" + p, "application/json");
	viewAsString = JSONObject.toJSONString(ImmutableMap.of("@graph",
		(JSONArray) JSONValue.parse(viewAsString)));
	search.indexSync(namespace, type, namespace + ":" + p, viewAsString);
	return namespace + ":" + p + " indexed!";
    }

    private String index(Node n) {
	String namespace = n.getNamespace();
	String pid = n.getPID();
	String p = pid.substring(pid.indexOf(":") + 1);
	return index(p, namespace, n.getContentType());
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
	List<String> parents = getRelatives(pid, IS_PART_OF);
	List<String> children = getRelatives(pid, HAS_PART);
	return representations.getReM(pid, format, fedoraExtern, parents,
		children);
    }

    /**
     * Returns a list of pids of related objects. Looks for other objects those
     * are connected to the pid by a certain relation
     * 
     * @param pid
     *            the pid to find relatives of
     * @param relation
     *            a relation that describes what kind of relatives you are
     *            looking for
     * @return list of pids of related objects
     */
    public List<String> getRelatives(String pid, String relation) {
	List<String> result = new Vector<String>();
	Node node = readNode(pid);
	List<Link> links = node.getRelsExt();
	for (Link l : links) {
	    if (l.getPredicate().equals(relation))
		result.add(l.getObject());
	}
	return result;
    }

    /**
     * @param type
     *            a contentType
     * @param namespace
     *            list only objects in this namespace
     * @param from
     *            show only hits starting at this index
     * @param until
     *            show only hits ending at this index
     * @param getListingFrom
     *            List Resources from elasticsearch or from fedora. Allowed
     *            values: "repo" and "es"
     * @return all objects of contentType type
     */
    public List<String> list(String type, String namespace, int from,
	    int until, String getListingFrom) {

	List<String> list = null;
	if (!getListingFrom.equals("es")) {
	    if (type == null || type.isEmpty())
		list = listAllFromRepo(namespace, from, until);
	    else
		list = listAllFromRepo(type, namespace, from, until);
	} else {
	    list = listAllFromSearch(type, namespace, from, until);
	}

	return list;
    }

    /**
     * @param type
     *            The objectTyp
     * @param namespace
     *            list only objects in this namespace
     * @param from
     *            show only hits starting at this index
     * @param until
     *            show only hits ending at this index
     * @return A list of pids with type {@type}
     */
    public List<String> listAllFromSearch(String type, String namespace,
	    int from, int until) {

	return search.listIds(namespace, type, from, until);
    }

    /**
     * @param type
     *            the type to be displaye
     * @param namespace
     *            list only objects in this namespace
     * @param from
     *            show only hits starting at this index
     * @param until
     *            show only hits ending at this index
     * @param getListingFrom
     *            List Resources from elasticsearch or from fedora. Allowed
     *            values: "repo" and "es"
     * @return html listing of all objects
     */
    public String listAsHtml(String type, String namespace, int from,
	    int until, String getListingFrom) {

	List<String> list = list(type, namespace, from, until, getListingFrom);

	return representations.getAllOfTypeAsHtml(list, type, namespace, from,
		until, getListingFrom);
    }

    private List<String> listAllFromRepo(String type, String namespace,
	    int from, int until) {
	if (from >= until || from < 0 || until < 0)
	    throw new HttpArchiveException(416, "Can not process. From: "
		    + from + "Until: " + until + ".");

	List<String> result = new Vector<String>();
	String query = "* <" + REL_CONTENT_TYPE + "> \"" + type + "\"";
	InputStream in = fedora.findTriples(query, FedoraVocabulary.SPO,
		FedoraVocabulary.N3);
	List<String> list = RdfUtils.getFedoraSubject(in);
	if (from >= list.size()) {
	    return new Vector<String>();
	}
	if (until < list.size()) {
	    list = list.subList(from, until);
	} else {
	    list = list.subList(from, list.size());
	}
	if (namespace == null || namespace.isEmpty())
	    return list;
	for (String item : list) {
	    if (item.startsWith(namespace + ":"))
		result.add(item);
	}
	return result;
    }

    private List<String> listAllFromRepo(String namespace, int from, int until) {
	if (from >= until || from < 0 || until < 0)
	    throw new HttpArchiveException(416, "Can not process. From: "
		    + from + "Until: " + until + ".");

	List<String> result = new Vector<String>();
	String query = "* <" + REL_IS_NODE_TYPE + "> \"" + TYPE_OBJECT + "\"";
	InputStream in = fedora.findTriples(query, FedoraVocabulary.SPO,
		FedoraVocabulary.N3);
	List<String> list = RdfUtils.getFedoraSubject(in);
	if (from >= list.size()) {
	    return new Vector<String>();
	}
	if (until < list.size()) {
	    list = list.subList(from, until);
	} else {
	    list = list.subList(from, list.size());
	}
	if (namespace == null || namespace.isEmpty())
	    return list;
	for (String item : list) {
	    if (item.startsWith(namespace + ":"))
		result.add(item);
	}
	return result;
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
    public String replaceUrn(String pid, String namespace, String snid) {
	String subject = namespace + ":" + pid;
	String urn = services.generateUrn(subject, snid);
	String hasUrn = "http://geni-orca.renci.org/owl/topology.owl#hasURN";
	// String sameAs = "http://www.w3.org/2002/07/owl#sameAs";
	String metadata = readMetadata(subject);
	metadata = RdfUtils.replaceTriple(subject, hasUrn, urn, true, metadata);
	updateMetadata(namespace + ":" + pid, metadata);
	return "Update " + subject + " metadata " + metadata;
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
	if (RdfUtils.hasTriple(subject, hasUrn, urn, metadata))
	    throw new ArchiveException(subject + "already has a urn: "
		    + metadata);
	metadata = RdfUtils.addTriple(subject, hasUrn, urn, true, metadata);
	updateMetadata(namespace + ":" + pid, metadata);
	return "Update " + subject + " metadata " + metadata;
    }

    /**
     * @return the host to where the urns must point
     */
    public String getUrnbase() {
	return urnbase;
    }

    /**
     * @param p
     *            the id part of a pid
     * @param namespace
     *            the namespace part of a pid
     * @param transformerId
     *            the id of the transformer
     */
    public void addTransformer(String p, String namespace, String transformerId) {
	String pid = namespace + ":" + p;
	Node node = readNode(pid);
	node.addTransformer(new Transformer(transformerId));
	fedora.updateNode(node);
    }

}
