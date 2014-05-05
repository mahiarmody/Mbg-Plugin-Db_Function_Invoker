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


package org.mybatis.generator.hierarchical.plugins.dbFunctions;


import java.io.Reader;
import java.io.IOException;

import java.util.List;

import hierarchical.modelDto.*; //these are our mbg generated compiled model classes & xxxByExample classes that we want to test.
import hierarchical.clientDao.*;

import java.text.DateFormat;
import java.text.ParseException;

import org.mybatis.generator.DbFunctionInvocationTestUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;

import org.junit.runner.RunWith;

import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.io.Resources;


/**
This class is used to test the whether or not the actual database function invocations
are made by the MyBatis Generator generated artifacts. This class will test the
select, insert, update, delete, and count methods of the generated artifacts.

@author Mahiar Mody
*/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4.class)
public class UsersMultiColsCrudTest
{
	private static SqlSessionFactory sqlSessionFactory;
	private static DateFormat df;


	@BeforeClass
	public static void setUpBeforeClass() throws IOException
	{
		// create a SqlSessionFactory
		Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		reader.close();

		df = DateFormat.getDateInstance(DateFormat.MEDIUM);

		DbFunctionInvocationTestUtils.resetIdentityValues();
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		sqlSessionFactory = null;
	}



	@Test
	public void test01BothInserts() throws ParseException
	{
		Users user1 = new Users();
		user1.setLogin("bdoe");
		user1.setFirstName("Bill");
		user1.setLastName("Doe");
		user1.setSurnameSound("Doe");
		user1.setPassword("B0H^3387");

		Users user2 = new Users();
		user2.setLogin("jbr");
		user2.setFirstName("Jill");
		user2.setLastName("Brown");
		user2.setSurnameSound("Brown");
		user2.setPassword("JBR^3387");
		user2.setIsActive(Boolean.FALSE);
		user2.setCreateDate(df.parse("Mar 21, 1857"));

		int[] cnt = {0,0};

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);

			cnt[0] = mapper.insertSelective(user1);

			cnt[1] = mapper.insert(user2);

			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("Number of Users records created using insert selective is wrong", 1, cnt[0]);
		Assert.assertEquals("Number of Users records created using insert is wrong", 1, cnt[1]);

