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


package org.mybatis.generator.flat.plugins.dbFunctions;


import java.io.Reader;
import java.io.IOException;

import java.util.List;

import flat.modelDto.*; //these are our mbg generated compiled model classes & xxxByExample classes that we want to test.
import flat.clientDao.*;

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
public class UserTutorialLobCrudTest
{
	private static SqlSessionFactory sqlSessionFactory;
	private static byte[] binVideo;


	@BeforeClass
	public static void setUpBeforeClass() throws IOException
	{
		// create a SqlSessionFactory
		Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		reader.close();

		binVideo = new byte[]{0,1,2,3};
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		sqlSessionFactory = null;
	}



	@Test
	public void test01BothInserts()
	{
		UserTutorial usrTutBlbSelective = new UserTutorial();
		usrTutBlbSelective.setUserTutorialId(Integer.valueOf(2));
		usrTutBlbSelective.setUserId(Integer.valueOf(1));
		usrTutBlbSelective.setTitle("2nd Tutorial");
		usrTutBlbSelective.setSummary("The summary of 2nd tutorial");
		usrTutBlbSelective.setNarrative("Linux is the way to go!");

		UserTutorial usrTutBlbPlain = new UserTutorial();
		usrTutBlbPlain.setUserTutorialId(Integer.valueOf(3));
		usrTutBlbPlain.setUserId(Integer.valueOf(1));
		usrTutBlbPlain.setTitle("Third Tutorial");
		usrTutBlbPlain.setSummary("Must be at least 20 characters long");
		usrTutBlbPlain.setNarrative("I'll teach you how to use the db invoker plugin");
		usrTutBlbPlain.setVideo(binVideo);
		usrTutBlbPlain.setVideoType("mp4");

		int[] cnt = {0,0};

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);

			cnt[0] = mapper.insertSelective(usrTutBlbSelective);

