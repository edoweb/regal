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
package de.nrw.hbz.edoweb2.api;

import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VERSION_NAME;
import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VOLUME_NAME;
import static de.nrw.hbz.edoweb2.api.Vocabulary.IS_VERSION;
import static de.nrw.hbz.edoweb2.api.Vocabulary.IS_VOLUME;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_BELONGS_TO_OBJECT;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_RELATED;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.IS_MEMBER_OF;
import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.ITEM_ID;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import de.nrw.hbz.edoweb2.archive.ArchiveFactory;
import de.nrw.hbz.edoweb2.archive.ArchiveInterface;
import de.nrw.hbz.edoweb2.datatypes.ComplexObject;
import de.nrw.hbz.edoweb2.datatypes.Link;
import de.nrw.hbz.edoweb2.datatypes.Node;
import de.nrw.hbz.edoweb2.fedora.FedoraFacade;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class Actions
{
	final static Logger logger = LoggerFactory.getLogger(Actions.class);
	ArchiveInterface archive = null;
	String fedoraExtern = null;
	String culturegraphUrl = null;
	String lobidUrl = null;
	String verbundUrl = null;
	String dataciteUrl = null;
	String baseUrl = null;
	String serverName = null;

	public Actions()
	{
		Properties properties = new Properties();
		try
		{
			properties.load(getClass().getResourceAsStream("/api.properties"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		fedoraExtern = properties.getProperty("fedoraExtern");
		culturegraphUrl = properties.getProperty("culturegraphUrl");
		lobidUrl = properties.getProperty("lobidUrl");
		verbundUrl = properties.getProperty("verbundUrl");
		dataciteUrl = properties.getProperty("dataciteUrl");
		baseUrl = properties.getProperty("baseUrl");
		serverName = properties.getProperty("serverName");
		archive = ArchiveFactory.getArchiveImpl(
				properties.getProperty("fedoraIntern"),
				properties.getProperty("user"),
				properties.getProperty("password"),
				properties.getProperty("sesameStore"));

	}

	// ---------------------OPTIMIZE--------------------------
	public String deleteAll(Vector<String> pids, boolean wait)
	{
		logger.info("Delete All");
		for (String pid : pids)
		{
			try
			{
				archive.deleteComplexObject(pid);
				if (wait)
					waitWorkaround();
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}

		return "deleteAll";
	}

	public String create(ComplexObject object, boolean wait)
			throws RemoteException
	{
		Node node = archive.createComplexObject(object);
		if (wait)
			waitWorkaround();

		return object.getRoot().getPID() + " CREATED!";
	}

	// public String update(String pid, StatusBean status, boolean wait)
	// {
	// try
	// {
	// Node node = archive.readNode(pid);
	// if (node != null)
	// {
	// Vector<String> v = new Vector<String>();
	// v.add(status.visibleFor.toString());
	// node.setRights(v);
	// archive.updateNode(pid, node);
	// if (wait)
	// waitWorkaround();
	// }
	// }
	// catch (RemoteException e)
	// {
	// e.printStackTrace();
	// }
	// return "update";
	// }

	public String delete(String pid, boolean wait)
	{

		try
		{
			archive.deleteComplexObject(pid);
			if (wait)
				waitWorkaround();
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return "delete";
	}

	private void waitWorkaround()
	{
		/*
		 * Workaround START
		 */
		try
		{
			logger.info("Wait 10 sec! Nasty workaround.");
			Thread.sleep(10000);
			logger.info("Stop Waiting! Nasty workaround.");
		}
		catch (InterruptedException e1)
		{

			e1.printStackTrace();
		}
		/*
		 * Workaround END
		 */
	}

	// -------------------------------------------------------

	public Vector<String> findByType(String type)
	{
		Vector<String> pids = new Vector<String>();
		String query = "* <http://purl.org/dc/elements/1.1/type> \"" + type
				+ "\"";
		InputStream stream = archive.findTriples(query, FedoraFacade.TYPE_SPO,
				FedoraFacade.FORMAT_N3);
		String findpid = null;
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
				findpid = st.getSubject().stringValue()
						.replace("info:fedora/", "");
				pids.add(findpid);
			}
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
		return pids;
	}

	// public StatusBean read(String pid)
	// {
	// try
	// {
	// Node object = archive.readObject(pid);
	// return new StatusBean(object);
	// }
	// catch (RemoteException e)
	// {
	// e.printStackTrace();
	// }
	// return null;
	// }

	public Response readData(String pid)
	{
		try
		{
			Node node = archive.readNode(pid);
			if (node != null && node.getDataUrl() != null)
			{
				try
				{
					return Response.temporaryRedirect(
							new java.net.URI(fedoraExtern + "/objects/" + pid
									+ "/datastreams/data/content")).build();
				}
				catch (URISyntaxException e)
				{
					e.printStackTrace();
				}
			}

		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public DCBeanAnnotated readDC(String pid)
	{

		logger.info("Read DC");
		try
		{
			Node node = archive.readNode(pid);
			if (node != null)
				return new DCBeanAnnotated(node);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public Response readMetadata(String pid)
	{
		try
		{
			Node node = archive.readNode(pid);
			if (node != null && node.getMetadataUrl() != null)
			{
				try
				{
					return Response.temporaryRedirect(
							new java.net.URI(fedoraExtern + "/objects/" + pid
									+ "/datastreams/metadata/content")).build();
				}
				catch (URISyntaxException e)
				{
					e.printStackTrace();
				}
			}

		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public String updateData(String pid, UploadDataBean content)
	{
		try
		{
			Node node = archive.readNode(pid);
			if (node != null)
			{
				node.setUploadData(content.path.getPath(), "data", content.mime);
				archive.updateNode(pid, node);
			}
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return "updateData";
	}

	public String updateDC(String pid, DCBeanAnnotated content)
	{
		logger.info("Update DC");
		try
		{
			Node node = archive.readNode(pid);
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
			archive.updateNode(pid, node);

		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}

		return "updateDC";
	}

	public String updateMetadata(String pid, UploadDataBean content)
	{

		try
		{
			Node node = archive.readNode(pid);
			if (node != null)
			{
				node.setMetadataFile(content.path.getPath());
				archive.updateNode(pid, node);
			}
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}

		return "updateMetadata";
	}

	public String findSubject(String rdfQuery)
	{
		String volumePid = null;
		InputStream stream = archive.findTriples(rdfQuery,
				FedoraFacade.TYPE_SPARQL, FedoraFacade.FORMAT_N3);

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
				volumePid = st.getSubject().stringValue()
						.replace("info:fedora/", "");
				break;
			}
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

		return volumePid;
	}

	public Vector<String> findObject(String pid, String pred)
	{
		String query = "<info:fedora/" + pid + "> <" + pred + "> *";
		logger.info(query);
		InputStream stream = archive.findTriples(query, FedoraFacade.TYPE_SPO,
				FedoraFacade.FORMAT_N3);
		Vector<String> findpids = new Vector<String>();
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
				findpids.add(st.getObject().stringValue()
						.replace("info:fedora/", ""));

			}
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
		return findpids;
	}

	public boolean nodeExists(String pid)
	{

		return archive.nodeExists(pid);
	}

	public String addUriPrefix(String pid)
	{
		return archive.addUriPrefix(pid);
	}

	public String getPid(String namespace) throws RemoteException
	{
		return archive.getPids(namespace, 1)[0];
	}

	public String addLinks(String pid, Vector<Link> links)
	{
		try
		{
			Node node = archive.readNode(pid);
			for (Link link : links)
			{
				node.addRelation(link);
			}
			// long start = System.nanoTime();
			archive.updateNode(node.getPID(), node);
			// long elapsed = System.nanoTime() - start;
			// System.out.println("update node duration: " + elapsed);

			return "Links succesfuly added";
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return "FAILED! No links added";
	}

	public String addLink(String pid, Link link)
	{
		Vector<Link> v = new Vector<Link>();
		v.add(link);
		return addLinks(pid, v);
	}

	public String updateLink(String pid, Link link)
	{
		try
		{
			Node node = archive.readNode(pid);
			Vector<Link> links = node.getRelsExt();
			Iterator<Link> iterator = links.iterator();

			while (iterator.hasNext())
			{
				Link l = iterator.next();
				if (l.getPredicate().compareTo(link.getPredicate()) == 0)
				{
					links.remove(l);
				}
			}

			links.add(link);
			node.setRelsExt(links);
			archive.updateNode(node.getPID(), node);
			return "Link succesfuly updated";
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return "FAILED! No links added";
	}

	// public void addChildToParent(String childPid, String parentPid)
	// {
	// try
	// {
	// ComplexObject parent = archive.readComplexObject(parentPid);
	// Node child = archive.readNode(childPid);
	// parent.addChild(new ComplexObjectNode(child));
	// archive.updateComplexObject(parent);
	// }
	// catch (RemoteException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	public void makeOAISet(String pid)
	{
		try
		{
			Node node = archive.readNode(pid);
			if (node.getSubject() != null)
				for (String subject : node.getSubject())
				{
					if (subject.startsWith("ddc"))
					{
						int end = 7;
						if (subject.length() < 7)
							end = subject.length();
						String ddc = subject.subSequence(4, end).toString();
						logger.info("Found ddc: " + ddc);

						String name = ddcmap(ddc);
						String spec = "ddc:" + ddc;
						String namespace = "oai";
						String oaipid = namespace + ":" + ddc;
						if (!this.nodeExists(oaipid))
						{
							createOAISet(name, spec, oaipid);
						}
						linkObjectToOaiSet(node, spec, oaipid);
					}

				}
			if (node.getType() != null)
				for (String type : node.getType())
				{
					if (type.startsWith("content-type"))
					{
						String docType = type.substring(9);
						logger.info("Found docType: " + docType);

						String name = docmap(docType);
						String spec = TypeType.contentType.toString() + ":"
								+ docType;
						String namespace = "oai";
						String oaipid = namespace + ":" + docType;
						if (!this.nodeExists(oaipid))
						{
							createOAISet(name, spec, oaipid);
						}
						linkObjectToOaiSet(node, spec, oaipid);
					}
				}

			String name = "open_access";
			String spec = "open_access";
			String namespace = "oai";
			String oaipid = namespace + ":" + "open_access";
			if (!this.nodeExists(oaipid))
			{
				createOAISet(name, spec, oaipid);
			}
			linkObjectToOaiSet(node, spec, oaipid);
		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createOAISet(String name, String spec, String pid)
	{
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
			try
			{
				archive.createComplexObject(new ComplexObject(oaiset));
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}

		}
	}

	void linkObjectToOaiSet(Node node, String spec, String pid)
	{
		Vector<Link> relations = node.getRelsExt();

		Iterator<Link> iter = relations.iterator();
		while (iter.hasNext())
		{

			String pred = iter.next().getPredicate();
			// System.out.println(pred);
			// logger.info(pred);
			if (pred.compareTo(IS_MEMBER_OF) == 0)
				iter.remove();
			else if (pred.compareTo(ITEM_ID) == 0)
				iter.remove();
		}
		Link link = new Link();
		link.setPredicate(IS_MEMBER_OF);
		link.setObject("info:fedora/" + pid, false);
		relations.add(link);

		link = new Link();
		link.setPredicate(ITEM_ID);
		link.setObject(getURI(node), false);
		relations.add(link);

		node.setRelsExt(relations);
		try
		{
			archive.updateNode(node.getPID(), node);
		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String docmap(String type)
	{
		if (type.compareTo("report") == 0)
		{
			return "Monograph";
		}
		if (type.compareTo("webpage") == 0)
		{
			return "Webpage";
		}
		if (type.compareTo("ejournal") == 0)
		{
			return "EJournal";
		}
		return "";
	}

	private String ddcmap(String number)
	{
		if (number == null || number.length() != 3)
			logger.info("Didn't found ddc name for ddc:" + number);
		String name = "";
		try
		{
			URL url = new URL("http://dewey.info/class/" + number
					+ "/2009-08/about.en");
			HttpClient httpClient = new HttpClient();

			HttpMethod method = new GetMethod(url.toString());
			httpClient.executeMethod(method);
			InputStream stream = method.getResponseBodyAsStream();
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder;
			factory.setNamespaceAware(true);
			factory.setExpandEntityReferences(false);
			docBuilder = factory.newDocumentBuilder();

			Document doc;

			doc = docBuilder.parse(stream);
			Element root = doc.getDocumentElement();
			root.normalize();
			try
			{
				name = root.getElementsByTagName("skos:prefLabel").item(0)
						.getTextContent();
				logger.info("Found ddc name: " + name);
			}
			catch (Exception e)
			{
				logger.info("Didn't found ddc name for ddc:" + number);
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (HttpException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}

		return name;
	}

	public String formatAll()
	{
		List<String> objects = archive.findNodes("test:*");
		StringBuffer result = new StringBuffer();
		for (String pid : objects)
		{
			result.append(pid + "\n");
			archive.deleteNode(pid);
		}
		objects = archive.findNodes("edoweb:*");
		for (String pid : objects)
		{
			result.append(pid + "\n");
			archive.deleteNode(pid);
		}
		objects = archive.findNodes("oai:*");
		for (String pid : objects)
		{
			result.append(pid + "\n");
			archive.deleteNode(pid);
		}
		return result.toString();
	}

	public View getView(String pid)
	{
		try
		{
			Node node = archive.readNode(pid);
			if (node == null)
				return null;

			Vector<String> types = node.getType();

			for (String t : types)
			{
				if (t.compareTo(TypeType.contentType.toString() + ":"
						+ ObjectType.ejournal.toString()) == 0)
				{
					return getView(node, ObjectType.ejournal);
				}
				else if (t.compareTo(TypeType.contentType.toString() + ":"
						+ ObjectType.webpage.toString()) == 0)
				{
					return getView(node, ObjectType.webpage);
				}
				else if (t.compareTo(TypeType.contentType.toString() + ":"
						+ ObjectType.monograph.toString()) == 0)
				{
					return getView(node, ObjectType.monograph);
				}
			}

		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public View getView(String pid, ObjectType type)
	{

		// String host = "http://" + urlInfo.getBaseUri().getHost() + "/";
		// String url = urlInfo.getPath();
		// String objectUrl = host + url.substring(0, url.lastIndexOf('/'));

		try
		{
			Node node = archive.readNode(pid);
			if (node == null)
				return null;
			return getView(node, type);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public View getView(Node node, ObjectType type)
	{
		View view = new View();
		String pid = node.getPID();
		String uri = getURI(node);
		view.setCreator(node.getCreator());
		view.setTitle(node.getTitle());
		view.setLanguage(node.getLanguage());
		view.setSubject(node.getSubject());
		view.setType(node.getType());
		view.setLocation(node.getSource());
		view.setPublisher(node.getPublisher());
		view.setDescription(node.getDescription());
		String label = node.getLabel();
		if (label != null && !label.isEmpty())
			view.addDescription(label);
		view.setUri(uri);

		String pidWithoutNamespace = pid.substring(pid.indexOf(':') + 1);
		view.addCacheUrl(this.serverName + "/edobase/" + pidWithoutNamespace);
		view.addFedoraUrl(this.fedoraExtern + "/objects/" + pid);
		view.addDigitoolUrl("http://klio.hbz-nrw.de:1801/webclient/MetadataManager?pid="
				+ pidWithoutNamespace);
		String query = "<info:fedora/" + pid + "> * *";
		try
		{
			view.addRisearchUrl(this.fedoraExtern
					+ "/risearch?type=triples&lang=spo&format=RDF/XML&query="
					+ URIUtil.encodeQuery(query));
		}
		catch (URIException e)
		{
		}

		String mime = node.getMimeType();
		view.addMedium(mime);
		if (mime != null && !mime.isEmpty()
				&& mime.compareTo("application/pdf") == 0)
		{
			view.addPdfUrl(uri + "/data");
		}
		if (mime != null && !mime.isEmpty()
				&& mime.compareTo("application/zip") == 0)
		{
			view.addZipUrl(uri + "/data");
		}
		for (String date : node.getDate())
		{
			view.addYear(date.substring(0, 4));
		}
		for (String ddc : node.getSubject())
		{
			if (ddc.startsWith("ddc"))
			{
				view.addDdc(ddc);
				break;
			}
		}

		for (String doi : node.getIdentifier())
		{
			if (doi.startsWith("doi"))
			{
				view.addDoi(doi);
				view.addDataciteUrl(dataciteUrl + doi);
				view.addBaseUrl(baseUrl + doi);
				break;
			}
		}

		for (String urn : node.getIdentifier())
		{
			if (urn.startsWith("urn"))
			{
				view.addUrn(urn);
				break;
			}
		}

		for (String alephid : node.getIdentifier())
		{
			if (alephid.startsWith("HT"))
			{
				view.addAlephId(alephid);
				view.addCulturegraphUrl(culturegraphUrl + alephid);
				view.addLobidUrl(lobidUrl + alephid);
				view.addVerbundUrl(verbundUrl + alephid);
				break;
			}
			else if (alephid.startsWith("TT"))
			{
				view.addAlephId(alephid);
				view.addCulturegraphUrl(culturegraphUrl + alephid);
				view.addLobidUrl(lobidUrl + alephid);
				view.addVerbundUrl(verbundUrl + alephid);
				break;
			}
		}

		for (String relPid : findObject(pid, REL_BELONGS_TO_OBJECT))
		{
			String relUrl = serverName + "/objects/" + relPid;

			// if (type == ObjectType.ejournalVolume)
			// {
			// relUrl = serverName + "/ejournal/" + relPid;
			// }
			//
			// if (type == ObjectType.webpageVersion)
			// {
			// relUrl = serverName + "/webpage/" + relPid;
			// }

			view.addIsPartOf(relUrl);
		}

		for (String relPid : findObject(pid, REL_IS_RELATED))
		{
			String relUrl = serverName + "/objects/" + relPid;

			if (type == ObjectType.ejournal)
			{
				String name = findObject(relPid, HAS_VOLUME_NAME).get(0);
				relUrl = uri.concat("/volume/" + name);
			}

			if (type == ObjectType.webpage)
			{
				String name = findObject(relPid, HAS_VERSION_NAME).get(0);
				relUrl = uri.concat("/version/" + name);
			}

			view.addHasPart(relUrl);
		}

		return view;
	}

	public String index(Node node, ObjectType type)
	{
		String message = "";

		View view = getView(node, type);

		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		try
		{
			WebResource index = c
					.resource("http://localhost:9200/edoweb/titel/"
							+ node.getPID());
			index.accept(MediaType.APPLICATION_JSON);

			JAXBContext jc = JAXBContext.newInstance(View.class);

			Configuration config = new Configuration();
			Map<String, String> xmlToJsonNamespaces = new HashMap<String, String>(
					1);
			xmlToJsonNamespaces.put(
					"http://www.w3.org/2001/XMLSchema-instance", "");
			config.setXmlToJsonNamespaces(xmlToJsonNamespaces);
			MappedNamespaceConvention con = new MappedNamespaceConvention(
					config);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(baos);
			Writer writer = new OutputStreamWriter(out);
			XMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(con,
					writer);

			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(view, xmlStreamWriter);
			String viewAsString = baos.toString("utf-8");
			logger.debug("JSON-------------------");
			logger.debug(viewAsString);
			logger.debug("-----------------------");
			message = index.put(String.class, viewAsString);
		}
		catch (Exception e)
		{
			return "Error! " + message + e.getMessage();
		}
		return "Success! " + message;
	}

	public String index(UriInfo urlInfo, String pid)
	{
		try
		{
			Node node = archive.readNode(pid);
			if (node == null)
				return "Node not found! " + pid;
			ObjectType type = null;
			for (String t : node.getType())
			{
				if (t.compareTo(TypeType.contentType.toString() + ":"
						+ ObjectType.monograph.toString()) == 0)
				{
					type = ObjectType.monograph;
					break;
				}
				else if (t.compareTo(TypeType.contentType.toString() + ":"
						+ ObjectType.ejournal.toString()) == 0)
				{
					type = ObjectType.ejournal;
					break;
				}
				else if (t.compareTo(TypeType.contentType.toString() + ":"
						+ ObjectType.webpage.toString()) == 0)
				{
					type = ObjectType.webpage;
					break;
				}
			}
			if (type == null)
				return "Sorry the node has no type! ERROR! " + pid;

			return index(node, type);
		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Something unexpected occured! " + pid;
	}

	private String getURI(Node node)
	{

		String typePath = null;
		for (String t : node.getType())
		{
			if (t.compareTo(TypeType.contentType.toString() + ":"
					+ ObjectType.monograph.toString()) == 0)
			{
				typePath = "objects";
				break;
			}
			else if (t.compareTo(TypeType.contentType.toString() + ":"
					+ ObjectType.ejournal.toString()) == 0)
			{
				typePath = "objects";
				break;
			}
			else if (t.compareTo(TypeType.contentType.toString() + ":"
					+ ObjectType.webpage.toString()) == 0)
			{
				typePath = "objects";
				break;
			}
			else if (t.compareTo(ObjectType.webpageVersion.toString()) == 0)
			{
				typePath = "objects";
				return serverName + "/" + typePath + "/" + getWebpagePid(node)
						+ "/version/" + getVersionName(node);
			}
			else if (t.compareTo(ObjectType.ejournalVolume.toString()) == 0)
			{
				typePath = "objects";
				return serverName + "/" + typePath + "/" + getJournalPid(node)
						+ "/volume/" + getVolumeName(node);
			}

		}
		if (typePath == null)
			return "Sorry the node has no type! ERROR! " + node.getPID();

		return serverName + "/" + typePath + "/" + node.getPID();
	}

	private String getVolumeName(Node node)
	{
		return findObject(node.getPID(), HAS_VOLUME_NAME).firstElement();
	}

	private String getJournalPid(Node node)
	{
		return findObject(node.getPID(), IS_VOLUME).firstElement();
	}

	private String getVersionName(Node node)
	{
		return findObject(node.getPID(), HAS_VERSION_NAME).firstElement();
	}

	private String getWebpagePid(Node node)
	{
		return findObject(node.getPID(), IS_VERSION).firstElement();
	}

	public List<String> getAll()
	{
		return archive.findNodes("edoweb:*");
	}

}
