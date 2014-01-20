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
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.stringtemplate.v4.ST;

import de.nrw.hbz.regal.mab.MabRecord.Person;
import de.nrw.hbz.regal.mab.MabRecord.PersonType;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class Mabencoder {
    ST st = null;

    /**
     * @param template
     *            a StringTemplate template file
     */
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

    /**
     * @param record
     *            a MabRecord
     * @return string output in binary form
     */
    public ByteArrayOutputStream render(MabRecord record) {
	addFields(record);
	addPersons(record);
	return render();
    }

    private void addFields(MabRecord record) {
	st.add("record", record);
    }

    private void addPersons(MabRecord record) {
	Iterator<Person> persons = record.personen.values().iterator();
	int cP = 1;
	int cC = 1;
	while (persons.hasNext()) {
	    Person p = persons.next();
	    if (p.type.equals(PersonType.natuerlichePerson)) {
		st.add("person" + cP, p);
		cP++;
	    } else {
		st.add("corporateBody" + cC, p);
		cC++;
	    }
	}
    }

    private ByteArrayOutputStream render() {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	PrintWriter writer = new PrintWriter(out);
	writer.append(st.render());
	writer.close();
	return out;
    }

}
