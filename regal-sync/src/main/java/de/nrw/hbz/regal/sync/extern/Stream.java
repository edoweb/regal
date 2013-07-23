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

import java.io.File;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class Stream {
    File stream;
    String mimeType;
    StreamType type;

    /**
     * @param stream
     *            well it is a file
     * @param mimeType
     *            the mime type of file's data
     * @param type
     *            a type (how is it used in the application context)
     */
    public Stream(File stream, String mimeType, StreamType type) {
	super();
	this.stream = stream;
	this.mimeType = mimeType;
	this.type = type;
    }

    /**
     * @return the actual file
     */
    public File getStream() {
	return stream;
    }

    /**
     * @param stream
     *            file
     */
    public void setStream(File stream) {
	this.stream = stream;
    }

    /**
     * @return the mime type
     */
    public String getMimeType() {
	return mimeType;
    }

    /**
     * @param mimeType
     *            the mime type
     */
    public void setMimeType(String mimeType) {
	this.mimeType = mimeType;
    }

    /**
     * @return the application specific type
     */
    public StreamType getType() {
	return type;
    }

    /**
     * @param type
     *            the application specific type
     */
    public void setType(StreamType type) {
	this.type = type;
    }

}
