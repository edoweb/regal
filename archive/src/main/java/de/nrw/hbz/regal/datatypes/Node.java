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

import static de.nrw.hbz.regal.datatypes.Vocabulary.TYPE_NODE;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.HAS_PART;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.IS_PART_OF;

import java.net.URL;
import java.util.Date;
import java.util.Vector;

/**
 * Class Node
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * 
 * About info uris: http://info-uri.info/registry/docs/misc/faq.html
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 11.08.2010
 * 
 */
public class Node
{

	protected String hasNodeType = null;
	protected String isNodeTypeOf = null;

	String metadataFile;
	String uploadFile;
	URL dataUrl;
	URL metadataUrl;
	Vector<Link> relsExt = new Vector<Link>();
	Vector<String> parentObjects = new Vector<String>();
	Vector<ContentModel> cms = new Vector<ContentModel>();
	private String label = null;
	private String type = null;
	private String pid = null;
	private String state = null;
	private String fileName = null;
	private String contentURL = null;
	private String mimeType = null;
	private String namespace = null;
	private String contentType = null;
	private Date lastModified = null;
	DCBean bean = new DCBean();

	public Node()
	{
		this.isNodeTypeOf = HAS_PART;
		this.hasNodeType = HAS_PART;
		setNodeType(TYPE_NODE);
	}

	public Node(String pid)
	{
		this.isNodeTypeOf = HAS_PART;
		this.hasNodeType = HAS_PART;
		setNodeType(TYPE_NODE);
		setPID(pid);
	}

	public Node addRelation(Link link)
	{
		if (link.getPredicate().compareTo(IS_PART_OF) == 0)
		{
			addObject(link.getObject());
			link.setLiteral(false);
		}
		relsExt.add(link);
		return this;
	}

	public Node addContentModel(ContentModel cm)
	{
		cms.add(cm);
		return this;
	}

	public Node addRelation(Node node)
	{
		// Me to you
		Link meToNode = new Link();
		meToNode.setPredicate(node.getHasNodeType());
		meToNode.setObject(Vocabulary.FEDORA_INFO_NAMESPACE + node.getPID(),
				false);
		addRelation(meToNode);

		// You to my parent objects
		Vector<String> objects = getObjects();
		if (objects == null || objects.size() == 0)

		{

			Link isParentRel = new Link();
			isParentRel.setPredicate(IS_PART_OF);
			isParentRel.setObject(Vocabulary.FEDORA_INFO_NAMESPACE + getPID(),
					false);
			node.addRelation(isParentRel);

		}
		else
			for (String rootPid : objects)
			{
				Link isParentRel = new Link();
				isParentRel.setPredicate(IS_PART_OF);
				isParentRel.setObject(rootPid, false);
				node.addRelation(isParentRel);

			}

		// You to me
		Link nodeToMe = new Link();
		nodeToMe.setPredicate(node.getIsNodeTypeOf());
		nodeToMe.setObject(Vocabulary.FEDORA_INFO_NAMESPACE + getPID(), false);
		node.addRelation(nodeToMe);

		return this;
	}

	public void removeRelation(String pred, String obj)
	{
		Vector<Link> newRels = new Vector<Link>();

		for (Link link : relsExt)
		{
			if (link.getPredicate().compareTo(pred) == 0
					&& link.getObject().compareTo(obj) == 0)
			{

			}
			else
			{
				newRels.add(link);
			}
		}

		deleteObjects();

		setRelsExt(newRels);
	}

	public Node setPID(String pid)
	{
		this.pid = pid;
		return this;
	}

	public Node setRelsExt(Vector<Link> rels)
	{
		// myNode.setRELSEXT(new RELSEXT_type0());
		relsExt = new Vector<Link>();
		for (Link link : rels)
		{
			addRelation(link);
		}

		return this;
	}

	public Node setNamespace(String namespace)
	{
		this.namespace = namespace;
		return this;
	}

	public Node setHasNodeType(String hasNodeType)
	{
		this.hasNodeType = hasNodeType;

		return this;
	}

	public Node setIsNodeTypeOf(String isNodeTypeOf)
	{
		this.isNodeTypeOf = isNodeTypeOf;

		return this;
	}