			cnt[1] = mapper.insert(usrTutBlbPlain);

			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("Number of UserTutorial records created using insert selective is wrong", 1, cnt[0]);
		Assert.assertEquals("Number of UserTutorial records created using insert is wrong", 1, cnt[1]);
	}



	@Test
	public void test05SelectByExample()
	{
		UserTutorialExample ex = new UserTutorialExample();
		UserTutorialExample.Criteria crit = ex.createCriteria();

		//Rec 1. created manually in db.
		crit.andUserIdEqualTo(Integer.valueOf(1));
		crit.andTitleEqualTo("MyBatis Generator Plugins");
		crit.andVideoTypeLike("fl%");

		List<UserTutorial> lstUsrTuts = null;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			lstUsrTuts = mapper.selectByExample(ex);
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("Incorrect number of matching UserTutorial rows returned for PK1", 1, lstUsrTuts.size());
		Assert.assertNotNull("UserTutorial with PK=1 not found.", lstUsrTuts.get(0));
		Assert.assertEquals("Incorrect video type for PK1", "flac", lstUsrTuts.get(0).getVideoType());


		//Rec 2. created by insert selective
		ex.clear();
		crit = ex.createCriteria();
		crit.andTitleEqualTo("2nd Tutorial");
		crit.andVideoTypeIsNull();

		sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			lstUsrTuts = mapper.selectByExample(ex);
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("Incorrect number of matching UserTutorial rows returned for PK2", 1, lstUsrTuts.size());
		Assert.assertNotNull("UserTutorial with PK=2 not found.", lstUsrTuts.get(0));
		Assert.assertNull("Incorrect video type for PK2", lstUsrTuts.get(0).getVideoType());


		//Rec 3. created by plain insert
		ex.clear();
		crit = ex.createCriteria();
		crit.andTitleLike("Third%");
		crit.andVideoTypeEqualTo("mp4");

		sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			lstUsrTuts = mapper.selectByExample(ex);
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("Incorrect number of matching UserTutorial rows returned for PK3", 1, lstUsrTuts.size());
		Assert.assertNotNull("UserTutorial with PK=3 not found.", lstUsrTuts.get(0));
		Assert.assertEquals("Incorrect video type for PK3", "mp4", lstUsrTuts.get(0).getVideoType());


		//Negative case
		ex.clear();
		ex.or().andUserIdEqualTo(1)
			.andUserTutorialIdGreaterThan(Integer.valueOf(5)); //same as making only an "and" criteria with userId=1 and userTutorialId=5

		sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			lstUsrTuts = mapper.selectByExample(ex);
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("No rows should have matched this select by example criterion, but some did", lstUsrTuts.size() == 0);
	}



	@Test
	public void test07SelectByExampleWithBlobs()
	{
		UserTutorialExample ex = new UserTutorialExample();
		UserTutorialExample.Criteria crit = ex.createCriteria();

		//Rec 1. created manually in db.
		crit.andUserIdEqualTo(Integer.valueOf(1));
		crit.andTitleEqualTo("MyBatis Generator Plugins");
		crit.andVideoTypeLike("fl%");

		List<UserTutorial> lstUsrTuts = null;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			lstUsrTuts = mapper.selectByExampleWithBLOBs(ex);
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("Incorrect number of matching UserTutorial rows returned for PK1", 1, lstUsrTuts.size());
		Assert.assertNotNull("UserTutorial with PK=1 not found.", lstUsrTuts.get(0));
		Assert.assertEquals("Incorrect summary for PK1", "This is a large summ...", lstUsrTuts.get(0).getSummary());
		Assert.assertEquals("Incorrect narrative for PK1", "The narrative goes here", lstUsrTuts.get(0).getNarrative());
		Assert.assertEquals("Incorrect video type for PK1", "flac", lstUsrTuts.get(0).getVideoType());


		//Rec 2. created by insert selective
		ex.clear();
		crit = ex.createCriteria();
		crit.andTitleEqualTo("2nd Tutorial");
		crit.andVideoTypeIsNull();

		sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			lstUsrTuts = mapper.selectByExampleWithBLOBs(ex);
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("Incorrect number of matching UserTutorial rows returned for PK2", 1, lstUsrTuts.size());
		Assert.assertNotNull("UserTutorial with PK=2 not found.", lstUsrTuts.get(0));
		Assert.assertEquals("Incorrect summary for PK2", "The summary of 2nd t...", lstUsrTuts.get(0).getSummary());
		Assert.assertEquals("Incorrect narrative for PK2", "Linux is the way to go!", lstUsrTuts.get(0).getNarrative());
		Assert.assertNull("Incorrect video type for PK2", lstUsrTuts.get(0).getVideoType());
		Assert.assertNull("Incorrect video for PK2", lstUsrTuts.get(0).getVideo());

		//Rec 3. created by plain insert
		ex.clear();
		crit = ex.createCriteria();
		crit.andTitleLike("Third%");
		crit.andVideoTypeEqualTo("mp4");

		sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			lstUsrTuts = mapper.selectByExampleWithBLOBs(ex);
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("Incorrect number of matching UserTutorial rows returned for PK3", 1, lstUsrTuts.size());
		Assert.assertNotNull("UserTutorial with PK=3 not found.", lstUsrTuts.get(0));
		Assert.assertEquals("Incorrect summary for PK3", "Must be at least 20 ...", lstUsrTuts.get(0).getSummary());
		Assert.assertEquals("Incorrect narrative for PK3", "I'll teach you how to use the db invoker plugin", lstUsrTuts.get(0).getNarrative());
		Assert.assertEquals("Incorrect video type for PK3", "mp4", lstUsrTuts.get(0).getVideoType());
		Assert.assertArrayEquals("Incorrect video for PK3", binVideo, lstUsrTuts.get(0).getVideo());

		//Negative case
		ex.clear();
		ex.or().andUserIdEqualTo(1)
			.andUserTutorialIdGreaterThan(Integer.valueOf(5)); //same as making only an "and" criteria with userId=1 and userTutorialId=5

		sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			lstUsrTuts = mapper.selectByExampleWithBLOBs(ex);
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("No rows should have matched this select by example criterion, but some did", lstUsrTuts.size() == 0);
	}


	@Test
	public void test10SelectByPrimaryKey()
	{
		UserTutorial[] usrTutArr = new UserTutorial[2];

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			usrTutArr[0] = mapper.selectByPrimaryKey(Integer.valueOf(3));
			usrTutArr[1] = mapper.selectByPrimaryKey(Integer.valueOf(4));
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertNotNull("UserTutorial with PK=3 not found.", usrTutArr[0]);
		Assert.assertEquals("Incorrect userTutorialId for PK3", Integer.valueOf(3), usrTutArr[0].getUserTutorialId());
		Assert.assertEquals("Incorrect userId FK for PK3", Integer.valueOf(1), usrTutArr[0].getUserId());
		Assert.assertEquals("Incorrect title for PK3", "Third Tutorial", usrTutArr[0].getTitle());
		Assert.assertEquals("Incorrect summary for PK3", "Must be at least 20 ...", usrTutArr[0].getSummary());
		Assert.assertEquals("Incorrect narrative for PK3", "I'll teach you how to use the db invoker plugin", usrTutArr[0].getNarrative());
		Assert.assertEquals("Incorrect video type for PK3", "mp4", usrTutArr[0].getVideoType());
		Assert.assertArrayEquals("Incorrect video for PK3", binVideo, usrTutArr[0].getVideo());

		Assert.assertNull("UserTutorial with PK=4 exists, but should not.", usrTutArr[1]);
	}


	@Test
	public void test15CountByExample()
	{
		UserTutorialExample ex = new UserTutorialExample();

		//Make a single OR clause.
		ex.or().andUserTutorialIdEqualTo(1);
		ex.or().andVideoTypeEqualTo("mp4");
		ex.or().andTitleEqualTo("2nd Tutorial");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
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
		UserTutorial recNewValues = new UserTutorial();

		recNewValues.setTitle("Tutorial #Two");
		recNewValues.setSummary("How to go fishing near crocodiles");
		recNewValues.setVideo(binVideo);
		recNewValues.setVideoType("avi");


		UserTutorialExample ex = new UserTutorialExample();
		ex.or().andUserTutorialIdEqualTo(Integer.valueOf(2))
			.andVideoTypeIsNull(); //The userTutorialId and videoType of the record to be updated.

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			cnt = mapper.updateByExampleSelective(recNewValues, ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("UserTutorial UpdateByExampleSelective - Only 1 record should be updated", cnt==1);
		Assert.assertTrue("UserTutorial UpdateByExampleSelective failed", isUserTutorialUpdateSuccessful(2, "Tutorial #Two",
			"How to go fishing ne...", "Linux is the way to go!", "avi"));
	}



	@Test
	public void test25UpdateByExample()
	{
		UserTutorialExample ex = new UserTutorialExample();
		ex.or().andUserTutorialIdEqualTo(Integer.valueOf(2)); //The userTutorialId of the record to be updated.

		UserTutorial recNewValues = new UserTutorial();

		recNewValues.setUserTutorialId(2);
		recNewValues.setUserId(1);
		recNewValues.setTitle("changed");
		recNewValues.setVideoType(null);

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			cnt = mapper.updateByExample(recNewValues, ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("UserTutorial UpdateByExample - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("UserTutorial UpdateByExample failed", isUserTutorialUpdateSuccessful(2, "changed",
			"How to go fishing ne...", "Linux is the way to go!", null));
	}



	@Test
	public void test27UpdateByExampleWithBLOBs()
	{
		binVideo[0] = (byte) 255;

		UserTutorial recNewValues = new UserTutorial();

		recNewValues.setUserTutorialId(2);
		recNewValues.setUserId(1);
		recNewValues.setSummary("Scuba diving to underwater ship");
		recNewValues.setVideo(binVideo);
		recNewValues.setVideoType("abc");


		UserTutorialExample ex = new UserTutorialExample();
		ex.or().andUserTutorialIdEqualTo(Integer.valueOf(2))
			.andVideoTypeIsNull(); //The userTutorialId and videoType of the record to be updated.

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			cnt = mapper.updateByExampleWithBLOBs(recNewValues, ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("UserTutorial UpdateByExampleWithBLOBs - Only 1 record should be updated", 1, cnt);
		Assert.assertTrue("UserTutorial UpdateByExampleWithBLOBs failed", isUserTutorialUpdateSuccessful(2, null,
			"Scuba diving to unde...", null, "abc"));
	}



	@Test
	public void test30UpdateByPrimaryKeySelective()
	{
		UserTutorial recNewValues = new UserTutorial();
		recNewValues.setUserTutorialId(2); //required to set PK for updateByPrimaryKeySelective
		recNewValues.setSummary("Mountain climbing and rock climbing");
		recNewValues.setTitle("climbing");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			cnt = mapper.updateByPrimaryKeySelective(recNewValues);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("UserTutorial updateByPrimaryKeySelective - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("UserTutorial UpdateByPrimaryKeySelective failed", isUserTutorialUpdateSuccessful(2, "climbing",
			"Mountain climbing an...", null, "abc"));
	}


	@Test
	public void test35UpdateByPrimaryKey()
	{
		UserTutorial recNewValues = new UserTutorial();

		recNewValues.setUserTutorialId(2);
		recNewValues.setUserId(1);
		recNewValues.setTitle("new title");
		recNewValues.setVideoType("dvx");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			cnt = mapper.updateByPrimaryKey(recNewValues);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("UserTutorial UpdateByPrimaryKey - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("UserTutorial UpdateByPrimaryKey failed", isUserTutorialUpdateSuccessful(2, "new title",
			"Mountain climbing an...", null, "dvx"));
	}


	@Test
	public void test37UpdateByPrimaryKeyWithBLOBs()
	{
		binVideo = null;

		UserTutorial recNewValues = new UserTutorial();

		recNewValues.setUserTutorialId(2); //always set the PK field
		recNewValues.setUserId(1);
		recNewValues.setSummary("Sky diving is thrilling");
		recNewValues.setNarrative("Ubuntu is a good linux flavor");
		recNewValues.setVideo(null);
		recNewValues.setVideoType(null);

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			cnt = mapper.updateByPrimaryKeyWithBLOBs(recNewValues);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("UserTutorial UpdateByPrimaryKeyWithBLOBs - Only 1 record should be updated", cnt==1);
		Assert.assertTrue("UserTutorial UpdateByPrimaryKeyWithBLOBs failed", isUserTutorialUpdateSuccessful(2, null,
			"Sky diving is thrill...", "Ubuntu is a good linux flavor", null));
	}


	@Test
	public void test40DeleteByExample()
	{
		UserTutorialExample ex = new UserTutorialExample();
		ex.or().andUserTutorialIdEqualTo(2);

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			cnt = mapper.deleteByExample(ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}
		
		Assert.assertTrue("UserTutorial deleteByExample - Cnt of deleted records should be 1", cnt==1);
	}


	@Test
	public void test45DeleteByPrimaryKey()
	{
		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			cnt = mapper.deleteByPrimaryKey(Integer.valueOf(3));
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}
		
		Assert.assertTrue("User deleteByPK - Cnt of deleted records should be 1", cnt==1);
	}




	private boolean isUserTutorialUpdateSuccessful(int userTutorialId, String title, String summary, String narrative, String videoType)
	{
		UserTutorial utb = null;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UserTutorialMapper mapper = sqlSession.getMapper(UserTutorialMapper.class);
			utb = mapper.selectByPrimaryKey(Integer.valueOf(userTutorialId));
		}
		finally
		{
			sqlSession.close();
		}

		return summary.equals(utb.getSummary())
			&& ( title == null ? utb.getTitle() == null : title.equals(utb.getTitle()) )
			&& ( narrative == null ? utb.getNarrative() == null : narrative.equals(utb.getNarrative()) )
			&& ( videoType == null ? utb.getVideoType() == null : videoType.equals(utb.getVideoType()) )
			&& ( binVideo == null ? utb.getVideo() == null : java.util.Arrays.equals(utb.getVideo(), binVideo) );
	}
}
