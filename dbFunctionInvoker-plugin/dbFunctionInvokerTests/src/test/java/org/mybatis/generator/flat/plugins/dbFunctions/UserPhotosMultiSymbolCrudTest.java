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
import java.util.Random;

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
public class UserPhotosMultiSymbolCrudTest
{
	private static SqlSessionFactory sqlSessionFactory;
	private static byte[] binPhotoData;


	@BeforeClass
	public static void setUpBeforeClass() throws IOException
	{
		// create a SqlSessionFactory
		Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		reader.close();

		binPhotoData = new byte[15];
		Random rnd = new Random();
		rnd.nextBytes(binPhotoData);
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		sqlSessionFactory = null;
	}



	@Test
	public void test01BothInserts()
	{
		UserPhotos usrPhotoSelective = new UserPhotos();
		usrPhotoSelective.setUserId(Integer.valueOf(1));
		usrPhotoSelective.setPhotoTitle("J2EE stack");

		UserPhotos usrPhotoPlain = new UserPhotos();
		usrPhotoPlain.setUserId(Integer.valueOf(1));
		usrPhotoPlain.setPhotoTitle("Core Java Layers");
		usrPhotoPlain.setPhoto(binPhotoData);
		usrPhotoPlain.setPhotoType("jpg");

		int[] cnt = {0,0};

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserPhotosMapper mapper = sqlSession.getMapper(UserPhotosMapper.class);

			cnt[0] = mapper.insertSelective(usrPhotoSelective);

			cnt[1] = mapper.insert(usrPhotoPlain);

			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("Number of UserPhotos records created using insert selective is wrong", 1, cnt[0]);
		Assert.assertEquals("Number of UserPhotos records created using insert is wrong", 1, cnt[1]);
	}


	@Test
	public void test05SelectByExample()
	{
		UserPhotosExample ex = new UserPhotosExample();
		UserPhotosExample.Criteria crit = ex.createCriteria();

		//Rec 1. created manually in db. Remember we can't select on LOB columns. MyBatis does not allow this.
		crit.andUserIdEqualTo(Integer.valueOf(1));
		crit.andPhotoTitleEqualTo("ImgHead Shot Mahiar");
		crit.andPhotoTypeEqualTo("png");

		List<UserPhotos> lstUsrPhotos = getUserPhotosRecords(ex);

		Assert.assertEquals("Too many rows matching UserPhotos returned for Head Shot photo", 1, lstUsrPhotos.size());
		Assert.assertNotNull("UserPhotos with Head Shot photo not found.", lstUsrPhotos.get(0));
		Assert.assertEquals("Incorrect photo_title for Head Shot photo", "Head Shot Mahiar-PHOTO", lstUsrPhotos.get(0).getPhotoTitle());
		Assert.assertEquals("Incorrect photo_type for Head Shot photo", "png", lstUsrPhotos.get(0).getPhotoType());


		//Rec 2. created by insert selective
		ex.clear();
		crit = ex.createCriteria();
		crit.andPhotoTypeIsNull();

		lstUsrPhotos = getUserPhotosRecords(ex);

		Assert.assertEquals("Too many rows matching UserPhotos returned for NULL photo type", 1, lstUsrPhotos.size());
		Assert.assertNotNull("UserPhotos with NULL photo type not found.", lstUsrPhotos.get(0));
		Assert.assertEquals("Incorrect photo_title for NULL photo type", "E stack-PHOTO", lstUsrPhotos.get(0).getPhotoTitle());
		Assert.assertNull("Incorrect photo_type for NULL photo", lstUsrPhotos.get(0).getPhotoType());


		//Rec 3. created by plain insert
		ex.clear();
		crit = ex.createCriteria();
		crit.andPhotoTitleEqualTo("Core Java Layers");
		crit.andPhotoTypeEqualTo("jpg");
		lstUsrPhotos = getUserPhotosRecords(ex);

		Assert.assertEquals("Too many rows matching UserPhotos returned for Core Java Layers", 1, lstUsrPhotos.size());
		Assert.assertNotNull("UserPhotos with Core Java Layers photo not found.", lstUsrPhotos.get(0));
		Assert.assertEquals("Incorrect photo_title for Core Java Layers photo", "e Java Layers-PHOTO", lstUsrPhotos.get(0).getPhotoTitle());
		Assert.assertEquals("Incorrect photo_type for Core Java Layers photo", "jpg", lstUsrPhotos.get(0).getPhotoType());


		//Negative case
		ex.clear();
		ex.or().andUserIdEqualTo(1)
			.andPhotoTitleEqualTo("C++"); //same as making only an "and" criteria with usrPhotoId=1 and surnameSound = 'Brown'.

		lstUsrPhotos = getUserPhotosRecords(ex);

		Assert.assertTrue("No rows should have matched this select by example criterion, but some did", lstUsrPhotos.size() == 0);
	}



