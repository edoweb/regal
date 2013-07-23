/*
 * Copyright 2012 hbz NRW (http://www.hbz-nrw.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.nrw.hbz.regal.api;

import java.util.List;

import de.nrw.hbz.regal.api.helper.CacheSurvey;
import de.nrw.hbz.regal.api.helper.View;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class CacheSurveyTest {

    /**
     * Test the profiling
     */
    public CacheSurveyTest() {

    }

    /**
     * * Test the profiling
     */
    // @Test
    public void testSurvey() {
	CacheSurvey survey = new CacheSurvey();
	List<View> rows = survey.survey();
	System.out
		.println("Number, Pid, AlephId,Creator,Title,Year,Type,DDC,URN,Rights");
	int count = 1;
	for (View row : rows) {
	    System.out.println("\"" + count++ + "\", \"" + row.getPid()
		    + "\",\"" + row.getAlephid() + "\",\"" + row.getCreator()
		    + "\",\"" + row.getTitle() + "\",\"" + row.getYear()
		    + "\",\"" + row.getType() + "\",\"" + row.getDdc()
		    + "\",\"" + row.getUrn() + "\",\"" + row.getRights()
		    + "\",\"" + row.getMessage() + "\"");
	}
    }

    /**
     * Some characteristic numbers.
     */
    public void testCharacteristics() {
	CacheSurvey survey = new CacheSurvey();
	List<View> rows = survey.survey();
	CollectionProfile chara = new CollectionProfile(rows);
	System.out.println("Number Of Elements: " + chara.all.size());
	System.out.println("Restricted Objects: " + chara.restricted.size());
	for (String str : chara.restricted) {
	    System.out.print(str + ",");
	}
	System.out.println(".");
	System.out
		.println("Unrestricted Objects: " + chara.unrestricted.size());
	System.out.println("NoRights Objects: " + chara.noRight.size());
	System.out.println("NoCreator Objects: " + chara.noCreator.size());
	System.out.println("NoTitle Objects: " + chara.noTitle.size());
	System.out.println("NoYear Objects: " + chara.noYear.size());
	System.out.println("NoDDC Objects: " + chara.noDDC.size());
	System.out.println("NoURN Objects: " + chara.noUrn.size());
	System.out.println("NoType Objects: " + chara.noType.size());
	System.out.println("Num of Types: " + chara.types.size());
	for (TypeObjectDictionary type : chara.types) {

	    System.out.println(type.type + ", " + type.uris.size());
	    for (String str : type.uris) {
		System.out.print(str + ",");
	    }
	    System.out.println(".");
	}

    }
}
