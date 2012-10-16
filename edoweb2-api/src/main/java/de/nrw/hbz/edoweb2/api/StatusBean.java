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
package de.nrw.hbz.edoweb2.api;

import javax.xml.bind.annotation.XmlRootElement;

import de.nrw.hbz.edoweb2.datatypes.Node;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@XmlRootElement
public class StatusBean
{

	public enum VISIBILITY {
		me, registered, all
	};

	public VISIBILITY visibleFor = VISIBILITY.me;

	public StatusBean()
	{

	}

	public StatusBean(Node node)
	{
		String visibility = node.getFirstRights();
		if (visibility.compareTo(VISIBILITY.me.toString()) == 0)
		{
			visibleFor = VISIBILITY.me;
		}
		else if (visibility.compareTo(VISIBILITY.registered.toString()) == 0)
		{
			visibleFor = VISIBILITY.registered;
		}
		else if (visibility.compareTo(VISIBILITY.all.toString()) == 0)
		{
			visibleFor = VISIBILITY.all;
		}
	}
}
