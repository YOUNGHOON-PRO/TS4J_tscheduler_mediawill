package com.tscheduler.generator;

import java.util.*;
import java.io.*;

import com.tscheduler.util.DataUnitInfo;
import com.tscheduler.util.DataUnitInfoList;
import com.tscheduler.util.EncryptUtil;
import com.tscheduler.util.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 수신자 리스트 파일을 DataUnitInfoList객체로 변환하는 클래스
 * @version 1.0
 * @author ymkim
 */
public class TransferFileToDataGenerator
{ 
	private static final Logger LOGGER = LogManager.getLogger(TransferFileToDataGenerator.class.getName());

	/**
	 * 파일을 받아들여서 그것을 DataUnitInfoList로 만들어준다.(다국어)
	 * @version 1.0
	 * @author ymkim
	 * @param rUserFile 수신자 리스트 파일
	 * @return DataUnitInfoList 수신자 리스트 객체
	 */
	public static DataUnitInfoList transferFileToDataUnit(File rUserFile, String charset)
	{
		DataUnitInfo rUserInfo = null;
		DataUnitInfoList rUserList = new DataUnitInfoList();

		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(rUserFile));
			String tempStr="";

			String rName="";
			String rID="";
			String rMail="";
			String enckey="";
			String map1="";
			String map2="";
			String map3="";
			String map4="";
			String map5="";
			String map6="";
			String map7="";
			String map8="";
			String map9="";
			String map10="";
			String map11="";
			String map12="";
			String map13="";
			String map14="";
			String map15="";
			
			Hashtable errorLogInfo = null;

