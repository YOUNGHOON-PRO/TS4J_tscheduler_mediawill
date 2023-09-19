package com.tscheduler.manager;
/*
* 설명: ReserveManager에서 처리할 예약 리스트를 가져온후  한 예약건수마다
*		수신자 리스트를 뽑는다.
*/
import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.Hashtable;

import com.tscheduler.util.DataUnitInfo;
import com.tscheduler.util.Config;
import com.tscheduler.util.CheckFormat;
import com.tscheduler.util.ErrorLogGenerator;
import com.tscheduler.util.ReserveStatusCode;
import com.tscheduler.util.ErrorStatusCode;
import com.tscheduler.dbbroker.DBManager;
import com.tscheduler.dbbroker.WorkDBManager;

/**
 * 수신자 리스트 관리 클래스(복구 모듈)
 * @version 1.0
 * @author ymkim
 */
public class RecoveryReceiveManager
{
	/**복구시 MID에 대해서 예약정보 가져오는 쿼리*/
	private static final String RECOVERY_MID_QUERY = (new StringBuffer("SELECT ")
			.append("MID, SUBID, TID, SPOS, SNAME, SMAIL, SID, RPOS, ")
			.append("QUERY, CTNPOS, SUBJECT, CONTENTS, CDATE, SDATE, ")
			.append("STATUS,DBCODE,REFMID, ATTACHFILE01, ATTACHFILE02, ")
			.append("ATTACHFILE03, ATTACHFILE04, ATTACHFILE05, REQUEST_KEY ")
			.append("FROM TS_MAILQUEUE WHERE MID = ?")).toString();

	/**예약 메일 정보*/
	DataUnitInfo reserveInfo = null;

	/**한 폴더당 들어갈 eml파일 수*/
	int emlNumPerFolder = 0;
	/**한 파일에 들어갈 수신자 수*/
	int legacyRSListSize = 0;

	/**
	 * 생성자
	 * @version 1.0
	 * @author ymkim
	 */
	public RecoveryReceiveManager()
	{
		Config cfg = Config.getInstance();
		cfg.loadConfig(Config.MAIN_CFG);
		emlNumPerFolder = cfg.getEmlNumPerFolder();
		legacyRSListSize = cfg.getLegacyRSListSize();
	}


