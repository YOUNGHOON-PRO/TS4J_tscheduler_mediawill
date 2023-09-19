package com.tscheduler.util;

import java.util.*;

/**
 * 메일 본문 내용 머지 변수들을 머지값으로 변환시켜주는 클래스
 * @version 1.0
 * @author ymkim
 */
public class MergeTrans {
  /**머지 구분자*/
  //private static final String MERGY_DELIMITER = "%";

  /**
   * 메일 본문에 들어있는 머지변수들을 해당하는 값으로 대체해준다.
   * @version 1.0
   * @author ymkim
   * @param tempContent 머지시킬 본문 내용
   * @param mergeHash 머지값들
   * @return String 머지한 본문 내용
   */
  public static String replaceMerge(String tempContent, Hashtable mergeHash) {
/*
    StringBuffer dt = new StringBuffer();
    dt.append(tempContent).append("\r\n");
    dt.append("-->").append("\r\n");
*/
    Enumeration keyEnum = mergeHash.keys();
    Enumeration valueEmum = mergeHash.elements();
    StringBuffer sb = null;

    String returnString = "";

    while (keyEnum.hasMoreElements()) {
      sb = new StringBuffer();
      String mergeKey = sb.append("$:").append( (String) keyEnum.
          nextElement())
          .append(":$").toString();
      sb = null;
      String mergeValue = (String) valueEmum.nextElement();

      int keyLength = mergeKey.length(); //머지키 문자열의 길이
      int fullContentSize = tempContent.length(); //전체 문자열 길이

      int indexPos = 0;
      int nextPos = 0;

      sb = new StringBuffer();

      while (true) {
        String tempStr = "";
        nextPos = tempContent.indexOf(mergeKey, indexPos);

        if (nextPos < 0) {
          sb.append(tempContent.substring(indexPos, fullContentSize));
          tempContent = sb.toString();
          break;
        }
        else {
          tempStr = tempContent.substring(indexPos, nextPos);
          sb.append(tempStr).append(mergeValue);
        }

        indexPos = nextPos + keyLength;
      }

      sb = null;
    }
    /*
    dt.append(tempContent).append("\r\n");
    LogUtil.logCurFile(dt.toString());
        */
    return tempContent;
  }
}