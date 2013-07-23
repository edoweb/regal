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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */

@SuppressWarnings("javadoc")
public class DigitalEntity {

    private boolean isParent = true;
    private String pid = null;
    private String usageType = null;
    private String location = null;

    private HashMap<StreamType, Stream> streams = null;
    private Vector<RelatedDigitalEntity> related = null;

    private String parentPid;
    private String label = null;
    private String type;

    private File xml = null;

    /**
     * @param location
     *            the directory of the entity
     */
    public DigitalEntity(String location) {
	this.location = location;

	related = new Vector<RelatedDigitalEntity>();
	streams = new HashMap<StreamType, Stream>();
    }

    private String fileToString(File file, String streamId) throws Exception {
	// String str = "";
	if (pid == null)
	    throw new Exception("Can't set Attribute please set PID first.");
	if (file == null || !file.exists()) {
	    System.out.println("NO MARC METADATA");
	    return "";
	}
	byte[] buffer = new byte[(int) file.length()];
	BufferedInputStream f = null;
	try {
	    f = new BufferedInputStream(new FileInputStream(file));
	    f.read(buffer);
	} finally {
	    if (f != null)
		try {
		    f.close();
		} catch (IOException ignored) {
		}
	}

	return new String(buffer);

    }

    private File stringToFile(String str, String streamId) throws Exception {
	if (pid == null)
	    throw new Exception("Can't set Attribute please set PID first.");
	// System.out.println(location + File.separator + pid + "_" + streamId
	// + ".xml");
	File file = new File(location + File.separator + "." + pid + "_"
		+ streamId + ".xml");

	file.createNewFile();
	FileOutputStream writer = null;
	try {
	    writer = new FileOutputStream(file);
	    writer.write(str.replace("\n", " ").replace("  ", " ")
		    .getBytes("utf-8"));
	} catch (IOException e) {

	    e.printStackTrace();
	} finally {
	    if (writer != null)
		try {
		    writer.flush();
		    writer.close();
		} catch (IOException ignored) {
		}
	}
	str = null;
	return file;
    }

    /**
     * @return a pid for the entity
     */
    public String getPid() {
	return pid;
    }

    /**
     * @param pid
     *            a pid for the entity
     */
    public void setPid(String pid) {
	this.pid = pid;
    }

    /**
     * @param type
     *            stream type
     * @return the stream
     */
    public Stream getStream(StreamType type) {
	return streams.get(type);
    }

    /**
     * @param xml
     *            a xml file
     */
    public void setXml(File xml) {
	this.xml = xml;
    }

    /**
     * @return xml file
     */
    public File getXml() {
	return xml;
    }

    /**
     * TODO: WTF?
     * 
     * @return a xml representation of myself
     */
    public File getMe() {
	return new File(location + File.separator + pid + ".xml");
    }

    /**
     * @param label
     *            a label for the entity
     */
    public void setLabel(String label) {
	this.label = label;
    }

    /**
     * @return a label for the entity
     */
    public String getLabel() {
	return label;
    }

    /**
     * @return do you have children?
     */
    public boolean isParent() {
	return isParent;
    }

    /**
     * @param isParent
     *            do you have children?
     */
    public void setIsParent(boolean isParent) {
	this.isParent = isParent;
    }

    /**
     * @param relPid
     *            set a parent
     */
    public void setParentPid(String relPid) {
	parentPid = relPid;
    }

    /**
     * @return if there is a hierarchy, return your parent
     */
    public String getParentPid() {
	return parentPid;
    }

    /**
     * @return the entities type
     */
    public String getType() {
	return type;
    }

    /**
     * @param type
     *            a user defined type for the entity
     */
    public void setType(String type) {
	this.type = type;
    }

    /**
     * @return all related entities
     */
    public Vector<RelatedDigitalEntity> getRelated() {
	return related;
    }

    /**
     * @param related
     *            alle related entities
     */
    public void setRelated(Vector<RelatedDigitalEntity> related) {
	this.related = related;
    }

    /**
     * @param entity
     *            a related entity
     * @param relation
     *            a user defined relation
     */
    public void addRelated(DigitalEntity entity, String relation) {
	addRelated(new RelatedDigitalEntity(entity, relation));
    }

    /**
     * @param relation
     *            a related entity
     */
    public void addRelated(RelatedDigitalEntity relation) {
	related.add(relation);
    }

    /**
     * @param file
     *            a file
     * @param mime
     *            a mimetype, e.g. application/pdf
     * @param type
     *            a stream type
     */
    public void addStream(File file, String mime, StreamType type) {
	streams.put(type, new Stream(file, mime, type));

    }

