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

/**
 * Fedora Vocabulary encapsulates some fedora specific names those are used for
 * data modelling and data retrieval.
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public abstract class FedoraVocabulary {
    /**
     * The Fedora 3.3 Relationship Vocabulary
     */
    // TODO Design issue
    /**
     * The namespace of all relations used for default fedora objects: See:
     * https://wiki.duraspace.org/display/FEDORA34/Fedora+Digital+Object+Model
     */
    public final static String INFO_NAMESPACE = "info:fedora/";// "http://127.0.0.1:8080/fedora/objects/";

    /**
     * The namespace of all fedora relations. See:
     * http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String FEDORA_RELS_NAMESPACE = "info:fedora/fedora-system:def/relations-external";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String IS_PART_OF = FEDORA_RELS_NAMESPACE + "#"
	    + "isPartOf";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String HAS_PART = FEDORA_RELS_NAMESPACE + "#"
	    + "hasPart";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String IS_CONSTITUENT_OF = FEDORA_RELS_NAMESPACE + "#"
	    + "isConstituentOf";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String HAS_CONSTITUENT = FEDORA_RELS_NAMESPACE + "#"
	    + "hasConstituent";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String IS_MEMBER_OF = FEDORA_RELS_NAMESPACE + "#"
	    + "isMemberOf";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String ITEM_ID = "http://www.openarchives.org/OAI/2.0/itemID";

    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String HAS_MEMBER = FEDORA_RELS_NAMESPACE + "#"
	    + "hasMember";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String IS_SUBSET_OF = FEDORA_RELS_NAMESPACE + "#"
	    + "isSubsetOf";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String HAS_SUBSET = FEDORA_RELS_NAMESPACE + "#"
	    + "hasSubset";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String IS_MEMBER_OF_COLLECTION = FEDORA_RELS_NAMESPACE
	    + "#" + "isMemberOfCollection";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String HAS_COLLECTION_MEMBER = FEDORA_RELS_NAMESPACE
	    + "#" + "hasCollectionMember";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String IS_DERIVATION_OF = FEDORA_RELS_NAMESPACE + "#"
	    + "isDerivationOf";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String HAS_DERIVATION = FEDORA_RELS_NAMESPACE + "#"
	    + "hasDerivation";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String IS_DEPENDENT_OF = FEDORA_RELS_NAMESPACE + "#"
	    + "isDependentOf";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String HAS_DEPENDENT = FEDORA_RELS_NAMESPACE + "#"
	    + "hasDependent";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String IS_DESCRIPTION_OF = FEDORA_RELS_NAMESPACE + "#"
	    + "isDescriptionOf";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String HAS_DESCRIPTION = FEDORA_RELS_NAMESPACE + "#"
	    + "isMetadataFor";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String HAS_METADATA = FEDORA_RELS_NAMESPACE + "#"
	    + "HasMetadata";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String IS_ANNOTATION_OF = FEDORA_RELS_NAMESPACE + "#"
	    + "isAnnotationOf";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String HAS_ANNOTATION = FEDORA_RELS_NAMESPACE + "#"
	    + "HasAnnotation";
    /**
     * See: http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs
     */
    public static final String HAS_EQUIVALENT = FEDORA_RELS_NAMESPACE + "#"
	    + "hasEquivalent";

    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA34/Fedora+Digital+Object+Model
     */
    public static final String HAS_MODEL = "info:fedora/fedora-system:def/model#hasModel";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA34/Fedora+Digital+Object+Model
     */
    public final static String REL_HAS_MODEL = INFO_NAMESPACE
	    + "fedora-system:def/model#hasModel";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA34/Fedora+Digital+Object+Model
     */
    public final static String REL_HAS_SERVICE = INFO_NAMESPACE
	    + "fedora-system:def/model#hasService";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA34/Fedora+Digital+Object+Model
     */
    public final static String REL_IS_DEPLOYMENT_OF = INFO_NAMESPACE
	    + "fedora-system:def/model#isDeploymentOf";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA34/Fedora+Digital+Object+Model
     */
    public final static String REL_IS_CONTRACTOR_OF = INFO_NAMESPACE
	    + "fedora-system:def/model#isContractorOf";

    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String DS_COMPOSITE_MODEL = "DS-COMPOSITE-MODEL";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String DS_COMPOSITE_MODEL_URI = INFO_NAMESPACE
	    + "fedora-system:FedoraDSCompositeModel-1.0";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String DS_METHODMAP = "METHODMAP";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String DS_METHODMAP_URI = INFO_NAMESPACE
	    + "fedora-system:FedoraSDefMethodMap-1.0";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String DS_METHODMAP_WSDL = "METHODMAP";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String DS_METHODMAP_WSDL_URI = INFO_NAMESPACE
	    + "fedora-system:FedoraSDepMethodMap-1.1";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String DS_INPUTSPEC = "DSINPUTSPEC";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String DS_INPUTSPEC_URI = INFO_NAMESPACE
	    + "fedora-system:FedoraDSInputSpec-1.1";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String DS_WSDL = "WSDL";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String DS_WSDL_URI = "http://schemas.xmlsoap.org/wsdl/";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String CM_CONTENTMODEL = "fedora-system:ContentModel-3.0";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String SDEF_CONTENTMODEL = "fedora-system:ServiceDefinition-3.0";
    /**
     * See:
     * https://wiki.duraspace.org/display/FEDORA36/Content+Model+Architecture
     */
    public static final String SDEP_CONTENTMODEL = "fedora-system:ServiceDeployment-3.0";

    /*
     * RISearch Vocabulary
     */
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String TYPE = "type";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String LANGUAGE = "lang";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String FORMAT = "format";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String LIMIT = "limit";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String DISTINCT = "distinct";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String STREAM = "stream";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String QUERY = "query";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String TEMPLATE = "template";

    // Values
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String TUPLES = "tuples";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String TRIPLES = "triples";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String ITQL = "iTQL";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String RDQL = "RDQL";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String SPO = "spo";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String CSV = "CSV";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String SIMPLE = "Simple";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String SPARQL = "sparql";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String TSV = "TSV";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String N_TRIPLES = "N-Triples";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String N3 = "N3";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String RDF_XML = "RDF/XML";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String TURTLE = "Turtle";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String ON = "on";
    /**
     * See: https://wiki.duraspace.org/display/FEDORA36/Resource+Index+Search
     */
    public static final String OFF = "off";

}
