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
package de.nrw.hbz.regal.search;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.search.SearchHits;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.nrw.hbz.regal.fedora.CopyUtils;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@SuppressWarnings("javadoc")
public class TestSearch {

    String edoweb2606976 = null;
    String query1 = null;
    SearchMock search = null;
    private String edoweb3273325, edoweb3273325_2007, edoweb3273331,
	    edowebMappingTest, drupalOne, drupalTwo, drupalThree, drupalFour,
	    drupalFive, drupalSix;

    @Before
    public void setUp() throws IOException {
	edoweb2606976 = CopyUtils.copyToString(
		Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("edoweb2606976.json"), "utf-8");
	edoweb3273325 = CopyUtils.copyToString(
		Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("edoweb3273325.json"), "utf-8");
	edoweb3273325_2007 = CopyUtils.copyToString(
		Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("edoweb3273325-2007.json"),
		"utf-8");
	edoweb3273331 = CopyUtils.copyToString(
		Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("edoweb3273331.json"), "utf-8");
	edowebMappingTest = CopyUtils
		.copyToString(Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("edowebMappingTest.json"), "utf-8");
	drupalOne = CopyUtils
		.copyToString(Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("1.json"), "utf-8");

	drupalTwo = CopyUtils
		.copyToString(Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("2.json"), "utf-8");
	drupalThree = CopyUtils
		.copyToString(Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("3.json"), "utf-8");
	drupalFour = CopyUtils
		.copyToString(Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("4.json"), "utf-8");
	drupalFive = CopyUtils
		.copyToString(Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("5.json"), "utf-8");
	drupalSix = CopyUtils
		.copyToString(Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("6.json"), "utf-8");

	query1 = CopyUtils.copyToString(Thread.currentThread()
		.getContextClassLoader().getResourceAsStream("query-1.json"),
		"utf-8");
	search = new SearchMock("test", "public-index-config.json");
	search.index("test", "monograph", "edoweb2606976", edoweb2606976);

    }

    @After
    public void tearDown() {
	search.delete("test", "type", "edoweb:123");
	for (int i = 100; i > 0; i--) {
	    search.delete("test", "monograph", "edoweb:" + i);
	}
	search.down();
    }

    @Test
    public void testCreation() {
	Assert.assertNotNull(search);
    }

    @Test
    public void testResourceListing() throws InterruptedException {
	SearchHits hits = search.listResources("test", "monograph", 0, 5);
	Assert.assertEquals(1, hits.getTotalHits());
    }

    @Test
    public void testResourceListing_withDefaultValues()
	    throws InterruptedException {
	SearchHits hits = search.listResources("", "", 0, 10);
	Assert.assertEquals(1, hits.getTotalHits());
    }

    @Test(expected = Search.InvalidRangeException.class)
    public void testResourceListing_withWrongParameter() {
	search.listResources("test", "monograph", 5, 1);
    }

    @Test
    public void testDelete() throws InterruptedException {
	SearchHits hits = search.listResources("test", "monograph", 0, 1);
	Assert.assertEquals(1, hits.getTotalHits());
	search.delete("test", "monograph", "edoweb2606976");
	hits = search.listResources("test", "monograph", 0, 1);
	Assert.assertEquals(0, hits.getTotalHits());
    }

    @Test
    public void testListIds() throws InterruptedException {
	search.index("test", "monograph", "edoweb2606976", edoweb2606976);
	List<String> list = search.listIds("test", "monograph", 0, 1);
	Assert.assertEquals(1, list.size());
	Assert.assertEquals(list.get(0), "edoweb2606976");
    }

    @Test
    public void testFromUntil() throws InterruptedException {
	for (int i = 100; i > 0; i--) {
	    search.index("test", "monograph", "edoweb:" + i, edoweb2606976);
	}
	List<String> list = search.listIds("test", "monograph", 0, 10);
	Assert.assertEquals(10, list.size());
	list = search.listIds("test", "monograph", 10, 50);
	Assert.assertEquals(40, list.size());
	list = search.listIds("test", "monograph", 60, 61);
	Assert.assertEquals(1, list.size());
	list = search.listIds("test", "monograph", 100, 150);
	Assert.assertEquals(1, list.size());
    }

    @Test
    public void mappingTest() {
	search.index("test", "monograph", "edoweb:3273325", edoweb3273325);
	search.index("test", "monograph", "edoweb:3273325-2007",
		edoweb3273325_2007);
	search.index("test", "monograph", "edoweb:3273331", edoweb3273331);
	SearchHits hits = search.query("test", "@graph.isPartOf",
		"edoweb:3273325-2007");
	Assert.assertEquals(1, hits.totalHits());
	Assert.assertEquals("edoweb:3273331", hits.getHits()[0].getId());
    }

    @Test
    public void indexTest() {
	search.index("test", "monograph", "edoweb:3273325", edoweb3273325);
	search.index("test", "monograph", "edoweb:3273325-2007",
		edoweb3273325_2007);
	search.index("test", "monograph", "edoweb:3273331", edoweb3273331);
    }

