package de.nrw.hbz.edoweb2.sesame;

import java.io.InputStream;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

import de.nrw.hbz.edoweb2.datatypes.Node;

public class SesameFacade
{
	public SesameFacade()
	{

	}

	public InputStream findTriples(String rdfQuery, QueryLanguage queryType,
			String outputFormat)
	{

		RepositoryConnection con = null;
		Repository myRepository = new HTTPRepository(
				"http://localhost:8080/openrdf-sesame/", "test");

		try
		{

			myRepository.initialize();
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
								System.out.println(name);
								System.out.println(bindingSet.getValue(name)
										.stringValue());
							}

						}
					}
					finally
					{
						result.close();
					}
				}
				finally
				{
					con.close();
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
		}
		return null;
	}

	public void updateNode(Node node)
	{
		// TODO Auto-generated method stub

	}
}
