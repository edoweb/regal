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

import static de.nrw.hbz.regal.fedora.FedoraVocabulary.HAS_PART;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.IS_PART_OF;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.SIMPLE;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.SPO;

import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import de.nrw.hbz.regal.datatypes.ComplexObject;
import de.nrw.hbz.regal.datatypes.ComplexObjectNode;
import de.nrw.hbz.regal.datatypes.ContentModel;
import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.exceptions.ArchiveException;
import de.nrw.hbz.regal.exceptions.NodeNotFoundException;
import de.nrw.hbz.regal.fedora.FedoraFacade;
import de.nrw.hbz.regal.fedora.FedoraInterface;
import de.nrw.hbz.regal.fedora.FedoraVocabulary;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
class Archive implements ArchiveInterface {
    // final static Logger logger = LoggerFactory.
    // .getLogger(HBZFedoraIngester.class);

    private FedoraInterface fedoraInterface = null;
    private static Archive me = null;

    public static Archive getInstance(String host, String user, String password) {
	if (me == null) {
	    me = new Archive(host, user, password);
	}
	return me;
    }

    private Archive(String host, String user, String password) {
	fedoraInterface = new FedoraFacade(host, user, password);
    }

    public FedoraInterface getFedoraInterface() {
	return fedoraInterface;
    }

    public void setFedoraInterface(FedoraInterface my_fedoraInterface) {
	this.fedoraInterface = my_fedoraInterface;
    }

    @Override
    public Node createRootObject(String namespace) {
	Node rootObject = null;
	String pid = fedoraInterface.getPid(namespace);
	rootObject = new Node();
	rootObject.setPID(pid);
	rootObject.setLabel("Default Object");
	rootObject.setNamespace(namespace);
	fedoraInterface.createNode(rootObject);
	return rootObject;
    }

    @Override
    public Node createNode(String parentPid) {
	Node node = null;
	Node parent = null;
	parent = fedoraInterface.readNode(parentPid);
	String namespace = parent.getNamespace();
	String pid = fedoraInterface.getPid(namespace);
	node = new Node();
	node.setPID(pid);
	node.setLabel("Blank Node");
	node.setNamespace(namespace);
	return createNode(parent, node);
    }

    @Override
    public Node createNode(Node parent, Node node) {
	String pid = node.getPID();
	if (nodeExists(pid)) {
	    throw new ArchiveException(pid + " already exists. Can't create.");
	}
	String namespace = parent.getNamespace();// FedoraFacade.pred2pid(parent.getNamespace());
	if (pid == null) {
	    pid = fedoraInterface.getPid(namespace);
	    node.setPID(pid);
	    node.setNamespace(namespace);
	}
	if (!fedoraInterface.nodeExists(pid)) {
	    node.setNamespace(namespace);
	    fedoraInterface.createNode(node);
	}
	node = fedoraInterface.readNode(node.getPID());
	// Parent to node
	Link meToNode = new Link();
	meToNode.setPredicate(FedoraVocabulary.HAS_PART);
	meToNode.setObject(addUriPrefix(node.getPID()), false);
	parent.addRelation(meToNode);
	Link nodeToMe = new Link();
	nodeToMe.setPredicate(FedoraVocabulary.IS_PART_OF);
	nodeToMe.setObject(addUriPrefix(parent.getPID()), false);
	node.addRelation(nodeToMe);
	fedoraInterface.updateNode(node);
	fedoraInterface.updateNode(parent);
	return node;
    }

    @Override
    public Node createComplexObject(ComplexObject tree) {
	Node object = tree.getRoot();
	createNode(object);
	for (int i = 0; i < tree.sizeOfChildren(); i++) {
	    ComplexObjectNode node = tree.getChild(i);
	    iterateCreate(node, object);
	}
	return readNode(object.getPID());

    }

    private void iterateCreate(ComplexObjectNode tnode, Node parent) {
	Node node = tnode.getMe();
	node = createNode(parent, node);
	for (int i = 0; i < tnode.sizeOfChildren(); i++) {
	    ComplexObjectNode n1 = tnode.getChild(i);
	    iterateCreate(n1, node);
	}
    }

    @Override
    public ComplexObject readComplexObject(String rootPID) {
	Node object = fedoraInterface.readNode(rootPID);
	ComplexObject complexObject = new ComplexObject(object);
	Vector<Link> rels = object.getRelsExt();
	for (Link rel : rels) {
	    if (rel.getPredicate().compareTo(HAS_PART) == 0) {
		String pid = removeUriPrefix(rel.getObject());
		if (pid.compareTo(rootPID) == 0)
		    continue;
		Node child = readNode(pid);
		ComplexObjectNode cn = new ComplexObjectNode(child);
		complexObject.addChild(cn);
		add(rootPID, cn, child.getRelsExt());
	    }
	}
	return complexObject;
    }

