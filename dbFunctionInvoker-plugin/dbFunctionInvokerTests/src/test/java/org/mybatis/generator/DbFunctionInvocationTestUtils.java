package org.mybatis.generator;


import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.io.IOException;

import java.util.Properties;

import org.xml.sax.SAXException;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;


public class DbFunctionInvocationTestUtils
{
	private static Properties conProp;

	public static void resetIdentityValues()
	{
		Connection con = null;
		Statement stmt = null;

		try
		{
			if(conProp == null)
			{
				String fileLoc = DbFunctionInvocationTestUtils.class.getResource("/mybatis-config.xml").toExternalForm();
				conProp = getConnectionPropertiesFromFile(fileLoc);
			}


			//Class.forName(conProp.getProperty("driver")); //not needed. Thanks to Maven it's on the classpath & initialized already.

			con = DriverManager.getConnection(conProp.getProperty("url"), conProp);
			stmt = con.createStatement();

			//The statement below resets the IDENTITY column and changes the next automatic value. See: http://www.hsqldb.org/doc/guide/ch09.html 
			stmt.execute("ALTER TABLE Users ALTER COLUMN user_id RESTART WITH 2");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
		finally
		{
			if(stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch(SQLException e)
				{
				}
			}

			if(con != null)
			{
				try
				{
					con.close();
				}
				catch(SQLException e)
				{
				}
			}
		}
	}



	private static Properties getConnectionPropertiesFromFile(String fileLocUri) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException
	{
		XPath xpath = XPathFactory.newInstance().newXPath();
		DocumentBuilder docBldr = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		Document docRoot = docBldr.parse(fileLocUri);

		String strDriver = (String) xpath.evaluate("/configuration/environments/environment/dataSource/property[@name='driver']/@value", docRoot, XPathConstants.STRING);
		String strUrl = (String) xpath.evaluate("/configuration/environments/environment/dataSource/property[@name='url']/@value", docRoot, XPathConstants.STRING);
		String strUserName = (String) xpath.evaluate("/configuration/environments/environment/dataSource/property[@name='username']/@value", docRoot, XPathConstants.STRING);

		Properties prop = new Properties();
		prop.setProperty("driver", strDriver);
		prop.setProperty("url", strUrl);
		prop.setProperty("user", strUserName);
		prop.setProperty("password", "");

		return prop;
	}
}

