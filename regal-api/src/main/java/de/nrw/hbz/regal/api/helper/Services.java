package de.nrw.hbz.regal.api.helper;

import static de.nrw.hbz.regal.fedora.FedoraVocabulary.IS_MEMBER_OF;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.ITEM_ID;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.culturegraph.mf.Flux;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import de.nrw.hbz.regal.datatypes.DCBean;
import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.exceptions.ArchiveException;
import de.nrw.hbz.regal.fedora.CopyUtils;
import de.nrw.hbz.regal.fedora.FedoraInterface;
import de.nrw.hbz.regal.fedora.RdfUtils;

class Services {
    final static Logger logger = LoggerFactory.getLogger(Services.class);
    FedoraInterface fedora = null;
    String uriPrefix = null;

    public Services(FedoraInterface fedora, String server) {
	this.fedora = fedora;
	uriPrefix = server + "/" + "resource" + "/";
    }

    /**
     * @param node
     *            generate metadatafile with lobid data for this node
     * @return a short message
     */
    public Node lobidify(Node node) {

	String pid = node.getPID();

	List<String> identifier = node.getBean().getIdentifier();
	String alephid = "";
	for (String id : identifier) {
	    if (id.startsWith("TT") || id.startsWith("HT")) {
		alephid = id;
		break;
	    }
	}
	if (alephid.isEmpty()) {
	    throw new HttpArchiveException(204, pid + " no Catalog-Id found");
	}

	String lobidUri = "http://lobid.org/resource/" + alephid;
	try {
	    URL lobidUrl = new URL(
		    "http://lobid.org/sparql/?query=describe+%3Chttp%3A%2F%2Flobid.org%2Fresource%2F"
			    + alephid + "%3E");

	    String str = RdfUtils.readRdfToString(lobidUrl, RDFFormat.TURTLE,
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
		str = RdfUtils.includeSameAs(str, pid);
	    }
	    File metadataFile = CopyUtils.copyStringToFile(str);

	    node.setMetadataFile(metadataFile.getAbsolutePath());
	    return node;
	} catch (MalformedURLException e) {
	    throw new HttpArchiveException(500, e);
	} catch (IOException e) {
	    throw new HttpArchiveException(500, e);
	}

    }

    /**
     * @param pid
     *            the pid of the object
     * @param namespace
     *            the namespace
     * @param view
     *            a view object
     * @return a epicur display for the pid
     */
    public String epicur(String pid, String namespace, View view) {
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
		+ generateUrn(pid, namespace, view)
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
     * @param pid
     *            The pid of an object
     * @return The metadata a oaidc-xml
     */
    public String oaidc(String pid) {

	Node node = fedora.readNode(pid);
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
     * @param node
     *            the node with pdf data
     * @param fedoraExtern
     *            the fedora endpoint for external users
     * @return the plain text content of the pdf
     */
    public String pdfbox(Node node, String fedoraExtern) {
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

	    File pdfFile = CopyUtils.download(content);
	    PdfText pdf = new PdfText();
	    return pdf.toString(pdfFile);
	} catch (MalformedURLException e) {
	    throw new HttpArchiveException(500, e);
	} catch (IOException e) {
	    throw new HttpArchiveException(500, e);

	}

    }

    /**
     * @param pid
     *            the pid of a node that must be published on the oai interface
     * @param fedoraExtern
     *            the fedora endpoint for external users
     * @return A short message.
     */
    public String makeOAISet(String pid, String fedoraExtern) {

	Node node = fedora.readNode(pid);
	try {
	    URL metadata = new URL(fedoraExtern + "/objects/" + pid
		    + "/datastreams/metadata/content");

	    OaiSetBuilder oaiSetBuilder = new OaiSetBuilder();

	    RepositoryResult<Statement> statements = RdfUtils
		    .getStatements(metadata);

	    while (statements.hasNext()) {
		Statement st = statements.next();
		String subject = st.getSubject().stringValue();
		String predicate = st.getPredicate().stringValue();
		String object = st.getObject().stringValue();

		OaiSet set = oaiSetBuilder.getSet(subject, predicate, object);
		if (!fedora.nodeExists(set.getPid())) {
		    createOAISet(set.getName(), set.getSpec(), set.getPid());
		}
		linkObjectToOaiSet(node, set.getSpec(), set.getPid());
	    }
	    String name = "open_access";
	    String spec = "open_access";
	    String namespace = "oai";
	    String oaipid = namespace + ":" + "open_access";
	    if (!fedora.nodeExists(oaipid)) {
		createOAISet(name, spec, oaipid);
	    }
	    linkObjectToOaiSet(node, spec, oaipid);

	    return pid + " successfully created oai sets!";

	} catch (Exception e) {
	    throw new HttpArchiveException(500, e);
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
     * Generates a urn or returns an existing urn.
     * 
     * @param pid
     *            the pid of an object
     * @param namespace
     *            the namespace
     * @param view
     *            a view of the pid
     * @return the urn
     */
    public String generateUrn(String pid, String namespace, View view) {
	try {
	    List<String> urns = view.getUrn();
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
     * @param fedoraExtern
     *            the fedora endpoint for external users
     * @return the plain text content of the pdf
     */
    public String itext(Node node, String fedoraExtern) {
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

	    File pdfFile = CopyUtils.download(content);
	    PdfText pdf = new PdfText();
	    return pdf.itext(pdfFile);
	} catch (MalformedURLException e) {
	    throw new HttpArchiveException(500, e);
	} catch (IOException e) {
	    throw new HttpArchiveException(500, e);

	}

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

	    DCBean dc = oaiset.getBean();
	    dc.addTitle(name);

	    oaiset.setDcBean(dc);

	    fedora.createNode(oaiset);

	}
    }
}
