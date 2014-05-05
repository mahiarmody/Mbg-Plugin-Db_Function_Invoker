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
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.Plugin;

import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.Attribute;

import org.mybatis.generator.logging.LogFactory;
import org.mybatis.generator.logging.Log;


/**
﻿This plugin supports incorporating or adding Database Functions Invocations 
(such as the soundex, RawToHex, database encryption functions, etc) to the 
MyBatis Generator generated SQL Map files (XML mapper files). The invoked 
database functions could be built-in database functions or user defined 
database functions. The database function invocations are specified for 
individual columns and act on that individual column's data. Function 
invocations cannot be specified for an entire table.

<p/>This plugin wraps the supplied column data with the specified database 
functions prior to inserting, updating, and searching. This allows database 
dependent custom manipulation of the supplied column data prior to inserting, 
updating, and searching of records. For example, hashing the supplied password 
value using a database function, before storing it in the database column, or 
checking for the equality of passwords by comparing the hashed value of the 
supplied password with the hash value stored in the database table column.

<p/>More generally, the specified function invocations are wrapped around all 
occurrences of the specified column values in the SQL Map file, which includes 
the Example_Where_Clause tags, the insert tags and the update tags in the SQL 
Map file.

<p/>This plugin requires at least one nested &lt;property&gt; element to be 
specified, detailing the column's name, on whose value, the database function 
will be applied; along with the database function itself that is to be applied. 
<b>If no nested &lt;property&gt; elements are specified, this plugin will be 
considered invalid, and consequently, won't run.</b>

<p/>To specify the database function invocations that apply to the value of a 
particular column, specify the fully qualified (see below) column name in the 
"name" attribute of the nested &lt;property&gt; element. Specify the Database 
Function Invocations applicable to this column's value, in the corresponding 
"value" attribute of the same nested &lt;property&gt; element. To reference the 
column value within the database function invocation, use the special key-word: 
<b><code>|COL_VAL|</code></b>

<p/>
The fully qualified column name, is, the name of the column as specified in the 
<code>&lt;table ...&gt;</code> configuration element of the MyBatis Generator's 
XML configuration file. Depending upon your database, the fully qualified 
column names are specified as:<br />
&lt;<b>schema name</b>&gt;.&lt;<b>table name</b>&gt;.&lt;<b>column name</b>&gt; 
<br />or<br />
&lt;<b>Catalog name</b>&gt;.&lt;<b>table name</b>&gt;.&lt;<b>column name</b>&gt; 
<br/>or, if your database does not support (or require you to explicitly 
specify) <i>schema</i>s or <i>catalog</i>s, then simply<br/> 
&lt;<b>table name</b>&gt;.&lt;<b>column name</b>&gt;

<p/><b>Note</b>
<ul> 
<li>The key-word "COL_VAL" must be specified in Upper Case only and must be 
surrounded by a single "pipe" character on either side.</li>
<li>There should be no space between the "pipe" character and the word 
"COL_VAL"</li>
<li>The <b><code>|COL_VAL|</code></b> key-word can be used multiple times 
within the same function (or nested function) if necessary</li>
<li>Database function invocations will appear exactly as specified in the 
"value" attribute (only replacing the <code>|COL_VAL|</code> key-word
appropriately). </li>
<li>The fully qualified column names are case insensitive</li>
<li>The use of spaces inside the "name" attribute of the nested property 
element is illegal and will result in that property element being ignored. A 
warning to this effect will be displayed when running this plugin.</li>
<li>LOB columns too can be wrapped in database function invocations. Note 
however, that the MyBatis Generator generated “where clause” does not 
include any LOB fields, thus the db function invocations will only be 
applicable to insert and update of LOBs.</li>
</ul> 


<p/>&nbsp;<br/>
<u><b>Example 1 - Simple use:</b></u>
<br/>To store or retrieve the soundex values for all employee last names in the 
column named “last_name_sdx” of the table named “Employees” in the 
schema or catalog named “public”.

<code>
<pre>
&lt;plugin type="org.mybatis.generator.plugins.dbFunctions.DbFunctionInvoker"&gt;
	&lt;property name="public.Employees.last_name_sdx" value="soundex(|COL_VAL|)" /&gt;
&lt;/plugin&gt;
</pre>
</code>


<p/>&nbsp;<br/>
<u><b>Example 2 - Nesting database functions:</b></u>
<br/>To store or retrieve SHA512 hashed passwords in the “password” column 
of the “Users” table in the “public” schema of a PostgreSQL database.
<br/>Note that the digest() function is nested inside the encode() function.

<code><pre>
&lt;plugin type="org.mybatis.generator.plugins.dbFunctions.DbFunctionInvoker"&gt;
	&lt;property name="public.Users.PassWord" value="encode(digest(|COL_VAL|, 'sha512'), 'hex')" /&gt;
&lt;/plugin&gt;
</pre></code>


<p/>&nbsp;<br/>
<u><b>Example 3 - Reusing the |COL_VAL| symbol multiple times:</b></u>
<br/>To store or retrieve the soundex value for the name of an image file after 
discarding the first three characters from the image name, in the 
“img_name” column of the “Images” table in a database that does not use 
schema or catalog names.

<code><pre>
&lt;plugin type="org.mybatis.generator.plugins.dbFunctions.DbFunctionInvoker"&gt;
	&lt;property name="Images.Img_NAme" value="soundex(substr(|COL_VAL|, 4, length(|COL_VAL|)))" /&gt;
&lt;/plugin&gt;
</pre></code>



<p/>&nbsp;<br/>
<u><b>Example 4 – Complete Example:</b></u>
<br/>In the “Users” table of a database not mandating schema/catalog 
specification, to store or retrieve the:
<ol>
<li>soundex value of user's last name in the “last_name_sdx” column</li>
<li>md5 hashed passwords in the “password” column</li>
</ol>
And, in the “UserPhotos” table, store the user's image file name after 
discarding the first three characters from the image file name in the 
“photo_title” column

<code><pre>

&lt;context ...&gt;

&lt;plugin type="org.mybatis.generator.plugins.dbFunctions.DbFunctionInvoker"&gt;
	&lt;property name="Users.last_name_sdx" value="soundex(|COL_VAL|)" /&gt;
	&lt;property name="UserS.PassWord" value="md5(|COL_VAL|, 'US-ASCII')" /&gt;
	&lt;property name="UserPhotos.Photo_title" value="substr(|COL_VAL|, 4, length(|COL_VAL|))" /&gt;
&lt;/plugin&gt;
...
...
&lt;table tableName="Users" domainObjectName="Users"&gt;
	&lt;generatedKey column="user_id" sqlStatement="CALL IDENTITY();" identity="true" type="post" /&gt;
	&lt;columnOverride column="last_name_sdx" property="surnameSound" /&gt;
&lt;/table&gt;
&lt;table tableName="UserPhotos" domainObjectName="UserPhotos" /&gt;
...
&lt;/context&gt;
</pre></code>

With the above-configured plugin, to insert a record in the Employees table:
<code><pre>

Users usr = new Users();
usr.setLogin("bdoe");
usr.setFirstName("Bill");
usr.setLastName("Doe");
//field last_name_sdx maps to Java property surnameSound, due to &lt;columnOverride ...&gt; specification
usr.setSurnameSound("Doe");
usr.setPassword("B0H^3387");

...
...
UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);
mapper.insert(usr);
// or mapper.insertSelective(usr);

...

</pre></code>

The above Java code snippet would store a new record, with the soundex value of 
the user's last name Doe as D000 and the password B0H^3387 as 
bb6db0a80c3c906a00b6eff0d6a78ca3 in the last_name_sdx and password columns of 
the Users table respectively.

<p />Similarly, to update only the photo_title column in the UserPhotos table 
give:
<code> <pre>
UserPhotos recNewValues = new UserPhotos();
recNewValues.setPhotoId(Integer.valueOf(3)); //need to set the PK for updateByPrimaryKeySelective
recNewValues.setPhotoTitle("ImgFront View");

...
...
UserPhotosMapper mapper = sqlSession.getMapper(UserPhotosMapper.class);
mapper.updateByPrimaryKeySelective(recNewValue);
...

</pre></code>

After the above update is committed successfully, the record with photo id = 3 
would have its photo_title column value set to “Front View”. The leading 
“Img” would be removed.

<p />Similarly, to select all records in the Users table that have an unhashed password
value of B0H^3387 give:

<code><pre>

UsersExample ex = new UsersExample();
UsersExample.Criteria crit = ex.createCriteria();
crit.andPasswordEqualTo("B0H^3387");
...
...
UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);
List<Users> lstUsers = mapper.selectByExample(ex);
...
</pre></code>

The above Java code snippet would return a list of User records whose unhashed password is
B0H^3387 (but stored in the database as hash value of bb6db0a80c3c906a00b6eff0d6a78ca3).

<p/>count, delete, etc. would work in a similar way.

@author Mahiar Mody
*/
public class DbFunctionInvoker extends PluginAdapter
{
	protected Map<String, List<String>> mapTblWithCols;
	protected Map<String, String> mapIgnoreCasePropLookup;
	protected Log logger;