		Assert.assertEquals("Incorrect user Id created for insert selective.", Integer.valueOf(2), user1.getUserId());
		Assert.assertEquals("Incorrect user Id created for plain insert.", Integer.valueOf(3), user2.getUserId());
	}


	@Test
	public void test05SelectByExample()
	{
		UsersExample ex = new UsersExample();
		UsersExample.Criteria crit = ex.createCriteria();

		//Rec 1. created manually in db.
		crit.andUserIdEqualTo(Integer.valueOf(1));
		crit.andPasswordEqualTo("!@#$");
		crit.andSurnameSoundEqualTo("Mody");

		List<Users> lstUsrs = getUsersRecords(ex);

		Assert.assertEquals("Too many matching users returned. Only 1 should have been returned for PK1", 1, lstUsrs.size());
		Assert.assertNotNull("Users with PK=1 not found.", lstUsrs.get(0));
		Assert.assertEquals("Incorrect user login for PK1", "mmody", lstUsrs.get(0).getLogin());
		Assert.assertEquals("Incorrect user surname sound for PK1", "M300", lstUsrs.get(0).getSurnameSound());
		Assert.assertEquals("Incorrect user password hash for PK1", "3a4d92a1200aad406ac50377c7d863aa", lstUsrs.get(0).getPassword());


		//Rec 2. created by insert selective
		ex.clear();
		crit = ex.createCriteria();
		crit.andSurnameSoundEqualTo("Doe");
		crit.andPasswordEqualTo("B0H^3387");

		lstUsrs = getUsersRecords(ex);

		Assert.assertEquals("Too many matching users returned. Only 1 should have been returned for PK2", 1, lstUsrs.size());
		Assert.assertNotNull("Users with PK=2 not found.", lstUsrs.get(0));
		Assert.assertEquals("Incorrect user login created for PK2", "bdoe", lstUsrs.get(0).getLogin());
		Assert.assertEquals("Incorrect user surname sound created for PK2", "D000", lstUsrs.get(0).getSurnameSound());
		Assert.assertEquals("Incorrect user password hash created for PK2", "bb6db0a80c3c906a00b6eff0d6a78ca3", lstUsrs.get(0).getPassword());


		//Rec 3. created by plain insert
		ex.clear();
		crit = ex.createCriteria();
		crit.andSurnameSoundEqualTo("Brown");
		crit.andPasswordEqualTo("JBR^3387");

		lstUsrs = getUsersRecords(ex);

		Assert.assertEquals("Too many matching users returned. Only 1 should have been returned for PK3", 1, lstUsrs.size());
		Assert.assertNotNull("Users with PK=3 not found.", lstUsrs.get(0));
		Assert.assertEquals("Incorrect user login created for PK3", "jbr", lstUsrs.get(0).getLogin());
		Assert.assertEquals("Incorrect user surname sound created for PK3", "B650", lstUsrs.get(0).getSurnameSound());
		Assert.assertEquals("Incorrect user password hash created for PK3", "17c85a1992afd734682098c3dca62def", lstUsrs.get(0).getPassword());
		Assert.assertEquals("Incorrect user status created for PK3", Boolean.FALSE, lstUsrs.get(0).getIsActive());
		Assert.assertEquals("Incorrect user Creation Date for PK3", "Mar 21, 1857", df.format(lstUsrs.get(0).getCreateDate()));


		//Negative case
		ex.clear();
		ex.or().andUserIdEqualTo(1)
			.andSurnameSoundEqualTo("Brown"); //same as making only an "and" criteria with userId=1 and surnameSound = 'Brown'.

		lstUsrs = getUsersRecords(ex);

		Assert.assertTrue("No rows should have matched this select by example criterion, but some did", lstUsrs.size() == 0);
	}


	@Test
	public void test10SelectByPrimaryKey()
	{
		Users[] usrArr = new Users[2];
		UsersKey[] pkArr = {new UsersKey(), new UsersKey()};

		pkArr[0].setUserId(Integer.valueOf(3));
		pkArr[1].setUserId(Integer.valueOf(4));

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);
			usrArr[0] = mapper.selectByPrimaryKey(pkArr[0]);
			usrArr[1] = mapper.selectByPrimaryKey(pkArr[1]);
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertNotNull("Users with PK=3 is null.", usrArr[0]);
		Assert.assertEquals("For with PK=3 incorrect login", "jbr", usrArr[0].getLogin());
		Assert.assertEquals("For with PK=3 incorrect first name", "Jill", usrArr[0].getFirstName());
		Assert.assertEquals("For with PK=3 incorrect last name", "Brown", usrArr[0].getLastName());
		Assert.assertEquals("For with PK=3 incorrect surname sound", "B650", usrArr[0].getSurnameSound());
		Assert.assertEquals("For with PK=3 incorrect password", "17c85a1992afd734682098c3dca62def", usrArr[0].getPassword());
		Assert.assertEquals("For with PK=3 incorrect status", Boolean.FALSE, usrArr[0].getIsActive());
		Assert.assertEquals("For with PK=3 incorrect create date", "Mar 21, 1857", df.format(usrArr[0].getCreateDate()));

		Assert.assertNull("Users with PK=4 exists, but should not.", usrArr[1]);
	}


	@Test
	public void test15CountByExample()
	{
		UsersExample ex = new UsersExample();

		//Make a single OR clause.
		ex.or().andUserIdEqualTo(Integer.valueOf(1));
		ex.or().andSurnameSoundEqualTo("Doe");
		ex.or().andPasswordEqualTo("JBR^3387");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);
			cnt = mapper.countByExample(ex);
		}
		finally
		{
			sqlSession.close();
		}
		
		Assert.assertTrue("countByExample - Counts in Db are incorrect", cnt==3);
	}


	@Test
	public void test20UpdateByExampleSelective()
	{
		Users recNewValues = new Users();
		recNewValues.setFirstName("Jannet");
		recNewValues.setSurnameSound("Wood");
		recNewValues.setPassword("TriPP3#");

		UsersExample ex = new UsersExample();
		ex.or().andUserIdEqualTo(Integer.valueOf(3)); //The userId of the record to be updated.

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);
			cnt = mapper.updateByExampleSelective(recNewValues, ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("Users UpdateByExampleSelective - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("Users UpdateByExampleSelective failed", isUserUpdateSuccessful(3, "Jannet", "W300",
			"693a7f09c14a15ecfbcdbe86d7568724"));
	}



	@Test
	@Ignore
	/**
	Cannot perform this test because of the limitations of the hsqldb database.
	HSQLDB requires that:
	When a row is inserted into a table, or an existing row is updated, no value except
	DEFAULT can be specified for a generated column.
	The MBG generated sql mapper file does not specify DEFAULT for the generated column,
	namely user_id, so when the sql update statement tries to update the user_id column
	with an Integer, the update statement to fail with a SQLException.
	See: http://hsqldb.org/doc/guide/databaseobjects-chapt.html#dbc_table_creation
	*/
	public void test25UpdateByExample()
	{
		UsersExample ex = new UsersExample();
		ex.or().andUserIdEqualTo(Integer.valueOf(3)); //The userId of the record to be updated.

		Users recNewValues = getUsersRecords(ex).get(0);
		recNewValues.setSurnameSound("Thompson");
		recNewValues.setPassword("FT0pn114");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);
			cnt = mapper.updateByExample(recNewValues, ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("Users UpdateByExample - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("Users UpdateByExample failed", isUserUpdateSuccessful(3, "Jannet", "T512",
			"a1b8a90aaccf675828ac5d1fac007fe1"));
	}



	@Test
	public void test30UpdateByPrimaryKeySelective()
	{
		Users recNewValues = new Users();
		recNewValues.setUserId(3); //required to set PK for updateByPrimaryKeySelective
		recNewValues.setSurnameSound("Simpsons");
		recNewValues.setPassword("SS55");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);
			cnt = mapper.updateByPrimaryKeySelective(recNewValues);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("Users updateByPrimaryKeySelective - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("Users UpdateByPrimaryKeySelective failed", isUserUpdateSuccessful(3, "Jannet", "S512",
			"edc8d2cbc8ae721e9b628848a6f6d8b6"));
	}


	@Test
	public void test35UpdateByPrimaryKey()
	{
		UsersExample ex = new UsersExample();
		ex.or().andUserIdEqualTo(Integer.valueOf(3)); //The userId of the record to be updated.

		Users recNewValues = getUsersRecords(ex).get(0);

		recNewValues.setSurnameSound("Green");
		recNewValues.setPassword("G384");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);
			cnt = mapper.updateByPrimaryKey(recNewValues);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("Users UpdateByPrimaryKey - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("Users UpdateByPrimaryKey failed", isUserUpdateSuccessful(3, "Jannet", "G650",
			"8a7b3bcdfc09de6b89a2bfdb09c641b9"));
	}



	@Test
	public void test40DeleteByExample()
	{
		UsersExample ex = new UsersExample();
		ex.or().andSurnameSoundEqualTo("Doe");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);
			cnt = mapper.deleteByExample(ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}
		
		Assert.assertTrue("Users deleteByExample - Cnt of deleted records should be 1", cnt==1);
	}


	@Test
	public void test45DeleteByPrimaryKey()
	{
		UsersKey usersKeyPk = new UsersKey();
		usersKeyPk.setUserId(Integer.valueOf(3));

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);
			cnt = mapper.deleteByPrimaryKey(usersKeyPk);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}
		
		Assert.assertTrue("User deleteByPK - Cnt of deleted records should be 1", cnt==1);
	}



	private List<Users> getUsersRecords(UsersExample ex)
	{
		List<Users> lstUsers = null;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);
			lstUsers = mapper.selectByExample(ex);
		}
		finally
		{
			sqlSession.close();
		}

		return lstUsers;
	}


	private boolean isUserUpdateSuccessful(int userId, String firstName, String soundexVal, String passwordHash)
	{
		UsersExample ex = new UsersExample();
		ex.or().andUserIdEqualTo(Integer.valueOf(userId));

		Users usr = getUsersRecords(ex).get(0);

		return firstName.equals(usr.getFirstName()) && soundexVal.equals(usr.getSurnameSound()) && passwordHash.equals(usr.getPassword());
	}
}
