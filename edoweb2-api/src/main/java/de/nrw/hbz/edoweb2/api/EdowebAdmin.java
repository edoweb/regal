package de.nrw.hbz.edoweb2.api;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/edowebAdmin")
public class EdowebAdmin
{
	Actions actions = new Actions();

	public EdowebAdmin()
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
	public MessageBean index(@PathParam("pid") String pid)
	{
		return new MessageBean(actions.index(pid));
	}

	@GET
	@Path("/profile/cache")
	@Produces({ "application/json", "application/xml" })
	public CollectionProfile profileCache()
	{
		CacheSurvey survey = new CacheSurvey();
		List<View> rows = survey.survey();
		return new CollectionProfile(rows);
	}

	@GET
	@Path("/profile/fedora")
	@Produces({ "application/json", "application/xml" })
	public CollectionProfile profileFedora()
	{
		FedoraSurvey survey = new FedoraSurvey();
		List<View> rows = survey.survey();
		return new CollectionProfile(rows);
	}

	@POST
	@Path("/lobidify/{pid}")
	@Produces({ "application/json", "application/xml" })
	public MessageBean lobidify(@PathParam("pid") String pid,
			@PathParam("alephid") String alephid)
	{
		return new MessageBean(actions.lobidify(pid));
	}

}
