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
import java.util.List;
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

    List<String> title = null;
    List<String> creator = null;
    List<String> year = null;
    List<String> type = null;
    List<String> subject = null;
    List<String> description = null;
    List<String> ddc = null;
    List<String> language = null;
    List<String> location = null;
    List<String> publisher = null;
    List<String> isPartOf = null;
    List<String> isPartOfName = null;
    List<String> hasPart = null;
    List<String> hasPartName = null;
    List<String> medium = null;
    List<String> pid = null;
    List<String> doi = null;
    List<String> urn = null;
    List<String> url = null;
    List<String> alephid = null;
    List<String> rights = null;
    List<String> identifier = null;
    List<String> contributer = null;
    @SuppressWarnings("rawtypes")
    List<SimpleEntry> predicates = null;

    // TODO refactor names
    List<String> pdfUrl = null;
    List<String> zipUrl = null;
    List<String> thumbnailUrl = null;
    List<String> ocrUrl = null;

    // TODO make this configurable
    List<String> verbundUrl = null;
    List<String> dataciteUrl = null;
    List<String> lobidUrl = null;
    List<String> culturegraphUrl = null;
    List<String> baseUrl = null;
    List<String> originalObjectUrl = null;
    List<String> cacheUrl = null;
    List<String> fedoraUrl = null;
    List<String> risearchUrl = null;
    List<String> message = null;

    private Date lastModified = null;

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
	contributer = new Vector<String>();

    }

    public List<String> getContributer() {
	return contributer;
    }

    /**
     * @return
     */
    public List<String> getIsPartOfName() {
	return isPartOfName;
    }

    /**
     * @param isPartOfName
     */
    public void setIsPartOfName(List<String> isPartOfName) {
	this.isPartOfName = isPartOfName;
    }

    public List<String> getHasPartName() {
	return hasPartName;
    }

    public void setHasPartName(List<String> hasPart) {
	this.hasPartName = hasPart;
    }

    public List<String> getDescription() {
	return description;
    }

    public void setDescription(List<String> description) {
	this.description = description;
    }

    public List<String> getRights() {
	return rights;
    }

    public void setRights(List<String> rights) {
	this.rights = rights;
    }

    public List<String> getOriginalObjectUrl() {
	return originalObjectUrl;
    }

    public void setOriginalObjectUrl(List<String> originalObject) {
	this.originalObjectUrl = originalObject;
    }

    public List<String> getCacheUrl() {
	return cacheUrl;
    }

    public void setCacheUrl(List<String> cacheUrl) {
	this.cacheUrl = cacheUrl;
    }

    public List<String> getFedoraUrl() {
	return fedoraUrl;
    }

    public void setFedoraUrl(List<String> fedoraUrl) {
	this.fedoraUrl = fedoraUrl;
    }

    public List<String> getRisearchUrl() {
	return risearchUrl;
    }

    public void setRisearchUrl(List<String> risearchUrl) {
	this.risearchUrl = risearchUrl;
    }

    public List<String> getHasPart() {
	return hasPart;
    }

    public List<String> getZipUrl() {
	return zipUrl;
    }

    public void setZipUrl(List<String> zipUrl) {
	this.zipUrl = zipUrl;
    }

    public void setHasPart(List<String> hasPart) {
	this.hasPart = hasPart;
    }

    public List<String> getDataciteUrl() {
	return dataciteUrl;
    }

    public void setDataciteUrl(List<String> dataciteUrl) {
	this.dataciteUrl = dataciteUrl;
    }

    public List<String> getBaseUrl() {
	return baseUrl;
    }

    public void setBaseUrl(List<String> baseUrl) {
	this.baseUrl = baseUrl;
    }

    public void setVerbundUrl(List<String> verbundUrl) {
	this.verbundUrl = verbundUrl;
    }

    public List<String> getDdc() {
	return ddc;
    }

    public void setDdc(List<String> ddc) {
	this.ddc = ddc;
    }

    public List<String> getSubject() {
	return subject;
    }

    public void setSubject(List<String> subject) {
	this.subject = subject;
    }

    public List<String> getCulturegraphUrl() {
	return culturegraphUrl;
    }

    public void setCulturegraphUrl(List<String> culturegraphUrl) {
	this.culturegraphUrl = culturegraphUrl;
    }

    public List<String> getLobidUrl() {
	return lobidUrl;
    }

    public void setLobidUrl(List<String> lobidUrl) {
	this.lobidUrl = lobidUrl;
    }

    public List<String> getLanguage() {
	return language;
    }

    public void setLanguage(List<String> language) {
	this.language = language;
    }

    public List<String> getDoi() {
	return doi;
    }

    public void setDoi(List<String> doi) {
	this.doi = doi;
    }

    public List<String> getUrn() {
	return urn;
    }

    public void setUrn(List<String> urn) {
	this.urn = urn;
    }

    public List<String> getUrl() {
	return url;
    }

    public void setUrl(List<String> url) {
	this.url = url;
    }

    public List<String> getLocation() {
	return location;
    }

    public void setLocation(List<String> location) {
	this.location = location;
    }

    public List<String> getIsPartOf() {
	return isPartOf;
    }

    public void setIsPartOf(List<String> isPartOf) {
	this.isPartOf = isPartOf;
    }

    public List<String> getMedium() {
	return medium;
    }

    public void setMedium(List<String> medium) {
	this.medium = medium;
    }

    public List<String> getAlephid() {
	return alephid;
    }

    public void setAlephid(List<String> alephid) {
	this.alephid = alephid;
    }

    public List<String> getType() {
	return type;
    }

    public void setType(List<String> type) {
	this.type = type;
    }

    public List<String> getPid() {
	return pid;
    }

    public void setPid(List<String> pid) {
	this.pid = pid;
    }

    public List<String> getTitle() {
	return title;
    }

    public void setTitle(List<String> title) {
	this.title = title;
    }

    public List<String> getCreator() {
	return creator;
    }

    public void setCreator(List<String> creator) {
	this.creator = creator;
    }

    public List<String> getPublisher() {
	return publisher;
    }

    public void setPublisher(List<String> publisher) {
	this.publisher = publisher;
    }

    public List<String> getYear() {
	return year;
    }

    public void setYear(List<String> year) {
	this.year = year;
    }

    public List<String> getPdfUrl() {
	return pdfUrl;
    }

    public void setPdfUrl(List<String> pdfUrl) {
	this.pdfUrl = pdfUrl;
    }

    public List<String> getThumbnailUrl() {
	return thumbnailUrl;
    }

    public void setThumbnailUrl(List<String> thumbnailUrl) {
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

    public String getFirstAlephId() {
	if (alephid.isEmpty())
	    return "";
	String str = alephid.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstPdfUrl() {
	if (pdfUrl.isEmpty())
	    return "";
	String str = pdfUrl.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstThumbnailUrl() {
	if (thumbnailUrl.isEmpty())
	    return "";
	String str = thumbnailUrl.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstLobidUrl() {
	if (lobidUrl.isEmpty())
	    return "";
	String str = lobidUrl.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstPid() {
	if (pid.isEmpty())
	    return "";
	String str = pid.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstVerbundUrl() {
	if (verbundUrl.isEmpty())
	    return "";
	String str = verbundUrl.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public List<String> getOcrUrl() {
	return ocrUrl;
    }

    public void setOcrUrl(List<String> ocrUrl) {
	this.ocrUrl = ocrUrl;
    }

    public String getUri() {
	return uri;
    }

    public void setUri(String uri) {
	this.uri = uri;
    }

    public List<String> getMessage() {
	return message;
    }

    public void setMessage(List<String> message) {
	this.message = message;
    }

    public boolean addMessage(String e) {
	return message.add(e);
    }

    public List<String> getVerbundUrl() {
	return verbundUrl;
    }

    public String getFirstDoi() {
	if (doi.isEmpty())
	    return "";
	String str = doi.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstZipUrl() {
	if (zipUrl.isEmpty())
	    return "";
	String str = zipUrl.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstFedoraUrl() {
	if (fedoraUrl.isEmpty())
	    return "";
	String str = fedoraUrl.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstCacheUrl() {
	if (cacheUrl.isEmpty())
	    return "";
	String str = cacheUrl.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstOriginalObjectUrl() {
	if (originalObjectUrl.isEmpty())
	    return "";
	String str = originalObjectUrl.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstRisearchUrl() {
	if (risearchUrl.isEmpty())
	    return "";
	String str = risearchUrl.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public String getFirstDescription() {
	if (description.isEmpty())
	    return "";
	String str = description.get(0);
	if (str == null)
	    str = "";
	return str;
    }

    public void addIdentifier(String id) {
	identifier.add(id);
    }

    public List<String> getIdentifier() {
	return identifier;
    }

    public void setIdentifier(List<String> identifier) {
	this.identifier = identifier;
    }

    @SuppressWarnings("rawtypes")
    public List<SimpleEntry> getPredicates() {
	return predicates;
    }

    public void setPredicates(
	    @SuppressWarnings("rawtypes") List<SimpleEntry> predicates) {
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

    public void setContributer(List<String> contributer) {
	this.contributer = contributer;
    }

}
