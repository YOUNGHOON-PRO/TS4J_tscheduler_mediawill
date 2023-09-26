package com.tscheduler.manager;

/*
 *설명: 메일 예약 리스트를 얻어온다.
 *	1) 여기서는 workDB 에 있는 발송그룹 저장테이블에서 값을 가져온다.
 *	2) 만일에 rpos이 query이면 이때는 legacyDB에서 값을 가져온다.
 */

//여기서는 유저 리스트만 뽑아올것인가 아니면 여기서 유저 리스트와 더불어서 전체 정보를 다 가져올것인가..?
//만일에 다 가져온다면 .. 이것은 유저 전체의 정보를 관리하는 그런 클래스가 될것이다.

import java.util.*;
import java.sql.*;
import java.io.File;
import java.io.FileInputStream;

import com.tscheduler.util.DataUnitInfoList;
import com.tscheduler.util.EncryptUtil;
import com.tscheduler.util.DataUnitInfo;
import com.tscheduler.util.ReserveStatusCode;
import com.custinfo.safedata.*;
import com.tscheduler.util.CheckFormat;
import com.tscheduler.util.Config;
import com.tscheduler.util.ErrorLogGenerator;
import com.tscheduler.util.ErrorStatusCode;
import com.tscheduler.util.LogUtil;
import com.tscheduler.dbbroker.DBManager;
import com.tscheduler.dbbroker.WorkDBManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 예약 메일 관리 클래스
 * @version 1.0
 * @author ymkim
 */
public class ReserveManager {
	
  private static final Logger LOGGER = LogManager.getLogger(ReserveManager.class.getName());
	
  /**발송예약테이블에서 하나의 값을 가져오는 쿼리*/
  private static String NEXT_RESERVE_MAIL_QUERY;

  /**MailQueue의 상태를 업데이트하는 쿼리*/
  public static final String STATUS_UPDATE_QUERY =
      "UPDATE TS_MAILQUEUE SET STATUS = ? WHERE MID = ? AND SUBID = ?";

  /**하나의 DataUnitInfoList로만으로 구성한다.*/
  private static DataUnitInfoList dataUnitList;

  /**한번에 가져오는 예약메일리스트의 갯수*/
  private static int numberOfReserve = 0;

  /**ReserveManager의 Singleton 객체*/
  private static ReserveManager rInstance;

  private ReserveManager() {
    readyQuery();
  }

