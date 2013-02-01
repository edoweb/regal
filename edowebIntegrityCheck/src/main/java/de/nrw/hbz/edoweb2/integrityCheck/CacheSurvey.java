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
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
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
		int count = 1;
		for (File file : cacheDirFile.listFiles())
		{
			if (file.isDirectory())
			{

				View row = createSurvey(new File(file + File.separator + "."
						+ file.getName() + "_MARC.xml"), file.getName());

				row.addRights(getRights(new File(file + File.separator + "."
						+ file.getName() + "_RIGHTS.xml")));
				rows.add(row);
			}
		}
		return rows;
	}

	private String getRights(File file)
	{
		String result = "unknown";
		try
		{
			String data = FileUtils.readFileToString(file);
			if (data.contains("everyone"))
			{
				result = "everyone";
			}
			else
			{
				result = "restricted";
			}
		}
		catch (IOException e)
		{

		}
		return result;
	}

	private View createSurvey(File file, String pid)
	{
		// logger.info(file.getAbsolutePath());

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
				// logger.info(record.toString());
				ControlField alephId = (ControlField) record
						.getVariableField("001");
				DataField title = (DataField) record.getVariableField("245");
				DataField urn = (DataField) record.getVariableField("856");
				DataField ddc = (DataField) record.getVariableField("082");
				List creators = record.getVariableFields(new String[] { "100",
						"110", "111", "700", "710", "711", "720" });

				List types = record.getVariableFields(new String[] { "655",
						"501" });

				view.addAlephId(alephId.getData());

				view.addTitle(title.getSubfield('a').getData());

				view.addUrn(urn.getSubfield('u').getData());

				view.addCreator(((DataField) (creators.get(0)))
						.getSubfield('a').getData());

				List stypes = ((DataField) (types.get(0)))

				.getSubfields();

				view.addType(((Subfield) stypes.get(0)).getData());

				view.addDdc(ddc.getSubfield('a').getData());

				DataField date = (DataField) record.getVariableField("260");
				view.addYear(date.getSubfield('c').getData());
				date = (DataField) record.getVariableField("005");
				view.addYear(date.getSubfield('a').getData());

			}

		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{

		}
		return view;
	}
}
