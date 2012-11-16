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

import static de.nrw.hbz.edoweb2.fedora.FedoraVocabulary.IS_MEMBER_OF;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
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
	String host = null;

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
		host = properties.getProperty("hostName");
		archive = ArchiveFactory.getArchiveImpl(
				properties.getProperty("fedoraUrl"),
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
		archive.createComplexObject(object);
		if (wait)
			waitWorkaround();
		return object.getRoot().getPID() + " CREATED!";
	}

	public String update(String pid, StatusBean status, boolean wait)
	{
		try
		{
			Node node = archive.readNode(pid);
			if (node != null)
			{
				Vector<String> v = new Vector<String>();
				v.add(status.visibleFor.toString());
				node.setRights(v);
				archive.updateNode(pid, node);
				if (wait)
					waitWorkaround();
			}
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return "update";
	}

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

	public StatusBean read(String pid)
	{
		try
		{
			Node object = archive.readObject(pid);
			return new StatusBean(object);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return null;
	}

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
							new java.net.URI(host + "/objects/" + pid
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
							new java.net.URI(host + "/objects/" + pid
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
			node.setDescription(content.getRelation());
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
			archive.updateNode(node.getPID(), node);
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
			for (Link l : links)
			{
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
					if (type.startsWith("doc-type"))
					{
						String docType = type.substring(9);
						logger.info("Found docType: " + docType);

						String name = docmap(docType);
						String spec = "doc-type:" + docType;
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

		Link link = new Link();
		link.setPredicate(IS_MEMBER_OF);
		link.setObject("info:fedora/" + pid, false);
		node.addRelation(link);
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
			return "Report";
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (HttpException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
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
}
