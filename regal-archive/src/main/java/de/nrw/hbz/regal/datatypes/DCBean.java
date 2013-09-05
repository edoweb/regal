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
package de.nrw.hbz.regal.datatypes;

import java.util.List;
import java.util.Vector;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class DCBean {
    List<String> contributer = new Vector<String>();
    List<String> coverage = new Vector<String>();
    List<String> creator = new Vector<String>();
    List<String> date = new Vector<String>();
    List<String> description = new Vector<String>();
    List<String> format = new Vector<String>();
    List<String> identifier = new Vector<String>();
    List<String> language = new Vector<String>();
    List<String> publisher = new Vector<String>();
    List<String> relation = new Vector<String>();
    List<String> rights = new Vector<String>();
    List<String> source = new Vector<String>();
    List<String> subject = new Vector<String>();
    List<String> title = new Vector<String>();
    List<String> type = new Vector<String>();

    public List<String> getContributer() {
	return contributer;
    }

    public DCBean setContributer(List<String> contributer) {
	this.contributer = contributer;
	return this;
    }

    public DCBean addContributer(String e) {
	contributer.add(e);
	return this;
    }

    public String getFirstContributer() {
	List<String> elements = getContributer();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getCoverage() {
	return coverage;
    }

    public DCBean setCoverage(List<String> coverage) {
	this.coverage = coverage;
	return this;
    }

    public DCBean addCoverage(String e) {
	coverage.add(e);
	return this;
    }

    public String getFirstCoverage() {
	List<String> elements = getCoverage();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getCreator() {
	return creator;
    }

    public DCBean setCreator(List<String> creator) {
	this.creator = creator;
	return this;
    }

    public DCBean addCreator(String e) {
	creator.add(e);
	return this;
    }

    public String getFirstCreator() {
	List<String> elements = getCreator();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getDate() {
	return date;
    }

    public DCBean setDate(List<String> date) {
	this.date = date;
	return this;
    }

    public DCBean addDate(String e) {
	date.add(e);
	return this;
    }

    public String getFirstDate() {
	List<String> elements = getDate();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getDescription() {
	return description;
    }

    public DCBean setDescription(List<String> description) {
	this.description = description;
	return this;
    }

    public DCBean addDescription(String e) {
	description.add(e);
	return this;
    }

    public String getFirstDescription() {
	List<String> elements = getDescription();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getFormat() {
	return format;
    }

    public DCBean setFormat(List<String> format) {
	this.format = format;
	return this;
    }

    public DCBean addFormat(String e) {
	format.add(e);
	return this;
    }

    public String getFirstFormat() {
	List<String> elements = getFormat();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getIdentifier() {
	return identifier;
    }

    public DCBean setIdentifier(List<String> identifier) {
	this.identifier = identifier;
	return this;
    }

    public DCBean addIdentifier(String e) {
	identifier.add(e);
	return this;
    }

    public String getFirstIdentifier() {
	List<String> elements = getIdentifier();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getLanguage() {
	return language;
    }

    public DCBean setLanguage(List<String> language) {
	this.language = language;
	return this;
    }

    public DCBean addLanguage(String e) {
	language.add(e);
	return this;
    }

    public String getFirstLanguage() {
	List<String> elements = getLanguage();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getPublisher() {
	return publisher;
    }

    public DCBean setPublisher(List<String> publisher) {
	this.publisher = publisher;
	return this;
    }

    public DCBean addPublisher(String e) {
	publisher.add(e);
	return this;
    }

    public String getFirstPublisher() {
	List<String> elements = getPublisher();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getRelation() {
	return relation;
    }

    public DCBean setRelation(List<String> relation) {
	this.relation = relation;
	return this;
    }

    public DCBean addRelation(String e) {
	relation.add(e);
	return this;
    }

    public String getFirstRelation() {
	List<String> elements = getRelation();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getRights() {
	return rights;
    }

    public DCBean setRights(List<String> rights) {
	this.rights = rights;
	return this;
    }

    public DCBean addRights(String e) {
	rights.add(e);
	return this;
    }

    public String getFirstRights() {
	List<String> elements = getRights();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getSource() {
	return source;
    }

    public DCBean setSource(List<String> source) {
	this.source = source;
	return this;
    }

    public DCBean addSource(String e) {
	source.add(e);
	return this;
    }

    public String getFirstSource() {
	List<String> elements = getSource();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getSubject() {
	return subject;
    }

    public DCBean setSubject(List<String> subject) {
	this.subject = subject;
	return this;
    }

    public DCBean addSubject(String e) {
	subject.add(e);
	return this;
    }

    public String getFirstSubject() {
	List<String> elements = getSubject();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getTitle() {
	return title;
    }

    public DCBean setTitle(List<String> title) {
	this.title = title;
	return this;
    }

    public DCBean addTitle(String e) {
	title.add(e);
	return this;
    }

    public String getFirstTitle() {
	List<String> elements = getTitle();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

    public List<String> getType() {
	return type;
    }

    public DCBean setType(List<String> type) {
	this.type = type;
	return this;
    }

    public DCBean addType(String e) {
	type.add(e);
	return this;
    }

    public String getFirstType() {
	List<String> elements = getType();
	if (elements == null || elements.size() == 0) {
	    return "";
	}

	return elements.get(0);
    }

}
