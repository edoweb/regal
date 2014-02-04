package de.nrw.hbz.regal.fedora;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.Graph;
import org.openrdf.rio.RDFFormat;

public class RdfUtilsTest {

    @Test
    public void testInputStreamToGraph() throws URISyntaxException {
	InputStream in = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream("HT015954381.txt");
	File expected = new File(Thread.currentThread().getContextClassLoader()
		.getResource("HT015954381_expected.txt").toURI().getPath());
	Graph graph = RdfUtils.readRdfToGraph(in, RDFFormat.NTRIPLES, "");
	String actual = RdfUtils.graphToString(graph, RDFFormat.NTRIPLES);
	System.out.println(actual);
	Assert.assertEquals(XmlUtils.fileToString(expected), actual);
    }
}
