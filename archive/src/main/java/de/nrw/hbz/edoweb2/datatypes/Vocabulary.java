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
package de.nrw.hbz.edoweb2.datatypes;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public abstract class Vocabulary
{

	// TODO Design issue
	public final static String FEDORA_INFO_NAMESPACE = "info:fedora/";// "http://127.0.0.1:8080/fedora/objects/";

	public final static String HBZ_MODEL_NAMESPACE = "info:hbz/hbz-ingest:def/model#";

	public final static String REL_BELONGS_TO_OBJECT = HBZ_MODEL_NAMESPACE
			+ "belongsToObject";
	public final static String REL_IS_RELATED = HBZ_MODEL_NAMESPACE
			+ "isRelated";

	public final static String REL_HAS_CONCEPT = HBZ_MODEL_NAMESPACE
			+ "hasConcept";

	public final static String REL_IS_CONCEPT_OF = HBZ_MODEL_NAMESPACE
			+ "isConceptOf";

	public final static String REL_HAS_REALISATION = HBZ_MODEL_NAMESPACE
			+ "hasRealisation";

	public final static String REL_IS_REALISATION_OF = HBZ_MODEL_NAMESPACE
			+ "isRealisationOf";

	public final static String REL_HAS_REPRESENTATION = HBZ_MODEL_NAMESPACE
			+ "hasRepresentation";

	public final static String REL_IS_REPRESENTATION_OF = HBZ_MODEL_NAMESPACE
			+ "isRepresentationOf";

	public final static String REL_IS_NODE_TYPE = HBZ_MODEL_NAMESPACE
			+ "isNodeType";

	public final static String REL_IS_IN_NAMESPACE = HBZ_MODEL_NAMESPACE
			+ "isInNamespace";
	public final static String REL_ORIGINAL_OBJECT_ID = HBZ_MODEL_NAMESPACE
			+ "originalObjectId";

	public final static String REL_ORIGINAL_DATASTREAM_ID = HBZ_MODEL_NAMESPACE
			+ "originalDatastreamId";

	public final static String REL_THUMBNAIL_URL = HBZ_MODEL_NAMESPACE
			+ "thumbnailUrl";
	public final static String REL_FULLTEXT_URL = HBZ_MODEL_NAMESPACE
			+ "fulltextUrl";

	public final static String REL_OCR_URL = HBZ_MODEL_NAMESPACE + "ocrUrl";

	public final static String REL_TOC_URL = HBZ_MODEL_NAMESPACE + "tocUrl";

	public final static String REL_XML_URL = HBZ_MODEL_NAMESPACE + "xmlUrl";

	public final static String REL_REPRESENTATION_TYPE = HBZ_MODEL_NAMESPACE
			+ "representationType";

	public final static String REL_CONTENT_TYPE = HBZ_MODEL_NAMESPACE
			+ "contentType";

	public final static String TYPE_THUMBNAIL = HBZ_MODEL_NAMESPACE
			+ "thumbnail";

	public final static String TYPE_FULLTEXT = HBZ_MODEL_NAMESPACE + "fulltext";

	public final static String TYPE_OCR = HBZ_MODEL_NAMESPACE + "ocr";

	public final static String TYPE_TOC = HBZ_MODEL_NAMESPACE + "toc";

	public final static String TYPE_XML = HBZ_MODEL_NAMESPACE + "toc";

	public final static String TYPE_NODE = HBZ_MODEL_NAMESPACE + "HBZ_NODE";

	public final static String TYPE_OBJECT = HBZ_MODEL_NAMESPACE + "HBZ_OBJECT";

	public final static String TYPE_CONCEPT = HBZ_MODEL_NAMESPACE
			+ "HBZ_CONCEPT";

	public final static String TYPE_REALISATION = HBZ_MODEL_NAMESPACE
			+ "HBZ_REALISATION";

	public final static String TYPE_REPRESENTATION = HBZ_MODEL_NAMESPACE
			+ "HBZ_REPRESENTATION";

	public static final String HAS_DATASTREAM = HBZ_MODEL_NAMESPACE + "data";

	public static final String DATASTREAM_MIME = HBZ_MODEL_NAMESPACE
			+ "mimeType";

	public static final String HAS_METADATASTREAM = HBZ_MODEL_NAMESPACE
			+ "metadata";

	public static final String METADATASTREAM_MIME = HBZ_MODEL_NAMESPACE
			+ "metaDataMimeType";

}
