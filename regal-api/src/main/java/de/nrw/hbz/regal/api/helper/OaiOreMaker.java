package de.nrw.hbz.regal.api.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;
import java.util.List;

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

import de.nrw.hbz.regal.datatypes.Node;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("serial")
public class OaiOreMaker {

    final static Logger logger = LoggerFactory.getLogger(OaiOreMaker.class);
    String server = null;
    String uriPrefix = null;

    Node node = null;
    String dcNamespace = "http://purl.org/dc/elements/1.1/";
    String dctermsNamespace = "http://purl.org/dc/terms/";
    String foafNamespace = "http://xmlns.com/foaf/0.1/";
    String oreNamespace = "http://www.openarchives.org/ore/terms/";
    String rdfNamespace = " http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    String rdfsNamespace = "http://www.w3.org/2000/01/rdf-schema#";
    String regalNamespace = "http://hbz-nrw.de/regal#";

    RepositoryConnection con = null;

    @SuppressWarnings("javadoc")
    public OaiOreMaker(Node node, String server, String uriPrefix) {
	this.server = server;
	this.uriPrefix = uriPrefix;
	this.node = node;
	try {
	    con = createRdfRepository();
	} catch (RepositoryException e) {
	    throw new CreateRepositoryException(e);
	}
    }

    /**
     * @param format
     *            application/rdf+xml text/plain application/json
     * @param parents
     *            all parents of the pid
     * @param children
     *            all children of the pid
     * @return a oai_ore resource map
     */
    public String getReM(String format, List<String> parents,
	    List<String> children) {
	String result = null;
	addDescriptiveData();
	addStructuralData(parents, children);
	result = write(format);
	closeRdfRepository();
	return result;
    }

    private void addDescriptiveData() {
	try {
	    URL metadata = new URL(server + "/fedora/objects/" + node.getPID()
		    + "/datastreams/metadata/content");
	    InputStream in = metadata.openStream();
	    con.add(in, node.getPID(), RDFFormat.N3);
	} catch (Exception e) {
	    throw new AddDescriptiveDataException(e);
	}
    }

    private String write(String format) {
	try {
	    if (format.compareTo("text/html") == 0) {
		return getHtml();
	    }
	    StringWriter out = new StringWriter();
	    RDFWriter writer = null;
	    String result = null;
	    writer = configureWriter(format, out, writer);
	    return write(out, writer, result);
	} catch (Exception e) {
	    throw new WriteRdfException(e);
	}
    }

    private String write(StringWriter out, RDFWriter writer, String result)
	    throws RepositoryException {
	try {

	    writer.startRDF();
	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, false);

	    while (statements.hasNext()) {
		Statement statement = statements.next();
		writer.handleStatement(statement);
	    }
	    writer.endRDF();
	    result = out.toString();

	} catch (RDFHandlerException e) {
	    logger.error(e.getMessage());
	}

