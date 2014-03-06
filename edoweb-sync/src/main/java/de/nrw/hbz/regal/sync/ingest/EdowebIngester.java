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

import de.nrw.hbz.regal.api.helper.ObjectType;
import de.nrw.hbz.regal.sync.extern.DigitalEntity;
import de.nrw.hbz.regal.sync.extern.DigitalEntityRelation;
import de.nrw.hbz.regal.sync.extern.RelatedDigitalEntity;
import de.nrw.hbz.regal.sync.extern.StreamType;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class EdowebIngester implements IngestInterface {
    final static Logger logger = LoggerFactory.getLogger(EdowebIngester.class);

    private String namespace = "edoweb";

    Webclient webclient = null;
    String host = null;

    @Override
    public void init(String host, String user, String password, String ns) {
	this.namespace = ns;
	this.host = host;
	webclient = new Webclient(namespace, user, password, host);
	webclient.initContentModels();
    }

    @Override
    public void ingest(DigitalEntity dtlBean) {
	logger.info(dtlBean.getPid() + " " + "Start ingest: " + namespace + ":"
		+ dtlBean.getPid());

	String partitionC = null;
	String pid = null;
	pid = dtlBean.getPid();

	partitionC = dtlBean.getType();

	try {

	    if (partitionC.compareTo("EJO01") == 0) {
		if (dtlBean.isParent()) {
		    logger.info(pid + ": start ingesting eJournal");
		    updateJournal(dtlBean);
		    logger.info(pid + ": end ingesting eJournal");
		} else {
		    logger.info(pid + ": start ingesting eJournal issue");
		    updatePart(dtlBean);
		    logger.info(pid + ": end ingesting eJournal issue");
		}
	    } else if (partitionC.compareTo("WPD01") == 0) {

		logger.info(pid + ": start updating monograph (wpd01)");
		updateMonographs(dtlBean);
		logger.info(pid + ": end updating monograph (wpd01)");
	    } else if (partitionC.compareTo("WPD02") == 0) {

		logger.info(pid + ": start updating monograph (wpd02)");
		updateMonographs(dtlBean);
		logger.info(pid + ": end updating monograph (wpd02)");
	    } else if (partitionC.compareTo("WSC01") == 0) {
		if (dtlBean.isParent()) {
		    logger.info(pid + ": start ingesting webpage (wsc01)");
		    updateWebpage(dtlBean);
		    logger.info(pid + ": end ingesting webpage (wsc01)");
		} else {
		    logger.info(pid
			    + ": start ingesting webpage version (wsc01)");
		    updateVersion(dtlBean);
		    logger.info(pid + ": end ingesting webpage version (wsc01)");
		}
	    } else if (partitionC.compareTo("WSI01") == 0) {
		logger.info(pid + ": start updating webpage (wsi01)");
		updateWebpage(dtlBean);
		logger.info(pid + ": end updating webpage (wsi01)");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.info(dtlBean.getPid() + " " + e.getMessage());
	}

	logger.info(dtlBean.getPid() + " " + "Thanx and goodbye!\n");
    }

    @Override
    public void update(DigitalEntity dtlBean) {
	logger.info(dtlBean.getPid() + " " + "Start update: " + namespace + ":"
		+ dtlBean.getPid());

	String partitionC = null;
	String pid = null;
	pid = dtlBean.getPid();

	partitionC = dtlBean.getType();

	try {

	    if (partitionC.compareTo("EJO01") == 0) {
		if (dtlBean.isParent()) {
		    logger.info(pid + ": start updating eJournal");
		    updateJournalParent(dtlBean);
		    logger.info(pid + ": end updating eJournal");
		} else {
		    logger.info(pid + ": start updating eJournal issue");
		    updateVolume(dtlBean);
		    logger.info(pid + ": end updating eJournal issue");
		}
	    } else if (partitionC.compareTo("WPD01") == 0) {
		logger.info(pid + ": start updating monograph (wpd01)");
		updateMonographs(dtlBean);
		logger.info(pid + ": end updating monograph (wpd01)");
	    } else if (partitionC.compareTo("WPD02") == 0) {

		logger.info(pid + ": start updating monograph (wpd02)");
		updateMonographs(dtlBean);
		logger.info(pid + ": end updating monograph (wpd02)");
	    } else if (partitionC.compareTo("WSC01") == 0) {
		if (dtlBean.isParent()) {
		    logger.info(pid + ": start updating webpage (wsc01)");
		    updateWebpageParent(dtlBean);
		    logger.info(pid + ": end updating webpage (wsc01)");
		} else {
		    logger.info(pid
			    + ": start updating webpage version (wsc01)");
		    updateVersion(dtlBean);
		    logger.info(pid + ": end updating webpage version (wsc01)");
		}
	    } else if (partitionC.compareTo("WSI01") == 0) {
		logger.info(pid + ": start updating webpage (wsi01)");
		updateWebpage(dtlBean);
		logger.info(pid + ": end updating webpage (wsi01)");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.info(dtlBean.getPid() + " " + e.getMessage());
	}

    }

    @Override
    public void delete(String p) {
	webclient.delete(p);

    }

    private void updatePart(DigitalEntity dtlBean) {

	String usageType = dtlBean.getUsageType();
	if (usageType.compareTo(ObjectType.volume.toString()) == 0) {
	    updateVolume(dtlBean);
	} else if (usageType.compareTo(ObjectType.file.toString()) == 0) {
	    updateFile(dtlBean);
	} else if (usageType.compareTo(ObjectType.version.toString()) == 0) {
	    updateVersion(dtlBean);
	} else // if (usageType.compareTo(ObjectType.issue.toString()) == 0)
	{
	    updateFile(dtlBean);
	}
    }

    private void updateVersion(DigitalEntity dtlBean) {
	String pid = namespace + ":" + dtlBean.getPid();
	try {
	    ObjectType t = ObjectType.version;
	    dtlBean.addTransformer("oaidc");
	    dtlBean.addTransformer("epicur");
	    webclient.createObject(dtlBean, t);
	    logger.info(pid + " " + "Found webpage version.");

	    String metadata = "<" + pid
		    + "> <http://purl.org/ontology/bibo/Website> \""
		    + dtlBean.getLabel() + "\" .\n" + "<" + pid
		    + "> <http://purl.org/dc/terms/title> \""
		    + dtlBean.getLabel() + "\" .\n";
	    webclient.setMetadata(dtlBean, metadata);
	    webclient.addUrn(dtlBean.getPid(), namespace, "hbz:929:02");
	    webclient.makeOaiSet(dtlBean);
	    logger.info(pid + " " + "updated.\n");
	} catch (IllegalArgumentException e) {
	    logger.debug(e.getMessage());
	}

    }

    private void updateVolume(DigitalEntity dtlBean) {
	String pid = namespace + ":" + dtlBean.getPid();
	logger.info(pid + " " + "Found eJournal volume.");
	initVolume(dtlBean, pid);
	Vector<DigitalEntity> issues = getParts(dtlBean);
	int num = issues.size();
	int count = 1;
	logger.info(pid + " Found " + num + " issues.");
	for (DigitalEntity issue : issues) {
	    logger.info("Part: " + (count++) + "/" + num);
	    updateIssue(issue);
	}

	logger.info(pid + " " + "updated.\n");
    }

    private void initVolume(DigitalEntity dtlBean, String pid) {
	try {
	    dtlBean.addTransformer("oaidc");
	    dtlBean.addTransformer("epicur");
	    ObjectType t = ObjectType.volume;
	    webclient.createResource(t, dtlBean);
	    String metadata = "<" + pid
		    + "> <http://purl.org/ontology/bibo/volume> \""
		    + dtlBean.getLabel() + "\" .\n" + "<" + pid
		    + "> <http://purl.org/dc/terms/title> \""
		    + dtlBean.getLabel() + "\" .\n";
	    webclient.setMetadata(dtlBean, metadata);
	    webclient.addUrn(dtlBean.getPid(), namespace, "hbz:929:02");
	    webclient.makeOaiSet(dtlBean);
	} catch (Exception e) {
	    logger.debug("", e);
	}
    }

    private void updateIssue(DigitalEntity issue) {
	try {

	    issue.addTransformer("oaidc");
	    issue.addTransformer("epicur");
	    updateFile(issue);
	    webclient.addUrn(issue.getPid(), namespace, "hbz:929:02");
	    webclient.makeOaiSet(issue);

	} catch (Exception e) {
	    logger.debug("", e);
	}

    }

    private void updateFile(DigitalEntity dtlBean) {
	String pid = namespace + ":" + dtlBean.getPid();

	try {
	    ObjectType t = ObjectType.file;
	    webclient.createObject(dtlBean, t);
	    logger.info(pid + " " + "Found file part.");
	    String metadata = "<" + pid
		    + "> <http://purl.org/dc/terms/title> \""
		    + dtlBean.getLabel() + "\" .\n";
	    webclient.setMetadata(dtlBean, metadata);
	    logger.info(pid + " " + "updated.\n");
	} catch (IllegalArgumentException e) {
	    logger.debug(e.getMessage());
	}

    }

    private void updateWebpage(DigitalEntity dtlBean) {
	String pid = namespace + ":" + dtlBean.getPid();
	try {
	    dtlBean.addTransformer("oaidc");
	    dtlBean.addTransformer("epicur");
	    webclient.createResource(ObjectType.webpage, dtlBean);
	    webclient.autoGenerateMetdata(dtlBean);
	    webclient.addUrn(dtlBean.getPid(), namespace, "hbz:929:02");
	    webclient.makeOaiSet(dtlBean);
	    if (dtlBean.getStream(StreamType.DATA).getMimeType()
		    .compareTo("application/zip") == 0)

	    {
		dtlBean.setParentPid(dtlBean.getPid());
		dtlBean.setPid(dtlBean.getPid() + "-1");
		updateFile(dtlBean);
	    }
	} catch (IllegalArgumentException e) {
	    logger.warn(e.getMessage());
	    // webclient.createResource(ObjectType.monograph, dtlBean);
	}

	Vector<DigitalEntity> list = getParts(dtlBean);
	int num = list.size();
	int count = 1;
	logger.info(pid + " Found " + num + " parts.");
	for (DigitalEntity b : list) {
	    logger.info("Part: " + (count++) + "/" + num);
	    updateVersion(b);
	}

	logger.info(pid + " " + "updated.\n");

    }

    private void updateMonographs(DigitalEntity dtlBean) {

	String pid = namespace + ":" + dtlBean.getPid();
	try {
	    dtlBean.addTransformer("oaidc");
	    dtlBean.addTransformer("epicur");
	    webclient.createResource(ObjectType.monograph, dtlBean);
	    webclient.autoGenerateMetdata(dtlBean);
	    webclient.addUrn(dtlBean.getPid(), namespace, "hbz:929:02");
	    webclient.makeOaiSet(dtlBean);
	    if (dtlBean.getStream(StreamType.DATA).getMimeType()
		    .compareTo("application/pdf") == 0) {
		dtlBean.setParentPid(dtlBean.getPid());
		dtlBean.setPid(dtlBean.getPid() + "-1");
		dtlBean.removeTransformer("oaidc");
		dtlBean.removeTransformer("epicur");
		updateFile(dtlBean);
	    }
	} catch (IllegalArgumentException e) {
	    logger.warn(e.getMessage());
	    // webclient.createResource(ObjectType.monograph, dtlBean);
	}

	Vector<DigitalEntity> list = getParts(dtlBean);
	int num = list.size();
	int count = 1;
	logger.info(pid + " Found " + num + " parts.");
	for (DigitalEntity b : list) {
	    logger.info("Part: " + (count++) + "/" + num);
	    updatePart(b);
	}

	logger.info(pid + " " + "updated.\n");

    }

    private void updateJournal(DigitalEntity dtlBean) {
	String pid = namespace + ":" + dtlBean.getPid();
	try {
	    initJournal(dtlBean, pid);
	    Vector<DigitalEntity> list = getParts(dtlBean);
	    int numOfVols = list.size();
	    int count = 1;
	    logger.info(pid + " Found " + numOfVols + " parts.");
	    for (DigitalEntity b : list) {
		logger.info("Part: " + (count++) + "/" + numOfVols);
		updatePart(b);
	    }
	    logger.info(pid + " " + "and all volumes updated.\n");
	} catch (Exception e) {
	    logger.error(pid + " " + e.getMessage());
	}
    }

    private void initJournal(DigitalEntity dtlBean, String pid) {
	try {
	    logger.info(pid + " Found ejournal.");
	    logger.info(dtlBean.toString());
	    dtlBean.addTransformer("oaidc");
	    dtlBean.addTransformer("epicur");
	    webclient.createResource(ObjectType.journal, dtlBean);
	    webclient.autoGenerateMetdata(dtlBean);
	    webclient.addUrn(dtlBean.getPid(), namespace, "hbz:929:02");
	    webclient.makeOaiSet(dtlBean);
	} catch (Exception e) {
	    logger.debug("", e);
	}
    }

    private void updateJournalParent(DigitalEntity dtlBean) {
	String pid = namespace + ":" + dtlBean.getPid();
	try {
	    logger.info(pid + " Found ejournal.");
	    dtlBean.addTransformer("oaidc");
	    dtlBean.addTransformer("epicur");
	    webclient.createResource(ObjectType.journal, dtlBean);
	    webclient.autoGenerateMetdata(dtlBean);
	    webclient.addUrn(dtlBean.getPid(), namespace, "hbz:929:02");
	    webclient.makeOaiSet(dtlBean);
	    Vector<DigitalEntity> parts = getParts(dtlBean);
	    int numOfVols = parts.size();
	    logger.info(pid + " " + "Found " + numOfVols + " parts.");
	    logger.info(pid + " " + "Will not update volumes.");
	    logger.info(pid + " " + "updated.\n");
	} catch (Exception e) {
	    logger.error(pid + " " + e.getMessage());
	}

    }

    private void updateWebpageParent(DigitalEntity dtlBean) {
	String pid = namespace + ":" + dtlBean.getPid();
	try {
	    logger.info(pid + " Found webpage.");
	    dtlBean.addTransformer("oaidc");
	    dtlBean.addTransformer("epicur");
	    webclient.createResource(ObjectType.webpage, dtlBean);
	    webclient.autoGenerateMetdata(dtlBean);
	    webclient.addUrn(dtlBean.getPid(), namespace, "hbz:929:02");
	    webclient.makeOaiSet(dtlBean);
	    Vector<DigitalEntity> viewLinks = getParts(dtlBean);
	    int numOfVersions = viewLinks.size();
	    logger.info(pid + " " + "Found " + numOfVersions + " versions.");
	    logger.info(pid + " " + "Will not update versions.");
	    logger.info(pid + " " + "updated.\n");
	} catch (Exception e) {
	    logger.info(pid + " " + e.getMessage());
	}

    }

    private Vector<DigitalEntity> getParts(DigitalEntity dtlBean) {
	Vector<DigitalEntity> links = new Vector<DigitalEntity>();
	for (RelatedDigitalEntity rel : dtlBean.getRelated()) {
	    if (rel.relation
		    .compareTo(DigitalEntityRelation.part_of.toString()) == 0)
		links.add(rel.entity);
	}
	return links;
    }

}