    @Test(expected = MapperParsingException.class)
    public void esSettings_fails() {
	search.down();
	search = new SearchMock("test", "public-index-config_fails.json");
	System.out.println("Fails with: "
		+ search.getSettings("test", "monograph"));
	search.index("test", "monograph",
		"edoweb:f1c9954d-f4d0-4d91-8f47-0a9c8f46df9b",
		edowebMappingTest);

    }

    @Test
    public void esSettings_succeed() {
	search.down();
	search = new SearchMock("test", "public-index-config_succeed.json");
	search.index("test", "monograph",
		"edoweb:f1c9954d-f4d0-4d91-8f47-0a9c8f46df9b",
		edowebMappingTest);
	System.out.println("Succeeds with: "
		+ search.getSettings("test", "monograph"));
    }

    @Test(expected = MapperParsingException.class)
    public void esSettingsDrupalBulk_fail() {
	// Make sure everything is fresh
	search.down();

	/*-
	 *  Initalize index with very simple mapping
	 * {
	 * "mappings": 
	 * {
	 *	"monograph": 
	 *	{
	 *		"properties": 
	 *		{
	 *			"@graph": 
	 *			{
	 *				"properties": 
	 *				{
	 *					"creatorName": 
	 *					{
	 *						"type": "string",
	 *						"index": "not_analyzed"
	 *					}
	 *				 }
	 *			 }
	 *		 }
	 *	 }
	 * }
	 * }
	 * 
	 */
	search = new SearchMock("test", "public-index-config_succeed.json");

	/*-
	 *Index
	 * {
	 * "@graph": 
	 * [
	 * 	{
	 * 	"@id" : "edoweb:1fe8fb0c-e844-4c07-9df3-7bf28d125e28",
	 * 		"creatorName": 
	 * 		{
	 * 			"@id": "http://d-nb.info/gnd/171948629"
	 * 		}
	 * 	}
	 * ]
	 * }
	 */
	search.index("test", "monograph",
		"edoweb:1fe8fb0c-e844-4c07-9df3-7bf28d125e28", drupalOne);

	// Print mapping
	System.out.println("\nCurrent Mapping: "
		+ search.getSettings("test", "monograph") + "\n");

	/*- 
	 * Index
	 * {
	 * "@graph": 
	 * [
	 * 	{
	 * 		"@id": "edoweb:f1c9954d-f4d0-4d91-8f47-0a9c8f46df9b",
	 * 		"creatorName": "1"
	 * 	}
	 * ]
	 * }
	 */
	// Here it goes off
	try {
	    search.index("test", "monograph",
		    "edoweb:f1c9954d-f4d0-4d91-8f47-0a9c8f46df9b", drupalFour);
	} catch (MapperParsingException e) {
	    e.printStackTrace();
	    throw e;
	}

    }

    @Test
    public void esSettingsDrupalBulk_succeed() {
	search.down();
	search = new SearchMock("test", "public-index-config_succeed.json");
	search.index("test", "monograph",
		"edoweb:1fe8fb0c-e844-4c07-9df3-7bf28d125e28", drupalOne);
	System.out.println("Succeeds with: "
		+ search.getSettings("test", "monograph"));
	search = new SearchMock("test", "public-index-config_succeed.json");
	search.index("test", "monograph",
		"edoweb:a601c448-b370-4bc5-b2ba-367b53ecc513", drupalTwo);
	System.out.println("Succeeds with: "
		+ search.getSettings("test", "monograph"));
	search = new SearchMock("test", "public-index-config_succeed.json");
	search.index("test", "monograph",
		"edoweb:ad744673-5ded-43a0-90c0-3f8f2223b4be", drupalThree);
	System.out.println("Succeeds with: "
		+ search.getSettings("test", "monograph"));
	search = new SearchMock("test", "public-index-config_succeed.json");
	search.index("test", "monograph",
		"edoweb:f1c9954d-f4d0-4d91-8f47-0a9c8f46df9b", drupalFour);
	System.out.println("Succeeds with: "
		+ search.getSettings("test", "monograph"));
	search = new SearchMock("test", "public-index-config_succeed.json");
	search.index("test", "monograph",
		"edoweb:ad462c5d-f566-41f9-986d-744c10bbd450", drupalFive);
	System.out.println("Succeeds with: "
		+ search.getSettings("test", "monograph"));
	search = new SearchMock("test", "public-index-config_succeed.json");
	search.index("test", "monograph",
		"edoweb:549b92ee-d0a0-4471-83dc-799b08f3c0f6", drupalSix);
	System.out.println("Succeeds with: "
		+ search.getSettings("test", "monograph"));

    }

    @Test(expected = MapperParsingException.class)
    public void esSettingsDrupalBulk_withNestedSettings_failsDirectly() {
	search.down();
	search = new SearchMock("test",
		"public-index-config_different_succeed.json");

	System.out.println("Succeeds with: "
		+ search.getSettings("test", "monograph"));

	search.index("test", "monograph",
		"edoweb:ad462c5d-f566-41f9-986d-744c10bbd450", drupalFour);

	search.index("test", "monograph",
		"edoweb:a601c448-b370-4bc5-b2ba-367b53ecc513", drupalOne);

    }
}
