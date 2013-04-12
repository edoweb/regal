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
package de.nrw.hbz.edoweb2.sync;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.configuration.BaseConfiguration;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
class MyPreferences extends BaseConfiguration
{
	MyPreferences(Class<?> cl)
	{
		try
		{
			Preferences p = Preferences.userNodeForPackage(cl);
			for (String preference : p.keys())
			{
				this.addProperty(preference, p.get(preference, null));
			}
		}
		catch (BackingStoreException e)
		{
			// then we end up with an empty set of preferences, no big deal
		}
	}
}
