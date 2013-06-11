package de.nrw.hbz.regal.sync.extern;

import java.io.File;

public class Stream
{
	File stream;
	String mimeType;
	StreamType type;

	public Stream(File stream, String mimeType, StreamType type)
	{
		super();
		this.stream = stream;
		this.mimeType = mimeType;
		this.type = type;
	}

	public File getStream()
	{
		return stream;
	}

	public void setStream(File stream)
	{
		this.stream = stream;
	}

	public String getMimeType()
	{
		return mimeType;
	}

	public void setMimeType(String mimeType)
	{
		this.mimeType = mimeType;
	}

	public StreamType getType()
	{
		return type;
	}

	public void setType(StreamType type)
	{
		this.type = type;
	}

}
