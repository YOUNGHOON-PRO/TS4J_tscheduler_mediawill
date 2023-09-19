package com.tscheduler.util;
/*
*설명: 한번만 property를 파일에서 읽어들인후에는 그 값들을 내부 변수명에 저장하고
  다시 활용한다.
*/
import java.util.*;

import com.tscheduler.util.EncryptUtil;

import java.io.*;

/**
 * 환경설정 파일을 관리하는 클래스
 * @version 1.0
 * @author ymkim
 */
public class Config
{
	/**Default DB Connection Pool의 Size*/
	public static int DEFAULT_MAX_CONN = 5;
	/**WORK_DB의 이름*/
	public static String WORK_DB = "WORK_DB";
	/**LEGACY_DB의 이름*/
	public static String LEGACY_DB = "LEGACY_DB";
	/**MAIN_CFG의 이름*/
	public static String MAIN_CFG = "MAIN_CFG";
	/**ROOT_DIRECTORY의 경로*/
	public static String ROOT_DIR = ".";
	/**구분자 값*/
	public static String DELIMITER ="''";
	/**CARRIGE RETURN 값*/
	public static String NEW_LINE = "\r\n";
	/**DB_CONFIG 파일의 경로*/
	public static String DB_CFG_FILE = "../config/database.cfg";
	/**MAIN_CONFIG 파일의 경로*/
	public static String MAIN_CFG_FILE = "../config/..MainConfig.cfg";
	/**db config파일을 읽어들였는지를 확인*/
	boolean isDBSetting = true;
	/**main config파일을 읽어들였는지를 확인*/
	boolean isMainSetting = true;

	/**트레이스 출력 여부*/
	boolean DebugOutput = true;

	/**db가 oracle인지 mssql인지 체크*/
	private String wd_DbType="";
	/**DB 연결 Driver*/
	private String wd_Driver="";
	/**DB 연결 URL*/
	private String wd_URL="";
	/**DB 연결 User ID*/
	private String wd_UserID="";
	/**DB 연결 User Password*/
	private String wd_UserPass="";
	
	/**DB 연결 User Password enc*/
	private String wd_PassYn="";
	
	/**Connection Pool의 연결 갯수*/
	private int    wd_MaxConn=0;

	/**한번에 가져오는 예약메일의 갯수*/
	private String resMaxSize="";
	/**디폴트 컨덴츠 타입*/
	private String cType="";
	/**디폴트 인코딩 타입*/
	private String contEncode="";
	/**헤더쪽의 인코딩 타입*/
	private String headEncode="";
	/**Charset*/
	private String charSet="";
	/**Boundary*/
	private String boundary="";
	/**TScheduler가 DB에 있는 MailQueue를 검색하는 Term*/
	private long   dbCheckTime;
	/**수신 확인 URL*/
	private String receiveDefineHost="";
	/**NeoSMTP구동 갯수*/
	private String transferNum="";
	/**Queue폴더의 경로*/
	private String queueFolder="";
	/**Merge 리스트*/
	private String mergyList="";
	/**한 폴더당 eml파일의 갯수*/
	private String emlNumPerFolder = "";
	/**한 MID에 대해서 한 파일에 저장되는 수신자 리스트 수*/
	private String legacyRSListSize = "";
	/**dbconfig인지 mainconfig인지 설정*/
	String cfgType="";

        /**다국어지원 설정*/
	private boolean isMultiLang;
	
	/**개인정보 체크 여부 */
	private String personal_yn;

	/**개인정보 채쿠 예외 처리 */
	private String personal_pass;
	
	/**Email정보 암호와 여부 (TS_mailqueue 의 smail ,TS_recipentinfo의 rmail)*/
	private String enc_yn;

	
	
	/**Config파일의 Singleton Instance파일*/
	static private Config cfgInstance;       // The single instance
	/**
	 * Config클래스 객체를 하나만 생성한다.(Singleton)
	 * @version 1.0
	 * @author ymkim
	 * @return Config Singleton 객체
	 */
	static synchronized public Config getInstance()
	{
		if (cfgInstance == null) {
			cfgInstance = new Config();
		}
		else
		{
//			status= false;
		}
		return cfgInstance;
	}

	/**
	 * Config클래스 생성자
	 * @version 1.0
	 * @author ymkim
	 */
	private Config()
	{

	}

	/**
	 * 해당 컨피그 파일을 읽어들인다.
	 * @version 1.0
	 * @author ymkim
	 * @param cfgType 연결할 환경파일(dbConfig 파일이나 혹은 mainConfig 파일)
	 */
	public void loadConfig(String cfgType)
	{
		cfgType = cfgType;

		if(cfgType.equals(WORK_DB)||cfgType.equals(LEGACY_DB)) //workDB를 연결할 경우(발송예약 리스트정보 가져올때)
		{
			dbCfgLoad();
		}
		else if(cfgType.equals(MAIN_CFG))	//T-Schedular의 여러 가지 Config정보를 가져올때
		{
			mainCfgLoad();
		}
	}