	@Test
	public void test10SelectByExampleWithBlobs()
	{
		UserPhotosExample ex = new UserPhotosExample();
		UserPhotosExample.Criteria crit = ex.createCriteria();

		//Rec 2. created by insert selective. Remember we can't select on LOB columns. MyBatis does not allow this.
		crit.andPhotoTitleEqualTo("Core Java Layers");
		crit.andPhotoTypeEqualTo("jpg");

		List<UserPhotos> lstUsrPhotos = null;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserPhotosMapper mapper = sqlSession.getMapper(UserPhotosMapper.class);
			lstUsrPhotos = mapper.selectByExampleWithBLOBs(ex);
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("Too many rows matching UserPhotos returned for Core Java Layers", 1, lstUsrPhotos.size());
		Assert.assertNotNull("UserPhotos with Core Java Layers not found.", lstUsrPhotos.get(0));
		Assert.assertTrue("Incorrect binary photo data for Core Java Layers photo", java.util.Arrays.equals(binPhotoData, lstUsrPhotos.get(0).getPhoto()));



		//Rec 3. created by plain insert
		ex.clear();
		crit = ex.createCriteria();
		crit.andPhotoTypeIsNull();

		sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserPhotosMapper mapper = sqlSession.getMapper(UserPhotosMapper.class);
			lstUsrPhotos = mapper.selectByExampleWithBLOBs(ex);
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertEquals("Too many rows matching UserPhotos returned for NULL photo type", 1, lstUsrPhotos.size());
		Assert.assertNotNull("UserPhotos with NULL photo type not found.", lstUsrPhotos.get(0));
		Assert.assertNull("Incorrect binary photo data NULL photo type", lstUsrPhotos.get(0).getPhoto());
	}



	@Test
	public void test15CountByExample()
	{
		UserPhotosExample ex = new UserPhotosExample();

		//Make a single OR clause.
		ex.or().andPhotoTitleEqualTo("Core Java Layers");
		ex.or().andPhotoTypeIsNull();

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UserPhotosMapper mapper = sqlSession.getMapper(UserPhotosMapper.class);
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
		UserPhotos recNewValues = new UserPhotos();
		recNewValues.setPhotoTitle("1234 onwards");
		recNewValues.setPhoto(binPhotoData);

		UserPhotosExample ex = new UserPhotosExample();
		ex.or().andPhotoTypeIsNull(); //The record to be updated.

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserPhotosMapper mapper = sqlSession.getMapper(UserPhotosMapper.class);
			cnt = mapper.updateByExampleSelective(recNewValues, ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("UserPhotos UpdateByExampleSelective - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("UserPhotos UpdateByExampleSelective failed", isUpdateSuccessful("1234 onwards", "4 onwards-PHOTO", null));
	}



	@Test
	public void test25UpdateByExample()
	{
		UserPhotosExample ex = new UserPhotosExample();
		ex.or().andPhotoTitleEqualTo("1234 onwards"); //The record to be updated.

		UserPhotos recNewValues = getUserPhotosRecords(ex).get(0);
		recNewValues.setPhotoTitle("Testing");
		recNewValues.setPhotoType("gif");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserPhotosMapper mapper = sqlSession.getMapper(UserPhotosMapper.class);
			cnt = mapper.updateByExample(recNewValues, ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("UserPhotos UpdateByExample - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("UserPhotos UpdateByExample failed", isUpdateSuccessful("Testing", "ting-PHOTO", "gif"));
	}



	@Test
	public void test30UpdateByExampleWithBlobs()
	{
		binPhotoData[0]^=1;

		UserPhotosExample ex = new UserPhotosExample();
		ex.or().andPhotoTitleEqualTo("Testing"); //The record to be updated.

		UserPhotos recNewValues = new UserPhotos();
		recNewValues.setUserId(1);
		recNewValues.setPhotoTitle("TheCopy");
		recNewValues.setPhotoType("raw");
		recNewValues.setPhoto(binPhotoData);

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserPhotosMapper mapper = sqlSession.getMapper(UserPhotosMapper.class);
			cnt = mapper.updateByExampleWithBLOBs(recNewValues, ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}

		Assert.assertTrue("UserPhotos UpdateByExampleWithBLOBs - Only 1 records should be updated", cnt==1);
		Assert.assertTrue("UserPhotos UpdateByExampleWithBLOBs failed", isUpdateSuccessful("TheCopy", "Copy-PHOTO", "raw"));
	}



	@Test
	public void test40DeleteByExample()
	{
		UserPhotosExample ex = new UserPhotosExample();
		ex.or().andPhotoTypeNotEqualTo("png");

		int cnt=0;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UserPhotosMapper mapper = sqlSession.getMapper(UserPhotosMapper.class);
			cnt = mapper.deleteByExample(ex);
			sqlSession.commit();
		}
		finally
		{
			sqlSession.close();
		}
		
		Assert.assertTrue("UserPhotos deleteByExample - Cnt of deleted records should be 1", cnt==2);
	}



	private List<UserPhotos> getUserPhotosRecords(UserPhotosExample ex)
	{
		List<UserPhotos> lstUserPhotos = null;

		SqlSession sqlSession = sqlSessionFactory.openSession();

		try
		{
			UserPhotosMapper mapper = sqlSession.getMapper(UserPhotosMapper.class);
			lstUserPhotos = mapper.selectByExample(ex);
		}
		finally
		{
			sqlSession.close();
		}

		return lstUserPhotos;
	}


	private boolean isUpdateSuccessful(String paramPhotoTitle, String resultPhotoTitle, String photoType)
	{
		UserPhotosExample ex = new UserPhotosExample();
		ex.or().andPhotoTitleEqualTo(paramPhotoTitle);

		UserPhotos usrPhotos = null;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		try
		{
			UserPhotosMapper mapper = sqlSession.getMapper(UserPhotosMapper.class);
			usrPhotos = mapper.selectByExampleWithBLOBs(ex).get(0);
		}
		finally
		{
			sqlSession.close();
		}

		return resultPhotoTitle.equals(usrPhotos.getPhotoTitle())
			&& java.util.Arrays.equals(binPhotoData, usrPhotos.getPhoto())
			&& (photoType == null ? usrPhotos.getPhotoType() == null : photoType.equals(usrPhotos.getPhotoType()));
	}
}
