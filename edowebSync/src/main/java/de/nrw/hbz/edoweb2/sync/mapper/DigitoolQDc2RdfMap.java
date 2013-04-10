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
package de.nrw.hbz.edoweb2.sync.mapper;

import java.util.Collections;
import java.util.Hashtable;

/**
 * Class DigitoolQDc2RdfMap
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 24.06.2011
 * 
 */
@SuppressWarnings("javadoc")
public class DigitoolQDc2RdfMap
{

	// TERMS FROM ELEMENTS
	// Dublin Core XML
	public final static String xmlDcContributer = "dc:contributer";
	public final static String xmlDcCoverage = "dc:coverage";
	public final static String xmlDcCreator = "dc:creator";
	public final static String xmlDcDate = "dc:date";
	public final static String xmlDcDescription = "dc:description";
	public final static String xmlDcFormat = "dc:format";
	public final static String xmlDcIdentifier = "dc:identifier";
	public final static String xmlDcLanguage = "dc:language";
	public final static String xmlDcPublisher = "dc:publisher";
	public final static String xmlDcRelation = "dc:relation";
	public final static String xmlDcRights = "dc:rights";
	public final static String xmlDcSource = "dc:source";
	public final static String xmlDcSubject = "dc:subject";
	public final static String xmlDcTitle = "dc:title";
	public final static String xmlDcType = "dc:type";

	// Dublin Core RDF
	public final static String rdfDcContributer = "http://purl.org/dc/terms/contributer";
	public final static String rdfDcCoverage = "http://purl.org/dc/terms/coverage";
	public final static String rdfDcCreator = "http://purl.org/dc/terms/creator";
	public final static String rdfDcDate = "http://purl.org/dc/terms/date";
	public final static String rdfDcDescription = "http://purl.org/dc/terms/description";
	public final static String rdfDcFormat = "http://purl.org/dc/terms/format";
	public final static String rdfDcIdentifier = "http://purl.org/dc/terms/identifier";
	public final static String rdfDcLanguage = "http://purl.org/dc/terms/language";
	public final static String rdfDcPublisher = "http://purl.org/dc/terms/publisher";
	public final static String rdfDcRelation = "http://purl.org/dc/terms/relation";
	public final static String rdfDcRights = "http://purl.org/dc/terms/rights";
	public final static String rdfDcSource = "http://purl.org/dc/terms/source";
	public final static String rdfDcSubject = "http://purl.org/dc/terms/subject";
	public final static String rdfDcTitle = "http://purl.org/dc/terms/title";
	public final static String rdfDcType = "http://purl.org/dc/terms/type";
	public final static String rdfDcIsPartOf = "http://purl.org/dc/terms/isPartOf";