  private void readyQuery() {
    //DB Type을 알아본다.
    Config cfg = Config.getInstance();
    cfg.loadConfig(Config.MAIN_CFG);
    int resMaxSize = Integer.parseInt(cfg.getResMaxSize());
    String dbType = cfg.getDBType();

    if ( (dbType.toUpperCase()).equals("ORACLE")) {
      if (cfg.isMultiLang()) {
        NEXT_RESERVE_MAIL_QUERY = new StringBuffer()
            .append(
            "SELECT MID, SUBID, TID, TS_CHARSET.CHARSET,SPOS, SNAME, SMAIL, SID, RPOS, ")
            .append(" QUERY, CTNPOS, SUBJECT, CONTENTS, CDATE,SDATE, STATUS, ")
            .append(
            " DBCODE, REFMID, ATTACHFILE01, ATTACHFILE02, ATTACHFILE03, ")
            .append(" ATTACHFILE04, ATTACHFILE05 ")
            .append("FROM TS_MAILQUEUE, TS_CHARSET ")
            .append("WHERE STATUS = '").append(ReserveStatusCode.
                                               DEFAULT_RESERVE).append("' ")
            .append(" AND CDATE < SYSDATE AND  ROWNUM <=").append(resMaxSize)
            .append(" AND TS_MAILQUEUE.CHARSET = TS_CHARSET.CODE")
            .toString();
      }
      else {
        NEXT_RESERVE_MAIL_QUERY = new StringBuffer()
		    .append("SELECT A.MID, A.SUBID, A.TID, A.SPOS, A.SNAME, A.SMAIL, A.SID, A.RPOS, ")
		    .append("A.QUERY, A.CTNPOS, A.SUBJECT, A.CONTENTS, A.CDATE, A.SDATE, A.STATUS, ")
		    .append("A.DBCODE, A.REFMID, A.ATTACHFILE01, A.ATTACHFILE02, A.ATTACHFILE03, A.ATTACHFILE04, A.ATTACHFILE05, ")
		    .append("B.SECU_ATT_YN ,B.SOURCE_URL, B.SECU_ATT_TYP, C.TITLE_CHK_YN, C.BODY_CHK_YN, C.ATTACH_FILE_CHK_YN, C.SECU_MAIL_CHK_YN ")
		    .append("FROM TS_MAILQUEUE A ")
		    .append("LEFT OUTER JOIN TS_WEBAGENT B ")
		    .append("ON A.TID = B.TID ")
		    .append("LEFT OUTER JOIN TS_SERVICETYP C ")
		    .append("ON A.TID = C.TID ")
		    .append("WHERE A.STATUS = '").append(ReserveStatusCode.DEFAULT_RESERVE).append("' ")
		    .append(" AND A.CDATE < SYSDATE AND  ROWNUM <=").append(resMaxSize)
		    .toString();
      }
    }
    else if ( (dbType.toUpperCase()).equals("MSSQL")) {
      if (cfg.isMultiLang()) {
        NEXT_RESERVE_MAIL_QUERY = new StringBuffer()
            .append("SELECT TOP ").append(resMaxSize)
            .append(
            " MID, SUBID, TID, TS_CHARSET.CHARSET, SPOS, SNAME, SMAIL, SID, RPOS,")
            .append(" QUERY, CTNPOS, SUBJECT, CONTENTS, CDATE, ")
            .append(" SDATE, STATUS, DBCODE, REFMID, ATTACHFILE01, ")
            .append(" ATTACHFILE02, ATTACHFILE03, ATTACHFILE04, ATTACHFILE05 ")
            .append("FROM TS_MAILQUEUE, TS_CHARSET ")
            .append("WHERE STATUS = '").append(ReserveStatusCode.
                                               DEFAULT_RESERVE).append("' ")
            .append(" AND CDATE < GETDATE()")
            .append(" AND TS_MAILQUEUE.CHARSET = TS_CHARSET.CODE")
            .toString();
      }
      else {
        NEXT_RESERVE_MAIL_QUERY = new StringBuffer()
//            .append("SELECT TOP ").append(resMaxSize)
//            .append(" MID, SUBID, TID, SPOS, SNAME, SMAIL, SID, RPOS,")
//            .append(" QUERY, CTNPOS, SUBJECT, CONTENTS, CDATE, ")
//            .append(" SDATE, STATUS, DBCODE, REFMID, ATTACHFILE01, ")
//            .append(" ATTACHFILE02, ATTACHFILE03, ATTACHFILE04, ATTACHFILE05 ")
//            .append("FROM TS_MAILQUEUE ")
//            .append("WHERE STATUS = '").append(ReserveStatusCode.
//                                               DEFAULT_RESERVE).append("' ")
//            .append(" AND CDATE < GETDATE()")
//            .toString();
        		
			.append("SELECT TOP ").append(resMaxSize)
			.append(" A.MID, A.SUBID, A.TID, A.SPOS, A.SNAME, A.SMAIL, A.SID, A.RPOS, ")
			.append(" A.QUERY, A.CTNPOS, A.SUBJECT, A.CONTENTS, A.CDATE, ")
			.append(" A.SDATE, A.STATUS, A.DBCODE, A.REFMID, A.ATTACHFILE01, ")
			.append(" A.ATTACHFILE02, A.ATTACHFILE03, A.ATTACHFILE04, A.ATTACHFILE05, A.REQUEST_KEY, ")
			.append(" B.SECU_ATT_YN ,B.SOURCE_URL, B.SECU_ATT_TYP, C.TITLE_CHK_YN, C.BODY_CHK_YN, C.ATTACH_FILE_CHK_YN, C.SECU_MAIL_CHK_YN ")
			.append(" FROM TS_MAILQUEUE A ")
			.append(" LEFT OUTER JOIN TS_WEBAGENT B ")
			.append(" ON A.TID = B.TID ")
			.append(" LEFT OUTER JOIN TS_SERVICETYP C ")
			.append(" ON A.TID = C.TID ")
			.append(" WHERE A.STATUS = '").append(ReserveStatusCode.
			                       DEFAULT_RESERVE).append("' ")
			.append(" AND CDATE < GETDATE()").toString();

      }
    }
    else if ( (dbType.toUpperCase()).equals("MYSQL")) {
        if (cfg.isMultiLang()) {
          NEXT_RESERVE_MAIL_QUERY = new StringBuffer()
              .append(" SELECT MID, SUBID, TID, TS_CHARSET.CHARSET, SPOS, SNAME, SMAIL, SID, RPOS,")
              .append(" QUERY, CTNPOS, SUBJECT, CONTENTS, CDATE, ")
              .append(" SDATE, STATUS, DBCODE, REFMID, ATTACHFILE01, ")
              .append(" ATTACHFILE02, ATTACHFILE03, ATTACHFILE04, ATTACHFILE05 ")
              .append("FROM TS_MAILQUEUE, TS_CHARSET ")
              .append("WHERE STATUS = '").append(ReserveStatusCode.
                                                 DEFAULT_RESERVE).append("' ")
              .append(" AND CDATE < NOW()")
              .append(" AND TS_MAILQUEUE.CHARSET = TS_CHARSET.CODE")
              .append(" LIMIT ").append(resMaxSize)
              .toString();
        }
        else {
            NEXT_RESERVE_MAIL_QUERY = new StringBuffer()
        		    .append("SELECT A.MID, A.SUBID, A.TID, A.SPOS, A.SNAME, A.SMAIL, A.SID, A.RPOS, ")
        		    .append("A.QUERY, A.CTNPOS, A.SUBJECT, A.CONTENTS, A.CDATE, A.SDATE, A.STATUS, ")
        		    .append("A.DBCODE, A.REFMID, A.ATTACHFILE01, A.ATTACHFILE02, A.ATTACHFILE03, A.ATTACHFILE04, A.ATTACHFILE05, A.REQUEST_KEY, ")
        		    .append("B.SECU_ATT_YN ,B.SOURCE_URL, B.SECU_ATT_TYP, C.TITLE_CHK_YN, C.BODY_CHK_YN, C.ATTACH_FILE_CHK_YN, C.SECU_MAIL_CHK_YN ")
        		    .append("FROM TS_MAILQUEUE A ")
        		    .append("LEFT OUTER JOIN TS_WEBAGENT B ")
        		    .append("ON A.TID = B.TID ")
        		    .append("LEFT OUTER JOIN TS_SERVICETYP C ")
        		    .append("ON A.TID = C.TID ")
        		    .append("WHERE A.STATUS = '").append(ReserveStatusCode.DEFAULT_RESERVE).append("' ")
        		    .append(" AND A.CDATE < NOW() LIMIT ").append(resMaxSize)
        		    .toString();
        }
      }
    else if ( (dbType.toUpperCase()).equals("MARIA")) {
        if (cfg.isMultiLang()) {
          NEXT_RESERVE_MAIL_QUERY = new StringBuffer()
              .append(" SELECT MID, SUBID, TID, TS_CHARSET.CHARSET, SPOS, SNAME, SMAIL, SID, RPOS,")
              .append(" QUERY, CTNPOS, SUBJECT, CONTENTS, CDATE, ")
              .append(" SDATE, STATUS, DBCODE, REFMID, ATTACHFILE01, ")
              .append(" ATTACHFILE02, ATTACHFILE03, ATTACHFILE04, ATTACHFILE05 ")
              .append("FROM TS_MAILQUEUE, TS_CHARSET ")
              .append("WHERE STATUS = '").append(ReserveStatusCode.
                                                 DEFAULT_RESERVE).append("' ")
              .append(" AND CDATE < NOW()")
              .append(" AND TS_MAILQUEUE.CHARSET = TS_CHARSET.CODE")
              .append(" LIMIT ").append(resMaxSize)
              .toString();
        }
        else {
            NEXT_RESERVE_MAIL_QUERY = new StringBuffer()
        		    .append("SELECT A.MID, A.SUBID, A.TID, A.SPOS, A.SNAME, A.SMAIL, A.SID, A.RPOS, ")
        		    .append("A.QUERY, A.CTNPOS, A.SUBJECT, A.CONTENTS, A.CDATE, A.SDATE, A.STATUS, ")
        		    .append("A.DBCODE, A.REFMID, A.ATTACHFILE01, A.ATTACHFILE02, A.ATTACHFILE03, A.ATTACHFILE04, A.ATTACHFILE05, A.REQUEST_KEY, ")
        		    .append("B.SECU_ATT_YN ,B.SOURCE_URL, B.SECU_ATT_TYP, C.TITLE_CHK_YN, C.BODY_CHK_YN, C.ATTACH_FILE_CHK_YN, C.SECU_MAIL_CHK_YN ")
        		    .append("FROM TS_MAILQUEUE A ")
        		    .append("LEFT OUTER JOIN TS_WEBAGENT B ")
        		    .append("ON A.TID = B.TID ")
        		    .append("LEFT OUTER JOIN TS_SERVICETYP C ")
        		    .append("ON A.TID = C.TID ")
        		    .append("WHERE A.STATUS = '").append(ReserveStatusCode.DEFAULT_RESERVE).append("' ")
        		    .append(" AND A.CDATE < NOW() LIMIT ").append(resMaxSize)
        		    .toString();
        }
      }
  }

