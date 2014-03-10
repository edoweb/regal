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

import java.io.InputStream;
import java.net.URL;

import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.mab.MabConverter;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class AlephMabMaker {

    @SuppressWarnings({ "javadoc", "serial" })
    public class AlephException extends RuntimeException {

	public AlephException(String message, Throwable cause) {
	    super(message, cause);
	}

	public AlephException(String message) {
	    super(message);
	}

    }

    /**
     * @param node
     *            the node to create a mab xml entry for
     * @param uriPrefix
     *            server host
     * @return a string containing mab xml for the obeject
     */
    public String aleph(Node node, String uriPrefix) {
	try {
	    String pid = node.getPID();
	    String metadata = uriPrefix + "/resource/" + pid + "/metadata";
	    InputStream input;
	    input = new URL(metadata).openConnection().getInputStream();
	    MabConverter converter = new MabConverter(node.getPID());
	    return new String(converter.convert(input).toByteArray(), "utf-8");

	} catch (Exception e) {
	    throw new AlephException("Conversion Problem!", e);
	}
    }

}
