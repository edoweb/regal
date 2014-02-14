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
    Search search = null;
    private String edoweb3273325;
    private String edoweb3273325_2007;
    private String edoweb3273331;

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

	query1 = CopyUtils.copyToString(Thread.currentThread()
		.getContextClassLoader().getResourceAsStream("query-1.json"),
		"utf-8");
	search = new Search();
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

	SearchHits hits = search.query("test",
		"@graph.http://purl.org/dc/terms/isPartOf.@id",
		"edoweb\\:3273325-2007");

	Assert.assertEquals(1, hits.totalHits());

    }
}
