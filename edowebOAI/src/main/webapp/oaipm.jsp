
<%@ page language="java" contentType="text/xml; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ page import="org.xml.sax.*"%><%@ page
	import="javax.xml.transform.*"%><%@ page import="java.io.*"%><%@ page
	import="java.net.*"%><%@ page import="java.util.*"%><%@ page
	import="java.text.*"%>
<%
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
	/**
	 * 
	 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 07.11.2011
	 * 
	 */
	InputStream is = null;
	BufferedInputStream bis = null;
	ByteArrayOutputStream bibos = null;
	ByteArrayOutputStream bos = null;
	String stream = null;
	try
	{// this jsp template parses the oai-pmh response of digitool 
		// it adds a processing instruction to the output required for xsl-transformtion
		// and if installed on the same server where the caller html form resists solves 
		// the cross domain problem associated wih xslt and js (hopefully)
		// Please edit the urls to your requirements

		String oaiServer = "http://klio.hbz-nrw.de";
		//String oaiServer="http://urania.hbz-nrw.de";
		String oaiPort = "8881";
		String oaiContext = "OAI-PUB";

		String targetServer = "http://klio.hbz-nrw.de";
		//String targetServer="http://urania.hbz-nrw.de";
		String targetPort = "1801";
		String targetContext = "edowebOAI/";

		String allSet = "edoweb-oai_dc-all";

		String[] originalSets = new String[] {
				"edoweb-oai_dc-pubType-wpd",
				"edoweb-oai_dc-pubType-ws", 
				"edoweb-oai_dc-pubType-ws_zip",
				"edoweb-oai_dc-pubType-ejo01_mets",
				"edoweb-oai_dc-pubType-ejo01_pdf", 
				"edoweb-oai_dc-all" };
		String[] displayedSets = new String[] { 
				"doc-type:report",
				"doc-type:website",
				"doc-type:webVersion", 
				"doc-type:periodical",
				"doc-type:periodicalPart", 
				"edoweb-oai_dc-all" };
		String[] namedSets = new String[] { 
				"report", 
				"Website",
				"Version of a Website",
				"Periodical", 
				"PeriodicalPart", 
				"All" };
		//-----------------------------------
		//-----------------------------------

		String targetserver = targetServer + ":" + targetPort + "/"
				+ targetContext;
		String oaiserver = oaiServer + ":" + oaiPort + "/" + oaiContext;

		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		String date = sdf.format(now);
		if (request.getParameter("verb") == null)
		{
			stream = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
					+ "<?xml-stylesheet type='text/xsl' href='oai2.xsl' ?>"
					+ "<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/"
					+ " http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">"
					+ "<responseDate>"
					+ date
					+ "Z</responseDate>"
					+ "<request>http://klio.hbz-nrw.de:1801/edowebOAI/</request>"
					+ "<error code=\"noVerb\">No verb please use "
					+ targetserver + "?verb=Identify</error>"
					+ "</OAI-PMH>";
		} else if (request.getParameter("verb").compareTo("ListSets") == 0)
		{

			stream = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
					+ "<?xml-stylesheet type='text/xsl' href='oai2.xsl' ?>"
					+ "<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/"
					+ "http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">"
					+ "<responseDate>" + date + "Z</responseDate>"
					+ "<request verb=\"ListSets\">" + targetserver
					+ "</request>";

			stream += "<ListSets>";
			for (int i = 0; i < displayedSets.length; i++)
			{
				stream += "<set>";
				stream += "<setSpec>";
				stream += displayedSets[i];
				stream += "</setSpec>";
				stream += "<setName>";
				stream += namedSets[i];
				stream += "</setName>";
				stream += "</set>";
			}
			stream += "</ListSets>";
			stream += "</OAI-PMH> ";
		} else if (request.getParameter("verb").compareTo(
				"ListMetadataFormats") == 0)
		{
			stream = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
					+ "<?xml-stylesheet type='text/xsl' href='oai2.xsl' ?>"
					+ "<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/"
					+ "http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">"
					+ "<responseDate>"
					+ date
					+ "Z</responseDate>"
					+ "<request verb=\"ListMetadataFormats\">"
					+ targetserver
					+ "</request>"
					+ "<ListMetadataFormats>"
					+ "<metadataFormat>"
					+ "<metadataPrefix>oai_dc</metadataPrefix>"
					+ "<schema>http://www.openarchives.org/OAI/2.0/oai_dc/oai_dc.xsd</schema>"
					+ "<metadataNamespace>http://www.openarchives.org/OAI/2.0/oai_dc</metadataNamespace>"
					+ "</metadataFormat>" + "</ListMetadataFormats>"
					+ "</OAI-PMH> ";
		} else
		{
			StringBuffer reqStr = new StringBuffer();

			boolean foundSet = false;

			Enumeration<String> parameterList = request
					.getParameterNames();
			//System.out.println(request.getQueryString());
			while (parameterList.hasMoreElements())
			{
				String parameterName = parameterList.nextElement()
						.toString();

				if (parameterName.compareTo("set") == 0)
				{
					foundSet = true;
				}

				reqStr.append(parameterName + "=");
				String[] parameterValues = request
						.getParameterValues(parameterName);

				for (int i = 0; i < parameterValues.length; i++)
				{
					reqStr.append(parameterValues[i]);
				}
				reqStr.append("&");
			}

			if (request.getParameter("verb").compareTo("ListRecords") == 0
					&& !foundSet)
			{
				reqStr.append("set=" + allSet + "&");
			} else if (request.getParameter("verb").compareTo(
					"ListIdentifiers") == 0
					&& !foundSet)
			{
				reqStr.append("set=" + allSet + "&");
			}

			String requestStr = reqStr
					.substring(0, reqStr.length() - 1);

			HashMap<String, String> dini2dtlSpecMap = new HashMap<String, String>();
			HashMap<String, String> dini2dtlNameMap = new HashMap<String, String>();
			HashMap<String, String> dtl2diniSpecMap = new HashMap<String, String>();
			HashMap<String, String> dtl2diniNameMap = new HashMap<String, String>();

			for (int i = 0; i < originalSets.length; i++)
			{
				dini2dtlSpecMap.put(originalSets[i], displayedSets[i]);
				dini2dtlNameMap.put(originalSets[i], namedSets[i]);

				dtl2diniSpecMap.put(displayedSets[i], originalSets[i]);
				dtl2diniNameMap.put(namedSets[i], originalSets[i]);
			}

			if (requestStr != null)
				for (String setSpec : dtl2diniSpecMap.keySet())
				{
					requestStr = requestStr.replace("set=" + setSpec,
							"set=" + dtl2diniSpecMap.get(setSpec));

				}

			String requestUrl = oaiserver + "?" + requestStr;

			URL rUrl = new URL(requestUrl);
			is = rUrl.openStream();
			bis = new BufferedInputStream(is);
			bibos = new ByteArrayOutputStream();

			int i = -1;
			while ((i = bis.read()) != -1)
			{
				bibos.write(i);
			}

			stream = new String(bibos.toByteArray(), "UTF-8");
			stream = stream
					.replace(
							"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>",
							"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<?xml-stylesheet type='text/xsl' href='oai2.xsl' ?>");
			stream = stream.replace(oaiPort + "/" + oaiContext,
					targetPort + "/" + targetContext);
			for (String setSpec : dini2dtlSpecMap.keySet())
			{
				stream = stream.replace("<setSpec>" + setSpec
						+ "</setSpec>",
						"<setSpec>" + dini2dtlSpecMap.get(setSpec)
								+ "</setSpec>");
			}
		}
		
		
		stream = stream.replaceAll("\n", "").replace("\r", "");
		byte[] bArray = stream.getBytes("UTF-8");
		bos = new ByteArrayOutputStream();
		for (int j = 0; j < bArray.length; j++)
		{
			bos.write(bArray[j]);
		}

		out.print(bos);
		
	} catch (Exception e)
	{
		e.printStackTrace();
	} finally
	{
		try
		{
			if (is != null)
				is.close();
		} catch (Exception e)
		{
		}

		try
		{
 
			if (bibos != null)
				bibos.close();
		} catch (Exception e)
		{

		}
		try
		{
			if (bos != null)
				bos.close();
		} catch (Exception e)
		{

		}
		try
		{
			if (bis != null)
				bis.close();
		} catch (Exception e)
		{

		}

	}
%>