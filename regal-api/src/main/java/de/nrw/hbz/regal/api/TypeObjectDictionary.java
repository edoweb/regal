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

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is used to list resources of a certain type.
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@XmlRootElement
public class TypeObjectDictionary {
    String type;
    List<String> uris;

    /**
     * creates an empty object.
     */
    public TypeObjectDictionary() {
	uris = new Vector<String>();
    }

    TypeObjectDictionary(String type, List<String> uris) {
	this.type = type;
	this.uris = uris;
    }

    /**
     * @return the type that is listed in the dicitonary
     */
    public String getType() {
	return type;
    }

    /**
     * @param type
     *            set the type
     */
    public void setType(String type) {
	this.type = type;
    }

    /**
     * @return get the Uris
     */
    public List<String> getUris() {
	return uris;
    }

    /**
     * @param uris
     *            set the uris
     */
    public void setUris(List<String> uris) {
	this.uris = uris;
    }

}
