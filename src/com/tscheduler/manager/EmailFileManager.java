package com.tscheduler.manager;

//만들어진 이메일을 파일에다가 저장하는 기능을 하는 곳
//Mid폴더 아래에 1,2,3순서로 파일을 만들어주고..
//현재 1,2,3...의 폴더에 파일을 만들면서 작업중일때는 폴더명 앞에 "C"를 붙여준다.
//eml파일의 포멧: mid_subdirectory_numbering.eml

import java.io.*;

import com.tscheduler.util.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 이메일 파일 관리 클래스
 * @version 1.0
 * @author ymkim
 */
public class EmailFileManager
{
	private static final Logger LOGGER = LogManager.getLogger(EmailFileManager.class.getName());
	/**작업중인 폴더에 붙는 이름*/
	private static final String PRE_WORKING="C";
	/**생성된 이메일의 확장자*/
	private static final String EMAIL_EXT = ".eml";


	/**
	 * 폴더를 생성하고 이메일 파일을 생성하는 작업한다.
	 *
	 * @version 1.0
	 * @author ymkim
	 * @param mID 예약 메일 ID
	 * @param midEmailNum 한 MID안에서의 생성된 이메일의 번호
	 * @param sendEmail 발송할 이메일 내용
	 * @param finalNum 한 MID안에서 마지막 이메일인지 체크
	 * @return boolean true - 이메일 생성이 성공, false - 이메일 생성이 실패
	 */
	public static boolean  generateEmailFile(String mID, String subID, int midEmailNum,
			String sendEmail, boolean finalNum)
	{
		Config cfg = Config.getInstance();
		cfg.loadConfig(Config.MAIN_CFG);
		String queueFolder = cfg.getQueueFolder();
		int transferNum = cfg.getTransferNum();
		int emlNumPerFolder = cfg.getEmlNumPerFolder();
		StringBuffer sb = null;

		String queuePath = "";
		String midPath = "";
		File midFolder = null;

		//Queue 폴더의 갯수에 따라서 값이정해진다.
		if( transferNum == 1 )// 큐 폴더의 갯수가 1이면 그대로 진행한다.
		{
			sb = new StringBuffer();
			queuePath = (sb.append(queueFolder).append("_0")
						 .append(File.separator).append("Merge_Queue")
						 .append(File.separator)).toString();
			sb = null;

			sb = new StringBuffer();
			//mID폴더를 생성한다.
			midPath = (sb.append(queuePath).append(mID).append("_").append(subID)
					   .append(File.separator)).toString();
			sb = null;
			midFolder = new File(midPath);

			if( !midFolder.isDirectory() ) {//이미 폴더가 만들어져 있으면
				midFolder.mkdir();
			}

			//mID의 하위 폴더를 만들어준다.(프로퍼티로 설정된 갯수를 기준으로 그 갯수만큼 잘라서 저장한다.)
			//일단 기준은 3개로 한다.
			String makeNum = new Integer((midEmailNum/emlNumPerFolder)+1).toString();

			sb = new StringBuffer();
			String midNumberPath = (sb.append(midPath).append(makeNum)
									.append(PRE_WORKING).append(File.separator)).toString();
			sb = null;
			File numFolder = new File(midNumberPath);
			if( !numFolder.isDirectory() ) {//이미 만들어진 폴더이면 만들지 않는다.
				numFolder.mkdir();
			}

			//파일명을 만든다.
			//eml파일의 포멧: mid_subdirectory_numbering.eml
			sb = new StringBuffer();
			String emlFileName = (sb.append(mID).append("_")
								  .append(makeNum).append("_").append(midEmailNum)
								  .append(EMAIL_EXT)).toString();
			sb = null;
			//String fileName = LogDateGenerator.getLogFolderName()+"_"+midEmailNum+EMAIL_EXT;

			String fileNamePath = midNumberPath + emlFileName;
			//파일을 만든다.
			File creteEmail = new File(fileNamePath);
			PrintWriter pr = null;

			try
			{
				pr = new PrintWriter(new FileWriter(creteEmail));
				pr.println(sendEmail);
				pr.flush();
			}
			catch(Exception e)
			{
				LOGGER.error(e);
				//e.printStackTrace();
				return false;
			}
			finally
			{
				if( pr!= null ) {
					pr.close();
				}
			}

			//숫자로 된 폴더의 갯수가 넘어가서 다음 폴더에다가 저장하면 작업하던 폴더를 "C"의 이름을 빼준다.
			if( finalNum )
			{
				File renameFolder = new File(midPath + makeNum);

				//폴더 바꾸는것이 실패하면 3번까지 다시 만들도록 시도한다.
				while( true )
				{
					if( numFolder.renameTo(renameFolder) )//폴더가 바뀌어진경우
					{
						//대부분 C가 빠지지 않는 경우는 기존에 그 폴더가 있기때문이다.
						//그러면 먼저 그 폴더를 먼저 지워주고 rename을 해준다.
						break;
					}
					else	//폴더가 바뀌어지지 않은경우
					{
						File[] fileList = renameFolder.listFiles();

						if( fileList == null ) {
							continue;
						}

						for( int i = 0; i < fileList.length; i++ ) {
							//그 안에 잇는 내용들을 먼저 지워준다.
							fileList[i].delete();
						}

						renameFolder.delete();
					}
				}
			}
			else
			{
				if( (midEmailNum != 0) && (((midEmailNum+1)%emlNumPerFolder) == 0) )
				{
					File renameFolder = new File(midPath + makeNum);

					while( true )
					{
						if( numFolder.renameTo(renameFolder) )//폴더가 바뀌어진경우
						{
							//대부분 C가 빠지지 않는 경우는 기존에 그 폴더가 있기때문이다.
							//그러면 먼저 그 폴더를 먼저 지워주고 rename을 해준다.
							break;
						}
						else	//폴더가 바뀌어지지 않은경우
						{
							File[] fileList = renameFolder.listFiles();

							if( fileList == null ) {
								continue;
							}

							for( int i = 0; i < fileList.length; i++ ) {
								//그 안에 잇는 내용들을 먼저 지워준다.
								fileList[i].delete();
							}

							renameFolder.delete();
						}
					}
				}
			}
		}
		else	//큐폴더가 여러개이면 해당 갯수별로 동시에 MID 폴더를 만들고 그 하위의 숫자 폴더는 해당 폴더를 번갈아가면서 생성해준다.
		{
			//해당 트랜스퍼갯수만큼 Queue폴더를 만들어준다.
			for( int i = 0; i < transferNum; i++ )
			{
				sb = new StringBuffer();
				queuePath = (sb.append(queueFolder).append("_").append(i).append(File.separator)
							 .append("Merge_Queue").append(File.separator).toString());
				sb = new StringBuffer();
				midPath = (sb.append(queuePath).append(mID).append("_").append(subID)
						   .append(File.separator)).toString();
				midFolder = new File(midPath);
				if( !midFolder.isDirectory() ) { //이미 폴더가 만들어져 있으면
					midFolder.mkdir();
				}
			}

			//mID의 하위 폴더를 만들어준다.(프로퍼티로 설정된 갯수를 기준으로 그 갯수만큼 잘라서 저장한다.)
			//일단 기준은 3개로 한다.
			String makeNum = new Integer((midEmailNum/emlNumPerFolder)+1).toString();

			//midPath가 달라진다.
			int queueSeq = (Integer.parseInt(makeNum)-1)%transferNum;

			sb = new StringBuffer();
			midPath = (sb.append(queueFolder).append("_").append(queueSeq)
					   .append(File.separator).append("Merge_Queue").append(File.separator)
					   .append(mID).append("_").append(subID).append(File.separator)).toString();
			sb = null;

			sb = new StringBuffer();
			String midNumberPath = (sb.append(midPath).append(makeNum)
									.append(PRE_WORKING).append(File.separator)).toString();
			sb = null;

			File numFolder = new File(midNumberPath);
			if( !numFolder.isDirectory() ) { //이미 만들어진 폴더이면 만들지 않는다.
				numFolder.mkdir();
			}

			//파일명을 만든다.
			//eml파일의 포멧: mid_subdirectory_numbering.eml
			String emlFileName = (new StringBuffer().append(mID).append("_")
								  .append(makeNum).append("_").append(midEmailNum)
								  .append(EMAIL_EXT)).toString();
			//String fileName = LogDateGenerator.getLogFolderName()+"_"+midEmailNum+EMAIL_EXT;

			String fileNamePath = new StringBuffer().append(midNumberPath).append(emlFileName).toString();
			//파일을 만든다.
			File creteEmail = new File(fileNamePath);
			PrintWriter pr = null;
			try
			{
				pr = new PrintWriter(new FileWriter(creteEmail));
				pr.println(sendEmail);
				pr.flush();
			}
			catch(Exception e)
			{
				LOGGER.error(e);
				//e.printStackTrace();
				return false;
			}
			finally
			{
				if( pr != null ) {
					pr.close();
					pr = null;
				}
			}

			//숫자로 된 폴더의 갯수가 넘어가서 다음 폴더에다가 저장하면 작업하던 폴더를 "C"의 이름을 빼준다.
			if( finalNum )
			{
				File renameFolder = new File(midPath + makeNum);

				//폴더 바꾸는것이 실패하면 3번까지 다시 만들도록 시도한다.
				while( true )
				{
					if( numFolder.renameTo(renameFolder) )//폴더가 바뀌어진경우
					{
						//대부분 C가 빠지지 않는 경우는 기존에 그 폴더가 있기때문이다.
						//그러면 먼저 그 폴더를 먼저 지워주고 rename을 해준다.
						break;
					}
					else	//폴더가 바뀌어지지 않은경우
					{
						File[] fileList = renameFolder.listFiles();
						if( fileList == null ) {
							continue;
						}

						for( int i = 0; i < fileList.length; i++ ) {
							//그 안에 잇는 내용들을 먼저 지워준다.
							fileList[i].delete();
						}

						renameFolder.delete();
					}
				}
			}
			else
			{
				if( (midEmailNum != 0)&&(((midEmailNum+1)%emlNumPerFolder) == 0) )
				{
					File renameFolder = new File(midPath + makeNum);

					while( true )
					{
						if( numFolder.renameTo(renameFolder) )//폴더가 바뀌어진경우
						{
							//대부분 C가 빠지지 않는 경우는 기존에 그 폴더가 있기때문이다.
							//그러면 먼저 그 폴더를 먼저 지워주고 rename을 해준다.
							break;
						}
						else	//폴더가 바뀌어지지 않은경우
						{
							File[] fileList = renameFolder.listFiles();
							if( fileList == null ) {
								continue;
							}

							for( int i = 0; i < fileList.length; i++ ) {
								//그 안에 잇는 내용들을 먼저 지워준다.
								fileList[i].delete();
							}

							renameFolder.delete();
						}
					}
				}
			}
		}
		return true;
	}
}