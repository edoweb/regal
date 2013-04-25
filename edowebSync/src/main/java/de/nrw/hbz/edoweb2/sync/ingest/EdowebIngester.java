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
package de.nrw.hbz.edoweb2.sync.ingest;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.edoweb2.api.ObjectType;
import de.nrw.hbz.edoweb2.datatypes.ContentModel;
import de.nrw.hbz.edoweb2.sync.extern.DigitalEntity;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class EdowebIngester implements IngestInterface
{
	final static Logger logger = LoggerFactory.getLogger(EdowebIngester.class);

	final static String namespace = "edoweb";

	Webclient webclient = null;
	String host = null;

	/**
	 * @param usr
	 *            a valid user
	 * @param pwd
	 *            the users password
	 * @param host
	 *            the host of the webapi
	 */
	public EdowebIngester(String usr, String pwd, String host)
	{
		this.host = host;
		webclient = new Webclient(namespace, usr, pwd, host);
	}

	@Override
	public ContentModel createContentModel()
	{
		// TODO implement
		return null;
	}

	@Override
	public void ingest(DigitalEntity dtlBean)
	{
		logger.info(dtlBean.getPid() + " " + "Start ingest: " + namespace + ":"
				+ dtlBean.getPid());

		String partitionC = null;
		String pid = null;
		pid = dtlBean.getPid();
		try
		{
			ControlBean control = new ControlBean(dtlBean);
			partitionC = control.getPartitionC().firstElement();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{

			if (partitionC.compareTo("EJO01") == 0)
			{
				if (dtlBean.isParent())
				{
					logger.info(pid + ": start ingesting eJournal");
					ingestEJournalComplete(dtlBean);
					logger.info(pid + ": end ingesting eJournal");
				}
				else
				{
					logger.info(pid + ": start ingesting eJournal volume");
					updateEJournalPart(dtlBean);
					logger.info(pid + ": end ingesting eJournal volume");
				}
			}
			else if (partitionC.compareTo("WPD01") == 0)
			{

				logger.info(pid + ": start updating monograph (wpd01)");
				updateMonographs(dtlBean);
				logger.info(pid + ": end updating monograph (wpd01)");
			}
			else if (partitionC.compareTo("WPD02") == 0)
			{

				logger.info(pid + ": start updating monograph (wpd02)");
				updateMonographs(dtlBean);
				logger.info(pid + ": end updating monograph (wpd02)");
			}
			else if (partitionC.compareTo("WSC01") == 0)
			{
				if (dtlBean.isParent())
				{
					logger.info(pid + ": start ingesting webpage (wsc01)");
					ingestWebpageComplete(dtlBean);
					logger.info(pid + ": end ingesting webpage (wsc01)");
				}
				else
				{
					logger.info(pid
							+ ": start ingesting webpage version (wsc01)");
					updateWebpagePart(dtlBean);
					logger.info(pid + ": end ingesting webpage version (wsc01)");
				}
			}
			else if (partitionC.compareTo("WSI01") == 0)
			{
				logger.info(pid + ": start updating webpage (wsi01)");
				updateSingleWebpage(dtlBean);
				logger.info(pid + ": end updating webpage (wsi01)");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.info(dtlBean.getPid() + " " + e.getMessage());
		}

		logger.info(dtlBean.getPid() + " " + "Thanx and goodbye!\n");
	}

	@Override
	public void update(DigitalEntity dtlBean)
	{
		logger.info(dtlBean.getPid() + " " + "Start update: " + namespace + ":"
				+ dtlBean.getPid());

		String partitionC = null;
		String pid = null;
		pid = dtlBean.getPid();
		try
		{
			ControlBean control = new ControlBean(dtlBean);
			partitionC = control.getPartitionC().firstElement();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{

			if (partitionC.compareTo("EJO01") == 0)
			{
				if (dtlBean.isParent())
				{
					logger.info(pid + ": start updating eJournal");
					updateEJournalParent(dtlBean);
					logger.info(pid + ": end updating eJournal");
				}
				else
				{
					logger.info(pid + ": start updating eJournal volume");
					updateEJournalPart(dtlBean);
					logger.info(pid + ": end updating eJournal volume");
				}
			}
			else if (partitionC.compareTo("WPD01") == 0)
			{
				logger.info(pid + ": start updating monograph (wpd01)");
				updateMonographs(dtlBean);
				logger.info(pid + ": end updating monograph (wpd01)");
			}
			else if (partitionC.compareTo("WPD02") == 0)
			{

				logger.info(pid + ": start updating monograph (wpd02)");
				updateMonographs(dtlBean);
				logger.info(pid + ": end updating monograph (wpd02)");
			}
			else if (partitionC.compareTo("WSC01") == 0)
			{
				if (dtlBean.isParent())
				{
					logger.info(pid + ": start updating webpage (wsc01)");
					updateWebpageParent(dtlBean);
					logger.info(pid + ": end updating webpage (wsc01)");
				}
				else
				{
					logger.info(pid
							+ ": start updating webpage version (wsc01)");
					updateWebpagePart(dtlBean);
					logger.info(pid + ": end updating webpage version (wsc01)");
				}
			}
			else if (partitionC.compareTo("WSI01") == 0)
			{
				logger.info(pid + ": start updating webpage (wsi01)");
				updateSingleWebpage(dtlBean);
				logger.info(pid + ": end updating webpage (wsi01)");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.info(dtlBean.getPid() + " " + e.getMessage());
		}

	}

	@Override
	public void delete(String p)
	{
		webclient.delete(p);

	}

	private void updateEJournalPart(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		logger.info(pid + " " + "Found eJournal volume.");
		String resource = host + ":8080/edoweb2-api/ejournal/" + namespace
				+ ":" + dtlBean.getParentPid() + "/volume/" + pid;
		webclient.createObject(dtlBean, "application/pdf",
				ObjectType.ejournalVolume);
		logger.info(pid + " " + "updated.\n");
	}

	private void updateWebpagePart(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		logger.info(pid + " Found webpage version.");
		String resource = host + ":8080/edoweb2-api/webpage/" + namespace + ":"
				+ dtlBean.getParentPid() + "/version/" + pid;
		webclient.createObject(dtlBean, "application/zip",
				ObjectType.webpageVersion);
		logger.info(pid + " " + "updated.\n");
	}

	private void updateMonographs(DigitalEntity dtlBean)
	{

		String pid = namespace + ":" + dtlBean.getPid();
		logger.info(pid + " Found monograph.");
		String resource = host + ":8080/edoweb2-api/monograph/" + pid;
		webclient
				.createObject(dtlBean, "application/pdf", ObjectType.monograph);
		webclient.metadata(dtlBean, ObjectType.monograph);
		logger.info(pid + " " + "updated.\n");
	}

	private void updateEJournalParent(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{
			logger.info(pid + " Found ejournal.");

			webclient.createResource(ObjectType.ejournal, dtlBean);
			webclient.metadata(dtlBean, ObjectType.ejournal);
			Vector<DigitalEntity> viewMainLinks = dtlBean.getViewMainLinks();
			int numOfVols = viewMainLinks.size();
			logger.info(pid + " " + "Found " + numOfVols + " volumes.");
			logger.info(pid + " " + "Will not update volumes.");
			logger.info(pid + " " + "updated.\n");
		}
		catch (Exception e)
		{
			logger.error(pid + " " + e.getMessage());
		}

	}

	private void updateWebpageParent(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{
			logger.info(pid + " Found webpage.");
			String webpage = host + ":8080/edoweb2-api/webpage/" + pid;
			webclient.createResource(ObjectType.webpage, dtlBean);
			webclient.metadata(dtlBean, ObjectType.webpage);
			Vector<DigitalEntity> viewLinks = dtlBean.getViewLinks();
			int numOfVersions = viewLinks.size();
			logger.info(pid + " " + "Found " + numOfVersions + " versions.");
			logger.info(pid + " " + "Will not update versions.");
			logger.info(pid + " " + "updated.\n");
		}
		catch (Exception e)
		{
			logger.info(pid + " " + e.getMessage());
		}

	}

	private void updateSingleWebpage(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{
			logger.info(pid + " Found webpage.");
			webclient.createResource(ObjectType.webpage, dtlBean);
			webclient.metadata(dtlBean, ObjectType.webpage);
			for (DigitalEntity b : dtlBean.getArchiveLinks())
			{
				String versionPid = namespace + ":" + b.getPid();
				if (b.getStreamMime().compareTo("application/zip") == 0)
				{
					webclient.createObject(b, "application/zip",
							ObjectType.webpageVersion);
					break;
				}
			}
			logger.info(pid + " " + "updated.\n");
		}
		catch (Exception e)
		{
			logger.error(pid + " " + e.getMessage());
		}

	}

	private void ingestEJournalComplete(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{
			logger.info(pid + " Found ejournal.");

			webclient.createResource(ObjectType.ejournal, dtlBean);
			webclient.metadata(dtlBean, ObjectType.ejournal);
			Vector<DigitalEntity> viewMainLinks = dtlBean.getViewMainLinks();
			int numOfVols = viewMainLinks.size();
			int count = 1;
			logger.info(pid + " Found " + numOfVols + " volumes.");
			for (DigitalEntity b : viewMainLinks)
			{
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

	private void ingestWebpageComplete(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{
			logger.info(pid + " Found webpage.");

			webclient.createResource(ObjectType.webpage, dtlBean);
			webclient.metadata(dtlBean, ObjectType.webpage);
			Vector<DigitalEntity> viewLinks = dtlBean.getViewLinks();
			int numOfVersions = viewLinks.size();
			logger.info(pid + " Found " + numOfVersions + " versions.");
			int count = 1;
			for (DigitalEntity b : viewLinks)
			{
				logger.info("Part: " + (count++) + "/" + numOfVersions);
				updateWebpagePart(b);
			}
			logger.info(pid + " " + "and all versions updated.\n");
		}
		catch (Exception e)
		{
			logger.info(pid + " " + e.getMessage());
		}

	}

}
