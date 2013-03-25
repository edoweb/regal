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
package de.nrw.hbz.edoweb2.archive;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.List;

import de.nrw.hbz.edoweb2.datatypes.ComplexObject;
import de.nrw.hbz.edoweb2.datatypes.Node;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 */
public interface ArchiveInterface
{

	/**
	 * @param namespace
	 *            The namespace of the new object
	 * @return The new created object.
	 * @throws RemoteException
	 *             if backend is not available
	 */
	public Node createRootObject(String namespace);

	/**
	 * @param object
	 *            A complex object
	 * @return the root of the newly created complex object
	 * @throws RemoteException
	 *             if backend is not available
	 */
	public Node createComplexObject(ComplexObject object);

	/**
	 * Adds a new node as child of the parent.
	 * 
	 * @param parent
	 *            the parent of the new node
	 * @param node
	 *            the new node
	 * @return The new created object.
	 * @throws RemoteException
	 *             if backend is not available
	 */
	public Node createNode(Node parent, Node node);

	/**
	 * @param parentPid
	 *            the pid of an existing node
	 * @return the new node
	 * @throws RemoteException
	 *             if backend is not available
	 */
	public Node createNode(String parentPid);

	/**
	 * @param pid
	 *            the pid of the node to read
	 * @return the node
	 * @throws RemoteException
	 *             if backend is not available
	 */
	public Node readObject(String pid);

	/**
	 * @param pid
	 *            the pid of the object to read
	 * @return A tree-like complex object
	 * @throws RemoteException
	 *             if backend is not available
	 */
	public ComplexObject readComplexObject(String pid);

	/**
	 * @param pid
	 *            of the node
	 * @return the node
	 * @throws RemoteException
	 *             if backend is not available
	 */
	public Node readNode(String pid);

	/**
	 * @param pid
	 *            of the node to update
	 * @param node
	 *            the new version of the node
	 * @throws RemoteException
	 *             if backend is not available
	 */
	public void updateObject(String pid, Node node);

	/**
	 * @param object
	 *            The new verions of the object
	 * @throws RemoteException
	 *             if backend is not available
	 */
	public void updateComplexObject(ComplexObject object);

	/**
	 * @param pid
	 *            The pid of the node to update
	 * @param node
	 *            The new version of the node
	 * @throws RemoteException
	 *             if backend is not available
	 */
	public void updateNode(String pid, Node node);

	/**
	 * @param pid
	 *            The pid that must be deleted
	 * @return The pid that has been deleted
	 * @throws RemoteException
	 *             if backend is not available
	 */
	public String deleteComplexObject(String pid);

	/**
	 * @param pid
	 *            The node that must be deleted
	 * @return the pid that has been deleted
	 */
	public String deleteNode(String pid);

	/**
	 * @param searchTerm
	 *            A search term
	 * @return A list of pids
	 */
	public List<String> findNodes(String searchTerm);

	/**
	 * @param rdfQuery
	 *            An rdf query
	 * @param queryType
	 *            The type of the query
	 * @param outputFormat
	 *            the type of the result
	 * @return the result as stream
	 */
	public InputStream findTriples(String rdfQuery, String queryType,
			String outputFormat);

	/**
	 * @param namespace
	 * @param number
	 * @return
	 */
	public String[] getPids(String namespace, int number);

	/**
	 * @param pid
	 * @return
	 */
	public boolean nodeExists(String pid);

	/**
	 * @param pid
	 *            A pid.
	 * @return the pid prfixed with a certain namespace, e.g info:fedora.
	 */
	public String addUriPrefix(String pid);

	/**
	 * @param pred
	 * @return the predicate prfixed with a certain namespace, e.g info:fedora.
	 */
	public String removeUriPrefix(String pred);

}