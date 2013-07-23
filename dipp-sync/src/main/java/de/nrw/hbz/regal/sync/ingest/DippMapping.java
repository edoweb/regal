package de.nrw.hbz.regal.sync.ingest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.FileUtils;
import org.culturegraph.mf.Flux;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schnasse schnassse@hbz-nrw.de
 * 
 */
public class DippMapping {
    final static Logger logger = LoggerFactory.getLogger(DippMapping.class);

    /**
     * @param file
     *            the content of the file will be mapped. Expected: Dipp qdc
     *            stream.
     * @param pid
     *            the pid becomes the new subject
     * @return a lobid conform mapping of dipp qdc datastream
     */
    public String map(File file, String pid) {
	try {
	    return flux(file, pid);
	} catch (URISyntaxException e) {
	    logger.error(e.getMessage());
	} catch (IOException e) {
	    logger.error(e.getMessage());
	} catch (RecognitionException e) {
	    logger.error(e.getMessage());
	}
	return null;
    }

    private String flux(File file, String pid) throws URISyntaxException,
	    IOException, RecognitionException {
	File outfile = File.createTempFile("lobid", "rdf");
	outfile.deleteOnExit();
	File fluxFile = new File(Thread.currentThread().getContextClassLoader()
		.getResource("dipp-qdc-to-lobid.flux").toURI());
	Flux.main(new String[] { fluxFile.getAbsolutePath(),
		"in=" + file.getAbsolutePath(),
		"out=" + outfile.getAbsolutePath(), "subject=" + pid });
	return FileUtils.readFileToString(outfile);
    }
}
