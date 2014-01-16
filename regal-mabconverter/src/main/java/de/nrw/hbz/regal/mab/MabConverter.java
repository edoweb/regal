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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import de.nrw.hbz.regal.fedora.RdfUtils;
import de.nrw.hbz.regal.mab.Person.PersonType;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class MabConverter {

    static MabConverter me = null;
    MabRecord record = null;
    Mabencoder encoder = null;
    private HashMap<String, String> map = new HashMap<String, String>();
    private HashMap<String, String> constants = new HashMap<String, String>();
    private HashMap<String, Person> persons = new HashMap<String, Person>();

    public enum Format {
	mabxml
    }

    private MabConverter() {
	record = new MabRecord();
    }

    public static MabConverter getInstance() {
	if (me == null)
	    me = new MabConverter();
	return me;
    }

    private void init() throws IOException {
	InputStream template = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream("mabxml-string-template.xml");
	map = new HashMap<String, String>();
	constants = new HashMap<String, String>();
	persons = new HashMap<String, Person>();
	encoder = new Mabencoder(template);
	loadMap("map.txt", map);
	loadMap("constants.txt", constants);
    }

    public ByteArrayOutputStream convert(InputStream in) throws IOException {
	init();

	emitConstants();
	return convert(in, RDFFormat.NTRIPLES, Format.mabxml);
    }

    private void emitConstants() {
	Set<Entry<String, String>> entries = constants.entrySet();
	for (Entry<String, String> e : entries) {
	    encoder.collectField(e.getKey(), e.getValue());
	}
    }

    private ByteArrayOutputStream convert(InputStream in,
	    RDFFormat inputFormat, Format output) {
	Graph graph = RdfUtils.readRdfToGraph(in, inputFormat, "");
	Iterator<Statement> it = graph.iterator();
	while (it.hasNext()) {
	    Statement st = it.next();
	    String pred = st.getPredicate().stringValue();
	    String obj = st.getObject().stringValue();

	    if (map.containsKey(pred)) {
		encoder.collectField(map.get(pred), obj);
	    } else {
		collect(st);
	    }

	}
	addCollectedStatements();
	return encoder.render();
    }

    private void addCollectedStatements() {
	int countPersons = 1;
	int countCorporateBodies = 1;
	for (Person p : persons.values()) {
	    if (p.type == PersonType.creator) {
		if (countPersons == 1) {
		    encoder.collectField("100", p.name);
		    encoder.collectField("1009", p.id);
		} else if (countPersons == 2) {
		    encoder.collectField("104", p.name);
		    encoder.collectField("1049", p.id);
		} else if (countPersons == 3) {
		    encoder.collectField("108", p.name);
		    encoder.collectField("1089", p.id);
		}
		countPersons++;
	    } else if (p.type == PersonType.corporateBody) {
		if (countPersons == 1) {
		    encoder.collectField("200", p.name);
		    encoder.collectField("2009", p.id);
		} else if (countPersons == 2) {
		    encoder.collectField("201", p.name);
		    encoder.collectField("2019", p.id);
		}
		countCorporateBodies++;
	    }
	}
    }

    private void collect(Statement st) {
	String pred = st.getPredicate().stringValue();
	String obj = st.getObject().stringValue();
	String subj = st.getSubject().stringValue();
	if (pred.equals(LobidVocabular.dceCreator)) {
	    if (!persons.containsKey(obj))
		persons.put(obj, new Person(obj));
	    else {
		// do nothing!
	    }
	} else if (pred.equals(LobidVocabular.gndPreferredName)) {
	    if (!persons.containsKey(subj)) {
		Person person = new Person(subj);
		persons.put(subj, person);
	    }
	    Person person = persons.get(subj);
	    person.name = obj;
	    persons.put(subj, person);
	} else if (pred.equals(LobidVocabular.gndDateOfBirth)) {
	    if (!persons.containsKey(subj)) {
		Person person = new Person(subj);
		persons.put(subj, person);
	    }
	    Person person = persons.get(subj);
	    person.name = obj;
	    persons.put(subj, person);
	} else if (obj.equals(LobidVocabular.gndDifferentiatedPerson)) {
	    if (!persons.containsKey(subj)) {
		Person person = new Person(subj);
		persons.put(subj, person);
	    }
	    Person person = persons.get(subj);
	    person.type = PersonType.creator;
	    persons.put(subj, person);
	} else if (obj.equals(LobidVocabular.gndUndifferentiatedPerson)) {
	    if (!persons.containsKey(subj)) {
		Person person = new Person(subj);
		persons.put(subj, person);
	    }
	    Person person = persons.get(subj);
	    person.type = PersonType.creator;
	    persons.put(subj, person);
	} else if (obj.equals(LobidVocabular.gndCorporateBody)) {
	    if (!persons.containsKey(subj)) {
		Person person = new Person(subj);
		persons.put(subj, person);
	    }
	    Person person = persons.get(subj);
	    person.type = PersonType.corporateBody;
	    persons.put(subj, person);
	} else if (obj.equals(LobidVocabular.gndOrganOfCorporateBody)) {
	    if (!persons.containsKey(subj)) {
		Person person = new Person(subj);
		persons.put(subj, person);
	    }
	    Person person = persons.get(subj);
	    person.type = PersonType.corporateBody;
	    persons.put(subj, person);
	}

    }

    private void loadMap(String filename, HashMap<String, String> fillMe)
	    throws IOException {
	File mapfile = new File(Thread.currentThread().getContextClassLoader()
		.getResource(filename).getPath());
	CsvMapper mapper = new CsvMapper();
	CsvSchema schema = mapper.schemaFor(KeyValue.class);
	com.fasterxml.jackson.databind.MappingIterator<KeyValue> it = mapper
		.reader(KeyValue.class).with(schema).readValues(mapfile);

	while (it.hasNextValue()) {
	    KeyValue pair = it.nextValue();
	    fillMe.put(pair.key, pair.value);
	}
    }

}
