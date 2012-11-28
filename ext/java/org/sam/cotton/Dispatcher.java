package org.sam.cotton;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Dispatcher extends HttpServlet {

  private static final long serialVersionUID = 5329853595838463691L;
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	// If the request has already been handled by one of the other handlers, just return.
	    
    response.setContentType("text/plain;charset=utf-8");
    response.setStatus(HttpServletResponse.SC_OK);    
    PrintWriter out = response.getWriter();
    out.println("Hello World!");
    
    HttpSession session = request.getSession();
    
    int counter = 0;
    if (session.getAttribute("counter") != null) {
    	counter = (Integer) session.getAttribute("counter");
    	counter++;
    }
    session.setAttribute("counter", counter);
    
    out.printf("COUNT: %d", counter);
  }
}