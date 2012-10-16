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
package de.nrw.hbz.edoweb2.fedora;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.List;

import de.nrw.hbz.edoweb2.datatypes.Node;

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
	public void createNode(Node node) throws RemoteException, IOException;

	public Node readNode(String nodePid) throws RemoteException;

	public void updateNode(Node node) throws RemoteException,
			UnsupportedEncodingException;

	public void deleteNode(String rootPID);

	public String getPid(String namespace) throws RemoteException;

	public String[] getPids(String namespace, int number)
			throws RemoteException;

	public boolean nodeExists(String pid);

	public boolean dataStreamExists(String pid, String datastreamId);

	public List<String> findPids(String simpleQuery, String queryType);

	public InputStream findTriples(String rdfQuery, String queryType,
			String outputFormat);

	public String addUriPrefix(String pid);

	public String removeUriPrefix(String pred);

}
