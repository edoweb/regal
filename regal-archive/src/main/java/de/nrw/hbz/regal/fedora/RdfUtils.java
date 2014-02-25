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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
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
	myGraph = readRdfToGraph(url, inf, accept);
	return graphToString(myGraph, outf);
    }

    public static String graphToString(Graph myGraph, RDFFormat outf) {
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

    public static String readRdfToString(InputStream in, RDFFormat inf,
	    RDFFormat outf, String accept) {
	Graph myGraph = null;
	myGraph = readRdfToGraph(in, inf, accept);
	return graphToString(myGraph, outf);
    }

    /**
     * @param url
     *            A url to read from
     * @param inf
     *            the rdf format of the url's data
     * @param accept
     *            the accept header
     * @return a Graph with the rdf
     */
    public static Graph readRdfToGraph(URL url, RDFFormat inf, String accept) {

	InputStream in = urlToInputStream(url, accept);
	return readRdfToGraph(in, inf, url.toString());
    }

    private static InputStream urlToInputStream(URL url, String accept) {
	URLConnection con = null;
	InputStream inputStream = null;
	try {
	    con = url.openConnection();
	    con.setRequestProperty("Accept", accept);
	    con.connect();
	    inputStream = con.getInputStream();
	} catch (IOException e) {
	    throw new UrlConnectionException(e);
	}
	return inputStream;
    }

    /**
     * @param inputStream
     *            an Input stream containing rdf data
     * @param inf
     *            the rdf format
     * @param baseUrl
     *            see sesame docu
     * @return a Graph representing the rdf in the input stream
     */
    public static Graph readRdfToGraph(InputStream inputStream, RDFFormat inf,
	    String baseUrl) {
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
    public static List<String> getFedoraSubject(InputStream in) {
	Vector<String> pids = new Vector<String>();
	String findpid = null;
	try {
	    RepositoryConnection con = RdfUtils.readRdfInputStreamToRepository(
		    in, RDFFormat.N3);

	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, true);

	    while (statements.hasNext()) {
		Statement st = statements.next();
		findpid = st.getSubject().stringValue()
			.replace("info:fedora/", "");
		pids.add(findpid);
	    }
	} catch (Exception e) {
	    throw new RdfException(e);
	}
	return pids;
    }

    /**
     * Follows the first sameAs link
     * 
     * @param url
     *            a url pointing to rdf data
     * @param pid
     *            the pid will become a subject
     * @return the original string plus the data from the sameAs resource
     */
    public static String followSameAsAndInclude(URL url, String pid) {
	URL followMe = null;
	String str = readRdfToString(url, RDFFormat.NTRIPLES,
		RDFFormat.NTRIPLES, "text/plain");
	followMe = getSameAsLink(url);
	if (followMe == null || !followMe.toString().contains("lobid")) {
	    return str;
	}
	String str1 = readRdfToString(followMe, RDFFormat.NTRIPLES,
		RDFFormat.NTRIPLES, "text/plain");
	str1 = Pattern.compile(followMe.toString()).matcher(str1)
		.replaceAll(Matcher.quoteReplacement(pid));
	return str + "\n" + str1;
    }

    private static URL getSameAsLink(URL sameAsUrl) {
	TupleQueryResult result = null;
	try {
	    RepositoryConnection con = RdfUtils.readRdfUrlToRepository(
		    sameAsUrl, RDFFormat.NTRIPLES);

	    String queryString = "SELECT x, y FROM {x} <http://www.w3.org/2002/07/owl#sameAs> {y}";

	    TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SERQL,
		    queryString);
	    result = tupleQuery.evaluate();

	    while (result.hasNext()) {
		BindingSet bindingSet = result.next();
		Value valueOfY = bindingSet.getValue("y");
		return new URL(valueOfY.stringValue());
	    }
	} catch (Exception e) {
	    throw new RdfException(e);
	} finally {
	    if (result != null)
		try {
		    result.close();
		} catch (QueryEvaluationException e) {

		}
	}

	return null;
    }

    /**
     * @param stream
     *            the stream contains triples in RDFFormat.N3
     * @return a List of objects without info:fedora/ at the beginning
     */
    public static List<String> getFedoraObjects(InputStream stream) {
	Vector<String> findpids = new Vector<String>();
	try {
	    RepositoryConnection con = RdfUtils.readRdfInputStreamToRepository(
		    stream, RDFFormat.N3);

	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, true);

	    while (statements.hasNext()) {
		Statement st = statements.next();
		findpids.add(st.getObject().stringValue()
			.replace("info:fedora/", ""));

	    }
	} catch (Exception e) {
	    throw new RdfException(e);
	}
	return findpids;
    }

    /**
     * @param metadata
     *            a Url with NTRIPLES metadata
     * @return all rdf statements
     */
    public static RepositoryResult<Statement> getStatements(URL metadata) {
	try {
	    RepositoryConnection con = RdfUtils.readRdfUrlToRepository(
		    metadata, RDFFormat.NTRIPLES);
	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, true);
	    return statements;
	} catch (Exception e) {
	    throw new RdfException(e);
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
	    return writeStatements(con, RDFFormat.RDFXML);
	} catch (RepositoryException e) {
	    throw new RdfException(e);
	}

    }

    private static String writeStatements(RepositoryConnection con,
	    RDFFormat outf) {
	StringWriter out = new StringWriter();
	RDFWriter writer = Rio.createWriter(outf, out);
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
	} catch (RepositoryException e) {
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

		}
	    }
	}
    }

    /**
     * @param subject
     *            find triples with this subject
     * @param predicate
     *            find triples with this predicate
     * @param rdfUrl
     *            url with rdf data
     * @param inf
     *            format of the rdf data
     * @param accept
     *            accept header for the url
     * @return a list of rdf objects
     */
    public static List<String> findRdfObjects(String subject, String predicate,
	    URL rdfUrl, RDFFormat inf, String accept) {
	RepositoryConnection con = RdfUtils.readRdfUrlToRepository(rdfUrl, inf);
	return findRdfObjects(subject, predicate, con);
    }

    private static List<String> findRdfObjects(String subject,
	    String predicate, RepositoryConnection con) {

	List<String> list = new Vector<String>();
	TupleQueryResult result = null;
	try {
	    String queryString = "SELECT  x, y FROM {x} <" + predicate
		    + "> {y}";
	    TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SERQL,
		    queryString);
	    result = tupleQuery.evaluate();

	    while (result.hasNext()) {
		BindingSet bindingSet = result.next();
		Value valueOfY = bindingSet.getValue("y");
		list.add(valueOfY.stringValue());
	    }
	    return list;
	} catch (Exception e) {
	    throw new RdfException(e);
	}
    }

    private static RepositoryConnection readRdfUrlToRepository(URL rdfUrl,
	    RDFFormat inf) {
	RepositoryConnection con = null;
	try {
	    Repository myRepository = new SailRepository(new MemoryStore());
	    myRepository.initialize();
	    con = myRepository.getConnection();
	    String baseURI = rdfUrl.toString();
	    con.add(rdfUrl, baseURI, inf);
	    return con;
	} catch (Exception e) {
	    throw new RdfException(e);
	}
    }

    private static RepositoryConnection readRdfInputStreamToRepository(
	    InputStream is, RDFFormat inf) {
	RepositoryConnection con = null;
	try {

	    Repository myRepository = new SailRepository(new MemoryStore());
	    myRepository.initialize();
	    con = myRepository.getConnection();
	    String baseURI = "";
	    con.add(is, baseURI, inf);
	    return con;
	} catch (Exception e) {
	    throw new RdfException(e);
	}
    }

    /**
     * Adds the given statement to the stream and removes all statements with
     * same subject and predicate
     * 
     * @param subject
     *            rdf subject
     * @param predicate
     *            rdf predicate
     * @param object
     *            rdf object
     * @param isLiteral
     *            true if the object is a literl
     * @param metadata
     *            the metadata as String
     * @return modified Metadata
     */
    public static String replaceTriple(String subject, String predicate,
	    String object, boolean isLiteral, final String metadata) {
	try {

	    InputStream is = new ByteArrayInputStream(
		    metadata.getBytes("UTF-8"));
	    RepositoryConnection con = readRdfInputStreamToRepository(is,
		    RDFFormat.NTRIPLES);
	    ValueFactory f = con.getValueFactory();
	    URI s = f.createURI(subject);
	    URI p = f.createURI(predicate);
	    Value o = null;
	    if (!isLiteral) {
		o = f.createURI(object);
	    } else {
		o = f.createLiteral(object);
	    }
	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, true);
	    while (statements.hasNext()) {
		Statement st = statements.next();
		if (st.getSubject().stringValue().equals(subject)
			&& st.getPredicate().stringValue().equals(predicate)) {
		    con.remove(st);
		}
	    }

	    con.add(s, p, o);
	    return writeStatements(con, RDFFormat.NTRIPLES);
	} catch (RepositoryException e) {
	    throw new RdfException(e);
	} catch (UnsupportedEncodingException e) {
	    throw new RdfException(e);
	}

    }

    /**
     * @param subject
     *            the triples subject
     * @param predicate
     *            the triples predicate
     * @param object
     *            the triples object
     * @param metadata
     *            ntriple string
     * @return true if the metadata string contains the triple
     */
    public static boolean hasTriple(String subject, String predicate,
	    String object, String metadata) {
	try {
	    InputStream is = new ByteArrayInputStream(
		    metadata.getBytes("UTF-8"));
	    RepositoryConnection con = readRdfInputStreamToRepository(is,
		    RDFFormat.NTRIPLES);

	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, true);
	    while (statements.hasNext()) {
		Statement st = statements.next();
		if (st.getSubject().stringValue().equals(subject)
			&& st.getPredicate().stringValue().equals(predicate)) {
		    return true;
		}
	    }

	} catch (RepositoryException e) {
	    throw new RdfException(e);
	} catch (UnsupportedEncodingException e) {
	    throw new RdfException(e);
	}
	return false;
    }

    /**
     * @param subject
     *            the triples subject
     * @param predicate
     *            the triples predicate
     * @param object
     *            the triples object
     * @param isLiteral
     *            true, if object is a literal
     * @param metadata
     *            ntriple rdf-string to add the triple
     * @return the string together with the new triple
     */
    public static String addTriple(String subject, String predicate,
	    String object, boolean isLiteral, String metadata) {
	try {
	    RepositoryConnection con = null;
	    if (metadata != null) {
		InputStream is = new ByteArrayInputStream(
			metadata.getBytes("UTF-8"));
		con = readRdfInputStreamToRepository(is, RDFFormat.NTRIPLES);
	    } else {
		Repository myRepository = new SailRepository(new MemoryStore());
		myRepository.initialize();
		con = myRepository.getConnection();
	    }
	    ValueFactory f = con.getValueFactory();
	    URI s = f.createURI(subject);
	    URI p = f.createURI(predicate);
	    Value o = null;
	    if (!isLiteral) {
		o = f.createURI(object);
	    } else {
		o = f.createLiteral(object);
	    }
	    con.add(s, p, o);
	    return writeStatements(con, RDFFormat.NTRIPLES);
	} catch (RepositoryException e) {
	    throw new RdfException(e);
	} catch (UnsupportedEncodingException e) {
	    throw new RdfException(e);
	}
    }

    /**
     * @param metadata
     *            n-triple
     */
    public static void validate(String metadata) {
	try {
	    InputStream is = new ByteArrayInputStream(
		    metadata.getBytes("UTF-8"));
	    readRdfInputStreamToRepository(is, RDFFormat.NTRIPLES);
	} catch (UnsupportedEncodingException e) {
	    throw new RdfException(e);
	}
    }
}
