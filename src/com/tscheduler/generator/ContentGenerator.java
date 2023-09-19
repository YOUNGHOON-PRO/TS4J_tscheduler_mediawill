package com.tscheduler.generator;

/*
 * 설명: 컨덴츠를 생성한다.
 *	헤더를 가져와서 본문 내용과 합쳐서 하나의 이메일 내용을 구성한다.
 */

import java.io.*;
import java.util.ArrayList;
import java.net.*;
import java.util.Hashtable;
import java.util.List;
import java.util.Enumeration;
import java.util.Arrays;

import javax.commerce.util.BASE64Encoder;
import javax.activation.*;
import javax.mail.internet.*;

import com.tscheduler.util.Config;
import com.tscheduler.util.DebugTrace;
import com.tscheduler.util.MergeTrans;
import com.tscheduler.util.ErrorLogGenerator;
import com.tscheduler.util.ReserveStatusCode;
import com.yettiesoft.javarose.SGException;
import com.yettiesoft.vestmail.VMCipherImpl;

import synap.next.JFilterUtil;
import synap.next.ParttenCheckUtil;

import com.tscheduler.manager.AttachFileManager;
import com.tscheduler.manager.LogFileManager;
import com.enders.excelconverter.HtmlToExcelConverter;
import com.enders.synctmp.*;
import com.pdf.convert.HtmlToPdf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 이메일 본문내용을 생성하는 클래스
 * @version 1.0
 * @author ymkim
 */
public class ContentGenerator {
	
  private static final Logger LOGGER = LogManager.getLogger(ContentGenerator.class.getName());
	
  /**머지리스트의 구분자*/
  private static final String MERGY_DELIMITER = "%";

  /**ContentGenerator의 Singleton객체*/
  private static ContentGenerator instance;
  
  private static String persoanl_pass="";
  private static String persoanl_yn ="";

  /**Html인지 아닌지 확인*/
  private boolean isHtml = true;
  ;

  /**
   * ContentGenerator의 singleton객체를 얻는다.
   * @version 1.0
   * @author ymkim
   * @return ContentGenerator ContentGenerator 객체를 얻는다.
   */
  public static ContentGenerator getInstance() {
    if (instance == null) {
      instance = new ContentGenerator();
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
  private ContentGenerator() {
	  
	  Config cfg = Config.getInstance();
	  persoanl_yn = cfg.getPersonal_yn();
	  persoanl_pass = cfg.getPersonal_pass();

	    
  }

  /**
   * 본문의 위치에 따라 그곳에 있는 이메일의 본문이 될 내용을 가져온다.(다국어)
   * @version 1.0
   * @author ymkim
   * @param mID 예약메일ID
   * @param pos 컨덴츠의 위치
   * @param content 컨덴츠 위치에 따른 내용(파일 path, url)
   * @return String 컨덴츠의 내용을 리턴한다.
   */
  public String getContent(String mID, String pos, String content, String charset) {
    String tmpContent = null;
    if (pos.equals("0") || (pos.toUpperCase()).equals("D")) { //컨덴츠의 내용이 디비에 있을때
      try{
        //tmpContent = new String(content.getBytes(charset));
        tmpContent = content;
      }catch(Exception e){
        e.printStackTrace();
      }
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
    }
    else if (pos.equals("1") || (pos.toUpperCase()).equals("W")) { //컨덴츠의 내용이 웹에 있을때
      tmpContent = getContentFromURL(mID, content, charset);
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
    }
    else if (pos.equals("2") || (pos.toUpperCase()).equals("F")) { //컨덴츠의 내용이 파일로 있을때
    	
		//##########################################################
		// 파일 동기화 작업	
		// content 경로의 파일을 원격지의 content와 동일여부 체크로 파일 동기화 
		//##########################################################
		//-----------------------------------------------------------
//		File f = new File(content);
//		SyncTemplete syncTemplete =  new SyncTemplete();
//		syncTemplete.sync(f.getName());
		//------------------------------------------------------------
        
      tmpContent = getContentFromFile(mID, content, charset);
      //중요한것!!!!!!!!!!!!!!!
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 2);
    }
    else {
      tmpContent = content;
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
    }

    if (tmpContent != null && (tmpContent.trim().equals("") || tmpContent.indexOf("<!--NODATA-->") != -1) ){
      return null;
    }

    return tmpContent;
  }

  /**
   * 본문의 위치에 따라 그곳에 있는 이메일의 본문이 될 내용을 가져온다.
   * @version 1.0
   * @author ymkim
   * @param mID 예약메일ID
   * @param pos 컨덴츠의 위치
   * @param content 컨덴츠 위치에 따른 내용(파일 path, url)
   * @return String 컨덴츠의 내용을 리턴한다.
   */
  public String getContent(String mID, String pos, String content)
  {
    String tmpContent = null;
    if (pos.equals("0") || (pos.toUpperCase()).equals("D")) { //컨덴츠의 내용이 디비에 있을때
      try{
        tmpContent = content;
      }catch(Exception e){
        e.printStackTrace();
      }
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
    }
    else if (pos.equals("1") || (pos.toUpperCase()).equals("W")) { //컨덴츠의 내용이 웹에 있을때
      tmpContent = getContentFromURL(mID, content);
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
    }
    else if (pos.equals("2") || (pos.toUpperCase()).equals("F")) { //컨덴츠의 내용이 파일로 있을때
      
	 //##########################################################
	 // 파일 동기화 작업	
	 // content 경로의 파일을 원격지의 content와 동일여부 체크로 파일 동기화 
	 //##########################################################
	 //-----------------------------------------------------------
//	 File f = new File(content);
//	 SyncTemplete syncTemplete =  new SyncTemplete();
//	 syncTemplete.sync(f.getName());
	 //------------------------------------------------------------
    	
      tmpContent = getContentFromFile(mID, content);
      //중요한것!!!!!!!!!!!!!!!
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 2);
    }
    else {
      tmpContent = content;
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
    }

    if (tmpContent != null && (tmpContent.trim().equals("") || tmpContent.indexOf("<!--NODATA-->") != -1) ){
      return null;
    }

    return tmpContent;
  }

  
  /**
   * 본문의 위치에 따라 그곳에 있는 이메일의 본문이 될 내용을 가져온다.
   * @version 1.0
   * @author ymkim
   * @param mID 예약메일ID
   * @param pos 컨덴츠의 위치
   * @param content 컨덴츠 위치에 따른 내용(파일 path, url)
   * @return String 컨덴츠의 내용을 리턴한다.
   */
  public String getContent_vest(String mID, String pos, String content)
  {
    String tmpContent = null;
// if (pos.equals("1") || (pos.toUpperCase()).equals("W")) { //컨덴츠의 내용이 웹에 있을때
      tmpContent = getContentFromURL(mID, content);
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
  //  }
    if (tmpContent != null && (tmpContent.trim().equals("") || tmpContent.indexOf("<!--NODATA-->") != -1) ){
      return null;
    }
    return tmpContent;
  }
  
  
  /**
   * 컨텐츠를 불러온다.(다국어)
   * @param mID
   * @param pos
   * @param content
   * @param mergeHash
   * @param charset
   * @return
   */
  public String getContent2(String mID, String pos, String content,
                            Hashtable mergeHash, String charset, String content_type) {
    String tmpContent;
    
    content = getTransferMerge(content, mergeHash);
    
    if (pos.equals("0") || (pos.toUpperCase()).equals("D")) { //컨덴츠의 내용이 디비에 있을때
    	if( content_type.equals("CONTENT_VEST") ) {
    		content = getContentFromURL(mID, content);
    	}
      tmpContent = getTransferMerge(content, mergeHash);
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
    }
    else if (pos.equals("1") || (pos.toUpperCase()).equals("W")) { //컨덴츠의 내용이 웹에 있을때
      String tmp = getTransferMerge(content, mergeHash);
      tmpContent = getContentFromURL(mID, tmp);
      tmpContent = getTransferMerge(tmpContent, mergeHash);

      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
    }
    else if (pos.equals("2") || (pos.toUpperCase()).equals("F")) { //컨덴츠의 내용이 파일로 있을때
    	
   	 //##########################################################
   	 // 파일 동기화 작업	
   	 // content 경로의 파일을 원격지의 content와 동일여부 체크로 파일 동기화 
   	 //##########################################################
   	 //-----------------------------------------------------------
//   	 File f = new File(content);
//   	 SyncTemplete syncTemplete =  new SyncTemplete();
//   	 syncTemplete.sync(f.getName());
   	 //------------------------------------------------------------
    	
      tmpContent = getContentFromFile(mID, content, charset);
      tmpContent = getTransferMerge(tmpContent, mergeHash);

      //중요한것!!!!!!!!!!!!!!!
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 2);
    }
    else {
      tmpContent = content;
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
    }

