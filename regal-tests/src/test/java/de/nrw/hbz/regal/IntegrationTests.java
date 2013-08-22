package de.nrw.hbz.regal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.nrw.hbz.regal.api.TestResource;
import de.nrw.hbz.regal.api.TestUpdateResource;
import de.nrw.hbz.regal.api.helper.TestActions;
import de.nrw.hbz.regal.fedora.FedoraFacadeTest;
import de.nrw.hbz.regal.sync.TestDigitoolDownloader;
import de.nrw.hbz.regal.sync.TestDippDownloader;
import de.nrw.hbz.regal.sync.TestEdowebDigitalEntityBuilder;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ TestResource.class, TestUpdateResource.class,
	TestActions.class, FedoraFacadeTest.class,
	TestDigitoolDownloader.class, TestDippDownloader.class,
	TestEdowebDigitalEntityBuilder.class })
public class IntegrationTests {

}