  /**
   * ReserveManager의 Singleton객체를 얻는다.(예약리스트를 새로 얻는다)
   * @version 1.0
   * @author ymkim
   * @return ReserveManager ReserveManager의 Singleton 객체
   */
  public synchronized static ReserveManager getInstance() {
    if (rInstance == null) { //최초 실행시
    	//LOGGER.info("새로 ReserveManager의 Instance를 만든다.");
      rInstance = new ReserveManager();
      //여기서 새로운 예약을 얻는다.
      dataUnitList = getNextReserveMail();
      return rInstance;
    }
    else { //두번째 이후...
      //LOGGER.info("기존에 있는 ReserveManager의 Instance를 사용한다");
    	
      //기존에 있는 instance내에 리스트가 없으면 새로운 리스트를 얻는다.
      if (dataUnitList == null) {
        dataUnitList = getNextReserveMail();
      }
      return rInstance;
    }
  }

  /**
   * ReserveManager의 Singleton객체를 얻는다.(Reserve Log에 들어있는 상태값을 Update해준다.)
   * @version 1.0
   * @author ymkim
   * @return ReserveManager ReserveManager의 Singleton 객체
   */
  public synchronized static ReserveManager getInstance(File recoveryFile) {
    if (setStatusUpdate(recoveryFile)) {
      LOGGER.info("상태 복구가 성공했습니다.");
      LogFileManager.deleteReserveFile();
    }

    return getInstance();
  }

