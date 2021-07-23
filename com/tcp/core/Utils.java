package com.tcp.core;

/**
 class: BaseLineMessage
 Purpose: setup the baseline respopnse message.
 Notes:
 Author: Tim Lane
 Date: 25/03/2014
 
 **/

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.apache.log4j.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utils {

  String convertedMessage;
  String logMsg;
  SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
  Date date;
  StubLog stubLog = new StubLog();
  
  public String convertHexToString(String hexMessage, Logger logger){
    
    StringBuilder sb = new StringBuilder();
    StringBuilder temp = new StringBuilder();
    //49204c6f7665204a617661 split into two characters 49, 20, 4c...
    for( int i=0; i<hexMessage.length()-1; i+=2 ){          //grab the hex in pairs
      try {
        String output = hexMessage.substring(i, (i + 2));   //convert hex to decimal
        int decimal = Integer.parseInt(output, 16);         //convert the decimal to character
        sb.append((char)decimal);
        temp.append(decimal);
      } catch (Exception e) {
        logMsg = "utils: error in processing HEX to String: " + e + " : " + hexMessage;
        stubLog.addMessage(logMsg,logger,"ERROR");
        sb.append("utils: error in processing HEX to String: " + e + " : " + hexMessage);
        break;
      } 
    }
    return sb.toString();
  }
  
  public String convertStringToHex(String stringMessage, Logger logger){
    
    String msgHex = null;
    msgHex = javax.xml.bind.DatatypeConverter.printHexBinary(stringMessage.getBytes());
    logMsg = "utils: processing string to hex : in " + stringMessage + " out : " + msgHex ;
    stubLog.addMessage(logMsg,logger,"DEBUG");
    return msgHex; 
    
    /*
    char[] chars = stringMessage.toCharArray();
    StringBuffer hex = new StringBuffer();
    for(int i = 0; i < chars.length; i++){
      try {
        hex.append(Integer.toHexString((int)chars[i]));
      } catch (Exception e) {
        logger.error(sdf.format(new Date()) + " utils: error in processing String to HEX : " + e + " : " + stringMessage);
        hex.append("utils: error in processing String to HEX : " + e + " : " + stringMessage);
        break;
      }
    }
    return hex.toString();*/
  }
  
 
  public String convertMessage (String strToConvert,String inFormat, String outFormat,Logger logger){
    
    logMsg = "utils: processing : " + strToConvert + " from : " + inFormat + " to: " + outFormat;
    stubLog.addMessage(logMsg,logger,"DEBUG");
    
    try {
      Charset charset_in = Charset.forName(inFormat);
      Charset charset_out = Charset.forName(outFormat);
      CharsetDecoder decoder = charset_in.newDecoder();
      CharsetEncoder encoder = charset_out.newEncoder();

      CharBuffer uCharBuffer = CharBuffer.wrap(strToConvert);
      ByteBuffer bbuf = encoder.encode(uCharBuffer);
      CharBuffer cbuf = decoder.decode(bbuf);
      strToConvert = cbuf.toString();
    } catch (CharacterCodingException e) {
      logMsg = "utils: error in processing EBCDIC to String: " + e + " : " + strToConvert;
      stubLog.addMessage(logMsg,logger,"ERROR");
      
    }
    logMsg = "utils: converted : " + strToConvert + " from : " + inFormat + " to: " + outFormat;
    stubLog.addMessage(logMsg,logger,"DEBUG");
    
    return strToConvert ;
    
    
  }
  
  
  
}
