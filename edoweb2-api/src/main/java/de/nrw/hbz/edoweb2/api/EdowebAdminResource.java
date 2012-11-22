package de.nrw.hbz.edoweb2.api;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("/edowebAdmin")
public class EdowebAdminResource
{
	Actions actions = new Actions();

	public EdowebAdminResource()
	{

	}

	@DELETE
	@Path("/formatAll")
	public String formatAll()
	{
		return actions.formatAll();
	}

	@DELETE
	@Path("/delete/{pid}")
	public String deleteMirror(@PathParam("pid") String pid)
	{

		String edo = actions.delete(pid, false);
		return edo;
	}

	@POST
	@Path("/makeOaiSet/{pid}")
	public String makeOaiSet(@PathParam("pid") String pid)
	{
		actions.makeOAISet(pid);

		return "";
	}

	@POST
	@Path("/index/{pid}")
	@Produces({ "application/json", "application/xml" })
	public MessageBean index(@PathParam("pid") String pid, @Context UriInfo info)
	{
		return new MessageBean(actions.index(info, pid));
	}
}
