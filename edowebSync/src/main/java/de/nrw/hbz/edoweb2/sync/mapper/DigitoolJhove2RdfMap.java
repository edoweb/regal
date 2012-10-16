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
 * Class DigitoolJhove2RdfMap
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 12.07.2011
 * 
 */
public class DigitoolJhove2RdfMap
{

	// Sets
	public final static String xmlRepInfoFormat = "//repInfo/format";
	public final static String xmlRepInfoSize = "//repInfo/size";
	public final static String xmlRepInfoVersion = "//repInfo/version";
	public final static String xmlRepInfoStatus = "//repInfo/status";
	public final static String xmlRepInfoSigMatch = "//repInfo/sigMatch/module";
	public final static String xmlRepInfoMimeType = "//repInfo/mimeType";
	public final static String xmlInfoTitle = "//property/values[../name/text()='Info']/property/values[../name/text()='Title']/value";
	public final static String xmlInfoAuthor = "//property/values[../name/text()='Info']/property/values[../name/text()='Author']/value";
	public final static String xmlInfoSubject = "//property/values[../name/text()='Info']/property/values[../name/text()='Subject']/value";
	public final static String xmlInfoKeywords = "//property/values[../name/text()='Info']/property/values[../name/text()='Keywords']/value";
	public final static String xmlInfoCreator = "//property/values[../name/text()='Info']/property/values[../name/text()='Creator']/value";
	public final static String xmlInfoProducer = "//property/values[../name/text()='Info']/property/values[../name/text()='Producer']/value";
	public final static String xmlInfoCreationDate = "//property/values[../name/text()='Info']/property/values[../name/text()='CreationDate']/value";
	public final static String xmlInfoModDate = "//property/values[../name/text()='Info']/property/values[../name/text()='ModDate']/value";

	public final static String xmlByteOrder = "//mix:ByteOrder";
	public final static String xmlImageMimeType = "//mix:MIMEType";
	public final static String xmlFileSize = "//mix:FileSize";
	public final static String xmlImageWidth = "//mix:ImageWidth";
	public final static String xmlImageLength = "//mix:ImageLength";

	// Single Value
	public final static String xmlNumberOfPages = "count(//property/name[text()='Page'])";
	public final static String xmlNumberOfImages = "count(//property/name[text()='Image'])";

	// Sets
	public final static String rdfRepInfoFormat = "http://www.hbz-nrw.de/jhove/info/format";
	public final static String rdfRepInfoSize = "http://www.hbz-nrw.de/jhove/info/size";
	public final static String rdfRepInfoVersion = "http://www.hbz-nrw.de/jhove/info/version";
	public final static String rdfRepInfoStatus = "http://www.hbz-nrw.de/jhove/info/status";
	public final static String rdfRepInfoSigMatch = "http://www.hbz-nrw.de/jhove/info/module";
	public final static String rdfRepInfoMimeType = "http://www.hbz-nrw.de/jhove/info/mimeType";
	public final static String rdfInfoTitle = "http://www.hbz-nrw.de/jhove/info/title";
	public final static String rdfInfoAuthor = "http://www.hbz-nrw.de/jhove/info/author";
	public final static String rdfInfoSubject = "http://www.hbz-nrw.de/jhove/info/subject";
	public final static String rdfInfoKeywords = "http://www.hbz-nrw.de/jhove/info/keywords";
	public final static String rdfInfoCreator = "http://www.hbz-nrw.de/jhove/info/creator";
	public final static String rdfInfoProducer = "http://www.hbz-nrw.de/jhove/info/producer";
	public final static String rdfInfoCreationDate = "http://www.hbz-nrw.de/jhove/info/creationDate";
	public final static String rdfInfoModDate = "http://www.hbz-nrw.de/jhove/info/modDate";

	public final static String rdfByteOrder = "http://www.loc.gov/mix/ByteOrder";
	public final static String rdfImageMimeType = "http://www.loc.gov/mix/MIMEType";
	public final static String rdfFileSize = "http://www.loc.gov/mix/FileSize";
	public final static String rdfImageWidth = "http://www.loc.gov/mix/ImageWidth";
	public final static String rdfImageLength = "http://www.loc.gov/mix/ImageLength";

	// Single Value
	public final static String rdfNumberOfPages = "http://www.hbz-nrw.de/jhove/info/numberOfPages";
	public final static String rdfNumberOfImages = "http://www.hbz-nrw.de/jhove/info/numberOfImages";

	Hashtable<String, String> dtl2rdfSets = null;
	Hashtable<String, String> dtl2rdfVals = null;

	public DigitoolJhove2RdfMap()
	{
		dtl2rdfSets = new Hashtable<String, String>();
		dtl2rdfVals = new Hashtable<String, String>();
		dtl2rdfSets.put(xmlRepInfoFormat, rdfRepInfoFormat);
		dtl2rdfSets.put(xmlRepInfoSize, rdfRepInfoSize);
		dtl2rdfSets.put(xmlRepInfoVersion, rdfRepInfoVersion);
		dtl2rdfSets.put(xmlRepInfoStatus, rdfRepInfoStatus);
		dtl2rdfSets.put(xmlRepInfoSigMatch, rdfRepInfoSigMatch);
		dtl2rdfSets.put(xmlRepInfoMimeType, rdfRepInfoMimeType);
		dtl2rdfSets.put(xmlInfoTitle, rdfInfoTitle);
		dtl2rdfSets.put(xmlInfoAuthor, rdfInfoAuthor);
		dtl2rdfSets.put(xmlInfoSubject, rdfInfoSubject);
		dtl2rdfSets.put(xmlInfoKeywords, rdfInfoKeywords);
		dtl2rdfSets.put(xmlInfoCreator, rdfInfoCreator);
		dtl2rdfSets.put(xmlInfoProducer, rdfInfoProducer);
		dtl2rdfSets.put(xmlInfoCreationDate, rdfInfoCreationDate);
		dtl2rdfSets.put(xmlInfoModDate, rdfInfoModDate);
		dtl2rdfVals.put(xmlNumberOfPages, rdfNumberOfPages);
		dtl2rdfVals.put(xmlNumberOfImages, rdfNumberOfImages);
		dtl2rdfVals.put(xmlByteOrder, rdfByteOrder);
		dtl2rdfVals.put(xmlImageMimeType, rdfImageMimeType);
		dtl2rdfVals.put(xmlFileSize, rdfFileSize);
		dtl2rdfVals.put(xmlImageWidth, rdfImageWidth);
		dtl2rdfVals.put(xmlImageLength, rdfImageLength);
	}

	public String getSetId(String key)
	{
		return dtl2rdfSets.get(key);
	}

	public Object[] getSetTagNames()
	{
		return Collections.list(dtl2rdfSets.keys()).toArray();
	}

	public String getValId(String key)
	{
		return dtl2rdfVals.get(key);
	}

	public Object[] getValTagNames()
	{
		return Collections.list(dtl2rdfVals.keys()).toArray();
	}

}
