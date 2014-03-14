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

import static de.nrw.hbz.regal.fedora.FedoraVocabulary.HAS_PART;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.IS_PART_OF;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.multipart.MultiPart;

import de.nrw.hbz.regal.api.helper.Actions;
import de.nrw.hbz.regal.api.helper.HttpArchiveException;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.exceptions.ArchiveException;

/**
 * GetResource collects all Methods for read access to a archive resource.
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@Path("/resource")
public class Resource {

    final static Logger logger = LoggerFactory.getLogger(Resource.class);

    Actions actions = null;

    /**
     * Creates a new GetResource
     * 
     * @throws IOException
     *             if properties of the Actions class can't get loaded
     */
    public Resource() throws IOException {
	actions = Actions.getInstance();
    }

    /**
     * Creates a new Resource. The Resource has a certain type an can be
     * connected to a parent resource.
     * 
     * @param pid
     *            the pid of the resource the pid of the new resource
     * @param namespace
     *            the namespace of the resource
     * @param input
     *            a json string of the form { "type" :
     *            "<monograph | ejournal | webpage | webpageVersion | ejournalVolume | monographPart >"
     *            , "parentPid" : "uuid" }
     * @return a human readable message and a status code 200 if successful or a
     *         400 if not
     */
    @PUT
    @Path("/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml" })
    @Consumes({ "application/json", "application/xml" })
    public String create(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace, CreateObjectBean input) {
	if (input == null) {
	    throw new HttpArchiveException(Status.BAD_REQUEST.getStatusCode(),
		    "You've posted no input: NULL. You must provide at least a type");
	} else if (input.type == null || input.type.isEmpty()) {
	    throw new HttpArchiveException(Status.BAD_REQUEST.getStatusCode(),
		    "The type you've provided is NULL or empty.");
	}
	try {
	    Node node = actions.createResource(input, pid, namespace);
	    return node.getPID() + " created/updated!";
	} catch (ArchiveException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}
    }

    /**
     * Creates a new Resource. The Resource has a certain type an can be
     * connected to a parent resource.
     * 
     * @param pid
     *            the pid of the resource the pid of the new resource
     * @param namespace
     *            the namespace of the resource
     * @param input
     *            a json string of the form { "type" :
     *            "<monograph | ejournal | webpage | webpageVersion | ejournalVolume | monographPart >"
     *            , "parentPid" : "uuid" }
     * @return a human readable message and a status code 200 if successful or a
     *         400 if not
     */
    @POST
    @Path("/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml" })
    @Consumes({ "application/json", "application/xml" })
    public String createPost(@PathParam("pid") String pid,
	    @PathParam("namepsace") String namespace,
	    final CreateObjectBean input) {
	return create(pid, namespace, input);
    }

    /**
     * Returns all resources of a certain type (optional).
     * 
     * @param type
     *            a contentType. Is optional. If no type is set. All resources
     *            will be returned.
     * @param namespace
     *            list only objects in this namespace. Is optional. If no
     *            namespace is defined. All resources will be returned.
     * @param from
     *            show only hits starting at this index. Is optional. Defaults
     *            to 0.
     * @param until
     *            show only hits ending at this index. Is optional. Defaults to
     *            10.
     * @param getListingFrom
     *            List Resources from elasticsearch or from fedora. Allowed
     *            values: "repo" and "es". Defaults to "es".
     * @return a list of all archived objects
     */
    @GET
    @Produces({ "application/json", "application/xml" })
    public Response getAll(
	    @DefaultValue("") @QueryParam("type") String type,
	    @DefaultValue("") @QueryParam("namespace") String namespace,
	    @DefaultValue("0") @QueryParam("from") int from,
	    @DefaultValue("10") @QueryParam("until") int until,
	    @DefaultValue("es") @QueryParam("getListingFrom") String getListingFrom) {
	ObjectList rem = new ObjectList(actions.list(type, namespace, from,
		until, getListingFrom));
	ResponseBuilder res = Response.ok().entity(rem);
	return res.build();
    }

    /**
     * Returns all resources of a certain type (optional).
     * 
     * @param type
     *            a contentType. Is optional. If no type is set. All resources
     *            will be returned.
     * @param namespace
     *            list only objects in this namespace. Is optional. If no
     *            namespace is defined. All resources will be returned.
     * @param from
     *            show only hits starting at this index. Is optional. Defaults
     *            to 0.
     * @param until
     *            show only hits ending at this index. Is optional. Defaults to
     *            10.
     * @param getListingFrom
     *            List Resources from elasticsearch or from fedora. Allowed
     *            values: "repo" and "es". Defaults to "es".
     * @return a list of all archived objects
     */
    @GET
    @Produces({ "text/html" })
    public Response getAllAsHtml(
	    @DefaultValue("") @QueryParam("type") String type,
	    @DefaultValue("") @QueryParam("namespace") String namespace,
	    @DefaultValue("0") @QueryParam("from") int from,
	    @DefaultValue("10") @QueryParam("until") int until,
	    @DefaultValue("es") @QueryParam("getListingFrom") String getListingFrom) {
	String rem = actions.listAsHtml(type, namespace, from, until,
		getListingFrom);
	ResponseBuilder res = Response.ok().entity(rem);
	return res.build();
    }

    /**
     * Deletes all Resources of a certain type.
     * 
     * @param type
     *            the type of resources that will be deleted
     * @param namespace
     *            list only objects in this namespace
     * @param from
     *            show only hits starting at this index
     * @param until
     *            show only hits ending at this index
     * @param getListingFrom
     *            allowed values are es and repo
     * @return A message and status code 200 if ok and 500 if not
     */
    @DELETE
    @Produces({ "application/json", "application/xml" })
    public String deleteAllOfType(
	    @DefaultValue("") @QueryParam("type") String type,
	    @DefaultValue("") @QueryParam("namespace") String namespace,
	    @DefaultValue("0") @QueryParam("from") int from,
	    @DefaultValue("10") @QueryParam("until") int until,
	    @DefaultValue("es") @QueryParam("getListingFrom") String getListingFrom) {
	try {

	    return actions.deleteAll(actions.list(type, namespace, from, until,
		    getListingFrom));
	} catch (ArchiveException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}
    }

    /**
     * Deletes a pid
     * 
     * @param pid
     *            the pid of the resource the pid of the resource that must be
     *            deleted
     * @param namespace
     *            the namespace of the resource
     * @return a human readable message and a status code of 200 if successful
     *         or a 500 if not.
     */
    @DELETE
    @Path("/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml" })
    public String delete(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	try {
	    return actions.delete(namespace + ":" + pid);

	} catch (ArchiveException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}
    }

    /**
     * Returns OAI-ORE as json-ld compact
     * 
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return a json representation of the resource
     * @throws URISyntaxException
     *             if redirection goes wrong
     */
    @GET
    @Path("/{namespace}:{pid}")
    @Produces({ "application/json+compact" })
    public Response getJsonCompact(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) throws URISyntaxException {
	// return about(namespace + ":" + pid);
	return Response
		.temporaryRedirect(
			new java.net.URI("../resource/" + namespace + ":" + pid
				+ ".json+compact")).status(303).build();
    }

    /**
     * Returns OAI-ORE as json-ld
     * 
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return a json representation of the resource
     * @throws URISyntaxException
     *             if redirection goes wrong
     */
    @GET
    @Path("/{namespace}:{pid}")
    @Produces({ "application/json" })
    public Response getJson(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) throws URISyntaxException {
	// return about(namespace + ":" + pid);
	return Response
		.temporaryRedirect(
			new java.net.URI("../resource/" + namespace + ":" + pid
				+ ".json")).status(303).build();
    }

    /**
     * Returns OAI-ORE as rdf-xml
     * 
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return a rdf/xml representation of the resource
     * @throws URISyntaxException
     *             if redirection goes wrong
     */
    @GET
    @Path("/{namespace}:{pid}")
    @Produces({ "application/rdf+xml" })
    public Response getRdfXml(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) throws URISyntaxException {
	// return about(namespace + ":" + pid);
	return Response
		.temporaryRedirect(
			new java.net.URI("../resource/" + namespace + ":" + pid
				+ ".rdf"))
		.header("accept", "application/rdf+xml").status(303).build();
    }

    /**
     * Returns a html view of the resource
     * 
     * @param pid
     *            the pid of the resource the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return an aggregated representation of the resource in html
     * @throws URISyntaxException
     *             if redirection goes wrong
     */
    @GET
    @Path("/{namespace}:{pid}")
    @Produces({ "text/html" })
    public Response getHtml(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) throws URISyntaxException {
	// return about(namespace + ":" + pid);
	return Response
		.temporaryRedirect(
			new java.net.URI("../resource/" + namespace + ":" + pid
				+ ".html")).status(303).build();
    }

    /**
     * Returns OAI-ORE as n-triple
     * 
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return n-triple representation of the resource
     * @throws URISyntaxException
     *             if redirection goes wrong
     */
    @GET
    @Path("/{namespace}:{pid}")
    @Produces({ "text/plain" })
    public Response getNtriple(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) throws URISyntaxException {
	// return about(namespace + ":" + pid);
	return Response
		.temporaryRedirect(
			new java.net.URI("../resource/" + namespace + ":" + pid
				+ ".rdf")).header("accept", "text/plain")
		.status(303).build();
    }

    /**
     * Lists all children of the resource
     * 
     * @param pid
     *            the pid of the resource the pid of a resource containing
     *            multiple volumes
     * @param namespace
     *            namespace of the resource
     * @return all volumes of the resource
     */
    @GET
    @Path("/{namespace}:{pid}/parts/")
    @Produces({ "application/json", "application/xml" })
    public ObjectList getAllParts(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {

	return new ObjectList(actions.getRelatives(namespace + ":" + pid,
		HAS_PART));
    }

    /**
     * Lists all parents of the resource
     * 
     * @param pid
     *            the pid of the resource the pid of a resource containing
     *            multiple volumes
     * @param namespace
     *            namespace of the resource
     * @return all volumes of the resource
     */
    @GET
    @Path("/{namespace}:{pid}/parents/")
    @Produces({ "application/json", "application/xml" })
    public ObjectList getAllParents(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {

	return new ObjectList(actions.getRelatives(namespace + ":" + pid,
		IS_PART_OF));
    }

    /**
     * Returns the actual data of a resource. The format of the binary data is
     * not defined.
     * 
     * @param p
     *            The pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return the actual binary data
     */
    @GET
    @Path("/{namespace}:{pid}/data")
    @Produces({ "application/*" })
    public Response readData(@PathParam("pid") String p,
	    @PathParam("namespace") String namespace) {
	try {
	    String pid = namespace + ":" + p;
	    Response res = actions.readData(pid);
	    if (res == null)
		throw new HttpArchiveException(404,
			"Datastream does not exist!");
	    return res;
	} catch (Exception e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}
    }

    /**
     * Updates the actual data of the aggregated resource
     * 
     * @param pid
     *            the pid of the resource the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @param multiPart
     *            The data is transfered as multipart data in order to provide
     *            upload of large files
     * @return A human readable message and a status code of 200 if successful
     *         an of 500 if not.
     */
    @PUT
    @Path("/{namespace}:{pid}/data")
    @Produces({ "application/json", "application/xml" })
    @Consumes("multipart/mixed")
    public String updateData(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace, MultiPart multiPart) {

	try {
	    String mimeType = multiPart.getBodyParts().get(1)
		    .getEntityAs(String.class);

	    String name = "data";
	    if (multiPart.getBodyParts().size() == 3) {
		name = multiPart.getBodyParts().get(2)
			.getEntityAs(String.class);
	    }
	    return actions.updateData(namespace + ":" + pid, multiPart
		    .getBodyParts().get(0).getEntityAs(InputStream.class),
		    mimeType, name);
	} catch (ArchiveException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	} catch (IOException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}

    }

    /**
     * Updates the actual data of the aggregated resource
     * 
     * @param pid
     *            the pid of the resource the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @param multiPart
     *            The data is transfered as multipart data in order to provide
     *            upload of large files
     * @return A human readable message and a status code of 200 if successful
     *         an of 500 if not.
     */
    @POST
    @Path("/{namespace}:{pid}/data")
    @Produces({ "application/json", "application/xml" })
    @Consumes("multipart/mixed")
    public String updateDataPost(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace, MultiPart multiPart) {

	return updateData(pid, namespace, multiPart);

    }

    /**
     * Deletes the data stream
     * 
     * @param pid
     *            the pid of the resource the pid of the resource that data must
     *            be deleted
     * @param namespace
     *            namespace of the resource
     * @return a human readable message and a status code of 200 if successful
     *         or a 500 if not.
     */
    @DELETE
    @Path("/{namespace}:{pid}/data")
    @Produces({ "application/json", "application/xml" })
    public String deleteData(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	try {
	    return actions.deleteData(namespace + ":" + pid);

	} catch (ArchiveException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}
    }

    /**
     * Returns the metadata of the resource
     * 
     * @param pid
     *            the pid of the resource the metadata of a pid
     * @param namespace
     *            namespace of the resource
     * @return the rdf metadata as n-triple
     */
    @GET
    @Path("/{namespace}:{pid}/metadata")
    @Produces({ "text/plain" })
    public String readMetadata(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {

	String result = actions.readMetadata(namespace + ":" + pid);
	return result;
    }

    /**
     * Updates the metadata of the resource
     * 
     * @param pid
     *            the pid of the resource The pid of the resource
     * @param namespace
     *            namespace of the resource
     * @param content
     *            the metadata as n-triple rdf
     * @return a human readable message and a status code of 200 if successful
     *         and 500 if not.
     */
    @PUT
    @Path("/{namespace}:{pid}/metadata")
    @Consumes({ "text/plain" })
    @Produces({ "text/plain" })
    public String updateMetadata(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace, String content) {
	return actions.updateMetadata(namespace + ":" + pid, content);
    }

    /**
     * Updates the metadata of the resource
     * 
     * @param pid
     *            the pid of the resource The pid of the resource
     * @param namespace
     *            namespace of the resource
     * @param content
     *            the metadata as n-triple rdf
     * @return a human readable message and a status code of 200 if successful
     *         and 500 if not.
     */
    @POST
    @Path("/{namespace}:{pid}/metadata")
    @Consumes({ "text/plain" })
    @Produces({ "text/plain" })
    public String updateMetadataPost(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace, String content) {
	return updateMetadata(pid, namespace, content);
    }

    /**
     * Deletes the metadata of the resource
     * 
     * @param pid
     *            the pid of the resource the pid of the resource that data must
     *            be deleted
     * @param namespace
     *            namespace of the resource
     * @return a human readable message and a status code of 200 if successful
     *         or a 500 if not.
     */
    @DELETE
    @Path("/{namespace}:{pid}/metadata")
    @Produces({ "application/json", "application/xml" })
    public String deleteMetadata(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	try {
	    return actions.deleteMetadata(pid, namespace);

	} catch (ArchiveException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}
    }

    /**
     * Returns Dublin Core metadata of the resource
     * 
     * @param pid
     *            the pid of the resource the pid of the resource
     * @param namespace
     *            namespace of the resource
     * @return the dublin core as json or xml
     */
    @GET
    @Path("/{namespace}:{pid}/dc")
    @Produces({ "application/xml", "application/json" })
    public DCBeanAnnotated readDC(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	return actions.readDC(namespace + ":" + pid);
    }

    /**
     * Updates Dublin Core metadata
     * 
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            namespace of the resource
     * @param content
     *            dublin core
     * @return a message
     */
    @PUT
    @Path("/{namespace}:{pid}/dc")
    @Produces({ "application/json", "application/xml" })
    @Consumes({ "application/json", "application/xml" })
    public String updateDC(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace, DCBeanAnnotated content) {
	try {
	    return actions.updateDC(namespace + ":" + pid, content);
	} catch (ArchiveException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}
    }

    /**
     * Updates dublin core metadata
     * 
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            namespace of the resource
     * @param content
     *            dublin core
     * @return a message
     */
    @POST
    @Path("/{namespace}:{pid}/dc")
    @Produces({ "application/json", "application/xml" })
    @Consumes({ "application/json", "application/xml" })
    public String updateDCPost(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace, DCBeanAnnotated content) {
	return updateDC(pid, namespace, content);
    }

    // @GET
    // @Path("/{namespace}:{pid}/oaisets")
    // @Produces({ "application/xml", "application/json" })
    // public ObjectList readOaiSets(@PathParam("pid") String pid,
    // @PathParam("namespace") String namespace) {
    //
    // }
    //
    // @PUT
    // @Path("/{namespace}:{pid}/oaisets")
    // @Produces({ "application/xml", "application/json" })
    // public String updateOaiSets(@PathParam("pid") String pid,
    // @PathParam("namespace") String namespace, ObjectList setlist) {
    //
    // }
    //
    // @DELETE
    // @Path("/{namespace}:{pid}/oaisets")
    // @Produces({ "application/xml", "application/json" })
    // public String deleteOaiSets(@PathParam("pid") String pid,
    // @PathParam("namespace") String namespace) {
    //
    // }

    /**
     * Assign the resource to a handfull of standard oaisets (in dependence to
     * /metadata).
     * 
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            namespace of the resource
     * @return human readable message
     */
    @POST
    @Path("/{namespace}:{pid}/oaisets/init")
    @Produces({ "application/xml", "application/json" })
    public String initOaiSets(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	try {
	    return actions.makeOAISet(namespace + ":" + pid);
	} catch (RuntimeException e) {
	    throw new HttpArchiveException(500, e);
	}
    }

    /**
     * Returns a OAI-ORE representation as rdf+xml.
     * 
     * @param pid
     *            the pid of the resource the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return an aggregated representation of the resource
     */
    @GET
    @Path("/{namespace}:{pid}.rdf")
    @Produces({ "application/rdf+xml" })
    public Response getReMAsRdfXml(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	String rem = actions.oaiore(namespace + ":" + pid,
		"application/rdf+xml");
	ResponseBuilder res = Response.ok()
		.lastModified(actions.getLastModified(namespace + ":" + pid))
		.entity(rem);

	return res.build();
    }

    /**
     * Returns a OAI-ORE representation as n-triples.
     * 
     * @param pid
     *            the pid of the resource the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return an aggregated representation of the resource
     */
    @GET
    @Path("/{namespace}:{pid}.rdf")
    @Produces({ "text/plain" })
    public Response getReMAsNTriple(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	String rem = actions.oaiore(namespace + ":" + pid, "text/plain");
	ResponseBuilder res = Response.ok()
		.lastModified(actions.getLastModified(namespace + ":" + pid))
		.entity(rem);

	return res.build();
    }

    /**
     * Returns a OAI-ORE representation as json-ld.
     * 
     * @param pid
     *            the pid of the resource the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return an aggregated representation of the resource
     */
    @GET
    @Path("/{namespace}:{pid}.json")
    @Produces({ "application/json" })
    public Response getReMAsJson(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	String rem = actions.oaiore(namespace + ":" + pid, "application/json");
	ResponseBuilder res = Response.ok()
		.lastModified(actions.getLastModified(namespace + ":" + pid))
		.entity(rem);

	return res.build();
    }

    /**
     * Returns a OAI-ORE representation as json-ld.
     * 
     * @param pid
     *            the pid of the resource the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return an aggregated representation of the resource
     */
    @GET
    @Path("/{namespace}:{pid}.json+compact")
    @Produces({ "application/json+compact" })
    public Response getReMAsJsonCompact(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	String rem = actions.oaiore(namespace + ":" + pid,
		"application/json+compact");
	ResponseBuilder res = Response.ok()
		.lastModified(actions.getLastModified(namespace + ":" + pid))
		.entity(rem);

	return res.build();
    }

    /**
     * Returns a OAI-ORE representation as html.
     * 
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return a html display of the aggregated resource
     */
    @GET
    @Path("/{namespace}:{pid}.html")
    @Produces({ "text/html" })
    public Response getReMAsHtml(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	String rem = actions.oaiore(namespace + ":" + pid, "text/html");
	// return rem;
	ResponseBuilder res = Response.ok()
		.lastModified(actions.getLastModified(namespace + ":" + pid))
		.entity(rem);

	return res.build();
    }

    /**
     * Returns a object representation as CreateObjectBean in json.
     * 
     * @param pid
     *            the pid of the resource
     * @param namespace
     *            the namespace of the resource
     * @return a html display of the aggregated resource
     */
    @GET
    @Path("/{namespace}:{pid}.regal")
    @Produces({ "application/json" })
    public Response getObjectAsJson(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace) {
	CreateObjectBean result = actions.getRegalJson(namespace + ":" + pid);
	// return rem;
	ResponseBuilder res = Response.ok()
		.lastModified(actions.getLastModified(namespace + ":" + pid))
		.entity(result);

	return res.build();
    }

    /**
     * Returns a oai-dc conversion of pid's metadata
     * 
     * @param pid
     *            the metadata of the identified resource will be transformed to
     *            oaidc
     * @return the oai_dc xml
     */
    @GET
    @Path("/{pid}.oaidc")
    @Produces({ "application/xml" })
    public String oaidc(@PathParam("pid") String pid) {
	try {
	    return actions.oaidc(pid);
	} catch (ArchiveException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}
    }

    /**
     * Returns epicur for the pid, if urn is available
     * 
     * @param pid
     *            pid with namespace
     * @return epicur xml
     */
    @GET
    @Path("/{pid}.epicur")
    @Produces({ "application/json", "application/xml" })
    public String epicur(@PathParam("pid") String pid) {
	try {
	    return actions.epicur(pid);
	} catch (ArchiveException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}
    }

    /**
     * Extractes text from pid/data if data exists and is a pdf
     * 
     * @param pid
     *            the pid must contain a data stream with mime type
     *            application/pdf
     * @param request
     *            lastModified is checked.
     * @return a text/plain message containing the extracted text
     */
    @GET
    @Path("/{pid}.pdfbox")
    @Produces({ "text/plain; charset=UTF-8" })
    public Response pdfbox(@PathParam("pid") String pid,
	    @Context Request request) {
	try {
	    Node node = actions.readNode(pid);

	    final EntityTag eTag = new EntityTag(node.getPID() + "_"
		    + node.getLastModified().getTime());

	    final CacheControl cacheControl = new CacheControl();
	    cacheControl.setMaxAge(-1);

	    ResponseBuilder builder = request.evaluatePreconditions(
		    node.getLastModified(), eTag);

	    // the user's information was modified, return it
	    if (builder == null) {
		builder = Response.ok(actions.pdfbox(node));
	    }

	    // the user's information was not modified, return a 304
	    return builder.cacheControl(cacheControl)
		    .lastModified(node.getLastModified()).tag(eTag).build();

	} catch (ArchiveException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}
    }

    /**
     * Convertes pid/data to pdfA if data exists and is a pdf
     * 
     * @param pid
     *            the pid must contain a data stream with mime type
     *            application/pdf
     * @param request
     *            lastModified is checked.
     * @return a text/plain message containing the extracted text
     */
    @GET
    @Path("/{pid}.pdfa")
    @Produces({ "text/plain; charset=UTF-8" })
    public Response pdfa(@PathParam("pid") String pid, @Context Request request) {
	try {
	    Node node = actions.readNode(pid);

	    final EntityTag eTag = new EntityTag(node.getPID() + "_"
		    + node.getLastModified().getTime());

	    final CacheControl cacheControl = new CacheControl();
	    cacheControl.setMaxAge(-1);

	    ResponseBuilder builder = request.evaluatePreconditions(
		    node.getLastModified(), eTag);

	    // the user's information was modified, return it
	    if (builder == null) {
		String redirectUrl = actions.pdfa(node);
		try {
		    builder = Response.temporaryRedirect(
			    new java.net.URI(redirectUrl)).status(303);
		} catch (URISyntaxException e) {
		    throw new HttpArchiveException(
			    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
		}
	    }

	    // the user's information was not modified, return a 304
	    return builder.cacheControl(cacheControl)
		    .lastModified(node.getLastModified()).tag(eTag).build();

	} catch (ArchiveException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}
    }

    /**
     * @param pid
     *            a pid with namespace
     * @param request
     *            request for context information
     * @return a aleph mab xml representation
     */
    @GET
    @Path("/{pid}.aleph")
    @Produces({ "application/xml; charset=UTF-8" })
    public String aleph(@PathParam("pid") String pid, @Context Request request) {
	try {
	    return actions.aleph(pid);
	} catch (ArchiveException e) {
	    throw new HttpArchiveException(
		    Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}
    }

}
