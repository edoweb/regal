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
public class DigitalEntity
{

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
	public DigitalEntity(String location)
	{
		this.location = location;

		related = new Vector<RelatedDigitalEntity>();
		streams = new HashMap<StreamType, Stream>();
	}

	private String fileToString(File file, String streamId) throws Exception
	{
		// String str = "";
		if (pid == null)
			throw new Exception("Can't set Attribute please set PID first.");
		if (file == null || !file.exists())
		{
			System.out.println("NO MARC METADATA");
			return "";
		}
		byte[] buffer = new byte[(int) file.length()];
		BufferedInputStream f = null;
		try
		{
			f = new BufferedInputStream(new FileInputStream(file));
			f.read(buffer);
		}
		finally
		{
			if (f != null)
				try
				{
					f.close();
				}
				catch (IOException ignored)
				{
				}
		}

		return new String(buffer);

	}

	private File stringToFile(String str, String streamId) throws Exception
	{
		if (pid == null)
			throw new Exception("Can't set Attribute please set PID first.");
		// System.out.println(location + File.separator + pid + "_" + streamId
		// + ".xml");
		File file = new File(location + File.separator + "." + pid + "_"
				+ streamId + ".xml");

		file.createNewFile();
		FileOutputStream writer = null;
		try
		{
			writer = new FileOutputStream(file);
			writer.write(str.replace("\n", " ").replace("  ", " ")
					.getBytes("utf-8"));
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		finally
		{
			if (writer != null)
				try
				{
					writer.flush();
					writer.close();
				}
				catch (IOException ignored)
				{
				}
		}
		str = null;
		return file;
	}

	public File getMarcFile()
	{
		return streams.get(StreamType.MARC).getStream();
	}

	public String getMarc() throws Exception
	{
		return fileToString(streams.get(StreamType.MARC).getStream(),
				StreamType.MARC.toString());
	}

	public void setMarc(String marc)
	{
		try
		{
			streams.put(StreamType.MARC,
					new Stream(stringToFile(marc, StreamType.MARC.toString()),
							"application/xml", StreamType.MARC));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getDc() throws Exception
	{
		return fileToString(streams.get(StreamType.DC).getStream(),
				StreamType.DC.toString());
	}

	public void setDc(String dc)
	{
		try
		{
			streams.put(StreamType.DC,
					new Stream(stringToFile(dc, StreamType.DC.toString()),
							"application/xml", StreamType.DC));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getMetsHdr() throws Exception
	{
		return fileToString(streams.get(StreamType.METS_HDR).getStream(),
				StreamType.METS_HDR.toString());
	}

	public void setMetsHdr(String metsHdr)
	{
		try
		{
			streams.put(
					StreamType.METS_HDR,
					new Stream(stringToFile(metsHdr,
							StreamType.METS_HDR.toString()), "application/xml",
							StreamType.METS_HDR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getStructMap() throws Exception
	{
		return fileToString(streams.get(StreamType.STRUCT_MAP).getStream(),
				StreamType.STRUCT_MAP.toString());
	}

	public void setStructMap(String structMap)
	{
		try
		{
			streams.put(
					StreamType.STRUCT_MAP,
					new Stream(stringToFile(structMap,
							StreamType.STRUCT_MAP.toString()),
							"application/xml", StreamType.STRUCT_MAP));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getFileSec() throws Exception
	{
		return fileToString(streams.get(StreamType.FILE_SEC).getStream(),
				StreamType.FILE_SEC.toString());
	}

	public void setFileSec(String fileSec)
	{
		try
		{
			streams.put(
					StreamType.FILE_SEC,
					new Stream(stringToFile(fileSec,
							StreamType.FILE_SEC.toString()), "application/xml",
							StreamType.FILE_SEC));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getJhove() throws Exception
	{
		return fileToString(streams.get(StreamType.JHOVE).getStream(),
				StreamType.JHOVE.toString());
	}

	public void setJhove(String jhove)
	{
		try
		{
			streams.put(StreamType.JHOVE,
					new Stream(
							stringToFile(jhove, StreamType.JHOVE.toString()),
							"application/xml", StreamType.JHOVE));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getRights() throws Exception
	{
		return fileToString(streams.get(StreamType.RIGHTS).getStream(),
				StreamType.RIGHTS.toString());
	}

	public void setRights(String rights)
	{
		try
		{
			streams.put(
					StreamType.RIGHTS,
					new Stream(stringToFile(rights,
							StreamType.RIGHTS.toString()), "application/xml",
							StreamType.RIGHTS));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getHistory() throws Exception
	{
		return fileToString(streams.get(StreamType.HIST).getStream(),
				StreamType.HIST.toString());
	}

	public void setHistory(String history)
	{
		try
		{
			streams.put(StreamType.HIST,
					new Stream(
							stringToFile(history, StreamType.HIST.toString()),
							"application/xml", StreamType.HIST));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getControl() throws Exception
	{
		return fileToString(streams.get(StreamType.CONTROL).getStream(),
				StreamType.CONTROL.toString());

	}

	public void setControl(String control)
	{
		try
		{
			streams.put(
					StreamType.CONTROL,
					new Stream(stringToFile(control,
							StreamType.CONTROL.toString()), "application/xml",
							StreamType.CONTROL));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getPid()
	{
		return pid;
	}

	public void setPid(String pid)
	{
		this.pid = pid;
	}

	public String getPreservation() throws Exception
	{
		return fileToString(streams.get(StreamType.PREMIS).getStream(),
				StreamType.PREMIS.toString());

	}

	public void setPreservation(String preservation)
	{
		try
		{
			streams.put(
					StreamType.PREMIS,
					new Stream(stringToFile(preservation,
							StreamType.PREMIS.toString()), "application/xml",
							StreamType.PREMIS));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getText() throws Exception
	{
		return fileToString(streams.get(StreamType.TEXT).getStream(),
				StreamType.TEXT.toString());
	}

	public void setText(String text)
	{
		try
		{
			streams.put(StreamType.TEXT,
					new Stream(stringToFile(text, StreamType.TEXT.toString()),
							"application/xml", StreamType.TEXT));
		}
		catch (Exception e)
		{
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

	public Stream getStream(StreamType type)
	{
		return streams.get(type);
	}

	public void setXml(File xml)
	{
		this.xml = xml;
	}

	public File getXml()
	{
		return xml;
	}

	public Vector<DigitalEntity> getArchiveLinks()
	{
		Vector<DigitalEntity> links = new Vector<DigitalEntity>();
		for (RelatedDigitalEntity rel : related)
		{
			if (rel.relation == DigitalEntityRelation.ARCHIVE.toString())
				links.add(rel.entity);
		}
		return links;
	}

	public Vector<DigitalEntity> getThumbnailLinks()
	{
		Vector<DigitalEntity> links = new Vector<DigitalEntity>();
		for (RelatedDigitalEntity rel : related)
		{
			if (rel.relation == DigitalEntityRelation.THUMBNAIL.toString())
				links.add(rel.entity);
		}
		return links;
	}

	public Vector<DigitalEntity> getViewLinks()
	{
		Vector<DigitalEntity> links = new Vector<DigitalEntity>();
		for (RelatedDigitalEntity rel : related)
		{
			if (rel.relation == DigitalEntityRelation.VIEW.toString())
				links.add(rel.entity);
		}
		return links;
	}

	public Vector<DigitalEntity> getIndexLinks()
	{
		Vector<DigitalEntity> links = new Vector<DigitalEntity>();
		for (RelatedDigitalEntity rel : related)
		{
			if (rel.relation == DigitalEntityRelation.INDEX.toString())
				links.add(rel.entity);
		}
		return links;
	}

	public String getUsageType()
	{
		return usageType;
	}

	public void setUsageType(String usageType)
	{
		this.usageType = usageType;
	}

	public File getMe()
	{
		return new File(location + File.separator + pid + ".xml");
	}

	public void addViewMainLink(DigitalEntity b)
	{
		related.add(new RelatedDigitalEntity(b, DigitalEntityRelation.VIEW_MAIN
				.toString()));
	}

	public void addViewLink(DigitalEntity b)
	{
		related.add(new RelatedDigitalEntity(b, DigitalEntityRelation.VIEW
				.toString()));

	}

	public void addIndexLink(DigitalEntity b)
	{
		related.add(new RelatedDigitalEntity(b, DigitalEntityRelation.INDEX
				.toString()));

	}

	public void addArchiveLink(DigitalEntity b)
	{
		related.add(new RelatedDigitalEntity(b, DigitalEntityRelation.ARCHIVE
				.toString()));

	}

	public void addThumbnailLink(DigitalEntity b)
	{
		related.add(new RelatedDigitalEntity(b, DigitalEntityRelation.THUMBNAIL
				.toString()));
	}

	public Vector<DigitalEntity> getViewMainLinks()
	{
		Vector<DigitalEntity> links = new Vector<DigitalEntity>();
		for (RelatedDigitalEntity rel : related)
		{
			if (rel.relation == DigitalEntityRelation.VIEW_MAIN.toString())
				links.add(rel.entity);
		}
		return links;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getLabel()
	{
		return label;
	}

	public boolean isParent()
	{
		return isParent;
	}

	public void setIsParent(boolean isParent)
	{
		this.isParent = isParent;
	}

	public void setParentPid(String relPid)
	{
		parentPid = relPid;
	}

	public String getParentPid()
	{
		return parentPid;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Vector<RelatedDigitalEntity> getRelated()
	{
		return related;
	}

	public void setRelated(Vector<RelatedDigitalEntity> related)
	{
		this.related = related;
	}

	public void addRelated(DigitalEntity entity, String relation)
	{
		addRelated(new RelatedDigitalEntity(entity, relation));
	}

	public void addRelated(RelatedDigitalEntity relation)
	{
		related.add(relation);
	}

	public void addStream(File file, String mime, StreamType type)
	{
		streams.put(type, new Stream(file, mime, type));

	}

	public File getStream()
	{
		return streams.get(StreamType.DATA).getStream();
	}

	public String getLocation()
	{
		return location;
	}
}
