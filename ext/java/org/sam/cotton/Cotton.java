package org.sam.cotton;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;

import java.io.File;

public class Cotton {
  public static void main(String[] args) throws Exception {
    start(9292);
  }

  public static Server start(Integer port) throws Exception {
    Server server = new Server(port);
    
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { new PublicFileHandler("public"), new Dispatcher() });
    
    // Uncomment to enable logging:
    handlers.addHandler(new RequestLogHandler());
    
    server.setHandler(handlers);
    server.start();
    server.join();

    return server;
  }
}