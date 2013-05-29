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
import java.util.Vector;

/**
 * @author Jan Schnasse, schnasse@hbz-nrw.de
 * 
 */
public class DigitalEntity
{
	public static String INDEX = "INDEX";
	public static String ARCHIVE = "ARCHIVE";
	public static String THUMBNAIL = "THUMBNAIL";
	public static String VIEW = "VIEW";
	public static String VIEW_MAIN = "VIEW_MAIN";
	public static String MANIFESTATION = "manifestation";
	public static String INCLUDE = "include";
	public static String PART_OF = "part_of";

	private boolean isParent = true;
	private String pid = null;
	private String usageType = null;
	private String location = null;

	private String DC = "DC";
	private String PREMIS = "PREMIS";
	private String JHOVE = "JHOVE";
	private String RIGHTS = "RIGHTS";
	private String HIST = "HIST";
	private String TEXT = "TEXT";
	private String CONTROL = "CONTROL";

	private String MARC = "MARC";
	private String METS_HDR = "METS_HDR";
	private String STRUCT_MAP = "STRUCT_MAP";
	private String FILE_SEC = "FILE_SEC";

	private File dc = null;
	private File preservation = null;
	private File jhove = null;
	private File rights = null;
	private File history = null;
	private File text = null;
	private File control = null;

	private File marc = null;
	private File metsHdr = null;
	private File structMap = null;
	private File fileSec = null;

	private String streamMime = null;
	private Vector<File> streams = null;
	private File xml = null;

	private Vector<DigitalEntity> archiveLinks = null;
	private Vector<DigitalEntity> thumbnailLinks = null;
	private Vector<DigitalEntity> indexLinks = null;
	private Vector<DigitalEntity> viewLinks = null;
	private Vector<DigitalEntity> viewMainLinks = null;

	private Vector<EntityRelation> related = null;

	private String parentPid;

	private String label = null;
	private String type;

