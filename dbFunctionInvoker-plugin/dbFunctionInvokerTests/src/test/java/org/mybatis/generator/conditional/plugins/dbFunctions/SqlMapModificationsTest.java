/*
 *    Copyright 2014 Mahiar Mody.
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


package org.mybatis.generator.conditional.plugins.dbFunctions;


import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;

import java.io.IOException;


import org.junit.Assert;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import org.junit.runner.RunWith;

import org.junit.runners.JUnit4;


/**
This class is used to test the MyBatis Generator generated SQL Map files with
the Conditional Model Type for the presence of database function invocations.

@author Mahiar Mody
*/
@RunWith(JUnit4.class)
public class SqlMapModificationsTest
{
	private static XPath xpath;
	private static DocumentBuilder docBldr;


	@BeforeClass
	public static void setUpBeforeClass() throws ParserConfigurationException
	{
		xpath = XPathFactory.newInstance().newXPath();
		docBldr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		xpath = null;
		docBldr = null;
	}


	//Start of testing Users

	/*
	The method below tests for two columns from the same table and hence cannot make use of
	the runTestsForByExampleWhereClauses() convenience method, which only tests for a single
	column from a given table. Hence the test case below is explicitely coded.
	*/
	@Test
	public void testBothExampleWhereClausesForUsers() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersMapper.xml").toExternalForm());

		String[] egWhereClauseIds = {"Example_Where_Clause", "Update_By_Example_Where_Clause"};

		for(String byEgWhereClauseId : egWhereClauseIds)
		{
			Node ndChoose = (Node) xpath.evaluate("/mapper/sql[@id='" + byEgWhereClauseId + "']/where/foreach/if/trim/foreach/choose", docRoot, XPathConstants.NODE);

			//No value
			String strTxt = (String) xpath.evaluate("when[@test='criterion.noValue']", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " the criterion.noValue tag is wrong.", "and ${criterion.condition}", strTxt.trim());

			//Single Value
			String strCnt = (String) xpath.evaluate("count(when[@test='criterion.singleValue']/choose/*)", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Wrong number of child tags under when[@test='criterion.singleValue']/choose tag",
				"3", strCnt);

			String strVal = (String) xpath.evaluate("when[@test=\"criterion.singleValue\"]/choose/when[@test=\"criterion.condition.toLowerCase().startsWith('last_name_sdx ')\"]", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in singleValue for last_name_sdx column",
				"and ${criterion.condition} soundex(#{criterion.value})", strVal.trim());

			strVal = (String) xpath.evaluate("when[@test=\"criterion.singleValue\"]/choose/when[@test=\"criterion.condition.toLowerCase().startsWith('password ')\"]", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in singleValue for password column",
				"and ${criterion.condition} md5(#{criterion.value}, 'US-ASCII')", strVal.trim());

			strVal = (String) xpath.evaluate("when[@test='criterion.singleValue']/choose/otherwise", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in singleValue for otherwise",
				"and ${criterion.condition} #{criterion.value}", strVal.trim());

			//Between Value
			strCnt = (String) xpath.evaluate("count(when[@test='criterion.betweenValue']/choose/*)", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Wrong number of child tags under when[@test='criterion.betweenValue']/choose tag",
					"3", strCnt);

			strVal = (String) xpath.evaluate("when[@test=\"criterion.betweenValue\"]/choose/when[@test=\"criterion.condition.toLowerCase().startsWith('last_name_sdx ')\"]", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in betweenValue for last_name_sdx column",
				"and ${criterion.condition} soundex(#{criterion.value}) and soundex(#{criterion.secondValue})", strVal.trim());

			strVal = (String) xpath.evaluate("when[@test=\"criterion.betweenValue\"]/choose/when[@test=\"criterion.condition.toLowerCase().startsWith('password ')\"]", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in betweenValue for password column",
				"and ${criterion.condition} md5(#{criterion.value}, 'US-ASCII') and md5(#{criterion.secondValue}, 'US-ASCII')", strVal.trim());

			strVal = (String) xpath.evaluate("when[@test='criterion.betweenValue']/choose/otherwise", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in betweenValue for otherwise",
				"and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}", strVal.trim());

			//List Value
			strCnt = (String) xpath.evaluate("count(when[@test='criterion.listValue']/foreach/choose/*)", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Wrong number of child node for listValue tag", "3", strCnt);

			strVal = (String) xpath.evaluate("when[@test=\"criterion.listValue\"]/foreach/choose/when[@test=\"criterion.condition.toLowerCase().startsWith('last_name_sdx ')\"]", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in listValue for last_name_sdx column",
				"soundex(#{listItem})", strVal.trim());

			strVal = (String) xpath.evaluate("when[@test=\"criterion.listValue\"]/foreach/choose/when[@test=\"criterion.condition.toLowerCase().startsWith('password ')\"]", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in listValue for password column",
				"md5(#{listItem}, 'US-ASCII')", strVal.trim());

			strVal = (String) xpath.evaluate("when[@test='criterion.listValue']/foreach/choose/otherwise", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in listValue for otherwise",
				"#{listItem}", strVal.trim());
		}
	}


