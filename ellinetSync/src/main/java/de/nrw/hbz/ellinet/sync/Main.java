package de.nrw.hbz.ellinet.sync;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.edoweb2.digitool.downloader.DigitoolDownloader;
import de.nrw.hbz.edoweb2.digitool.pidreporter.OaiPidGrabber;
import de.nrw.hbz.edoweb2.sync.DigitoolDownloadConfiguration;
import de.nrw.hbz.edoweb2.sync.extern.DigitalEntity;
import de.nrw.hbz.edoweb2.sync.extern.DigitalEntityBuilder;
import de.nrw.hbz.edoweb2.sync.ingest.FedoraIngester;
import de.nrw.hbz.edoweb2.sync.ingest.IngestInterface;

/**
 * Class Main
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 03.06.2011
 * 
 */
public class Main
{

	final static Logger logger = LoggerFactory.getLogger(Main.class);

	IngestInterface ingester = null;
	OaiPidGrabber harvester = null;
	DigitoolDownloader downloader = null;

	public Main()
	{

	}

	/*
	 * DigitoolIngester
	 * 
	 * --harvestFromScratch --force-download --force-update --force-reingest
	 * --generate-html-browsing
	 */

	public static void main(String[] args)
	{
		Options options = new Options();

		options.addOption("?", "help", false, "Print usage information");
		options.addOption(
				"m",
				"mode",
				true,
				"Specify mode: \n "
						+ "INIT: All PIDs will be downloaded. All pids will be updated or created.\n"
						+ "SYNC: Modified or new PIDs will be downloaded and updated or created\n "
						+ "CONT: All PIDs that aren't already downloaded will be downloaded and created or updated\n"
						+ "UPDT: All PIDs in cache will be reingested"
						+ "PIDL: Use this mode in combination with -list to provide a file with a newline separated pidlist"
						+ "MODL: Recreates only the Content model"
						+ "DELE: Use this mode in combination with -list to provide a file with a newline separated pidlist");
		options.addOption("u", "user", true, "Specify username");
		options.addOption("p", "password", true, "Specify password");
		options.addOption("dtl", "dtl", true, "Specify digitool url");
		options.addOption("cache", "cache", true, "Specify local directory");
		options.addOption("oai", "oai", true, "Specify the OAI-PMH endpoint");
		Option setOption = new Option("set", "set", true,
				"Specify an OAI setSpec");
		setOption.setValueSeparator(',');

		options.addOption(setOption);
		options.addOption("timestamp", "timestamp", true,
				"Specify a local file e.g. .oaitimestamp");
		options.addOption("fedoraBase", "fedoraBase", true,
				"The Fedora Baseurl");
		options.addOption("host", "host", true, "The Fedora Baseurl");
		options.addOption(
				"list",
				"list",
				true,
				"Path to a file with a newline separated pidlist. Only needed in combination with mode PIDL and DELE.");

		try
		{
			DigitoolDownloadConfiguration config = new DigitoolDownloadConfiguration(
					args, options, Main.class);

			if (config.hasOption("help") | !config.hasOption("mode")
					| !config.hasOption("user") | !config.hasOption("password")
					| !config.hasOption("dtl") | !config.hasOption("cache")
					| !config.hasOption("oai") | !config.hasOption("set")
					| !config.hasOption("timestamp")
					| !config.hasOption("fedoraBase")
					| !config.hasOption("host"))

			{
				showHelp(options);
				return;
			}

			String mode = config.getOptionValue("mode");
			String user = config.getOptionValue("user");
			String password = config.getOptionValue("password");
			String dtl = config.getOptionValue("dtl");
			String cache = config.getOptionValue("cache");
			String oai = config.getOptionValue("oai");
			String set = config.getOptionValue("set");
			String timestamp = config.getOptionValue("timestamp");
			String fedoraBase = config.getOptionValue("fedoraBase");
			String host = config.getOptionValue("host");
			String pidListFile = null;
			if (config.hasOption("list"))
			{
				pidListFile = config.getOptionValue("list");
			}
			Main main = new Main();
			main.run(mode, user, password, dtl, cache, oai, set, timestamp,
					fedoraBase, host, pidListFile);

		}
		catch (ParseException e)
		{

			e.printStackTrace();
		}

	}

	private static void showHelp(Options options)
	{
		HelpFormatter help = new HelpFormatter();
		help.printHelp(" ", options);
	}

