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
package de.nrw.hbz.regal.api.helper;

import de.nrw.hbz.regal.datatypes.ContentModel;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class ContentModelFactory {
    /**
     * @param namespace
     *            the namespace. Contentmodels live in their own namespaces
     * @return a ContentModel object
     */
    public static ContentModel createHeadModel(String namespace) {
	ContentModel cm = new ContentModel();
	cm.setContentModelPID(namespace + "CM:headObjectModel");
	cm.setServiceDefinitionPID(namespace + "CM:headServiceDefinition");
	cm.setServiceDeploymentPID(namespace + "CM:headServiceDeployment");

	cm.addMethod("oai_dc", "http://localhost/utils/oaidc/(pid)");
	cm.addMethod("epicur", "http://localhost/utils/epicur/(pid)");

	return cm;
    }

    /**
     * @param namespace
     *            a namespace for the model.Contentmodels live in their own
     *            namespaces
     * @return a ContentModel object
     */
    public static ContentModel createPdfModel(String namespace) {
	ContentModel cm = new ContentModel();
	cm.setContentModelPID(namespace + "CM:pdfObjectModel");
	cm.setServiceDefinitionPID(namespace + "CM:pdfServiceDefinition");
	cm.setServiceDeploymentPID(namespace + "CM:pdfServiceDeployment");

	cm.addMethod("pdfbox", "http://localhost/utils/pdfbox/(pid)");
	cm.addMethod("pdfa", "http://localhost/utils/pdfa/(pid)");

	return cm;
    }

    /**
     * @param namespace
     *            Contentmodels live in their own namespaces
     * @return a ContentModel object
     */
    public static ContentModel createMonographModel(String namespace) {
	ContentModel cm = new ContentModel();
	cm.setContentModelPID(namespace + "CM:MonographObjectModel");
	cm.setServiceDefinitionPID(namespace + "CM:MonographServiceDefinition");
	cm.setServiceDeploymentPID(namespace + "CM:MonographServiceDeployment");

	return cm;
    }

    /**
     * @param namespace
     *            Contentmodels live in their own namespaces
     * @return a ContentModel object
     */
    public static ContentModel createWebpageModel(String namespace) {
	ContentModel cm = new ContentModel();
	cm.setContentModelPID(namespace + "CM:WebpageObjectModel");
	cm.setServiceDefinitionPID(namespace + "CM:WebpageServiceDefinition");
	cm.setServiceDeploymentPID(namespace + "CM:WebpageServiceDeployment");

	return cm;
    }

    /**
     * @param namespace
     *            Contentmodels live in their own namespaces
     * @return a ContentModel object
     */
    public static ContentModel createEJournalModel(String namespace) {
	ContentModel cm = new ContentModel();
	cm.setContentModelPID(namespace + "CM:EJournalObjectModel");
	cm.setServiceDefinitionPID(namespace + "CM:EJournalServiceDefinition");
	cm.setServiceDeploymentPID(namespace + "CM:EJournalServiceDeployment");

	return cm;
    }

    /**
     * @param namespace
     *            Contentmodels live in their own namespaces
     * @return a ContentModel object
     */
    public static ContentModel createVolumeModel(String namespace) {
	ContentModel cm = new ContentModel();
	cm.setContentModelPID(namespace + "CM:VolumeObjectModel");
	cm.setServiceDefinitionPID(namespace + "CM:VolumeServiceDefinition");
	cm.setServiceDeploymentPID(namespace + "CM:VolumeServiceDeployment");

	return cm;
    }

    /**
     * @param namespace
     *            Contentmodels live in their own namespaces
     * @return a ContentModel object
     */
    public static ContentModel createVersionModel(String namespace) {
	ContentModel cm = new ContentModel();
	cm.setContentModelPID(namespace + "CM:VersionObjectModel");
	cm.setServiceDefinitionPID(namespace + "CM:VersionServiceDefinition");
	cm.setServiceDeploymentPID(namespace + "CM:VersionServiceDeployment");

	return cm;
    }

    /**
     * @param namespace
     *            Contentmodels live in their own namespaces
     * @return a ContentModel object
     */
    public static ContentModel createFileModel(String namespace) {
	ContentModel cm = new ContentModel();
	cm.setContentModelPID(namespace + "CM:FileObjectModel");
	cm.setServiceDefinitionPID(namespace + "CM:FileServiceDefinition");
	cm.setServiceDeploymentPID(namespace + "CM:FileServiceDeployment");

	return cm;
    }

    /**
     * @param namespace
     *            Contentmodels live in their own namespaces
     * @return a ContentModel object
     */
    public static ContentModel createIssueModel(String namespace) {
	ContentModel cm = new ContentModel();
	cm.setContentModelPID(namespace + "CM:IssueObjectModel");
	cm.setServiceDefinitionPID(namespace + "CM:IssueServiceDefinition");
	cm.setServiceDeploymentPID(namespace + "CM:IssueServiceDeployment");

	return cm;
    }

}
