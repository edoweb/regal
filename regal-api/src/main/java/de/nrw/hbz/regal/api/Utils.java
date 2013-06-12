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
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import de.nrw.hbz.regal.api.helper.Actions;
import de.nrw.hbz.regal.api.helper.CacheSurvey;
import de.nrw.hbz.regal.api.helper.FedoraSurvey;
import de.nrw.hbz.regal.api.helper.HttpArchiveException;
import de.nrw.hbz.regal.api.helper.View;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.exceptions.ArchiveException;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@Path("/utils")
public class Utils
{
	Actions actions = null;

	/**
	 * @throws IOException
	 *             if properties cannot be loaded
	 * 
	 */
	public Utils() throws IOException
	{

		actions = new Actions();
	}

	/**
	 * @param pid
	 *            The pid to delete
	 * @return A message or an ArchiveException
	 */
	@DELETE
	@Path("/deleteNamespace/{namespace}")
	@Produces({ "application/json", "application/xml" })
	public String deleteNamespace(@PathParam("namespace") String namespace)
	{
		try
		{
			return actions.deleteNamespace(namespace);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}

	}

	@POST
	@Path("/makeOaiSet/{pid}")
	@Produces({ "application/json", "application/xml" })
	public String makeOaiSet(@PathParam("pid") String pid)
	{
		try
		{
			return actions.makeOAISet(pid);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}

	}

	@POST
	@Path("/index/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public String index(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		try
		{
			return actions.index(pid, namespace);
		}
		catch (ArchiveException e)
		{

			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	@GET
	@Path("/profile/cache")
	@Produces({ "application/json", "application/xml" })
	public CollectionProfile profileCache()
	{
		CacheSurvey survey = new CacheSurvey();
		List<View> rows = survey.survey();
		try
		{
			return new CollectionProfile(rows);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	@GET
	@Path("/profile/fedora")
	@Produces({ "application/json", "application/xml" })
	public CollectionProfile profileFedora()
	{
		try
		{
			FedoraSurvey survey = new FedoraSurvey();
			List<View> rows = survey.survey();

			return new CollectionProfile(rows);
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

	@POST
	@Path("/lobidify/{pid}")
	@Produces({ "application/json", "application/xml" })
	public String lobidify(@PathParam("pid") String pid)
	{
		try
		{
			return actions.lobidify(pid);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	@GET
	@Path("/oaidc/{pid}")
	@Produces({ "application/xml" })
	public String oaidc(@PathParam("pid") String pid)
	{
		try
		{
			return actions.oaidc(pid);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	@GET
	@Path("/epicur/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public String epicur(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		try
		{
			return actions.epicur(pid, namespace);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	@GET
	@Path("/pdfbox/{pid}")
	@Produces({ "text/plain" })
	public Response pdfbox(@PathParam("pid") String pid,
			@Context Request request)
	{
		try
		{
			Node node = actions.readNode(pid);

			final EntityTag eTag = new EntityTag(node.getPID() + "_"
					+ node.getLastModified().getTime());

			final CacheControl cacheControl = new CacheControl();
			cacheControl.setMaxAge(-1);

			ResponseBuilder builder = request.evaluatePreconditions(
					node.getLastModified(), eTag);

			// the user's information was modified, return it
			if (builder == null)
			{
				builder = Response.ok(actions.pdfbox(node));
			}

			// the user's information was not modified, return a 304
			return builder.cacheControl(cacheControl)
					.lastModified(node.getLastModified()).tag(eTag).build();

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	@POST
	@Path("/contentModels/{namespace}/init")
	@Produces({ "text/plain" })
	public String contentModelsInit(@PathParam("namespace") String namespace)
	{
		try
		{
			return actions.contentModelsInit(namespace);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

}
