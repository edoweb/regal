package de.nrw.hbz.edoweb2.integrityCheck;

import java.util.List;

import org.junit.Test;

import de.nrw.hbz.edoweb2.api.View;

public class CacheSurveyTest
{

	public CacheSurveyTest()
	{

	}

	public void testSurvey()
	{
		CacheSurvey survey = new CacheSurvey();
		List<View> rows = survey.survey();
		System.out
				.println("Number, Pid, AlephId,Creator,Title,Year,Type,DDC,URN,Rights");
		int count = 1;
		for (View row : rows)
		{
			System.out
					.println("\"" + count++ + "\", \"" + row.getPid() + "\",\""
							+ row.getAlephid() + "\",\"" + row.getCreator()
							+ "\",\"" + row.getTitle() + "\",\""
							+ row.getYear() + "\",\"" + row.getType() + "\",\""
							+ row.getDdc() + "\",\"" + row.getUrn() + "\",\""
							+ row.getRights() + "\"");
		}
	}

	@Test
	public void testCharacteristics()
	{
		CacheSurvey survey = new CacheSurvey();
		List<View> rows = survey.survey();
		CacheCharacteristics chara = new CacheCharacteristics(rows);
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

	}
}
