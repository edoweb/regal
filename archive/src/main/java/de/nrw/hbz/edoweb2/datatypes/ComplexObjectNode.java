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

import java.util.List;
import java.util.Vector;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 */
public class ComplexObjectNode
{

	Node me = null;
	List<ComplexObjectNode> children = null;

	public ComplexObjectNode(Node node)
	{
		children = new Vector<ComplexObjectNode>();
		me = node;
	}

	public ComplexObjectNode(Node node, String pid)
	{
		children = new Vector<ComplexObjectNode>();
		me = node;
		me.setPID(pid);
	}

	public ComplexObjectNode(String pid)
	{
		children = new Vector<ComplexObjectNode>();
		me = new Node();
		me.setPID(pid);
	}

	public Node getMe()
	{
		return me;
	}

	public void setMe(Node me)
	{
		this.me = me;
	}

	public boolean addChild(ComplexObjectNode child)
	{
		if (child == null)
			return false;
		if (me == null)
			System.out.println("NULL STUPID");
		child.getMe().setNamespace(me.getNamespace());
		return children.add(child);
	}

	public ComplexObjectNode getChild(int index)
	{
		return children.get(index);
	}

	public int sizeOfChildren()
	{
		return children.size();
	}

}