	protected static final String COL_VAL_BY_EXAMPLE = "#{RECORD.";
	protected static final String COL_VAL = "#{";
	protected static final String DB_FN_COL_VAL = "|COL_VAL|";
	protected static final String JAVA_PROP = "|JAVA_PROP|";

	public DbFunctionInvoker()
	{
		mapTblWithCols = new HashMap<String, List<String>>();
		mapIgnoreCasePropLookup = new HashMap<String, String>();

		LogFactory.forceJavaLogging();
		logger = LogFactory.getLog(this.getClass());
	}


	protected String validateProperty(String propKey)
	{
		if(propKey.trim().length() == 0)
			return "Property key cannot be empty. Property key ignored.";

		if(propKey.indexOf(' ') != -1)
			return "Propery key cannot contain spaces. Property key ignored: " + propKey;

		int indx1 = propKey.indexOf('.');
		int indx2 = propKey.indexOf('.', indx1+1);

		if(indx1 == -1)
			return "Missing '.' character in Property Key. Function invocations can only be applied to columns not tables. Property key ignored: " + propKey;

		if(indx1==0 || indx1==propKey.length()-1 || indx2==propKey.length()-1)
			return "Property key cannot start or end with a '.' character. Property key ignored: " + propKey;

		if(indx2-indx1 == 1)
			return "Property key cannot contain consequtive '.' characters. Property key ignored: " + propKey;


		String propValue = properties.getProperty(propKey);

		if(propValue.trim().length() == 0)
			return "Property value cannot be empty. Property key ignored: " + propKey;

		return null;
	}


	private boolean isPropertyReferencingIgnoredColumn(List<TableConfiguration> lstTblCfgs, List<String> warnings, String propKey)
	{
		String fullyQualifiedTableNameUc = propKey.substring(0, propKey.lastIndexOf('.')).toUpperCase();
		String colNameUc = propKey.substring(propKey.lastIndexOf('.')+1).toUpperCase();

		for(TableConfiguration tblCfg : lstTblCfgs)
		{
			String tblNmUc = tblCfg.getTableName().toUpperCase();

			if(fullyQualifiedTableNameUc.equals(tblNmUc) || fullyQualifiedTableNameUc.endsWith('.'+tblNmUc))
				return tblCfg.isColumnIgnored(colNameUc); //the isColumnIgnored() checking happens using equalsIgnoreCase(), unless the column name has spaces in it, which is not permitted here.
		}

		warnings.add("Table name specified in property key is not configured in the MyBatis Generator XML Configuration file. Property key: " + propKey);
		return true; //ensures that invalid fully qualified column references in properties are skipped.
	}


