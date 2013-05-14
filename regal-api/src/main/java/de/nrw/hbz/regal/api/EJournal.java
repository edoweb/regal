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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.multipart.MultiPart;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@Path("/ejournal")
public class EJournal
{
	final static Logger logger = LoggerFactory.getLogger(EJournal.class);

	Resources resources = null;

	public EJournal() throws IOException
	{

		resources = new Resources();

	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return resources.getAllOfType(ObjectType.journal.toString());

	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public String deleteAll()
	{
		return resources.deleteAllOfType(ObjectType.journal.toString());

	}

	@DELETE
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public String deleteEJournal(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		return resources.delete(pid, namespace);
	}

	@PUT
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public String createEJournal(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		CreateObjectBean input = new CreateObjectBean();
		input.type = ObjectType.journal.toString();
		return resources.create(pid, namespace, input);

	}

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readEJournalDC(@PathParam("pid") String pid)
	{
		return resources.readDC(pid);

	}

	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateEJournalDCPost(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return resources.updateDC(pid, content);
	}

	@PUT
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateEJournalDCPut(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return resources.updateDC(pid, content);
	}

	@GET
	@Path("/{pid}/metadata")
	@Produces({ "text/plain" })
	public String readEJournalMetadata(@PathParam("pid") String pid)
	{
		return resources.readMetadata(pid);
	}

	@PUT
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateEJournalMetadata(@PathParam("pid") String pid,
			String content)
	{
		return resources.updateMetadata(pid, content);
	}

	@Deprecated
	@POST
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateEJournalMetadataPost(@PathParam("pid") String pid,
			String content)
	{
		return resources.updateMetadata(pid, content);

	}

	@GET
	@Path("/{pid}/volume/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVolumes(@PathParam("pid") String pid)
	{
		return resources.getAllParts(pid);
	}

	@PUT
	@Path("/{pid}/volume/{namespace}:{volumePid}")
	@Produces({ "application/json", "application/xml" })
	public String createEJournalVolume(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace,
			@PathParam("volumePid") String volumePid)
	{
		CreateObjectBean input = new CreateObjectBean();
		input.type = ObjectType.volume.toString();
		input.parentPid = pid;
		return resources.create(volumePid, namespace, input);
	}

	@GET
	@Path("/{pid}/about")
	@Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	public Response getView(@PathParam("pid") String pid)
	{
		return resources.about(pid);
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
						new java.net.URI("/resources/" + namespace + ":" + pid
								+ "/about")).status(303).build();
	}

	@GET
	@Path("/{pid}/volume/{namespace}:{volumePid}/data")
	@Produces({ "application/*" })
	public Response readVolumeData(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid,
			@PathParam("namespace") String namespace)
	{
		return resources.readData(volumePid, namespace);
	}

	@POST
	@Path("/{pid}/volume/{namespace}:{volumePid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes("multipart/mixed")
	public String updateVolumeData(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid,
			@PathParam("namespace") String namespace, MultiPart multiPart)
	{
		return resources.updateData(volumePid, namespace, multiPart);
	}

	@GET
	@Path("/{pid}/volume/{volumePid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readVolumeDC(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{
		return resources.readDC(volumePid);
	}

	@POST
	@Path("/{pid}/volume/{volumePid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateVolumeDC(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid, DCBeanAnnotated content)
	{
		return resources.updateDC(volumePid, content);
	}

	@GET
	@Path("/{pid}/volume/{volumePid}/metadata")
	@Produces({ "application/*" })
	public String readVolumeMetadata(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{
		return resources.readMetadata(volumePid);
	}

	@PUT
	@Path("/{pid}/volume/{volumePid}/metadata")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "text/plain" })
	public String updateVolumeMetadata(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid, String content)
	{
		return resources.updateMetadata(volumePid, content);
	}

	@Deprecated
	@POST
	@Path("/{pid}/volume/{volumePid}/metadata")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "text/plain" })
	public String updateVolumeMetadataPost(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid, String content)
	{
		return resources.updateMetadata(volumePid, content);
	}
}
