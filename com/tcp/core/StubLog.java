package com.tcp.core;

/**
class: StubLog
Purpose: handle messaging
Notes:
Author: Tim Lane
Date: 25/03/2014

**/

import org.apache.log4j.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;
public class StubLog {
 
  SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
  Date date;

  public void addMessage(String msg, Logger logger, String msgType){
    
    if (msgType.toUpperCase().equals("ERROR")) {
       logger.error(sdf.format(new Date()) + " - " + msg);
    } else if (msgType.toUpperCase().equals("WARN") ) {
       logger.info(sdf.format(new Date()) + " - " + msg);
    } else if (msgType.toUpperCase().equals("INFO") && (logger.isInfoEnabled())) {
       logger.info(sdf.format(new Date()) + " - " + msg);
    } else if (msgType.toUpperCase().equals("DEBUG") && (logger.isDebugEnabled())) {
       logger.debug(sdf.format(new Date()) + " - " + msg);
    }
    return ;

  }
  
}