    private void add(String rootPID, ComplexObjectNode cn, Vector<Link> rels) {
	for (Link rel : rels) {
	    if (rel.getPredicate().compareTo(HAS_PART) == 0) {
		String pid = removeUriPrefix(rel.getObject());
		if (pid.compareTo(rootPID) == 0)
		    continue;
		Node child = readNode(pid);
		ComplexObjectNode cn2 = new ComplexObjectNode(child);
		cn.addChild(cn2);
		add(rootPID, cn2, child.getRelsExt());
	    }
	}
    }

    @Override
    public Node readNode(String rootPID) {
	if (!nodeExists(rootPID)) {
	    throw new NodeNotFoundException(rootPID + " doesn't exist.");
	}
	Node node = fedoraInterface.readNode(rootPID);
	return node;
    }

    @Override
    public void updateNode(String nodePid, Node node) {
	if (!nodeExists(nodePid)) {
	    throw new NodeNotFoundException(nodePid + " doesn't exist.");
	}
	node.setPID(nodePid);
	fedoraInterface.updateNode(node);
	// sesame.updateNode(node);
    }

    @Override
    public String deleteComplexObject(String rootPID) {
	if (!nodeExists(rootPID)) {
	    throw new NodeNotFoundException(rootPID
		    + " doesn't exist. Can't delete!");
	}
	// logger.info("deleteObject");
	fedoraInterface.deleteNode(rootPID);
	// Find all children
	List<String> pids = null;
	pids = fedoraInterface.findPids("* <" + IS_PART_OF + "> <"
		+ addUriPrefix(rootPID) + ">", SPO);
	// Delete all children
	if (pids != null)
	    for (String pid : pids) {
		// Remove relation
		Node node = readNode(pid);
		Vector<String> objects = node.getParents();
		// If no object relation remains: delete
		if (objects == null || objects.size() == 1)
		    deleteComplexObject(node.getPID());
		else {
		    System.out
			    .println(pid
				    + " node is shared by other objects. Can't delete!");
		    node.removeRelation(IS_PART_OF, rootPID);
		    fedoraInterface.updateNode(node);
		}
	    }
	return rootPID;
    }

    @Override
    public String deleteNode(String pid) {
	if (!nodeExists(pid)) {
	    throw new NodeNotFoundException(pid
		    + " doesn't exist. Can't delete node.");
	}
	fedoraInterface.deleteNode(pid);
	return pid;
    }

    @Override
    public String deleteDatastream(String pid, String datastreamName) {
	if (!nodeExists(pid)) {
	    throw new NodeNotFoundException(pid
		    + " doesn't exist. Can't delete node.");
	}
	fedoraInterface.deleteDatastream(pid, datastreamName);
	return pid;
    }

    @Override
    public List<String> findNodes(String searchTerm) {
	return fedoraInterface.findPids(searchTerm, SIMPLE);
    }

    @Override
    public String[] getPids(String namespace, int number) {
	return fedoraInterface.getPids(namespace, number);
    }

    @Override
    public void updateComplexObject(ComplexObject tree) {
	Node object = tree.getRoot();
	for (int i = 0; i < tree.sizeOfChildren(); i++) {
	    ComplexObjectNode node = tree.getChild(i);
	    iterateUpdate(node, object);
	}
	updateNode(object.getPID(), object);
    }

    private void iterateUpdate(ComplexObjectNode tnode, Node parent) {
	Node node = tnode.getMe();
	updateNode(node.getPID(), node);
	for (int i = 0; i < tnode.sizeOfChildren(); i++) {
	    ComplexObjectNode n1 = tnode.getChild(i);
	    iterateUpdate(n1, node);
	}
    }

    @Override
    public boolean nodeExists(String pid) {
	return fedoraInterface.nodeExists(pid);
    }

    @Override
    public InputStream findTriples(String rdfQuery, String queryType,
	    String outputFormat) {
	return fedoraInterface.findTriples(rdfQuery, queryType, outputFormat);
    }

    @Override
    public String addUriPrefix(String pid) {

	return fedoraInterface.addUriPrefix(pid);
    }

    @Override
    public String removeUriPrefix(String pred) {

	return fedoraInterface.removeUriPrefix(pred);
    }

    @Override
    public void updateContentModel(ContentModel createEdowebMonographModel) {
	fedoraInterface.updateContentModel(createEdowebMonographModel);
    }

    private Node createNode(Node object) {
	Node rootObject = null;
	String pid = object.getPID();
	if (nodeExists(pid)) {
	    throw new ArchiveException(pid + " already exists. Can't create.");
	}
	String namespace = object.getNamespace();
	if (namespace == null) {
	    throw new ArchiveException(pid + " has no namespace.");
	}
	if (pid == null) {
	    pid = fedoraInterface.getPid(namespace);
	    object.setPID(pid);
	}
	fedoraInterface.createNode(object);
	return rootObject;
    }

    public void readDcToNode(Node node, InputStream in, String dcNamespace) {
	fedoraInterface.readDcToNode(node, in, dcNamespace);
    }

    /*
     * #How is the #earth so #small. #elliptic #rider, #spaceegg, #transport of
     * my #soul. #ahouuuuu
     */

}
