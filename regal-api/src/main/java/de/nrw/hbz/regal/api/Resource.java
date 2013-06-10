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
package de.nrw.hbz.regal.api;

import static de.nrw.hbz.regal.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.regal.datatypes.Vocabulary.TYPE_OBJECT;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.HAS_PART;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.IS_PART_OF;

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

import de.nrw.hbz.regal.datatypes.ComplexObject;
import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.exceptions.ArchiveException;

/**
 * GetResource collects all Methods for read access to a archive resource.
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@Path("/resource")
public class Resource
{

	final static Logger logger = LoggerFactory.getLogger(Resource.class);

	Actions actions = null;

	/**
	 * Creates a new GetResource
	 * 
	 * @throws IOException
	 *             if properties of the Actions class can't get loaded
	 */
	public Resource() throws IOException
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
	@Path("/{namespace}:{pid}/data")
	@Produces({ "application/*" })
	public Response readData(@PathParam("pid") String p,
			@PathParam("namespace") String namespace)
	{
		try
		{
			String pid = namespace + ":" + p;
			Response res = actions.readData(pid);
			if (res == null)
				throw new HttpArchiveException(404,
						"Datastream does not exist!");
			return res;
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
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/{namespace}:{pid}/about")
	@Produces({ "application/json" })
	public Response about(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace) throws URISyntaxException
	{
		return getJson(pid, namespace);
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return an aggregated representation of the resource
	 */
	@GET
	@Path("/{namespace}:{pid}.rdf")
	@Produces({ "application/rdf+xml" })
	public Response getReMAsRdfXml(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		String rem = actions.getReM(namespace + ":" + pid,
				"application/rdf+xml");
		ResponseBuilder res = Response.ok()
				.lastModified(actions.getLastModified(namespace + ":" + pid))
				.entity(rem);

		return res.build();
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return an aggregated representation of the resource
	 */
	@GET
	@Path("/{namespace}:{pid}.rdf")
	@Produces({ "text/plain" })
	public Response getReMAsNTriple(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		String rem = actions.getReM(namespace + ":" + pid, "text/plain");
		ResponseBuilder res = Response.ok()
				.lastModified(actions.getLastModified(namespace + ":" + pid))
				.entity(rem);

		return res.build();
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return an aggregated representation of the resource
	 */
	@GET
	@Path("/{namespace}:{pid}.json")
	@Produces({ "application/json" })
	public Response getReMAsJson(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		String rem = actions.getReM(namespace + ":" + pid, "application/json");
		ResponseBuilder res = Response.ok()
				.lastModified(actions.getLastModified(namespace + ":" + pid))
				.entity(rem);

		return res.build();
	}

	@GET
	@Path("/{namespace}:{pid}.html")
	@Produces({ "text/html" })
	public Response getReMAsHtml(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		String rem = actions.getReM(namespace + ":" + pid, "text/html");
		// return rem;
		ResponseBuilder res = Response.ok()
				.lastModified(actions.getLastModified(namespace + ":" + pid))
				.entity(rem);

		return res.build();
	}

	@GET
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json" })
	public Response getJson(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace) throws URISyntaxException
	{
		// return about(namespace + ":" + pid);
		return Response
				.temporaryRedirect(
						new java.net.URI("../resource/" + namespace + ":" + pid
								+ ".json")).status(303).build();
	}

	@GET
	@Path("/{namespace}:{pid}")
	@Produces({ "application/rdf+xml" })
	public Response getRdfXml(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace) throws URISyntaxException
	{
		// return about(namespace + ":" + pid);
		return Response
				.temporaryRedirect(
						new java.net.URI("../resource/" + namespace + ":" + pid
								+ ".rdf"))
				.header("accept", "application/rdf+xml").status(303).build();
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return an aggregated representation of the resource
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/{namespace}:{pid}")
	@Produces({ "text/html" })
	public Response getHtml(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace) throws URISyntaxException
	{
		// return about(namespace + ":" + pid);
		return Response
				.temporaryRedirect(
						new java.net.URI("../resource/" + namespace + ":" + pid
								+ ".html")).status(303).build();
	}

	@GET
	@Path("/{namespace}:{pid}")
	@Produces({ "text/plain" })
	public Response getNtriple(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace) throws URISyntaxException
	{
		// return about(namespace + ":" + pid);
		return Response
				.temporaryRedirect(
						new java.net.URI("../resource/" + namespace + ":" + pid
								+ ".rdf")).header("accept", "text/plain")
				.status(303).build();
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return an aggregated representation of the resource
	 */
	@GET
	@Path("/{namespace}:{pid}.dc")
	@Produces({ "application/xml" })
	public Response getDC(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		return getOAI_DC(pid, namespace);
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
			String result = actions.readMetadata(pid);
			return result;
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

	@GET
	@Path("/{namespace}:{pid}/fulltext")
	@Produces({ "application/xml", "application/json" })
	public Response getFulltext(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		try
		{
			return actions.getFulltext(pid, namespace);
		}
		catch (URISyntaxException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}

	}

	@GET
	@Path("/{namespace}:{pid}/epicur")
	@Produces({ "application/xml", "application/json" })
	public Response getEpicur(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		try
		{
			return actions.getEpicur(pid, namespace);
		}
		catch (URISyntaxException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}

	}

	@GET
	@Path("/{namespace}:{pid}/oai_dc")
	@Produces({ "application/xml", "application/json" })
	public Response getOAI_DC(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		try
		{
			return actions.getOAI_DC(pid, namespace);
		}
		catch (URISyntaxException e)
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
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
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
		else if (input.type.compareTo(ObjectType.journal.toString()) == 0)
			return createEJournal(pid, namespace);
		else if (input.type.compareTo(ObjectType.webpage.toString()) == 0)
			return createWebpage(pid, namespace);
		else if (input.type.compareTo(ObjectType.version.toString()) == 0)
			return createWebpageVersion(input.parentPid, pid, namespace);
		else if (input.type.compareTo(ObjectType.volume.toString()) == 0)
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
	@Consumes({ "application/json", "application/xml" })
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
	@Path("/{namespace}:{pid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes("multipart/mixed")
	public String updateData(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace, MultiPart multiPart)
	{

		try
		{
			String mimeType = multiPart.getBodyParts().get(1)
					.getEntityAs(String.class);

			String name = "data";
			if (multiPart.getBodyParts().size() == 3)
			{
				name = multiPart.getBodyParts().get(2)
						.getEntityAs(String.class);
			}
			// if (mimeType.compareTo("application/pdf") == 0)
			// {
			// Node node = actions.readNode(namespace + ":" + pid);
			// node.addContentModel(ContentModelFactory
			// .createPdfModel(namespace));
			// actions.updateNode(node);
			// }
			return actions.updateData(namespace + ":" + pid, multiPart
					.getBodyParts().get(0).getEntityAs(InputStream.class),
					mimeType, name);
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
	@Path("/{namespace}:{pid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes("multipart/mixed")
	public String updateDataPost(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace, MultiPart multiPart)
	{

		return updateData(pid, namespace, multiPart);

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

		return new ObjectList(actions.findObject(pid, HAS_PART));
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

		return new ObjectList(actions.findObject(pid, IS_PART_OF));
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
				throw new HttpArchiveException(304,
						"Node already exists. I do nothing!");
			}
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(pid);
			rootObject.setContentType(input.getType());
			rootObject.addContentModel(ContentModelFactory
					.createHeadModel(namespace));
			if (input.getParentPid() != null && !input.getParentPid().isEmpty())
			{
				String parentPid = input.getParentPid();
				link = new Link();
				link.setPredicate(IS_PART_OF);
				link.setObject(parentPid, false);
				rootObject.addRelation(link);

				link = new Link();
				link.setPredicate(HAS_PART);
				link.setObject(pid, false);
				actions.addLink(parentPid, link);
			}

			ComplexObject object = new ComplexObject(rootObject);
			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			e.printStackTrace();
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	// --SPECIAL---

	private String createWebpage(String p, String namespace)
	{
		try
		{
			logger.info("create Webpage");
			String pid = namespace + ":" + p;
			if (actions.nodeExists(pid))
			{
				throw new HttpArchiveException(304,
						"Node already exists. I do nothing!");
			}
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(pid);
			rootObject.setContentType(ObjectType.webpage.toString());
			rootObject.addContentModel(ContentModelFactory
					.createWebpageModel(namespace));
			rootObject.addContentModel(ContentModelFactory
					.createHeadModel(namespace));

			ComplexObject object = new ComplexObject(rootObject);
			return actions.create(object, false);

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
		ComplexObject object = null;
		try
		{
			if (actions.nodeExists(pid))
			{
				throw new HttpArchiveException(304,
						"Node already exists. I do nothing!");
			}
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(pid);
			rootObject.setContentType(ObjectType.monograph.toString());
			rootObject.addContentModel(ContentModelFactory
					.createMonographModel(namespace));
			rootObject.addContentModel(ContentModelFactory
					.createHeadModel(namespace));
			rootObject.addContentModel(ContentModelFactory
					.createPdfModel(namespace));

			object = new ComplexObject(rootObject);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}

		return actions.create(object, false);
	}

	private String createEJournal(String p, String namespace)
	{
		String pid = namespace + ":" + p;
		logger.info("create EJournal");
		try
		{
			if (actions.nodeExists(pid))
			{
				throw new HttpArchiveException(304,
						"Node already exists. I do nothing!");
			}

			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(pid);
			rootObject.setContentType(ObjectType.journal.toString());
			rootObject.addContentModel(ContentModelFactory
					.createEJournalModel(namespace));
			rootObject.addContentModel(ContentModelFactory
					.createHeadModel(namespace));

			ComplexObject object = new ComplexObject(rootObject);
			return actions.create(object, false);

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
			if (actions.nodeExists(versionPid))
			{
				throw new HttpArchiveException(304,
						"Node already exists. I do nothing!");
			}
			logger.info("create Webpage Version");

			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(IS_PART_OF);
			link.setObject(parentPid, false);
			rootObject.addRelation(link);

			rootObject.setNamespace(namespace).setPID(versionPid);
			rootObject.setContentType(ObjectType.version.toString());
			rootObject.addContentModel(ContentModelFactory
					.createVersionModel(namespace));

			ComplexObject object = new ComplexObject(rootObject);

			link = new Link();
			link.setPredicate(HAS_PART);
			link.setObject(versionPid, false);

			actions.addLink(parentPid, link);

			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			e.printStackTrace();
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
			if (actions.nodeExists(volumePid))
			{
				throw new HttpArchiveException(304,
						"Node already exists. I do nothing!");
			}
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(IS_PART_OF);
			link.setObject(parentPid, false);
			rootObject.addRelation(link);

			rootObject.setNamespace(namespace).setPID(volumePid);
			rootObject.setContentType(ObjectType.volume.toString());
			rootObject.addContentModel(ContentModelFactory
					.createVolumeModel(namespace));
			rootObject.addContentModel(ContentModelFactory
					.createPdfModel(namespace));

			ComplexObject object = new ComplexObject(rootObject);

			link = new Link();
			link.setPredicate(HAS_PART);
			link.setObject(volumePid, false);
			actions.addLink(parentPid, link);

			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			e.printStackTrace();
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}

	}

}