	/**
	 * @param location
	 *            the directory of the entity
	 */
	public DigitalEntity(String location)
	{
		this.location = location;
		archiveLinks = new Vector<DigitalEntity>();
		thumbnailLinks = new Vector<DigitalEntity>();
		indexLinks = new Vector<DigitalEntity>();
		viewLinks = new Vector<DigitalEntity>();
		viewMainLinks = new Vector<DigitalEntity>();
		related = new Vector<EntityRelation>();
		streams = new Vector<File>();
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

	public String getMarc() throws Exception
	{
		return fileToString(marc, MARC);
	}

	public void setMarc(String marc)
	{
		try
		{
			this.marc = stringToFile(marc, MARC);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getDc() throws Exception
	{
		return fileToString(dc, DC);
	}

	public void setDc(String dc)
	{
		try
		{
			this.dc = stringToFile(dc, DC);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	public String getMetsHdr() throws Exception
	{
		return fileToString(metsHdr, METS_HDR);
	}

	public void setMetsHdr(String metsHdr)
	{
		try
		{
			this.metsHdr = stringToFile(metsHdr, METS_HDR);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	public String getStructMap() throws Exception
	{
		return fileToString(structMap, STRUCT_MAP);
	}

	public void setStructMap(String structMap)
	{
		try
		{
			this.structMap = stringToFile(structMap, STRUCT_MAP);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	public String getFileSec() throws Exception
	{
		return fileToString(fileSec, FILE_SEC);
	}

	public void setFileSec(String fileSec)
	{
		try
		{
			this.fileSec = stringToFile(fileSec, FILE_SEC);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	public String getJhove() throws Exception
	{
		return fileToString(jhove, JHOVE);
	}

	public void setJhove(String jhove)
	{
		try
		{
			this.jhove = stringToFile(jhove, JHOVE);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	public String getRights() throws Exception
	{
		return fileToString(rights, RIGHTS);
	}

	public void setRights(String rights)
	{
		try
		{
			this.rights = stringToFile(rights, RIGHTS);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	public String getHistory() throws Exception
	{
		return fileToString(history, HIST);
	}

	public void setHistory(String history)
	{
		try
		{
			this.history = stringToFile(history, HIST);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	public String getControl() throws Exception
	{
		return fileToString(control, CONTROL);
	}

	public void setControl(String control)
	{
		try
		{
			this.control = stringToFile(control, CONTROL);
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
		return fileToString(preservation, PREMIS);
	}

	public void setPreservation(String preservation)
	{
		try
		{
			this.preservation = stringToFile(preservation, PREMIS);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	public String getText() throws Exception
	{
		return fileToString(text, TEXT);
	}

	public void setText(String text)
	{
		try
		{
			this.text = stringToFile(text, TEXT);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	@Override
	public String toString()
	{
		return "pid: " + pid + "\n" + "control: " + control + "\n" + "dc: "
				+ dc + "\n" + "preservation: " + preservation + "\n"
				+ "jhove: " + jhove + "\n" + "rights: " + rights + "\n"
				+ "history: " + history + "\n" + "text: " + text + "\n";
	}

	public File getFirstStream()
	{
		return streams.firstElement();
	}

	public Vector<File> getStreams()
	{
		return streams;
	}

	public void setXml(File xml)
	{
		this.xml = xml;
	}

	public File getXml()
	{
		return xml;
	}

	public void setStream(File stream)
	{
		this.streams.set(0, stream);
	}

	public Vector<DigitalEntity> getArchiveLinks()
	{
		return archiveLinks;
	}

	public void setArchiveLink(DigitalEntity archiveLink)
	{
		archiveLinks = new Vector<DigitalEntity>();
		archiveLinks.add(archiveLink);
	}

	public Vector<DigitalEntity> getThumbnailLinks()
	{
		return thumbnailLinks;
	}

	public void setThumbnailLink(DigitalEntity thumbnailLink)
	{
		thumbnailLinks = new Vector<DigitalEntity>();
		thumbnailLinks.add(thumbnailLink);
	}

	public void setViewLink(DigitalEntity viewLink)
	{
		viewLinks = new Vector<DigitalEntity>();
		viewLinks.add(viewLink);
	}

	public void setViewMainLink(DigitalEntity viewLink)
	{
		viewMainLinks = new Vector<DigitalEntity>();
		viewMainLinks.add(viewLink);
	}

	public Vector<DigitalEntity> getViewLinks()
	{
		return viewLinks;
	}

	public Vector<DigitalEntity> getIndexLinks()
	{
		return indexLinks;
	}

	public void setIndexLink(DigitalEntity indexLink)
	{
		indexLinks = new Vector<DigitalEntity>();
		indexLinks.add(indexLink);
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
		viewMainLinks.add(b);
	}

	public void addViewLink(DigitalEntity b)
	{
		viewLinks.add(b);
	}

	public void addIndexLink(DigitalEntity b)
	{
		indexLinks.add(b);
	}

	public void addArchiveLink(DigitalEntity b)
	{
		archiveLinks.add(b);
	}

	public void addThumbnailLink(DigitalEntity b)
	{
		thumbnailLinks.add(b);
	}

	public Vector<DigitalEntity> getViewMainLinks()
	{
		return viewMainLinks;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getLabel()
	{
		return label;
	}

	public String getStreamMime()
	{
		return streamMime;
	}

	public void setStreamMime(String streamMime)
	{
		this.streamMime = streamMime;
	}

	public File getMarcFile()
	{
		return this.marc;
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

	public Vector<EntityRelation> getRelated()
	{
		return related;
	}

	public void setRelated(Vector<EntityRelation> related)
	{
		this.related = related;
	}

	public void addRelated(DigitalEntity entity, String relation)
	{
		addRelated(new EntityRelation(entity, relation));
	}

	public void addRelated(EntityRelation relation)
	{
		related.add(relation);
	}

	public void addStream(File file)
	{
		streams.add(file);

	}
}
