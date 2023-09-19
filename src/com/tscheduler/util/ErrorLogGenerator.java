package com.tscheduler.util;

//에러 로그 파일의 포맷을 생성하는 클래스
import java.util.Hashtable;

import com.tscheduler.manager.LogFileManager;

/**
 * 에러 로그 관리 클래스
 * @version 1.0
 * @author ymkim
 */
public class ErrorLogGenerator
{
	/**
	 * 에러 로그 파일을 만들어준다.
	 * @version 1.0
	 * @author ymkim
	 * @param errorClassName 에러가 발생한 클래스명
	 * @param errorType 에러종류
	 * @param comment 에러메시지
	 * @param etcInfo 기타에러정보
	 */
	public static void setErrorLogFormat(String errorClassName, String errorType, String comment,String etcInfo)
	{
		String errorTime = LogDateGenerator.getLogTime(); //에러가 발생한 시점

		String demimiter = Config.DELIMITER;
		StringBuffer sb = new StringBuffer();
		sb.append(errorTime).append(demimiter).append(errorClassName)
				.append(demimiter).append(errorType).append(demimiter)
				.append(comment).append(demimiter).append(etcInfo);

		//생성된 에러로그를 넣어준다.
		LogFileManager.setErrorInfoInsert(sb.toString());
		sb = null;
	}
}