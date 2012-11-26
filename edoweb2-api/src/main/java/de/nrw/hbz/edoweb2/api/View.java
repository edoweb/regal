package de.nrw.hbz.edoweb2.api;

import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class View
{
	String uri = null;

	Vector<String> title = null;
	Vector<String> creator = null;
	Vector<String> year = null;
	Vector<String> type = null;
	Vector<String> subject = null;
	Vector<String> ddc = null;
	Vector<String> language = null;
	Vector<String> location = null;
	Vector<String> publisher = null;
	Vector<String> isPartOf = null;
	Vector<String> hasPart = null;
	Vector<String> medium = null;
	Vector<String> pid = null;
	Vector<String> doi = null;
	Vector<String> urn = null;
	Vector<String> url = null;
	Vector<String> alephid = null;
	Vector<String> lobidUrl = null;
	Vector<String> culturegraphUrl = null;
	Vector<String> verbundUrl = null;
	Vector<String> dataciteUrl = null;
	Vector<String> baseUrl = null;
	Vector<String> pdfUrl = null;
	Vector<String> zipUrl = null;
	Vector<String> thumbnailUrl = null;
	Vector<String> ocrUrl = null;

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

	public View()
	{
		title = new Vector<String>();
		creator = new Vector<String>();
		year = new Vector<String>();
		type = new Vector<String>();
		subject = new Vector<String>();
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

	public Vector<String> getHasPart()
	{
		return hasPart;
	}

	public Vector<String> getZipUrl()
	{
		return zipUrl;
	}

	public void setZipUrl(Vector<String> zipUrl)
	{
		this.zipUrl = zipUrl;
	}

	public void setHasPart(Vector<String> hasPart)
	{
		this.hasPart = hasPart;
	}

	public Vector<String> getDataciteUrl()
	{
		return dataciteUrl;
	}

	public void setDataciteUrl(Vector<String> dataciteUrl)
	{
		this.dataciteUrl = dataciteUrl;
	}

	public Vector<String> getBaseUrl()
	{
		return baseUrl;
	}

	public void setBaseUrl(Vector<String> baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	public void setVerbundUrl(Vector<String> verbundUrl)
	{
		this.verbundUrl = verbundUrl;
	}

	public Vector<String> getDdc()
	{
		return ddc;
	}

	public void setDdc(Vector<String> ddc)
	{
		this.ddc = ddc;
	}

	public Vector<String> getSubject()
	{
		return subject;
	}

	public void setSubject(Vector<String> subject)
	{
		this.subject = subject;
	}

	public Vector<String> getCulturegraphUrl()
	{
		return culturegraphUrl;
	}

	public void setCulturegraphUrl(Vector<String> culturegraphUrl)
	{
		this.culturegraphUrl = culturegraphUrl;
	}

	public Vector<String> getLobidUrl()
	{
		return lobidUrl;
	}

	public void setLobidUrl(Vector<String> lobidUrl)
	{
		this.lobidUrl = lobidUrl;
	}

	public Vector<String> getLanguage()
	{
		return language;
	}

	public void setLanguage(Vector<String> language)
	{
		this.language = language;
	}

	public Vector<String> getDoi()
	{
		return doi;
	}

	public void setDoi(Vector<String> doi)
	{
		this.doi = doi;
	}

	public Vector<String> getUrn()
	{
		return urn;
	}

	public void setUrn(Vector<String> urn)
	{
		this.urn = urn;
	}

	public Vector<String> getUrl()
	{
		return url;
	}

	public void setUrl(Vector<String> url)
	{
		this.url = url;
	}

	public Vector<String> getLocation()
	{
		return location;
	}

	public void setLocation(Vector<String> location)
	{
		this.location = location;
	}

	public Vector<String> getIsPartOf()
	{
		return isPartOf;
	}

	public void setIsPartOf(Vector<String> isPartOf)
	{
		this.isPartOf = isPartOf;
	}

	public Vector<String> getMedium()
	{
		return medium;
	}

	public void setMedium(Vector<String> medium)
	{
		this.medium = medium;
	}

	public Vector<String> getAlephid()
	{
		return alephid;
	}

	public void setAlephid(Vector<String> alephid)
	{
		this.alephid = alephid;
	}

	public Vector<String> getType()
	{
		return type;
	}

	public void setType(Vector<String> type)
	{
		this.type = type;
	}

	public Vector<String> getPid()
	{
		return pid;
	}

	public void setPid(Vector<String> pid)
	{
		this.pid = pid;
	}

	public Vector<String> getTitle()
	{
		return title;
	}

	public void setTitle(Vector<String> title)
	{
		this.title = title;
	}

	public Vector<String> getCreator()
	{
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

	public void setCreator(Vector<String> creator)
	{
		this.creator = creator;
	}

	public Vector<String> getPublisher()
	{
		return publisher;
	}

	public void setPublisher(Vector<String> publisher)
	{
		this.publisher = publisher;
	}

	public Vector<String> getYear()
	{
		return year;
	}

	public void setYear(Vector<String> year)
	{
		this.year = year;
	}

	public Vector<String> getPdfUrl()
	{
		return pdfUrl;
	}

	public void setPdfUrl(Vector<String> pdfUrl)
	{
		this.pdfUrl = pdfUrl;
	}

	public Vector<String> getThumbnailUrl()
	{
		return thumbnailUrl;
	}

	public void setThumbnailUrl(Vector<String> thumbnailUrl)
	{
		this.thumbnailUrl = thumbnailUrl;
	}

	public boolean addAlephId(String e)
	{
		return alephid.add(e);
	}

	public boolean addCreator(String e)
	{
		return creator.add(e);
	}

	public boolean addTitle(String e)
	{
		return title.add(e);
	}

	public boolean addSubject(String e)
	{
		return subject.add(e);
	}

	public boolean addYear(String e)
	{
		return year.add(e);
	}

	public boolean addType(String e)
	{
		return type.add(e);
	}

	public boolean addDDC(String e)
	{
		return ddc.add(e);
	}

	public boolean addLanguage(String e)
	{
		return language.add(e);
	}

	public boolean addLocation(String e)
	{
		return location.add(e);
	}

	public boolean addIsPartOf(String e)
	{
		return isPartOf.add(e);
	}

	public boolean addMedium(String e)
	{
		return medium.add(e);
	}

	public boolean addPid(String e)
	{
		return pid.add(e);
	}

	public boolean addDoi(String e)
	{
		return doi.add(e);
	}

	public boolean addUrn(String e)
	{
		return urn.add(e);
	}

	public boolean addUrl(String e)
	{
		return url.add(e);
	}

	public boolean addPdfUrl(String e)
	{
		return pdfUrl.add(e);
	}

	public boolean addThumbnailUrl(String e)
	{
		return thumbnailUrl.add(e);
	}

	public boolean addLobidUrl(String e)
	{
		return lobidUrl.add(e);
	}

	public boolean addCulturegraphUrl(String e)
	{
		return culturegraphUrl.add(e);
	}

	public boolean addVerbundUrl(String e)
	{
		return verbundUrl.add(e);
	}

	public boolean addDdc(String e)
	{
		return ddc.add(e);
	}

	public boolean addDataciteUrl(String e)
	{
		return dataciteUrl.add(e);
	}

	public boolean addBaseUrl(String e)
	{
		return baseUrl.add(e);
	}

	public boolean addHasPart(String e)
	{
		return hasPart.add(e);
	}

	public boolean addZipUrl(String e)
	{
		return zipUrl.add(e);
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

	public String getFirstAlephId()
	{
		if (alephid.isEmpty())
			return "";
		String str = alephid.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	public String getFirstPdfUrl()
	{
		if (pdfUrl.isEmpty())
			return "";
		String str = pdfUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	public String getFirstThumbnailUrl()
	{
		if (thumbnailUrl.isEmpty())
			return "";
		String str = thumbnailUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	public String getFirstLobidUrl()
	{
		if (lobidUrl.isEmpty())
			return "";
		String str = lobidUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	public String getFirstPid()
	{
		if (pid.isEmpty())
			return "";
		String str = pid.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	public String getFirstVerbundUrl()
	{
		if (verbundUrl.isEmpty())
			return "";
		String str = verbundUrl.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	public Vector<String> getOcrUrl()
	{
		return ocrUrl;
	}

	public void setOcrUrl(Vector<String> ocrUrl)
	{
		this.ocrUrl = ocrUrl;
	}

	public String getUri()
	{
		return uri;
	}

	public void setUri(String uri)
	{
		this.uri = uri;
	}

	public Vector<String> getVerbundUrl()
	{
		return verbundUrl;
	}

	public String getFirstDoi()
	{
		if (doi.isEmpty())
			return "";
		String str = doi.firstElement();
		if (str == null)
			str = "";
		return str;
	}

	public String getFirstZipUrl()
	{
		if (zipUrl.isEmpty())
			return "";
		String str = zipUrl.firstElement();
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

}
