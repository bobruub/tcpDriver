package com.tcp.tcpDriver;

import com.tcp.core.*;

/**
 class: tcpStub
 Purpose: main method for TCP stubbing
 Notes: tcp only
 Author: Tim Lane
 Date: 24/03/2014
 Version: 
 0.1 24/03/2014 lanet - initial write
 **/

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class tcpDriver {
  
  private tcpProperties tcpProperties;
  private ServerSocket serverSocket;
  private LogFileProperties logFileProperties;
  private static String tcpVersion = "1.1";
  
  ReceiverEvent receiverEvent = null;
  StubLog stubLog = new StubLog();
  String logMsg = null;
  
  Socket clientSocket = null;
  List<String> receiverEventsCntr = new ArrayList<String>();
  
  // Create an TCP Stub for a particular TCP port
  public tcpDriver(tcpProperties tcpProperties, LogFileProperties logFileProperties)
  {
    this.tcpProperties = tcpProperties;
    this.logFileProperties = logFileProperties;
  }
  
  static Logger logger = Logger.getLogger(tcpDriver.class);
  public static void main(String[] args) {
    
    /*
     * get config file, need command line option
     */
    String configFileName = null;
    
    System.out.println("tcpDriver: version " + tcpVersion);
    
    if (args.length > 0) {
      configFileName = args[0];
      System.out.println("tcpDriver: using config file: " + configFileName);
    } else {
      configFileName = "C:\\dbox\\Dropbox\\java\\tcpDriver\\xml\\tcpDriver.xml";
      //configFileName = "C:\\Users\\lanetadmin\\Documents\\java_source\\tcpDriver\\xml\\tcpDriver.xml";
      
      System.out.println("tcpDriver: using default config file: " + configFileName);
    } 
    
    try {
      /*
       * open XML config file and from xml config file read TCP properties
       */
      XMLExtractor extractor = new XMLExtractor(new FileInputStream(new File(configFileName)));
      tcpProperties tcpProperties = new tcpProperties(extractor.getElement("TCPServer"));
      tcpProperties.setConfigFileName(configFileName);
      
      /* setup logging
       * TRACE < DEBUG < INFO < WARN < ERROR < FATAL
       * this section lets the xml config overwrite the log4j config file
       * mainly done for simplicitys sake.
       */
      LogFileProperties logFileProperties = new LogFileProperties(extractor.getElement("Header")) ;
      System.out.println("tcpDriver: log4j config file : " + logFileProperties.getLogFileName()); 
      PropertyConfigurator.configure(logFileProperties.getLogFileName());
      if (logFileProperties.getLogLevel().toUpperCase().equals("INFO")) {
        logger.setLevel(Level.INFO);
      } else if (logFileProperties.getLogLevel().toUpperCase().equals("DEBUG")) {
        logger.setLevel(Level.DEBUG);
      } else if (logFileProperties.getLogLevel().toUpperCase().equals("WARN")) {
        logger.setLevel(Level.WARN);
      } else if (logFileProperties.getLogLevel().toUpperCase().equals("ERROR")) {
        logger.setLevel(Level.ERROR);
      } else if (logFileProperties.getLogLevel().toUpperCase().equals("FATAL")) {
        logger.setLevel(Level.FATAL);
      } else if (logFileProperties.getLogLevel().toUpperCase().equals("TRACE")) {
        logger.setLevel(Level.TRACE);
      }
      System.out.println("tcpDriver: logging level set to : " + logger.getLevel().toString());
      logger.info("Non SSL version " + tcpVersion);
      
      tcpDriver tcpDriver = new tcpDriver(tcpProperties, logFileProperties);
      tcpDriver.RunIsolator();
    } catch (Exception e) {
      logger.error("error extracting XML file " + configFileName);
      e.printStackTrace();
      System.exit(1);
    }
    
  }
  
  ServerSocket getServerSocket() throws Exception {
    logger.info("Preparing a regular TCP Server Socket on server:port " + tcpProperties.getServerIP() + ":" + tcpProperties.getServerPort());
    return new ServerSocket (tcpProperties.getServerPort(),
                             tcpProperties.getServerBacklog(), 
                             InetAddress.getByName(tcpProperties.getServerIP()));
    
  }
  
  public void RunIsolator() {
    
    CoreProperties coreProperties = new CoreProperties(tcpProperties.getConfigFileName(),logger);
    /*
     * display stub information to log file
     */
    logMsg = "Author: " + coreProperties.getAuthor()
      + " Name: " + coreProperties.getName()
      + " Description: " + coreProperties.getDescription()
      + " Date: " + coreProperties.getDate();
    stubLog.addMessage(logMsg, logger,"INFO");
    /*
     * load the baseline response message templates
     */
    for (int i = 0; i < coreProperties.getBaselineMessages().size(); i++) {
      BaseLineMessage baseLineMessage =  (BaseLineMessage) coreProperties.getBaselineMessages().get(i);
    }
    /*
     * load the receiver events and the associated messages
     */
    for (int i = 0; i < coreProperties.getReceiverEvents().size(); i++) {
      receiverEvent =  (ReceiverEvent) coreProperties.getReceiverEvents().get(i);
      String receiverName = receiverEvent.getName();
      int numberOfMessages = receiverEvent.getMessages().size();
      for (int c = 0; c < numberOfMessages;  c++ ) {
        EventMessage message = (EventMessage) receiverEvent.getMessages().get(c);
      }
    }
    /*
     * setup thread pool
     */
    logMsg = "setting up threadpool of size: " + tcpProperties.getThreadCount(); 
    stubLog.addMessage(logMsg, logger,"INFO");
    logMsg = "executing for iteration count: " + tcpProperties.getIterations(); 
    stubLog.addMessage(logMsg, logger,"INFO");
    logMsg = "ramping up for: " + tcpProperties.getRampUp(); 
    stubLog.addMessage(logMsg, logger,"INFO");
    
    Double rampUpDelay = (double)Integer.parseInt(tcpProperties.getRampUp()) / (double)tcpProperties.getThreadCount();
    
    logMsg = "rampup delay: " + rampUpDelay; 
    stubLog.addMessage(logMsg, logger,"INFO");
    
    ExecutorService executor = Executors.newFixedThreadPool(tcpProperties.getThreadCount());
    
    boolean socketLoop = true;
    boolean connectionLoop = true;
    int connectionLoopCntr = 0;
    int receiverEventCntr = 0;
    while (socketLoop) {
      
      if (connectionLoopCntr == Integer.parseInt(tcpProperties.getIterations()))
        break;
      connectionLoopCntr ++;
      serverSocket = null;
      /*
       * open the socket
       */
      try {
        
        logMsg = "DBUG : tcpDriver: connecting on " + tcpProperties.getServerIP() + ":" + tcpProperties.getServerPort();
        stubLog.addMessage(logMsg, logger,"DEBUG");
        clientSocket = new Socket(tcpProperties.getServerIP(), Integer.valueOf(tcpProperties.getServerPort()));
        clientSocket.setSoTimeout(10000);
      } catch (Exception e) {
        logMsg = "tcpDriver : Unable to connect on " + tcpProperties.getServerIP() + ":" + tcpProperties.getServerPort();
        stubLog.addMessage(logMsg, logger,"ERROR");
        e.printStackTrace();
        // exit on fail to bind port id.
        System.exit(1);
      }
              
        logMsg = "DBUG : tcpDriver: receiverevent counter " + receiverEventCntr;
        stubLog.addMessage(logMsg, logger,"DEBUG");


        
      ReceiverEvent receiverEvent =  (ReceiverEvent) coreProperties.getReceiverEvents().get(receiverEventCntr);
      Runnable tcpDriverWorker = new tcpDriverWorker(clientSocket, 
                                                     tcpProperties,
                                                     coreProperties,
                                                     receiverEvent);
      executor.execute(tcpDriverWorker);
      if (coreProperties.getReceiverEvents().size() -1 == receiverEventCntr){
        receiverEventCntr = 0;
      } else {
        receiverEventCntr++;
      }
      /*
       * set a rampup delay
       */
      
      try {
        logMsg = "DBUG : tcpDriver : rampup delay : " + rampUpDelay;
        stubLog.addMessage(logMsg, logger,"DEBUG");
        Long longWaitTime = Double.valueOf(rampUpDelay * 1000).longValue();
        Thread.sleep(longWaitTime);
        logMsg = "DBUG : tcpDriver : rampup delay : " + rampUpDelay;
        stubLog.addMessage(logMsg, logger,"DEBUG");
        
      } catch (InterruptedException e) {
        logMsg = "ERRR : tcpDriver : error in thread sleep : " + e;
        stubLog.addMessage(logMsg,logger,"ERROR");
        e.printStackTrace();
      }
    }
    executor.shutdown();
    while (!executor.isTerminated()) {
    }
  }
}

