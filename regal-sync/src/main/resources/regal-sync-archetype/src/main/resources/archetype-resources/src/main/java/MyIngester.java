#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package};

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.sync.extern.DigitalEntity;
import ${package}.sync.ingest.IngestInterface;
import ${package}.sync.ingest.Webclient;

public class MyIngester implements IngestInterface {
    final static Logger logger = LoggerFactory.getLogger(MyIngester.class);

    private String namespace = null;
    String host = null;
    Webclient webclient = null;

    public void init(String host, String user, String password, String ns) {
	this.namespace = ns;
	this.host = host;
	webclient = new Webclient(namespace, user, password, host);
    }

    public void ingest(DigitalEntity dtlBean) {
	// TODO: implement me
    }

    public void update(DigitalEntity dtlBean) {
	// TODO: implement me
    }

    public void delete(String pid) {
	// TODO: implement me
    }

}
