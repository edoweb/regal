package de.nrw.hbz.regal.sync;

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

import de.nrw.hbz.regal.PIDReporter;
import de.nrw.hbz.regal.sync.extern.DigitalEntity;
import de.nrw.hbz.regal.sync.extern.DigitalEntityBuilderInterface;
import de.nrw.hbz.regal.sync.ingest.DownloaderInterface;
import de.nrw.hbz.regal.sync.ingest.IngestInterface;

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
public class Syncer {

    final static Logger logger = LoggerFactory.getLogger(Syncer.class);

    private PIDReporter harvester;
    private IngestInterface ingester;
    private DownloaderInterface downloader;
    private DigitalEntityBuilderInterface builder;
    private String mode;
    private String user;
    private String password;
    private String dtl;
    private String cache;
    private String oai;
    private String set;
    private String timestamp;
    // private String fedoraBase;
    private String host;
    private String pidListFile;
    private Options options;
    private String namespace;

    /**
     * @param ingester
     *            the ingester is used to pass data to the archive
     * @param downloader
     *            the downloader is used to download data from a foreign system
     * @param builder
     *            the builder assembles DigitalEntities from the downloaded data
     *            and passes it to the ingester.
     */
    public Syncer(IngestInterface ingester, DownloaderInterface downloader,
	    DigitalEntityBuilderInterface builder) {
	this.ingester = ingester;
	this.downloader = downloader;
	this.builder = builder;
	options = new Options();

	options.addOption("?", "help", false, "Print usage information");
	options.addOption(
		"m",
		"mode",
		true,
		"Specify mode: \n "
			+ "INIT: All PIDs will be downloaded. All pids will be updated or created.\n"
			+ "SYNC: Modified or new PIDs will be downloaded and updated or created\n "
			+ "CONT: All PIDs that aren't already downloaded will be downloaded and created or updated\n"
			+ "UPDT: In accordance to the timestamp all modified PIDs will be reingested"
			+ "PIDL: Use this mode in combination with -list to provide a file with a newline separated pidlist"
			+ "DELE: Use this mode in combination with -list to provide a file with a newline separated pidlist");
	options.addOption("u", "user", true, "Specify username");
	options.addOption("p", "password", true, "Specify password");
	options.addOption("dtl", "dtl", true, "Specify digitool url");
	options.addOption("cache", "cache", true, "Specify local directory");
	options.addOption("oai", "oai", true, "Specify the OAI-PMH endpoint");
	Option setOption = new Option("set", "set", true,
		"Specify an OAI setSpec");
	setOption.setValueSeparator(',');
	setOption.setRequired(false);
	setOption.setOptionalArg(true);
	options.addOption(setOption);
	options.addOption("timestamp", "timestamp", true,
		"Specify a local file e.g. .oaitimestamp");
	options.addOption("fedoraBase", "fedoraBase", true,
		"The Fedora Baseurl");
	options.addOption("host", "host", true, "The Fedora Baseurl");
	options.addOption("namespace", "namespace", true,
		"The namespace to operate on.");
	options.addOption(
		"list",
		"list",
		true,
		"Path to a file with a newline separated pidlist. Only needed in combination with mode PIDL and DELE.");

    }

    /**
     * @param args
     *            usage: -oai,--oai <arg> Specify the OAI-PMH endpoint -m,--mode
     *            <arg> Specify mode: INIT: All PIDs will be downloaded. All
     *            pids will be updated or created. SYNC: Modified or new PIDs
     *            will be downloaded and updated or created CONT: All PIDs that
     *            aren't already downloaded will be downloaded and created or
     *            updated UPDT: In accordance to the timestamp all modified PIDs
     *            will be reingestedPIDL: Use this mode in combination with
     *            -list to provide a file with a newline separated pidlistMODL:
     *            Recreates only the Content modelDELE: Use this mode in
     *            combination with -list to provide a file with a newline
     *            separated pidlist -?,--help Print usage information
     *            -cache,--cache <arg> Specify local directory -dtl,--dtl <arg>
     *            Specify digitool url -fedoraBase,--fedoraBase <arg> The Fedora
     *            Baseurl -host,--host <arg> The Fedora Baseurl -list,--list
     *            <arg> Path to a file with a newline separated pidlist. Only
     *            needed in combination with mode PIDL and DELE.
     *            -namespace,--namespace <arg> The namespace to operate on.
     *            -p,--password <arg> Specify password -set,--set <arg> Specify
     *            an OAI setSpec -timestamp,--timestamp <arg> Specify a local
     *            file e.g. .oaitimestamp -u,--user <arg> Specify username
     */
    public void main(String[] args) {
	init(args);
	run();
    }

