package com.tscheduler.manager;

import java.io.*;
import java.util.*;

import com.tscheduler.util.DataUnitInfo;
import com.tscheduler.util.DataUnitInfoList;
import com.tscheduler.util.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 수신자 리스트 파일 관리 클래스
 * @version 1.0
 * @author ymkim
 */
public class ReceiverFileManager
{
	
	private static final Logger LOGGER = LogManager.getLogger(ReceiverFileManager.class.getName());
	
	/**MID에 대한 인코딩된 첨부파일이 있는곳*/
	public static String RECEIVER_FOLDER = "ReceiverList";

	/**
	 * 수신자 리스트를 지워준다.
	 * @version 1.0
	 * @author ymkim
	 * @param boolean true - 수신자 리스트 삭제 성공 , false - 수신자 리스트 삭제 실패
	 */
	public static boolean deleteTempReceiverList()
	{
		try
		{
			String receiverFolderStr = Config.ROOT_DIR+File.separator+LogFileManager.LOG_FOLDER+File.separator+RECEIVER_FOLDER+File.separator;
			File receiverRootFolder = new File(receiverFolderStr);

			File[] receiverMIDFolderList = receiverRootFolder.listFiles();

			if( receiverMIDFolderList == null )
			{
				return false;
			}

			for( int i = 0; i < receiverMIDFolderList.length; i++ )
			{
				if( receiverMIDFolderList[i].isDirectory() ) {
					File[] delMIDSubFile = receiverMIDFolderList[i].listFiles();
					for( int k = 0; k < delMIDSubFile.length ;k++ ) {
						delMIDSubFile[k].delete();	//파일을 지운다.
					}
				}
				receiverMIDFolderList[i].delete(); //폴더를 지운다.
			}
		}
		catch(Exception e)
		{
			LOGGER.error(e);
			//e.printStackTrace();
			return false;
		}
		return true;
	}
}