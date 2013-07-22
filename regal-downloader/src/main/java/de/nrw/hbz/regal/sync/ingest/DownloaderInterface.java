/*
 * Copyright 2012 hbz NRW (http://www.hbz-nrw.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.nrw.hbz.regal.sync.ingest;

import java.io.IOException;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
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