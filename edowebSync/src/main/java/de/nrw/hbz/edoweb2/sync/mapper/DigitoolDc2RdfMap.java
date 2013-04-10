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
 * Class DTL2RDFMap
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 20.06.2011
 * 
 */
@SuppressWarnings("javadoc")
public class DigitoolDc2RdfMap
{
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
	public final static String rdfDcContributer = "http://purl.org/dc/elements/1.1/contributer";
	public final static String rdfDcCoverage = "http://purl.org/dc/elements/1.1/coverage";
	public final static String rdfDcCreator = "http://purl.org/dc/elements/1.1/creator";
	public final static String rdfDcDate = "http://purl.org/dc/elements/1.1/date";
	public final static String rdfDcDescription = "http://purl.org/dc/elements/1.1/description";
	public final static String rdfDcFormat = "http://purl.org/dc/elements/1.1/format";
	public final static String rdfDcIdentifier = "http://purl.org/dc/elements/1.1/identifier";
	public final static String rdfDcLanguage = "http://purl.org/dc/elements/1.1/language";
	public final static String rdfDcPublisher = "http://purl.org/dc/elements/1.1/publisher";
	public final static String rdfDcRelation = "http://purl.org/dc/elements/1.1/relation";
	public final static String rdfDcRights = "http://purl.org/dc/elements/1.1/rights";
	public final static String rdfDcSource = "http://purl.org/dc/elements/1.1/source";
	public final static String rdfDcSubject = "http://purl.org/dc/elements/1.1/subject";
	public final static String rdfDcTitle = "http://purl.org/dc/elements/1.1/title";
	public final static String rdfDcType = "http://purl.org/dc/elements/1.1/type";

	Hashtable<String, String> dtl2rdf = null;

	// Qualified Dublin Core

	public DigitoolDc2RdfMap()
	{
		dtl2rdf = new Hashtable<String, String>();
		dtl2rdf.put(xmlDcContributer, rdfDcContributer);
		dtl2rdf.put(xmlDcCoverage, rdfDcCoverage);
		dtl2rdf.put(xmlDcCreator, rdfDcCreator);
		dtl2rdf.put(xmlDcDate, rdfDcDate);
		dtl2rdf.put(xmlDcDescription, rdfDcDescription);
		dtl2rdf.put(xmlDcFormat, rdfDcFormat);
		dtl2rdf.put(xmlDcIdentifier, rdfDcIdentifier);
		dtl2rdf.put(xmlDcLanguage, rdfDcLanguage);
		dtl2rdf.put(xmlDcPublisher, rdfDcPublisher);
		dtl2rdf.put(xmlDcRelation, rdfDcRelation);
		dtl2rdf.put(xmlDcRights, rdfDcRights);
		dtl2rdf.put(xmlDcSource, rdfDcSource);
		dtl2rdf.put(xmlDcSubject, rdfDcSubject);
		dtl2rdf.put(xmlDcTitle, rdfDcTitle);
		dtl2rdf.put(xmlDcType, rdfDcType);
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
