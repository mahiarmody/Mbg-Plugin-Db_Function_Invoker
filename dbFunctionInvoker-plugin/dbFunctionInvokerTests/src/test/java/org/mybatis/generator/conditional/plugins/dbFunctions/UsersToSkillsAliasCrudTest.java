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


import java.io.Reader;
import java.io.IOException;

import java.util.List;

import conditional.modelDto.*; //these are our mbg generated compiled model classes & xxxByExample classes that we want to test.
import conditional.clientDao.*;

import org.junit.Assert;
import org.junit.Test;
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
public class UsersToSkillsAliasCrudTest
{
	private static SqlSessionFactory sqlSessionFactory;


	@BeforeClass
	public static void setUpBeforeClass() throws IOException
	{
		// create a SqlSessionFactory
		Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		reader.close();
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		sqlSessionFactory = null;
	}

	@Test
	public void test01BothInserts()
	{
		UsersToSkills utsSelective = new UsersToSkills();
		utsSelective.setUserId(Integer.valueOf(1)); //mahiar
		utsSelective.setSkillId(Short.valueOf((short)2)); //postgresql

		UsersToSkills utsPlain = new UsersToSkills();
		utsPlain.setUserId(Integer.valueOf(1)); //mahiar
		utsPlain.setSkillId(Short.valueOf((short)3)); //mybatis
		utsPlain.setLevel(" Outstanding  "); //explicitly specified.

		int[] cnt = {0,0};

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UsersToSkillsMapper mapper = sqlSession.getMapper(UsersToSkillsMapper.class);

			cnt[0] = mapper.insertSelective(utsSelective);

			cnt[1] = mapper.insert(utsPlain);

			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("Number of UsersToSkills records created using insert selective is wrong", 1, cnt[0]);
		Assert.assertEquals("Number of UsersToSkills records created using insert is wrong", 1, cnt[1]);
	}


	@Test
	public void test05SelectByExample()
	{
		UsersToSkillsExample ex = new UsersToSkillsExample();

		//Rec 1. created manually in db.
		ex.or().andUserIdEqualTo(Integer.valueOf(1))
			.andSkillIdEqualTo(Short.valueOf((short)1))
			.andLevelEqualTo("Excellent"); //a single AND condition.

		List<UsersToSkills> lstUsrsToSkills = getUsersToSkillsRecords(ex);

		Assert.assertEquals("Too many matching UsersToSkills returned. Only 1 should have been returned for PK1,1", 1, lstUsrsToSkills.size());
		Assert.assertNotNull("UsersToSkills with PK=1,1 not found.", lstUsrsToSkills.get(0));
		Assert.assertEquals("Incorrect skill level for PK1,1", "Excellent", lstUsrsToSkills.get(0).getLevel());


		//Rec 2. created by insert selective
		ex.clear();
		ex.or().andUserIdEqualTo(Integer.valueOf(1))
			.andSkillIdEqualTo(Short.valueOf((short)2)); // postgresql

		lstUsrsToSkills = getUsersToSkillsRecords(ex);

		Assert.assertEquals("Too many matching UsersToSkills returned. Only 1 should have been returned for PK1,2", 1, lstUsrsToSkills.size());
		Assert.assertNotNull("UsersToSkills with PK=1,2 not found.", lstUsrsToSkills.get(0));
		Assert.assertEquals("Incorrect skill level created for PK1,2", "Unknown", lstUsrsToSkills.get(0).getLevel());


		//Rec 3. created by plain insert
		ex.clear();		
		ex.or().andUserIdEqualTo(Integer.valueOf(1)) //mahiar
			.andSkillIdEqualTo(Short.valueOf((short)3)); // MyBatis

		lstUsrsToSkills = getUsersToSkillsRecords(ex);

		Assert.assertEquals("Too many matching UsersToSkills returned. Only 1 should have been returned for PK1,3", 1, lstUsrsToSkills.size());
		Assert.assertNotNull("UsersToSkills with PK=1,3 not found.", lstUsrsToSkills.get(0));
		Assert.assertEquals("Incorrect skill level created for PK1,3", "Outstandin", lstUsrsToSkills.get(0).getLevel());


		//Negative case
		ex.clear();
		ex.or().andUserIdEqualTo(Integer.valueOf(1))
			.andSkillIdEqualTo(Short.valueOf((short)4)); // invalid

		lstUsrsToSkills = getUsersToSkillsRecords(ex);

		Assert.assertTrue("No rows should have matched this select by example criterion, but some did", lstUsrsToSkills.size() == 0);
	}


	@Test
	public void test10SelectByPrimaryKey()
	{
		UsersToSkillsKey[] pk = {new UsersToSkillsKey(), new UsersToSkillsKey()};
		pk[0].setUserId(Integer.valueOf(1));
		pk[0].setSkillId(Short.valueOf((short)3));
		pk[1].setUserId(Integer.valueOf(1));
		pk[1].setSkillId(Short.valueOf((short)4));

		UsersToSkills[] usrToSkArr = new UsersToSkills[2];

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UsersToSkillsMapper mapper = sqlSession.getMapper(UsersToSkillsMapper.class);
			usrToSkArr[0] = mapper.selectByPrimaryKey(pk[0]);
			usrToSkArr[1] = mapper.selectByPrimaryKey(pk[1]);
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertNotNull("UsersToSkills with PK=1,3 is null.", usrToSkArr[0]);
		Assert.assertEquals("For with PK=1,3 incorrect level", "Outstandin", usrToSkArr[0].getLevel());
		Assert.assertEquals("For with PK=1,3 incorrect UserId", Integer.valueOf(1), usrToSkArr[0].getUserId());
		Assert.assertEquals("For with PK=1,3 incorrect skillId", Short.valueOf((short)3), usrToSkArr[0].getSkillId());

		Assert.assertNull("UsersToSkills with PK=1,4 exists, but should not.", usrToSkArr[1]);
	}


