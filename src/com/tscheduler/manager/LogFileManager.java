package com.tscheduler.manager;

import java.io.*;
import java.util.*;
import java.sql.*;

import com.tscheduler.util.DataUnitInfoList;
import com.tscheduler.util.Config;
import com.tscheduler.util.ReserveStatusCode;
import com.tscheduler.util.LogDateGenerator;
import com.tscheduler.util.ErrorLogGenerator;
import com.tscheduler.dbbroker.DBManager;
import com.tscheduler.dbbroker.WorkDBManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Log파일의 날짜 표현 클래스
 * @version 1.0
 * @author ymkim
 */
public class LogFileManager {
	
  private static final Logger LOGGER = LogManager.getLogger(LogFileManager.class.getName());
	
  /**로그 폴더*/
  public static String LOG_FOLDER = "Log";
  /**예약 메일 로그 폴더*/
  public static String RESERVE_LIST_FOLDER = "ReserveLog";
  /**에러 로그 폴더*/
  public static String ERROR_LIST_FOLDER = "ErrorLog";
  /**실행시간에 발생하는 에러 로그 폴더*/
  public static String RUN_ERROR_LIST_FOLDER = "RunErrorLog";
  /**수신자 리스트 폴더*/
  public static String RECEIVER_LIST_FOLDER = "ReceiverList";
  /**예약 메일 로그 파일명*/
  public static String RESERVE_FILE = "ReserveLog.log";
  /**로그 파일 확장자*/
  public static String LOG_EXT = ".log";
  /**백업파일명*/
  public static String BACK_EXT = ".bak";

  /**수신확인폴더*/
  public static String RESPONSE_CONFIRM_FOLDER_NAME = "ResponseConfirmLog";
  /**수신확인 파일*/
  public static String RESPONSE_CONFIRM_FILE_NAME = "ResponseConfirmInfo.log";
  /**수신자 리스트 파일*/
  public static String RECEIVER_FILE_NAME = "R_List";

  /**예약로그가 쌓이는 폴더*/
  public static String RESERVE_LOG_FOLDER = Config.ROOT_DIR + File.separator +
      LOG_FOLDER + File.separator + RESERVE_LIST_FOLDER + File.separator;
  /**에러로그가 쌓이는 폴더*/
  public static String ERROR_LOG_FOLDER = Config.ROOT_DIR + File.separator +
      LOG_FOLDER + File.separator + ERROR_LIST_FOLDER + File.separator;
  /**실행시간 에러로그가 쌓이는 폴더*/
  public static String RUN_ERROR_LOG_FOLDER = Config.ROOT_DIR + File.separator +
      LOG_FOLDER + File.separator + RUN_ERROR_LIST_FOLDER + File.separator;
  /**수신확인이 쌓이는 폴더*/
  public static String RESPONSE_CONFIRM_FOLDER = Config.ROOT_DIR +
      File.separator + LOG_FOLDER + File.separator +
      RESPONSE_CONFIRM_FOLDER_NAME + File.separator;
  /**수신자 리스트가 쌓이는 폴더*/
  public static String RECEIVER_LOG_FOLDER = Config.ROOT_DIR + File.separator +
      LOG_FOLDER + File.separator + RECEIVER_LIST_FOLDER + File.separator;

  /**예약 리스트파일*/
  public static String RESERVE_FULL_PATH = RESERVE_LOG_FOLDER + File.separator +
      RESERVE_FILE;
  /**수신확인 파일*/
  public static String RESPONSE_CONFIRM_FULL_PATH = RESPONSE_CONFIRM_FOLDER +
      File.separator + RESPONSE_CONFIRM_FILE_NAME;

  /**수신확인테이블 검색용쿼리*/
  public static String RES_SEARCH_QUERY =
      "SELECT RSID FROM TS_RESPONSELOG WHERE RSID = ?";
  /**수신확인테이블 입력용쿼리*/
  public static String RES_INSERT_QUERY =
      "INSERT INTO TS_RESPONSELOG VALUES(?,?,?,?,?,?,?)";
  /**수신확인통계테이블 검색용쿼리*/
  public static String RES_STATIC_SEARCH_QUERY =
      "SELECT COUNT(*) FROM TS_RESPONSELOG WHERE MID = ? AND SUBID = ?";
  /**수신확인통계테이블 수정용쿼리*/
  public static String RES_STATIC_UPDATE_QUERY =
      "UPDATE TS_RESPONSE_RSINFO SET RSCOUNT = ? WHERE MID=? AND SUBID = ?";

