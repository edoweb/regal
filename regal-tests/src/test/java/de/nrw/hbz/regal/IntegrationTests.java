package de.nrw.hbz.regal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.nrw.hbz.regal.api.TestResource;
import de.nrw.hbz.regal.api.TestUpdateResource;
import de.nrw.hbz.regal.api.helper.TestActions;
import de.nrw.hbz.regal.fedora.FedoraFacadeTest;
import de.nrw.hbz.regal.sync.TestDigitoolDownloader;
import de.nrw.hbz.regal.sync.TestDippDownloader;
import de.nrw.hbz.regal.sync.TestEdoweb2Fedora;
import de.nrw.hbz.regal.sync.TestEdowebDigitalEntityBuilder;
import de.nrw.hbz.regal.sync.TestOpusDownloader;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@Suite.SuiteClasses({ TestResource.class, TestUpdateResource.class,
	TestActions.class, FedoraFacadeTest.class,
	TestDigitoolDownloader.class, TestDippDownloader.class,
	TestEdoweb2Fedora.class, TestEdowebDigitalEntityBuilder.class,
	TestOpusDownloader.class })
public class IntegrationTests {

}
