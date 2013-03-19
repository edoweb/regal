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

package de.nrw.hbz.edoweb2.sync.mapper;

import java.util.Iterator;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.nrw.hbz.edoweb2.sync.extern.DigitalEntity;
import de.nrw.hbz.edoweb2.sync.util.XMLUtils;

/**
 * Class JhoveBean
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de creation date: 12.07.2011
 * 
 */
public class JhoveBean
{
	DigitalEntity dtlBean = null;
	DigitoolJhove2RdfMap map = new DigitoolJhove2RdfMap();

	Vector<String> format = new Vector<String>();
	Vector<String> size = new Vector<String>();
	Vector<String> version = new Vector<String>();
	Vector<String> status = new Vector<String>();
	Vector<String> sigmatch = new Vector<String>();
	Vector<String> mimeType = new Vector<String>();
	Vector<String> title = new Vector<String>();
	Vector<String> author = new Vector<String>();
	Vector<String> subject = new Vector<String>();
	Vector<String> keywords = new Vector<String>();
	Vector<String> creator = new Vector<String>();
	Vector<String> producer = new Vector<String>();
	Vector<String> creationDate = new Vector<String>();
	Vector<String> modeDate = new Vector<String>();
	Vector<String> numberOfPages = new Vector<String>();
	Vector<String> numberOfImages = new Vector<String>();

	Vector<String> imageMimeType = new Vector<String>();
	Vector<String> imageFileSize = new Vector<String>();
	Vector<String> imageByteOrder = new Vector<String>();
	Vector<String> imageWidth = new Vector<String>();
	Vector<String> imageLength = new Vector<String>();

