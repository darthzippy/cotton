package org.sam.cotton;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

import org.sam.cotton.RequestLogHandler;
import org.sam.cotton.session.InfinispanSession;
import org.sam.cotton.session.InfinispanSessionManager;

import java.io.File;

public class Cotton {
  public static void main(String[] args) throws Exception {
    start(9292);
  }

  public static Server start(Integer port) throws Exception {
    Server server = new Server(port);
    
    ServletContextHandler app = new ServletContextHandler(ServletContextHandler.SESSIONS);
    app.setContextPath("/");
    app.setResourceBase("public");
    app.setServer(server);

    ServletHolder dispatcher = new ServletHolder(new Dispatcher());
    app.addServlet(dispatcher, "/*");
    
    // initialize infinispan with a given configuration
    DefaultCacheManager cache_manager = new DefaultCacheManager();
    Cache<String, InfinispanSession> cache = cache_manager.getCache();
    
    // start the cache
    cache.start();
    SessionHandler session_handler = new SessionHandler(new InfinispanSessionManager(cache));
    app.setSessionHandler(session_handler);
    
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { new PublicFileHandler("public"), app });
    
    // Uncomment to enable logging:
    handlers.addHandler(new RequestLogHandler());
    
    server.setHandler(handlers);
    server.start();
    server.join();

    return server;
  }
}