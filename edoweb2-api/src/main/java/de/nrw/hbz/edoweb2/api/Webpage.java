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
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@Path("/webpage")
public class Webpage
{
	final static Logger logger = LoggerFactory.getLogger(Webpage.class);

	DeleteResource delete = null;
	GetResource get = null;
	PutResource put = null;
	PostResource post = null;

	public Webpage() throws IOException
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
		return delete.deleteResourceOfType(ObjectType.webpage.toString());
	}

	@PUT
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	public String createWebpage(@PathParam("pid") String pid)
	{
		CreateObjectBean input = new CreateObjectBean();
		input.type = ObjectType.webpage.toString();
		return put.createResource(pid, input);
	}

	@DELETE
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	public String deleteWebpage(@PathParam("pid") String pid)
	{
		return delete.deleteResource(pid);
	}

	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateWebpageDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return put.updateResourceDC(pid, content);
	}

	@PUT
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateWebpageMetadata(@PathParam("pid") String pid,
			String content)
	{
		return put.updateResourceMetadata(pid, content);
	}

	@Deprecated
	@POST
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateWebpageMetadataPost(@PathParam("pid") String pid,
			String content)
	{
		return post.updateResourceMetadata(pid, content);
	}

	@PUT
	@Path("/{pid}/version/{versionPid}")
	@Produces({ "application/json", "application/xml" })
	public String createWebpageVersion(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		CreateObjectBean input = new CreateObjectBean();
		input.type = ObjectType.webpageVersion.toString();
		input.parentPid = pid;
		return put.createResource(versionPid, input);

	}

	@POST
	@Path("/{pid}/version/{versionPid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateWebpageVersionDC(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid, DCBeanAnnotated content)
	{
		return put.updateResourceDC(versionPid, content);
	}

	@POST
	@Path("/{pid}/version/{versionPid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes("multipart/mixed")
	public String updateWebpageVersionData(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid, MultiPart multiPart)
	{
		return put.updateResourceData(versionPid, multiPart);
	}

	@PUT
	@Path("/{pid}/version/{versionPid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateWebpageVersionMetadata(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid, String content)
	{
		return put.updateResourceMetadata(versionPid, content);
	}

	@Deprecated
	@POST
	@Path("/{pid}/version/{versionPid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateWebpageVersionMetadataPost(
			@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid, String content)
	{
		return post.updateResourceMetadata(versionPid, content);
	}

	@GET
	@Path("/{pid}/version/{versionPid}/metadata")
	@Produces({ "application/*" })
	public String readWebpageVersionMetadata(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		return get.readMetadata(versionPid);

	}

	@GET
	@Path("/{pid}/version/{versionPid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readWebpageVersionDC(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		return get.readDC(versionPid);

	}

	@GET
	@Path("/{pid}/version/{versionPid}/data")
	@Produces({ "application/*" })
	public Response readWebpageVersionData(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		return get.readData(versionPid);

	}

	@GET
	@Path("/{pid}/version/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVersions(@PathParam("pid") String pid)
	{
		return get.getAllVersions(pid);

	}

	@GET
	@Path("/{pid}/metadata")
	@Produces({ "text/plain" })
	public String readWebpageMetadata(@PathParam("pid") String pid)
	{
		return get.readMetadata(pid);
	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return get.getAllOfType(ObjectType.webpage.toString());
	}

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
	public DCBeanAnnotated readWebpageDC(@PathParam("pid") String pid)
	{
		return get.readDC(pid);
	}
}
