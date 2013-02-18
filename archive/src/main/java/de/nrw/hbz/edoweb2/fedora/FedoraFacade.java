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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.fcrepo.client.FedoraClient;
import org.fcrepo.client.Uploader;
import org.fcrepo.client.utility.AutoPurger;
import org.fcrepo.client.utility.ingest.AutoIngestor;
import org.fcrepo.client.utility.ingest.XMLBuilder;
import org.fcrepo.client.utility.ingest.XMLBuilder.OBJECT_TYPE;
import org.fcrepo.common.Constants;
import org.fcrepo.server.access.FedoraAPIAMTOM;
import org.fcrepo.server.management.FedoraAPIMMTOM;
import org.fcrepo.server.types.gen.ArrayOfString;
import org.fcrepo.server.types.gen.Datastream;
import org.fcrepo.server.types.mtom.gen.MIMETypedStream;
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
import com.yourmediashelf.fedora.client.request.FedoraRequest;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;

import de.nrw.hbz.edoweb2.datatypes.ContentModel;
import de.nrw.hbz.edoweb2.datatypes.Link;
import de.nrw.hbz.edoweb2.datatypes.Node;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 */
public class FedoraFacade implements FedoraInterface, Constants
{

	/**
	 * TODO: This class is way too long. Cut version independent stuff from
	 * actual fedora calls!
	 * 
	 * */

	/**
	 * TODO Use the mediashelf client for all operations
	 */
	public final static String TYPE_SIMPLE = "simple";
	public static final String TYPE_SPO = "spo";
	public static final String TYPE_SPARQL = "sparql";
	public static final String FORMAT_N3 = "N3";
	private FedoraAPIAMTOM fedoraAccess;
	private FedoraAPIMMTOM fedoraManager;
	private FedoraClient fedoraClient;

	private final String host;
	private final String user;
	private final String passwd;
	String objecUrl = "info:fedora";