    /**
     * @param args
     *            main args
     */
    public void init(String[] args) {
	try {
	    DigitoolDownloadConfiguration config = new DigitoolDownloadConfiguration(
		    args, options, Syncer.class);

	    if (config.hasOption("help") | !config.hasOption("mode")
		    | !config.hasOption("user") | !config.hasOption("password")
		    | !config.hasOption("dtl") | !config.hasOption("cache")
		    | !config.hasOption("oai") | !config.hasOption("timestamp")
		    | !config.hasOption("fedoraBase")
		    | !config.hasOption("host")
		    | !config.hasOption("namespace"))

	    {
		showHelp(options);
		return;
	    }

	    mode = config.getOptionValue("mode");
	    user = config.getOptionValue("user");
	    password = config.getOptionValue("password");
	    dtl = config.getOptionValue("dtl");
	    cache = config.getOptionValue("cache");
	    oai = config.getOptionValue("oai");
	    set = config.getOptionValue("set");
	    timestamp = config.getOptionValue("timestamp");
	    // fedoraBase = config.getOptionValue("fedoraBase");
	    host = config.getOptionValue("host");
	    namespace = config.getOptionValue("namespace");
	    pidListFile = null;
	    if (config.hasOption("list")) {
		pidListFile = config.getOptionValue("list");
	    }

	    harvester = new de.nrw.hbz.regal.PIDReporter(oai, timestamp);
	    downloader.init(dtl, cache);
	    ingester.init(host, user, password, namespace);
	} catch (ParseException e) {

	    e.printStackTrace();
	}
    }

    private void showHelp(Options options) {
	HelpFormatter help = new HelpFormatter();
	help.printHelp(" ", options);
    }

    private void run() {
	if (mode.compareTo("INIT") == 0) {
	    init(set);
	} else if (mode.compareTo("SYNC") == 0) {
	    sync(set);
	} else if (mode.compareTo("CONT") == 0) {
	    cont(set);
	} else if (mode.compareTo("UPDT") == 0) {
	    updt(set);
	} else if (mode.compareTo("PIDL") == 0) {

	    pidl(pidListFile);

	} else if (mode.compareTo("DELE") == 0) {

	    dele(pidListFile);

	}

    }

