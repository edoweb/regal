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
 * /monograph/{pid}/[dc|metadata|data]
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@Path("/monograph")
public class Monograph {
    final static Logger logger = LoggerFactory.getLogger(Monograph.class);

    Resource resources = null;

    /**
     * @throws IOException
     *             if resources failed to initialise
     */
    public Monograph() throws IOException {

	resources = new Resource();

    }

    /**
     * @param pid
     *            the pid of the resource th monograph pid
     * @param namespace
     *            the namespace of the resources the namespace of the resources
     * @return a message
     */
    @PUT
    @Path("/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml" })
    public String createMonograph(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	CreateObjectBean input = new CreateObjectBean();
	input.type = ObjectType.monograph.toString();
	return resources.create(pid, namespace, input);

    }

    /**
     * @param pid
     *            the pid of the resource
     * @param content
     *            dublin core
     * @return a message
     */
    @Deprecated
    @POST
    @Path("/{pid}/dc")
    @Produces({ "application/json", "application/xml" })
    @Consumes({ "application/json", "application/xml" })
    public String updateMonographDC(@PathParam("pid") String pid,
	    DCBeanAnnotated content) {

	return resources.updateDC(pid, content);

    }

    /**
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            the namespace of the resources
     * @return the data
     */
    @GET
    @Path("/{namespace}:{pid}/data")
    @Produces({ "application/*", "application/json" })
    public Response readMonographData(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	return resources.readData(pid, namespace);
    }

    /**
     * @param pid
     *            the pid of the resource
     * @return a message
     */
    @GET
    @Path("/{pid}/metadata")
    @Produces({ "text/plain" })
    public String readMonographMetadata(@PathParam("pid") String pid) {
	return resources.readMetadata(pid);
    }

    /**
     * @return a list of all monographs
     */
    @GET
    @Produces({ "application/json", "application/xml" })
    public ObjectList getAll() {
	return resources.getAllOfType(ObjectType.monograph.toString());

    }

    /**
     * @return a list of all monographs in html
     */
    @GET
    @Produces({ "text/html" })
    public Response getAllAsHtml() {
	String rem = resources.getAllOfTypeAsHtml(ObjectType.monograph
		.toString());
	ResponseBuilder res = Response.ok().entity(rem);
	return res.build();
    }

    /**
     * @param pid
     *            the pid of the resource the pid of the resource
     * @param namespace
     *            the namespace of the resources
     * @return an aggregated representation of the resource
     * @throws URISyntaxException
     *             if redirection has been coded wrong
     */
    @GET
    @Path("/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml", "text/html" })
    public Response getResource(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) throws URISyntaxException {
	return Response
		.temporaryRedirect(
			new java.net.URI("../resource/" + namespace + ":" + pid
				+ "/about")).status(303).build();
    }

    /**
     * @param pid
     *            the pid of the resource
     * @return dublin core
     */
    @GET
    @Path("/{pid}/dc")
    @Produces({ "application/xml", "application/json" })
    public DCBeanAnnotated readMonographDC(@PathParam("pid") String pid) {
	return resources.readDC(pid);
    }

    /**
     * @param pid
     *            the pid of the resource
     * @param content
     *            n-triple metadata
     * @return a message
     */
    @PUT
    @Path("/{pid}/metadata")
    @Consumes({ "text/plain" })
    @Produces({ "text/plain" })
    public String updateMonographMetadata(@PathParam("pid") String pid,
	    String content) {
	return resources.updateMetadata(pid, content);
    }

    /**
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            the namespace of the resources
     * @param multiPart
     *            the data as multipart encoded chunks
     * @return a message
     */
    @POST
    @Path("/{namespace}:{pid}/data")
    @Produces({ "application/json", "application/xml" })
    @Consumes("multipart/mixed")
    public String updateMonographData(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace, MultiPart multiPart) {
	return resources.updateData(pid, namespace, multiPart);
    }

    /**
     * @param pid
     *            the pid of the resource
     * @param content
     *            n-triple metadata
     * @return a message
     */
    @POST
    @Path("/{pid}/metadata")
    @Consumes({ "text/plain" })
    @Produces({ "text/plain" })
    public String updateMonographMetadataPost(@PathParam("pid") String pid,
	    String content) {
	return resources.updateMetadata(pid, content);
    }

    /**
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            the namespace of the resources
     * @return a message
     */
    @DELETE
    @Path("/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml" })
    public String deleteMonograph(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	return resources.delete(pid, namespace);
    }

    /**
     * @return a message
     */
    @DELETE
    @Produces({ "application/json", "application/xml" })
    public String deleteAll() {
	return resources.deleteAllOfType(ObjectType.monograph.toString());

    }

    // be it hot or be it not
}
