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
package de.nrw.hbz.edoweb2.digitool.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.nrw.hbz.edoweb2.digitool.pidreporter.PIDReporter;

/**
 * Class DigitoolDownloader
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
public class DigitoolDownloader
{
	final static Logger logger = LoggerFactory
			.getLogger(DigitoolDownloader.class);

	// DigitalEntityBeanBuilder beanBuilder = null;
	String downloadLoaction = null;
	String objectDirectory = null;
	String server = null;
	boolean updated = false;
	boolean downloaded = false;

	/**
	 * Only for main->run! API-CALLs must use DigitoolDownloader(String,String);
	 */
	private DigitoolDownloader()
	{
		// beanBuilder = new DigitalEntityBeanBuilder();

	}

	/**
	 * @param server
	 * @param downloadLocation
	 */
	public DigitoolDownloader(String server, String downloadLocation)
	{
		this.downloadLoaction = downloadLocation;
		this.server = server;
		// beanBuilder = new DigitalEntityBeanBuilder();
	}

	public String download(String pid) throws IOException
	{
		return download(pid, true);
	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param string
	 */
	public String download(String pid, boolean forceDownload)
			throws IOException
	{

		objectDirectory = downloadLoaction + File.separator + pid;

		File dir = new File(objectDirectory);
		File digitalEntityFile = null;
		if (!dir.exists())
		{
			logger.info("Create Directory " + dir.getAbsoluteFile()
					+ " and start to Download files");
			dir.mkdir();
			digitalEntityFile = getView(pid);
			getRelated(digitalEntityFile, pid);
			try
			{
				getStream(digitalEntityFile, pid);
			}
			catch (Exception e)
			{
				logger.error(e.getMessage());
			}
			// beanBuilder.buildComplexBean(objectDirectory, pid);
			setUpdated(false);
			setDownloaded(true);
		}
		else if (forceDownload)
		{
			logger.info("Directory " + dir.getAbsoluteFile()
					+ " exists. Force override.");
			FileUtils.deleteDirectory(dir);
			dir.mkdir();
			digitalEntityFile = getView(pid);
			getRelated(digitalEntityFile, pid);
			try
			{
				getStream(digitalEntityFile, pid);
			}
			catch (Exception e)
			{
				logger.error(e.getMessage());
			}
			// beanBuilder.buildComplexBean(objectDirectory, pid);
			setUpdated(true);
			setDownloaded(true);
		}
		else
		{
			logger.info("Directory " + dir.getAbsoluteFile()
					+ " exists. Step over.");
			setDownloaded(false);
		}

		if (digitalEntityFile != null)
		{
			String ppid = getParent(digitalEntityFile, pid);

			if (ppid != null)
			{

				String path = downloadLoaction + File.separator + ppid;
				File parent = new File(path);
				if (!parent.exists())
				{
					FileUtils.deleteDirectory(dir);
					throw new IOException(
							"Can't download part without downloading parent. Parent pid is "
									+ ppid);
				}
				else
				{
					File tdir = new File(parent.getAbsolutePath());
					if (!tdir.exists())
					{
						for (String file : dir.list())
						{
							File f = new File(dir.getAbsoluteFile()
									+ File.separator + file);
							if (f.isDirectory())
							{
								FileUtils.moveDirectoryToDirectory(f, tdir,
										true);
							}
							else
							{
								File test = new File(tdir + File.separator
										+ f.getName());
								if (test.exists())
									test.delete();
								FileUtils.moveFileToDirectory(f, tdir, true);

							}
						}

						setUpdated(false);
						setDownloaded(true);
					}
					else if (forceDownload)
					{

						for (String file : dir.list())
						{
							File f = new File(dir.getAbsoluteFile()
									+ File.separator + file);
							if (f.isDirectory())
							{
								File test = new File(tdir + File.separator
										+ f.getName());
								if (test.exists())
									FileUtils.deleteDirectory(test);
								FileUtils.moveDirectoryToDirectory(f, tdir,
										true);
							}
							else
							{
								File test = new File(tdir + File.separator
										+ f.getName());
								if (test.exists())
									test.delete();
								FileUtils.moveFileToDirectory(f, tdir, true);
							}
						}
						setUpdated(true);
						setDownloaded(true);
					}
					else
					{
						logger.info("Directory " + tdir.getAbsoluteFile()
								+ " exists. Step over.");
						setDownloaded(false);
					}

				}
				FileUtils.deleteDirectory(dir);
				return parent.getAbsolutePath();
			}
		}
		return dir.getAbsolutePath();
	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param digitalEntityFile
	 * @param pid
	 * @throws IOException
	 */
	private void getStream(File digitalEntityFile, String pid)
			throws IOException
	{
		Element root = getDocument(digitalEntityFile);
		if (root == null)
		{
			logger.error("Not able to download related files. XML parsing error: "
					+ pid);
			return;
		}
		Node streamRef = root.getElementsByTagName("stream_ref").item(0);

		String filename = ((Element) streamRef)
				.getElementsByTagName("file_name").item(0).getTextContent();
		// DIFF between pdfs and zips here! Make different DeliveryRule in
		// Digitoo
		if (filename == null || filename.isEmpty())
			return;
		File streamDir = new File(objectDirectory + File.separator + pid);
		if (!streamDir.exists())
		{
			streamDir.mkdir();
		}
		String path = streamDir.getAbsolutePath() + File.separator + filename;

		String fileExtension = path.substring(path.lastIndexOf('.'));
		File streamFile = new File(path);
		URL url = null;
		if (fileExtension.compareTo(".zip") == 0)
		{
			// System.out.println("Found zip!");
			url = new URL(server + "/webclient/DeliveryManager?pid=" + pid
					+ "&custom_att_2=default_viewer");
			// System.out.println("wget -O test.zip \"" + url.toString() +
			// "\"");
		}
		else
		{
			// System.out.println("Found not zip!");
			url = new URL(server + "/webclient/DeliveryManager?pid=" + pid
					+ "&amp;custom_att_2=simple_viewer");
		}

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setInstanceFollowRedirects(true);

		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try
		{
			in = new BufferedInputStream(con.getInputStream());
			out = new BufferedOutputStream(new FileOutputStream(streamFile));
			byte[] buf = new byte[1024];
			int n;
			while ((n = in.read(buf)) != -1)
			{
				out.write(buf, 0, n);
			}
		}
		catch (FileNotFoundException e)
		{
			logger.error("File Not Found (" + pid + "): " + e.getMessage());
		}
		finally
		{
			if (out != null)
				out.close();
			if (in != null)
				in.close();
		}
	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param pid
	 * @throws IOException
	 */
	private void getRelated(File digitalEntityFile, String pid)
			throws IOException
	{
		// File indexFile = null;
		Element root = getDocument(digitalEntityFile);
		if (root == null)
		{
			logger.error("Not able to download related files. XML parsing error: "
					+ pid);
			return;
		}
		NodeList list = root.getElementsByTagName("relation");
		for (int i = 0; i < list.getLength(); i++)
		{
			Node item = list.item(i);
			String relPid = ((Element) item).getElementsByTagName("pid")
					.item(0).getTextContent();
			// String usageType = ((Element) item)
			// .getElementsByTagName("usage_type").item(0)
			// .getTextContent();
			File file = new File(objectDirectory + File.separator + relPid
					+ ".xml");
			getXml(file, relPid);
			getStream(file, relPid);
		}

	}

	private String getParent(File digitalEntityFile, String pid)
			throws IOException
	{
		// File indexFile = null;
		Element root = getDocument(digitalEntityFile);
		if (root == null)
		{
			logger.error("Not able to download related files. XML parsing error: "
					+ pid);
			return null;
		}
		NodeList list = root.getElementsByTagName("relation");
		for (int i = 0; i < list.getLength(); i++)
		{
			Node item = list.item(i);
			String relPid = ((Element) item).getElementsByTagName("pid")
					.item(0).getTextContent();
			String type = ((Element) item).getElementsByTagName("type").item(0)
					.getTextContent();

			if (type.compareTo("part_of") == 0)
			{
				return relPid;
			}
		}
		return null;

	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param pid
	 * @throws IOException
	 */
	private File getView(String pid) throws IOException
	{

		File digitalEntityFile = new File(objectDirectory + File.separator
				+ pid + ".xml");
		digitalEntityFile = getXml(digitalEntityFile, pid);
		return digitalEntityFile;
	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param digitalEntityFile
	 * @return
	 */
	private File getXml(File file, String pid) throws IOException
	{
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
		while ((n = in.read(buf)) != -1)
		{
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
		while ((n = in.read(buf)) != -1)
		{
			out.write(buf, 0, n);
		}

		out.close();
		in.close();

		return file;
	}

	/**
	 * <p>
	 * <em>Title: </em>
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param in
	 * @return
	 */
	private String transformString(String str)
	{
		Pattern p1 = Pattern.compile("<\\!\\[CDATA\\[");
		Pattern p2 = Pattern.compile("\\]\\]>");
		Pattern p3 = Pattern.compile("<.xml\\ version=[^>]*>");

		Matcher m = p1.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result = m.find();
		while (result)
		{
			m.appendReplacement(sb, "");
			result = m.find();
		}
		m.appendTail(sb);
		str = null;
		m = p2.matcher(sb);
		StringBuffer sb2 = new StringBuffer();
		result = m.find();
		while (result)
		{
			m.appendReplacement(sb2, "");
			result = m.find();
		}
		m.appendTail(sb2);
		sb = null;
		m = p3.matcher(sb2);
		StringBuffer sb3 = new StringBuffer();
		result = m.find();
		while (result)
		{
			m.appendReplacement(sb3, "");
			result = m.find();
		}
		m.appendTail(sb3);
		sb2 = null;
		return sb3.toString();
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

	public boolean hasUpdated()
	{
		return updated;
	}

	public void setUpdated(boolean updated)
	{
		this.updated = updated;
	}

	public boolean hasDownloaded()
	{
		return downloaded;
	}

	public void setDownloaded(boolean downloaded)
	{
		this.downloaded = downloaded;
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
		this.downloadLoaction = properties
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

		DigitoolDownloader main = new DigitoolDownloader();
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
