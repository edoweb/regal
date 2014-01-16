package de.nrw.hbz.regal.mab;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.stringtemplate.v4.ST;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class Mabencoder {
    private final StringWriter writer = new StringWriter();
    ST st = null;

    public Mabencoder(InputStream template) {
	try {
	    StringWriter st_writer = new StringWriter();
	    IOUtils.copy(template, st_writer);
	    String data = st_writer.toString();
	    st = new ST(data, '$', '$');
	} catch (IOException e) {
	    throw new MabException("Couldn't read template file.", e);
	}
    }

    public ByteArrayOutputStream render() {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	PrintWriter writer = new PrintWriter(out);
	writer.append(st.render());
	writer.close();
	return out;
    }

    public void collectField(final String name, final String value) {
	if (name == null || name.isEmpty()) {
	    return;
	}
	st.add(name, value);
    }
}
