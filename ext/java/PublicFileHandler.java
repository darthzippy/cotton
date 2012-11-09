package org.sam.cotton;

import org.eclipse.jetty.server.handler.ResourceHandler;

// See ResourceHandler source here:
//
//   http://download.eclipse.org/jetty/stable-8/xref/org/eclipse/jetty/server/handler/ResourceHandler.html
//
// We need to override it's behavior to skip directory listing, and not handle the request.
// That way if the request matches an existing directory (root will always match, which is the main problem),
// instead of getting a 403 like now, the Handler will pass, and our HandlerCollection will invoke
// our Dispatcher instead.
//
// The PublicFileHandler should be used ONLY for serving static FILES.
public class PublicFileHandler extends ResourceHandler {

  public PublicFileHandler() {
    super();
    // setWelcomeFiles(new String[]{ "index.html" });
    setResourceBase("public");
  }

}