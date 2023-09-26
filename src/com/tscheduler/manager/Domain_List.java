/*
* 클래스명: DBRecorder_Insert.java
* 버전정보: JDK 1.4.1
* 요약설명: DOMAIN 별 통계 처리
* 작성일자: 2003-04-04 하광범_a
 */

package com.tscheduler.manager;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.*;

import com.tscheduler.util.Config;
import com.tscheduler.manager.Statistics_DOMAIN;
import com.tscheduler.dbbroker.DBManager;
import com.tscheduler.dbbroker.WorkDBManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Domain_List
{
	private static final Logger LOGGER = LogManager.getLogger(Domain_List.class.getName());
	
	/**
	 *    MID  별 등록 DOMAIN 리스트 만듬
	 *    @param    int tmpMID
	 *    @return   Vector
	 */
	public Vector getDomainList(String tmpYY, String tmpMM)
	{
		Vector vTmp=new Vector();

		Connection conn=null;
		Statement stmt=null;
		ResultSet rs=null;

		try
		{
			conn = DBManager.getConnection(Config.WORK_DB);

			String strQuery_stat_DOMAIN = (new StringBuffer("SELECT DOMAINNAME, ")
					.append("SYY, SMM FROM TS_MAILQUEUE_DOMAIN WHERE SYY='")
					.append(tmpYY).append("' and SMM='").append(tmpMM).append("'")).toString();

			//LOGGER.info(strQuery_stat_DOMAIN);
			
			stmt=conn.createStatement();
			rs=stmt.executeQuery(strQuery_stat_DOMAIN);

			while(rs.next())
			{
				Statistics_DOMAIN statistics_DOMAIN=new Statistics_DOMAIN();
				statistics_DOMAIN.DomainName=(rs.getString("DOMAINNAME")).toLowerCase();
				statistics_DOMAIN.YY=rs.getString("SYY");
				statistics_DOMAIN.MM=rs.getString("SMM");

				//LOGGER.info(rs.getString("SYY")+"/"+rs.getString("SMM")+" : " + rs.getString("DOMAINNAME"));
				
				vTmp.addElement(statistics_DOMAIN);
			}
			return vTmp;
		}
		catch(Exception e)
		{
			LOGGER.error(e);
			return null;
		}
		finally
		{
			try {
				WorkDBManager.releaseConnection(conn);
			}
			catch(Exception e) { LOGGER.error(e);}

			try {
				if( stmt != null ) {
					stmt.close();
					stmt = null;
				}
			}
			catch(Exception e) {
				LOGGER.error(e);
			}

			try
			{
				if( rs != null ) {
					rs.close();
					rs = null;
				}
			}
			catch(Exception e) {
				LOGGER.error(e);
			}
		}
	}
} // End of Class
