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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.BaseConfiguration;

public class MyConfiguration extends BaseConfiguration
{
	/**
	 * 
	 * generates the configuration in terms of arguments and options
	 * 
	 * @param args
	 *            Command line arguments
	 * @param options
	 *            Command line options
	 * @throws ParseException
	 *             When the configuration cannot be parsed
	 * 
	 * @author Jan Schnasse, schnasse@hbz-nrw.de
	 * 
	 */
	public MyConfiguration(String[] args, Options options)
			throws ParseException
	{
		CommandLineParser parser = new BasicParser();
		CommandLine commandLine = parser.parse(options, args);
		for (Option option : commandLine.getOptions())
		{
			String key = option.getLongOpt();
			// System.out.println(key);
			if (key.compareTo("set") == 0)
			{
				String[] vals = option.getValues();
				if (vals == null || vals.length == 0)
				{
					this.addProperty(key, "N/A");
				}
				else
				{
					StringBuffer val = new StringBuffer();
					for (int i = 0; i < vals.length; i++)
					{
						val.append(vals[i]);
						val.append(",");
					}
					val.delete(val.length(), val.length());
					this.addProperty(key, val.toString());
				}
			}
			else
			{
				String val = option.getValue();
				if (val == null)
				{
					this.addProperty(key, "N/A");
				}
				else
				{

					this.addProperty(key, val);
				}
			}
		}
	}
}
