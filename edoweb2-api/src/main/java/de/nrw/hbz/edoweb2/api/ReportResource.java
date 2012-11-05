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

/**
 * /report/{pid}/[dc|metadata|data]
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@Path("/report")
public class ReportResource
{

	ObjectType objectType = ObjectType.report;
	String namespace = "edoweb";

	Actions actions = new Actions();

	public ReportResource()
	{

	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return new ObjectList(actions.findByType(objectType));
	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public String deleteAll()
	{
		return actions.deleteAll(actions.findByType(objectType));
	}

	@PUT
	@Path("/{pid}")
	public String createReport(@PathParam("pid") String pid)
	{
		System.out.println("CREATE");
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
					.addCreator("ReportRessource")
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
	@Produces({ "application/json", "application/xml" })
	public StatusBean readReport(@PathParam("pid") String pid)
	{
		return actions.read(pid);
	}

	@POST
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateReport(@PathParam("pid") String pid, StatusBean status)
	{
		return actions.update(pid, status);
	}

	@DELETE
	@Path("/{pid}")
	public String deleteReport(@PathParam("pid") String pid)
	{
		System.out.println("DELETE");
		actions.delete(pid);
		return pid + " DELETED!";
	}

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	public DCBeanAnnotated readReportDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}

	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateReportDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return actions.updateDC(pid, content);
	}

	@GET
	@Path("/{pid}/data")
	@Produces({ "application/*" })
	public Response readReportData(@PathParam("pid") String pid)
	{
		return actions.readData(pid);
	}

	@POST
	@Path("/{pid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateReportData(@PathParam("pid") String pid,
			UploadDataBean content)
	{
		return actions.updateData(pid, content);
	}

	@GET
	@Path("/{pid}/metadata")
	public Response readReportMetadata(@PathParam("pid") String pid)
	{
		return actions.readMetadata(pid);
	}

	@POST
	@Path("/{pid}/metadata")
	public String updateReportMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{
		return actions.updateMetadata(pid, content);
	}

	// be it hot or be it not
}
