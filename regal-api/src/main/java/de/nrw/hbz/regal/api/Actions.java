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

import static de.nrw.hbz.regal.datatypes.Vocabulary.REL_CONTENT_TYPE;
import static de.nrw.hbz.regal.datatypes.Vocabulary.REL_IS_NODE_TYPE;
import static de.nrw.hbz.regal.datatypes.Vocabulary.TYPE_OBJECT;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.HAS_PART;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.IS_MEMBER_OF;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.IS_PART_OF;
import static de.nrw.hbz.regal.fedora.FedoraVocabulary.ITEM_ID;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.culturegraph.mf.Flux;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import de.nrw.hbz.regal.ArchiveFactory;
import de.nrw.hbz.regal.ArchiveInterface;
import de.nrw.hbz.regal.datatypes.ComplexObject;
import de.nrw.hbz.regal.datatypes.Link;
import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.exceptions.ArchiveException;
import de.nrw.hbz.regal.exceptions.NodeNotFoundException;
import de.nrw.hbz.regal.fedora.FedoraVocabulary;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
class Actions
{
	final static Logger logger = LoggerFactory.getLogger(Actions.class);
	private ArchiveInterface archive = null;
	private String fedoraExtern = null;
	private String serverName = null;
	private String uriPrefix = null;
	private String lobidUrl = null;
	private String verbundUrl = null;
	private String dataciteUrl;
	private String baseUrl;
	private String culturegraphUrl;

	// String namespace = null;

	/**
	 * @throws IOException
	 *             if properties can not be loaded.
	 */
	Actions() throws IOException
	{
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/api.properties"));
		fedoraExtern = properties.getProperty("fedoraExtern");
		serverName = properties.getProperty("serverName");
		archive = ArchiveFactory.getArchiveImpl(
				properties.getProperty("fedoraIntern"),
				properties.getProperty("user"),
				properties.getProperty("password"));
		uriPrefix = serverName + "/" + "resources" + "/";

		properties.load(getClass().getResourceAsStream(
				"/externalLinks.properties"));
		lobidUrl = properties.getProperty("lobidUrl");
		verbundUrl = properties.getProperty("verbundUrl");
		dataciteUrl = properties.getProperty("dataciteUrl");
		baseUrl = properties.getProperty("baseUrl");
		culturegraphUrl = properties.getProperty("culturegraphUrl");

	}

	/**
	 * @param pids
	 *            The pids that must be deleted
	 * @param wait
	 *            If wait is true the method will wait few secs in order to get
	 *            sync with the fedora triple store. TODO: Remove this ugly
	 *            workaround.
	 * @return A short message.
	 */
	String deleteAll(List<String> pids, boolean wait)
	{
		if (pids == null || pids.isEmpty())
			throw new HttpArchiveException(304, "Nothing to delete!");
		logger.info("Delete All");
		StringBuffer msg = new StringBuffer();
		for (String pid : pids)
		{

			try
			{
				msg.append(delete(pid, wait) + "\n");
			}
			catch (Exception e)
			{
				logger.warn(pid + " " + e.getMessage());
			}
		}

		return msg.toString();
	}

	/**
	 * @param object
	 *            A representation of this object will be created in the
	 *            archive.
	 * @param wait
	 *            If wait is true the method will wait few secs in order to get
	 *            sync with the fedora triple store. TODO: Remove this ugly
	 *            workaround.
	 * @return a short message
	 */
	String create(ComplexObject object, boolean wait)
	{
		archive.createComplexObject(object);
		if (wait)
			waitWorkaround();

		return object.getRoot().getPID() + " successfully created!";
	}

	/**
	 * @param pid
	 *            The pid that must be deleted
	 * @param wait
	 *            If wait is true the method will wait few secs in order to get
	 *            sync with the fedora triple store. TODO: Remove this ugly
	 *            workaround.
	 * @return A short Message
	 */
	String delete(String pid, boolean wait)
	{

		String msg = "";
		archive.deleteComplexObject(pid);

		try
		{
			outdex(pid);
		}
		catch (Exception e)
		{
			msg = e.getMessage();
		}
		if (wait)
			waitWorkaround();

		return pid + " successfully deleted! " + msg;
	}

	String deleteMetadata(String pid)
	{

		archive.deleteDatastream(pid, "metadata");

		return pid + ": metadata - datastream successfully deleted! ";
	}

	String deleteData(String pid)
	{
		archive.deleteDatastream(pid, "data");
		return pid + ": data - datastream successfully deleted! ";
	}

	// -------------------------------------------------------