	public FedoraFacade(String host, String aUser, String aPassword)
	{
		this.user = aUser;
		this.passwd = aPassword;
		this.host = host;

		try
		{

			fedoraClient = new FedoraClient(host, user, passwd);

			fedoraAccess = fedoraClient.getAPIAMTOM();
			fedoraManager = fedoraClient.getAPIMMTOM();

		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: Creates a FedoraObject with properties of node
	 * </p>
	 * 
	 * @param node
	 *            the local POJO which is serialized as Fedora object
	 * @throws IOException
	 * @throws RemoteException
	 */
	@Override
	public void createNode(Node node) throws RemoteException, IOException
	{

		byte[] foxmlObject = serializeNode(node.getPID(), node.getLabel(),
				new XMLBuilder(fedoraManager),
				XMLBuilder.OBJECT_TYPE.dataObject);
		AutoIngestor.ingestAndCommit(this.fedoraAccess, fedoraManager,
				new ByteArrayInputStream(foxmlObject), FOXML1_1.uri,
				"Created with HBZ Webservice");

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
		createRelsExt(node);
	}

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: The corresponding fedora object will be read into a new node
	 * </p>
	 * 
	 * @param node
	 *            It is assumed that node has the correct PID of an existing
	 *            Node which will be replaced by this method
	 * @throws RemoteException
	 */
	@SuppressWarnings("static-access")
	@Override
	public Node readNode(String nodePid) throws RemoteException
	{
		if (!nodeExists(nodePid))
			return null;
		Node node = new Node();
		node.setPID(nodePid);
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

			node.setLabel(fedora.getObjectProfile(nodePid).execute().getLabel());

		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
		return node;
	}

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: The corresponding fedora object will be updated
	 * (overwritten)
	 * </p>
	 * 
	 * @param node
	 *            It is assumed that node has the correct PID of an existing
	 *            Node which will be replaced by this method
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public void updateNode(Node node) throws RemoteException,
			UnsupportedEncodingException
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
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> findPids(String rdfQuery, String queryFormat)
	{

		if (queryFormat.compareTo(TYPE_SIMPLE) == 0)
		{

			return findPidsSimple(rdfQuery);
		}

		else
		{
			return findPidsRdf(rdfQuery, queryFormat);
		}

	}

	private List<String> findPidsRdf(String rdfQuery, String queryFormat)
	{
		InputStream stream = findTriples(rdfQuery, FedoraFacade.TYPE_SPO,
				FedoraFacade.FORMAT_N3);

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

			e.printStackTrace();
		}
		catch (RDFParseException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
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
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@SuppressWarnings("static-access")
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
			e.printStackTrace();
		}
		catch (FedoraClientException e)
		{
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: If the object 'pid' has a datastream with 'datastreamId' the
	 * method returns true.
	 * </p>
	 * 
	 * @param pid
	 *            of the object
	 * @param datastreamId
	 *            of the objects datastream
	 * @return true ig datastream exists
	 */
	@Override
	public boolean dataStreamExists(String pid, String datastreamId)
	{
		Datastream datastream = fedoraManager.getDatastream(pid, datastreamId,
				null);
		if (datastream == null)
			return false;
		return true;
	}

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: Delete all relations from object with 'pid' to object with
	 * 'rootPID'
	 * </p>
	 * 
	 * @param pid
	 *            the relations of this one will be deleted
	 * @param rootPID
	 *            will no longer be referenced
	 * @throws Exception
	 */
	// @Override
	// public void deleteRelationsByObjects(String pid, String rootPID)
	// throws Exception
	// {
	// String[] predicates = null;
	// try
	// {
	// predicates = findPredicates(pid, rootPID);
	// }
	// catch (Exception e)
	// {
	// return;
	// }
	// for (String pred : predicates)
	// {
	// fedoraManager.purgeRelationship(pid2pred(pid), pred,
	// pid2pred(rootPID), false, null);
	//
	// }
	//
	// }

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: A new pid in the "test" namespace is generated
	 * </p>
	 * 
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public String getPid(String namespace) throws RemoteException
	{
		List<String> pids = fedoraManager.getNextPID(new BigInteger("1"),
				namespace);
		return pids.get(0);
	}

	@Override
	public String[] getPids(String namespace, int number)
			throws RemoteException
	{
		List<String> pids = fedoraManager.getNextPID(
				new BigInteger("" + number), namespace);
		String[] arr = new String[pids.size()];
		return pids.toArray(arr);
	}

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: The fedora object will be deleted
	 * </p>
	 * 
	 * @param rootPID
	 *            identifier of a fedora object
	 */
	@Override
	public void deleteNode(String rootPID)
	{

		AutoPurger purger;
		try
		{
			purger = new AutoPurger(fedoraManager);
			purger.purge(rootPID, "delete"); // Change: delete "false" as third
												// param
		}
		catch (MalformedURLException e)
		{
			// e.printStackTrace();
		}
		catch (ServiceException e)
		{
			// e.printStackTrace();
		}
		catch (RemoteException e)
		{
			// e.printStackTrace();
		}
		catch (IOException e)
		{
			// e.printStackTrace();
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}

	}

	@Override
	public boolean nodeExists(String pid)
	{
		try
		{
			fedoraManager.getObjectXML(pid);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
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
					fedoraManager.addRelationship(pid,
							curHBZLink.getPredicate(), curHBZLink.getObject(),
							curHBZLink.isLiteral(), null);
				}
				else
				{
					// System.out.println("NOT isLiteral");
					fedoraManager.addRelationship(pid,
							curHBZLink.getPredicate(),
							addUriPrefix(curHBZLink.getObject()),
							curHBZLink.isLiteral(), null);
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

			String state = "A";
			String formatURI = "";
			ArrayOfString altIDs = null;
			String checksumType = null;
			String location = null;
			boolean versionable = true;

			Uploader uploader = getUploader();
			location = uploader.upload(new BufferedInputStream(
					new FileInputStream(node.getUploadFile())));
			String newID = fedoraManager.addDatastream(node.getPID(),
					node.getFileName(), altIDs,
					node.getFileName(),
					versionable, // DEFAULT_VERSIONABLE
					node.getMimeType(), formatURI, location, "M", state,
					checksumType, null, // checksum type and checksum
					"A datastream");
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
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void createMetadataStream(Node node)
	{

		try
		{

			String state = "A";
			String formatURI = "";
			ArrayOfString altIDs = null;
			String checksumType = null;
			String location = null;
			boolean versionable = true;

			Uploader uploader = getUploader();
			location = uploader.upload(new BufferedInputStream(
					new FileInputStream(node.getMetadataFile())));
			String newID = fedoraManager.addDatastream(node.getPID(),
					"metadata", altIDs, "metadata",
					versionable, // DEFAULT_VERSIONABLE
					"text/turtle", formatURI, location, "M", state,
					checksumType, null, // checksum type and checksum
					"metadata");
			node.setMetadataUrl(new URL(this.host + "/objects/" + node.getPID()
					+ "/datastreams/" + newID + "/content"));
			Link link = new Link();
			link.setObject(node.getMetadataFile(), true);
			link.setPredicate(HAS_METADATASTREAM);
			node.addRelation(link);

			link = new Link();
			link.setObject("text/turtle", true);
			link.setPredicate(METADATASTREAM_MIME);
			node.addRelation(link);

		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void updateManagedStream(Node node)
	{

		try
		{
			String formatURI = "";
			ArrayOfString altIDs = null;
			String checksumType = null;
			String state = "A";
			String location = null;
			boolean versionable = true;

			Uploader uploader = getUploader();
			location = uploader.upload(new BufferedInputStream(
					new FileInputStream(node.getUploadFile())));

			String newID = null;
			if (dataStreamExists(node.getPID(), node.getFileName()))
			{
				newID = fedoraManager.modifyDatastreamByReference(
						node.getPID(), node.getFileName(), altIDs,
						node.getFileName(), node.getMimeType(), formatURI,
						location, checksumType, null, "", true);
			}
			else
			{
				newID = fedoraManager.addDatastream(node.getPID(),
						node.getFileName(), altIDs,
						node.getFileName(),
						versionable, // DEFAULT_VERSIONABLE
						node.getMimeType(), formatURI, location, "M", state,
						checksumType, null, // checksum type and checksum
						"A datastream");
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
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void updateMetadataStream(Node node)
	{
		try
		{
			String state = "A";
			String formatURI = "";
			ArrayOfString altIDs = null;
			String checksumType = null;
			String location = null;
			boolean versionable = true;

			Uploader uploader = getUploader();
			location = uploader.upload(new BufferedInputStream(
					new FileInputStream(node.getMetadataFile())));
			String newID = null;
			if (dataStreamExists(node.getPID(), "metadata"))
			{
				newID = fedoraManager.modifyDatastreamByReference(
						node.getPID(), "metadata", altIDs, "metadata",
						"text/turtle", formatURI, location, checksumType, null,
						"", true);
			}
			else
			{
				// String newID =
				newID = fedoraManager.addDatastream(node.getPID(), "metadata",
						altIDs, "metadata",
						versionable, // DEFAULT_VERSIONABLE
						"text/turtle", formatURI, location, "M", state,
						checksumType, null, // checksum type and checksum
						"metadata");
			}
			node.setDataUrl(new URL(host + "/objects/" + node.getPID()
					+ "/datastreams/" + newID + "/content"));
			Link link = new Link();
			link.setObject(node.getMetadataFile(), true);
			link.setPredicate(HAS_METADATASTREAM);
			node.addRelation(link);

			link = new Link();
			link.setObject("text/turtle", true);
			link.setPredicate(METADATASTREAM_MIME);
			node.addRelation(link);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		catch (RemoteException e)
		{
			// Model is probably already there
			// message: ... still exists ...
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

	private void createContentModel(ContentModel cm) throws RemoteException,
			IOException
	{
		// String state = "A";

		String foCMPid = cm.getContentModelPID();
		String foSDefPid = cm.getServiceDefinitionPID();
		String foSDepPid = cm.getServiceDeploymentPID();

		// Create CM
		byte[] cmFoxmlObject = serializeNode(foCMPid, "Content Model",
				new XMLBuilder(fedoraManager),
				XMLBuilder.OBJECT_TYPE.contentModel);
		AutoIngestor.ingestAndCommit(this.fedoraAccess, fedoraManager,
				new ByteArrayInputStream(cmFoxmlObject), FOXML1_1.uri,
				"Created with HBZ Webservice");

		// Create SDef
		byte[] sDefFoxmlObject = serializeNode(foSDefPid, "ServiceDefinition",
				new XMLBuilder(fedoraManager),
				XMLBuilder.OBJECT_TYPE.serviceDefinition);
		AutoIngestor.ingestAndCommit(this.fedoraAccess, fedoraManager,
				new ByteArrayInputStream(sDefFoxmlObject), FOXML1_1.uri,
				"Created with HBZ Webservice");

		// Create SDep
		byte[] sDepFoxmlObject = serializeNode(foSDepPid, "ServiceDeployment",
				new XMLBuilder(fedoraManager),
				XMLBuilder.OBJECT_TYPE.serviceDeployment);
		AutoIngestor.ingestAndCommit(this.fedoraAccess, fedoraManager,
				new ByteArrayInputStream(sDepFoxmlObject), FOXML1_1.uri,
				"Created with HBZ Webservice");

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

		DataHandler dh = new DataHandler(new ByteArrayDataSource(
				getDsCompositeModel(cm).getBytes("UTF-8"), "text/xml"));

		fedoraManager
				.modifyDatastreamByValue(
						foCMPid,
						de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_COMPOSITE_MODEL,
						null,
						"DS-Composite-Stream",
						"text/xml",
						de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_COMPOSITE_MODEL_URI,
						dh, "DISABLED", null, "UPDATE OF METHODMAP STREAM",
						false);

		// Add Methodmap to sDef
		dh = new DataHandler(new ByteArrayDataSource(getMethodMap(cm).getBytes(
				"UTF-8"), "text/xml"));
		fedoraManager.modifyDatastreamByValue(foSDefPid, DS_METHODMAP, null,
				"Methodmap-Stream", "text/xml", DS_METHODMAP_URI, dh,
				"DISABLED", null, "UPDATE OF METHODMAP STREAM", false);

		// Add Methodmap to sDep
		dh = new DataHandler(new ByteArrayDataSource(getMethodMapToWsdl(cm)
				.getBytes("UTF-8"), "text/xml"));
		fedoraManager.modifyDatastreamByValue(foSDepPid, DS_METHODMAP_WSDL,
				null, "Methodmap-Stream", "text/xml", DS_METHODMAP_WSDL_URI,
				dh, "DISABLED", null, "UPDATE OF METHODMAP STREAM", false);

		// Add DSINPUTSPEC to sDep
		dh = new DataHandler(new ByteArrayDataSource(getDSInputSpec().getBytes(
				"UTF-8"), "text/xml"));
		fedoraManager.modifyDatastreamByValue(foSDepPid, DS_INPUTSPEC, null,
				"DSINPUTSPEC-Stream", "text/xml", DS_INPUTSPEC_URI, dh,
				"DISABLED", null, "UPDATE OF DSINPUTSPEC STREAM", false);

		// Add WSDL to sDep
		// System.out.println(foSDepPid);
		// System.out.println(cm.getWsdl());
		dh = new DataHandler(new ByteArrayDataSource(getWsdl(cm).getBytes(
				"UTF-8"), "text/xml"));
		fedoraManager.modifyDatastreamByValue(foSDepPid, DS_WSDL, null,
				"WSDL-Stream", "text/xml", DS_WSDL_URI, dh, "DISABLED", null,
				"UPDATE OF WSDL STREAM", false);
	}

	private void readDcToNode(Node node) throws RemoteException
	{
		MIMETypedStream ds = fedoraAccess.getDatastreamDissemination(
				node.getPID(), "DC", null);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setExpandEntityReferences(false);
		try
		{
			DocumentBuilder docBuilder = factory.newDocumentBuilder();

			Document doc = docBuilder.parse(new BufferedInputStream(ds
					.getStream().getInputStream())); // TODO Correct? UTF-8?
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
	 */
	private void readRelsExt(Node node)
	{

		try
		{
			MIMETypedStream ds = fedoraAccess.getDatastreamDissemination(
					node.getPID(), "RELS-EXT", null);

			Repository myRepository = new SailRepository(new MemoryStore());
			myRepository.initialize();

			RepositoryConnection con = myRepository.getConnection();
			String baseURI = "";

			try
			{
				ValueFactory f = myRepository.getValueFactory();
				URI objectId = f.createURI("info:fedora/" + node.getPID());
				con.add(new BufferedInputStream(ds.getStream().getInputStream()),
						baseURI, RDFFormat.RDFXML);
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

						String object = link.getObject();
						try
						{
							if (object == null)
								throw new URISyntaxException(" ", "Is an Null",
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
					e.printStackTrace();
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

			e.printStackTrace();
		}
		catch (RemoteException e)
		{

			e.printStackTrace();
		}
		catch (RDFParseException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}

	}

	private void readContentModels(Node node) throws RemoteException
	{

		MIMETypedStream ds = fedoraAccess.getDatastreamDissemination(
				node.getPID(), "HBZCMInfoStream", null);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document doc = docBuilder.parse(new BufferedInputStream(ds
					.getStream().getInputStream()));
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
						fedoraManager.addRelationship(pid,
								curHBZLink.getPredicate(),
								curHBZLink.getObject(), true, null);
					}
					else
					{
						// System.out.println("NOT isLiteral");

						fedoraManager.addRelationship(pid,
								curHBZLink.getPredicate(),
								curHBZLink.getObject(), false, null);
					}
				}
				catch (Exception e)
				{
					// System.out.println("Try as Literal:");
					try
					{
						fedoraManager.addRelationship(pid,
								curHBZLink.getPredicate(),
								curHBZLink.getObject(), true, null);
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
		try
		{
			Node old = readNode(pid);
			links.removeAll(old.getRelsExt());
		}
		catch (RemoteException e)
		{

		}
		updateRelsExt(pid, links);

	}

	private void updateDc(Node node) throws RemoteException,
			UnsupportedEncodingException
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

		String result = preamble + update.toString() + fazit;
		DataHandler dh = new DataHandler(new ByteArrayDataSource(
				result.getBytes("UTF-8"), "text/xml"));
		fedoraManager.modifyDatastreamByValue(node.getPID(), "DC", null,
				"Dublin Core Record for this object", "text/xml",
				"http://www.openarchives.org/OAI/2.0/oai_dc/", dh, "DISABLED",
				null, "UPDATE OF DC STREAM", false);
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
			String state = "A";
			String label = "RDF Statements about this object";
			String mimeType = "application/rdf+xml";
			String formatURI = "info:fedora/fedora-system:FedoraRELSExt-1.0";
			ArrayOfString altIDs = null;
			String checksumType = null;
			String location = null;
			boolean versionable = true;

			String initialContent = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rel=\"info:fedora/fedora-system:def/relations-external#\">"
					+ "    <rdf:Description rdf:about=\"info:fedora/"
					+ pid
					+ "\">" + "    </rdf:Description>" + "</rdf:RDF>";
			ByteArrayInputStream is = new ByteArrayInputStream(
					initialContent.getBytes());

			Uploader uploader = getUploader();
			location = uploader.upload(is);

			@SuppressWarnings("unused")
			String newID = fedoraManager.addDatastream(pid, "RELS-EXT", altIDs,
					label,
					versionable, // DEFAULT_VERSIONABLE
					mimeType, formatURI, location, "X", state, checksumType,
					null, // checksum type and checksum
					"Try to add RELS-EXT datastream");

		}
		catch (RemoteException e)
		{

		}
		catch (Exception e)
		{

		}
	}

	private byte[] serializeNode(String pid, String label,
			XMLBuilder xmlBuilder, OBJECT_TYPE type)
	{

		try
		{
			String objXML = xmlBuilder.createObjectXML(type, pid, label);

			objXML = setOwnerToXMLString(objXML);

			return objXML.getBytes("UTF-8");
		}
		catch (RemoteException e)
		{

			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{

			e.printStackTrace();
		}

		return null;
	}

	private void createFedoraXMLForContentModels(Node node)
	{

		try
		{
			String state = "A";
			String label = "ContentModelInformationStream";
			String mimeType = "application/rdf+xml";
			String formatURI = "info:hbz/hbz-system:HBZContentModelInfoStream1.0";
			ArrayOfString altIDs = null;
			String checksumType = null;
			String location = null;
			boolean versionable = true;

			Uploader uploader = getUploader();
			String initialContent = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rel=\"info:fedora/fedora-system:def/relations-external#\">"
					+ "    <rdf:Description rdf:about=\"info:fedora/"
					+ node.getPID()
					+ "\">"
					+ "    </rdf:Description>"
					+ "</rdf:RDF>";
			ByteArrayInputStream is = new ByteArrayInputStream(
					initialContent.getBytes());

			location = uploader.upload(is);

			// String newID =
			fedoraManager.addDatastream(node.getPID(), "HBZCMInfoStream",
					altIDs, label,
					versionable, // DEFAULT_VERSIONABLE
					mimeType, formatURI, location, "X", state, checksumType,
					null, // checksum type and checksum
					"Add HBZCMInfoStream");

		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return;
		}

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
			DataHandler dh = new DataHandler(new ByteArrayDataSource(infoStream
					.toString().getBytes("UTF-8"), "text/xml"));
			fedoraManager.modifyDatastreamByValue(node.getPID(),
					"HBZCMInfoStream", null, "HBZCMInfoStream", "text/xml",
					"info:hbz/hbz-system:HBZContentModelInfoStream1.0", dh,
					"DISABLED", null, "UPDATE OF HBZCMInfoStream", false);
		}
		catch (UnsupportedEncodingException e)
		{

			e.printStackTrace();
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
}