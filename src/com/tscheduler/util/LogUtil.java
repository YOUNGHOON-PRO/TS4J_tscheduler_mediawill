package com.tscheduler.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.io.*;

public class LogUtil {
  public LogUtil() {
    new File("log").mkdirs();
  }

  public static void logCurFile(String log){
    try {
      FileOutputStream o = new FileOutputStream("log" + File.separator +
                                                System.currentTimeMillis() +
                                                ".html");
      o.write(log.getBytes());
      o.flush();
      o.close();
    }
    catch (IOException ie) {
      ie.printStackTrace();
    }
  }

}