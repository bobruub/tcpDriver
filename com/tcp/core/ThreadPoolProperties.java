package com.tcp.core;

/**
class: ThreadPoolProperties
Purpose: holds thread pool properties for HTTP requests.
Notes:
Author: Tim Lane
Date: 24/03/2014

**/
public class ThreadPoolProperties {
  
  private int ThreadPoolSize;
    
  public void setThreadPoolSize(String ThreadPoolSize){
    this.ThreadPoolSize = Integer.parseInt(ThreadPoolSize);
  }
  
  public int getThreadPoolSize(){
    return ThreadPoolSize;
  }
  
  
}
