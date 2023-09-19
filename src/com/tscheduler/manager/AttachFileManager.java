package com.tscheduler.manager;

import java.io.*;
import java.util.*;

import com.tscheduler.util.DataUnitInfo;
import com.tscheduler.util.DataUnitInfoList;
import com.tscheduler.util.Config;

/**
 * 첨부파일 관리 클래스
 * @version 1.0
 * @author ymkim
 */
public class AttachFileManager
{

	/**MID에 대한 인코딩된 첨부파일이 있는 폴더명*/
	public static String ATTACH_ENC_FOLDER = "TempStorage";

	/**
	 * 인코딩한 첨부파일 폴더를 지워준다.
	 * @version 1.0
	 * @author ymkim
	 * @return boolean true - 첨부파일을 잘 지웠다, false - 첨부파일이 삭제가 실패하였다.
	 */
	public static boolean deleteEncAttach()
	{
		try
		{
			//String attachEncFolderStr = Config.ROOT_DIR+File.separator+LogFileManager.LOG_FOLDER+File.separator+ATTACH_ENC_FOLDER+File.separator;
			String attachEncFolderStr = Config.ROOT_DIR+File.separator+File.separator+ATTACH_ENC_FOLDER+File.separator;

			File attachEncRootFolder = new File(attachEncFolderStr);
			File[] attachMIDFolderList = attachEncRootFolder.listFiles();

			if(attachMIDFolderList == null) return false;

			for(int i=0; i<attachMIDFolderList.length; i++)
			{
				if(attachMIDFolderList[i].isDirectory())
				{
				File[] delMIDSubFile = attachMIDFolderList[i].listFiles();
				for(int k=0; k<delMIDSubFile.length ;k++)
				{
					delMIDSubFile[k].delete();	//파일을 지운다.
				}
			}
			attachMIDFolderList[i].delete(); //폴더를 지운다.
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
}