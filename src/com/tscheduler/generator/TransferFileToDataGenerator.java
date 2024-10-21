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
			String map16="";
			String map17="";
			String map18="";
			String map19="";
			String map20="";
			String map21="";
			String map22="";
			String map23="";
			String map24="";
			String map25="";
			String map26="";
			String map27="";
			String map28="";
			String map29="";
			String map30="";
			String map31="";
			String map32="";
			String map33="";
			String map34="";
			String map35="";
			String map36="";
			String map37="";
			String map38="";
			String map39="";
			String map40="";
			String map41="";
			String map42="";
			String map43="";
			String map44="";
			String map45="";
			String map46="";
			String map47="";
			String map48="";
			String map49="";
			String map50="";

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
						map16 = st.nextToken();
						map17 = st.nextToken();
						map18 = st.nextToken();
						map19 = st.nextToken();
						map20 = st.nextToken();
						map21 = st.nextToken();
						map22 = st.nextToken();
						map23 = st.nextToken();
						map24 = st.nextToken();
						map25 = st.nextToken();
						map26 = st.nextToken();
						map27 = st.nextToken();
						map28 = st.nextToken();
						map29 = st.nextToken();
						map30 = st.nextToken();
						map31 = st.nextToken();
						map32 = st.nextToken();
						map33 = st.nextToken();
						map34 = st.nextToken();
						map35 = st.nextToken();
						map36 = st.nextToken();
						map37 = st.nextToken();
						map38 = st.nextToken();
						map39 = st.nextToken();
						map40 = st.nextToken();
						map41 = st.nextToken();
						map42 = st.nextToken();
						map43 = st.nextToken();
						map44 = st.nextToken();
						map45 = st.nextToken();
						map46 = st.nextToken();
						map47 = st.nextToken();
						map48 = st.nextToken();
						map49 = st.nextToken();
						map50 = st.nextToken();

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
					rUserInfo.setString("MAP16",map16);
					rUserInfo.setString("MAP17",map17);
					rUserInfo.setString("MAP18",map18);
					rUserInfo.setString("MAP19",map19);
					rUserInfo.setString("MAP20",map20);
					rUserInfo.setString("MAP21",map21);
					rUserInfo.setString("MAP22",map22);
					rUserInfo.setString("MAP23",map23);
					rUserInfo.setString("MAP24",map24);
					rUserInfo.setString("MAP25",map25);
					rUserInfo.setString("MAP26",map26);
					rUserInfo.setString("MAP27",map27);
					rUserInfo.setString("MAP28",map28);
					rUserInfo.setString("MAP29",map29);
					rUserInfo.setString("MAP30",map30);
					rUserInfo.setString("MAP31",map31);
					rUserInfo.setString("MAP32",map32);
					rUserInfo.setString("MAP33",map33);
					rUserInfo.setString("MAP34",map34);
					rUserInfo.setString("MAP35",map35);
					rUserInfo.setString("MAP36",map36);
					rUserInfo.setString("MAP37",map37);
					rUserInfo.setString("MAP38",map38);
					rUserInfo.setString("MAP39",map39);
					rUserInfo.setString("MAP40",map40);
					rUserInfo.setString("MAP41",map41);
					rUserInfo.setString("MAP42",map42);
					rUserInfo.setString("MAP43",map43);
					rUserInfo.setString("MAP44",map44);
					rUserInfo.setString("MAP45",map45);
					rUserInfo.setString("MAP46",map46);
					rUserInfo.setString("MAP47",map47);
					rUserInfo.setString("MAP48",map48);
					rUserInfo.setString("MAP49",map49);
					rUserInfo.setString("MAP50",map50);

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
            			String map16="";      
            			String map17="";      
            			String map18="";      
            			String map19="";      
            			String map20="";      
            			String map21="";      
            			String map22="";      
            			String map23="";      
            			String map24="";      
            			String map25="";      
            			String map26="";      
            			String map27="";      
            			String map28="";      
            			String map29="";      
            			String map30="";      
            			String map31="";      
            			String map32="";      
            			String map33="";      
            			String map34="";      
            			String map35="";      
            			String map36="";      
            			String map37="";      
            			String map38="";      
            			String map39="";      
            			String map40="";      
            			String map41="";      
            			String map42="";      
            			String map43="";      
            			String map44="";      
            			String map45="";      
            			String map46="";      
            			String map47="";      
            			String map48="";      
            			String map49="";      
            			String map50="";      

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
                        						map16 = st.nextToken();
                        						map17 = st.nextToken();
                        						map18 = st.nextToken();
                        						map19 = st.nextToken();
                        						map20 = st.nextToken();
                        						map21 = st.nextToken();
                        						map22 = st.nextToken();
                        						map23 = st.nextToken();
                        						map24 = st.nextToken();
                        						map25 = st.nextToken();
                        						map26 = st.nextToken();
                        						map27 = st.nextToken();
                        						map28 = st.nextToken();
                        						map29 = st.nextToken();
                        						map30 = st.nextToken();
                        						map31 = st.nextToken();
                        						map32 = st.nextToken();
                        						map33 = st.nextToken();
                        						map34 = st.nextToken();
                        						map35 = st.nextToken();
                        						map36 = st.nextToken();
                        						map37 = st.nextToken();
                        						map38 = st.nextToken();
                        						map39 = st.nextToken();
                        						map40 = st.nextToken();
                        						map41 = st.nextToken();
                        						map42 = st.nextToken();
                        						map43 = st.nextToken();
                        						map44 = st.nextToken();
                        						map45 = st.nextToken();
                        						map46 = st.nextToken();
                        						map47 = st.nextToken();
                        						map48 = st.nextToken();
                        						map49 = st.nextToken();
                        						map50 = st.nextToken();
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
                                        
                                        if(enckey.equals("flage")){	rUserInfo.setString("ENCKEY",""); }else{ rUserInfo.setString("ENCKEY",enckey); }
                                        if(map1.equals("flage")){ rUserInfo.setString("MAP1",""); }else{ rUserInfo.setString("MAP1",map1); }
                                        if(map2.equals("flage")){ rUserInfo.setString("MAP2",""); }else{ rUserInfo.setString("MAP2",map2); }
                                        if(map3.equals("flage")){ rUserInfo.setString("MAP3",""); }else{ rUserInfo.setString("MAP3",map3); }
                                        if(map4.equals("flage")){ rUserInfo.setString("MAP4",""); }else{ rUserInfo.setString("MAP4",map4); }
                                        if(map5.equals("flage")){ rUserInfo.setString("MAP5",""); }else{ rUserInfo.setString("MAP5",map5); }
                                        if(map6.equals("flage")){ rUserInfo.setString("MAP6",""); }else{ rUserInfo.setString("MAP6",map6); }
                                        if(map7.equals("flage")){ rUserInfo.setString("MAP7",""); }else{ rUserInfo.setString("MAP7",map7); }
                                        if(map8.equals("flage")){ rUserInfo.setString("MAP8",""); }else{ rUserInfo.setString("MAP8",map8); }
                                        if(map9.equals("flage")){ rUserInfo.setString("MAP9",""); }else{ rUserInfo.setString("MAP9",map9); }
                                        if(map10.equals("flage")){ rUserInfo.setString("MAP10",""); }else{ rUserInfo.setString("MAP10",map10); }
										if(map11.equals("flage")){ rUserInfo.setString("MAP11",""); }else{ rUserInfo.setString("MAP11",map11); }
										if(map12.equals("flage")){ rUserInfo.setString("MAP12",""); }else{ rUserInfo.setString("MAP12",map12); }
										if(map13.equals("flage")){ rUserInfo.setString("MAP13",""); }else{ rUserInfo.setString("MAP13",map13); }
										if(map14.equals("flage")){ rUserInfo.setString("MAP14",""); }else{ rUserInfo.setString("MAP14",map14); }
										if(map15.equals("flage")){ rUserInfo.setString("MAP15",""); }else{ rUserInfo.setString("MAP15",map15); }
										if(map16.equals("flage")){ rUserInfo.setString("MAP16",""); }else{ rUserInfo.setString("MAP16",map16); }
										if(map17.equals("flage")){ rUserInfo.setString("MAP17",""); }else{ rUserInfo.setString("MAP17",map17); }
										if(map18.equals("flage")){ rUserInfo.setString("MAP18",""); }else{ rUserInfo.setString("MAP18",map18); }
										if(map19.equals("flage")){ rUserInfo.setString("MAP19",""); }else{ rUserInfo.setString("MAP19",map19); }
                                        if(map20.equals("flage")){ rUserInfo.setString("MAP20",""); }else{ rUserInfo.setString("MAP20",map20); }
                                        if(map21.equals("flage")){ rUserInfo.setString("MAP21",""); }else{ rUserInfo.setString("MAP21",map21); }
                                        if(map22.equals("flage")){ rUserInfo.setString("MAP22",""); }else{ rUserInfo.setString("MAP22",map22); }
                                        if(map23.equals("flage")){ rUserInfo.setString("MAP23",""); }else{ rUserInfo.setString("MAP23",map23); }
                                        if(map24.equals("flage")){ rUserInfo.setString("MAP24",""); }else{ rUserInfo.setString("MAP24",map24); }
                                        if(map25.equals("flage")){ rUserInfo.setString("MAP25",""); }else{ rUserInfo.setString("MAP25",map25); }
                                        if(map26.equals("flage")){ rUserInfo.setString("MAP26",""); }else{ rUserInfo.setString("MAP26",map26); }
                                        if(map27.equals("flage")){ rUserInfo.setString("MAP27",""); }else{ rUserInfo.setString("MAP27",map27); }
                                        if(map28.equals("flage")){ rUserInfo.setString("MAP28",""); }else{ rUserInfo.setString("MAP28",map28); }
                                        if(map29.equals("flage")){ rUserInfo.setString("MAP29",""); }else{ rUserInfo.setString("MAP29",map29); }
                                        if(map30.equals("flage")){ rUserInfo.setString("MAP30",""); }else{ rUserInfo.setString("MAP30",map30); }
                                        if(map31.equals("flage")){ rUserInfo.setString("MAP31",""); }else{ rUserInfo.setString("MAP31",map31); }
                                        if(map32.equals("flage")){ rUserInfo.setString("MAP32",""); }else{ rUserInfo.setString("MAP32",map32); }
                                        if(map33.equals("flage")){ rUserInfo.setString("MAP33",""); }else{ rUserInfo.setString("MAP33",map33); }
                                        if(map34.equals("flage")){ rUserInfo.setString("MAP34",""); }else{ rUserInfo.setString("MAP34",map34); }
                                        if(map35.equals("flage")){ rUserInfo.setString("MAP35",""); }else{ rUserInfo.setString("MAP35",map35); }
                                        if(map36.equals("flage")){ rUserInfo.setString("MAP36",""); }else{ rUserInfo.setString("MAP36",map36); }
                                        if(map37.equals("flage")){ rUserInfo.setString("MAP37",""); }else{ rUserInfo.setString("MAP37",map37); }
                                        if(map38.equals("flage")){ rUserInfo.setString("MAP38",""); }else{ rUserInfo.setString("MAP38",map38); }
                                        if(map39.equals("flage")){ rUserInfo.setString("MAP39",""); }else{ rUserInfo.setString("MAP39",map39); }
                                        if(map40.equals("flage")){ rUserInfo.setString("MAP40",""); }else{ rUserInfo.setString("MAP40",map40); }
                                        if(map41.equals("flage")){ rUserInfo.setString("MAP41",""); }else{ rUserInfo.setString("MAP41",map41); }
                                        if(map42.equals("flage")){ rUserInfo.setString("MAP42",""); }else{ rUserInfo.setString("MAP42",map42); }
                                        if(map43.equals("flage")){ rUserInfo.setString("MAP43",""); }else{ rUserInfo.setString("MAP43",map43); }
                                        if(map44.equals("flage")){ rUserInfo.setString("MAP44",""); }else{ rUserInfo.setString("MAP44",map44); }
                                        if(map45.equals("flage")){ rUserInfo.setString("MAP45",""); }else{ rUserInfo.setString("MAP45",map45); }
                                        if(map46.equals("flage")){ rUserInfo.setString("MAP46",""); }else{ rUserInfo.setString("MAP46",map46); }
                                        if(map47.equals("flage")){ rUserInfo.setString("MAP47",""); }else{ rUserInfo.setString("MAP47",map47); }
                                        if(map48.equals("flage")){ rUserInfo.setString("MAP48",""); }else{ rUserInfo.setString("MAP48",map48); }
                                        if(map49.equals("flage")){ rUserInfo.setString("MAP49",""); }else{ rUserInfo.setString("MAP49",map49); }
                                        if(map50.equals("flage")){ rUserInfo.setString("MAP50",""); }else{ rUserInfo.setString("MAP50",map50); }

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