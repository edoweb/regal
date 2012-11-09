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

import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.HBZ_MODEL_NAMESPACE;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.TYPE_OBJECT;

import java.rmi.RemoteException;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import de.nrw.hbz.edoweb2.datatypes.ComplexObject;
import de.nrw.hbz.edoweb2.datatypes.ComplexObjectNode;
import de.nrw.hbz.edoweb2.datatypes.Link;
import de.nrw.hbz.edoweb2.datatypes.Node;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@Path("/wsc")
public class WSCResource
{
	String VIEW_MAIN = HBZ_MODEL_NAMESPACE + "view_main";
	String VIEW = HBZ_MODEL_NAMESPACE + "view";
	String INDEX = HBZ_MODEL_NAMESPACE + "index";
	String ARCHIVE = HBZ_MODEL_NAMESPACE + "archive";
	String WEBARCHIVE = HBZ_MODEL_NAMESPACE + "webarchive";

	ObjectType objectType = ObjectType.wsc;
	String namespace = "dtl";

	Actions actions = new Actions();

	public WSCResource()
	{

	}

	@PUT
	@Path("/{pid}")
	public String createWSC(@PathParam("pid") String pid)
	{
		try
		{
			if (actions.nodeExists(pid))
				return "ERROR: Node already exists";
			Node root = new Node();
			root.addTitle("RootObject (not initialized yet)");
			root.setNodeType(TYPE_OBJECT);
			Link link = new Link();
			link.setPredicate(REL_IS_NODE_TYPE);
			link.setObject(TYPE_OBJECT, true);
			root.addRelation(link);
			root.setNamespace(namespace).setPID(pid)
					.addType(objectType.toString()).addRights("me");
			root.addContentModel(ContentModelFactory.createWpdCM(namespace,
					objectType));

			Node view_main = new Node(pid + "_1");
			view_main.addTitle("Fulltext XML (not initialized yet)");
			Node view = new Node(pid + "_2");
			view.addTitle("OCR XML (not initialized yet)");
			Node index = new Node(pid + "_3");
			index.addTitle("TOC XML (not initialized yet)");
			Node fulltext = new Node(pid + "_4");
			fulltext.addTitle("Fulltext Data(not initialized yet)");
			Node ocr = new Node(pid + "_5");
			ocr.addTitle("OCR Data (not initialized yet)");
			Node toc = new Node(pid + "_6");
			toc.addTitle("TOC DATA (not initialized yet)");

			root.addRelation(new Link(VIEW_MAIN, actions.addUriPrefix(view_main
					.getPID()), false));
			root.addRelation(new Link(VIEW,
					actions.addUriPrefix(view.getPID()), false));
			root.addRelation(new Link(INDEX, actions.addUriPrefix(index
					.getPID()), false));
			//
			// root.addRelation(new Link(FULLTEXT, actions.addUriPrefix(fulltext
			// .getPID()), false));
			// root.addRelation(new Link(OCR,
			// actions.addUriPrefix(ocr.getPID()),
			// false));
			// root.addRelation(new Link(TOC,
			// actions.addUriPrefix(toc.getPID()),
			// false));

			ComplexObject object = new ComplexObject(root);
			object.addChild(new ComplexObjectNode(view_main));
			object.addChild(new ComplexObjectNode(view));
			object.addChild(new ComplexObjectNode(index));
			object.addChild(new ComplexObjectNode(fulltext));
			object.addChild(new ComplexObjectNode(ocr));
			object.addChild(new ComplexObjectNode(toc));

			return actions.create(object);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return "failed";
	}

}
