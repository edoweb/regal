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

	/**
	 * Deletes all reports curl -X DELETE
	 * http://localhost:8080/edoweb2-api/report; echo
	 * 
	 * @return
	 */
	@DELETE
	@Produces("application/json")
	public String deleteAll()
	{
		return actions.deleteAll(actions.findByType(objectType));
	}

	/**
	 * Erzeugt eine neue Amtsdruckschrift. curl -X PUT
	 * http://localhost:8080/edoweb2-api/report/edoweb:123; echo
	 * 
	 * @param pid
	 *            Die gewünschte PID für die Amtsdruckschrift
	 * @param content
	 *            Administrative Metadaten
	 * @return
	 */
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

	/**
	 * Liest die administrativen Metadaten einer Amtsdruckschrift curl -X GET
	 * http://localhost:8080/edoweb2-api/report/edoweb:123; echo
	 * 
	 * @param pid
	 * @return
	 */
	@GET
	@Path("/{pid}")
	@Produces("application/json")
	public StatusBean readReport(@PathParam("pid") String pid)
	{
		return actions.read(pid);
	}

	/**
	 * Updated die Administrativen Metadaten einer Amtsdruckschrift curl -XPOST
	 * -H "accept:application/json" -H "Content-Type: application/json"
	 * http://localhost:8080/edoweb2-api/report/edoweb:123 -d
	 * '{"dc":{"creator":"fedoraAdmin","identifier":"edoweb:123","type":"report"
	 * } , " d o c T y p e " : " r e p o r t " , " v i s i b l e F o r " :
	 * " m e " } ' ; echo
	 * 
	 * @param pid
	 * @param content
	 * @return
	 */
	@POST
	@Path("/{pid}")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json" })
	public String updateReport(@PathParam("pid") String pid, StatusBean status)
	{
		return actions.update(pid, status);
	}

	/**
	 * Löscht die komplette Amtsdruckschrift. curl -X DELETE
	 * http://localhost:8080/edoweb2-api/report/edoweb:123; echo
	 * 
	 * @param pid
	 * @return
	 */
	@DELETE
	@Path("/{pid}")
	public String deleteReport(@PathParam("pid") String pid)
	{
		System.out.println("DELETE");
		actions.delete(pid);
		return pid + " DELETED!";
	}

	/**
	 * Liest die Dublin Core Metadaten einer Amtsdruckschrift
	 * 
	 * @param pid
	 * @return
	 */
	@GET
	@Path("/{pid}/dc")
	@Produces("application/json")
	public DCBeanAnnotated readReportDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}

	/**
	 * Updated die Dublin Core Metadaten einer Amtsdruckschrift
	 * 
	 * @param pid
	 * @param content
	 * @return
	 */
	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json" })
	public String updateReportDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return actions.updateDC(pid, content);
	}

	/**
	 * Liest die Daten einer Amtsdruckschrift
	 * 
	 * @param pid
	 * @return
	 */
	@GET
	@Path("/{pid}/data")
	@Produces({ "application/*" })
	public Response readReportData(@PathParam("pid") String pid)
	{
		return actions.readData(pid);
	}

	/**
	 * Updated die Daten einer Amtsdruckschrift
	 * 
	 * @param pid
	 * @param content
	 * @return
	 */
	@POST
	@Path("/{pid}/data")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json" })
	public String updateReportData(@PathParam("pid") String pid,
			UploadDataBean content)
	{
		return actions.updateData(pid, content);
	}

	/**
	 * Liest die Metadaten einer Amtsdruckschrift
	 * 
	 * @param pid
	 * @return
	 */
	@GET
	@Path("/{pid}/metadata")
	public Response readReportMetadata(@PathParam("pid") String pid)
	{
		return actions.readMetadata(pid);
	}

	/**
	 * Updated die Metadaten einer Amtsdruckschrift
	 * 
	 * @param pid
	 * @param content
	 * @return
	 */
	@POST
	@Path("/{pid}/metadata")
	public String updateReportMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{
		return actions.updateMetadata(pid, content);
	}

	// be it hot or be it not
}
