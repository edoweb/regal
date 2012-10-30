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
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.TYPE_OBJECT;

import java.rmi.RemoteException;

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

@Path("/ejournal")
public class EJournalResource
{

	String IS_VOLUME = HBZ_MODEL_NAMESPACE + "isVolumeOf";
	String HAS_VOLUME = HBZ_MODEL_NAMESPACE + "hasVolume";
	String HAS_VOLUME_NAME = HBZ_MODEL_NAMESPACE + "hasVolumeName";

	ObjectType objectType = ObjectType.ejournal;
	String namespace = "edoweb";

	Actions actions = new Actions();

	public EJournalResource()
	{

	}

	@DELETE
	@Produces("application/json")
	public String deleteAll()
	{
		return actions.deleteAll(actions.findByType(objectType));
	}

	@PUT
	@Path("/{pid}")
	public String createEJournal(@PathParam("pid") String pid)
	{
		System.out.println("create EJournal");
		try
		{
			if (actions.nodeExists(pid))
				return "ERROR: Node already exists";
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(pid)
					.addCreator("EjournalRessource")
					.addType(objectType.toString()).addRights("me");

			rootObject.addContentModel(ContentModelFactory.createReportCM(
					namespace, objectType));

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
	@Path("/{pid}")
	@Produces("application/json")
	public StatusBean readEJournal(@PathParam("pid") String pid)
	{
		return actions.read(pid);
	}

	@POST
	@Path("/{pid}")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json" })
	public String updateEJournal(@PathParam("pid") String pid, StatusBean status)
	{
		return actions.update(pid, status);
	}

	@DELETE
	@Path("/{pid}")
	public String deleteEJournal(@PathParam("pid") String pid)
	{
		System.out.println("delete EJournal");
		actions.delete(pid);
		return pid + " EJournal deleted!";
	}

	@GET
	@Path("/{pid}/dc")
	@Produces("application/json")
	public DCBeanAnnotated readEJournalDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}

	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json" })
	public String updateEJournalDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return actions.updateDC(pid, content);
	}

	// @GET
	// @Path("/{pid}/data")
	// @Produces({ "application/*" })
	// public Response readEJournalData(@PathParam("pid") String pid)
	// {
	// return actions.readData(pid);
	// }

	// @POST
	// @Path("/{pid}/data")
	// @Produces({ "application/xml", "application/json" })
	// @Consumes({ "application/xml", "application/json" })
	// public String updateEJournalData(@PathParam("pid") String pid,
	// UploadDataBean content)
	// {
	// return actions.updateData(pid, content);
	// }

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

	@PUT
	@Path("/{pid}/volume/{volId}")
	public String createEJournalVolume(@PathParam("pid") String pid,
			@PathParam("volId") String volId)
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
			link.setPredicate(this.HAS_VOLUME_NAME);
			link.setObject(volId, true);
			rootObject.addRelation(link);

			rootObject.setNamespace(namespace).setPID(volumeId)
					.addCreator("EjournalVolumeRessource")
					.addType(objectType.toString()).addRights("me");

			rootObject.addContentModel(ContentModelFactory.createReportCM(
					namespace, objectType));

			ComplexObject object = new ComplexObject(rootObject);

			link = new Link();
			link.setPredicate(this.HAS_VOLUME);
			link.setObject(volumeId, false);
			actions.addLink(pid, link);

			return actions.create(object);

		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "create EJournal Volume Failed";
	}

	@GET
	@Path("/{pid}/volume/{volId}/data")
	@Produces({ "application/*" })
	public Response readVolumeData(@PathParam("pid") String pid,
			@PathParam("volId") String volId)
	{
		String volumePid = null;
		// 1. Ask pid about a volume with Id volId
		// 2. Get the fedoraPid and store it in volumePid

		// pid hasVolume ?volumePid
		// ?volumePid hasName volId

		return actions.readData(volumePid);
	}

	@POST
	@Path("/{pid}/volume/{volId}/data")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json" })
	public String updateVolumeData(@PathParam("pid") String pid,
			UploadDataBean content)
	{
		String volumePid = null;
		return actions.updateData(volumePid, content);
	}

	@GET
	@Path("/{pid}/volume/{volId}/dc")
	@Produces({ "application/*" })
	public DCBeanAnnotated readVolumeDC(@PathParam("pid") String pid)
	{
		String volumePid = null;
		return actions.readDC(volumePid);
	}

	@POST
	@Path("/{pid}/volume/{volId}/dc")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json" })
	public String updateVolumeDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		String volumePid = null;
		return actions.updateDC(volumePid, content);
	}

	@GET
	@Path("/{pid}/volume/{volId}/metadata")
	@Produces({ "application/*" })
	public Response readVolumeMetadata(@PathParam("pid") String pid)
	{
		String volumePid = null;
		return actions.readMetadata(volumePid);
	}

	@POST
	@Path("/{pid}/volume/{volId}/metadata")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json" })
	public String updateVolumeMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{
		String volumePid = null;
		return actions.updateMetadata(volumePid, content);
	}
}
