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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.nrw.hbz.regal.PIDReporter;

/**
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public abstract class Downloader implements DownloaderInterface {
    final static protected Logger logger = LoggerFactory
	    .getLogger(Downloader.class);

    protected String getDownloadLocation() {
	return downloadLocation;
    }

    protected void setDownloadLocation(String downloadLocation) {
	this.downloadLocation = downloadLocation;
    }

    protected String getObjectDirectory() {
	return objectDirectory;
    }

    protected void setObjectDirectory(String objectDirectory) {
	this.objectDirectory = objectDirectory;
    }

    protected String getServer() {
	return server;
    }

    protected void setServer(String server) {
	this.server = server;
    }

    protected HashMap<String, String> getMap() {
	return map;
    }

    protected void setMap(HashMap<String, String> map) {
	this.map = map;
    }

    protected boolean isUpdated() {
	return updated;
    }

    protected boolean isDownloaded() {
	return downloaded;
    }

    String downloadLocation = null;
    String objectDirectory = null;
    String server = null;
    boolean updated = false;
    boolean downloaded = false;
    HashMap<String, String> map = new HashMap<String, String>();

    /**
     * Please implement this to download a single object. The method will be
     * called for each object pid provided by the regal framework. The method is
     * called from download(pid,force).
     * 
     * @param downloadDirectory
     *            The directory does already exist and is empty. The object can
     *            be downloaded to this directory.
     * @param pid
     *            The pid of the object, that must be downloaded.
     */
    protected abstract void downloadObject(File downloadDirectory, String pid);

    @Override
    public String download(String pid) throws IOException {
	return download(pid, true);
    }

    @Override
    public String download(String pid, boolean forceDownload)
	    throws IOException {
	if (map.containsKey(pid))
	    throw new IOException(pid + " already visited!");
	objectDirectory = downloadLocation + File.separator
		+ URLEncoder.encode(pid, "utf-8");
	File dir = new File(objectDirectory);
	if (!dir.exists()) {
	    logger.info("Create Directory " + dir.getAbsoluteFile()
		    + " and start to Download files");
	    dir.mkdirs();

	    try {
		if (!map.containsKey(pid)) {
		    map.put(pid, pid);

		} else {
		    throw new Exception(pid + " already visited!");
		}
		downloadObject(dir, pid);
	    } catch (Exception e) {
		logger.debug(e.getMessage());
	    }
	    setUpdated(false);
	    setDownloaded(true);
	} else if (forceDownload) {
	    logger.info("Directory " + dir.getAbsoluteFile()
		    + " exists. Force override.");
	    FileUtils.deleteDirectory(dir);
	    dir.mkdirs();

	    try {
		if (!map.containsKey(pid)) {
		    map.put(pid, pid);

		} else {
		    throw new Exception(pid + " already visited!");
		}
		downloadObject(dir, pid);
	    } catch (Exception e) {
		logger.debug(e.getMessage());
	    }

	    setUpdated(true);
	    setDownloaded(true);
	} else {
	    logger.info("Directory " + dir.getAbsoluteFile()
		    + " exists. Step over.");
	    setDownloaded(false);
	    setUpdated(false);
	}
	map.clear();
	return dir.getAbsolutePath();
    }

    @Override
    public boolean hasUpdated() {
	return updated;
    }

    @Override
    public boolean hasDownloaded() {
	return downloaded;
    }

    @Override
    public void init(String server, String downloadLocation) {
	this.downloadLocation = downloadLocation;
	this.server = server;
    }

    private void setDownloaded(boolean downloaded) {
	this.downloaded = downloaded;
    }

    private void setUpdated(boolean updated) {
	this.updated = updated;
    }

    protected void run(String propFile) throws IOException {
	Properties properties = new Properties();
	try {
	    properties.load(new BufferedInputStream(new FileInputStream(
		    propFile)));
	} catch (IOException e) {
	    throw new IOException("Could not open " + propFile + "!");
	}
	this.server = properties.getProperty("piddownloader.server");
	this.downloadLocation = properties
		.getProperty("piddownloader.downloadLocation");

	PIDReporter pidreporter = new PIDReporter();
	Vector<String> pids = pidreporter.getPids(propFile);

	for (int i = 0; i < pids.size(); i++) {
	    String pid = pids.elementAt(i);
	    logger.info((i + 1) + "/" + pids.size() + " Download " + pid + " !");
	    download(pid);
	}

    }

    protected Element stringToElement(String data) {
	try {
	    DocumentBuilderFactory factory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder docBuilder;

	    docBuilder = factory.newDocumentBuilder();

	    Document doc;

	    doc = docBuilder.parse(new ByteArrayInputStream(data
		    .getBytes("utf-8")));
	    Element root = doc.getDocumentElement();
	    root.normalize();
	    return root;
	} catch (FileNotFoundException e) {

	    e.printStackTrace();
	} catch (SAXException e) {

	    e.printStackTrace();
	} catch (IOException e) {

	    e.printStackTrace();
	} catch (ParserConfigurationException e) {

	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    protected void download(File dir, String url) {
	try {
	    URL dataStreamUrl = new URL(url);
	    File file = new File(dir.getAbsolutePath() + ""
		    + dataStreamUrl.getFile());
	    if (!file.exists()) {
		file.getParentFile().mkdirs();
		file.createNewFile();
	    }
	    OutputStream output = new FileOutputStream(file);
	    IOUtils.copy(dataStreamUrl.openStream(), output);
	} catch (Exception e) {
	    throw new DownloadException(e);
	}
    }

    protected void downloadText(File file, URL url) {
	try {
	    String data = null;
	    StringWriter writer = new StringWriter();
	    IOUtils.copy(url.openStream(), writer);
	    data = writer.toString();
	    FileUtils.writeStringToFile(file, data, "utf-8");
	} catch (MalformedURLException e) {
	    throw new DownloadException(e);
	} catch (IOException e) {
	    throw new DownloadException(e);
	}
    }

    @SuppressWarnings("resource")
    protected void zip(File directory, File zipfile) throws IOException {
	URI base = directory.toURI();
	Deque<File> queue = new LinkedList<File>();
	queue.push(directory);
	OutputStream out = new FileOutputStream(zipfile);
	Closeable res = out;
	try {
	    ZipOutputStream zout = new ZipOutputStream(out);
	    res = zout;
	    while (!queue.isEmpty()) {
		directory = queue.pop();
		for (File kid : directory.listFiles()) {
		    String name = base.relativize(kid.toURI()).getPath();
		    if (kid.isDirectory()) {
			queue.push(kid);
			name = name.endsWith("/") ? name : name + "/";
			zout.putNextEntry(new ZipEntry(name));
		    } else {
			zout.putNextEntry(new ZipEntry(name));
			copy(kid, zout);
			zout.closeEntry();
		    }
		}
	    }
	} finally {
	    res.close();

	}
    }

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

    protected void copy(File file, OutputStream out) throws IOException {
	InputStream in = new FileInputStream(file);
	try {
	    copy(in, out);
	} finally {
	    in.close();
	}
    }

    protected void copy(InputStream in, File file) throws IOException {
	OutputStream out = new FileOutputStream(file);
	try {
	    copy(in, out);
	} finally {
	    out.close();
	}
    }

    public class DownloadException extends RuntimeException {
	public DownloadException(Throwable arg0) {
	    super(arg0);
	}
    }
}
