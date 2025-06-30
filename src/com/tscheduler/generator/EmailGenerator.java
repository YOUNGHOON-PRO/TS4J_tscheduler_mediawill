package com.tscheduler.generator;

import java.util.*;
import java.io.*;

import com.tscheduler.util.*;

import synap.next.ParttenCheckUtil;

import com.tscheduler.manager.*;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 이메일 생성 전체 관리 클래스
 * 
 * @version 1.0
 * @author ymkim
 */
public class EmailGenerator {

	private static final Logger LOGGER = LogManager.getLogger(EmailGenerator.class.getName());

  /** 구분자 */
  private static final String CNT_CODE_DELIM = "``";
  private static String persoanl_pass = "";
  private static String persoanl_yn = "";

  /**
   * 이메일을 만든다.
   * 
   * @version 1.0
   * @author ymkim
   * @param ruserFileList 수신자파일리스트
   * @param reserveInfo 예약 메일 정보
   */
  public static void makeEmail(File[] rUserFileList, DataUnitInfo reserveInfo) {
    ArrayList errorLogInfoList = new ArrayList();
    Hashtable errorLogInfo = null;
    // 수신확인 호스트정보와 머지리스트 값
    Config cfg = Config.getInstance();
    cfg.loadConfig(Config.MAIN_CFG);
    String receiveDefineHost = cfg.getReceiveDefineHost();  //http://103.9.32.183:20000/TS/Receiver/ReceiverCheck.jsp
    String mergeList = cfg.getMergyList();	//RID``RNAME``RMAIL``HRNAME``ENCKEY``MAP1``MAP2``MAP3``MAP4``MAP5``MAP6``MAP7``MAP8``MAP9``MAP10``MAP11``MAP12``MAP13``MAP14``MAP15``MAP16``MAP17``MAP18``MAP19``MAP20``MAP21``MAP22``MAP23``MAP24``MAP25``MAP26``MAP27``MAP28``MAP29``MAP30``MAP31``MAP32``MAP33``MAP34``MAP35``MAP36``MAP37``MAP38``MAP39``MAP40``MAP41``MAP42``MAP43``MAP44``MAP45``MAP46``MAP47``MAP48``MAP49``MAP50``BIZKEY``
    boolean bMergeContents = false;
    int legacyRSListSize = cfg.getLegacyRSListSize();	//1000
    StringBuffer sb = null;
    
    persoanl_yn = cfg.getPersonal_yn();
    persoanl_pass = cfg.getPersonal_pass();

    //헤더를 만든다
    HeaderGenerator header = HeaderGenerator.getInstance();
    //컨덴츠를 만든다.
    ContentGenerator content = ContentGenerator.getInstance();
    ContentGenerator content_vest = ContentGenerator.getInstance();

    //발신자를 위한 정보들을 뽑아온다.
    String mID = reserveInfo.getString("MID");
    String subID = reserveInfo.getString("SUBID");
    String tID = reserveInfo.getString("TID");
    String sID = reserveInfo.getString("SID");
    String refMID = reserveInfo.getString("REFMID");
    String sMail = reserveInfo.getString("SMAIL");
    
    String sName = reserveInfo.getString("SNAME");
    String subject = reserveInfo.getString("SUBJECT");
    String charset = reserveInfo.getString("CHARSET");
    String[] attachFileList = new String[5];
    attachFileList[0] = reserveInfo.getString("ATTACHFILE01");
    attachFileList[1] = reserveInfo.getString("ATTACHFILE02");
    attachFileList[2] = reserveInfo.getString("ATTACHFILE03");
    attachFileList[3] = reserveInfo.getString("ATTACHFILE04");
    attachFileList[4] = reserveInfo.getString("ATTACHFILE05");

    String ctnPos = reserveInfo.getString("CTNPOS");
    String contents = reserveInfo.getString("CONTENTS");
    String rawContent = "";
    String rawContent_vest = "";
    
    String	enckey = reserveInfo.getString("ENCKEY");
    String	map1 = reserveInfo.getString("MAP1");
    String	map2 = reserveInfo.getString("MAP2");
    String	map3 = reserveInfo.getString("MAP3");
    String	map4 = reserveInfo.getString("MAP4");
    String	map5 = reserveInfo.getString("MAP5");
    String	map6 = reserveInfo.getString("MAP6");
    String	map7 = reserveInfo.getString("MAP7");
    String	map8 = reserveInfo.getString("MAP8");
    String	map9 = reserveInfo.getString("MAP9");
    String	map10 = reserveInfo.getString("MAP10");
    String	map11 = reserveInfo.getString("MAP11");
    String	map12 = reserveInfo.getString("MAP12");
    String	map13 = reserveInfo.getString("MAP13");
    String	map14 = reserveInfo.getString("MAP14");
    String	map15 = reserveInfo.getString("MAP15");
    String	map16 = reserveInfo.getString("MAP16");
    String	map17 = reserveInfo.getString("MAP17");
    String	map18 = reserveInfo.getString("MAP18");
    String	map19 = reserveInfo.getString("MAP19");
    String	map20 = reserveInfo.getString("MAP20");
    String	map21 = reserveInfo.getString("MAP21");
    String	map22 = reserveInfo.getString("MAP22");
    String	map23 = reserveInfo.getString("MAP23");
    String	map24 = reserveInfo.getString("MAP24");
    String	map25 = reserveInfo.getString("MAP25");
    String	map26 = reserveInfo.getString("MAP26");
    String	map27 = reserveInfo.getString("MAP27");
    String	map28 = reserveInfo.getString("MAP28");
    String	map29 = reserveInfo.getString("MAP29");
    String	map30 = reserveInfo.getString("MAP30");
    String	map31 = reserveInfo.getString("MAP31");
    String	map32 = reserveInfo.getString("MAP32");
    String	map33 = reserveInfo.getString("MAP33");
    String	map34 = reserveInfo.getString("MAP34");
    String	map35 = reserveInfo.getString("MAP35");
    String	map36 = reserveInfo.getString("MAP36");
    String	map37 = reserveInfo.getString("MAP37");
    String	map38 = reserveInfo.getString("MAP38");
    String	map39 = reserveInfo.getString("MAP39");
    String	map40 = reserveInfo.getString("MAP40");
    String	map41 = reserveInfo.getString("MAP41");
    String	map42 = reserveInfo.getString("MAP42");
    String	map43 = reserveInfo.getString("MAP43");
    String	map44 = reserveInfo.getString("MAP44");
    String	map45 = reserveInfo.getString("MAP45");
    String	map46 = reserveInfo.getString("MAP46");
    String	map47 = reserveInfo.getString("MAP47");
    String	map48 = reserveInfo.getString("MAP48");
    String	map49 = reserveInfo.getString("MAP49");
    String	map50 = reserveInfo.getString("MAP50");
    
    String	bizkey = reserveInfo.getString("BIZKEY");
    
    String	secu_att_yn = reserveInfo.getString("SECU_ATT_YN");
    String	source_url = reserveInfo.getString("SOURCE_URL");
    String	secu_att_typ = reserveInfo.getString("SECU_ATT_TYP");
    
    String	title_chk_yn = reserveInfo.getString("TITLE_CHK_YN");
    String	body_chk_yn = reserveInfo.getString("BODY_CHK_YN");
    String	attach_file_chk_yn = reserveInfo.getString("ATTACH_FILE_CHK_YN");
    String	secu_mail_chk_yn = reserveInfo.getString("SECU_MAIL_CHK_YN");
    
    String requestKey = reserveInfo.getString("REQUEST_KEY");
    
    //contents 에 config에 설정한 머지키가 있는지 확인한다.
    StringTokenizer st1 = new StringTokenizer(mergeList, CNT_CODE_DELIM);
    int tokenCount = st1.countTokens();
    if (tokenCount != 0) {
      String tempContents = contents.toLowerCase();
      for (int i = 0; i < tokenCount; i++) {
        if (tempContents.indexOf("$:" + st1.nextToken().trim().toLowerCase() + ":$") != -1) {
          bMergeContents = true;
          break;
        }
      }
    }

    if(Config.getInstance().isMultiLang()){
      header.setSenderInfo(sName, sMail, sMail, charset);
    }else{
      header.setSenderInfo(sName, sMail, sMail);
    }

    //4. 본문이 Html파일인지 아닌지 체크한다.
    boolean isHtml = false;
    if (!bMergeContents) {//머지되는 컨텐츠가 아닌 경우
      //수신자에 대해서 처리하기전에  컨덴츠와 첨부파일을 가져온다.
      //1. 컨덴츠를 가져온다.(머지하기 전의 내용을 그대로 가져온다.)
      if(Config.getInstance().isMultiLang()){
        rawContent = content.getContent(mID, ctnPos, contents, charset);
      }else{
        rawContent = content.getContent(mID, ctnPos, contents);

        //보안 HTML
        if("Y".equals(secu_att_yn) && "HTML".equals(secu_att_typ)){
        	rawContent_vest = content.getContent_vest(mID, ctnPos, source_url);
        
        //보안 PDF
        }else if("Y".equals(secu_att_yn) && "PDF".equals(secu_att_typ)){        	
        	rawContent_vest = content.getContent_vest(mID, ctnPos, source_url);
        
        //보안 EXCEL
        }else if("Y".equals(secu_att_yn) && "EXCEL".equals(secu_att_typ)){
        	rawContent_vest = content.getContent_vest(mID, ctnPos, source_url);
        }
        
      }
      isHtml = content.getIsHtml();
    }

    //2. 첨부파일을 가져온다.
    String attachContent = "";
    boolean isAttach = false;
    if (attachFileList[0] != null && !attachFileList[0].equals("")) {
      DebugTrace.print("첨부파일 읽기 시작...");
      attachContent = content.getAttachFile(mID, attachFileList, attach_file_chk_yn);
      DebugTrace.println("   완료!!");

      if(attachContent != null) {
    	  isAttach = true;  
      }
    }

    //3. 머지인지 체크한후 머지내용들을 가져온다.
    boolean isMerge = false;

    /*************************************************
         ** 여기서 컨덴츠와 첨부파일이 제대로 가져왔는지 체크한후에 제대로 가져오지 않으면 컨덴츠 생성을 해주지 않으며 또한 에러처리를 해준다.**
     *************************************************/
    //LOGGER.info(rawContent);
    if (rawContent == null || attachContent == null || attachContent.equals("personal_error")) { //첨부파일이나 혹은 본문내용이
    																							 // 제대로 가져오지 못하면
    																							 // 컨덴츠 생성을 하지
    																							 // 않는다.
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
      
      if(attachContent.equals("personal_error")) {
    	  errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_CONTENT2);  
    	  
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
          errorLogInfo.put("MAP16", map16);
          errorLogInfo.put("MAP17", map17);
          errorLogInfo.put("MAP18", map18);
          errorLogInfo.put("MAP19", map19);
          errorLogInfo.put("MAP20", map20);
          errorLogInfo.put("MAP21", map21);
          errorLogInfo.put("MAP22", map22);
          errorLogInfo.put("MAP23", map23);
          errorLogInfo.put("MAP24", map24);
          errorLogInfo.put("MAP25", map25);
          errorLogInfo.put("MAP26", map26);
          errorLogInfo.put("MAP27", map27);
          errorLogInfo.put("MAP28", map28);
          errorLogInfo.put("MAP29", map29);
          errorLogInfo.put("MAP30", map30);
          errorLogInfo.put("MAP31", map31);
          errorLogInfo.put("MAP32", map32);
          errorLogInfo.put("MAP33", map33);
          errorLogInfo.put("MAP34", map34);
          errorLogInfo.put("MAP35", map35);
          errorLogInfo.put("MAP36", map36);
          errorLogInfo.put("MAP37", map37);
          errorLogInfo.put("MAP38", map38);
          errorLogInfo.put("MAP39", map39);
          errorLogInfo.put("MAP40", map40);
          errorLogInfo.put("MAP41", map41);
          errorLogInfo.put("MAP42", map42);
          errorLogInfo.put("MAP43", map43);
          errorLogInfo.put("MAP44", map44);
          errorLogInfo.put("MAP45", map45);
          errorLogInfo.put("MAP46", map46);
          errorLogInfo.put("MAP47", map47);
          errorLogInfo.put("MAP48", map48);
          errorLogInfo.put("MAP49", map49);
          errorLogInfo.put("MAP50", map50);
          errorLogInfo.put("BIZKEY", bizkey);
    	  
    	  ErrorLogGenerator.setErrorLogFormat("EmailGenerator",
                  ReserveStatusCode.CONTENTS_FAIL_TYPE,
                  ReserveStatusCode.
                  ATTACH_PERSONAL, mID);
    	  
      }else {
    	  errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_CONTENT);  
    	  
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
          errorLogInfo.put("MAP16", map16);
          errorLogInfo.put("MAP17", map17);
          errorLogInfo.put("MAP18", map18);
          errorLogInfo.put("MAP19", map19);
          errorLogInfo.put("MAP20", map20);
          errorLogInfo.put("MAP21", map21);
          errorLogInfo.put("MAP22", map22);
          errorLogInfo.put("MAP23", map23);
          errorLogInfo.put("MAP24", map24);
          errorLogInfo.put("MAP25", map25);
          errorLogInfo.put("MAP26", map26);
          errorLogInfo.put("MAP27", map27);
          errorLogInfo.put("MAP28", map28);
          errorLogInfo.put("MAP29", map29);
          errorLogInfo.put("MAP30", map30);
          errorLogInfo.put("MAP31", map31);
          errorLogInfo.put("MAP32", map32);
          errorLogInfo.put("MAP33", map33);
          errorLogInfo.put("MAP34", map34);
          errorLogInfo.put("MAP35", map35);
          errorLogInfo.put("MAP36", map36);
          errorLogInfo.put("MAP37", map37);
          errorLogInfo.put("MAP38", map38);
          errorLogInfo.put("MAP39", map39);
          errorLogInfo.put("MAP40", map40);
          errorLogInfo.put("MAP41", map41);
          errorLogInfo.put("MAP42", map42);
          errorLogInfo.put("MAP43", map43);
          errorLogInfo.put("MAP44", map44);
          errorLogInfo.put("MAP45", map45);
          errorLogInfo.put("MAP46", map46);
          errorLogInfo.put("MAP47", map47);
          errorLogInfo.put("MAP48", map48);
          errorLogInfo.put("MAP49", map49);
          errorLogInfo.put("MAP50", map50);
          errorLogInfo.put("BIZKEY", bizkey);

          ErrorLogGenerator.setErrorLogFormat("EmailGenerator",
                                              ReserveStatusCode.CONTENTS_FAIL_TYPE,
                                              ReserveStatusCode.
                                              CONTENT_NOT_FOUND_COMMENT, mID);
      }

