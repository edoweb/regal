package de.nrw.hbz.edoweb2.api;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/delete")
public class EdowebAdminResource
{
	Actions actions = new Actions();

	public EdowebAdminResource()
	{

	}

	@POST
	@Path("/deleteMirror/{pid}")
	public String deleteMirror(@PathParam("pid") String pid)
	{

		String edo = actions.delete("edoweb:" + pid);
		String dtl = actions.delete("dtl:" + pid);
		return edo + "\n" + dtl;
	}
}
