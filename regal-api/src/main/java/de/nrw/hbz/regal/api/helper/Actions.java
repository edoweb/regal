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
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.IS_MEMBER_OF;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.IS_PART_OF;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.ITEM_ID;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.culturegraph.mf.Flux;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.BasicWriterSettings;
import org.openrdf.rio.helpers.JSONLDMode;
import org.openrdf.rio.helpers.JSONLDSettings;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import de.nrw.hbz.regal.api.CreateObjectBean;
import de.nrw.hbz.regal.api.DCBeanAnnotated;
import de.nrw.hbz.regal.datatypes.ContentModel;
import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.exceptions.ArchiveException;
import de.nrw.hbz.regal.fedora.FedoraFactory;
import de.nrw.hbz.regal.fedora.FedoraInterface;
import de.nrw.hbz.regal.fedora.FedoraVocabulary;

/**
 * Actions provide a single class to access the archive. All endpoints are using
 * this class.
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class Actions {
    final static Logger logger = LoggerFactory.getLogger(Actions.class);
    private FedoraInterface fedora = null;
    private String fedoraExtern = null;
    private String serverName = null;
    private String uriPrefix = null;
    private String lobidUrl = null;
    private String verbundUrl = null;
    private String dataciteUrl;
    private String baseUrl;
    private String culturegraphUrl;

    // String namespace = null;

    /**
     * @throws IOException
     *             if properties can not be loaded.
     */
    public Actions() throws IOException {
	Properties properties = new Properties();
	properties.load(getClass().getResourceAsStream("/api.properties"));
	fedoraExtern = properties.getProperty("fedoraExtern");
	serverName = properties.getProperty("serverName");
	fedora = FedoraFactory.getFedoraImpl(
		properties.getProperty("fedoraIntern"),
		properties.getProperty("user"),
		properties.getProperty("password"));
	uriPrefix = serverName + "/" + "resource" + "/";

	properties.load(getClass().getResourceAsStream(
		"/externalLinks.properties"));
	lobidUrl = properties.getProperty("lobidUrl");
	verbundUrl = properties.getProperty("verbundUrl");
	dataciteUrl = properties.getProperty("dataciteUrl");
	baseUrl = properties.getProperty("baseUrl");
	culturegraphUrl = properties.getProperty("culturegraphUrl");

    }

    /**
     * @param pids
     *            The pids that must be deleted
     * @param wait
     *            If wait is true the method will wait few secs in order to get
     *            sync with the fedora triple store. TODO: Remove this ugly
     *            workaround.
     * @return A short message.
     */
    public String deleteAll(List<String> pids, boolean wait) {
	if (pids == null || pids.isEmpty())
	    throw new HttpArchiveException(304, "Nothing to delete!");
	logger.info("Delete All");
	StringBuffer msg = new StringBuffer();
	for (String pid : pids) {

	    try {
		msg.append(delete(pid, wait) + "\n");
	    } catch (Exception e) {
		logger.warn(pid + " " + e.getMessage());
	    }
	}

	return msg.toString();
    }

    // /**
    // * @param object
    // * A representation of this object will be created in the
    // * archive.
    // * @param wait
    // * If wait is true the method will wait few secs in order to get
    // * sync with the fedora triple store. TODO: Remove this ugly
    // * workaround.
    // * @return a short message
    // */
    // public String create(ComplexObject object, boolean wait) {
    // archive.createComplexObject(object);
    // if (wait)
    // waitWorkaround();
    //
    // return object.getRoot().getPID() + " successfully created!";
    // }

    /**
     * @param pid
     *            The pid that must be deleted
     * @param wait
     *            If wait is true the method will wait few secs in order to get
     *            sync with the fedora triple store. TODO: Remove this ugly
     *            workaround.
     * @return A short Message
     */
    public String delete(String pid, boolean wait) {

	String msg = "";
	fedora.deleteComplexObject(pid);

	try {
	    outdex(pid);
	} catch (Exception e) {
	    msg = e.getMessage();
	}
	if (wait)
	    waitWorkaround();

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
	Vector<String> pids = new Vector<String>();
	String query = "* <" + REL_CONTENT_TYPE + "> \"" + type + "\"";
	InputStream stream = fedora.findTriples(query, FedoraVocabulary.SPO,
		FedoraVocabulary.N3);
	String findpid = null;
	RepositoryConnection con = null;
	Repository myRepository = new SailRepository(new MemoryStore());
	try {
	    myRepository.initialize();
	    con = myRepository.getConnection();
	    String baseURI = "";

	    con.add(stream, baseURI, RDFFormat.N3);

	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, true);

	    while (statements.hasNext()) {
		Statement st = statements.next();
		findpid = st.getSubject().stringValue()
			.replace("info:fedora/", "");
		pids.add(findpid);
	    }
	} catch (RepositoryException e) {

	    e.printStackTrace();
	} catch (RDFParseException e) {

	    e.printStackTrace();
	} catch (IOException e) {

	    e.printStackTrace();
	} finally {
	    if (con != null) {
		try {
		    con.close();
		} catch (RepositoryException e) {
		    e.printStackTrace();
		}
	    }
	}
	return pids;
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
	Vector<String> findpids = new Vector<String>();
	RepositoryConnection con = null;
	Repository myRepository = new SailRepository(new MemoryStore());
	try {
	    myRepository.initialize();
	    con = myRepository.getConnection();
	    String baseURI = "";

	    con.add(stream, baseURI, RDFFormat.N3);

	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, true);

	    while (statements.hasNext()) {
		Statement st = statements.next();
		findpids.add(st.getObject().stringValue()
			.replace("info:fedora/", ""));

	    }
	} catch (RepositoryException e) {

	    e.printStackTrace();
	} catch (RDFParseException e) {

	    e.printStackTrace();
	} catch (IOException e) {

	    e.printStackTrace();
	} finally {
	    if (con != null) {
		try {
		    con.close();
		} catch (RepositoryException e) {
		    e.printStackTrace();
		}
	    }
	}
	return findpids;
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

	node = readNode(pid);

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

	Node node = readNode(pid);
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
	Node node = readNode(pid);
	InputStream is = null;
	if (node != null) {

	    try {
		URL url = new URL(fedoraExtern + "/objects/" + pid
			+ "/datastreams/metadata/content");

		URLConnection connection = url.openConnection();

		try {
		    is = connection.getInputStream();
		    result = IOUtils.toString(is);
		} catch (IOException ioe) {
		    if (connection instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			int statusCode = httpConn.getResponseCode();
			if (statusCode != 200) {
			    throw new HttpArchiveException(statusCode,
				    httpConn.getResponseMessage());
			} else {

			}
		    }
		}

	    } finally {
		if (is != null)
		    IOUtils.closeQuietly(is);
	    }

	}
	if (result == null || result.isEmpty()) {
	    throw new HttpArchiveException(404, "Datastream does not exist!");
	}
	return result;

    }

    // /**
    // * @param pid
    // * the pid that must be updated
    // * @param content
    // * the data as byte array
    // * @param mimeType
    // * the mimetype of the data
    // * @return A short message
    // * @throws IOException
    // * if data can not be written to a tmp file
    // */
    // String updateData(String pid, byte[] content, String mimeType, String
    // name)
    // throws IOException
    // {
    //
    // if (content == null || content.length == 0)
    // {
    // throw new ArchiveException(pid
    // + " you've tried to upload an empty byte array."
    // + " This action is not supported. Use HTTP DELETE instead.");
    // }
    // File tmp = File.createTempFile("Datafile", "tmp");
    // tmp.deleteOnExit();
    //
    // FileUtils.writeByteArrayToFile(tmp, content);
    // Node node = readNode(pid);
    // if (node != null)
    // {
    // node.setUploadData(tmp.getAbsolutePath(), "data", mimeType);
    // archive.updateNode(pid, node);
    // }
    //
    // return pid + " data successfully updated!";
    //
    // }

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
	    throw new ArchiveException(pid
		    + " you've tried to upload an empty stream."
		    + " This action is not supported. Use HTTP DELETE instead.");
	}
	File tmp = new File(name);
	tmp.deleteOnExit();

	// THIS DOESN'T WORK and will end in large Files
	// TODO find out what happens here
	// IOUtils.copy(content, new FileWriter(tmp));

	// go on with the classic method
	OutputStream out = null;
	try {

	    int read = 0;
	    byte[] bytes = new byte[1024];

	    out = new FileOutputStream(tmp);
	    while ((read = content.read(bytes)) != -1) {
		out.write(bytes, 0, read);
	    }

	} catch (IOException e) {

	    throw new IOException(e);
	} finally {
	    try {

		if (out != null)
		    out.close();
	    } catch (IOException e) {

	    }
	}

	Node node = readNode(pid);
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
	Node node = readNode(pid);
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
	    throw new ArchiveException(pid
		    + "You've tried to upload an empty string."
		    + " This action is not supported."
		    + " Use HTTP DELETE instead.");
	}
	File file = File.createTempFile("tmpmetadata", "tmp");
	file.deleteOnExit();
	FileUtils.writeStringToFile(file, content);
	Node node = readNode(pid);
	if (node != null) {
	    node.setMetadataFile(file.getAbsolutePath());
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

	Node node = readNode(pid);
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
     * @param pid
     *            the pid of a node that must be published on the oai interface
     * @return A short message.
     */
    public String makeOAISet(String pid) {

	Node node = readNode(pid);
	try {
	    URL metadata = new URL(fedoraExtern + "/objects/" + pid
		    + "/datastreams/metadata/content");
	    InputStream in = null;
	    OaiSetBuilder oaiSetBuilder = new OaiSetBuilder();
	    if (metadata != null) {

		try {
		    in = metadata.openStream();

		    RepositoryConnection con = null;
		    Repository myRepository = new SailRepository(
			    new MemoryStore());
		    try {
			myRepository.initialize();
			con = myRepository.getConnection();
			String baseURI = "";

			con.add(in, baseURI, RDFFormat.N3);

			RepositoryResult<Statement> statements = con
				.getStatements(null, null, null, true);

			while (statements.hasNext()) {
			    Statement st = statements.next();
			    String subject = st.getSubject().stringValue();
			    String predicate = st.getPredicate().stringValue();
			    String object = st.getObject().stringValue();

			    OaiSet set = oaiSetBuilder.getSet(subject,
				    predicate, object);
			    if (!this.nodeExists(set.getPid())) {
				createOAISet(set.getName(), set.getSpec(),
					set.getPid());
			    }
			    linkObjectToOaiSet(node, set.getSpec(),
				    set.getPid());

			}

		    } catch (RepositoryException e) {

			e.printStackTrace();
		    } catch (RDFParseException e) {

			e.printStackTrace();
		    } catch (IOException e) {

			e.printStackTrace();
		    } finally {
			if (con != null) {
			    try {
				con.close();
			    } catch (RepositoryException e) {
				e.printStackTrace();
			    }
			}
		    }
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		} finally {
		    IOUtils.closeQuietly(in);
		}
	    }

	    String name = "open_access";
	    String spec = "open_access";
	    String namespace = "oai";
	    String oaipid = namespace + ":" + "open_access";
	    if (!this.nodeExists(oaipid)) {
		createOAISet(name, spec, oaipid);
	    }
	    linkObjectToOaiSet(node, spec, oaipid);

	    return pid + " successfully created oai sets!";
	} catch (MalformedURLException e) {
	    throw new HttpArchiveException(500, e.getMessage());
	}
    }

    /**
     * @param pid
     *            The pid to remove from index
     * @return A short message
     */
    public String outdex(String pid) {

	String namespace = pid.substring(0, pid.indexOf(':'));
	ClientConfig cc = new DefaultClientConfig();
	cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
	cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
	Client c = Client.create(cc);
	String indexUrl = "http://localhost:9200/" + namespace + "/titel/"
		+ pid;
	try {

	    WebResource index = c.resource(indexUrl);
	    index.accept(MediaType.APPLICATION_JSON);

	    index.delete();
	} catch (Exception e) {
	    throw new ArchiveException(pid + " can't delete from index: "
		    + indexUrl, e);
	}
	return pid + " remove from index!";
    }

    /**
     * @param p
     *            The pid that must be indexed
     * @param namespace
     *            the namespace of the pid
     * @return a short message.
     */
    public String index(String p, String namespace) {
	String message = "";
	String viewAsString = "";
	String pid = namespace + ":" + p;
	// View view = getView(pid);

	ClientConfig cc = new DefaultClientConfig();
	cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
	cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
	Client c = Client.create(cc);
	try {

	    // TODO configure port and host
	    WebResource index = c.resource("http://localhost:9200/" + namespace
		    + "/titel/" + pid);
	    index.accept("application/json");
	    URL url = new URL("http://localhost/resource/" + pid + "/about");
	    URLConnection con = url.openConnection();
	    con.setRequestProperty("Accept", "application/json");
	    con.connect();
	    InputStream in = con.getInputStream();
	    StringWriter writer = new StringWriter();
	    IOUtils.copy(in, writer, "UTF-8");
	    viewAsString = writer.toString();
	    in.close();
	    message = index.put(String.class, viewAsString);
	} catch (Exception e) {
	    throw new ArchiveException("Error! " + message + e.getMessage(), e);
	}
	return "Success! " + message + "\n" + viewAsString;
    }

    /**
     * @return a list of all objects
     */
    public List<String> getAll() {
	Vector<String> pids = new Vector<String>();
	String query = "* <" + REL_IS_NODE_TYPE + "> \"" + TYPE_OBJECT + "\"";
	InputStream stream = fedora.findTriples(query, FedoraVocabulary.SPO,
		FedoraVocabulary.N3);
	String findpid = null;
	RepositoryConnection con = null;
	Repository myRepository = new SailRepository(new MemoryStore());
	try {
	    myRepository.initialize();
	    con = myRepository.getConnection();
	    String baseURI = "";

	    con.add(stream, baseURI, RDFFormat.N3);

	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, true);

	    while (statements.hasNext()) {
		Statement st = statements.next();
		findpid = st.getSubject().stringValue()
			.replace("info:fedora/", "");
		pids.add(findpid);
	    }
	} catch (RepositoryException e) {

	    e.printStackTrace();
	} catch (RDFParseException e) {

	    e.printStackTrace();
	} catch (IOException e) {

	    e.printStackTrace();
	} finally {
	    if (con != null) {
		try {
		    con.close();
		} catch (RepositoryException e) {
		    e.printStackTrace();
		}
	    }
	}
	return pids;
    }

    /**
     * @param pid
     *            The pid for which to load lobid rdf
     * @return a short message
     */
    public String lobidify(String pid) {

	Node node;

	node = readNode(pid);

	List<String> identifier = node.getIdentifier();
	String alephid = "";
	for (String id : identifier) {
	    if (id.startsWith("TT") || id.startsWith("HT")) {
		alephid = id;
		break;
	    }
	}
	if (alephid.isEmpty()) {
	    throw new ArchiveException(pid + " no Catalog-Id found");
	}

	String lobidUri = "http://lobid.org/resource/" + alephid;
	try {
	    URL lobidUrl = new URL(
		    "http://lobid.org/sparql/?query=describe+%3Chttp%3A%2F%2Flobid.org%2Fresource%2F"
			    + alephid + "%3E");

	    String str = readRdfToString(lobidUrl, RDFFormat.TURTLE,
		    RDFFormat.NTRIPLES, "text/plain");
	    str = Pattern.compile(lobidUri).matcher(str)
		    .replaceAll(Matcher.quoteReplacement(pid))
		    + "<"
		    + pid
		    + "> <http://www.umbel.org/specifications/vocabulary#isLike> <"
		    + lobidUri
		    + "> .\n"
		    + "<"
		    + pid
		    + "> <http://purl.org/lobid/lv#hbzID> \""
		    + alephid
		    + "\" .";

	    if (str.contains("http://www.w3.org/2002/07/owl#sameAs")) {
		str = includeSameAs(str, pid);
	    }
	    updateMetadata(pid, str);
	} catch (MalformedURLException e) {
	    throw new HttpArchiveException(500, e.getMessage());
	} catch (IOException e) {
	    throw new HttpArchiveException(500, e.getMessage());
	}

	return pid + " lobid metadata successfully loaded!";

    }

    private String readRdfToString(URL url, RDFFormat inf, RDFFormat outf,
	    String accept) {

	Graph myGraph = null;
	try {
	    myGraph = readRdfUrlToGraph(url, inf, accept);
	} catch (IOException e) {
	    throw new HttpArchiveException(500, e.getMessage());
	}

	StringWriter out = new StringWriter();
	RDFWriter writer = Rio.createWriter(outf, out);
	try {
	    writer.startRDF();
	    for (Statement st : myGraph) {
		writer.handleStatement(st);
	    }
	    writer.endRDF();
	} catch (RDFHandlerException e) {
	    throw new HttpArchiveException(500, e.getMessage());
	}
	return out.getBuffer().toString();

    }

    private Graph readRdfUrlToGraph(URL url, RDFFormat inf, String accept)
	    throws IOException {
	URLConnection con = url.openConnection();
	con.setRequestProperty("Accept", accept);
	con.connect();
	InputStream inputStream = con.getInputStream();
	return readRdfInputstreamToGraph(inputStream, inf, url.toString());
    }

    private Graph readRdfInputstreamToGraph(InputStream inputStream,
	    RDFFormat inf, String baseUrl) throws IOException {
	RDFParser rdfParser = Rio.createParser(inf);
	org.openrdf.model.Graph myGraph = new TreeModel();
	StatementCollector collector = new StatementCollector(myGraph);
	rdfParser.setRDFHandler(collector);
	try {
	    rdfParser.parse(inputStream, baseUrl);
	} catch (IOException e) {
	    throw new HttpArchiveException(500, e.getMessage());
	} catch (RDFParseException e) {
	    throw new HttpArchiveException(500, e.getMessage());
	} catch (RDFHandlerException e) {
	    throw new HttpArchiveException(500, e.getMessage());
	}

	return myGraph;
    }

    /**
     * @param pid
     *            The pid of an object
     * @return The metadata a oaidc-xml
     */
    public String oaidc(String pid) {

	Node node = readNode(pid);
	if (node == null)
	    return "No node with pid " + pid + " found";

	String metadata = "http://localhost/resource/" + pid + "/metadata";
	try {
	    File outfile = File.createTempFile("oaidc", "xml");
	    outfile.deleteOnExit();
	    File fluxFile = new File(Thread.currentThread()
		    .getContextClassLoader()
		    .getResource("morph-lobid-to-oaidc.flux").toURI());
	    Flux.main(new String[] { fluxFile.getAbsolutePath(),
		    "url=" + metadata, "out=" + outfile.getAbsolutePath() });
	    return FileUtils.readFileToString(outfile);
	} catch (IOException e) {
	    throw new ArchiveException(pid + " " + e.getMessage(), e);
	} catch (URISyntaxException e) {
	    throw new ArchiveException(pid + " " + e.getMessage(), e);
	} catch (RecognitionException e) {
	    throw new ArchiveException(pid + " " + e.getMessage(), e);
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
	String status = "urn_new";
	String result = "<epicur xmlns=\"urn:nbn:de:1111-2004033116\" xmlns:xsi=\"http://www.w3.com/2001/XMLSchema-instance\" xsi:schemaLocation=\"urn:nbn:de:1111-2004033116 http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd\">\n"
		+ "\t<administrative_data>\n"
		+ "\t\t<delivery>\n"
		+ "\t\t\t<update_status type=\""
		+ status
		+ "\"></update_status>\n"
		+ "\t\t\t<transfer type=\"oai\"></transfer>\n"
		+ "\t\t</delivery>\n"
		+ "\t</administrative_data>\n"
		+ "<record>\n"
		+ "\t<identifier scheme=\"urn:nbn:de\">"
		+ generateUrn(pid, namespace)
		+ "</identifier>\n"
		+ "\t<resource>\n"
		+ "\t\t<identifier origin=\"original\" role=\"primary\" scheme=\"url\" type=\"frontpage\">"
		+ uriPrefix
		+ ""
		+ namespace
		+ ":"
		+ pid
		+ "</identifier>\n"
		+ "\t\t<format scheme=\"imt\">text/html</format>\n"
		+ "\t</resource>" + "</record>\n" + "</epicur> ";
	return result;
    }

    /**
     * Generates a urn or returns an existing urn.
     * 
     * @param pid
     *            the pid of an object
     * @param namespace
     *            the namespace
     * @return the urn
     */
    public String generateUrn(String pid, String namespace) {
	try {
	    List<String> urns = getView(pid).getUrn();
	    if (urns != null && !urns.isEmpty()) {
		if (urns.size() > 1) {
		    logger.warn("Found multiple urns " + urns.size());
		} else {
		    return urns.get(0);
		}
	    }
	} catch (Exception e) {
	    logger.warn("You attempt to create an urn for non-existing object.");
	}

	String snid = namespace;
	String niss = pid;

	URN urn = new URN(snid, niss);
	return urn.toString();

    }

    /**
     * @param node
     *            the node with pdf data
     * @return the plain text content of the pdf
     */
    public String pdfbox(Node node) {
	String pid = node.getPID();

	String mimeType = node.getMimeType();
	if (mimeType == null)
	    throw new HttpArchiveException(
		    404,
		    "The node "
			    + pid
			    + " does not provide a mime type. It may not even contain data at all!");
	if (mimeType.compareTo("application/pdf") != 0)
	    throw new HttpArchiveException(406,
		    "Wrong mime type. Cannot extract text from " + mimeType);

	URL content = null;
	try {
	    content = new URL(fedoraExtern + "/objects/" + pid
		    + "/datastreams/data/content");

	    File pdfFile = download(content);
	    PdfText pdf = new PdfText();
	    return pdf.toString(pdfFile);
	} catch (MalformedURLException e) {
	    throw new HttpArchiveException(500, e.getMessage());
	} catch (IOException e) {
	    throw new HttpArchiveException(500, "Not able to download "
		    + content);

	}

    }

    /**
     * @param node
     *            the node with pdf data
     * @return the plain text content of the pdf
     */
    public String itext(Node node) {
	String pid = node.getPID();

	String mimeType = node.getMimeType();
	if (mimeType == null)
	    throw new HttpArchiveException(
		    404,
		    "The node "
			    + pid
			    + " does not provide a mime type. It may not even contain data at all!");
	if (mimeType.compareTo("application/pdf") != 0)
	    throw new HttpArchiveException(406,
		    "Wrong mime type. Cannot extract text from " + mimeType);

	URL content = null;
	try {
	    content = new URL(fedoraExtern + "/objects/" + pid
		    + "/datastreams/data/content");

	    File pdfFile = download(content);
	    PdfText pdf = new PdfText();
	    return pdf.itext(pdfFile);
	} catch (MalformedURLException e) {
	    throw new HttpArchiveException(500, e.getMessage());
	} catch (IOException e) {
	    throw new HttpArchiveException(500, "Not able to download "
		    + content);

	}

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
	    throw new HttpArchiveException(500, e.getMessage());
	}
    }

    /**
     * @param pid
     *            the pid
     * @return the node for the pid
     */
    public Node readNode(String pid) {
	try {
	    return fedora.readNode(pid);
	} catch (ArchiveException e) {
	    throw new HttpArchiveException(404, e.getMessage());
	}
    }

    /**
     * @param namespace
     *            the namespace will be deleted
     * @return a message
     */
    public String deleteNamespace(String namespace) {
	List<String> objects = fedora.findNodes(namespace + ":*");
	return deleteAll(objects, false);
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
	Node node = readNode(pid);
	return node.getLastModified();
    }

    /**
     * @param pid
     *            A pid
     * @return true if the pid exists and fals if not
     */
    public boolean nodeExists(String pid) {
	return fedora.nodeExists(pid);
    }

    /**
     * @param pid
     *            the pid
     * @param format
     *            application/rdf+xml text/plain application/json
     * @return a oai_ore resource map
     */
    public String getReM(String pid, String format) {
	String result = null;
	Node node = readNode(pid);

	@SuppressWarnings("unused")
	String dcNamespace = "http://purl.org/dc/elements/1.1/";
	String dctermsNamespace = "http://purl.org/dc/terms/";
	@SuppressWarnings("unused")
	String foafNamespace = "http://xmlns.com/foaf/0.1/";
	String oreNamespace = "http://www.openarchives.org/ore/terms/";
	@SuppressWarnings("unused")
	String rdfNamespace = " http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	@SuppressWarnings("unused")
	String rdfsNamespace = "http://www.w3.org/2000/01/rdf-schema#";
	String regalNamespace = "http://hbz-nrw.de/regal#";

	InputStream in = null;
	RepositoryConnection con = null;
	try {
	    SailRepository myRepository = new SailRepository(new MemoryStore());

	    myRepository.initialize();
	    con = myRepository.getConnection();
	    String baseURI = "";
	    try {
		URL metadata = new URL(fedoraExtern + "/objects/" + pid
			+ "/datastreams/metadata/content");
		in = metadata.openStream();
		con.add(in, baseURI, RDFFormat.N3);
	    } catch (Exception e) {
		logger.warn(e.getMessage());
	    }

	    // Graph remGraph = new org.openrdf.model.impl.GraphImpl();
	    ValueFactory f = myRepository.getValueFactory();
	    // Links
	    View view = getExternalLinks(pid);
	    // Things
	    URI aggregation = f.createURI(/* uriPrefix + */pid);
	    URI rem = f.createURI(/* uriPrefix + */pid + ".rdf");
	    URI regal = f.createURI("https://github.com/edoweb/regal/");
	    URI data = f.createURI(aggregation.stringValue() + "/data");
	    URI fulltext = f.createURI(aggregation.stringValue() + "/fulltext");
	    Literal cType = f.createLiteral(node.getContentType());
	    Literal lastTimeModified = f.createLiteral(getLastModified(pid));
	    String mime = node.getMimeType();

	    // Predicates
	    // ore
	    URI describes = f.createURI(oreNamespace, "describes");
	    URI isDescribedBy = f.createURI(oreNamespace, "isDescribedBy");
	    URI aggregates = f.createURI(oreNamespace, "aggregates");
	    URI isAggregatedBy = f.createURI(oreNamespace, "isAggregatedBy");
	    URI similarTo = f.createURI(oreNamespace, "similarTo");
	    // dc
	    URI isPartOf = f.createURI(dctermsNamespace, "isPartOf");
	    URI hasPart = f.createURI(dctermsNamespace, "hasPart");
	    URI modified = f.createURI(dctermsNamespace, "modified");
	    URI creator = f.createURI(dctermsNamespace, "creator");
	    URI dcFormat = f.createURI(dctermsNamespace, "format");
	    URI dcHasFormat = f.createURI(dctermsNamespace, "hasFormat");
	    // regal
	    URI contentType = f.createURI(regalNamespace, "contentType");

	    // Statements

	    if (mime != null && !mime.isEmpty()) {
		Literal dataMime = f.createLiteral(mime);
		con.add(data, dcFormat, dataMime);
		con.add(aggregation, aggregates, data);
		if (dataMime.toString().compareTo("application/pdf") == 0) {
		    con.add(aggregation, aggregates, fulltext);
		    con.add(data, dcHasFormat, fulltext);
		}

	    }

	    String str = getOriginalUri(pid);
	    if (str != null && !str.isEmpty()) {
		URI originalObject = f.createURI(str);
		con.add(aggregation, similarTo, originalObject);

	    }
	    str = view.getFirstLobidUrl();
	    if (str != null && !str.isEmpty()) {
		URI lobidResource = f.createURI(str);
		con.add(aggregation, similarTo, lobidResource);

	    }
	    str = view.getFirstVerbundUrl();
	    if (str != null && !str.isEmpty()) {
		URI catalogResource = f.createURI(str);
		con.add(aggregation, similarTo, catalogResource);

	    }
	    try {
		str = getCacheUri(pid);
		if (str != null && !str.isEmpty()) {
		    URI cacheResource = f.createURI(str);
		    con.add(aggregation, similarTo, cacheResource);
		}
	    } catch (UnsupportedEncodingException e) {

	    }
	    URI fedoraObject = f.createURI(this.fedoraExtern + "/objects/"
		    + pid);

	    con.add(rem, describes, aggregation);
	    con.add(rem, modified, lastTimeModified);
	    con.add(rem, creator, regal);

	    con.add(aggregation, isDescribedBy, rem);

	    con.add(aggregation, similarTo, fedoraObject);
	    con.add(aggregation, contentType, cType);

	    for (String relPid : findObject(pid, IS_PART_OF)) {
		URI relUrl = f.createURI(/* uriPrefix + */relPid);

		con.add(aggregation, isAggregatedBy, relUrl);
		con.add(aggregation, isPartOf, relUrl);
	    }

	    for (String relPid : findObject(pid, HAS_PART)) {
		URI relUrl = f.createURI(/* uriPrefix + */relPid);

		con.add(aggregation, aggregates, relUrl);
		con.add(aggregation, hasPart, relUrl);

	    }
	    StringWriter out = new StringWriter();

	    RDFWriter writer = null;
	    if (format.compareTo("application/rdf+xml") == 0) {
		writer = Rio.createWriter(RDFFormat.RDFXML, out);
	    } else if (format.compareTo("text/plain") == 0) {
		writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
	    } else if (format.compareTo("application/json") == 0) {
		writer = Rio.createWriter(RDFFormat.JSONLD, out);

		writer.getWriterConfig().set(JSONLDSettings.JSONLD_MODE,
			JSONLDMode.EXPAND);

		writer.getWriterConfig().set(BasicWriterSettings.PRETTY_PRINT,
			true);

	    } else if (format.compareTo("text/html") == 0) {
		// TODO: This will work one day
		// writer = Rio.createWriter(RDFFormat.RDFA, out);
		writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
		try {

		    writer.startRDF();

		    RepositoryResult<Statement> statements = con.getStatements(
			    null, null, null, false);

		    while (statements.hasNext()) {
			Statement statement = statements.next();

			writer.handleStatement(statement);
		    }
		    writer.endRDF();
		    result = out.toString();

		} catch (RDFHandlerException e) {
		    logger.error(e.getMessage());
		}
		return getHtml(result, mime, pid);

	    } else if (format.compareTo("application/json+elasticsearch") == 0) {
		writer = Rio.createWriter(RDFFormat.JSONLD, out);

		writer.getWriterConfig().set(JSONLDSettings.JSONLD_MODE,
			JSONLDMode.EXPAND);

		writer.getWriterConfig().set(BasicWriterSettings.PRETTY_PRINT,
			true);
		try {

		    writer.startRDF();

		    RepositoryResult<Statement> statements = con.getStatements(
			    null, null, null, false);

		    while (statements.hasNext()) {
			Statement statement = statements.next();
			if (statement.getSubject().stringValue().endsWith(pid))
			    writer.handleStatement(statement);
		    }
		    writer.endRDF();
		    result = out.toString();
		    result = result.substring(1, result.length() - 1);
		    return result;
		} catch (RDFHandlerException e) {
		    logger.error(e.getMessage());
		}

	    } else {
		throw new HttpArchiveException(406, format
			+ " is not supported");
	    }

	    try {

		writer.startRDF();
		RepositoryResult<Statement> statements = con.getStatements(
			null, null, null, false);

		while (statements.hasNext()) {
		    Statement statement = statements.next();
		    writer.handleStatement(statement);
		}
		writer.endRDF();
		result = out.toString();

	    } catch (RDFHandlerException e) {
		logger.error(e.getMessage());
	    }
	} catch (RepositoryException e) {

	    logger.error(e.getMessage());
	} finally {
	    if (con != null) {
		try {
		    con.close();
		} catch (RepositoryException e) {
		    logger.error(e.getMessage());
		    e.printStackTrace();
		}
	    }
	}
	return result;

    }

    private String getHtml(String rdf, String mime, String pid) {

	String result = "";
	RepositoryConnection con = null;
	try {
	    java.net.URL fileLocation = Thread.currentThread()
		    .getContextClassLoader().getResource("html.html");

	    StringWriter writer = new StringWriter();
	    IOUtils.copy(fileLocation.openStream(), writer);
	    String data = writer.toString();

	    ST st = new ST(data, '$', '$');
	    st.add("serverRoot", serverName);

	    if (mime != null) {
		String dataLink = uriPrefix + pid + "/data";
		String logoLink = "";
		if (mime.compareTo("application/pdf") == 0) {
		    logoLink = "/pdflogo.svg";
		} else if (mime.compareTo("application/zip") == 0) {
		    logoLink = "/zip.png";
		} else {
		    logoLink = "/data.png";
		}
		st.add("data", "<tr><td class=\"textlink\"><a	href=\""
			+ dataLink + "\"><img src=\"" + logoLink
			+ "\" width=\"100\" /></a></td></tr>");
	    } else {
		st.add("data", "");
	    }

	    SailRepository myRepository = new SailRepository(new MemoryStore());

	    myRepository.initialize();
	    con = myRepository.getConnection();
	    String baseURI = "";
	    try {
		con.add(new StringReader(rdf), baseURI, RDFFormat.N3);
		RepositoryResult<Statement> statements = con.getStatements(
			null, null, null, false);
		while (statements.hasNext()) {
		    Statement statement = statements.next();
		    String subject = statement.getSubject().stringValue();
		    String predicate = statement.getPredicate().stringValue();
		    String object = statement.getObject().stringValue();

		    MyTriple triple = new MyTriple(subject, predicate, object,
			    pid);

		    if (predicate.compareTo("http://purl.org/dc/terms/hasPart") == 0
			    || predicate
				    .compareTo("http://purl.org/dc/terms/isPartOf") == 0) {
			st.add("relations", triple);
		    } else if (predicate
			    .compareTo("http://www.openarchives.org/ore/terms/aggregates") == 0
			    || predicate
				    .compareTo("http://www.openarchives.org/ore/terms/isAggregatedBy") == 0)

		    {
			// do nothing!;
		    } else if (predicate
			    .compareTo("http://www.openarchives.org/ore/terms/similarTo") == 0) {
			st.add("links", triple);
		    } else {
			st.add("statements", triple);
		    }

		}
		result = st.render();
	    } catch (Exception e) {
		logger.warn(e.getMessage());
	    }

	} catch (RepositoryException e) {

	    logger.error(e.getMessage());
	}

	catch (IOException e) {
	    logger.error(e.getMessage());
	}

	finally {
	    if (con != null) {
		try {
		    con.close();
		} catch (RepositoryException e) {
		    logger.error(e.getMessage());
		    e.printStackTrace();
		}
	    }
	}
	return result;

    }

    private void linkObjectToOaiSet(Node node, String spec, String pid) {

	node.removeRelations(IS_MEMBER_OF);
	node.removeRelations(ITEM_ID);

	Link link = new Link();
	link.setPredicate(IS_MEMBER_OF);
	link.setObject("info:fedora/" + pid, false);
	node.addRelation(link);

	link = new Link();
	link.setPredicate(ITEM_ID);
	link.setObject(uriPrefix + node.getPID(), false);
	node.addRelation(link);

	fedora.updateNode(node);
    }

    private String getCacheUri(String pid) throws UnsupportedEncodingException {
	String cacheUri = null;
	String pidWithoutNamespace = pid.substring(pid.indexOf(':') + 1);
	String namespace = pid.substring(0, pid.indexOf(':'));

	if (pid.contains("edoweb") || pid.contains("ellinet")) {
	    if (pid.length() <= 17) {
		cacheUri = this.serverName + "/" + namespace + "base/"
			+ pidWithoutNamespace;

	    }
	}
	if (pid.contains("dipp")) {
	    cacheUri = this.serverName
		    + "/"
		    + namespace
		    + "base/"
		    + URLEncoder.encode(URLEncoder.encode(pid, "utf-8"),
			    "utf-8");

	}
	if (pid.contains("ubm")) {
	    cacheUri = this.serverName + "/" + namespace + "base/"
		    + pidWithoutNamespace;

	}
	if (pid.contains("fhdd")) {
	    cacheUri = this.serverName + "/" + namespace + "base/"
		    + pidWithoutNamespace;

	}
	if (pid.contains("kola")) {
	    cacheUri = this.serverName + "/" + namespace + "base/"
		    + pidWithoutNamespace;

	}
	return cacheUri;
    }

    private View getExternalLinks(String pid) {
	View view = new View();
	Node node = readNode(pid);
	for (String id : node.getIdentifier()) {
	    if (id.startsWith("doi")) {
		view.addDoi(id);
		view.addDataciteUrl(dataciteUrl + id);
		view.addBaseUrl(baseUrl + id);
	    } else if (id.startsWith("urn")) {
		view.addUrn(id);
		break;
	    } else if (id.startsWith("HT")) {
		view.addAlephId(id);
		view.addCulturegraphUrl(culturegraphUrl + id);
		view.addLobidUrl(lobidUrl + id);
		view.addVerbundUrl(verbundUrl + id);
		break;
	    } else if (id.startsWith("TT")) {
		view.addAlephId(id);
		view.addCulturegraphUrl(culturegraphUrl + id);
		view.addLobidUrl(lobidUrl + id);
		view.addVerbundUrl(verbundUrl + id);
		break;
	    } else {
		view.addIdentifier(id);
	    }
	}
	return view;
    }

    private String getOriginalUri(String pid) {
	String pidWithoutNamespace = pid.substring(pid.indexOf(':') + 1);
	String originalUri = null;
	if (pid.contains("edoweb") || pid.contains("ellinet")) {
	    if (pid.length() <= 17) {
		originalUri = "http://klio.hbz-nrw.de:1801/webclient/MetadataManager?pid="
			+ pidWithoutNamespace;

	    }
	}
	if (pid.contains("dipp")) {
	    originalUri = "http://193.30.112.23:9280/fedora/get/" + pid
		    + "/QDC";

	}
	if (pid.contains("ubm")) {
	    originalUri = "http://ubm.opus.hbz-nrw.de/frontdoor.php?source_opus="
		    + pidWithoutNamespace + "&la=de";

	}
	if (pid.contains("fhdd")) {
	    originalUri = "http://fhdd.opus.hbz-nrw.de/frontdoor.php?source_opus="
		    + pidWithoutNamespace + "&la=de";

	}
	if (pid.contains("kola")) {
	    originalUri = "http://kola.opus.hbz-nrw.de/frontdoor.php?source_opus="
		    + pidWithoutNamespace + "&la=de";

	}
	return originalUri;
    }

    private String includeSameAs(String str, String pid) {
	// <edoweb:4245081> <http://www.w3.org/2002/07/owl#sameAs>
	// <http://lobid.org/resource/ZDB2502002-X>
	System.out.println(pid + " include sameAs");
	URL url = null;
	try {
	    Repository myRepository = new SailRepository(new MemoryStore());
	    myRepository.initialize();

	    String baseURI = "http://example.org/example/local";

	    RepositoryConnection con = myRepository.getConnection();
	    try {
		con.add(new StringReader(str), baseURI, RDFFormat.NTRIPLES);

		String queryString = "SELECT x, y FROM {x} <http://www.w3.org/2002/07/owl#sameAs> {y}";

		TupleQuery tupleQuery = con.prepareTupleQuery(
			QueryLanguage.SERQL, queryString);
		TupleQueryResult result = tupleQuery.evaluate();
		try {
		    while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			Value valueOfY = bindingSet.getValue("y");
			System.out.println(pid + " is same as "
				+ valueOfY.stringValue());
			url = new URL(valueOfY.stringValue());

		    }
		} catch (MalformedURLException e) {
		    logger.warn("Not able to include sameAs data.");
		} finally {
		    result.close();
		}
	    } catch (IOException e) {
		logger.error(e.getMessage());
	    } finally {
		con.close();
	    }
	} catch (OpenRDFException e) {
	    logger.error(e.getMessage());
	}

	if (url == null) {
	    System.out.println("Not able to include sameAs data.");
	    logger.warn("Not able to include sameAs data.");
	    return str;
	}

	InputStream in = null;
	try {

	    URLConnection con = url.openConnection();
	    con.setRequestProperty("Accept", "text/plain");
	    con.connect();

	    in = con.getInputStream();
	    StringWriter writer = new StringWriter();
	    IOUtils.copy(in, writer, "UTF-8");
	    String str1 = writer.toString();

	    str1 = Pattern.compile(url.toString()).matcher(str1)
		    .replaceAll(Matcher.quoteReplacement(pid));
	    return str + "\n" + str1;

	} catch (IOException e) {
	    throw new ArchiveException(pid
		    + " IOException happens during copy operation.", e);
	} finally {
	    try {
		if (in != null)
		    in.close();
	    } catch (IOException e) {
		throw new ArchiveException(pid
			+ " wasn't able to close stream.", e);
	    }
	}
	// throw new ArchiveException("Not able to include sameAs data.");
    }

    private void createOAISet(String name, String spec, String pid) {
	String setSpecPred = "http://www.openarchives.org/OAI/2.0/setSpec";
	String setNamePred = "http://www.openarchives.org/OAI/2.0/setName";

	Link setSpecLink = new Link();
	setSpecLink.setPredicate(setSpecPred);

	Link setNameLink = new Link();
	setNameLink.setPredicate(setNamePred);

	String namespace = "oai";
	{
	    Node oaiset = new Node();
	    oaiset.setNamespace(namespace);
	    oaiset.setPID(pid);

	    setSpecLink.setObject(spec, true);
	    oaiset.addRelation(setSpecLink);

	    setNameLink.setObject(name, true);
	    oaiset.addRelation(setNameLink);
	    oaiset.addTitle(name);

	    fedora.createNode(oaiset);

	}
    }

    private void waitWorkaround() {
	/*
	 * Workaround START
	 */
	// try {
	// logger.info("Wait 10 sec! Nasty workaround.");
	// //Thread.sleep(10000);
	// logger.info("Stop Waiting! Nasty workaround.");
	// } catch (InterruptedException e1) {
	//
	// e1.printStackTrace();
	// }
	/*
	 * Workaround END
	 */
    }

    /**
     * @param pid
     *            The pid of an existing object.
     * @return the view of the object
     */
    public View getView(String pid) {

	Node node = readNode(pid);
	String oaidc = oaidc(pid);
	fedora.readDcToNode(node, new ByteArrayInputStream(oaidc.getBytes()),
		"oai_dc");
	return getView(node);

    }

    /**
     * @param node
     *            An object as node
     * @param type
     *            The type
     * @return the view of the object of type type.
     */
    View getView(Node node) {

	String pid = node.getPID();
	String uri = pid;
	String apiUrl = serverName + "/resource/" + pid;

	View view = new View();
	view.setLastModified(node.getLastModified());
	view.setCreator(node.getCreator());
	view.setTitle(node.getTitle());
	view.setLanguage(node.getLanguage());
	view.setSubject(node.getSubject());
	view.setType(node.getType());
	view.setLocation(node.getSource());
	view.setPublisher(node.getPublisher());
	view.setDescription(node.getDescription());
	view.setContributer(node.getContributer());
	String label = node.getLabel();

	if (label != null && !label.isEmpty())
	    view.addDescription(label);
	view.setUri(uri);
	view.setApiUrl(apiUrl);
	view.setContentType(node.getContentType());

	String pidWithoutNamespace = pid.substring(pid.indexOf(':') + 1);

	view.addFedoraUrl(this.fedoraExtern + "/objects/" + pid);

	// TODO You know what to do!
	if (pid.contains("edoweb") || pid.contains("ellinet")) {
	    if (pid.length() <= 17) {
		view.addOriginalObjectUrl("http://klio.hbz-nrw.de:1801/webclient/MetadataManager?pid="
			+ pidWithoutNamespace);
		// TODO only if synced Resource
		view.addCacheUrl(this.serverName + "/" + node.getNamespace()
			+ "base/" + pidWithoutNamespace);

	    }
	}
	if (pid.contains("dipp")) {

	    // TODO only if synced Resource
	    view.addOriginalObjectUrl("http://193.30.112.23:9280/fedora/get/"
		    + pid + "/QDC");

	    try {
		view.addCacheUrl(this.serverName
			+ "/"
			+ node.getNamespace()
			+ "base/"
			+ URLEncoder.encode(URLEncoder.encode(pid, "utf-8"),
				"utf-8"));
	    } catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	}
	if (pid.contains("ubm")) {

	    // TODO only if synced Resource
	    view.addOriginalObjectUrl("http://ubm.opus.hbz-nrw.de/frontdoor.php?source_opus="
		    + pidWithoutNamespace + "&la=de");
	    view.addCacheUrl(this.serverName + "/" + node.getNamespace()
		    + "base/" + pidWithoutNamespace);

	}
	if (pid.contains("fhdd")) {

	    // TODO only if synced Resource
	    view.addOriginalObjectUrl("http://fhdd.opus.hbz-nrw.de/frontdoor.php?source_opus="
		    + pidWithoutNamespace + "&la=de");
	    view.addCacheUrl(this.serverName + "/" + node.getNamespace()
		    + "base/" + pidWithoutNamespace);

	}
	if (pid.contains("kola")) {

	    // TODO only if synced Resource
	    view.addOriginalObjectUrl("http://kola.opus.hbz-nrw.de/frontdoor.php?source_opus="
		    + pidWithoutNamespace + "&la=de");
	    view.addCacheUrl(this.serverName + "/" + node.getNamespace()
		    + "base/" + pidWithoutNamespace);

	}
	String query = "<info:fedora/" + pid + "> * *";
	try {
	    view.addRisearchUrl(this.fedoraExtern
		    + "/risearch?type=triples&lang=spo&format=RDF/XML&query="
		    + URIUtil.encodeQuery(query));
	} catch (URIException e) {
	}

	String mime = node.getMimeType();

	if (mime != null && !mime.isEmpty()) {
	    if (mime.compareTo("application/pdf") == 0) {
		view.addPdfUrl(apiUrl + "/data");
	    }
	    if (mime.compareTo("application/zip") == 0) {
		view.addZipUrl(apiUrl + "/data");
	    }
	}
	for (String date : node.getDate()) {
	    view.addYear(date.substring(0, 4));
	}
	for (String ddc : node.getSubject()) {
	    if (ddc.startsWith("ddc")) {
		view.addDdc(ddc);
		break;
	    }
	}

	for (String id : node.getIdentifier()) {
	    if (id.startsWith("doi")) {
		view.addDoi(id);
		view.addDataciteUrl(dataciteUrl + id);
		view.addBaseUrl(baseUrl + id);
	    } else if (id.startsWith("urn")) {
		view.addUrn(id);
		break;
	    } else if (id.startsWith("HT")) {
		view.addAlephId(id);
		view.addCulturegraphUrl(culturegraphUrl + id);
		view.addLobidUrl(lobidUrl + id);
		view.addVerbundUrl(verbundUrl + id);
		break;
	    } else if (id.startsWith("TT")) {
		view.addAlephId(id);
		view.addCulturegraphUrl(culturegraphUrl + id);
		view.addLobidUrl(lobidUrl + id);
		view.addVerbundUrl(verbundUrl + id);
		break;
	    } else {
		view.addIdentifier(id);
	    }
	}

	for (String relPid : findObject(pid, IS_PART_OF)) {
	    String relUrl = serverName + "/resources/" + relPid;

	    view.addIsPartOf(relUrl, relPid);
	}

	for (String relPid : findObject(pid, HAS_PART)) {
	    String relUrl = serverName + "/resources/" + relPid;

	    List<String> desc = findObject(relPid,
		    "http://purl.org/dc/elements/1.1/description");

	    if (desc == null || desc.isEmpty()) {
		view.addHasPart(relUrl, relPid);
	    } else if (desc.size() == 1) {
		view.addHasPart(relUrl, desc.get(0));
	    } else {
		StringBuffer buf = new StringBuffer();
		for (String d : desc) {
		    buf.append(d + " ");
		}
		view.addHasPart(relUrl, buf.toString());
	    }

	}

	return view;
    }

    private File download(URL url) throws IOException {
	File file = null;
	InputStream in = null;
	FileOutputStream out = null;
	try {

	    file = File.createTempFile("tmp", "bin");
	    file.deleteOnExit();

	    URLConnection uc = url.openConnection();
	    uc.connect();
	    in = uc.getInputStream();
	    out = new FileOutputStream(file);

	    byte[] buffer = new byte[1024];
	    int bytesRead = -1;
	    while ((bytesRead = in.read(buffer)) > -1) {
		out.write(buffer, 0, bytesRead);
	    }

	} finally {

	    if (in != null)
		in.close();
	    if (out != null)
		out.close();

	}
	return file;
    }

    private class MyTriple {
	String subject;
	String predicate;
	String object;
	String pid;

	public MyTriple(String subject, String predicate, String object,
		String pid) {
	    this.subject = subject;
	    this.predicate = predicate;
	    this.object = object;
	    this.pid = pid;
	}

	public String toString() {
	    String subjectLink = null;
	    String objectLink = null;
	    String namespace = pid.substring(0, pid.indexOf(":"));
	    if (subject.startsWith(pid)) {
		subjectLink = uriPrefix + subject;
	    } else {
		subjectLink = subject;
	    }
	    if (object.startsWith(namespace)) {
		objectLink = uriPrefix + object;
	    } else if (object.startsWith("http")) {
		objectLink = object;
	    }
	    if (predicate.compareTo("http://hbz-nrw.de/regal#contentType") == 0) {
		objectLink = "/" + object + "/";
	    }
	    if (objectLink != null) {
		return "<tr><td><a href=\"" + subjectLink + "\">" + subject
			+ "</a></td><td><a href=\"" + predicate + "\">"
			+ predicate + "</a></td><td about=\"" + subject
			+ "\"><a property=\"" + predicate + "\" href=\""
			+ objectLink + "\">" + object + "</a></td></tr>";
	    } else {
		return "<tr><td><a href=\"" + subjectLink + "\">" + subject
			+ "</a></td><td><a href=\"" + predicate + "\">"
			+ predicate + "</a></td><td about=\"" + subject + "\">"
			+ object + "</td></tr>";
	    }
	}
    }

    /**
     * @return all objects in a html list
     */
    public String getAllAsHtml() {
	String result = "";
	try {
	    java.net.URL fileLocation = Thread.currentThread()
		    .getContextClassLoader().getResource("list.html");

	    StringWriter writer = new StringWriter();
	    IOUtils.copy(fileLocation.openStream(), writer);
	    String data = writer.toString();

	    ST st = new ST(data, '$', '$');
	    st.add("type", "resource");
	    List<String> list = getAll();
	    for (String item : list) {
		st.add("items", "<li><a href=\"" + uriPrefix + item + "\">"
			+ item + "</a></li>");
	    }
	    result = st.render();
	} catch (IOException e) {
	    throw new HttpArchiveException(500,
		    "IOException during html creation");
	}

	return result;
    }

    /**
     * @param type
     *            the type to be displaye
     * @return html listing of all objects
     */
    public String getAllOfTypeAsHtml(String type) {

	String result = "";
	try {
	    java.net.URL fileLocation = Thread.currentThread()
		    .getContextClassLoader().getResource("list.html");

	    StringWriter writer = new StringWriter();
	    IOUtils.copy(fileLocation.openStream(), writer);
	    String data = writer.toString();

	    ST st = new ST(data, '$', '$');
	    st.add("type", type);
	    List<String> list = findByType(type);
	    for (String item : list) {
		st.add("items", "<li><a href=\"" + uriPrefix + item + "\">"
			+ item + "</a></li>");

	    }
	    result = st.render();
	} catch (IOException e) {
	    throw new HttpArchiveException(500,
		    "IOException during html creation");
	}

	return result;
    }

    /**
     * @param pid
     *            the pid to read from
     * @return the parentPid and contentType as json
     */
    public CreateObjectBean getRegalJson(String pid) {
	Node node = readNode(pid);
	CreateObjectBean result = new CreateObjectBean();
	String parentPid = null;
	String type = node.getContentType();
	parentPid = fedora.getNodeParent(node);
	result.setParentPid(parentPid);
	result.setType(type);
	return result;
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

	try {
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

	} catch (ArchiveException e) {
	    e.printStackTrace();
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(),
		    e.getMessage());
	}
    }

}
