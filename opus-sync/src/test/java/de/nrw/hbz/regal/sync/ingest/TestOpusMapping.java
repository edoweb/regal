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
package de.nrw.hbz.regal.sync.ingest;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class TestOpusMapping {
    @Test
    public void mappingTest() throws URISyntaxException {
	OpusMapping mapping = new OpusMapping();
	File file = new File(Thread.currentThread().getContextClassLoader()
		.getResource("xMetaDissPlus.xml").toURI());
	System.out.println(mapping.map(file, "ubm:6"));
    }
}
