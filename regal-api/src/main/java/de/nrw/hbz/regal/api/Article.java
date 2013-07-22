/*
 * Copyright 2012 hbz NRW (http://www.hbz-nrw.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this article except in compliance with the License.
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

import de.nrw.hbz.regal.api.helper.ObjectType;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@Path("/article")
public class Article {
    final static Logger logger = LoggerFactory.getLogger(Article.class);

    Resource resources = null;

    /**
     * Creates a new enpoint for articles
     * 
     * @throws IOException
     *             if resources can not be configured properly
     */
    public Article() throws IOException {

	resources = new Resource();

    }

    /**
     * @return a message with a list of deleted articles
     */
    @DELETE
    @Produces({ "application/json", "application/xml" })
    public String deleteAll() {
	return resources.deleteAllOfType(ObjectType.article.toString());
    }

    /**
     * @param pid
     *            a uniq id, recommended is a uuid
     * @param namespace
     *            a namespace e.g. edoweb
     * @return a message if or if not the creation was successful
     */
    @PUT
    @Path("/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml" })
    public String create(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	CreateObjectBean input = new CreateObjectBean();
	input.type = ObjectType.article.toString();
	return resources.create(pid, namespace, input);
    }

    /**
     * @param pid
     *            the pid of the article to be deleted
     * @param namespace
     *            the namespace
     * @return a message if or if not the deletion has been successful
     */
    @DELETE
    @Path("/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml" })
    public String delete(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	return resources.delete(pid, namespace);
    }

    /**
     * @param pid
     *            the pid of the object
     * @param content
     *            the dublin core data
     * @return a message
     */
    @POST
    @Path("/{pid}/dc")
    @Produces({ "application/json", "application/xml" })
    @Consumes({ "application/json", "application/xml" })
    public String updateDC(@PathParam("pid") String pid, DCBeanAnnotated content) {
	return resources.updateDC(pid, content);
    }

    /**
     * @param pid
     *            the articles pid
     * @param content
     *            the metdata as n-triple
     * @return a message
     */
    @PUT
    @Path("/{pid}/metadata")
    @Consumes({ "text/plain" })
    @Produces({ "text/plain" })
    public String updateMetadata(@PathParam("pid") String pid, String content) {
	return resources.updateMetadata(pid, content);
    }

    /**
     * @param pid
     *            the articles pid
     * @param content
     *            the n-triple content
     * @return a message
     */
    @POST
    @Path("/{pid}/metadata")
    @Consumes({ "text/plain" })
    @Produces({ "text/plain" })
    public String updateMetadataPost(@PathParam("pid") String pid,
	    String content) {
	return resources.updateMetadata(pid, content);
    }

    /**
     * @return a list of all article pids
     */
    @GET
    @Produces({ "application/json", "application/xml" })
    public ObjectList getAll() {
	return resources.getAllOfType(ObjectType.article.toString());
    }

    /**
     * @return a list of all article pids as html
     */
    @GET
    @Produces({ "text/html" })
    public Response getAllAsHtml() {
	String rem = resources
		.getAllOfTypeAsHtml(ObjectType.article.toString());
	ResponseBuilder res = Response.ok().entity(rem);
	return res.build();
    }

    /**
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return an aggregated representation of the resource
     * @throws URISyntaxException
     *             if the 303 redirection fails
     */
    @GET
    @Path("/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml", "text/html" })
    public Response read(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) throws URISyntaxException {
	return Response
		.temporaryRedirect(
			new java.net.URI("../resource/" + namespace + ":" + pid
				+ "/about")).status(303).build();
    }

    /**
     * @param pid
     *            the pid of the article
     * @return dublin core data associated with this resource
     */
    @GET
    @Path("/{pid}/dc")
    @Produces({ "application/xml", "application/json" })
    public DCBeanAnnotated readDC(@PathParam("pid") String pid) {
	return resources.readDC(pid);
    }
}
