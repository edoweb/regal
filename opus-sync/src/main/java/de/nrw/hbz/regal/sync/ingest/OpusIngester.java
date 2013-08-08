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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.regal.api.helper.ObjectType;
import de.nrw.hbz.regal.datatypes.ContentModel;
import de.nrw.hbz.regal.sync.extern.DigitalEntity;
import de.nrw.hbz.regal.sync.extern.StreamType;

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
public class OpusIngester implements IngestInterface {
    final static Logger logger = LoggerFactory.getLogger(OpusIngester.class);

    private String namespace = "opus";
    String host = null;
    Webclient webclient = null;
    HashMap<String, String> map = new HashMap<String, String>();

    @Override
    public void init(String host, String user, String password, String ns) {
	this.namespace = ns;
	this.host = host;
	webclient = new Webclient(namespace, user, password, host);
    }

    @Override
    public ContentModel createContentModel() {
	return null;
    }

    @Override
    public void ingest(DigitalEntity dtlBean) {
	String pid = dtlBean.getPid().replace(':', '-');
	dtlBean.setPid(pid);
	// pid = pid.substring(pid.lastIndexOf(':') + 1);
	logger.info("Start ingest: " + namespace + ":" + pid);

	updateMonograph(dtlBean);

    }

    @Override
    public void update(DigitalEntity dtlBean) {
	ingest(dtlBean);
    }

    private void updateMonograph(DigitalEntity dtlBean) {
	String pid = dtlBean.getPid();

	map.clear();
	try {
	    webclient.createObject(dtlBean, "application/pdf",
		    ObjectType.monograph);
	    logger.info(pid + " " + "updated.\n");
	    OpusMapping mapper = new OpusMapping();
	    String metadata = mapper.map(
		    dtlBean.getStream(StreamType.xMetaDissPlus).getFile(),
		    namespace + ":" + dtlBean.getPid());

	    webclient.autoGenerateMetadataMerge(dtlBean, metadata);
	    webclient.publish(dtlBean);
	} catch (IllegalArgumentException e) {
	    logger.debug(e.getMessage());
	}
    }

    @Override
    public void delete(String pid) {
	webclient.delete(pid.substring(pid.lastIndexOf(':') + 1));
    }

    @Override
    public void setNamespace(String namespace) {
	this.namespace = namespace;

    }
}