	public void run(String mode, String user, String password, String dtl,
			String cache, String oai, String set, String timestamp,
			String fedoraBase, String host, String pidListFile)
	{
		// boolean generateHTMLBrowsing = true;
		String server = dtl;
		String downloadLocation = cache;
		String sets = set;
		String oaiServer = oai;
		String timestampFile = timestamp;

		harvester = new OaiPidGrabber(oaiServer, timestampFile);
		downloader = new DigitoolDownloader(server, downloadLocation);

		ingester = new FedoraIngester(user, password, host);

		if (mode.compareTo("INIT") == 0)
		{
			init(sets);
		}
		else if (mode.compareTo("SYNC") == 0)
		{
			sync(sets);
		}
		else if (mode.compareTo("CONT") == 0)
		{
			cont(sets);
		}
		else if (mode.compareTo("UPDT") == 0)
		{
			updt(sets);
		}
		else if (mode.compareTo("PIDL") == 0)
		{

			pidl(pidListFile);

		}
		else if (mode.compareTo("DELE") == 0)
		{

			dele(pidListFile);

		}
		else if (mode.compareTo("MODL") == 0)
		{

			// IngestInterface ingester = new FedoraIngester("ellinet",
			// fedoraBase, user, password, axisHome);
			ingester.createContentModel();

		}

	}

