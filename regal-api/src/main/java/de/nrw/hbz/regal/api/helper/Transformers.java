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

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.datatypes.Transformer;
import de.nrw.hbz.regal.fedora.FedoraInterface;

/**
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class Transformers {

    final static Logger logger = LoggerFactory.getLogger(Transformers.class);
    FedoraInterface fedora = null;
    String server = null;
    String uriPrefix = null;

    /**
     * @param fedora
     * @param server
     */
    public Transformers(FedoraInterface fedora, String server) {
	this.fedora = fedora;
	this.server = server;
    }

    /**
     * @param p
     * @param namespace
     * @param transformerId
     * @return
     */
    public String connectTransformer(String p, String namespace,
	    String transformerId) {
	String pid = namespace + ":" + p;
	Node n = fedora.readNode(pid);
	n.addTransformer(createTransformer(transformerId));
	fedora.updateNode(n);
	return "Epicur Transfomer added";
    }

    /**
     * @param p
     * @param namespace
     * @param transformerId
     * @return
     */
    public String unconnectTransformer(String p, String namespace,
	    String transformerId) {
	String pid = namespace + ":" + p;
	Node n = fedora.readNode(pid);
	n.removeTransformer(transformerId);
	fedora.updateNode(n);
	return "Epicur Transfomer added";
    }

    public List<String> listTransformers(String namespace) {
	return fedora.findNodes("CM:*");
    }

    public void updateTransformer(String transformerId) {
	createTransformer(transformerId);
    }

    private Transformer createTransformer(String transformerId) {
	Transformer cm = new Transformer(transformerId);
	cm.addMethod(transformerId, server + "/utils/" + transformerId
		+ "/(pid)");
	List<Transformer> l = new Vector<Transformer>();
	l.add(cm);
	fedora.updateContentModels(l);
	return cm;
    }
}
