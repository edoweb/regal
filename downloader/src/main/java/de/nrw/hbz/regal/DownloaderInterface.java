package de.nrw.hbz.regal;

import java.io.IOException;

public interface DownloaderInterface
{

	/**
	 * @param pid
	 *            the digitool pid
	 * @return a message for the user
	 * @throws IOException
	 *             if something goes wrong
	 */
	public abstract String download(String pid) throws IOException;

	/**
	 * @param pid
	 *            a valid digitool pid
	 * @param forceDownload
	 *            if true the data will be downloaded. if false the data will
	 *            only be downloaded if isn't there yet
	 * @return a message for the user
	 * @throws IOException
	 *             if something goes wrong
	 */
	public abstract String download(String pid, boolean forceDownload)
			throws IOException;

	/**
	 * @return true if the downloader has updated an existing dataset
	 */
	public abstract boolean hasUpdated();

	/**
	 * @return true if data has been downloaded
	 */
	public abstract boolean hasDownloaded();

	/**
	 * @param server
	 *            the server to download from
	 * @param cache
	 *            a location in file system
	 */
	public abstract void init(String server, String cache);

}