	return result;
    }

    private RDFWriter configureWriter(String format, StringWriter out,
	    RDFWriter writer) {
	if (format.equals("application/rdf+xml")) {
	    writer = Rio.createWriter(RDFFormat.RDFXML, out);
	} else if (format.compareTo("text/plain") == 0) {
	    writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
	} else if (format.compareTo("application/json") == 0) {
	    writer = Rio.createWriter(RDFFormat.JSONLD, out);
	    writer.getWriterConfig().set(JSONLDSettings.JSONLD_MODE,
		    JSONLDMode.EXPAND);
	    writer.getWriterConfig()
		    .set(BasicWriterSettings.PRETTY_PRINT, true);
	} else {
	    throw new HttpArchiveException(406, format + " is not supported");
	}
	return writer;
    }

    private String getHtml() throws RDFHandlerException, RepositoryException {
	StringWriter out = new StringWriter();
	RDFWriter writer = null;
	String result = null;
	writer = Rio.createWriter(RDFFormat.NTRIPLES, out);
	writer.startRDF();
	RepositoryResult<Statement> statements = con.getStatements(null, null,
		null, false);
	while (statements.hasNext()) {
	    Statement statement = statements.next();
	    writer.handleStatement(statement);
	}
	writer.endRDF();
	result = out.toString();
	return getHtml(result, node.getMimeType(), node.getPID());
    }

    private RepositoryConnection createRdfRepository()
	    throws RepositoryException {
	RepositoryConnection con = null;
	SailRepository myRepository = new SailRepository(new MemoryStore());
	myRepository.initialize();
	con = myRepository.getConnection();
	return con;
    }

    private void closeRdfRepository() {
	try {
	    if (con != null)
		con.close();
	} catch (Exception e) {
	    throw new CreateRepositoryException(e);
	}
    }

    private void addStructuralData(List<String> parents, List<String> children) {
	try {
	    String pid = node.getPID();
	    Date lastModified = node.getLastModified();
	    // Graph remGraph = new org.openrdf.model.impl.GraphImpl();
	    ValueFactory f = con.getValueFactory();

	    // Things
	    URI aggregation = f.createURI(/* uriPrefix + */pid);
	    URI rem = f.createURI(/* uriPrefix + */pid + ".rdf");
	    URI regal = f.createURI("https://github.com/edoweb/regal/");
	    URI data = f.createURI(aggregation.stringValue() + "/data");
	    URI fulltext = f.createURI(aggregation.stringValue() + "/fulltext");
	    Literal cType = f.createLiteral(node.getContentType());
	    Literal lastTimeModified = f.createLiteral(lastModified);
	    String mime = node.getMimeType();
	    String label = node.getFileLabel();

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
	    // rdfs
	    URI rdfsLabel = f.createURI(rdfsNamespace, "label");
	    // regal
	    URI contentType = f.createURI(regalNamespace, "contentType");
	    URI hasData = f.createURI(regalNamespace, "hasData");

	    // Statements

	    if (mime != null && !mime.isEmpty()) {
		Literal dataMime = f.createLiteral(mime);
		con.add(data, dcFormat, dataMime);
		con.add(aggregation, aggregates, data);
		con.add(aggregation, hasData, data);
		if (dataMime.toString().compareTo("application/pdf") == 0) {
		    con.add(aggregation, aggregates, fulltext);
		    con.add(data, dcHasFormat, fulltext);
		}

	    }

	    if (label != null && !label.isEmpty()) {
		Literal labelLiteral = f.createLiteral(label);
		con.add(data, rdfsLabel, labelLiteral);
	    }

	    String str = getOriginalUri(pid);
	    if (str != null && !str.isEmpty()) {
		URI originalObject = f.createURI(str);
		con.add(aggregation, similarTo, originalObject);

	    }

	    URI fedoraObject = f.createURI(server + "/fedora/objects/" + pid);

	    con.add(rem, describes, aggregation);
	    con.add(rem, modified, lastTimeModified);
	    con.add(rem, creator, regal);

	    con.add(aggregation, isDescribedBy, rem);

	    con.add(aggregation, similarTo, fedoraObject);
	    con.add(aggregation, contentType, cType);

	    for (String relPid : parents) {
		URI relUrl = f.createURI(relPid);
		con.add(aggregation, isAggregatedBy, relUrl);
		con.add(aggregation, isPartOf, relUrl);
	    }

	    for (String relPid : children) {
		URI relUrl = f.createURI(relPid);
		con.add(aggregation, aggregates, relUrl);
		con.add(aggregation, hasPart, relUrl);

	    }
	} catch (Exception e) {
	    throw new AddStructuralDataException(e);
	}
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
		    logoLink = "pdflogo.svg";
		} else if (mime.compareTo("application/zip") == 0) {
		    logoLink = "zip.png";
		} else {
		    logoLink = "data.png";
		}
		st.add("data", "<tr><td class=\"textlink\"><a	href=\""
			+ dataLink + "\"><img src=\"/img/" + logoLink
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
		objectLink = "/resource?type=" + object;
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

    private class CreateRepositoryException extends RuntimeException {
	public CreateRepositoryException(Throwable e) {
	    super(e);
	}
    }

    private class AddDescriptiveDataException extends RuntimeException {
	public AddDescriptiveDataException(Throwable e) {
	    super(e);
	}
    }

    private class AddStructuralDataException extends RuntimeException {
	public AddStructuralDataException(Throwable e) {
	    super(e);
	}
    }

    private class WriteRdfException extends RuntimeException {
	public WriteRdfException(Throwable e) {
	    super(e);
	}
    }

}
