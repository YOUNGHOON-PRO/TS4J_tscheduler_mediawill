package com.tscheduler.generator;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TSchedulerDaemon implements Daemon {
	
	private static final Logger LOGGER = LogManager.getLogger(TSchedulerDaemon.class.getName());
	
  TSchedulerMain main;

  public TSchedulerDaemon() {
  }


public void init(DaemonContext context) throws Exception {
 println("TSchedulerDaemon instance: init()");
}

public void start() {
 println("TSchedulerDaemon instance: start(): in");

 main = new TSchedulerMain();
 main.main(new String[1]);

 println("TSchedulerDaemon instance: start(): out");
}

public void stop() throws Exception {
 println("TSchedulerDaemon instance: stop(): in");

 main.shutdown();

 println("TSchedulerDaemon instance: stop(): out");
}

public void destroy() {
 println("TSchedulerDaemon instance: destroy(): in");

 println("TSchedulerDaemon instance: destroy(): out");
}

private String getCurrentTime() {
 java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat(
     "yyyy/MM/dd HH:mm:ss", java.util.Locale.US);
 return fmt.format(new java.util.Date());
}

private void println(String msg) {
	LOGGER.info(getCurrentTime() + " : " + msg);
}



}
