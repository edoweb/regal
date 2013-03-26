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

import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VOLUME;
import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VOLUME_NAME;
import static de.nrw.hbz.edoweb2.api.Vocabulary.IS_VOLUME;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_BELONGS_TO_OBJECT;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_RELATED;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.TYPE_OBJECT;

import java.io.IOException;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.edoweb2.archive.exceptions.ArchiveException;
import de.nrw.hbz.edoweb2.datatypes.ComplexObject;
import de.nrw.hbz.edoweb2.datatypes.Link;
import de.nrw.hbz.edoweb2.datatypes.Node;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@Path("/ejournal")
public class EJournal
{
	final static Logger logger = LoggerFactory.getLogger(EJournal.class);
	ObjectType ejournalType = ObjectType.ejournal;
	ObjectType volumeType = ObjectType.ejournalVolume;
	String namespace = "edoweb";

	Actions actions = null;

	public EJournal() throws IOException
	{
		actions = new Actions();
	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		try
		{
			return new ObjectList(actions.findByType(TypeType.contentType
					.toString() + ":" + ejournalType.toString()));
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public String deleteAll()
	{
		try
		{
			String eJournal = actions.deleteAll(
					actions.findByType(TypeType.contentType.toString() + ":"
							+ ejournalType.toString()), false);
			String eJournalVolume = actions.deleteAll(
					actions.findByType(volumeType.toString()), false);
			return eJournal + "\n" + eJournalVolume;

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	// @GET
	// @Path("/{namespace}:{pid}")
	// @Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	// public View getView(@PathParam("pid") String pid)
	// {
	// try
	// {
	// return actions.getView(namespace + ":" + pid, ObjectType.ejournal);
	// }
	//
	// catch (ArchiveException e)
	// {
	// throw new HttpArchiveException(
	// Status.INTERNAL_SERVER_ERROR.getStatusCode(),
	// e.getMessage());
	// }
	// }

	@DELETE
	@Path("/{userNamespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public String deleteEJournal(@PathParam("pid") String pid,
			@PathParam("userNamespace") String userNamespace)
	{
		try
		{
			logger.info("delete EJournal");
			return actions.delete(userNamespace + ":" + pid, false);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	@PUT
	@Path("/{userNamespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public String createEJournal(@PathParam("pid") String pid,
			@PathParam("userNamespace") String userNamespace)
	{
		logger.info("create EJournal");
		try
		{
			if (actions.nodeExists(userNamespace + ":" + pid))
			{
				throw new HttpArchiveException(
						Status.INTERNAL_SERVER_ERROR.getStatusCode(),
						userNamespace + ":" + pid
								+ " node already exists. I do nothing!");
			}
			if (userNamespace.compareTo(namespace) != 0)
			{
				throw new HttpArchiveException(
						Status.INTERNAL_SERVER_ERROR.getStatusCode(),
						userNamespace + ":" + pid
								+ " Wrong namespace. Must be " + namespace);
			}
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, false);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(namespace + ":" + pid);

			rootObject.addContentModel(ContentModelFactory.createMonographCM(
					namespace, ejournalType));

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

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readEJournalDC(@PathParam("pid") String pid)
	{
		try
		{
			return actions.readDC(pid);
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
	public String updateEJournalDC(@PathParam("pid") String pid,
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

	@GET
	@Path("/{pid}/metadata")
	@Produces({ "text/plain" })
	public String readEJournalMetadata(@PathParam("pid") String pid)
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

	@PUT
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateEJournalMetadata(@PathParam("pid") String pid,
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
	@POST
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateEJournalMetadataPost(@PathParam("pid") String pid,
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

	@GET
	@Path("/{pid}/volume/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVolumes(@PathParam("pid") String pid)
	{
		try
		{
			return new ObjectList(actions.findObject(pid, HAS_VOLUME));
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	@PUT
	@Path("/{pid}/volume/{volumePid}")
	@Produces({ "application/json", "application/xml" })
	public String createEJournalVolume(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
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
			link.setObject(pid, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(REL_BELONGS_TO_OBJECT);
			link.setObject(pid, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(HAS_VOLUME_NAME);
			link.setObject(volumePid, true);
			rootObject.addRelation(link);

			rootObject.setNamespace(namespace).setPID(volumePid);

			rootObject.addContentModel(ContentModelFactory.createMonographCM(
					namespace, volumeType));

			ComplexObject object = new ComplexObject(rootObject);

			link = new Link();
			link.setPredicate(HAS_VOLUME);
			link.setObject(volumePid, false);
			actions.addLink(pid, link);

			link = new Link();
			link.setPredicate(REL_IS_RELATED);
			link.setObject(volumePid, false);
			actions.addLink(pid, link);

			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}

	}

	// @GET
	// @Path("/{pid}/volume/{volumePid}")
	// @Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	// public View getVolumeView(@PathParam("pid") String pid,
	// @PathParam("volumePid") String volumePid)
	// {
	// try
	// {
	// return actions.getView(volumePid, ObjectType.ejournalVolume);
	// }
	// catch (ArchiveException e)
	// {
	// throw new HttpArchiveException(
	// Status.INTERNAL_SERVER_ERROR.getStatusCode(),
	// e.getMessage());
	// }
	// }

	@GET
	@Path("/{pid}/volume/{volumePid}/data")
	@Produces({ "application/*" })
	public Response readVolumeData(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{
		try
		{
			return actions.readData(volumePid);
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

	@POST
	@Path("/{pid}/volume/{volumePid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/pdf" })
	public String updateVolumeData(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid, byte[] content,
			@Context HttpHeaders headers)
	{
		try
		{
			return actions.updateData(volumePid, content, headers
					.getMediaType().toString());
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

	@GET
	@Path("/{pid}/volume/{volumePid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readVolumeDC(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{
		try
		{
			return actions.readDC(volumePid);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	@POST
	@Path("/{pid}/volume/{volumePid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateVolumeDC(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid, DCBeanAnnotated content)
	{
		try
		{
			return actions.updateDC(volumePid, content);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}

	}

	@GET
	@Path("/{pid}/volume/{volumePid}/metadata")
	@Produces({ "application/*" })
	public String readVolumeMetadata(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{
		try
		{
			return actions.readMetadata(volumePid);
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

	@PUT
	@Path("/{pid}/volume/{volumePid}/metadata")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "text/plain" })
	public String updateVolumeMetadata(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid, String content)
	{
		try
		{
			return actions.updateMetadata(volumePid, content);
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
	@POST
	@Path("/{pid}/volume/{volumePid}/metadata")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "text/plain" })
	public String updateVolumeMetadataPost(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid, String content)
	{
		try
		{
			return actions.updateMetadata(volumePid, content);
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
}
