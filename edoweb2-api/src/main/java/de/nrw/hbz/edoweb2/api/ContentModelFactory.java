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
	 * @return a ContentModel object
	 */
	public static ContentModel createHeadModel(String namespace)
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID(namespace + "CM:headObjectModel");
		cm.setServiceDefinitionPID(namespace + "CM:headServiceDefinition");
		cm.setServiceDeploymentPID(namespace + "CM:headServiceDeployment");

		cm.addMethod("oai_dc", "http://localhost/utils/oaidc/(pid)");
		cm.addMethod("epicur", "http://localhost/utils/epicur/(pid)");

		return cm;
	}

	/**
	 * @return a ContentModel object
	 */
	public static ContentModel createPdfModel(String namespace)
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID(namespace + "CM:pdfObjectModel");
		cm.setServiceDefinitionPID(namespace + "CM:pdfServiceDefinition");
		cm.setServiceDeploymentPID(namespace + "CM:pdfServiceDeployment");

		cm.addMethod("pdfbox", "http://localhost/utils/pdfbox/(pid)");
		cm.addMethod("itext", "http://localhost/utils/itext/(pid)");

		return cm;
	}

	/**
	 * @return a ContentModel object
	 */
	public static ContentModel createMonographModel(String namespace)
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID(namespace + "CM:edowebMonographObjectModel");
		cm.setServiceDefinitionPID(namespace
				+ "CM:edowebMonographServiceDefinition");
		cm.setServiceDeploymentPID(namespace
				+ "CM:edowebMonographServiceDeployment");

		return cm;
	}

	/**
	 * @return a ContentModel object
	 */
	public static ContentModel createWebpageModel(String namespace)
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID(namespace + "CM:edowebWebpageObjectModel");
		cm.setServiceDefinitionPID(namespace
				+ "CM:edowebWebpageServiceDefinition");
		cm.setServiceDeploymentPID(namespace
				+ "CM:edowebWebpageServiceDeployment");

		return cm;
	}

	/**
	 * @return a ContentModel object
	 */
	public static ContentModel createEJournalModel(String namespace)
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID(namespace + "CM:edowebEJournalObjectModel");
		cm.setServiceDefinitionPID(namespace
				+ "CM:edowebEJournalServiceDefinition");
		cm.setServiceDeploymentPID(namespace
				+ "CM:edowebEJournalServiceDeployment");

		return cm;
	}

	/**
	 * @return a ContentModel object
	 */
	public static ContentModel createVolumeModel(String namespace)
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID(namespace + "CM:edowebVolumeObjectModel");
		cm.setServiceDefinitionPID(namespace
				+ "CM:edowebVolumeServiceDefinition");
		cm.setServiceDeploymentPID(namespace
				+ "CM:edowebVolumeServiceDeployment");

		return cm;
	}

	/**
	 * @return a ContentModel object
	 */
	public static ContentModel createVersionModel(String namespace)
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID(namespace + "CM:edowebVersionObjectModel");
		cm.setServiceDefinitionPID(namespace
				+ "CM:edowebVersionServiceDefinition");
		cm.setServiceDeploymentPID(namespace
				+ "CM:edowebVersionServiceDeployment");

		return cm;
	}

}