    /**
     * @return the data stream
     */
    public File getStream() {
	return streams.get(StreamType.DATA).getStream();
    }

    /**
     * @return the base location of the streams in filesystem
     */
    public String getLocation() {
	return location;
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @return a Marc File
     */
    public File getMarcFile() {
	return streams.get(StreamType.MARC).getStream();
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @return MarcFile
     * @throws Exception
     *             if io fails
     */
    public String getMarc() throws Exception {
	return fileToString(streams.get(StreamType.MARC).getStream(),
		StreamType.MARC.toString());
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @param marc
     *            a marc string
     */
    public void setMarc(String marc) {
	try {
	    streams.put(StreamType.MARC,
		    new Stream(stringToFile(marc, StreamType.MARC.toString()),
			    "application/xml", StreamType.MARC));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @return dc string
     * @throws Exception
     *             if io fails
     */
    public String getDc() throws Exception {
	return fileToString(streams.get(StreamType.DC).getStream(),
		StreamType.DC.toString());
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @param dc
     *            dc string
     */
    public void setDc(String dc) {
	try {
	    streams.put(StreamType.DC,
		    new Stream(stringToFile(dc, StreamType.DC.toString()),
			    "application/xml", StreamType.DC));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @return
     * @throws Exception
     */
    public String getMetsHdr() throws Exception {
	return fileToString(streams.get(StreamType.METS_HDR).getStream(),
		StreamType.METS_HDR.toString());
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @param metsHdr
     */
    public void setMetsHdr(String metsHdr) {
	try {
	    streams.put(
		    StreamType.METS_HDR,
		    new Stream(stringToFile(metsHdr,
			    StreamType.METS_HDR.toString()), "application/xml",
			    StreamType.METS_HDR));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @return
     * @throws Exception
     */
    public String getStructMap() throws Exception {
	return fileToString(streams.get(StreamType.STRUCT_MAP).getStream(),
		StreamType.STRUCT_MAP.toString());
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @param structMap
     */
    public void setStructMap(String structMap) {
	try {
	    streams.put(
		    StreamType.STRUCT_MAP,
		    new Stream(stringToFile(structMap,
			    StreamType.STRUCT_MAP.toString()),
			    "application/xml", StreamType.STRUCT_MAP));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @return
     * @throws Exception
     */
    public String getFileSec() throws Exception {
	return fileToString(streams.get(StreamType.FILE_SEC).getStream(),
		StreamType.FILE_SEC.toString());
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @param fileSec
     */
    public void setFileSec(String fileSec) {
	try {
	    streams.put(
		    StreamType.FILE_SEC,
		    new Stream(stringToFile(fileSec,
			    StreamType.FILE_SEC.toString()), "application/xml",
			    StreamType.FILE_SEC));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @return
     * @throws Exception
     */
    public String getJhove() throws Exception {
	return fileToString(streams.get(StreamType.JHOVE).getStream(),
		StreamType.JHOVE.toString());
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @param jhove
     */
    public void setJhove(String jhove) {
	try {
	    streams.put(StreamType.JHOVE,
		    new Stream(
			    stringToFile(jhove, StreamType.JHOVE.toString()),
			    "application/xml", StreamType.JHOVE));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @return
     * @throws Exception
     */
    public String getRights() throws Exception {
	return fileToString(streams.get(StreamType.RIGHTS).getStream(),
		StreamType.RIGHTS.toString());
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @param rights
     */
    public void setRights(String rights) {
	try {
	    streams.put(
		    StreamType.RIGHTS,
		    new Stream(stringToFile(rights,
			    StreamType.RIGHTS.toString()), "application/xml",
			    StreamType.RIGHTS));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @return
     * @throws Exception
     */
    public String getHistory() throws Exception {
	return fileToString(streams.get(StreamType.HIST).getStream(),
		StreamType.HIST.toString());
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @param history
     */
    public void setHistory(String history) {
	try {
	    streams.put(StreamType.HIST,
		    new Stream(
			    stringToFile(history, StreamType.HIST.toString()),
			    "application/xml", StreamType.HIST));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @return
     * @throws Exception
     */
    public String getControl() throws Exception {
	return fileToString(streams.get(StreamType.CONTROL).getStream(),
		StreamType.CONTROL.toString());

    }

    /**
     * TODO: Remove:Digitool Specific
     * 
     * @param control
     */
    public void setControl(String control) {
	try {
	    streams.put(
		    StreamType.CONTROL,
		    new Stream(stringToFile(control,
			    StreamType.CONTROL.toString()), "application/xml",
			    StreamType.CONTROL));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * @param b
     */
    public void addViewMainLink(DigitalEntity b) {
	related.add(new RelatedDigitalEntity(b, DigitalEntityRelation.VIEW_MAIN
		.toString()));
    }

    /**
     * @param b
     */
    public void addViewLink(DigitalEntity b) {
	related.add(new RelatedDigitalEntity(b, DigitalEntityRelation.VIEW
		.toString()));

    }

    /**
     * @param b
     */
    public void addIndexLink(DigitalEntity b) {
	related.add(new RelatedDigitalEntity(b, DigitalEntityRelation.INDEX
		.toString()));

    }

    /**
     * @param b
     */
    public void addArchiveLink(DigitalEntity b) {
	related.add(new RelatedDigitalEntity(b, DigitalEntityRelation.ARCHIVE
		.toString()));

    }

    /**
     * @param b
     */
    public void addThumbnailLink(DigitalEntity b) {
	related.add(new RelatedDigitalEntity(b, DigitalEntityRelation.THUMBNAIL
		.toString()));
    }

    /**
     * @return
     */
    public Vector<DigitalEntity> getViewMainLinks() {
	Vector<DigitalEntity> links = new Vector<DigitalEntity>();
	for (RelatedDigitalEntity rel : related) {
	    if (rel.relation == DigitalEntityRelation.VIEW_MAIN.toString())
		links.add(rel.entity);
	}
	return links;
    }

    /**
     * @return
     * @throws Exception
     */
    public String getPreservation() throws Exception {
	return fileToString(streams.get(StreamType.PREMIS).getStream(),
		StreamType.PREMIS.toString());

    }

    /**
     * @param preservation
     */
    public void setPreservation(String preservation) {
	try {
	    streams.put(
		    StreamType.PREMIS,
		    new Stream(stringToFile(preservation,
			    StreamType.PREMIS.toString()), "application/xml",
			    StreamType.PREMIS));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * @return
     * @throws Exception
     */
    public String getText() throws Exception {
	return fileToString(streams.get(StreamType.TEXT).getStream(),
		StreamType.TEXT.toString());
    }

    /**
     * @param text
     */
    public void setText(String text) {
	try {
	    streams.put(StreamType.TEXT,
		    new Stream(stringToFile(text, StreamType.TEXT.toString()),
			    "application/xml", StreamType.TEXT));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    // @Override
    // public String toString()
    // {
    // // return "pid: " + pid + "\n" + "control: " + control + "\n" + "dc: "
    // // + dc + "\n" + "preservation: " + preservation + "\n"
    // // + "jhove: " + jhove + "\n" + "rights: " + rights + "\n"
    // // + "history: " + history + "\n" + "text: " + text + "\n";
    // }
    /**
     * @return
     */
    public Vector<DigitalEntity> getArchiveLinks() {
	Vector<DigitalEntity> links = new Vector<DigitalEntity>();
	for (RelatedDigitalEntity rel : related) {
	    if (rel.relation == DigitalEntityRelation.ARCHIVE.toString())
		links.add(rel.entity);
	}
	return links;
    }

    /**
     * @return
     */
    public Vector<DigitalEntity> getThumbnailLinks() {
	Vector<DigitalEntity> links = new Vector<DigitalEntity>();
	for (RelatedDigitalEntity rel : related) {
	    if (rel.relation == DigitalEntityRelation.THUMBNAIL.toString())
		links.add(rel.entity);
	}
	return links;
    }

    /**
     * @return
     */
    public Vector<DigitalEntity> getViewLinks() {
	Vector<DigitalEntity> links = new Vector<DigitalEntity>();
	for (RelatedDigitalEntity rel : related) {
	    if (rel.relation == DigitalEntityRelation.VIEW.toString())
		links.add(rel.entity);
	}
	return links;
    }

    /**
     * @return
     */
    public Vector<DigitalEntity> getIndexLinks() {
	Vector<DigitalEntity> links = new Vector<DigitalEntity>();
	for (RelatedDigitalEntity rel : related) {
	    if (rel.relation == DigitalEntityRelation.INDEX.toString())
		links.add(rel.entity);
	}
	return links;
    }

    /**
     * @return
     */
    public String getUsageType() {
	return usageType;
    }

    public void setUsageType(String usageType) {
	this.usageType = usageType;
    }
}
