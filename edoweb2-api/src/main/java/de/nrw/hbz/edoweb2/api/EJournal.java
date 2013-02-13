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

import java.rmi.RemoteException;
import java.util.Vector;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	Actions actions = new Actions();

	public EJournal()
	{

	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return new ObjectList(actions.findByType(TypeType.contentType
				.toString() + ":" + ejournalType.toString()));
	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public MessageBean deleteAll()
	{
		String eJournal = actions.deleteAll(
				actions.findByType(TypeType.contentType.toString() + ":"
						+ ejournalType.toString()), false);
		String eJournalVolume = actions.deleteAll(
				actions.findByType(volumeType.toString()), false);
		return new MessageBean(eJournal + "\n" + eJournalVolume);
	}

	// @GET
	// @Path("/{namespace}:{pid}")
	// @Produces({ "application/json", "application/xml" })
	// public StatusBean readEJournal(@PathParam("pid") String pid,
	// @PathParam("namespace") String userNamespace)
	// {
	//
	// return actions.read(namespace + ":" + pid);
	// }

	@GET
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	public View getView(@PathParam("pid") String pid)
	{
		return actions.getView(namespace + ":" + pid, ObjectType.ejournal);
	}

	// @POST
	// @Path("/{namespace}:{pid}")
	// @Produces({ "application/json", "application/xml" })
	// @Consumes({ "application/json", "application/xml" })
	// public MessageBean updateEJournal(@PathParam("pid") String pid,
	// StatusBean status, @PathParam("namespace") String userNamespace)
	// {
	// return new MessageBean(actions.update(namespace + ":" + pid, status,
	// false));
	// }

	@DELETE
	@Path("/{namespace}:{pid}")
	public MessageBean deleteEJournal(@PathParam("pid") String pid,
			@PathParam("namespace") String userNamespace)
	{
		logger.info("delete EJournal");
		actions.delete(namespace + ":" + pid, false);
		return new MessageBean(namespace + ":" + pid + " EJournal deleted!");
	}

	@PUT
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public Response createEJournal(@PathParam("pid") String pid,
			@PathParam("namespace") String userNamespace)
	{
		logger.info("create EJournal");
		try
		{
			if (actions.nodeExists(namespace + ":" + pid))
			{
				logger.warn("Node exists: " + pid);
				MessageBean msg = new MessageBean(
						"Node already exists. I do nothing!");
				Response response = Response.status(409)
						.type(MediaType.APPLICATION_JSON).entity(msg).build();
				logger.warn("Node exists: " + pid);
				return response;
			}
			if (userNamespace.compareTo(namespace) != 0)
			{
				MessageBean msg = new MessageBean(" Wrong namespace. Must be "
						+ namespace);
				Response response = Response.status(409)
						.type(MediaType.APPLICATION_JSON).entity(msg).build();
				logger.warn("Node exists: " + pid);
				return response;
			}
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, false);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(namespace + ":" + pid)
					.addCreator("EjournalRessource")
					.addType(ejournalType.toString()).addRights("me");

			rootObject.addContentModel(ContentModelFactory.createMonographCM(
					namespace, ejournalType));

			ComplexObject object = new ComplexObject(rootObject);
			MessageBean msg = new MessageBean(actions.create(object, true));
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(msg)
					.build();

		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MessageBean msg = new MessageBean("Create Failed");
		return Response.serverError().type(MediaType.APPLICATION_JSON)
				.entity(msg).build();
	}

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readEJournalDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}

	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public MessageBean updateEJournalDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return new MessageBean(actions.updateDC(pid, content));
	}

	@GET
	@Path("/{pid}/metadata")
	public Response readEJournalMetadata(@PathParam("pid") String pid)
	{
		return actions.readMetadata(pid);
	}

	@POST
	@Path("/{pid}/metadata")
	public MessageBean updateEJournalMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{
		return new MessageBean(actions.updateMetadata(pid, content));
	}

	@GET
	@Path("/{pid}/volume/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVolumes(@PathParam("pid") String pid)
	{
		Vector<String> v = new Vector<String>();

		for (String volPid : actions.findObject(pid, HAS_VOLUME))
		{

			v.add(actions.findObject(volPid, HAS_VOLUME_NAME).get(0));

		}
		return new ObjectList(v);
	}

	@PUT
	@Path("/{pid}/volume/{volName}")
	@Produces({ "application/json", "application/xml" })
	public MessageBean createEJournalVolume(@PathParam("pid") String pid,
			@PathParam("volName") String volName)
	{

		logger.info("create EJournal Volume");
		try
		{

			String volumeId = actions.getPid(namespace);
			if (actions.nodeExists(volumeId))
				return new MessageBean("ERROR: Node already exists");
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
			link.setObject(volName, true);
			rootObject.addRelation(link);

			rootObject.setNamespace(namespace).setPID(volumeId)
					.addCreator("EjournalVolumeRessource")
					.addType(volumeType.toString()).addRights("me");

			rootObject.addContentModel(ContentModelFactory.createMonographCM(
					namespace, volumeType));

			ComplexObject object = new ComplexObject(rootObject);

			link = new Link();
			link.setPredicate(HAS_VOLUME);
			link.setObject(volumeId, false);
			actions.addLink(pid, link);

			link = new Link();
			link.setPredicate(REL_IS_RELATED);
			link.setObject(volumeId, false);
			actions.addLink(pid, link);

			return new MessageBean(actions.create(object, true));

		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return new MessageBean("create eJournal Volume Failed");
	}

	// @GET
	// @Path("/{pid}/volume/{volName}")
	// @Produces({ "application/*" })
	// public StatusBean readVolume(@PathParam("pid") String pid,
	// @PathParam("volName") String volName)
	// {
	// String volumePid = null;
	// String query = getVolumeQuery(volName, pid);
	// volumePid = actions.findSubject(query);
	//
	// return actions.read(volumePid);
	// }

	@GET
	@Path("/{pid}/volume/{volName}")
	@Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	public View getVolumeView(@PathParam("pid") String pid,
			@PathParam("volName") String volName)
	{
		String volumePid = null;
		String query = getVolumeQuery(volName, pid);
		volumePid = actions.findSubject(query);
		return actions.getView(volumePid, ObjectType.ejournalVolume);
	}

	@GET
	@Path("/{pid}/volume/{volName}/data")
	@Produces({ "application/*" })
	public Response readVolumeData(@PathParam("pid") String pid,
			@PathParam("volName") String volName)
	{
		String volumePid = null;
		String query = getVolumeQuery(volName, pid);
		volumePid = actions.findSubject(query);

		return actions.readData(volumePid);
	}

	@POST
	@Path("/{pid}/volume/{volName}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public MessageBean updateVolumeData(@PathParam("pid") String pid,
			@PathParam("volName") String volName, UploadDataBean content)
	{
		String volumePid = null;
		String query = getVolumeQuery(volName, pid);
		volumePid = actions.findSubject(query);
		return new MessageBean(actions.updateData(volumePid, content));
	}

	@GET
	@Path("/{pid}/volume/{volName}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readVolumeDC(@PathParam("pid") String pid,
			@PathParam("volName") String volName)
	{
		String volumePid = null;
		String query = getVolumeQuery(volName, pid);
		volumePid = actions.findSubject(query);
		return actions.readDC(volumePid);
	}

	@POST
	@Path("/{pid}/volume/{volName}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public MessageBean updateVolumeDC(@PathParam("pid") String pid,
			@PathParam("volName") String volName, DCBeanAnnotated content)
	{
		String volumePid = null;
		String query = getVolumeQuery(volName, pid);
		volumePid = actions.findSubject(query);
		return new MessageBean(actions.updateDC(volumePid, content));
	}

	@GET
	@Path("/{pid}/volume/{volName}/metadata")
	@Produces({ "application/*" })
	public Response readVolumeMetadata(@PathParam("pid") String pid,
			@PathParam("volName") String volName)
	{
		String volumePid = null;
		String query = getVolumeQuery(volName, pid);
		volumePid = actions.findSubject(query);
		return actions.readMetadata(volumePid);
	}

	@POST
	@Path("/{pid}/volume/{volName}/metadata")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public MessageBean updateVolumeMetadata(@PathParam("pid") String pid,
			@PathParam("volName") String volName, UploadDataBean content)
	{
		String volumePid = null;
		String query = getVolumeQuery(volName, pid);
		volumePid = actions.findSubject(query);
		return new MessageBean(actions.updateMetadata(volumePid, content));
	}

	public static String getVolumeQuery(String volName, String pid)
	{
		return "SELECT ?volPid ?p ?o WHERE "
				+ "	{"
				+ "	?volPid <info:hbz/hbz-ingest:def/model#isVolumeOf> <info:fedora/"
				+ pid
				+ "> . ?volPid <info:hbz/hbz-ingest:def/model#hasVolumeName> \""
				+ volName + "\". ?volPid ?p ?o .	} ";
	}
}
