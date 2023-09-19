package com.tscheduler.generator;
/*
*	설명:Header를 구성한다. 만든다.
*/

import java.util.*;

import javax.mail.internet.*;
import javax.commerce.util.BASE64Encoder;

import com.tscheduler.util.Config;
import com.tscheduler.util.LogDateGenerator;
import com.tscheduler.util.MergeTrans;

import synap.next.JFilterUtil;
import synap.next.ParttenCheckUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 헤더 생성 클래스
 * @version 1.0
 * @author ymkim
 */
public class HeaderGenerator implements Header
{
	
	private static final Logger LOGGER = LogManager.getLogger(HeaderGenerator.class.getName());
	
	/**디폴트 컨덴츠 타입*/
	private static String DEFAULT_CONTENT_TYPE="text/html";
	/**디폴트 CharacterSet*/
	private static String DEFAULT_CHARSET="ks_c_5601-1987";
	/**디폴트 인코딩 타입*/
	private static String DEFAULT_CONT_ENC_TYPE="8bit";
	/**디폴트 헤더 인코딩 타입*/
	private static String DEFAULT_HEAD_ENC_TYPE="8bit";
	/**디폴트 Boundary*/
	private static String DEFAULT_BOUNDARY="NEOCAST_BOUNDARY---NextPart_000";

	/**HeaderGenerator의 Singleton 객체*/
	private static HeaderGenerator instance;

	/**개인정보 체크 여부*/
	private static String persoanl_yn ="";
	/**개인정보 체크 예외 처리*/
	private static String persoanl_pass="";

	/**발송자이름*/
	String fromName;
	/**발송자 이메일*/
	String fromEmail;
	/**수신자 이름*/
	String rName;
	/**수신자 이메일*/
	String rEmail;
	/**회신주소*/
	String replyEmail;
	/**컨덴츠 타입*/
	String cType;
	/**Character set*/
	String charSet;
	/**인코딩 타입*/
	String contEncodeType;
	/**헤더 인코딩 타입*/
	String headEncodeType;

	

	/**
	 * HeaderGenerator의 singleton객체를 얻는다.
	 * @version 1.0
	 * @author ymkim
	 * @return HeaderGenerator HeaderGenerator 객체를 얻는다.
	 */
	public static HeaderGenerator getInstance()
	{
		if( instance == null ) {
			instance = new HeaderGenerator();
		}
		else {
		}
		return instance;
	}

	/**
	 * 생성자
	 * @version 1.0
	 * @author ymkim
	 */
	private HeaderGenerator()
	{
		//환경설정파일에서 디폴트 값들을 가져온다.
		getMimeDefaultValue();
	}

	//.contents-type과  Content-Transfer-Encoding:타입을 설정한다.
	//생성자
	//발신자 이름, 빌신자 이메일, 리턴메일주소
	/**
	 * 헤더정보를 입력한다.(발송자 이름, 발송자 이메일, 회신메일주소)
	 * @version 1.0
	 * @author ymkim
	 * @param fromName 발송자 이름
	 * @param fromEmail 발송자 이메일
	 * @param replyEmail 회신 이메일
	 */
	public void setSenderInfo(String fromName, String fromEmail, String replyEmail)
	{
		//디폴트일경우는 컨덴츠타입과 인코딩타입을 디폴트로 설정한다.
		setSenderInfo(fromName, fromEmail, replyEmail, DEFAULT_CONTENT_TYPE, DEFAULT_CHARSET,DEFAULT_CONT_ENC_TYPE,DEFAULT_HEAD_ENC_TYPE);
	}

	/**
	 * 헤더 정보를 입력한다.(발송자 이름, 발송자 이메일, 회신메일주소, 컨덴츠 타입)
	 * @version 1.0
	 * @author ymkim
	 * @param fromName 발송자 이름
	 * @param fromEmail 발송자 이메일
	 * @param replyEmail 회신 이메일
	 * @param cType 컨덴츠 타입
	 */
	public void setSenderInfo(String fromName, String fromEmail, String replyEmail, String charset)
	{
		setSenderInfo(fromName, fromEmail, replyEmail, DEFAULT_CONTENT_TYPE, charset,DEFAULT_CONT_ENC_TYPE,DEFAULT_HEAD_ENC_TYPE);
	}


