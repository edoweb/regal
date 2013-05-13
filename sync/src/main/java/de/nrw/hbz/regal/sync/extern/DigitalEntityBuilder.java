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
package de.nrw.hbz.regal.sync.extern;

/**
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
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
