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
import java.util.Vector;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import de.nrw.hbz.regal.api.helper.Actions;
import de.nrw.hbz.regal.api.helper.HttpArchiveException;
import de.nrw.hbz.regal.datatypes.Transformer;

/**
 * This class defines some RPC-Like experimental endpoints under /utils. Most of
 * it will be included in the more resource centric interface at /resource
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@Path("/utils")
public class Utils {
    Actions actions = null;

    /**
     * @throws IOException
     *             if properties cannot be loaded
     * 
     */
    public Utils() throws IOException {
	actions = Actions.getInstance();
    }

    /**
     * Deletes all objects in a certain namespace.
     * 
     * @param namespace
     *            the namespace to delete.
     * @return A message or an ArchiveException
     */
    @DELETE
    @Path("/deleteNamespace/{namespace}")
    @Produces({ "application/json", "application/xml" })
    public String deleteNamespace(@PathParam("namespace") String namespace) {

	return actions.deleteByQuery(namespace + ":*");

    }

    /**
     * Deletes all objects in a certain namespace.
     * 
     * @param query
     *            all objects matched by this query will be deleted
     * @return A message or an ArchiveException
     */
    @DELETE
    @Path("/deleteByQuery/{query}")
    @Produces({ "application/json", "application/xml" })
    public String deleteByQuery(@PathParam("query") String query) {

	return actions.deleteByQuery(query);

    }

    /**
     * Generates OAI-Sets for the object
     * 
     * @param pid
     *            the pid of the object, that must be published in a oai set.
     * @return a message
     */
    @POST
    @Path("/makeOaiSet/{pid}")
    @Produces({ "application/json", "application/xml" })
    public String makeOaiSet(@PathParam("pid") String pid) {
	try {
	    return actions.makeOAISet(pid);
	} catch (RuntimeException e) {
	    throw new HttpArchiveException(500, e);
	}

    }

    /**
     * Add an object to the elasticsearch index
     * 
     * @param pid
     *            the pid to be indexed
     * @param namespace
     *            the namespace of the resource
     * @param type
     *            the type of the resource
     * @return a message
     */
    @POST
    @Path("/index/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml" })
    public String index(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace,
	    @QueryParam("type") final String type) {

	return actions.index(namespace + ":" + pid, namespace, type);

    }

    /**
     * Removes an object from the elasticsearch index
     * 
     * @param pid
     *            the pid of the object
     * @param namespace
     *            the namespace of the object
     * @param type
     *            the type of the object
     * @return a message
     */
    @DELETE
    @Path("/index/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml" })
    public String removeFromindex(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace,
	    @QueryParam("type") final String type) {

	return actions.removeFromIndex(namespace, type, namespace + ":" + pid);

    }

    /**
     * Add an object to the elasticsearch index
     * 
     * @param pid
     *            the pid to be indexed
     * @param namespace
     *            the namespace of the resource
     * @param type
     *            the type of the resource
     * @return a message
     */
    @POST
    @Path("/public_index/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml" })
    public String publicIndex(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace,
	    @QueryParam("type") final String type) {

	return actions
		.index(namespace + ":" + pid, "public_" + namespace, type);

    }

    /**
     * Removes an object from the elasticsearch index
     * 
     * @param pid
     *            the pid of the object
     * @param namespace
     *            the namespace of the object
     * @param type
     *            the type of the object
     * @return a message
     */
    @DELETE
    @Path("/public_index/{namespace}:{pid}")
    @Produces({ "application/json", "application/xml" })
    public String removeFromPublicIndex(@PathParam("pid") String pid,
	    @PathParam("namespace") String namespace,
	    @QueryParam("type") final String type) {

	return actions.removeFromIndex("public_" + namespace, type, namespace
		+ ":" + pid);

    }

    /**
     * Fetches bibliographic metadata from lobid. A lobid id must be available
     * on /metadata of the pid
     * 
     * @param pid
     *            the pid that must be enriched with lobid data
     * @return a message
     */
    @POST
    @Path("/lobidify/{pid}")
    @Produces({ "application/json", "application/xml" })
    public String lobidify(@PathParam("pid") String pid) {
	return actions.lobidify(pid);
    }

    /**
     * Adds an Urn to the pid. If the pid already has an urn a exception will be
     * thrown.
     * 
     * @param id
     *            pid without namespace
     * @param namespace
     *            the namespace
     * @param snid
     *            a urn snid
     * @return urn
     */
    @POST
    @Path("/addUrn")
    @Produces({ "application/json", "application/xml" })
    public String addUrn(@QueryParam("id") final String id,
	    @QueryParam("namespace") final String namespace,
	    @QueryParam("snid") final String snid) {
	return actions.addUrn(id, namespace, snid);
    }

    /**
     * Replaces, or if not exists adds an URN
     * 
     * @param id
     *            pid without namespace
     * @param namespace
     *            the namespace
     * @param snid
     *            a urn snid
     * @return urn
     */
    @POST
    @Path("/replaceUrn")
    @Produces({ "application/json", "application/xml" })
    public String replaceUrn(@QueryParam("id") final String id,
	    @QueryParam("namespace") final String namespace,
	    @QueryParam("snid") final String snid) {
	return actions.replaceUrn(id, namespace, snid);
    }

    /**
     * Reininit ContentModels for a certain namespace
     * 
     * @param namespace
     *            namespace of the model
     * @return a message
     */
    @POST
    @Path("/initContentModels")
    @Produces({ "text/plain" })
    public String initContentModels(
	    @DefaultValue("") @QueryParam("namespace") String namespace) {
	List<Transformer> transformers = new Vector<Transformer>();
	transformers
		.add(new Transformer(namespace + "epicur", "epicur", actions
			.getServer()
			+ "/resource/(pid)."
			+ namespace
			+ "epicur"));
	transformers.add(new Transformer(namespace + "oaidc", "oaidc", actions
		.getServer() + "/resource/(pid)." + namespace + "oaidc"));
	transformers.add(new Transformer(namespace + "pdfa", "pdfa", actions
		.getServer() + "/resource/(pid)." + namespace + "pdfa"));
	transformers
		.add(new Transformer(namespace + "pdfbox", "pdfbox", actions
			.getServer()
			+ "/resource/(pid)."
			+ namespace
			+ "pdfbox"));
	transformers.add(new Transformer(namespace + "aleph", "aleph", actions
		.getServer() + "/resource/(pid)." + namespace + "aleph"));
	actions.contentModelsInit(transformers);
	return "Reinit contentModels " + namespace + "epicur, " + namespace
		+ "oaidc, " + namespace + "pdfa, " + namespace + "pdfbox, "
		+ namespace + "aleph";
    }

}
