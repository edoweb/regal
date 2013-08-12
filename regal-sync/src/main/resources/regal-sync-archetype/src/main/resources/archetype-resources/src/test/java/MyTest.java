#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package};

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

import ${package}.sync.Main;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class MyTest {

    @Test
    public void mappingTest() throws URISyntaxException {
	MyMapping mapping = new MyMapping();
	File file = new File(Thread.currentThread().getContextClassLoader()
		.getResource("testObject.xml").toURI());
	System.out.println(mapping.map(file, "mysubject:1"));
    }

    @Test
    public void mainTest() throws URISyntaxException {
	String downloadlocation = "http://fhdd.opus.hbz-nrw.de/oai2/oai2.php?verb=GetRecord&metadataPrefix=XMetaDissPlus&identifier=oai:fhdd.opus.hbz-nrw.de:";
	String oailocation = "http://fhdd.opus.hbz-nrw.de/oai2/oai2.php";
	String oaiset = "has-source-swb:false";

	String password = "your-regal-password";
	String user = "your-regal-user";
	String localcache = "/tmp/test/";
	String oaitimestamp = "oaitimestamp-test";
	String fedoraUrl = "http://localhost:8080/fedora";
	String pidlist = "pidlist.txt";
	Main.main(new String[] { "--mode", "INIT", "--user", user,
		"--password", password, "--dtl", downloadlocation, "-cache",
		localcache, "--oai", oailocation, "--set", oaiset,
		"--timestamp", oaitimestamp, "--fedoraBase", fedoraUrl,
		"--host", "http://localhost", "-list", pidlist, "-namespace",
		"test" });

	File timestamp = new File(oaitimestamp);
	timestamp.deleteOnExit();

	File cache = new File(localcache);
	System.out.println(cache.list());

    }

}
