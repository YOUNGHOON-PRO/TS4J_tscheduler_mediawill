/*
* 클래스명:DBManager
* 설명: 컨넥션 풀 매니저
* 	1. WorkDbB에 대해서는 컨넥션 풀을 만든다.
*	2. LegacyDB에 대해서는 일반 컨넥션을 만들어서 준다.
*/

package com.tscheduler.dbbroker;

import java.sql.*;
import java.util.Hashtable;

import com.tscheduler.util.*;

/**
 * 컨넥션 풀 매니저 클래스
 * @version 1.0
 * @author ymkim
 */
public class DBManager
{
	/**레거시 DB의 Driver 정보*/
	private static String dbDriver;
	/**레거시 DB의 URL 정보*/
	private static String dbURL;
	/**레거시 DB의 User ID 정보*/
	private static String dbUserID;
	/**레거시 DB의 User Pass 정보*/
	private static String dbUserPass;

	/**
	 * LegacyDB 정보를 세팅한다.
	 * @version 1.0
	 * @author ymkim
	 * @param hashtable Legacy DB 정보
	 */
	public static void setLDBInfo(Hashtable legacyInfo)
	{
		dbDriver = (String)legacyInfo.get("DRIVER");
		dbURL = (String)legacyInfo.get("DBURL");
		dbUserID = (String)legacyInfo.get("USERID");
		dbUserPass = (String)legacyInfo.get("USERPASS");
	}

	/**
	 * 컨넥션을 얻는다.(dbType에 따라서 달라진다. WorkDB일지 LegacyDB일지)
	 * @version 1.0
	 * @author ymkim
	 * @param dbType DB 연결 정보
	 * @param Connection 얻는 컨넥션
	 */
	public static Connection getConnection(String dbType)
	{
		if( dbType.equals(Config.WORK_DB) ) {
			return WorkDBManager.getConnection();
		}
		else if( dbType.equals(Config.LEGACY_DB) ) {
			return LegacyDBManager.getConnection(dbDriver,dbURL,dbUserID,dbUserPass);
		}
		else
		{
			DebugTrace.println("컨넥션을 얻을수 없습니다.");
			return null;
		}
	}
}