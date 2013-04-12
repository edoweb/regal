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

/**
 * @author jan
 * 
 */
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
