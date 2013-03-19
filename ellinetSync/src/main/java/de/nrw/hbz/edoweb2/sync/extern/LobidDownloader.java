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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import de.nrw.hbz.edoweb2.datatypes.Link;

/**
 * Class LobidDownloader
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
public class LobidDownloader
{
	static public Link[] getLobidData(String htNummer)
	{
		Vector<Link> result = new Vector<Link>();

		BufferedInputStream in = null;
		RepositoryConnection con = null;
		try
		{
			// wget --header "Accept: text/turtle"
			// "http://lobid.org/resource/HT002191600"
			// application/rdf+xml
			URL url = new URL("http://lobid.org/resource/" + htNummer);

			URLConnection urlcon = url.openConnection();
			urlcon.setRequestProperty("Accept", "text/turtle");

			in = new BufferedInputStream(urlcon.getInputStream());

			Repository myRepository = new SailRepository(new MemoryStore());
			myRepository.initialize();

			con = myRepository.getConnection();
			String baseURI = "http://example.org/example/local";

			ValueFactory f = myRepository.getValueFactory();
			URI objectId = f.createURI(url.toExternalForm());
			con.add(in, baseURI, RDFFormat.N3);
			RepositoryResult<Statement> statements = con.getStatements(
					objectId, null, null, true);

			while (statements.hasNext())
			{
				Statement st = statements.next();
				URI predUri = st.getPredicate();
				Value objUri = st.getObject();
				if (objUri.stringValue().compareTo(
						"http://www.w3.org/2004/03/trix/rdfg-1/Graph") == 0)
				{
					continue;
				}
				else if (objUri.stringValue().startsWith("node"))
					continue;
				String predicate = predUri.stringValue();
				predicate = predicate.replace(
						"http://purl.org/dc/elements/1.1/",
						"http://purl.org/dc/terms/");
				Link link = new Link();
				link.setObject(objUri.stringValue());
				link.setPredicate(predicate);

				try
				{
					new URL(link.getObject());
					link.setLiteral(false);
				}
				catch (MalformedURLException e)
				{

				}
				result.add(link);
				// System.out.println(" READ: <" + htNummer + "> <"
				// + link.getPredicate() + "> <" + link.getObject() + ">");

			}

			objectId = f.createURI("http://lobid.org/resource/" + htNummer);
			con.add(in, baseURI, RDFFormat.N3);

			statements = con.getStatements(objectId, null, null, true);

			while (statements.hasNext())
			{
				Statement st = statements.next();
				URI predUri = st.getPredicate();
				Value objUri = st.getObject();

				if (objUri.stringValue().startsWith("node"))
					continue;
				String predicate = predUri.stringValue();
				predicate = predicate.replace(
						"http://purl.org/dc/elements/1.1/",
						"http://purl.org/dc/terms/");
				Link link = new Link();
				link.setObject(objUri.stringValue());

				link.setPredicate(predicate);

				try
				{
					new URL(link.getObject());
					link.setLiteral(false);
				}
				catch (MalformedURLException e)
				{

				}

				// System.out.println(" READ: <" + htNummer + "> <"
				// + link.getPredicate() + "> <" + link.getObject() + ">");
				result.add(link);
			}

			// HBZLink link = new HBZLink();
			// link.setPredicate(DigitoolQDc2RdfMap.URI);
			// link.setObject(url.toExternalForm());
			// result.add(link);

			Link[] resultArr = new Link[result.size()];
			return result.toArray(resultArr);

		}
		catch (MalformedURLException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		catch (RepositoryException e)
		{

			e.printStackTrace();
		}
		catch (RDFParseException e)
		{

			e.printStackTrace();
		}
		catch (Exception e)
		{

		}
		finally
		{
			if (in != null)
				try
				{
					in.close();
				}
				catch (IOException ignored)
				{

				}
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (RepositoryException e)
				{

				}
			}
		}
		return null;
	}

}
