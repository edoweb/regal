package de.nrw.hbz.edoweb2.sync.extern;

public interface DigitalEntityBuilder
{
	/**
	 * Builds a java representation of a Digitool object the original object
	 * must be downloaded first in a local directory using DigitoolDownloader.
	 * 
	 * @param baseDir
	 *            the dir in which the downloaded digitool object exists
	 * @param pid
	 *            the pid of the object
	 * @return a DigitalEntity java representation of the digitool object
	 * @throws Exception
	 *             if something goes wrong you probably want to stop.
	 */
	public DigitalEntity build(String baseDir, String pid) throws Exception;
}
