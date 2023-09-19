package com.tscheduler.util;
/*
* 에러 코드 정리
*/

/**
 * 예약 메일 상태 코드 클래스
 * @version 1.0
 * @author ymkim
 */
public class ReserveStatusCode
{
	//수신자 ID 없음
	public static String NO_USERID = "NOUSERID";
	//수신자 이름 없음
	public static String NO_USERNAME = "NOUSERNAME";
	//수신자 이메일 없음
	public static String NO_USERMAIL = "NOUSERMAIL";

	//--> 상태에 대한 코드
	/**기본 예약 상태 코드*/
	public static String DEFAULT_RESERVE = "0";
	/**발송자 이메일 주소 에러 코드*/
	public static String S_EMAIL_ERROR = "1";
	/**수신자 이메일 주소 에러 코드*/
	public static String R_EMAIL_ERROR = "2";
	/**수신자 리스트 추출 실패 코드*/
	public static String R_LIST_EXTRACT_ERROR = "3";
	/**컨덴츠 생성 성공 코드*/
	public static String CONTENTS_SUCCESS = "4";
	/**컨덴츠 생성 에러 코드*/
	public static String CONTENTS_FAIL = "5";

	//-->상태에 대한 제목
	/**기본 예약 상태 제목*/
	public static String DEFAULT_RESERVE_TYPE = "RESERVE";
	/**발송자 이메일 주소 에러 제목*/
	public static String S_EMAIL_ERROR_TYPE = "SEND_EMAIL_ERROR";
	/**발송자 리스트 추출 실패 제목*/
	public static String S_LIST_EXTRACT_ERROR_TYPE = "SENDER_EXTRACT_ERROR";
	/**수신자 이메일 주소 에러 제목*/
	public static String R_EMAIL_ERROR_TYPE = "RECEIVER_EMAIL_ERROR";
	/**수신자 리스트 추출 실패 제목*/
	public static String R_LIST_EXTRACT_ERROR_TYPE = "RECEIVER_EXTRACT_ERROR";
	/**컨덴츠 생성 성공 제목*/
	public static String CONTENTS_SUCCESS_TYPE = "CONTENT_CREATION_SUCCESS";
	/**컨덴츠 생성 에러 제목*/
	public static String CONTENTS_FAIL_TYPE = "CONTENT_CREATION_FAIL";
	/**SQL에러 제목*/
	public static String SQL_ERROR_TYPE = "SQL_ERROR";

	//-->상태에 대한 설명(에러나 혹은 성공등)
	/**기본 예약 상태 설명*/
	public static String DEFAULT_RESERVE_COMMENT = "예약되어 있습니다";
	/**발송자 이메일 주소 에러 설명*/
	public static String S_EMAIL_ERROR_COMMENT = "발송자의 이메일 규칙이 잘못되었습니다";
	/**발송자 리스트 SQL 에러 설명*/
	public static String S_LIST_EXTRACT_SQL_ERROR_COMMENT = "발송자 리스트를 얻는 SQL문이 잘못되었습니다";
	/**수신자 이메일 주소 에러 설명*/
	public static String R_EMAIL_ERROR_COMMENT = "수신자의 이메일 규칙이 잘못되었습니다";
	/**수신자 리스트 SQL 에러 설명*/
	public static String R_LIST_EXTRACT_SQL_ERROR_COMMENT = "수신자 리스트를 얻는 SQL문이 잘못되었습니다";
	/**수신자 리스트 SQL 에러 설명*/
	public static String R_LIST_EXTRACT_DB_ERROR_COMMENT = "수신자 리스트를 얻는 DB에 문제가 발생했습니다";
	/**수신자 리스트 File 에러 설명*/
	public static String R_LIST_EXTRACT_FILE_ERROR_COMMENT = "수신자 리스트를 얻는 FILE에 문제가 발생했습니다";
	/**수신자 리스트 없음 에러 설명*/
	public static String R_LIST_NOBODY_ERROR_COMMENT = "수신자가 아무도 없습니다.";
	/**컨덴츠 생성 성공 설명*/
	public static String CONTENTS_SUCCESS_COMMENT = "컨덴츠를 생성하였습니다";
	/**컨덴츠 생성 에러 설명*/
	public static String CONTENTS_WRITE_FAIL_COMMENT = "컨덴츠를 FILE에 쓰다가 에러가 발생했습니다";
	/**로그 파일생성시 에러 설명*/
	public static String LOG_WRITE_FAIL_COMMENT = "로그 파일을 쓰다가 에러가 발생했습니다";
	/**SQL 상태 업데이트 에러 설명*/
	public static String STATUS_UPDATE_FAIL_COMMENT = "DB에 상태를 업데이트 하다가 에러가 발생했습니다";
	/**지정된 패스에 컨덴츠 파일이 없는 에러 설명*/
	public static String CONTENT_NOT_FOUND_COMMENT = "지정된 패스나 컨덴츠 파일이 없거나 혹은 잘못된 URL입니다";
	/**지정된 패스에 첨부파일이 없는 에러 설명*/
	public static String ATTACH_FILE_NOT_FOUND_COMMENT = "지정된 패스에 첨부파일이 없습니다";
	/**LegacyDB의 정보를 얻기 실패에 대한 설명*/
	public static String LEGACY_INFO_FAIL_COMMENT = "DBCODE에 따른 LEGACY DB의 정보를 가져올수가 없습니다";
	/**ResultLog를 입력시 실패에 대한 설명*/
	public static String RESULT_LOG_INPUT_FAIL_COMMENT = "RESULT_LOG입력시 SQL 에러가 발생하였습니다.";
	/**응답로그테이블 에러 설명*/
	public static String RESPONSE_LOG_FAIL_COMMENT = "응답로그테이블이 작동하지 않습니다.";
	/**개인정보 체크 설명*/
	public static String SUBJECT_PERSONAL = "고객정보포함";
	/**개인정보 체크 설명*/
	public static String BODY_PERSONAL = "고객정보포함";
	/**개인정보 체크 설명*/
	public static String ATTACH_PERSONAL = "첨부파일에 개인정보가 있습니다.";
	/**보안메일 체크 설명*/
	public static String SECU_CONTENT = "EXCEL 변환에 실패하였습니다..";
	
}