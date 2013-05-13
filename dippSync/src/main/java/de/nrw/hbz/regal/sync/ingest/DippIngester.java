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

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.regal.api.ObjectType;
import de.nrw.hbz.regal.datatypes.ContentModel;
import de.nrw.hbz.regal.sync.extern.DigitalEntity;

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

		String partitionC = null;

		if (dtlBean.getType().compareTo("article") == 0)
		{
			updateEJournalPart(dtlBean);
		}
		else if (dtlBean.getType().compareTo("journal") == 0)
		{
			updateJournal(dtlBean);
		}
	}

	@Override
	public void update(DigitalEntity dtlBean)
	{
		ingest(dtlBean);
	}

	private void updateJournal(DigitalEntity dtlBean)
	{
		String pid = dtlBean.getPid();
		String ppid = dtlBean.getPid().substring(
				dtlBean.getPid().lastIndexOf(':') + 1);
		dtlBean.setPid(ppid);
		try
		{
			logger.info(pid + " Found ejournal.");

			webclient.createResource(ObjectType.ejournal, dtlBean);
			webclient.metadata(dtlBean);
			Vector<DigitalEntity> viewLinks = dtlBean.getViewLinks();
			int numOfVols = viewLinks.size();
			int count = 1;
			logger.info(pid + " Found " + numOfVols + " volumes.");
			for (DigitalEntity b : viewLinks)
			{
				b.setParentPid(ppid);
				logger.info("Part: " + (count++) + "/" + numOfVols);
				String p = b.getPid();
				String pp = b.getPid().substring(
						b.getPid().lastIndexOf(':') + 1);
				dtlBean.setPid(pp);

				updateEJournalPart(b);
				webclient.metadata(b);
			}
			logger.info(pid + " " + "and all volumes updated.\n");
		}
		catch (Exception e)
		{
			logger.error(pid + " " + e.getMessage());
		}
	}

	private void updateEJournalPart(DigitalEntity dtlBean)
	{
		String pid = dtlBean.getPid();
		String ppid = dtlBean.getPid().substring(
				dtlBean.getPid().lastIndexOf(':') + 1);
		dtlBean.setPid(ppid);
		logger.info(pid + " " + "Found eJournal article.");
		webclient.createObject(dtlBean, "application/zip", ObjectType.article);
		logger.info(pid + " " + "updated.\n");

	}

	@Override
	public void delete(String pid)
	{
		webclient.delete(pid);
	}

	@Override
	public void setNamespace(String namespace)
	{
		this.namespace = namespace;

	}
}
