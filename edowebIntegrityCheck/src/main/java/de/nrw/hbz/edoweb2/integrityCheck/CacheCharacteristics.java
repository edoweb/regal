package de.nrw.hbz.edoweb2.integrityCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import de.nrw.hbz.edoweb2.api.View;

public class CacheCharacteristics
{
	public List<View> all;
	public List<View> restricted = new Vector<View>();
	public List<View> unrestricted = new Vector<View>();
	public List<View> noRight = new Vector<View>();
	public List<View> noDDC = new Vector<View>();
	public List<View> noUrn = new Vector<View>();
	public List<View> noYear = new Vector<View>();
	public List<View> noTitle = new Vector<View>();
	public List<View> noCreator = new Vector<View>();
	public List<View> noType = new Vector<View>();

	public HashMap<String, Integer> map = new HashMap<String, Integer>();

	public CacheCharacteristics(List<View> all)
	{
		this.all = all;

		for (View view : all)
		{
			Vector<String> rights = view.getRights();
			Vector<String> ddcs = view.getDdc();
			Vector<String> urns = view.getUrn();
			Vector<String> years = view.getYear();
			Vector<String> titles = view.getTitle();
			Vector<String> creators = view.getCreator();
			Vector<String> types = view.getType();

			for (String right : rights)
			{
				if (right.compareTo("everyone") == 0)
				{
					unrestricted.add(view);
				}
				else if (right.compareTo("restricted") == 0)
				{
					restricted.add(view);
				}
				else
				{
					noRight.add(view);
				}
			}

			if (ddcs.isEmpty())
				noDDC.add(view);
			if (urns.isEmpty())
				noUrn.add(view);
			if (years.isEmpty())
				noYear.add(view);
			if (titles.isEmpty())
				noTitle.add(view);
			if (creators.isEmpty())
				noCreator.add(view);
			if (types.isEmpty())
				noType.add(view);
			else
			{
				for (String type : types)
				{
					if (map.containsKey(type))
					{
						map.put(type, new Integer(map.get(type).intValue() + 1));
					}
					else
					{
						map.put(type, new Integer(1));
					}
				}
			}
		}

	}
}