	@Test
	public void testIgnoreColumnsNotInExampleWhereClauses() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersMapper.xml").toExternalForm());

		String[] egWhereClauseIds = {"Example_Where_Clause", "Update_By_Example_Where_Clause"};

		for(String byEgWhereClauseId : egWhereClauseIds)
		{
			Node ndChoose = (Node) xpath.evaluate("/mapper/sql[@id='" + byEgWhereClauseId + "']/where/foreach/if/trim/foreach/choose", docRoot, XPathConstants.NODE);

			//Single Value
			String strVal = (String) xpath.evaluate("count(when[@test=\"criterion.singleValue\"]/choose/when[@test=\"criterion.condition.toLowerCase().startsWith('ignoredcol ')\"])", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag found in singleValue for ignored column: ignoredCol", "0", strVal);

			//Between Value
			strVal = (String) xpath.evaluate("count(when[@test=\"criterion.betweenValue\"]/choose/when[@test=\"criterion.condition.toLowerCase().startsWith('ignoredcol ')\"])", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag found in betweenValue for ignored column: ignoredCol", "0", strVal);

			//List Value
			strVal = (String) xpath.evaluate("count(when[@test=\"criterion.listValue\"]/foreach/choose/when[@test=\"criterion.condition.toLowerCase().startsWith('ignoredcol ')\"])", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in listValue for ignored column: ignoredCol", "0", strVal);
		}
	}


	@Test
	public void testInsertUsers() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/insert[@id='insert']/text()[starts-with(normalize-space(.), 'insert into')]", docRoot, XPathConstants.STRING);

		sqlLine = sqlLine.toLowerCase();

		Assert.assertTrue("md5 function invocation missing from Insert", sqlLine.contains("md5(#{password,jdbctype=varchar}, 'us-ascii')"));
		Assert.assertFalse("ignored column present in Insert", sqlLine.contains("ignoredcol"));
	}


	@Test
	public void testInsertForColumnOverrideUsers() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/insert[@id='insert']/text()[starts-with(normalize-space(.), 'insert into')]", docRoot, XPathConstants.STRING);

		sqlLine = sqlLine.toLowerCase();

		Assert.assertTrue("soundex function invocation missing from Insert", sqlLine.contains("soundex(#{surnamesound,jdbctype=char})"));
	}


	@Test
	public void testInsertSelectiveUsers() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/insert[@id='insertSelective']/trim[@prefix='values (']/if[@test='password != null']", docRoot, XPathConstants.STRING);
		sqlLine = sqlLine.trim();

		Assert.assertEquals("md5 function invocation missing from Insert Selective", "md5(#{password,jdbcType=VARCHAR}, 'US-ASCII'),", sqlLine);


