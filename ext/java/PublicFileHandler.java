package org.sam.cotton;

import org.eclipse.jetty.server.handler.ResourceHandler;

public class PublicFileHandler extends ResourceHandler {

  public PublicFileHandler() {
    super();
    // setWelcomeFiles(new String[]{ "index.html" });
    setResourceBase("public");
  }

}