  /**
   * 현재 작업중인 예약리스트를 반환한다.
   * @version 1.0
   * @author ymkim
   * @return DataUnitInfoList 현재 작업중인 예약메일 리스트
   */
  public DataUnitInfoList getReserveMailList() {
    return dataUnitList;
  }

  /**
   * 현재 작업중인 예약리스트를 세팅합니다.
   * @version 1.0
   * @author ymkim
   * @param DataUnitInfoList 현재 작업중인 예약메일 리스트
   */
  public void setReserveMailList(DataUnitInfoList dataUnitList) {
    this.dataUnitList = dataUnitList;
  }

  /**
   * MailQueue 테이블에서 발송 예약 메일 리스트를 가져온다.
   * @version 1.0
   * @author ymkim
   * @return DataUnitInfoList 예약메일 리스트
   */
  private static DataUnitInfoList getNextReserveMail() {
    //DB연결 Instance를 얻는다.
    Connection con_work = DBManager.getConnection(Config.WORK_DB);
    Statement pstmt = null;
    ResultSet rs = null;

    //이번에 작업할 발송예약 정보를 가져온다.(mid, rpos)
    String mID = "";
    String subID = "";
    String rPos = null;
    //기타 필드 시작
    String tID = null;
    String sPos = null;
    String sName = null;
    String sMail = null;
    String sID = null;
    String query = null;
    String ctnPos = null;
    String subject = null;
    String contents = null;
    String cDate = null;
    String sDate = null;
    String status = null;
    String dbCode = null;
    String refMID = null;
    String charset = null;
    String attachFile01 = null;
    String attachFile02 = null;
    String attachFile03 = null;
    String attachFile04 = null;
    String attachFile05 = null;
    
    String secu_att_yn = null;
    String source_url = null;
    String secu_att_typ = null;
    
    String title_chk_yn = null;
    String body_chk_yn = null;
    String attach_file_chk_yn = null;
    String secu_mail_chk_yn = null;
    
    String requestKey = null;
        
    //기타 필드 끝

    //파일로 발송예약정보가 저장됐는지.. 확인하기 위해
    //boolean fileBack = false;

    //발송 예약정보 리스트
    DataUnitInfoList reserveInfoList = new DataUnitInfoList();

    //발송 예약정보
    DataUnitInfo reserveInfo = null;

    //하나의 발송예약정보에 대한 수신자 리스트(하나 또는 그 이상)
    DataUnitInfoList rUserList = null;

    //2003.11.14 영맨 추가
    //에러 로그 리스트
    ArrayList errorLogInfoList = new ArrayList();
    //에러 로그
    Hashtable errorLogInfo = null;
    //에러 발생한 것들에 대해 상태업데이트할 내용들
    DataUnitInfoList errorDataUnitInfoList = new DataUnitInfoList();

    
    ///암호화/복호화
    String ALGORITHM = "PBEWithMD5AndDES";
    String KEYSTRING = "ENDERSUMS";
    EncryptUtil enc =  new EncryptUtil();
    
    CustInfoSafeData safeDbEnc = new CustInfoSafeData();
    
    
    Config cfg = Config.getInstance();
    
    try {

      pstmt = con_work.createStatement();
      rs = pstmt.executeQuery(NEXT_RESERVE_MAIL_QUERY);

      //값을 0으로 초기화한다.
      numberOfReserve = 0;

      while (rs.next()) {
        reserveInfo = new DataUnitInfo();
        mID = rs.getString("MID");

        //기타 내용들 시작
        subID = rs.getString("SUBID");
        tID = rs.getString("TID");

        sPos = rs.getString("SPOS");
        sName = rs.getString("SNAME");

        sMail = rs.getString("SMAIL");
        
        //복호화
        if("Y".equals(cfg.getEnc_yn())) {
        	try {
        		//sMail = safeDbEnc.getDecrypt(sMail, "NOT_RNNO");
        		sMail = enc.getJasyptDecryptedFixString(ALGORITHM, KEYSTRING, sMail);
			} catch (Exception e) {
				LOGGER.error(e);
				// TODO: handle exception
			}
        		
        }
        
        sID = rs.getString("SID");
        rPos = rs.getString("RPOS");
        query = rs.getString("QUERY");
        ctnPos = rs.getString("CTNPOS");
        subject = rs.getString("SUBJECT");

        if (Config.getInstance().isMultiLang()) {
          charset = rs.getString("CHARSET");
          contents = getResultByCharStream(rs.getCharacterStream("CONTENTS"),
                                           charset);
        }
        else {
          contents = rs.getString("CONTENTS");
        }

        cDate = rs.getString("CDATE");
        sDate = rs.getString("SDATE");
        status = rs.getString("STATUS");
        dbCode = rs.getString("DBCODE");
        refMID = rs.getString("REFMID");
        attachFile01 = rs.getString("ATTACHFILE01");
        attachFile02 = rs.getString("ATTACHFILE02");
        attachFile03 = rs.getString("ATTACHFILE03");
        attachFile04 = rs.getString("ATTACHFILE04");
        attachFile05 = rs.getString("ATTACHFILE05");
        
        secu_att_yn = rs.getString("SECU_ATT_YN");
        source_url = rs.getString("SOURCE_URL");
        secu_att_typ = rs.getString("SECU_ATT_TYP");
        
        title_chk_yn = rs.getString("TITLE_CHK_YN");
        body_chk_yn = rs.getString("BODY_CHK_YN");
        attach_file_chk_yn = rs.getString("ATTACH_FILE_CHK_YN");
        secu_mail_chk_yn = rs.getString("SECU_MAIL_CHK_YN");
        
        requestKey = rs.getString("REQUEST_KEY");
        //기타 내용들 끝

        reserveInfo.setString("MID", mID);
        reserveInfo.setString("SUBID", subID);
        reserveInfo.setString("RPOS", rPos);
        //기타 내용들 시작
        reserveInfo.setString("TID", tID);
        reserveInfo.setString("CHARSET", charset);
        reserveInfo.setString("SPOS", sPos);
        reserveInfo.setString("SNAME", sName);
        reserveInfo.setString("SMAIL", sMail);
        reserveInfo.setString("SID", sID);
        reserveInfo.setString("QUERY", query);
        reserveInfo.setString("CTNPOS", ctnPos);
        reserveInfo.setString("SUBJECT", subject);
        reserveInfo.setString("CONTENTS", contents);
        reserveInfo.setString("CDATE", cDate);
        reserveInfo.setString("SDATE", sDate);
        reserveInfo.setString("STATUS", status);
        reserveInfo.setString("DBCODE", dbCode);
        reserveInfo.setString("REFMID", refMID);
        reserveInfo.setString("ATTACHFILE01", attachFile01);
        reserveInfo.setString("ATTACHFILE02", attachFile02);
        reserveInfo.setString("ATTACHFILE03", attachFile03);
        reserveInfo.setString("ATTACHFILE04", attachFile04);
        reserveInfo.setString("ATTACHFILE05", attachFile05);
        
        reserveInfo.setString("SECU_ATT_YN", secu_att_yn);
        reserveInfo.setString("SOURCE_URL", source_url);
        reserveInfo.setString("SECU_ATT_TYP", secu_att_typ);
        
        reserveInfo.setString("TITLE_CHK_YN", title_chk_yn);
        reserveInfo.setString("BODY_CHK_YN", body_chk_yn);
        reserveInfo.setString("ATTACH_FILE_CHK_YN", attach_file_chk_yn);
        reserveInfo.setString("SECU_MAIL_CHK_YN", secu_mail_chk_yn);
        
        reserveInfo.setString("REQUEST_KEY", requestKey);
        //기타 내용들 끝

        //발송자 이메일 주소에러를 체크한다. (이메일 필터링)
        if (CheckFormat.checkEmail(sMail)) {
          numberOfReserve++;
          reserveInfo.setString("STATUS", ReserveStatusCode.DEFAULT_RESERVE);
          reserveInfoList.addDataUnitInfo(reserveInfo);
        }
        else { //이메일 체크에서 오류가 있는것은 reserveInfoList에 넣어주지  아니 한다.
          errorLogInfo = new Hashtable();
          errorLogInfo.put("MID", mID);
          errorLogInfo.put("SUBID", subID);
          errorLogInfo.put("TID", tID);
          errorLogInfo.put("SID", sID);
          errorLogInfo.put("SNAME", sName);
          errorLogInfo.put("SMAIL", sMail);
          errorLogInfo.put("REFMID", refMID);
          if(charset == null) {
        	  errorLogInfo.put("CHARSET", "Nodata");  
          }else {
        	  errorLogInfo.put("CHARSET", charset);  
          }
          errorLogInfo.put("RID", ResultLogManager.NO_USER_ID);
          errorLogInfo.put("RNAME", ResultLogManager.NO_USER_NAME);
          errorLogInfo.put("RMAIL", ResultLogManager.NO_USER_MAIL);
          errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_RMAIL);
          errorLogInfoList.add(errorLogInfo);

          reserveInfo.setString("STATUS", ReserveStatusCode.S_EMAIL_ERROR);
          reserveInfoList.addDataUnitInfo(reserveInfo);

          //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
          //최초에 일어나는 일이라면.. 당연히 파일이 없기때문에 statusUpdate가 되어지지 않는다.
          LogFileManager.setReserveStatusUpdate(mID, subID,
                                                ReserveStatusCode.S_EMAIL_ERROR);
          //에러 로그를 남겨준다.
          ErrorLogGenerator.setErrorLogFormat("ReserveManager",
                                              ReserveStatusCode.
                                              S_EMAIL_ERROR_TYPE,
                                              ReserveStatusCode.
                                              S_EMAIL_ERROR_COMMENT, mID);
        }
      }

//			//에러가 발생한것이 있으면
//			if(errorDataUnitInfoList.size()!=0)
//				setStatusUpdate(errorDataUnitInfoList);

      if (errorLogInfoList.size() != 0) {
        ResultLogManager.InsertResultLog(errorLogInfoList);  // ts_resultlog , TS_MAILQUEUE_RSINFO, TS_MAILQUEUE_DOMAIN 테이블들에 에러로그 업데이트
      }

      //일단 여기서 파일로 백업한다. 여기에서 작업을 하는 ReserveInfo를 파일로 백업한다.
      //내용이 아무것도 없으면 파일로 백업하지 않는다.
      if (numberOfReserve != 0) {
        LogFileManager.setReserveInfoInsert(reserveInfoList);
      }
    }
    catch (Exception e) {
    	LOGGER.error(e);
    	//e.printStackTrace();
      WorkDBManager.refreshConn();
      if(e instanceof SQLException){
        LOGGER.info("err-sql:" + NEXT_RESERVE_MAIL_QUERY);
      }
      //e.printStackTrace();
      LogFileManager.runLogWriter("setStatusUpdate", e.toString());

      //에러 로그에 남겨준다.
      ErrorLogGenerator.setErrorLogFormat("ReserveManager",
                                          ReserveStatusCode.S_LIST_EXTRACT_ERROR_TYPE,
                                          ReserveStatusCode.S_LIST_EXTRACT_SQL_ERROR_COMMENT, mID);
      reserveInfoList = null;
    }
    finally {
      try {
        WorkDBManager.releaseConnection(con_work);
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
    return reserveInfoList;
  }

  /**
   * 한번에 가져오는 메일 예약 리스트의 갯수를 반환한다.
   * @version 1.0
   * @author ymkim
   * @return int 예약 메일 리스트의 갯수
   */
  public int getNumberOfReserve() {
    if (dataUnitList != null) {
      return dataUnitList.size();
    }
    else {
      return 0;
    }
  }

  /**
   * 예약 테이블의 상태를 업데이트 한다.(메모리에 있는 값으로 상태를 업데이트 한다)
   * @version 1.0
   * @author ymkim
   * @param reserveList 예약 메일 리스트
   * @return boolean true - 상태 업데이트 성공, false - 상태 업데이트 실패
   */
  public boolean setStatusUpdate(DataUnitInfoList reserveList) {
    //public void addStringIndex(int index, String key,String value)
    //reserveInfoList.addStringIndex(
    //DB연결 Instance를 얻는다.
    Connection con_work = null;
    PreparedStatement pstmt = null;

    String[] statusList = null;
    String[] midList = null;
    String[] sidList = null;

    boolean return_value = false;

    //에러 로그 리스트
    ArrayList errorLogInfoList = new ArrayList();
    Hashtable errorLogInfo = null;

    try {
      //DBManager dManager = DBManager.getInstance(Config.WORK_DB);
      con_work = DBManager.getConnection(Config.WORK_DB);

      pstmt = con_work.prepareStatement(STATUS_UPDATE_QUERY);

      statusList = reserveList.getStringArray("STATUS");
      midList = reserveList.getStringArray("MID");
      sidList = reserveList.getStringArray("SUBID");

      for (int i = 0; i < midList.length; i++) {
        if (statusList[i] == ReserveStatusCode.CONTENTS_SUCCESS) { //성공이면
          pstmt.setString(1, null);
        }
        else {
          pstmt.setString(1, statusList[i]);
        }
        pstmt.setString(2, midList[i]);
        pstmt.setInt(3, Integer.parseInt(sidList[i]));
        pstmt.executeUpdate();
      }

      //con_work.commit();
      return_value = true;
    }
    catch (Exception e) {
    	LOGGER.error(e);
      WorkDBManager.refreshConn();
      //e.printStackTrace();
      LogFileManager.runLogWriter("setStatusUpdate", e.toString());

      //에러 로그를 남겨준다.
      String errorInfo = new StringBuffer().append(midList[0])
          .append("-").append(midList[midList.length - 1]).toString();
      ErrorLogGenerator.setErrorLogFormat("ReserveManager",
                                          ReserveStatusCode.SQL_ERROR_TYPE,
                                          ReserveStatusCode.
                                          STATUS_UPDATE_FAIL_COMMENT, errorInfo);

      return_value = false;
    }
    finally {
      try {
        WorkDBManager.releaseConnection(con_work);
      }
      catch (Exception e) {LOGGER.error(e);}

      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception e) {LOGGER.error(e);}
    }
    return return_value;
  }

