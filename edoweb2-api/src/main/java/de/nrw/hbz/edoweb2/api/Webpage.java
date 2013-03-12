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
import static de.nrw.hbz.edoweb2.api.Vocabulary.IS_CURRENT_VERSION;
import static de.nrw.hbz.edoweb2.api.Vocabulary.IS_VERSION;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_BELONGS_TO_OBJECT;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_RELATED;
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
public class Webpage
{
	final static Logger logger = LoggerFactory.getLogger(Webpage.class);

	ObjectType webpageType = ObjectType.webpage;
	ObjectType webpageVersionType = ObjectType.webpageVersion;
	String namespace = "edoweb";
	String subnamespace = "edoweb";

	Actions actions = new Actions();

	public Webpage()
	{

	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public MessageBean deleteAll()
	{
		String eJournal = actions.deleteAll(
				actions.findByType(TypeType.contentType.toString() + ":"
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
			link.setObject(TYPE_OBJECT, false);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(pid);

			rootObject.addContentModel(ContentModelFactory.createMonographCM(
					namespace, webpageType));

			ComplexObject object = new ComplexObject(rootObject);
			MessageBean msg = new MessageBean(actions.create(object, true));
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(msg)
					.build();

		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		MessageBean msg = new MessageBean("Create Failed");
		return Response.serverError().type(MediaType.APPLICATION_JSON)
				.entity(msg).build();
	}

	@DELETE
	@Path("/{pid}")
	public MessageBean deleteWebpage(@PathParam("pid") String pid)
	{
		actions.delete(pid, false);
		return new MessageBean(pid + " DELETED!");
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

	@POST
	@Path("/{pid}/metadata")
	public MessageBean updateWebpageMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{
		return new MessageBean(actions.updateMetadata(pid, content));
	}

	@PUT
	@Path("/{pid}/version/{versionPid}")
	@Produces({ "application/json", "application/xml" })
	public MessageBean createWebpageVersion(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		logger.info("create Webpage Version");
		try
		{
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(IS_VERSION);
			link.setObject(pid, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(HAS_VERSION_NAME);
			link.setObject(versionPid, true);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(REL_BELONGS_TO_OBJECT);
			link.setObject(pid, false);
			rootObject.addRelation(link);

			rootObject.setNamespace(namespace).setPID(versionPid);

			rootObject.addContentModel(ContentModelFactory.createMonographCM(
					namespace, webpageType));

			ComplexObject object = new ComplexObject(rootObject);

			link = new Link();
			link.setPredicate(HAS_VERSION);
			link.setObject(versionPid, false);

			actions.addLink(pid, link);

			link = new Link();
			link.setPredicate(REL_IS_RELATED);
			link.setObject(versionPid, false);

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
	@Path("/{pid}/version/{versionPid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public MessageBean updateWebpageVersionDC(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid, DCBeanAnnotated content)
	{
		return new MessageBean(actions.updateDC(versionPid, content));
	}

	@POST
	@Path("/{pid}/version/{versionPid}/data")
	public MessageBean updateWebpageVersionData(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid, UploadDataBean content)
	{
		return new MessageBean(actions.updateData(versionPid, content));
	}

	@POST
	@Path("/{pid}/version/{versionPid}/metadata")
	public MessageBean updateWebpageVersionMetadata(
			@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid, UploadDataBean content)
	{
		return new MessageBean(actions.updateMetadata(versionPid, content));
	}

	@POST
	@Path("/{pid}/current/{versionPid}")
	public MessageBean setCurrentVersion(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		Link link = new Link();
		link.setPredicate(IS_CURRENT_VERSION);
		link.setObject(versionPid);
		return new MessageBean(actions.updateLink(pid, link));
	}

	@GET
	@Path("/{pid}/version/{versionPid}/metadata")
	@Produces({ "application/*" })
	public Response readWebpageVersionMetadata(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		return actions.readMetadata(versionPid);
	}

	@GET
	@Path("/{pid}/version/{versionPid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readWebpageVersionDC(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		return actions.readDC(versionPid);
	}

	@GET
	@Path("/{pid}/version/{versionPid}/data")
	@Produces({ "application/*" })
	public Response readWebpageVersionData(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		return actions.readData(versionPid);
	}

	@GET
	@Path("/{pid}/version/{versionPid}")
	@Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	public View getVersionView(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		return actions.getView(versionPid, ObjectType.webpageVersion);
	}

	@GET
	@Path("/{pid}/version/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVersions(@PathParam("pid") String pid)
	{
		return new ObjectList(actions.findObject(pid, HAS_VERSION));
	}

	@GET
	@Path("/{pid}/metadata")
	public Response readWebpageMetadata(@PathParam("pid") String pid)
	{
		return actions.readMetadata(pid);
	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return new ObjectList(actions.findByType(TypeType.contentType
				.toString() + ":" + webpageType.toString()));
	}

	@GET
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	public View getView(@PathParam("pid") String pid)
	{
		return actions.getView(pid, ObjectType.webpage);
	}

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readWebpageDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}
}
