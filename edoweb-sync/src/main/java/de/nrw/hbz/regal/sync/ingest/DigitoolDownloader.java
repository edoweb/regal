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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.nrw.hbz.regal.fedora.XmlUtils;
import de.nrw.hbz.regal.sync.extern.Md5Checksum;

/**
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class DigitoolDownloader extends Downloader {

    @SuppressWarnings({ "serial", "javadoc" })
    public class ChecksumNotMatchException extends RuntimeException {

    }

    @Override
    protected void downloadObject(File downloadDirectory, String pid) {
	try {
	    String baseDir = downloadDirectory.getAbsolutePath();
	    File digitalEntityFile = getView(pid, baseDir);
	    getRelated(digitalEntityFile, pid, baseDir);
	    getStream(digitalEntityFile, pid, baseDir);
	} catch (Exception e) {
	    logger.error(pid + " " + e);
	}

    }

    /**
     * @param digitalEntityFile
     * @param pid
     * @throws IOException
     */
    private void getStream(File digitalEntityFile, String pid, String baseDir)
	    throws IOException {
	Element root = XmlUtils.getDocument(digitalEntityFile);
	if (root == null) {
	    logger.error("Not able to download related files. XML parsing error: "
		    + pid);
	    return;
	}
	Node streamRef = root.getElementsByTagName("stream_ref").item(0);

	String filename = ((Element) streamRef)
		.getElementsByTagName("file_name").item(0).getTextContent();

	if (filename == null || filename.isEmpty())
	    return;
	File streamDir = new File(baseDir + File.separator + pid);
	if (!streamDir.exists()) {
	    streamDir.mkdir();
	}
	String path = streamDir.getAbsolutePath() + File.separator + filename;

	String fileExtension = path.substring(path.lastIndexOf('.'));
	File streamFile = new File(path);
	URL url = null;
	if (fileExtension.compareTo(".zip") == 0) {
	    url = new URL(server + "/webclient/DeliveryManager?pid=" + pid
		    + "&custom_att_2=default_viewer");
	} else {
	    url = new URL(server + "/webclient/DeliveryManager?pid=" + pid
		    + "&amp;custom_att_2=simple_viewer");
	}
	HttpURLConnection con = (HttpURLConnection) url.openConnection();
	con.setInstanceFollowRedirects(true);
	copy(con.getInputStream(), streamFile);
	String digitoolMd5 = getDigitoolMd5(root);
	String md5 = getMd5(streamFile);
	logger.info(pid + " md5: " + digitoolMd5 + " , " + md5);

	/*
	 * if null we cannot prove incorrect (sic!) transmission and therefore
	 * will NOT throw an exception
	 */
	if (digitoolMd5 != null && !digitoolMd5.equals(md5))
	    throw new ChecksumNotMatchException();
    }

    private String getMd5(File stream) {
	Md5Checksum md5 = new Md5Checksum();
	return md5.getMd5Checksum(stream);
    }

    private String getDigitoolMd5(Element root) {
	List<Element> elements = XmlUtils
		.getElements(
			"//checksum/checksumMethod[text()=\"MD5\"]/following-sibling::checksumValue[1]",
			root, null);
	if (elements.size() != 1)
	    return null;
	else
	    return elements.get(0).getTextContent();
    }

    /**
     * 
     * @param pid
     * @throws IOException
     */
    private void getRelated(File digitalEntityFile, String pid, String baseDir)
	    throws IOException {
	// File indexFile = null;
	Element root = XmlUtils.getDocument(digitalEntityFile);
	if (root == null) {
	    logger.error("Not able to download related files. XML parsing error: "
		    + pid);
	    return;
	}
	NodeList list = root.getElementsByTagName("relation");
	for (int i = 0; i < list.getLength(); i++) {
	    Node item = list.item(i);
	    String relPid = ((Element) item).getElementsByTagName("pid")
		    .item(0).getTextContent();
	    File file = new File(baseDir + File.separator + relPid + ".xml");
	    getXml(file, relPid);
	    getStream(file, relPid, baseDir);
	}

    }

    /**
     * @param pid
     * @throws IOException
     */
    private File getView(String pid, String baseDir) throws IOException {

	File digitalEntityFile = new File(baseDir + File.separator + pid
		+ ".xml");
	digitalEntityFile = getXml(digitalEntityFile, pid);
	return digitalEntityFile;
    }

    /**
     * @param digitalEntityFile
     * @return
     */
    private File getXml(File file, String pid) throws IOException {
	URL url = new URL(
		server
			+ "/webclient/DeliveryManager?application=Staff&user=Staff&metadata_request=true&pid="
			+ pid + "&GET_XML=1");
	URLConnection con = url.openConnection();
	BufferedReader in = new BufferedReader(new InputStreamReader(
		con.getInputStream(), "UTF-8"));

	StringWriter strOut = new StringWriter();

	// Copy stream to String
	char[] buf = new char[1024];
	int n;
	while ((n = in.read(buf)) != -1) {
	    strOut.write(buf, 0, n);
	}

	String str = strOut.toString();
	strOut.close();
	in.close();

	// transform String
	str = transformString(str);

	// copy String to File
	in = new BufferedReader(new StringReader(str));
	BufferedWriter out = new BufferedWriter(new FileWriter(file));
	buf = new char[1024];
	while ((n = in.read(buf)) != -1) {
	    out.write(buf, 0, n);
	}

	out.close();
	in.close();

	return file;
    }

    /**
     * @param in
     * @return
     */
    private String transformString(String str) {
	Pattern p1 = Pattern.compile("<\\!\\[CDATA\\[");
	Pattern p2 = Pattern.compile("\\]\\]>");
	Pattern p3 = Pattern.compile("<.xml\\ version=[^>]*>");

	Matcher m = p1.matcher(str);
	StringBuffer sb = new StringBuffer();
	boolean result = m.find();
	while (result) {
	    m.appendReplacement(sb, "");
	    result = m.find();
	}
	m.appendTail(sb);
	str = null;
	m = p2.matcher(sb);
	StringBuffer sb2 = new StringBuffer();
	result = m.find();
	while (result) {
	    m.appendReplacement(sb2, "");
	    result = m.find();
	}
	m.appendTail(sb2);
	sb = null;
	m = p3.matcher(sb2);
	StringBuffer sb3 = new StringBuffer();
	result = m.find();
	while (result) {
	    m.appendReplacement(sb3, "");
	    result = m.find();
	}
	m.appendTail(sb3);
	sb2 = null;
	return sb3.toString();
    }

    /**
     * @param argv
     *            the argument vector must contain exactly one item which points
     *            to a valid property file
     */
    public static void main(String[] argv) {
	if (argv.length != 1) {
	    System.out.println("\nWrong Number of Arguments!");
	    System.out.println("Please specify a config.properties file!");
	    System.out
		    .println("Example: java -jar dtldownloader.jar dtldownloader.properties\n");
	    System.out
		    .println("Example Properties File:\n\tpidreporter.server=http://urania.hbz-nrw.de:1801/edowebOAI/\n\tpidreporter.set=null\n\tpidreporter.harvestFromScratch=true\n\tpidreporter.pidFile=pids.txt\n\tpiddownloader.server=http://klio.hbz-nrw.de:1801\n\tpiddownloader.downloadLocation=/tmp/zbmed");
	    System.exit(1);
	}

	DigitoolDownloader main = new DigitoolDownloader();

	main.run(argv[0]);

    }

    @SuppressWarnings({ "javadoc", "serial" })
    public class ParentNotFoundException extends RuntimeException {

	public ParentNotFoundException() {

	}

	public ParentNotFoundException(String arg0) {
	    super(arg0);
	}

	public ParentNotFoundException(Throwable arg0) {
	    super(arg0);
	}

	public ParentNotFoundException(String arg0, Throwable arg1) {
	    super(arg0, arg1);
	}

    }
}
