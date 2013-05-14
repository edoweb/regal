package de.nrw.hbz.regal.api;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.multipart.MultiPart;

@Path("/file")
public class FileResource
{
	final static Logger logger = LoggerFactory.getLogger(Monograph.class);

	Resources resources = null;

	public FileResource() throws IOException
	{

		resources = new Resources();

	}

	@PUT
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public String create(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		CreateObjectBean input = new CreateObjectBean();
		input.type = ObjectType.file.toString();
		return resources.create(pid, namespace, input);
	}

	@GET
	@Path("/{pid}/data")
	@Produces({ "application/*", "application/json" })
	public Response readData(@PathParam("pid") String pid)
	{
		return resources.readData(pid);
	}

	@GET
	@Path("/{pid}/metadata")
	@Produces({ "text/plain" })
	public String readMetadata(@PathParam("pid") String pid)
	{
		return resources.readMetadata(pid);
	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return resources.getAllOfType(ObjectType.file.toString());

	}

	@GET
	@Path("/{pid}/about")
	@Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	public Response about(@PathParam("pid") String pid)
	{
		return resources.about(pid);
	}

	/**
	 * @param pid
	 *            the pid of the resource
	 * @return an aggregated representation of the resource
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml", "text/html" })
	public Response get(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace) throws URISyntaxException
	{
		return Response
				.temporaryRedirect(
						new java.net.URI("/resources/" + namespace + ":" + pid
								+ "/about")).status(303).build();
	}

	@POST
	@Path("/{pid}/dc")
	@Produces({ "application/json", "application/xml" })
	@Consumes({ "application/json", "application/xml" })
	public String updateDC(@PathParam("pid") String pid, DCBeanAnnotated content)
	{

		return resources.updateDC(pid, content);

	}

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readDC(@PathParam("pid") String pid)
	{
		return resources.readDC(pid);
	}

	@PUT
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateMetadata(@PathParam("pid") String pid, String content)
	{
		return resources.updateMetadata(pid, content);
	}

	@POST
	@Path("/{pid}/data")
	@Produces({ "application/json", "application/xml" })
	@Consumes("multipart/mixed")
	public String updateData(@PathParam("pid") String pid, MultiPart multiPart)
	{
		return resources.updateData(pid, multiPart);
	}

	@POST
	@Path("/{pid}/metadata")
	@Consumes({ "text/plain" })
	@Produces({ "text/plain" })
	public String updateMetadataPost(@PathParam("pid") String pid,
			String content)
	{
		return resources.updateMetadata(pid, content);
	}

	@DELETE
	@Path("/{namespace}:{pid}")
	@Produces({ "application/json", "application/xml" })
	public String delete(@PathParam("pid") String pid,
			@PathParam("namespace") String namespace)
	{
		return resources.delete(pid, namespace);
	}

	@DELETE
	@Produces({ "application/json", "application/xml" })
	public String deleteAll()
	{
		return resources.deleteAllOfType(ObjectType.file.toString());

	}
}
