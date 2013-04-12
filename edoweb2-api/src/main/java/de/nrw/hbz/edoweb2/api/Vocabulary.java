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
package de.nrw.hbz.edoweb2.api;

import static de.nrw.hbz.edoweb2.datatypes.Vocabulary.HBZ_MODEL_NAMESPACE;

public class Vocabulary
{

	public static final String IS_VOLUME = HBZ_MODEL_NAMESPACE + "isVolumeOf";
	public static final String HAS_VOLUME = HBZ_MODEL_NAMESPACE + "hasVolume";
	public static final String HAS_VOLUME_NAME = HBZ_MODEL_NAMESPACE
			+ "hasVolumeName";
	public static final String IS_VERSION = HBZ_MODEL_NAMESPACE + "isVersionOf";
	public static final String HAS_VERSION = HBZ_MODEL_NAMESPACE + "hasVersion";
	public static final String HAS_VERSION_NAME = HBZ_MODEL_NAMESPACE
			+ "hasVersionName";
	public static final String IS_CURRENT_VERSION = HBZ_MODEL_NAMESPACE
			+ "isCurrentVersion";
}