			while((tempStr=br.readLine())!=null)
			{
				rUserInfo = new DataUnitInfo();
				StringTokenizer st = new StringTokenizer(tempStr, Config.DELIMITER);
				if(st.hasMoreTokens())
				{
					try
					{
						rMail = st.nextToken();
						rName = st.nextToken();
						rID = st.nextToken();
						enckey = st.nextToken();
						map1 = st.nextToken();
						map2 = st.nextToken();
						map3 = st.nextToken();
						map4 = st.nextToken();
						map5 = st.nextToken();
						map6 = st.nextToken();
						map7 = st.nextToken();
						map8 = st.nextToken();
						map9 = st.nextToken();
						map10 = st.nextToken();
						map11 = st.nextToken();
						map12 = st.nextToken();
						map13 = st.nextToken();
						map14 = st.nextToken();
						map15 = st.nextToken();
					}
					catch(NoSuchElementException exp)
					{
						LOGGER.error(exp);
						rUserList = null;
						break;
					}

//					if(rID.equals("null"))
//					{
//						rID = null;
//					}
//					if(rName.equals("null"))
//					{
//						rName = null;
//					}

					rUserInfo.setString("RID",rID);
					rUserInfo.setString("RNAME",rName);
					rUserInfo.setString("HRNAME",new String(rName.getBytes(),charset));
					rUserInfo.setString("RMAIL",rMail);
					rUserInfo.setString("ENCKEY",enckey);
					rUserInfo.setString("MAP1",map1);
					rUserInfo.setString("MAP2",map2);
					rUserInfo.setString("MAP3",map3);
					rUserInfo.setString("MAP4",map4);
					rUserInfo.setString("MAP5",map5);
					rUserInfo.setString("MAP6",map6);
					rUserInfo.setString("MAP7",map7);
					rUserInfo.setString("MAP8",map8);
					rUserInfo.setString("MAP9",map9);
					rUserInfo.setString("MAP10",map10);
					rUserInfo.setString("MAP11",map11);
					rUserInfo.setString("MAP12",map12);
					rUserInfo.setString("MAP13",map13);
					rUserInfo.setString("MAP14",map14);
					rUserInfo.setString("MAP15",map15);
					
					rUserList.addDataUnitInfo(rUserInfo);
				}
			}
		}
		catch(Exception e)
		{
			LOGGER.error(e);
			//e.printStackTrace();
			rUserList = null;
		}
		finally
		{
			try
			{
				if( br != null ) {
					br.close();
					br = null;
				}
			}
			catch(Exception e) {
				LOGGER.error(e);
			}
		}
		return rUserList;
	}

        /**
         * 파일을 받아들여서 그것을 DataUnitInfoList로 만들어준다.
         * @version 1.0
         * @author ymkim
         * @param rUserFile 수신자 리스트 파일
         * @return DataUnitInfoList 수신자 리스트 객체
         */
        public static DataUnitInfoList transferFileToDataUnit(File rUserFile)
        {
                DataUnitInfo rUserInfo = null;
                DataUnitInfoList rUserList = new DataUnitInfoList();

                BufferedReader br = null;
                
                try
                {
                        br = new BufferedReader(new FileReader(rUserFile));
                        String tempStr="";

                        String rName="";
                        String rID="";
                        String rMail="";
                        String enckey="";
            			String map1="";
            			String map2="";
            			String map3="";
            			String map4="";                        
            			String map5="";      
            			String map6="";
            			String map7="";
            			String map8="";
            			String map9="";                        
            			String map10="";      
            			String map11="";
            			String map12="";
            			String map13="";
            			String map14="";                        
            			String map15="";      

                        Hashtable errorLogInfo = null;

                        while((tempStr=br.readLine())!=null)
                        {
                                rUserInfo = new DataUnitInfo();
                                StringTokenizer st = new StringTokenizer(tempStr, Config.DELIMITER);
                                if(st.hasMoreTokens())
                                {
                                        try
                                        {
                                                rMail = st.nextToken();
                                                rName = st.nextToken();
                                                rID = st.nextToken();
                                                enckey = st.nextToken();
                        						map1 = st.nextToken();
                        						map2 = st.nextToken();
                        						map3 = st.nextToken();
                        						map4 = st.nextToken();
                        						map5 = st.nextToken();
                        						map6 = st.nextToken();
                        						map7 = st.nextToken();
                        						map8 = st.nextToken();
                        						map9 = st.nextToken();
                        						map10 = st.nextToken();
                        						map11 = st.nextToken();
                        						map12 = st.nextToken();
                        						map13 = st.nextToken();
                        						map14 = st.nextToken();
                        						map15 = st.nextToken();
                        						
                                        }
                                        catch(NoSuchElementException exp)
                                        {
                                        	LOGGER.error(exp);
                                                rUserList = null;
                                                break;
                                        }

//					if(rID.equals("null"))
//					{
//						rID = null;
//					}
//					if(rName.equals("null"))
//					{
//						rName = null;
//					}

                                        rUserInfo.setString("RID",rID);
                                        rUserInfo.setString("RNAME",rName);
                                        rUserInfo.setString("HRNAME",rName);
                                        rUserInfo.setString("RMAIL",rMail);
                                        
                                        if(enckey.equals("flage")){
                                        	rUserInfo.setString("ENCKEY","");
                                        }else{
                                        	rUserInfo.setString("ENCKEY",enckey);
                                        }
                                        if(map1.equals("flage")){
                                        	rUserInfo.setString("MAP1","");
                                        }else{
                                        	rUserInfo.setString("MAP1",map1);
                                        }
                                        if(map2.equals("flage")){
                                        	rUserInfo.setString("MAP2","");
                                        }else{
                                        	rUserInfo.setString("MAP2",map2);
                                        }
                                        if(map3.equals("flage")){
                                        	rUserInfo.setString("MAP3","");
                                        }else{
                                        	rUserInfo.setString("MAP3",map3);
                                        }
                                        if(map4.equals("flage")){
                                        	rUserInfo.setString("MAP4","");
                                        }else{
                                        	rUserInfo.setString("MAP4",map4);
                                        }
                                        if(map5.equals("flage")){
                                        	rUserInfo.setString("MAP5","");
                                        }else{
                                        	rUserInfo.setString("MAP5",map5);
                                        }
                                        if(map6.equals("flage")){
                                        	rUserInfo.setString("MAP6","");
                                        }else{
                                        	rUserInfo.setString("MAP6",map6);
                                        }
                                        if(map7.equals("flage")){
                                        	rUserInfo.setString("MAP7","");
                                        }else{
                                        	rUserInfo.setString("MAP7",map7);
                                        }
                                        if(map8.equals("flage")){
                                        	rUserInfo.setString("MAP8","");
                                        }else{
                                        	rUserInfo.setString("MAP8",map8);
                                        }
                                        if(map9.equals("flage")){
                                        	rUserInfo.setString("MAP9","");
                                        }else{
                                        	rUserInfo.setString("MAP9",map9);
                                        }
                                        if(map10.equals("flage")){
                                        	rUserInfo.setString("MAP10","");
                                        }else{
                                        	rUserInfo.setString("MAP10",map10);
                                        }
                                        if(map11.equals("flage")){
                                        	rUserInfo.setString("MAP11","");
                                        }else{
                                        	rUserInfo.setString("MAP11",map11);
                                        }
                                        if(map12.equals("flage")){
                                        	rUserInfo.setString("MAP12","");
                                        }else{
                                        	rUserInfo.setString("MAP12",map12);
                                        }
                                        if(map13.equals("flage")){
                                        	rUserInfo.setString("MAP13","");
                                        }else{
                                        	rUserInfo.setString("MAP13",map13);
                                        }
                                        if(map14.equals("flage")){
                                        	rUserInfo.setString("MAP14","");
                                        }else{
                                        	rUserInfo.setString("MAP14",map14);
                                        }
                                        if(map15.equals("flage")){
                                        	rUserInfo.setString("MAP15","");
                                        }else{
                                        	rUserInfo.setString("MAP15",map15);
                                        }

                                        rUserList.addDataUnitInfo(rUserInfo);
                                }
                        }
                }
                catch(Exception e)
                {
                	LOGGER.error(e);
                        //e.printStackTrace();
                        rUserList = null;
                }
                finally
                {
                        try
                        {
                                if( br != null ) {
                                        br.close();
                                        br = null;
                                }
                        }
                        catch(Exception e) {
                        	LOGGER.error(e);
                        }
                }
                return rUserList;
        }

}