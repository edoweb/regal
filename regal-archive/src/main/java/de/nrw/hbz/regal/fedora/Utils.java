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
package de.nrw.hbz.regal.fedora;

import static de.nrw.hbz.regal.datatypes.Vocabulary.REL_CONTENT_TYPE;
import static de.nrw.hbz.regal.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.CM_CONTENTMODEL;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.DS_COMPOSITE_MODEL;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.DS_COMPOSITE_MODEL_URI;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.DS_INPUTSPEC;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.DS_INPUTSPEC_URI;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.DS_METHODMAP;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.DS_METHODMAP_URI;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.DS_METHODMAP_WSDL;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.DS_METHODMAP_WSDL_URI;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.DS_WSDL;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.DS_WSDL_URI;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.INFO_NAMESPACE;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.REL_HAS_MODEL;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.REL_HAS_SERVICE;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.REL_IS_CONTRACTOR_OF;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.REL_IS_DEPLOYMENT_OF;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.SDEF_CONTENTMODEL;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.SDEP_CONTENTMODEL;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.AddRelationship;
import com.yourmediashelf.fedora.client.request.FindObjects;
import com.yourmediashelf.fedora.client.request.GetDatastreamDissemination;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.ListDatastreams;
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.request.PurgeRelationship;
import com.yourmediashelf.fedora.client.request.Upload;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;
import com.yourmediashelf.fedora.client.response.ListDatastreamsResponse;
import com.yourmediashelf.fedora.client.response.UploadResponse;
import com.yourmediashelf.fedora.generated.access.DatastreamType;

import de.nrw.hbz.regal.datatypes.ContentModel;
import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.exceptions.ArchiveException;