    if (tmpContent != null && (tmpContent.trim().equals("") || tmpContent.indexOf("<!--NODATA-->") != -1))
      return null;

    return tmpContent;
  }

  /**
   * 컨텐츠를 불러온다.
   * @param mID
   * @param pos
   * @param content
   * @param mergeHash
   * @param charset
   * @return
   */
  public String getContent2(String mID, String pos, String content,
                            Hashtable mergeHash) {
    String tmpContent;
    if (pos.equals("0") || (pos.toUpperCase()).equals("D")) { //컨덴츠의 내용이 디비에 있을때
      tmpContent = getTransferMerge(content, mergeHash);
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
    }
    else if (pos.equals("1") || (pos.toUpperCase()).equals("W")) { //컨덴츠의 내용이 웹에 있을때
      String tmp = getTransferMerge(content, mergeHash); 	// URL에 머지 치환
      tmpContent = getContentFromURL(mID, tmp);  			// URL에서 html가져오기
      tmpContent = getTransferMerge(tmpContent, mergeHash); // URL에서 가져온 html 머지 치환

      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
    }
    else if (pos.equals("2") || (pos.toUpperCase()).equals("F")) { //컨덴츠의 내용이 파일로 있을때
    	
   	 //##########################################################
   	 // 파일 동기화 작업	
   	 // content 경로의 파일을 원격지의 content와 동일여부 체크로 파일 동기화 
   	 //##########################################################
   	 //-----------------------------------------------------------
//   	 File f = new File(content);
//   	 SyncTemplete syncTemplete =  new SyncTemplete();
//   	 syncTemplete.sync(f.getName());
   	 //------------------------------------------------------------
    	
      tmpContent = getContentFromFile(mID, content);
      tmpContent = getTransferMerge(tmpContent, mergeHash);

      //중요한것!!!!!!!!!!!!!!!
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 2);
    }
    else {
      tmpContent = content;
      //내용이 html인지 아닌지 체크한다.
      isHtml = checkHtml(tmpContent, content, 1);
    }

    if (tmpContent != null && (tmpContent.trim().equals("") || tmpContent.indexOf("<!--NODATA-->") != -1))
      return null;

    return tmpContent;
  }


  /**
   * 내용이 Html인지 Txt인지 리턴한다.
   * @version 1.0
   * @author ymkim
   * @return boolean true - html이다 false - html이 아니다.
   */
  public boolean getIsHtml() {
    return isHtml;
  }

  /**
   * 본문의 내용이 html인지 아닌지 체크한다. ctnPos이 1이면 디비나 혹은 웹이며 ctnPos이 2이면 파일 경로 이다.
   * @version 1.0
   * @author ymkim
   * @param tmpContent 본문내용
   * @param fileName 본문이 되는 파일명
   * @param ctnPos - 1이면 디비나 혹은 웹, ctnPos - 2이면 파일 경로
   * @return boolean true - 본문이 html이다 false - 본문이 html이 아니다.
   */
  public boolean checkHtml(String tmpContent, String fileName, int ctnPos) {
    if (tmpContent == null) {
      return false;
    }

    if (ctnPos == 1) {
      if ( (tmpContent.toUpperCase()).indexOf("<HTML") == -1) {
        return false; //아무것도 찾은것이 없다
      }
      else {
        return true;
      }
    }
    else {
      if ( (fileName.toUpperCase()).endsWith("HTML") ||
          (fileName.toUpperCase()).endsWith("HTM")) {
        return true;
      }
      else {
        return false;
      }
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
  String getTransferMerge(String tmpContent, Hashtable mergyHash) {
    return MergeTrans.replaceMerge(tmpContent, mergyHash);
  }

  /**
   * 본문에 인코딩을 해준다.
   * @version 1.0
   * @author ymkim
   * @param tmpContent 본문내용
   * @param encType 인코딩 타입
   * @return String 본문에 인코딩을 적용한 내용
   */
  private String getTransferEncoding(String tmpContent, String encType) {
    //인코딩을 해준다. 만일에 8bit나 혹은 7bit이면 컨덴츠 내용 그대로 들어가구
    //Base64로 인코딩 해준다.
    if ( (encType.toUpperCase()).equals("BASE64")) {
      try {
        BASE64Encoder encoder = new BASE64Encoder();
        tmpContent = encoder.encode(tmpContent.getBytes());
        //return MimeUtility.encodeText(content,"ks_c_5601-1987","B");
        return tmpContent;
      }
      catch (Exception e) {
        e.printStackTrace();
        return "";
      }
    }
    else if ( (encType.toUpperCase()).equals("QUOTED-PRINTABLE")) {
      //!!!!!! 아직 본문 내용에 대해서 Quoted-printable은 지원대상에서 빼기로 하자...ㅡ.ㅡ;;
      try {
        return MimeUtility.encodeText(tmpContent, "ks_c_5601-1987", "Q");
      }
      catch (Exception e) {
        e.printStackTrace();
        return "";
      }
    }
    else { //Base64나 Quoted-printable가 아니면 그냥 그 내용 그대로 넘겨준다.
      return tmpContent;
    }
  }

  /**
   * URL에서 본문내용을 가져온다(다국어)
   * @version 1.0
   * @author ymkim
   * @param mID 예약메일ID
   * @param urlString URL
   * @return String 본문 내용
   */
  private String getContentFromURL(String mID, String urlString, String charset) {
    StringBuffer sb = new StringBuffer();
    BufferedReader br = null;
    String tempUrl = urlString;
    if (! ( (urlString.toUpperCase()).startsWith("HTTP://"))) {
      tempUrl = "http://" + urlString;
    }
    String return_value = null;

    try {
      URL url = new URL(tempUrl);
      URLConnection conn = url.openConnection();
      HttpURLConnection httpConn = (HttpURLConnection) conn;
      int responseCode = httpConn.getResponseCode();

      if (responseCode == HttpURLConnection.HTTP_OK) {
        InputStream in = httpConn.getInputStream();
        String encoding = httpConn.getContentEncoding();
        br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        for (String temp; (temp = br.readLine()) != null; ) {
          sb.append(temp).append(Header.NEW_LINE);
        }
      }else{
        throw new Exception("CONTENTS_FAIL FROM URL");
      }
      return_value = new String(sb.toString().getBytes(charset));
    }
    catch (Exception e) {
//      e.printStackTrace();
      LogFileManager.runLogWriter("getLegacyDBInfo", e.toString());

      //에러 로그를 남겨준다.
      ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                          ReserveStatusCode.CONTENTS_FAIL_TYPE,
                                          ReserveStatusCode.
                                          CONTENT_NOT_FOUND_COMMENT, mID);
      return_value = null;
    }
    finally {
      sb = null;
      try {
        if (br != null) {
          br.close();
          br = null;
        }
      }
      catch (IOException e) {
      }
    }
    return return_value;
  }

  /**
   * URL에서 본문내용을 가져온다
   * @version 1.0
   * @author ymkim
   * @param mID 예약메일ID
   * @param urlString URL
   * @return String 본문 내용
   */
  private String getContentFromURL(String mID, String urlString) {
    StringBuffer sb = new StringBuffer();
    BufferedReader br = null;
    String tempUrl = urlString;
    if (! ( (urlString.toUpperCase()).startsWith("HTTP://"))) {
      tempUrl = "http://" + urlString;
    }
    String return_value = null;

    try {
    	//한글 깨짐 문제가 있어 조치
     	//URLEncoder 시 특수문자까지 인코딩 되는 문제가 있어 해당 문구들은 replace 조치  (= , :, / , ? , &)
    	tempUrl = URLEncoder.encode(tempUrl,"UTF-8").replace("%3D", "=").replace("%3A", ":").replace("%2F", "/").replace("%3F", "?").replace("%26", "&");

    	URL url = new URL(tempUrl);
      
      URLConnection conn = url.openConnection();
      HttpURLConnection httpConn = (HttpURLConnection) conn;
      int responseCode = httpConn.getResponseCode();

      if (responseCode == HttpURLConnection.HTTP_OK) {
        InputStream in = httpConn.getInputStream();
        String encoding = httpConn.getContentEncoding();

        br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        //br = new BufferedReader(new InputStreamReader(in));

        for (String temp; (temp = br.readLine()) != null; ) {
          sb.append(temp).append(Header.NEW_LINE);
        }
      }else{
        throw new Exception("CONTENTS_FAIL FROM URL");
      }

      return_value = sb.toString();
    }
    catch (Exception e) {
      e.printStackTrace();
      LogFileManager.runLogWriter("getLegacyDBInfo", e.toString());

      //에러 로그를 남겨준다.
      ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                          ReserveStatusCode.CONTENTS_FAIL_TYPE,
                                          ReserveStatusCode.
                                          CONTENT_NOT_FOUND_COMMENT, mID);
      return_value = null;
    }
    finally {
      sb = null;
      try {
        if (br != null) {
          br.close();
          br = null;
        }
      }
      catch (IOException e) {
      }
    }
    return return_value;
  }


  /**
   * 파일에서 본문내용을 가져온다.(다국어)
   * @version 2.0
   * @author jinwoo
   * @param mID 예약메일ID
   * @param path 파일 경로
   * @return String 본문 내용
   */
  private String getContentFromFile(String mID, String path, String charset) {
    StringBuffer sb = new StringBuffer();
//    BufferedReader br = null;
    FileInputStream fis = null;
    String return_value = null;
    byte[] data = new byte[1024];

    try {
//      br = new BufferedReader(new FileReader(new File(path)));
//      String readContent;
//      while ( (readContent = br.readLine()) != null) {

      fis = new FileInputStream(new File(path));
      int cnt = -1;
      while ( (cnt = fis.read(data)) != -1) {
        sb.append(new String(data));
        Arrays.fill(data, (byte) 0);
      }

      return_value = sb.toString();
    }
    catch (Exception e) {
      e.printStackTrace();
      LogFileManager.runLogWriter("getLegacyDBInfo", e.toString());

      //에러 로그를 남겨준다.
      ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                          ReserveStatusCode.CONTENTS_FAIL_TYPE,
                                          ReserveStatusCode.
                                          CONTENT_NOT_FOUND_COMMENT, mID);

      return_value = null;
    }
    finally {
      sb = null;
      /*
            try {
              if (br != null) {
                br.close();
              }
            }
            catch (IOException e) {
            }
       */
      try {
        if (fis != null) {
          fis.close();
        }
      }
      catch (IOException e) {
      }
    }

      return return_value;
    }

    /**
     * 파일에서 본문내용을 가져온다.
     * @version 2.0
     * @author jinwoo
     * @param mID 예약메일ID
     * @param path 파일 경로
     * @return String 본문 내용
     */
    private String getContentFromFile(String mID, String path) {
    StringBuffer sb = new StringBuffer();
//    BufferedReader br = null;
    FileInputStream fis = null;
    String return_value = null;
    byte[] data = new byte[1024];

    try {
//      br = new BufferedReader(new FileReader(new File(path)));
//      String readContent;
//      while ( (readContent = br.readLine()) != null) {

      fis = new FileInputStream(new File(path));
      int cnt = -1;
      while ( (cnt = fis.read(data)) != -1) {
        sb.append(new String(data));
        Arrays.fill(data, (byte) 0);
      }

      return_value = sb.toString();
    }
    catch (Exception e) {
      e.printStackTrace();
      LogFileManager.runLogWriter("getLegacyDBInfo", e.toString());

      //에러 로그를 남겨준다.
      ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                          ReserveStatusCode.CONTENTS_FAIL_TYPE,
                                          ReserveStatusCode.
                                          CONTENT_NOT_FOUND_COMMENT, mID);

      return_value = null;
    }
    finally {
      sb = null;
      /*
            try {
              if (br != null) {
                br.close();
              }
            }
            catch (IOException e) {
            }
       */
      try {
        if (fis != null) {
          fis.close();
        }
      }
      catch (IOException e) {
      }
    }

      return return_value;
    }


    /**
     * 첨부파일을 얻어온후에 그것을 Base64로 인코딩한다
     * @param mid 예약메일ID
     * @param attachFileList attachFileList 첨부파일 경로 리스트
     * @return  boolean true - 인코딩 성공, false - 인코딩 실패
     */
    private boolean setAttachFile(String mid, String[] attachFileList) {
      FileInputStream fi = null;
      FileOutputStream fo = null;
      StringBuffer sb = null;

      boolean return_value = false;

      try {
        //일단은 만들어준다.
        int attachSize = attachFileList.length;
        for (int i = 0; i < attachSize; i++) {
          if (attachFileList[i] != null && !attachFileList[i].equals("")) {
            sb = new StringBuffer();
            String attachEncFolder = sb.append(Config.ROOT_DIR).append(File.
                separator)
                .append(AttachFileManager.ATTACH_ENC_FOLDER).append(File.
                separator).toString();    // .\TempStorage\
            sb = null;
            //일단 MID 폴더를 만들어준다.
            File f = new File(attachEncFolder + mid);  // .\TempStorage\189
            if (!f.isDirectory()) {
              f.mkdir();
            }

            BASE64Encoder encoder = new BASE64Encoder();
            //encoder.encode(new FileInputStream(new File("Test.zip")),new FileOutputStream(new File("Test.txt")));

            sb = new StringBuffer();
            fi = new FileInputStream(new File(attachFileList[i]));   //[c:/test.txt, , , , ]
            fo = new FileOutputStream(new File(sb.append(attachEncFolder).
                                               append(
                mid)
                                               .append(File.separator).append(
                "attach_")
                                               .append(i).append(".txt").
                                               toString()));
            sb = null;
            encoder.encode(fi, fo);
          }
        }

        return_value = true;
        //차후에는 얻어오기만한다.
      }
      catch (Exception e) {
        e.printStackTrace();

        //에러 로그를 남겨준다.
        ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                            ReserveStatusCode.
                                            CONTENTS_FAIL_TYPE,
                                            ReserveStatusCode.
                                            ATTACH_FILE_NOT_FOUND_COMMENT, mid);
        return_value = false;
      }
      finally {
        try {
          if (fi != null) {
            fi.close();
          }
        }
        catch (IOException e) {
        }

        try {
          if (fo != null) {
            fo.close();
          }
        }
        catch (IOException e) {
        }
      }
      return return_value;
    }

    /**
     * 인코딩된 첨부파일들을 전부 얻어온다.
     * @version 1.0
     * @author ymkim
     * @param mid 예약메일ID
     * @param attachFileList 첨부파일 경로 리스트
     * @return String 인코딩된 첨부파일의 내용들
     */
    public String getAttachFile(String mid, String[] attachFileList, String attach_file_chk_yn) {
      if (!setAttachFile(mid, attachFileList)) {    // attachFilelist : [c:/test.txt, , , , ]
        return " ";
      }

      BufferedReader br = null;
      String allEncAttahStr = "";
      String return_value = "";
      StringBuffer sb = null;

      try {
        //일단은 만들어준다.
        int attachSize = attachFileList.length;
        for (int i = 0; i < attachSize; i++) {
        	
        	//---------------------------------------------------------------------------
       		//첨부파일 개인정보 체크 start
       		//---------------------------------------------------------------------------

        	//config파일 사용
        	//if(attachFileList[i].length() != 0  && "Y".equals(persoanl_yn)) {
        	
        	if(attachFileList[i].length() != 0  && "Y".equals(attach_file_chk_yn)) {
        	
        		JFilterUtil jFilterUtil = new JFilterUtil(attachFileList[i]);
            	
            	String[] passedNumbers = persoanl_pass.split(",");  // TScheduler.conf에 PERSONAL_PASS 값을 가져옴
                
                List<String> passedList = new ArrayList<>();
                for (int z = 0; z < passedNumbers.length; z++) {
                	passedList.add(passedNumbers[z]);
                	//LOGGER.info("phons : "+passedNumbers[i]);
                }
        		
                //휴대폰번호 체크
        		boolean CellCK = false;
                //JFilterUtil jFilterUtil = new JFilterUtil(srcFile);
        		CellCK = jFilterUtil.hasCellPhoneNumber(passedList);
        		//LOGGER.info("첨부파일 CellCK : " + CellCK);
               
        		//전화번호 체크		
        		boolean TellCK = false;
        		TellCK = jFilterUtil.hasTelePhoneNumber(passedList);
        		//LOGGER.info("첨부파일 TellCK : " + TellCK);
        		
                //주민번호 체크
        		boolean PersonalCK = false;
        		PersonalCK = jFilterUtil.hasPersonalId();
        		//LOGGER.info("첨부파일 PersonalCK : " + PersonalCK);
        		
        		//이메일 체크
        		boolean EmailCK = false;
        		EmailCK = jFilterUtil.hasEamil(passedList);
        		//LOGGER.info("첨부파일 EmailCK : " + EmailCK);
        		
    			if((CellCK) || (TellCK) || (PersonalCK) || (EmailCK)) {// 제목에 개인정보가 있으면 null 처리
    				allEncAttahStr =  "personal_error";
    				
    				break;
    			}
           		
        	}
        	//---------------------------------------------------------------------------
        	
          if (attachFileList[i] != null && !attachFileList[i].equals("")) {
            sb = new StringBuffer();
            String attachEncFolder = sb.append(Config.ROOT_DIR)
                .append(File.separator).append(AttachFileManager.
                                               ATTACH_ENC_FOLDER)
                .append(File.separator).toString();
            sb = null;

            sb = new StringBuffer();
            //일단 MID 폴더를 만들어준다.
            File rawFile = new File(attachFileList[i]);   // c:\test.txt
            File encFile = new File(sb.append(attachEncFolder).append(mid)
                                    .append(File.separator).append("attach_").
                                    append(i)
                                    .append(".txt").toString());    // .\TempStorage\189\attach_0.txt
            //f.mkdir();
            sb = null;

            br = new BufferedReader(new FileReader(encFile));
            String str = "";

            StringBuffer sb2 = new StringBuffer();
            //이러면 하나의 파일에서 내용을 읽어들이게 된다.
            while ( (str = br.readLine()) != null) {
              sb2.append(str).append("\n");
            }

            String fileName = rawFile.getName();
            //컨덴츠 헤더+첨부파일 내용을 연결시켜준다.
            String tempHeader = getAttachHeader(getContentTypeFromFile(rawFile),
                                                HeaderGenerator.getCharSet(),
                                                "base64",
                                                HeaderGenerator.getBoundary(),
                                                fileName);
            sb = new StringBuffer();
            allEncAttahStr = sb.append(allEncAttahStr).append(tempHeader).
                append(
                sb2.toString())
                .append(Header.NEW_LINE).toString();
            sb = null;
            sb2 = null;
          }
        }

        return_value = allEncAttahStr;
        //차후에는 얻어오기만한다.
      }
      catch (Exception e) {
        e.printStackTrace();
        //첨부파일이 없을때에 에러로 처리하고 싶으면 return_value를 널로 넘긴다.
        //첨부파일이 없더라도 그냥 메일을 보내고 싶으면 return_value를 " " 로 넘긴다.
        //return_value = " ";
        return_value = null;
      }
      finally {
        try {
          if (br != null) {
            br.close();
            br = null;
          }
        }
        catch (IOException e) {
        }
      }

      return return_value;
    }

    /**
     * 첨부파일의 Content-Type을 알아오기
     * @version 1.0
     * @author ymkim
     * @param attachFile 첨부파일
     * @return String 첨부파일의 Content-Type
     */
    public String getContentTypeFromFile(File attachFile) {
      MimetypesFileTypeMap fileType = new MimetypesFileTypeMap();
      return fileType.getContentType(attachFile);
    }

    /**
     * 첨부파일이 있을경우 본문내용쪽의 헤더 내용을 얻는다.
     * @version 1.0
     * @author ymkim
     * @param cType 컨덴츠 타입
     * @param charSet CharacterSet
     * @param encType 인코딩 타입
     * @param boundary Boundary
     * @param isHtml Html확인
     * @return String 본문의 헤더를 생성해준다.
     */
    public String getContentHeader(String cType, String charSet, String encType,
                                   String boundary, boolean isHtml) {
      //텍스트라도 이제는 html로 바꾸어준\uFFFD
      cType = "text/html";
      String retVal = "";
      
      StringBuffer sb = new StringBuffer();
      sb.append("--").append(boundary).append(Header.NEW_LINE)
          .append(Header.CONTENT_TYPE).append(Header.COLON).append(cType).
          append(
          Header.SEMICOLON).append(Header.NEW_LINE)
          .append(Header.CONTINUE_CHAR).append(Header.CHRACTER_SET).append(
          "=\"").
          append(charSet).append('\"').append(Header.NEW_LINE)
          .append(Header.CONTENT_TRANSFER_ENCODING).append(Header.COLON).append(
          encType).append(Header.NEW_LINE).append(Header.NEW_LINE);
      retVal = sb.toString();
      sb = null;
      return retVal;
    }

    /**
     * 첨부파일이 있을경우 첨부파일쪽의 헤더 내용을 얻는다.
     * @version 1.0
     * @author ymkim
     * @param cType 컨덴츠 타입
     * @param charSet CharacterSet
     * @param encType 인코딩 타입
     * @param boundary Boundary
     * @param fileName 화일 이름
     * @return String 첨부파일의 헤더를 생성해준다.
     */
    public String getAttachHeader(String cType, String charSet, String encType,
                                  String boundary, String fileName) {
      String returnVal = "";
      StringBuffer sb = new StringBuffer();
      sb.append("--").append(boundary).append(Header.NEW_LINE)
          .append(Header.CONTENT_TYPE).append(Header.COLON).append(cType).
          append(
          Header.SEMICOLON).append(Header.NEW_LINE)
          .append(Header.CONTINUE_CHAR).append(Header.NAME).append("=\"").
          append(
          fileName).append('\"').append(Header.NEW_LINE)
          .append(Header.CONTENT_TRANSFER_ENCODING).append(Header.COLON).append(
          encType).append(Header.NEW_LINE)
          .append(Header.CONTENT_DISPOSITION).append(Header.COLON).append(
          Header.
          ATTACHMENT).append(Header.SEMICOLON).append(Header.NEW_LINE)
          .append(Header.CONTINUE_CHAR).append(Header.FILENAME).append("=\"").
          append(fileName).append('\"').append(Header.NEW_LINE).append(Header.
          NEW_LINE);
      returnVal = sb.toString();
      sb = null;
      return returnVal;
    }

    /**
     * 머지메일이 아닌경우의 완전한 본문(본문내용+첨부파일)을 구성한다.
     * @version 1.0
     * @author ymkim
     * @param rawContent 본문 내용
     * @param attachContent 첨부 내용
     * @param isHtml 첨부파일인지 아닌지체크
     * @return String 완전한 본문 내용을 구성한다.
     */
    public String generateContent(String mid, String rawContent, String ctnsPos,
                                  String attachContent, boolean isHtml) {
      //String tmpContent = getContent(pos,content);

      //인코딩을 해준다.
      String transferStr = "";
      if (isHtml) {
        transferStr = getTransferEncoding(rawContent,
                                          HeaderGenerator.getContEncType());
      }
      else {
        transferStr = rawContent;
      }

      //인코딩한 것과 첨부파일을 붙여준다.
      if (attachContent != null && !attachContent.equals("")) {
        StringBuffer sb = new StringBuffer();
        transferStr = sb.append(getContentHeader(HeaderGenerator.getContentType(),
                                                 HeaderGenerator.getCharSet(),
                                                 HeaderGenerator.getContEncType(),
                                                 HeaderGenerator.getBoundary(),
                                                 isHtml))
            .append(transferStr).append(Header.NEW_LINE).append(attachContent)
            .append("--").append(HeaderGenerator.getBoundary()).append("--").
            toString();
        sb = null;
      }

      return transferStr;
    }

    /**
     * 머지 메일인경우 완전한 본문(본문내용+첨부파일)을 구성한다.
     * @version 1.0
     * @author ymkim
     * @param rawContent 본문 내용
     * @param attachContent 첨부 내용
     * @param isHtml 첨부파일인지 아닌지체크
     * @param mergyHash 머지 리스트
     * @param rawContent_vest 보안메일 내용 
     * @param secu_att_yn 보안메일 적용여부
     * @param rMAP1 보안메일 암호키  
     * @return String 완전한 본문 내용을 구성한다.
     */
    public String generateContent(String mid, String rawContent, String ctnsPos,
                                  String attachContent, boolean isHtml,
                                  Hashtable mergyHash, String rawContent_vest, String secu_att_yn, String rENCKEY, String secu_att_typ) {
      // 머지 메일일 경우 머지 시켜준다.
      // 실제 메일에 머지값이 머지되는 부분이다.

      String mergyStr = getTransferMerge(rawContent, mergyHash);
      String mergyStr_vest = "";
      
      // 보안 HTML
      if ("Y".equals(secu_att_yn) && rawContent_vest != null && "HTML".equals(secu_att_typ)) {
    	  mergyStr_vest = getTransferMerge(rawContent_vest, mergyHash);
    	  
      // 보안 PDF
      }else if ("Y".equals(secu_att_yn) && rawContent_vest != null && "PDF".equals(secu_att_typ)) {
    	  mergyStr_vest = getTransferMerge(rawContent_vest, mergyHash);  
      
      // 보안 EXCEL
      }else if ("Y".equals(secu_att_yn) && rawContent_vest != null && "EXCEL".equals(secu_att_typ)) {
    	  mergyStr_vest = getTransferMerge(rawContent_vest, mergyHash);  
      }

      if (mergyStr == null) {
        return null;
      }

      //인코딩을 해준다.
      String transferStr = "";
      String transferStr_vest = "";

      if (isHtml) {
        transferStr = getTransferEncoding(mergyStr,  HeaderGenerator.getContEncType());
        
        // 보안 HTML
        if ("Y".equals(secu_att_yn) && "HTML".equals(secu_att_typ)) {
        	mergyStr_vest =  getContentFromURL(mid, mergyStr_vest);
        	transferStr_vest = getTransferEncoding(mergyStr_vest,"8bit");
        
        // 보안 PDF
        }else if ("Y".equals(secu_att_yn) && "PDF".equals(secu_att_typ)) {
        	mergyStr_vest =  getContentFromURL(mid, mergyStr_vest);
        	transferStr_vest = getTransferEncoding(mergyStr_vest,"8bit");

        // 보안 EXCEL
        }else if ("Y".equals(secu_att_yn) && "EXCEL".equals(secu_att_typ)) {
        	mergyStr_vest =  getContentFromURL(mid, mergyStr_vest);
        	transferStr_vest = getTransferEncoding(mergyStr_vest,"8bit");
        }
        
      }
      else {
        transferStr = mergyStr;
        
        // 보안 HTML
        if ("Y".equals(secu_att_yn) && "HTML".equals(secu_att_typ) ) {
        	transferStr_vest = mergyStr_vest;
        
        // 보안 PDF
        }else if ("Y".equals(secu_att_yn) && "PDF".equals(secu_att_typ) ) {
        	transferStr_vest = mergyStr_vest;
        
        // 보안 EXCEL
        }else if ("Y".equals(secu_att_yn) && "EXCEL".equals(secu_att_typ) ) {
        	transferStr_vest = mergyStr_vest;
        }
        
      }
      
      //보안 HTML 적용
      if ("Y".equals(secu_att_yn) && "HTML".equals(secu_att_typ)) {

    	  VMCipherImpl aCipherInterface = null;
		      try {
				aCipherInterface = new VMCipherImpl();
			} catch (SGException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
				String encMail = null;
				String templateType1 = null;
		
				//템플릿 (비밀번호 입력 화면) 
				try {
					templateType1 = readFile ("./template/template.html", "utf-8");
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				try {
					encMail = aCipherInterface.makeMailContentWithTemplate (
							rENCKEY, 				// 암호 
							transferStr_vest, 	// 본문 내용
							templateType1);	// 템플릿 내용 
					
				} catch (SGException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
				saveFile("./sample/output/"+ mid +"_screatfile.html", encMail.getBytes(), "UTF-8");
				String AAA = readFile("./sample/output/"+ mid +"_screatfile.html", "UTF-8");
				
				transferStr_vest = AAA;
				} catch (IOException e) {
				e.printStackTrace();
				}
		
				//보안메일로 만든 html 파일을 base64로 변환하여 첨부파일 헤어와 본문으로 구성한다.
				String attachContent_vest = "";
				String[] attachFileList_vest = new String[1];
				attachFileList_vest[0] = "./sample/output/"+ mid +"_screatfile.html";
				attachContent_vest = getAttachFile_vest(mid, attachFileList_vest ,secu_att_yn ,secu_att_typ);
      
				   //보안메일 전용 인코딩한 것과 첨부파일을 붙여준다.
			      if (attachContent_vest != null && !attachContent_vest.equals("")) {
			    	  
			    	  if (attachContent != null && !attachContent.equals("")) {
			    		  StringBuffer sb = new StringBuffer();
			    	        transferStr = sb.append(getContentHeader(HeaderGenerator.getContentType(),
			    	                                                 HeaderGenerator.getCharSet(),
			    	                                                 HeaderGenerator.getContEncType(),
			    	                                                 HeaderGenerator.getBoundary(),
			    	                                                 isHtml))
			    	            .append(transferStr).append(Header.NEW_LINE).append(attachContent_vest).append(attachContent)
			    	            .append("--").append(HeaderGenerator.getBoundary()).append("--").
			    	            toString();
			    	        sb = null;
			        
			    	  }else {
			    		  StringBuffer sb = new StringBuffer();
			    	        transferStr = sb.append(getContentHeader(HeaderGenerator.getContentType(),
			    	                                                 HeaderGenerator.getCharSet(),
			    	                                                 HeaderGenerator.getContEncType(),
			    	                                                 HeaderGenerator.getBoundary(),
			    	                                                 isHtml))
			    	            .append(transferStr).append(Header.NEW_LINE).append(attachContent_vest)
			    	            .append("--").append(HeaderGenerator.getBoundary()).append("--").
			    	            toString();
			    	        sb = null;
			    	  }
			        
			      }
	
	  // 보안 PDF 적용		      
      }else if ("Y".equals(secu_att_yn) && "PDF".equals(secu_att_typ)) {
    	  
      	//HtmlToPdf convert = new HtmlToPdf("../config/TScheduler.conf");
      	HtmlToPdf convert = new HtmlToPdf("./config/TScheduler.conf");
  		
  		convert.setOrientation("Portrait");			//Portrait:세로  , Landscape:가로
  		convert.setMarginTop(10);					//페이지 프레임 상단 10 여백
  		convert.setMarginBottom(10);				//페이지 프레임 하단 10 여백
  		convert.setMarginLeft(10);					//페이지 프레임 왼쪽 10 여백
  		convert.setMarginRight(10);					//페이지 프레임 오른쪽 10 여백
      	
      	String pdfFile = "./sample/output/"+ mid +"_screatfile.pdf";
      	try {
      		convert.htmlContentToEncryptPdf(transferStr_vest, pdfFile, rENCKEY);

      	}catch (Exception e) {
				e.printStackTrace();
		}
      	
			//보안메일로 만든 html 파일을 base64로 변환하여 첨부파일 헤어와 본문으로 구성한다.
			String attachContent_vest = "";
			String[] attachFileList_vest = new String[1];
			attachFileList_vest[0] = "./sample/output/"+ mid +"_screatfile.pdf";
			attachContent_vest = getAttachFile_vest(mid, attachFileList_vest, secu_att_yn ,secu_att_typ);
  
			  //보안메일 전용 인코딩한 것과 첨부파일을 붙여준다.
		      if (attachContent_vest != null && !attachContent_vest.equals("")) {
		    	  
		    	  if (attachContent != null && !attachContent.equals("")) {
		    		  StringBuffer sb = new StringBuffer();
		    	        transferStr = sb.append(getContentHeader(HeaderGenerator.getContentType(),
		    	                                                 HeaderGenerator.getCharSet(),
		    	                                                 HeaderGenerator.getContEncType(),
		    	                                                 HeaderGenerator.getBoundary(),
		    	                                                 isHtml))
		    	            .append(transferStr).append(Header.NEW_LINE).append(attachContent_vest).append(attachContent)
		    	            .append("--").append(HeaderGenerator.getBoundary()).append("--").
		    	            toString();
		    	        sb = null;
		        
		    	  }else {
		    		  StringBuffer sb = new StringBuffer();
		    	        transferStr = sb.append(getContentHeader(HeaderGenerator.getContentType(),
		    	                                                 HeaderGenerator.getCharSet(),
		    	                                                 HeaderGenerator.getContEncType(),
		    	                                                 HeaderGenerator.getBoundary(),
		    	                                                 isHtml))
		    	            .append(transferStr).append(Header.NEW_LINE).append(attachContent_vest)
		    	            .append("--").append(HeaderGenerator.getBoundary()).append("--").
		    	            toString();
		    	        sb = null;
		    	  }
		        
		      }
	
	  // 보안 EXCEL 적용	      
	  }else if ("Y".equals(secu_att_yn) && "EXCEL".equals(secu_att_typ)) {
		  
		  
          String excelFile = mid +"_screatfile";
          try {
        	  HtmlToExcelConverter excelConverter = new HtmlToExcelConverter(transferStr_vest);
              excelConverter.getWorkBook(excelFile, "./sample/output/", rENCKEY);
          
          	//보안메일로 만든 html 파일을 base64로 변환하여 첨부파일 헤어와 본문으로 구성한다.
			String attachContent_vest = "";
			String[] attachFileList_vest = new String[1];
			attachFileList_vest[0] = "./sample/output/"+ mid +"_screatfile.xlsx";
			attachContent_vest = getAttachFile_vest(mid, attachFileList_vest, secu_att_yn ,secu_att_typ);
	
			//보안메일 전용 인코딩한 것과 첨부파일을 붙여준다.
		      if (attachContent_vest != null && !attachContent_vest.equals("")) {
		    	  
		    	  if (attachContent != null && !attachContent.equals("")) {
		    		  StringBuffer sb = new StringBuffer();
		    	        transferStr = sb.append(getContentHeader(HeaderGenerator.getContentType(),
		    	                                                 HeaderGenerator.getCharSet(),
		    	                                                 HeaderGenerator.getContEncType(),
		    	                                                 HeaderGenerator.getBoundary(),
		    	                                                 isHtml))
		    	            .append(transferStr).append(Header.NEW_LINE).append(attachContent_vest).append(attachContent)
		    	            .append("--").append(HeaderGenerator.getBoundary()).append("--").
		    	            toString();
		    	        sb = null;
		        
		    	  }else {
		    		  StringBuffer sb = new StringBuffer();
		    	        transferStr = sb.append(getContentHeader(HeaderGenerator.getContentType(),
		    	                                                 HeaderGenerator.getCharSet(),
		    	                                                 HeaderGenerator.getContEncType(),
		    	                                                 HeaderGenerator.getBoundary(),
		    	                                                 isHtml))
		    	            .append(transferStr).append(Header.NEW_LINE).append(attachContent_vest)
		    	            .append("--").append(HeaderGenerator.getBoundary()).append("--").
		    	            toString();
		    	        sb = null;
		    	  }
		        
		      }
	
          }catch (Exception e) {
  			// TODO: handle exception
          	  e.printStackTrace();
          	transferStr = "ErrorEXCEL";
            }
		      
    // 일반 메일 적용
	}else {
    	  if (attachContent != null && !attachContent.equals("")) {
    		//그냥 첨부파일만 있을 경우
      	  	StringBuffer sb = new StringBuffer();
  	        transferStr = sb.append(getContentHeader(HeaderGenerator.getContentType(),
  	                                                 HeaderGenerator.getCharSet(),
  	                                                 HeaderGenerator.getContEncType(),
  	                                                 HeaderGenerator.getBoundary(),
  	                                                 isHtml))
  	            .append(transferStr).append(Header.NEW_LINE).append(attachContent)
  	            .append("--").append(HeaderGenerator.getBoundary()).append("--").
  	            toString();
  	        sb = null;
    	  }
      }

      return transferStr;
    }
    
    
    
    /**
     * vest 보안메일 저장
     * @param fname
     * @param content
     * @param charset
     * @throws IOException
     */
  	public static void saveFile(String fname, byte[] content, String charset) throws IOException {
      	File aEncFile = new File(fname);
      	aEncFile.createNewFile();
      	
      	BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aEncFile.getPath()), charset));
  		output.write(new String(content));	
  		output.close();
  	}
    
  	/**
  	 * vest 보안메일 readFile
  	 * @param fname
  	 * @param charset
  	 * @return
  	 * @throws IOException
  	 */
	public static String readFile(String fname, String charset) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(new FileInputStream(fname), charset));
		String sread = null;
		while((sread = br.readLine())!=null) {					
			sb.append(sread).append("\n");
		}
		br.close();
		return sb.toString();
	}
	
	
	  /**
     * 첨부파일을 얻어온후에 그것을 Base64로 인코딩한다
     * @param mid 예약메일ID
     * @param attachFileList attachFileList 첨부파일 경로 리스트
     * @return  boolean true - 인코딩 성공, false - 인코딩 실패
     */
    private boolean setAttachFile_vest(String mid, String[] attachFileList, String secu_att_yn, String secu_att_typ) {
      FileInputStream fi = null;
      FileOutputStream fo = null;
      StringBuffer sb = null;

      boolean return_value = false;

      try {
        //일단은 만들어준다.
        int attachSize = attachFileList.length;
        for (int i = 0; i < attachSize; i++) {
          if (attachFileList[i] != null && !attachFileList[i].equals("")) {
            sb = new StringBuffer();
            String attachEncFolder = sb.append(Config.ROOT_DIR).append(File.
                separator)
                .append(AttachFileManager.ATTACH_ENC_FOLDER).append(File.
                separator).toString();    // .\TempStorage\
            sb = null;
            //일단 MID 폴더를 만들어준다.
            File f = new File(attachEncFolder + mid);  // .\TempStorage\189
            if (!f.isDirectory()) {
              f.mkdir();
            }

            BASE64Encoder encoder = new BASE64Encoder();
            //encoder.encode(new FileInputStream(new File("Test.zip")),new FileOutputStream(new File("Test.txt")));

            sb = new StringBuffer();
            fi = new FileInputStream(new File(attachFileList[i]));   //[c:/test.txt, , , , ]
            fo = new FileOutputStream(new File(sb.append(attachEncFolder).
                                               append(
                mid)
                                               .append(File.separator).append(
                "attach_vest")
                                               .append(i).append(".txt").
                                               toString()));
            sb = null;
            encoder.encode(fi, fo);
          }
        }

        return_value = true;
        //차후에는 얻어오기만한다.
      }
      catch (Exception e) {
        e.printStackTrace();

        //에러 로그를 남겨준다.
        ErrorLogGenerator.setErrorLogFormat(this.getClass().getName(),
                                            ReserveStatusCode.
                                            CONTENTS_FAIL_TYPE,
                                            ReserveStatusCode.
                                            ATTACH_FILE_NOT_FOUND_COMMENT, mid);
        return_value = false;
      }
      finally {
        try {
          if (fi != null) {
            fi.close();
          }
        }
        catch (IOException e) {
        }

        try {
          if (fo != null) {
            fo.close();
          }
        }
        catch (IOException e) {
        }
      }
      return return_value;
    }
  	
    
    
    /**
     * 인코딩된 첨부파일들을 전부 얻어온다.
     * @version 1.0
     * @author ymkim
     * @param mid 예약메일ID
     * @param attachFileList 첨부파일 경로 리스트
     * @return String 인코딩된 첨부파일의 내용들
     */
    public String getAttachFile_vest(String mid, String[] attachFileList, String secu_att_yn, String secu_att_typ) {
      if (!setAttachFile_vest(mid, attachFileList, secu_att_yn, secu_att_typ)) {    // attachFilelist : [c:/test.txt, , , , ]
        return " ";
      }

      BufferedReader br = null;
      String allEncAttahStr = "";
      String return_value = "";
      StringBuffer sb = null;

      try {
        //일단은 만들어준다.
        int attachSize = attachFileList.length;
        for (int i = 0; i < attachSize; i++) {
          if (attachFileList[i] != null && !attachFileList[i].equals("")) {
            sb = new StringBuffer();
            String attachEncFolder = sb.append(Config.ROOT_DIR)
                .append(File.separator).append(AttachFileManager.
                                               ATTACH_ENC_FOLDER)
                .append(File.separator).toString();
            sb = null;

            sb = new StringBuffer();
            //일단 MID 폴더를 만들어준다.
            File rawFile = new File(attachFileList[i]);   // c:\test.txt    .\sample\output\2222.html
            File encFile = new File(sb.append(attachEncFolder).append(mid)
                                    .append(File.separator).append("attach_vest").
                                    append(i)
                                    .append(".txt").toString());    // .\TempStorage\189\attach_0.txt	.\TempStorage\227\attach_vest0.txt
            //f.mkdir();
            sb = null;

            br = new BufferedReader(new FileReader(encFile));
            String str = "";

            StringBuffer sb2 = new StringBuffer();
            //이러면 하나의 파일에서 내용을 읽어들이게 된다.
            while ( (str = br.readLine()) != null) {
              sb2.append(str).append("\n");
            }

            //String fileName = rawFile.getName();
            String fileName = "";
            String tempHeader = "";
            
            //보안 HTML
            if ("Y".equals(secu_att_yn) && "HTML".equals(secu_att_typ)) {
            	
            	// 메일의 보여줄 첨부파일 이름
            	fileName = "secretfile.html";		
            	
            	//컨덴츠 헤더+첨부파일 내용을 연결시켜준다.
            	tempHeader = getAttachHeader(getContentTypeFromFile(rawFile),
                        HeaderGenerator.getCharSet(),
                        "base64",
                        HeaderGenerator.getBoundary(),
                        fileName);
            
            //보안 PDF
            }else if ("Y".equals(secu_att_yn) && "PDF".equals(secu_att_typ)) {
            	fileName = "secretfile.pdf";
            	
            	tempHeader = getAttachHeader("application/pdf",
                        HeaderGenerator.getCharSet(),
                        "base64",
                        HeaderGenerator.getBoundary(),
                        fileName);
            	
            //보안 EXCEL
            }else if ("Y".equals(secu_att_yn) && "EXCEL".equals(secu_att_typ)) {
            	fileName = "secretfile.xlsx";
            	
            	tempHeader = getAttachHeader("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        HeaderGenerator.getCharSet(),
                        "base64",
                        HeaderGenerator.getBoundary(),
                        fileName);
            }
            
            
            
            
            sb = new StringBuffer();
            allEncAttahStr = sb.append(allEncAttahStr).append(tempHeader).
                append(
                sb2.toString())
                .append(Header.NEW_LINE).toString();
            sb = null;
            sb2 = null;
            
            
            //보안메일 파일을 지운다
            rawFile.delete();  //html 암호화파일  			.\sample\output\297_screatfile.html
            encFile.delete();  //html base64 변환파일		.\TempStorage\297\attach_vest0.txt
            
          }
        }

        return_value = allEncAttahStr;
        //차후에는 얻어오기만한다.
        
        
        
      }
      catch (Exception e) {
        e.printStackTrace();
        //첨부파일이 없을때에 에러로 처리하고 싶으면 return_value를 널로 넘긴다.
        //첨부파일이 없더라도 그냥 메일을 보내고 싶으면 return_value를 " " 로 넘긴다.
        //return_value = " ";
        return_value = null;
      }
      finally {
        try {
          if (br != null) {
            br.close();
            br = null;
          }
        }
        catch (IOException e) {
        }
      }

      return return_value;
    }
    
    
    
    
  }
