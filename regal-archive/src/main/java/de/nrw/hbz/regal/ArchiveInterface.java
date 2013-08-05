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
package de.nrw.hbz.regal;

import java.io.InputStream;
import java.util.List;

import de.nrw.hbz.regal.datatypes.ComplexObject;
import de.nrw.hbz.regal.datatypes.ContentModel;
import de.nrw.hbz.regal.datatypes.Node;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 */
public interface ArchiveInterface {

    /**
     * @param namespace
     *            The namespace of the new object
     * @return The new created object.
     */
    public Node createRootObject(String namespace);

    /**
     * @param object
     *            A complex object
     * @return the root of the newly created complex object
     */
    public Node createComplexObject(ComplexObject object);

    /**
     * Creates a node.
     * 
     * @param node
     *            the new node
     * @return The new created object.
     */
    public Node createNode(Node node);

    /**
     * Adds a new node as child of the parent.
     * 
     * @param parent
     *            the parent of the new node
     * @param node
     *            the new node
     * @return The new created object.
     */
    public Node createNode(Node parent, Node node);

    /**
     * @param parentPid
     *            the pid of an existing node
     * @return the new node
     */
    public Node createNode(String parentPid);

    /**
     * @param pid
     *            the pid of the object to read
     * @return A tree-like complex object
     */
    public ComplexObject readComplexObject(String pid);

    /**
     * @param pid
     *            of the node
     * @return the node
     */
    public Node readNode(String pid);

    /**
     * @param object
     *            The new verions of the object
     */
    public void updateComplexObject(ComplexObject object);

    /**
     * @param pid
     *            The pid of the node to update
     * @param node
     *            The new version of the node
     */
    public void updateNode(String pid, Node node);

    /**
     * @param pid
     *            The pid that must be deleted
     * @return The pid that has been deleted
     */
    public String deleteComplexObject(String pid);

    /**
     * @param pid
     *            The node that must be deleted
     * @return the pid that has been deleted
     */
    public String deleteNode(String pid);

    /**
     * @param pid
     *            the pid of the object
     * @param datastreamName
     *            the name of the datastream that must be deleted
     * @return the pid:datastream that has been deleted
     */
    public String deleteDatastream(String pid, String datastreamName);

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
     *            pids from this namespace will be returned
     * @param number
     *            number of pids
     * @return A Array of pids.
     */
    public String[] getPids(String namespace, int number);

    /**
     * @param pid
     *            the pid of the node
     * @return true if the node exists false if not
     */
    public boolean nodeExists(String pid);

    /**
     * @param pid
     *            A pid.
     * @return the pid prfixed with a certain namespace, e.g info:fedora.
     */
    public String addUriPrefix(String pid);

    /**
     * Removes "info:fedora/" from pred
     * 
     * @param pred
     *            string
     * @return the predicate prfixed with a certain namespace, e.g info:fedora.
     */
    public String removeUriPrefix(String pred);

    /**
     * Update of the contenModel object.
     * 
     * @param createEdowebMonographModel
     *            a contentModel
     */
    public void updateContentModel(ContentModel createEdowebMonographModel);

    /**
     * @param node
     *            dc stream will be added to this node
     * @param in
     *            stream containing xml dc data
     * @param dcNamespace
     *            namespace of the dc
     */
    public void readDcToNode(Node node, InputStream in, String dcNamespace);

}