	@Test
	public void test15CountByExample()
	{
		UsersToSkillsExample ex = new UsersToSkillsExample();

		//Make a single OR clause.
		ex.or().andLevelEqualTo("Excellent");
		ex.or().andLevelEqualTo("Incorrect Value");
		ex.or().andLevelEqualTo("Outstandin");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UsersToSkillsMapper mapper = sqlSession.getMapper(UsersToSkillsMapper.class);
			cnt = mapper.countByExample(ex);
		}
		finally
		{
			sqlSession.close();
		}
		
		Assert.assertTrue("countByExample - Counts in Db are incorrect", cnt==2);
	}


	@Test
	public void test20UpdateByExampleSelective()
	{
		UsersToSkills recNewValues = new UsersToSkills();
		recNewValues.setLevel("just too good");

		UsersToSkillsExample ex = new UsersToSkillsExample();
		ex.or().andUserIdEqualTo(Integer.valueOf(1))
			.andSkillIdEqualTo(Short.valueOf((short)3)); //The userId,skillId combination of the record to be updated.

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UsersToSkillsMapper mapper = sqlSession.getMapper(UsersToSkillsMapper.class);
			cnt = mapper.updateByExampleSelective(recNewValues, ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("UsersToSkills UpdateByExampleSelective - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("UsersToSkills UpdateByExampleSelective failed", isUpdateSuccessful(1, (short)3, "just too g"));
	}



	@Test
	public void test25UpdateByExample()
	{
		UsersToSkillsExample ex = new UsersToSkillsExample();
		ex.or().andUserIdEqualTo(Integer.valueOf(1))
			.andSkillIdEqualTo(Short.valueOf((short)3)); //The userId,shortId combination of the record to be updated.

		UsersToSkills recNewValues = getUsersToSkillsRecords(ex).get(0);
		recNewValues.setLevel("poor");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UsersToSkillsMapper mapper = sqlSession.getMapper(UsersToSkillsMapper.class);
			cnt = mapper.updateByExample(recNewValues, ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("UsersToSkills UpdateByExample - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("UsersToSkills UpdateByExample failed", isUpdateSuccessful(1, (short)3, "poor"));
	}



	@Test
	public void test30UpdateByPrimaryKeySelective()
	{
		UsersToSkills recNewValues = new UsersToSkills();
		recNewValues.setUserId(Integer.valueOf(1)); //required to set PK combo for updateByPrimaryKeySelective
		recNewValues.setSkillId(Short.valueOf((short)3)); //required to set PK combo for updateByPrimaryKeySelective
		recNewValues.setLevel("Nice");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UsersToSkillsMapper mapper = sqlSession.getMapper(UsersToSkillsMapper.class);
			cnt = mapper.updateByPrimaryKeySelective(recNewValues);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("UsersToSkills updateByPrimaryKeySelective - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("UsersToSkills UpdateByPrimaryKeySelective failed", isUpdateSuccessful(1, (short)3, "Nice"));
	}


	@Test
	public void test35UpdateByPrimaryKey()
	{
		UsersToSkills recNewValues = new UsersToSkills();
		recNewValues.setUserId(Integer.valueOf(1));
		recNewValues.setSkillId(Short.valueOf((short)3));
		recNewValues.setLevel("Okish");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UsersToSkillsMapper mapper = sqlSession.getMapper(UsersToSkillsMapper.class);
			cnt = mapper.updateByPrimaryKey(recNewValues);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("UsersToSkills UpdateByPrimaryKey - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("UsersToSkills UpdateByPrimaryKey failed", isUpdateSuccessful(1, (short)3, "Okish"));
	}



	@Test
	public void test40DeleteByExample()
	{
		UsersToSkillsExample ex = new UsersToSkillsExample();
		ex.or().andLevelEqualTo("Okish");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UsersToSkillsMapper mapper = sqlSession.getMapper(UsersToSkillsMapper.class);
			cnt = mapper.deleteByExample(ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}
		
		Assert.assertTrue("UsersToSkills deleteByExample - Cnt of deleted records should be 1", cnt==1);
	}


	@Test
	public void test45DeleteByPrimaryKey()
	{
		UsersToSkillsKey pk = new UsersToSkillsKey();
		pk.setUserId(Integer.valueOf(1));
		pk.setSkillId(Short.valueOf((short)2));

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UsersToSkillsMapper mapper = sqlSession.getMapper(UsersToSkillsMapper.class);
			cnt = mapper.deleteByPrimaryKey(pk);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}
		
		Assert.assertTrue("User deleteByPK - Cnt of deleted records should be 1", cnt==1);
	}


	private List<UsersToSkills> getUsersToSkillsRecords(UsersToSkillsExample ex)
	{
		List<UsersToSkills> lstUsersToSkills = null;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UsersToSkillsMapper mapper = sqlSession.getMapper(UsersToSkillsMapper.class);
			lstUsersToSkills = mapper.selectByExample(ex);
		}
		finally
		{
			sqlSession.close();
		}

		return lstUsersToSkills;
	}


	private boolean isUpdateSuccessful(int userId, short skillId, String level)
	{
		UsersToSkillsExample ex = new UsersToSkillsExample();
		ex.or().andUserIdEqualTo(Integer.valueOf(userId))
			.andSkillIdEqualTo(Short.valueOf(skillId));

		UsersToSkills usrSkill = getUsersToSkillsRecords(ex).get(0);

		return level.equals(usrSkill.getLevel());
	}
}
