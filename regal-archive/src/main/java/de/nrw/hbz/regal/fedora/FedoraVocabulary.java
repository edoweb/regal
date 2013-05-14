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
package de.nrw.hbz.regal.fedora;

public abstract class FedoraVocabulary
{

	/**
	 * The Fedora 3.3 Relationship Vokabulary
	 */
	// TODO Design issue
	public final static String INFO_NAMESPACE = "info:fedora/";// "http://127.0.0.1:8080/fedora/objects/";

	public static final String FEDORA_RELS_NAMESPACE = "info:fedora/fedora-system:def/relations-external";

	public static final String IS_PART_OF = FEDORA_RELS_NAMESPACE + "#"
			+ "isPartOf";
	public static final String HAS_PART = FEDORA_RELS_NAMESPACE + "#"
			+ "hasPart";
	public static final String IS_CONSTITUENT_OF = FEDORA_RELS_NAMESPACE + "#"
			+ "isConstituentOf";
	public static final String HAS_CONSTITUENT = FEDORA_RELS_NAMESPACE + "#"
			+ "hasConstituent";
	public static final String IS_MEMBER_OF = FEDORA_RELS_NAMESPACE + "#"
			+ "isMemberOf";

	public static final String ITEM_ID = "http://www.openarchives.org/OAI/2.0/itemID";

	public static final String HAS_MEMBER = FEDORA_RELS_NAMESPACE + "#"
			+ "hasMember";
	public static final String IS_SUBSET_OF = FEDORA_RELS_NAMESPACE + "#"
			+ "isSubsetOf";
	public static final String HAS_SUBSET = FEDORA_RELS_NAMESPACE + "#"
			+ "hasSubset";
	public static final String IS_MEMBER_OF_COLLECTION = FEDORA_RELS_NAMESPACE
			+ "#" + "isMemberOfCollection";
	public static final String HAS_COLLECTION_MEMBER = FEDORA_RELS_NAMESPACE
			+ "#" + "hasCollectionMember";
	public static final String IS_DERIVATION_OF = FEDORA_RELS_NAMESPACE + "#"
			+ "isDerivationOf";
	public static final String HAS_DERIVATION = FEDORA_RELS_NAMESPACE + "#"
			+ "hasDerivation";
	public static final String IS_DEPENDENT_OF = FEDORA_RELS_NAMESPACE + "#"
			+ "isDependentOf";
	public static final String HAS_DEPENDENT = FEDORA_RELS_NAMESPACE + "#"
			+ "hasDependent";
	public static final String IS_DESCRIPTION_OF = FEDORA_RELS_NAMESPACE + "#"
			+ "isDescriptionOf";
	public static final String HAS_DESCRIPTION = FEDORA_RELS_NAMESPACE + "#"
			+ "isMetadataFor";
	public static final String HAS_METADATA = FEDORA_RELS_NAMESPACE + "#"
			+ "HasMetadata";
	public static final String IS_ANNOTATION_OF = FEDORA_RELS_NAMESPACE + "#"
			+ "isAnnotationOf";
	public static final String HAS_ANNOTATION = FEDORA_RELS_NAMESPACE + "#"
			+ "HasAnnotation";
	public static final String HAS_EQUIVALENT = FEDORA_RELS_NAMESPACE + "#"
			+ "hasEquivalent";
	public static final String HAS_MODEL = "info:fedora/fedora-system:def/model#hasModel";

	public final static String REL_HAS_MODEL = INFO_NAMESPACE
			+ "fedora-system:def/model#hasModel";
	public final static String REL_HAS_SERVICE = INFO_NAMESPACE
			+ "fedora-system:def/model#hasService";
	public final static String REL_IS_DEPLOYMENT_OF = INFO_NAMESPACE
			+ "fedora-system:def/model#isDeploymentOf";
	public final static String REL_IS_CONTRACTOR_OF = INFO_NAMESPACE
			+ "fedora-system:def/model#isContractorOf";

	public static final String DS_COMPOSITE_MODEL = "DS-COMPOSITE-MODEL";
	public static final String DS_COMPOSITE_MODEL_URI = INFO_NAMESPACE
			+ "fedora-system:FedoraDSCompositeModel-1.0";
	public static final String DS_METHODMAP = "METHODMAP";
	public static final String DS_METHODMAP_URI = INFO_NAMESPACE
			+ "fedora-system:FedoraSDefMethodMap-1.0";
	public static final String DS_METHODMAP_WSDL = "METHODMAP";
	public static final String DS_METHODMAP_WSDL_URI = INFO_NAMESPACE
			+ "fedora-system:FedoraSDepMethodMap-1.1";
	public static final String DS_INPUTSPEC = "DSINPUTSPEC";
	public static final String DS_INPUTSPEC_URI = INFO_NAMESPACE
			+ "fedora-system:FedoraDSInputSpec-1.1";
	public static final String DS_WSDL = "WSDL";
	public static final String DS_WSDL_URI = "http://schemas.xmlsoap.org/wsdl/";
	public static final String CM_CONTENTMODEL = "fedora-system:ContentModel-3.0";
	public static final String SDEF_CONTENTMODEL = "fedora-system:ServiceDefinition-3.0";
	public static final String SDEP_CONTENTMODEL = "fedora-system:ServiceDeployment-3.0";

	/**
	 * Other
	 */

	public static final String PID = "pid";
	public static final String ID = "id";
	public static final String SUBJECT = "subject";
	public static final String PREDICATE = "predicate";
	public static final String OBJECT = "object";

	public static final String[] RESULT_FIELDS = { "pid", "label", "state",
			"ownerId", "cDate", "mDate", "dcmDate", "title", "creator",
			"subject", "description", "publisher", "contributor", "date",
			"type", "format", "identifier", "source", "language", "relation",
			"coverage", "rights" };

	/**
	 * Spesific Datastream Identifier
	 */

	public static final String QDC = "QDC";
	public static final String DIPP_EXT = "DiPPExt";
	public static final String RELS_EXT = "RELS-EXT";
	public static final String DC = "DC";
	public static final String OAI_EPICUR = "oai_epicur";
	public static final String OAI_DOAJ = "oai_doaj";
	public static final String DIPP_ADM = "DiPPAdm";
	public static final String OAI_DC = "DiPPAdm";

	/**
	 * RISearch Vocabulary
	 */
	// Keys
	public static final String BASE_URL = "baseURL";
	public static final String TYPE = "type";
	public static final String LANGUAGE = "lang";
	public static final String FORMAT = "format";
	public static final String LIMIT = "limit";
	public static final String DISTINCT = "distinct";
	public static final String STREAM = "stream";
	public static final String QUERY = "query";
	public static final String TEMPLATE = "template";

	// Values
	public static final String TUPLES = "tuples";
	public static final String TRIPLES = "triples";
	public static final String ITQL = "iTQL";
	public static final String RDQL = "RDQL";
	public static final String SPO = "spo";
	public static final String CSV = "CSV";
	public static final String SIMPLE = "Simple";
	public static final String SPARQL = "sparql";
	public static final String TSV = "TSV";
	public static final String N_TRIPLES = "N-Triples";
	public static final String N3 = "N3";
	public static final String RDF_XML = "RDF/XML";
	public static final String TURTLE = "Turtle";
	public static final String ON = "on";
	public static final String OFF = "off";

}
