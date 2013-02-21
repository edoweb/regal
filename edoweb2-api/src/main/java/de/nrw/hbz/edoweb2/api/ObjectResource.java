package de.nrw.hbz.edoweb2.api;

import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VERSION;
import static de.nrw.hbz.edoweb2.api.Vocabulary.HAS_VOLUME;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/objects")
public class ObjectResource
{

	final static Logger logger = LoggerFactory.getLogger(ObjectResource.class);

	String namespace = "edoweb";

	Actions actions = new Actions();

	@GET
	@Path("/{pid}/data")
	@Produces({ "application/*" })
	public Response readData(@PathParam("pid") String pid)
	{
		return actions.readData(pid);
	}

	// @GET
	// @Path("/{pid}/volume/{volumePid}")
	// @Produces({ "application/*" })
	// public StatusBean readVolume(@PathParam("pid") String pid,
	// @PathParam("volumePid") String volumePid)
	// {
	// String volumePid = null;
	// String query = EJournal.getVolumeQuery(volumePid, pid);
	// volumePid = actions.findSubject(query);
	//
	// return actions.read(volumePid);
	// }

	@GET
	@Path("/{pid}/volume/{volumePid}")
	@Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	public View getVolumeView(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{
		// String volumePid = null;
		// String query = EJournal.getVolumeQuery(volumePid, pid);
		// volumePid = actions.findSubject(query);
		return actions.getView(volumePid, ObjectType.ejournalVolume);
	}

	@GET
	@Path("/{pid}/volume/{volumePid}/data")
	@Produces({ "application/*" })
	public Response readVolumeData(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{
		// String volumePid = null;
		// String query = EJournal.getVolumeQuery(volumePid, pid);
		// volumePid = actions.findSubject(query);

		return actions.readData(volumePid);
	}

	@GET
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAll()
	{
		return new ObjectList(actions.getAll());
	}

	@GET
	@Path("/{pid}")
	@Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	public View getView(@PathParam("pid") String pid)
	{
		return actions.getView(pid);
	}

	@GET
	@Path("/{pid}/volume/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVolumes(@PathParam("pid") String pid)
	{
		// Vector<String> v = new Vector<String>();
		//
		// for (String volPid : )
		// {
		//
		// v.add(actions.findObject(volPid, HAS_VOLUME_NAME).get(0));
		//
		// }
		return new ObjectList(actions.findObject(pid, HAS_VOLUME));
	}

	@GET
	@Path("/{pid}/volume/{volumePid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readVolumeDC(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{
		// String volumePid = null;
		// String query = EJournal.getVolumeQuery(volumePid, pid);
		// volumePid = actions.findSubject(query);
		return actions.readDC(volumePid);
	}

	@GET
	@Path("/{pid}/volume/{volumePid}/metadata")
	@Produces({ "application/*" })
	public Response readVolumeMetadata(@PathParam("pid") String pid,
			@PathParam("volumePid") String volumePid)
	{
		// String volumePid = null;
		// String query = EJournal.getVolumeQuery(volumePid, pid);
		// volumePid = actions.findSubject(query);
		return actions.readMetadata(volumePid);
	}

	@GET
	@Path("/{pid}/metadata")
	public Response readMetadata(@PathParam("pid") String pid)
	{
		return actions.readMetadata(pid);
	}

	// @GET
	// @Path("/{namespace}:{pid}")
	// @Produces({ "application/json", "application/xml" })
	// public StatusBean read(@PathParam("pid") String pid,
	// @PathParam("namespace") String userNamespace)
	// {
	//
	// return actions.read(namespace + ":" + pid);
	// }

	@GET
	@Path("/{pid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readDC(@PathParam("pid") String pid)
	{
		return actions.readDC(pid);
	}

	@GET
	@Path("/{pid}/version/{versionPid}/metadata")
	@Produces({ "application/*" })
	public Response readWebpageVersionMetadata(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		// String versionPid = null;
		// String query = Webpage.getVersionQuery(versionPid, pid);
		// versionPid = actions.findSubject(query);
		return actions.readMetadata(versionPid);
	}

	@GET
	@Path("/{pid}/version/{versionPid}/dc")
	@Produces({ "application/xml", "application/json" })
	public DCBeanAnnotated readWebpageVersionDC(@PathParam("pid") String pid,
			@PathParam("versionPid") String versionPid)
	{
		// String versionPid = null;
		// String query = Webpage.getVersionQuery(versionName, pid);
		// versionPid = actions.findSubject(query);
		return actions.readDC(versionPid);
	}

	@GET
	@Path("/{pid}/version/{versionPid}/data")
	@Produces({ "application/*" })
	public Response readWebpageVersionData(@PathParam("pid") String pid,
			@PathParam("versionName") String versionPid)
	{
		// String versionPid = null;
		// String query = Webpage.getVersionQuery(versionName, pid);
		// versionPid = actions.findSubject(query);
		return actions.readData(versionPid);
	}

	// @GET
	// @Path("/{pid}/version/{versionName}")
	// @Produces({ "application/json", "application/xml" })
	// public StatusBean readWebpageVersion(@PathParam("pid") String pid,
	// @PathParam("versionName") String versionName)
	// {
	// String versionPid = null;
	// String query = Webpage.getVersionQuery(versionName, pid);
	// versionPid = actions.findSubject(query);
	//
	// return actions.read(versionPid);
	// }

	@GET
	@Path("/{pid}/version/{versionPid}")
	@Produces({ "application/json", "application/xml", MediaType.TEXT_HTML })
	public View getVersionView(@PathParam("pid") String pid,
			@PathParam("versionName") String versionPid)
	{
		// String versionPid = null;
		// String query = Webpage.getVersionQuery(versionName, pid);
		// versionPid = actions.findSubject(query);
		return actions.getView(versionPid, ObjectType.webpageVersion);
	}

	@GET
	@Path("/{pid}/version/")
	@Produces({ "application/json", "application/xml" })
	public ObjectList getAllVersions(@PathParam("pid") String pid)
	{
		// Vector<String> v = new Vector<String>();
		//
		// for (String volPid : actions.findObject(pid, HAS_VERSION))
		// {
		//
		// v.add(actions.findObject(volPid, HAS_VERSION_NAME).get(0));
		//
		// }
		return new ObjectList(actions.findObject(pid, HAS_VERSION));
	}

}
