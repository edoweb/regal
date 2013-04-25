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
import javax.ws.rs.core.Response.ResponseBuilder;
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

	// String namespace = "edoweb";

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
	public Response about(@PathParam("pid") String pid)
	{
		View view = actions.getView(pid);
		ResponseBuilder res = Response.ok()
				.lastModified(view.getLastModified()).entity(view);

		return res.build();
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return an aggregated representation of the resource
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml", "text/html" })
	public Response get(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace) throws URISyntaxException
	{
		return Response
				.temporaryRedirect(
						new java.net.URI("/resources/" + namespace + ":" + pid
								+ "/about")).status(303).build();
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
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	@Consumes("application/json")
	public String create(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace, CreateObjectBean input)
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
			return createMonograph(pid, namespace);
		else if (input.type.compareTo(ObjectType.ejournal.toString()) == 0)
			return createEJournal(pid, namespace);
		else if (input.type.compareTo(ObjectType.webpage.toString()) == 0)
			return createWebpage(pid, namespace);
		else if (input.type.compareTo(ObjectType.webpageVersion.toString()) == 0)
			return createWebpageVersion(input.parentPid, pid, namespace);
		else if (input.type.compareTo(ObjectType.ejournalVolume.toString()) == 0)
			return createEJournalVolume(input.parentPid, pid, namespace);

		else
			return createResource(input, pid, namespace);

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
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	@Consumes("application/json")
	public String createPost(@PathParam("pid") String pid,
			@PathParam("namepsace") String namespace,
			final CreateObjectBean input)
	{
		return create(pid, namespace, input);
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
	public String updateData(@PathParam("pid") String pid, MultiPart multiPart)
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
	public String updateDataPost(@PathParam("pid") String pid,
			MultiPart multiPart)
	{

		return updateData(pid, multiPart);

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
	public String updateMetadata(@PathParam("pid") String pid, String content)
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
	public String updateMetadataPost(@PathParam("pid") String pid,
			String content)
	{
		return updateMetadata(pid, content);
	}

	@PUT
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateDC(@PathParam("pid") String pid, DCBeanAnnotated content)
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

	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateDCPost(String pid, DCBeanAnnotated content)
	{
		return updateDC(pid, content);
	}

	/**
	 * @param pid
	 *            the pid of the resource that must be deleted
	 * @return a human readable message and a status code of 200 if successful
	 *         or a 500 if not.
	 */
	@DELETE
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public String delete(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		try
		{
			return actions.delete(namespace + ":" + pid, false);

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
	public String deleteData(@PathParam("pid") String pid)
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
	public String deleteMetadata(@PathParam("pid") String pid)
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
	public String deleteAllOfType(@PathParam("type") String type)
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

	/**
	 * @param pid
	 *            the pid of a resource containing multiple volumes
	 * @return all volumes of the resource
	 */
	@GET
	@Path("/{pid}/parts/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllParts(@PathParam("pid") String pid)
	{

		return new ObjectList(actions.findObject(pid, REL_IS_RELATED));
	}

	/**
	 * @param pid
	 *            the pid of a resource containing multiple volumes
	 * @return all volumes of the resource
	 */
	@GET
	@Path("/{pid}/parents/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllParents(@PathParam("pid") String pid)
	{

		return new ObjectList(actions.findObject(pid, REL_BELONGS_TO_OBJECT));
	}

	private String createResource(CreateObjectBean input, String p,
			String namespace)
	{
		logger.info("create " + input.type);
		String pid = namespace + ":" + p;
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
			rootObject.setContentType(input.getType());
			rootObject.addContentModel(ContentModelFactory
					.createHeadModel(namespace));
			if (input.getParentPid() != null && !input.getParentPid().isEmpty())
			{
				String parentPid = input.getParentPid();
				link = new Link();
				link.setPredicate(REL_BELONGS_TO_OBJECT);
				link.setObject(parentPid, false);
				rootObject.addRelation(link);

				link = new Link();
				link.setPredicate(REL_IS_RELATED);
				link.setObject(pid, false);
				actions.addLink(parentPid, link);
			}

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

	// --SPECIAL---

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

	private String createWebpage(String p, String namespace)
	{
		try
		{
			logger.info("create Webpage");
			String pid = namespace + ":" + p;
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
			rootObject.addContentModel(ContentModelFactory
					.createWebpageModel(namespace));
			rootObject.addContentModel(ContentModelFactory
					.createHeadModel(namespace));

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

	private String createMonograph(String p, String namespace)
	{
		logger.info("create Monograph");
		String pid = namespace + ":" + p;
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
			rootObject.addContentModel(ContentModelFactory
					.createMonographModel(namespace));
			rootObject.addContentModel(ContentModelFactory
					.createHeadModel(namespace));
			rootObject.addContentModel(ContentModelFactory
					.createPdfModel(namespace));

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

	private String createEJournal(String p, String namespace)
	{
		String pid = namespace + ":" + p;
		logger.info("create EJournal");
		try
		{
			if (actions.nodeExists(pid))
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
			rootObject.setNamespace(namespace).setPID(pid);
			rootObject.setContentType(ObjectType.ejournal.toString());
			rootObject.addContentModel(ContentModelFactory
					.createEJournalModel(namespace));
			rootObject.addContentModel(ContentModelFactory
					.createHeadModel(namespace));

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

	private String createWebpageVersion(String parentPid, String p,
			String namespace)
	{
		String versionPid = namespace + ":" + p;
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
			rootObject.addContentModel(ContentModelFactory
					.createVersionModel(namespace));

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

	private String createEJournalVolume(String parentPid, String p,
			String namespace)
	{

		String volumePid = namespace + ":" + p;
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
			rootObject.addContentModel(ContentModelFactory
					.createVolumeModel(namespace));
			rootObject.addContentModel(ContentModelFactory
					.createPdfModel(namespace));

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
