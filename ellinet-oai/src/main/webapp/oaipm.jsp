<%@ page language="java" contentType="text/xml; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="org.xml.sax.*"%><%@ page import="javax.xml.transform.*"%><%@ page import="java.io.*"%><%@ page import="java.net.*"%><%@ page import="java.util.*"%><%
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
    	 * @author Jan Schnasse, schnasse@hbz-nrw.de
    	 * 
    	 */
    try {
	// Please edit the urls to your requirements
	String url = "http://klio.hbz-nrw.de:8881/OAI-PUB?";
    String requestStr = request.getQueryString();
   
 	
 	HashMap<String,String> dini2dtlSpecMap = new HashMap<String,String>();
 	HashMap<String,String> dini2dtlNameMap = new HashMap<String,String>();
 	
 	dini2dtlSpecMap.put("zbmed-oai_dc-ddc-610","ddc:610");
 	dini2dtlSpecMap.put("zbmed-oai_dc-ddc-630","ddc:630");
 	dini2dtlSpecMap.put("zbmed-oai_dc-openAccess","open_access");
 	dini2dtlSpecMap.put("zbmed-oai_dc-pubType-Other","doc-type:Other");
 	dini2dtlSpecMap.put("zbmed-oai_dc-pubType-doctoralThesis","doc-type:doctoralThesis");
 	dini2dtlSpecMap.put("zbmed-oai_dc-pubType-conferenceObject","doc-type:conferenceObject");
 	dini2dtlSpecMap.put("zbmed-oai_dc-pubType-book","doc-type:book");

 	dini2dtlNameMap.put("zbmed-oai_dc-ddc-610","Medical sciences Medicine");
 	dini2dtlNameMap.put("zbmed-oai_dc-ddc-630","Agriculture");
 	dini2dtlNameMap.put("zbmed-oai_dc-openAccess","open_access");
 	dini2dtlNameMap.put("zbmed-oai_dc-pubType-Other","Other");
 	dini2dtlNameMap.put("zbmed-oai_dc-pubType-doctoralThesis","DoctoralThesis");
 	dini2dtlNameMap.put("zbmed-oai_dc-pubType-conferenceObject","ConferenceObject");
 	dini2dtlNameMap.put("zbmed-oai_dc-pubType-book","Book");
 	
 	
 	HashMap<String,String> dtl2diniSpecMap = new HashMap<String,String>();
 	HashMap<String,String> dtl2diniNameMap = new HashMap<String,String>();
 	
 	dtl2diniSpecMap.put("ddc:610","zbmed-oai_dc-ddc-610");
 	dtl2diniSpecMap.put("ddc:630","zbmed-oai_dc-ddc-630");
 	dtl2diniSpecMap.put("open_access","zbmed-oai_dc-openAccess");
 	dtl2diniSpecMap.put("doc-type:Other","zbmed-oai_dc-pubType-Other");
 	dtl2diniSpecMap.put("doc-type:doctoralThesis","zbmed-oai_dc-pubType-doctoralThesis");
 	dtl2diniSpecMap.put("doc-type:conferenceObject","zbmed-oai_dc-pubType-conferenceObject");
 	dtl2diniSpecMap.put("doc-type:book","zbmed-oai_dc-pubType-book");

 	dtl2diniNameMap.put("Medical sciences Medicine","zbmed-oai_dc-ddc-610");
 	dtl2diniNameMap.put("Agriculture","zbmed-oai_dc-ddc-630");
 	dtl2diniNameMap.put("open_access","zbmed-oai_dc-openAccess");
 	dtl2diniNameMap.put("Other","zbmed-oai_dc-pubType-Other");
 	dtl2diniNameMap.put("DoctoralThesis","zbmed-oai_dc-pubType-doctoralThesis");
 	dtl2diniNameMap.put("ConferenceObject","zbmed-oai_dc-pubType-conferenceObject");
 	dtl2diniNameMap.put("Book","zbmed-oai_dc-pubType-book");

 
 	for(String setSpec : dtl2diniSpecMap.keySet())
 	{
 		requestStr=requestStr.replace("set="+setSpec,"set="+dtl2diniSpecMap.get(setSpec));
 		
 	}
 		
 		
 	String requestUrl = url + requestStr;
	URL rUrl = new URL(requestUrl);

	InputStream is = rUrl.openStream();
	BufferedInputStream bis = new BufferedInputStream(is);

	ByteArrayOutputStream bibos = new ByteArrayOutputStream ();
	
	int i = -1;
	while((i = bis.read()) != -1){
		bibos.write(i);
	}

	String stream = new String(bibos.toByteArray(), "UTF-8");
	stream = stream.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>", "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<?xml-stylesheet type='text/xsl' href='/OaiPmh/oai2.xsl' ?>");
 	
	for (String setSpec : dini2dtlSpecMap.keySet())
	{
		stream = stream.replace("<setSpec>"+setSpec+"</setSpec>","<setSpec>"+dini2dtlSpecMap.get(setSpec)+"</setSpec>");
	}
	
	
	byte[] bArray = stream.getBytes("UTF-8");
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	for(int j= 0; j< bArray.length; j++){
		bos.write(bArray[j]);
	}
	
    out.print(bos);
    }
  catch (Exception e) {
    e.printStackTrace( );
    }
%>