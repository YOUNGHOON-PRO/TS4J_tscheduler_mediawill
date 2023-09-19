package com.tscheduler.generator;

//NeoSMTP에 필요한 추가 정보를 만들어준다.
import java.util.*;

/**
 * NeoSMTP에게 보내는 정보를 생성하는 클래스
 * @version 1.0
 * @author ymkim
 */
public class SMTPSendInfoGenerator
{
	/**
	 * NeoSMTP에게 보내는 정보를 생성한다.
	 * @version 1.0
	 * @author ymkim
	 * @param smtpSendInfo NeoSMTP에게 보낼 정보들
	 * @return String NeoSMTP가 읽어들이도록 규격화시킨 문자열
	 */
	public static String generateSMTPSendInfo(Hashtable smtpSendInfo)
	{
		String sMail = (String) smtpSendInfo.get("SMAIL");
		String rMail = (String) smtpSendInfo.get("RMAIL");
		String sID = (String) smtpSendInfo.get("SID");
		String sName = (String) smtpSendInfo.get("SNAME");
		String rID = (String) smtpSendInfo.get("RID");
		String rName = (String) smtpSendInfo.get("RNAME");
		String tID = (String) smtpSendInfo.get("TID");
		String refMID = (String) smtpSendInfo.get("REFMID");
		String subID = (String) smtpSendInfo.get("SUBID");
		String requestKey = (String) smtpSendInfo.get("REQUEST_KEY");
		StringBuffer sb = new StringBuffer();

		sb.append(sMail).append(Header.NEW_LINE)
				.append(rMail).append(Header.NEW_LINE)
				.append(sID).append(Header.NEW_LINE)
				.append(sName).append(Header.NEW_LINE)
				.append(rID).append(Header.NEW_LINE)
				.append(rName).append(Header.NEW_LINE)
				.append(tID).append(Header.NEW_LINE)
				.append(refMID).append(Header.NEW_LINE)
				.append(subID).append(Header.NEW_LINE)
				.append(requestKey).append(Header.NEW_LINE)
				.append("<!--DATAPART-->").append(Header.NEW_LINE);
		return sb.toString();
	}
}