	public Node setNodeType(String str)
	{
		this.type = str;

		return this;
	}

	public Node setState(String str)
	{
		this.state = str;
		return this;
	}

	public Node setUploadData(String localLocation, String fileName,
			String mimeType)
	{
		uploadFile = localLocation;
		setMimeType(mimeType);
		setFileName(fileName);
		return this;

	}

	public Node setContentURL(String url)
	{
		this.contentURL = url;
		return this;
	}

	public Node setUploadFile(String uploadFile)
	{
		this.uploadFile = uploadFile;
		return this;
	}

	public Node setMimeType(String mimeType)
	{
		this.mimeType = mimeType;
		return this;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public Node setFileName(String fileName)
	{
		if (fileName != null)
		{
			int length = fileName.length();
			if (length > 32)
			{
				fileName = fileName.substring(0, 14) + "___"
						+ fileName.substring(length - 14);
			}
		}
		this.fileName = fileName;
		return this;
	}

	public String getMetadataFile()
	{
		return metadataFile;
	}

	public void setMetadataFile(String metadataFile)
	{
		this.metadataFile = metadataFile;
	}

	public String getMimeType()
	{
		return mimeType;
	}

	public String getIsNodeTypeOf()
	{
		return isNodeTypeOf;
	}

	public String getContentURL()
	{
		return contentURL;
	}

	public String getUploadFile()
	{
		return uploadFile;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName()
	{
		return fileName;
	}

	public Vector<ContentModel> getContentModels()
	{

		return cms;
	}

	public String getState()
	{
		return state;
	}

	public Vector<String> getObjects()
	{
		return parentObjects;
	}

	public String getHasNodeType()
	{
		return hasNodeType;
	}

	public String getPID()
	{
		return pid;
	}

	public Vector<Link> getRelsExt()
	{
		return relsExt;
	}

	public String getNamespace()
	{
		return namespace;
	}

	// ??????
	public String getNodeType()
	{
		return type;
	}

	public String getLabel()
	{
		return label;
	}

	public Node setLabel(String str)
	{
		label = str;
		return this;
	}

	private Node addObject(String obj)
	{
		parentObjects.add(obj);
		return this;
	}

	private void deleteObjects()
	{
		parentObjects = new Vector<String>();
	}

	public URL getDataUrl()
	{

		return dataUrl;
	}

	public void setDataUrl(URL url)
	{
		this.dataUrl = url;
	}

	public URL getMetadataUrl()
	{

		return metadataUrl;
	}

	public void setMetadataUrl(URL url)
	{
		this.metadataUrl = url;
	}

	public Node addContributer(String e)
	{
		bean.addContributer(e);
		return this;
	}

	public Node addCoverage(String e)
	{
		bean.addCoverage(e);
		return this;
	}

	public Node addCreator(String e)
	{
		bean.addCreator(e);
		return this;
	}

	public Node addDate(String e)
	{
		bean.addDate(e);
		return this;
	}

	public Node addDescription(String e)
	{
		bean.addDescription(e);
		return this;
	}

	public Vector<String> getFormat()
	{
		return bean.getFormat();
	}

	public Node addFormat(String e)
	{
		bean.addFormat(e);
		return this;
	}

	public Vector<String> getIdentifier()
	{
		return bean.getIdentifier();
	}

	public Node addIdentifier(String e)
	{
		bean.addIdentifier(e);
		return this;
	}

	public Vector<String> getLanguage()
	{
		return bean.getLanguage();
	}

	public Node addLanguage(String e)
	{
		bean.addLanguage(e);
		return this;
	}

	public Vector<String> getPublisher()
	{
		return bean.getPublisher();
	}

	public Node addPublisher(String e)
	{
		bean.addPublisher(e);
		return this;
	}

	public Vector<String> getDCRelation()
	{
		return bean.getRelation();
	}

	public Node addDCRelation(String e)
	{
		bean.addRelation(e);
		return this;
	}

	public Vector<String> getRights()
	{
		return bean.getRights();
	}

	public Node addRights(String e)
	{
		bean.addRights(e);
		return this;
	}

	public String getFirstRights()
	{
		return bean.getFirstRights();
	}

	public Vector<String> getSource()
	{
		return bean.getSource();
	}

	public Node addSource(String e)
	{
		bean.addSource(e);
		return this;
	}

	public String getFirstSource()
	{
		return bean.getFirstSource();
	}

	public Vector<String> getSubject()
	{
		return bean.getSubject();
	}

	public Node addSubject(String e)
	{
		bean.addSubject(e);
		return this;
	}

	public String getFirstSubject()
	{
		return bean.getFirstSubject();
	}

	public Vector<String> getTitle()
	{
		return bean.getTitle();
	}

	public Node addTitle(String e)
	{
		bean.addTitle(e);
		return this;
	}

	public String getFirstTitle()
	{
		return bean.getFirstTitle();
	}

	public Vector<String> getType()
	{
		return bean.getType();
	}

	public Node addType(String e)
	{
		bean.addType(e);
		return this;
	}

	@Override
	public boolean equals(Object obj)
	{
		return bean.equals(obj);
	}

	public Vector<String> getContributer()
	{
		return bean.getContributer();
	}

	public String getFirstContributer()
	{
		return bean.getFirstContributer();
	}

	public Vector<String> getCoverage()
	{
		return bean.getCoverage();
	}

	public String getFirstCoverage()
	{
		return bean.getFirstCoverage();
	}

	public Vector<String> getCreator()
	{
		return bean.getCreator();
	}

	public String getFirstCreator()
	{
		return bean.getFirstCreator();
	}

	public Vector<String> getDate()
	{
		return bean.getDate();
	}

	public String getFirstDate()
	{
		return bean.getFirstDate();
	}

	public Vector<String> getDescription()
	{
		return bean.getDescription();
	}

	public String getFirstDescription()
	{
		return bean.getFirstDescription();
	}

	public String getFirstFormat()
	{
		return bean.getFirstFormat();
	}

	public String getFirstIdentifier()
	{
		return bean.getFirstIdentifier();
	}

	public String getFirstLanguage()
	{
		return bean.getFirstLanguage();
	}

	public String getFirstPublisher()
	{
		return bean.getFirstPublisher();
	}

	public String getFirstRelation()
	{
		return bean.getFirstRelation();
	}

	public String getFirstType()
	{
		return bean.getFirstType();
	}

	@Override
	public int hashCode()
	{
		return bean.hashCode();
	}

	public void setContributer(Vector<String> contributer)
	{
		bean.setContributer(contributer);
	}

	public void setCoverage(Vector<String> coverage)
	{
		bean.setCoverage(coverage);
	}

	public void setCreator(Vector<String> creator)
	{
		bean.setCreator(creator);
	}

	public void setDate(Vector<String> date)
	{
		bean.setDate(date);
	}

	public void setDescription(Vector<String> description)
	{
		bean.setDescription(description);
	}

	public void setFormat(Vector<String> format)
	{
		bean.setFormat(format);
	}

	public void setIdentifier(Vector<String> identifier)
	{
		bean.setIdentifier(identifier);
	}

	public void setLanguage(Vector<String> language)
	{
		bean.setLanguage(language);
	}

	public void setPublisher(Vector<String> publisher)
	{
		bean.setPublisher(publisher);
	}

	public void setDCRelation(Vector<String> relation)
	{
		bean.setRelation(relation);
	}

	public void setRights(Vector<String> rights)
	{
		bean.setRights(rights);
	}

	public void setSource(Vector<String> source)
	{
		bean.setSource(source);
	}

	public void setSubject(Vector<String> subject)
	{
		bean.setSubject(subject);
	}

	public void setTitle(Vector<String> title)
	{
		bean.setTitle(title);
	}

	public void setType(Vector<String> type)
	{
		bean.setType(type);
	}

	public DCBean getBean()
	{
		return bean;
	}

	public void setContentType(String type)
	{
		contentType = type;
	}

	public String getContentType()
	{
		return contentType;
	}

	public Date getLastModified()
	{
		return lastModified;
	}

	public void setLastModified(Date lastModified)
	{
		this.lastModified = lastModified;
	}
}
