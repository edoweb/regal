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
package de.nrw.hbz.edoweb2.datatypes;

import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.TYPE_OBJECT;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 */
public class ComplexObject
{

	ComplexObjectNode root = null;

	public ComplexObject()
	{
		root = new ComplexObjectNode(new Node());
		root.getMe().setNodeType(TYPE_OBJECT);
		Link link = new Link();
		link.setPredicate(REL_IS_NODE_TYPE);
		link.setObject(TYPE_OBJECT, false);
		root.getMe().addRelation(link);
	}

	public ComplexObject(Node root)
	{
		this.root = new ComplexObjectNode(root);
	}

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

	public Node getRoot()
	{
		return root.getMe();
	}

	public void setRoot(ComplexObjectNode root)
	{
		this.root = root;

	}

	public boolean addChild(ComplexObjectNode arg0)
	{

		return root.addChild(arg0);
	}

	public ComplexObjectNode getChild(int index)
	{
		return root.getChild(index);
	}

	public int sizeOfChildren()
	{
		return root.sizeOfChildren();
	}

}
