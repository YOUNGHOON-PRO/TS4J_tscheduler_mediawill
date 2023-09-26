package com.tscheduler.dbbroker;

// 컨넥션 풀 프로그램
import java.sql.*;
import java.util.Vector;

import com.tscheduler.util.Config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * WorkDB 관리 클래스
 * @version 1.0
 * @author ymkim
 */
public class WorkDBManager
{
	private static final Logger LOGGER = LogManager.getLogger(WorkDBManager.class.getName());
	
	/**대기하고 있는 컨넥션*/
	private static Vector watingPool;
	/**사용하고 있는 컨넥션*/
	private static Vector usingPool;

	/**DB 연결 Driver*/
	private String dbDriver;
	/**DB 연결 URL*/
	private String dbURL;
	/**DB 연결 User ID*/
	private String dbUserID;
	/**DB 연결 User Password*/
	private String dbUserPass;
	/**DB ConnectionPool의 최대 연결 갯수*/
	private int maxConn;

	/**WorkDBManager의 Singleton 객체*/
	private static WorkDBManager instance;

	/**
	 * 컨넥션을 얻는다.
	 * @version 1.0
	 * @author ymkim
	 * @return Connection
	 */
	public static Connection getConnection()
	{
		if( instance == null ) {
			instance = new WorkDBManager();
		}
		else {
		}
		return instance.getFreeConnection();
	}

	/**
	 * 생성자
	 * @version 1.0
	 * @author ymkim
	 */
	private WorkDBManager()
	{
		setUp();
	}

	/**
	 * WorkDB 연결을 위한 정보를 얻는다.
	 * @version 1.0
	 * @author ymkim
	 */
	public void setUp()
	{
		Config workDBCFG = Config.getInstance();
		workDBCFG.loadConfig(Config.WORK_DB);
		dbDriver = workDBCFG.getDBDriver();
		dbURL = workDBCFG.getDBURL();
		dbUserID = workDBCFG.getDBUserID();
		dbUserPass = workDBCFG.getDBUserPass();
		maxConn = workDBCFG.getMaxConn();

		if( maxConn <= 0 ) {
			maxConn = Config.DEFAULT_MAX_CONN; //디폴트로 5로 설정한다.
		}

		try
		{
			Class.forName(dbDriver);
		}
		catch(Exception e) {
			LOGGER.error(e);
			//e.printStackTrace();
		}

		watingPool = new Vector(maxConn);
		usingPool = new Vector(maxConn);

		newMakeConnection(maxConn);
	}


	/**
	 * 컨넥션 풀에 들어갈 컨넥션들을 생성해준다.
	 * @version 1.0
	 * @author ymkim
	 * @param maxConn 컨넥션 풀에 생성될 컨넥션의 갯수
	 */
	public void newMakeConnection(int maxConn)
	{
		LOGGER.info("최초에 만들어질 컨넥션 갯수는 :"+maxConn);
		Connection con = null;
		try
		{
			for( int i = 0; i < maxConn; i++ )
			{
				con = DriverManager.getConnection(dbURL, dbUserID, dbUserPass);
				//con.setAutoCommit(false);
				con.setAutoCommit(true);
				LOGGER.info("새로운컨넥션을만든다:_" + i + "_" + con);
				watingPool.add(con);
			}
		}
		catch(Exception e)
		{
			LOGGER.error(e);
			//e.printStackTrace();
		}
	}


	/**
	 * 미사용중인 컨넥션을 넘겨준다.
	 * @version 1.0
	 * @author ymkim
	 * @return Connection 컨넥션
	 */
	private synchronized Connection getFreeConnection()
	{
		Connection con = null;

		try
		{
			if( watingPool.isEmpty() ) //비어 있으면 아무것도 기다렸다가 다시 컨넥션을 요구한다.
			{
				LOGGER.info("컨넥션이 비어있다.");
				this.wait(500);
//				refreshConn();
				getConnection();
			}
			else
			{
				con = (Connection) watingPool.remove(0);

				LOGGER.info("------------------------------------");
				LOGGER.info("현재 사용중인 컨넥션수:"+usingPool.size());
				LOGGER.info("현재 대기중인 컨넥션수:"+watingPool.size());
				LOGGER.info("------------------------------------");
				
				usingPool.add(con);
			}
		}
		catch(Exception e) {
			LOGGER.error(e);
			//e.printStackTrace();
		}
		return con;
	}

	/**
	 * 현재 사용중인 컨넥션과 사용중이 아닌 컨넥션을 확인한다.
	 * @version 1.0
	 * @author ymkim
	 */
	public static void checkConnection()
	{
		LOGGER.info("------------------------------------");
		LOGGER.info("현재 사용중인 컨넥션수 : " + usingPool.size());
		LOGGER.info("현재 대기중인 컨넥션수 : " + watingPool.size());

		for( int i = 0; i < watingPool.size(); i++ ) {
			//LOGGER.info("현재 만들어진 컨넥션:"+usingPool.get(i));
		}
		LOGGER.info("------------------------------------");
	}

	/**
	 * 사용중인 컨넥션을 다시 대기중인 상태로 전환한다.
	 * @version 1.0
	 * @author ymkim
	 * @param con 대기상태로 전환할 컨넥션
	 */
	public static synchronized void releaseConnection(Connection con)
	{
		if( usingPool.remove(con) ) {
			watingPool.add(con);
		}
	}

	/**
	 * 모든 컨넥션을 제거한다.
	 * @version 1.0
	 * @author ymkim
	 */
	public synchronized void closeAllConn()
	{
		try
		{
			for( int i = 0; i < watingPool.size(); i++ )
			{
				Connection con = (Connection)watingPool.remove(0);
				if( con != null ) {
					con.close();
				}
			}

			for( int i = 0; i < usingPool.size(); i++ )
			{
				Connection con = (Connection)usingPool.remove(0);
				if( con != null ) {
					con.close();
				}
			}
		}
		catch(Exception e) {
			LOGGER.error(e);
			//e.printStackTrace();
		}
	}

	/**
	 * finalize() 메소드를 오버라이딩하여 모든 컨넥션을 끊어주는 작업을 한다.
	 * @version 1.0
	 * @author ymkim
	 */
	public void finalize()
	{
		closeAllConn();
	}


	/**
	 * 모든 컨넥션을 제거하고 다시 컨넥션을 생성한다.
	 * @version 1.0
	 * @author ymkim
	 */
	public static synchronized void refreshConn()
	{
		//모든 컨넥션을 끊는다.
		try
		{
			for( int i = 0; i < watingPool.size(); i++ )
			{
				Connection con = (Connection)watingPool.remove(0);
				if(con != null) {
					con.close();
				}
			}

			for( int i = 0; i < usingPool.size(); i++ )
			{
				Connection con = (Connection)usingPool.remove(0);
				if( con != null ) {
					con.close();
				}
			}
		}
		catch(Exception e) {
			LOGGER.error(e);
			//e.printStackTrace();
		}

		//다시 커넥션을 맺어준다.
		instance = null;
		instance = new WorkDBManager();
	}
}