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
package de.nrw.hbz.regal.sync.ingest;

import java.util.HashMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.regal.api.ObjectType;
import de.nrw.hbz.regal.datatypes.ContentModel;
import de.nrw.hbz.regal.sync.extern.DigitalEntity;
import de.nrw.hbz.regal.sync.extern.EntityRelation;

/**
 * Class FedoraIngester
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
public class DippIngester implements IngestInterface
{
	final static Logger logger = LoggerFactory.getLogger(DippIngester.class);

	private String namespace = "dipp";
	String host = null;
	Webclient webclient = null;
	HashMap<String, String> map = new HashMap<String, String>();

	@Override
	public void init(String host, String user, String password)
	{
		this.host = host;
		webclient = new Webclient(namespace, user, password, host);
	}

	@Override
	public ContentModel createContentModel()
	{
		return null;
	}

	@Override
	public void ingest(DigitalEntity dtlBean)
	{
		String pid = dtlBean.getPid().substring(
				dtlBean.getPid().lastIndexOf(':') + 1);
		logger.info("Start ingest: " + namespace + ":" + pid);

		if (dtlBean.getType().compareTo("article") == 0)
		{
			updateArticle(dtlBean);
		}
	}

	@Override
	public void update(DigitalEntity dtlBean)
	{
		ingest(dtlBean);
	}

	private String printRelations(DigitalEntity subject, DigitalEntity dtlBean)
	{

		StringBuffer result = new StringBuffer();
		String pid = dtlBean.getPid();
		if (map.containsKey(pid))
		{
			return "";
		}
		else
		{
			map.put(pid, pid);
		}
		Vector<EntityRelation> related = dtlBean.getRelated();
		int num = related.size();
		int count = 1;
		// logger.info(pid + " Found " + num + " parts.");

		if (isParent(dtlBean))
			logger.info("\n-----------------------\n" + pid + " is a Journal!"
					+ "\n-----------------------");
		for (EntityRelation relation : related)
		{
			logger.info("INGEST-GRAPH: \"" + pid + "\"->\""
					+ relation.entity.getPid() + "\" [label=\""
					+ relation.relation + "\"]");
			if (relation.relation.compareTo("rel:isMemberOfCollection") == 0
					|| relation.relation.compareTo("rel:isSubsetOf") == 0)
			{
				String str = "<" + namespace + ":" + subject.getPid() + ">"
						+ " <http://purl.org/dc/elements/1.1/relation> \""
						+ relation.entity.getLabel() + "\" .\n";
				// logger.info(str);
				result.append(str);
				result.append(printRelations(subject, relation.entity));
			}

		}
		return result.toString();
	}

	private boolean isParent(DigitalEntity dtlBean)
	{
		if (dtlBean.getPid().contains("oai"))
			return false;
		Vector<EntityRelation> related = dtlBean.getRelated();
		int num = related.size();
		int count = 1;
		for (EntityRelation relation : related)
		{
			String rel = relation.relation;

			if (rel.compareTo("rel:isSubsetOf") == 0)
				return false;
			if (rel.compareTo("rel:isMemberOfCollection") == 0)
				return false;
		}
		return true;
	}

	private void updateArticle(DigitalEntity dtlBean)
	{
		String pid = dtlBean.getPid();
		String ppid = dtlBean.getPid().substring(
				dtlBean.getPid().lastIndexOf(':') + 1);
		dtlBean.setPid(ppid);

		String metadata = printRelations(dtlBean, dtlBean);
		// logger.info(metadata);
		map.clear();
		logger.info(pid + " " + "Found eJournal article.");
		webclient.createObject(dtlBean, "application/zip", ObjectType.article);
		logger.info(pid + " " + "updated.\n");
		webclient.metadata(dtlBean, metadata);
		logger.info(pid + " " + "and all related updated.\n");
	}

	@Override
	public void delete(String pid)
	{
		webclient.delete(pid.substring(pid.lastIndexOf(':') + 1));
	}

	@Override
	public void setNamespace(String namespace)
	{
		this.namespace = namespace;

	}
}
