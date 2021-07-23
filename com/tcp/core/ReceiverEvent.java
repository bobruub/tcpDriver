package com.tcp.core;

/**
class: ReceiverEvent
Purpose: setup the receiver events.
Notes:
Author: Tim Lane
Date: 25/03/2014

**/

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiverEvent {
  
    public static final String DEFAULT_TYPE = "DEFAULT";
    public static final String KEY_TYPE_STRING = "STRING";
    public static final String KEY_TYPE_REGEX = "STRING";
    
    private String name;
    private String type = "";
    private String keyType;
    private String keyValue;
    private String msgFormat;
    /* reciver events can have more than one response message
     *  <ReceiverEvent Name="TestPage" KeyType="STRING" KeyValue="test_page">
          <EventMessage BaselineMessage="TestPage" WaitDistribution="UNIFORM" MinWait="0.2" MaxWait="1.2"/>
          <EventMessage BaselineMessage="TestPage2" WaitDistribution="UNIFORM" MinWait="0.2" MaxWait="1.2"/>
         </ReceiverEvent>
    */
    private List<EventMessage> messages = new ArrayList<EventMessage>();
    
    public List<EventMessage> getMessages() {
        return messages;
    }

    public void addMessage(EventMessage message)
    {
        messages.add(message);
    }
    
    public void setDefault() {
        setName("DEFAULT");
        setType(DEFAULT_TYPE);
    }
    
    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }
    //
    public void setMsgFormat(String msgFormat) {
        this.msgFormat = msgFormat;
    }
    
    public String getMsgFormat() {
       return msgFormat;
    }
    //
    
    
    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }
    
    public String getKeyValue() {
       return keyValue;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public boolean matches(String inputMsg){
        if (keyType.equals(KEY_TYPE_STRING))
        {
            if (inputMsg.contains(keyValue))
                return true;
        }
        else // must be regex:
        {
            Pattern myPattern = Pattern.compile(keyValue);
            Matcher matcher = myPattern.matcher(inputMsg);
            if (matcher.find())
                return true;
        }
        return false;
    }
        
        
}