	@Override
	public boolean validate(List<String> warnings)
	{
		if(properties.isEmpty())
		{
			warnings.add("The DB Function Invoker plugin requires at least one nested <property> element, but none are specified."
				+ " Hence, the DB Function Invoker plugin will not be run.");

			return false;
		}


		List<TableConfiguration> lstTblCfgs = getContext().getTableConfigurations();

		for(String propKey : properties.stringPropertyNames())
		{
			String strValidationErr = validateProperty(propKey);
			if(strValidationErr != null)
			{
				warnings.add(strValidationErr);
				continue;
			}

			if(isPropertyReferencingIgnoredColumn(lstTblCfgs, warnings, propKey))
				continue;

			mapIgnoreCasePropLookup.put(propKey.toUpperCase(), propKey);


			String fullyQualifiedTableName = propKey.substring(0, propKey.lastIndexOf('.')).toUpperCase();
			String colName = propKey.substring(propKey.lastIndexOf('.')+1).toUpperCase();

			List<String> lstCols = mapTblWithCols.get(fullyQualifiedTableName);
			if(lstCols == null)
			{
				lstCols = new ArrayList<String>();
				mapTblWithCols.put(fullyQualifiedTableName, lstCols);
			}

			lstCols.add(colName);
		}

		return true;
	}


	@Override
	public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
		// Mybatis mapper uses OGNL expression language for xml mapper.
		// For more info on using OGNL see: http://commons.apache.org/proper/commons-ognl/language-guide.html

		String fullyQualifiedTableName = introspectedTable.getFullyQualifiedTableNameAtRuntime().toUpperCase();

		List<String> lstCols = mapTblWithCols.get(fullyQualifiedTableName); //get all the fully qualified column names from the plugin for the specified introspectedTable 

		if(lstCols != null)
		{
			String tblAlias = introspectedTable.getTableConfiguration().getAlias();

			XmlElement xeWhenSingleValue = getSingleXmlElement(element, "where/foreach/if/trim/foreach/choose/when[@test='criterion.singleValue']");
			XmlElement xeWhenBetweenValue = getSingleXmlElement(element, "where/foreach/if/trim/foreach/choose/when[@test='criterion.betweenValue']");
			XmlElement xeForEachList = getSingleXmlElement(element, "where/foreach/if/trim/foreach/choose/when[@test='criterion.listValue']/foreach");

			XmlElement xeInjSingleValue = getInjectedWhereClauseSqlFragments(xeWhenSingleValue, 'S', fullyQualifiedTableName, lstCols, tblAlias);
			XmlElement xeInjBetweenValue = getInjectedWhereClauseSqlFragments(xeWhenBetweenValue, 'B', fullyQualifiedTableName, lstCols, tblAlias);
			XmlElement xeInjListValue = getInjectedWhereClauseSqlFragments(xeForEachList, 'L', fullyQualifiedTableName, lstCols, tblAlias);
		
			xeWhenSingleValue.addElement(xeInjSingleValue);
			xeWhenBetweenValue.addElement(xeInjBetweenValue);
			xeForEachList.addElement(xeInjListValue);
		}

