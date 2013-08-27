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
package de.nrw.hbz.regal.fedora;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.sail.memory.MemoryStore;

import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.exceptions.ArchiveException;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class RdfUtils {

    /**
     * @param url
     *            the url to read from
     * @param inf
     *            the rdf format
     * @param outf
     *            the rdf output format
     * @param accept
     *            the accept header for the url
     * @return a String containing the url in a certain rdf format
     */
    public static String readRdfToString(URL url, RDFFormat inf,
	    RDFFormat outf, String accept) {

	Graph myGraph = null;
	try {
	    myGraph = readRdfUrlToGraph(url, inf, accept);
	} catch (IOException e) {
	    throw new RdfException(e);
	}

	StringWriter out = new StringWriter();
	RDFWriter writer = Rio.createWriter(outf, out);
	try {
	    writer.startRDF();
	    for (Statement st : myGraph) {
		writer.handleStatement(st);
	    }
	    writer.endRDF();
	} catch (RDFHandlerException e) {
	    throw new RdfException(e);
	}
	return out.getBuffer().toString();

    }

    /**
     * @param url
     *            A url to read from
     * @param inf
     *            the rdf format of the url's data
     * @param accept
     *            the accept header
     * @return a Graph with the rdf
     * @throws IOException
     *             if something goes wrong
     */
    public static Graph readRdfUrlToGraph(URL url, RDFFormat inf, String accept)
	    throws IOException {
	URLConnection con = url.openConnection();
	con.setRequestProperty("Accept", accept);
	con.connect();
	InputStream inputStream = con.getInputStream();
	return readRdfInputstreamToGraph(inputStream, inf, url.toString());
    }

    /**
     * @param inputStream
     *            an Input stream containing rdf data
     * @param inf
     *            the rdf format
     * @param baseUrl
     *            see sesame docu
     * @return a Graph representing the rdf in the input stream
     * @throws IOException
     *             if something goes wrong
     */
    public static Graph readRdfInputstreamToGraph(InputStream inputStream,
	    RDFFormat inf, String baseUrl) throws IOException {
	RDFParser rdfParser = Rio.createParser(inf);
	org.openrdf.model.Graph myGraph = new TreeModel();
	StatementCollector collector = new StatementCollector(myGraph);
	rdfParser.setRDFHandler(collector);
	try {
	    rdfParser.parse(inputStream, baseUrl);
	} catch (IOException e) {
	    throw new RdfException(e);
	} catch (RDFParseException e) {
	    throw new RdfException(e);
	} catch (RDFHandlerException e) {
	    throw new RdfException(e);
	}

	return myGraph;
    }

    /**
     * @param in
     *            rdf data in RDFFormat.N3
     * @return all subjects without info:fedora/ at the beginning
     */
    public static Vector<String> getFedoraSubject(InputStream in) {
	Vector<String> pids = new Vector<String>();
	String findpid = null;
	RepositoryConnection con = null;
	Repository myRepository = new SailRepository(new MemoryStore());
	try {
	    myRepository.initialize();
	    con = myRepository.getConnection();
	    String baseURI = "";

	    con.add(in, baseURI, RDFFormat.N3);

	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, true);

	    while (statements.hasNext()) {
		Statement st = statements.next();
		findpid = st.getSubject().stringValue()
			.replace("info:fedora/", "");
		pids.add(findpid);
	    }
	} catch (RepositoryException e) {

	    e.printStackTrace();
	} catch (RDFParseException e) {

	    e.printStackTrace();
	} catch (IOException e) {

	    e.printStackTrace();
	} finally {
	    if (con != null) {
		try {
		    con.close();
		} catch (RepositoryException e) {
		    e.printStackTrace();
		}
	    }
	}
	return pids;
    }

    /**
     * Follows the first sameAs link
     * 
     * @param str
     *            rdf data as RDFFormat.NTRIPLES
     * @param pid
     *            the pid will become a subject
     * @return the original string plus the data from the sameAs resource
     */
    public static String includeSameAs(String str, String pid) {
	// <edoweb:4245081> <http://www.w3.org/2002/07/owl#sameAs>
	// <http://lobid.org/resource/ZDB2502002-X>
	// System.out.println(pid + " include sameAs");
	URL url = null;
	try {
	    Repository myRepository = new SailRepository(new MemoryStore());
	    myRepository.initialize();

	    String baseURI = "http://example.org/example/local";

	    RepositoryConnection con = myRepository.getConnection();
	    try {
		con.add(new StringReader(str), baseURI, RDFFormat.NTRIPLES);

		String queryString = "SELECT x, y FROM {x} <http://www.w3.org/2002/07/owl#sameAs> {y}";

		TupleQuery tupleQuery = con.prepareTupleQuery(
			QueryLanguage.SERQL, queryString);
		TupleQueryResult result = tupleQuery.evaluate();
		try {
		    while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			Value valueOfY = bindingSet.getValue("y");
			url = new URL(valueOfY.stringValue());

		    }
		} catch (MalformedURLException e) {
		    e.printStackTrace();
		} finally {
		    result.close();
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    } finally {
		con.close();
	    }
	} catch (OpenRDFException e) {
	    e.printStackTrace();
	}

	if (url == null) {

	    return str;
	}

	InputStream in = null;
	try {

	    URLConnection con = url.openConnection();
	    con.setRequestProperty("Accept", "text/plain");
	    con.connect();

	    in = con.getInputStream();
	    StringWriter writer = new StringWriter();
	    IOUtils.copy(in, writer, "UTF-8");
	    String str1 = writer.toString();

	    str1 = Pattern.compile(url.toString()).matcher(str1)
		    .replaceAll(Matcher.quoteReplacement(pid));
	    return str + "\n" + str1;

	} catch (IOException e) {
	    throw new ArchiveException(pid
		    + " IOException happens during copy operation.", e);
	} finally {
	    try {
		if (in != null)
		    in.close();
	    } catch (IOException e) {
		throw new ArchiveException(pid
			+ " wasn't able to close stream.", e);
	    }
	}
	// throw new ArchiveException("Not able to include sameAs data.");
    }

    /**
     * @param stream
     *            the stream contains triples in RDFFormat.N3
     * @return a List of objects without info:fedora/ at the beginning
     */
    public static List<String> getFedoraObjects(InputStream stream) {
	Vector<String> findpids = new Vector<String>();
	RepositoryConnection con = null;
	Repository myRepository = new SailRepository(new MemoryStore());
	try {
	    myRepository.initialize();
	    con = myRepository.getConnection();
	    String baseURI = "";

	    con.add(stream, baseURI, RDFFormat.N3);

	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, true);

	    while (statements.hasNext()) {
		Statement st = statements.next();
		findpids.add(st.getObject().stringValue()
			.replace("info:fedora/", ""));

	    }
	} catch (RepositoryException e) {

	    e.printStackTrace();
	} catch (RDFParseException e) {

	    e.printStackTrace();
	} catch (IOException e) {

	    e.printStackTrace();
	} finally {
	    if (con != null) {
		try {
		    con.close();
		} catch (RepositoryException e) {
		    e.printStackTrace();
		}
	    }
	}
	return findpids;
    }

    /**
     * @param metadata
     *            a Url with NTRIPLES metadata
     * @return all rdf statements
     */
    public static RepositoryResult<Statement> getStatements(URL metadata) {
	InputStream in = null;
	RepositoryConnection con = null;
	try {
	    in = metadata.openStream();

	    Repository myRepository = new SailRepository(new MemoryStore());

	    myRepository.initialize();
	    con = myRepository.getConnection();
	    String baseURI = "";

	    con.add(in, baseURI, RDFFormat.NTRIPLES);

	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, true);

	    return statements;

	} catch (Exception e) {
	    throw new RdfException(e);
	} finally {
	    if (con != null) {
		try {
		    con.close();
		} catch (RepositoryException e) {
		    throw new RdfException(e);
		}
	    }
	}
    }

    /**
     * @param pid
     *            a pid
     * @param links
     *            all links
     * @return a valid relsExt datastream as string
     */
    public static String getFedoraRelsExt(String pid, List<Link> links) {
	RepositoryConnection con = null;
	SailRepository myRepository = new SailRepository(new MemoryStore());
	try {
	    myRepository.initialize();
	    con = myRepository.getConnection();
	    addStatements(pid, links, con, myRepository);
	    return writeStatements(con);
	} catch (RepositoryException e) {
	    throw new RdfException(e);
	}

    }

    private static String writeStatements(RepositoryConnection con)
	    throws RepositoryException {
	StringWriter out = new StringWriter();
	RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, out);
	String result = null;
	try {
	    writer.startRDF();
	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, false);

	    while (statements.hasNext()) {
		Statement statement = statements.next();
		writer.handleStatement(statement);
	    }
	    writer.endRDF();
	    result = out.toString();

	} catch (RDFHandlerException e) {
	    throw new RdfException(e);
	}
	return result;
    }

    private static void addStatements(String pid, List<Link> links,
	    RepositoryConnection con, SailRepository myRepository)
	    throws RepositoryException {

	ValueFactory f = myRepository.getValueFactory();
	URI subject = f.createURI("info:fedora/" + pid);
	for (Link link : links) {
	    URI predicate = f.createURI(link.getPredicate());
	    if (link.getObject() == null || link.getObject().isEmpty())
		continue;
	    if (link.isLiteral()) {
		Literal object = f.createLiteral(link.getObject());
		con.add(subject, predicate, object);
	    } else {
		try {
		    URI object = f.createURI(link.getObject());
		    con.add(subject, predicate, object);
		} catch (IllegalArgumentException e) {
		    // TODO implement
		}
	    }
	}
    }
}
