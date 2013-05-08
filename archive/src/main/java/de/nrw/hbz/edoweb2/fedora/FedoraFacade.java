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

import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_CONTENT_TYPE;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.CM_CONTENTMODEL;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_COMPOSITE_MODEL;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.DS_COMPOSITE_MODEL_URI;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openrdf.model.Statement;
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

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.FedoraRequest;
import com.yourmediashelf.fedora.client.request.GetDatastreamDissemination;
import com.yourmediashelf.fedora.client.request.GetNextPID;
import com.yourmediashelf.fedora.client.request.GetObjectProfile;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.ListDatastreams;
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.request.PurgeObject;
import com.yourmediashelf.fedora.client.request.RiSearch;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.GetNextPIDResponse;
import com.yourmediashelf.fedora.client.response.GetObjectProfileResponse;
import com.yourmediashelf.fedora.client.response.IngestResponse;
import com.yourmediashelf.fedora.client.response.ListDatastreamsResponse;
import com.yourmediashelf.fedora.generated.access.DatastreamType;
import com.yourmediashelf.fedora.generated.management.PidList;

import de.nrw.hbz.edoweb2.archive.exceptions.ArchiveException;
import de.nrw.hbz.edoweb2.archive.exceptions.NodeNotFoundException;
import de.nrw.hbz.edoweb2.datatypes.ContentModel;
import de.nrw.hbz.edoweb2.datatypes.Link;
import de.nrw.hbz.edoweb2.datatypes.Node;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 */
public class FedoraFacade implements FedoraInterface
{

	private final String host;
	private final String user;
	private final String passwd;

	Utils utils = null;

