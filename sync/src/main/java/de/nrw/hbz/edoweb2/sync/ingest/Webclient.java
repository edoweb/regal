package de.nrw.hbz.edoweb2.sync.ingest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;

import javax.ws.rs.core.MediaType;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;

import de.nrw.hbz.edoweb2.api.CreateObjectBean;
import de.nrw.hbz.edoweb2.api.DCBeanAnnotated;
import de.nrw.hbz.edoweb2.api.ObjectType;
import de.nrw.hbz.edoweb2.sync.extern.DigitalEntity;

public class Webclient
{
	final static Logger logger = LoggerFactory.getLogger(Webclient.class);

	String namespace = null;
	String endpoint = null;
	String host = null;
	Client webclient = null;

	public Webclient(String namespace, String user, String password, String host)
	{
		this.host = host;
		this.namespace = namespace;
		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(MultiPartWriter.class);
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		cc.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
		cc.getProperties().put(
				DefaultApacheHttpClientConfig.PROPERTY_CHUNKED_ENCODING_SIZE,
				1024);
		webclient = Client.create(cc);
		webclient.addFilter(new HTTPBasicAuthFilter(user, password));
		endpoint = host + ":8080/edoweb2-api/resources/";
	}

	public void metadata(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		String resource = endpoint + pid;
		try
		{
			updateDC(resource + "/dc", dtlBean);
		}
		catch (Exception e)
		{
			logger.error(dtlBean.getPid() + " " + e.getMessage());
		}
		try
		{
			lobidify(dtlBean);
		}
		catch (Exception e)
		{
			logger.error(dtlBean.getPid() + " " + e.getMessage());
		}
		try
		{
			index(dtlBean);
		}
		catch (Exception e)
		{
			logger.error(dtlBean.getPid() + " " + e.getMessage());
		}
		try
		{
			oaiProvide(dtlBean);
		}
		catch (Exception e)
		{
			logger.error(dtlBean.getPid() + " " + e.getMessage());
		}

	}

	public void createObject(DigitalEntity dtlBean, String expectedMime,
			ObjectType type)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		String resource = endpoint + pid;
		String data = resource + "/data";

		createResource(type, dtlBean);