	/**
	 * 헤더 정보를 입력한다.(발송자 이름, 발송자 이메일, 회신메일주소, 컨덴츠 타입,Character Set)
	 * @version 1.0
	 * @author ymkim
	 * @param fromName 발송자 이름
	 * @param fromEmail 발송자 이메일
	 * @param replyEmail 회신 이메일
	 * @param cType 컨덴츠 타입
	 * @param charSet Character Set
	 */
	public void setSenderInfo(String fromName, String fromEmail, String replyEmail, String cType,String charSet)
	{
		setSenderInfo(fromName, fromEmail, replyEmail, cType, charSet,DEFAULT_CONT_ENC_TYPE,DEFAULT_HEAD_ENC_TYPE);
	}


	/**
	 * 헤더 정보를 입력한다.(발송자 이름, 발송자 이메일, 회신메일주소, 컨덴츠 타입,Character Set, 본문 인코딩 타입)
	 * @version 1.0
	 * @author ymkim
	 * @param fromName 발송자 이름
	 * @param fromEmail 발송자 이메일
	 * @param replyEmail 회신 이메일
	 * @param cType 컨덴츠 타입
	 * @param charSet Character Set
	 * @param contEncodeType 본문 인코딩 타입
	 */
	public void setSenderInfo(String fromName, String fromEmail, String replyEmail, String cType,String charSet,String contEncodeType)
	{
		setSenderInfo(fromName, fromEmail, replyEmail, cType, charSet,contEncodeType,DEFAULT_HEAD_ENC_TYPE);
	}

	/**
	 * 헤더 정보를 입력한다.(발송자 이름, 발송자 이메일, 회신메일주소, 컨덴츠 타입,Character Set, 본문 인코딩 타입,헤더 인코딩 타입)
	 * @version 1.0
	 * @author ymkim
	 * @param fromName 발송자 이름
	 * @param fromEmail 발송자 이메일
	 * @param replyEmail 회신 이메일
	 * @param cType 컨덴츠 타입
	 * @param charSet Character Set
	 * @param contEncodeType 본문 인코딩 타입
	 * @param headEncodeType 헤더 인코딩 타입
	 */
	public void setSenderInfo(String fromName, String fromEmail, String replyEmail, String cType,String charSet,String contEncodeType,String headEncodeType)
	{
		this.fromName =fromName;
		this.fromEmail =fromEmail;
		this.replyEmail =replyEmail;
		this.cType =cType;
		this.charSet =charSet;
		this.contEncodeType =contEncodeType;
		this.headEncodeType =headEncodeType;
	}


	/**
	 * 환경설정파일에서 디폴트 케릭터타입, 디폴트 캐릭터셋, 디폴트 컨덴츠 인코딩타입, 디폴트 헤더 인코딩 타입등을 가져온다.
	 * @version 1.0
	 * @author ymkim
	 */
	public void getMimeDefaultValue()
	{
		Config cfg = Config.getInstance();
		cfg.loadConfig(Config.MAIN_CFG);

		DEFAULT_CONTENT_TYPE = cfg.getDefaultContentType();
		DEFAULT_CHARSET = cfg.getDefaultCharset();
		DEFAULT_CONT_ENC_TYPE = cfg.getDefaultContEncType();
		DEFAULT_HEAD_ENC_TYPE = cfg.getDefaultHeadEncType();
		DEFAULT_BOUNDARY = cfg.getDefaultBoundary();
		persoanl_yn = cfg.getPersonal_yn();
		persoanl_pass = cfg.getPersonal_pass();
	}

	/**
	 * 디폴트 컨덴츠 타입을 얻는다.
	 * @version 1.0
	 * @author ymkim
	 * @return String 디폴트 컨덴츠 타입
	 */
	public static String getContentType()
	{
		return DEFAULT_CONTENT_TYPE;
	}

	/**
	 * 디폴트 Character Set을 얻는다.
	 * @version 1.0
	 * @author ymkim
	 * @return String 디폴트 Character Set
	 */
	public static String getCharSet()
	{
		return DEFAULT_CHARSET;
	}

