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

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class OaiSet {
    String name = null;
    String spec = null;
    String pid = null;

    /**
     * @param name
     *            set name
     * @param spec
     *            set spec
     * @param pid
     *            set pid
     */
    public OaiSet(String name, String spec, String pid) {
	super();
	this.name = name;
	this.spec = spec;
	this.pid = pid;
    }

    /**
     * @return the set name
     */
    public String getName() {
	return name;
    }

    /**
     * @param name
     *            the set name
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @return the set spec
     */
    public String getSpec() {
	return spec;
    }

    /**
     * @param spec
     *            the set spec
     */
    public void setSpec(String spec) {
	this.spec = spec;
    }

    /**
     * @return the set pid
     */
    public String getPid() {
	return pid;
    }

    protected void setPid(String pid) {
	this.pid = pid;
    }

}
