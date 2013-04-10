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

package de.nrw.hbz.edoweb2.sync.mapper;

import java.util.Collections;
import java.util.Hashtable;

/**
 * Class DigitoolPremis2RdfMap
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 11.07.2011
 * 
 */
@SuppressWarnings("javadoc")
public class DigitoolPremis2RdfMap
{

	public final static String urn = "http://purl.org/dc/elements/1.1/identifier";

	public final static String xmlUrn = "objectIdentifierValue";

	Hashtable<String, String> dtl2rdf = null;

	// Qualified Dublin Core

	public DigitoolPremis2RdfMap()
	{
		dtl2rdf = new Hashtable<String, String>();
		dtl2rdf.put(urn, xmlUrn);
	}

	public String get(String key)
	{
		return dtl2rdf.get(key);
	}

	public Object[] getTagNames()
	{
		return Collections.list(dtl2rdf.keys()).toArray();
	}
}
