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
package de.nrw.hbz.regal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.fedora.FedoraFactory;
import de.nrw.hbz.regal.fedora.FedoraInterface;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class ArchiveIntegrationTest {

    FedoraInterface archive = null;

    Properties properties = null;
    Node rootObject = null;

    @Before
    public void setUp() {

	try {
	    properties = new Properties();
	    properties.load(getClass().getResourceAsStream("/test.properties"));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	archive = FedoraFactory.getFedoraImpl(
		properties.getProperty("fedoraUrl"),
		properties.getProperty("user"),
		properties.getProperty("password"));

    }

    @Test
    public void createObject() {

    }

    @Test
    public void readObject() {

    }

    @Test
    public void updateObject() {

    }

    @Test
    public void deleteObject() {

    }

    @After
    public void tearDown() {
	List<String> objects = archive.findNodes("test:*");
	for (String pid : objects) {
	    archive.deleteNode(pid);
	}

	objects = archive.findNodes("testCM:*");
	for (String pid : objects) {
	    archive.deleteNode(pid);
	}

    }
}
