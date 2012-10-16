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

	public Node createRootObject(String namespace) throws RemoteException;

	public Node createComplexObject(ComplexObject tree) throws RemoteException;

	public Node createNode(Node parent, Node node) throws RemoteException;

	public Node createNode(String parentPid) throws RemoteException;

	public Node readObject(String rootPID) throws RemoteException;

	public ComplexObject readComplexObject(String rootPID)
			throws RemoteException;

	public Node readNode(String rootPID) throws RemoteException;

	public void updateObject(String nodePid, Node object)
			throws RemoteException;

	public void updateComplexObject(ComplexObject object)
			throws RemoteException;

	public void updateNode(String nodePid, Node node) throws RemoteException;

	public String deleteComplexObject(String rootPID) throws RemoteException;

	public String deleteNode(String pid);

	public List<String> findNodes(String searchTerm);

	public InputStream findTriples(String rdfQuery, String queryType,
			String outputFormat);

	public String[] getPids(String namespace, int number)
			throws RemoteException;

	public boolean nodeExists(String pid);

	public String addUriPrefix(String pid);

	public String removeUriPrefix(String pred);

}