/**
 * The utils class provides commonly used "low-level-methods" to the
 * FedoraFacade.
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class Utils {

    @SuppressWarnings({ "javadoc", "serial" })
    public class NoPidFoundException extends RuntimeException {

	public NoPidFoundException() {
	}

	public NoPidFoundException(String arg0) {
	    super(arg0);
	}

	public NoPidFoundException(Throwable arg0) {
	    super(arg0);
	}

	public NoPidFoundException(String arg0, Throwable arg1) {
	    super(arg0, arg1);
	}

    }

    ContentModelBuilder cmBuilder = new ContentModelBuilder();
    private String user = null;

    /**
     * @param host
     *            The fedora host.
     * @param user
     *            A valid fedora user.
     */
    public Utils(String host, String user) {
	this.user = user;
    }

    /**
     * Prefixes a pid with FEDORA_INFO_NAMESPACE
     * 
     * @param pid
     *            The pid that must be prefixed
     * @return FEDORA_INFO_NAMESPACE + pid
     */
    public String addUriPrefix(final String pid) {

	if (pid.contains(INFO_NAMESPACE.toString()))
	    return pid;
	String pred = INFO_NAMESPACE.toString() + pid;
	return pred;
    }

    void purgeRelationships(String pid, List<Link> list) {

	for (Link link : list) {
	    System.out.println("PURGE: " + addUriPrefix(pid) + " <"
		    + link.getPredicate() + "> " + link.getObject());
	    try {
		new PurgeRelationship(pid).subject(addUriPrefix(pid))
			.predicate(link.getPredicate())
			.object(link.getObject(), link.isLiteral()).execute();
	    } catch (FedoraClientException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    void addRelationships(String pid, List<Link> links) {

	if (links != null)
	    for (Link link : links) {

		try {
		    new AddRelationship(pid).predicate(link.getPredicate())
			    .object(link.getObject(), link.isLiteral())
			    .execute();
		} catch (Exception e) {
		    try {
			new AddRelationship(pid).predicate(link.getPredicate())
				.object(link.getObject(), true).execute();
		    } catch (Exception e2) {

		    }

		}

	    }

    }

    /**
     * @param pid
     *            The pid of the fedora object.
     * @param datastreamId
     *            The datastream ID of the datastream
     * @return true if the datastream exists, false if not.
     */
    public boolean dataStreamExists(String pid, String datastreamId) {
	try {

	    ListDatastreamsResponse response = new ListDatastreams(pid)
		    .execute();

	    for (DatastreamType ds : response.getDatastreams()) {
		if (ds.getDsid().compareTo(datastreamId) == 0)
		    return true;
	    }

	} catch (FedoraClientException e) {
	    return false;
	}
	return false;
    }

    String removeUriPrefix(String pred) {
	String pid = pred.replace(INFO_NAMESPACE, "");

	return pid;
    }

    void createRelsExt(Node node) {

	String pid = node.getPID();

	// IF DATASTREAM ! EXISTS
	// CREATE DATASTREAM
	// ADD RELATIONS

	if (!dataStreamExists(pid, "RELS-EXT")) {
	    System.out.println("PID " + pid + " doesn't exist, create new");
	    createFedoraXmlForRelsExt(pid);

	}

	List<Link> links = node.getRelsExt();
	createRelsExt(pid, links);

    }

    boolean nodeExists(String pid) {
	try {

	    FindObjectsResponse response = new FindObjects().terms(pid).pid()
		    .execute();
	    for (String p : response.getPids()) {
		if (p.compareTo(pid) == 0)
		    return true;
	    }

	} catch (Exception e) {
	    return false;
	}
	return false;
    }

    /**
     * Description: Allows to ingest a local file as managed datastream of the
     * object </p>
     * 
     * @param pid
     *            of the object
     * @param datastreamID
     *            to identify the datastream
     * @param fileLocation
     *            to specify the managed content of the datastream
     * @param mimeType
     *            of the uploaded file
     */
    void createManagedStream(Node node) {

	try {

	    File file = new File(node.getUploadFile());
	    UploadResponse response = new Upload(file).execute();

	    String location = response.getUploadLocation();
	    String label = node.getFileLabel();
	    if (label == null || label.isEmpty())
		label = file.getName();
	    new AddDatastream(node.getPID(), "data").versionable(true)
		    .dsLabel(label).dsState("A").controlGroup("M")
		    .mimeType(node.getMimeType()).dsLocation(location)
		    .execute();

	} catch (Exception e) {
	    throw new ArchiveException(node.getPID()
		    + " an unknown exception occured.", e);
	}
    }

    void createMetadataStream(Node node) {

	try {

	    Upload request = new Upload(new File(node.getMetadataFile()));
	    UploadResponse response = request.execute();
	    String location = response.getUploadLocation();

	    new AddDatastream(node.getPID(), "metadata").versionable(true)
		    .dsState("A").dsLabel("n-triple rdf metadata")
		    .controlGroup("M").mimeType(node.getMimeType())
		    .dsLocation(location).execute();
	} catch (Exception e) {
	    throw new ArchiveException(node.getPID()
		    + " an unknown exception occured.", e);
	}
    }

    void updateManagedStream(Node node) {

	try {
	    File file = new File(node.getUploadFile());
	    Upload request = new Upload(file);
	    UploadResponse response = request.execute();
	    String location = response.getUploadLocation();

	    if (dataStreamExists(node.getPID(), "data")) {
		new ModifyDatastream(node.getPID(), "data").versionable(true)
			.dsState("A").dsLabel(file.getName()).controlGroup("M")
			.mimeType(node.getMimeType()).dsLocation(location)
			.execute();
	    } else {
		new AddDatastream(node.getPID(), "data").versionable(true)
			.dsState("A").dsLabel(node.getFileLabel())
			.mimeType(node.getMimeType()).dsLocation(location)
			.controlGroup("M").execute();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new ArchiveException(node.getPID()
		    + " an unknown exception occured.", e);
	}
    }

    void updateMetadataStream(Node node) {
	try {

	    Upload request = new Upload(new File(node.getMetadataFile()));
	    UploadResponse response = request.execute();
	    String location = response.getUploadLocation();

	    if (dataStreamExists(node.getPID(), "metadata")) {
		new ModifyDatastream(node.getPID(), "metadata")
			.versionable(true).dsLabel("n-triple rdf metadata")
			.dsState("A").controlGroup("M").mimeType("text/plain")
			.dsLocation(location).execute();
	    } else {
		new AddDatastream(node.getPID(), "metadata").versionable(true)
			.dsState("A").dsLabel("n-triple rdf metadata")
			.controlGroup("M").mimeType("text/plain")
			.dsLocation(location).execute();
	    }
	} catch (Exception e) {
	    throw new ArchiveException(node.getPID()
		    + " an unknown exception occured.", e);
	}
    }

    void readRelsExt(Node node) throws FedoraClientException {

	try {
	    FedoraResponse response = new GetDatastreamDissemination(
		    node.getPID(), "RELS-EXT").download(true).execute();
	    InputStream ds = response.getEntityInputStream();

	    Repository myRepository = new SailRepository(new MemoryStore());
	    myRepository.initialize();

	    RepositoryConnection con = myRepository.getConnection();
	    String baseURI = "";

	    try {
		ValueFactory f = myRepository.getValueFactory();
		URI objectId = f.createURI("info:fedora/" + node.getPID());
		con.add(new BufferedInputStream(ds), baseURI, RDFFormat.RDFXML);
		RepositoryResult<Statement> statements = con.getStatements(
			objectId, null, null, true);

		try {
		    while (statements.hasNext()) {
			Statement st = statements.next();

			URI predUri = st.getPredicate();
			Value objUri = st.getObject();

			Link link = new Link();
			link.setObject(objUri.stringValue(), false);
			link.setPredicate(predUri.stringValue());

			if (link.getPredicate().compareTo(REL_IS_NODE_TYPE) == 0) {
			    node.setType(link.getObject());
			} else if (link.getPredicate().compareTo(
				REL_CONTENT_TYPE) == 0) {
			    node.setContentType(link.getObject());
			}

			String object = link.getObject();
			try {
			    if (object == null)
				throw new URISyntaxException(" ", "Is a Null",
					0);
			    if (object.isEmpty())
				throw new URISyntaxException(" ",
					"Is an Empty String", 0);
			    if (!object.contains(":") && !object.contains("/"))
				throw new URISyntaxException(object,
					"Contains no namespace and no Slash", 0);

			    new java.net.URI(object);

			    link.setLiteral(false);
			} catch (URISyntaxException e) {
			}

			node.addRelation(link);

		    }
		} catch (Exception e) {
		    throw new ArchiveException(node.getPID()
			    + " an unknown exception occured.", e);
		}

		finally {
		    statements.close(); // make sure the result object is closed
					// properly
		}

	    } finally {
		con.close();
	    }

	} catch (RepositoryException e) {

	    throw new ArchiveException(node.getPID()
		    + " an unknown exception occured.", e);
	} catch (RemoteException e) {

	    throw new ArchiveException(node.getPID()
		    + " an unknown exception occured.", e);
	} catch (RDFParseException e) {

	    throw new ArchiveException(node.getPID()
		    + " an unknown exception occured.", e);
	} catch (IOException e) {

	    throw new ArchiveException(node.getPID()
		    + " an unknown exception occured.", e);
	}

    }

    /**
     * Creates new Rels-Ext datastream in object 'pid' </p>
     * 
     * @param pid
     *            of the object
     * 
     */
    void createFedoraXmlForRelsExt(String pid) {
	try {

	    String initialContent = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rel=\"info:fedora/fedora-system:def/relations-external#\">"
		    + "    <rdf:Description rdf:about=\"info:fedora/"
		    + pid
		    + "\">" + "    </rdf:Description>" + "</rdf:RDF>";

	    new AddDatastream(pid, "RELS-EXT").mimeType("application/rdf+xml")
		    .formatURI("info:fedora/fedora-system:FedoraRELSExt-1.0")
		    .versionable(true).content(initialContent).execute();

	} catch (Exception e) {
	    throw new ArchiveException(e.getMessage(), e);
	}
    }

    void updateFedoraXmlForRelsExt(String pid, List<Link> statements) {
	// System.out.println("Create new REL-EXT "+pid);
	String initialContent = null;
	try {

	    initialContent = RdfUtils.getFedoraRelsExt(pid, statements);

	    new ModifyDatastream(pid, "RELS-EXT")
		    .mimeType("application/rdf+xml")
		    .formatURI("info:fedora/fedora-system:FedoraRELSExt-1.0")
		    .versionable(true).content(initialContent).execute();

	} catch (Exception e) {
	    throw new ArchiveException(initialContent, e);
	}
    }

    List<String> findPidsSimple(String rdfQuery) {

	try {
	    FindObjectsResponse response = new FindObjects().maxResults(50)
		    .resultFormat("xml").pid().terms(rdfQuery).execute();
	    if (!response.hasNext())
		return response.getPids();
	    List<String> result = response.getPids();
	    while (response.hasNext()) {

		response = new FindObjects().pid()
			.sessionToken(response.getToken()).maxResults(50)
			.resultFormat("xml").execute();
		result.addAll(response.getPids());

	    }

	    return result;
	} catch (FedoraClientException e) {
	    throw new NoPidFoundException(rdfQuery, e);
	}

    }

    String setOwnerToXMLString(String objXML) {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	try {
	    DocumentBuilder docBuilder = factory.newDocumentBuilder();
	    Document doc = docBuilder.parse(new BufferedInputStream(
		    new ByteArrayInputStream(objXML.getBytes())));
	    Element root = doc.getDocumentElement();
	    root.normalize();

	    NodeList properties = root.getElementsByTagName("foxml:property");
	    for (int i = 0; i < properties.getLength(); i++) {
		Element n = (Element) properties.item(i);
		String attribute = n.getAttribute("NAME");

		if (attribute
			.compareTo("info:fedora/fedora-system:def/model#ownerId") == 0) {

		    n.setAttribute("VALUE", user);

		    break;
		}
	    }

	    try {
		doc.normalize();
		Source source = new DOMSource(doc);
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);

		TransformerFactory fac = TransformerFactory.newInstance();
		Transformer transformer = fac.newTransformer();
		transformer.transform(source, result);

		return stringWriter.toString();

	    } catch (TransformerConfigurationException e) {
		e.printStackTrace();
	    } catch (TransformerException e) {
		e.printStackTrace();
	    }

	} catch (ParserConfigurationException e) {

	    e.printStackTrace();
	} catch (SAXException e) {

	    e.printStackTrace();
	} catch (IOException e) {

	    e.printStackTrace();
	}

	return null;
    }

    private void createRelsExt(String pid, List<Link> links) {
	if (links != null)
	    for (Link curHBZLink : links) {
		if (curHBZLink == null)
		    return;
		try {
		    if (curHBZLink.isLiteral()) {

			new AddRelationship(pid)
				.predicate(curHBZLink.getPredicate())
				.object(curHBZLink.getObject(),
					curHBZLink.isLiteral()).execute();
		    } else {

			new AddRelationship(pid)
				.predicate(curHBZLink.getPredicate())
				.object(addUriPrefix(curHBZLink.getObject()),
					curHBZLink.isLiteral()).execute();

		    }
		} catch (Exception e) {
		}
	    }
    }

    void updateRelsExt(Node node) {
	String pid = node.getPID();
	String type = node.getContentType();

	if (!dataStreamExists(pid, "RELS-EXT")) {
	    createFedoraXmlForRelsExt(pid);
	}

	Link link = new Link();
	link.setPredicate(REL_CONTENT_TYPE);
	link.setObject(type, true);
	node.addRelation(link);

	link = new Link();
	link.setObject(node.getNodeType(), true);
	link.setPredicate(REL_IS_NODE_TYPE);
	node.addRelation(link);

	updateFedoraXmlForRelsExt(pid, node.getRelsExt());
    }

    void createContentModels(Node node) {
	List<ContentModel> models = node.getContentModels();
	if (models == null)
	    return;
	for (ContentModel m : models) {
	    createContentModel(m, node);
	}
    }

    void createContentModel(ContentModel hbzNodeContentModel, Node node) {

	try {
	    // If necessary create Model
	    createContentModel(hbzNodeContentModel);
	} catch (Exception e) {

	}
	// Add Model to Object
	Link link = new Link();
	link.setPredicate(REL_HAS_MODEL);
	link.setObject(addUriPrefix(hbzNodeContentModel.getContentModelPID()),
		false);
	node.addRelation(link);

    }

    void createContentModel(ContentModel cm) throws FedoraClientException,
	    UnsupportedEncodingException {
	String foCMPid = cm.getContentModelPID();
	String foSDefPid = cm.getServiceDefinitionPID();
	String foSDepPid = cm.getServiceDeploymentPID();

	new Ingest(foCMPid).label("Content Model").execute();

	new Ingest(foSDefPid).label("ServiceDefinition").execute();

	new Ingest(foSDepPid).label("ServiceDeployment").execute();

	// Add Relations
	Vector<Link> cmHBZLinks = new Vector<Link>();
	Link cmHBZLink1 = new Link();
	cmHBZLink1.setPredicate(REL_HAS_SERVICE);
	cmHBZLink1.setObject(addUriPrefix(foSDefPid), false);

	cmHBZLinks.add(cmHBZLink1);

	Link cmHBZLink2 = new Link();
	cmHBZLink2.setPredicate(REL_HAS_MODEL);
	cmHBZLink2.setObject(addUriPrefix(CM_CONTENTMODEL), false);
	cmHBZLinks.add(cmHBZLink2);

	addRelationships(foCMPid, cmHBZLinks);

	Vector<Link> sDefHBZLinks = new Vector<Link>();
	Link sDefHBZLink = new Link();
	sDefHBZLink.setPredicate(REL_HAS_MODEL);
	sDefHBZLink.setObject(addUriPrefix(SDEF_CONTENTMODEL), false);
	sDefHBZLinks.add(sDefHBZLink);

	addRelationships(foSDefPid, sDefHBZLinks);

	Vector<Link> sDepHBZLinks = new Vector<Link>();
	Link sDepHBZLink1 = new Link();
	sDepHBZLink1.setPredicate(REL_IS_DEPLOYMENT_OF);
	sDepHBZLink1.setObject(addUriPrefix(foSDefPid), false);
	sDepHBZLinks.add(sDepHBZLink1);

	Link sDepHBZLink2 = new Link();
	sDepHBZLink2.setPredicate(REL_IS_CONTRACTOR_OF);
	sDepHBZLink2.setObject(addUriPrefix(foCMPid), false);
	sDepHBZLinks.add(sDepHBZLink2);

	Link sDepHBZLink3 = new Link();
	sDepHBZLink3.setPredicate(REL_HAS_MODEL);
	sDepHBZLink3.setObject(addUriPrefix(SDEP_CONTENTMODEL), false);
	sDepHBZLinks.add(sDepHBZLink3);

	addRelationships(foSDepPid, sDepHBZLinks);

	new AddDatastream(foCMPid, DS_COMPOSITE_MODEL)
		.dsLabel("DS-Composite-Stream").versionable(true)
		.formatURI(DS_COMPOSITE_MODEL_URI).dsState("A")
		.controlGroup("X").mimeType("text/xml")
		.content(cmBuilder.getDsCompositeModel(cm)).execute();

	new AddDatastream(foSDefPid, DS_METHODMAP).dsLabel("Methodmap-Stream")
		.versionable(true).formatURI(DS_METHODMAP_URI).dsState("A")
		.controlGroup("X").mimeType("text/xml")
		.content(cmBuilder.getMethodMap(cm)).execute();

	new AddDatastream(foSDepPid, DS_METHODMAP_WSDL)
		.dsLabel("Methodmap-Stream").versionable(true)
		.formatURI(DS_METHODMAP_WSDL_URI).dsState("A")
		.controlGroup("X").mimeType("text/xml")
		.content(cmBuilder.getMethodMapToWsdl(cm)).execute();

	new AddDatastream(foSDepPid, DS_INPUTSPEC)
		.dsLabel("DSINPUTSPEC-Stream").versionable(true)
		.formatURI(DS_INPUTSPEC_URI).dsState("A").controlGroup("X")
		.mimeType("text/xml").content(cmBuilder.getDSInputSpec())
		.execute();

	new AddDatastream(foSDepPid, DS_WSDL).dsLabel("WSDL-Stream")
		.versionable(true).formatURI(DS_WSDL_URI).dsState("A")
		.controlGroup("X").mimeType("text/xml")
		.content(cmBuilder.getWsdl(cm)).execute();

    }
}