	/**
	 * @param type
	 *            The objectTyp
	 * @return A list of pids with type {@type}
	 */
	Vector<String> findByType(String type)
	{
		Vector<String> pids = new Vector<String>();
		String query = "* <" + REL_CONTENT_TYPE + "> \"" + type + "\"";
		InputStream stream = archive.findTriples(query, FedoraVocabulary.SPO,
				FedoraVocabulary.N3);
		String findpid = null;
		RepositoryConnection con = null;
		Repository myRepository = new SailRepository(new MemoryStore());
		try
		{
			myRepository.initialize();
			con = myRepository.getConnection();
			String baseURI = "";

			con.add(stream, baseURI, RDFFormat.N3);

			RepositoryResult<Statement> statements = con.getStatements(null,
					null, null, true);

			while (statements.hasNext())
			{
				Statement st = statements.next();
				findpid = st.getSubject().stringValue()
						.replace("info:fedora/", "");
				pids.add(findpid);
			}
		}
		catch (RepositoryException e)
		{

			e.printStackTrace();
		}
		catch (RDFParseException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (RepositoryException e)
				{
					e.printStackTrace();
				}
			}
		}
		return pids;
	}

	/**
	 * @param rdfQuery
	 *            A sparql query
	 * @return a short message
	 */
	String findSubject(String rdfQuery)
	{
		String volumePid = null;
		InputStream stream = archive.findTriples(rdfQuery,
				FedoraVocabulary.SPARQL, FedoraVocabulary.N3);

		RepositoryConnection con = null;
		Repository myRepository = new SailRepository(new MemoryStore());
		try
		{
			myRepository.initialize();
			con = myRepository.getConnection();
			String baseURI = "";

			con.add(stream, baseURI, RDFFormat.N3);

			RepositoryResult<Statement> statements = con.getStatements(null,
					null, null, true);

			while (statements.hasNext())
			{
				Statement st = statements.next();
				volumePid = st.getSubject().stringValue()
						.replace("info:fedora/", "");
				break;
			}
		}
		catch (RepositoryException e)
		{

			e.printStackTrace();
		}
		catch (RDFParseException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (RepositoryException e)
				{
					e.printStackTrace();
				}
			}
		}

		return volumePid;
	}

	/**
	 * 
	 * @param pid
	 *            The pid
	 * @param pred
	 *            the predicate
	 * @return A list of objects that are referenced by pid/predicate
	 *         combination.
	 */
	List<String> findObject(String pid, String pred)
	{
		String query = "<info:fedora/" + pid + "> <" + pred + "> *";
		logger.info(query);
		InputStream stream = archive.findTriples(query, FedoraVocabulary.SPO,
				FedoraVocabulary.N3);
		Vector<String> findpids = new Vector<String>();
		RepositoryConnection con = null;
		Repository myRepository = new SailRepository(new MemoryStore());
		try
		{
			myRepository.initialize();
			con = myRepository.getConnection();
			String baseURI = "";

			con.add(stream, baseURI, RDFFormat.N3);

			RepositoryResult<Statement> statements = con.getStatements(null,
					null, null, true);

			while (statements.hasNext())
			{
				Statement st = statements.next();
				findpids.add(st.getObject().stringValue()
						.replace("info:fedora/", ""));

			}
		}
		catch (RepositoryException e)
		{

			e.printStackTrace();
		}
		catch (RDFParseException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (RepositoryException e)
				{
					e.printStackTrace();
				}
			}
		}
		return findpids;
	}

	/**
	 * @param pid
	 *            The pid to read the data from
	 * @return the data part of the pid
	 * @throws URISyntaxException
	 *             if the data url is not wellformed
	 */
	Response readData(String pid) throws URISyntaxException
	{

		Node node = null;

		node = readNode(pid);

		if (node != null)
		{
			return Response.temporaryRedirect(
					new java.net.URI(fedoraExtern + "/objects/" + pid
							+ "/datastreams/data/content")).build();

		}
		return null;

	}

	/**
	 * @param pid
	 *            The pid to read the dublin core stream from.
	 * @return A DCBeanAnnotated java object.
	 */
	DCBeanAnnotated readDC(String pid)
	{

		logger.info("Read DC");

		Node node = readNode(pid);
		if (node != null)
			return new DCBeanAnnotated(node);

		return null;

	}

	/**
	 * @param pid
	 *            The pid to read metadata stream.
	 * @return The Metadatastream
	 * @throws URISyntaxException
	 *             if the metadata url is not valid.
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	String readMetadata(String pid) throws URISyntaxException,
			MalformedURLException, IOException
	{

		String result = null;
		Node node = readNode(pid);

		if (node != null)
		{

			InputStream in = null;
			try
			{
				in = new URL(fedoraExtern + "/objects/" + pid
						+ "/datastreams/metadata/content").openStream();
				result = IOUtils.toString(in);

			}
			finally
			{
				if (in != null)
					IOUtils.closeQuietly(in);
			}

		}
		if (result == null || result.isEmpty())
		{
			throw new HttpArchiveException(404, "Datastream does not exist!");
		}
		return result;

	}

	/**
	 * @param pid
	 *            the pid that must be updated
	 * @param content
	 *            the data as byte array
	 * @param mimeType
	 *            the mimetype of the data
	 * @return A short message
	 * @throws IOException
	 *             if data can not be written to a tmp file
	 */
	String updateData(String pid, byte[] content, String mimeType)
			throws IOException
	{

		if (content == null || content.length == 0)
		{
			throw new ArchiveException(pid
					+ " you've tried to upload an empty byte array."
					+ " This action is not supported. Use HTTP DELETE instead.");
		}
		File tmp = File.createTempFile("Datafile", "tmp");
		tmp.deleteOnExit();

		FileUtils.writeByteArrayToFile(tmp, content);
		Node node = readNode(pid);
		if (node != null)
		{
			node.setUploadData(tmp.getAbsolutePath(), "data", mimeType);
			archive.updateNode(pid, node);
		}

		return pid + " data successfully updated!";

	}

	/**
	 * @param pid
	 *            the pid that must be updated
	 * @param content
	 *            the data as byte array
	 * @param mimeType
	 *            the mimetype of the data
	 * @return A short message
	 * @throws IOException
	 *             if data can not be written to a tmp file
	 */
	String updateData(String pid, InputStream content, String mimeType)
			throws IOException
	{

		if (content == null)
		{
			throw new ArchiveException(pid
					+ " you've tried to upload an empty stream."
					+ " This action is not supported. Use HTTP DELETE instead.");
		}
		File tmp = File.createTempFile("Datafile", "tmp");
		tmp.deleteOnExit();

		// File tmp = new File("/tmp/edoweb.zip");

		// THIS DOESN'T WORK and will end in large Files
		// TODO find out what happens here
		// IOUtils.copy(content, new FileWriter(tmp));

		// go on with the classic method
		OutputStream out = null;
		try
		{

			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(tmp);
			while ((read = content.read(bytes)) != -1)
			{
				out.write(bytes, 0, read);
			}

		}
		catch (IOException e)
		{

			throw new IOException(e);
		}
		finally
		{
			try
			{

				if (out != null)
					out.close();
			}
			catch (IOException e)
			{

			}
		}

		Node node = readNode(pid);
		if (node != null)
		{
			node.setUploadData(tmp.getAbsolutePath(), "data", mimeType);
			archive.updateNode(pid, node);
		}

		return pid + " data successfully updated!";

	}

	/**
	 * @param pid
	 *            The pid that must be updated
	 * @param content
	 *            A dublin core object
	 * @return a short message
	 */
	String updateDC(String pid, DCBeanAnnotated content)
	{

		logger.info("Update DC");

		content.trim();
		Node node = readNode(pid);
		node.setContributer(content.getContributer());
		node.setCoverage(content.getCoverage());
		node.setCreator(content.getCreator());
		node.setDate(content.getDate());
		node.setDescription(content.getDescription());
		node.setFormat(content.getFormat());
		node.setIdentifier(content.getIdentifier());
		node.setLanguage(content.getLanguage());
		node.setPublisher(content.getPublisher());
		node.setDescription(content.getDescription());
		node.setRights(content.getRights());
		node.setSource(content.getSource());
		node.setSubject(content.getSubject());
		node.setTitle(content.getTitle());
		node.setType(content.getType());
		archive.updateNode(pid, node);

		return pid + " dc successfully updated!";

	}

	/**
	 * @param pid
	 *            The pid that must be updated
	 * @param content
	 *            The metadata as rdf string
	 * @return a short message
	 * @throws IOException
	 *             if the metadata can not be cached
	 */
	String updateMetadata(String pid, String content) throws IOException
	{

		if (content == null || content.isEmpty())
		{
			throw new ArchiveException(pid
					+ "You've tried to upload an empty string."
					+ " This action is not supported."
					+ " Use HTTP DELETE instead.");
		}
		File file = File.createTempFile("tmpmetadata", "tmp");
		file.deleteOnExit();
		FileUtils.writeStringToFile(file, content);
		Node node = readNode(pid);
		if (node != null)
		{
			node.setMetadataFile(file.getAbsolutePath());
			archive.updateNode(pid, node);
		}

		return pid + " metadata successfully updated!";

	}

	/**
	 * @param pid
	 *            A pid
	 * @return true if the pid exists and fals if not
	 */
	boolean nodeExists(String pid)
	{
		return archive.nodeExists(pid);
	}

	/**
	 * @param pid
	 *            A pid
	 * @return the pid prefixed with a certain namespace.
	 */
	String addUriPrefix(String pid)
	{
		return archive.addUriPrefix(pid);
	}

	/**
	 * @param namespace
	 *            the namespace of the pid
	 * @return a new generated empty pid
	 */
	String getPid(String namespace)
	{
		return archive.getPids(namespace, 1)[0];
	}

	/**
	 * @param pid
	 *            The pid to which links must be added
	 * @param links
	 *            list of links
	 * @return a short message
	 */
	String addLinks(String pid, List<Link> links)
	{

		Node node = readNode(pid);
		for (Link link : links)
		{
			node.addRelation(link);
		}
		// long start = System.nanoTime();
		archive.updateNode(node.getPID(), node);
		// long elapsed = System.nanoTime() - start;
		// System.out.println("update node duration: " + elapsed);

		return pid + " " + links + " links successfully added.";

	}

	/**
	 * @param pid
	 *            The pid to which links must be added uses: Vector<Link> v =
	 *            new Vector<Link>(); v.add(link); return addLinks(pid, v);
	 * @param link
	 *            a link
	 * @return a short message
	 */
	String addLink(String pid, Link link)
	{
		Vector<Link> v = new Vector<Link>();
		v.add(link);
		return addLinks(pid, v);
	}

	/**
	 * If a link with same predicate exists it will be replaced.
	 * 
	 * @param pid
	 *            pid of the object
	 * @param link
	 *            link to be updated
	 * @return a short message
	 */
	String updateLink(String pid, Link link)
	{

		Node node = readNode(pid);
		Vector<Link> links = node.getRelsExt();
		Iterator<Link> iterator = links.iterator();

		while (iterator.hasNext())
		{
			Link l = iterator.next();
			if (l.getPredicate().compareTo(link.getPredicate()) == 0)
			{
				links.remove(l);
			}
		}

		links.add(link);
		node.setRelsExt(links);
		archive.updateNode(node.getPID(), node);
		return pid + " " + link + " link successfully updated.";

	}

	/**
	 * @param pid
	 *            the pid of a node that must be published on the oai interface
	 * @return A short message.
	 */
	String makeOAISet(String pid)
	{

		String ddc = null;
		Node node = readNode(pid);
		try
		{
			URL metadata = new URL(fedoraExtern + "/objects/" + pid
					+ "/datastreams/metadata/content");
			InputStream in = null;
			if (metadata != null)
			{

				try
				{
					in = metadata.openStream();

					RepositoryConnection con = null;
					Repository myRepository = new SailRepository(
							new MemoryStore());
					try
					{
						myRepository.initialize();
						con = myRepository.getConnection();
						String baseURI = "";

						con.add(in, baseURI, RDFFormat.N3);

						RepositoryResult<Statement> statements = con
								.getStatements(null, null, null, true);

						while (statements.hasNext())
						{
							Statement st = statements.next();

							String rdfPredicate = st.getPredicate()
									.stringValue();
							if (rdfPredicate
									.compareTo("http://purl.org/dc/terms/subject") == 0)
							{
								String rdfObject = st.getObject().stringValue();
								if (rdfObject
										.startsWith("http://dewey.info/class/"))
								{
									ddc = rdfObject.subSequence(
											rdfObject.length() - 4,
											rdfObject.length() - 1).toString();
									System.out.println("Found rdf ddc: " + ddc);

									String name = ddcmap(ddc);
									String spec = "ddc:" + ddc;
									String namespace = "oai";
									String oaipid = namespace + ":" + ddc;
									if (!this.nodeExists(oaipid))
									{
										createOAISet(name, spec, oaipid);
									}
									linkObjectToOaiSet(node, spec, oaipid);
								}
							}

						}

					}
					catch (RepositoryException e)
					{

						e.printStackTrace();
					}
					catch (RDFParseException e)
					{

						e.printStackTrace();
					}
					catch (IOException e)
					{

						e.printStackTrace();
					}
					finally
					{
						if (con != null)
						{
							try
							{
								con.close();
							}
							catch (RepositoryException e)
							{
								e.printStackTrace();
							}
						}
					}
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				finally
				{
					IOUtils.closeQuietly(in);
				}
			}
			// TODO this block is deprecated as soon as lobid works like
			// expected
			if (ddc == null && node.getSubject() != null)
				for (String subject : node.getSubject())
				{
					if (subject.startsWith("ddc"))
					{
						int end = 7;
						if (subject.length() < 7)
							end = subject.length();
						ddc = subject.subSequence(4, end).toString();
						logger.info("Found ddc: " + ddc);

						String name = ddcmap(ddc);
						String spec = "ddc:" + ddc;
						String namespace = "oai";
						String oaipid = namespace + ":" + ddc;
						if (!this.nodeExists(oaipid))
						{
							createOAISet(name, spec, oaipid);
						}
						linkObjectToOaiSet(node, spec, oaipid);
					}

				}
			if (node.getContentType() != null)
			{
				String docType = node.getContentType();

				logger.info("Found contentType: " + docType);

				String name = docmap(docType);
				String spec = TypeType.contentType.toString() + ":" + docType;
				String namespace = "oai";
				String oaipid = namespace + ":" + docType;
				if (!this.nodeExists(oaipid))
				{
					createOAISet(name, spec, oaipid);
				}
				linkObjectToOaiSet(node, spec, oaipid);
			}
			else
			// TODO this block is deprecated as soon as lobid works like
			// expected
			if (node.getType() != null)
				for (String type : node.getType())
				{
					if (type.startsWith(TypeType.contentType.toString()))
					{
						String docType = type.substring(type.indexOf(':') + 1);
						logger.info("Found contentType: " + docType);

						String name = docmap(docType);
						String spec = TypeType.contentType.toString() + ":"
								+ docType;
						String namespace = "oai";
						String oaipid = namespace + ":" + docType;
						if (!this.nodeExists(oaipid))
						{
							createOAISet(name, spec, oaipid);
						}
						linkObjectToOaiSet(node, spec, oaipid);
					}
				}

			String name = "open_access";
			String spec = "open_access";
			String namespace = "oai";
			String oaipid = namespace + ":" + "open_access";
			if (!this.nodeExists(oaipid))
			{
				createOAISet(name, spec, oaipid);
			}
			linkObjectToOaiSet(node, spec, oaipid);

			return pid + " successfully created oai sets!";
		}
		catch (MalformedURLException e)
		{
			throw new HttpArchiveException(500, e.getMessage());
		}
	}

	void linkObjectToOaiSet(Node node, String spec, String pid)
	{
		Vector<Link> relations = node.getRelsExt();

		Iterator<Link> iter = relations.iterator();
		while (iter.hasNext())
		{

			String pred = iter.next().getPredicate();
			// System.out.println(pred);
			// logger.info(pred);
			if (pred.compareTo(IS_MEMBER_OF) == 0)
				iter.remove();
			else if (pred.compareTo(ITEM_ID) == 0)
				iter.remove();
		}
		Link link = new Link();
		link.setPredicate(IS_MEMBER_OF);
		link.setObject("info:fedora/" + pid, false);
		relations.add(link);

		link = new Link();
		link.setPredicate(ITEM_ID);
		link.setObject(uriPrefix + node.getPID(), false);
		relations.add(link);

		node.setRelsExt(relations);

		archive.updateNode(node.getPID(), node);

	}

	/**
	 * @param pid
	 *            The pid of an existing object.
	 * @param type
	 *            the type of the object.
	 * @return the typed view of the object
	 */
	View getView(String pid)
	{

		Node node = readNode(pid);
		return getView(node);

	}

	/**
	 * @param node
	 *            An object as node
	 * @param type
	 *            The type
	 * @return the view of the object of type type.
	 */
	View getView(Node node)
	{

		String pid = node.getPID();
		String uri = uriPrefix + pid;
		View view = new View();
		view.setLastModified(node.getLastModified());
		view.setCreator(node.getCreator());
		view.setTitle(node.getTitle());
		view.setLanguage(node.getLanguage());
		view.setSubject(node.getSubject());
		view.setType(node.getType());
		view.setLocation(node.getSource());
		view.setPublisher(node.getPublisher());
		view.setDescription(node.getDescription());
		String label = node.getLabel();

		if (label != null && !label.isEmpty())
			view.addDescription(label);
		view.setUri(uri);
		view.addType(TypeType.contentType + ":" + node.getContentType());

		String pidWithoutNamespace = pid.substring(pid.indexOf(':') + 1);

		view.addFedoraUrl(this.fedoraExtern + "/objects/" + pid);

		// TODO You know what to do!
		if (pid.contains("edoweb") || pid.contains("ellinet"))
		{
			if (pid.length() <= 17)
			{
				view.addDigitoolUrl("http://klio.hbz-nrw.de:1801/webclient/MetadataManager?pid="
						+ pidWithoutNamespace);
				// TODO only if synced Resource
				view.addCacheUrl(this.serverName + "/" + node.getNamespace()
						+ "base/" + pidWithoutNamespace);

			}
		}
		if (pid.contains("dipp"))
		{

			// TODO only if synced Resource
			view.addCacheUrl(this.serverName + "/" + node.getNamespace()
					+ "base/" + pid);

		}
		String query = "<info:fedora/" + pid + "> * *";
		try
		{
			view.addRisearchUrl(this.fedoraExtern
					+ "/risearch?type=triples&lang=spo&format=RDF/XML&query="
					+ URIUtil.encodeQuery(query));
		}
		catch (URIException e)
		{
		}

		String mime = node.getMimeType();

		if (mime != null && !mime.isEmpty())
		{
			if (mime.compareTo("application/pdf") == 0)
			{
				view.addPdfUrl(uri + "/data");
			}
			if (mime.compareTo("application/zip") == 0)
			{
				view.addZipUrl(uri + "/data");
			}
		}
		for (String date : node.getDate())
		{
			view.addYear(date.substring(0, 4));
		}
		for (String ddc : node.getSubject())
		{
			if (ddc.startsWith("ddc"))
			{
				view.addDdc(ddc);
				break;
			}
		}

		for (String id : node.getIdentifier())
		{
			if (id.startsWith("doi"))
			{
				view.addDoi(id);
				view.addDataciteUrl(dataciteUrl + id);
				view.addBaseUrl(baseUrl + id);
			}
			else if (id.startsWith("urn"))
			{
				view.addUrn(id);
				break;
			}
			else if (id.startsWith("HT"))
			{
				view.addAlephId(id);
				view.addCulturegraphUrl(culturegraphUrl + id);
				view.addLobidUrl(lobidUrl + id);
				view.addVerbundUrl(verbundUrl + id);
				break;
			}
			else if (id.startsWith("TT"))
			{
				view.addAlephId(id);
				view.addCulturegraphUrl(culturegraphUrl + id);
				view.addLobidUrl(lobidUrl + id);
				view.addVerbundUrl(verbundUrl + id);
				break;
			}
			else
			{
				view.addIdentifier(id);
			}
		}

		for (String relPid : findObject(pid, IS_PART_OF))
		{
			String relUrl = serverName + "/resources/" + relPid;

			view.addIsPartOf(relUrl, relPid);
		}

		for (String relPid : findObject(pid, HAS_PART))
		{
			String relUrl = serverName + "/resources/" + relPid;

			List<String> desc = findObject(relPid,
					"http://purl.org/dc/elements/1.1/description");

			if (desc == null || desc.isEmpty())
			{
				view.addHasPart(relUrl, relPid);
			}
			else if (desc.size() == 1)
			{
				view.addHasPart(relUrl, desc.get(0));
			}
			else
			{
				StringBuffer buf = new StringBuffer();
				for (String d : desc)
				{
					buf.append(d + " ");
				}
				view.addHasPart(relUrl, buf.toString());
			}

		}

		InputStream in = null;

		try
		{
			URL metadata = new URL(fedoraExtern + "/objects/" + pid
					+ "/datastreams/metadata/content");
			in = metadata.openStream();

			RepositoryConnection con = null;
			Repository myRepository = new SailRepository(new MemoryStore());
			try
			{
				myRepository.initialize();
				con = myRepository.getConnection();
				String baseURI = "";

				con.add(in, baseURI, RDFFormat.N3);

				RepositoryResult<Statement> statements = con.getStatements(
						null, null, null, true);

				while (statements.hasNext())
				{
					Statement st = statements.next();
					String rdfSubject = st.getSubject().stringValue();

					if (rdfSubject.compareTo(pid) == 0)
					{
						view.addPredicate(st.getPredicate().stringValue(), st
								.getObject().stringValue());
					}
				}

			}
			catch (RepositoryException e)
			{

				e.printStackTrace();
			}
			catch (RDFParseException e)
			{

				e.printStackTrace();
			}
			catch (IOException e)
			{

				e.printStackTrace();
			}
			finally
			{
				if (con != null)
				{
					try
					{
						con.close();
					}
					catch (RepositoryException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally
		{
			IOUtils.closeQuietly(in);
		}

		return view;
	}

	/**
	 * @param pid
	 *            The pid to remove from index
	 * @return A short message
	 */
	String outdex(String pid)
	{

		String namespace = pid.substring(0, pid.indexOf(':'));
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		String indexUrl = "http://localhost:9200/" + namespace + "/titel/"
				+ pid;
		try
		{

			WebResource index = c.resource(indexUrl);
			index.accept(MediaType.APPLICATION_JSON);

			index.delete();
		}
		catch (Exception e)
		{
			throw new ArchiveException(pid + " can't delete from index: "
					+ indexUrl, e);
		}
		return pid + " remove from index!";
	}

	/**
	 * @param pid
	 *            The pid that must be indexed
	 * @return a short message.
	 */
	String index(String p, String namespace)
	{
		String message = "";
		String viewAsString = "";
		String pid = namespace + ":" + p;
		// View view = getView(pid);

		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		Client c = Client.create(cc);
		try
		{

			// TODO configure port and host
			WebResource index = c.resource("http://localhost:9200/" + namespace
					+ "/titel/" + pid);
			index.accept(MediaType.APPLICATION_JSON);
			URL url = new URL("http://localhost/resources/" + pid + "/about");
			URLConnection con = url.openConnection();
			con.setRequestProperty("Accept", "application/json");
			con.connect();
			InputStream in = con.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			viewAsString = writer.toString();
			in.close();
			message = index.put(String.class, viewAsString);
		}
		catch (Exception e)
		{
			throw new ArchiveException("Error! " + message + e.getMessage(), e);
		}
		return "Success! " + message + "\n" + viewAsString;
	}

	/**
	 * @return a list of all objects
	 */
	List<String> getAll()
	{
		Vector<String> pids = new Vector<String>();
		String query = "* <" + REL_IS_NODE_TYPE + "> \"" + TYPE_OBJECT + "\"";
		InputStream stream = archive.findTriples(query, FedoraVocabulary.SPO,
				FedoraVocabulary.N3);
		String findpid = null;
		RepositoryConnection con = null;
		Repository myRepository = new SailRepository(new MemoryStore());
		try
		{
			myRepository.initialize();
			con = myRepository.getConnection();
			String baseURI = "";

			con.add(stream, baseURI, RDFFormat.N3);

			RepositoryResult<Statement> statements = con.getStatements(null,
					null, null, true);

			while (statements.hasNext())
			{
				Statement st = statements.next();
				findpid = st.getSubject().stringValue()
						.replace("info:fedora/", "");
				pids.add(findpid);
			}
		}
		catch (RepositoryException e)
		{

			e.printStackTrace();
		}
		catch (RDFParseException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (RepositoryException e)
				{
					e.printStackTrace();
				}
			}
		}
		return pids;
	}

	/**
	 * @param pid
	 *            The pid for which to load lobid rdf
	 * @return a short message
	 */
	String lobidify(String pid)
	{

		Node node;

		node = readNode(pid);

		List<String> identifier = node.getIdentifier();
		String alephid = "";
		for (String id : identifier)
		{
			if (id.startsWith("TT") || id.startsWith("HT"))
			{
				alephid = id;
				break;
			}
		}
		if (alephid.isEmpty())
		{
			throw new ArchiveException(pid + " no Catalog-Id found");
		}
		String lobidUrl = "http://lobid.org/resource/" + alephid;
		InputStream in = null;
		try
		{
			URL url = new URL(lobidUrl);

			URLConnection con = url.openConnection();
			con.setRequestProperty("Accept", "text/plain");
			con.connect();

			in = con.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			String str = writer.toString();
			str = Pattern.compile(lobidUrl).matcher(str)
					.replaceAll(Matcher.quoteReplacement(pid))
					+ "<"
					+ pid
					+ "> <http://www.umbel.org/specifications/vocabulary#isLike> <"
					+ lobidUrl + "> .";

			updateMetadata(pid, str);
		}
		catch (IOException e)
		{
			throw new ArchiveException(pid
					+ " IOException happens during copy operation.", e);
		}
		finally
		{
			try
			{
				if (in != null)
					in.close();
			}
			catch (IOException e)
			{
				throw new ArchiveException(pid
						+ " wasn't able to close stream.", e);
			}
		}

		return pid + " lobid metadata successfully loaded!";

	}

	/**
	 * @param pid
	 *            The pid of an object
	 * @return The metadata a oaidc-xml
	 */
	String oaidc(String pid)
	{

		Node node = readNode(pid);
		if (node == null)
			return "No node with pid " + pid + " found";

		String metadata = "http://localhost/resources/" + pid + "/metadata";
		try
		{
			File outfile = File.createTempFile("oaidc", "xml");
			outfile.deleteOnExit();
			File fluxFile = new File(Thread.currentThread()
					.getContextClassLoader()
					.getResource("morph-lobid-to-oaidc.flux").toURI());
			Flux.main(new String[] { fluxFile.getAbsolutePath(),
					"url=" + metadata, "out=" + outfile.getAbsolutePath() });
			return FileUtils.readFileToString(outfile);
		}
		catch (IOException e)
		{
			throw new ArchiveException(pid + " " + e.getMessage(), e);
		}
		catch (URISyntaxException e)
		{
			throw new ArchiveException(pid + " " + e.getMessage(), e);
		}
		catch (RecognitionException e)
		{
			throw new ArchiveException(pid + " " + e.getMessage(), e);
		}

	}

	String epicur(String pid, String namespace)
	{
		String status = "urn_new";
		String result = "<epicur xmlns=\"urn:nbn:de:1111-2004033116\" xmlns:xsi=\"http://www.w3.com/2001/XMLSchema-instance\" xsi:schemaLocation=\"urn:nbn:de:1111-2004033116 http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd\">\n"
				+ "\t<administrative_data>\n"
				+ "\t\t<delivery>\n"
				+ "\t\t\t<update_status type=\""
				+ status
				+ "\"></update_status>\n"
				+ "\t\t\t<transfer type=\"oai\"></transfer>\n"
				+ "\t\t</delivery>\n"
				+ "\t</administrative_data>\n"
				+ "<record>\n"
				+ "\t<identifier scheme=\"urn:nbn:de\">"
				+ generateUrn(pid, namespace)
				+ "</identifier>\n"
				+ "\t<resource>\n"
				+ "\t\t<identifier origin=\"original\" role=\"primary\" scheme=\"url\" type=\"frontpage\">"
				+ uriPrefix
				+ ""
				+ namespace
				+ ":"
				+ pid
				+ "</identifier>\n"
				+ "\t\t<format scheme=\"imt\">text/html</format>\n"
				+ "\t</resource>" + "</record>\n" + "</epicur> ";
		return result;
	}

	String generateUrn(String pid, String namespace)
	{
		try
		{
			List<String> urns = getView(pid).getUrn();
			if (urns != null && !urns.isEmpty())
			{
				if (urns.size() > 1)
				{
					logger.warn("Found multiple urns " + urns.size());
				}
				else
				{
					return urns.get(0);
				}
			}
		}
		catch (Exception e)
		{
			logger.warn("You attempt to create an urn for non-existing object.");
		}
		String result = null;
		String raw = null;
		String urn = "urn";
		String nbn = "nbn";
		String de = "de";
		String snid = namespace;
		String niss = pid;

		raw = urn + ":" + nbn + ":" + de + ":" + snid + "-" + niss;

		String checksum = getUrnChecksum(raw);
		result = raw + "" + checksum;

		return result;
	}

	private String getUrnChecksum(String rawUrn)
	{
		String checksum = "";
		int ps = 0;
		char[] urnArray = urnMap(rawUrn);
		for (int i = 1; i <= urnArray.length; i++)
		{
			ps = ps + i * Integer.parseInt(String.valueOf(urnArray[i - 1]));
		}
		int q = ps
				/ Integer.parseInt(String
						.valueOf(urnArray[urnArray.length - 1]));
		checksum = String.valueOf(q);
		return String.valueOf(checksum.charAt(checksum.length() - 1));
	}

	private char[] urnMap(String urn)
	{
		Properties mKonkordanz = new Properties();
		mKonkordanz.setProperty("0", "1");

		mKonkordanz.setProperty("1", "2");

		mKonkordanz.setProperty("2", "3");

		mKonkordanz.setProperty("3", "4");

		mKonkordanz.setProperty("4", "5");

		mKonkordanz.setProperty("5", "6");

		mKonkordanz.setProperty("6", "7");

		mKonkordanz.setProperty("7", "8");

		mKonkordanz.setProperty("8", "9");

		mKonkordanz.setProperty("9", "41");

		mKonkordanz.setProperty("a", "18");

		mKonkordanz.setProperty("b", "14");

		mKonkordanz.setProperty("c", "19");

		mKonkordanz.setProperty("d", "15");

		mKonkordanz.setProperty("e", "16");

		mKonkordanz.setProperty("f", "21");

		mKonkordanz.setProperty("g", "22");

		mKonkordanz.setProperty("h", "23");

		mKonkordanz.setProperty("i", "24");

		mKonkordanz.setProperty("j", "25");

		mKonkordanz.setProperty("k", "42");

		mKonkordanz.setProperty("l", "26");

		mKonkordanz.setProperty("m", "27");

		mKonkordanz.setProperty("n", "13");

		mKonkordanz.setProperty("o", "28");

		mKonkordanz.setProperty("p", "29");

		mKonkordanz.setProperty("q", "31");

		mKonkordanz.setProperty("r", "12");

		mKonkordanz.setProperty("s", "32");

		mKonkordanz.setProperty("t", "33");

		mKonkordanz.setProperty("u", "11");

		mKonkordanz.setProperty("v", "34");

		mKonkordanz.setProperty("w", "35");

		mKonkordanz.setProperty("x", "36");

		mKonkordanz.setProperty("y", "37");

		mKonkordanz.setProperty("z", "38");

		mKonkordanz.setProperty("-", "39");

		mKonkordanz.setProperty(":", "17");

		char[] urnArray = urn.toLowerCase().toCharArray();

		StringBuffer strBuf = new StringBuffer();

		for (int i = 0; i < urnArray.length; i++)
		{

			strBuf.append(Integer.parseInt(mKonkordanz.getProperty(String
					.valueOf(urnArray[i]))));

		}

		urnArray = strBuf.toString().toCharArray();
		return urnArray;
	}

	private void createOAISet(String name, String spec, String pid)
	{
		String setSpecPred = "http://www.openarchives.org/OAI/2.0/setSpec";
		String setNamePred = "http://www.openarchives.org/OAI/2.0/setName";

		Link setSpecLink = new Link();
		setSpecLink.setPredicate(setSpecPred);

		Link setNameLink = new Link();
		setNameLink.setPredicate(setNamePred);

		String namespace = "oai";
		{
			Node oaiset = new Node();
			oaiset.setNamespace(namespace);
			oaiset.setPID(pid);

			setSpecLink.setObject(spec, true);
			oaiset.addRelation(setSpecLink);

			setNameLink.setObject(name, true);
			oaiset.addRelation(setNameLink);
			oaiset.addTitle(name);

			archive.createComplexObject(new ComplexObject(oaiset));

		}
	}

	private String docmap(String type)
	{
		if (type.compareTo("report") == 0)
		{
			return "Monograph";
		}
		if (type.compareTo("webpage") == 0)
		{
			return "Webpage";
		}
		if (type.compareTo("ejournal") == 0)
		{
			return "EJournal";
		}
		return "";
	}

	private String ddcmap(String number)
	{
		if (number == null || number.length() != 3)
			logger.info("Didn't found ddc name for ddc:" + number);
		String name = "";
		try
		{
			URL url = new URL("http://dewey.info/class/" + number
					+ "/2009-08/about.en");
			HttpClient httpClient = new HttpClient();

			HttpMethod method = new GetMethod(url.toString());
			httpClient.executeMethod(method);
			InputStream stream = method.getResponseBodyAsStream();
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder;
			factory.setNamespaceAware(true);
			factory.setExpandEntityReferences(false);
			docBuilder = factory.newDocumentBuilder();

			Document doc;

			doc = docBuilder.parse(stream);
			Element root = doc.getDocumentElement();
			root.normalize();
			try
			{
				name = root.getElementsByTagName("skos:prefLabel").item(0)
						.getTextContent();
				logger.info("Found ddc name: " + name);
			}
			catch (Exception e)
			{
				logger.info("Didn't found ddc name for ddc:" + number);
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (HttpException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}

		return name;
	}

	private void waitWorkaround()
	{
		/*
		 * Workaround START
		 */
		try
		{
			logger.info("Wait 10 sec! Nasty workaround.");
			Thread.sleep(10000);
			logger.info("Stop Waiting! Nasty workaround.");
		}
		catch (InterruptedException e1)
		{

			e1.printStackTrace();
		}
		/*
		 * Workaround END
		 */
	}

	public String pdfbox(Node node)
	{
		String pid = node.getPID();

		String mimeType = node.getMimeType();
		if (mimeType == null)
			throw new HttpArchiveException(
					404,
					"The node "
							+ pid
							+ " does not provide a mime type. It may not even contain data at all!");
		if (mimeType.compareTo("application/pdf") != 0)
			throw new HttpArchiveException(406,
					"Wrong mime type. Cannot extract text from " + mimeType);
		File pdfFile = null;
		InputStream in = null;
		try
		{
			URL content = new URL(fedoraExtern + "/objects/" + pid
					+ "/datastreams/data/content");
			pdfFile = File.createTempFile("pdf", "pdf");
			pdfFile.deleteOnExit();
			URL url = new URL(lobidUrl);

			URLConnection uc = content.openConnection();
			uc.connect();
			in = uc.getInputStream();
			FileOutputStream out = new FileOutputStream(pdfFile);

			byte[] buffer = new byte[1024];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) > -1)
			{
				out.write(buffer, 0, bytesRead);
			}
			in.close();

		}
		catch (IOException e)
		{
			throw new ArchiveException(pid
					+ " IOException happens during copy operation.", e);
		}
		finally
		{
			try
			{
				if (in != null)
					in.close();
			}
			catch (IOException e)
			{
				throw new ArchiveException(pid
						+ " wasn't able to close stream.", e);
			}
		}

		PDDocument doc = null;

		try
		{
			doc = PDDocument.load(pdfFile);
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(doc);
			return text;
		}
		catch (IOException e)
		{
			throw new HttpArchiveException(500, "Didn't find  pdf file.");
		}
		catch (Exception e)
		{
			throw new HttpArchiveException(500, e.getMessage());
		}
		finally
		{
			if (doc != null)
			{
				try
				{
					if (doc != null)
						doc.close();
				}
				catch (IOException e)
				{

				}
			}
		}
	}

	public String itext(String pid)
	{

		Node node = readNode(pid);

		String mimeType = node.getMimeType();
		if (mimeType == null)
			throw new HttpArchiveException(
					404,
					"The node "
							+ pid
							+ " does not provide a mime type. It may not even contain data at all!");
		if (mimeType.compareTo("application/pdf") != 0)
			throw new HttpArchiveException(406,
					"Wrong mime type. Cannot extract text from " + mimeType);
		File pdfFile = null;
		InputStream in = null;
		try
		{
			URL content = new URL(fedoraExtern + "/objects/" + pid
					+ "/datastreams/data/content");
			pdfFile = File.createTempFile("pdf", "pdf");
			pdfFile.deleteOnExit();
			URL url = new URL(lobidUrl);

			URLConnection uc = content.openConnection();
			uc.connect();
			in = uc.getInputStream();
			FileOutputStream out = new FileOutputStream(pdfFile);

			byte[] buffer = new byte[1024];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) > -1)
			{
				out.write(buffer, 0, bytesRead);
			}
			in.close();

		}
		catch (IOException e)
		{
			throw new ArchiveException(pid
					+ " IOException happens during copy operation.", e);
		}
		finally
		{
			try
			{
				if (in != null)
					in.close();
			}
			catch (IOException e)
			{
				throw new ArchiveException(pid
						+ " wasn't able to close stream.", e);
			}
		}
		PdfReader reader;
		try
		{
			reader = new PdfReader(pdfFile.getAbsolutePath());
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			StringBuffer out = new StringBuffer();
			TextExtractionStrategy strategy;
			for (int i = 1; i <= reader.getNumberOfPages(); i++)
			{
				strategy = parser.processContent(i,
						new SimpleTextExtractionStrategy());
				out.append(strategy.getResultantText());
			}

			return out.toString();
		}
		catch (IOException e)
		{
			throw new HttpArchiveException(500, "itext problem");
		}

	}

	public String contentModelsInit(String namespace)
	{
		try
		{

			archive.updateContentModel(ContentModelFactory
					.createHeadModel(namespace));

			archive.updateContentModel(ContentModelFactory
					.createEJournalModel(namespace));
			archive.updateContentModel(ContentModelFactory
					.createMonographModel(namespace));
			archive.updateContentModel(ContentModelFactory
					.createWebpageModel(namespace));
			archive.updateContentModel(ContentModelFactory
					.createVersionModel(namespace));
			archive.updateContentModel(ContentModelFactory
					.createVolumeModel(namespace));

			archive.updateContentModel(ContentModelFactory
					.createPdfModel(namespace));

			return "Success!";
		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(500, e.getMessage());
		}
	}

	public Node readNode(String pid)
	{
		try
		{
			return archive.readNode(pid);
		}
		catch (NodeNotFoundException e)
		{
			throw new HttpArchiveException(404, e.getMessage());
		}
	}

	public String deleteNamespace(String namespace)
	{
		List<String> objects = archive.findNodes(namespace + ":*");
		return deleteAll(objects, false);
	}

	public Response getFulltext(String pid, String namespace)
			throws URISyntaxException
	{

		return Response.temporaryRedirect(
				new java.net.URI(fedoraExtern + "/objects/" + namespace + ":"
						+ pid + "/methods/" + namespace
						+ "CM:pdfServiceDefinition/pdfbox")).build();

	}

	public Response getEpicur(String pid, String namespace)
			throws URISyntaxException
	{

		return Response.temporaryRedirect(
				new java.net.URI(fedoraExtern + "/objects/" + namespace + ":"
						+ pid + "/methods/" + namespace
						+ "CM:headServiceDefinition/epicur")).build();

	}

	public Response getOAI_DC(String pid, String namespace)
			throws URISyntaxException
	{

		return Response.temporaryRedirect(
				new java.net.URI(fedoraExtern + "/objects/" + namespace + ":"
						+ pid + "/methods/" + namespace
						+ "CM:headServiceDefinition/oai_dc")).build();

	}

}
