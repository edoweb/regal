package de.nrw.hbz.edoweb2.api;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FedoraSurvey
{
	final static Logger logger = LoggerFactory.getLogger(FedoraSurvey.class);
	String cacheDir = "/opt/edoweb/edobase/";
	String uriBase = "http://orthos.hbz-nrw.de/objects";

	Actions actions = new Actions();

	public FedoraSurvey()
	{

	}

	public List<View> survey()
	{
		List<String> pids = actions.getAll();
		Vector<View> rows = new Vector<View>();

		for (String pid : pids)
		{
			rows.add(actions.getView(pid));
		}

		return rows;
	}

}