	private Vector<String> readPidlist(String pidListFile)
			throws FileNotFoundException
	{
		if (pidListFile == null)
			throw new FileNotFoundException(
					"Please provide a pidListFile via -list <filename>\n");

		File file = new File(pidListFile);
		Vector<String> result = new Vector<String>();
		BufferedReader reader = null;
		String str = null;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			while ((str = reader.readLine()) != null)
			{
				result.add(str);
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
				reader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}

	/*
	 * "INIT: All PIDs will be downloaded. All pids will be updated or created.\n"
	 */
	void init(String sets)
	{
		boolean harvestFromScratch = true;
		boolean forceDownload = true;

		Vector<String> pids = harvester.harvest(sets, harvestFromScratch);
		logger.info("Verarbeite " + pids.size() + " Dateneinheiten.");
		DigitalEntityBuilder builder = new DigitalEntityBuilder();

		int size = pids.size();
		for (int i = 0; i < size; i++)
		{
			try
			{
				logger.info((i + 1) + " / " + size);
				String pid = pids.get(i);
				String baseDir = downloader.download(pid, forceDownload);
				logger.info("\tBuild Bean \t" + pid);

				if (!downloader.hasUpdated())
				{
					logger.info("New Files Available: Start Ingest!");
					DigitalEntity dtlBean = builder.buildComplexBean(baseDir,
							pids.get(i));

					ingester.ingest(dtlBean);
					dtlBean = null;
				}
				else if (downloader.hasUpdated())
				{
					logger.info("Update Files!");
					DigitalEntity dtlBean = builder.buildComplexBean(baseDir,
							pids.get(i));
					ingester.ingest(dtlBean);
					dtlBean = null;
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/*
	 * +
	 * "SYNC: Modified or new PIDs will be downloaded and updated or created\n "
	 */
	void sync(String sets)
	{
		boolean harvestFromScratch = false;
		boolean forceDownload = true;

		Vector<String> pids = harvester.harvest(sets, harvestFromScratch);
		logger.info("Verarbeite " + pids.size() + " Dateneinheiten.");

		DigitalEntityBuilder builder = new DigitalEntityBuilder();

		int size = pids.size();
		for (int i = 0; i < size; i++)
		{
			try
			{
				logger.info((i + 1) + " / " + size);
				String pid = pids.get(i);
				String baseDir = downloader.download(pid, forceDownload);
				logger.info("\tBuild Bean \t" + pid);

				if (!downloader.hasUpdated())
				{
					logger.info("New Files Available: Start Ingest!");
					DigitalEntity dtlBean = builder.buildComplexBean(baseDir,
							pids.get(i));

					ingester.ingest(dtlBean);
					dtlBean = null;
				}
				else if (downloader.hasUpdated())
				{
					logger.info("Update Files!");
					DigitalEntity dtlBean = builder.buildComplexBean(baseDir,
							pids.get(i));
					ingester.update(dtlBean);
					dtlBean = null;
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/*
	 * +
	 * "CONT: All PIDs that aren't already downloaded will be downloaded and created or updated\n"
	 */
	void cont(String sets)
	{
		boolean harvestFromScratch = true;
		boolean forceDownload = false;

		Vector<String> pids = harvester.harvest(sets, harvestFromScratch);
		logger.info("Verarbeite " + pids.size() + " Dateneinheiten.");
		DigitalEntityBuilder builder = new DigitalEntityBuilder();
		// IngestInterface ingester = new FedoraIngester("ellinet",
		// fedoraBase, user, password, axisHome);

		int size = pids.size();
		for (int i = 0; i < size; i++)
		{
			try
			{
				logger.info((i + 1) + " / " + size);
				String pid = pids.get(i);
				String baseDir = downloader.download(pid, forceDownload);
				logger.info("\tBuild Bean \t" + pid);

				if (!downloader.hasUpdated() && downloader.hasDownloaded())
				{
					logger.info("New Files Available: Start Ingest!");
					DigitalEntity dtlBean = builder.buildComplexBean(baseDir,
							pids.get(i));

					ingester.ingest(dtlBean);
					dtlBean = null;
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/*
	 * + "UPDT: All PIDs in cache will be reingested"
	 * 
	 * TODO: The implementation doesn't fit to the specification
	 */
	void updt(String sets)
	{
		boolean harvestFromScratch = true;
		boolean forceDownload = false;

		Vector<String> pids = harvester.harvest(sets, harvestFromScratch);
		logger.info("Verarbeite " + pids.size() + " Dateneinheiten.");

		DigitalEntityBuilder builder = new DigitalEntityBuilder();
		// IngestInterface ingester = new FedoraIngester("ellinet",
		// fedoraBase, user, password, axisHome);
		int size = pids.size();
		for (int i = 0; i < size; i++)
		{
			try
			{
				logger.info((i + 1) + " / " + size);
				String pid = pids.get(i);
				String baseDir = downloader.download(pid, forceDownload);
				logger.info("\tBuild Bean \t" + pid);

				if (!downloader.hasUpdated())
				{
					logger.info("New Files Available: Start Ingest!");
					DigitalEntity dtlBean = builder.buildComplexBean(baseDir,
							pids.get(i));

					ingester.ingest(dtlBean);
					dtlBean = null;
				}
				else if (downloader.hasUpdated())
				{
					logger.info("Update Files!");
					DigitalEntity dtlBean = builder.buildComplexBean(baseDir,
							pids.get(i));
					ingester.ingest(dtlBean);
					dtlBean = null;
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	void pidl(String pidListFile)
	{
		Vector<String> pids;
		try
		{
			pids = readPidlist(pidListFile);
			DigitalEntityBuilder builder = new DigitalEntityBuilder();
			// IngestInterface ingester = new FedoraIngester("ellinet",
			// fedoraBase, user, password, axisHome);
			int size = pids.size();
			for (int i = 0; i < size; i++)
			{
				try
				{
					logger.info((i + 1) + " / " + size);
					String pid = pids.get(i);

					String baseDir = downloader.download(pid, false);

					if (!downloader.hasUpdated())
					{

						DigitalEntity dtlBean = builder.buildComplexBean(
								baseDir, pids.get(i));

						ingester.ingest(dtlBean);
						dtlBean = null;
						logger.info((i + 1) + "/" + size + " " + pid
								+ " has been processed!\n");
					}
					else if (downloader.hasUpdated())
					{

						DigitalEntity dtlBean = builder.buildComplexBean(
								baseDir, pids.get(i));
						ingester.ingest(dtlBean);
						dtlBean = null;
						logger.info((i + 1) + "/" + size + " " + pid
								+ " has been updated!\n");
					}

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		}
		catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	void dele(String pidListFile)
	{
		Vector<String> pids;
		try
		{
			pids = readPidlist(pidListFile);
			// DigitalEntityBeanBuilder builder = new
			// DigitalEntityBeanBuilder();
			// IngestInterface ingester = new FedoraIngester("ellinet",
			// fedoraBase, user, password, axisHome);
			int size = pids.size();
			for (int i = 0; i < size; i++)
			{
				logger.info((i + 1) + " / " + size);
				String pid = pids.get(i);

				ingester.delete(pid);
				logger.info((i + 1) + "/" + size + " " + pid + " deleted!\n");

			}

		}
		catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
