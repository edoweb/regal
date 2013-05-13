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
package de.nrw.hbz.regal.datatypes;

import static de.nrw.hbz.regal.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.regal.datatypes.Vocabulary.TYPE_OBJECT;

/**
 * A Complex object is a tree of ComplexObjectNode.
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 */
public class ComplexObject
{

	ComplexObjectNode root = null;

	/**
	 * Creates a object with type TYPE_OBJECT
	 * 
	 */
	public ComplexObject()
	{
		root = new ComplexObjectNode(new Node());
		root.getMe().setNodeType(TYPE_OBJECT);
		Link link = new Link();
		link.setPredicate(REL_IS_NODE_TYPE);
		link.setObject(TYPE_OBJECT, false);
		root.getMe().addRelation(link);
	}

	/**
	 * @param root
	 *            the root node of the complex object
	 */
	public ComplexObject(Node root)
	{
		this.root = new ComplexObjectNode(root);
	}

	/**
	 * @param namespace
	 *            create the object in a certain namespace
	 */
	public ComplexObject(String namespace)
	{
		Node rootObject = new Node();
		rootObject.setNamespace(namespace);
		root = new ComplexObjectNode(rootObject);
		root.getMe().setNodeType(TYPE_OBJECT);
		Link link = new Link();
		link.setPredicate(REL_IS_NODE_TYPE);
		link.setObject(TYPE_OBJECT, false);
		root.getMe().addRelation(link);

	}

	/**
	 * @param namespace
	 *            The intended namespace of the object.
	 * @param pid
	 *            The pid (with namespace prefix) of the new node.
	 */
	public ComplexObject(String namespace, String pid)
	{
		Node rootObject = new Node();
		rootObject.setNamespace(namespace);
		rootObject.setPID(pid);
		root = new ComplexObjectNode(rootObject);
		root.getMe().setNodeType(TYPE_OBJECT);
		Link link = new Link();
		link.setPredicate(REL_IS_NODE_TYPE);
		link.setObject(TYPE_OBJECT, true);
		root.getMe().addRelation(link);

	}

	/**
	 * @return the root node of the complex object
	 */
	public Node getRoot()
	{
		return root.getMe();
	}

	/**
	 * @param root
	 *            the root node of the complex object
	 */
	public void setRoot(ComplexObjectNode root)
	{
		this.root = root;

	}

	/**
	 * @param node
	 *            Add a new child to the root node.
	 * @return true if success, false if not. TODO: Exceptions!
	 */
	public boolean addChild(ComplexObjectNode node)
	{
		return root.addChild(node);
	}

	/**
	 * @param index
	 *            The index of the child do be returned
	 * @return a node
	 */
	public ComplexObjectNode getChild(int index)
	{
		return root.getChild(index);
	}

	/**
	 * @return the number of childs.
	 */
	public int sizeOfChildren()
	{
		return root.sizeOfChildren();
	}

}
