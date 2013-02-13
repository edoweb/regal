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
	public static ContentModel createMonographCM(String namespace,
			ObjectType type)
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID(namespace + ":" + type.toString() + "ObjectModel");
		cm.setServiceDefinitionPID(namespace + ":" + type.toString()
				+ "ServiceDefinition");
		cm.setServiceDeploymentPID(namespace + ":" + type.toString()
				+ "ServiceDeployment");

		cm.addMethod("oai_dc",
				"http://localhost:8080/services/report/oai_dc/(pid)");
		cm.addMethod("xepicur",
				"http://localhost:8080/services/report/xepicur/(pid)");
		cm.addMethod("xMetaDissPlus",
				"http://localhost:8080/services/report/xMetaDissPlus/(pid)");
		cm.addMethod("Aleph_MARC",
				"http://localhost:8080/services/report/aleph_marc/(pid)");
		cm.addMethod("mets", "http://localhost:8080/services/report/mets/(pid)");

		return cm;
	}

	public static ContentModel createWpdCM(String namespace, ObjectType type)
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID(namespace + ":" + type.toString() + "ObjectModel");
		cm.setServiceDefinitionPID(namespace + ":" + type.toString()
				+ "ServiceDefinition");
		cm.setServiceDeploymentPID(namespace + ":" + type.toString()
				+ "ServiceDeployment");

		cm.addMethod("oai_dc",
				"http://localhost:8080/services/wpd/oai_dc/(pid)");
		cm.addMethod("xepicur",
				"http://localhost:8080/services/wpd/xepicur/(pid)");
		cm.addMethod("xMetaDissPlus",
				"http://localhost:8080/services/wpd/xMetaDissPlus/(pid)");
		cm.addMethod("Aleph_MARC",
				"http://localhost:8080/services/wpd/aleph_marc/(pid)");
		cm.addMethod("mets", "http://localhost:8080/services/wpd/mets/(pid)");

		return cm;
	}

}
