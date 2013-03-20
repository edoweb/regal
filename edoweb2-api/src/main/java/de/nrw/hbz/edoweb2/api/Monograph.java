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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.edoweb2.datatypes.ComplexObject;
import de.nrw.hbz.edoweb2.datatypes.Link;
import de.nrw.hbz.edoweb2.datatypes.Node;

/**
 * /monograph/{pid}/[dc|metadata|data]
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@Path("/monograph")
public class Monograph
{
	final static Logger logger = LoggerFactory.getLogger(Monograph.class);
	ObjectType objectType = ObjectType.monograph;
	String namespace = "edoweb";

	Actions actions = new Actions();

	public Monograph()
	{

	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public MessageBean deleteAll()
	{
		return new MessageBean(actions.deleteAll(
				actions.findByType(TypeType.contentType.toString() + ":"
						+ objectType.toString()), false));
	}

	@PUT
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	public Response createMonograph(@PathParam("pid") String pid)
	{
		logger.info("create Monograph");
		try
		{
			if (actions.nodeExists(pid))
			{
				// String msg =
				// "{\"message\":\" Node already exists. I do nothing!\"}";
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
					namespace, objectType));

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

	// @POST
	// @Path("/{pid}")
	// @Produces({ "application/json", "application/xml" })
	// @Consumes({ "application/json", "application/xml" })
	// public MessageBean updateMonograph(@PathParam("pid") String pid,
	// StatusBean status)
	// {
	// return new MessageBean(actions.update(pid, status, false));
	// }

	@DELETE
	@Path("/{pid}")
	public MessageBean deleteMonograph(@PathParam("pid") String pid)
	{
		logger.info("DELETE");
		actions.delete(pid, false);
		return new MessageBean(pid + " DELETED!");
	}

	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public MessageBean updateMonographDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return new MessageBean(actions.updateDC(pid, content));
	}

	@GET
	@Path("/{pid}/data")
	@Produces({ "application/*" })
	public Response readMonographData(@PathParam("pid") String pid)
	{
		return actions.readData(pid);
	}

	@POST
	@Path("/{pid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/pdf" })
	public MessageBean updateMonographData(@PathParam("pid") String pid,
			byte[] content, @Context HttpHeaders headers)
	{
		return new MessageBean(actions.updateData(pid, content, headers
				.getMediaType().toString()));
	}

	@GET
	@Path("/{pid}/metadata")
	public Response readMonographMetadata(@PathParam("pid") String pid)
	{
		return actions.readMetadata(pid);
	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return new ObjectList(actions.findByType(TypeType.contentType
				.toString() + ":" + objectType.toString()));
	}

	// @GET
	// @Path("/{pid}")
	// @Produces({ "application/json", "application/xml" })
	// public StatusBean readMonograph(@PathParam("pid") String pid)
	// {
	// return actions.read(pid);
	// }

	@GET
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	public View getView(@PathParam("pid") String pid)
	{
		return actions.getView(pid, this.objectType);
	}

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readMonographDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}

	@PUT
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	public MessageBean updateMonographMetadata(@PathParam("pid") String pid,
			String content)
	{
		return new MessageBean(actions.updateMetadata(pid, content));
	}

	@Deprecated
	@POST
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	public MessageBean updateMonographMetadataPost(
			@PathParam("pid") String pid, String content)
	{
		return new MessageBean(actions.updateMetadata(pid, content));
	}

	// be it hot or be it not
}
