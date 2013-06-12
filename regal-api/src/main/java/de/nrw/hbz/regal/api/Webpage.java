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
package de.nrw.hbz.regal.api;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.multipart.MultiPart;

import de.nrw.hbz.regal.api.helper.ObjectType;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@Path("/webpage")
public class Webpage
{
	final static Logger logger = LoggerFactory.getLogger(Webpage.class);

	Resource resources = null;

	public Webpage() throws IOException
	{

		resources = new Resource();

	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public String deleteAll()
	{
		return resources.deleteAllOfType(ObjectType.webpage.toString());
	}

	@PUT
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public String createWebpage(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		CreateObjectBean input = new CreateObjectBean();
		input.type = ObjectType.webpage.toString();
		return resources.create(pid, namespace, input);
	}

	@DELETE
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public String deleteWebpage(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		return resources.delete(pid, namespace);
	}

	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateWebpageDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return resources.updateDC(pid, content);
	}

	@PUT
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateWebpageMetadata(@PathParam("pid") String pid,
			String content)
	{
		return resources.updateMetadata(pid, content);
	}

	@Deprecated
	@POST
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateWebpageMetadataPost(@PathParam("pid") String pid,
			String content)
	{
		return resources.updateMetadata(pid, content);
	}

	@PUT
	@Path("/{pid}/version/{namespace}:{versionPid}")
	@Produces({ "application/json", "application/xml" })
	public String createWebpageVersion(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid,
			@PathParam("namespace") String namespace)
	{
		CreateObjectBean input = new CreateObjectBean();
		input.type = ObjectType.version.toString();
		input.parentPid = pid;
		return resources.create(versionPid, namespace, input);

	}

	@POST
	@Path("/{pid}/version/{versionPid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateWebpageVersionDC(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid, DCBeanAnnotated content)
	{
		return resources.updateDC(versionPid, content);
	}

	@POST
	@Path("/{namespace}:{pid}/version/{versionPid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes("multipart/mixed")
	public String updateWebpageVersionData(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace,
			@PathParam("versionPid") String versionPid, MultiPart multiPart)
	{
		return resources.updateData(versionPid, namespace, multiPart);
	}

	@PUT
	@Path("/{pid}/version/{versionPid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateWebpageVersionMetadata(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid, String content)
	{
		return resources.updateMetadata(versionPid, content);
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
		return resources.updateMetadata(versionPid, content);
	}

	@GET
	@Path("/{pid}/version/{versionPid}/metadata")
	@Produces({ "application/*" })
	public String readWebpageVersionMetadata(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		return resources.readMetadata(versionPid);

	}

	@GET
	@Path("/{pid}/version/{versionPid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readWebpageVersionDC(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		return resources.readDC(versionPid);

	}

	@GET
	@Path("/{pid}/version/{namespace}:{versionPid}/data")
	@Produces({ "application/*" })
	public Response readWebpageVersionData(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid,
			@PathParam("namespace") String namespace)
	{
		return resources.readData(versionPid, namespace);

	}

	@GET
	@Path("/{pid}/version/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVersions(@PathParam("pid") String pid)
	{
		return resources.getAllParts(pid);

	}

	@GET
	@Path("/{pid}/metadata")
	@Produces({ "text/plain" })
	public String readWebpageMetadata(@PathParam("pid") String pid)
	{
		return resources.readMetadata(pid);
	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return resources.getAllOfType(ObjectType.webpage.toString());
	}

	@GET
	@Produces({ "text/html" })
	public Response getAllAsHtml()
	{
		String rem = resources
				.getAllOfTypeAsHtml(ObjectType.webpage.toString());
		ResponseBuilder res = Response.ok().entity(rem);
		return res.build();
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return an aggregated representation of the resource
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml", "text/html" })
	public Response getResource(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace) throws URISyntaxException
	{
		return Response
				.temporaryRedirect(
						new java.net.URI("../resource/" + namespace + ":" + pid
								+ "/about")).status(303).build();
	}

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readWebpageDC(@PathParam("pid") String pid)
	{
		return resources.readDC(pid);
	}
}
