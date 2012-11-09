package de.nrw.hbz.edoweb2.api;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/edowebAdmin")
public class EdowebAdminResource
{
	Actions actions = new Actions();

	public EdowebAdminResource()
	{

	}

	@DELETE
	@Path("/deleteMirror/{pid}")
	public String deleteMirror(@PathParam("pid") String pid)
	{

		String edo = actions.delete("edoweb:" + pid);
		String dtl = actions.delete("dtl:" + pid);
		return edo + "\n" + dtl;
	}
}
