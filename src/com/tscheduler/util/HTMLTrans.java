package com.tscheduler.util;
import java.util.StringTokenizer;
import java.io.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 메일 본문 내용이 텍스트일 경우 html로 변환해준다.
 * @version 1.0
 * @author ymkim
 */
public class HTMLTrans
{
	
	private static final Logger LOGGER = LogManager.getLogger(HTMLTrans.class.getName());
	
	/**
	 * 메일 본문에 들어있는 머지변수들을 해당하는 값으로 대체해준다.
	 * @version 1.0
	 * @author ymkim
	 * @param tempContent 머지시킬 본문 내용
	 * @param mergeHash 머지값들
	 * @return String 머지한 본문 내용
	 */
	public static String transeHtml(String tempContent)
	{
		StringBuffer sb = new StringBuffer();

		StringTokenizer st = new StringTokenizer(tempContent, System.getProperty("line.separator"));

		while( st.hasMoreTokens() )
		{
			sb.append(st.nextToken());
			sb.append("<br>");
			sb.append(System.getProperty("line.separator"));
		}
		LOGGER.info(sb.toString());
		return sb.toString();
	}

//	public static void main(String[] args)
//	{
//		StringBuffer sb = new StringBuffer();
//		BufferedReader br = null;
//
//		String return_value=null;
//
//		try
//		{
//			br = new BufferedReader(new FileReader(new File("d:"+File.separator+"추천게임.txt")));
//
//			String readContent;
//
//			while((readContent=br.readLine()) !=null)
//			{
//				sb.append(readContent).append(System.getProperty("line.separator"));
//			}
//
//			return_value = sb.toString();
//			LOGGER.info(return_value);
//			LOGGER.info("변환후:"+transeHtml(return_value));
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
}