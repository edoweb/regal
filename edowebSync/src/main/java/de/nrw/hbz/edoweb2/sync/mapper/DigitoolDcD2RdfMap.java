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
 * Class DigitoolDcD2RdfMap
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
public class DigitoolDcD2RdfMap
{

	public final static String creatorPersonalName = "http://www.hbz-nrw.de/dtl/creatorPersonalName";
	public final static String contributerPersonalName = "http://www.hbz-nrw.de/dtl/contributerPersonalName";
	public final static String statementOfResponsibility = "http://www.hbz-nrw.de/dtl/statementOfResponsibility";
	public final static String extent = "http://www.hbz-nrw.de/dtl/extent";
	public final static String publisherThesisNote = "http://www.hbz-nrw.de/dtl/publisherThesisNote";
	public final static String placeOfPublicationUniversity = "http://www.hbz-nrw.de/dtl/placeOfPublicationUniversity";
	public final static String placeOfPublication = "http://www.hbz-nrw.de/dtl/placeOfPublication";
	public final static String furtherTitle = "http://www.hbz-nrw.de/dtl/furtherTitle";
	public final static String creatorDateOfBirth = "http://www.hbz-nrw.de/dtl/creatorDateOfBirth";
	public final static String creatorPlaceOfBirth = "http://www.hbz-nrw.de/dtl/creatorPlaceOfBirth";
	public final static String tableOfContents = "http://www.hbz-nrw.de/dtl/tableOfContents";
	public final static String publisherUniversityType = "http://www.hbz-nrw.de/dtl/publisherUniversityType";
	public final static String publisherAddress = "http://www.hbz-nrw.de/dtl/publisherAddress";
	public final static String contributorInstitute = "http://www.hbz-nrw.de/dtl/contributorInstitute";
	public final static String contributorDepartment = "http://www.hbz-nrw.de/dtl/contributorDepartment";
	public final static String contributorAdvisor = "http://www.hbz-nrw.de/dtl/contributorAdvisor";
	public final static String contributorUniversity = "http://www.hbz-nrw.de/dtl/contributorUniversity";
	public final static String dateAccepted = "http://www.hbz-nrw.de/dtl/dateAccepted";

	public final static String xmlCreatorPersonalName = "dcterms:creatorPersonalName";
	public final static String xmlContributorPersonalName = "dcterms:contributorPersonalName";
	public final static String xmlStatementOfResponsibility = "dcterms:statementOfResponsibility";
	public final static String xmlExtent = "dcterms:extent";
	public final static String xmlPublisherThesisNote = "dcterms:publisherThesisNote";
	public final static String xmlPlaceOfPublicationUniversity = "dcterms:placeOfPublicationUniversity";
	public final static String xmlPlaceOfPublication = "dcterms:placeOfPublication";
	public final static String xmlfurtherTitle = "dcterms:furtherTitle";
	public final static String xmlplaceOfPublicationUniversity = "dcterms:placeOfPublicationUniversity";
	public final static String xmlcreatorDateOfBirth = "dcterms:creatorDateOfBirth";
	public final static String xmlcreatorPlaceOfBirth = "dcterms:creatorPlaceOfBirth";
	public final static String xmltableOfContents = "dcterms:tableOfContents";
	public final static String xmlpublisherUniversityType = "dcterms:publisherUniversityType";
	public final static String xmlpublisherAddress = "dcterms:publisherAddress";
	public final static String xmlcontributorInstitute = "dcterms:contributorInstitute";
	public final static String xmlcontributorDepartment = "dcterms:contributorDepartment";
	public final static String xmlcontributorAdvisor = "dcterms:contributorAdvisor";
	public final static String xmlcontributorUniversity = "dcterms:contributorUniversity";
	public final static String xmldateAccepted = "dcterms:dateAccepted";

	Hashtable<String, String> dtl2rdf = null;

	// Qualified Dublin Core

	public DigitoolDcD2RdfMap()
	{
		dtl2rdf = new Hashtable<String, String>();
		dtl2rdf.put(creatorPersonalName, xmlCreatorPersonalName);
		dtl2rdf.put(statementOfResponsibility, xmlStatementOfResponsibility);
		dtl2rdf.put(extent, xmlExtent);
		dtl2rdf.put(publisherThesisNote, xmlPublisherThesisNote);
		dtl2rdf.put(placeOfPublicationUniversity,
				xmlPlaceOfPublicationUniversity);
		dtl2rdf.put(placeOfPublication, xmlPlaceOfPublication);
		dtl2rdf.put(furtherTitle, xmlfurtherTitle);
		dtl2rdf.put(contributerPersonalName, xmlContributorPersonalName);
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
