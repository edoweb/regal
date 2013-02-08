package de.nrw.hbz.edoweb2.api;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CollectionProfile
{
	public List<View> all;
	public List<String> restricted = new Vector<String>();
	public List<String> unrestricted = new Vector<String>();
	public List<String> noRight = new Vector<String>();
	public List<String> noDDC = new Vector<String>();
	public List<String> noUrn = new Vector<String>();
	public List<String> noYear = new Vector<String>();
	public List<String> noTitle = new Vector<String>();
	public List<String> noCreator = new Vector<String>();
	public List<String> noType = new Vector<String>();
	public List<TypeObjectDictionary> types = new Vector<TypeObjectDictionary>();

	private HashMap<String, List<String>> map = new HashMap<String, List<String>>();

	public CollectionProfile()
	{

	}

	public CollectionProfile(List<View> all)
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
					unrestricted.add(view.getUri());
				}
				else if (right.compareTo("restricted") == 0)
				{
					restricted.add(view.getUri());
				}
				else
				{
					noRight.add(view.getUri());
				}
			}

			if (ddcs.isEmpty())
				noDDC.add(view.getUri());
			if (urns.isEmpty())
				noUrn.add(view.getUri());
			if (years.isEmpty())
				noYear.add(view.getUri());
			if (titles.isEmpty())
				noTitle.add(view.getUri());
			if (creators.isEmpty())
				noCreator.add(view.getUri());
			if (types.isEmpty())
				noType.add(view.getUri());
			else
			{
				for (String type : types)
				{
					List<String> uris = null;
					if (map.containsKey(type))
					{
						uris = map.get(type);

					}
					else
					{
						uris = new Vector<String>();
					}
					uris.add(view.getUri());
					map.put(type, uris);
				}
			}

		}
		for (String type : map.keySet())
		{
			types.add(new TypeObjectDictionary(type, map.get(type)));
		}

	}

}
