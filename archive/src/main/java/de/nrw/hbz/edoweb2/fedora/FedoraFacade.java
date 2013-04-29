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
package de.nrw.hbz.edoweb2.fedora;

import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.DATASTREAM_MIME;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.HAS_DATASTREAM;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.HAS_METADATASTREAM;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.METADATASTREAM_MIME;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_CONTENT_TYPE;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_IN_NAMESPACE;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.CM_CONTENTMODEL;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_INPUTSPEC;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_INPUTSPEC_URI;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_METHODMAP;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_METHODMAP_URI;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_METHODMAP_WSDL;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_METHODMAP_WSDL_URI;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_WSDL;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_WSDL_URI;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.REL_HAS_MODEL;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.REL_HAS_SERVICE;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.REL_IS_CONTRACTOR_OF;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.REL_IS_DEPLOYMENT_OF;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.SDEF_CONTENTMODEL;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.SDEP_CONTENTMODEL;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
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

import org.fcrepo.client.Uploader;
import org.fcrepo.common.Constants;
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
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.AddRelationship;
import com.yourmediashelf.fedora.client.request.FedoraRequest;
import com.yourmediashelf.fedora.client.request.GetDatastream;
import com.yourmediashelf.fedora.client.request.GetDatastreamDissemination;
import com.yourmediashelf.fedora.client.request.GetNextPID;
import com.yourmediashelf.fedora.client.request.GetObjectXML;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.request.PurgeObject;
import com.yourmediashelf.fedora.client.request.Upload;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;
import com.yourmediashelf.fedora.client.response.GetDatastreamResponse;
import com.yourmediashelf.fedora.client.response.GetNextPIDResponse;
import com.yourmediashelf.fedora.client.response.UploadResponse;
import com.yourmediashelf.fedora.generated.management.PidList;

import de.nrw.hbz.edoweb2.archive.exceptions.ArchiveException;
import de.nrw.hbz.edoweb2.archive.exceptions.NodeNotFoundException;
import de.nrw.hbz.edoweb2.datatypes.ContentModel;
import de.nrw.hbz.edoweb2.datatypes.Link;
import de.nrw.hbz.edoweb2.datatypes.Node;

//import org.fcrepo.client.Uploader;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 */
@SuppressWarnings("static-access")
public class FedoraFacade implements FedoraInterface, Constants
{

	/**
	 * TODO: This class is way too long. Cut version independent stuff from
	 * actual fedora calls!
	 * 
	 * */
	private com.yourmediashelf.fedora.client.FedoraClient fedora;
	private final String host;
	private final String user;
	private final String passwd;
	String objecUrl = "info:fedora";

	/**
	 * @param host
	 *            The url of the fedora web endpoint
	 * @param aUser
	 *            A valid fedora user
	 * @param aPassword
	 *            The password of the fedora user
	 */
	public FedoraFacade(String host, String aUser, String aPassword)
	{
		this.user = aUser;
		this.passwd = aPassword;
		this.host = host;

		try
		{
			FedoraCredentials credentials = new FedoraCredentials(host, aUser,
					aPassword);
			fedora = new com.yourmediashelf.fedora.client.FedoraClient(
					credentials);
			FedoraRequest.setDefaultClient(fedora);

		}
		catch (MalformedURLException e)
		{
			throw new ArchiveException("The variable host: " + host
					+ " may contain a malformed url.", e);
		}

	}

	@Override
	public void createNode(Node node)
	{

		try
		{
			// byte[] foxmlObject = serializeNode(node.getPID(),
			// node.getLabel(),
			// new XMLBuilder(fedoraManager),
			// XMLBuilder.OBJECT_TYPE.dataObject);
			//
			// AutoIngestor ingestor = new AutoIngestor(fedoraAccess,
			// fedoraManager);
			//
			// ingestor.ingestAndCommit(new ByteArrayInputStream(foxmlObject),
			// FOXML1_1.uri, "Created with HBZ Webservice");

			new Ingest(node.getPID()).label(node.getLabel()).execute();

			updateDc(node);
			createContentModels(node);

			if (node.getUploadFile() != null)
			{
				createManagedStream(node);

			}
			if (node.getMetadataFile() != null)
			{
				createMetadataStream(node);

			}
			Link link = new Link();
			link.setObject(node.getNamespace(), true);
			link.setPredicate(REL_IS_IN_NAMESPACE);

			node.addRelation(link);

			link = new Link();
			link.setObject(node.getContentType(), true);
			link.setPredicate(REL_CONTENT_TYPE);

			node.addRelation(link);

			createRelsExt(node);
		}
		catch (FedoraClientException e)
		{
			throw new ArchiveException("An unknown exception occured. "
					+ e.getMessage(), e);
		}

	}

	@Override
	public Node readNode(String pid)
	{
		if (!nodeExists(pid))
			throw new NodeNotFoundException(pid + " does not exist!");
		Node node = new Node();
		node.setPID(pid);

		try
		{
			readDcToNode(node);
			readRelsExt(node);
			readContentModels(node);

			FedoraCredentials credentials = new FedoraCredentials(host, user,
					passwd);
			com.yourmediashelf.fedora.client.FedoraClient fedora = new com.yourmediashelf.fedora.client.FedoraClient(
					credentials);

			FedoraRequest.setDefaultClient(fedora);

			node.setLabel(fedora.getObjectProfile(pid).execute().getLabel());
			node.setLastModified(fedora.getLastModifiedDate(pid));
		}
		catch (FedoraClientException e)
		{
			throw new ArchiveException("An unknown exception occured.", e);
		}
		catch (java.net.MalformedURLException e)
		{
			throw new ArchiveException("The variable host: " + host
					+ " may contain a malformed url.", e);
		}
		catch (RemoteException e)
		{
			throw new ArchiveException("An unknown exception occured.", e);
		}

		return node;
	}

	@Override
	public void updateNode(Node node)
	{
		updateDc(node);
		// updateContentModels(node);

		if (node.getUploadFile() != null)
		{

			updateManagedStream(node);
		}

		if (node.getMetadataFile() != null)
		{
			updateMetadataStream(node);
		}
		updateRelsExt(node);

	}

	@Override
	public InputStream findTriples(String rdfQuery, String queryType,
			String outputFormat)
	{
		TripleSearch search = new TripleSearch(this.host, this.user,
				this.passwd);
		try
		{
			return search.find(rdfQuery, queryType, outputFormat);
		}
		catch (IOException e)
		{
			throw new ArchiveException("An unknown exception occured.", e);
		}

	}

	@Override
	public List<String> findPids(String rdfQuery, String queryFormat)
	{

		if (queryFormat.compareTo(FedoraVocabulary.SIMPLE) == 0)
		{

			return findPidsSimple(rdfQuery);
		}

		else
		{
			return findPidsRdf(rdfQuery, queryFormat);
		}

	}

	@Override
	public boolean dataStreamExists(String pid, String datastreamId)
	{
		try
		{

			GetDatastreamResponse response = new GetDatastream(pid,
					datastreamId).execute();

			response.getDatastreamProfile();

		}
		catch (FedoraClientException e)
		{
			return false;
		}
		return true;
	}

	@Override
	public String getPid(String namespace)
	{
		try
		{
			GetNextPIDResponse response = new GetNextPID().namespace(namespace)
					.execute();
			return response.getPid();
		}
		catch (FedoraClientException e)
		{
			throw new ArchiveException(e.getMessage(), e);
		}
	}

	@Override
	public String[] getPids(String namespace, int number)

