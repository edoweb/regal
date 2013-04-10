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
package de.nrw.hbz.edoweb2.api;

import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class View
{
	String uri = null;

	Vector<String> title = null;
	Vector<String> creator = null;
	Vector<String> year = null;
	Vector<String> type = null;
	Vector<String> subject = null;
	Vector<String> description = null;
	Vector<String> ddc = null;
	Vector<String> language = null;
	Vector<String> location = null;
	Vector<String> publisher = null;
	Vector<String> isPartOf = null;
	Vector<String> isPartOfName = null;
	Vector<String> hasPart = null;
	Vector<String> hasPartName = null;
	Vector<String> medium = null;
	Vector<String> pid = null;
	Vector<String> doi = null;
	Vector<String> urn = null;
	Vector<String> url = null;
	Vector<String> alephid = null;
	Vector<String> rights = null;

	// TODO refactor names
	Vector<String> pdfUrl = null;
	Vector<String> zipUrl = null;
	Vector<String> thumbnailUrl = null;
	Vector<String> ocrUrl = null;

	// TODO make this configurable
	Vector<String> verbundUrl = null;
	Vector<String> dataciteUrl = null;
	Vector<String> lobidUrl = null;
	Vector<String> culturegraphUrl = null;
	Vector<String> baseUrl = null;
	Vector<String> digitoolUrl = null;
	Vector<String> cacheUrl = null;
	Vector<String> fedoraUrl = null;
	Vector<String> risearchUrl = null;
	Vector<String> message = null;

	// Vector<String> htmlUrl = null;
	// Vector<String> metsUrl = null;
	// Vector<String> oaioreUrl = null;
	// Vector<String> oaidcUrl = null;
	// Vector<String> xepicurUrl = null;
	// Vector<String> xmetadissplusUrl = null;
	// Vector<String> alephMarcUrl = null;
	// Vector<String> editorUrl = null;
	// Vector<String> oaiPmhUrl = null;
	// Vector<String> qdcUrl = null;
	// Vector<String> jsonUrl = null;

	View()
	{
		title = new Vector<String>();
		creator = new Vector<String>();
		year = new Vector<String>();
		type = new Vector<String>();
		subject = new Vector<String>();
		description = new Vector<String>();
		ddc = new Vector<String>();
		language = new Vector<String>();
		location = new Vector<String>();
		publisher = new Vector<String>();
		isPartOf = new Vector<String>();
		medium = new Vector<String>();
		pid = new Vector<String>();
		doi = new Vector<String>();
		urn = new Vector<String>();
		url = new Vector<String>();
		alephid = new Vector<String>();
		pdfUrl = new Vector<String>();
		thumbnailUrl = new Vector<String>();
		uri = new String();
		lobidUrl = new Vector<String>();
		culturegraphUrl = new Vector<String>();
		verbundUrl = new Vector<String>();
		ocrUrl = new Vector<String>();
		isPartOf = new Vector<String>();
		hasPart = new Vector<String>();
		dataciteUrl = new Vector<String>();
		baseUrl = new Vector<String>();
		zipUrl = new Vector<String>();
		digitoolUrl = new Vector<String>();
		cacheUrl = new Vector<String>();
		fedoraUrl = new Vector<String>();
		risearchUrl = new Vector<String>();
		rights = new Vector<String>();
		message = new Vector<String>();
		isPartOfName = new Vector<String>();
		hasPartName = new Vector<String>();
		// Not Implemented yet: First make Cool URIs!
		// metsUrl = new Vector<String>();
		// oaioreUrl = new Vector<String>();
		// oaidcUrl = new Vector<String>();
		// xepicurUrl = new Vector<String>();
		// xmetadissplusUrl = new Vector<String>();
		// alephMarcUrl = new Vector<String>();
		// editorUrl = new Vector<String>();
		// oaiPmhUrl = new Vector<String>();
		// qdcUrl = new Vector<String>();
		// htmlUrl = new Vector<String>();
		// jsonUrl = new Vector<String>();
	}

	// Vector<String> getQdcUrl()
	// {
	// return qdcUrl;
	// }
	//
	// void setQdcUrl(Vector<String> qdcUrl)
	// {
	// this.qdcUrl = qdcUrl;
	// }
	//
	// Vector<String> getMetsUrl()
	// {
	// return metsUrl;
	// }
	//
	// void setMetsUrl(Vector<String> metsUrl)
	// {
	// this.metsUrl = metsUrl;
	// }
	//
	// Vector<String> getOaioreUrl()
	// {
	// return oaioreUrl;
	// }
	//
	// void setOaioreUrl(Vector<String> oaioreUrl)
	// {
	// this.oaioreUrl = oaioreUrl;
	// }
	//
	// Vector<String> getOaidcUrl()
	// {
	// return oaidcUrl;
	// }
	//
	// void setOaidcUrl(Vector<String> oaidcUrl)
	// {
	// this.oaidcUrl = oaidcUrl;
	// }
	//
	// Vector<String> getXepicurUrl()
	// {
	// return xepicurUrl;
	// }
	//
	// void setXepicurUrl(Vector<String> xepicurUrl)
	// {
	// this.xepicurUrl = xepicurUrl;
	// }
	//
	// Vector<String> getXmetadissplusUrl()
	// {
	// return xmetadissplusUrl;
	// }
	//
	// void setXmetadissplusUrl(Vector<String> xmetadissplusUrl)
	// {
	// this.xmetadissplusUrl = xmetadissplusUrl;
	// }
	//
	// Vector<String> getAlephMarcUrl()
	// {
	// return alephMarcUrl;
	// }
	//
	// void setAlephMarcUrl(Vector<String> alephMarcUrl)
	// {
	// this.alephMarcUrl = alephMarcUrl;
	// }
	//
	// Vector<String> getEditorUrl()
	// {
	// return editorUrl;
	// }
	//
	// void setEditorUrl(Vector<String> editorUrl)
	// {
	// this.editorUrl = editorUrl;
	// }
	//
	// Vector<String> getOaiPmhUrl()
	// {
	// return oaiPmhUrl;
	// }
	//
	// void setOaiPmhUrl(Vector<String> oaiPmhUrl)
	// {
	// this.oaiPmhUrl = oaiPmhUrl;
	// }
	//
	// Vector<String> getVerbundUrl()
	// {
	// return verbundUrl;
	// }

	Vector<String> getIsPartOfName()
	{
		return isPartOfName;
	}

	void setIsPartOfName(Vector<String> isPartOfName)
	{
		this.isPartOfName = isPartOfName;
	}

	Vector<String> getHasPartName()
	{
		return hasPartName;
	}

	void setHasPartName(Vector<String> hasPart)
	{
		this.hasPartName = hasPart;
	}

	Vector<String> getDescription()
	{
		return description;
	}

	void setDescription(Vector<String> description)
	{
		this.description = description;
	}

	Vector<String> getRights()
	{
		return rights;
	}

	void setRights(Vector<String> rights)
	{
		this.rights = rights;
	}

	Vector<String> getDigitoolUrl()
	{
		return digitoolUrl;
	}

	void setDigitoolUrl(Vector<String> digitoolUrl)
	{
		this.digitoolUrl = digitoolUrl;
	}

	Vector<String> getCacheUrl()
	{
		return cacheUrl;
	}

	void setCacheUrl(Vector<String> cacheUrl)
	{
		this.cacheUrl = cacheUrl;
	}

	Vector<String> getFedoraUrl()
	{
		return fedoraUrl;
	}

	void setFedoraUrl(Vector<String> fedoraUrl)
	{
		this.fedoraUrl = fedoraUrl;
	}

	Vector<String> getRisearchUrl()
	{
		return risearchUrl;
	}

	void setRisearchUrl(Vector<String> risearchUrl)
	{
		this.risearchUrl = risearchUrl;
	}

	Vector<String> getHasPart()
	{
		return hasPart;
	}

	Vector<String> getZipUrl()
	{
		return zipUrl;
	}

	void setZipUrl(Vector<String> zipUrl)
	{
		this.zipUrl = zipUrl;
	}

	void setHasPart(Vector<String> hasPart)
	{
		this.hasPart = hasPart;
	}

	Vector<String> getDataciteUrl()
	{
		return dataciteUrl;
	}

	void setDataciteUrl(Vector<String> dataciteUrl)
	{
		this.dataciteUrl = dataciteUrl;
	}

	Vector<String> getBaseUrl()
	{
		return baseUrl;
	}

	void setBaseUrl(Vector<String> baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	void setVerbundUrl(Vector<String> verbundUrl)
	{
		this.verbundUrl = verbundUrl;
	}

	Vector<String> getDdc()
	{
		return ddc;
	}

	void setDdc(Vector<String> ddc)
	{
		this.ddc = ddc;
	}

	Vector<String> getSubject()
	{
		return subject;
	}

	void setSubject(Vector<String> subject)
	{
		this.subject = subject;
	}

	Vector<String> getCulturegraphUrl()
	{
		return culturegraphUrl;
	}

	void setCulturegraphUrl(Vector<String> culturegraphUrl)
	{
		this.culturegraphUrl = culturegraphUrl;
	}

	Vector<String> getLobidUrl()
	{
		return lobidUrl;
	}

	void setLobidUrl(Vector<String> lobidUrl)
	{
		this.lobidUrl = lobidUrl;
	}

	Vector<String> getLanguage()
	{
		return language;
	}

	void setLanguage(Vector<String> language)
	{
		this.language = language;
	}

	Vector<String> getDoi()
	{
		return doi;
	}

	void setDoi(Vector<String> doi)
	{
		this.doi = doi;
	}

	Vector<String> getUrn()
	{
		return urn;
	}

	void setUrn(Vector<String> urn)
	{
		this.urn = urn;
	}

	Vector<String> getUrl()
	{
		return url;
	}

	void setUrl(Vector<String> url)
	{
		this.url = url;
	}

	Vector<String> getLocation()
	{
		return location;
	}

	void setLocation(Vector<String> location)
	{
		this.location = location;
	}

	Vector<String> getIsPartOf()
	{
		return isPartOf;
	}

	void setIsPartOf(Vector<String> isPartOf)
	{
		this.isPartOf = isPartOf;
	}

	Vector<String> getMedium()
	{
		return medium;
	}

	void setMedium(Vector<String> medium)
	{
		this.medium = medium;
	}

	Vector<String> getAlephid()
	{
		return alephid;
	}

	void setAlephid(Vector<String> alephid)
	{
		this.alephid = alephid;
	}

	Vector<String> getType()
	{
		return type;
	}

	void setType(Vector<String> type)
	{
		this.type = type;
	}

	Vector<String> getPid()
	{
		return pid;
	}

	void setPid(Vector<String> pid)
	{
		this.pid = pid;
	}

	Vector<String> getTitle()
	{
		return title;
	}

	void setTitle(Vector<String> title)
	{
		this.title = title;
	}

	Vector<String> getCreator()
	{
		return creator;
	}

	// Vector<String> getHtmlUrl()
	// {
	// return htmlUrl;
	// }
	//
	// void setHtmlUrl(Vector<String> htmlUrl)
	// {
	// this.htmlUrl = htmlUrl;
	// }

	void setCreator(Vector<String> creator)
	{
		this.creator = creator;
	}

	Vector<String> getPublisher()
	{
		return publisher;
	}

	void setPublisher(Vector<String> publisher)
	{
		this.publisher = publisher;
	}

	Vector<String> getYear()
	{
		return year;
	}

	void setYear(Vector<String> year)
	{
		this.year = year;
	}

	Vector<String> getPdfUrl()
	{
		return pdfUrl;
	}

	void setPdfUrl(Vector<String> pdfUrl)
	{
		this.pdfUrl = pdfUrl;
	}

	Vector<String> getThumbnailUrl()
	{
		return thumbnailUrl;
	}

	void setThumbnailUrl(Vector<String> thumbnailUrl)
	{
		this.thumbnailUrl = thumbnailUrl;
	}

	boolean addAlephId(String e)
	{
		return alephid.add(e);
	}

	boolean addCreator(String e)
	{
		return creator.add(e);
	}

	boolean addTitle(String e)
	{
		return title.add(e);
	}

	boolean addSubject(String e)
	{
		return subject.add(e);
	}

	boolean addYear(String e)
	{
		return year.add(e);
	}

	boolean addType(String e)
	{
		return type.add(e);
	}

	boolean addDDC(String e)
	{
		return ddc.add(e);
	}

	boolean addLanguage(String e)
	{
		return language.add(e);
	}

	boolean addLocation(String e)
	{
		return location.add(e);
	}

	boolean addIsPartOf(String e, String name)
	{
		isPartOfName.add(name);
		return isPartOf.add(e);
	}

	boolean addMedium(String e)
	{
		return medium.add(e);
	}

	boolean addPid(String e)
	{
		return pid.add(e);
	}

	boolean addDoi(String e)
	{
		return doi.add(e);
	}

	boolean addUrn(String e)
	{
		return urn.add(e);
	}

	boolean addUrl(String e)
	{
		return url.add(e);
	}

	boolean addPdfUrl(String e)
	{
		return pdfUrl.add(e);
	}

	boolean addThumbnailUrl(String e)
	{
		return thumbnailUrl.add(e);
	}

	boolean addLobidUrl(String e)
	{
		return lobidUrl.add(e);
	}

	boolean addCulturegraphUrl(String e)
	{
		return culturegraphUrl.add(e);
	}

	boolean addVerbundUrl(String e)
	{
		return verbundUrl.add(e);
	}

	boolean addDdc(String e)
	{
		return ddc.add(e);
	}

	boolean addDataciteUrl(String e)
	{
		return dataciteUrl.add(e);
	}

	boolean addBaseUrl(String e)
	{
		return baseUrl.add(e);
	}

	boolean addHasPart(String e, String name)
	{
		hasPartName.add(name);
		return hasPart.add(e);
	}

	boolean addZipUrl(String e)
	{
		return zipUrl.add(e);
	}

	boolean addFedoraUrl(String e)
	{
		return fedoraUrl.add(e);
	}

	boolean addCacheUrl(String e)
	{
		return cacheUrl.add(e);
	}

	boolean addRisearchUrl(String e)
	{
		return risearchUrl.add(e);
	}

	boolean addDigitoolUrl(String e)
	{
		return digitoolUrl.add(e);
	}

	boolean addDescription(String e)
	{
		return description.add(e);
	}

	boolean addRights(String e)
	{
		return rights.add(e);
	}

	// boolean addQdcUrl(String e)
	// {
	// return qdcUrl.add(e);
	// }
	//
	// boolean addOaiOreUrl(String e)
	// {
	// return oaioreUrl.add(e);
	// }
	//
	// boolean addHtmlUrl(String e)
	// {
	// return htmlUrl.add(e);
	// }
	//
	// boolean addJsonUrl(String e)
	// {
	// return jsonUrl.add(e);
	// }
	//
	// Vector<String> getJsonUrl()
	// {
	// return jsonUrl;
	// }
	//
	// void setJsonUrl(Vector<String> jsonUrl)
	// {
	// this.jsonUrl = jsonUrl;
	// }

	String getFirstAlephId()
	{
		if (alephid.isEmpty())
			return "";
		String str = alephid.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	String getFirstPdfUrl()
	{
		if (pdfUrl.isEmpty())
			return "";
		String str = pdfUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	String getFirstThumbnailUrl()
	{
		if (thumbnailUrl.isEmpty())
			return "";
		String str = thumbnailUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	String getFirstLobidUrl()
	{
		if (lobidUrl.isEmpty())
			return "";
		String str = lobidUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	String getFirstPid()
	{
		if (pid.isEmpty())
			return "";
		String str = pid.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	String getFirstVerbundUrl()
	{
		if (verbundUrl.isEmpty())
			return "";
		String str = verbundUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	Vector<String> getOcrUrl()
	{
		return ocrUrl;
	}

	void setOcrUrl(Vector<String> ocrUrl)
	{
		this.ocrUrl = ocrUrl;
	}

	String getUri()
	{
		return uri;
	}

	void setUri(String uri)
	{
		this.uri = uri;
	}

	Vector<String> getMessage()
	{
		return message;
	}

	void setMessage(Vector<String> message)
	{
		this.message = message;
	}

	boolean addMessage(String e)
	{
		return message.add(e);
	}

	Vector<String> getVerbundUrl()
	{
		return verbundUrl;
	}

	String getFirstDoi()
	{
		if (doi.isEmpty())
			return "";
		String str = doi.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	String getFirstZipUrl()
	{
		if (zipUrl.isEmpty())
			return "";
		String str = zipUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	String getFirstFedoraUrl()
	{
		if (fedoraUrl.isEmpty())
			return "";
		String str = fedoraUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	String getFirstCacheUrl()
	{
		if (cacheUrl.isEmpty())
			return "";
		String str = cacheUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	String getFirstDigitoolUrl()
	{
		if (digitoolUrl.isEmpty())
			return "";
		String str = digitoolUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	String getFirstRisearchUrl()
	{
		if (risearchUrl.isEmpty())
			return "";
		String str = risearchUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	String getFirstDescription()
	{
		if (description.isEmpty())
			return "";
		String str = description.firstElement();
		if (str == null)
			str = "";
		return str;
	}
	// String getFirstQdcUrl()
	// {
	// String str = qdcUrl.firstElement();
	// if (str == null)
	// str = "";
	// return str;
	// }
	//
	// String getFirstOaiOreUrl()
	// {
	// String str = oaioreUrl.firstElement();
	// if (str == null)
	// str = "";
	// return str;
	// }
	//
	// String getFirstHtmlUrl()
	// {
	// String str = htmlUrl.firstElement();
	// if (str == null)
	// str = "";
	// return str;
	// }
	//
	// String getFirstJsonUrl()
	// {
	// String str = jsonUrl.firstElement();
	// if (str == null)
	// str = "";
	// return str;
	// }

}
