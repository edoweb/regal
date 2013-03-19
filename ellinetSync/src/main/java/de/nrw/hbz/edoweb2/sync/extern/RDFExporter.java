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

package de.nrw.hbz.edoweb2.sync.extern;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;

/**
 * Class RDFExporter
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, email schnasse@hbz-nrw.de
 * 
 */
public class RDFExporter
{
	String baseUrl = null;

	public RDFExporter(String baseUrl)
	{
		this.baseUrl = baseUrl;

	}

	String query(String query) throws IOException
	{
		HttpClient httpClient = new HttpClient();
		httpClient.getState().setCredentials(
				new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials("fedoraAdmin", "fedoraAdmin1"));
		HttpMethod method = new GetMethod(baseUrl + "/risearch");
		method.setQueryString("type=triples&lang=sparql&format=RDF/XML&query="
				+ URIUtil.encodeQuery(query));
		httpClient.executeMethod(method);
		// System.out.println(method.getQueryString());
		return method.getResponseBodyAsString();
	}

	public void export(String exportDirPath) throws HttpException, IOException
	{
		File exportDir = new File(exportDirPath);

		String queryObjects = "SELECT ?s ?p ?o "
				+ " WHERE"
				+ " {  "
				+ " ?s <info:hbz/hbz-ingest:def/model#isNodeType> \"info:hbz/hbz-ingest:def/model#HBZ_OBJECT\" ."
				+ " ?s ?p ?o" + " }";

		String queryConcepts = "SELECT ?s ?p ?o"
				+ " WHERE"
				+ " {  "
				+ " ?s <info:hbz/hbz-ingest:def/model#isNodeType> \"info:hbz/hbz-ingest:def/model#HBZ_CONCEPT\" ."
				+ " ?s ?p ?o" + " }";

		String queryRealisations = "SELECT ?s ?p ?o"
				+ " WHERE"
				+ " {  "
				+ " ?s <info:hbz/hbz-ingest:def/model#isNodeType> \"info:hbz/hbz-ingest:def/model#HBZ_REALISATION\" ."
				+ " ?s ?p ?o" + " }";

		String queryRepresentations = "SELECT ?s ?p ?o"
				+ " WHERE"
				+ " {  "
				+ " ?s <info:hbz/hbz-ingest:def/model#isNodeType> \"info:hbz/hbz-ingest:def/model#HBZ_REPRESENTATION\" ."
				+ " ?s ?p ?o" + " }";

		String objects = query(queryObjects);
		save(transform(objects), exportDir.getAbsolutePath() + File.separator
				+ "fedoraObjectsDump.rdf");

		String concepts = query(queryConcepts);
		save(transform(concepts), exportDir.getAbsolutePath() + File.separator
				+ "fedoraConceptsDump.rdf");

		String realisations = query(queryRealisations);
		save(transform(realisations), exportDir.getAbsolutePath()
				+ File.separator + "fedoraRealisationsDump.rdf");

		String representations = query(queryRepresentations);
		save(transform(representations), exportDir.getAbsolutePath()
				+ File.separator + "fedoraRepresentationsDump.rdf");
	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param transform
	 * @param string
	 * @throws IOException
	 */
	private void save(String str, String path) throws IOException
	{
		File file = new File(path);

		file.createNewFile();
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(str);
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		finally
		{
			if (writer != null)
				try
				{
					writer.flush();
					writer.close();
				}
				catch (IOException ignored)
				{
				}
		}
	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * sed s/"info:fedora"/"https:\/\/localhost:8443\/fedora\/objects"/g
	 * $startfile > tmp
	 * 
	 * sed -e s/"rdf:Description"/"item"/g tmp \ -e
	 * s/"<rdf:type>.*<\/rdf:type>"/""/g > $startfile
	 * 
	 * @param objects
	 * @return
	 */
	private String transform(String str)
	{
		// System.out.println(str);
		str = str.replaceAll("info:fedora", baseUrl + "/objects");
		str = str.replaceAll("rdf:Description", "item");
		str = str.replaceAll("<rdf:type[^>]*>",
				"<dc:type xmlns:dc=\"http://purl.org/dc/terms/\">");
		str = str.replaceAll("</rdf:type>", "</dc:type>");
		return str;
	}
}
