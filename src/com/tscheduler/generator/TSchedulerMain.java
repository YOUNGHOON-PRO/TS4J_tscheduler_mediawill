// 이제 모든 패키지의 짜집기를 여기서 해준다.
// 즉. 예약리스트에서 예약목록을 가져온후에 각 예약에 대한 수신자 리스트를 가져온후에
// 그에 대해서 하나씩 메일을 만든다.
package com.tscheduler.generator;

import java.util.*;
import java.io.File;

import com.tscheduler.util.DataUnitInfo;
import com.tscheduler.util.DataUnitInfoList;
import com.tscheduler.util.Config;
import com.tscheduler.util.ReserveStatusCode;

import synap.next.JFilterUtil;
import synap.next.ParttenCheckUtil;

import com.tscheduler.util.ErrorLogGenerator;
import com.tscheduler.manager.ReserveManager;
import com.tscheduler.manager.Test;
import com.tscheduler.manager.ReceiveManager;
import com.tscheduler.manager.ReceiverFileManager;
import com.tscheduler.manager.RecoveryReserveManager;
import com.tscheduler.manager.LogFileManager;
import com.tscheduler.manager.EmailFileManager;
import com.tscheduler.dbbroker.WorkDBManager;
import com.tscheduler.manager.AttachFileManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TScheduler_Generator의 Main 클래스
 * @version 1.0
 * @author ymkim
 */

public class TSchedulerMain extends Thread
{
	
	private static final Logger LOGGER = LogManager.getLogger(TSchedulerMain.class.getName());
	
	
	/*예약된 메일이 있나 확인하는 시간*/
	long dbCheckTime;

	/**
	 * TScheduler_Generator의 생성자
	 * @version 1.0
	 * @author ymkim
	 */
	public TSchedulerMain()
	{
		Config cfg = Config.getInstance();
		cfg.loadConfig(Config.WORK_DB);
		cfg.loadConfig(Config.MAIN_CFG);
		dbCheckTime = cfg.getDBCheckTime();
	}

	/**
	 * Thread의 run()메소드 구현
	 * @version 1.0
	 * @author ymkim
	 */
	public void run()
	{
		//1.ReserveManager(예약관리)에서 일정갯수를 가져온다.
		//2.가져온 예약리스트를 가지고 수신자 리스트를 뽑는다.
		//3.하나의 수신자 정보를 가지고 컨덴츠를 생성한다.
		//4.마지막으로 상태를 WorkDB에 저장한다.(물론 그 정보를 파일로도 가지고있어야 한다)
		while( true )
		{
			//ReserveLog 파일이 있는지 체크한다. 파일이 있다는것은 상태 업데이트가 안됐다는뜻이다.
			File backFile = new File(LogFileManager.RESERVE_FULL_PATH);   // Log/ReserveLog/
			ReserveManager resMana = null;

			if( backFile.isFile() ) {
				//먼저 파일에 있는 상태를 디비에 업데이트한다.
				resMana = ReserveManager.getInstance(backFile);
			}
			else {
				resMana = ReserveManager.getInstance();
			}
			int reserveMailNum = resMana.getNumberOfReserve();

                        showProcess(String.valueOf(reserveMailNum));

			if( reserveMailNum == 0 ) //즉 WorkDB에서 작업할 예약리스트가 없다면 잠시 Sleep한다.
			{
				try
				{
					//WorkDBManager.checkConnection();
					this.sleep(dbCheckTime);

					//예약리스트를 널로 구성하여 새로 예약리스트를 받아온다.
					resMana.setReserveMailList(null);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			else {
				//작업을 진행한다.
				runProcess(resMana);
			}
		}
	}

	/**
	 * 이메일 Generating 작업을 위한 전체 적인 작업 진행을 기술한다.
	 * @version 1.0
	 * @author ymkim
	 * @param resMana ReserveManager(예약 메일 관리 클래스)
	 */
	public void runProcess(ReserveManager resMana)
	{
		DataUnitInfoList reserveList = resMana.getReserveMailList();
		int reserveMailNum = resMana.getNumberOfReserve();

		for( int i = 0; i < reserveMailNum; i++ )
		{
			ReceiveManager recMana = new ReceiveManager();

			//순서대로 예약리스트중에서 하나씩 가져와서 그것을 수신자리스트를 뽑아온다.receiverUserList가 바로 수신자 리스트이다.
			File[] receiverUserList = recMana.getReceiveUserListFromReserve(reserveList);

			if( receiverUserList != null )
			{
				//도메인별로 그룹핑한후에 그룹핑한 도메인들을 MailQueue_Domain에다가 넣어준다.
				//DomainGroupGenerator.insertCntPerDomain();
				//이 유저 정보를 가지고 이메일(컨덴츠)을 만든다
				EmailGenerator.makeEmail(receiverUserList, reserveList.getDataUnitInfo(i));
			}

			//컨덴츠 완성 시간을 넣어준다.
			ContentMakeTimeGenerator.updateCompleteTime((reserveList.getDataUnitInfo(i)).getString("MID"));
		}

		//먼저 각 예약메일에 대한 DB 상태 업데이트를 해준다.
		if( resMana.setStatusUpdate(reserveList) )
		{
			//업데이트 성공시에는 ReserveLog.log파일을 지워준다.
			LogFileManager.deleteReserveFile();
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

		//예약리스트를 널로 구성하여 새로 예약리스트를 받아온다.
		resMana.setReserveMailList(null);
	}

	/**
	 * 복구모듈을 작동시킨다
	 * @version 1.0
	 * @author ymkim
	 */
	public void recoveryModule() {
		RecoveryReserveManager.recovery();
	}

        private void showProcess(String msg){
          StringBuffer sb = new StringBuffer("[TScheduler v2.0] ");
          sb.append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
          sb.append(" - ").append(msg);
          LOGGER.info(sb.toString());
        }

	/**
	 * TScheduler_Generator의 실행부분
	 * @version 1.0
	 * @author ymkim
	 */
	public static void main(String[] args)
	{
		
		TSchedulerMain main = new TSchedulerMain();
		//최초 복구 모듈을 작동시킨다.(이것은 C폴더를 기준으로 한다)
		main.recoveryModule();
		main.start();
	}

        public static void shutdown() {
          LOGGER.info("TScheduler shutdown.");
          System.exit(0);
        }

}