		if (dtlBean.getStreamMime().compareTo(expectedMime) != 0)
		{
			DigitalEntity fulltextObject = null;
			for (DigitalEntity view : dtlBean.getViewMainLinks())
			{

				if (view.getStreamMime().compareTo(expectedMime) == 0)
				{
					fulltextObject = view;
					break;
				}
			}
			if (fulltextObject == null)
			{
				for (DigitalEntity view : dtlBean.getViewLinks())
				{

					if (view.getStreamMime().compareTo(expectedMime) == 0)
					{
						fulltextObject = view;
						break;
					}
				}
			}
			if (fulltextObject != null)
			{
				updateData(data, fulltextObject);
			}
			else
			{
				logger.warn(pid + " found no valid data.");
				logger.info(pid + " expected " + expectedMime + " , found "
						+ dtlBean.getStreamMime());
			}
		}
		else
		{
			updateData(data, dtlBean);
		}
		updateLabel(resource, dtlBean);
	}

	public void createResource(ObjectType type, DigitalEntity dtlBean)
	{

		String pid = namespace + ":" + dtlBean.getPid();
		String resourceUrl = this.endpoint + pid;
		WebResource resource = webclient.resource(resourceUrl);
		CreateObjectBean input = new CreateObjectBean();
		input.setType(type.toString());
		input.setParentPid(dtlBean.getParentPid());

		try
		{
			resource.put(input);
		}
		catch (UniformInterfaceException e)
		{
			logger.info(pid + " already exists - will be updated!");
			logger.info(pid + " resourceUrl: " + resourceUrl);
			e.printStackTrace();
		}
	}

	private void updateDC(String endpoint, DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource webpageDC = webclient.resource(endpoint);

		DCBeanAnnotated dc = new DCBeanAnnotated();

		try
		{

			if (dtlBean.getMarcFile() != null)
				dc.add(marc2dc(dtlBean));
			else if (dtlBean.getDc() != null)
			{
				dc.add(new DCBeanAnnotated(dtlBean.getDc()));
			}
			else
			{
				logger.warn(pid
						+ " not able to create dublin core data. No Marc or DC metadata found.");
			}

			dc.addDescription(dtlBean.getLabel());
			webpageDC.put(dc);

		}
		catch (UniformInterfaceException e)
		{
			logger.info(pid + " " + e.getMessage());
		}
		catch (Exception e)
		{
			logger.debug(pid + " " + e.getMessage());
		}
	}

	private void updateLabel(String endpoint, DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource webpageDC = webclient.resource(endpoint + "/dc");

		DCBeanAnnotated dc = new DCBeanAnnotated();

		try
		{
			dc.addTitle("Version of: " + pid);
			dc.addDescription(dtlBean.getLabel());
			webpageDC.put(dc);

		}
		catch (UniformInterfaceException e)
		{
			logger.info(pid + " " + e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e)
		{
			logger.debug(pid + " " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void updateData(String endpoint, DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource data = webclient.resource(endpoint);

		try
		{
			logger.info(pid + " Update data: " + dtlBean.getStreamMime());
			MultiPart multiPart = new MultiPart();
			multiPart.bodyPart(new StreamDataBodyPart("InputStream",
					new FileInputStream(dtlBean.getStream()), dtlBean
							.getStream().getName()));
			multiPart.bodyPart(new BodyPart(dtlBean.getStreamMime(),
					MediaType.TEXT_PLAIN_TYPE));
			data.type("multipart/mixed").post(multiPart);

		}
		catch (UniformInterfaceException e)
		{
			logger.error(pid + " " + e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			logger.error(pid + " " + "FileNotFound "
					+ dtlBean.getStream().getAbsolutePath());
		}
		catch (Exception e)
		{
			logger.error(pid + " " + e.getMessage());
		}

	}

	private void lobidify(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource lobid = webclient.resource(host
				+ ":8080/edoweb2-api/utils/lobidify/" + namespace + ":"
				+ dtlBean.getPid());
		try

		{
			lobid.type("text/plain").post();
		}
		catch (UniformInterfaceException e)
		{
			logger.warn(pid + " fetching lobid-data failed");
		}
	}

	private void index(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{

			WebResource index = webclient.resource(host
					+ ":8080/edoweb2-api/utils/index/" + pid);
			index.post();
			logger.info(pid + ": got indexed!");
		}
		catch (UniformInterfaceException e)
		{
			logger.warn(pid + " " + "Not indexed! "
					+ e.getResponse().getEntity(String.class));
		}
		catch (Exception e)
		{
			logger.warn(pid + " " + "Not indexed! " + e.getMessage());
		}
	}

	private void oaiProvide(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		WebResource oaiSet = webclient.resource(host
				+ ":8080/edoweb2-api/utils/makeOaiSet/" + namespace + ":"
				+ dtlBean.getPid());
		try
		{
			oaiSet.post();
		}
		catch (UniformInterfaceException e)
		{
			logger.warn(pid + " " + "Not oai provided! " + e.getMessage());
		}
	}

	private DCBeanAnnotated marc2dc(DigitalEntity dtlBean)
	{
		String pid = namespace + ":" + dtlBean.getPid();
		try
		{
			StringWriter str = new StringWriter();
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory
					.newTransformer(new StreamSource(ClassLoader
							.getSystemResourceAsStream("MARC21slim2OAIDC.xsl")));
			transformer.transform(new StreamSource(dtlBean.getMarcFile()),
					new StreamResult(str));
			String xmlStr = str.getBuffer().toString();
			DCBeanAnnotated dc = new DCBeanAnnotated(xmlStr);
			return dc;

		}
		catch (Throwable t)
		{
			logger.warn(pid + " " + t.getCause().getMessage());
		}
		return null;
	}

	public void delete(String p)
	{
		String pid = namespace + ":" + p;

		WebResource delete = webclient.resource(host
				+ ":8080/edoweb2-api/utils/delete/" + pid);
		try
		{
			delete.delete();
		}
		catch (UniformInterfaceException e)
		{
			logger.info(pid + " Can't delete!");
		}
	}
}
