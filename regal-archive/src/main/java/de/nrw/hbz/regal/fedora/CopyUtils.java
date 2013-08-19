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
package de.nrw.hbz.regal.fedora;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class CopyUtils {

    /**
     * @param content
     *            the string will be written to a tmp file
     * @return a temp file which will be deleted on exit
     * @throws IOException
     *             if something goes wrong
     */
    public static File copyStringToFile(String content) throws IOException {
	File file = File.createTempFile("tmpmetadata", "tmp");
	file.deleteOnExit();
	FileUtils.writeStringToFile(file, content);
	return file;
    }

    /**
     * @param content
     *            the inputstream will be written to the file
     * @param tmp
     *            the file to copy into
     * @throws IOException
     *             if something goes wrong
     */
    public static void copy(InputStream content, File tmp) throws IOException {
	OutputStream out = null;
	try {

	    int read = 0;
	    byte[] bytes = new byte[1024];

	    out = new FileOutputStream(tmp);
	    while ((read = content.read(bytes)) != -1) {
		out.write(bytes, 0, read);
	    }

	} catch (IOException e) {

	    throw new IOException(e);
	} finally {
	    try {

		if (out != null)
		    out.close();
	    } catch (IOException e) {

	    }
	}
    }

    /**
     * @param url
     *            the Url will be downloaded to a tmp file
     * @return a tmp file which will be deleted on exit
     * @throws IOException
     *             if something goes wrong
     */
    public static File download(URL url) throws IOException {
	File file = null;
	InputStream in = null;
	FileOutputStream out = null;
	try {

	    file = File.createTempFile("tmp", "bin");
	    file.deleteOnExit();

	    URLConnection uc = url.openConnection();
	    uc.connect();
	    in = uc.getInputStream();
	    out = new FileOutputStream(file);

	    byte[] buffer = new byte[1024];
	    int bytesRead = -1;
	    while ((bytesRead = in.read(buffer)) > -1) {
		out.write(buffer, 0, bytesRead);
	    }

	} finally {

	    if (in != null)
		in.close();
	    if (out != null)
		out.close();

	}
	return file;
    }

    /**
     * @param in
     *            read from this
     * @param out
     *            copy to this
     * @throws IOException
     *             if something goes wrong
     */
    protected void copy(InputStream in, OutputStream out) throws IOException {
	byte[] buffer = new byte[1024];
	while (true) {
	    int readCount = in.read(buffer);
	    if (readCount < 0) {
		break;
	    }
	    out.write(buffer, 0, readCount);
	}
    }

    /**
     * @param is
     *            the stream will be copied to the string
     * @param encoding
     *            a java encoding, e.g. utf-8
     * @return the input stream as string
     * @throws IOException
     *             if something goes wrong
     */
    public static String copyToString(InputStream is, String encoding)
	    throws IOException {
	try {
	    StringWriter writer = new StringWriter();
	    IOUtils.copy(is, writer, encoding);
	    return writer.toString();
	} finally {
	    IOUtils.closeQuietly(is);
	}
    }
}
