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
	public static ContentModel createEdowebHeadModel()
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID("edowebCM:edowebObjectModel");
		cm.setServiceDefinitionPID("edowebCM:edowebServiceDefinition");
		cm.setServiceDeploymentPID("edowebCM:edowebServiceDeployment");

		cm.addMethod("oai_dc", "http://localhost/utils/oaidc/(pid)");
		cm.addMethod("epicur", "http://localhost/utils/epicur/(pid)");

		return cm;
	}

	/**
	 * @return a ContentModel object
	 */
	public static ContentModel createEdowebPdfModel()
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID("edowebCM:pdfObjectModel");
		cm.setServiceDefinitionPID("edowebCM:pdfServiceDefinition");
		cm.setServiceDeploymentPID("edowebCM:pdfServiceDeployment");

		cm.addMethod("pdfbox", "http://localhost/utils/pdfbox/(pid)");
		cm.addMethod("itext", "http://localhost/utils/itext/(pid)");

		return cm;
	}

	/**
	 * @return a ContentModel object
	 */
	public static ContentModel createEdowebMonographModel()
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID("edowebCM:edowebMonographObjectModel");
		cm.setServiceDefinitionPID("edowebCM:edowebMonographServiceDefinition");
		cm.setServiceDeploymentPID("edowebCM:edowebMonographServiceDeployment");

		return cm;
	}

	/**
	 * @return a ContentModel object
	 */
	public static ContentModel createEdowebWebpageModel()
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID("edowebCM:edowebWebpageObjectModel");
		cm.setServiceDefinitionPID("edowebCM:edowebWebpageServiceDefinition");
		cm.setServiceDeploymentPID("edowebCM:edowebWebpageServiceDeployment");

		return cm;
	}

	/**
	 * @return a ContentModel object
	 */
	public static ContentModel createEdowebEJournalModel()
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID("edowebCM:edowebEJournalObjectModel");
		cm.setServiceDefinitionPID("edowebCM:edowebEJournalServiceDefinition");
		cm.setServiceDeploymentPID("edowebCM:edowebEJournalServiceDeployment");

		return cm;
	}

	/**
	 * @return a ContentModel object
	 */
	public static ContentModel createEdowebVolumeModel()
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID("edowebCM:edowebVolumeObjectModel");
		cm.setServiceDefinitionPID("edowebCM:edowebVolumeServiceDefinition");
		cm.setServiceDeploymentPID("edowebCM:edowebVolumeServiceDeployment");

		return cm;
	}

	/**
	 * @return a ContentModel object
	 */
	public static ContentModel createEdowebVersionModel()
	{
		ContentModel cm = new ContentModel();
		cm.setContentModelPID("edowebCM:edowebVersionObjectModel");
		cm.setServiceDefinitionPID("edowebCM:edowebVersionServiceDefinition");
		cm.setServiceDeploymentPID("edowebCM:edowebVersionServiceDeployment");

		return cm;
	}

}
