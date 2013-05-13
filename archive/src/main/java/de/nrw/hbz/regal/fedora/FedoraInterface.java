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
package de.nrw.hbz.regal.fedora;

import java.io.InputStream;
import java.util.List;

import de.nrw.hbz.regal.datatypes.ContentModel;
import de.nrw.hbz.regal.datatypes.Node;

/**
 * Class FedoraInterface
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public interface FedoraInterface
{
	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: Creates a object with properties of node
	 * </p>
	 * 
	 * @param node
	 *            the object as Fedora object
	 */
	public void createNode(Node node);

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: The corresponding fedora object will be read into a new node
	 * </p>
	 * 
	 * @param pid
	 *            The pid of an existing node.
	 * @return The node as java object
	 * 
	 */
	public Node readNode(String pid);

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: The corresponding fedora object will be updated
	 * (overwritten)
	 * </p>
	 * 
	 * @param node
	 *            It is assumed that node has the correct PID of an existing
	 *            Node which will be replaced by this method
	 */
	public void updateNode(Node node);

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: The fedora object will be deleted
	 * </p>
	 * 
	 * @param rootPID
	 *            identifier of a fedora object
	 */
	public void deleteNode(String rootPID);

	/**
	 * @param pid
	 *            the pid of the object
	 * @param datastream
	 *            the datastream that must be deleted
	 */
	public void deleteDatastream(String pid, String datastream);

	/**
	 * @param namespace
	 *            The namespace to generate a pid .
	 * @return A new generated id.
	 */
	public String getPid(String namespace);

	/**
	 * @param namespace
	 *            The namespace of the pids
	 * @param number
	 *            the number of pids
	 * @return An array of empty pids.
	 */
	public String[] getPids(String namespace, int number);

	/**
	 * @param pid
	 *            the pid of the node
	 * @return true if exists and false if not
	 */
	public boolean nodeExists(String pid);

	/**
	 * 
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description: If the object 'pid' has a datastream with 'datastreamId' the
	 * method returns true.
	 * </p>
	 * 
	 * @param pid
	 *            of the object
	 * @param datastreamId
	 *            of the objects datastream
	 * @return true if datastream exists
	 */
	public boolean dataStreamExists(String pid, String datastreamId);

	/**
	 * @param simpleQuery
	 *            the query
	 * @param queryType
	 *            if equals "simple" a simple search is done if equals "rdf" it
	 *            is the same like findTriples(rdfQuery, FedoraVocabulary.SPO,
	 *            FedoraVocabulary.N3);
	 * @return A List of pids
	 */
	public List<String> findPids(String simpleQuery, String queryType);

	/**
	 * @param rdfQuery
	 *            A query
	 * @param queryType
	 *            The type of the query
	 * @param outputFormat
	 *            the format of the returned stream
	 * @return The query result as stream
	 */
	public InputStream findTriples(String rdfQuery, String queryType,
			String outputFormat);

	/**
	 * @param pid
	 *            a pid
	 * @return , e.g info:fedora.
	 */
	public String addUriPrefix(String pid);

	/**
	 * @param pred
	 *            a predicate
	 * @return , e.g info:fedora.
	 */
	public String removeUriPrefix(String pred);

	/**
	 * Updates the ContentModel object
	 * 
	 * @param cm
	 *            A ContentModel
	 */
	public void updateContentModel(ContentModel cm);

}