		sqlLine = (String) xpath.evaluate("count(/mapper/insert[@id='insertSelective']/trim[@prefix='values (']/if[@test='ignoredCol != null'])", docRoot, XPathConstants.STRING);
		Assert.assertEquals("ignored column present in Insert Selective", "0", sqlLine);
	}


	@Test
	public void testInsertSelectiveForColumnOverride() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/insert[@id='insertSelective']/trim[@prefix='values (']/if[@test='surnameSound != null']", docRoot, XPathConstants.STRING);

		sqlLine = sqlLine.trim();

		Assert.assertEquals("soundex function invocation missing from Insert Selective", "soundex(#{surnameSound,jdbcType=CHAR}),", sqlLine);
	}


	@Test
	public void testUpdateByExampleSelectiveUsers() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByExampleSelective']/set/if[@test='record.surnameSound != null']", docRoot, XPathConstants.STRING);

		sqlLine = sqlLine.trim();

		Assert.assertEquals("soundex function invocation missing from Update By Example Selective",
			"LAST_NAME_SDX = soundex(#{record.surnameSound,jdbcType=CHAR}),", sqlLine);



		sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByExampleSelective']/set/if[@test='record.password != null']", docRoot, XPathConstants.STRING);

		Assert.assertEquals("md5 function invocation missing from Update By Example Selective",
			"PASSWORD = md5(#{record.password,jdbcType=VARCHAR}, 'US-ASCII'),", sqlLine.trim());



		sqlLine = (String) xpath.evaluate("count(/mapper/update[@id='updateByExampleSelective']/set/if[@test='record.ignoredCol != null'])", docRoot, XPathConstants.STRING);
		Assert.assertEquals("ignored column present in Update By Example Selective", "0", sqlLine);
	}


	@Test
	public void testUpdateByExampleUsers() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByExample']/text()[starts-with(normalize-space(.), 'update ')]", docRoot, XPathConstants.STRING);

		sqlLine = sqlLine.trim();

		Assert.assertTrue("soundex function invocation missing from Update By Example",
			sqlLine.contains("LAST_NAME_SDX = soundex(#{record.surnameSound,jdbcType=CHAR}),"));

		Assert.assertTrue("md5 function invocation missing from Update By Example",
			sqlLine.contains("PASSWORD = md5(#{record.password,jdbcType=VARCHAR}, 'US-ASCII'),"));

		Assert.assertFalse("ignored column present in Update By Example", sqlLine.contains("ignoredCol"));
	}


	@Test
	public void testUpdateByPrimaryKeySelectiveUsers() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByPrimaryKeySelective']/set/if[@test='surnameSound != null']", docRoot, XPathConstants.STRING);

		sqlLine = sqlLine.trim();

		Assert.assertEquals("soundex function invocation missing from Update By Primary Key Selective",
			"LAST_NAME_SDX = soundex(#{surnameSound,jdbcType=CHAR}),", sqlLine);



		sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByPrimaryKeySelective']/set/if[@test='password != null']", docRoot, XPathConstants.STRING);

		Assert.assertEquals("md5 function invocation missing from Update By Primary Key Selective",
			"PASSWORD = md5(#{password,jdbcType=VARCHAR}, 'US-ASCII'),", sqlLine.trim());



		sqlLine = (String) xpath.evaluate("count(/mapper/update[@id='updateByPrimaryKeySelective']/set/if[@test='ignoredCol != null'])", docRoot, XPathConstants.STRING);

		Assert.assertEquals("ignored column present in Update By Primary Key Selective", "0", sqlLine);
	}


	@Test
	public void testUpdateByPrimaryKeyUsers() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByPrimaryKey']/text()[starts-with(normalize-space(.), 'update ')]", docRoot, XPathConstants.STRING);

		sqlLine = sqlLine.trim();

		Assert.assertTrue("soundex function invocation missing from Update By Primary Key",
			sqlLine.contains("LAST_NAME_SDX = soundex(#{surnameSound,jdbcType=CHAR}),"));

		Assert.assertTrue("md5 function invocation missing from Update By Primary Key",
			sqlLine.contains("PASSWORD = md5(#{password,jdbcType=VARCHAR}, 'US-ASCII'),"));

		Assert.assertFalse("ignored column present in Update By Primary Key", sqlLine.contains("ignoredCol"));
	}

	//End of testing Users



	//Start of testing UsersToSkills

	@Test
	public void testTableAliasInExampleWhereClausesForUsersToSkills() throws IOException, SAXException, XPathExpressionException
	{
		runTestsForByExampleWhereClauses("UsersToSkillsMapper.xml", "assocus.level", "aliased column: assocus.level",
			"and ${criterion.condition} left(trim(#{criterion.value}), 10)",
			"and ${criterion.condition} left(trim(#{criterion.value}), 10) and left(trim(#{criterion.secondValue}), 10)",
			"left(trim(#{listItem}), 10)");
	}

	@Test
	public void testInsertUsersToSkillsAlias() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersToSkillsMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/insert[@id='insert']/text()[starts-with(normalize-space(.), 'insert into')]", docRoot, XPathConstants.STRING);

		Assert.assertTrue("left & trim function invocations missing from Insert", sqlLine.contains("left(trim(#{level,jdbcType=VARCHAR}), 10)"));
	}


	@Test
	public void testInsertSelectiveUsersToSkillsWithAlias() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersToSkillsMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/insert[@id='insertSelective']/trim[@prefix='values (']/if[@test='level != null']", docRoot, XPathConstants.STRING);
		sqlLine = sqlLine.trim();

		Assert.assertEquals("left & trim function invocations missing from Insert Selective", "left(trim(#{level,jdbcType=VARCHAR}), 10),", sqlLine);
	}


	@Test
	public void testUpdateByExampleSelectiveUsersToSkillsWithAlias() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersToSkillsMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByExampleSelective']/set/if[@test='record.level != null']", docRoot, XPathConstants.STRING);

		Assert.assertEquals("left & trim function invocations missing from Update By Example Selective",
			"AssocUS.LEVEL = left(trim(#{record.level,jdbcType=VARCHAR}), 10),", sqlLine.trim());
	}


	@Test
	public void testUpdateByExampleUsersToSkillsWithAlias() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersToSkillsMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByExample']/text()[starts-with(normalize-space(.), 'update ')]", docRoot, XPathConstants.STRING);

		Assert.assertTrue("left & trim function invocations missing from Update By Example",
			sqlLine.contains("AssocUS.LEVEL = left(trim(#{record.level,jdbcType=VARCHAR}), 10)"));
	}


	@Test
	public void testUpdateByPrimaryKeySelectiveUsersToSkillsWithAlias() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersToSkillsMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByPrimaryKeySelective']/set/if[@test='level != null']", docRoot, XPathConstants.STRING);

		Assert.assertEquals("left & trim function invocations missing from Update By Primary Key Selective",
			"LEVEL = left(trim(#{level,jdbcType=VARCHAR}), 10),", sqlLine.trim());
	}


	@Test
	public void testUpdateByPrimaryKeyUsersToSkillsWithAlias() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UsersToSkillsMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByPrimaryKey']/text()[starts-with(normalize-space(.), 'update ')]", docRoot, XPathConstants.STRING);

		sqlLine = sqlLine.trim();

		Assert.assertTrue("left & trim function invocations missing from Update By Primary Key",
			sqlLine.contains("LEVEL = left(trim(#{level,jdbcType=VARCHAR}), 10)"));
	}

	//End of testing UsersToSkills



	//Start of testing UserPhotos

	@Test
	public void testExampleWhereClausesForUserPhotos() throws IOException, SAXException, XPathExpressionException
	{
		runTestsForByExampleWhereClauses("UserPhotosMapper.xml", "photo_title", "photo_title column",
			"and ${criterion.condition} concat(substr(#{criterion.value}, 4, length(#{criterion.value})), '-PHOTO')",
			"and ${criterion.condition} concat(substr(#{criterion.value}, 4, length(#{criterion.value})), '-PHOTO') and concat(substr(#{criterion.secondValue}, 4, length(#{criterion.secondValue})), '-PHOTO')",
			"concat(substr(#{listItem}, 4, length(#{listItem})), '-PHOTO')");
	}


	@Test
	public void testInsertUserPhotos() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserPhotosMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/insert[@id='insert']/text()[starts-with(normalize-space(.), 'insert into')]", docRoot, XPathConstants.STRING);

		Assert.assertTrue("concat & substr function invocations missing from Insert",
			sqlLine.contains("concat(substr(#{photoTitle,jdbcType=VARCHAR}, 4, length(#{photoTitle,jdbcType=VARCHAR})), '-PHOTO')"));
	}


	@Test
	public void testInsertSelectiveUserPhotos() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserPhotosMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/insert[@id='insertSelective']/trim[@prefix='values (']/if[@test='photoTitle != null']", docRoot, XPathConstants.STRING);
		sqlLine = sqlLine.trim();

		Assert.assertEquals("concat & substr function invocations missing from Insert Selective",
			"concat(substr(#{photoTitle,jdbcType=VARCHAR}, 4, length(#{photoTitle,jdbcType=VARCHAR})), '-PHOTO'),", sqlLine);
	}


	@Test
	public void testUpdateByExampleSelectiveUserPhotos() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserPhotosMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByExampleSelective']/set/if[@test='record.photoTitle != null']", docRoot, XPathConstants.STRING);

		Assert.assertEquals("concat & substr function invocations missing from Update By Example Selective",
			"PHOTO_TITLE = concat(substr(#{record.photoTitle,jdbcType=VARCHAR}, 4, length(#{record.photoTitle,jdbcType=VARCHAR})), '-PHOTO'),", sqlLine.trim());
	}


	@Test
	public void testUpdateByExampleUserPhotos() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserPhotosMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByExample']/text()[starts-with(normalize-space(.), 'update ')]", docRoot, XPathConstants.STRING);

		Assert.assertTrue("concat & substr function invocations missing from Update By Example",
			sqlLine.contains("PHOTO_TITLE = concat(substr(#{record.photoTitle,jdbcType=VARCHAR}, 4, length(#{record.photoTitle,jdbcType=VARCHAR})), '-PHOTO')"));
	}


	@Test
	public void testUpdateByExampleWithBlobsUserPhotos() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserPhotosMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByExampleWithBLOBs']/text()[starts-with(normalize-space(.), 'update ')]", docRoot, XPathConstants.STRING);

		Assert.assertTrue("concat & substr function invocations missing from Update By Example",
			sqlLine.contains("PHOTO_TITLE = concat(substr(#{record.photoTitle,jdbcType=VARCHAR}, 4, length(#{record.photoTitle,jdbcType=VARCHAR})), '-PHOTO')"));
	}

	//End of testing UserPhotos



	//Start of testing UserTutorial

	@Test
	public void testExampleWhereClausesForUserTutorial() throws IOException, SAXException, XPathExpressionException
	{
		runTestsForByExampleWhereClauses("UserTutorialMapper.xml", "summary", "summary column",
			"and ${criterion.condition} concat(left(#{criterion.value}, 20), '...')",
			"and ${criterion.condition} concat(left(#{criterion.value}, 20), '...') and concat(left(#{criterion.secondValue}, 20), '...')",
			"concat(left(#{listItem}, 20), '...')");
	}

	@Test
	public void testInsertUserTutorial() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserTutorialMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/insert[@id='insert']/text()[starts-with(normalize-space(.), 'insert into')]", docRoot, XPathConstants.STRING);

		Assert.assertTrue("concat & left function invocations missing from Insert", sqlLine.contains("concat(left(#{summary,jdbcType=CLOB}, 20), '...')"));
	}


	@Test
	public void testInsertSelectiveUserTutorial() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserTutorialMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/insert[@id='insertSelective']/trim[@prefix='values (']/if[@test='summary != null']", docRoot, XPathConstants.STRING);
		sqlLine = sqlLine.trim();

		Assert.assertEquals("concat & left function invocations missing from Insert Selective", "concat(left(#{summary,jdbcType=CLOB}, 20), '...'),", sqlLine);
	}



	@Test
	public void testUpdateByExampleSelectiveUserTutorial() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserTutorialMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByExampleSelective']/set/if[@test='record.summary != null']", docRoot, XPathConstants.STRING);

		Assert.assertEquals("concat & left function invocations missing from Update By Example Selective",
			"SUMMARY = concat(left(#{record.summary,jdbcType=CLOB}, 20), '...'),", sqlLine.trim());
	}


	@Test
	public void testUpdateByExampleWithBLOBsUserTutorial() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserTutorialMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByExampleWithBLOBs']/text()[starts-with(normalize-space(.), 'update ')]", docRoot, XPathConstants.STRING);

		Assert.assertTrue("concat & left function invocations missing from Update By Example",
			sqlLine.contains("SUMMARY = concat(left(#{record.summary,jdbcType=CLOB}, 20), '...')"));
	}


	@Test
	public void testUpdateByExampleUserTutorial() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserTutorialMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByExample']/text()[starts-with(normalize-space(.), 'update ')]", docRoot, XPathConstants.STRING);

		Assert.assertFalse("concat & left function invocations missing from Update By Example",
			sqlLine.contains("SUMMARY = concat(left(#{record.summary,jdbcType=CLOB}, 20), '...')"));
	}


	@Test
	public void testUpdateByPrimaryKeySelectiveUserTutorial() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserTutorialMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByPrimaryKeySelective']/set/if[@test='summary != null']", docRoot, XPathConstants.STRING);

		Assert.assertEquals("concat & left function invocations missing from Update By Primary Key Selective",
			"SUMMARY = concat(left(#{summary,jdbcType=CLOB}, 20), '...'),", sqlLine.trim());
	}


	@Test
	public void testUpdateByPrimaryKeyWithBLOBsUserTutorial() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserTutorialMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByPrimaryKeyWithBLOBs']/text()[starts-with(normalize-space(.), 'update ')]", docRoot, XPathConstants.STRING);

		Assert.assertTrue("concat & left function invocations present in Update By Primary Key",
			sqlLine.contains("SUMMARY = concat(left(#{summary,jdbcType=CLOB}, 20), '...')"));
	}


	@Test
	public void testUpdateByPrimaryKeyUserTutorial() throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/UserTutorialMapper.xml").toExternalForm());

		String sqlLine = (String) xpath.evaluate("/mapper/update[@id='updateByPrimaryKey']/text()[starts-with(normalize-space(.), 'update ')]", docRoot, XPathConstants.STRING);

		Assert.assertFalse("concat & left function invocations present in Update By Primary Key",
			sqlLine.contains("SUMMARY = concat(left(#{summary,jdbcType=CLOB}, 20), '...')"));
	}

	//End of testing UsersTutorial




	private void runTestsForByExampleWhereClauses(String mapperFileName, String colNm, String errMsgForCol,
		String expectedSingleVal, String expectedBetweenVal, String expectedListVal) throws IOException, SAXException, XPathExpressionException
	{
		Document docRoot = docBldr.parse(this.getClass().getResource("/conditional/sqlMaps/" + mapperFileName).toExternalForm());

		String[] egWhereClauseIds = {"Example_Where_Clause", "Update_By_Example_Where_Clause"};

		for(String byEgWhereClauseId : egWhereClauseIds)
		{
			Node ndChoose = (Node) xpath.evaluate("/mapper/sql[@id='" + byEgWhereClauseId + "']/where/foreach/if/trim/foreach/choose", docRoot, XPathConstants.NODE);

			//No value
			String strTxt = (String) xpath.evaluate("when[@test='criterion.noValue']", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " the criterion.noValue tag is wrong.", "and ${criterion.condition}", strTxt.trim());

			//Single Value
			String strCnt = (String) xpath.evaluate("count(when[@test='criterion.singleValue']/choose/*)", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Wrong number of child tags under when[@test='criterion.singleValue']/choose tag",
				"2", strCnt);

			String strVal = (String) xpath.evaluate("when[@test=\"criterion.singleValue\"]/choose/when[@test=\"criterion.condition.toLowerCase().startsWith('" + colNm +" ')\"]", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in singleValue for " + errMsgForCol,
				expectedSingleVal, strVal.trim());

			strVal = (String) xpath.evaluate("when[@test='criterion.singleValue']/choose/otherwise", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in singleValue for otherwise",
				"and ${criterion.condition} #{criterion.value}", strVal.trim());

			//Between Value
			strCnt = (String) xpath.evaluate("count(when[@test='criterion.betweenValue']/choose/*)", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Wrong number of child tags under when[@test='criterion.betweenValue']/choose tag",
					"2", strCnt);

			strVal = (String) xpath.evaluate("when[@test=\"criterion.betweenValue\"]/choose/when[@test=\"criterion.condition.toLowerCase().startsWith('" + colNm + " ')\"]", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in betweenValue for " + errMsgForCol,
				expectedBetweenVal, strVal.trim());

			strVal = (String) xpath.evaluate("when[@test='criterion.betweenValue']/choose/otherwise", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in betweenValue for otherwise",
				"and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}", strVal.trim());

			//List Value
			strCnt = (String) xpath.evaluate("count(when[@test='criterion.listValue']/foreach/choose/*)", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Wrong number of child node for listValue tag", "2", strCnt);

			strVal = (String) xpath.evaluate("when[@test=\"criterion.listValue\"]/foreach/choose/when[@test=\"criterion.condition.toLowerCase().startsWith('" + colNm + " ')\"]", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in listValue for " + errMsgForCol,
				expectedListVal, strVal.trim());

			strVal = (String) xpath.evaluate("when[@test='criterion.listValue']/foreach/choose/otherwise", ndChoose, XPathConstants.STRING);
			Assert.assertEquals("In " + byEgWhereClauseId + " Incorrect tag value in listValue for otherwise",
				"#{listItem}", strVal.trim());
		}
	}
}
