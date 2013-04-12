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
package de.nrw.hbz.edoweb2.digitool.pidreporter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kb.oai.OAIException;
import se.kb.oai.pmh.IdentifiersList;
import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.ResumptionToken;

/**
 * Class OaiPidGrabber
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
class OaiPidGrabber
{
	String server = null;
	String timestampfile = null;

	final Logger logger = LoggerFactory.getLogger(OaiPidGrabber.class);

	OaiPidGrabber(String server, String timestampFile)
	{
		this.server = server;
		this.timestampfile = timestampFile;
	}

	Vector<String> harvest(String set, boolean harvestFromScratch)
	{

		String[] sets = null;
		if (set.compareTo("null") != 0)
		{
			sets = set.split(",");
		}
		return harvest(sets, harvestFromScratch);
	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param setSpec
	 * @return
	 */
	Vector<String> harvest(String[] sets, boolean harvestFromScratch)
	{

		logger.info("Start harvesting " + server + " !");

		OaiPmhServer oaiserver = new OaiPmhServer(server);
		Vector<String> result = new Vector<String>();
		try
		{
			// File baseDir = new File(harvestLocation + File.separator +
			// setSpec);
			// if (!baseDir.exists())
			// {
			// baseDir.mkdir();
			// }
			String fromStr = null;
			BufferedReader reader = null;
			try
			{
				if (!harvestFromScratch)
				{
					File oaifile = new File(this.timestampfile);
					if (!oaifile.exists())
					{
						logger.warn("Timestamp file " + this.timestampfile
								+ " is not available! First harvest!?");
						logger.warn("I continue with harvest from scratch!");
					}
					else
					{
						reader = new BufferedReader(new FileReader(oaifile));
						String input = reader.readLine();
						fromStr = input;
					}
					logger.info("Harvest all Records from " + fromStr + " !");
				}
				else
				{
					logger.info("Harvest all Records! No from= Parameter set!");
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if (reader != null)
						reader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			String until = null;
			if (sets == null)
			{
				logger.info("Set spec is null  !");
				IdentifiersList reclist = oaiserver.listIdentifiers("oai_dc",
						fromStr, until, null);
				String dateString = reclist.getResponseDate();
				logger.info("Harvest Date " + dateString + " !");
				BufferedWriter writer = null;
				try
				{
					writer = new BufferedWriter(new FileWriter(new File(
							this.timestampfile)));
					writer.write(dateString);
					writer.flush();

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally
				{

					try
					{
						if (writer != null)
							writer.close();
					}
					catch (IOException e)
					{

						e.printStackTrace();
					}
				}

				do
				{
					result.addAll(collectPids(reclist));
					ResumptionToken token = reclist.getResumptionToken();
					if (token == null)
					{
						break;
					}
					reclist = oaiserver.listIdentifiers(token);

				}
				while (true);
			}
			else
			{
				for (int i = 0; i < sets.length; i++)
				{
					logger.info("Set spec is " + sets[i].trim() + " !");
					IdentifiersList reclist = oaiserver.listIdentifiers(
							"oai_dc", fromStr, until, sets[i].trim());
					if (i == 0)
					{
						String dateString = reclist.getResponseDate();
						logger.info("Harvest Date " + dateString + " !");
						BufferedWriter writer = null;
						try
						{
							writer = new BufferedWriter(new FileWriter(
									new File(this.timestampfile)));
							writer.write(dateString);
							writer.flush();

						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						finally
						{

							try
							{
								if (writer != null)
									writer.close();
							}
							catch (IOException e)
							{

								e.printStackTrace();
							}
						}
					}
					do
					{
						result.addAll(collectPids(reclist));
						ResumptionToken token = reclist.getResumptionToken();
						if (token == null)
						{
							break;
						}
						reclist = oaiserver.listIdentifiers(token);

					}
					while (true);
				}
			}

		}
		catch (OAIException e)
		{
			logger.warn("Harvesting ended in an empty response! Old timestape is still correct!");
		}
		logger.info("Found " + result.size() + " pids !");
		return result;
	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param reclist
	 * @return
	 */
	private Vector<String> collectPids(IdentifiersList reclist)
	{
		String stream = reclist.getResponse().asXML();

		Vector<String> result = new Vector<String>();
		int start = 0;
		// int i = 0;
		Pattern pattern = Pattern.compile("<identifier>oai:[^:]*:([0-9]*)");
		Matcher matcher = pattern.matcher(stream);
		while (matcher.find(start))
		{
			String pid = stream.substring(matcher.start(1), matcher.end(1));

			result.add(pid);
			start = matcher.end();
		}
		return result;
	}

}
