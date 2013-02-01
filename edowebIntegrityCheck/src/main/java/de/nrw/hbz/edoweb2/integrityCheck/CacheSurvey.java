package de.nrw.hbz.edoweb2.integrityCheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.edoweb2.api.View;

public class CacheSurvey
{
	final static Logger logger = LoggerFactory.getLogger(CacheSurvey.class);
	String cacheDir = "/opt/edoweb/edobase/";

	public CacheSurvey()
	{

	}

	public List<View> survey()
	{

		List<View> rows = new Vector<View>();
		File cacheDirFile = new File(cacheDir);
		for (File file : cacheDirFile.listFiles())
		{
			if (file.isDirectory())
			{

				View row = createSurvey(new File(file + File.separator + "."
						+ file.getName() + "_MARC.xml"), file.getName());

				rows.add(row);
			}
		}
		return rows;
	}

	private View createSurvey(File file, String pid)
	{
		logger.info(file.getAbsolutePath());
		// preprocess(file);
		View view = new View();
		view.addPid(pid);
		InputStream in;
		try
		{
			in = new FileInputStream(file);

			MarcReader reader = new MarcXmlReader(in);
			while (reader.hasNext())
			{
				Record record = reader.next();

				System.out.println(record.toString());
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
	}

	private void preprocess(File file)
	{
		/*
		 * Todo : Workaround for some bug.
		 */
		try
		{
			String content = FileUtils.readFileToString(file);
			content = content.replaceAll("nam  2200000 u 4500",
					"00000    a2200000   4500");
			FileUtils.writeStringToFile(file, content);
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}

	}
	// private View createSurvey(File file, String pid)
	// {
	// View view = new View();
	// view.addPid(pid);
	// logger.info(view.getFirstPid());
	// try
	// {
	// Element root = XMLUtils.getDocument(FileUtils
	// .readFileToString(file));
	//
	// NodeList fields = root.getElementsByTagName("controlfield");
	// for (int i = 0; i < fields.getLength(); i++)
	// {
	// Element field = (Element) fields.item(i);
	// if ("001".compareTo(field.getAttributeNode("tag")
	// .getTextContent()) == 0)
	// {
	// view.addAlephId(field.getTextContent());
	// logger.info(view.getFirstAlephId());
	// }
	// }
	// fields = root.getElementsByTagName("datafield");
	// for (int i = 0; i < fields.getLength(); i++)
	// {
	// Element field = (Element) fields.item(i);
	// if ("245".compareTo(field.getAttributeNode("tag")
	// .getTextContent()) == 0)
	// {
	// NodeList subfields = field.getElementsByTagName("subfield");
	//
	// for (int j = 0; j < subfields.getLength(); j++)
	// {
	// Element subfield = (Element) subfields.item(j);
	// if ("h".compareTo(subfield.getAttributeNode("code")
	// .getTextContent()) == 0)
	// {
	// // do nothing
	// }
	// else
	// {
	// view.addTitle(subfield.getTextContent());
	// logger.info(view.getTitle().firstElement());
	// }
	// }
	// }
	// }
	// }
	// catch (NullPointerException e)
	// {
	// e.printStackTrace();
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// return view;
	// }
}