      errorLogInfoList.add(errorLogInfo);

      reserveInfo.setString("STATUS", ReserveStatusCode.CONTENTS_FAIL);
      //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
      LogFileManager.setReserveStatusUpdate(mID, subID,
                                            ReserveStatusCode.CONTENTS_FAIL);

      //먼가 에러 로그가 쌓인것이 있으면 그 로그를 넣어준다.
      ResultLogManager.InsertResultLog(errorLogInfoList);
      return;
    }

    DataUnitInfoList rUserList = null;
    int fileSize = rUserFileList.length;
    for (int k = 0; k < fileSize; k++) {
      ArrayList mergyArray = new ArrayList();

      if(Config.getInstance().isMultiLang()){
        rUserList = TransferFileToDataGenerator.transferFileToDataUnit(
            rUserFileList[k], charset);
      }else{
        rUserList = TransferFileToDataGenerator.transferFileToDataUnit(
            rUserFileList[k]);
      }

      
      String[] rIDList = rUserList.getStringArray("RID");
      String[] rNameList = rUserList.getStringArray("HRNAME");
      String[] rMailList = rUserList.getStringArray("RMAIL");
      String[] rENCKEY = rUserList.getStringArray("ENCKEY");
      String[] rMAP1 = rUserList.getStringArray("MAP1");
      String[] rMAP2 = rUserList.getStringArray("MAP2");
      String[] rMAP3 = rUserList.getStringArray("MAP3");
      String[] rMAP4 = rUserList.getStringArray("MAP4");
      String[] rMAP5 = rUserList.getStringArray("MAP5");
      String[] rMAP6 = rUserList.getStringArray("MAP6");
      String[] rMAP7 = rUserList.getStringArray("MAP7");
      String[] rMAP8 = rUserList.getStringArray("MAP8");
      String[] rMAP9 = rUserList.getStringArray("MAP9");
      String[] rMAP10 = rUserList.getStringArray("MAP10");
      String[] rMAP11 = rUserList.getStringArray("MAP11");
      String[] rMAP12 = rUserList.getStringArray("MAP12");
      String[] rMAP13 = rUserList.getStringArray("MAP13");
      String[] rMAP14 = rUserList.getStringArray("MAP14");
      String[] rMAP15 = rUserList.getStringArray("MAP15");
      String[] rMAP16 = rUserList.getStringArray("MAP16");
      String[] rMAP17 = rUserList.getStringArray("MAP17");
      String[] rMAP18 = rUserList.getStringArray("MAP18");
      String[] rMAP19 = rUserList.getStringArray("MAP19");
      String[] rMAP20 = rUserList.getStringArray("MAP20");
      String[] rMAP21 = rUserList.getStringArray("MAP21");
      String[] rMAP22 = rUserList.getStringArray("MAP22");
      String[] rMAP23 = rUserList.getStringArray("MAP23");
      String[] rMAP24 = rUserList.getStringArray("MAP24");
      String[] rMAP25 = rUserList.getStringArray("MAP25");
      String[] rMAP26 = rUserList.getStringArray("MAP26");
      String[] rMAP27 = rUserList.getStringArray("MAP27");
      String[] rMAP28 = rUserList.getStringArray("MAP28");
      String[] rMAP29 = rUserList.getStringArray("MAP29");
      String[] rMAP30 = rUserList.getStringArray("MAP30");
      String[] rMAP31 = rUserList.getStringArray("MAP31");
      String[] rMAP32 = rUserList.getStringArray("MAP32");
      String[] rMAP33 = rUserList.getStringArray("MAP33");
      String[] rMAP34 = rUserList.getStringArray("MAP34");
      String[] rMAP35 = rUserList.getStringArray("MAP35");
      String[] rMAP36 = rUserList.getStringArray("MAP36");
      String[] rMAP37 = rUserList.getStringArray("MAP37");
      String[] rMAP38 = rUserList.getStringArray("MAP38");
      String[] rMAP39 = rUserList.getStringArray("MAP39");
      String[] rMAP40 = rUserList.getStringArray("MAP40");
      String[] rMAP41 = rUserList.getStringArray("MAP41");
      String[] rMAP42 = rUserList.getStringArray("MAP42");
      String[] rMAP43 = rUserList.getStringArray("MAP43");
      String[] rMAP44 = rUserList.getStringArray("MAP44");
      String[] rMAP45 = rUserList.getStringArray("MAP45");
      String[] rMAP46 = rUserList.getStringArray("MAP46");
      String[] rMAP47 = rUserList.getStringArray("MAP47");
      String[] rMAP48 = rUserList.getStringArray("MAP48");
      String[] rMAP49 = rUserList.getStringArray("MAP49");
      String[] rMAP50 = rUserList.getStringArray("MAP50");
      String[] rBIZKEY = rUserList.getStringArray("BIZKEY");
      
      //수신자의 수를 센다.
      int rUserNum = rMailList.length;

      //기본머지에 해당하는 개인정보를 가져온다.
      if (mergeList != null && !mergeList.equals("")) {
        for (int i = 0; i < rUserNum; i++) {
          StringTokenizer st = new StringTokenizer(mergeList, CNT_CODE_DELIM);
          String tempKey;
          String tempValue;
          Hashtable mergeHash = new Hashtable();

          while (st.hasMoreTokens()) {
            tempKey = st.nextToken();
            //mergeKey.add(tempKey);
            tempValue = rUserList.getStringIndex(i, tempKey.toUpperCase());
            //mergeValue.add(tempValue);
            mergeHash.put(tempKey, tempValue);
          }
          mergyArray.add(mergeHash);
        }
        isMerge = true;
      }

      //여기서 부터 컨덴츠를 생성해주는 부분
      try {
        for (int i = 0; i < rUserNum; i++) {
        	
        	//보안메일 첨부 URL 미리 치환1
        	source_url = content.getTransferMerge(source_url, (Hashtable) mergyArray.get(i));
        	rawContent_vest = content.getTransferMerge(source_url, (Hashtable) mergyArray.get(i));
        	
          //header 생성
          String headerStr = header.generateHeader(subject, rNameList[i], rMailList[i], isAttach, isHtml, (Hashtable) mergyArray.get(i),ctnPos,secu_att_yn, secu_att_typ, title_chk_yn, body_chk_yn, attach_file_chk_yn, secu_mail_chk_yn);
/*
					 * StringBuffer dt = new StringBuffer(); dt.append(rNameList[i]).append("<br>");
					 * dt.append(((Hashtable) mergyArray.get(i)).get("RNAME"));
					 * LogUtil.logCurFile(dt.toString());
*/
          String contentStr;
          String raw_receiveStr = "";

          if (bMergeContents) {//머지내용이 있으면, 머지시킨다.
            if(Config.getInstance().isMultiLang()){
              rawContent = content.getContent2(mID, ctnPos, contents,
                                               (Hashtable) mergyArray.get(i),
                                               charset,"CONTENT");
            }else{
              rawContent = content.getContent2(mID, ctnPos, contents,(Hashtable)mergyArray.get(i), charset,"CONTENT");
              //rawContent_vest = content_vest.getContent2(mID, ctnPos, source_url,(Hashtable)mergyArray.get(i), charset,"CONTENT_VEST");
            }
            isHtml = content.getIsHtml();
          }

          sb = new StringBuffer();
          if (isHtml) { //본문내용이 html이면
            //수신모듈을 붙여준다.
            //수신확인 호스트정보

            //수신확인테이블의 Unique필드를 만들어주기 위해서
            raw_receiveStr = (sb.append(rawContent.trim()).append("<br><img src=\"")
                              .append(receiveDefineHost).append("?rsID=")
                              .append(mID).append(i).append("&mID=").append(mID)
                              //.append("&rName=").append(rNameList[i])
                              .append("&rName=").append(URLEncoder.encode(rNameList[i],"UTF-8"))
                              .append("&rMail=").append(rMailList[i])
                              .append("&rID=").append(rIDList[i])
                              .append("&refMID=").append(refMID)
                              .append("&subID=").append(subID)
                              .append("\" width=0 height=0 border=0>")).
                toString();
          }
          else { //본문 내용이 text이면
            raw_receiveStr = (sb.append(HTMLTrans.transeHtml(rawContent.trim()))
                              .append("<img src=\"")
                              .append(receiveDefineHost).append("?rsID=")
                              .append(mID).append(i).append("&mID=").append(mID)
                              //.append("&rName=").append(rNameList[i])
                              .append("&rName=").append(URLEncoder.encode(rNameList[i],"UTF-8"))
                              .append("&rMail=").append(rMailList[i])
                              .append("&rID=").append(rIDList[i])
                              .append("&refMID=").append(refMID)
                              .append("&subID=").append(subID)
                              .append("\" width=0 height=0 border=0>")).
                toString();
          }

          sb = null;

          //머지메일인지 아닌지 체크한다.
          if (isMerge) { //머지메일이다.
            contentStr = content.generateContent(mID, raw_receiveStr, ctnPos,
                                                 attachContent, isHtml,
                                                 (Hashtable) mergyArray.get(i), rawContent_vest, secu_att_yn, rENCKEY[i] , secu_att_typ);
            //contentStr = javax.mail.internet.MimeUtility.encodeText(contentStr, charset, "B");
          }
          else { // 머지메일이 아니다.(동보성메일이다)
            contentStr = content.generateContent(mID, raw_receiveStr, ctnPos,
                                                 attachContent, isHtml);
          }
      
          
         //if("Y".equals(persoanl_yn)) {
          
          if("Y".equals(body_chk_yn)) {
        	//---------------------------------------------------------------------------
       		//본문 개인정보 체크 start
       		//---------------------------------------------------------------------------
     		String[] passedNumbers = persoanl_pass.split(",");  // TScheduler.conf에 PERSONAL_PASS 값을 가져옴
              
             List<String> passedList = new ArrayList<>();
             for (int z = 0; z < passedNumbers.length; z++) {
             	passedList.add(passedNumbers[z]);
             	//LOGGER.info("phons : "+passedNumbers[i]);
             }
     		
             //휴대폰번호 체크
     		boolean CellCK = false;
             //JFilterUtil jFilterUtil = new JFilterUtil(srcFile);
     		CellCK = ParttenCheckUtil.hasCellPhoneNumber(passedList, contentStr);
     		//LOGGER.info("본문 CellCK : " + CellCK);
            
     		//전화번호 체크		
     		boolean TellCK = false;
     		TellCK = ParttenCheckUtil.hasTelePhoneNumber(passedList, contentStr);
     		//LOGGER.info("본문 TellCK : " + TellCK);
     		
             //주민번호 체크
     		boolean PersonalCK = false;
     		PersonalCK = ParttenCheckUtil.hasPersonalId(contentStr);
     		//LOGGER.info("본문 PersonalCK : " + PersonalCK);
     		
     		 //이메일 체크
     		boolean EmailCK = false;
     		EmailCK = ParttenCheckUtil.hasEmail(passedList, contentStr);
     		//LOGGER.info("본문 EmailCK : " + EmailCK);
     		
			if((CellCK) || (TellCK) || (PersonalCK) || (EmailCK)) {
				contentStr = "personal_error";
			}

						// ---------------------------------------------------------------------------
					}

					// 개인정보 체크로 headerStr도 체크
          if (contentStr == null || headerStr ==null || contentStr.equals("personal_error") || contentStr.equals("ErrorEXCEL")) {
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
            if(headerStr == null) {// 개인정보 에러일 경우
            	errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_CONTENT2);
            	
            	ErrorLogGenerator.setErrorLogFormat("EmailGenerator",
                        ReserveStatusCode.
                        CONTENTS_FAIL_TYPE,
                        ReserveStatusCode.
                        SUBJECT_PERSONAL, mID);
            }else if(contentStr.equals("personal_error")) {
            	errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_CONTENT2);
            	
            	ErrorLogGenerator.setErrorLogFormat("EmailGenerator",
                        ReserveStatusCode.
                        CONTENTS_FAIL_TYPE,
                        ReserveStatusCode.
                        BODY_PERSONAL, mID);
            }else if(contentStr.equals("ErrorEXCEL")) {
            	errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_SECU_CONTENT);
            	
            	ErrorLogGenerator.setErrorLogFormat("EmailGenerator",
                        ReserveStatusCode.
                        CONTENTS_FAIL_TYPE,
                        ReserveStatusCode.
                        BODY_PERSONAL, mID);
            }else {
            	errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_CONTENT);
            	
            	ErrorLogGenerator.setErrorLogFormat("EmailGenerator",
                        ReserveStatusCode.
                        CONTENTS_FAIL_TYPE,
                        ReserveStatusCode.
                        CONTENT_NOT_FOUND_COMMENT, mID);
            }

            errorLogInfoList.add(errorLogInfo);

            reserveInfo.setString("STATUS", ReserveStatusCode.CONTENTS_FAIL);
            //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
            LogFileManager.setReserveStatusUpdate(mID, subID,
                                                  ReserveStatusCode.CONTENTS_FAIL);

            //먼가 에러 로그가 쌓인것이 있으면 그 로그를 넣어준다.
            ResultLogManager.InsertResultLog(errorLogInfoList);
            continue;
          }

          //완전한 본문 만들기
          String email = headerStr + contentStr;

		String targetDomains = Config.getInstance().getDKIM_TARGET_DOMAIN();
		if (!targetDomains.equals("")) {
			targetDomains = targetDomains.trim().toLowerCase();

			// 메일 수신자의 도메인 추출
			String toDomain = rMailList[i].split("@")[1].trim().toLowerCase();
			// 메일 발신자의 도메인 추출
			String fromDomain = sMail.split("@")[1].trim().toLowerCase();

			LOGGER.info("DKIM 적용 대상 도메인 목록 {}, 수신자 도메인: {}", targetDomains, toDomain);

			if (targetDomains.indexOf(toDomain) > -1) {
//							LOGGER.debug("Normal MIME<<<{}>>>", email);
				
				String dkimMime = SimpleJavaDKIMSign.addDKIMSignature(
						email, 
//						Config.getInstance().getDKIM_DOMAIN(),
						fromDomain,
						Config.getInstance().getDKIM_SELECTOR(),
						Config.getInstance().getDKIM_PRIVATE_KEY()
					);
//							LOGGER.debug("DKIM MIME<<<{}>>>", dkimMime);
				email = dkimMime;
			} 
		}

          //*************************************************
           //NeoSMTP에서 필요한 여러 정보를 넣어준다.
           //*************************************************
          Hashtable smtpSendInfo = new Hashtable();

          smtpSendInfo.put("SMAIL", sMail);
          smtpSendInfo.put("SUBID", subID);
          smtpSendInfo.put("RMAIL", rMailList[i]);
          smtpSendInfo.put("SID", sID);
          smtpSendInfo.put("SNAME", sName);
          smtpSendInfo.put("RID", rIDList[i]);
          smtpSendInfo.put("RNAME", rNameList[i]);
          smtpSendInfo.put("TID", tID);
          smtpSendInfo.put("REFMID", refMID);
          smtpSendInfo.put("REQUEST_KEY", requestKey);

		  String smtpInfo = SMTPSendInfoGenerator.generateSMTPSendInfo(smtpSendInfo);

          //SMTP 전송정보+ 이메일정보
          String sendEmail = smtpInfo + email;

          //MID폴더를 만든후에 이메일을 그 폴더의 하위 폴더에 500개 단위로 해서 1,2,3 번호를 달아준후에 만들어진 이메일을 넣어준다.(갯수는 프로퍼티로뺀다)
          //이제 이 만들어진 파일을 해당폴더에 만들어 넣어준다.

          boolean finalNumber = false;
          if (i == rUserNum - 1) {
            finalNumber = true;
          }

          if (!EmailFileManager.generateEmailFile(mID, subID,
                                                  legacyRSListSize * k + i,
                                                  sendEmail, finalNumber)) {
            //파일 생성에 실패하면 그 실패한 컨덴츠는 ResultLog에 쌓아준다.
            //에러가 발생하면 그 다음 리스트들은 전부 생성 에러로 넘겨준다.
            errorLogInfo = new Hashtable();
            errorLogInfo.put("MID", mID);
            errorLogInfo.put("SUBID", subID);
            errorLogInfo.put("TID", tID);
            errorLogInfo.put("SID", sID);
            errorLogInfo.put("SNAME", sName);
            errorLogInfo.put("SMAIL", sMail);
            errorLogInfo.put("REFMID", refMID);
            errorLogInfo.put("RID", rIDList[i]);
            errorLogInfo.put("RNAME", rNameList[i]);
            errorLogInfo.put("RMAIL", rMailList[i]);
            errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_CONTENT);

            errorLogInfoList.add(errorLogInfo);
          }
        }

        //먼가 에러 로그가 쌓인것이 있으면 그 로그를 넣어준다.
        if (errorLogInfoList.size() != 0 && rUserNum!=1 ) {
          ResultLogManager.InsertResultLog(errorLogInfoList);
        }

        //컨덴츠 생성 성공했을때는 성공에 대한 상태를 넣어준다.
        reserveInfo.setString("STATUS", ReserveStatusCode.CONTENTS_SUCCESS);
        //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
        LogFileManager.setReserveStatusUpdate(mID, subID,
                                              ReserveStatusCode.CONTENTS_SUCCESS);
      }
      catch (Exception e) {
        e.printStackTrace();

        //컨덴츠 생성 실패했을때는 실패에 대한 상태를 넣어준다.
        reserveInfo.setString("STATUS", ReserveStatusCode.CONTENTS_FAIL);
        //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
        LogFileManager.setReserveStatusUpdate(mID, subID,
                                              ReserveStatusCode.CONTENTS_FAIL);

        //에러 로그를 남겨준다.
        ErrorLogGenerator.setErrorLogFormat("EmailGenerator",
                                            ReserveStatusCode.CONTENTS_FAIL_TYPE,
                                            ReserveStatusCode.
                                            CONTENTS_WRITE_FAIL_COMMENT, mID);
      }
    }
  }


  
  /**
   * 이메일을 만든다.(복구를 위한)
   * @version 1.0
   * @author ymkim
   * @param ruserFileList 수신자 파일 리스트
   * @param reserveInfo 예약 메일 정보
   * @param preMakeNum 이미 생성된 것만큼의 이메일 수(중복 발송을 막기위해)
   */
  public static void makeEmail(File[] rUserFileList, DataUnitInfo reserveInfo,
                               int preMakeNum) {
    ArrayList errorLogInfoList = new ArrayList();
    Hashtable errorLogInfo = null;

    //수신확인 호스트정보와 머지리스트 값
    Config cfg = Config.getInstance();
    cfg.loadConfig(Config.MAIN_CFG);
    String receiveDefineHost = cfg.getReceiveDefineHost();
    String mergyList = cfg.getMergyList();
    int legacyRSListSize = cfg.getLegacyRSListSize();
    boolean bMergeContents = false;
    StringBuffer sb = null;

    //헤더를 만든다
    HeaderGenerator header = HeaderGenerator.getInstance();
    //컨덴츠를 만든다.
    ContentGenerator content = ContentGenerator.getInstance();
    ContentGenerator content_vest = ContentGenerator.getInstance();

    //발신자를 위한 정보들을 뽑아온다.
    String mID = reserveInfo.getString("MID");
    String subID = reserveInfo.getString("SUBID");
    String tID = reserveInfo.getString("TID");
    String sID = reserveInfo.getString("SID");
    String refMID = reserveInfo.getString("REFMID");
    String sMail = reserveInfo.getString("SMAIL");
    String sName = reserveInfo.getString("SNAME");
    String subject = reserveInfo.getString("SUBJECT");
    String charset = reserveInfo.getString("CHARSET");
    String[] attachFileList = new String[5];
    attachFileList[0] = reserveInfo.getString("ATTACHFILE01");
    attachFileList[1] = reserveInfo.getString("ATTACHFILE02");
    attachFileList[2] = reserveInfo.getString("ATTACHFILE03");
    attachFileList[3] = reserveInfo.getString("ATTACHFILE04");
    attachFileList[4] = reserveInfo.getString("ATTACHFILE05");

    String ctnPos = reserveInfo.getString("CTNPOS");
    String contents = reserveInfo.getString("CONTENTS");
    String rawContent = "";
    String rawContent_vest = "";
    
    String	enckey = reserveInfo.getString("ENCKEY");
    String	map1 = reserveInfo.getString("MAP1");
    String	map2 = reserveInfo.getString("MAP2");
    String	map3 = reserveInfo.getString("MAP3");
    String	map4 = reserveInfo.getString("MAP4");
    String	map5 = reserveInfo.getString("MAP5");
    String	map6 = reserveInfo.getString("MAP6");
    String	map7 = reserveInfo.getString("MAP7");
    String	map8 = reserveInfo.getString("MAP8");
    String	map9 = reserveInfo.getString("MAP9");
    String	map10 = reserveInfo.getString("MAP10");
    String	map11 = reserveInfo.getString("MAP11");
    String	map12 = reserveInfo.getString("MAP12");
    String	map13 = reserveInfo.getString("MAP13");
    String	map14 = reserveInfo.getString("MAP14");
    String	map15 = reserveInfo.getString("MAP15");
    String	map16 = reserveInfo.getString("MAP16");
    String	map17 = reserveInfo.getString("MAP17");
    String	map18 = reserveInfo.getString("MAP18");
    String	map19 = reserveInfo.getString("MAP19");
    String	map20 = reserveInfo.getString("MAP20");
    String	map21 = reserveInfo.getString("MAP21");
    String	map22 = reserveInfo.getString("MAP22");
    String	map23 = reserveInfo.getString("MAP23");
    String	map24 = reserveInfo.getString("MAP24");
    String	map25 = reserveInfo.getString("MAP25");
    String	map26 = reserveInfo.getString("MAP26");
    String	map27 = reserveInfo.getString("MAP27");
    String	map28 = reserveInfo.getString("MAP28");
    String	map29 = reserveInfo.getString("MAP29");
    String	map30 = reserveInfo.getString("MAP30");
    String	map31 = reserveInfo.getString("MAP31");
    String	map32 = reserveInfo.getString("MAP32");
    String	map33 = reserveInfo.getString("MAP33");
    String	map34 = reserveInfo.getString("MAP34");
    String	map35 = reserveInfo.getString("MAP35");
    String	map36 = reserveInfo.getString("MAP36");
    String	map37 = reserveInfo.getString("MAP37");
    String	map38 = reserveInfo.getString("MAP38");
    String	map39 = reserveInfo.getString("MAP39");
    String	map40 = reserveInfo.getString("MAP40");
    String	map41 = reserveInfo.getString("MAP41");
    String	map42 = reserveInfo.getString("MAP42");
    String	map43 = reserveInfo.getString("MAP43");
    String	map44 = reserveInfo.getString("MAP44");
    String	map45 = reserveInfo.getString("MAP45");
    String	map46 = reserveInfo.getString("MAP46");
    String	map47 = reserveInfo.getString("MAP47");
    String	map48 = reserveInfo.getString("MAP48");
    String	map49 = reserveInfo.getString("MAP49");
    String	map50 = reserveInfo.getString("MAP50");
    String	bizkey = reserveInfo.getString("BIZKEY");
    
    String	secu_att_yn = reserveInfo.getString("SECU_ATT_YN");
    String	source_url = reserveInfo.getString("SOURCE_URL");
    String	secu_att_typ = reserveInfo.getString("SECU_ATT_TYP");
    
    String	title_chk_yn = reserveInfo.getString("TITLE_CHK_YN");
    String	body_chk_yn = reserveInfo.getString("BODY_CHK_YN");
    String	attach_file_chk_yn = reserveInfo.getString("ATTACH_FILE_CHK_YN");
    String	secu_mail_chk_yn = reserveInfo.getString("SECU_MAIL_CHK_YN");
    
    String requestKey = reserveInfo.getString("REQUEST_KEY");

    StringTokenizer st1 = new StringTokenizer(mergyList, CNT_CODE_DELIM);
    int tokenCount = st1.countTokens();

    if (tokenCount != 0) {
      String tempContents = contents.toLowerCase();
      for (int i = 0; i < tokenCount; i++) {
		if (tempContents.indexOf("$:" + st1.nextToken().trim().toLowerCase() + ":$") != -1) {
          bMergeContents = true;
          break;
        }
      }
    }

    header.setSenderInfo(sName, sMail, sMail, charset);

    //4. 본문이 Html파일인지 아닌지 체크한다.
    boolean isHtml = false;

    if (bMergeContents == false) {
      //수신자에 대해서 처리하기전에  컨덴츠와 첨부파일을 가져온다.
      //1. 컨덴츠를 가져온다.(머지하기 전의 내용을 그대로 가져온다.)
      rawContent = content.getContent(mID, ctnPos, contents, charset);
      rawContent_vest = content.getContent(mID, ctnPos, source_url);
      isHtml = content.getIsHtml();
    }

    //2. 첨부파일을 가져온다.
    String attachContent = "";
    boolean isAttach = false;
    if (attachFileList[0] != null && !attachFileList[0].equals("")) {
      DebugTrace.print("첨부파일 읽기 시작...");
      attachContent = content.getAttachFile(mID, attachFileList, attach_file_chk_yn);
      DebugTrace.println("   완료!!!");
      isAttach = true;
    }
    //3. 머지인지 체크한후 머지내용들을 가져온다.
    boolean isMerge = false;

    /*************************************************
         ** 여기서 컨덴츠와 첨부파일이 제대로 가져왔는지 체크한후에 제대로 가져오지 않으면 컨덴츠 생성을 해주지 않으며 또한 에러처리를 해준다.**
     *************************************************/
    if (rawContent == null || attachContent == null) { //첨부파일이나 혹은 본문내용이 제대로 가져오지 못하면 컨덴츠 생성을 하지 않는다.
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
      errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_CONTENT);

      ErrorLogGenerator.setErrorLogFormat("EmailGenerator",
                                          ReserveStatusCode.CONTENTS_FAIL_TYPE,
                                          ReserveStatusCode.
                                          CONTENT_NOT_FOUND_COMMENT, mID);
      errorLogInfoList.add(errorLogInfo);

      reserveInfo.setString("STATUS", ReserveStatusCode.CONTENTS_FAIL);
      //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
      LogFileManager.setReserveStatusUpdate(mID, subID,
                                            ReserveStatusCode.CONTENTS_FAIL);

      //먼가 에러 로그가 쌓인것이 있으면 그 로그를 넣어준다.
      ResultLogManager.InsertResultLog(errorLogInfoList);
      return;
    }

    DataUnitInfoList rUserList = null;
    int fileSize = rUserFileList.length;
    for (int k = 0; k < fileSize; k++) {
      ArrayList mergyArray = new ArrayList();
      rUserList = TransferFileToDataGenerator.transferFileToDataUnit(
          rUserFileList[k], charset);

      String[] rIDList = rUserList.getStringArray("RID");
      String[] rNameList = rUserList.getStringArray("RNAME");
      String[] rMailList = rUserList.getStringArray("RMAIL");
      String[] rENCKEY = rUserList.getStringArray("ENCKEY");
      String[] rMAP1 = rUserList.getStringArray("MAP1");
      String[] rMAP2 = rUserList.getStringArray("MAP2");
      String[] rMAP3 = rUserList.getStringArray("MAP3");
      String[] rMAP4 = rUserList.getStringArray("MAP4");
      String[] rMAP5 = rUserList.getStringArray("MAP5");
      String[] rMAP6 = rUserList.getStringArray("MAP6");
      String[] rMAP7 = rUserList.getStringArray("MAP7");
      String[] rMAP8 = rUserList.getStringArray("MAP8");
      String[] rMAP9 = rUserList.getStringArray("MAP9");
      String[] rMAP10 = rUserList.getStringArray("MAP10");
      String[] rMAP11 = rUserList.getStringArray("MAP11");
      String[] rMAP12 = rUserList.getStringArray("MAP12");
      String[] rMAP13 = rUserList.getStringArray("MAP13");
      String[] rMAP14 = rUserList.getStringArray("MAP14");
      String[] rMAP15 = rUserList.getStringArray("MAP15");
      String[] rMAP16 = rUserList.getStringArray("MAP16");
      String[] rMAP17 = rUserList.getStringArray("MAP17");
      String[] rMAP18 = rUserList.getStringArray("MAP18");
      String[] rMAP19 = rUserList.getStringArray("MAP19");
      String[] rMAP20 = rUserList.getStringArray("MAP20");
      String[] rMAP21 = rUserList.getStringArray("MAP21");
      String[] rMAP22 = rUserList.getStringArray("MAP22");
      String[] rMAP23 = rUserList.getStringArray("MAP23");
      String[] rMAP24 = rUserList.getStringArray("MAP24");
      String[] rMAP25 = rUserList.getStringArray("MAP25");
      String[] rMAP26 = rUserList.getStringArray("MAP26");
      String[] rMAP27 = rUserList.getStringArray("MAP27");
      String[] rMAP28 = rUserList.getStringArray("MAP28");
      String[] rMAP29 = rUserList.getStringArray("MAP29");
      String[] rMAP30 = rUserList.getStringArray("MAP30");
      String[] rMAP31 = rUserList.getStringArray("MAP31");
      String[] rMAP32 = rUserList.getStringArray("MAP32");
      String[] rMAP33 = rUserList.getStringArray("MAP33");
      String[] rMAP34 = rUserList.getStringArray("MAP34");
      String[] rMAP35 = rUserList.getStringArray("MAP35");
      String[] rMAP36 = rUserList.getStringArray("MAP36");
      String[] rMAP37 = rUserList.getStringArray("MAP37");
      String[] rMAP38 = rUserList.getStringArray("MAP38");
      String[] rMAP39 = rUserList.getStringArray("MAP39");
      String[] rMAP40 = rUserList.getStringArray("MAP40");
      String[] rMAP41 = rUserList.getStringArray("MAP41");
      String[] rMAP42 = rUserList.getStringArray("MAP42");
      String[] rMAP43 = rUserList.getStringArray("MAP43");
      String[] rMAP44 = rUserList.getStringArray("MAP44");
      String[] rMAP45 = rUserList.getStringArray("MAP45");
      String[] rMAP46 = rUserList.getStringArray("MAP46");
      String[] rMAP47 = rUserList.getStringArray("MAP47");
      String[] rMAP48 = rUserList.getStringArray("MAP48");
      String[] rMAP49 = rUserList.getStringArray("MAP49");
      String[] rMAP50 = rUserList.getStringArray("MAP50");
      String[] rBIZKEY = rUserList.getStringArray("BIZKEY");
      
      //수신자의 수를 센다.
      int rUserNum = rNameList.length;

      if (mergyList != null && !mergyList.equals("")) { //머지메일이다.
        for (int i = 0; i < rUserNum; i++) {
          StringTokenizer st = new StringTokenizer(mergyList, CNT_CODE_DELIM);
          String tempKey;
          String tempValue;
          Hashtable mergyHash = new Hashtable();

          while (st.hasMoreTokens()) {
            tempKey = st.nextToken();
            //mergeKey.add(tempKey);
            tempValue = rUserList.getStringIndex(i, tempKey.toUpperCase());
            //mergeValue.add(tempValue);
            mergyHash.put(tempKey, tempValue);
          }
          mergyArray.add(mergyHash);
        }

        isMerge = true;
      }

      //여기서 부터 컨덴츠를 생성해주는 부분
      try {
        for (int i = 0; i < rUserNum; i++) {
        	
        	//보안메일 첨부 URL 미리 치환2
        	source_url = content.getTransferMerge(source_url, (Hashtable) mergyArray.get(i));
        	rawContent_vest = content.getTransferMerge(source_url, (Hashtable) mergyArray.get(i));
        	
        	
          if ( (legacyRSListSize * k + i) < preMakeNum) {
            continue;
          }

          String headerStr = header.generateHeader(subject, rNameList[i],
              rMailList[i],
              isAttach, isHtml, (Hashtable) mergyArray.get(i),ctnPos, secu_att_yn, secu_att_typ, title_chk_yn, body_chk_yn, attach_file_chk_yn, secu_mail_chk_yn);
          String contentStr;
          String raw_receiveStr = "";

          if (bMergeContents) {
            rawContent = content.getContent2(mID, ctnPos, contents,
                                             (Hashtable) mergyArray.get(i),
                                             charset,"CONTETNT");
            rawContent_vest = content_vest.getContent2(mID, ctnPos, source_url,(Hashtable)mergyArray.get(i),charset,"CONTETNT_VEST");
            isHtml = content.getIsHtml();
          }

          sb = new StringBuffer();
          if (isHtml) { //본문내용이 html이면
            //수신모듈을 붙여준다.
            //수신확인 호스트정보

            //수신확인테이블의 Unique필드를 만들어주기 위해서
            raw_receiveStr = (sb.append(rawContent.trim())
                              .append("\r\n")
                              .append("<img src=\"")
                              .append(receiveDefineHost).append("?rsID=")
                              .append(mID).append(i).append("&mID=").append(mID)
                              //.append("&rName=").append(rNameList[i])
                              .append("&rName=").append(URLEncoder.encode(rNameList[i],"UTF-8"))
                              .append("&rMail=").append(rMailList[i])
                              .append("&rID=").append(rIDList[i])
                              .append("&refMID=").append(refMID)
                              .append("&subID=").append(subID)
                              .append("\" width=0 height=0 border=0>")).
                toString();
          }
          else {
            raw_receiveStr = (sb.append(HTMLTrans.transeHtml(rawContent).trim())
                              .append("\r\n")
                              .append("\r\n<img src=\"")
                              .append(receiveDefineHost).append("?rsID=")
                              .append(mID).append(i).append("&mID=").append(mID)
                              //.append("&rName=").append(rNameList[i])
                              .append("&rName=").append(URLEncoder.encode(rNameList[i],"UTF-8"))
                              .append("&rMail=").append(rMailList[i])
                              .append("&rID=").append(rIDList[i])
                              .append("&refMID=").append(refMID)
                              .append("&subID=").append(subID)
                              .append("\" width=0 height=0 border=0>")).
                toString();
          }

          sb = null;

          DebugTrace.print("컨텐트 생성...");

          //머지메일인지 아닌지 체크한다.
          if (isMerge) { //머지메일이다.
			contentStr = content.generateContent(mID, raw_receiveStr, ctnPos,
                                                 attachContent, isHtml,
                                                 (Hashtable) mergyArray.get(i),rawContent_vest, secu_att_yn, rENCKEY[i], secu_att_typ);
          }
          else { // 머지메일이 아니다.(동보성메일이다)
            contentStr = content.generateContent(mID, raw_receiveStr, ctnPos,
                                                 attachContent, isHtml);
          }

          //개인정보 체크로 headerStr도 체크
          if (contentStr == null || headerStr ==null) {
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
            errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_CONTENT);

            ErrorLogGenerator.setErrorLogFormat("EmailGenerator",
                                                ReserveStatusCode.
                                                CONTENTS_FAIL_TYPE,
                                                ReserveStatusCode.
                                                CONTENT_NOT_FOUND_COMMENT, mID);
            errorLogInfoList.add(errorLogInfo);

            reserveInfo.setString("STATUS", ReserveStatusCode.CONTENTS_FAIL);
            //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
            LogFileManager.setReserveStatusUpdate(mID, subID,
                                                  ReserveStatusCode.CONTENTS_FAIL);

            //먼가 에러 로그가 쌓인것이 있으면 그 로그를 넣어준다.
            ResultLogManager.InsertResultLog(errorLogInfoList);
            continue;
          }

          DebugTrace.println("   완료!!!"); 

          //완전한 본문 만들기
          String email = headerStr + contentStr;

          //*************************************************
           //NeoSMTP에서 필요한 여러 정보를 넣어준다.
           //*************************************************
          Hashtable smtpSendInfo = new Hashtable();

          smtpSendInfo.put("SMAIL", sMail);
          smtpSendInfo.put("SUBID", subID);
          smtpSendInfo.put("RMAIL", rMailList[i]);
          smtpSendInfo.put("SID", sID);
          smtpSendInfo.put("SNAME", sName);
          smtpSendInfo.put("RID", rIDList[i]);
          smtpSendInfo.put("RNAME", rNameList[i]);
          smtpSendInfo.put("TID", tID);
          smtpSendInfo.put("REFMID", refMID);
          smtpSendInfo.put("REQUEST_KEY", requestKey);

          String smtpInfo = SMTPSendInfoGenerator.generateSMTPSendInfo(
              smtpSendInfo);
          
          //SMTP 전송정보+ 이메일정보
          String sendEmail = smtpInfo + email;

          //MID폴더를 만든후에 이메일을 그 폴더의 하위 폴더에 500개 단위로 해서 1,2,3 번호를 달아준후에 만들어진 이메일을 넣어준다.(갯수는 프로퍼티로뺀다)
          //이제 이 만들어진 파일을 해당폴더에 만들어 넣어준다.

          boolean finalNumber = false;
          if (i == rUserNum - 1) {
            finalNumber = true;
          }

          if (EmailFileManager.generateEmailFile(mID, subID,
                                                 legacyRSListSize * k + i,
                                                 sendEmail, finalNumber)) {
            //파일 생성에 성공하면
            DebugTrace.println("파일 생성 성공");
          }
          else {
            DebugTrace.println("파일 생성 실패");

            //파일 생성에 실패하면 그 실패한 컨덴츠는 ResultLog에 쌓아준다.
            //에러가 발생하면 그 다음 리스트들은 전부 생성 에러로 넘겨준다.
            errorLogInfo = new Hashtable();
            errorLogInfo.put("MID", mID);
            errorLogInfo.put("SUBID", subID);
            errorLogInfo.put("TID", tID);
            errorLogInfo.put("SID", sID);
            errorLogInfo.put("SNAME", sName);
            errorLogInfo.put("SMAIL", sMail);
            errorLogInfo.put("REFMID", refMID);
            errorLogInfo.put("RID", rIDList[i]);
            errorLogInfo.put("RNAME", rNameList[i]);
            errorLogInfo.put("RMAIL", rMailList[i]);
            errorLogInfo.put("RCODE", ErrorStatusCode.ECODE_CONTENT);

            errorLogInfoList.add(errorLogInfo);
          }
        }

        DebugTrace.println("에러 로그를 삽입합니다.");
        //먼가 에러 로그가 쌓인것이 있으면 그 로그를 넣어준다.
        if (errorLogInfoList.size() != 0) {
          ResultLogManager.InsertResultLog(errorLogInfoList);
        }

        //컨덴츠 생성 성공했을때는 성공에 대한 상태를 넣어준다.
        reserveInfo.setString("STATUS", ReserveStatusCode.CONTENTS_SUCCESS);
        //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
        LogFileManager.setReserveStatusUpdate(mID, subID,
                                              ReserveStatusCode.CONTENTS_SUCCESS);
        DebugTrace.println("작업 완료...");
      }
      catch (Exception e) {
        e.printStackTrace();

        //컨덴츠 생성 실패했을때는 실패에 대한 상태를 넣어준다.
        reserveInfo.setString("STATUS", ReserveStatusCode.CONTENTS_FAIL);
        //복구를 감안해서 예약리스트 Status를 파일로 저장한다.
        LogFileManager.setReserveStatusUpdate(mID, subID,
                                              ReserveStatusCode.CONTENTS_FAIL);

        //에러 로그를 남겨준다.
        ErrorLogGenerator.setErrorLogFormat("EmailGenerator",
                                            ReserveStatusCode.CONTENTS_FAIL_TYPE,
                                            ReserveStatusCode.
                                            CONTENTS_WRITE_FAIL_COMMENT, mID);
      }
    }
  }
}