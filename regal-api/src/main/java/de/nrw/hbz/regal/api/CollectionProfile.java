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
package de.nrw.hbz.regal.api;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

import de.nrw.hbz.regal.api.helper.View;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@XmlRootElement
public class CollectionProfile {
    String message = "";
    List<View> all;
    List<String> restricted = new Vector<String>();
    List<String> unrestricted = new Vector<String>();
    List<String> noRight = new Vector<String>();
    List<String> noDDC = new Vector<String>();
    List<String> noUrn = new Vector<String>();
    List<String> noYear = new Vector<String>();
    List<String> noTitle = new Vector<String>();
    List<String> noCreator = new Vector<String>();
    List<String> noType = new Vector<String>();
    List<TypeObjectDictionary> types = new Vector<TypeObjectDictionary>();
    private HashMap<String, List<String>> map = new HashMap<String, List<String>>();

    /**
     * Default constructor
     */
    public CollectionProfile() {

    }

    CollectionProfile(List<View> all) {
	this.all = all;

	for (View view : all) {
	    if (view == null) {
		continue;
	    }

	    Vector<String> ddcs = view.getDdc();
	    Vector<String> urns = view.getUrn();
	    Vector<String> years = view.getYear();
	    Vector<String> titles = view.getTitle();
	    Vector<String> creators = view.getCreator();
	    Vector<String> types = view.getType();

	    Vector<String> rights = view.getRights();
	    if (rights != null)
		for (String right : rights) {
		    if (right == null)
			continue;
		    if (right.compareTo("everyone") == 0) {
			unrestricted.add(view.getUri());
		    } else if (right.compareTo("restricted") == 0) {
			restricted.add(view.getUri());
		    } else {
			noRight.add(view.getUri());
		    }
		}

	    if (ddcs == null || ddcs.isEmpty())
		noDDC.add(view.getUri());
	    if (urns == null || urns.isEmpty())
		noUrn.add(view.getUri());
	    if (years == null || years.isEmpty())
		noYear.add(view.getUri());
	    if (titles == null || titles.isEmpty())
		noTitle.add(view.getUri());
	    if (creators == null || creators.isEmpty())
		noCreator.add(view.getUri());
	    if (types == null || types.isEmpty())
		noType.add(view.getUri());
	    else {
		for (String type : types) {
		    if (type == null)
			continue;
		    List<String> uris = null;
		    if (map.containsKey(type)) {
			uris = map.get(type);

		    } else {
			uris = new Vector<String>();
		    }
		    uris.add(view.getUri());
		    map.put(type, uris);
		}
	    }

	}
	for (String type : map.keySet()) {
	    if (type == null)
		continue;
	    types.add(new TypeObjectDictionary(type, map.get(type)));
	}

	StringWriter strwrt = new StringWriter();
	PrintWriter out = new PrintWriter(strwrt);

	out.append("Number Of Elements: " + all.size() + " - ");
	out.println("Restricted Objects: " + restricted.size() + " - ");
	out.println("Unrestricted Objects: " + unrestricted.size() + " - ");
	out.println("NoRights Objects: " + noRight.size() + " - ");
	out.println("NoCreator Objects: " + noCreator.size() + " - ");
	out.println("NoTitle Objects: " + noTitle.size() + " - ");
	out.println("NoYear Objects: " + noYear.size() + " - ");
	out.println("NoDDC Objects: " + noDDC.size() + " - ");
	out.println("NoURN Objects: " + noUrn.size() + " - ");
	out.println("NoType Objects: " + noType.size() + " - ");
	out.println("Num of Types: " + types.size() + " - ");
	for (TypeObjectDictionary type : types) {
	    out.println(type.type + ": " + type.uris.size() + " - ");
	}
	out.close();
	message = strwrt.getBuffer().toString();
    }

    String getMessage() {
	return message;
    }

    void setMessage(String message) {
	this.message = message;
    }

    List<View> getAll() {
	return all;
    }

    void setAll(List<View> all) {
	this.all = all;
    }

    List<String> getRestricted() {
	return restricted;
    }

    void setRestricted(List<String> restricted) {
	this.restricted = restricted;
    }

    List<String> getUnrestricted() {
	return unrestricted;
    }

    void setUnrestricted(List<String> unrestricted) {
	this.unrestricted = unrestricted;
    }

    List<String> getNoRight() {
	return noRight;
    }

    void setNoRight(List<String> noRight) {
	this.noRight = noRight;
    }

    List<String> getNoDDC() {
	return noDDC;
    }

    void setNoDDC(List<String> noDDC) {
	this.noDDC = noDDC;
    }

    List<String> getNoUrn() {
	return noUrn;
    }

    void setNoUrn(List<String> noUrn) {
	this.noUrn = noUrn;
    }

    List<String> getNoYear() {
	return noYear;
    }

    void setNoYear(List<String> noYear) {
	this.noYear = noYear;
    }

    List<String> getNoTitle() {
	return noTitle;
    }

    void setNoTitle(List<String> noTitle) {
	this.noTitle = noTitle;
    }

    List<String> getNoCreator() {
	return noCreator;
    }

    void setNoCreator(List<String> noCreator) {
	this.noCreator = noCreator;
    }

    List<String> getNoType() {
	return noType;
    }

    void setNoType(List<String> noType) {
	this.noType = noType;
    }

    List<TypeObjectDictionary> getTypes() {
	return types;
    }

    void setTypes(List<TypeObjectDictionary> types) {
	this.types = types;
    }

}
