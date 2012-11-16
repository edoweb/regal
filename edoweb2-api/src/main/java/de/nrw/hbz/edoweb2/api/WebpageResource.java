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
@Path("/webpage")
public class WebpageResource
{
	final static Logger logger = LoggerFactory.getLogger(WebpageResource.class);
	String IS_VERSION = HBZ_MODEL_NAMESPACE + "isVersionOf";
	String HAS_VERSION = HBZ_MODEL_NAMESPACE + "hasVersion";
	String HAS_VERSION_NAME = HBZ_MODEL_NAMESPACE + "hasVersionName";
	String IS_CURRENT_VERSION = HBZ_MODEL_NAMESPACE + "isCurrentVersion";

	ObjectType webpageType = ObjectType.webpage;
	ObjectType webpageVersionType = ObjectType.webpageVersion;
	String namespace = "edoweb";
	String subnamespace = "edoweb";

	Actions actions = new Actions();

	public WebpageResource()
	{

	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return new ObjectList(actions.findByType("doc-type:"
				+ webpageType.toString()));
	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public MessageBean deleteAll()
	{
		String eJournal = actions
				.deleteAll(actions.findByType("doc-type:"
						+ webpageType.toString()), false);
		String eJournalVolume = actions.deleteAll(
				actions.findByType(webpageVersionType.toString()), false);
		return new MessageBean(eJournal + "\n" + eJournalVolume);
	}

	@PUT
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	public Response createWebpage(@PathParam("pid") String pid)
	{
		logger.info("create Webpage");
		try
		{
			if (actions.nodeExists(pid))
			{
				MessageBean msg = new MessageBean(
						"Node already exists. I do nothing!");
				Response response = Response.status(409)
						.type(MediaType.APPLICATION_JSON).entity(msg).build();
				logger.warn("Node exists: " + pid);
				return response;
			}
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
	public MessageBean updateWebpage(@PathParam("pid") String pid,
			StatusBean status)
	{
		return new MessageBean(actions.update(pid, status, false));
	}

	@DELETE
	@Path("/{pid}")
	public MessageBean deleteWebpage(@PathParam("pid") String pid)
	{
		actions.delete(pid, false);
		return new MessageBean(pid + " DELETED!");
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
	public MessageBean updateWebpageDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return new MessageBean(actions.updateDC(pid, content));
	}

	@GET
	@Path("/{pid}/metadata")
	public Response readWebpageMetadata(@PathParam("pid") String pid)
	{
		return actions.readMetadata(pid);
	}

	@POST
	@Path("/{pid}/metadata")
	public MessageBean updateWebpageMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{
		return new MessageBean(actions.updateMetadata(pid, content));
	}

	@PUT
	@Path("/{pid}/version/{versionName}")
	@Produces({ "application/json", "application/xml" })
	public MessageBean createWebpageVersion(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName)
	{
		logger.info("create Webpage Version");
		try
		{
			String volumeId = actions.getPid(namespace);
			if (actions.nodeExists(volumeId))
				return new MessageBean("ERROR: Node already exists");
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

			return new MessageBean(actions.create(object, true));
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return new MessageBean("create WebpageVersion Failed");
	}

	@POST
	@Path("/{pid}/version/{versionName}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public MessageBean updateWebpageVersionDC(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName,
			DCBeanAnnotated content)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);
		return new MessageBean(actions.updateDC(versionPid, content));
	}

	@POST
	@Path("/{pid}/version/{versionName}/data")
	public MessageBean updateWebpageVersionData(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName, UploadDataBean content)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);
		return new MessageBean(actions.updateData(versionPid, content));
	}

	@POST
	@Path("/{pid}/version/{versionName}/metadata")
	public MessageBean updateWebpageVersionMetadata(
			@PathParam("pid") String pid,
			@PathParam("versionName") String versionName, UploadDataBean content)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);
		return new MessageBean(actions.updateMetadata(versionPid, content));
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
	@Produces({ "application/json", "application/xml" })
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
	public StatusBean readWebpageVersion(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);

		return actions.read(versionPid);
	}

	@GET
	@Path("/{pid}/version/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVersions(@PathParam("pid") String pid)
	{
		Vector<String> v = new Vector<String>();

		for (String volPid : actions.findObject(pid, HAS_VERSION))
		{

			v.add(actions.findObject(volPid, HAS_VERSION_NAME).get(0));

		}
		return new ObjectList(v);
	}

	@POST
	@Path("/{pid}/current/{versionName}")
	public MessageBean setCurrentVersion(@PathParam("pid") String pid,
			@PathParam("versionName") String versionName)
	{
		String versionPid = null;
		String query = getVersionQuery(versionName, pid);
		versionPid = actions.findSubject(query);
		Link link = new Link();
		link.setPredicate(IS_CURRENT_VERSION);
		link.setObject(versionPid);
		return new MessageBean(actions.updateLink(pid, link));
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
