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

import java.io.IOException;

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

import com.sun.jersey.multipart.MultiPart;

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

	DeleteResource delete = null;
	GetResource get = null;
	PutResource put = null;
	PostResource post = null;

	public Monograph() throws IOException
	{
		delete = new DeleteResource();
		get = new GetResource();
		put = new PutResource();
		post = new PostResource();
	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public String deleteAll()
	{
		return delete.deleteResource(ObjectType.monograph.toString());

	}

	@PUT
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	public String createMonograph(@PathParam("pid") String pid)
	{
		CreateObjectBean input = new CreateObjectBean();
		input.type = ObjectType.monograph.toString();
		return put.createResource(pid, input);

	}

	@DELETE
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	public String deleteMonograph(@PathParam("pid") String pid)
	{
		return delete.deleteResource(pid);
	}

	@Deprecated
	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateMonographDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{

		return put.updateResourceDC(pid, content);

	}

	@GET
	@Path("/{pid}/data")
	@Produces({ "application/*", "application/json" })
	public Response readMonographData(@PathParam("pid") String pid)
	{
		return get.readData(pid);
	}

	@POST
	@Path("/{pid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes("multipart/mixed")
	public String updateMonographData(@PathParam("pid") String pid,
			MultiPart multiPart)
	{
		return put.updateResourceData(pid, multiPart);
	}

	@GET
	@Path("/{pid}/metadata")
	@Produces({ "text/plain" })
	public String readMonographMetadata(@PathParam("pid") String pid)
	{
		return get.readMetadata(pid);
	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return get.getAllOfType(ObjectType.monograph.toString());

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
		return get.getView(pid);
	}

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readMonographDC(@PathParam("pid") String pid)
	{
		return get.readDC(pid);
	}

	@PUT
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateMonographMetadata(@PathParam("pid") String pid,
			String content)
	{
		return put.updateResourceMetadata(pid, content);
	}

	@POST
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateMonographMetadataPost(@PathParam("pid") String pid,
			String content)
	{
		return post.updateResourceMetadata(pid, content);
	}

	// be it hot or be it not
}
