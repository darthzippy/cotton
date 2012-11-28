package org.sam.cotton;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.OnConsoleStatusListener;

public class RequestLogHandler extends org.eclipse.jetty.server.handler.RequestLogHandler {

   static {	 
     LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
     StatusManager statusManager = context.getStatusManager();
     OnConsoleStatusListener onConsoleListener = new OnConsoleStatusListener();
     statusManager.add(onConsoleListener);
   }

  public RequestLogHandler() {
    super();
    // setRequestLog(new RequestLogImpl());
  }

}