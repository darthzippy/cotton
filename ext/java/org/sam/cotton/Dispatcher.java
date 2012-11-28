package org.sam.cotton;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.session.SessionHandler;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

import org.sam.cotton.session.InfinispanSession;
import org.sam.cotton.session.InfinispanSessionManager;

public class Dispatcher extends HttpServlet {

	private static final long serialVersionUID = 5329853595838463691L;

public Dispatcher() {
    super();
    
    // initialize infinispan with a given configuration
    DefaultCacheManager cache_manager = new DefaultCacheManager();
    Cache<String, InfinispanSession> cache = cache_manager.getCache();
    
    // start the cache
    cache.start();
    SessionHandler session_handler = new SessionHandler(new InfinispanSessionManager(cache));
//    setSessionHandler(session_handler);
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	// If the request has already been handled by one of the other handlers, just return.
	    
    response.setContentType("text/plain;charset=utf-8");
    response.setStatus(HttpServletResponse.SC_OK);    
    response.getWriter().println("Hello World!");
  }
}