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
		System.out.println("Hole alle pids");
		List<String> pids = actions.getAll();
		Vector<View> rows = new Vector<View>();

		System.out.println("Hole infos für: " + pids.size() + " pids.");
		int count = 0;
		for (String pid : pids)
		{
			System.out.println("Pid: " + (++count));
			View view = actions.getView(pid);
			if (view != null)
				rows.add(view);
		}

		return rows;
	}
}
