/*
 *    Copyright 2014 Mahiar Mody
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.mybatis.generator.plugins.dbFunctions;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.api.dom.xml.Element;


/**
This class encapsulates the generated SQL statement and its offset within
&lt;update ...&gt; and &lt;insert ...&gt; tags of the SQL Mapper XML file.

<p />
The main tags in the SQL Mapper Files, such as insert and update, consist
of multiple TextElements. Every line of text inside this main tag, that
is itself not an Xml tag, constitutes a single TextElement. This includes
the opening and closing comment markers, each line of the MBG generated
comment itself, and the individual lines that make up the entire SQL statement.

<p />
This class provides a <code>static</code> convience method named
<code>getTextElementsContainingSqlStatement</code> that only extracts
those TextElements that constitute the actual SQL statement, along with the
offset of the first TextElement amongst them,
within the parent &lt;insert...&gt; or &lt;update...&gt; tag, from which
the TextElements were extracted.

<p />
The TextElements that constitute the SQL statement are placed in a
<code>List</code>. The offset of the first TextElement that constitutes
the actual SQL statement is stored in an <code>int</code>.


@author Mahiar Mody
*/
public class SqlXmlFragments
{
	private int insertIndexOffset;
	private List<TextElement> lstTeSqlElms;

	private SqlXmlFragments()
	{
	}


	public int getInsertIndexOffset()
	{
		return insertIndexOffset;
	}

	public List<TextElement> getSqlTextElementsList()
	{
		return lstTeSqlElms;
	}

	public void setInsertIndexOffset(int insertIndexOffset)
	{
		this.insertIndexOffset = insertIndexOffset;
	}

	public void setSqlTextElementsList(List<TextElement> lstTeSqlElms)
	{
		this.lstTeSqlElms = lstTeSqlElms;
	}

	/**
	Returns a <code>SqlXmlFragments</code> object from the specified SQL Mapper file's insert
	or update tag viz element. The returned <code>SqlXmlFragments</code> object contains only those
	TextElements that constitute the actual SQL statement along with the offset of the first
	TextElement amongst them.

	@param  element the SQL Mapper file's &lt;insert&gt; or &lt;update&gt; tag from which to extract the actual SQL statements and offset
	@param  startSqlTextToFind the first few characters of the TextElement that identify the start of the actual SQL statement
	@return the <code>SqlXmlFragments</code> object containing the extracted SQL statements and offset
	*/
	public static SqlXmlFragments getTextElementsContainingSqlStatement(XmlElement element, String startSqlTextToFind)
	{
		SqlXmlFragments sxf = new SqlXmlFragments();

		List<TextElement> lstTeSqlElms = new ArrayList<TextElement>();

		int i = 0;
		Iterator<Element> itrElm = element.getElements().iterator();

		/* The following loop only attempts to find the first TextElement containing the actual SQL Statement,
		which can be identified by comparing the TextElements starting text with "startSqlTextToFind" */

		while(itrElm.hasNext())
		{
			Element elm = itrElm.next();

			if(elm instanceof TextElement && elm.getFormattedContent(0).toLowerCase().startsWith(startSqlTextToFind))
			{
				sxf.setInsertIndexOffset(i);

				lstTeSqlElms.add((TextElement) elm);
				itrElm.remove();

				break;
			}

			++i;
		}

		while(itrElm.hasNext()) //this loop adds all the subsequent TextElements consisting of the actual SQL Statements to the internal List
		{
			Element elm = itrElm.next();

			if(elm instanceof TextElement)
			{
				lstTeSqlElms.add((TextElement) elm);
				itrElm.remove();
			}
			else
				break;
		}

		sxf.setSqlTextElementsList(lstTeSqlElms);

		return sxf;
	}
}
