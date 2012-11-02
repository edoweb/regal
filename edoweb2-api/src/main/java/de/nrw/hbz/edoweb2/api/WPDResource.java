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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.edoweb2.datatypes.ComplexObject;
import de.nrw.hbz.edoweb2.datatypes.ComplexObjectNode;
import de.nrw.hbz.edoweb2.datatypes.Link;
import de.nrw.hbz.edoweb2.datatypes.Node;

/**
 * /wpd01/{pid}/[dc|metadata|data]/[view_main|main|index]/[dc|metadata|data]
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@Path("/wpd")
public class WPDResource
{
	final static Logger logger = LoggerFactory.getLogger(WPDResource.class);

	String VIEW_MAIN = HBZ_MODEL_NAMESPACE + "view_main";
	String VIEW = HBZ_MODEL_NAMESPACE + "view";
	String INDEX = HBZ_MODEL_NAMESPACE + "index";

	String FULLTEXT = HBZ_MODEL_NAMESPACE + "fulltext";
	String OCR = HBZ_MODEL_NAMESPACE + "ocr";
	String TOC = HBZ_MODEL_NAMESPACE + "toc";

	ObjectType wpdType = ObjectType.wpd;
	String namespace = "dtl";

	Actions actions = new Actions();

	public WPDResource()
	{

	}

	@DELETE
	public String deleteAll()
	{
		return actions.deleteAll(actions.findByType(wpdType));
	}

	@PUT
	@Path("/{pid}")
	public String createWpd(@PathParam("pid") String pid)
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
			root.setNamespace(namespace).setPID(pid).addCreator("REST Service")
					.addType(wpdType.toString()).addRights("me");
			root.addContentModel(ContentModelFactory.createWpdCM(namespace,
					wpdType));

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

			root.addRelation(new Link(FULLTEXT, actions.addUriPrefix(fulltext
					.getPID()), false));
			root.addRelation(new Link(OCR, actions.addUriPrefix(ocr.getPID()),
					false));
			root.addRelation(new Link(TOC, actions.addUriPrefix(toc.getPID()),
					false));

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

	@GET
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	public StatusBean readWpd(@PathParam("pid") String pid)
	{
		return actions.read(pid);
	}

	@POST
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateWpd(@PathParam("pid") String pid, StatusBean status)
	{
		return actions.update(pid, status);
	}

	@DELETE
	@Path("/{pid}")
	public String deleteWpd(@PathParam("pid") String pid)
	{
		return actions.delete(pid);
	}

	@GET
	@Path("/{pid}/data")
	@Produces({ "application/*" })
	public Response readWpdData(@PathParam("pid") String pid)
	{
		return actions.readData(pid);
	}

	@GET
	@Path("/{pid}/dc")
	@Produces("application/json")
	public DCBeanAnnotated readWpdDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}

	@GET
	@Path("/{pid}/metadata")
	public Response readWpdMetadata(@PathParam("pid") String pid)
	{
		return actions.readMetadata(pid);
	}

	@POST
	@Path("/{pid}/data")
	public String updateWpdData(@PathParam("pid") String pid,
			UploadDataBean content)
	{
		return actions.updateData(pid, content);
	}

	@POST
	@Path("/{pid}/dc")
	public String updateWpdDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{
		return actions.updateDC(pid, content);
	}

	@POST
	@Path("/{pid}/metadata")
	public String updateWpdMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		return actions.updateMetadata(pid, content);
	}

	@GET
	@Path("/{pid}/view_main/dc")
	public DCBeanAnnotated readViewMainDC(@PathParam("pid") String pid)
	{

		return actions.readDC(actions.findObject(pid, VIEW_MAIN));
	}

	@GET
	@Path("/{pid}/view_main/metadata")
	public Response readViewMainMetadata(@PathParam("pid") String pid)
	{

		return actions.readMetadata(actions.findObject(pid, VIEW_MAIN));
	}

	@GET
	@Path("/{pid}/view_main/data")
	public Response readViewMainData(@PathParam("pid") String pid)
	{

		return actions.readData(actions.findObject(pid, VIEW_MAIN));
	}

	@POST
	@Path("/{pid}/view_main/data")
	public String updateViewMainData(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		String viewMainPid = actions.findObject(pid, VIEW_MAIN);

		logger.debug("Updata view_main/data " + viewMainPid);
		System.out.println("Updata view_main/data " + viewMainPid);

		return actions.updateData(viewMainPid, content);
	}

	@POST
	@Path("/{pid}/view_main/dc")
	public String updateViewMainDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{

		return actions.updateDC(actions.findObject(pid, VIEW_MAIN), content);
	}

	@POST
	@Path("/{pid}/view_main/metadata")
	public String updateViewMainMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		return actions.updateMetadata(actions.findObject(pid, VIEW_MAIN), content);
	}

	@GET
	@Path("/{pid}/view/dc")
	public DCBeanAnnotated readViewDC(@PathParam("pid") String pid)
	{

		return actions.readDC(actions.findObject(pid, VIEW));
	}

	@GET
	@Path("/{pid}/view/metadata")
	public Response readViewMetadata(@PathParam("pid") String pid)
	{

		return actions.readMetadata(actions.findObject(pid, VIEW));
	}

	@POST
	@Path("/{pid}/view/data")
	public String updateViewData(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		return actions.updateData(actions.findObject(pid, VIEW), content);
	}

	@POST
	@Path("/{pid}/view/dc")
	public String updateViewDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{

		return actions.updateDC(actions.findObject(pid, VIEW), content);
	}

	@POST
	@Path("/{pid}/view/metadata")
	public String updateViewMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		return actions.updateMetadata(actions.findObject(pid, VIEW), content);
	}

	@GET
	@Path("/{pid}/index/dc")
	public DCBeanAnnotated readIndexDC(@PathParam("pid") String pid)
	{

		return actions.readDC(actions.findObject(pid, INDEX));
	}

	@GET
	@Path("/{pid}/index/metadata")
	public Response readIndexMetadata(@PathParam("pid") String pid)
	{

		return actions.readMetadata(actions.findObject(pid, INDEX));
	}

	@GET
	@Path("/{pid}/index/data")
	public Response readIndexData(@PathParam("pid") String pid)
	{

		return actions.readData(actions.findObject(pid, INDEX));
	}

	@POST
	@Path("/{pid}/index/data")
	public String updateIndexData(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		return actions.updateData(actions.findObject(pid, INDEX), content);
	}

	@POST
	@Path("/{pid}/index/dc")
	public String updateIndexDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{

		return actions.updateDC(actions.findObject(pid, INDEX), content);
	}

	@POST
	@Path("/{pid}/index/metadata")
	public String updateIndexMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		return actions.updateMetadata(actions.findObject(pid, INDEX), content);
	}

	@GET
	@Path("/{pid}/fulltext/dc")
	public DCBeanAnnotated readFulltextDC(@PathParam("pid") String pid)
	{

		return actions.readDC(actions.findObject(pid, INDEX));
	}

	@GET
	@Path("/{pid}/fulltext/metadata")
	public Response readFulltextMetadata(@PathParam("pid") String pid)
	{

		return actions.readMetadata(actions.findObject(pid, INDEX));
	}

	@GET
	@Path("/{pid}/fulltext/data")
	public Response readFulltextData(@PathParam("pid") String pid)
	{

		return actions.readData(actions.findObject(pid, FULLTEXT));
	}

	@POST
	@Path("/{pid}/fulltext/data")
	public String updateFulltextData(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		return actions.updateData(actions.findObject(pid, FULLTEXT), content);
	}

	@POST
	@Path("/{pid}/fulltext/dc")
	public String updateFulltextDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{

		return actions.updateDC(actions.findObject(pid, FULLTEXT), content);
	}

	@POST
	@Path("/{pid}/fulltext/metadata")
	public String updateFulltextMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		return actions.updateMetadata(actions.findObject(pid, FULLTEXT), content);
	}

	@GET
	@Path("/{pid}/ocr/dc")
	public DCBeanAnnotated readOcrDC(@PathParam("pid") String pid)
	{

		return actions.readDC(actions.findObject(pid, OCR));
	}

	@GET
	@Path("/{pid}/ocr/metadata")
	public Response readOcrMetadata(@PathParam("pid") String pid)
	{

		return actions.readMetadata(actions.findObject(pid, OCR));
	}

	@GET
	@Path("/{pid}/ocr/data")
	public Response readOcrData(@PathParam("pid") String pid)
	{

		return actions.readData(actions.findObject(pid, OCR));
	}

	@POST
	@Path("/{pid}/ocr/data")
	public String updateOcrData(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		return actions.updateData(actions.findObject(pid, OCR), content);
	}

	@POST
	@Path("/{pid}/ocr/dc")
	public String updateOcrDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{

		return actions.updateDC(actions.findObject(pid, OCR), content);
	}

	@POST
	@Path("/{pid}/ocr/metadata")
	public String updateOcrMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		return actions.updateMetadata(actions.findObject(pid, OCR), content);
	}

	@GET
	@Path("/{pid}/toc/dc")
	public DCBeanAnnotated readTocDC(@PathParam("pid") String pid)
	{

		return actions.readDC(actions.findObject(pid, TOC));
	}

	@GET
	@Path("/{pid}/toc/metadata")
	public Response readTocMetadata(@PathParam("pid") String pid)
	{

		return actions.readMetadata(actions.findObject(pid, TOC));
	}

	@GET
	@Path("/{pid}/toc/data")
	public Response readTocData(@PathParam("pid") String pid)
	{

		return actions.readData(actions.findObject(pid, TOC));
	}

	@POST
	@Path("/{pid}/toc/data")
	public String updateTocData(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		return actions.updateData(actions.findObject(pid, TOC), content);
	}

	@POST
	@Path("/{pid}/toc/dc")
	public String updateTocDC(@PathParam("pid") String pid,
			DCBeanAnnotated content)
	{

		return actions.updateDC(actions.findObject(pid, TOC), content);
	}

	@POST
	@Path("/{pid}/toc/metadata")
	public String updateTocMetadata(@PathParam("pid") String pid,
			UploadDataBean content)
	{

		return actions.updateMetadata(actions.findObject(pid, TOC), content);
	}

}
