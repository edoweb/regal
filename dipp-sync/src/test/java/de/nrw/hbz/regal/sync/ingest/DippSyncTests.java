package de.nrw.hbz.regal.sync.ingest;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

public class DippSyncTests
{

	@Test
	public void testMapping() throws URISyntaxException
	{
		DippMapping mapping = new DippMapping();
		File file = new File(Thread.currentThread()
				.getContextClassLoader()
				.getResource("QDC.xml").toURI());
		System.out.println(mapping.map(file,"dipp:1002"));
	}
}