	ContentModelBuilder cmBuilder = new ContentModelBuilder();

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
		utils = new Utils(host, aUser);
		this.user = aUser;
		this.host = host;
		this.passwd = aPassword;
		try
		{
			FedoraCredentials credentials = new FedoraCredentials(host, aUser,
					aPassword);
			FedoraClient fedora = new com.yourmediashelf.fedora.client.FedoraClient(
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

			IngestResponse response = new Ingest(node.getPID()).label(
					node.getLabel()).execute();

			utils.updateDc(node);
			createContentModels(node);

			if (node.getUploadFile() != null)
			{
				utils.createManagedStream(node);

			}
			if (node.getMetadataFile() != null)
			{
				utils.createMetadataStream(node);

			}

			Link link = new Link();
			link.setObject(node.getContentType(), true);
			link.setPredicate(REL_CONTENT_TYPE);

			node.addRelation(link);

			utils.createRelsExt(node);
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		node.setNamespace(pid.substring(0, pid.indexOf(':')));

		try
		{
			utils.readDcToNode(node);
			utils.readRelsExt(node);
			// TODO Fix me
			// readContentModels(node);
			GetObjectProfileResponse prof = new GetObjectProfile(pid).execute();

			node.setLabel(prof.getLabel());
			node.setLastModified(prof.getLastModifiedDate());

		}
		catch (FedoraClientException e)
		{
			throw new ArchiveException("An unknown exception occured.", e);
		}
		catch (RemoteException e)
		{
			throw new ArchiveException("An unknown exception occured.", e);
		}

		try
		{
			FedoraResponse response = new GetDatastreamDissemination(pid,
					"data").download(true).execute();
			node.setMimeType(response.getType());

			node.setDataUrl(new URL(host + "/objects/" + pid
					+ "/datastreams/data/content"));

			node.setMetadataUrl(new URL(host + "/objects/" + pid
					+ "/datastreams/metadata/content"));

		}
		catch (MalformedURLException e)
		{

		}
		catch (FedoraClientException e)
		{

		}

		return node;
	}

	@Override
	public void updateNode(Node node)
	{
		utils.updateDc(node);
		// updateContentModels(node);

		if (node.getUploadFile() != null)
		{

			utils.updateManagedStream(node);
		}

		if (node.getMetadataFile() != null)
		{
			utils.updateMetadataStream(node);
		}
		updateRelsExt(node);

	}

	@Override
	public List<String> findPids(String rdfQuery, String queryFormat)
	{

		if (queryFormat.compareTo(FedoraVocabulary.SIMPLE) == 0)
		{

			return utils.findPidsSimple(rdfQuery);
		}

		else
		{
			return findPidsRdf(rdfQuery, queryFormat);
		}

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
		return utils.nodeExists(pid);
	}

	@Override
	public void updateContentModel(ContentModel cm)
	{
		if (nodeExists(cm.getContentModelPID()))
			deleteNode(cm.getContentModelPID());
		if (nodeExists(cm.getServiceDefinitionPID()))
			deleteNode(cm.getServiceDefinitionPID());
		if (nodeExists(cm.getServiceDeploymentPID()))
			deleteNode(cm.getServiceDeploymentPID());
		try
		{
			createContentModel(cm);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ArchiveException(e.getMessage(), e);
		}
		catch (FedoraClientException e)
		{
			throw new ArchiveException(e.getMessage(), e);
		}
	}

	@Override
	public InputStream findTriples(String query, String queryFormat,
			String outputformat)
	{
		try
		{
			FedoraResponse response = new RiSearch(query).format(outputformat)
					.lang(queryFormat).type("triples").execute();
			return response.getEntityInputStream();
		}
		catch (Exception e)
		{
			throw new ArchiveException(e.getMessage(), e);
		}
	}

	@Override
	public String removeUriPrefix(String pred)
	{
		return utils.removeUriPrefix(pred);
	}

	@Override
	public String addUriPrefix(String pid)
	{

		return utils.addUriPrefix(pid);
	}

	@Override
	public boolean dataStreamExists(String pid, String datastreamId)
	{
		try
		{

			ListDatastreamsResponse response = new ListDatastreams(pid)
					.execute();

			for (DatastreamType ds : response.getDatastreams())
			{
				if (ds.getDsid().compareTo(datastreamId) == 0)
					return true;
			}

		}
		catch (FedoraClientException e)
		{
			return false;
		}
		return false;
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
			utils.createFedoraXmlForRelsExt(pid);
		}
		Vector<Link> links = node.getRelsExt();

		Node old = readNode(pid);
		links.removeAll(old.getRelsExt());

		utils.updateRelsExt(pid, links);

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

	private void createContentModels(Node node)
	{
		Vector<ContentModel> models = node.getContentModels();
		if (models == null)
			return;
		for (ContentModel m : models)
		{
			createContentModel(m, node);
		}
		// cmBuilder.createFedoraXMLForContentModels(node);
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
		link.setObject(
				utils.addUriPrefix(hbzNodeContentModel.getContentModelPID()),
				false);
		node.addRelation(link);

	}

	private void createContentModel(ContentModel cm)
			throws FedoraClientException, UnsupportedEncodingException
	{
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
		cmHBZLink1.setObject(utils.addUriPrefix(foSDefPid), false);

		cmHBZLinks.add(cmHBZLink1);

		Link cmHBZLink2 = new Link();
		cmHBZLink2.setPredicate(REL_HAS_MODEL);
		cmHBZLink2.setObject(utils.addUriPrefix(CM_CONTENTMODEL), false);
		cmHBZLinks.add(cmHBZLink2);

		utils.updateRelsExt(foCMPid, cmHBZLinks);

		Vector<Link> sDefHBZLinks = new Vector<Link>();
		Link sDefHBZLink = new Link();
		sDefHBZLink.setPredicate(REL_HAS_MODEL);
		sDefHBZLink.setObject(utils.addUriPrefix(SDEF_CONTENTMODEL), false);
		sDefHBZLinks.add(sDefHBZLink);

		utils.updateRelsExt(foSDefPid, sDefHBZLinks);

		Vector<Link> sDepHBZLinks = new Vector<Link>();
		Link sDepHBZLink1 = new Link();
		sDepHBZLink1.setPredicate(REL_IS_DEPLOYMENT_OF);
		sDepHBZLink1.setObject(utils.addUriPrefix(foSDefPid), false);
		sDepHBZLinks.add(sDepHBZLink1);

		Link sDepHBZLink2 = new Link();
		sDepHBZLink2.setPredicate(REL_IS_CONTRACTOR_OF);
		sDepHBZLink2.setObject(utils.addUriPrefix(foCMPid), false);
		sDepHBZLinks.add(sDepHBZLink2);

		Link sDepHBZLink3 = new Link();
		sDepHBZLink3.setPredicate(REL_HAS_MODEL);
		sDepHBZLink3.setObject(utils.addUriPrefix(SDEP_CONTENTMODEL), false);
		sDepHBZLinks.add(sDepHBZLink3);

		utils.updateRelsExt(foSDepPid, sDepHBZLinks);

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

}