	/**
	 * 복구시 특정 MID에 대해서 특정 위치의 수신자 부터 수신자 리스트를 얻는다.
	 * @version 1.0
	 * @author ymkim
	 * @param mID 예약 메일 ID
	 * @param intSubNum 수신자리스트에서의 특정 위치
	 * @return File[] 수신자 리스트 파일
	 */
	public File[] getRecoveryUsrInfoList(String mID,int intSubNum)
	{
		Connection conn = null;
		ResultSet rs =null;
		PreparedStatement pstmt = null;

		File[] return_value = null;
		int preMakeEmlSize = emlNumPerFolder*(intSubNum-1);

		try
		{
			conn = DBManager.getConnection(Config.WORK_DB);
			pstmt = conn.prepareStatement(RECOVERY_MID_QUERY);
			pstmt.setString(1, mID);
			rs = pstmt.executeQuery();

			reserveInfo = new DataUnitInfo();

			String subID        = "";
			String tID 			= "";
			String sPos 		= "";
			String sID 			= "";
			String sName 		= "";
			String sMail 		= "";
			String refMID 		= "";
			String rPos			= "";
			String query		= "";
			String dbCode 		= "";
			String ctnPos 		= "";
			String subject 		= "";
			String contents 	= "";
			String status 		= "";
			String attach01 	= "";
			String attach02 	= "";
			String attach03 	= "";
			String attach04 	= "";
			String attach05 	= "";
			
			String requestKey 	="";

			if(rs.next())
			{
				subID   = rs.getString("SUBID");
				tID 	= rs.getString("TID");
				sPos	= rs.getString("SPOS");
				sID 	= rs.getString("SID");
				sName 	= rs.getString("SNAME");
				sMail 	= rs.getString("SMAIL");
				rPos	= rs.getString("RPOS");
				query 	= rs.getString("QUERY");
				ctnPos	= rs.getString("CTNPOS");
				subject	= rs.getString("SUBJECT");
				contents= rs.getString("CONTENTS");
				status	= rs.getString("STATUS");
				dbCode 	= rs.getString("DBCODE");
				refMID 	= rs.getString("REFMID");
				attach01= rs.getString("ATTACHFILE01");
				attach02= rs.getString("ATTACHFILE02");
				attach03= rs.getString("ATTACHFILE03");
				attach04= rs.getString("ATTACHFILE04");
				attach05= rs.getString("ATTACHFILE05");
				requestKey=rs.getString("REQUEST_KEY");

				reserveInfo.setString("MID", mID);
				reserveInfo.setString("SUBID", subID);
				reserveInfo.setString("TID", tID);
				reserveInfo.setString("SPOS", sPos);
				reserveInfo.setString("SID", sID);
				reserveInfo.setString("SNAME", sName);
				reserveInfo.setString("SMAIL", sMail);
				reserveInfo.setString("REFMID", refMID);
				reserveInfo.setString("RPOS", rPos);
				reserveInfo.setString("DBCODE", dbCode);
				reserveInfo.setString("QUERY", query);
				reserveInfo.setString("CTNPOS", ctnPos);
				reserveInfo.setString("SUBJECT", subject);
				reserveInfo.setString("CONTENTS", contents);
				reserveInfo.setString("STATUS", status);
				reserveInfo.setString("ATTACHFILE01", attach01);
				reserveInfo.setString("ATTACHFILE02", attach02);
				reserveInfo.setString("ATTACHFILE03", attach03);
				reserveInfo.setString("ATTACHFILE04", attach04);
				reserveInfo.setString("ATTACHFILE05", attach05);
				reserveInfo.setString("REQUEST_KEY", requestKey);
			}

			if( rPos.equals("0") || rPos.toUpperCase().equals("L") ) {	//WorkDB쪽에 있는 리스트일경우
				return getRecoveryUsrInfoListFromDB(reserveInfo,preMakeEmlSize);
			}
			else if( rPos.equals("1") || rPos.toUpperCase().equals("Q") ) {	//쿼리일 경우
				//쿼리일경우만 LegacyDB에서 값을 가져오는 경우이다.
				Hashtable hash = getLegacyDBInfo(mID, dbCode);
				return getRecoveryUsrInfoListFromDB(reserveInfo,query,hash,preMakeEmlSize);
			}
			else if( rPos.equals("2") || rPos.toUpperCase().equals("F") ) {	//파일일 경우
				return getRecoveryUsrInfoListFromFile(reserveInfo,query,preMakeEmlSize);
			}
			else {
				return null;
			}
		}
		catch(Exception e)
		{
			WorkDBManager.refreshConn();
			e.printStackTrace();

			return_value = null;
		}
		finally
		{
			try
			{
				WorkDBManager.releaseConnection(conn);
				if( pstmt != null ) {
					pstmt.close();
				}
				if( rs != null ) {
					rs.close();
				}
			}
			catch(Exception e) {
			}
		}
		return return_value;
	}