	// REAL TERMS
	public final static String alternative = "http://purl.org/dc/terms/alternative";
	public final static String tableOfContents = "http://purl.org/dc/terms/tableOfContents";
	public final static String Abstract = "http://purl.org/dc/terms/abstract";
	public final static String created = "http://purl.org/dc/terms/created";
	public final static String valid = "http://purl.org/dc/terms/valid";
	public final static String available = "http://purl.org/dc/terms/available";
	public final static String issued = "http://purl.org/dc/terms/issued";
	public final static String modified = "http://purl.org/dc/terms/modified";
	public final static String extent = "http://purl.org/dc/terms/extent";
	public final static String medium = "http://purl.org/dc/terms/medium";
	public final static String isVersionOf = "http://purl.org/dc/terms/isVersionOf";
	public final static String hasVersion = "http://purl.org/dc/terms/hasVersion";
	public final static String isReplacedBy = "http://purl.org/dc/terms/isReplacedBy";
	public final static String replaces = "http://purl.org/dc/terms/replaces";
	public final static String isRequiredBy = "http://purl.org/dc/terms/isRequiredBy";
	public final static String requires = "http://purl.org/dc/terms/requires";
	public final static String isPartOf = "http://purl.org/dc/terms/isPartOf";
	public final static String hasPart = "http://purl.org/dc/terms/hasPart";
	public final static String isReferencedBy = "http://purl.org/dc/terms/isReferencedBy";
	public final static String references = "http://purl.org/dc/terms/references";
	public final static String isFormatOf = "http://purl.org/dc/terms/isFormatOf";
	public final static String hasFormat = "http://purl.org/dc/terms/hasFormat";
	public final static String conformsTo = "http://purl.org/dc/terms/conformsTo";
	public final static String spatial = "http://purl.org/dc/terms/spatial";
	public final static String temporal = "http://purl.org/dc/terms/temporal";
	public final static String mediator = "http://purl.org/dc/terms/mediator";
	public final static String dateAccepted = "http://purl.org/dc/terms/dateAccepted";
	public final static String dateCopyrighted = "http://purl.org/dc/terms/dateCopyrighted";
	public final static String dateSubmitted = "http://purl.org/dc/terms/dateSubmitted";
	public final static String educationLevel = "http://purl.org/dc/terms/educationLevel";
	public final static String accessRights = "http://purl.org/dc/terms/accessRights";
	public final static String bibliographicCitation = "http://purl.org/dc/terms/bibliographicCitation";
	public final static String license = "http://purl.org/dc/terms/license";
	public final static String rightsHolder = "http://purl.org/dc/terms/rightsHolder";
	public final static String provenance = "http://purl.org/dc/terms/provenance";
	public final static String instructionalMethod = "http://purl.org/dc/terms/instructionalMethod";
	public final static String accrualMethod = "http://purl.org/dc/terms/accrualMethod";
	public final static String accrualPeriodicity = "http://purl.org/dc/terms/accrualPeriodicity";
	public final static String accrualPolicy = "http://purl.org/dc/terms/accrualPolicy";
	public final static String Agent = "http://purl.org/dc/terms/Agent";
	public final static String AgentClass = "http://purl.org/dc/terms/AgentClass";
	public final static String BibliographicResource = "http://purl.org/dc/terms/BibliographicResource";
	public final static String FileFormat = "http://purl.org/dc/terms/FileFormat";
	public final static String Frequency = "http://purl.org/dc/terms/Frequency";
	public final static String Jurisdiction = "http://purl.org/dc/terms/Jurisdiction";
	public final static String LicenseDocument = "http://purl.org/dc/terms/LicenseDocument";
	public final static String LinguisticSystem = "http://purl.org/dc/terms/LinguisticSystem";
	public final static String Location = "http://purl.org/dc/terms/Location";
	public final static String LocationPeriodOrJurisdiction = "http://purl.org/dc/terms/LocationPeriodOrJurisdiction";
	public final static String MediaType = "http://purl.org/dc/terms/MediaType";
	public final static String MediaTypeOrExtent = "http://purl.org/dc/terms/MediaTypeOrExtent";
	public final static String MethodOfInstruction = "http://purl.org/dc/terms/MethodOfInstruction";
	public final static String MethodOfAccrual = "http://purl.org/dc/terms/MethodOfAccrual";
	public final static String PeriodOfTime = "http://purl.org/dc/terms/PeriodOfTime";
	public final static String PhysicalMedium = "http://purl.org/dc/terms/PhysicalMedium";
	public final static String PhysicalResource = "http://purl.org/dc/terms/PhysicalResource";
	public final static String Policy = "http://purl.org/dc/terms/Policy";
	public final static String ProvenanceStatement = "http://purl.org/dc/terms/ProvenanceStatement";
	public final static String RightsStatement = "http://purl.org/dc/terms/RightsStatement";
	public final static String SizeOrDuration = "http://purl.org/dc/terms/SizeOrDuration";
	public final static String Standard = "http://purl.org/dc/terms/Standard";
	public final static String ISO639Minus2 = "http://purl.org/dc/terms/ISO639-2";
	public final static String RFC1766 = "http://purl.org/dc/terms/RFC1766";
	public final static String URI = "http://purl.org/dc/terms/URI";
	public final static String Point = "http://purl.org/dc/terms/Point";
	public final static String ISO3166 = "http://purl.org/dc/terms/ISO3166";
	public final static String Box = "http://purl.org/dc/terms/Box";
	public final static String Period = "http://purl.org/dc/terms/Period";
	public final static String W3CDTF = "http://purl.org/dc/terms/W3CDTF";
	public final static String RFC3066 = "http://purl.org/dc/terms/RFC3066";
	public final static String RFC5646 = "http://purl.org/dc/terms/RFC5646";
	public final static String RFC4646 = "http://purl.org/dc/terms/RFC4646";
	public final static String ISO639Minus3 = "http://purl.org/dc/terms/ISO639-3";
	public final static String LCSH = "http://purl.org/dc/terms/LCSH";
	public final static String MESH = "http://purl.org/dc/terms/MESH";
	public final static String DDC = "http://purl.org/dc/terms/DDC";
	public final static String LCC = "http://purl.org/dc/terms/LCC";
	public final static String UDC = "http://purl.org/dc/terms/UDC";
	public final static String DCMIType = "http://purl.org/dc/terms/DCMIType";
	public final static String IMT = "http://purl.org/dc/terms/IMT";
	public final static String TGN = "http://purl.org/dc/terms/TGN";
	public final static String NLM = "http://purl.org/dc/terms/NLM";

	public final static String xmlMedium = "dcterms:medium";
	public final static String xmlExtent = "dcterms:extent";
	public final static String xmlIsPartOf = "dcterms:isPartOf";
	public final static String xmlAbstract = "dcterms:abstract";
	public final static String xmlIssued = "dcterms:issued";
	public final static String xmlAlternative = "dcterms:alternative";
	Hashtable<String, String> dtl2rdf = null;

	// Qualified Dublin Core

	public DigitoolQDc2RdfMap()
	{
		dtl2rdf = new Hashtable<String, String>();
		dtl2rdf.put(medium, xmlMedium);
		dtl2rdf.put(extent, xmlExtent);
		dtl2rdf.put(isPartOf, xmlIsPartOf);
		dtl2rdf.put(Abstract, xmlAbstract);
		dtl2rdf.put(issued, xmlIssued);
		dtl2rdf.put(alternative, xmlAlternative);
	}

	public String get(String key)
	{
		return dtl2rdf.get(key);
	}

	public Object[] getTagNames()
	{
		return Collections.list(dtl2rdf.keys()).toArray();
	}
}
