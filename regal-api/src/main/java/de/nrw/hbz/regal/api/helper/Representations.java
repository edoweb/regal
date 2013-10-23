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
package de.nrw.hbz.regal.api.helper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import de.nrw.hbz.regal.api.CreateObjectBean;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.fedora.FedoraInterface;

/**
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
class Representations {
    final static Logger logger = LoggerFactory.getLogger(Representations.class);
    FedoraInterface fedora = null;
    String server = null;
    String uriPrefix = null;

    public Representations(FedoraInterface fedora, String server) {
	this.fedora = fedora;
	this.server = server;
	uriPrefix = server + "/" + "resource" + "/";

    }

    /**
     * @param list
     *            a list with pids
     * @param type
     *            the type to be displaye
     * @param namespace
     *            list only objects in this namespace
     * @param from
     *            show only hits starting at this index
     * @param until
     *            show only hits ending at this index
     * @param getListingFrom
     *            tells from which component the listing comes from
     * 
     * @return html listing of all objects
     */
    public String getAllOfTypeAsHtml(List<String> list, String type,
	    String namespace, int from, int until, String getListingFrom) {

	String result = "";
	try {
	    java.net.URL fileLocation = Thread.currentThread()
		    .getContextClassLoader().getResource("list.html");

	    StringWriter writer = new StringWriter();
	    IOUtils.copy(fileLocation.openStream(), writer);
	    String data = writer.toString();

	    if (type == null || type.isEmpty())
		type = "resource";
	    if (namespace == null || namespace.isEmpty())
		namespace = "all namespaces";

	    ST st = new ST(data, '$', '$');
	    st.add("type", type);
	    st.add("namespace", namespace);
	    st.add("from", from);
	    st.add("until", until);
	    st.add("getListingFrom", getListingFrom);

	    for (String item : list) {
		st.add("items", "<li><a href=\"" + uriPrefix + item + "\">"
			+ item + "</a></li>");

	    }
	    result = st.render();
	} catch (IOException e) {
	    throw new HttpArchiveException(500, e);
	}

	return result;
    }

    /**
     * @param pid
     *            the pid to read from
     * @return the parentPid and contentType as json
     */
    public CreateObjectBean getRegalJson(String pid) {
	Node node = fedora.readNode(pid);
	CreateObjectBean result = new CreateObjectBean();
	String parentPid = null;
	String type = node.getContentType();
	parentPid = fedora.getNodeParent(node);
	result.setParentPid(parentPid);
	result.setType(type);
	return result;
    }

    public String getReM(String pid, String format, String fedoraExtern,
	    List<String> parents, List<String> children) {
	Node node = fedora.readNode(pid);
	OaiOreMaker ore = new OaiOreMaker(node, server, uriPrefix);
	return ore.getReM(format, parents, children);
    }

}
