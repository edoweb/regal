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
package de.nrw.hbz.dipp.sync.ingest;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.edoweb2.api.ObjectType;
import de.nrw.hbz.edoweb2.datatypes.ContentModel;
import de.nrw.hbz.edoweb2.sync.extern.DigitalEntity;
import de.nrw.hbz.edoweb2.sync.ingest.IngestInterface;
import de.nrw.hbz.edoweb2.sync.ingest.Webclient;

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

	final static String namespace = "dipp";
	String host = null;
	Webclient webclient = null;

	public DippIngester(String usr, String pwd, String host)
	{
		this.host = host;
		webclient = new Webclient(namespace, usr, pwd, host);
	}

	@Override
	public ContentModel createContentModel()
	{
		return null;
	}

	@Override
	public void ingest(DigitalEntity dtlBean)
	{
		logger.info("Start ingest: " + namespace + ":" + dtlBean.getPid());

		String partitionC = null;
		String pid = null;
		pid = dtlBean.getPid();
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
		try
		{
			logger.info(pid + " Found ejournal.");
			String ejournal = host + ":8080/edoweb2-api/ejournal/" + pid;
			webclient.createResource(ejournal, dtlBean);
			webclient.metadata(ejournal, dtlBean, ObjectType.ejournal);
			Vector<DigitalEntity> viewLinks = dtlBean.getViewLinks();
			int numOfVols = viewLinks.size();
			int count = 1;
			logger.info(pid + " Found " + numOfVols + " volumes.");
			for (DigitalEntity b : viewLinks)
			{
				b.setParentPid(pid);
				logger.info("Part: " + (count++) + "/" + numOfVols);
				updateEJournalPart(b);
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
		logger.info(pid + " " + "Found eJournal volume.");
		String resource = host + ":8080/edoweb2-api/ejournal/"
				+ dtlBean.getParentPid() + "/volume/" + pid;
		webclient.createObject(resource, dtlBean, "application/zip",
				ObjectType.ejournalVolume);
		logger.info(pid + " " + "updated.\n");

	}

	@Override
	public void delete(String pid)
	{
		webclient.delete(pid);
	}
}
