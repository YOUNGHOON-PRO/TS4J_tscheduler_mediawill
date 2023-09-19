package com.tscheduler.manager;
/*
*설명: 에러 결과들을 디비에 넣어준다.
*/

import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;

import com.tscheduler.util.ReserveStatusCode;
import com.tscheduler.util.Config;
import com.tscheduler.util.CheckFormat;
import com.tscheduler.util.LogDateGenerator;
import com.tscheduler.util.ErrorLogGenerator;
import com.tscheduler.util.ErrorStatusCode;
import com.custinfo.safedata.CustInfoSafeData;
import com.tscheduler.dbbroker.DBManager;
import com.tscheduler.dbbroker.WorkDBManager;
import com.tscheduler.generator.DomainGroupGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 결과 로그 관리 클래스
 * @version 1.0
 * @author ymkim
 */
public class ResultLogManager
{
	private static final Logger LOGGER = LogManager.getLogger(ResultLogManager.class.getName());
	
	public static String NO_USER_ID = "NO_ID";
	public static String NO_USER_NAME = "NO_USER";
	public static String NO_USER_MAIL = "NO_MAIL";

//	private static Vector vStatistics_MID = null ;
//  private static Vector vStatistics_DOMAIN = null;
//  private static Domain_List domain_List = null;

	//TScheduler Generating 결과를 입력하는 Query(오라클 용)
	private static String RESULT_LOG_QUERY_ORA = (new StringBuffer()
			.append("INSERT INTO TS_RESULTLOG(MID, SUBID, TID, RID, RNAME, RMAIL, ")
			.append("SID, SNAME, SMAIL, RCODE, STIME, REFMID ,WDATE) ")
			.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate)")).toString();

	// TScheduler Generating 결과를 입력하는 Query(MSSQL 용)
	private static String RESULT_LOG_QUERY_MSSQL = (new StringBuffer()
			.append("INSERT INTO TS_RESULTLOG(MID, SUBID, TID, RID, RNAME, RMAIL, ")
			.append("SID, SNAME, SMAIL, RCODE, STIME, REFMID ,WDATE) ")
			.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())")).toString();
	
	// TScheduler Generating 결과를 입력하는 Query(MSSQL 용)
	private static String RESULT_LOG_QUERY_MARIA = (new StringBuffer()
			.append("INSERT INTO TS_RESULTLOG(MID, SUBID, TID, RID, RNAME, RMAIL, ")
			.append("SID, SNAME, SMAIL, RCODE, STIME, REFMID ,WDATE) ")
			.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())")).toString();

	
	
	public ResultLogManager()
	{
//		vStatistics_MID = new Vector();
//		vStatistics_DOMAIN = new Vector();
//	    domain_List=new Domain_List();
	}

