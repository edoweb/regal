package de.nrw.hbz.regal.api.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.BasicWriterSettings;
import org.openrdf.rio.helpers.JSONLDMode;
import org.openrdf.rio.helpers.JSONLDSettings;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import de.nrw.hbz.regal.api.CreateObjectBean;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.fedora.FedoraInterface;

class Representations {
    final static Logger logger = LoggerFactory.getLogger(Representations.class);
    FedoraInterface fedora = null;
    String server = null;
    String uriPrefix = null;

    public Representations(FedoraInterface fedora, String server) {
	this.fedora = fedora;
	this.server = server;
	uriPrefix = server + "/" + "resource" + "/";

    }

    /**
     * @param pid
     *            the pid
     * @param format
     *            application/rdf+xml text/plain application/json
     * @param metadata
     *            where to read metadata from
     * @param lastModified
     *            when the object was last modified
     * @param fedoraHost
     *            the fedoraHost for externals
     * @param parents
     *            all parents of the pid
     * @param children
     *            all children of the pid
     * @return a oai_ore resource map
     */
    public String getReM(String pid, String format, URL metadata,
	    Date lastModified, String fedoraHost, List<String> parents,
	    List<String> children) {
	String result = null;
	Node node = fedora.readNode(pid);

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
	    Literal lastTimeModified = f.createLiteral(lastModified);
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

	    URI fedoraObject = f.createURI(fedoraHost + "/objects/" + pid);

	    con.add(rem, describes, aggregation);
	    con.add(rem, modified, lastTimeModified);
	    con.add(rem, creator, regal);

	    con.add(aggregation, isDescribedBy, rem);

	    con.add(aggregation, similarTo, fedoraObject);
	    con.add(aggregation, contentType, cType);

	    for (String relPid : parents) {
		URI relUrl = f.createURI(/* uriPrefix + */relPid);

		con.add(aggregation, isAggregatedBy, relUrl);
		con.add(aggregation, isPartOf, relUrl);
	    }

	    for (String relPid : children) {
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

    private View getExternalLinks(String pid) {
	View view = new View();
	Node node = fedora.readNode(pid);
	for (String id : node.getBean().getIdentifier()) {
	    if (id.startsWith("doi")) {
		view.addDoi(id);
	    } else if (id.startsWith("urn")) {
		view.addUrn(id);
		break;
	    } else if (id.startsWith("HT")) {
		view.addAlephId(id);
		break;
	    } else if (id.startsWith("TT")) {
		view.addAlephId(id);
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
	    st.add("serverRoot", server);

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

    /**
     * @param pid
     *            The pid of an existing object.
     * @param oaidc
     *            the oaidc data of the object
     * @param fedoraExtern
     *            the fedora entpoint url for external users
     * @return the view of the object
     */
    public View getView(String pid, String oaidc, String fedoraExtern) {

	Node node = fedora.readNode(pid);
	fedora.readDcToNode(node, new ByteArrayInputStream(oaidc.getBytes()),
		"oai_dc");
	return getView(node, fedoraExtern);

    }

    /**
     * @param node
     *            An object as node
     * @param type
     *            The type
     * @return the view of the object of type type.
     */
    View getView(Node node, String fedoraExtern) {

	String pid = node.getPID();
	String uri = pid;
	String apiUrl = server + "/resource/" + pid;

	View view = new View();
	view.setLastModified(node.getLastModified());
	view.setCreator(node.getBean().getCreator());
	view.setTitle(node.getBean().getTitle());
	view.setLanguage(node.getBean().getLanguage());
	view.setSubject(node.getBean().getSubject());
	view.setType(node.getBean().getType());
	view.setLocation(node.getBean().getSource());
	view.setPublisher(node.getBean().getPublisher());
	view.setDescription(node.getBean().getDescription());
	view.setContributer(node.getBean().getContributer());
	String label = node.getLabel();

	if (label != null && !label.isEmpty())
	    view.addDescription(label);
	view.setUri(uri);
	view.setApiUrl(apiUrl);
	view.setContentType(node.getContentType());

	String pidWithoutNamespace = pid.substring(pid.indexOf(':') + 1);

	view.addFedoraUrl(fedoraExtern + "/objects/" + pid);

	// TODO You know what to do!
	if (pid.contains("edoweb") || pid.contains("ellinet")) {
	    if (pid.length() <= 17) {
		view.addOriginalObjectUrl("http://klio.hbz-nrw.de:1801/webclient/MetadataManager?pid="
			+ pidWithoutNamespace);
		// TODO only if synced Resource
		view.addCacheUrl(server + "/" + node.getNamespace() + "base/"
			+ pidWithoutNamespace);

	    }
	}
	if (pid.contains("dipp")) {

	    // TODO only if synced Resource
	    view.addOriginalObjectUrl("http://193.30.112.23:9280/fedora/get/"
		    + pid + "/QDC");

	    try {
		view.addCacheUrl(server
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
	    view.addCacheUrl(server + "/" + node.getNamespace() + "base/"
		    + pidWithoutNamespace);

	}
	if (pid.contains("fhdd")) {

	    // TODO only if synced Resource
	    view.addOriginalObjectUrl("http://fhdd.opus.hbz-nrw.de/frontdoor.php?source_opus="
		    + pidWithoutNamespace + "&la=de");
	    view.addCacheUrl(server + "/" + node.getNamespace() + "base/"
		    + pidWithoutNamespace);

	}
	if (pid.contains("kola")) {

	    // TODO only if synced Resource
	    view.addOriginalObjectUrl("http://kola.opus.hbz-nrw.de/frontdoor.php?source_opus="
		    + pidWithoutNamespace + "&la=de");
	    view.addCacheUrl(server + "/" + node.getNamespace() + "base/"
		    + pidWithoutNamespace);

	}
	String query = "<info:fedora/" + pid + "> * *";
	try {
	    view.addRisearchUrl(fedoraExtern
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
	for (String date : node.getBean().getDate()) {
	    view.addYear(date.substring(0, 4));
	}
	for (String ddc : node.getBean().getSubject()) {
	    if (ddc.startsWith("ddc")) {
		view.addDdc(ddc);
		break;
	    }
	}

	for (String id : node.getBean().getIdentifier()) {
	    if (id.startsWith("doi")) {
		view.addDoi(id);

	    } else if (id.startsWith("urn")) {
		view.addUrn(id);
		break;
	    } else if (id.startsWith("HT")) {
		view.addAlephId(id);
		break;
	    } else if (id.startsWith("TT")) {
		view.addAlephId(id);
		break;
	    } else {
		view.addIdentifier(id);
	    }
	}
	return view;
    }

    /**
     * @param list
     *            a list with pids
     * @param type
     *            the type to be displaye
     * @return html listing of all objects
     */
    public String getAllOfTypeAsHtml(List<String> list, String type) {

	String result = "";
	try {
	    java.net.URL fileLocation = Thread.currentThread()
		    .getContextClassLoader().getResource("list.html");

	    StringWriter writer = new StringWriter();
	    IOUtils.copy(fileLocation.openStream(), writer);
	    String data = writer.toString();

	    ST st = new ST(data, '$', '$');
	    st.add("type", type);
	    for (String item : list) {
		st.add("items", "<li><a href=\"" + uriPrefix + item + "\">"
			+ item + "</a></li>");

	    }
	    result = st.render();
	} catch (IOException e) {
	    throw new HttpArchiveException(500, e);
	}

	return result;
    }

    /**
     * @param pid
     *            the pid to read from
     * @return the parentPid and contentType as json
     */
    public CreateObjectBean getRegalJson(String pid) {
	Node node = fedora.readNode(pid);
	CreateObjectBean result = new CreateObjectBean();
	String parentPid = null;
	String type = node.getContentType();
	parentPid = fedora.getNodeParent(node);
	result.setParentPid(parentPid);
	result.setType(type);
	return result;
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
}
