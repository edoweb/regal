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
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
public class OpusDownloader extends Downloader
{

	protected void downloadObject(File dir, String pid)
	{
		try
		{
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