	/**
	 * 결과 로그를 WorkDB에 입력한다.
	 * @version 1.0
	 * @author ymkim
	 * @param logInfoList 입력할결과로그
	 * @boolean true - 로그 입력 성공, false - 로그 입력 실패
	 */
	public static boolean InsertResultLog(ArrayList logInfoList)
	{
		Connection con_work = DBManager.getConnection(Config.WORK_DB);

		LOGGER.info(con_work);

		PreparedStatement pstmt = null;
		PreparedStatement pstmt_stat_MID = null;
		PreparedStatement pstmt_stat_DOMAIN = null;

		String mID = "";
		String subID = "";
		String tID;
		String sName;
		String sMail;
		String sID;
		String rName;
		String rMail;
		String rID;
		String rCode;
		String refMID;

		boolean return_value = false;

		try
		{
			//영맨 꽁수(일단은 여기에서만 AutoCommit을 True로 설정한다.
			con_work.setAutoCommit(true);
			//

			Config cfg = Config.getInstance();
			cfg.loadConfig(Config.MAIN_CFG);

			String dbType = cfg.getDBType(); //MsSql인지 Oracle인지 알아온다.

			int logInfoListSize = logInfoList.size();

			Hashtable logInfo = null;

			if( dbType.toUpperCase().equals("ORACLE") ) {
				pstmt = con_work.prepareStatement(RESULT_LOG_QUERY_ORA);
			}
			else if( dbType.toUpperCase().equals("MSSQL") ) {
				pstmt = con_work.prepareStatement(RESULT_LOG_QUERY_MSSQL);
			}
			else if( dbType.toUpperCase().equals("MARIA") ) {
				pstmt = con_work.prepareStatement(RESULT_LOG_QUERY_MARIA);
			}
			else {	//디비타입을 정하지 않았습니다.
				return false;
			}

			String strQuery_stat="UPDATE TS_MAILQUEUE_RSINFO SET ECODE41=ECODE41+?, ECODE42=ECODE42+?, CCOUNT=CCOUNT+1 "
					   + " WHERE MID = ? AND SUBID = ?";
			pstmt_stat_MID = con_work.prepareStatement(strQuery_stat);

			String strQuery_stat_DOMAIN="UPDATE TS_MAILQUEUE_DOMAIN SET ECODE41=ECODE41+?, ECODE42=ECODE42+?"+
							   " WHERE DOMAINNAME = ? AND SYY = ? AND SMM = ?";
			pstmt_stat_DOMAIN = con_work.prepareStatement(strQuery_stat_DOMAIN);

			CustInfoSafeData safeDbEnc = new CustInfoSafeData();
			
			for( int i = 0; i < logInfoListSize; i++ )
			{
				logInfo = (Hashtable)logInfoList.get(i);

				mID = (String) logInfo.get("MID");
				subID = (String) logInfo.get("SUBID");
				tID = (String) logInfo.get("TID");
				sName = (String) logInfo.get("SNAME");
				sMail = (String )logInfo.get("SMAIL");
				
		          //SMAIL 암호화
		          if("Y".equals(cfg.getEnc_yn())) {
		        	  //tmp7 = enc.getJasyptEncryptedString(ALGORITHM, KEYSTRING, tmp7);
		        	  //sMail = safeDbEnc.getEncrypt(sMail, "NOT_RNNO");
		        	  sMail = safeDbEnc.getEncrypt(sMail, "ENDERSUMS");
		          }
				
				sID = (String) logInfo.get("SID");
				rName = (String) logInfo.get("RNAME");
				rMail = (String) logInfo.get("RMAIL");
				rID = (String) logInfo.get("RID");
				rCode = (String) logInfo.get("RCODE");
				refMID = (String) logInfo.get("REFMID");

				//statistics_MID(mID,Integer.parseInt(rCode));
				//statistics_Domain(Integer.parseInt(rCode),rMail);

				pstmt.setString(1, mID);
				pstmt.setInt(2, Integer.parseInt(subID));
				pstmt.setString(3, tID);
				pstmt.setString(4, rID);
				pstmt.setString(5, rName);
				pstmt.setString(6, rMail);
				pstmt.setString(7, sID);
				pstmt.setString(8, sName);
				pstmt.setString(9, sMail);
				pstmt.setString(10, rCode);
				pstmt.setString(11, LogDateGenerator.getResultLogTime());
				pstmt.setString(12, refMID);

				pstmt.executeUpdate();

				//발송결과 통계 테이블에 넣어준다.
				if( rCode.equalsIgnoreCase(ErrorStatusCode.ECODE_RMAIL) )        //수신자 이메일 에러인경우
				{
					pstmt_stat_MID.setInt(1, 1);
					pstmt_stat_MID.setInt(2, 0);
				}
				else if( rCode.equalsIgnoreCase(ErrorStatusCode.ECODE_CONTENT) ) //컨덴츠 생성 에러인경우
				{
					pstmt_stat_MID.setInt(1,0);
					pstmt_stat_MID.setInt(2,1);
				}
				else if( rCode.equalsIgnoreCase(ErrorStatusCode.ECODE_CONTENT2) ) //개인정보 생성 에러인경우
				{
					pstmt_stat_MID.setInt(1,0);
					pstmt_stat_MID.setInt(2,1);
				}
				else if( rCode.equalsIgnoreCase(ErrorStatusCode.ECODE_SECU_CONTENT) ) //보안메일 엑셀 생성 에러인경우
				{
					pstmt_stat_MID.setInt(1,0);
					pstmt_stat_MID.setInt(2,1);
				}
				

				pstmt_stat_MID.setString(3, mID);
				pstmt_stat_MID.setInt(4, Integer.parseInt(subID));
				pstmt_stat_MID.executeUpdate();

				LOGGER.info(rCode);

				//발송 도메인 통계 테이블에 넣어준다.
				if( rCode.equalsIgnoreCase(ErrorStatusCode.ECODE_RMAIL) )	//수신자 이메일 에러인경우
				{
					pstmt_stat_DOMAIN.setInt(1, 1);
					pstmt_stat_DOMAIN.setInt(2, 0);
				}
				else if( rCode.equalsIgnoreCase(ErrorStatusCode.ECODE_CONTENT) )	//컨덴츠 생성 에러인경우
				{
					pstmt_stat_DOMAIN.setInt(1, 0);
					pstmt_stat_DOMAIN.setInt(2, 1);
				}
				else if( rCode.equalsIgnoreCase(ErrorStatusCode.ECODE_CONTENT2) )	//개인정보 생성 에러인경우
				{
					pstmt_stat_DOMAIN.setInt(1, 0);
					pstmt_stat_DOMAIN.setInt(2, 1);
				}
				else if( rCode.equalsIgnoreCase(ErrorStatusCode.ECODE_SECU_CONTENT) )	//보안메일 엑셀 생성 에러인경우
				{
					pstmt_stat_DOMAIN.setInt(1, 0);
					pstmt_stat_DOMAIN.setInt(2, 1);
				}
				
				//체크해서 현재달에 빠진것이 있으면 만들어 넣는다.
				//DomainGroupGenerator.insertCntPerDomain();

				String currentDate = getDateString();
				StringTokenizer st = new StringTokenizer(currentDate, "/");
				String tmpYY = st.nextToken();
				String tmpMM = st.nextToken();

				//도메인명구하기
				String tmpDomainName = "etc";
				//if(CheckFormat.checkEmail(rMail))
				//{
				//	StringTokenizer st1=new StringTokenizer(rMail.toLowerCase(),"@");
				//    st1.nextToken();
				//    tmpDomainName=st1.nextToken();
				//}else
				//{
				//	tmpDomainName  ="etc";
				//}

				pstmt_stat_DOMAIN.setString(3, tmpDomainName);
				pstmt_stat_DOMAIN.setString(4, tmpYY);
				pstmt_stat_DOMAIN.setString(5, tmpMM);

				pstmt_stat_DOMAIN.executeUpdate();
			}

			con_work.commit();

			return_value = true;
		}
		catch(Exception e)
		{
			WorkDBManager.refreshConn();
			e.printStackTrace();

			//에러 로그에 남겨준다.
			ErrorLogGenerator.setErrorLogFormat("ResultLogManager", ReserveStatusCode.SQL_ERROR_TYPE,ReserveStatusCode.RESULT_LOG_INPUT_FAIL_COMMENT,mID);

			return_value = false;
		}
		finally
		{
			LOGGER.info(con_work);
			try { if( pstmt != null ) { pstmt.close(); } } catch(Exception e) {}
			try { if( pstmt_stat_MID != null ) { pstmt_stat_MID.close(); } } catch(Exception e) {}
			try { if( pstmt_stat_DOMAIN != null ) { pstmt_stat_DOMAIN.close(); } } catch(Exception e) {}
			//영맨 꽁수.. AutoCommit을 다시 원래대로 false로 바꿔준다.
			try { con_work.setAutoCommit(true); } catch(Exception e) {}
			try { WorkDBManager.releaseConnection(con_work); } catch(Exception e) {}

//				if( vStatistics_MID != null) vStatistics_MID.removeAllElements();
//				if( vStatistics_DOMAIN != null) vStatistics_DOMAIN.removeAllElements();
		}
		return return_value;
	}

	private static synchronized String getDateString()
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());

		SimpleDateFormat fmt = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.US);
		String tmpDate = fmt.format(cal.getTime());

		return tmpDate;
	}
}