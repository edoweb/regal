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
package de.nrw.hbz.edoweb2.datatypes;

/**
 * Class HBZLink
 * 
 * <p>
 * <em>Title: </em>
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Jan Schnasse, schnasse@hbz-nrw.de TODO Replace by a standard
 *         implementation
 */
public class Link
{
	boolean isLiteral = false;
	private String predicate = null;
	private String object = null;

	public Link()
	{

	}

	public Link(String predicate, String object, boolean isLiteral)
	{
		this.predicate = predicate;
		this.object = object;
		this.isLiteral = isLiteral;
	}

	public boolean isLiteral()
	{
		return isLiteral;
	}

	public void setLiteral(boolean isLiteral)
	{
		this.isLiteral = isLiteral;
	}

	public String getPredicate()
	{
		return predicate;
	}

	public void setPredicate(String predicate)
	{
		this.predicate = predicate;
	}

	public String getObject()
	{
		return object;
	}

	public void setObject(String object, boolean isLiteral)
	{
		this.object = object;
		this.isLiteral = isLiteral;

	}

	public void setObject(String object)
	{
		this.object = object;

	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Link other = (Link) obj;
		boolean sameA = (this.predicate == other.predicate)
				|| (this.predicate != null && this.predicate
						.equalsIgnoreCase(other.predicate));
		if (!sameA)
			return false;
		boolean sameB = (this.object == other.object)
				|| (this.object != null && this.object
						.equalsIgnoreCase(other.object));
		if (!sameB)
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 89
				* hash
				+ (this.predicate == null ? 0 : this.predicate.toUpperCase()
						.hashCode());
		hash = 89
				* hash
				+ (this.object == null ? 0 : this.object.toUpperCase()
						.hashCode());
		return hash;
	}

}
