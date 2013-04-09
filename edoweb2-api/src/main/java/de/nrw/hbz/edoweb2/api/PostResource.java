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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.sun.jersey.multipart.MultiPart;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@Path("/resources")
public class PostResource
{
	PutResource put;

	public PostResource() throws IOException
	{
		put = new PutResource();
	}

	/**
	 * Creates a new Resource. The Resource has a certain type an can be
	 * connected to a parent resource.
	 * 
	 * @param pid
	 *            the pid of the new resource
	 * @param input
	 *            a json string of the form { "type" :
	 *            "<monograph | ejournal | webpage | webpageVersion | ejournalVolume | monographPart >"
	 *            , "parentPid" : "uuid" }
	 * @return a human readable message and a status code 200 if successful or a
	 *         400 if not
	 */
	@POST
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	@Consumes("application/json")
	public String createResource(@PathParam("pid") String pid,
			final CreateObjectBean input)
	{
		return put.createResource(pid, input);
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @param multiPart
	 *            The data is transfered as multipart data in order to provide
	 *            upload of large files
	 * @return A human readable message and a status code of 200 if successful
	 *         an of 500 if not.
	 */
	@POST
	@Path("/{pid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes("multipart/mixed")
	public String updateResourceData(@PathParam("pid") String pid,
			MultiPart multiPart)
	{

		return put.updateResourceData(pid, multiPart);

	}

	/**
	 * @param pid
	 *            The pid of the resource
	 * @param content
	 *            the metadata as n-triple rdf
	 * @return a human readable message and a status code of 200 if successful
	 *         and 500 if not.
	 */
	@POST
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateResourceMetadata(@PathParam("pid") String pid,
			String content)
	{
		return put.updateResourceMetadata(pid, content);
	}

	@Deprecated
	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateResourceDC(String pid, DCBeanAnnotated content)
	{
		return put.updateResourceDC(pid, content);
	}
}
