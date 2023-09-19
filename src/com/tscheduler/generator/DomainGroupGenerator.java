package com.tscheduler.generator;

import java.sql.*;
import java.util.*;
import java.io.*;

import com.tscheduler.util.Config;
import com.tscheduler.util.DataUnitInfo;
import com.tscheduler.util.DataUnitInfoList;
import com.tscheduler.dbbroker.DBManager;
import com.tscheduler.dbbroker.WorkDBManager;

/**
 * 도메인 그룹핑 클래스
 * @version 1.0
 * @author ymkim
 */
public class DomainGroupGenerator
{
	/**도메인에 포함되지 않은 ETC의 경우*/
	private static final String ETC_DOMAIN = "etc";

	/**도메인 리스트를 얻는 쿼리(오라클용)*/
	private static final String SELECT_MAILQUEUE_DOMAIN_QUERY_ORA =
			" SELECT DOMAINNAME FROM TS_DOMAIN_INFO				"+
			"	WHERE DOMAINNAME NOT IN(						"+
			"		SELECT DOMAINNAME FROM TS_MAILQUEUE_DOMAIN 	"+
			"			WHERE SYY=TO_CHAR(SYSDATE,'YY') 		"+
			"				AND SMM = TO_CHAR(SYSDATE,'MM')		"+
			"				AND DOMAINNAME IN (					"+
			"					SELECT DOMAINNAME FROM TS_DOMAIN_INFO	"+
			"				)									"+
			"	)												";

	/**도메인 리스트를 얻는 쿼리(MSSQL용)*/
	private static final String SELECT_MAILQUEUE_DOMAIN_QUERY_SQL =
			" SELECT DOMAINNAME FROM TS_DOMAIN_INFO				"+
			"	WHERE DOMAINNAME NOT IN(						"+
			"		SELECT DOMAINNAME FROM TS_MAILQUEUE_DOMAIN 	"+
			"			WHERE SYY = (SUBSTRING(CONVERT(VARCHAR(4),DATEPART(YEAR,GETDATE())),3,2)) "+
			"				AND SMM = (SUBSTRING(CONVERT(VARCHAR(10),GETDATE(),120),6,2))	"+
			"				AND DOMAINNAME IN (					"+
			"					SELECT DOMAINNAME FROM TS_DOMAIN_INFO	"+
			"				)									"+
			"	)												";

	/**도메인 리스트를 얻는 쿼리(MARIA용)*/
	private static final String SELECT_MAILQUEUE_DOMAIN_QUERY_MARIA =
			" SELECT DOMAINNAME FROM TS_DOMAIN_INFO				"+
			"	WHERE DOMAINNAME NOT IN(						"+
			"		SELECT DOMAINNAME FROM TS_MAILQUEUE_DOMAIN 	"+
			"			WHERE SYY = (SUBSTRING(CONVERT(VARCHAR(4),DATEPART(YEAR,GETDATE())),3,2)) "+
			"				AND SMM = (SUBSTRING(CONVERT(VARCHAR(10),GETDATE(),120),6,2))	"+
			"				AND DOMAINNAME IN (					"+
			"					SELECT DOMAINNAME FROM TS_DOMAIN_INFO	"+
			"				)									"+
			"	)												";

	/**도메인 통계 테이블 입력 쿼리*/
	private static final String INSERT_DOMAIN_QUERY = "INSERT INTO TS_MAILQUEUE_DOMAIN(DOMAINNAME) VALUES(?)";

	/**
	 * domain_info테이블에서 검색해와서 기존에 있다면 입력해주지 않고, 기존에 없는거라면 입력해준다.
	 * @version 1.0
	 * @author ymkim
	 * @return boolean true - 입력 성공, false - 입력 실패 혹은 입력내용 없음
	 */
	public static boolean insertCntPerDomain()
	{
		boolean return_value = false;

		Connection con_work = null;
		PreparedStatement pstmt = null;

		//매번 새로운 리스트가 없는지 체크한다.
		ArrayList checkList = getRegDomainList();

		if( checkList.size() == 0 ) //입력할것이 아무것도 없으면
		{
			return_value = false;
		}
		else	//입력할것이 있다면 입력해줘야 한다.
		{
			try
			{
				con_work = DBManager.getConnection(Config.WORK_DB);
				pstmt = con_work.prepareStatement(INSERT_DOMAIN_QUERY);
				for( int i = 0; i < checkList.size(); i++ )
				{
					pstmt.setString(1, (String)checkList.get(i));
					pstmt.executeUpdate();
				}

				con_work.commit();
			}
			catch(Exception e)
			{
				try {
					con_work.rollback();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
				WorkDBManager.refreshConn();
				e.printStackTrace();
				return_value = false;
			}
			finally
			{
				try
				{
					WorkDBManager.releaseConnection(con_work);
					if( pstmt != null ) {
						pstmt.close();
					}
				}
				catch(Exception e) {
				}
			}
		}
		return return_value;
	}


	/**
	 * 이미 등록된 도메인 리스트를 가져온다.
	 * @version 1.0
	 * @author ymkim
	 * @return ArrayList 도메인 리스트
	 */
	private static ArrayList getRegDomainList()
	{
		Connection con_work =null;
		PreparedStatement pstmt=null;
		ResultSet rs = null;

		ArrayList return_value = new ArrayList();

		try
		{
			Config cfg = Config.getInstance();
			cfg.loadConfig(Config.MAIN_CFG);
			String dbType = cfg.getDBType(); //MsSql인지 Oracle인지 알아온다.

			con_work = DBManager.getConnection(Config.WORK_DB);

			if( dbType.equalsIgnoreCase("ORACLE") ) {
				pstmt = con_work.prepareStatement(SELECT_MAILQUEUE_DOMAIN_QUERY_ORA);
			}
			else if( dbType.equalsIgnoreCase("MSSQL") ) {
				pstmt = con_work.prepareStatement(SELECT_MAILQUEUE_DOMAIN_QUERY_SQL);
			}
			
			else if( dbType.equalsIgnoreCase("MARIA") ) {
				pstmt = con_work.prepareStatement(SELECT_MAILQUEUE_DOMAIN_QUERY_MARIA);
			}
			else {
				pstmt = con_work.prepareStatement(SELECT_MAILQUEUE_DOMAIN_QUERY_SQL);
			}

			rs = pstmt.executeQuery();

			while( rs.next() ) {
				return_value.add(rs.getString("DOMAINNAME"));
			}
		}
		catch(Exception e)
		{
			WorkDBManager.refreshConn();
			e.printStackTrace();
		}
		finally
		{
			try
			{
				WorkDBManager.releaseConnection(con_work);
				if( rs != null ) {
					rs.close();
				}
				if( pstmt != null ) {
					pstmt.close();
				}
			}
			catch(Exception e) {
			}
		}
		return return_value;
	}
}