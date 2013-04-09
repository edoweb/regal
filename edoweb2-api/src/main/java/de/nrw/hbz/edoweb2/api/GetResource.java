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
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.edoweb2.archive.exceptions.ArchiveException;

/**
 * GetResource collects all Methods for read access to a archive resource.
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@Path("/resources")
public class GetResource
{

	final static Logger logger = LoggerFactory.getLogger(GetResource.class);

	String namespace = "edoweb";

	Actions actions = null;

	/**
	 * Creates a new GetResource
	 * 
	 * @throws IOException
	 *             if properties of the Actions class can't get loaded
	 */
	public GetResource() throws IOException
	{
		actions = new Actions();
	}

	/**
	 * Returns the actual data of a resource. The format of the binary data is
	 * not defined.
	 * 
	 * @param pid
	 *            The pid of the resource
	 * @return the actual binary data
	 */
	@GET
	@Path("/{pid}/data")
	@Produces({ "application/*" })
	public Response readData(@PathParam("pid") String pid)
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

	/**
	 * @return a list of all archived objects
	 */
	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return new ObjectList(actions.getAll());
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return an aggregated representation of the resource
	 */
	@GET
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml", "text/html" })
	public View getView(@PathParam("pid") String pid)
	{
		return actions.getView(pid);
	}

	/**
	 * @param pid
	 *            the pid of a resource containing multiple volumes
	 * @return all volumes of the resource
	 */
	@GET
	@Path("/{pid}/volume/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVolumes(@PathParam("pid") String pid)
	{

		return new ObjectList(actions.findObject(pid, HAS_VOLUME));
	}

	/**
	 * @param pid
	 *            the metadata of a pid
	 * @return the rdf metadata as n-triple
	 */
	@GET
	@Path("/{pid}/metadata")
	@Produces({ "text/plain" })
	public String readMetadata(@PathParam("pid") String pid)
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

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return the dublin core as json or xml
	 */
	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}

	/**
	 * @param pid
	 *            the pid of the resource containing versions
	 * @return a list with pids of each version
	 */
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
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	/**
	 * @param type
	 *            the type of the resources that must be returned
	 * @return a list of pids
	 */
	@GET
	@Path("/{pid}/type/{type}")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllOfType(@PathParam("type") String type)
	{
		try
		{
			return new ObjectList(actions.findByType(TypeType.contentType
					.toString() + ":" + type));
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

}
