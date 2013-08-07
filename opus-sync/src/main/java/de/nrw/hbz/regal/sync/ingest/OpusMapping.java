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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.culturegraph.mf.Flux;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class OpusMapping {
    final static Logger logger = LoggerFactory.getLogger(OpusMapping.class);

    /**
     * @param file
     *            the content of the file will be mapped. Expected: Dipp qdc
     *            stream.
     * @param pid
     *            the pid becomes the new subject
     * @return a lobid conform mapping of dipp qdc datastream
     */
    public String map(File file, String pid) {
	try {
	    System.out.println(file.getAbsolutePath());
	    return flux(file, pid);
	} catch (URISyntaxException e) {
	    logger.error(e.getMessage());
	} catch (IOException e) {
	    logger.error(e.getMessage());
	} catch (RecognitionException e) {
	    logger.error(e.getMessage());
	}
	return null;
    }

    private String flux(File file, String pid) throws URISyntaxException,
	    IOException, RecognitionException {
	File outfile = File.createTempFile("lobid", "rdf");
	outfile.deleteOnExit();
	File fluxFile = createFile("opus-xmetadissplus-to-lobid.flux",
		"/tmp/opus-xmetadissplus-to-lobid.flux");
	createFile("opus-xmetadissplus-to-lobid.xml",
		"/tmp/opus-xmetadissplus-to-lobid.xml");

	Flux.main(new String[] { fluxFile.getAbsolutePath(),
		"in=" + file.getAbsolutePath(),
		"out=" + outfile.getAbsolutePath(), "subject=" + pid });
	return FileUtils.readFileToString(outfile).trim();
    }

    private File createFile(String resourceName, String path)
	    throws IOException {
	InputStream in = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream(resourceName);
	File tempFile = new File(path);
	tempFile.deleteOnExit();
	FileOutputStream out = null;
	try {
	    out = new FileOutputStream(tempFile);
	    IOUtils.copy(in, out);
	} finally {
	    if (out != null)
		out.close();
	}
	return tempFile;
    }
}
