package de.nrw.hbz.regal.mab;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.stringtemplate.v4.ST;

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
