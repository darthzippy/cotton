package org.sam.cotton;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class Dispatcher extends AbstractHandler {

  public void handle(
    String target,
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