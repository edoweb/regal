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
import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VERSION_NAME;
import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VOLUME;
import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VOLUME_NAME;
import static de.nrw.hbz.edoweb2.api.Vocabulary.IS_VERSION;
import static de.nrw.hbz.edoweb2.api.Vocabulary.IS_VOLUME;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_BELONGS_TO_OBJECT;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_RELATED;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.TYPE_OBJECT;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.multipart.MultiPart;

import de.nrw.hbz.edoweb2.archive.exceptions.ArchiveException;
import de.nrw.hbz.edoweb2.datatypes.ComplexObject;
import de.nrw.hbz.edoweb2.datatypes.Link;
import de.nrw.hbz.edoweb2.datatypes.Node;

/**
 * Collects all methods for creating resources
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@Path("/resources")
public class PutResource
{

	final static Logger logger = LoggerFactory.getLogger(PutResource.class);

	Actions actions = null;
	final String namespace = "edoweb";

	/**
	 * @throws IOException
	 *             if Actions can't not be initialized.
	 */
	public PutResource() throws IOException
	{
		actions = new Actions();
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
	@PUT
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	@Consumes("application/json")
	public String createResource(@PathParam("pid") String pid,
			final CreateObjectBean input)
	{
		if (input.type.compareTo(ObjectType.monograph.toString()) == 0)
			return createMonograph(pid);
		else if (input.type.compareTo(ObjectType.ejournal.toString()) == 0)
			return createEJournal(pid);
		else if (input.type.compareTo(ObjectType.webpage.toString()) == 0)
			return createWebpage(pid);
		else if (input.type.compareTo(ObjectType.webpageVersion.toString()) == 0)
			return createWebpageVersion(input.parentPid, pid);
		else if (input.type.compareTo(ObjectType.ejournalVolume.toString()) == 0)
			return createEJournalVolume(input.parentPid, pid);

		throw new HttpArchiveException(Status.BAD_REQUEST.getStatusCode(),
				"The type you've provided " + input.type
						+ " does not exist or hasn't yet been implemented.");
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
	@PUT
	@Path("/{pid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes("multipart/mixed")
	public String updateResourceData(@PathParam("pid") String pid,
			MultiPart multiPart)
	{

		try
		{
			return actions.updateData(pid, multiPart.getBodyParts().get(0)
					.getEntityAs(InputStream.class), multiPart.getMediaType()
					.toString());
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

	/**
	 * @param pid
	 *            The pid of the resource
	 * @param content
	 *            the metadata as n-triple rdf
	 * @return a human readable message and a status code of 200 if successful
	 *         and 500 if not.
	 */
	@PUT
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateResourceMetadata(@PathParam("pid") String pid,
			String content)
	{
		try
		{
			return actions.updateMetadata(pid, content);
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

	private String createWebpage(String pid)
	{
		try
		{
			logger.info("create Webpage");

			if (actions.nodeExists(pid))
			{
				throw new HttpArchiveException(
						Status.INTERNAL_SERVER_ERROR.getStatusCode(),
						"Node already exists. I do nothing!");
			}
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, false);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(pid);

			rootObject.addContentModel(ContentModelFactory.createMonographCM(
					namespace, ObjectType.webpage));

			ComplexObject object = new ComplexObject(rootObject);
			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	private String createMonograph(String pid)
	{
		logger.info("create Monograph");
		try
		{
			if (actions.nodeExists(pid))
			{
				throw new HttpArchiveException(
						Status.INTERNAL_SERVER_ERROR.getStatusCode(),
						"Node already exists. I do nothing!");
			}
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, false);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(pid);

			rootObject.addContentModel(ContentModelFactory.createMonographCM(
					namespace, ObjectType.monograph));

			ComplexObject object = new ComplexObject(rootObject);
			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	private String createEJournal(String pid)
	{
		logger.info("create EJournal");
		try
		{
			if (actions.nodeExists(namespace + ":" + pid))
			{
				throw new HttpArchiveException(
						Status.INTERNAL_SERVER_ERROR.getStatusCode(), namespace
								+ ":" + pid
								+ " node already exists. I do nothing!");
			}

			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, false);
			rootObject.addRelation(link);
			rootObject.setNamespace(namespace).setPID(namespace + ":" + pid);

			rootObject.addContentModel(ContentModelFactory.createMonographCM(
					namespace, ObjectType.ejournal));

			ComplexObject object = new ComplexObject(rootObject);
			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	private String createWebpageVersion(String parentPid, String versionPid)
	{
		try
		{
			logger.info("create Webpage Version");

			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(IS_VERSION);
			link.setObject(parentPid, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(HAS_VERSION_NAME);
			link.setObject(versionPid, true);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(REL_BELONGS_TO_OBJECT);
			link.setObject(parentPid, false);
			rootObject.addRelation(link);

			rootObject.setNamespace(namespace).setPID(versionPid);

			rootObject.addContentModel(ContentModelFactory.createMonographCM(
					namespace, ObjectType.webpageVersion));

			ComplexObject object = new ComplexObject(rootObject);

			link = new Link();
			link.setPredicate(HAS_VERSION);
			link.setObject(versionPid, false);

			actions.addLink(parentPid, link);

			link = new Link();
			link.setPredicate(REL_IS_RELATED);
			link.setObject(versionPid, false);

			actions.addLink(parentPid, link);

			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	private String createEJournalVolume(String parentPid, String volumePid)
	{

		logger.info("create EJournal Volume");
		try
		{
			Node rootObject = new Node();
			rootObject.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(IS_VOLUME);
			link.setObject(parentPid, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(REL_BELONGS_TO_OBJECT);
			link.setObject(parentPid, false);
			rootObject.addRelation(link);

			link = new Link();
			link.setPredicate(HAS_VOLUME_NAME);
			link.setObject(volumePid, true);
			rootObject.addRelation(link);

			rootObject.setNamespace(namespace).setPID(volumePid);

			rootObject.addContentModel(ContentModelFactory.createMonographCM(
					namespace, ObjectType.ejournalVolume));

			ComplexObject object = new ComplexObject(rootObject);

			link = new Link();
			link.setPredicate(HAS_VOLUME);
			link.setObject(volumePid, false);
			actions.addLink(parentPid, link);

			link = new Link();
			link.setPredicate(REL_IS_RELATED);
			link.setObject(volumePid, false);
			actions.addLink(parentPid, link);

			return actions.create(object, true);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}

	}

}
