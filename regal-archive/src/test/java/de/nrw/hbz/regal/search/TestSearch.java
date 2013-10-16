package de.nrw.hbz.regal.search;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.nrw.hbz.regal.fedora.CopyUtils;

public class TestSearch {

    String testData = null;
    Search search = null;

    @Before
    public void setUp() throws IOException {
	testData = CopyUtils.copyToString(Thread.currentThread()
		.getContextClassLoader().getResourceAsStream("testData.json"),
		"utf-8");
	search = new Search();
    }

    @Test
    public void testCreation() {
	Assert.assertNotNull(search);
    }

    @Test
    public void testIndex() {
	search.index("edoweb", "edoweb:123", testData);
    }
}
