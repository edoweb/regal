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
 * A Simple Bean to list resources.
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@XmlRootElement
public class ObjectList {
    List<String> list = null;

    /**
     * Default constructor creates an empty list.
     */
    public ObjectList() {
	list = new Vector<String>();
    }

    /**
     * @param v
     *            initialises with the passed list
     */
    public ObjectList(List<String> v) {
	list = v;
    }

    /**
     * @return returns the list
     */
    public List<String> getList() {
	return list;
    }

    /**
     * @param list
     *            sets the list
     */
    public void setList(List<String> list) {
	this.list = list;
    }

}
