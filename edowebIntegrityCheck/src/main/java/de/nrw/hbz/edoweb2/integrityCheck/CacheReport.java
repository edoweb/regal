package de.nrw.hbz.edoweb2.integrityCheck;

import java.util.List;

import de.nrw.hbz.edoweb2.api.Report;
import de.nrw.hbz.edoweb2.api.View;

public class CacheReport
{
	private List<View> rows = null;
	private CacheCharacteristics chara = null;

	public CacheReport()
	{
		CacheSurvey survey = new CacheSurvey();
		rows = survey.survey();
		CacheCharacteristics chara = new CacheCharacteristics(rows);
	}

	public Report report()
	{
		Report report = new Report();

		for (View row : rows)
		{
			report.add(row);
		}

		System.out.println("Number Of Elements: " + chara.all.size());
		System.out.println("Restricted Objects: " + chara.restricted.size());
		System.out
				.println("Unrestricted Objects: " + chara.unrestricted.size());
		System.out.println("NoRights Objects: " + chara.noRight.size());
		System.out.println("NoCreator Objects: " + chara.noCreator.size());
		System.out.println("NoTitle Objects: " + chara.noTitle.size());
		System.out.println("NoYear Objects: " + chara.noYear.size());
		System.out.println("NoDDC Objects: " + chara.noDDC.size());
		System.out.println("NoURN Objects: " + chara.noUrn.size());
		System.out.println("NoType Objects: " + chara.noType.size());
		System.out.println("Num of Types: " + chara.map.size());
		for (String type : chara.map.keySet())
		{
			System.out.println(type + ", " + chara.map.get(type).intValue());
		}
		return report;
	}
}
