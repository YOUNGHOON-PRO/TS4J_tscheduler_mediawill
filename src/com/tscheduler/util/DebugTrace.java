package com.tscheduler.util;

/**
 * <p>Title: TScheduler for Java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003 Neocast Co.,Ltd</p>
 * <p>Company: Neocast Co.,Ltd</p>
 * @author unascribed
 * @version 1.0
 */

import com.tscheduler.util.Config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugTrace
{
	
	private static final Logger LOGGER = LogManager.getLogger(DebugTrace.class.getName());
	
	public static void println(String msg)
	{
		Config cfg = Config.getInstance();
		if( cfg.getDebugOutput() ) {
			LOGGER.info(msg);
			System.out.flush();
		}
	}

	public static void print(String msg)
	{
		Config cfg = Config.getInstance();
		if( cfg.getDebugOutput() ) {
			LOGGER.info(msg);
			System.out.flush();
		}
	}
}