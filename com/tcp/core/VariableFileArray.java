package com.tcp.core;

/**
 class: variableFileArray
 Purpose: holds the details of the variables file reads and writes
 in an in memory array for lookup.
 Notes:
 Author: Tim Lane
 Date: 20/05/2014
 
 **/

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;

public class VariableFileArray {
  
  private List<String> fileArray = new ArrayList<String>();
  private int listCounter;
  
  StubLog stubLog = new StubLog();
  String logMsg = "";
  private Logger logger;

  
  public void initialise(String fileName){
    File file = new File(fileName);
    try {
      BufferedReader in = new BufferedReader(new FileReader(file));
      String text = null;
      
      while ((text = in.readLine()) != null) {
        fileArray.add(text);
        
      }
      in.close();
    } catch (Exception e){
      logMsg = "variableFileArray: error processing variable file: " 
                           + fileName + " : " + e;
              stubLog.addMessage(logMsg, logger,"ERROR");
      
    } 
    
    setListCounter();
  }
  
  public void setListCounter(){
    this.listCounter=0;
  }
  
  public void updateListCounter(){
    this.listCounter++;
    if (this.listCounter == this.fileArray.size()){
      this.listCounter=0;
    }
  }
  
  public String getNextValue(String delimiterVar, String dataVar) {
    
    String nextValue = "file next record not found";
    /*
     * if a delimiter or data columnis passed assume we are loading data from a column
     */ 
    if (delimiterVar.length() > 0 || dataVar.length() > 0) { 
      String delimiter=":";
      if (delimiterVar.length() > 0) {
        delimiter=delimiterVar;
      } 
      
      /*
       * if the data colum is not set assume 1
       */
      int dataColumn=1;
      if (dataVar.length() > 0) {
        dataColumn=Integer.parseInt(dataVar);
      } 
      String [] lineVar=fileArray.get(this.listCounter).split(delimiter);
      nextValue=lineVar[dataColumn-1];
      
    } else { 
      /*
       * otherwise, assume the whole line is being returned
       */
      nextValue = this.fileArray.get(this.listCounter);
    }
    updateListCounter();
    return nextValue; 
  }
  
    public String getNextValue() {
    
      String nextValue = "file next record not found";
        nextValue = this.fileArray.get(this.listCounter);
    updateListCounter();
    
    return nextValue; 
  }
  
  public String getRandomValue(String delimiterVar, String dataVar) {
    int minValue=0;
    int maxValue=this.fileArray.size();
    Random r = new Random();
    int occurrence = r.nextInt(maxValue - minValue) + minValue;
    String nextValue = "file random record not found";
    /*
     * if a delimiter or column is passed assume we are loading data from a column
     */ 
    if (delimiterVar.length() > 0 || dataVar.length() > 0) { 
      String delimiter=":";
      if (delimiterVar.length() > 0) {
        delimiter=delimiterVar;
      } 
      /*
       * if the data colum is not set assume 1
       */
      int dataColumn=1;
      if (dataVar.length() > 0) {
        dataColumn=Integer.parseInt(dataVar);
      } 
      String [] lineVar=fileArray.get(occurrence).split(delimiter);
      nextValue=lineVar[dataColumn-1];
      
    } else { 
      /*
       * otherwise, assume the whole line is being returned
       */
      nextValue = this.fileArray.get(occurrence);
    }
    
    return nextValue; 
  }
  
   public String getRandomValue() {
    int minValue=0;
    int maxValue=this.fileArray.size();
    Random r = new Random();
    int occurrence = r.nextInt(maxValue - minValue) + minValue;
    String nextValue = "file random record not found";
    nextValue = this.fileArray.get(occurrence);
       
    return nextValue; 
  }
   
    public String lookupValue(String keyValue) {
    
    String retValue="file lookup not found for value: " + keyValue;
    String delimiter=",";
    int keyColumn=1;
    int dataColumn=2;
    
    /*
     * loop through data file memory array until a match is found
     */
    for (int i=0;i<fileArray.size();i++) {
      String [] lineVar=fileArray.get(i).split(delimiter);
      if (lineVar[keyColumn-1].equals(keyValue)){
        retValue=lineVar[dataColumn-1];
        break; // found data so stop looking
      }
    }
    
    return retValue; 
  }
    
  public String lookupValue(String lookupValue, String delimiterVar, 
                            String keyVar, String dataVar) {
    
    String retValue="file lookup not found for value: " + lookupValue;
    String delimiter=":";
    int keyColumn=1;
    int dataColumn=2;
    if (delimiterVar.length() > 0) {
      delimiter=delimiterVar;
    } 
    if (keyVar.length() > 0) {
      keyColumn=Integer.parseInt(keyVar);
    }
    if (dataVar.length() > 0) {
      dataColumn=Integer.parseInt(dataVar);
    }
    /*
     * loop through data file memory array until a match is found
     */
    for (int i=0;i<fileArray.size();i++) {
      String [] lineVar=fileArray.get(i).split(delimiter);
      if (lineVar[keyColumn-1].equals(lookupValue)){
        retValue=lineVar[dataColumn-1];
        break; // found data so stop looking
      }
    }
    return retValue; 
  }
}
