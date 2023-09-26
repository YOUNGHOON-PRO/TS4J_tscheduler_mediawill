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
import com.tscheduler.util.DataUnitInfoList;
import com.tscheduler.util.EncryptUtil;
import com.tscheduler.util.Config;
import com.custinfo.safedata.*;
import com.tscheduler.util.CheckFormat;
import com.tscheduler.util.ErrorLogGenerator;
import com.tscheduler.util.ReserveStatusCode;
import com.tscheduler.util.ErrorStatusCode;
import com.tscheduler.dbbroker.DBManager;
import com.tscheduler.dbbroker.WorkDBManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 수신자 리스트 관리 클래스
 * @version 1.0
 * @author ymkim
 */
public class ReceiveManager {
	
  private static final Logger LOGGER = LogManager.getLogger(Test.class.getName());
	
  /**예약 메일 리스트*/
  private DataUnitInfoList reserveList;
  /**예약 메일*/
  private DataUnitInfo reserveInfo;

  /**발송그룹저장테이블에서 MID에 해당하는 값을 가져오는 쿼리*/
  public static final String RUSER_INFO_QUERY = "SELECT MID, SUBID, RID, RNAME, RMAIL, ENCKEY, MAP1, MAP2, MAP3, MAP4, MAP5, MAP6, MAP7, MAP8, MAP9, MAP10, MAP11, MAP12, MAP13, MAP14, MAP15 FROM TS_RECIPIENTINFO WHERE MID = ? AND SUBID = ?";

  /**LegacyDB의 정보를 가져오는 쿼리*/
  public static final String LEGACY_INFO_QUERY =
      "SELECT DRIVER, DBURL, USERID, USERPASS FROM TS_DBCONINFO WHERE DBCODE = ?";

  /**파일 하나에 들어가는 수신자의 수*/
  int legacyRSListSize = 0;

  /**
   * 생성자
   * @version 1.0
   * @author ymkim
   */
  public ReceiveManager() {
    Config cfg = Config.getInstance();
    cfg.loadConfig(Config.MAIN_CFG);
    legacyRSListSize = cfg.getLegacyRSListSize();
  }

  /**
   * 예약 메일에 대한 수신자 리스트 파일을 얻는다.
   * @version 1.0
   * @author ymkim
   * @param reserveList 예약 메일 리스트
   * @return File[] 수신자 리스트 파일
   */
  public File[] getReceiveUserListFromReserve(DataUnitInfoList reserveList) {
    //얻어온 리스트중에서 하나의 DataUnitInfo를 얻어온다.
    if ( (reserveInfo = reserveList.getSequenceDataUnitInfo()) != null) {
      String rPos = reserveInfo.getString("RPOS");
      String mID = reserveInfo.getString("MID");
      String subID = reserveInfo.getString("SUBID");
      String dbCode = reserveInfo.getString("DBCODE");
      String query = reserveInfo.getString("QUERY");
      String status = reserveInfo.getString("STATUS");

      //상태가 0(대기) 상태가 아니라면 이것은 에러이므로 수신자 리스트를 가져오지 않는다.
      if (! (status.equals("0"))) {
        return null;
      }

      if (rPos.equals("0") || rPos.toUpperCase().equals("L")) { // WorkDB쪽에 있는 리스트일경우
        return getRecUsrInfoListFromDB(reserveInfo);
      }
      else if (rPos.equals("1") || rPos.toUpperCase().equals("Q")) { //쿼리일 경우
        //쿼리일경우만 LegacyDB에서 값을 가져오는 경우이다.
        Hashtable hash = getLegacyDBInfo(mID, dbCode);
        return getRecUsrInfoListFromDB(reserveInfo, query, hash);
      }
      else if (rPos.equals("2") || rPos.toUpperCase().equals("F")) { //파일일 경우
        return getRecUsrInfoListFromFile(reserveInfo, query);
      }
      else {
        return null;
      }
    }
    else {
      return null;
    }
  }

