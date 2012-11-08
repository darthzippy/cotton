package org.sam.cotton;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import ch.qos.logback.access.jetty.RequestLogImpl;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.OnConsoleStatusListener;

import java.io.File;

public class Cotton {
  public static void main(String[] args) throws Exception {
    start(9292);
  }

  public static Server start(Integer port) throws Exception {
    configureConsoleLogger();

    Server server = new Server(port);
    setHandlers(server, new Dispatcher(), getRequestLogHandler());

    server.start();
    server.join();

    return server;
  }

  private static RequestLogHandler getRequestLogHandler() {
    RequestLogHandler requestLogHandler = new RequestLogHandler();
    requestLogHandler.setRequestLog(new RequestLogImpl());
    return requestLogHandler;
  }

  private static void configureConsoleLogger() {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    StatusManager statusManager = context.getStatusManager();
    OnConsoleStatusListener onConsoleListener = new OnConsoleStatusListener();
    statusManager.add(onConsoleListener);
  }

  private static void setHandlers(Server server, Handler... handlers) {
    HandlerCollection handlers_collection = new HandlerCollection();

    handlers_collection.addHandler(new ContextHandlerCollection());
    for(Handler handler : handlers)
      handlers_collection.addHandler(handler);

    server.setHandler(handlers_collection);
  }
}