	/**
	 * legacyDB의 정보를 가져온다.
	 * @version 1.0
	 * @author ymkim
	 * @param mID 예약 메일 ID
	 * @param dbCode LegacyDB 연결 코드
	 * @return Hashtable LegacyDB연결 정보
	 */
	public Hashtable getLegacyDBInfo(String mID ,String dbCode)
	{
		Hashtable legacyInfo = new Hashtable();
		Connection conn = null;

		ResultSet rs =null;
		PreparedStatement pstmt = null;

		try
		{
			conn = DBManager.getConnection(Config.WORK_DB);
			pstmt = conn.prepareStatement(ReceiveManager.LEGACY_INFO_QUERY);
			pstmt.setString(1, dbCode);

			rs = pstmt.executeQuery();

			String driver;
			String dbURL;
			String userID;
			String userPass;

			if(rs.next())
			{
				driver = rs.getString("DRIVER");
				dbURL = rs.getString("DBURL");
				userID = rs.getString("USERID");
				userPass = rs.getString("USERPASS");

				legacyInfo.put("DRIVER", driver);
				legacyInfo.put("DBURL", dbURL);
				legacyInfo.put("USERID", userID);
				legacyInfo.put("USERPASS", userPass);
			}
		}
		catch(Exception e)
		{
			WorkDBManager.refreshConn();
			e.printStackTrace();

			//에러 로그를 남겨준다.
			ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(), ReserveStatusCode.SQL_ERROR_TYPE,ReserveStatusCode.LEGACY_INFO_FAIL_COMMENT,mID);

			legacyInfo = null;
		}
		finally
		{
			try
			{
				WorkDBManager.releaseConnection(conn);
				if( pstmt != null ) {
					pstmt.close();
				}
				if( rs != null ) {
					rs.close();
				}
			}
			catch(Exception e) {
			}
		}
		return legacyInfo;
	}


	/**
	 * WorkDB에 있는 수신자 리스트를 얻는다.(중복 방지)
	 * @version 1.0
	 * @author ymkim
	 * @param reserveInfo 예약메일 정보
	 * @param preMakeEmlSize 이미 생성된 eml파일의 갯수
	 * @return File[] 수신자 리스트 파일
	 */
	public File[] getRecoveryUsrInfoListFromDB(DataUnitInfo reserveInfo, int preMakeEmlSize )
	{
		Connection conn = null;

		ResultSet rs =null;
		PreparedStatement pstmt = null;

		String mID = reserveInfo.getString("MID");
		String subID = reserveInfo.getString("SUBID");
		String tID = reserveInfo.getString("TID");
		String sID = reserveInfo.getString("SID");
		String sName = reserveInfo.getString("SNAME");
		String sMail = reserveInfo.getString("SMAIL");
		String refMID = reserveInfo.getString("REFMID");

		//에러로그
		ArrayList errorLogInfoList = new ArrayList();

		String rUserMIDFolder = LogFileManager.RECEIVER_LOG_FOLDER+mID+File.separator;
		File rListFilePath = new File(rUserMIDFolder);
		if( !(rListFilePath.isDirectory()) ) {
			rListFilePath.mkdir();
		}

		File[] rListFileArray = null;

		try
		{
			conn = DBManager.getConnection(Config.WORK_DB);
			pstmt = conn.prepareStatement(ReceiveManager.RUSER_INFO_QUERY);
			pstmt.setString(1, mID);
			rs = pstmt.executeQuery();

			String rID;
			String rName;
			String rMail;

			Hashtable errorLogInfo = null;

			StringBuffer sb = new StringBuffer();
			String receiverStr = "";
			int receiverNumber = 0;

			int rUserCnt = 0;

			//수신자가 없다.
			boolean noReceiver = true;

			while(rs.next())
			{
				//수신자가 있다.
				noReceiver = false;

				if(preMakeEmlSize > rUserCnt++) continue;

				rID = rs.getString("RID");
				rName = rs.getString("RNAME");
				rMail = rs.getString("RMAIL");

				//Null을 막아준다.
				if( rID == null ) {
					rID = "";
				}

				if( rName == null ) {
					rName = "";
				}

				if( CheckFormat.checkEmail(rMail) && !(rID.equals("")) && !(rName.equals("")))
				{
					receiverStr = (new StringBuffer(rMail).append(Config.DELIMITER)
								   .append(rName).append(Config.DELIMITER)
								   .append(rID).append(Config.NEW_LINE)).toString();
					sb.append(receiverStr);
					receiverNumber++;

					//LEGACY_RS_LIST_SIZE개가 되면 파일로 저장한다.
					if( receiverNumber % legacyRSListSize == 0 )
					{
						makeRUserFileList(receiverNumber, sb,rUserMIDFolder);
						sb = new StringBuffer();
					}
				}
				else
				{
					errorLogInfo = new Hashtable();
					errorLogInfo.put("MID", mID);
					errorLogInfo.put("SUBID", subID);
					errorLogInfo.put("TID", tID);
					errorLogInfo.put("SID", sID);
					errorLogInfo.put("SNAME", sName);
					errorLogInfo.put("SMAIL", sMail);
					errorLogInfo.put("REFMID", refMID);
					errorLogInfo.put("RID", rID);
					errorLogInfo.put("RNAME", rName);
					errorLogInfo.put("RMAIL", rMail);
					errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_RMAIL);

					//이메일의 문법이 틀릴때에는 실패에 대한 상태를 넣어준다.
					reserveInfo.setString("STATUS",ReserveStatusCode.R_EMAIL_ERROR);
					//복구를 감안해서 예약리스트 Status를 파일로 저장한다.
					LogFileManager.setReserveStatusUpdate(mID, subID, ReserveStatusCode.R_EMAIL_ERROR);
					ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(), ReserveStatusCode.R_EMAIL_ERROR_TYPE,ReserveStatusCode.R_EMAIL_ERROR_COMMENT,mID);
					errorLogInfoList.add(errorLogInfo);
				}
			}

			if( receiverNumber != 0 )
			{
				makeRUserFileList(receiverNumber, sb,rUserMIDFolder);

				//파일이 완성된 후에는 파일리스트를 리턴한다.
				if( rListFilePath.isDirectory() ) {
					rListFileArray = rListFilePath.listFiles();
				}
			}

			if( noReceiver )	//수신자가 없다면..(Recipientinfo테이블에 MID에 대한 내용이 없다면)
			{
				//이메일의 문법이 틀릴때에는 실패에 대한 상태를 넣어준다.
				reserveInfo.setString("STATUS",ReserveStatusCode.R_LIST_EXTRACT_ERROR);
				//복구를 감안해서 예약리스트 Status를 파일로 저장한다.
				LogFileManager.setReserveStatusUpdate(mID, subID, ReserveStatusCode.R_LIST_EXTRACT_ERROR);
				ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(), ReserveStatusCode.R_LIST_EXTRACT_ERROR_TYPE,ReserveStatusCode.R_LIST_NOBODY_ERROR_COMMENT,mID);

				errorLogInfo = new Hashtable();
				errorLogInfo.put("MID", mID);
				errorLogInfo.put("SUBID", subID);
				errorLogInfo.put("TID", tID);
				errorLogInfo.put("SID", sID);
				errorLogInfo.put("SNAME", sName);
				errorLogInfo.put("SMAIL", sMail);
				errorLogInfo.put("REFMID", refMID);
				errorLogInfo.put("RID", ReserveStatusCode.NO_USERID);
				errorLogInfo.put("RNAME", ReserveStatusCode.NO_USERNAME);
				errorLogInfo.put("RMAIL", ReserveStatusCode.NO_USERMAIL);
				errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_RMAIL);
				errorLogInfoList.add(errorLogInfo);
			}

			if( errorLogInfoList.size() != 0 ) {
				ResultLogManager.InsertResultLog(errorLogInfoList);
			}
		}
		catch(Exception e)
		{
			WorkDBManager.refreshConn();
			e.printStackTrace();

			reserveInfo.setString("STATUS",ReserveStatusCode.R_LIST_EXTRACT_ERROR);
			//복구를 감안해서 예약리스트 Status를 파일로 저장한다.
			LogFileManager.setReserveStatusUpdate(mID, subID, ReserveStatusCode.R_LIST_EXTRACT_ERROR);

			//에러 로그를 남겨준다.
			ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(), ReserveStatusCode.R_LIST_EXTRACT_ERROR_TYPE,ReserveStatusCode.R_LIST_EXTRACT_DB_ERROR_COMMENT,mID);

			rListFileArray = null;
		}
		finally
		{
			try
			{
				WorkDBManager.releaseConnection(conn);
				if( pstmt != null ) {
					pstmt.close();
				}
				if( rs != null ) {
					rs.close();
				}
			}
			catch(Exception e) {
			}
		}
		return rListFileArray;
	}

	/**
	 * LegacyDB에 있는 수신자 리스트를 얻는다.(중복 방지)
	 * @version 1.0
	 * @author ymkim
	 * @param reserveInfo 예약 메일 정보
	 * @param query 수신자를 얻는 SQL Query
	 * @param legacyInfo LegacyDB 연결 정보
	 * @param preMakeEmlSize 이미 생성된 eml파일의 갯수
	 * @return File[] 수신자 리스트 파일
	 */
	public File[] getRecoveryUsrInfoListFromDB(DataUnitInfo reserveInfo, String query,
			Hashtable legacyInfo, int preMakeEmlSize)
	{
		//에러 로그 리스트
		ArrayList errorLogInfoList = new ArrayList();
		Hashtable errorLogInfo = null;

		Connection conn = null;
		ResultSet rs =null;
		PreparedStatement pstmt = null;

		DBManager.setLDBInfo(legacyInfo);

		String mID = reserveInfo.getString("MID");
		String subID = reserveInfo.getString("SUBID");
		String tID = reserveInfo.getString("TID");
		String sID = reserveInfo.getString("SID");
		String sName = reserveInfo.getString("SNAME");
		String sMail = reserveInfo.getString("SMAIL");
		String refMID = reserveInfo.getString("REFMID");

		String rUserMIDFolder = LogFileManager.RECEIVER_LOG_FOLDER+mID+File.separator;
		File rListFilePath = new File(rUserMIDFolder);
		if( !(rListFilePath.isDirectory()) ) {
			rListFilePath.mkdir();
		}

		//컨넥션이 널이면 즉! legacyDB에서 컨넥션을 가져오지 못했다면.... 로그를 남긴다.
		if( conn == null )
		{
			errorLogInfo = new Hashtable();
			errorLogInfo.put("MID", mID);
			errorLogInfo.put("SUBID", subID);
			errorLogInfo.put("TID", tID);
			errorLogInfo.put("SID", sID);
			errorLogInfo.put("SNAME", sName);
			errorLogInfo.put("SMAIL", sMail);
			errorLogInfo.put("REFMID", refMID);
			errorLogInfo.put("RID", ReserveStatusCode.NO_USERID);
			errorLogInfo.put("RNAME", ReserveStatusCode.NO_USERNAME);
			errorLogInfo.put("RMAIL", ReserveStatusCode.NO_USERMAIL);
			errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_RMAIL);
			errorLogInfoList.add(errorLogInfo);
			ResultLogManager.InsertResultLog(errorLogInfoList);

			reserveInfo.setString("STATUS",ReserveStatusCode.R_LIST_EXTRACT_ERROR);
			//복구를 감안해서 예약리스트 Status를 파일로 저장한다.
			LogFileManager.setReserveStatusUpdate(mID, subID, ReserveStatusCode.R_LIST_EXTRACT_ERROR);
			//에러 로그를 남겨준다.
			ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(), ReserveStatusCode.R_LIST_EXTRACT_ERROR_TYPE,ReserveStatusCode.R_LIST_EXTRACT_DB_ERROR_COMMENT,mID);
			return null;
		}

		File[] rListFileArray = null;

		try
		{
			conn = DBManager.getConnection(Config.LEGACY_DB);
			pstmt = conn.prepareStatement(query);
			//pstmt.setString(1, mID);
			rs = pstmt.executeQuery();

			String rID;
			String rName;
			String rMail;

			StringBuffer sb = new StringBuffer();
			String receiverStr = "";
			int receiverNumber = 0;
			int rUserCnt = 0;

			//수신자가 없다.
			boolean noReceiver = true;

			while( rs.next() )
			{
				//수신자가 있다.
				noReceiver = false;

				if( preMakeEmlSize > rUserCnt++ ) {
					continue;
				}

				rID = rs.getString("RID");
				rName = rs.getString("RNAME");
				rMail = rs.getString("RMAIL");

				//Null을 막아준다.
				if( rID == null ) {
					rID = "";
				}
				if( rName == null ) {
					rName = "";
				}

				//if(CheckFormat.checkEmail(rMail))
				if( CheckFormat.checkEmail(rMail) && !(rID.equals("")) && !(rName.equals("")) )
				{
					receiverStr = (new StringBuffer(rMail).append(Config.DELIMITER)
								   .append(rName).append(Config.DELIMITER)
								   .append(rID).append(Config.NEW_LINE)).toString();
					sb.append(receiverStr);
					receiverNumber++;

					//LEGACY_RS_LIST_SIZE개가 되면 파일로 저장한다.
					if( receiverNumber % legacyRSListSize == 0 ) {
						makeRUserFileList(receiverNumber, sb, rUserMIDFolder);
						sb = new StringBuffer();
					}
				}
				else
				{
					errorLogInfo = new Hashtable();
					errorLogInfo.put("MID", mID);
					errorLogInfo.put("SUBID", subID);
					errorLogInfo.put("TID", tID);
					errorLogInfo.put("SID", sID);
					errorLogInfo.put("SNAME", sName);
					errorLogInfo.put("SMAIL", sMail);
					errorLogInfo.put("REFMID", refMID);
					errorLogInfo.put("RID", rID);
					errorLogInfo.put("RNAME", rName);
					errorLogInfo.put("RMAIL", rMail);
					errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_RMAIL);

					//이메일의 문법이 틀릴때에는 실패에 대한 상태를 넣어준다.
					reserveInfo.setString("STATUS",ReserveStatusCode.R_EMAIL_ERROR);
					//복구를 감안해서 예약리스트 Status를 파일로 저장한다.
					LogFileManager.setReserveStatusUpdate(mID, subID, ReserveStatusCode.R_EMAIL_ERROR);
					ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(), ReserveStatusCode.R_EMAIL_ERROR_TYPE,ReserveStatusCode.R_EMAIL_ERROR_COMMENT,mID);
					errorLogInfoList.add(errorLogInfo);
				}
			}

			if( receiverNumber != 0 )
			{
				makeRUserFileList(receiverNumber, sb,rUserMIDFolder);

				//파일이 완성된 후에는 파일리스트를 리턴한다.

				if(rListFilePath.isDirectory()) {
					rListFileArray = rListFilePath.listFiles();
				}
			}

			if(noReceiver)	//수신자가 없다면..(Recipientinfo테이블에 MID에 대한 내용이 없다면)
			{
				//이메일의 문법이 틀릴때에는 실패에 대한 상태를 넣어준다.
				reserveInfo.setString("STATUS",ReserveStatusCode.R_LIST_EXTRACT_ERROR);
				//복구를 감안해서 예약리스트 Status를 파일로 저장한다.
				LogFileManager.setReserveStatusUpdate(mID, subID, ReserveStatusCode.R_LIST_EXTRACT_ERROR);
				ErrorLogGenerator.setErrorLogFormat("ReceiveManager", ReserveStatusCode.R_LIST_EXTRACT_ERROR_TYPE,ReserveStatusCode.R_LIST_NOBODY_ERROR_COMMENT,mID);

				errorLogInfo = new Hashtable();
				errorLogInfo.put("MID", mID);
				errorLogInfo.put("SUBID", subID);
				errorLogInfo.put("TID", tID);
				errorLogInfo.put("SID", sID);
				errorLogInfo.put("SNAME", sName);
				errorLogInfo.put("SMAIL", sMail);
				errorLogInfo.put("REFMID", refMID);
				errorLogInfo.put("RID", ReserveStatusCode.NO_USERID);
				errorLogInfo.put("RNAME", ReserveStatusCode.NO_USERNAME);
				errorLogInfo.put("RMAIL", ReserveStatusCode.NO_USERMAIL);
				errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_RMAIL);
				errorLogInfoList.add(errorLogInfo);
			}

			if( errorLogInfoList.size() != 0 ) {
				ResultLogManager.InsertResultLog(errorLogInfoList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();

			reserveInfo.setString("STATUS",ReserveStatusCode.R_LIST_EXTRACT_ERROR);

			//복구를 감안해서 예약리스트 Status를 파일로 저장한다.
			LogFileManager.setReserveStatusUpdate(mID, subID, ReserveStatusCode.R_LIST_EXTRACT_ERROR);
			//에러 로그를 남겨준다.
			ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(), ReserveStatusCode.R_LIST_EXTRACT_ERROR_TYPE,ReserveStatusCode.R_LIST_EXTRACT_SQL_ERROR_COMMENT,mID);

			rListFileArray = null;
		}
		finally
		{
			try
			{
				if( conn != null ) {
					conn.close();
					conn = null;
				}
			}
			catch(Exception e) {}

			try
			{
				if( pstmt != null ) {
					pstmt.close();
					pstmt = null;
				}
			}
			catch(Exception e) {}

			try
			{
				if( rs != null ) {
					rs.close();
					rs = null;
				}
			}
			catch(Exception e) {}
		}
		return rListFileArray;
	}


	/**
	 * File에 있는 수신자 리스트를 얻는다.(중복 방지)
	 * @version 1.0
	 * @author ymkim
	 * @param reserveInfo 예약 메일 정보
	 * @param query 수신자 리스트가 있는 파일의 경로
	 * @param preMakeEmlSize 이미 생성된 eml파일의 갯수
	 * @return File[] 수신자 리스트 파일
	 */
	public File[] getRecoveryUsrInfoListFromFile(DataUnitInfo reserveInfo,String query, int preMakeEmlSize)
	{
		String mID = reserveInfo.getString("MID");
		String subID = reserveInfo.getString("SUBID");
		String tID = reserveInfo.getString("TID");
		String sID = reserveInfo.getString("SID");
		String sName = reserveInfo.getString("SNAME");
		String sMail = reserveInfo.getString("SMAIL");
		String refMID = reserveInfo.getString("REFMID");

		//에러 로그 리스트
		ArrayList errorLogInfoList = new ArrayList();

		String rUserMIDFolder = LogFileManager.RECEIVER_LOG_FOLDER+mID+File.separator;
		File rListFilePath = new File(rUserMIDFolder);
		if( !(rListFilePath.isDirectory()) ) {
			rListFilePath.mkdir();
		}

		File[] rListFileArray = null;

		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(new File(query)));
			String tempStr = "";
			String rName = "";
			String rID = "";
			String rMail = "";

			Hashtable errorLogInfo = null;

			StringBuffer sb = new StringBuffer();
			String receiverStr = "";
			int receiverNumber = 0;

			int rUserCnt = 0;

			//수신자가 없다.
			boolean noReceiver = true;

			while( (tempStr = br.readLine()) != null )
			{
				//수신자가 있다.
				noReceiver = false;

				if( preMakeEmlSize > rUserCnt++ ) {
					continue;
				}

				StringTokenizer st = new StringTokenizer(tempStr,Config.DELIMITER);
				if( st.hasMoreTokens() )
				{
					try
					{
						rMail = st.nextToken();
						rName = st.nextToken();
						rID = st.nextToken();

						//Null을 막아준다.
						if( rID == null ) {
							rID = "";
						}

						if( rName == null ) {
							rName = "";
						}

						//if(CheckFormat.checkEmail(rMail))
						if( CheckFormat.checkEmail(rMail) && !(rID.equals("")) && !(rName.equals("")) )
						{
							receiverStr = (new StringBuffer(rMail).append(Config.DELIMITER)
									.append(rName).append(Config.DELIMITER)
									.append(rID).append(Config.NEW_LINE)).toString();
							sb.append(receiverStr);
							receiverNumber++;

							//LEGACY_RS_LIST_SIZE개가 되면 파일로 저장한다.
							if( receiverNumber % legacyRSListSize == 0 )
							{
								makeRUserFileList(receiverNumber, sb,rUserMIDFolder);
								sb = new StringBuffer();
							}
						}
						else
						{
							errorLogInfo = new Hashtable();
							errorLogInfo.put("MID", mID);
							errorLogInfo.put("SUBID", subID);
							errorLogInfo.put("TID", tID);
							errorLogInfo.put("SID", sID);
							errorLogInfo.put("SNAME", sName);
							errorLogInfo.put("SMAIL", sMail);
							errorLogInfo.put("REFMID", refMID);
							errorLogInfo.put("RID", rID);
							errorLogInfo.put("RNAME", rName);
							errorLogInfo.put("RMAIL", rMail);
							errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_RMAIL);

							//이메일의 문법이 틀릴때에는 실패에 대한 상태를 넣어준다.
							reserveInfo.setString("STATUS",ReserveStatusCode.R_EMAIL_ERROR);
							//복구를 감안해서 예약리스트 Status를 파일로 저장한다.
							LogFileManager.setReserveStatusUpdate(mID, subID, ReserveStatusCode.R_EMAIL_ERROR);
							ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(), ReserveStatusCode.R_EMAIL_ERROR_TYPE,ReserveStatusCode.R_EMAIL_ERROR_COMMENT,mID);
							errorLogInfoList.add(errorLogInfo);
						}
					}
					catch(NoSuchElementException exp)
					{
						reserveInfo.setString("STATUS",ReserveStatusCode.R_LIST_EXTRACT_ERROR);
						//복구를 감안해서 예약리스트 Status를 파일로 저장한다.
						LogFileManager.setReserveStatusUpdate(mID, subID, ReserveStatusCode.R_LIST_EXTRACT_ERROR);
						//에러 로그를 남겨준다.
						ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(), ReserveStatusCode.R_LIST_EXTRACT_ERROR_TYPE,ReserveStatusCode.R_LIST_EXTRACT_FILE_ERROR_COMMENT,mID);
						break;
					}
				}
			}

			if( receiverNumber != 0 )
			{
				makeRUserFileList(receiverNumber, sb,rUserMIDFolder);
				//파일이 완성된 후에는 파일리스트를 리턴한다.

				if( rListFilePath.isDirectory() ) {
					rListFileArray = rListFilePath.listFiles();
				}
			}

			if( noReceiver )	//수신자가 없다면..(Recipientinfo테이블에 MID에 대한 내용이 없다면)
			{
				//이메일의 문법이 틀릴때에는 실패에 대한 상태를 넣어준다.
				reserveInfo.setString("STATUS",ReserveStatusCode.R_LIST_EXTRACT_ERROR);
				//복구를 감안해서 예약리스트 Status를 파일로 저장한다.
				LogFileManager.setReserveStatusUpdate(mID, subID, ReserveStatusCode.R_LIST_EXTRACT_ERROR);
				ErrorLogGenerator.setErrorLogFormat("ReceiveManager", ReserveStatusCode.R_LIST_EXTRACT_ERROR_TYPE,ReserveStatusCode.R_LIST_NOBODY_ERROR_COMMENT,mID);

				errorLogInfo = new Hashtable();
				errorLogInfo.put("MID", mID);
				errorLogInfo.put("SUBID", subID);
				errorLogInfo.put("TID", tID);
				errorLogInfo.put("SID", sID);
				errorLogInfo.put("SNAME", sName);
				errorLogInfo.put("SMAIL", sMail);
				errorLogInfo.put("REFMID", refMID);
				errorLogInfo.put("RID", ReserveStatusCode.NO_USERID);
				errorLogInfo.put("RNAME", ReserveStatusCode.NO_USERNAME);
				errorLogInfo.put("RMAIL", ReserveStatusCode.NO_USERMAIL);
				errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_RMAIL);
				errorLogInfoList.add(errorLogInfo);
			}

			if( errorLogInfoList.size() != 0 ) {
				ResultLogManager.InsertResultLog(errorLogInfoList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			rListFileArray = null;
		}
		finally
		{
			try
			{
				if( br != null ) {
					br.close();
				}
			}
			catch(Exception e) {
			}
		}
		return rListFileArray;
	}

	/**
	 * 수신자를 얻어와서 파일로 만든다.
	 * @version 1.0
	 * @author ymkim
	 * @param receiverNumber 수신자 수
	 * @param sb 수신자 정보 내용
	 * @param destPath 저장될 파일 경로
	 * @return boolean true - 파일 생성 성공, false - 파일 생성 실패
	 */
	public boolean makeRUserFileList(int receiverNumber, StringBuffer sb, String destPath)
	{
		int fileNum = receiverNumber/legacyRSListSize;
		FileWriter fw  = null;
		StringBuffer sbuffer = null;
		boolean bResult = true;

		try
		{
			sbuffer = new StringBuffer();
			fw = new FileWriter(new File(sbuffer.append(destPath).append(LogFileManager.RECEIVER_FILE_NAME)
					.append(fileNum).append(LogFileManager.LOG_EXT).toString()));
			sbuffer = null;
			fw.write(sb.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			bResult = false;
		}
		finally
		{
			try
			{
				if( fw != null ) {
					fw.close();
					fw = null;
				}
			}
			catch(Exception e) {
			}
		}
		return bResult;
	}

	public DataUnitInfo getReserveInfoUnit() {
		return reserveInfo;
	}
}
