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

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Vector;

import javax.ws.rs.core.Response;

import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

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
	ArchiveInterface archive = null;

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

		archive = ArchiveFactory.getArchiveImpl(
				properties.getProperty("fedoraUrl"),
				properties.getProperty("user"),
				properties.getProperty("password"),
				properties.getProperty("sesameStore"));

	}

	public String deleteAll(Vector<String> pids)
	{
		System.out.println("Delete All");
		for (String pid : pids)
		{
			try
			{
				archive.deleteComplexObject(pid);
				waitWorkaround();
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}

		return "deleteAll";
	}

	public Vector<String> findByType(ObjectType type)
	{
		Vector<String> pids = new Vector<String>();
		String query = "* <http://purl.org/dc/elements/1.1/type> \""
				+ type.toString() + "\"";
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

	public String create(ComplexObject object) throws RemoteException
	{
		archive.createComplexObject(object);
		waitWorkaround();
		return object.getRoot().getPID() + " CREATED!";
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

	public String update(String pid, StatusBean status)
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
				waitWorkaround();
			}
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return "update";
	}

	public String delete(String pid)
	{

		try
		{
			archive.deleteComplexObject(pid);
			waitWorkaround();
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return "delete";
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
							new java.net.URI(node.getDataUrl().toString()))
							.build();
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

		System.out.println("Read DC");
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
							new java.net.URI(node.getMetadataUrl().toString()))
							.build();
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
		System.out.println("Update DC");
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
		System.out.println(query);
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

	private void waitWorkaround()
	{
		/*
		 * Workaround START
		 */
		try
		{
			System.out.println("Wait 10 sec! Nasty workaround.");
			Thread.sleep(10000);
			System.out.println("Stop Waiting! Nasty workaround.");
		}
		catch (InterruptedException e1)
		{

			e1.printStackTrace();
		}
		/*
		 * Workaround END
		 */
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
}
