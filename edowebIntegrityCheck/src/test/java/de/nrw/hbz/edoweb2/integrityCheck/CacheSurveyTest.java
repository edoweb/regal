package de.nrw.hbz.edoweb2.integrityCheck;

import org.junit.Test;

public class CacheSurveyTest
{

	public CacheSurveyTest()
	{

	}

	@Test
	public void testSurvey()
	{
		CacheSurvey survey = new CacheSurvey();
		survey.survey();
	}
}