	/**
	 * 디폴트 본문 인코딩 타입을 얻는다.
	 * @version 1.0
	 * @author ymkim
	 * @return String 디폴트 본문 인코딩 타입
	 */
	public static String getContEncType()
	{
		return DEFAULT_CONT_ENC_TYPE;
	}

	/**
	 * 디폴트 Boundary을 얻는다.
	 * @version 1.0
	 * @author ymkim
	 * @return String 디폴트 Boundary
	 */
	public static String getBoundary()
	{
		return DEFAULT_BOUNDARY;
	}


	/**
	 * From,과 To를 구성할때 인코딩타입등을 고려해서 생성해준다.
	 * @version 1.0
	 * @author ymkim
	 * @param name 인코딩할 이름
	 * @param email 인코딩할 이메일
	 * @return String 인코딩한 문자열
	 */
	public String addressToEnc(String name, String email)
	{
		StringBuffer tempSb = new StringBuffer();
		String encodeStr = "";
		try
		{
			if( (headEncodeType.toUpperCase()).equals("B") ||
			   (headEncodeType.toUpperCase()).equals("Q") )
			{
				if( name != null ) {
					encodeStr = (tempSb.append(transferEnc(name,charSet,headEncodeType)).append(' ')
								 .append('<').append(email).append('>')).toString();
				}
				else {
					encodeStr = (tempSb.append('<').append(email).append('>')).toString();
				}
			}
			else
			{
				if( name != null ) {
					encodeStr = (tempSb.append('\"').append(name).append('\"').append(' ')
								 .append('<').append(email).append('>')).toString();
				}
				else {
					encodeStr = (tempSb.append('<').append(email).append('>')).toString();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return encodeStr;
	}


	/**
	 * 제목을 인코딩해준다.
	 * @version 1.0
	 * @author ymkim
	 * @param subject 인코딩할 제목
	 * @return String 인코딩한 제목
	 */
	public String subjectToEnc(String subject)
	{
		if( subject != null ) {
			return transferEnc(subject, charSet, headEncodeType);
		}
		else {
			return "";
		}
	}

	/**
	 * 일반 문자열을 인코딩해준다.
	 * @version 1.0
	 * @author ymkim
	 * @param text 인코딩할 문자열
	 * @param charSet Character Set
	 * @param encType 인코딩 타입
	 * @return String 인코딩한 문자열
	 */
	public String transferEnc(String text, String charSet, String encType)
	{
		String tran = "";
		try
		{
			if( encType.equals("Q") || encType.equals("B") ) {
				tran = MimeUtility.encodeText(text, charSet, encType);
			}
			else {
				tran = text;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return tran;
	}

	/**
	 * 헤더 전체를 구성한다.
	 * @version 1.0
	 * @author ymkim
	 * @param subejct 제목
	 * @param rName 수신자이름
	 * @param rEmail 수신자메일
	 * @param isAttach 첨부파일 유무
	 * @param isHtml Html 유무
	 * @return String 완성된 헤더 문자열
	 */
        public String generateHeader(
            String subject,
            String rName,
            String rEmail,
            boolean isAttach,
            boolean isHtml,
            Hashtable hash,
            String ctnPos,
            String secu_att_yn,
            String secu_att_typ,
            String title_chk_yn, 
            String body_chk_yn, 
            String attach_file_chk_yn, 
            String secu_mail_chk_yn
        		)
        {
		String new_subject = getTransferMerge(subject,hash);

		//---------------------------------------------------------------------------
		//제목 개인정보 체크 start
		//---------------------------------------------------------------------------
		
		//config파일 사용
		//if("Y".equals(persoanl_yn)) {
		
		if("Y".equals(title_chk_yn)) {
			//예외처리 번호들 
			String[] passedNumbers = persoanl_pass.split(",");  // TScheduler.conf에 PERSONAL_PASS 값을 가져옴
	         
	        List<String> passedList = new ArrayList<>();
	        for (int i = 0; i < passedNumbers.length; i++) {
	        	passedList.add(passedNumbers[i]);
	        	//LOGGER.info("phons : "+passedNumbers[i]);
	        }
			
	        //휴대폰번호 체크
			boolean CellCK = false;
	        //JFilterUtil jFilterUtil = new JFilterUtil(srcFile);
			CellCK = ParttenCheckUtil.hasCellPhoneNumber(passedList, new_subject);
			//LOGGER.info("제목 CellCK : " + CellCK);
	       
			//전화번호 체크		
			boolean TellCK = false;
			TellCK = ParttenCheckUtil.hasTelePhoneNumber(passedList, new_subject);
			//LOGGER.info("제목 TellCK : " + TellCK);
			
	        //주민번호 체크
			boolean PersonalCK = false;
			PersonalCK = ParttenCheckUtil.hasPersonalId(new_subject);
			//LOGGER.info("제목 PersonalCK : " + PersonalCK);
			
		    //주민번호 체크
			boolean EmailCK = false;
			EmailCK = ParttenCheckUtil.hasEmail(passedList, new_subject);
			//LOGGER.info("제목 EmailCK : " + EmailCK);
			
			if((CellCK) || (TellCK) || (PersonalCK) || (EmailCK)) {
				return null;
			}else {
				//텍스트라도 이제는 html로 바꾸어준다.
				cType = "text/html";
				//if(isHtml)//html파일이면
				//{
				//	cType = "text/html";
				//}else//
				//{
				//	cType = "text/plain";
				//}

				StringBuffer tempSb = new StringBuffer();
				//
				String mailDate = LogDateGenerator.getMailFormatTime();

				//From구성
				tempSb.append(FROM).append(COLON).append(addressToEnc(fromName, fromEmail)).append(NEW_LINE);
				//To구성
				tempSb.append(TO).append(COLON).append(addressToEnc(rName,rEmail)).append(NEW_LINE);
				//Subject구성
				tempSb.append(SUBJECT).append(COLON).append(subjectToEnc(new_subject)).append(NEW_LINE);
				//Date구성
				tempSb.append(DATE).append(COLON).append(mailDate).append(NEW_LINE);
				//Mime구성
				tempSb.append(MIME_VERSION).append(COLON).append(DEFAULT_MIME_VERSION).append(NEW_LINE);
				//content_type구성(첨부파일이 있으면 multipart/mixed로 처리한다.)
				
				//보안 HTML
				if( isAttach ||  "Y".equals(secu_att_yn) && "HTML".equals(secu_att_typ))//첨부파일 또는 URL+보안메일이 있으면
				{
					tempSb.append(CONTENT_TYPE).append(COLON).append(CONTENT_TYPE_MIXED).append(';').append(NEW_LINE)
							.append(CONTINUE_CHAR).append(BOUNDARY).append("=\"").append(DEFAULT_BOUNDARY).append('\"').append(NEW_LINE);

				//보안 PDF
				}else if( isAttach ||  "Y".equals(secu_att_yn) && "PDF".equals(secu_att_typ))
				{
					tempSb.append(CONTENT_TYPE).append(COLON).append(CONTENT_TYPE_MIXED).append(';').append(NEW_LINE)
							.append(CONTINUE_CHAR).append(BOUNDARY).append("=\"").append(DEFAULT_BOUNDARY).append('\"').append(NEW_LINE);

				//보안 EXCEL	
				}else if( isAttach ||  "Y".equals(secu_att_yn) && "EXCEL".equals(secu_att_typ))
				{
					tempSb.append(CONTENT_TYPE).append(COLON).append(CONTENT_TYPE_MIXED).append(';').append(NEW_LINE)
							.append(CONTINUE_CHAR).append(BOUNDARY).append("=\"").append(DEFAULT_BOUNDARY).append('\"').append(NEW_LINE);
				
				//첨부파일이 없으면
				}else 
				{
					tempSb.append(CONTENT_TYPE).append(COLON).append(cType).append(';').append(NEW_LINE)
							.append(CONTINUE_CHAR).append(CHRACTER_SET).append("=\"").append(charSet).append('\"').append(NEW_LINE);

					//content_transfer-encoding타입(멀티파트타입인경우에는 헤더에서는 빠진다)
					tempSb.append(CONTENT_TRANSFER_ENCODING).append(COLON).append(contEncodeType).append(NEW_LINE);
				}

				//마지막으로 한줄을 띄워준다.
				tempSb.append(NEW_LINE);
				return tempSb.toString();
			}
			
			//---------------------------------------------------------------------------
			
		}else {

			//텍스트라도 이제는 html로 바꾸어준다.
			cType = "text/html";
			//if(isHtml)//html파일이면
			//{
			//	cType = "text/html";
			//}else//
			//{
			//	cType = "text/plain";
			//}

			StringBuffer tempSb = new StringBuffer();
			//
			String mailDate = LogDateGenerator.getMailFormatTime();

			//From구성
			tempSb.append(FROM).append(COLON).append(addressToEnc(fromName, fromEmail)).append(NEW_LINE);
			//To구성
			tempSb.append(TO).append(COLON).append(addressToEnc(rName,rEmail)).append(NEW_LINE);
			//Subject구성
			tempSb.append(SUBJECT).append(COLON).append(subjectToEnc(new_subject)).append(NEW_LINE);
			//Date구성
			tempSb.append(DATE).append(COLON).append(mailDate).append(NEW_LINE);
			//Mime구성
			tempSb.append(MIME_VERSION).append(COLON).append(DEFAULT_MIME_VERSION).append(NEW_LINE);
			//content_type구성(첨부파일이 있으면 multipart/mixed로 처리한다.)

			//보안 HTML
			if( isAttach || "Y".equals(secu_att_yn) && "HTML".equals(secu_att_typ))//첨부파일 또는 URL+보안메일이 있으면
			{
				tempSb.append(CONTENT_TYPE).append(COLON).append(CONTENT_TYPE_MIXED).append(';').append(NEW_LINE)
						.append(CONTINUE_CHAR).append(BOUNDARY).append("=\"").append(DEFAULT_BOUNDARY).append('\"').append(NEW_LINE);

			//보안 PDF	
			}else if( isAttach || "Y".equals(secu_att_yn) && "PDF".equals(secu_att_typ))
			{
				tempSb.append(CONTENT_TYPE).append(COLON).append(CONTENT_TYPE_MIXED).append(';').append(NEW_LINE)
						.append(CONTINUE_CHAR).append(BOUNDARY).append("=\"").append(DEFAULT_BOUNDARY).append('\"').append(NEW_LINE);

			//보안 PDF	
			}else if( isAttach || "Y".equals(secu_att_yn) && "EXCEL".equals(secu_att_typ))
			{
				tempSb.append(CONTENT_TYPE).append(COLON).append(CONTENT_TYPE_MIXED).append(';').append(NEW_LINE)
						.append(CONTINUE_CHAR).append(BOUNDARY).append("=\"").append(DEFAULT_BOUNDARY).append('\"').append(NEW_LINE);

			// 첨부파일이 없으면
			}else 
			{
				tempSb.append(CONTENT_TYPE).append(COLON).append(cType).append(';').append(NEW_LINE)
						.append(CONTINUE_CHAR).append(CHRACTER_SET).append("=\"").append(charSet).append('\"').append(NEW_LINE);

				//content_transfer-encoding타입(멀티파트타입인경우에는 헤더에서는 빠진다)
				tempSb.append(CONTENT_TRANSFER_ENCODING).append(COLON).append(contEncodeType).append(NEW_LINE);
			}

			//마지막으로 한줄을 띄워준다.
			tempSb.append(NEW_LINE);
			return tempSb.toString();
		}


	
		
		
	}

	/**
	 * 머지메일일 경우 머지구분자를 해당 값으로 바꿔준다.
	 * @version 1.0
	 * @author ymkim
	 * @param tmpContent 본문내용
	 * @param mergyHash 머지 리스트
	 * @return String 본문에 머지리스트를 적용한 내용
	 */
	private String getTransferMerge(String tmpContent,Hashtable mergyHash)
	{
		tmpContent = MergeTrans.replaceMerge(tmpContent,mergyHash);
		return tmpContent;
	}
}