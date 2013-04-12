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

import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VERSION;
import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VERSION_NAME;
import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VOLUME;
import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VOLUME_NAME;
import static de.nrw.hbz.edoweb2.api.Vocabulary.IS_VERSION;
import static de.nrw.hbz.edoweb2.api.Vocabulary.IS_VOLUME;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_BELONGS_TO_OBJECT;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_RELATED;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.TYPE_OBJECT;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.multipart.MultiPart;

import de.nrw.hbz.edoweb2.archive.exceptions.ArchiveException;
import de.nrw.hbz.edoweb2.datatypes.ComplexObject;
import de.nrw.hbz.edoweb2.datatypes.Link;
import de.nrw.hbz.edoweb2.datatypes.Node;

/**
 * GetResource collects all Methods for read access to a archive resource.
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@Path("/resources")
public class Resources
{

	final static Logger logger = LoggerFactory.getLogger(Resources.class);

	String namespace = "edoweb";

	Actions actions = null;

	/**
	 * Creates a new GetResource
	 * 
	 * @throws IOException
	 *             if properties of the Actions class can't get loaded
	 */
	public Resources() throws IOException
	{
		actions = new Actions();
	}

	/**
	 * Returns the actual data of a resource. The format of the binary data is
	 * not defined.
	 * 
	 * @param pid
	 *            The pid of the resource
	 * @return the actual binary data
	 */
	@GET
	@Path("/{pid}/data")
	@Produces({ "application/*" })
	public Response readData(@PathParam("pid") String pid)
	{
		try
		{
			return actions.readData(pid);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
		catch (URISyntaxException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	/**
	 * @return a list of all archived objects
	 */
	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return new ObjectList(actions.getAll());
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return an aggregated representation of the resource
	 */
	@GET
	@Path("/{pid}/about")
	@Produces({ "application/json", "application/xml", "text/html" })
	public View getView(@PathParam("pid") String pid)
	{
		return actions.getView(pid);
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return an aggregated representation of the resource
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml", "text/html" })
	public Response getResource(@PathParam("pid") String pid)
			throws URISyntaxException
	{
		return Response
				.temporaryRedirect(
						new java.net.URI("/resources/" + pid + "/about"))
				.status(303).build();
	}

	/**
	 * @param pid
	 *            the pid of a resource containing multiple volumes
	 * @return all volumes of the resource
	 */
	@GET
	@Path("/{pid}/volume/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVolumes(@PathParam("pid") String pid)
	{

		return new ObjectList(actions.findObject(pid, HAS_VOLUME));
	}

	/**
	 * @param pid
	 *            the metadata of a pid
	 * @return the rdf metadata as n-triple
	 */
	@GET
	@Path("/{pid}/metadata")
	@Produces({ "text/plain" })
	public String readMetadata(@PathParam("pid") String pid)
	{
		try
		{
			return actions.readMetadata(pid);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
		catch (URISyntaxException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
		catch (MalformedURLException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
		catch (IOException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return the dublin core as json or xml
	 */
	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}

	/**
	 * @param pid
	 *            the pid of the resource containing versions
	 * @return a list with pids of each version
	 */
	@GET
	@Path("/{pid}/version/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVersions(@PathParam("pid") String pid)
	{
		try
		{
			return new ObjectList(actions.findObject(pid, HAS_VERSION));
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	/**
	 * @param type
	 *            the type of the resources that must be returned
	 * @return a list of pids
	 */
	@GET
	@Path("/type/{type}")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllOfType(@PathParam("type") String type)
	{
		try
		{
			return new ObjectList(actions.findByType(type));
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	/**
	 * Creates a new Resource. The Resource has a certain type an can be
	 * connected to a parent resource.
	 * 
	 * @param pid
	 *            the pid of the new resource
	 * @param input
	 *            a json string of the form { "type" :
	 *            "<monograph | ejournal | webpage | webpageVersion | ejournalVolume | monographPart >"
	 *            , "parentPid" : "uuid" }
	 * @return a human readable message and a status code 200 if successful or a
	 *         400 if not
	 */
	@PUT
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	@Consumes("application/json")
	public String createResource(@PathParam("pid") String pid,
			CreateObjectBean input)
	{
		if (input == null)
		{
			throw new HttpArchiveException(Status.BAD_REQUEST.getStatusCode(),
					"You've posted no input: NULL. You must provide at least a type");
		}
		else if (input.type == null || input.type.isEmpty())
		{
			throw new HttpArchiveException(Status.BAD_REQUEST.getStatusCode(),
					"The type you've provided is NULL or empty.");
		}
		if (input.type.compareTo(ObjectType.monograph.toString()) == 0)
			return createMonograph(pid);
		else if (input.type.compareTo(ObjectType.ejournal.toString()) == 0)
			return createEJournal(pid);
		else if (input.type.compareTo(ObjectType.webpage.toString()) == 0)
			return createWebpage(pid);
		else if (input.type.compareTo(ObjectType.webpageVersion.toString()) == 0)
			return createWebpageVersion(input.parentPid, pid);
		else if (input.type.compareTo(ObjectType.ejournalVolume.toString()) == 0)
			return createEJournalVolume(input.parentPid, pid);

		throw new HttpArchiveException(Status.BAD_REQUEST.getStatusCode(),
				"The type you've provided " + input.type
						+ " does not exist or hasn't yet been implemented.");
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @param multiPart
	 *            The data is transfered as multipart data in order to provide
	 *            upload of large files
	 * @return A human readable message and a status code of 200 if successful
	 *         an of 500 if not.
	 */
	@PUT
	@Path("/{pid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes("multipart/mixed")
	public String updateResourceData(@PathParam("pid") String pid,
			MultiPart multiPart)
	{

		try
		{
			return actions.updateData(pid, multiPart.getBodyParts().get(0)
					.getEntityAs(InputStream.class), multiPart.getBodyParts()
					.get(1).getEntityAs(String.class));
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
		catch (IOException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}

	}

	/**
	 * @param pid
	 *            The pid of the resource
	 * @param content
	 *            the metadata as n-triple rdf
	 * @return a human readable message and a status code of 200 if successful
	 *         and 500 if not.
	 */
	@PUT
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateResourceMetadata(@PathParam("pid") String pid,
			String content)
	{
		try
		{
			return actions.updateMetadata(pid, content);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
		catch (IOException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	@Deprecated
	@PUT
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateResourceDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		try
		{
			return actions.updateDC(pid, content);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	/**
	 * Creates a new Resource. The Resource has a certain type an can be
	 * connected to a parent resource.
	 * 
	 * @param pid
	 *            the pid of the new resource
	 * @param input
	 *            a json string of the form { "type" :
	 *            "<monograph | ejournal | webpage | webpageVersion | ejournalVolume | monographPart >"
	 *            , "parentPid" : "uuid" }
	 * @return a human readable message and a status code 200 if successful or a
	 *         400 if not
	 */
	@POST
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	@Consumes("application/json")
	public String createResourcePost(@PathParam("pid") String pid,
			final CreateObjectBean input)
	{
		return createResource(pid, input);
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @param multiPart
	 *            The data is transfered as multipart data in order to provide
	 *            upload of large files
	 * @return A human readable message and a status code of 200 if successful
	 *         an of 500 if not.
	 */
	@POST
	@Path("/{pid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes("multipart/mixed")
	public String updateResourceDataPost(@PathParam("pid") String pid,
			MultiPart multiPart)
	{

		return updateResourceData(pid, multiPart);

	}

	/**
	 * @param pid
	 *            The pid of the resource
	 * @param content
	 *            the metadata as n-triple rdf
	 * @return a human readable message and a status code of 200 if successful
	 *         and 500 if not.
	 */
	@POST
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateResourceMetadataPost(@PathParam("pid") String pid,
			String content)
	{
		return updateResourceMetadata(pid, content);
	}

	@Deprecated
	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateResourceDCPost(String pid, DCBeanAnnotated content)
	{
		return updateResourceDC(pid, content);
	}

	/**
	 * @param pid
	 *            the pid of the resource that must be deleted
	 * @return a human readable message and a status code of 200 if successful
	 *         or a 500 if not.
	 */
	@DELETE
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	public String deleteResource(@PathParam("pid") String pid)
	{
		try
		{
			return actions.delete(pid, false);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	/**
	 * @param pid
	 *            the pid of the resource that data must be deleted
	 * @return a human readable message and a status code of 200 if successful
	 *         or a 500 if not.
	 */
	@DELETE
	@Path("/{pid}/data")
	@Produces({ "application/json", "application/xml" })
	public String deleteResourceData(@PathParam("pid") String pid)
	{
		try
		{
			return actions.deleteData(pid);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	/**
	 * @param pid
	 *            the pid of the resource that data must be deleted
	 * @return a human readable message and a status code of 200 if successful
	 *         or a 500 if not.
	 */
	@DELETE
	@Path("/{pid}/metadata")
	@Produces({ "application/json", "application/xml" })
	public String deleteResourceMetadata(@PathParam("pid") String pid)
	{
		try
		{
			return actions.deleteMetadata(pid);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	/**
	 * Deletes all Resources of a certain type.
	 * 
	 * @param type
	 *            the type of resources that will be deleted
	 * @return A message and status code 200 if ok and 500 if not
	 */
	@DELETE
	@Path("/type/{type}")
	@Produces({ "application/json", "application/xml" })
	public String deleteResourceOfType(@PathParam("type") String type)
	{
		try
		{
			return actions.deleteAll(actions.findByType(type), false);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	private String createWebpage(String pid)
	{
		try
		{
			logger.info("create Webpage");

			if (actions.nodeExists(pid))
			{
				throw new HttpArchiveException(
						Status.INTERNAL_SERVER_ERROR.getStatusCode(),
						"Node already exists. I do nothing!");
			}
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, false);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(pid);
			rootObject.setContentType(ObjectType.webpage.toString());
			rootObject.addContentModel(ContentModelFactory.create(namespace,
					ObjectType.webpage.toString()));
			rootObject.addContentModel(ContentModelFactory.create(namespace,
					"edoweb"));

			ComplexObject object = new ComplexObject(rootObject);
			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	private String createMonograph(String pid)
	{
		logger.info("create Monograph");
		try
		{
			if (actions.nodeExists(pid))
			{
				throw new HttpArchiveException(
						Status.INTERNAL_SERVER_ERROR.getStatusCode(),
						"Node already exists. I do nothing!");
			}
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, false);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(pid);
			rootObject.setContentType(ObjectType.monograph.toString());
			rootObject.addContentModel(ContentModelFactory.create(namespace,
					ObjectType.monograph.toString()));
			rootObject.addContentModel(ContentModelFactory.create(namespace,
					"edoweb"));

			ComplexObject object = new ComplexObject(rootObject);
			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	private String createEJournal(String pid)
	{
		logger.info("create EJournal");
		try
		{
			if (actions.nodeExists(namespace + ":" + pid))
			{
				throw new HttpArchiveException(
						Status.INTERNAL_SERVER_ERROR.getStatusCode(), namespace
								+ ":" + pid
								+ " node already exists. I do nothing!");
			}

			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, false);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(namespace + ":" + pid);
			rootObject.setContentType(ObjectType.ejournal.toString());
			rootObject.addContentModel(ContentModelFactory.create(namespace,
					ObjectType.ejournal.toString()));
			rootObject.addContentModel(ContentModelFactory.create(namespace,
					"edoweb"));

			ComplexObject object = new ComplexObject(rootObject);
			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	private String createWebpageVersion(String parentPid, String versionPid)
	{
		try
		{
			logger.info("create Webpage Version");

			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(IS_VERSION);
			link.setObject(parentPid, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(HAS_VERSION_NAME);
			link.setObject(versionPid, true);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(REL_BELONGS_TO_OBJECT);
			link.setObject(parentPid, false);
			rootObject.addRelation(link);

			rootObject.setNamespace(namespace).setPID(versionPid);
			rootObject.setContentType(ObjectType.webpageVersion.toString());
			rootObject.addContentModel(ContentModelFactory.create(namespace,
					ObjectType.webpageVersion.toString()));
			rootObject.addContentModel(ContentModelFactory.create(namespace,
					"edoweb"));

			ComplexObject object = new ComplexObject(rootObject);

			link = new Link();
			link.setPredicate(HAS_VERSION);
			link.setObject(versionPid, false);

			actions.addLink(parentPid, link);

			link = new Link();
			link.setPredicate(REL_IS_RELATED);
			link.setObject(versionPid, false);

			actions.addLink(parentPid, link);

			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	private String createEJournalVolume(String parentPid, String volumePid)
	{

		logger.info("create EJournal Volume");
		try
		{
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(IS_VOLUME);
			link.setObject(parentPid, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(REL_BELONGS_TO_OBJECT);
			link.setObject(parentPid, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(HAS_VOLUME_NAME);
			link.setObject(volumePid, true);
			rootObject.addRelation(link);

			rootObject.setNamespace(namespace).setPID(volumePid);
			rootObject.setContentType(ObjectType.ejournalVolume.toString());
			rootObject.addContentModel(ContentModelFactory.create(namespace,
					ObjectType.ejournalVolume.toString()));
			rootObject.addContentModel(ContentModelFactory.create(namespace,
					"edoweb"));

			ComplexObject object = new ComplexObject(rootObject);

			link = new Link();
			link.setPredicate(HAS_VOLUME);
			link.setObject(volumePid, false);
			actions.addLink(parentPid, link);

			link = new Link();
			link.setPredicate(REL_IS_RELATED);
			link.setObject(volumePid, false);
			actions.addLink(parentPid, link);

			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}

	}
}
