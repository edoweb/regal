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

    String testData = null;
    Search search = null;

    @Before
    public void setUp() throws IOException {
	testData = CopyUtils.copyToString(Thread.currentThread()
		.getContextClassLoader().getResourceAsStream("testData.json"),
		"utf-8");
	search = new Search();
	search.indexSync("test", "monograph", "edoweb:123", testData);
    }

    @After
    public void tearDown() {
	search.deleteSync("test", "type", "edoweb:123");
	for (int i = 100; i > 0; i--) {
	    search.deleteSync("test", "monograph", "edoweb:" + i);
	}
    }

    @Test
    public void testCreation() {
	Assert.assertNotNull(search);
    }

    @Test
    public void testResourceListing() throws InterruptedException {
	Thread.sleep(1000);
	SearchHits hits = search.listResources("test", "monograph", 0, 5);
	Assert.assertEquals(1, hits.getTotalHits());
    }

    @Test
    public void testResourceListing_withDefaultValues()
	    throws InterruptedException {
	Thread.sleep(1000);
	SearchHits hits = search.listResources("", "", 0, 10);
	Assert.assertEquals(1, hits.getTotalHits());
    }

    @Test(expected = Search.InvalidRangeException.class)
    public void testResourceListing_withWrongParameter() {
	search.listResources("test", "monograph", 5, 1);
    }

    @Test
    public void testDelete() throws InterruptedException {
	Thread.sleep(1000);
	SearchHits hits = search.listResources("test", "monograph", 0, 1);
	Assert.assertEquals(1, hits.getTotalHits());
	search.deleteSync("test", "monograph", "edoweb:123");
	hits = search.listResources("test", "monograph", 0, 1);
	Assert.assertEquals(0, hits.getTotalHits());
    }

    @Test
    public void testListIds() throws InterruptedException {
	search.indexSync("test", "monograph", "edoweb:123", testData);
	Thread.sleep(1000);
	List<String> list = search.listIds("test", "monograph", 0, 1);
	Assert.assertEquals(1, list.size());
	Assert.assertEquals(list.get(0), "edoweb:123");
    }

    @Test
    public void testFromUntil() throws InterruptedException {
	for (int i = 100; i > 0; i--) {
	    search.indexSync("test", "monograph", "edoweb:" + i, testData);
	}
	Thread.sleep(1000);
	List<String> list = search.listIds("test", "monograph", 0, 10);
	Assert.assertEquals(10, list.size());
	list = search.listIds("test", "monograph", 10, 50);
	Assert.assertEquals(40, list.size());
	list = search.listIds("test", "monograph", 60, 61);
	Assert.assertEquals(1, list.size());

	list = search.listIds("test", "monograph", 100, 150);
	Assert.assertEquals(1, list.size());
    }
}
