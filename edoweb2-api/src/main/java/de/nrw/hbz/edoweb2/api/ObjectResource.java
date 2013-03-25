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
import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VOLUME;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.status.Status;
import de.nrw.hbz.edoweb2.archive.exceptions.ArchiveException;

@Path("/objects")
public class ObjectResource
{

	final static Logger logger = LoggerFactory.getLogger(ObjectResource.class);

	String namespace = "edoweb";

	Actions actions = null;

	public ObjectResource() throws IOException
	{
		actions = new Actions();
	}

	@GET
	@Path("/{pid}/data")
	@Produces({ "application/*" })
	public Response readData(@PathParam("pid") String pid)
	{
		try
		{
			return actions.readData(pid);
		}
		catch (ArchiveException | URISyntaxException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}
	}

	@GET
	@Path("/{pid}/volume/{volumePid}")
	@Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	public View getVolumeView(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{

		try
		{
			return actions.getView(volumePid, ObjectType.ejournalVolume);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}
	}

	@GET
	@Path("/{pid}/volume/{volumePid}/data")
	@Produces({ "application/*" })
	public Response readVolumeData(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{

		try
		{
			return actions.readData(volumePid);
		}
		catch (ArchiveException | URISyntaxException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}
	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return new ObjectList(actions.getAll());
	}

	@GET
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml", "text/html" })
	public View getView(@PathParam("pid") String pid)
	{
		return actions.getView(pid);
	}

	@GET
	@Path("/{pid}/volume/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVolumes(@PathParam("pid") String pid)
	{

		return new ObjectList(actions.findObject(pid, HAS_VOLUME));
	}

	@GET
	@Path("/{pid}/volume/{volumePid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readVolumeDC(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{

		return actions.readDC(volumePid);
	}

	@GET
	@Path("/{pid}/volume/{volumePid}/metadata")
	@Produces({ "application/*" })
	public Response readVolumeMetadata(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{

		try
		{
			return actions.readMetadata(volumePid);
		}
		catch (ArchiveException | URISyntaxException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}
	}

	@GET
	@Path("/{pid}/metadata")
	public Response readMetadata(@PathParam("pid") String pid)
	{
		try
		{
			return actions.readMetadata(pid);
		}
		catch (ArchiveException | URISyntaxException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}
	}

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}

	@GET
	@Path("/{pid}/version/{versionPid}/metadata")
	@Produces({ "application/*" })
	public Response readWebpageVersionMetadata(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		try
		{

			return actions.readMetadata(versionPid);
		}

		catch (ArchiveException | URISyntaxException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}
	}

	@GET
	@Path("/{pid}/version/{versionPid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readWebpageVersionDC(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		try
		{

			return actions.readDC(versionPid);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}
	}

	@GET
	@Path("/{pid}/version/{versionPid}/data")
	@Produces({ "application/*" })
	public Response readWebpageVersionData(@PathParam("pid") String pid,
			@PathParam("versionName") String versionPid)
	{
		try
		{

			return actions.readData(versionPid);
		}
		catch (ArchiveException | URISyntaxException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}
	}

	@GET
	@Path("/{pid}/version/{versionPid}")
	@Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	public View getVersionView(@PathParam("pid") String pid,
			@PathParam("versionName") String versionPid)
	{
		try
		{

			return actions.getView(versionPid, ObjectType.webpageVersion);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}
	}

	@GET
	@Path("/{pid}/version/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVersions(@PathParam("pid") String pid)
	{

		try
		{
			return new ObjectList(actions.findObject(pid, HAS_VERSION));
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}
	}

}
