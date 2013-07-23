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

import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("javadoc")
/**
 * View gives an aggregated view of a resource.
 * 
 * The current approach is based on oai_ore transformations. This class will be deleted in future.
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@XmlRootElement
public class View {
    String uri = null;
    String contentType = null;
    String apiUrl = null;

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
    Vector<String> identifier = null;
    @SuppressWarnings("rawtypes")
    Vector<SimpleEntry> predicates = null;

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
    Vector<String> originalObjectUrl = null;
    Vector<String> cacheUrl = null;
    Vector<String> fedoraUrl = null;
    Vector<String> risearchUrl = null;
    Vector<String> message = null;

    private Date lastModified = null;

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

    /**
     * Creates an empty view with preinitialised datastructs
     */
    @SuppressWarnings("rawtypes")
    public View() {
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
	originalObjectUrl = new Vector<String>();
	cacheUrl = new Vector<String>();
	fedoraUrl = new Vector<String>();
	risearchUrl = new Vector<String>();
	rights = new Vector<String>();
	message = new Vector<String>();
	isPartOfName = new Vector<String>();
	hasPartName = new Vector<String>();
	identifier = new Vector<String>();
	predicates = new Vector<SimpleEntry>();
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

    // public Vector<String> getQdcUrl()
    // {
    // return qdcUrl;
    // }
    //
    // public void setQdcUrl(Vector<String> qdcUrl)
    // {
    // this.qdcUrl = qdcUrl;
    // }
    //
    // public Vector<String> getMetsUrl()
    // {
    // return metsUrl;
    // }
    //
    // public void setMetsUrl(Vector<String> metsUrl)
    // {
    // this.metsUrl = metsUrl;
    // }
    //
    // public Vector<String> getOaioreUrl()
    // {
    // return oaioreUrl;
    // }
    //
    // public void setOaioreUrl(Vector<String> oaioreUrl)
    // {
    // this.oaioreUrl = oaioreUrl;
    // }
    //
    // public Vector<String> getOaidcUrl()
    // {
    // return oaidcUrl;
    // }
    //
    // public void setOaidcUrl(Vector<String> oaidcUrl)
    // {
    // this.oaidcUrl = oaidcUrl;
    // }
    //
    // public Vector<String> getXepicurUrl()
    // {
    // return xepicurUrl;
    // }
    //
    // public void setXepicurUrl(Vector<String> xepicurUrl)
    // {
    // this.xepicurUrl = xepicurUrl;
    // }
    //
    // public Vector<String> getXmetadissplusUrl()
    // {
    // return xmetadissplusUrl;
    // }
    //
    // public void setXmetadissplusUrl(Vector<String> xmetadissplusUrl)
    // {
    // this.xmetadissplusUrl = xmetadissplusUrl;
    // }
    //
    // public Vector<String> getAlephMarcUrl()
    // {
    // return alephMarcUrl;
    // }
    //
    // public void setAlephMarcUrl(Vector<String> alephMarcUrl)
    // {
    // this.alephMarcUrl = alephMarcUrl;
    // }
    //
    // public Vector<String> getEditorUrl()
    // {
    // return editorUrl;
    // }
    //
    // public void setEditorUrl(Vector<String> editorUrl)
    // {
    // this.editorUrl = editorUrl;
    // }
    //
    // public Vector<String> getOaiPmhUrl()
    // {
    // return oaiPmhUrl;
    // }
    //
    // public void setOaiPmhUrl(Vector<String> oaiPmhUrl)
    // {
    // this.oaiPmhUrl = oaiPmhUrl;
    // }
    //
    // public Vector<String> getVerbundUrl()
    // {
    // return verbundUrl;
    // }

    /**
     * @return
     */
    public Vector<String> getIsPartOfName() {
	return isPartOfName;
    }

    /**
     * @param isPartOfName
     */
    public void setIsPartOfName(Vector<String> isPartOfName) {
	this.isPartOfName = isPartOfName;
    }

    public Vector<String> getHasPartName() {
	return hasPartName;
    }

    public void setHasPartName(Vector<String> hasPart) {
	this.hasPartName = hasPart;
    }

    public Vector<String> getDescription() {
	return description;
    }

    public void setDescription(Vector<String> description) {
	this.description = description;
    }

    public Vector<String> getRights() {
	return rights;
    }

    public void setRights(Vector<String> rights) {
	this.rights = rights;
    }

    public Vector<String> getOriginalObjectUrl() {
	return originalObjectUrl;
    }

    public void setOriginalObjectUrl(Vector<String> originalObject) {
	this.originalObjectUrl = originalObject;
    }

    public Vector<String> getCacheUrl() {
	return cacheUrl;
    }

    public void setCacheUrl(Vector<String> cacheUrl) {
	this.cacheUrl = cacheUrl;
    }

    public Vector<String> getFedoraUrl() {
	return fedoraUrl;
    }

    public void setFedoraUrl(Vector<String> fedoraUrl) {
	this.fedoraUrl = fedoraUrl;
    }

    public Vector<String> getRisearchUrl() {
	return risearchUrl;
    }

    public void setRisearchUrl(Vector<String> risearchUrl) {
	this.risearchUrl = risearchUrl;
    }

    public Vector<String> getHasPart() {
	return hasPart;
    }

    public Vector<String> getZipUrl() {
	return zipUrl;
    }

    public void setZipUrl(Vector<String> zipUrl) {
	this.zipUrl = zipUrl;
    }

    public void setHasPart(Vector<String> hasPart) {
	this.hasPart = hasPart;
    }

    public Vector<String> getDataciteUrl() {
	return dataciteUrl;
    }

    public void setDataciteUrl(Vector<String> dataciteUrl) {
	this.dataciteUrl = dataciteUrl;
    }

    public Vector<String> getBaseUrl() {
	return baseUrl;
    }

    public void setBaseUrl(Vector<String> baseUrl) {
	this.baseUrl = baseUrl;
    }

    public void setVerbundUrl(Vector<String> verbundUrl) {
	this.verbundUrl = verbundUrl;
    }

    public Vector<String> getDdc() {
	return ddc;
    }

    public void setDdc(Vector<String> ddc) {
	this.ddc = ddc;
    }

    public Vector<String> getSubject() {
	return subject;
    }

    public void setSubject(Vector<String> subject) {
	this.subject = subject;
    }

    public Vector<String> getCulturegraphUrl() {
	return culturegraphUrl;
    }

    public void setCulturegraphUrl(Vector<String> culturegraphUrl) {
	this.culturegraphUrl = culturegraphUrl;
    }

    public Vector<String> getLobidUrl() {
	return lobidUrl;
    }

    public void setLobidUrl(Vector<String> lobidUrl) {
	this.lobidUrl = lobidUrl;
    }

    public Vector<String> getLanguage() {
	return language;
    }

    public void setLanguage(Vector<String> language) {
	this.language = language;
    }

    public Vector<String> getDoi() {
	return doi;
    }

    public void setDoi(Vector<String> doi) {
	this.doi = doi;
    }

    public Vector<String> getUrn() {
	return urn;
    }

    public void setUrn(Vector<String> urn) {
	this.urn = urn;
    }

    public Vector<String> getUrl() {
	return url;
    }

    public void setUrl(Vector<String> url) {
	this.url = url;
    }

    public Vector<String> getLocation() {
	return location;
    }

    public void setLocation(Vector<String> location) {
	this.location = location;
    }

    public Vector<String> getIsPartOf() {
	return isPartOf;
    }

    public void setIsPartOf(Vector<String> isPartOf) {
	this.isPartOf = isPartOf;
    }

    public Vector<String> getMedium() {
	return medium;
    }

    public void setMedium(Vector<String> medium) {
	this.medium = medium;
    }

    public Vector<String> getAlephid() {
	return alephid;
    }

    public void setAlephid(Vector<String> alephid) {
	this.alephid = alephid;
    }

    public Vector<String> getType() {
	return type;
    }

    public void setType(Vector<String> type) {
	this.type = type;
    }

    public Vector<String> getPid() {
	return pid;
    }

    public void setPid(Vector<String> pid) {
	this.pid = pid;
    }

    public Vector<String> getTitle() {
	return title;
    }

    public void setTitle(Vector<String> title) {
	this.title = title;
    }

    public Vector<String> getCreator() {
	return creator;
    }

    // public Vector<String> getHtmlUrl()
    // {
    // return htmlUrl;
    // }
    //
    // public void setHtmlUrl(Vector<String> htmlUrl)
    // {
    // this.htmlUrl = htmlUrl;
    // }

    public void setCreator(Vector<String> creator) {
	this.creator = creator;
    }

    public Vector<String> getPublisher() {
	return publisher;
    }

    public void setPublisher(Vector<String> publisher) {
	this.publisher = publisher;
    }

    public Vector<String> getYear() {
	return year;
    }

    public void setYear(Vector<String> year) {
	this.year = year;
    }

    public Vector<String> getPdfUrl() {
	return pdfUrl;
    }

    public void setPdfUrl(Vector<String> pdfUrl) {
	this.pdfUrl = pdfUrl;
    }

    public Vector<String> getThumbnailUrl() {
	return thumbnailUrl;
    }

    public void setThumbnailUrl(Vector<String> thumbnailUrl) {
	this.thumbnailUrl = thumbnailUrl;
    }

    public boolean addAlephId(String e) {
	return alephid.add(e);
    }

    public boolean addCreator(String e) {
	return creator.add(e);
    }

    public boolean addTitle(String e) {
	return title.add(e);
    }

    public boolean addSubject(String e) {
	return subject.add(e);
    }

    public boolean addYear(String e) {
	return year.add(e);
    }

    public boolean addType(String e) {
	return type.add(e);
    }

    public boolean addDDC(String e) {
	return ddc.add(e);
    }

    public boolean addLanguage(String e) {
	return language.add(e);
    }

    public boolean addLocation(String e) {
	return location.add(e);
    }

    public boolean addIsPartOf(String e, String name) {
	isPartOfName.add(name);
	return isPartOf.add(e);
    }

    public boolean addMedium(String e) {
	return medium.add(e);
    }

    public boolean addPid(String e) {
	return pid.add(e);
    }

    public boolean addDoi(String e) {
	return doi.add(e);
    }

    public boolean addUrn(String e) {
	return urn.add(e);
    }

    public boolean addUrl(String e) {
	return url.add(e);
    }

    public boolean addPdfUrl(String e) {
	return pdfUrl.add(e);
    }

    public boolean addThumbnailUrl(String e) {
	return thumbnailUrl.add(e);
    }

    public boolean addLobidUrl(String e) {
	return lobidUrl.add(e);
    }

    public boolean addCulturegraphUrl(String e) {
	return culturegraphUrl.add(e);
    }

    public boolean addVerbundUrl(String e) {
	return verbundUrl.add(e);
    }

    public boolean addDdc(String e) {
	return ddc.add(e);
    }

    public boolean addDataciteUrl(String e) {
	return dataciteUrl.add(e);
    }

    public boolean addBaseUrl(String e) {
	return baseUrl.add(e);
    }

    public boolean addHasPart(String e, String name) {
	hasPartName.add(name);
	return hasPart.add(e);
    }

    public boolean addZipUrl(String e) {
	return zipUrl.add(e);
    }

    public boolean addFedoraUrl(String e) {
	return fedoraUrl.add(e);
    }

    public boolean addCacheUrl(String e) {
	return cacheUrl.add(e);
    }

    public boolean addRisearchUrl(String e) {
	return risearchUrl.add(e);
    }

    public boolean addOriginalObjectUrl(String e) {
	return originalObjectUrl.add(e);
    }

    public boolean addDescription(String e) {
	return description.add(e);
    }

    public boolean addRights(String e) {
	return rights.add(e);
    }

    // public boolean addQdcUrl(String e)
    // {
    // return qdcUrl.add(e);
    // }
    //
    // public boolean addOaiOreUrl(String e)
    // {
    // return oaioreUrl.add(e);
    // }
    //
    // public boolean addHtmlUrl(String e)
    // {
    // return htmlUrl.add(e);
    // }
    //
    // public boolean addJsonUrl(String e)
    // {
    // return jsonUrl.add(e);
    // }
    //
    // public Vector<String> getJsonUrl()
    // {
    // return jsonUrl;
    // }
    //
    // public void setJsonUrl(Vector<String> jsonUrl)
    // {
    // this.jsonUrl = jsonUrl;
    // }

    public String getFirstAlephId() {
	if (alephid.isEmpty())
	    return "";
	String str = alephid.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstPdfUrl() {
	if (pdfUrl.isEmpty())
	    return "";
	String str = pdfUrl.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstThumbnailUrl() {
	if (thumbnailUrl.isEmpty())
	    return "";
	String str = thumbnailUrl.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstLobidUrl() {
	if (lobidUrl.isEmpty())
	    return "";
	String str = lobidUrl.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstPid() {
	if (pid.isEmpty())
	    return "";
	String str = pid.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstVerbundUrl() {
	if (verbundUrl.isEmpty())
	    return "";
	String str = verbundUrl.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    public Vector<String> getOcrUrl() {
	return ocrUrl;
    }

    public void setOcrUrl(Vector<String> ocrUrl) {
	this.ocrUrl = ocrUrl;
    }

    public String getUri() {
	return uri;
    }

    public void setUri(String uri) {
	this.uri = uri;
    }

    public Vector<String> getMessage() {
	return message;
    }

    public void setMessage(Vector<String> message) {
	this.message = message;
    }

    public boolean addMessage(String e) {
	return message.add(e);
    }

    public Vector<String> getVerbundUrl() {
	return verbundUrl;
    }

    public String getFirstDoi() {
	if (doi.isEmpty())
	    return "";
	String str = doi.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstZipUrl() {
	if (zipUrl.isEmpty())
	    return "";
	String str = zipUrl.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstFedoraUrl() {
	if (fedoraUrl.isEmpty())
	    return "";
	String str = fedoraUrl.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstCacheUrl() {
	if (cacheUrl.isEmpty())
	    return "";
	String str = cacheUrl.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstOriginalObjectUrl() {
	if (originalObjectUrl.isEmpty())
	    return "";
	String str = originalObjectUrl.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstRisearchUrl() {
	if (risearchUrl.isEmpty())
	    return "";
	String str = risearchUrl.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstDescription() {
	if (description.isEmpty())
	    return "";
	String str = description.firstElement();
	if (str == null)
	    str = "";
	return str;
    }

    // public String getFirstQdcUrl()
    // {
    // String str = qdcUrl.firstElement();
    // if (str == null)
    // str = "";
    // return str;
    // }
    //
    // public String getFirstOaiOreUrl()
    // {
    // String str = oaioreUrl.firstElement();
    // if (str == null)
    // str = "";
    // return str;
    // }
    //
    // public String getFirstHtmlUrl()
    // {
    // String str = htmlUrl.firstElement();
    // if (str == null)
    // str = "";
    // return str;
    // }
    //
    // public String getFirstJsonUrl()
    // {
    // String str = jsonUrl.firstElement();
    // if (str == null)
    // str = "";
    // return str;
    // }

    public void addIdentifier(String id) {
	identifier.add(id);
    }

    public Vector<String> getIdentifier() {
	return identifier;
    }

    public void setIdentifier(Vector<String> identifier) {
	this.identifier = identifier;
    }

    @SuppressWarnings("rawtypes")
    public Vector<SimpleEntry> getPredicates() {
	return predicates;
    }

    public void setPredicates(
	    @SuppressWarnings("rawtypes") Vector<SimpleEntry> predicates) {
	this.predicates = predicates;
    }

    public void addPredicate(String pred, String object) {
	predicates.add(new SimpleEntry<String, String>(pred, object));
    }

    public void setLastModified(Date lm) {
	lastModified = lm;
    }

    public Date getLastModified() {
	return lastModified;
    }

    public String getContentType() {
	return contentType;
    }

    public void setContentType(String contentType) {
	this.contentType = contentType;
    }

    public String getApiUrl() {
	return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
	this.apiUrl = apiUrl;
    }

}
