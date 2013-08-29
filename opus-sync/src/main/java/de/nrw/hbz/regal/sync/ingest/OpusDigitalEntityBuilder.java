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

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.nrw.hbz.regal.sync.extern.DigitalEntity;
import de.nrw.hbz.regal.sync.extern.DigitalEntityBuilderInterface;
import de.nrw.hbz.regal.sync.extern.StreamType;
import de.nrw.hbz.regal.sync.extern.XmlUtils;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class OpusDigitalEntityBuilder implements DigitalEntityBuilderInterface {
    final static Logger logger = LoggerFactory
	    .getLogger(OpusDigitalEntityBuilder.class);

    HashMap<String, DigitalEntity> map = new HashMap<String, DigitalEntity>();

    @Override
    public DigitalEntity build(String baseDir, String pid) {

	// pid = pid.replace(':', '-');
	if (!map.containsKey(pid)) {
	    DigitalEntity e = new DigitalEntity(baseDir);
	    e.setPid(pid);
	    // store reference to e
	    map.put(pid, e);
	    // update Reference
	    e = buildDigitalEntity(baseDir, pid, e);
	    return e;
	}
	return map.get(pid);
    }

    private DigitalEntity buildDigitalEntity(String baseDir, String pid,
	    DigitalEntity dtlDe) {
	// dtlDe = new DigitalEntity(baseDir);
	File file = new File(baseDir + File.separator + pid + ".xml");
	dtlDe.addStream(file, "application/xml", StreamType.xMetaDissPlus);
	try {
	    Vector<String> files = new Vector<String>();

	    Element root = XmlUtils.getDocument(file);

	    // dtlDe.setDc(nodeToString(root));
	    NodeList list = root.getElementsByTagName("dc:title");

	    if (list != null && list.getLength() > 0) {
		dtlDe.setLabel(list.item(0).getTextContent());
	    }

	    list = root.getElementsByTagName("dc:type");
	    if (list != null && list.getLength() > 0) {
		for (int i = 0; i < list.getLength(); i++) {
		    Element el = (Element) list.item(i);
		    String type = el.getAttribute("xsi:type");
		    if (type.compareTo("oai:pub-type") == 0) {
			dtlDe.setType(el.getTextContent());
		    }

		}
	    }

	    NodeList fileProperties = root
		    .getElementsByTagName("ddb:fileProperties");

	    for (int i = 0; i < fileProperties.getLength(); i++) {
		Element fileProperty = (Element) fileProperties.item(i);
		String filename = fileProperty.getAttribute("ddb:fileName");
		files.add(filename);
	    }

	    int i = 0;
	    for (String f : files) {

		if (f.endsWith("pdf")) {
		    i++;
		    dtlDe.addStream(new File(baseDir + File.separator + pid
			    + "_" + i + ".pdf"), "application/pdf",
			    StreamType.DATA);

		}

	    }
	}

	catch (Exception e) {
	    logger.debug(e.getMessage());
	}

	return dtlDe;

    }

}