  /**
   * 예약 메일 로그를 파일로 저장한다.
   * @version 1.0
   * @author ymkim
   * @param reserveInfoList 예약 메일 리스트
   * @return boolean true - 파일 저장 성공, false - 파일 저장 실패
   */
  public static boolean setReserveInfoInsert(DataUnitInfoList reserveInfoList) {
    File mdir = new File(RESERVE_LOG_FOLDER);

    int rSize = reserveInfoList.getDataUnitInfoListSize();

    String[] midList = reserveInfoList.getStringArray("MID");
    String[] subidList = reserveInfoList.getStringArray("SUBID");

    FileOutputStream fo = null;

    boolean return_value = false;

    try {
      Properties pro = new Properties();
      fo = new FileOutputStream(new File(mdir, RESERVE_FILE));
      StringBuffer msgKey = new StringBuffer();
      for (int i = 0; i < midList.length; i++) {
        msgKey.append(midList[i]).append("_").append(subidList[i]);
        pro.setProperty(msgKey.toString(), ReserveStatusCode.DEFAULT_RESERVE);
        msgKey.setLength(0);
      }
      pro.store(fo, "");
      return_value = true;
    }
    catch (Exception e) {
      e.printStackTrace();
      return_value = false;
    }
    finally {
      try {
        if (fo != null) {
          fo.close();
          fo = null;
        }
      }
      catch (Exception e) {
      }
    }
    return return_value;
  }

  /**
   * 예약 메일 로그를 파일에 상태값만 바꿔준다.
   * @version 1.0
   * @author ymkim
   * @param mID 예약 메일 ID
   * @param status 수정할 상태값
   * @return boolean true - 파일 수정 성공, false - 파일 수정 실패
   */
  public static boolean setReserveStatusUpdate(String mID, String subID,
                                               String status) {
    Properties pro = new Properties();
    FileInputStream fi = null;
    FileOutputStream fo = null;
    String strMsgID = new StringBuffer()
        .append(mID).append("_").append(subID).toString();
    boolean return_value = false;

    try {
      File backFile = new File(RESERVE_FULL_PATH);
      if (backFile.isFile()) { //파일이 있으면;
        fi = new FileInputStream(new File(RESERVE_FULL_PATH));
        pro.load(fi);
      }

      fo = new FileOutputStream(new File(RESERVE_FULL_PATH));
      pro.setProperty(strMsgID, status);
      pro.store(fo, strMsgID);
      return_value = true;
    }
    catch (Exception e) {
      e.printStackTrace();
      return_value = false;
    }
    finally {
      try {
        if (fi != null) {
          fi.close();
          fi = null;
        }
      }
      catch (Exception e) {
      }

      try {
        if (fo != null) {
          fo.close();
          fo = null;
        }
      }
      catch (Exception e) {
      }
    }
    return return_value;
  }

  /**
   * Reserve파일(예약파일)의 내용을 다 사용하면 그 파일을 지운다.
   * @version 1.0
   * @author ymkim
   * @return boolean true - 파일 삭제 성공, false - 파일 삭제 실패
   */
  public static boolean deleteReserveFile() {
    boolean return_value = false;
    try {
      File resFile = new File(RESERVE_FULL_PATH);
      while (true) {
        if (! (resFile.isFile())) { //파일이 없다면...
          break;
        }

        if (resFile.delete()) {
          return_value = true;
          break;
        }
        LOGGER.info("안지워졌따");
      }
//			if(resFile.delete())
//			{
//				return_value = true;
//			}else
//			{
//				return_value = false;
//			}

    }
    catch (Exception e) {
      return_value = false;
    }
    return return_value;
  }

  /**
   * 에러로그 처리를 파일에 저장한다.
   * @version 1.0
   * @author ymkim
   * @param errorInfo 에러 정보 내용
   * @return boolean true - 파일 생성 성공, false - 파일 생성 실패
   */
  public static boolean setErrorInfoInsert(String errorInfo) {
    String errorLogName = LogDateGenerator.getLogFolderName() + LOG_EXT;
    String errorLogFullPath = ERROR_LOG_FOLDER + errorLogName;
    PrintWriter pw = null;

    boolean return_value = false;

    try {
      pw = new PrintWriter(new FileWriter(errorLogFullPath, true));
      pw.println(errorInfo);
      pw.flush();

      return_value = true;
    }
    catch (Exception e) {
      e.printStackTrace();
      return_value = false;
    }
    finally {
      if (pw != null) {
        pw.close();
      }
    }
    return return_value;
  }

  /**
   * 실시간 에러로그 처리를 파일에 저장한다.
   * @version 1.0
   * @author ymkim
   * @param errorInfo 에러 정보 내용
   * @return boolean true - 파일 생성 성공, false - 파일 생성 실패
   */
  public static boolean runLogWriter(String mid, String errorInfo) {
    String errorLogName = LogDateGenerator.getLogFolderName() + LOG_EXT;
    String errorLogFullPath = RUN_ERROR_LOG_FOLDER + errorLogName;
    PrintWriter pw = null;

    boolean return_value = false;

    try {
      LOGGER.info(errorLogFullPath);
      pw = new PrintWriter(new FileWriter(errorLogFullPath, true));
      pw.println("[Date:" + LogDateGenerator.getResultLogTime() + "/MID:" + mid +
                 "]" + errorInfo);
      pw.flush();

      return_value = true;
    }
    catch (Exception e) {
      e.printStackTrace();
      return_value = false;
    }
    finally {
      if (pw != null) {
        pw.close();
      }
    }
    return return_value;
  }
}