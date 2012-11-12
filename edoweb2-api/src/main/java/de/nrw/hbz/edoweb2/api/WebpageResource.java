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
@Path("/webpage")
public class WebpageResource
{
	String IS_VERSION = HBZ_MODEL_NAMESPACE + "isVersionOf";
	String HAS_VERSION = HBZ_MODEL_NAMESPACE + "hasVersion";
	String HAS_VERSION_NAME = HBZ_MODEL_NAMESPACE + "hasVersionName";
	String IS_CURRENT_VERSION = HBZ_MODEL_NAMESPACE + "isCurrentVersion";

	ObjectType webpageType = ObjectType.webpage;
	ObjectType webpageVersionType = ObjectType.webpageVersion;
	String namespace = "edoweb";

	Actions actions = new Actions();

	public WebpageResource()
	{

	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return new ObjectList(actions.findByType(webpageType));
	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public String deleteAll()
	{
		String eJournal = actions.deleteAll(actions.findByType(webpageType));
		String eJournalVolume = actions.deleteAll(actions
				.findByType(webpageVersionType));
		return eJournal + "\n" + eJournalVolume;
	}

	@PUT
	@Path("/{pid}")
	public String createWebpage(@PathParam("pid") String pid)
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
					.addCreator("WebpageRessource")
					.addType(webpageType.toString()).addRights("me");

			rootObject.addContentModel(ContentModelFactory.createReportCM(
					namespace, webpageType));

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
	public StatusBean readWebpage(@PathParam("pid") String pid)
	{
		return actions.read(pid);
	}

	@POST
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateWebpage(@PathParam("pid") String pid, StatusBean status)
	{
		return actions.update(pid, status);
	}

	@DELETE
	@Path("/{pid}")
	public String deleteWebpage(@PathParam("pid") String pid)
	{
		System.out.println("DELETE");
		actions.delete(pid);
		return pid + " DELETED!";
	}

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	public DCBeanAnnotated readWebpageDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}

	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateWebpageDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return actions.updateDC(pid, content);
	}

	@GET
	@Path("/{pid}/metadata")
	public Response readWebpageMetadata(@PathParam("pid") String pid)
	{
		return actions.readMetadata(pid);
	}

	@POST
	@Path("/{pid}/metadata")
	public String updateWebpageMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{
		return actions.updateMetadata(pid, content);
	}

	@PUT
	@Path("/{pid}/version/{versionName}")
	public String createWebpageVersion(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName)
	{
		System.out.println("create Webpage Version");
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
			link.setPredicate(this.IS_VERSION);
			link.setObject(pid, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(this.HAS_VERSION_NAME);
			link.setObject(versionName, true);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(REL_BELONGS_TO_OBJECT);
			link.setObject(pid, false);
			rootObject.addRelation(link);

			rootObject.setNamespace(namespace).setPID(volumeId)
					.addType(ObjectType.webpageVersion.toString())
					.addRights("me");

			rootObject.addContentModel(ContentModelFactory.createReportCM(
					namespace, webpageType));

			ComplexObject object = new ComplexObject(rootObject);

			link = new Link();
			link.setPredicate(this.HAS_VERSION);
			link.setObject(volumeId, false);
			actions.addLink(pid, link);

			String result = actions.create(object);
			// actions.addChildToParent(volumeId, pid);
			return result;
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return "create WebpageVersion Failed";
	}

	@POST
	@Path("/{pid}/version/{versionName}/dc")
	public String updateWebpageVersionDC(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName,
			DCBeanAnnotated content)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);
		return actions.updateDC(versionPid, content);
	}

	@POST
	@Path("/{pid}/version/{versionName}/data")
	public String updateWebpageVersionData(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName, UploadDataBean content)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);
		return actions.updateData(versionPid, content);
	}

	@POST
	@Path("/{pid}/version/{versionName}/metadata")
	public String updateWebpageVersionMetadata(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName, UploadDataBean content)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);
		return actions.updateMetadata(versionPid, content);
	}

	@GET
	@Path("/{pid}/version/{versionName}/metadata")
	@Produces({ "application/*" })
	public Response readWebpageVersionMetadata(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);
		return actions.readMetadata(versionPid);
	}

	@GET
	@Path("/{pid}/version/{versionName}/dc")
	@Produces({ "application/*" })
	public DCBeanAnnotated readWebpageVersionDC(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);
		return actions.readDC(versionPid);
	}

	@GET
	@Path("/{pid}/version/{versionName}/data")
	@Produces({ "application/*" })
	public Response readWebpageVersionData(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);
		return actions.readData(versionPid);
	}

	@GET
	@Path("/{pid}/version/{versionName}")
	@Produces({ "application/json", "application/xml" })
	public String readWebpageVersion(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);
		return versionPid;
	}

	@GET
	@Path("/{pid}/version/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVersions(@PathParam("pid") String pid)
	{
		Vector<String> v = new Vector<String>();

		for (String volPid : actions.findObject(pid, HAS_VERSION))
		{

			v.add(actions.findObject(volPid, HAS_VERSION).get(0));

		}
		return new ObjectList(v);
	}

	@POST
	@Path("/{pid}/current/{versionName}")
	public String setCurrentVersion(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);
		Link link = new Link();
		link.setPredicate(IS_CURRENT_VERSION);
		link.setObject(versionPid);
		return actions.updateLink(pid, link);
	}

	String getVersionQuery(String versionName, String pid)
	{
		return "SELECT ?volPid ?p ?o WHERE "
				+ "	{"
				+ "	?volPid <info:hbz/hbz-ingest:def/model#isVersionOf> <info:fedora/"
				+ pid
				+ "> . ?volPid <info:hbz/hbz-ingest:def/model#hasVersionName> \""
				+ versionName + "\". ?volPid ?p ?o .	} ";
	}
}
