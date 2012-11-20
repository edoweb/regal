package de.nrw.hbz.edoweb2.api;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces("text/html")
public class HTMLWriter implements MessageBodyWriter<Object>
{
	String str = "";

	@Override
	public long getSize(Object arg0, Class<?> arg1, Type arg2,
			Annotation[] arg3, MediaType arg4)
	{
		/* return -1 if the content length cannot be determined */
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type type,
			Annotation[] annotations, MediaType mediaType)
	{
		return true;
	}

	@Override
	public void writeTo(Object object, Class<?> clazz, Type type,
			Annotation[] annotation, MediaType mediaType,
			MultivaluedMap<String, Object> map, OutputStream out)
			throws IOException, WebApplicationException
	{

		if (object instanceof View)
		{
			str = HtmlAdapter.getHtml((View) object);
			final PrintWriter writer = new PrintWriter(out);
			writer.println(str);
			writer.close();
		}
		else
		{

		}

	}

}
