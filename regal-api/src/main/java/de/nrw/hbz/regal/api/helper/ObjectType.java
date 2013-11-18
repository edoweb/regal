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
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public enum ObjectType {
    /**
     * A Monograph consists of one node with one datastream, and one metadata
     * stream. To generate a multiple file monograph the file and supplement
     * resource types can be used.
     */
    monograph,
    /**
     * A Journal consists of volumes, issues and articles
     */
    journal,
    /**
     * A volume consists of issues and articles
     * 
     */
    volume,
    /**
     * A issue can contain articles.
     * 
     */
    issue,
    /**
     * A article consist of one node with one datastream, and one metadata
     * stream.To generate a multiple file articles the file and supplement
     * resource types can be used.
     * 
     */
    article,
    /**
     * A webpage consists of versions
     */
    webpage,
    /**
     * A version consists of one node with one datastream and one
     * metadatastream.
     * 
     */
    version,
    /**
     * A file resource can be attached to each of the above resource types
     * 
     */
    file,
    /**
     * A supplement resource can be attached to each of the above resource
     * types.
     */
    supplement,
    /**
     * A transformer object exists only once in repository. Other objects can be
     * linked to transformer objects in order to provide conversion services on
     * fedora level.
     */
    transformer

}
