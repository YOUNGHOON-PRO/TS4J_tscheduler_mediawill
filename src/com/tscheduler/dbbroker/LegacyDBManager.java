package com.tscheduler.dbbroker;

//LegacyDB 연결 관리
import java.sql.*;
import com.tscheduler.util.*;

/**
 * Legacy DB 관리 클래스
 * @version 1.0
 * @author ymkim
 */
public class LegacyDBManager
{
	/**LegacyDBManager의 Singleton 객체*/
	private static LegacyDBManager instance;       // The single instance

	/**
	 * LegacyDBManager의 Singleton객체를 통해 컨넥션을 얻는다.
	 * @version 1.0
	 * @author ymkim
	 * @param dbDriver DB Driver
	 * @param dbURL DB URL
	 * @param dbUserID DB User ID
	 * @param dbUserPass DB User Password
	 * @return Connection 객체
	 */
	public static Connection getConnection(String dbDriver, String dbURL,
			String dbUserID, String dbUserPass)
	{
		if( instance == null ) {
			instance = new LegacyDBManager();
		}
		else {
		}
		return instance.getFreeConnection(dbDriver, dbURL, dbUserID, dbUserPass);
	}

	private LegacyDBManager() {
	}

	/**
	 * 컨넥션을 얻는다.
	 * @param dbDriver DB Driver
	 * @param dbURL DB URL
	 * @param dbUserID DB User ID
	 * @param dbUserPass DB User Password
	 * @return Connection 객체
	 */
	private synchronized Connection getFreeConnection(String dbDriver, String dbURL,
			String dbUserID, String dbUserPass)
	{
		Connection con = null;
		try
		{
			Class.forName(dbDriver);
			con = DriverManager.getConnection(dbURL,dbUserID, dbUserPass);
			DebugTrace.println("컨넥션을 얻었습니다");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return con;
	}
}