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
package de.nrw.hbz.edoweb2.fedora;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;

/**
 * Class TripleSearch
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
class TripleSearch
{
	/*
	 * 
	 * http://localhost:8080/fedora/risearch?type=triples &flush=[true (default
	 * is false)] &lang=SPO|iTQL|RDQL &format=N-Triples|Notation
	 * 3|RDF/XML|Turtle &limit=[1 or more (default is no limit)]
	 * &distinct=[on(default is off)] &stream=[on (default is off)]
	 * &query=QUERY_TEXT_OR_URL &template=[TEMPLATE_TEXT_OR_URL (if applicable)]
	 */
	private final String host;
	private final String user;
	private final String passwd;

	//
	TripleSearch(String host, String user, String passwd)
	{
		this.host = host + "/risearch";
		this.user = user;
		this.passwd = passwd;
	}

	InputStream find(String query, String queryFormat, String outputformat)
			throws IOException
	{
		HttpClient httpClient = new HttpClient();
		httpClient.getState().setCredentials(
				new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(user, passwd));
		HttpMethod method = new GetMethod(host);
		method.setQueryString("type=triples&lang=" + queryFormat + "&format="
				+ outputformat + "&query=" + URIUtil.encodeQuery(query));
		httpClient.executeMethod(method);
		return method.getResponseBodyAsStream();
	}
}
