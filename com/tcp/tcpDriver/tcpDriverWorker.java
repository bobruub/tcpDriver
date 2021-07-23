package com.tcp.tcpDriver;

import com.tcp.core.*;
/**
 class: tcpStubWorker
 Purpose: new thread for TCP stub.
 Notes:
 Author: Tim Lane
 Date: 24/03/2014
 **/

import com.sharkysoft.printf.Printf;
import java.net.Socket;
import java.io.BufferedOutputStream;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class tcpDriverWorker extends StubWorker implements Runnable{
  
  public static final String DELIMITED_TYPE = "Delimited";
  public static final String MULTIDELIMITED_TYPE = "MultiDelimited";
  public static final String EXTRACTVALUE_TYPE = "ExtractValue";
  public static final String FILE_READ_TYPE = "FileRead";
  public static final String NUMBER_TYPE = "Number";
  public static final String POSITIONAL_TYPE = "Positional";
  public static final String RANDOM_DOUBLE_TYPE = "RandomDouble";
  public static final String RANDOM_LONG_TYPE = "RandomLong";
  public static final String STRING_TYPE = "String";
  public static final String THREAD_TYPE = "Thread";
  public static final String TIMESTAMP_TYPE = "Timestamp";
  public static final String LOOKUP_TYPE = "Lookup";
  public static final String HEX_TYPE = "HEX";
  public static final String GUID_TYPE = "Guid";
  public static final String THREAD_COUNT_TYPE = "ThreadCount";
  public static final String CONTENT_LENGTH_TYPE = "ContentLength";
  public static final String RECEIVEREVENT_COUNT_TYPE = "ReceiverCount";
  public static final String DATABASE_LOOKUP_TYPE = "DatabaseLookup";
  public static final String SESSION_TYPE = "SessionId";
  
  private Socket clientSocket;
  private tcpProperties tcpProperties;
  private CoreProperties coreProperties;
  private Logger logger;
  private Utils utils;
  private BaseLineMessage baseLineMessage;
  private ReceiverEvent receiverEvent;
  
  public tcpDriverWorker(Socket clientSocket, 
                         tcpProperties tcpProperties,
                         CoreProperties coreProperties,
                         ReceiverEvent receiverEvent){
    this.clientSocket = clientSocket;
    this.tcpProperties = tcpProperties;
    this.coreProperties = coreProperties;
    this.logger = coreProperties.getLogger();
    this.receiverEvent = receiverEvent;
    
  }
  
  @Override
  public void run() {
    
    String searchLine = null;
    String searchType = null;
    String searchValue = null;
    String receiverName = null;
    String threadName = null;
    String charSet = null;
    EventMessage message;
    String outStream = null;
    String logMsg;
    StubLog stubLog = new StubLog();
    String responseFromServer = null;
    String messageToServer = null;
    Utils utils = new Utils();
    
    try
    {
      tcpProperties.setActiveThreadCount();
      threadName = Printf.format("%.8d", new Object[] {Double.valueOf(Thread.currentThread().getId())});
      /*
       * send data
       */
      receiverName = receiverEvent.getName();
      message = (EventMessage) receiverEvent.getMessages().get(0); // assume one message per event.
      String messageName = message.getName();
      String responseMsg = getBaselineMessage(messageName,coreProperties);
      responseMsg = getBaselineMessage(messageName,coreProperties);  
      logMsg = "DBUG : T" + threadName + " : receiverName: " + receiverName + " : message name: " + messageName 
        + " message content : " + responseMsg;
      stubLog.addMessage(logMsg,logger,"DEBUG");
      
      // messageToServer = baseLineMessage.getCdata();
      
      outStream = processMessage(message,
                                 logger,
                                 coreProperties,
                                 "searchLine",
                                 stubLog,
                                 threadName,
                                 receiverEvent,
                                 charSet,
                                 clientSocket,
                                 tcpProperties,
                                 utils);
      try {
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
       
        outToServer.writeBytes(outStream + '\n');
        responseFromServer = inFromServer.readLine();
        clientSocket.close();
        logMsg = "RECV : T" + threadName + " : " + responseFromServer;
        stubLog.addMessage(logMsg, logger,"INFO");
        if (receiverEvent.getMsgFormat().toUpperCase().equals("HEX")){
          // convert message to hex
          responseFromServer = utils.convertHexToString(responseFromServer,logger);
          logMsg = "RECV : T" + threadName + " : NON HEX : " + responseFromServer;
          stubLog.addMessage(logMsg, logger,"INFO");
        }

        
      }
      catch (Exception e) {
        logMsg = "WARN : T" + threadName + " : tcpStubWorker: warning getting data : " + e.getMessage();
        stubLog.addMessage(logMsg, logger,"ERROR");
      } 
      
    } catch (Exception e) { 
      logMsg = "ERRR : T" + " : tcpDriverWorker: error writing to output stream. : " + e.toString();
      stubLog.addMessage(logMsg,logger,"ERROR");
      e.printStackTrace();  
      try {
        clientSocket.close();  
      } catch (Exception ec) {
        logMsg = "ERRR : T" + " : tcpDriverWorker: error closing socket : " + ec.toString();
        stubLog.addMessage(logMsg,logger,"ERROR");
        ec.printStackTrace();  
      } 
      return;
    } 
  }
  /*-------------------------------------------------------------------------------------------------------------------
   * process the message
   *-----------------------------------------------------------------------------------------------------------------*/
  private String processMessage(EventMessage message,Logger logger,CoreProperties coreProperties,
                                String searchLine,StubLog stubLog,String threadName, 
                                ReceiverEvent receiverEvent,String charSet, Socket clientSocket,
                                tcpProperties tcpProperties,Utils utils){
    
    String messageName=message.getName();
    String waitDistribution = message.getWaitDistribution();
    double waitFrom = message.getWaitFrom();
    double waitTo = message.getWaitTo(); 
    double waitTime = 0;
    long longWaitTime = 0;
    String logMsg = null;
    String responseMsg=null;
    String outStream = null;
    String receiverName = receiverEvent.getName();
    /*
     * once we have set the message config find the corresponding BASELINE message (response data)
     * in the baseline message array
     */
    responseMsg = getBaselineMessage(messageName,coreProperties);
    /*
     * now we have the BASELINE response data need to replace all Variables, tagged with %varName%
     * in the response message. So loop through all variables and see if they exist in the response message
     */
    responseMsg = processResponseMessage(searchLine,responseMsg,coreProperties,logger,threadName);
    /*
     * process the wait time based on the individual message requirements
     */ 
    waitTime = getRandonNumber(waitDistribution,waitFrom,waitTo);
    logMsg = "DBUG : T" + threadName  
      + " : tcpDriverWorker: sleeping for : " + waitTime;
    stubLog.addMessage(logMsg,logger,"DEBUG");
    
    try {
      longWaitTime = Double.valueOf(waitTime * 1000).longValue();
      Thread.sleep(longWaitTime);
    } catch (InterruptedException e) {
      logMsg = "ERRR : tcpStubWorker: error in thread sleep : " + e;
      stubLog.addMessage(logMsg,logger,"ERROR");
      e.printStackTrace();
    }
    logMsg = "SEND : T" + threadName  
          + " : Wait : " + String.format("%.3f", waitTime)
          + " : sender name : " + receiverName + " : message name: " + messageName 
          + " : message content : " + responseMsg;
        stubLog.addMessage(logMsg, logger,"INFO");
    
    if (receiverEvent.getMsgFormat().toUpperCase().equals("HEX")){
      // convert message to hex
      responseMsg = utils.convertStringToHex(responseMsg,logger);
    logMsg = "SEND : T" + threadName  
          + " : Wait : " + String.format("%.3f", waitTime)
          + " : sender name : " + receiverName + " : message name: " + messageName 
          + " : message content HEX : " + responseMsg;
        stubLog.addMessage(logMsg, logger,"INFO");
      
    }
    
    
    return responseMsg;
    
  }
  
}

