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

import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.HBZ_MODEL_NAMESPACE;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_BELONGS_TO_OBJECT;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_NODE_TYPE;
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
import javax.ws.rs.core.Response;

import de.nrw.hbz.edoweb2.datatypes.ComplexObject;
import de.nrw.hbz.edoweb2.datatypes.Link;
import de.nrw.hbz.edoweb2.datatypes.Node;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@Path("/ejournal")
public class EJournalResource
{

	String IS_VOLUME = HBZ_MODEL_NAMESPACE + "isVolumeOf";
	String HAS_VOLUME = HBZ_MODEL_NAMESPACE + "hasVolume";
	String HAS_VOLUME_NAME = HBZ_MODEL_NAMESPACE + "hasVolumeName";

	ObjectType ejournalType = ObjectType.ejournal;
	ObjectType volumeType = ObjectType.ejournalVolume;
	String namespace = "edoweb";

	Actions actions = new Actions();

	public EJournalResource()
	{

	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return new ObjectList(actions.findByType(ejournalType));
	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public String deleteAll()
	{
		String eJournal = actions.deleteAll(actions.findByType(ejournalType));
		String eJournalVolume = actions.deleteAll(actions
				.findByType(volumeType));
		return eJournal + "\n" + eJournalVolume;
	}

	@GET
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public StatusBean readEJournal(@PathParam("pid") String pid,
			@PathParam("namespace") String userNamespace)
	{
		System.out.println("read EJournal");
		return actions.read(namespace + ":" + pid);
	}

	@POST
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateEJournal(@PathParam("pid") String pid,
			StatusBean status, @PathParam("namespace") String userNamespace)
	{
		return actions.update(namespace + ":" + pid, status);
	}

	@DELETE
	@Path("/{namespace}:{pid}")
	public String deleteEJournal(@PathParam("pid") String pid,
			@PathParam("namespace") String userNamespace)
	{
		System.out.println("delete EJournal");
		actions.delete(namespace + ":" + pid);
		return namespace + ":" + pid + " EJournal deleted!";
	}

	@PUT
	@Path("/{namespace}:{pid}")
	public String createEJournal(@PathParam("pid") String pid,
			@PathParam("namespace") String userNamespace)
	{
		System.out.println("create EJournal");
		try
		{
			if (actions.nodeExists(pid))
				return "ERROR: Node already exists";
			if (userNamespace.compareTo(namespace) != 0)
				return "ERROR: Namespace MUST be " + namespace;
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(namespace + ":" + pid)
					.addCreator("EjournalRessource")
					.addType(ejournalType.toString()).addRights("me");

			rootObject.addContentModel(ContentModelFactory.createReportCM(
					namespace, ejournalType));

			ComplexObject object = new ComplexObject(rootObject);
			return actions.create(object);

		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Create Failed";
	}

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	public DCBeanAnnotated readEJournalDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}

	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateEJournalDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return actions.updateDC(pid, content);
	}

	@GET
	@Path("/{pid}/metadata")
	public Response readEJournalMetadata(@PathParam("pid") String pid)
	{
		return actions.readMetadata(pid);
	}

	@POST
	@Path("/{pid}/metadata")
	public String updateEJournalMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{
		return actions.updateMetadata(pid, content);
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
	public String createEJournalVolume(@PathParam("pid") String pid,
			@PathParam("volName") String volName)
	{

		System.out.println("create EJournal Volume");
		try
		{

			String volumeId = actions.getPid(namespace);
			if (actions.nodeExists(volumeId))
				return "ERROR: Node already exists";
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(this.IS_VOLUME);
			link.setObject(pid, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(REL_BELONGS_TO_OBJECT);
			link.setObject(pid, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(this.HAS_VOLUME_NAME);
			link.setObject(volName, true);
			rootObject.addRelation(link);

			rootObject.setNamespace(namespace).setPID(volumeId)
					.addCreator("EjournalVolumeRessource")
					.addType(volumeType.toString()).addRights("me");

			rootObject.addContentModel(ContentModelFactory.createReportCM(
					namespace, volumeType));

			ComplexObject object = new ComplexObject(rootObject);

			link = new Link();
			link.setPredicate(this.HAS_VOLUME);
			link.setObject(volumeId, false);

			link = new Link();
			link.setPredicate(this.HAS_VOLUME);
			link.setObject(volumeId, false);

			actions.addLink(pid, link);

			String result = actions.create(object);
			// actions.addChildToParent(volumeId, pid);
			return result;
		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "create EJournal Volume Failed";
	}

	@GET
	@Path("/{pid}/volume/{volName}")
	@Produces({ "application/*" })
	public StatusBean readVolume(@PathParam("pid") String pid,
			@PathParam("volName") String volName)
	{
		String volumePid = null;
		String query = getVolumeQuery(volName, pid);
		volumePid = actions.findSubject(query);

		return actions.read(volumePid);
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
	public String updateVolumeData(@PathParam("pid") String pid,
			@PathParam("volName") String volName, UploadDataBean content)
	{
		String volumePid = null;
		String query = getVolumeQuery(volName, pid);
		volumePid = actions.findSubject(query);
		return actions.updateData(volumePid, content);
	}

	@GET
	@Path("/{pid}/volume/{volName}/dc")
	@Produces({ "application/*" })
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
	public String updateVolumeDC(@PathParam("pid") String pid,
			@PathParam("volName") String volName, DCBeanAnnotated content)
	{
		String volumePid = null;
		String query = getVolumeQuery(volName, pid);
		volumePid = actions.findSubject(query);
		return actions.updateDC(volumePid, content);
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
	public String updateVolumeMetadata(@PathParam("pid") String pid,
			@PathParam("volName") String volName, UploadDataBean content)
	{
		String volumePid = null;
		String query = getVolumeQuery(volName, pid);
		volumePid = actions.findSubject(query);
		return actions.updateMetadata(volumePid, content);
	}

	private String getVolumeQuery(String volName, String pid)
	{
		return "SELECT ?volPid ?p ?o WHERE "
				+ "	{"
				+ "	?volPid <info:hbz/hbz-ingest:def/model#isVolumeOf> <info:fedora/"
				+ pid
				+ "> . ?volPid <info:hbz/hbz-ingest:def/model#hasVolumeName> \""
				+ volName + "\". ?volPid ?p ?o .	} ";
	}
}