  /**
   * legacyDB의 정보를 가져온다.
   * @version 1.0
   * @author ymkim
   * @param mID 예약 메일 ID
   * @param dbCode LegacyDB 연결 코드
   * @return Hashtable LegacyDB연결 정보
   */
  public Hashtable getLegacyDBInfo(String mID, String dbCode) {
    Hashtable legacyInfo = new Hashtable();
    Connection conn = DBManager.getConnection(Config.WORK_DB);

    ResultSet rs = null;
    PreparedStatement pstmt = null;
    
    Config cfg = Config.getInstance();
    
    //복호화
	String ALGORITHM = "PBEWithMD5AndDES";
	String KEYSTRING = "ENDERSUMS";
	EncryptUtil enc =  new EncryptUtil();    
  		
    
    try {
      pstmt = conn.prepareStatement(LEGACY_INFO_QUERY);
      pstmt.setString(1, dbCode);

      rs = pstmt.executeQuery();

      String driver = "";
      String dbURL = "";
      String userID = "";
      String userPass = "";

      if (rs.next()) {
        driver = rs.getString("DRIVER");
        dbURL = rs.getString("DBURL");
        userID = rs.getString("USERID");
        userPass = rs.getString("USERPASS");
        
      //복호화
		if("Y".equals(cfg.getDBUserPassYN())) {
			userPass = enc.getJasyptDecryptedString(ALGORITHM, KEYSTRING, userPass);
		}

        legacyInfo.put("DRIVER", driver);
        legacyInfo.put("DBURL", dbURL);
        legacyInfo.put("USERID", userID);
        legacyInfo.put("USERPASS", userPass);
      }
    }
    catch (Exception e) {
    	LOGGER.error(e);
      WorkDBManager.refreshConn();
      //e.printStackTrace();
      LogFileManager.runLogWriter("getLegacyDBInfo", e.toString());

      //에러 로그를 남겨준다.
      ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                          ReserveStatusCode.SQL_ERROR_TYPE,
                                          ReserveStatusCode.
                                          LEGACY_INFO_FAIL_COMMENT, mID);

      legacyInfo = null;
    }
    finally {
      try {
        WorkDBManager.releaseConnection(conn);
      }
      catch (Exception e) {LOGGER.error(e);}

      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception e) {LOGGER.error(e);}

