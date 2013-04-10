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

import de.nrw.hbz.edoweb2.datatypes.ContentModel;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class ContentModelFactory
{
	/**
	 * @param namespace
	 *            namespace in which the content model object will be created
	 * @param type
	 *            the type of object
	 * @return a ContentModel object
	 */
	public static ContentModel create(String namespace,
			ObjectType type)
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID(namespace + ":" + type.toString() + "ObjectModel");
		cm.setServiceDefinitionPID(namespace + ":" + type.toString()
				+ "ServiceDefinition");
		cm.setServiceDeploymentPID(namespace + ":" + type.toString()
				+ "ServiceDeployment");

		cm.addMethod("oai_dc", "http://localhost/utils/oaidc/(pid)");
		cm.addMethod("lobid", "http://localhost/resources/(pid)/metadata");

		return cm;
	}

}
