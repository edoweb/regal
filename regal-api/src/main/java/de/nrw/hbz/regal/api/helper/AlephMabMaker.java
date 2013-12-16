package de.nrw.hbz.regal.api.helper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.FileUtils;
import org.culturegraph.mf.Flux;

import de.nrw.hbz.regal.datatypes.Node;
import de.nrw.hbz.regal.exceptions.ArchiveException;

/**
 * 517 Diplomarbeit
 * http://193.30.112.134/F/?func=find-c&ccl_term=IDN%3DTT002234244
 * Selbstständiges Werk
 * http://193.30.112.134/F/?func=find-c&ccl_term=IDN%3DHT015763211 Band
 * http://193.30.112.134/F/?func=find-c&ccl_term=IDN%3DHT015771469
 * 
 * @author jan
 * 
 */
public class AlephMabMaker {

    public String aleph(Node node, String uriPrefix) {

	String pid = node.getPID();
	if (node == null)
	    return "No node with pid " + pid + " found";

	String metadata = uriPrefix + pid + "/metadata";
	try {
	    File outfile = File.createTempFile("mabxml", "xml");
	    outfile.deleteOnExit();
	    File fluxFile = new File(Thread.currentThread()
		    .getContextClassLoader()
		    .getResource("morph-lobid-to-mabxml.flux").toURI());
	    Flux.main(new String[] { fluxFile.getAbsolutePath(),
		    "url=" + metadata, "out=" + outfile.getAbsolutePath() });
	    return FileUtils.readFileToString(outfile);
	} catch (IOException e) {
	    throw new ArchiveException(pid + " " + e.getMessage(), e);
	} catch (URISyntaxException e) {
	    throw new ArchiveException(pid + " " + e.getMessage(), e);
	} catch (RecognitionException e) {
	    throw new ArchiveException(pid + " " + e.getMessage(), e);
	}
    }

}
