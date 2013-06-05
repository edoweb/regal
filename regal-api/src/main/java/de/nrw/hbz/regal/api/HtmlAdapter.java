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
package de.nrw.hbz.regal.api;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author jan
 * 
 */
class HtmlAdapter
{

	static String getHtml(View view)
	{

		String edowebStyle = "/css/style.css";
		String edowebLogo = "/logo.gif";

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder parser;

			parser = factory.newDocumentBuilder();
			Document doc = parser.newDocument();
			Element html = doc.createElement("html");
			html.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
			html.setAttribute("xml:lang", "de");
			Element head = doc.createElement("head");
			Element css = doc.createElement("link");
			css.setAttribute("rel", "stylesheet");
			css.setAttribute("href", edowebStyle);
			css.setAttribute("type", "text/css");

			Element css2 = doc.createElement("link");
			css2.setAttribute("rel", "stylesheet");
			css2.setAttribute("href",
					"http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css");

			Element script1 = doc.createElement("script");
			script1.setAttribute("src",
					"http://code.jquery.com/jquery-1.9.1.js");
			script1.appendChild(doc.createComment("workaround"));

			Element script2 = doc.createElement("script");
			script2.setAttribute("src",
					"http://code.jquery.com/ui/1.10.3/jquery-ui.js");
			script2.appendChild(doc.createComment("workaround"));

			Element script3 = doc.createElement("script");
			script3.appendChild(doc
					.createTextNode("$(function() {$( \"#tabs\" ).tabs();});"));

			head.appendChild(css);
			head.appendChild(css2);
			head.appendChild(script1);
			head.appendChild(script2);
			head.appendChild(script3);

			Element body = doc.createElement("body");

			Element div = doc.createElement("div");
			div.setAttribute("class", "logo");
			Element image = doc.createElement("image");
			image.setAttribute("src", edowebLogo);
			div.appendChild(image);
			body.appendChild(div);

			setItem(body, doc, view);

			html.appendChild(head);
			html.appendChild(body);
			doc.appendChild(html);

			TransformerFactory tfact = TransformerFactory.newInstance();
			Transformer transformer = tfact.newTransformer();
			StringWriter writer = new StringWriter();
			Result result = new StreamResult(writer);
			Source source = new DOMSource(doc);
			transformer.transform(source, result);
			writer.flush();
			writer.close();
			String xml = writer.toString();

			return xml;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	private static void setItem(Element body, Document doc, View view)
	{

		Element div = doc.createElement("div");
		div.setAttribute("id", "tabs");
		Element tabul = doc.createElement("ul");
		Element li1 = doc.createElement("li");
		Element a = doc.createElement("a");
		a.setAttribute("href", "#tabs-1");
		a.appendChild(doc.createTextNode("Metadata"));
		li1.appendChild(a);

		// Element li2 = doc.createElement("li");
		// Element a2 = doc.createElement("a");
		// a2.setAttribute("href", "#tabs-2");
		// a2.appendChild(doc.createTextNode("Catalog"));
		// li2.appendChild(a2);

		Element li3 = doc.createElement("li");
		Element a3 = doc.createElement("a");
		a3.setAttribute("href", "#tabs-3");
		a3.appendChild(doc.createTextNode("Relations"));
		li3.appendChild(a3);

		Element li4 = doc.createElement("li");
		Element a4 = doc.createElement("a");
		a4.setAttribute("href", "#tabs-4");
		a4.appendChild(doc.createTextNode("Links"));
		li4.appendChild(a4);

		tabul.appendChild(li1);
		// tabul.appendChild(li2);
		tabul.appendChild(li3);
		tabul.appendChild(li4);

		div.appendChild(tabul);

		Element searchDiv = doc.createElement("div");
		searchDiv.setAttribute("id", "tabheader");
		Element searchLink = doc.createElement("a");
		try
		{
			String host = new URI(view.getApiUrl()).getHost();
			searchLink.setAttribute("href", "http://" + host);
			Element searchImage = doc.createElement("img");
			searchImage.setAttribute("src", "/search.png");
			searchImage.setAttribute("height", "40");
			searchLink.appendChild(searchImage);
			searchDiv.appendChild(searchLink);
			div.appendChild(searchDiv);
		}
		catch (URISyntaxException e)
		{

		}

		Element divTab1 = doc.createElement("div");
		divTab1.setAttribute("id", "tabs-1");
		div.appendChild(divTab1);

		Element table = doc.createElement("table");

		addToTable(doc, table, "Fulltext", view.getPdfUrl());

		addToTable(doc, table, "Url", view.getApiUrl());

		addToTable(doc, table, "Uri", view.getUri());

		addToTable(doc, table, "Content-Type", view.getContentType());

		addToTable(doc, table, "Aleph Id", view.getAlephid());

		addToTable(doc, table, "Webarchive", view.getZipUrl());

		addToTable(doc, table, "Title", view.getTitle());

		addToTable(doc, table, "Creator", view.getCreator());

		addToTable(doc, table, "Year", view.getYear());

		addToTable(doc, table, "Type", view.getType());

		addToTable(doc, table, "Publisher", view.getPublisher());

		addToTable(doc, table, "Language", view.getLanguage());

		addToTable(doc, table, "Location", view.getLocation());

		addToTable(doc, table, "Subject", view.getSubject());

		addToTable(doc, table, "Description", view.getDescription());

		addToTable(doc, table, "DDC", view.getDdc());

		addToTable(doc, table, "DOI", view.getDoi());

		addToTable(doc, table, "URN", view.getUrn());

		addToTable(doc, table, "Related Identifier", view.getIdentifier());

		addToTable(doc, table, "Related Url", view.getUrl());

		addToTable(doc, table, "Last Modified", view.getLastModified()
				.toString());

		// divTab1.appendChild(table);
		// Element divTab2 = doc.createElement("div");
		// divTab2.setAttribute("id", "tabs-2");
		// div.appendChild(divTab2);
		//
		// table = doc.createElement("table");

		// Element tr = doc.createElement("tr");
		// Element td = doc.createElement("td");
		// td.setAttribute("class", "tablelabel");
		// td.setAttribute("colspan", "4");
		//
		// tr = doc.createElement("tr");
		//
		// td.setAttribute("colspan", "4");
		//
		// td.appendChild(doc.createTextNode("About: " + view.getUri()));
		// tr.appendChild(td);
		// table.appendChild(tr);
		//
		// for (SimpleEntry entry : view.getPredicates())
		// {
		// addToTable(doc, table, entry.getKey().toString(), entry.getValue()
		// .toString());
		// }

		divTab1.appendChild(table);

		Element divTab3 = doc.createElement("div");
		divTab3.setAttribute("id", "tabs-3");
		div.appendChild(divTab3);

		table = doc.createElement("table");
		table.appendChild(doc.createComment("avoid empty elements for firefox"));
		divTab3.appendChild(table);

		addHasPart(doc, table, "hasPart", view);

		addIsPartOf(doc, table, "isPartOf", view);

		Element divTab4 = doc.createElement("div");
		divTab4.setAttribute("id", "tabs-4");
		div.appendChild(divTab4);

		table = doc.createElement("table");
		divTab4.appendChild(table);

		Element tr = doc.createElement("tr");
		Element td = doc.createElement("td");
		Element td0 = doc.createElement("td");

		td0.setAttribute("class", "plabel");
		td0.appendChild(doc.createTextNode("External"));

		if (view.getFirstLobidUrl() != null
				&& !view.getFirstLobidUrl().isEmpty())
		{
			a = doc.createElement("a");
			a.setAttribute("href", view.getFirstLobidUrl());
			a.setAttribute("id", "lobidLink");
			a.appendChild(doc.createTextNode("@ lobid.org"));
			td.appendChild(a);
		}

		if (view.getFirstVerbundUrl() != null
				&& !view.getFirstVerbundUrl().isEmpty())
		{
			a = doc.createElement("a");
			a.setAttribute("href", view.getFirstVerbundUrl());
			a.setAttribute("id", "verbundLink");
			a.appendChild(doc.createTextNode("@ hbz-nrw.de"));
			td.appendChild(a);
		}

		if (view.getFirstOriginalObjectUrl() != null
				&& !view.getFirstOriginalObjectUrl().isEmpty())
		{
			a = doc.createElement("a");
			a.setAttribute("href", view.getFirstOriginalObjectUrl());
			a.setAttribute("id", "digitoolLink");
			a.appendChild(doc.createTextNode("@ original object"));
			td.appendChild(a);
		}

		tr.appendChild(td0);
		tr.appendChild(td);

		table.appendChild(tr);

		tr = doc.createElement("tr");
		td = doc.createElement("td");
		td0 = doc.createElement("td");

		td0.setAttribute("class", "plabel");
		td0.appendChild(doc.createTextNode("Internal"));

		a = doc.createElement("a");
		a.setAttribute("href", view.getFirstFedoraUrl());
		a.setAttribute("id", "fedoraLink");
		a.appendChild(doc.createTextNode("@ fedora"));
		td.appendChild(a);

		a = doc.createElement("a");
		a.setAttribute("href", view.getFirstRisearchUrl());
		a.setAttribute("id", "risearchLink");
		a.appendChild(doc.createTextNode("@ risearch"));
		td.appendChild(a);
		// TODO only if synced resource
		if (view.getFirstCacheUrl() != null
				&& !view.getFirstCacheUrl().isEmpty())
		{
			a = doc.createElement("a");
			a.setAttribute("href", view.getFirstCacheUrl());
			a.setAttribute("id", "cacheLink");
			a.appendChild(doc.createTextNode("@ cache"));
			td.appendChild(a);
		}

		tr.appendChild(td0);
		tr.appendChild(td);

		table.appendChild(tr);

		tr = doc.createElement("tr");
		td0 = doc.createElement("td");
		td = doc.createElement("td");
		doc.createElement("img");

		td0.setAttribute("class", "editIcon");

		tr.appendChild(td0);

		td.setAttribute("class", "fedoraLogo");

		tr.appendChild(td);

		table.appendChild(tr);

		body.appendChild(div);

	}

	static void addToTable(Document doc, Element table, String fieldName,
			String value)
	{
		Vector<String> values = new Vector<String>();
		values.add(value);
		addToTable(doc, table, fieldName, values);
	}

	static void addToTable(Document doc, Element table, String fieldName,
			Vector<String> values)
	{
		String urnResolver = "http://nbn-resolving.de/";
		String doiResolver = "http://dx.doi.org/";
		String pdfLogo = "/pdflogo.svg";
		String zipLogo = "/zip.png";
		if (fieldName == "http://purl.org/ontology/bibo/doi")
		{

			for (String str : values)
			{
				Element tr = doc.createElement("tr");
				Element td1 = doc.createElement("td");
				td1.setAttribute("class", "plabel");
				Element td2 = doc.createElement("td");

				td1.appendChild(doc.createTextNode(fieldName));

				Element resolver = doc.createElement("a");
				resolver.setAttribute("href", doiResolver + str);
				resolver.appendChild(doc.createTextNode(str));
				td2.appendChild(resolver);

				tr.appendChild(td1);
				tr.appendChild(td2);
				table.appendChild(tr);
			}

		}
		else if (fieldName.compareTo("URN") == 0)
		{

			for (String str : values)
			{
				Element tr = doc.createElement("tr");
				Element td1 = doc.createElement("td");
				td1.setAttribute("class", "plabel");
				Element td2 = doc.createElement("td");

				td1.appendChild(doc.createTextNode(fieldName));

				Element resolver = doc.createElement("a");
				resolver.setAttribute("href", urnResolver + str);
				resolver.appendChild(doc.createTextNode(str));
				td2.appendChild(resolver);

				tr.appendChild(td1);
				tr.appendChild(td2);
				table.appendChild(tr);
			}
		}
		else if (fieldName.compareTo("isPartOf") == 0
				|| fieldName.compareTo("hasPart") == 0)
		{
			for (String str : values)
			{
				Element tr = doc.createElement("tr");
				Element td1 = doc.createElement("td");
				td1.setAttribute("class", "plabel");
				Element td2 = doc.createElement("td");

				td1.appendChild(doc.createTextNode(fieldName));

				// String description = getDescription(str);
				Element link = doc.createElement("a");
				link.setAttribute("href", str);
				link.setAttribute("class", "relationLink");
				// link.appendChild(doc.createTextNode(description + " ("
				// + str.substring(str.lastIndexOf('/') + 1) + ")"));

				link.appendChild(doc.createTextNode(URLDecoder.decode(str
						.substring(str.lastIndexOf('/') + 1))));
				td2.appendChild(link);
				td2.setAttribute("class", "relation");
				tr.appendChild(td1);
				tr.appendChild(td2);
				table.appendChild(tr);
			}
		}
		else if (fieldName.compareTo("Fulltext") == 0)
		{
			for (String str : values)
			{
				Element tr = doc.createElement("tr");
				Element td1 = doc.createElement("td");
				td1.setAttribute("class", "plabel");
				Element td2 = doc.createElement("td");

				td1.appendChild(doc.createTextNode(fieldName));

				Element image = doc.createElement("img");
				image.setAttribute("src", pdfLogo);
				image.setAttribute("width", "100");

				Element link = doc.createElement("a");
				link.setAttribute("href", str);
				link.appendChild(image);
				td2.appendChild(link);
				td2.setAttribute("class", "textlink");
				tr.appendChild(td1);
				tr.appendChild(td2);
				table.appendChild(tr);
			}
		}
		else if (fieldName.compareTo("Webarchive") == 0)
		{
			for (String str : values)
			{
				Element tr = doc.createElement("tr");
				Element td1 = doc.createElement("td");
				td1.setAttribute("class", "plabel");
				Element td2 = doc.createElement("td");

				td1.appendChild(doc.createTextNode(fieldName));

				Element image = doc.createElement("img");
				image.setAttribute("src", zipLogo);
				image.setAttribute("width", "100");

				Element link = doc.createElement("a");
				link.setAttribute("href", str);
				link.appendChild(image);
				td2.appendChild(link);
				td2.setAttribute("class", "textlink");
				tr.appendChild(td1);
				tr.appendChild(td2);
				table.appendChild(tr);
			}
		}
		else
		{
			for (String str : values)
			{
				Element tr = doc.createElement("tr");
				Element td1 = doc.createElement("td");
				td1.setAttribute("class", "plabel");
				Element td2 = doc.createElement("td");

				td1.appendChild(doc.createTextNode(fieldName));

				if (str.startsWith("http"))
				{
					Element link = doc.createElement("a");
					link.setAttribute("href", str);
					link.appendChild(doc.createTextNode(str));
					td2.appendChild(link);
				}
				else
				{
					td2.appendChild(doc.createTextNode(str));
				}

				tr.appendChild(td1);
				tr.appendChild(td2);
				table.appendChild(tr);
			}
		}
	}

	static void addIsPartOf(Document doc, Element table, String fieldName,
			View view)
	{

		List<String> isPartOf = view.getIsPartOf();
		List<String> isPartOfName = view.getIsPartOfName();
		for (int i = 0; i < isPartOf.size(); i++)
		{
			String url = isPartOf.get(i);
			String name = isPartOfName.get(i);
			Element tr = doc.createElement("tr");
			Element td1 = doc.createElement("td");
			td1.setAttribute("class", "plabel");
			Element td2 = doc.createElement("td");

			td1.appendChild(doc.createTextNode(fieldName));

			// String description = getDescription(str);
			Element link = doc.createElement("a");
			link.setAttribute("href", url);
			link.setAttribute("class", "relationLink");
			// link.appendChild(doc.createTextNode(description + " ("
			// + str.substring(str.lastIndexOf('/') + 1) + ")"));

			link.appendChild(doc.createTextNode(name));
			td2.appendChild(link);
			td2.setAttribute("class", "relation");
			tr.appendChild(td1);
			tr.appendChild(td2);
			table.appendChild(tr);
		}

	}

	static void addHasPart(Document doc, Element table, String fieldName,
			View view)
	{

		List<String> hasPart = view.getHasPart();
		List<String> hasPartName = view.getHasPartName();
		for (int i = 0; i < hasPart.size(); i++)
		{
			String url = hasPart.get(i);
			String name = hasPartName.get(i);
			Element tr = doc.createElement("tr");
			Element td1 = doc.createElement("td");
			td1.setAttribute("class", "plabel");
			Element td2 = doc.createElement("td");

			td1.appendChild(doc.createTextNode(fieldName));

			// String description = getDescription(str);
			Element link = doc.createElement("a");
			link.setAttribute("href", url);
			link.setAttribute("class", "relationLink");
			// link.appendChild(doc.createTextNode(description + " ("
			// + str.substring(str.lastIndexOf('/') + 1) + ")"));

			link.appendChild(doc.createTextNode(name));
			td2.appendChild(link);
			td2.setAttribute("class", "relation");
			tr.appendChild(td1);
			tr.appendChild(td2);
			table.appendChild(tr);
		}

	}
	// static String getDescription(String str)
	// {
	// String pid = str.substring(str.lastIndexOf('/') + 1);
	//
	// Actions actions = new Actions();
	// Vector<String> descriptions = actions.findObject("edoweb:" + pid,
	// "http://purl.org/dc/elements/1.1/description");
	// if (descriptions != null && !descriptions.isEmpty())
	// return descriptions.firstElement();
	//
	// return "No Description available!";
	// }

}
