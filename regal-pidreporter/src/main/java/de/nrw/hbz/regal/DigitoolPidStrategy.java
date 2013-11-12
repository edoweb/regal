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
package de.nrw.hbz.regal;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.kb.oai.pmh.IdentifiersList;

/**
 * @author Jan schnasse schnasse@hbz-nrw.de
 * 
 */
public class DigitoolPidStrategy implements CollectPidStrategy {

    public List<String> collectPids(IdentifiersList reclist) {
	String stream = reclist.getResponse().asXML();

	Vector<String> result = new Vector<String>();
	int start = 0;
	// int i = 0;
	Pattern pattern = Pattern
		.compile("<identifier>oai:[^:]*:([^<]*)</identifier>");
	Matcher matcher = pattern.matcher(stream);
	while (matcher.find(start)) {
	    String pid = stream.substring(matcher.start(1), matcher.end(1));

	    result.add(pid);
	    start = matcher.end();
	}
	return result;
    }

}
