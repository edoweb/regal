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
package de.nrw.hbz.regal.api;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class TestWebpageApi {
    @Before
    @After
    public void cleanUp() throws IOException {
	Utils utils = new Utils();
	try {
	    utils.deleteNamespace("test");
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	}
	try {
	    utils.deleteNamespace("textCM");
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	}
    }

    @Test
    public void testResources() throws IOException {
	Resource resources = new Resource();

	CreateObjectBean input = new CreateObjectBean();
	input.type = "webpage";

	resources.create("1234", "test", input);
	input.type = "version";
	input.parentPid = "test:1234";
	resources.create("4567", "test", input);

    }
}
