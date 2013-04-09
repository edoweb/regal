package de.nrw.hbz.edoweb2.api;

import java.io.IOException;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import de.nrw.hbz.edoweb2.archive.exceptions.ArchiveException;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class DeleteResource
{

	Actions actions = null;

	/**
	 * @throws IOException
	 *             if initialization of Actions class fails
	 */
	public DeleteResource() throws IOException
	{
		actions = new Actions();
	}

	/**
	 * @param pid
	 *            the pid of the resource that must be deleted
	 * @return a human readable message and a status code of 200 if successful
	 *         or a 500 if not.
	 */
	@DELETE
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml" })
	public String deleteResource(@PathParam("pid") String pid)
	{
		try
		{
			return actions.delete(pid, false);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	/**
	 * @param pid
	 *            the pid of the resource that data must be deleted
	 * @return a human readable message and a status code of 200 if successful
	 *         or a 500 if not.
	 */
	@DELETE
	@Path("/{pid}/data")
	@Produces({ "application/json", "application/xml" })
	public String deleteResourceData(@PathParam("pid") String pid)
	{
		try
		{
			return actions.deleteData(pid);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

	/**
	 * @param pid
	 *            the pid of the resource that data must be deleted
	 * @return a human readable message and a status code of 200 if successful
	 *         or a 500 if not.
	 */
	@DELETE
	@Path("/{pid}/metadata")
	@Produces({ "application/json", "application/xml" })
	public String deleteResourceMetadata(@PathParam("pid") String pid)
	{
		try
		{
			return actions.deleteMetadata(pid);

		}
		catch (ArchiveException e)
		{
			throw new HttpArchiveException(
					Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
		}
	}

}
