package de.nrw.hbz.regal.sync;

import org.junit.Test;

import de.nrw.hbz.regal.api.helper.XmlUtils;
import de.nrw.hbz.regal.sync.extern.DigitalEntity;
import de.nrw.hbz.regal.sync.extern.StreamType;
import de.nrw.hbz.regal.sync.ingest.DigitoolDownloader;
import de.nrw.hbz.regal.sync.ingest.EdowebDigitalEntityBuilder;

@SuppressWarnings("javadoc")
public class TestEdowebDigitalEntityBuilder {

    @Test
    public void testEdowebDigitalEntityBulder() throws Exception {
	String server = "http://klio.hbz-nrw.de:1801";
	String downloadLocation = "/tmp/edowebbase/";
	String pid = "3237397";
	DigitoolDownloader downloader = new DigitoolDownloader();
	downloader.init(server, downloadLocation);
	String location = downloader.download(pid);
	EdowebDigitalEntityBuilder builder = new EdowebDigitalEntityBuilder();
	DigitalEntity de = builder.build(location, pid);
	System.out.println(de);
	System.out.println(XmlUtils.fileToString(de.getStream(StreamType.MARC)
		.getFile()));

    }
}