	/**
	 * DB의 종류(Oracle, Mssql)
	 * @version 1.0
	 * @author ymkim
	 * @return String dbType
	 */
	public String getDBType()
	{
		return wd_DbType;
	}

	/**
	 * 드라이버값을 반환한다.
	 * @version 1.0
	 * @author ymkim
	 * @return String DB 연결 Driver
	 */
	public String getDBDriver() {
		return  wd_Driver;
	}

	public boolean getDebugOutput() {
		return DebugOutput;
	}

	/**
	 * 연결 URL을 반환한다
	 * @version 1.0
	 * @author ymkim
	 * @return String DB연결 URL
	 */
	public String getDBURL() {
		return  wd_URL;
	}

	/**
	 * DB 연결 유저 ID를 반환한다.
	 * @version 1.0
	 * @author ymkim
	 * @return String DB연결 User ID
	 */
	public String getDBUserID() {
		return  wd_UserID;
	}

	/**
	 * DB연결 유저 Pass를 반환한다.
	 * @version 1.0
	 * @author ymkim
	 * @return String DB연결 User Pass
	 */
	public String getDBUserPass() {
		return  wd_UserPass;
	}
	
	/**
	 * DB연결 유저 Pass를 반환한다.
	 * @version 1.0
	 * @author ymkim
	 * @return String DB연결 User Pass
	 */
	public String getDBUserPassYN() {
		return  wd_PassYn;
	}


	/**
	 * DB의 컨넥션풀의 갯수를 얻어온다.
	 * @version 1.0
	 * @author ymkim
	 * @return int 컨넥션 풀 연결 갯수
	 */
	public int getMaxConn()
	{
		return  wd_MaxConn;
	}


	/**
	 * WorkDB에서 예약리스트를 받아들일 갯수를 반환한다.
	 * @version 1.0
	 * @author ymkim
	 * @return String 한번에 가져올 예약 메일 리스트의 갯수
	 */
	public String getResMaxSize()
	{
		return resMaxSize;
	}


	/**
	 * 디폴트 컨덴츠 타입
	 * @version 1.0
	 * @author ymkim
	 * @return String 디폴트 컨덴츠 타입
	 */
	public String getDefaultContentType()
	{
		return cType;
	}


	/**
	 * 디폴트 Charset
	 * @version 1.0
	 * @author ymkim
	 * @return String 디폴트 Charset
	 */
	public String getDefaultCharset()
	{
		return charSet;
	}


	/**
	 * 디폴트 컨덴츠 인코딩타입
	 * @version 1.0
	 * @author ymkim
	 * @return String 디폴트 컨덴츠 인코딩타입
	 */
	public String getDefaultContEncType()
	{
		return contEncode;
	}


	/**
	 * 디폴트 헤더 인코딩타입
	 * @version 1.0
	 * @author ymkim
	 * @return String 디폴트 헤더 인코딩타입
	 */
	public String getDefaultHeadEncType()
	{
		return headEncode;
	}


	/**
	 * 디폴트 Boundary
	 * @version 1.0
	 * @author ymkim
	 * @return String 디폴트 Boundary
	 */
	public String getDefaultBoundary()
	{
		return boundary;
	}


	/**
	 * DB체크 타임을 얻어온다.
	 * @version 1.0
	 * @author ymkim
	 * @return long DB체크 타임
	 */
	public long getDBCheckTime()
	{
		return dbCheckTime;
	}


	/**
	 * 수신확인 연결 Host정보를 얻어온다.
	 * @version 1.0
	 * @author ymkim
	 * @return String 수신확인 연결 Host정보
	 */
	public String getReceiveDefineHost()
	{
		return receiveDefineHost;
	}


	/**
	 * TRANSFER(NeoSMTP)의 갯수를 얻는다.
	 * @version 1.0
	 * @author ymkim
	 * @return int NeoSMTP구동수
	 */
	public int getTransferNum()
	{
		if(transferNum.equals("") || transferNum ==null)
		{
			return 0;
		}
		else
		{
			try
			{
				return Integer.parseInt(transferNum);
			}
			catch(Exception e)
			{
				return 0;
			}
		}
	}


	/**
	 * Queue 폴더 경로
	 * @version 1.0
	 * @author ymkim
	 * @return String Queue 폴더 경로
	 */
	public String getQueueFolder()
	{
		return queueFolder;
	}


	/**
	 * 머지할 내용들
	 * @version 1.0
	 * @author ymkim
	 * @return String 머지 리스트
	 */
	public String getMergyList()
	{
		return mergyList;
	}

	/**
	 * 개인정보 체크 여부 및 예외처리 
	 * @return
	 */
	public String getPersonal_yn() {
		return personal_yn;
	}

	public void setPersonal_yn(String personal_yn) {
		this.personal_yn = personal_yn;
	}

	public String getPersonal_pass() {
		return personal_pass;
	}

	public void setPersonal_pass(String personal_pass) {
		this.personal_pass = personal_pass;
	}
	
