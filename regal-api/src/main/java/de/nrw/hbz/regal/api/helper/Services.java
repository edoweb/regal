package de.nrw.hbz.regal.api.helper;

import static de.nrw.hbz.regal.fedora.FedoraVocabulary.IS_MEMBER_OF;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.ITEM_ID;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.FileUtils;
import org.culturegraph.mf.Flux;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import de.nrw.hbz.regal.datatypes.DCBean;
import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.exceptions.ArchiveException;
import de.nrw.hbz.regal.fedora.CopyUtils;
import de.nrw.hbz.regal.fedora.FedoraInterface;
import de.nrw.hbz.regal.fedora.RdfUtils;

class Services {

    @SuppressWarnings("serial")
    public class MetadataNotFoundException extends RuntimeException {

	public MetadataNotFoundException() {
	    // TODO Auto-generated constructor stub
	}

	public MetadataNotFoundException(String arg0) {
	    super(arg0);
	    // TODO Auto-generated constructor stub
	}

	public MetadataNotFoundException(Throwable arg0) {
	    super(arg0);
	    // TODO Auto-generated constructor stub
	}

	public MetadataNotFoundException(String arg0, Throwable arg1) {
	    super(arg0, arg1);
	    // TODO Auto-generated constructor stub
	}

    }

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
	    throw new HttpArchiveException(500, pid + " no Catalog-Id found");
	}

	String lobidUri = "http://lobid.org/resource/" + alephid;
	try {
	    URL lobidUrl = new URL("http://api.lobid.org/resource?id="
		    + alephid);

	    String str = RdfUtils.readRdfToString(lobidUrl, RDFFormat.NTRIPLES,
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
		str = RdfUtils.followSameAsAndInclude(lobidUrl, pid);
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
     * @param url
     *            the url the urn must point to
     * @param urn
     *            the urn
     * 
     * @return a epicur display for the pid
     */
    public String epicur(String url, String urn) {
	String status = "urn_new";
	String result = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<epicur xmlns=\"urn:nbn:de:1111-2004033116\" xsi:schemaLocation=\"urn:nbn:de:1111-2004033116 http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd\">\n"
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
		+ urn
		+ "</identifier>\n"
		+ "\t<resource>\n"
		+ "\t\t<identifier origin=\"original\" role=\"primary\" scheme=\"url\" type=\"frontpage\">"
		+ url
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

	String metadata = this.uriPrefix + pid + "/metadata";
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
		if (set == null) {
		    continue;
		}
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
	    throw new MetadataNotFoundException(e);
	}
    }

    /**
     * Generates a urn
     * 
     * @param niss
     *            usually the pid of an object
     * @param snid
     *            usually the namespace
     * @return the urn
     */
    public String generateUrn(String niss, String snid) {
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

    public String pdfa(Node node, String fedoraExtern) {
	String redirectUrl = null;
	try {
	    URL pdfaConverter = new URL(
		    "http://nyx.hbz-nrw.de/pdfa/api/convertFromUrl?inputFile="
			    + fedoraExtern + "/objects/" + node.getPID()
			    + "/datastreams/data/content");

	    HttpURLConnection connection = (HttpURLConnection) pdfaConverter
		    .openConnection();
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("Accept", "application/xml");
	    Element root = XmlUtils.getDocument(connection.getInputStream());
	    List<Element> elements = XmlUtils.getElements("//resultFileUrl",
		    root, null);
	    if (elements.size() != 1) {
		throw new ArchiveException(
			"PDFa conversion returns wrong numbers of resultFileUrls: "
				+ elements.size());
	    }
	    redirectUrl = elements.get(0).getTextContent();

	    return redirectUrl;

	} catch (MalformedURLException e) {
	    throw new HttpArchiveException(500, e);
	} catch (IOException e) {
	    throw new HttpArchiveException(500, e);
	}

    }
}