    /*
     * "INIT: All PIDs will be downloaded. All pids will be updated or created.\n"
     */
    void init(String sets) {
	boolean harvestFromScratch = true;
	boolean forceDownload = true;

	Vector<String> pids = harvester.harvest(sets, harvestFromScratch);
	logger.info("Verarbeite " + pids.size() + " Dateneinheiten.");

	int size = pids.size();
	for (int i = 0; i < size; i++) {
	    try {
		logger.info((i + 1) + " / " + size);
		String pid = pids.get(i);
		String baseDir = downloader.download(pid, forceDownload);
		logger.info("\tBuild Bean \t" + pid);

		if (!downloader.hasUpdated()) {
		    logger.info("New Files Available: Start Ingest!");
		    DigitalEntity dtlBean = builder.build(baseDir, pids.get(i));

		    ingester.ingest(dtlBean);
		    dtlBean = null;
		} else if (downloader.hasUpdated()) {
		    logger.info("Update Files!");
		    DigitalEntity dtlBean = builder.build(baseDir, pids.get(i));
		    ingester.ingest(dtlBean);
		    dtlBean = null;
		}

	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

    }

    /*
     * +
     * "SYNC: Modified or new PIDs will be downloaded and updated or created\n "
     */
    void sync(String sets) {
	boolean harvestFromScratch = false;
	boolean forceDownload = true;

	Vector<String> pids = harvester.harvest(sets, harvestFromScratch);
	logger.info("Verarbeite " + pids.size() + " Dateneinheiten.");

	int size = pids.size();
	for (int i = 0; i < size; i++) {
	    try {
		logger.info((i + 1) + " / " + size);
		String pid = pids.get(i);
		String baseDir = downloader.download(pid, forceDownload);
		logger.info("\tBuild Bean \t" + pid);

		if (!downloader.hasUpdated()) {
		    logger.info("New Files Available: Start Ingest!");
		    DigitalEntity dtlBean = builder.build(baseDir, pids.get(i));

		    ingester.ingest(dtlBean);
		    dtlBean = null;
		} else if (downloader.hasUpdated()) {
		    logger.info("Update Files!");
		    DigitalEntity dtlBean = builder.build(baseDir, pids.get(i));
		    ingester.update(dtlBean);
		    dtlBean = null;
		}

	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

    }

    /*
     * +
     * "CONT: All PIDs that aren't already downloaded will be downloaded and created or updated\n"
     */
    void cont(String sets) {
	boolean harvestFromScratch = true;
	boolean forceDownload = false;
	Vector<String> pids = harvester.harvest(sets, harvestFromScratch);
	logger.info("Verarbeite " + pids.size() + " Dateneinheiten.");
	int size = pids.size();
	for (int i = 0; i < size; i++) {
	    try {
		logger.info((i + 1) + " / " + size);
		String pid = pids.get(i);
		String baseDir = downloader.download(pid, forceDownload);
		logger.info("\tBuild Bean \t" + pid);

		if (!downloader.hasUpdated() && downloader.hasDownloaded()) {
		    logger.info("New Files Available: Start Ingest!");
		    DigitalEntity dtlBean = builder.build(baseDir, pids.get(i));

		    ingester.ingest(dtlBean);
		    dtlBean = null;
		}

	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

    }

    /*
     * +
     * "UPDT: in accordance to the timestamp all modified PIDs will be reingested"
     */
    void updt(String sets) {
	boolean harvestFromScratch = false;
	boolean forceDownload = false;

	Vector<String> pids = harvester.harvest(sets, harvestFromScratch);
	logger.info("Verarbeite " + pids.size() + " Dateneinheiten.");

	int size = pids.size();
	for (int i = 0; i < size; i++) {
	    try {
		logger.info((i + 1) + " / " + size);
		String pid = pids.get(i);
		String baseDir = downloader.download(pid, forceDownload);
		logger.info("\tBuild Bean \t" + pid);

		logger.info("New Files Available: Start Ingest!");
		DigitalEntity dtlBean = builder.build(baseDir, pid);
		ingester.delete(pid);
		ingester.ingest(dtlBean);
		dtlBean = null;

	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    void pidl(String pidListFile) {
	Vector<String> pids;
	try {
	    pids = readPidlist(pidListFile);
	    int size = pids.size();
	    for (int i = 0; i < size; i++) {
		try {
		    logger.info((i + 1) + " / " + size);
		    String pid = pids.get(i);

		    String baseDir = downloader.download(pid, false);

		    if (!downloader.hasUpdated()) {

			DigitalEntity dtlBean = builder.build(baseDir,
				pids.get(i));

			ingester.ingest(dtlBean);
			dtlBean = null;
			logger.info((i + 1) + "/" + size + " " + pid
				+ " has been processed!\n");
		    } else if (downloader.hasUpdated()) {

			DigitalEntity dtlBean = builder.build(baseDir,
				pids.get(i));
			ingester.ingest(dtlBean);
			dtlBean = null;
			logger.info((i + 1) + "/" + size + " " + pid
				+ " has been updated!\n");
		    }

		} catch (IOException e) {
		    e.printStackTrace();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }

	} catch (FileNotFoundException e1) {
	    e1.printStackTrace();
	}
    }

    void dele(String pidListFile) {
	Vector<String> pids;
	try {
	    pids = readPidlist(pidListFile);
	    int size = pids.size();
	    for (int i = 0; i < size; i++) {
		logger.info((i + 1) + " / " + size);
		String pid = pids.get(i);
		ingester.delete(pid);
		logger.info((i + 1) + "/" + size + " " + pid + " deleted!\n");
	    }
	} catch (FileNotFoundException e1) {
	    e1.printStackTrace();
	}
    }

    private Vector<String> readPidlist(String pidListFile)
	    throws FileNotFoundException {
	if (pidListFile == null)
	    throw new FileNotFoundException(
		    "Please provide a pidListFile via -list <filename>\n");

	File file = new File(pidListFile);
	Vector<String> result = new Vector<String>();
	BufferedReader reader = null;
	String str = null;
	try {
	    reader = new BufferedReader(new FileReader(file));
	    while ((str = reader.readLine()) != null) {
		result.add(str);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    try {
		reader.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	return result;
    }

}
