package org.sam.cotton;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.session.SessionHandler;

import org.sam.cotton.session.InfinispanSessionData;
import org.sam.cotton.session.InfinispanSessionManager;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.Cache;

public class Dispatcher extends SessionHandler {

  public Dispatcher() {
    super();
    // initialize inifinispan with a given configuration
    DefaultCacheManager cache_manager = new DefaultCacheManager();
    Cache<String, InfinispanSessionData> cache = cache_manager.getCache();
    
    // create a InfinispanSessionManager instance
    InfinispanSessionManager session_manager = new InfinispanSessionManager();
    
    // and apply the infinispan cache
    session_manager.setCache(cache);
    // and start the cache
    cache.start();
    
    setSessionManager(session_manager);
  }
  
  public void doHandle(
    Request baseRequest,
    HttpServletRequest request,
    HttpServletResponse response
    ) throws IOException, ServletException {
    // If the request has already been handled by one of the other handlers, just return.
    if(baseRequest.isHandled()) return;

    response.setContentType("text/plain;charset=utf-8");
    response.setStatus(HttpServletResponse.SC_OK);
    baseRequest.setHandled(true);

    response.getWriter().println("Hello World!");
  }
}