	{
		try
		{
			GetNextPIDResponse response = new GetNextPID().namespace(namespace)
					.numPIDs(number).execute();
			PidList list = response.getPids();
			String[] arr = new String[list.getPid().size()];
			list.getPid().toArray(arr);
			return arr;
		}
		catch (FedoraClientException e)
		{
			throw new ArchiveException(e.getMessage(), e);
		}
	}

	@Override
	public void deleteNode(String rootPID)
	{
		try
		{
			new PurgeObject(rootPID).execute();

			// AutoPurger purger = new AutoPurger(fedoraManager);
			// purger.purge(rootPID, "delete");
		}
		catch (FedoraClientException e)
		{
			throw new ArchiveException(rootPID
					+ " an unknown exception occured.", e);
		}
	}

	@Override
	public void deleteDatastream(String pid, String datastream)
	{

		try
		{
			new ModifyDatastream(pid, datastream).dsState("D").execute();
		}
		catch (FedoraClientException e)
		{
			throw new ArchiveException("Deletion of " + pid + "/" + datastream
					+ " not possible.", e);
		}

	}

	@Override
	public boolean nodeExists(String pid)
	{
		try
		{

			new GetObjectXML(pid).execute();

		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	@Override
	public String addUriPrefix(String pid)
	{

		if (pid.contains(objecUrl.toString()))
			return pid;
		String pred = objecUrl.toString() + "/" + pid;
		return pred;
	}

	@Override
	public String removeUriPrefix(String pred)
	{
		String pid = pred.replace(this.objecUrl.toString() + "/", "");

		return pid;
	}

	/**
	 * @param cm
	 *            A fedora-like content model
	 * @return The fedora-ready content model as String.
	 */
	public String getDsCompositeModel(ContentModel cm)
	{
		String start = " <dsCompositeModel xmlns=\"info:fedora/fedora-system:def/dsCompositeModel#\">";

		StringBuffer middle = new StringBuffer();
		Vector<String> prescribedDSIds = cm.getPrescribedDSIds();

		for (int i = 0; i < prescribedDSIds.size(); i++)
		{
			String dsid = prescribedDSIds.get(i);
			String furi = cm.getPrescribedDSformatURIs().get(i);
			String mtype = cm.getPrescribedDSMimeTypes().get(i);

			middle.append("<dsTypeModel ID=\"" + dsid + "\">"
					+ "<form FORMAT_URI=\"" + furi + "\" MIME=\"" + mtype
					+ "\"/>" + "</dsTypeModel>");
		}

		String end = "</dsCompositeModel>";

		return start + middle.toString() + end;
	}

	/**
	 * @param cm
	 *            A fedora-like content model
	 * @return A fedora-like methodmap as string
	 */
	public String getMethodMap(ContentModel cm)
	{
		String start = "<fmm:MethodMap name=\"MethodMap\" xmlns:fmm=\"http://fedora.comm.nsdlib.org/service/methodmap\">";

		StringBuffer middle = new StringBuffer();
		for (int i = 0; i < cm.getMethodNames().size(); i++)
		{
			String methodName = cm.getMethodNames().get(i);
			middle.append("<fmm:Method label=\"" + methodName
					+ "\" operationName=\"" + methodName + "\"/>");
		}

		String end = "</fmm:MethodMap>";

		return start + middle.toString() + end;
	}

	/**
	 * @param cm
	 *            a fedora-like content model
	 * @return the fedora-like method map to wsdl mapping as string
	 */
	public String getMethodMapToWsdl(ContentModel cm)
	{

		String start = "<fmm:MethodMap name=\"MethodMap\" xmlns:fmm=\"http://fedora.comm.nsdlib.org/service/methodmap\">";

		StringBuffer middle = new StringBuffer();
		for (int i = 0; i < cm.getMethodNames().size(); i++)
		{
			String methodName = cm.getMethodNames().get(i);
			middle.append("    <fmm:Method operationLabel=\""
					+ methodName
					+ "\" operationName=\""
					+ methodName
					+ "\" wsdlMsgName=\""
					+ methodName
					+ "Request\" wsdlMsgOutput=\"dissemResponse\">"
					+ "        <fmm:DatastreamInputParm defaultValue=\"\" label=\""
					+ methodName
					+ "\" parmName=\"DC\" passBy=\"URL_REF\" required=\"false\"/>"
					+ "<fmm:DefaultInputParm defaultValue=\"$pid\" parmName=\"pid\" passBy=\"VALUE\" required=\"true\"/>"
					+ "    </fmm:Method>");
		}

		String end = "</fmm:MethodMap>";

		return start + middle.toString() + end;
	}

	/**
	 * @return the fedora-like input-spec as string
	 */
	public String getDSInputSpec()
	{
		return "<fbs:DSInputSpec label=\"Undefined\" xmlns:fbs=\"http://fedora.comm.nsdlib.org/service/bindspec\">"
				+ "<fbs:DSInput DSMax=\"1\" DSMin=\"1\" DSOrdinality=\"false\" wsdlMsgPartName=\"DC\">"
				+ "	    <fbs:DSInputLabel>DC Binding</fbs:DSInputLabel>"
				+ "	    <fbs:DSMIME>text/xml</fbs:DSMIME>"
				+ "	    <fbs:DSInputInstruction/>"
				+ "	  </fbs:DSInput>"
				+ "	</fbs:DSInputSpec>";

	}

	/**
	 * @param cm
	 *            a fedora-like content model
	 * @return the fedora-like wsdl as string
	 */
	public String getWsdl(ContentModel cm)
	{
		String start = "<wsdl:definitions name=\"Undefined\" targetNamespace=\"bmech\""
				+ "	    xmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\" xmlns:mime=\"http://schemas.xmlsoap.org/wsdl/mime/\""
				+ "	    xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap\" xmlns:soapenc=\"http://schemas.xmlsoap.org/wsdl/soap/encoding\""
				+ "	    xmlns:this=\"bmech\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">";

		StringBuffer middle = new StringBuffer();
		for (int i = 0; i < cm.getMethodNames().size(); i++)
		{
			String methodName = cm.getMethodNames().get(i);
			middle.append("<wsdl:message name=\"" + methodName + "Request\">"
					+ "	    </wsdl:message>");
		}

		middle.append("	    <wsdl:message name=\"dissemResponse\">"
				+ "	      <wsdl:part name=\"dissem\" type=\"xsd:base64Binary\"/>"
				+ "	    </wsdl:message>");

		middle.append("<wsdl:portType name=\"HBZContentModelPortType\">");
		for (int i = 0; i < cm.getMethodNames().size(); i++)
		{
			String methodName = cm.getMethodNames().get(i);
			middle.append("<wsdl:operation name=\"" + methodName + "\">"
					+ "	        <wsdl:input message=\"this:" + methodName
					+ "Request\"/>"
					+ "	        <wsdl:output message=\"this:dissemResponse\"/>"
					+ "	      </wsdl:operation>");
		}
		middle.append("</wsdl:portType>");
		middle.append("<wsdl:service name=\"HBZContentModelImpl\">");
		for (int i = 0; i < cm.getMethodNames().size(); i++)
		{
			// String methodName =
			cm.getMethodNames().get(i);
			middle.append("<wsdl:port binding=\"this:HBZContentModelImpl_http\" name=\"HBZContentModelImpl_port\">"
					+ "	        <http:address location=\"LOCAL\"/>"
					+ "	      </wsdl:port>");
		}
		middle.append("</wsdl:service>");
		middle.append(" <wsdl:binding name=\"HBZContentModelImpl_http\" type=\"this:HBZContentModelPortType\">"
				+ "	      <http:binding verb=\"GET\"/>");

		for (int i = 0; i < cm.getMethodNames().size(); i++)
		{
			String methodName = cm.getMethodNames().get(i);
			String methodLocation = cm.getMethodLocations().get(i);
			middle.append("<wsdl:operation name=\"" + methodName + "\">"
					+ "	        <http:operation location=\"" + methodLocation
					+ "\"/>" + "	        <wsdl:input>"
					+ "	          <http:urlReplacement/>"
					+ "	        </wsdl:input>" + "	        <wsdl:output>"
					+ "	          <mime:content type=\"text/xml\"/>"
					+ "	        </wsdl:output>" + "	      </wsdl:operation>");
		}
		String end = " </wsdl:binding>" + "	  </wsdl:definitions>";
		return start + middle.toString() + end;
	}

	@Override
	public void updateContentModel(ContentModel cm)
	{
		deleteNode(cm.getContentModelPID());
		deleteNode(cm.getServiceDefinitionPID());
		deleteNode(cm.getServiceDeploymentPID());
		try
		{
			createContentModel(cm);
		}
		catch (IOException e)
		{
			throw new ArchiveException(
					"Problem during update of ContentModel: "
							+ cm.getContentModelPID(), e);

		}
		catch (FedoraClientException e)
		{
			throw new ArchiveException(
					"Problem during update of ContentModel: "
							+ cm.getContentModelPID(), e);
		}

	}

	private void createRelsExt(Node node)
	{

		String pid = node.getPID();

		// IF DATASTREAM ! EXISTS
		// CREATE DATASTREAM
		// ADD RELATIONS

		if (!dataStreamExists(pid, "RELS-EXT"))
		{
			// System.out.println("PID "+pid+" doesn't exist, create new");
			createFedoraXmlForRelsExt(pid);

		}

		Vector<Link> links = node.getRelsExt();
		createRelsExt(pid, links);

	}

	private void createRelsExt(String pid, Vector<Link> links)
	{
		if (links != null)
			for (Link curHBZLink : links)
			{
				if (curHBZLink == null)
					return;
				// System.out.println(" CREATE: <" + pid + "> <"
				// + curHBZLink.getPredicate() + "> <"
				// + curHBZLink.getObject() + ">");

				if (curHBZLink.isLiteral())
				{
					// System.out.println("isLiteral");
					// fedoraManager.addRelationship(pid,
					// curHBZLink.getPredicate(), curHBZLink.getObject(),
					// curHBZLink.isLiteral(), null);
					new AddRelationship(pid).predicate(
							curHBZLink.getPredicate()).object(
							curHBZLink.getObject(), curHBZLink.isLiteral());
				}
				else
				{
					// System.out.println("NOT isLiteral");
					// fedoraManager.addRelationship(pid,
					// curHBZLink.getPredicate(),
					// addUriPrefix(curHBZLink.getObject()),
					// curHBZLink.isLiteral(), null);
					new AddRelationship(pid).predicate(
							curHBZLink.getPredicate()).object(
							addUriPrefix(curHBZLink.getObject()),
							curHBZLink.isLiteral());

				}
			}
	}

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: Allows to ingest a local file as managed datastream of the
	 * object
	 * </p>
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
	private void createManagedStream(Node node)
	{

		try
		{

			UploadResponse response = new Upload(new File(node.getUploadFile()))
					.execute();

			String location = response.getUploadLocation();

			new AddDatastream(node.getPID(), node.getFileName())
					.versionable(true).dsState("A").controlGroup("M")
					.mimeType(node.getMimeType()).dsLocation(location)
					.execute();

			node.setDataUrl(new URL(host + "/objects/" + node.getPID()
					+ "/datastreams/" + node.getFileName() + "/content"));
			Link link = new Link();
			link.setObject(node.getFileName(), true);
			link.setPredicate(HAS_DATASTREAM);
			node.addRelation(link);

			link = new Link();
			link.setObject(node.getMimeType(), true);
			link.setPredicate(DATASTREAM_MIME);
			node.addRelation(link);

		}
		catch (Exception e)
		{
			throw new ArchiveException(node.getPID()
					+ " an unknown exception occured.", e);
		}
	}

	private void createMetadataStream(Node node)
	{

		try
		{

			Upload request = new Upload(new File(node.getMetadataFile()));
			UploadResponse response = request.execute();
			String location = response.getUploadLocation();

			new AddDatastream(node.getPID(), "metadata").versionable(true)
					.dsState("A").controlGroup("M")
					.mimeType(node.getMimeType()).dsLocation(location)
					.execute();

			node.setMetadataUrl(new URL(this.host + "/objects/" + node.getPID()
					+ "/datastreams/" + "metadata" + "/content"));
			Link link = new Link();
			link.setObject(node.getMetadataFile(), true);
			link.setPredicate(HAS_METADATASTREAM);
			node.addRelation(link);

			link = new Link();
			link.setObject("text/plain", true);
			link.setPredicate(METADATASTREAM_MIME);
			node.addRelation(link);

		}
		catch (Exception e)
		{
			throw new ArchiveException(node.getPID()
					+ " an unknown exception occured.", e);
		}
	}

	private void updateManagedStream(Node node)
	{

		try
		{
			Upload request = new Upload(new File(node.getUploadFile()));
			UploadResponse response = request.execute();
			String location = response.getUploadLocation();

			String newID = null;
			if (dataStreamExists(node.getPID(), node.getFileName()))
			{
				new ModifyDatastream(node.getPID(), node.getFileName())
						.versionable(true).dsState("A").controlGroup("M")
						.mimeType(node.getMimeType()).dsLocation(location)
						.execute();
			}
			else
			{
				new AddDatastream(node.getPID(), node.getFileName())
						.versionable(true).dsState("A")
						.mimeType(node.getMimeType())
						.mimeType(node.getMimeType()).dsLocation(location)
						.controlGroup("M").execute();
			}
			node.setDataUrl(new URL(host + "/objects/" + node.getPID()
					+ "/datastreams/" + newID + "/content"));
			Link link = new Link();
			link.setObject(node.getFileName(), true);
			link.setPredicate(HAS_DATASTREAM);
			node.addRelation(link);

			link = new Link();
			link.setObject(node.getMimeType(), true);
			link.setPredicate(DATASTREAM_MIME);
			node.addRelation(link);
		}
		catch (Exception e)
		{
			throw new ArchiveException(node.getPID()
					+ " an unknown exception occured.", e);
		}
	}

	private void updateMetadataStream(Node node)
	{
		try
		{

			Upload request = new Upload(new File(node.getMetadataFile()));
			UploadResponse response = request.execute();
			String location = response.getUploadLocation();

			String newID = null;
			if (dataStreamExists(node.getPID(), "metadata"))
			{
				new ModifyDatastream(node.getPID(), "metadata")
						.versionable(true).dsState("A").controlGroup("M")
						.mimeType(node.getMimeType()).dsLocation(location)
						.execute();
			}
			else
			{
				new AddDatastream(node.getPID(), "metadata").versionable(true)
						.dsState("A").controlGroup("M")
						.mimeType(node.getMimeType()).dsLocation(location)
						.execute();
			}
			node.setDataUrl(new URL(host + "/objects/" + node.getPID()
					+ "/datastreams/" + newID + "/content"));
			Link link = new Link();
			link.setObject(node.getMetadataFile(), true);
			link.setPredicate(HAS_METADATASTREAM);
			node.addRelation(link);

			link = new Link();
			link.setObject("text/plain", true);
			link.setPredicate(METADATASTREAM_MIME);
			node.addRelation(link);
		}
		catch (Exception e)
		{
			throw new ArchiveException(node.getPID()
					+ " an unknown exception occured.", e);
		}
	}

	private void createContentModels(Node node)
	{
		Vector<ContentModel> models = node.getContentModels();
		if (models == null)
			return;
		for (ContentModel m : models)
		{
			createContentModel(m, node);
		}
		createFedoraXMLForContentModels(node);
	}

	private void createContentModel(ContentModel hbzNodeContentModel, Node node)
	{

		try
		{
			// If necessary create Model
			createContentModel(hbzNodeContentModel);
		}
		catch (Exception e)
		{

		}
		// Add Model to Object
		Link link = new Link();
		link.setPredicate(REL_HAS_MODEL);
		link.setObject(addUriPrefix(hbzNodeContentModel.getContentModelPID()),
				false);
		node.addRelation(link);

	}

	private void createContentModel(ContentModel cm)
			throws FedoraClientException, UnsupportedEncodingException
	{
		// String state = "A";

		String foCMPid = cm.getContentModelPID();
		String foSDefPid = cm.getServiceDefinitionPID();
		String foSDepPid = cm.getServiceDeploymentPID();

		// Create CM
		// byte[] cmFoxmlObject = serializeNode(foCMPid, "Content Model",
		// new XMLBuilder(fedoraManager),
		// XMLBuilder.OBJECT_TYPE.contentModel);
		// AutoIngestor.ingestAndCommit(this.fedoraAccess, fedoraManager,
		// new ByteArrayInputStream(cmFoxmlObject), FOXML1_1.uri,
		// "Created with HBZ Webservice");

		new Ingest(foCMPid).label("Content Model").execute();

		// // Create SDef
		// byte[] sDefFoxmlObject = serializeNode(foSDefPid,
		// "ServiceDefinition",
		// new XMLBuilder(fedoraManager),
		// XMLBuilder.OBJECT_TYPE.serviceDefinition);
		// AutoIngestor.ingestAndCommit(this.fedoraAccess, fedoraManager,
		// new ByteArrayInputStream(sDefFoxmlObject), FOXML1_1.uri,
		// "Created with HBZ Webservice");

		new Ingest(foSDefPid).label("ServiceDefinition").execute();

		// // Create SDep
		// byte[] sDepFoxmlObject = serializeNode(foSDepPid,
		// "ServiceDeployment",
		// new XMLBuilder(fedoraManager),
		// XMLBuilder.OBJECT_TYPE.serviceDeployment);
		// AutoIngestor.ingestAndCommit(this.fedoraAccess, fedoraManager,
		// new ByteArrayInputStream(sDepFoxmlObject), FOXML1_1.uri,
		// "Created with HBZ Webservice");
		new Ingest(foSDepPid).label("ServiceDeployment").execute();

		// Create Rels-Ext Datestreams
		createFedoraXmlForRelsExt(foCMPid);
		createFedoraXmlForRelsExt(foSDefPid);
		createFedoraXmlForRelsExt(foSDepPid);

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

		updateRelsExt(foCMPid, cmHBZLinks);

		Vector<Link> sDefHBZLinks = new Vector<Link>();
		Link sDefHBZLink = new Link();
		sDefHBZLink.setPredicate(REL_HAS_MODEL);
		sDefHBZLink.setObject(addUriPrefix(SDEF_CONTENTMODEL), false);
		sDefHBZLinks.add(sDefHBZLink);

		updateRelsExt(foSDefPid, sDefHBZLinks);

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

		this.updateRelsExt(foSDepPid, sDepHBZLinks);

		// DataHandler dh = new DataHandler(new ByteArrayDataSource(
		// getDsCompositeModel(cm).getBytes("UTF-8"), "text/xml"));
		//
		// fedoraManager
		// .modifyDatastreamByValue(
		// foCMPid,
		// de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_COMPOSITE_MODEL,
		// null,
		// "DS-Composite-Stream",
		// "text/xml",
		// de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_COMPOSITE_MODEL_URI,
		// dh, "DISABLED", null, "UPDATE OF METHODMAP STREAM",
		// false);

		new ModifyDatastream(foCMPid,
				de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_COMPOSITE_MODEL)
				.versionable(true)
				.formatURI(
						de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_COMPOSITE_MODEL_URI)
				.dsState("A").controlGroup("M").mimeType("text/xml")
				.content(getDsCompositeModel(cm)).execute();

		// Add Methodmap to sDef
		// dh = new DataHandler(new
		// ByteArrayDataSource(getMethodMap(cm).getBytes(
		// "UTF-8"), "text/xml"));
		// fedoraManager.modifyDatastreamByValue(foSDefPid, DS_METHODMAP, null,
		// "Methodmap-Stream", "text/xml", DS_METHODMAP_URI, dh,
		// "DISABLED", null, "UPDATE OF METHODMAP STREAM", false);

		new ModifyDatastream(foSDefPid, DS_METHODMAP).versionable(true)
				.formatURI(DS_METHODMAP_URI).dsState("A").controlGroup("M")
				.mimeType("text/xml").content(getMethodMap(cm)).execute();

		// Add Methodmap to sDep
		// dh = new DataHandler(new ByteArrayDataSource(getMethodMapToWsdl(cm)
		// .getBytes("UTF-8"), "text/xml"));
		// fedoraManager.modifyDatastreamByValue(foSDepPid, DS_METHODMAP_WSDL,
		// null, "Methodmap-Stream", "text/xml", DS_METHODMAP_WSDL_URI,
		// dh, "DISABLED", null, "UPDATE OF METHODMAP STREAM", false);

		new ModifyDatastream(foSDepPid, DS_METHODMAP_WSDL).versionable(true)
				.formatURI(DS_METHODMAP_WSDL_URI).dsState("A")
				.controlGroup("M").mimeType("text/xml")
				.content(getMethodMapToWsdl(cm)).execute();

		// Add DSINPUTSPEC to sDep
		// dh = new DataHandler(new
		// ByteArrayDataSource(getDSInputSpec().getBytes(
		// "UTF-8"), "text/xml"));
		// fedoraManager.modifyDatastreamByValue(foSDepPid, DS_INPUTSPEC, null,
		// "DSINPUTSPEC-Stream", "text/xml", DS_INPUTSPEC_URI, dh,
		// "DISABLED", null, "UPDATE OF DSINPUTSPEC STREAM", false);

		new ModifyDatastream(foSDepPid, DS_INPUTSPEC).versionable(true)
				.formatURI(DS_INPUTSPEC_URI).dsState("A").controlGroup("M")
				.mimeType("text/xml").content(getDSInputSpec()).execute();

		// Add WSDL to sDep
		// System.out.println(foSDepPid);
		// System.out.println(cm.getWsdl());
		// dh = new DataHandler(new ByteArrayDataSource(getWsdl(cm).getBytes(
		// "UTF-8"), "text/xml"));
		// fedoraManager.modifyDatastreamByValue(foSDepPid, DS_WSDL, null,
		// "WSDL-Stream", "text/xml", DS_WSDL_URI, dh, "DISABLED", null,
		// "UPDATE OF WSDL STREAM", false);

		new ModifyDatastream(foSDepPid, DS_WSDL).versionable(true)
				.formatURI(DS_WSDL_URI).dsState("A").controlGroup("M")
				.mimeType("text/xml").content(getWsdl(cm)).execute();

	}

	private void readDcToNode(Node node) throws RemoteException,
			FedoraClientException
	{
		// MIMETypedStream ds = fedoraAccess.getDatastreamDissemination(
		// node.getPID(), "DC", null);

		FedoraResponse response = new GetDatastreamDissemination(node.getPID(),
				"DC").download(true).execute();
		InputStream ds = response.getEntityInputStream();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setExpandEntityReferences(false);
		try
		{
			DocumentBuilder docBuilder = factory.newDocumentBuilder();

			Document doc = docBuilder.parse(new BufferedInputStream(ds)); // TODO
																			// Correct?
																			// UTF-8?
			Element root = doc.getDocumentElement();
			root.normalize();

			NodeList contributer = root.getElementsByTagName("dc:contributer");
			NodeList coverage = root.getElementsByTagName("dc:coverage");
			NodeList creator = root.getElementsByTagName("dc:creator");
			NodeList date = root.getElementsByTagName("dc:date");
			NodeList description = root.getElementsByTagName("dc:description");
			NodeList format = root.getElementsByTagName("dc:format");
			NodeList identifier = root.getElementsByTagName("dc:identifier");
			NodeList label = root.getElementsByTagName("dc:label");
			NodeList language = root.getElementsByTagName("dc:language");
			NodeList publisher = root.getElementsByTagName("dc:publisher");
			NodeList rights = root.getElementsByTagName("dc:rights");
			NodeList source = root.getElementsByTagName("dc:source");
			NodeList subject = root.getElementsByTagName("dc:subject");
			NodeList title = root.getElementsByTagName("dc:title");
			NodeList type = root.getElementsByTagName("dc:type");

			if (contributer != null && contributer.getLength() != 0)
			{
				for (int i = 0; i < contributer.getLength(); i++)
				{
					node.addContributer(transformFromXMLEntity(contributer
							.item(i).getTextContent()));
				}
			}
			if (coverage != null && coverage.getLength() != 0)
			{
				for (int i = 0; i < coverage.getLength(); i++)
				{
					node.addCoverage(transformFromXMLEntity(coverage.item(i)
							.getTextContent()));
				}
			}
			if (creator != null && creator.getLength() != 0)
			{
				for (int i = 0; i < creator.getLength(); i++)
				{
					node.addCreator(transformFromXMLEntity(creator.item(i)
							.getTextContent()));
				}
			}
			if (date != null && date.getLength() != 0)
			{
				for (int i = 0; i < date.getLength(); i++)
				{
					node.addDate(transformFromXMLEntity(date.item(i)
							.getTextContent()));
				}
			}
			if (description != null && description.getLength() != 0)
			{
				for (int i = 0; i < description.getLength(); i++)
				{
					node.addDescription(transformFromXMLEntity(description
							.item(i).getTextContent()));
				}
			}
			if (format != null && format.getLength() != 0)
			{
				for (int i = 0; i < format.getLength(); i++)
				{
					node.addFormat(transformFromXMLEntity(format.item(i)
							.getTextContent()));
				}
			}
			if (identifier != null && identifier.getLength() != 0)
			{
				for (int i = 0; i < identifier.getLength(); i++)
				{
					node.addIdentifier(transformFromXMLEntity(identifier
							.item(i).getTextContent()));
				}
			}
			if (label != null && label.getLength() != 0)
			{
				for (int i = 0; i < label.getLength(); i++)
				{
					// TODO set oder add
					node.setLabel(transformFromXMLEntity(label.item(i)
							.getTextContent()));
				}
			}
			if (language != null && language.getLength() != 0)
			{
				for (int i = 0; i < language.getLength(); i++)
				{
					node.addLanguage(transformFromXMLEntity(language.item(i)
							.getTextContent()));
				}
			}
			if (publisher != null && publisher.getLength() != 0)
			{
				for (int i = 0; i < publisher.getLength(); i++)
				{
					node.addPublisher(transformFromXMLEntity(publisher.item(i)
							.getTextContent()));
				}
			}
			if (rights != null && rights.getLength() != 0)
			{
				for (int i = 0; i < rights.getLength(); i++)
				{
					node.addRights(transformFromXMLEntity(rights.item(i)
							.getTextContent()));
				}
			}
			if (source != null && source.getLength() != 0)
			{
				for (int i = 0; i < source.getLength(); i++)
				{
					node.addSource(transformFromXMLEntity(source.item(i)
							.getTextContent()));
				}
			}
			if (subject != null && subject.getLength() != 0)
			{
				for (int i = 0; i < subject.getLength(); i++)
				{
					node.addSubject(transformFromXMLEntity(subject.item(i)
							.getTextContent()));
				}
			}
			if (title != null && title.getLength() != 0)
			{
				for (int i = 0; i < title.getLength(); i++)
				{
					node.addTitle(transformFromXMLEntity(title.item(i)
							.getTextContent()));
				}
			}
			if (type != null && type.getLength() != 0)
			{
				for (int i = 0; i < type.getLength(); i++)
				{
					node.addType(transformFromXMLEntity(type.item(i)
							.getTextContent()));
				}
			}

		}
		catch (ParserConfigurationException e)
		{

			throw new ArchiveException("An unknown exception occured.", e);
		}
		catch (SAXException e)
		{

			throw new ArchiveException("An unknown exception occured.", e);
		}
		catch (IOException e)
		{

			throw new ArchiveException("An unknown exception occured.", e);
		}

	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param node
	 * @throws FedoraClientException
	 */
	private void readRelsExt(Node node) throws FedoraClientException
	{

		try
		{
			FedoraResponse response = new GetDatastreamDissemination(
					node.getPID(), "RELS-EXT").download(true).execute();
			InputStream ds = response.getEntityInputStream();

			// MIMETypedStream ds = fedoraAccess.getDatastreamDissemination(
			// node.getPID(), "RELS-EXT", null);

			Repository myRepository = new SailRepository(new MemoryStore());
			myRepository.initialize();

			RepositoryConnection con = myRepository.getConnection();
			String baseURI = "";

			try
			{
				ValueFactory f = myRepository.getValueFactory();
				URI objectId = f.createURI("info:fedora/" + node.getPID());
				con.add(new BufferedInputStream(ds), baseURI, RDFFormat.RDFXML);
				RepositoryResult<Statement> statements = con.getStatements(
						objectId, null, null, true);

				try
				{
					while (statements.hasNext())
					{
						Statement st = statements.next();

						URI predUri = st.getPredicate();
						Value objUri = st.getObject();

						Link link = new Link();
						link.setObject(objUri.stringValue());
						link.setPredicate(predUri.stringValue());

						// System.out.println(" READ: <" + node.getPID() + "> <"
						// + link.getPredicate() + "> <"
						// + link.getObject() + ">");

						if (link.getPredicate().compareTo(REL_IS_NODE_TYPE) == 0)
						{
							node.setNodeType(link.getObject());
						}
						else if (link.getPredicate().compareTo(
								REL_IS_IN_NAMESPACE) == 0)
						{
							node.setNamespace(link.getObject());
						}
						else if (link.getPredicate().compareTo(HAS_DATASTREAM) == 0)
						{
							node.setFileName(link.getObject());
							node.setDataUrl(new URL(host + "/objects/"
									+ node.getPID() + "/datastreams/"
									+ node.getFileName() + "/content"));

						}
						else if (link.getPredicate().compareTo(DATASTREAM_MIME) == 0)
						{
							node.setMimeType(link.getObject());
						}
						else if (link.getPredicate().compareTo(
								HAS_METADATASTREAM) == 0)
						{
							node.setMetadataUrl(new URL(host + "/objects/"
									+ node.getPID()
									+ "/datastreams/metadata/content"));
						}
						else if (link.getPredicate()
								.compareTo(REL_CONTENT_TYPE) == 0)
						{
							node.setContentType(link.getObject());
						}

						String object = link.getObject();
						try
						{
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
							// System.out.println("Is not Literal");
						}
						catch (URISyntaxException e)
						{
							// System.out.println(e.getMessage());
							// System.out.println("Is Literal");
						}

						node.addRelation(link);

					}
				}
				catch (Exception e)
				{
					throw new ArchiveException(node.getPID()
							+ " an unknown exception occured.", e);
				}

				finally
				{
					statements.close(); // make sure the result object is closed
										// properly
				}

			}
			finally
			{
				con.close();
			}

		}
		catch (RepositoryException e)
		{

			throw new ArchiveException(node.getPID()
					+ " an unknown exception occured.", e);
		}
		catch (RemoteException e)
		{

			throw new ArchiveException(node.getPID()
					+ " an unknown exception occured.", e);
		}
		catch (RDFParseException e)
		{

			throw new ArchiveException(node.getPID()
					+ " an unknown exception occured.", e);
		}
		catch (IOException e)
		{

			throw new ArchiveException(node.getPID()
					+ " an unknown exception occured.", e);
		}

	}

	private void readContentModels(Node node) throws RemoteException,
			FedoraClientException
	{

		// MIMETypedStream ds = fedoraAccess.getDatastreamDissemination(
		// node.getPID(), "HBZCMInfoStream", null);
		//
		FedoraResponse response = new GetDatastreamDissemination(node.getPID(),
				"HBZCMInfoStream").download(true).execute();
		InputStream ds = response.getEntityInputStream();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document doc = docBuilder.parse(new BufferedInputStream(ds));
			Element root = doc.getDocumentElement();
			root.normalize();

			NodeList contentModels = root.getElementsByTagName("ContentModel");

			for (int i = 0; i < contentModels.getLength(); i++)
			{
				ContentModel newModel = new ContentModel();

				org.w3c.dom.Node contentModelIdNode = root
						.getElementsByTagName("ContentModelPid").item(i);
				org.w3c.dom.Node serviceDefIdNode = root.getElementsByTagName(
						"ServiceDefPid").item(i);
				org.w3c.dom.Node serviceDepIdNode = root.getElementsByTagName(
						"ServiceDepPid").item(i);

				newModel.setContentModelPID(contentModelIdNode.getTextContent());
				newModel.setServiceDefinitionPID(serviceDefIdNode
						.getTextContent());
				newModel.setServiceDeploymentPID(serviceDepIdNode
						.getTextContent());

				org.w3c.dom.Node prescribedDss = root.getElementsByTagName(
						"PrescribedDSs").item(i);

				NodeList prescribedDs = ((Element) (prescribedDss))
						.getElementsByTagName("PrescribedDS");

				for (int j = 0; j < prescribedDs.getLength(); j++)
				{
					org.w3c.dom.Node dsid = ((Element) (prescribedDs.item(j)))
							.getElementsByTagName("dsid").item(0);
					org.w3c.dom.Node uri = ((Element) (prescribedDs.item(j)))
							.getElementsByTagName("uri").item(0);
					org.w3c.dom.Node mimeType = ((Element) (prescribedDs
							.item(j))).getElementsByTagName("mimeType").item(0);

					newModel.addPrescribedDs(dsid.getTextContent(),
							uri.getTextContent(), mimeType.getTextContent());
				}

				org.w3c.dom.Node methods = root.getElementsByTagName("Methods")
						.item(i);
				NodeList methodsKids = ((Element) (methods))
						.getElementsByTagName("Method");

				for (int j = 0; j < methodsKids.getLength(); j++)
				{
					org.w3c.dom.Node name = ((Element) (methodsKids.item(j)))
							.getElementsByTagName("name").item(0);
					org.w3c.dom.Node loc = ((Element) (methodsKids.item(j)))
							.getElementsByTagName("serviceLocation").item(0);

					newModel.addMethod(name.getTextContent(),
							loc.getTextContent());

				}
				node.addContentModel(newModel);
			}

		}
		catch (ParserConfigurationException e)
		{

			throw new ArchiveException(node.getPID()
					+ " an unknown exception occured.", e);
		}
		catch (SAXException e)
		{

			throw new ArchiveException(node.getPID()
					+ " an unknown exception occured.", e);
		}
		catch (IOException e)
		{

			throw new ArchiveException(node.getPID()
					+ " an unknown exception occured.", e);
		}

	}

	private void updateRelsExt(String pid, Vector<Link> links)
	{

		if (links != null)
			for (Link curHBZLink : links)
			{
				if (curHBZLink == null)
					return;

				// System.out.println("UPDATE: <" + pid + "> <"
				// + curHBZLink.getPredicate() + "> <"
				// + curHBZLink.getObject() + ">");
				try
				{
					if (curHBZLink.isLiteral())
					{
						// System.out.println("isLiteral");
						// fedoraManager.addRelationship(pid,
						// curHBZLink.getPredicate(),
						// curHBZLink.getObject(), true, null);
						new AddRelationship(pid)
								.predicate(curHBZLink.getPredicate())
								.object(curHBZLink.getObject(), true).execute();
					}
					else
					{
						// System.out.println("NOT isLiteral");

						// fedoraManager.addRelationship(pid,
						// curHBZLink.getPredicate(),
						// curHBZLink.getObject(), false, null);
						new AddRelationship(pid)
								.predicate(curHBZLink.getPredicate())
								.object(curHBZLink.getObject(), false)
								.execute();
					}
				}
				catch (Exception e)
				{
					// System.out.println("Try as Literal:");
					try
					{
						// fedoraManager.addRelationship(pid,
						// curHBZLink.getPredicate(),
						// curHBZLink.getObject(), true, null);
						new AddRelationship(pid)
								.predicate(curHBZLink.getPredicate())
								.object(curHBZLink.getObject(), true).execute();
					}
					catch (Exception e2)
					{
						System.out.println("UPDATE: Could not ingest: <" + pid
								+ "> <" + curHBZLink.getPredicate() + "> <"
								+ curHBZLink.getObject() + ">");

					}

				}

			}

	}

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: RELS-Ext are added to the POJO and to the corresponding
	 * fedora object
	 * </p>
	 * 
	 * @param node
	 */
	private void updateRelsExt(Node node)
	{

		String pid = node.getPID();

		// IF DATASTREAM ! EXISTS
		// CREATE DATASTREAM
		// ADD RELATIONS

		if (!dataStreamExists(pid, "RELS-EXT"))
		{
			createFedoraXmlForRelsExt(pid);
		}
		Vector<Link> links = node.getRelsExt();

		Node old = readNode(pid);
		links.removeAll(old.getRelsExt());

		updateRelsExt(pid, links);

	}

	private void updateDc(Node node)
	{
		String preamble = ""
				+ "<oai_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\" "
				+ "xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\""
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/"
				+ "http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">";
		String fazit = "</oai_dc:dc>";

		String tagStart = "<dc:";
		String tagEnd = ">";
		String endTagStart = "</dc:";

		StringBuffer update = new StringBuffer();

		Vector<String> contributer = null;
		Vector<String> coverage = null;
		Vector<String> creator = null;
		Vector<String> date = null;
		Vector<String> description = null;
		Vector<String> format = null;
		Vector<String> identifier = null;
		// String[] label = null;
		Vector<String> language = null;
		Vector<String> publisher = null;
		Vector<String> rights = null;
		Vector<String> source = null;
		Vector<String> subject = null;
		Vector<String> title = null;
		Vector<String> type = null;

		if ((contributer = node.getContributer()) != null)
		{
			for (String str : contributer)
			{
				String scontributer = tagStart + "contributer" + tagEnd
						+ transformToXMLEntity(str) + endTagStart
						+ "contributer" + tagEnd;
				update.append(scontributer + "\n");
			}
		}
		if ((coverage = node.getCoverage()) != null)
		{
			for (String str : coverage)
			{
				String scoverage = tagStart + "coverage" + tagEnd
						+ transformToXMLEntity(str) + endTagStart + "coverage"
						+ tagEnd;
				update.append(scoverage + "\n");
			}
		}
		if ((creator = node.getCreator()) != null)
		{
			for (String str : creator)
			{

				String screator = tagStart + "creator" + tagEnd
						+ transformToXMLEntity(str) + endTagStart + "creator"
						+ tagEnd;
				update.append(screator + "\n");
			}
		}
		if ((date = node.getDate()) != null)
		{
			for (String str : date)
			{
				String sdate = tagStart + "date" + tagEnd
						+ transformToXMLEntity(str) + endTagStart + "date"

						+ tagEnd;
				update.append(sdate + "\n");
			}
		}
		if ((description = node.getDescription()) != null)
		{
			for (String str : description)
			{
				String sdescription = tagStart + "description" + tagEnd
						+ transformToXMLEntity(str) + endTagStart
						+ "description" + tagEnd;
				update.append(sdescription + "\n");
			}
		}
		if ((format = node.getFormat()) != null)
		{
			for (String str : format)
			{
				String sformat = tagStart + "format" + tagEnd
						+ transformToXMLEntity(str) + endTagStart + "format"
						+ tagEnd;
				update.append(sformat + "\n");
			}
		}
		if ((identifier = node.getIdentifier()) != null)
		{
			for (String str : identifier)
			{
				String sidentifier = tagStart + "identifier" + tagEnd
						+ transformToXMLEntity(str) + endTagStart
						+ "identifier" + tagEnd;
				update.append(sidentifier + "\n");
			}
		}
		/*
		 * if ((label = node.getLabel()) != null) { for (int i = 0; i <
		 * label.length; i++) { String slabel = tagStart + "label" + tagEnd +
		 * label[i] + endTagStart + "label" + tagEnd; update.append(label +
		 * "\n"); } }
		 */
		if ((language = node.getLanguage()) != null)
		{
			for (String str : language)
			{
				String slanguage = tagStart + "language" + tagEnd
						+ transformToXMLEntity(str) + endTagStart + "language"
						+ tagEnd;
				update.append(slanguage + "\n");
			}
		}
		if ((publisher = node.getPublisher()) != null)
		{
			for (String str : publisher)
			{
				String spublisher = tagStart + "publisher" + tagEnd
						+ transformToXMLEntity(str) + endTagStart + "publisher"
						+ tagEnd;
				update.append(spublisher + "\n");
			}
		}
		if ((rights = node.getRights()) != null)
		{
			for (String str : rights)
			{
				String srights = tagStart + "rights" + tagEnd
						+ transformToXMLEntity(str) + endTagStart + "rights"
						+ tagEnd;
				update.append(srights + "\n");
			}
		}
		if ((source = node.getSource()) != null)
		{
			for (String str : source)
			{
				String ssource = tagStart + "source" + tagEnd
						+ transformToXMLEntity(str) + endTagStart + "source"
						+ tagEnd;
				update.append(ssource + "\n");
			}
		}
		if ((subject = node.getSubject()) != null)
		{
			for (String str : subject)
			{
				String ssubject = tagStart + "subject" + tagEnd
						+ transformToXMLEntity(str) + endTagStart + "subject"
						+ tagEnd;
				update.append(ssubject + "\n");
			}
		}
		if ((title = node.getTitle()) != null)
		{
			for (String str : title)
			{
				String stitle = tagStart + "title" + tagEnd
						+ transformToXMLEntity(str) + endTagStart + "title"
						+ tagEnd;
				update.append(stitle + "\n");
			}
		}
		if ((type = node.getType()) != null)
		{
			for (String str : type)
			{
				String stype = tagStart + "type" + tagEnd
						+ transformToXMLEntity(str) + endTagStart + "type"
						+ tagEnd;
				update.append(stype + "\n");
			}
		}

		try
		{
			String result = preamble + update.toString() + fazit;
			// DataHandler dh = new DataHandler(new ByteArrayDataSource(
			// result.getBytes("UTF-8"), "text/xml"));
			// fedoraManager.modifyDatastreamByValue(node.getPID(), "DC", null,
			// "Dublin Core Record for this object", "text/xml",
			// "http://www.openarchives.org/OAI/2.0/oai_dc/", dh,
			// "DISABLED", null, "UPDATE OF DC STREAM", false);
			new ModifyDatastream(node.getPID(), "DC").mimeType("text/xml")
					.formatURI("http://www.openarchives.org/OAI/2.0/oai_dc/")
					.versionable(true).content(result).execute();

		}
		catch (FedoraClientException e)
		{
			throw new ArchiveException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: Loads files up to a fedora directory for ingesting
	 * </p>
	 * 
	 * @return
	 * @throws IOException
	 */
	private Uploader getUploader() throws IOException
	{
		URL url = new URL(host);
		return new Uploader(url.getProtocol(), url.getHost(), url.getPort(),
				url.getPath(), user, passwd);
	}

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: Creates new Rels-Ext datastream in object 'pid'
	 * </p>
	 * 
	 * @param pid
	 *            of the object
	 * 
	 */
	private void createFedoraXmlForRelsExt(String pid)
	{
		// System.out.println("Create new REL-EXT "+pid);
		try
		{
			// String state = "A";
			// String label = "RDF Statements about this object";
			// String mimeType = "application/rdf+xml";
			// String formatURI = "info:fedora/fedora-system:FedoraRELSExt-1.0";
			// ArrayOfString altIDs = null;
			// String checksumType = null;
			// String location = null;
			// boolean versionable = true;

			String initialContent = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rel=\"info:fedora/fedora-system:def/relations-external#\">"
					+ "    <rdf:Description rdf:about=\"info:fedora/"
					+ pid
					+ "\">" + "    </rdf:Description>" + "</rdf:RDF>";
			ByteArrayInputStream is = new ByteArrayInputStream(
					initialContent.getBytes());

			Uploader uploader = getUploader();
			String location = uploader.upload(is);

			// @SuppressWarnings("unused")
			// String newID = fedoraManager.addDatastream(pid, "RELS-EXT",
			// altIDs,
			// label,
			// versionable, // DEFAULT_VERSIONABLE
			// mimeType, formatURI, location, "X", state, checksumType,
			// null, // checksum type and checksum
			// "Try to add RELS-EXT datastream");
			new ModifyDatastream(pid, "RELS-EXT")
					.mimeType("application/rdf+xml")
					.formatURI("info:fedora/fedora-system:FedoraRELSExt-1.0")
					.versionable(true).dsLocation(location).execute();

		}
		catch (RemoteException e)
		{
			throw new ArchiveException(e.getMessage(), e);
		}
		catch (Exception e)
		{
			throw new ArchiveException(e.getMessage(), e);
		}
	}

	// private byte[] serializeNode(String pid, String label,
	// XMLBuilder xmlBuilder, OBJECT_TYPE type)
	// {
	//
	// try
	// {
	// String objXML = xmlBuilder.createObjectXML(type, pid, label);
	//
	// objXML = setOwnerToXMLString(objXML);
	//
	// return objXML.getBytes("UTF-8");
	// }
	// catch (RemoteException e)
	// {
	//
	// e.printStackTrace();
	// }
	// catch (UnsupportedEncodingException e)
	// {
	//
	// e.printStackTrace();
	// }
	//
	// return null;
	// }

	private void createFedoraXMLForContentModels(Node node)
	{
		StringBuffer infoStream = new StringBuffer();
		Vector<ContentModel> models = node.getContentModels();

		infoStream.append("<ContentModelInfoStream>");
		for (ContentModel model : models)
		{
			infoStream.append("<ContentModel>");
			infoStream.append("<ContentModelPid>" + model.getContentModelPID()
					+ "</ContentModelPid>");
			infoStream.append("<ServiceDefPid>"
					+ model.getServiceDefinitionPID() + "</ServiceDefPid>");
			infoStream.append("<ServiceDepPid>"
					+ model.getServiceDeploymentPID() + "</ServiceDepPid>");
			infoStream.append("<PrescribedDSs>");

			Vector<String> psids = model.getPrescribedDSIds();
			Vector<String> uris = model.getPrescribedDSformatURIs();
			Vector<String> mimes = model.getPrescribedDSMimeTypes();
			for (int i = 0; i < psids.size(); i++)
			{
				infoStream.append("<PrescribedDS>");
				infoStream.append("<dsid>" + psids.get(i) + "</dsid>");
				infoStream.append("<uri>" + uris.get(i) + "</uri>");
				infoStream.append("<mimeType>" + mimes.get(i) + "</mimeType>");
				infoStream.append("</PrescribedDS>");
			}
			infoStream.append("</PrescribedDSs>");

			Vector<String> names = model.getMethodNames();
			Vector<String> locs = model.getMethodLocations();
			infoStream.append("<Methods>");
			for (int i = 0; i < names.size(); i++)
			{
				infoStream.append("<Method>");
				infoStream.append("<name>" + names.get(i) + "</name>");
				infoStream.append("<serviceLocation>" + locs.get(i)
						+ "</serviceLocation>");
				infoStream.append("</Method>");
			}
			infoStream.append("</Methods>");

			infoStream.append("</ContentModel>");
		}
		infoStream.append("</ContentModelInfoStream>");

		try
		{
			// System.out.println(node.getPID());
			// DataHandler dh = new DataHandler(new
			// ByteArrayDataSource(infoStream
			// .toString().getBytes("UTF-8"), "text/xml"));
			// fedoraManager.modifyDatastreamByValue(node.getPID(),
			// "HBZCMInfoStream", null, "HBZCMInfoStream", "text/xml",
			// "info:hbz/hbz-system:HBZContentModelInfoStream1.0", dh,
			// "DISABLED", null, "UPDATE OF HBZCMInfoStream", false);

			new ModifyDatastream(node.getPID(), "HBZCMInfoStream")
					.mimeType("text/xml")
					.formatURI(
							"info:hbz/hbz-system:HBZContentModelInfoStream1.0")
					.versionable(true).dsLocation(infoStream.toString())
					.execute();
		}

		catch (FedoraClientException e)
		{
			throw new ArchiveException(e.getMessage(), e);
		}

	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param user2
	 * @param objXML
	 */
	private String setOwnerToXMLString(String objXML)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document doc = docBuilder.parse(new BufferedInputStream(
					new ByteArrayInputStream(objXML.getBytes())));
			Element root = doc.getDocumentElement();
			root.normalize();

			NodeList properties = root.getElementsByTagName("foxml:property");
			for (int i = 0; i < properties.getLength(); i++)
			{
				Element n = (Element) properties.item(i);
				String attribute = n.getAttribute("NAME");

				if (attribute
						.compareTo("info:fedora/fedora-system:def/model#ownerId") == 0)
				{

					n.setAttribute("VALUE", user);
					// String a = n.getAttribute("VALUE");

					break;
				}
			}

			try
			{
				doc.normalize();
				Source source = new DOMSource(doc);
				StringWriter stringWriter = new StringWriter();
				Result result = new StreamResult(stringWriter);

				TransformerFactory fac = TransformerFactory.newInstance();
				Transformer transformer = fac.newTransformer();
				transformer.transform(source, result);

				return stringWriter.toString();

			}
			catch (TransformerConfigurationException e)
			{
				e.printStackTrace();
			}
			catch (TransformerException e)
			{
				e.printStackTrace();
			}

		}
		catch (ParserConfigurationException e)
		{

			e.printStackTrace();
		}
		catch (SAXException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param textContent
	 * @return
	 */
	private String transformFromXMLEntity(String textContent)
	{
		return textContent.replaceAll("[&]amp;", "&");
	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param string
	 * @return
	 */
	private String transformToXMLEntity(String string)
	{
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(
				string);
		char character = iterator.current();
		while (character != CharacterIterator.DONE)
		{
			if (character == '<')
			{
				result.append("&lt;");
			}
			else if (character == '>')
			{
				result.append("&gt;");
			}
			else if (character == '\"')
			{
				result.append("&quot;");
			}
			else if (character == '\'')
			{
				result.append("&#039;");
			}
			else if (character == '&')
			{
				result.append("&amp;");
			}
			else
			{

				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();

	}

	private List<String> findPidsRdf(String rdfQuery, String queryFormat)
	{
		InputStream stream = findTriples(rdfQuery, FedoraVocabulary.SPO,
				FedoraVocabulary.N3);

		List<String> resultVector = new Vector<String>();
		RepositoryConnection con = null;
		Repository myRepository = new SailRepository(new MemoryStore());

		try
		{

			myRepository.initialize();

			con = myRepository.getConnection();
			String baseURI = "";

			con.add(stream, baseURI, RDFFormat.N3);

			RepositoryResult<Statement> statements = con.getStatements(null,
					null, null, true);

			while (statements.hasNext())
			{
				Statement st = statements.next();
				String str = removeUriPrefix(st.getSubject().stringValue());

				resultVector.add(str);

			}
			return resultVector;

		}
		catch (RepositoryException e)
		{

			throw new ArchiveException("An unknown exception occured.", e);
		}
		catch (RDFParseException e)
		{

			throw new ArchiveException("An unknown exception occured.", e);
		}
		catch (IOException e)
		{

			throw new ArchiveException("An unknown exception occured.", e);
		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (RepositoryException e)
				{
					throw new ArchiveException("Can not close stream.", e);
				}
			}
		}
	}

	private List<String> findPidsSimple(String rdfQuery)
	{
		FedoraCredentials credentials;
		try
		{

			credentials = new FedoraCredentials(host, user, passwd);
			com.yourmediashelf.fedora.client.FedoraClient fedora = new com.yourmediashelf.fedora.client.FedoraClient(
					credentials);

			FedoraRequest.setDefaultClient(fedora);

			FindObjectsResponse response = fedora.findObjects().maxResults(50)
					.resultFormat("xml").pid().terms(rdfQuery).execute();
			if (!response.hasNext())
				return response.getPids();
			List<String> result = response.getPids();
			while (response.hasNext())
			{

				response = fedora.findObjects().pid()
						.sessionToken(response.getToken()).maxResults(50)
						.resultFormat("xml").execute();
				result.addAll(response.getPids());

			}

			return result;
		}
		catch (MalformedURLException e)
		{
			throw new ArchiveException("An unknown exception occured.", e);
		}
		catch (FedoraClientException e)
		{
			throw new ArchiveException("An unknown exception occured.", e);
		}

	}
}