      try {
        if (rs != null) {
          rs.close();
        }
      }
      catch (Exception e) {LOGGER.error(e);}
    }
    return legacyInfo;
  }

  /**
   * WorkDB에 있는 수신자 리스트를 얻는다.
   * @version 1.0
   * @author ymkim
   * @param reserveInfo 예약메일 정보
   * @return File[] 수신자 리스트 파일
   */
  public File[] getRecUsrInfoListFromDB(DataUnitInfo reserveInfo) {
    Connection conn = null;
    conn = DBManager.getConnection(Config.WORK_DB);

    ResultSet rs = null;
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

    String rUserMIDFolder = (new StringBuffer().append(LogFileManager.
        RECEIVER_LOG_FOLDER)
                             .append(mID).append(File.separator)).toString();
    File rListFilePath = new File(rUserMIDFolder);

    if (! (rListFilePath.isDirectory())) {
      rListFilePath.mkdir();
    }

    File[] rListFileArray = null;
    
    ///암호화/복호화
    String ALGORITHM = "PBEWithMD5AndDES";
    String KEYSTRING = "ENDERSUMS";
    EncryptUtil enc =  new EncryptUtil();
    
    CustInfoSafeData safeDbEnc = new CustInfoSafeData();

    Config cfg = Config.getInstance();
    
    try {
      pstmt = conn.prepareStatement(RUSER_INFO_QUERY);

      pstmt.setString(1, mID);
      pstmt.setInt(2, Integer.parseInt(subID));
      rs = pstmt.executeQuery();

      String rID;
      String rName;
      String rMail;
      String enckey;
      String map1;
      String map2;
      String map3;
      String map4;
      String map5;
      String map6;
      String map7;
      String map8;
      String map9;
      String map10;
      String map11;
      String map12;
      String map13;
      String map14;
      String map15;

      Hashtable errorLogInfo = null;

      StringBuffer sb = new StringBuffer();
      String receiverStr = "";
      int receiverNumber = 0;

      //수신자가 없다.
      boolean noReceiver = true;

      while (rs.next()) {
        //수신자가 있다.
        noReceiver = false;

        rID = rs.getString("RID");
        rName = rs.getString("RNAME");
        rMail = rs.getString("RMAIL");
        enckey = rs.getString("ENCKEY");
        map1 = rs.getString("MAP1");
        map2 = rs.getString("MAP2");
        map3 = rs.getString("MAP3");
        map4 = rs.getString("MAP4");
        map5 = rs.getString("MAP5");
        map6 = rs.getString("MAP6");
        map7 = rs.getString("MAP7");
        map8 = rs.getString("MAP8");
        map9 = rs.getString("MAP9");
        map10 = rs.getString("MAP10");
        map11 = rs.getString("MAP11");
        map12 = rs.getString("MAP12");
        map13 = rs.getString("MAP13");
        map14 = rs.getString("MAP14");
        map15 = rs.getString("MAP15");

        //복호화
        if("Y".equals(cfg.getEnc_yn())) {
        	//rMail = safeDbEnc.getDecrypt(rMail, "NOT_RNNO");
       		rMail = enc.getJasyptDecryptedFixString(ALGORITHM, KEYSTRING, rMail);
    		}	
        
        //Null을 막아준다.
        if (rID == null) {
          rID = "";
        }

        if (rName == null) {
          rName = "";
        }

        
        if (map1 == null || map1.equals("")) {
        	map1 = "flage";
          }
        if (enckey == null || enckey.equals("")) {
        	enckey = "flage";
          }
        if (map2 == null || map2.equals("")) {
        	map2 = "flage";
          }
        if (map3 == null || map3.equals("")) {
        	map3 = "flage";
          }
        if (map4 == null || map4.equals("")) {
        	map4 = "flage";
        }
        if (map5 == null || map5.equals("")) {
        	map5 = "flage";
        } 
        if (map6 == null || map6.equals("")) {
        	map6 = "flage";
        } 
        if (map7 == null || map7.equals("")) {
        	map7 = "flage";
        } 
        if (map8 == null || map8.equals("")) {
        	map8 = "flage";
        } 
        if (map9 == null || map9.equals("")) {
        	map9 = "flage";
        } 
        if (map10 == null || map10.equals("")) {
        	map10 = "flage";
        } 
        if (map11 == null || map11.equals("")) {
        	map11 = "flage";
        } 
        if (map12 == null || map12.equals("")) {
        	map12 = "flage";
        } 
        if (map13 == null || map13.equals("")) {
        	map13 = "flage";
        } 
        if (map14 == null || map14.equals("")) {
        	map14 = "flage";
        } 
        if (map15 == null || map15.equals("")) {
        	map15 = "flage";
        } 
        
        
        //if(CheckFormat.checkEmail(rMail)			//2003.10.22 영맨고침
        //기존에 이메일만 체크하던것에서 ... rID와 rName도 체크한다.
        if (CheckFormat.checkEmail(rMail) && ! (rID.equals("")) &&
            ! (rName.equals(""))) {
          receiverStr = (new StringBuffer(rMail).append(Config.DELIMITER)
                         .append(rName).append(Config.DELIMITER)
                         .append(rID).append(Config.DELIMITER)
                         .append(enckey).append(Config.DELIMITER)
				         .append(map1).append(Config.DELIMITER)
				         .append(map2).append(Config.DELIMITER)
				         .append(map3).append(Config.DELIMITER)
				         .append(map4).append(Config.DELIMITER)
				         .append(map5).append(Config.DELIMITER)
				         .append(map6).append(Config.DELIMITER)
				         .append(map7).append(Config.DELIMITER)
				         .append(map8).append(Config.DELIMITER)
				         .append(map9).append(Config.DELIMITER)
				         .append(map10).append(Config.DELIMITER)
				         .append(map11).append(Config.DELIMITER)
				         .append(map12).append(Config.DELIMITER)
				         .append(map13).append(Config.DELIMITER)
				         .append(map14).append(Config.DELIMITER)
						 .append(map15).append(Config.NEW_LINE)).toString();
          
          receiverStr = receiverStr.replaceAll("\r\n", "<br>");
          
          sb.append(receiverStr);
          receiverNumber++;

          //LEGACY_RS_LIST_SIZE개가 되면 파일로 저장한다.
          if (receiverNumber % legacyRSListSize == 0) {   //legacyRSListSize : 1000명   config에서 설정
            if (Config.getInstance().isMultiLang()) {
              makeRUserFileList(receiverNumber, sb, rUserMIDFolder,
                                reserveInfo.getString("CHARSET"));
            }
            else {
              makeRUserFileList(receiverNumber, sb, rUserMIDFolder);  //수신자 정보를 읽어 R_List 파일로 생성한다.
            }
            sb = new StringBuffer();
          }
        }
        else {
          LOGGER.info("에러가 있기 때문에 여기다가 넣어준다");
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
          
          errorLogInfo.put("ENCKEY", enckey);
          errorLogInfo.put("MAP1", map1);
          errorLogInfo.put("MAP2", map2);
          errorLogInfo.put("MAP3", map3);
          errorLogInfo.put("MAP4", map4);
          errorLogInfo.put("MAP5", map5);
          errorLogInfo.put("MAP6", map6);
          errorLogInfo.put("MAP7", map7);
          errorLogInfo.put("MAP8", map8);
          errorLogInfo.put("MAP9", map9);
          errorLogInfo.put("MAP10", map10);
          errorLogInfo.put("MAP11", map11);
          errorLogInfo.put("MAP12", map12);
          errorLogInfo.put("MAP13", map13);
          errorLogInfo.put("MAP14", map14);
          errorLogInfo.put("MAP15", map15);
          

          // 이메일의 문법이 틀릴때에는 실패에 대한 상태를 넣어준다.
          reserveInfo.setString("STATUS", ReserveStatusCode.R_EMAIL_ERROR);
          //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
          LogFileManager.setReserveStatusUpdate(mID, subID,
                                                ReserveStatusCode.R_EMAIL_ERROR);
          ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                              ReserveStatusCode.
                                              R_EMAIL_ERROR_TYPE,
                                              ReserveStatusCode.
                                              R_EMAIL_ERROR_COMMENT, mID);
          errorLogInfoList.add(errorLogInfo);
        }
      }

      //수신자가 아무도 없으면.. 파일을만들지 않는다.
      if (receiverNumber != 0) {
        if (Config.getInstance().isMultiLang()) {
          makeRUserFileList(receiverNumber, sb, rUserMIDFolder,
                            reserveInfo.getString("CHARSET"));
        }
        else {
          makeRUserFileList(receiverNumber, sb, rUserMIDFolder);
        }
        //파일이 완성된 후에는 파일리스트를 리턴한다.
        if (rListFilePath.isDirectory()) {
          rListFileArray = rListFilePath.listFiles();
        }
      }

      // 수신자가 없다면..(Recipientinfo테이블에 MID에 대한 내용이 없다면)
      if (noReceiver) {
        // 이메일의 문법이 틀릴때에는 실패에 대한 상태를 넣어준다.
        reserveInfo.setString("STATUS", ReserveStatusCode.R_LIST_EXTRACT_ERROR);
        // 복구를 감안해서 예약리스트 Status를 파일로 저장한다.
        LogFileManager.setReserveStatusUpdate(mID, subID,
                                              ReserveStatusCode.R_LIST_EXTRACT_ERROR);
        ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                            ReserveStatusCode.
                                            R_LIST_EXTRACT_ERROR_TYPE,
                                            ReserveStatusCode.
                                            R_LIST_NOBODY_ERROR_COMMENT, mID);

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

      //에러 로그에 대해서 통계테이블과 ResultLog테이블에 넣어준다.
      if (errorLogInfoList.size() != 0) {
        ResultLogManager.InsertResultLog(errorLogInfoList);
      }
    }
    catch (Exception e) {
    	LOGGER.error(e);
      WorkDBManager.refreshConn();
      //e.printStackTrace();
      LogFileManager.runLogWriter(mID, e.toString());

      reserveInfo.setString("STATUS", ReserveStatusCode.R_LIST_EXTRACT_ERROR);
      //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
      LogFileManager.setReserveStatusUpdate(mID, subID,
                                            ReserveStatusCode.R_LIST_EXTRACT_ERROR);

      //에러 로그를 남겨준다.
      ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                          ReserveStatusCode.
                                          R_LIST_EXTRACT_ERROR_TYPE,
                                          ReserveStatusCode.
                                          R_LIST_EXTRACT_DB_ERROR_COMMENT, mID);

      rListFileArray = null;
    }
    finally {
      try {
        WorkDBManager.releaseConnection(conn);
      }
      catch (Exception e) {LOGGER.error(e);}

      try {
        if (pstmt != null) {
          pstmt.close();
          pstmt = null;
        }
      }
      catch (Exception e) {LOGGER.error(e);}

      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
      }
      catch (Exception e) {LOGGER.error(e);}
    }
    return rListFileArray;
  }

  /**
   * LegacyDB에 있는 수신자 리스트를 얻는다.
   * @version 1.0
   * @author ymkim
   * @param reserveInfo 예약 메일 정보
   * @param query 수신자를 얻는 SQL Query
   * @param legacyInfo LegacyDB 연결 정보
   * @return File[] 수신자 리스트 파일
   */
  public File[] getRecUsrInfoListFromDB(DataUnitInfo reserveInfo,
                                        String query, Hashtable legacyInfo) {
    //에러 로그 리스트
    ArrayList errorLogInfoList = new ArrayList();
    //에러 로그
    Hashtable errorLogInfo = null;

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;

    DBManager.setLDBInfo(legacyInfo);
    conn = DBManager.getConnection(Config.LEGACY_DB);

    String mID = reserveInfo.getString("MID");
    String subID = reserveInfo.getString("SUBID");
    String tID = reserveInfo.getString("TID");
    String sID = reserveInfo.getString("SID");
    String sName = reserveInfo.getString("SNAME");
    String sMail = reserveInfo.getString("SMAIL");
    String refMID = reserveInfo.getString("REFMID");

    String rUserMIDFolder = new StringBuffer(LogFileManager.RECEIVER_LOG_FOLDER)
        .append(mID).append(File.separator).toString();
    File rListFilePath = new File(rUserMIDFolder);
    if (! (rListFilePath.isDirectory())) {
      rListFilePath.mkdir();
    }

    File[] rListFileArray = null;

    //컨넥션이 널이면 즉! legacyDB에서 컨넥션을 가져오지 못했다면.... 로그를 남긴다.
    if (conn == null) {
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

      reserveInfo.setString("STATUS", ReserveStatusCode.R_LIST_EXTRACT_ERROR);
      //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
      LogFileManager.setReserveStatusUpdate(mID, subID,
                                            ReserveStatusCode.R_LIST_EXTRACT_ERROR);
      //에러 로그를 남겨준다.
      ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                          ReserveStatusCode.
                                          R_LIST_EXTRACT_ERROR_TYPE,
                                          ReserveStatusCode.
                                          R_LIST_EXTRACT_DB_ERROR_COMMENT, mID);
      return null;
    }

    
    ///암호화/복호화
    String ALGORITHM = "PBEWithMD5AndDES";
    String KEYSTRING = "ENDERSUMS";
    EncryptUtil enc =  new EncryptUtil();
    
    CustInfoSafeData safeDbEnc = new CustInfoSafeData();

    Config cfg = Config.getInstance();
    
    try {
      pstmt = conn.prepareStatement(query);
      //pstmt.setString(1, mID);
      rs = pstmt.executeQuery();

      String rID;
      String rName;
      String rMail;
      String enckey;
      String map1;
      String map2;
      String map3;
      String map4;
      String map5;
      String map6;
      String map7;
      String map8;
      String map9;
      String map10;
      String map11;
      String map12;
      String map13;
      String map14;
      String map15;

      StringBuffer sb = new StringBuffer();
      String receiverStr = "";
      int receiverNumber = 0;

      //수신자가 없다.
      boolean noReceiver = true;

      ResultSetMetaData rsmd = rs.getMetaData();
      int columnCnt = rsmd.getColumnCount(); //컬럼의 수

      Map<String, String> map = new HashMap<String, String>();
      while (rs.next()) {
    	  
    	  for(int i=1; i<=columnCnt; i++) {
    		  map.put(rsmd.getColumnName(i), rsmd.getColumnName(i));
    	  }
    	
        //수신자가 있다.
        noReceiver = false;

        rID = rs.getString("RID");
        rName = rs.getString("RNAME");
        rMail = rs.getString("RMAIL");
        enckey = rs.getString("ENCKEY");
        
        map1 = (map.get("MAP1")!=null) ? map1 = rs.getString("MAP1") : null;
        map2 = (map.get("MAP2")!=null) ? map2 = rs.getString("MAP2") : null;
        map3 = (map.get("MAP3")!=null) ? map3 = rs.getString("MAP3") : null;
        map4 = (map.get("MAP4")!=null) ? map4 = rs.getString("MAP4") : null;
        map5 = (map.get("MAP5")!=null) ? map5 = rs.getString("MAP5") : null;
        map6 = (map.get("MAP6")!=null) ? map6 = rs.getString("MAP6") : null;
        map7 = (map.get("MAP7")!=null) ? map7 = rs.getString("MAP7") : null;
        map8 = (map.get("MAP8")!=null) ? map8 = rs.getString("MAP8") : null;
        map9 = (map.get("MAP9")!=null) ? map9 = rs.getString("MAP9") : null;
        map10 = (map.get("MAP10")!=null) ? map10 = rs.getString("MAP10") : null;
        map11 = (map.get("MAP11")!=null) ? map11 = rs.getString("MAP11") : null;
        map12 = (map.get("MAP12")!=null) ? map12 = rs.getString("MAP12") : null;
        map13 = (map.get("MAP13")!=null) ? map13 = rs.getString("MAP13") : null;
        map14 = (map.get("MAP14")!=null) ? map14 = rs.getString("MAP14") : null;
        map15 = (map.get("MAP15")!=null) ? map15 = rs.getString("MAP15") : null;
        
//        map1 = rs.getString("MAP1");
//        map2 = rs.getString("MAP2");
//        map3 = rs.getString("MAP3");
//        map4 = rs.getString("MAP4");
//        map5 = rs.getString("MAP5");
//        map6 = rs.getString("MAP6");
//        map7 = rs.getString("MAP7");
//        map8 = rs.getString("MAP8");
//        map9 = rs.getString("MAP9");
//        map10 = rs.getString("MAP10");
//        map11 = rs.getString("MAP11");
//        map12 = rs.getString("MAP12");
//        map13 = rs.getString("MAP13");
//        map14 = rs.getString("MAP14");
//		map15 = rs.getString("MAP15");
        
        //복호화
        if("Y".equals(cfg.getEnc_yn())) {
        	//rMail = safeDbEnc.getDecrypt(rMail, "NOT_RNNO");
       		rMail = enc.getJasyptDecryptedString(ALGORITHM, KEYSTRING, rMail);
    		}	
        
        //Null을 막아준다.
        if (rID == null) {
          rID = "flage";
        }
        if (rName == null) {
          rName = "flage";
        }
        if (enckey == null) {
        	enckey = "flage";
          }
        if (map1 == null) {
        	map1 = "flage";
          }
        if (map2 == null) {
        	map2 = "flage";
          }
        if (map3 == null) {
        	map3 = "flage";
          }
        if (map4 == null) {
        	map4 = "flage";
        }
        if (map5 == null) {
        	map5 = "flage";
        }    
        if (map6 == null) {
        	map6 = "flage";
        }    
        if (map7 == null) {
        	map8 = "flage";
        }    
        if (map8 == null) {
        	map8 = "flage";
        }    
        if (map9 == null) {
        	map9 = "flage";
        }    
        if (map10 == null) {
        	map10 = "flage";
        }    
        if (map11 == null) {
        	map11 = "flage";
        }    
        if (map12 == null) {
        	map12 = "flage";
        }    
        if (map13 == null) {
        	map13 = "flage";
        }    
        if (map14 == null) {
        	map14 = "flage";
        }    
        if (map15 == null) {
        	map15 = "flage";
        }    
        
          //if(CheckFormat.checkEmail(rMail))
        if (CheckFormat.checkEmail(rMail) && ! (rID.equals("")) &&
            ! (rName.equals(""))) {
          receiverStr = (new StringBuffer().append(rMail).append(Config.
              DELIMITER)
                         .append(rName).append(Config.DELIMITER)
                         .append(rID).append(Config.DELIMITER)
                         .append(enckey).append(Config.DELIMITER)
                         .append(map1).append(Config.DELIMITER)
                         .append(map2).append(Config.DELIMITER)
                         .append(map3).append(Config.DELIMITER)
                         .append(map4).append(Config.DELIMITER)
                         .append(map5).append(Config.DELIMITER)
                         .append(map6).append(Config.DELIMITER)
                         .append(map7).append(Config.DELIMITER)
                         .append(map8).append(Config.DELIMITER)
                         .append(map9).append(Config.DELIMITER)
                         .append(map10).append(Config.DELIMITER)
                         .append(map11).append(Config.DELIMITER)
                         .append(map12).append(Config.DELIMITER)
                         .append(map13).append(Config.DELIMITER)
                         .append(map14).append(Config.DELIMITER)
                         .append(map15).append(Config.DELIMITER)
                         .append(Config.NEW_LINE)).toString();
          sb.append(receiverStr);
          receiverNumber++;

          // LEGACY_RS_LIST_SIZE개가 되면 파일로 저장한다.
          if (receiverNumber % legacyRSListSize == 0) {
            if (Config.getInstance().isMultiLang()) {
              makeRUserFileList(receiverNumber, sb, rUserMIDFolder,
                                reserveInfo.getString("CHARSET"));
            }
            else {
              makeRUserFileList(receiverNumber, sb, rUserMIDFolder);
            }
            sb = new StringBuffer();
          }
        }
        else {
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
          reserveInfo.setString("STATUS", ReserveStatusCode.R_EMAIL_ERROR);
          //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
          LogFileManager.setReserveStatusUpdate(mID, subID,
                                                ReserveStatusCode.R_EMAIL_ERROR);

          ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                              ReserveStatusCode.
                                              R_EMAIL_ERROR_TYPE,
                                              ReserveStatusCode.
                                              R_EMAIL_ERROR_COMMENT, mID);
          errorLogInfoList.add(errorLogInfo);
        }
      }

      if (receiverNumber != 0) {
        if (Config.getInstance().isMultiLang()) {
          makeRUserFileList(receiverNumber, sb, rUserMIDFolder,
                            reserveInfo.getString("CHARSET"));
        }
        else {
          makeRUserFileList(receiverNumber, sb, rUserMIDFolder);
        }
        //파일이 완성된 후에는 파일리스트를 리턴한다.
        if (rListFilePath.isDirectory()) {
          rListFileArray = rListFilePath.listFiles();
        }
      }

      if (noReceiver) { //수신자가 없다면..(Recipientinfo테이블에 MID에 대한 내용이 없다면)
        //이메일의 문법이 틀릴때에는 실패에 대한 상태를 넣어준다.
        reserveInfo.setString("STATUS", ReserveStatusCode.R_LIST_EXTRACT_ERROR);

        //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
        LogFileManager.setReserveStatusUpdate(mID, subID,
                                              ReserveStatusCode.R_LIST_EXTRACT_ERROR);
        ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                            ReserveStatusCode.
                                            R_LIST_EXTRACT_ERROR_TYPE,
                                            ReserveStatusCode.
                                            R_LIST_NOBODY_ERROR_COMMENT, mID);

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

      if (errorLogInfoList.size() != 0) {
        ResultLogManager.InsertResultLog(errorLogInfoList);
      }
    }
    catch (Exception e) {
    	LOGGER.error(e);
      //e.printStackTrace();
      LogFileManager.runLogWriter(mID, e.toString());

      reserveInfo.setString("STATUS", ReserveStatusCode.R_LIST_EXTRACT_ERROR);

      //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
      LogFileManager.setReserveStatusUpdate(mID, subID,
                                            ReserveStatusCode.R_LIST_EXTRACT_ERROR);
      //에러 로그를 남겨준다.
      ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                          ReserveStatusCode.
                                          R_LIST_EXTRACT_ERROR_TYPE,
                                          ReserveStatusCode.
                                          R_LIST_EXTRACT_SQL_ERROR_COMMENT, mID);

      rListFileArray = null;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
      }
      catch (Exception e) {LOGGER.error(e);}

      try {
        if (pstmt != null) {
          pstmt.close();
          pstmt = null;
        }
      }
      catch (Exception e) {LOGGER.error(e);}

      try {
        if (conn != null) {
          conn.close();
          conn = null;
        }
      }
      catch (Exception e) {LOGGER.error(e);}
    }
    return rListFileArray;
  }

  /**
   * File에 있는 수신자 리스트를 얻는다.
   * @version 1.0
   * @author ymkim
   * @param reserveInfo 예약 메일 정보
   * @param query 수신자 리스트가 있는 파일의 경로
   * @return File[] 수신자 리스트 파일
   */
  public File[] getRecUsrInfoListFromFile(DataUnitInfo reserveInfo,
                                          String query) {
    String mID = reserveInfo.getString("MID");
    String subID = reserveInfo.getString("SUBID");
    String tID = reserveInfo.getString("TID");
    String sID = reserveInfo.getString("SID");
    String sName = reserveInfo.getString("SNAME");
    String sMail = reserveInfo.getString("SMAIL");
    String refMID = reserveInfo.getString("REFMID");

    //에러 로그 리스트
    ArrayList errorLogInfoList = new ArrayList();
    Hashtable errorLogInfo = null;

    String rUserMIDFolder = new StringBuffer(LogFileManager.RECEIVER_LOG_FOLDER)
        .append(mID).append(File.separator).toString();
    File rListFilePath = new File(rUserMIDFolder);
    if (! (rListFilePath.isDirectory())) {
      rListFilePath.mkdir();
    }

    File[] rListFileArray = null;

    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(new File(query)));
      String tempStr = "";

      String rName = "";
      String rID = "";
      String rMail = "";

      StringBuffer sb = new StringBuffer();
      String receiverStr = "";

      int receiverNumber = 0;
      int i = 0;

      //수신자가 없다.
      boolean noReceiver = true;

      while ( (tempStr = br.readLine()) != null) {
        //수신자가 있다.
        noReceiver = false;

        StringTokenizer st = new StringTokenizer(tempStr, Config.DELIMITER);
        if (st.hasMoreTokens()) {
          try {
            rMail = st.nextToken();
            rName = st.nextToken();
            rID = st.nextToken();

            //Null을 막아준다.
            if (rID == null)
              rID = "";
            if (rName == null) {
              rName = "";
            }

            //if(CheckFormat.checkEmail(rMail))
            if (CheckFormat.checkEmail(rMail) && ! (rID.equals("")) &&
                ! (rName.equals(""))) {
              receiverStr = (new StringBuffer(rMail).append(Config.DELIMITER)
                             .append(rName).append(Config.DELIMITER)
                             .append(rID).append(Config.NEW_LINE)).toString();
              sb.append(receiverStr);
              receiverNumber++;

              //LEGACY_RS_LIST_SIZE개가 되면 파일로 저장한다.
              if (receiverNumber % legacyRSListSize == 0) {
                if (Config.getInstance().isMultiLang()) {
                  makeRUserFileList(receiverNumber, sb, rUserMIDFolder,
                                    reserveInfo.getString("CHARSET"));
                }
                else {
                  makeRUserFileList(receiverNumber, sb, rUserMIDFolder);
                }
                sb = new StringBuffer();
              }
            }
            else {
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
              reserveInfo.setString("STATUS", ReserveStatusCode.R_EMAIL_ERROR);

              //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
              LogFileManager.setReserveStatusUpdate(mID, subID,
                  ReserveStatusCode.R_EMAIL_ERROR);
              ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                                  ReserveStatusCode.
                                                  R_EMAIL_ERROR_TYPE,
                                                  ReserveStatusCode.
                                                  R_EMAIL_ERROR_COMMENT, mID);
              errorLogInfoList.add(errorLogInfo);
            }
          }
          catch (NoSuchElementException exp) {
        	  LOGGER.error(exp);
            reserveInfo.setString("STATUS",
                                  ReserveStatusCode.R_LIST_EXTRACT_ERROR);
            //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
            LogFileManager.setReserveStatusUpdate(mID, subID,
                                                  ReserveStatusCode.R_LIST_EXTRACT_ERROR);
            //에러 로그를 남겨준다.
            ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                                ReserveStatusCode.
                                                R_LIST_EXTRACT_ERROR_TYPE,
                                                ReserveStatusCode.
                R_LIST_EXTRACT_FILE_ERROR_COMMENT, mID);
            break;
          }
        }
      }

      if (receiverNumber != 0) {
        if (Config.getInstance().isMultiLang()) {
          makeRUserFileList(receiverNumber, sb, rUserMIDFolder,
                            reserveInfo.getString("CHARSET"));
        }
        else {
          makeRUserFileList(receiverNumber, sb, rUserMIDFolder);
        }
        if (rListFilePath.isDirectory()) {
          rListFileArray = rListFilePath.listFiles();
        }
      }

      if (noReceiver) { //수신자가 없다면..(Recipientinfo테이블에 MID에 대한 내용이 없다면)
        //이메일의 문법이 틀릴때에는 실패에 대한 상태를 넣어준다.
        reserveInfo.setString("STATUS", ReserveStatusCode.R_LIST_EXTRACT_ERROR);

        //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
        LogFileManager.setReserveStatusUpdate(mID, subID,
                                              ReserveStatusCode.R_LIST_EXTRACT_ERROR);
        ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                            ReserveStatusCode.
                                            R_LIST_EXTRACT_ERROR_TYPE,
                                            ReserveStatusCode.
                                            R_LIST_NOBODY_ERROR_COMMENT, mID);

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

      if (errorLogInfoList.size() != 0) {
        ResultLogManager.InsertResultLog(errorLogInfoList);
      }
      //파일이 완성된 후에는 파일리스트를 리턴한다.
    }
    catch (Exception e) {
    	LOGGER.error(e);
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

      //e.printStackTrace();
      LogFileManager.runLogWriter(mID, e.toString());

      reserveInfo.setString("STATUS", ReserveStatusCode.R_LIST_EXTRACT_ERROR);

      //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
      LogFileManager.setReserveStatusUpdate(mID, subID,
                                            ReserveStatusCode.R_LIST_EXTRACT_ERROR);
      //에러 로그를 남겨준다.
      ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                          ReserveStatusCode.
                                          R_LIST_EXTRACT_ERROR_TYPE,
                                          ReserveStatusCode.
                                          R_LIST_EXTRACT_SQL_ERROR_COMMENT, mID);

      rListFileArray = null;
    }
    finally {
      try {
        if (br != null) {
          br.close();
        }
      }
      catch (Exception e) {
    	  LOGGER.error(e);
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
  public boolean makeRUserFileList(int receiverNumber, StringBuffer sb,
                                   String destPath) {
    int fileNum = (receiverNumber - 1) / legacyRSListSize;
    FileWriter fw = null;
    try {
      String fileName = new StringBuffer()
          .append(destPath)
          .append(LogFileManager.RECEIVER_FILE_NAME)
          .append(fileNum)
          .append(LogFileManager.LOG_EXT)
          .toString();

      fw = new FileWriter(fileName, true);
      fw.write(sb.toString());
    }
    catch (Exception e) {
    	LOGGER.error(e);
      //e.printStackTrace();
      return false;
    }
    finally {
      try {
        if (fw != null) {
          fw.close();
        }
      }
      catch (Exception e) {LOGGER.error(e);}
    }
    return true;
  }

  /**
   * 수신자를 얻어와서 파일로 만든다.(다국어)
   * @version 1.0
   * @author ymkim
   * @param receiverNumber 수신자 수
   * @param sb 수신자 정보 내용
   * @param destPath 저장될 파일 경로
   * @return boolean true - 파일 생성 성공, false - 파일 생성 실패
   */
  public boolean makeRUserFileList(int receiverNumber, StringBuffer sb,
                                   String destPath, String charset) {
    int fileNum = (receiverNumber - 1) / legacyRSListSize;
    FileOutputStream fw = null;

    try {

      fw = new FileOutputStream( (new StringBuffer().append(destPath)
                                  .append(LogFileManager.RECEIVER_FILE_NAME).
                                  append(fileNum)
                                  .append(LogFileManager.LOG_EXT)).toString(), true);
      fw.write(sb.toString().getBytes(charset));
      //fw.write(sb.toString().getBytes());
    }
    catch (Exception e) {
    	LOGGER.error(e);
      //e.printStackTrace();
      return false;
    }
    finally {
      try {
        if (fw != null) {
          fw.close();
        }
      }
      catch (Exception e) {LOGGER.error(e);}
    }
    return true;
  }
}