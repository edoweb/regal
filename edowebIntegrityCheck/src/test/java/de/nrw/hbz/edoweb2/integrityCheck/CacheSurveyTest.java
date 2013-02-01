package de.nrw.hbz.edoweb2.integrityCheck;

import java.util.List;

import org.junit.Test;

import de.nrw.hbz.edoweb2.api.View;

public class CacheSurveyTest
{

	public CacheSurveyTest()
	{

	}

	@Test
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

}
