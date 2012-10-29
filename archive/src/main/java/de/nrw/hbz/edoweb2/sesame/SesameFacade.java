package de.nrw.hbz.edoweb2.sesame;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openrdf.OpenRDFException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.nativerdf.NativeStore;

import de.nrw.hbz.edoweb2.datatypes.Node;

public class SesameFacade
{

	String fedoraUser = null;
	String fedoraPwd = null;
	String sesameRepository = null;

	public SesameFacade(String fedoraUser, String fedoraPwd,
			String sesameRepository)
	{
		this.fedoraUser = fedoraUser;
		this.fedoraPwd = fedoraPwd;
		this.sesameRepository = sesameRepository;
	}

	public void addTurtleStream(InputStream turtleStream, String baseURI)
	{

		RepositoryConnection con = null;

		File dataDir = new File(sesameRepository);
		Repository myRepository = new SailRepository(new NativeStore(dataDir));

		try
		{
			myRepository.initialize();
		}
		catch (RepositoryException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			con = myRepository.getConnection();
			try
			{
				try
				{
					con.add(turtleStream, baseURI, RDFFormat.TURTLE);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					con.close();
					con = null;
				}
			}
			catch (OpenRDFException e)
			{
				e.printStackTrace();
			}

		}
		catch (RepositoryException e)
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try
			{
				myRepository.shutDown();
			}
			catch (RepositoryException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public InputStream findTriples(String rdfQuery, QueryLanguage queryType,
			String outputFormat)
	{

		RepositoryConnection con = null;

		File dataDir = new File(sesameRepository);
		Repository myRepository = new SailRepository(new NativeStore(dataDir));

		try
		{
			myRepository.initialize();
		}
		catch (RepositoryException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			con = myRepository.getConnection();
			try
			{

				try
				{

					TupleQuery tupleQuery = con.prepareTupleQuery(queryType,
							rdfQuery);
					System.out.println(rdfQuery);
					TupleQueryResult result = tupleQuery.evaluate();
					List<String> bindingNames = result.getBindingNames();
					try
					{

						while (result.hasNext())
						{

							BindingSet bindingSet = result.next();

							for (String name : bindingNames)
							{
								// System.out.println(name);
								System.out.println(bindingSet.getValue(name)
										.stringValue());
							}

						}
					}
					finally
					{
						result.close();
						result = null;
					}
				}
				finally
				{
					con.close();
					con = null;
				}
			}
			catch (OpenRDFException e)
			{
				// handle exception
				e.printStackTrace();
			}

		}
		catch (RepositoryException e)
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try
			{
				myRepository.shutDown();
			}
			catch (RepositoryException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	public void updateNode(Node node)
	{
		URL metadata = node.getMetadataUrl();
		if (metadata == null)
			return;

		try
		{

			HttpClient httpClient = new HttpClient();
			httpClient.getState().setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
					new UsernamePasswordCredentials(fedoraUser, fedoraPwd));
			HttpMethod method = new GetMethod(metadata.toString());

			httpClient.executeMethod(method);
			InputStream content = method.getResponseBodyAsStream();

			addTurtleStream(content, "");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
