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

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public abstract class Vocabulary {

    /**
     * Self developed vocabularies
     */
    public final static String HBZ_MODEL_NAMESPACE = "info:hbz/hbz-ingest:def/model#";

    /**
     * A Nodetype describes the role of the node from the perspective of the
     * general datamodel
     */
    public final static String REL_IS_NODE_TYPE = HBZ_MODEL_NAMESPACE
	    + "isNodeType";
    /**
     * A contentType is bound to certain characteristics regarding the content
     * of the node
     */
    public final static String REL_CONTENT_TYPE = HBZ_MODEL_NAMESPACE
	    + "contentType";
    /**
     * A default type
     */
    public final static String TYPE_NODE = HBZ_MODEL_NAMESPACE + "HBZ_NODE";

    /**
     * A default type
     */
    public final static String TYPE_OBJECT = HBZ_MODEL_NAMESPACE + "HBZ_OBJECT";
}
