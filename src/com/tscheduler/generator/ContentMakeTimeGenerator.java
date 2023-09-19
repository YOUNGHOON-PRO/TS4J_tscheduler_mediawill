package com.tscheduler.generator;

//한 MID에 대한 컨덴츠 생성 시간
import java.sql.*;

import com.tscheduler.util.Config;
import com.tscheduler.dbbroker.DBManager;
import com.tscheduler.dbbroker.WorkDBManager;


/**
 * 이메일 컨덴츠 생성 시간 관리 클래스
 * @version 1.0
 * @author ymkim
 */
public class ContentMakeTimeGenerator
{
	/**상태를 업데이트하는 쿼리*/
	private static final String DATE_UPDATE_QUERY_ORA = "UPDATE TS_MAILQUEUE SET SDATE=sysdate WHERE MID=?";

	/**상태를 업데이트하는 쿼리*/
	private static final String DATE_UPDATE_QUERY_MSSQL = "UPDATE TS_MAILQUEUE SET SDATE=getDate() WHERE MID=?";

	/**
	 * 컨덴츠 생성 완료 시간을 DB에 update시킨다.
	 * @version 1.0
	 * @author ymkim
	 * @param mID 예약메일ID
	 * @return boolean true - db입력 완료, false - db입력 실패
	 */
	public static boolean updateCompleteTime(String mID)
	{
		Connection con_work =null;
		PreparedStatement pstmt=null;
		String [] statusList = null;
		String [] midList = null;

		boolean return_value = false;
		try
		{
			Config cfg = Config.getInstance();
			cfg.loadConfig(Config.MAIN_CFG);
			String dbType = cfg.getDBType(); //MsSql인지 Oracle인지 알아온다.

			con_work = DBManager.getConnection(Config.WORK_DB);

			if( (dbType.toUpperCase()).equals("ORACLE") ) {
				pstmt = con_work.prepareStatement(DATE_UPDATE_QUERY_ORA);
			}
			else if( (dbType.toUpperCase()).equals("MSSQL") ) {
				pstmt = con_work.prepareStatement(DATE_UPDATE_QUERY_MSSQL);
			}
			else {	//디비타입을 정하지 않았습니다.
				return false;
			}

			pstmt.setString(1,mID);
			pstmt.executeUpdate();

			con_work.commit();
			return_value = true;
		}
		catch(Exception e)
		{
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
		return return_value;
	}
}