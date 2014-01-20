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
package de.nrw.hbz.regal.mab;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openrdf.rio.RDFFormat;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class MabConverter {

    static MabConverter me = null;
    Mabencoder encoder = null;
    private String topic;

    /**
     * @author jan
     * 
     */
    public enum Format {
	/**
	 * mabxml for hbz-"Schnittstelle Metadaten"
	 */
	mabxml
    }

    /**
     * @param topic
     *            the id of a resource we are talking about.
     * @throws IOException
     *             if template file for encoder is not avail.
     */
    public MabConverter(String topic) throws IOException {
	this.topic = topic;
	InputStream template = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream("mabxml-string-template-on-record.xml");
	encoder = new Mabencoder(template);
    }

    /**
     * @param in
     *            An n-triple rdf Inputstream
     * @return the n-triples converted to mabxml
     */
    public ByteArrayOutputStream convert(InputStream in) {

	return convert(in, RDFFormat.NTRIPLES, Format.mabxml);
    }

    private ByteArrayOutputStream convert(InputStream in,
	    RDFFormat inputFormat, Format output) {
	RegalToMabMapper mapper = new RegalToMabMapper();
	MabRecord record = mapper.map(in, topic);
	return encoder.render(record);
    }

}
