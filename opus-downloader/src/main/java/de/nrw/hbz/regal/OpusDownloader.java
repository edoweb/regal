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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * http://193.30.112.23:9280/fedora/get/dipp:1001?xml=true
 * http://193.30.112.23:9280/fedora/listDatastreams/dipp:1001?xml=true
 * http://193.30.112.23:9280/fedora/get/dipp:1001/DiPPExt
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class OpusDownloader implements DownloaderInterface
{
	final static Logger logger = LoggerFactory.getLogger(OpusDownloader.class);

	// DigitalEntityBeanBuilder beanBuilder = null;
	String downloadLocation = null;
	String objectDirectory = null;
	String server = null;
	boolean updated = false;
	boolean downloaded = false;
	HashMap<String, String> map = new HashMap<String, String>();

	public OpusDownloader()
	{
		// beanBuilder = new DigitalEntityBeanBuilder();

	}

	/**
	 * @param server
	 *            the digitool server to download from
	 * @param downloadLocation
	 *            a local directory to store the downloaded data
	 */
	public void init(String server, String downloadLocation)
	{
		this.downloadLocation = downloadLocation;
		this.server = server;
		// beanBuilder = new DigitalEntityBeanBuilder();
	}

	/**
	 * @param pid
	 *            the digitool pid
	 * @return a message for the user
	 * @throws IOException
	 *             if something goes wrong
	 */
	public String download(String pid) throws IOException
	{

		return download(pid, true);

	}

	/**
	 * @param pid
	 *            a valid digitool pid
	 * @param forceDownload
	 *            if true the data will be downloaded. if false the data will
	 *            only be downloaded if isn't there yet
	 * @return a message for the user
	 * @throws IOException
	 *             if something goes wrong
	 */
	public String download(String pid, boolean forceDownload)
			throws IOException
	{

		if (map.containsKey(pid))
			throw new IOException(pid + " already visited!");

		objectDirectory = downloadLocation + File.separator + pid;

		File dir = new File(objectDirectory);

		if (!dir.exists())
		{

			logger.info("Create Directory " + dir.getAbsoluteFile()
					+ " and start to Download files");
			dir.mkdirs();

			try
			{

				downloadObject(dir, pid);
			}
			catch (Exception e)
			{
				logger.debug(e.getMessage());
			}
			setUpdated(false);
			setDownloaded(true);
		}
		else if (forceDownload)
		{
			logger.info("Directory " + dir.getAbsoluteFile()
					+ " exists. Force override.");
			FileUtils.deleteDirectory(dir);
			dir.mkdirs();

			try
			{
				downloadObject(dir, pid);
			}
			catch (Exception e)
			{
				logger.debug(e.getMessage());
			}

			setUpdated(true);
			setDownloaded(true);
		}
		else
		{
			logger.info("Directory " + dir.getAbsoluteFile()
					+ " exists. Step over.");
			setDownloaded(false);
			setUpdated(false);
		}
		map.clear();
		return dir.getAbsolutePath();

	}

	private void downloadObject(File dir, String pid) throws Exception
	{
		try
		{
			if (!map.containsKey(pid))
			{
				map.put(pid, pid);

			}
			else
			{
				throw new Exception(pid + " already visited!");
			}

			downloadXMetaDissPlus(dir, pid);
			downloadPdfs(dir, pid);

		}
		catch (MalformedURLException e)
		{
			logger.error(e.getMessage());

		}
		catch (IOException e)
		{

		}

	}

	private void downloadXMetaDissPlus(File dir, String pid) throws IOException
	{

		String url = server + pid;
		logger.info("Download: " + url);
		URL dataStreamUrl = new URL(url);
		File dataStreamFile = new File(dir.getAbsolutePath() + File.separator
				+ "" + pid + ".xml");
		// dataStreamFile.createNewFile();

		logger.info("Save: " + dataStreamFile.getAbsolutePath());

		String data = null;
		StringWriter writer = new StringWriter();
		IOUtils.copy(dataStreamUrl.openStream(), writer);
		data = writer.toString();
		FileUtils.writeStringToFile(dataStreamFile, data, "utf-8");

		// InputStream in = null;
		// URLConnection uc = dataStreamUrl.openConnection();
		// uc.connect();
		// in = uc.getInputStream();
		// FileOutputStream out = new FileOutputStream(dataStreamFile);
		//
		// byte[] buffer = new byte[1024];
		// int bytesRead = -1;
		// while ((bytesRead = in.read(buffer)) > -1)
		// {
		// out.write(buffer, 0, bytesRead);
		// }
		// in.close();

	}

	private void downloadPdfs(File dir, String pid)
	{
		Vector<String> files = new Vector<String>();
		String identifier = null;
		Element xMetaDissPlus = getDocument(new File(dir.getAbsolutePath()
				+ File.separator + pid + ".xml"));

		NodeList identifiers = xMetaDissPlus
				.getElementsByTagName("ddb:identifier");

		for (int i = 0; i < identifiers.getLength(); i++)
		{
			Element id = (Element) identifiers.item(i);
			identifier = id.getTextContent();
		}
		NodeList fileProperties = xMetaDissPlus
				.getElementsByTagName("ddb:fileProperties");

		for (int i = 0; i < fileProperties.getLength(); i++)
		{
			Element fileProperty = (Element) fileProperties.item(i);
			String filename = fileProperty.getAttribute("ddb:fileName");
			files.add(filename);
		}

		int i = 0;
		for (String file : files)
		{

			try
			{
				if (file.endsWith("pdf"))
				{
					i++;
					download(dir.getAbsoluteFile() + File.separator + pid + "_"
							+ i + ".pdf", identifier + "/pdf/" + file);
				}
			}
			catch (IOException e)
			{
				logger.error(e.getMessage());
			}
		}
	}

	private void download(String file, String url) throws IOException
	{
		URL dataStreamUrl = new URL(url);

		InputStream in = null;
		URLConnection uc = dataStreamUrl.openConnection();
		uc.connect();
		in = uc.getInputStream();
		FileOutputStream out = new FileOutputStream(file);

		byte[] buffer = new byte[1024];
		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) > -1)
		{
			out.write(buffer, 0, bytesRead);
		}
		in.close();
	}

	private Element stringToElement(String data)
	{
		try
		{
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
		}
		catch (FileNotFoundException e)
		{

			e.printStackTrace();
		}
		catch (SAXException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{

			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return true if the downloader has updated an existing dataset
	 */
	public boolean hasUpdated()
	{
		return updated;
	}

	/**
	 * @return true if data has been downloaded
	 */
	public boolean hasDownloaded()
	{
		return downloaded;
	}

	private void setDownloaded(boolean downloaded)
	{
		this.downloaded = downloaded;
	}

	private void setUpdated(boolean updated)
	{
		this.updated = updated;
	}

	private File getXml(File file, URL url) throws IOException
	{

		URLConnection con = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream(), "UTF-8"));

		StringWriter strOut = new StringWriter();

		// Copy stream to String
		char[] buf = new char[1024];
		int n;
		while ((n = in.read(buf)) != -1)
		{
			strOut.write(buf, 0, n);
		}

		String str = strOut.toString();
		strOut.close();
		in.close();

		// copy String to File
		in = new BufferedReader(new StringReader(str));
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		buf = new char[1024];
		while ((n = in.read(buf)) != -1)
		{
			out.write(buf, 0, n);
		}

		out.close();
		in.close();

		return file;
	}

	private Element getDocument(File digitalEntityFile)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder;

			docBuilder = factory.newDocumentBuilder();

			Document doc;

			doc = docBuilder.parse(new BufferedInputStream(new FileInputStream(
					digitalEntityFile)));
			Element root = doc.getDocumentElement();
			root.normalize();
			return root;
		}
		catch (FileNotFoundException e)
		{

			e.printStackTrace();
		}
		catch (SAXException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{

			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	void zip(File directory, File zipfile) throws IOException
	{
		URI base = directory.toURI();
		Deque<File> queue = new LinkedList<File>();
		queue.push(directory);
		OutputStream out = new FileOutputStream(zipfile);
		Closeable res = out;
		try
		{
			ZipOutputStream zout = new ZipOutputStream(out);
			res = zout;
			while (!queue.isEmpty())
			{
				directory = queue.pop();
				for (File kid : directory.listFiles())
				{
					String name = base.relativize(kid.toURI()).getPath();
					if (kid.isDirectory())
					{
						queue.push(kid);
						name = name.endsWith("/") ? name : name + "/";
						zout.putNextEntry(new ZipEntry(name));
					}
					else
					{
						zout.putNextEntry(new ZipEntry(name));
						copy(kid, zout);
						zout.closeEntry();
					}
				}
			}
		}
		finally
		{
			res.close();
		}
	}

	void copy(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024];
		while (true)
		{
			int readCount = in.read(buffer);
			if (readCount < 0)
			{
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}

	void copy(File file, OutputStream out) throws IOException
	{
		InputStream in = new FileInputStream(file);
		try
		{
			copy(in, out);
		}
		finally
		{
			in.close();
		}
	}

	void copy(InputStream in, File file) throws IOException
	{
		OutputStream out = new FileOutputStream(file);
		try
		{
			copy(in, out);
		}
		finally
		{
			out.close();
		}
	}

	private void run(String propFile) throws IOException
	{
		Properties properties = new Properties();
		try
		{
			properties.load(new BufferedInputStream(new FileInputStream(
					propFile)));
		}
		catch (IOException e)
		{
			throw new IOException("Could not open " + propFile + "!");
		}
		this.server = properties.getProperty("piddownloader.server");
		this.downloadLocation = properties
				.getProperty("piddownloader.downloadLocation");

		PIDReporter pidreporter = new PIDReporter();
		Vector<String> pids = pidreporter.getPids(propFile);

		for (int i = 0; i < pids.size(); i++)
		{
			String pid = pids.elementAt(i);
			logger.info((i + 1) + "/" + pids.size() + " Download " + pid + " !");
			download(pid);
		}

	}

	/**
	 * @param argv
	 *            the argument vector must contain exactly one item which points
	 *            to a valid property file
	 */
	public static void main(String[] argv)
	{
		if (argv.length != 1)
		{
			System.out.println("\nWrong Number of Arguments!");
			System.out.println("Please specify a config.properties file!");
			System.out
					.println("Example: java -jar dtldownloader.jar dtldownloader.properties\n");
			System.out
					.println("Example Properties File:\n\tpidreporter.server=http://urania.hbz-nrw.de:1801/edowebOAI/\n\tpidreporter.set=null\n\tpidreporter.harvestFromScratch=true\n\tpidreporter.pidFile=pids.txt\n\tpiddownloader.server=http://klio.hbz-nrw.de:1801\n\tpiddownloader.downloadLocation=/tmp/zbmed");
			System.exit(1);
		}

		OpusDownloader main = new OpusDownloader();
		try
		{
			main.run(argv[0]);
		}
		catch (IOException e)
		{
			logger.warn(e.getMessage());
			System.exit(2);
		}
	}

}
