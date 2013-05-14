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
package de.nrw.hbz.regal.sync;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 * @author Jan Schnasse
 * 
 */
public class DigitoolDownloadConfiguration
{
	private CompositeConfiguration config = null;

	/**
	 * @param args
	 *            Command line arguments
	 * @param options
	 *            Command line options
	 * @param cl
	 *            Calling class
	 * @throws ParseException
	 *             When the configuration cannot be parsed
	 * 
	 * @author Jan Schnasse, schnasse@hbz-nrw.de
	 * 
	 */
	public DigitoolDownloadConfiguration(String[] args, Options options,
			Class<?> cl) throws ParseException
	{
		Collection<Configuration> confs = new ArrayList<Configuration>();
		confs.add(new MyConfiguration(args, options));
		confs.add(new MyPreferences(cl));
		this.config = new CompositeConfiguration(confs);
	}

	/**
	 * @param key
	 *            Option key to check
	 * @return <code>true</code> if the configuration contains the key
	 */
	public boolean hasOption(String key)
	{
		return this.config.containsKey(key);
	}

	/**
	 * @param key
	 *            Option key to retrieve
	 * @return Value of the option key
	 */
	public String getOptionValue(String key)
	{
		return this.config.getString(key);
	}

}