  /**
   * 예약 테이블의 상태를 업데이트 한다.(파일에 있는 값으로 상태를 업데이트 한다. 즉, 상태값의 복구가 이루어진다.)
   * @version 1.0
   * @author ymkim
   * @param recoveryFile 예약 메일 로그파일
   * @return boolean true - 상태 업데이트 성공, false - 상태 업데이트 실패
   */
  public static boolean setStatusUpdate(File recoveryFile) {
    Connection con_work = null;
    PreparedStatement pstmt = null;

    Properties pro = new Properties();
    FileInputStream fi = null;

    String firstMID = "";
    String lastMID = "";

    boolean return_value = false;

    try {
      con_work = DBManager.getConnection(Config.WORK_DB);
      pstmt = con_work.prepareStatement(STATUS_UPDATE_QUERY);

      //MID=STATUS 의 형태로 저장되어있는 파일의 정보를 Properties 에서 불러들인다.
      fi = new FileInputStream(recoveryFile);
      pro.load(fi);

      //Enumeration enum = pro.propertyNames();
      Iterator enu = pro.keySet().iterator();
      String key = "";
      String value = "";

      StringTokenizer stnID = null;
      String mid = "";
      String subid = "";

      int countKey = 0;

      while (enu.hasNext()) {
        key = (String) enu.next();
        value = pro.getProperty(key);

        stnID = new StringTokenizer(key, "_");
        if (stnID.countTokens() != 2) {
          throw new Exception(
              "msgID 의 형식에 이상이 있거나, msgID에 '_', '=' 가 포함되어잇습니다.");
        }

        mid = stnID.nextToken();
        subid = stnID.nextToken();

        if (countKey == 0) {
          firstMID = key;
        }

        lastMID = value;

        if (value == ReserveStatusCode.CONTENTS_SUCCESS) { //성공이면
          pstmt.setString(1, null);
        }
        else {
          pstmt.setString(1, value);
        }
        pstmt.setString(2, mid);
        pstmt.setString(3, subid);
        pstmt.executeUpdate();

        countKey++;
      }

      //con_work.commit();

      return_value = true;
    }
    catch (Exception e) {
    	LOGGER.error(e);
      WorkDBManager.refreshConn();
      //e.printStackTrace();
      LogFileManager.runLogWriter("setStatusUpdate", e.toString());

      //에러 로그를 남겨준다.
      String errorInfo = firstMID + "-" + lastMID;
      ErrorLogGenerator.setErrorLogFormat("ReserveManager",
                                          ReserveStatusCode.SQL_ERROR_TYPE,
                                          ReserveStatusCode.
                                          STATUS_UPDATE_FAIL_COMMENT, errorInfo);

      return_value = false;
    }
    finally {
      try {
        if (fi != null) {
          fi.close();
        }
      }
      catch (Exception e) {LOGGER.error(e);}

      try {
        WorkDBManager.releaseConnection(con_work);
      }
      catch (Exception e) {LOGGER.error(e);}

      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception e) {LOGGER.error(e);}
    }
    return return_value;
  }

  private static String getResultByCharStream(java.io.Reader in, String charset) {
    String result = null;
    StringBuffer sb = new StringBuffer();
    java.io.BufferedReader br = null;
    try {
      br = new java.io.BufferedReader(in);
      for (String temp; (temp = br.readLine()) != null; ) {
        sb.append(temp).append("\r\n");
      }
      //result = sb.toString();
      //result = new String(sb.toString().getBytes("utf-8"), charset);
      //result = new String(sb.toString().getBytes(charset), "utf-8" );
      result = new String(sb.toString().getBytes(charset));
    }
    catch (java.io.IOException ie) {
    	LOGGER.error(ie);
      //ie.printStackTrace();
    }
    finally {
      try {
        if (br != null)
          br.close();
      }
      catch (Exception e) {LOGGER.error(e);}
    }
    return result;
  }

  private static String getResultByCharStream(java.io.Reader in) {
    String result = null;
    StringBuffer sb = new StringBuffer();
    java.io.BufferedReader br = null;
    try {
      br = new java.io.BufferedReader(in);
      for (String temp; (temp = br.readLine()) != null; ) {
        sb.append(temp).append("\r\n");
      }
      result = sb.toString();
      //result = new String(sb.toString().getBytes("utf-8"), charset);
      //result = new String(sb.toString().getBytes(charset), "utf-8" );
    }
    catch (java.io.IOException ie) {
    	LOGGER.error(ie);
      //ie.printStackTrace();
    }
    finally {
      try {
        if (br != null)
          br.close();
      }
      catch (Exception e) {LOGGER.error(e);}
    }
    return result;
  }

}