	public JhoveBean(DigitalEntity dtlBean) throws Exception
	{
		this.dtlBean = dtlBean;

		Element root = XMLUtils.getDocument(dtlBean.getJhove());

		XPathFactory factory = XPathFactory.newInstance();

		XPath xpath = factory.newXPath();
		XPathExpression expr;

		NamespaceContext ctx = new NamespaceContext()
		{
			@Override
			public String getNamespaceURI(String prefix)
			{
				if (prefix.equals("mix"))
					return "http://www.loc.gov/mix/";
				else
					return XMLConstants.NULL_NS_URI;
			}

			@Override
			public String getPrefix(String namespace)
			{
				if (namespace.equals("http://www.loc.gov/mix/"))
					return "mix";
				else
					return null;
			}

			@Override
			@SuppressWarnings("rawtypes")
			public Iterator getPrefixes(String namespace)
			{
				return null;
			}
		};

		xpath.setNamespaceContext(ctx);

		Object result;
		NodeList nodes;
		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlInfoAuthor);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addAuthor(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e20)
		{

			e20.printStackTrace();
		}
		catch (DOMException e20)
		{

			e20.printStackTrace();
		}
		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlInfoCreationDate);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addCreationDate(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e19)
		{

			e19.printStackTrace();
		}
		catch (DOMException e19)
		{

			e19.printStackTrace();
		}

		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlInfoCreator);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addCreator(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e18)
		{

			e18.printStackTrace();
		}
		catch (DOMException e18)
		{

			e18.printStackTrace();
		}

		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlInfoKeywords);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addKeywords(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e17)
		{

			e17.printStackTrace();
		}
		catch (DOMException e17)
		{

			e17.printStackTrace();
		}

		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlInfoModDate);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addModeDate(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e16)
		{

			e16.printStackTrace();
		}
		catch (DOMException e16)
		{

			e16.printStackTrace();
		}

		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlInfoProducer);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addProducer(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e15)
		{

			e15.printStackTrace();
		}
		catch (DOMException e15)
		{

			e15.printStackTrace();
		}
		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlInfoSubject);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addSubject(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e14)
		{

			e14.printStackTrace();
		}
		catch (DOMException e14)
		{

			e14.printStackTrace();
		}
		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlInfoTitle);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addTitle(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e13)
		{

			e13.printStackTrace();
		}
		catch (DOMException e13)
		{

			e13.printStackTrace();
		}

		String number;
		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlNumberOfImages);
			number = expr.evaluate(root);
			addNumberOfImages(number);
		}
		catch (XPathExpressionException e12)
		{

			e12.printStackTrace();
		}

		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlNumberOfPages);
			number = expr.evaluate(root);
			addNumberOfPages(number);
		}
		catch (XPathExpressionException e11)
		{

			e11.printStackTrace();
		}

		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlRepInfoFormat);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addFormat(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e10)
		{

			e10.printStackTrace();
		}
		catch (DOMException e10)
		{

			e10.printStackTrace();
		}
		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlRepInfoMimeType);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addMimeType(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e9)
		{

			e9.printStackTrace();
		}
		catch (DOMException e9)
		{

			e9.printStackTrace();
		}
		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlRepInfoSigMatch);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addSigmatch(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e8)
		{

			e8.printStackTrace();
		}
		catch (DOMException e8)
		{

			e8.printStackTrace();
		}
		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlRepInfoSize);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addSize(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e7)
		{

			e7.printStackTrace();
		}
		catch (DOMException e7)
		{

			e7.printStackTrace();
		}
		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlRepInfoStatus);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addStatus(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e6)
		{

			e6.printStackTrace();
		}
		catch (DOMException e6)
		{

			e6.printStackTrace();
		}
		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlRepInfoVersion);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addVersion(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e5)
		{

			e5.printStackTrace();
		}
		catch (DOMException e5)
		{

			e5.printStackTrace();
		}

		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlFileSize);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addImageFileSize(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e4)
		{

			e4.printStackTrace();
		}
		catch (DOMException e4)
		{

			e4.printStackTrace();
		}

		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlImageMimeType);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addImageMimeType(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e3)
		{

			e3.printStackTrace();
		}
		catch (DOMException e3)
		{

			e3.printStackTrace();
		}

		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlByteOrder);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addImageByteOrder(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e2)
		{

			e2.printStackTrace();
		}
		catch (DOMException e2)
		{

			e2.printStackTrace();
		}

		try
		{

			expr = xpath.compile(DigitoolJhove2RdfMap.xmlImageWidth);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addImageWidth(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e1)
		{

			e1.printStackTrace();
		}
		catch (DOMException e1)
		{

			e1.printStackTrace();
		}

		try
		{
			expr = xpath.compile(DigitoolJhove2RdfMap.xmlImageLength);
			result = expr.evaluate(root, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++)
			{

				addImageLength(nodes.item(i).getTextContent());
			}
		}
		catch (XPathExpressionException e)
		{

			e.printStackTrace();
		}
		catch (DOMException e)
		{

			e.printStackTrace();
		}

	}

	public Vector<String> getFormat()
	{
		return format;
	}

	public void setFormat(Vector<String> format)
	{
		this.format = format;
	}

	public boolean addFormat(String e)
	{
		return format.add(e);
	}

	public String getFirstFormat()
	{
		Vector<String> elements = getFormat();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getSize()
	{
		return size;
	}

	public void setSize(Vector<String> size)
	{
		this.size = size;
	}

	public boolean addSize(String e)
	{
		return size.add(e);
	}

	public String getFirstSize()
	{
		Vector<String> elements = getSize();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getVersion()
	{
		return version;
	}

	public void setVersion(Vector<String> version)
	{
		this.version = version;
	}

	public boolean addVersion(String e)
	{
		return version.add(e);
	}

	public String getFirstVersion()
	{
		Vector<String> elements = getFormat();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getStatus()
	{
		return status;
	}

	public void setStatus(Vector<String> status)
	{
		this.status = status;
	}

	public boolean addStatus(String e)
	{
		return status.add(e);
	}

	public String getFirstStatus()
	{
		Vector<String> elements = getStatus();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getSigmatch()
	{
		return sigmatch;
	}

	public void setSigmatch(Vector<String> sigmatch)
	{
		this.sigmatch = sigmatch;
	}

	public boolean addSigmatch(String e)
	{
		return sigmatch.add(e);
	}

	public String getFirstSigmatch()
	{
		Vector<String> elements = getSigmatch();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getMimeType()
	{
		return mimeType;
	}

	public void setMimeType(Vector<String> mimeType)
	{
		this.mimeType = mimeType;
	}

	public boolean addMimeType(String e)
	{
		return mimeType.add(e);
	}

	public String getFirstMimeType()
	{
		Vector<String> elements = getMimeType();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getTitle()
	{
		return title;
	}

	public void setTitle(Vector<String> title)
	{
		this.title = title;
	}

	public boolean addTitle(String e)
	{
		return title.add(e);
	}

	public String getFirstTitle()
	{
		Vector<String> elements = getTitle();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getAuthor()
	{
		return author;
	}

	public void setAuthor(Vector<String> author)
	{
		this.author = author;
	}

	public boolean addAuthor(String e)
	{
		return author.add(e);
	}

	public String getFirstAuthor()
	{
		Vector<String> elements = getAuthor();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getSubject()
	{
		return subject;
	}

	public void setSubject(Vector<String> subject)
	{
		this.subject = subject;
	}

	public boolean addSubject(String e)
	{
		return subject.add(e);
	}

	public String getFirstSubject()
	{
		Vector<String> elements = getSubject();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getKeywords()
	{
		return keywords;
	}

	public void setKeywords(Vector<String> keywords)
	{
		this.keywords = keywords;
	}

	public boolean addKeywords(String e)
	{
		return keywords.add(e);
	}

	public String getFirstKeywords()
	{
		Vector<String> elements = getKeywords();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getCreator()
	{
		return creator;
	}

	public void setCreator(Vector<String> creator)
	{
		this.creator = creator;
	}

	public boolean addCreator(String e)
	{
		return creator.add(e);
	}

	public String getFirstCreator()
	{
		Vector<String> elements = getCreator();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getProducer()
	{
		return producer;
	}

	public void setProducer(Vector<String> producer)
	{
		this.producer = producer;
	}

	public boolean addProducer(String e)
	{
		return producer.add(e);
	}

	public String getFirstProducer()
	{
		Vector<String> elements = getProducer();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(Vector<String> creationDate)
	{
		this.creationDate = creationDate;
	}

	public boolean addCreationDate(String e)
	{
		return creationDate.add(e);
	}

	public String getFirstCreationDate()
	{
		Vector<String> elements = getCreationDate();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getModeDate()
	{
		return modeDate;
	}

	public void setModeDate(Vector<String> modeDate)
	{
		this.modeDate = modeDate;
	}

	public boolean addModeDate(String e)
	{
		return modeDate.add(e);
	}

	public String getFirstModeDate()
	{
		Vector<String> elements = getModeDate();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getNumberOfPages()
	{
		return numberOfPages;
	}

	public void setNumberOfPages(Vector<String> numberOfPages)
	{
		this.numberOfPages = numberOfPages;
	}

	public boolean addNumberOfPages(String e)
	{
		return numberOfPages.add(e);
	}

	public String getFirstNumberOfPages()
	{
		Vector<String> elements = getNumberOfPages();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getNumberOfImages()
	{
		return numberOfImages;
	}

	public void setNumberOfImages(Vector<String> numberOfImages)
	{
		this.numberOfImages = numberOfImages;
	}

	public boolean addNumberOfImages(String e)
	{
		return numberOfImages.add(e);
	}

	public String getFirstNumberOfImages()
	{
		Vector<String> elements = getNumberOfImages();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getImageMimeType()
	{
		return imageMimeType;
	}

	public void setImageMimeType(Vector<String> imageMimeType)
	{
		this.imageMimeType = imageMimeType;
	}

	public boolean addImageMimeType(String e)
	{
		return imageMimeType.add(e);
	}

	public String getFirstImageMimeType()
	{
		Vector<String> elements = getImageMimeType();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getImageFileSize()
	{
		return imageFileSize;
	}

	public void setImageFileSize(Vector<String> imageFileSize)
	{
		this.imageFileSize = imageFileSize;
	}

	public boolean addImageFileSize(String e)
	{
		return imageFileSize.add(e);
	}

	public String getFirstImageFileSize()
	{
		Vector<String> elements = getImageFileSize();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getImageByteOrder()
	{
		return imageByteOrder;
	}

	public void setImageByteOrder(Vector<String> imageByteOrder)
	{
		this.imageByteOrder = imageByteOrder;
	}

	public boolean addImageByteOrder(String e)
	{
		return imageByteOrder.add(e);
	}

	public String getFirstImageByteOrder()
	{
		Vector<String> elements = getImageByteOrder();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getImageWidth()
	{
		return imageWidth;
	}

	public void setImageWidth(Vector<String> imageWidth)
	{
		this.imageWidth = imageWidth;
	}

	public boolean addImageWidth(String e)
	{
		return imageWidth.add(e);
	}

	public String getFirstImageWidth()
	{
		Vector<String> elements = getImageWidth();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

	public Vector<String> getImageLength()
	{
		return imageLength;
	}

	public void setImageLength(Vector<String> imageLength)
	{
		this.imageLength = imageLength;
	}

	public boolean addImageLength(String e)
	{
		return imageLength.add(e);
	}

	public String getFirstImageLength()
	{
		Vector<String> elements = getImageLength();
		if (elements == null || elements.size() == 0)
		{
			return "";
		}

		return elements.elementAt(0);
	}

}
