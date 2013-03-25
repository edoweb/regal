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
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import ch.qos.logback.core.status.Status;
import de.nrw.hbz.edoweb2.archive.exceptions.ArchiveException;

@Path("/edowebAdmin")
public class EdowebAdmin
{
	Actions actions = null;

	/**
	 * @throws IOException
	 *             if properties cannot be loaded
	 * 
	 */
	public EdowebAdmin() throws IOException
	{

		actions = new Actions();
	}

	/**
	 * @return A message or an HttpArchiveException
	 */
	@DELETE
	@Path("/formatAll")
	public String formatAll()
	{
		try
		{
			return actions.formatAll();
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}
	}

	/**
	 * @param pid
	 *            The pid to delete
	 * @return A message or an ArchiveException
	 */
	@DELETE
	@Path("/delete/{pid}")
	public String delete(@PathParam("pid") String pid)
	{
		try
		{
			return actions.delete(pid, false);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}

	}

	@POST
	@Path("/makeOaiSet/{pid}")
	public String makeOaiSet(@PathParam("pid") String pid)
	{
		try
		{
			return actions.makeOAISet(pid);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}

	}

	@POST
	@Path("/index/{pid}")
	@Produces({ "application/json", "application/xml" })
	public String index(@PathParam("pid") String pid)
	{
		try
		{
			return actions.index(pid);
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
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
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
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
		catch (ArchiveException | IOException e)
		{
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
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
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
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
			throw new HttpArchiveException(Status.ERROR, e.getMessage());
		}
	}

}