		return true;
	}


	@Override
	public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
		injectCustomSqlIntoContigiousTextElements(false, true, element, introspectedTable, "insert into ", "insert");
		return true;
	}


	@Override
	public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
		injectCustomSqlAtXpath(false, "trim[@prefix='values (']/if[@test='" + JAVA_PROP + " != null']", element, introspectedTable);
		return true;
	}


	@Override
	public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
		injectCustomSqlAtXpath(true, "set/if[@test='record." + JAVA_PROP + " != null']", element, introspectedTable);
		return true;
	}


	@Override
	public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
		injectCustomSqlIntoContigiousTextElements(true, true, element, introspectedTable, "update ", "updateByExampleWithBLOBs");
		return true;
	}

	@Override
	public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
		injectCustomSqlIntoContigiousTextElements(true, false, element, introspectedTable, "update ", "updateByExample");
		return true;
	}

	@Override
	public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
		injectCustomSqlAtXpath(false, "set/if[@test='" + JAVA_PROP + " != null']", element, introspectedTable);
		return true;
	}

	@Override
	public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
		injectCustomSqlIntoContigiousTextElements(false, true, element, introspectedTable, "update ", "updateByPrimaryKeyWithBLOBs");
		return true;
	}

	@Override
	public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
		injectCustomSqlIntoContigiousTextElements(false, false, element, introspectedTable, "update ", "updateByPrimaryKey");
		return true;
	}


	/**
	Returns an XmlElement containing db function invocations injected into the specified xeTarget element.
	This method injects the db funtion invocations into specific parts (critera types) of the SQL
	"xxx_Example_Where_Clause" tag. The SQL "xxx_Example_Where_Clause" tag handles different critera
	types in different &lt;when test=...&gt; tags. The specified xeTarget identifies the
	&lt;when test=...&gt; tag into which the db function invocation is injected.

	<p />From the specified xeTarget, this method removes the MBG generated criterion.xxx line,
	and replaces it with a new &lt;choose&gt; tag. For each fully qualified column name specified
	in the plugin, which belongs to the specified fqTableName, this method appends a new
	&lt;when test=...&gt; tag, to the newly added &lt;choose&gt; tag.
	The test attribute of the newly appended &lt;when test=...&gt;
	tag compares the starting value in criterion.condition to the name of the column
	specified in the plugin, while the contents of the newly appended &lt;when test=...&gt; tag,
	contains the criterion.value injected with the corresponding db function invocation.

	<p />The previously removed MBG generated criterion.xxx line, is wrapped into
	an &lt;otherwise&gt; tag, which too is appended to the newly added &lt;choose&gt; tag.
	Finally, this method returns the &lt;choose&gt; tag with criterion.condition and
	criterion.value appropriatly injected with db function invocations.

	<p />The <code>critType</code> constant specifies the the type of the "when" tag into
	which the db function invocation is to be injected. It can only have the following possible
	values:
	<ol>
	<li>'S' to specify criterion.singleValue</li>
	<li>'B' to specify criterion.betweenValue</li>
	<li>'L' to specify criterion.listValue</li>
	</ol>
	An invalid value of critType will cause an IllegalArgumentException to be thrown.

	@param xeTarget  the "when" tag of the main "xxx_Example_Where_Clause" sql tag, into which the db function invocations will be injected
	@param critType  a constant indicating the "when" tag type, such as 'S' for "criterion.singleValue", into which the db function invocations
						will be injected
	@param fqTableName  the fully qualified table name, of the table whose column values need db function invocation injections
	@param lstCols  the List of fully qualified column names specified in the plugin that need db function invocation injection
	@param tblAlias  the alias, if any, for the table whose column values need db function invocation injections, as specified
						in the <code>&lt;table ...&gt;</code> element of the MBG XML configuration file
	@return an XmlElement containing db funtion invocations injected into the specified xeTarget element
	@throws IllegalArgumentException if the value of critType is not one of 'S', 'B', or 'L'
	*/
	protected XmlElement getInjectedWhereClauseSqlFragments(XmlElement xeTarget, char critType, String fqTableName, List<String> lstCols, String tblAlias)
	{
		if(critType != 'S' && critType != 'B' && critType != 'L')
			throw new IllegalArgumentException("Invalid critType argument specified: " + critType);

		if(tblAlias == null)
			tblAlias = "";
		else
			tblAlias = tblAlias.toLowerCase() + '.';


		StringBuilder sb = new StringBuilder();

		XmlElement xeChoose = new XmlElement("choose"); //create the new <choose> element.

		//for loop to add <when test=...> tags to the <choose> tag created above, for all fully qualified column names specified in the plugin belonging to table fqTableName 
		for(String colName : lstCols)
		{
			String injectedDbFnInvk = properties.getProperty(mapIgnoreCasePropLookup.get(fqTableName + '.' + colName));

			//Start of code to inject the db function invocation into criterion.value

			if(critType != 'L')
			{
				sb.append("and ${criterion.condition} ");
				sb.append(injectedDbFnInvk.replace(DB_FN_COL_VAL, "#{criterion.value}"));

				if(critType == 'B')
				{
					sb.append(" and ");
					sb.append(injectedDbFnInvk.replace(DB_FN_COL_VAL, "#{criterion.secondValue}"));
				}
			}
			else
				sb.append(injectedDbFnInvk.replace(DB_FN_COL_VAL, "#{listItem}"));

			//End of code to inject the db function invocation into criterion.value



			TextElement teInjectedSqlLine = new TextElement(sb.toString()); //wrap db function invocation injected criterion.value line into TextElement 

			XmlElement xeWhen = new XmlElement("when"); //create the new <when test=...> tag
			xeWhen.addAttribute(new Attribute("test", "criterion.condition.toLowerCase().startsWith('" + tblAlias + colName.toLowerCase() + " ')"));

			xeWhen.addElement(teInjectedSqlLine); //add the TextElement containing db function invocation injected criterion.value line
			xeChoose.addElement(xeWhen); //append the newly created <when test=...> tag to the <choose> element

			sb.delete(0, sb.length());
		}

		TextElement teDefaultPlainSqlLine = (TextElement) xeTarget.getElements().remove(0); //Remove the MBG generate criterion.xxx line
			
		XmlElement xeOtherwise = new XmlElement("otherwise");
		xeOtherwise.addElement(teDefaultPlainSqlLine); //Wrap the MBG generated criterion.xxx line into the <otherwise> tag

		xeChoose.addElement(xeOtherwise); //append the <otherwise> tag to the <choose> element created above.

		return xeChoose;
	}


	/**
	This method is called for all Non-selective type of Sql Xml Maps elements, which have multiple contigious
	TextElements.
	<br/>For Non-selective type Sql Xml Maps, it is not possible to obtain a direct xpath to the column value.
	This is because the column values are not enclosed in unique xpaths. Instead they are grouped together
	with the column names and sql commands (insert or update) to form one big multi-line sql statement
	consisting of multiple TextElements. Each TextElement represents a single line of the multi-line
	sql statement.

	<p />This method first checks if any of the fully qualified column names specified in the plugin
	belong to the specified introspectedTable. If none of the fully qualified column names specified in the
	plugin belong to the specified introspected table, this method does nothing and simply returns.

	<p />If however, some of the fully qualified column names specified in the plugin do belong to the
	specified intorspectedTable, the matching columns in the specified introspectedTable are stored in
	a List of IntrospectedColumn objects.

	<p />Next, this method removes all the TextElements that make up the entire multi-line SQL statement
	from the specified element and store them in a SqlXmlFragments object. The position or offset of
	the first extracted TextElement within the specified element is also stored in the SqlXmlFragments
	object.

	<p />Lastly, the specified element, the SqlXmlFragments object, the List of IntorspectedColumns
	along with other requiered parameters are passed to the
	<code>doCustomSqlInjectionForTextElements</code> method for the actual injection of the
	db function invocations.

	@param isByExample  <code>true</code> if the main insert or update tag of the SQL Mapper file is of type "xxxByExample". <code>false</code> otherwise.
	@param isWithBlobs <code>true</code> if the main insert or update tag of the SQL Mapper file is of type "xxxWithBLOBs". <code>false</code> otherwise.
	@param element the main insert or update tag of the SQL Mapper file
	@param introspectedTable the table to be searched for columns whose fully qualified names are specified in the plugin
	@param sqlStmtStart the starting text of the actual SQL statement
	@param sqlMapElementId the id attribute of the main insert or update tag of the SQL Mapper file
	@see DbFunctionInvoker#doCustomSqlInjectionForTextElements(boolean, XmlElement, SqlXmlFragments, String, List) doCustomSqlInjectionForTextElements
	*/
	protected void injectCustomSqlIntoContigiousTextElements(boolean isByExample, boolean isWithBlobs, XmlElement element, IntrospectedTable introspectedTable, String sqlStmtStart, String sqlMapElementId)
	{
		//Start of code to check if any of the columns in the supplied IntrospectedTable "introspectedTable" have db function invocations associated with them

		String fullyQualifiedTableName = introspectedTable.getFullyQualifiedTableNameAtRuntime().toUpperCase();
		List<String> lstColNames = mapTblWithCols.get(fullyQualifiedTableName);

		if(lstColNames == null) //if true, no columns of the introspected table need to be injected with custom SQL, so do nothing and proceed.
			return;

		//End of code to check if any of the columns in the supplied IntrospectedTable "introspectedTable" have db function invocations associated with them



		//Start of code to get the List of IntrospectedColumns matching the supplied List of column names, "lstColNames", from the supplied IntrospectedTable "introspectedTable".

		/*
		In the method call below, when the parameter "isWithBlobs" is false, only the Base columns from the
		introspected table are checked for matching column names with the supplied List of column names viz.
		"lstColNames". Thus, whenever the db function invocation is applied to the LOB columns ONLY, and
		the value of "isWithBlobs" is false, the returned IntrospectedColumn List is empty. This is because
		none of the LOB column names in "lstColNames" are found in the base column list of the introspected table.
		Since none of the base columns of the introspected table need to be injected with custom SQL, there's nothing
		to do and we can safely return from this function.
		*/
		List<IntrospectedColumn> lstIntrospectedCols = getIntrospectedColumnsFor(introspectedTable, isWithBlobs, lstColNames);

		if(lstIntrospectedCols.size() == 0) //if true, none of the base columns need custom SQL injecting. So do nothing and return. 
			return;

		//End of code to get the List of IntrospectedColumns matching the supplied List of column names, "lstColNames", from the supplied IntrospectedTable "introspectedTable".



		//Start of code to remove the actual SQL statement TextElements from the supplied XmlElement "element" and store them in a SqlXmlFragments object.

		SqlXmlFragments sqlXmlFrags = SqlXmlFragments.getTextElementsContainingSqlStatement(element, sqlStmtStart);

		if(sqlXmlFrags.getSqlTextElementsList().size() == 0)
		{
			logger.warn("The " + element.getName() + " SQL text not found in the generated element. Generated element not modified: " + sqlMapElementId);
			return;
		}

		//End of code to remove the actual SQL statement TextElements from the supplied XmlElement "element" and store them in a SqlXmlFragments object.


		//The method call below actually injects the db function invocations into the supplied XmlElement "element"
		doCustomSqlInjectionForTextElements(isByExample, element, sqlXmlFrags, fullyQualifiedTableName, lstIntrospectedCols);
	}



	/**
	This method handles the actual db function invocation injections for all Non-selective type of Sql
	Xml Maps elements. All Non-selective type of Sql Xml Maps have multiple contigious TextElements
	representing the entire multi-line sql statement.

	<p />The supplied sqlXmlFrags contains such a List of TextElements, with each representing a single line
	of the multi-line sql statement. This method iterates over each TextElement (line of sql statement),
	probing it for column values needing db function invocation injection.

	<p />Each iterated TextElement (line of sql statement) may contain multiple column values needing
	db function invocation injection. Hence, each iterated TextElement, is probed by every column in
	the specified list of introspected columns for column values needing db function injection.

	<p />Once all column values in each TextElement needing db function injection are injected with their
	respective db function invocations, the modified TextElements are re-inserted back into the specified
	element at the offset position from which they were originally extracted.

	@param isByExample  <code>true</code> if the main insert or update tag of the SQL Mapper file is of type "xxxByExample". <code>false</code> otherwise.
	@param element  the main insert or update tag of the SQL Mapper file
	@param sqlXmlFrags  the <code>SqlXmlFragments</code> object containing the SQL statement TextElement tags and the offset position
						in the element tag at which to re-insert the SQL statements having the db function invocations
	@param fqTblNm  the fully qualified table name of the table whose column values need db function invocation injections
	@param lstIntrCols  the List of introspected columns whose values need db function invocation injection
	@see DbFunctionInvoker#injectCustomSqlIntoContigiousTextElements(boolean, boolean, XmlElement, IntrospectedTable, String, String) injectCustomSqlIntoContigiousTextElements
	*/
	protected void doCustomSqlInjectionForTextElements(boolean isByExample, XmlElement element, SqlXmlFragments sqlXmlFrags, String fqTblNm, List<IntrospectedColumn> lstIntrCols)
	{
		StringBuilder sbSql = new StringBuilder();

		List<TextElement> lstSqlTxtElm = sqlXmlFrags.getSqlTextElementsList();

		ListIterator<TextElement> lstItrTeSqlElms = lstSqlTxtElm.listIterator(lstSqlTxtElm.size());

		/*
		Reverse iterate over each item in the List of TextElements of the supplied SqlXmlFragment object "sqlXmlFrags".
		Each TextElement item contains a single line of the multi-lined SQL statement that is represented by the entire List of TextElements.
		Reverse iteration is necessary because each TextElement will be re-inserted at the same index position in the
		original XmlElement viz. "element", from which they were previously extracted. The index position is the offset
		of the first TextElement that was previously removed from "element".
		*/
		while(lstItrTeSqlElms.hasPrevious())
		{
			/* In the statement below, add the text content of the currently iterated TextElement into the empty StringBuilder.
			The formatted content includes not only the single line of the SQL statement's text but also its indentation. */
			sbSql.append(lstItrTeSqlElms.previous().getFormattedContent(0));

			Iterator<IntrospectedColumn> itrIntrCols = lstIntrCols.iterator();

			/*
			For each TextElement item representing a single line of SQL Statement, iterate over the List of introspected
			columns whose values need db function invocation injection.
			This loop addresses the possibility that multiple introspected column values needing db function invocation
			injections may be present within the current single line of SQL statement.
			*/
			while(itrIntrCols.hasNext())
			{
				IntrospectedColumn intrCol = itrIntrCols.next();

				/*
				The variable "strColPattern" represents how the VALUE of the currently iterated upon introspected
				column viz. "intrCol" is written out by the MBG in the generated SQL statement.
				For all "ByExample" type of SQL Map elements, the column values are written out as: #{record.<col's java property name>}
				Otherwise, the columns values are written out as: #{<col's java property name>}
				*/
				String strColPattern = null;
				if(isByExample)
					strColPattern = COL_VAL_BY_EXAMPLE + intrCol.getJavaProperty().toUpperCase();
				else
					strColPattern = COL_VAL + intrCol.getJavaProperty().toUpperCase();


				/*Start of code to search for the existence of the currently iterated introspected column's VALUE
				in the currently iterated line of the generate SQL statement */

				String strSql = sbSql.toString().toUpperCase(); //make the search for strColPattern possible by setting everything is in upper case.

				int indxStart = strSql.indexOf(strColPattern);
				int indxEnd = strSql.indexOf('}', indxStart+strColPattern.length()); //skip the starting strColPattern part, hence the +sbColPattern.length()

				if(indxStart == -1 || indxEnd == -1) //column not found.
					continue;

				/*End of code to search for the existence of the currently iterated introspected column's VALUE
				in the currently iterated line of the generate SQL statement */



				//Start of code to inject the db function invocation

				String sqlColFrag = sbSql.substring(indxStart, indxEnd+1); //obtain just the text representing the introspected column's VALUE

				//In the statement below, get the required db function invocations specified in the plugin
				String dbFnInvk = properties.getProperty(mapIgnoreCasePropLookup.get(fqTblNm + '.' + intrCol.getActualColumnName().toUpperCase()));

				/* In the statement blow, first, all occurrences of the plugin's keyword "|COL_VAL|" in the db function invoction
				are replaced with the actual VALUE of the currently introspected column. This modified db function invocation
				is then used to replace the VALUE of the currently introspected column within the original MBG generated
				line of SQL statement. */
				sbSql.replace(indxStart, indxEnd+1, dbFnInvk.replace(DB_FN_COL_VAL, sqlColFrag));

				//End of code to inject the db function invocation

				itrIntrCols.remove();
			}

			/* In the statement below, insert the single line of SQL statement that is now injected with the db
			function invocations back into the original XmlElement, at the position from which the TextElement
			representing the start of the SQL statements was previously removed. */
			element.addElement(sqlXmlFrags.getInsertIndexOffset(), new TextElement(sbSql.toString()));

			sbSql.delete(0, sbSql.length()); //empty the StringBuilder
		}


		if(lstIntrCols.size() > 0)
		{
			for(IntrospectedColumn intrCol : lstIntrCols)
			{
				sbSql.append(intrCol.getActualColumnName());
				sbSql.append(',');
			}

			sbSql.deleteCharAt(sbSql.length()-1);

			logger.warn("Column(s) not found in the generated sql map element. Column(s) ignored: " + sbSql.toString());
		}
	}


	/**
	Returns a List of IntrospectedColumns from the specified introspectedTable that correspond to the fully qualified
	column names specified in the lstColNames List.

	<p/>
	When the <code>isWithBlobs</code> parameter is true, all columns of the specified introspected table are searched.
	When <code>isWithBlobs</code> is false, only the base columns of the specified introspected table are searched and
	not the LOB columns.

	@param introspectedTable  the <code>IntrospectedTable</code> from which to return the List of introspected columns
	@param isWithBlobs  a boolen indicating whether or not the main insert or update tag of the SQL Mapper file is of type "WithBLOBs"
	@param lstColNames  the List of fully qualified column names representing the Introspected columns to be returned
	@return the List of <code>IntrospectedColumn</code> objects whose names match the specified lstColNames List
	*/
	protected List<IntrospectedColumn> getIntrospectedColumnsFor(IntrospectedTable introspectedTable, boolean isWithBlobs, List<String> lstColNames)
	{
		List<IntrospectedColumn> lstIntrColsWithFnCall = new ArrayList<IntrospectedColumn>();
		int i=0, sz=lstColNames.size();

		List<IntrospectedColumn> lstAvailableIntrospectedCols = isWithBlobs ? introspectedTable.getAllColumns() : introspectedTable.getBaseColumns();

		for(IntrospectedColumn intrCol : lstAvailableIntrospectedCols)
		{
			if(lstColNames.contains(intrCol.getActualColumnName().toUpperCase()))
			{
				lstIntrColsWithFnCall.add(intrCol);
				++i;
			}

			if(i == sz)
				break;
		}

		return lstIntrColsWithFnCall;
	}



	/** This method is called for only the "selective" type of Sql Xml Maps.
	The "selective" type of Sql Xml Maps consist of a multi-line Sql statement
	in which the value of each column is nested inside its own unique	
	&lt;if test="..."&gt; tag, within the main insert or update tag of the SQL
	Mapper file.
	<br/>Thus, for selective type of Sql Xml Maps, it is possible to obtain a
	direct xpath to the column value needing db function invocation injection,
	because every column value is enclosed in a unique xpath.

	<p />Every unique xpath has an identical xpath structure, with only the value of the
	"test" attribute of every xpath differing. The "test" attribute's value is the
	Java property name of the column whose value is nested inside the &lt;if test....&gt;
	tag. Hence, only a single generalized xpath expression needs to be specified to this method,
	substituting the keyword <code>JAVA_PROP</code> in place of the actual value of the
	"test" attribute. At runtime, this method will replace the <code>JAVA_PROP</code>
	keyword with the actual Java property name, to derive the unique introspected column
	specific xpath.

	<p />This method first checks if any of the fully qualified column names specified in the plugin
	belong to the specified introspectedTable. If none of the fully qualified column names specified in the
	plugin belong to the specified introspected table, this method does nothing and simply returns.

	<p />If however, some of the fully qualified column names specified in the plugin do belong to the
	specified intorspectedTable, the matching columns in the specified introspectedTable are stored in
	a List of IntrospectedColumn objects.

	<p />Next, this method iterates over the obtained list of introspected columns. In each
	iteration, this method gets the "&lt;if test=...&gt;" XmlElement matching the derived xpath,
	unique to the introspected column currently being iterated upon. The unique xpath is derived
	by replacing the keyword <code>JAVA_PROP</code> in the specified xpath expression with the
	actual Java property of the intospected column being iterated upon. Next, from the obtained
	&lt;if test...&gt; XmlElement, the TextElement containing the value of the currently
	iterated introspected column, is removed. From the removed TextElement, the column's value is extracted.
	Next, the db function invocations are applied to the extracted column value. Finally,
	the column value woven with the injected db function invocations are wrapped inside a new
	TextElement which is then re-inserted into the &lt;if test...&gt; XmlElement.

	@param isByExample  <code>true</code> if the main insert or update tag of the SQL Mapper file is of type "xxxByExample". <code>false</code> otherwise.
	@param xpath  the generalized xpath expression specifying the xpath to the nested &lt;if test...&gt; tags, containing column values
	@param element  the main insert or update tag of the SQL Mapper file
	@param introspectedTable  the <code>IntrospectedTable</code> to be searched for columns needed db function invocation injection
	*/
	protected void injectCustomSqlAtXpath(boolean isByExample, String xpath, XmlElement element, IntrospectedTable introspectedTable)
	{
		String fullyQualifiedTableName = introspectedTable.getFullyQualifiedTableNameAtRuntime().toUpperCase();
		List<String> lstColNames = mapTblWithCols.get(fullyQualifiedTableName);

		if(lstColNames == null) //if true, no columns of the introspected table need to be injected with custom SQL, so do nothing and proceed.
			return;

		/*
		In the statement below, the "isWithBlobs" parameter is hard-coded to "true" because all "selective" type of Sql
		Xml Maps always perform updates or inserts on LOB columns.
		Since the "isWithBlobs" parameter is true, all columns in the supplied introspected table viz. "introspectedTable"
		will be searched for matching introspected columns having names corresponding to the supplied List of column names,
		thereby precluding the possibility of an empty List of IntrospectedColumns being returned.
		*/
		List<IntrospectedColumn> lstIntrColsNeedingInjection = getIntrospectedColumnsFor(introspectedTable, true, lstColNames);

		if(lstIntrColsNeedingInjection.size() == 0) //if true, an error condition. Perhaps typos exist in the fully qualified column names specified in the plugin.
		{
			StringBuilder sbErr = new StringBuilder();
			for(String colNm : lstColNames)
			{
				sbErr.append(colNm);
				sbErr.append(',');
			}

			sbErr.deleteCharAt(sbErr.length()-1); //remove the trailing comma

			logger.warn("No introspected Column(s) found in introspected table "
				+ introspectedTable.getFullyQualifiedTableNameAtRuntime()
				+ " for the following column(s): " + sbErr.toString());

			return;
		}


		StringBuilder sbFmtdColVal = new StringBuilder();

		for(IntrospectedColumn intrCol : lstIntrColsNeedingInjection)
		{
			//The statement below obtains the "&lt;if ...&gt;" XmlElement matching the derived xpath unique to the intorspected column in the current iteration
			XmlElement ifXeElm = getSingleXmlElement(element, xpath.replace(JAVA_PROP, intrCol.getJavaProperty()));

			//The statement below extracts the only child TextElement of the above-obtained &lt;if ...&gt;
			TextElement teElm = (TextElement) ifXeElm.getElements().remove(0);

			/* The statement below, adds the formatted content of the above-obtained TextElement into a StringBuilder object.
			The formatted content includes not only the column value of the currently iterated introspected column, but also
			its indentation and comma. */
			sbFmtdColVal.append(teElm.getFormattedContent(0));

			/*
			The variable "strColPattern" represents how the VALUE of the currently iterated upon introspected
			column viz. "intrCol" is written out by the MBG in the generated SQL statement.
			For all "ByExample" type of SQL Map elements, the column values are written out as: #{record.<col's java property name>}
			Otherwise, the columns values are written out as: #{<col's java property name>}
			*/
			String strColPattern = null;
			if(isByExample)
				strColPattern = COL_VAL_BY_EXAMPLE + intrCol.getJavaProperty().toUpperCase();
			else
				strColPattern = COL_VAL + intrCol.getJavaProperty().toUpperCase();


			//Start of code to extract the currently iterated introspected column's value, which is identified by "strColPattern"

			String strSql = sbFmtdColVal.toString().toUpperCase(); //make the search for strColPattern possible by setting everything is in upper case.

			int indxStart = strSql.indexOf(strColPattern);
			int indxEnd = strSql.indexOf('}', indxStart+strColPattern.length()); //skip the starting strColPattern part, hence the +sbColPattern.length()

			if(indxStart == -1 || indxEnd == -1) //column not found.
			{
				logger.warn("Column not found in the generated sql map selective: " + intrCol.getActualColumnName());
				continue;
			}

			String sqlColVal = sbFmtdColVal.substring(indxStart, indxEnd+1); //obtain just the text matching "strColPattern"

			//End of code to extract the currently iterated introspected column's value, which is identified by "strColPattern"


			//Start of code to inject the db function invocation

			String dbFnInvk = properties.getProperty(mapIgnoreCasePropLookup.get(fullyQualifiedTableName + '.'
				+ intrCol.getActualColumnName().toUpperCase())); //get the required db function invocations specified in the plugin

			/* In the statement blow, first, all occurrences of the plugin's keyword "|COL_VAL|" in the db function invoction
			are replaced with the actual VALUE of the currently introspected column. This modified db function invocation
			is then used to replace the VALUE of the currently introspected column within the original MBG generated
			line of SQL statement. */
			sbFmtdColVal.replace(indxStart, indxEnd+1, dbFnInvk.replace(DB_FN_COL_VAL, sqlColVal));

			//The statement below adds the db function invocation injected column value back into the &lt;if ...%gt; tag from which it was originaly removed
			ifXeElm.addElement(new TextElement(sbFmtdColVal.toString()));

			//End of code to inject the db function invocation

			sbFmtdColVal.delete(0, sbFmtdColVal.length());
		}
	}


	/**
	Returns an XmlElement matching the specified xpath expression from the specified
	root XmlElement. The specified xpath expression has limited capabilities as compared to its
	counterpart in Java, and as such only supports simple XmlElement traversal based on
	XmlElement names and attributes.
	
	If no descendent XmlElement of the specified element matches the specified xpath expression,
	this method returns null.

	@param root  the starting XmlElement in which the specified xpath is to be evaluated
	@param xpath the xpath expression
	@return the XmlElement that is the result of evaluating the xpath expression 
	*/
	protected XmlElement getSingleXmlElement(XmlElement root, String xpath)
	{
		int indx = xpath.indexOf('/');
		String strXpathSegmentToSearch = indx == -1 ? xpath : xpath.substring(0, indx); //get only the first element name specified on the xpath

		Attribute[] attrArr = null;

		if(strXpathSegmentToSearch.indexOf('[') != -1)
		{
			attrArr = parseAttributesInSegment(strXpathSegmentToSearch);
			strXpathSegmentToSearch = strXpathSegmentToSearch.substring(0, strXpathSegmentToSearch.indexOf('[')); //keep only the element name, stripping off the attributes 
		}

		for(Element elm : root.getElements()) //iterate over all the child elements, searching for the child element with name and attributes matching the xpath segment
		{
			if(elm instanceof TextElement)
				continue;

			XmlElement xeElm = (XmlElement) elm;

			if(xeElm.getName().equals(strXpathSegmentToSearch) && (attrArr==null || isXmlElementContainingAttributes(xeElm, attrArr)))
			{
				if(indx == -1) //if true, the complete xpath expression is recursed and evaluated. Hence, the matching "xeElm" is the XmlElement of interest.
					return xeElm;
				else
					return getSingleXmlElement(xeElm, xpath.substring(indx+1)); //recurse the remaining xpath expression inside the matching "xeElm" XmlElement 
			}
		}

		return null;
	}


	/**
	Parses the xpath attributes embedded in the specified String argument as an array of <code>Attribute</code>
	objects. The string argument must be an individual xpath expression segment that may or may not contain
	embedded attributes. An xpath expression segment consists of a single tag name that forms part of the
	entire xpath expression. More specifically it represents the part of the xpath expression between any two
	consecutive "/" characters.

	<p />If no attributes are embedded in the individual xpath expression segment, this method returns null.
	The leading @ sign from the attribute name, and single quotation mark surrounding the attribute value
	in the xpath expression, are removed before storing in the <code>Attribute</code> object.

	@param strXpathSegmentToSearch the individual xpath expression segment whose embedded attributes need to be parsed
	@return  an array of <code>Attribute</code> objects parsed from the specified individual xpath expression segment
	*/
	protected Attribute[] parseAttributesInSegment(String strXpathSegmentToSearch)
	{
		List<Attribute> lstAttr = new ArrayList<Attribute>();

		int indx = strXpathSegmentToSearch.indexOf('[');

		if(indx == -1) //if true, the specified xpath segment does not have any attribute based searches specified.
			return null;

		String[] strAttrArr = strXpathSegmentToSearch.substring(indx+1, strXpathSegmentToSearch.length()-1).split("and", 0);

		for(String strAttr : strAttrArr)
		{
			String[] pair = strAttr.split("=", 2);

			/* The statement below removes the leading @ sign from the attribute name, and removes the single quotation
			mark surrounding the attribute value, before storing the attribute name & value into a new Attribute object */
			lstAttr.add(new Attribute(pair[0].trim().substring(1), pair[1].trim().replace("'", "")));
		}

		return lstAttr.toArray(new Attribute[0]);
	}


	/**
	Returns a boolean indicating whether or not the specified XmlElement contains the specified Attributes.
	This method returns true if the specified XmlElement viz. "xeElm" contains all the Attributes specified
	in the Attribute array "attrArr", false otherwise.

	<p />This method returns true if the specified Attribute array is null. The specified XmlElement may
	contain more attributes than those specified in the Attribute array. Additional attributes in the
	XmlElement are ignored and do not in any way influence the outcome of this method.

	@param xeElm  the XmlElement in which to search for Attributes
	@param attrArr the array of Attributes that need to be searched
	@return true if the specified XmlElement contains all the Attributes specified in the Attribute array, false otherwise
	*/
	protected boolean isXmlElementContainingAttributes(XmlElement xeElm, Attribute[] attrArr)
	{
		if(attrArr == null)
			return true;

		int matchedAttrCnt = 0;

		for(Attribute attrXe : xeElm.getAttributes())	
		{
			for(Attribute attrToFind : attrArr)
			{
				if(attrXe.getName().equals(attrToFind.getName()) && attrXe.getValue().equals(attrToFind.getValue()))
				{
					++matchedAttrCnt;
					break;
				}
			}
		}

		return matchedAttrCnt == attrArr.length;
	}
}