	public String getEnc_yn() {
		return enc_yn;
	}

	public void setEnc_yn(String enc_yn) {
		this.enc_yn = enc_yn;
	}

	/**
	 * 폴더 안에 들어갈 eml파일 갯수
	 * @version 1.0
	 * @author ymkim
	 * @return int 폴더 안에 들어갈 eml파일 갯수
	 */
	public int getEmlNumPerFolder()
	{
		int emlSize = 0;
		try
		{
			emlSize = Integer.parseInt(emlNumPerFolder);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return emlSize;
	}


	/**
	 * 한 MID에 대해서 한번에 파일로 저장할 수신자 수
	 * @version 1.0
	 * @author ymkim
	 * @return int 한 MID에 대해서 한번에 파일로 저장할 수신자 수
	 */
	public int getLegacyRSListSize()
	{
		int rsSize = 0;
		try
		{
			rsSize = Integer.parseInt(legacyRSListSize);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return rsSize;
	}

        /**
         *  다국어지원을 할 것인지 유무
         */
        public boolean isMultiLang(){
          return isMultiLang;
        }


	/**
	 * DB연결에 관련한 config파일을 읽어들인다.
	 * @version 1.0
	 * @author ymkim
	 */
	private void dbCfgLoad()
	{
		if(isDBSetting)
		{
			//String dbCfgPath = "../config/database.conf";
			String dbCfgPath = "./config/database.conf";
			FileInputStream fi = null;
			
	        //복호화
			String ALGORITHM = "PBEWithMD5AndDES";
			String KEYSTRING = "ENDERSUMS";
			EncryptUtil enc =  new EncryptUtil();
			
			try
			{
				fi = new FileInputStream(new File(dbCfgPath));
				Properties prop = new Properties();
				prop.load(fi);
				wd_Driver = prop.getProperty("DRIVER");
				wd_URL = prop.getProperty("URL");
				wd_UserID = prop.getProperty("USER");
				wd_UserPass = prop.getProperty("PASSWARD");
				wd_PassYn = prop.getProperty("PASSWARD_YN");
				
				//복호화
				if("Y".equals(wd_PassYn)) {
					wd_UserPass = enc.getJasyptDecryptedFixString(ALGORITHM, KEYSTRING, wd_UserPass);
				}
				
				try
				{
					wd_MaxConn = Integer.parseInt(prop.getProperty("MAX_CONN"));
				}
				catch(Exception e)
				{
					wd_MaxConn = DEFAULT_MAX_CONN;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try {
					if( fi != null ) {
						fi.close();
					}
				}
				catch(Exception e) {
				}
				isDBSetting = false;
			}
		}
		else
		{
		}
	}

	/**
	 * T-Schedular메인 설정 파일을 읽어들인다.
	 * @version 1.0
	 * @author ymkim
	 */
	private void mainCfgLoad()
	{
		if(isMainSetting)
		{
			//String mainCfgPath = "../config/TScheduler.conf";
			String mainCfgPath = "./config/TScheduler.conf";
			FileInputStream fi = null;
			try
			{

				fi = new FileInputStream(new File(mainCfgPath));
				Properties prop = new Properties();
				prop.load(fi);

				//WorkDB에서 예약리스트를 받아들일 갯수
				wd_DbType 	=  prop.getProperty("WD_DB_TYPE");
				resMaxSize 	=  prop.getProperty("RES_MAX_SIZE");
				cType 		=  prop.getProperty("DEFAULT_CONTENT_TYPE");
				charSet 	=  prop.getProperty("DEFAULT_CHARSET");
				contEncode	=  prop.getProperty("DEFAULT_CONTENTS_ENCODING");
				headEncode	=  prop.getProperty("DEFAULT_HEADER_ENCODING");
				boundary	=  prop.getProperty("DEFAULT_BOUNDARY");
				dbCheckTime =  Long.parseLong(prop.getProperty("DB_CHECK_TIME"));
				receiveDefineHost	=  prop.getProperty("RECEIVE_HOST_URL");
				transferNum	=  prop.getProperty("MULTI_TRANSFER");
				queueFolder =  prop.getProperty("QUEUE_FOLDER");
				mergyList =  prop.getProperty("MERGY_LIST");
				emlNumPerFolder =  prop.getProperty("EML_NUM_PER_FOLDER");
				legacyRSListSize =  prop.getProperty("LEGACY_RS_LIST_SIZE");
                                isMultiLang = new Boolean(prop.getProperty("ISMULTILANG","false")).booleanValue();
				DebugOutput = Boolean.getBoolean(prop.getProperty("DEBUG_OUTPUT", "true"));
				
				personal_yn = prop.getProperty("PERSONAL_YN");
				personal_pass = prop.getProperty("PERSONAL_PASS");
				
				enc_yn = prop.getProperty("ENC_YN");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try {
					if( fi != null ) {
						fi.close();
						fi = null;
					}
				}
				catch(Exception e) {
				}
				isMainSetting = false;
			}
		}
		else
		{
		}
	}
}