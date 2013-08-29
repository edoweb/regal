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
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.regal.PIDReporter;

/**
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public abstract class Downloader implements DownloaderInterface {
    @SuppressWarnings({ "javadoc", "serial" })
    public class CopyException extends RuntimeException {

	public CopyException() {
	}

	public CopyException(String message) {
	    super(message);
	}

	public CopyException(Throwable cause) {
	    super(cause);
	}

	public CopyException(String message, Throwable cause) {
	    super(message, cause);
	}

    }

    @SuppressWarnings({ "javadoc", "serial" })
    public class ZipDownloaderException extends RuntimeException {

	public ZipDownloaderException() {
	}

	public ZipDownloaderException(String message) {
	    super(message);
	}

	public ZipDownloaderException(Throwable cause) {
	    super(cause);
	}

	public ZipDownloaderException(String message, Throwable cause) {
	    super(message, cause);
	}

    }

    @SuppressWarnings({ "javadoc", "serial" })
    public class LoadPropertiesException extends RuntimeException {

	public LoadPropertiesException() {
	}

	public LoadPropertiesException(String message) {
	    super(message);
	}

	public LoadPropertiesException(Throwable cause) {
	    super(cause);
	}

	public LoadPropertiesException(String message, Throwable cause) {
	    super(message, cause);
	}

    }

    @SuppressWarnings({ "javadoc", "serial" })
    public class DeleteDirectoryException extends RuntimeException {

	public DeleteDirectoryException() {
	}

	public DeleteDirectoryException(String message) {
	    super(message);
	}

	public DeleteDirectoryException(Throwable cause) {
	    super(cause);
	}

	public DeleteDirectoryException(String message, Throwable cause) {
	    super(message, cause);
	}

    }

    @SuppressWarnings({ "javadoc", "serial" })
    public class EncodingException extends RuntimeException {

	public EncodingException() {
	}

	public EncodingException(String message) {
	    super(message);
	}

	public EncodingException(Throwable cause) {
	    super(cause);
	}

	public EncodingException(String message, Throwable cause) {
	    super(message, cause);
	}

    }

    @SuppressWarnings({ "javadoc", "serial" })
    public class AlreadyVisitedException extends RuntimeException {

	public AlreadyVisitedException() {
	}

	public AlreadyVisitedException(String message) {
	    super(message);
	}

	public AlreadyVisitedException(Throwable cause) {
	    super(cause);
	}

	public AlreadyVisitedException(String message, Throwable cause) {
	    super(message, cause);
	}

    }

    @SuppressWarnings({ "javadoc", "serial" })
    public class DownloadException extends RuntimeException {

	public DownloadException(Throwable e) {
	    super(e);
	}
    }

    /**
     * A logger for the Downloader
     */
    final static protected Logger logger = LoggerFactory
	    .getLogger(Downloader.class);
    String downloadLocation = null;
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
    public String download(String pid) {
	return download(pid, true);
    }

    @Override
    public String download(String pid, boolean forceDownload) {
	if (map.containsKey(pid))
	    throw new AlreadyVisitedException(pid + " already visited!");
	String objectDirectory = null;
	try {
	    objectDirectory = downloadLocation + File.separator
		    + URLEncoder.encode(pid, "utf-8");
	} catch (UnsupportedEncodingException e1) {
	    throw new EncodingException(e1);
	}
	File dir = new File(objectDirectory);
	if (!dir.exists()) {
	    logger.info("Create Directory " + dir.getAbsoluteFile()
		    + " and start to Download files");
	    dir.mkdirs();

	    try {
		if (!map.containsKey(pid)) {
		    map.put(pid, pid);

		} else {
		    throw new AlreadyVisitedException(pid + " already visited!");
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
	    try {
		FileUtils.deleteDirectory(dir);
	    } catch (IOException e1) {
		throw new DeleteDirectoryException(e1);
	    }
	    dir.mkdirs();

	    try {
		if (!map.containsKey(pid)) {
		    map.put(pid, pid);

		} else {
		    throw new AlreadyVisitedException(pid + " already visited!");
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

    /**
     * Will be initalised during init()
     * 
     * @return a local download directory
     */
    protected String getDownloadLocation() {
	return downloadLocation;
    }

    /**
     * @param downloadLocation
     *            a local download directory
     */
    protected void setDownloadLocation(String downloadLocation) {
	this.downloadLocation = downloadLocation;
    }

    /**
     * @return a base url to download from
     */
    protected String getServer() {
	return server;
    }

    /**
     * @param server
     *            a base url to download from
     */
    protected void setServer(String server) {
	this.server = server;
    }

    /**
     * @return a map to remember which objects are already download
     */
    protected HashMap<String, String> getMap() {
	return map;
    }

    /**
     * @param map
     *            a map to remember which objects are already download
     */
    protected void setMap(HashMap<String, String> map) {
	this.map = map;
    }

    /**
     * @return if an existing object was updated
     */
    protected boolean isUpdated() {
	return updated;
    }

    /**
     * @return if an existing object was downloaded
     */
    protected boolean isDownloaded() {
	return downloaded;
    }

    /**
     * @param propFile
     *            a property file with two properties piddownloader.server - for
     *            the server to download from piddownloader.downloadLocation -
     *            for the local directory to download to
     */
    protected void run(String propFile) {
	Properties properties = new Properties();
	try {
	    properties.load(new BufferedInputStream(new FileInputStream(
		    propFile)));
	} catch (IOException e) {
	    throw new LoadPropertiesException("Could not open " + propFile
		    + "!");
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

    /**
     * Creates a directory structure from the url and downloads into it.
     * Non-existing directories will be created.
     * 
     * @param dir
     *            a directory to write into
     * @param url
     *            a url to download from
     */
    protected void downloadToDir(File dir, String url) {
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

    /**
     * @param file
     *            the downloaded data will be written to this file
     * @param url
     *            a url to download from
     */
    protected void download(File file, String url) {
	try {
	    URL dataStreamUrl = new URL(url);

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

    /**
     * @param file
     *            the utf-8 text will be written to this file
     * @param url
     *            a url with utf-8 text data
     */
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

    /**
     * @param directory
     *            the directory will be zipped
     * @param zipfile
     *            the Outputfile
     */
    @SuppressWarnings("resource")
    protected void zip(File directory, File zipfile) {
	Closeable res = null;
	try {
	    URI base = directory.toURI();
	    Deque<File> queue = new LinkedList<File>();
	    queue.push(directory);
	    OutputStream out = new FileOutputStream(zipfile);
	    res = out;

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
	} catch (IOException e) {
	    throw new ZipDownloaderException(e);
	} finally {
	    try {
		if (res != null)
		    res.close();
	    } catch (IOException e) {
		throw new ZipDownloaderException(e);
	    }

	}
    }

    /**
     * @param in
     *            read from this
     * @param out
     *            copy to this
     */
    protected void copy(InputStream in, OutputStream out) {
	try {
	    byte[] buffer = new byte[1024];
	    while (true) {
		int readCount = in.read(buffer);
		if (readCount < 0) {
		    break;
		}
		out.write(buffer, 0, readCount);
	    }
	} catch (Exception e) {
	    throw new CopyException(e);
	}
    }

    /**
     * @param file
     *            read from here
     * @param out
     *            copy to here
     */
    protected void copy(File file, OutputStream out) {
	InputStream in = null;
	try {
	    in = new FileInputStream(file);
	    copy(in, out);
	} catch (FileNotFoundException e) {
	    throw new CopyException(e);
	} finally {
	    try {
		if (in != null)
		    in.close();
	    } catch (IOException e) {
		throw new CopyException(e);
	    }
	}
    }

    /**
     * @param in
     *            read from here
     * @param file
     *            copy to here
     */
    protected void copy(InputStream in, File file) {
	OutputStream out = null;
	try {
	    out = new FileOutputStream(file);
	    copy(in, out);
	} catch (FileNotFoundException e) {
	    throw new CopyException(e);
	} finally {
	    try {
		if (out != null)
		    out.close();
	    } catch (IOException e) {
		throw new CopyException(e);
	    }
	}
    }

    /**
     * @param downloaded
     *            if something has been downloaded
     */
    protected void setDownloaded(boolean downloaded) {
	this.downloaded = downloaded;
    }

    /**
     * @param updated
     *            if an object was updated
     */
    protected void setUpdated(boolean updated) {
	this.updated = updated;
    }

}
