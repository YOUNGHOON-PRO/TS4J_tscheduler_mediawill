package com.tscheduler.manager;

//Recovery모듈
//이것은 상태에 따라서 작동을 하게 된다.
import java.util.*;
import java.sql.*;
import java.io.*;

import com.tscheduler.util.Config;
import com.tscheduler.util.DataUnitInfo;
import com.tscheduler.util.DataUnitInfoList;
import com.tscheduler.generator.EmailGenerator;
import com.tscheduler.generator.ContentMakeTimeGenerator;
import com.tscheduler.dbbroker.DBManager;
import com.tscheduler.dbbroker.WorkDBManager;
import com.tscheduler.util.ReserveStatusCode;
import com.tscheduler.util.ErrorLogGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 예약 메일 관리 클래스(복구 모듈)
 * @version 1.0
 * @author ymkim
 */
public class RecoveryReserveManager
{
	private static final Logger LOGGER = LogManager.getLogger(RecoveryReserveManager.class.getName());
	
	public static String queueFolder = "";
	public static int transferNum = 0;
	public static int emlNumPerFolder = 0;

	/**
	 * 생성자
	 * @version 1.0
	 * @author ymkim
	 */
	public static void recovery() {
		searchBreakFolder();
	}

	/**
	 * 작업중에 중단된 폴더를 찾아 그곳 부터 복구 작업을 진행한다.
	 * @version 1.0
	 * @author ymkim
	 */
	public static void searchBreakFolder()
	{
		Config cfg = Config.getInstance();
		cfg.loadConfig(Config.MAIN_CFG);
		queueFolder = cfg.getQueueFolder();
		transferNum = cfg.getTransferNum();
		emlNumPerFolder = cfg.getEmlNumPerFolder();

		ArrayList breakMIDFolderList = new ArrayList();

		//Queue 폴더의 갯수에 따라서 값이정해진다.
		if( transferNum == 1 )// 큐 폴더의 갯수가 1이면 그대로 진행한다.
		{
			String queuePath = new StringBuffer(queueFolder).append("_0")
					.append(File.separator).append("Merge_Queue").append(File.separator).toString();
			File queuePathFile = new File(queuePath);
			//File queuePathFile = new File(".\\Queue_0\\Merge_Queue");
			File[] midFolderFile = queuePathFile.listFiles();

			for( int i = 0; i < midFolderFile.length; i++ )
			{
				if( midFolderFile[i].isDirectory() )
				{
					File[] subNumFolderFile = midFolderFile[i].listFiles();

					for( int k = 0; k < subNumFolderFile.length ; k++ )
					{
						if(((subNumFolderFile[k].getName()).toUpperCase()).endsWith("C")) {
							//이 타이밍에 복구 기능을 작동시킨다.
							runRecovery(midFolderFile[i].getName(),subNumFolderFile[k].getName());
							//breakMIDFolderList.add(midFolderFile[i].getName());
						}
					}
				}
			}
			//중단된 폴더부터 다시 만들어준다.
		}
		else	//Queue 폴더가 여러개 있는 경우
		{
			for( int i = 0; i < transferNum ; i++ )
			{
				String queuePath = (new StringBuffer(queueFolder).append("_").append(i).append(File.separator).append("Merge_Queue").append(File.separator)).toString();
				File queuePathFile = new File(queuePath);
				File[] midFolderFile = queuePathFile.listFiles();

				for( int k = 0; k < midFolderFile.length; k++ )
				{
					if( midFolderFile[k].isDirectory() ) {
						File[] subNumFolderFile = midFolderFile[k].listFiles();

						for( int y = 0; y < subNumFolderFile.length ; y++ )
						{
							if( ((subNumFolderFile[y].getName()).toUpperCase()).endsWith("C") ) {
								//이 타이밍에 복구 기능을 작동시킨다.
								runRecovery(midFolderFile[k].getName(),subNumFolderFile[y].getName());
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 복구 진행 상황 관리 하는곳
	 * @version 1.0
	 * @author ymkim
	 * @param mID 작업이 중단되 예약 메일 ID
	 * @param subNum 작업이 중단된 특정 위치
	 */
	public static void runRecovery(String mID, String subNum)
	{
		//우선 기존의 MID안에 있는 폴더들을 다 지워준다.
		int tempIndex = subNum.indexOf("C");
		String tempSubNum = subNum.substring(0,tempIndex);

		int intSubNum = 0;
		try {
			intSubNum = Integer.parseInt(tempSubNum);
		}
		catch(Exception e) {
			LOGGER.error(e);
			//e.printStackTrace();
		}

		int preMakeEmlSize = emlNumPerFolder*(intSubNum-1);

		RecoveryReceiveManager recovery = new RecoveryReceiveManager();
		File[] rUserListFile = recovery.getRecoveryUsrInfoList(mID, intSubNum);
		DataUnitInfo reserveInfo = recovery.getReserveInfoUnit();

		//얻어온 수신자를 가지고 다시 만들어준다.
		if( rUserListFile != null ) {
			//이 유저 정보를 가지고 이메일(컨덴츠)을 만든다
			EmailGenerator.makeEmail(rUserListFile, reserveInfo,preMakeEmlSize);
		}

		//컨덴츠 완성 시간을 넣어준다.
		ContentMakeTimeGenerator.updateCompleteTime(mID);

		//먼저 각 예약메일에 대한 DB 상태 업데이트를 해준다.
		if( setStatusUpdate(reserveInfo) )
		{
			//업데이트 성공시에는 ReserveLog.log파일을 지워준다.
			//LogFileManager.deleteReserveFile();
		}
		else
		{
			//업데이트 실패시에는 그대로 남겨둔다. 왜냐하면 나중에 다시 그 파일을 가지고 업데이트 해야 하기 때문이다.
		}

		//쓸데 없는 첨부파일이나 혹은 ReceiverList를 전부 지워준다.
		//인코딩한 첨부파일들을 지워준다.(TemplateStorage)폴더 아래에 있는 파일들을 지워준다.
		AttachFileManager.deleteEncAttach();

		//임시 수신자 리스트를 지워준다.(파일로 저장된)
		ReceiverFileManager.deleteTempReceiverList();
	}

	/**
	 * 작업이 멈춰진 폴더를 지워준다.(새로 복구되면 기존에 있는 작업 중단된 폴더를 지워준다)
	 * @version 1.0
	 * @author ymkim
	 * @param mID 작업이 중단되 예약 메일 ID
	 * @param subNum 작업이 중단된 특정 위치
	 * @boolean true - 폴더 삭제 성공, false - 폴더 삭제 실패
	 */
	public boolean deleteSubNumFolder(String mID, String subNum)
	{
		StringBuffer sb = null;
		try
		{
			//Queue 폴더의 갯수에 따라서 값이정해진다.
			if( transferNum == 1 )// 큐 폴더의 갯수가 1이면 그대로 진행한다.
			{
				sb = new StringBuffer();
				String queuePath = sb.append(queueFolder).append("_0")
					 .append(File.separator).append("Merge_Queue").append(File.separator).toString();
				sb = null;
				sb = new StringBuffer();
				String subNumPath = sb.append(queuePath).append(mID)
					  .append(File.separator).append(subNum).append(File.separator).toString();
				sb = null;

				File subNumFile = new File(subNumPath);
				File [] emlFiles = subNumFile.listFiles();
				for( int i = 0; i < emlFiles.length ;i++ ) {
					emlFiles[i].delete();
				}
				subNumFile.delete();
			}
			else	//Queue 폴더가 여러개 있는 경우
			{
				for( int i = 0; i < transferNum; i++ )
				{
					sb = new StringBuffer();
					String queuePath = (sb.append(queueFolder).append("_").append(i).append(File.separator)
										.append("Merge_Queue").append(File.separator)).toString();
					sb = null;
					sb = new StringBuffer();
					String subNumPath = (sb.append(queuePath)
							.append(mID).append(File.separator)
							.append(subNum).append(File.separator)).toString();
					sb = null;

					File subNumFile = new File(subNumPath);
					File [] emlFiles = subNumFile.listFiles();
					for( int k = 0; k < emlFiles.length; k++ ) {
						emlFiles[k].delete();
					}
					subNumFile.delete();
				}
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

	/**
	 * 예약 테이블의 상태를 업데이트 한다.(메모리에 있는 값으로 상태를 업데이트 한다)
	 * @version 1.0
	 * @author ymkim
	 * @param reserveInfo 예약 메일 정보
	 * @boolean true - 상태 업데이트 성공, false - 상태 업데이트 실패
	 */
	public static boolean setStatusUpdate(DataUnitInfo reserveInfo)
	{
		//DB연결 Instance를 얻는다.
		Connection con_work = null;
		PreparedStatement pstmt = null;
		String status = "";
		String mID = "";

		status = reserveInfo.getString("STATUS");
		mID = reserveInfo.getString("MID");

		boolean return_value = false;

		try
		{
			con_work = DBManager.getConnection(Config.WORK_DB);
			pstmt = con_work.prepareStatement(ReserveManager.STATUS_UPDATE_QUERY);
			pstmt.setString(1, status);
			pstmt.setString(2, mID);
			pstmt.executeUpdate();

			con_work.commit();
			return_value = true;
		}
		catch(Exception e)
		{
			LOGGER.error(e);
			WorkDBManager.refreshConn();
			//e.printStackTrace();

			//에러 로그를 남겨준다.
			String errorInfo = mID;
			ErrorLogGenerator.setErrorLogFormat("RecoveryReserveManager", ReserveStatusCode.SQL_ERROR_TYPE,ReserveStatusCode.STATUS_UPDATE_FAIL_COMMENT,errorInfo);

			return_value = false;
		}
		finally
		{
			try
			{
				WorkDBManager.releaseConnection(con_work);
				if( pstmt != null ) {
					pstmt.close();
				}
			}
			catch(Exception e) {
				LOGGER.error(e);
			}
		}
		return return_value;
	}
}