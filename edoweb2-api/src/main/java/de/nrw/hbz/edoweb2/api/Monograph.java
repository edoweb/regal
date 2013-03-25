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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.edoweb2.archive.exceptions.ArchiveException;
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

	Actions actions = null;

	public Monograph() throws IOException
	{
		actions = new Actions();
	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public String deleteAll()
	{
		try
		{
			return actions.deleteAll(
					actions.findByType(TypeType.contentType.toString() + ":"
							+ objectType.toString()), false);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	@PUT
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	public String createMonograph(@PathParam("pid") String pid)
	{
		logger.info("create Monograph");
		try
		{
			if (actions.nodeExists(pid))
			{
				throw new HttpArchiveException(
						Status.INTERNAL_SERVER_ERROR.getStatusCode(),
						"Node already exists. I do nothing!");
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
			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
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
	@Produces({ "application/json", "application/xml" })
	public String deleteMonograph(@PathParam("pid") String pid)
	{
		try
		{
			return actions.delete(pid, false);
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
	public String updateMonographDC(@PathParam("pid") String pid,
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
	@Path("/{pid}/data")
	@Produces({ "application/*", "application/json" })
	public Response readMonographData(@PathParam("pid") String pid)
	{
		try
		{
			return actions.readData(pid);
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
	@Path("/{pid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/pdf" })
	public String updateMonographData(@PathParam("pid") String pid,
			byte[] content, @Context HttpHeaders headers)
	{
		try
		{

			return actions.updateData(pid, content, headers.getMediaType()
					.toString());

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
		catch (NullPointerException e)
		{

			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					"You must provide a mimeType via http header Content-Type.");
		}
	}

	@GET
	@Path("/{pid}/metadata")
	@Produces({ "text/plain" })
	public String readMonographMetadata(@PathParam("pid") String pid)
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

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		try
		{
			return new ObjectList(actions.findByType(TypeType.contentType
					.toString() + ":" + objectType.toString()));
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
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
		try
		{
			return actions.getView(pid, this.objectType);
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
	public DCBeanAnnotated readMonographDC(@PathParam("pid") String pid)
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

	@PUT
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateMonographMetadata(@PathParam("pid") String pid,
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
	public String updateMonographMetadataPost(@PathParam("pid") String pid,
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

	// be it hot or be it not
}
