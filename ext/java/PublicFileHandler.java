package org.sam.cotton;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandler.Context;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;


/* ------------------------------------------------------------ */
/** Resource Handler.
*
* This handle will serve static content and handle If-Modified-Since headers.
* No caching is done.
* Requests for resources that do not exist are let pass (Eg no 404's).
*
*
* @org.apache.xbean.XBean
*/
public class PublicFileHandler extends HandlerWrapper {
  private static final Logger LOG = Log.getLogger(PublicFileHandler.class);

  ContextHandler _context;
  Resource _baseResource = null;
  String[] _welcomeFiles = { "index.html" };
  MimeTypes _mimeTypes = new MimeTypes();
  ByteArrayBuffer _cacheControl;

  public PublicFileHandler(String baseResource) {
    try {
      _baseResource = Resource.newResource(baseResource);
    } catch (Exception e) {
      LOG.warn(e.toString());
      LOG.debug(e);
      throw new IllegalArgumentException(baseResource);
    }
  }

  public MimeTypes getMimeTypes() {
    return _mimeTypes;
  }

  public void setMimeTypes(MimeTypes mimeTypes) {
    _mimeTypes = mimeTypes;
  }

  @Override
  public void doStart() throws Exception {
    Context scontext = ContextHandler.getCurrentContext();
    _context = (scontext == null ? null : scontext.getContextHandler());

    super.doStart();
  }

  /* ------------------------------------------------------------ */
  /**
   * @return the cacheControl header to set on all static content.
   */
  public String getCacheControl() {
    return _cacheControl.toString();
  }

  /* ------------------------------------------------------------ */
  /**
   * @param cacheControl the cacheControl header to set on all static content.
   */
  public void setCacheControl(String cacheControl) {
    _cacheControl = cacheControl == null ? null : new ByteArrayBuffer(cacheControl);
  }

  public Resource getResource(String path) throws MalformedURLException {
    if(path == null || !path.startsWith("/"))
      throw new MalformedURLException(path);

    Resource base = _baseResource;
    if(base == null) {
      if(_context == null)
        return null;
      base=_context.getBaseResource();
      if(base == null)
        return null;
    }

    try {
      path=URIUtil.canonicalPath(path);
      return base.addPath(path);
    } catch(Exception e) {
      LOG.ignore(e);
    }

    return null;
  }

  protected Resource getResource(HttpServletRequest request) throws MalformedURLException {
    String servletPath;
    String pathInfo;
    Boolean included = request.getAttribute(Dispatcher.INCLUDE_REQUEST_URI) != null;
    if (included != null && included.booleanValue()) {
      servletPath = (String)request.getAttribute(Dispatcher.INCLUDE_SERVLET_PATH);
      pathInfo = (String)request.getAttribute(Dispatcher.INCLUDE_PATH_INFO);

      if (servletPath == null && pathInfo == null) {
        servletPath = request.getServletPath();
        pathInfo = request.getPathInfo();
      }
    } else {
      servletPath = request.getServletPath();
      pathInfo = request.getPathInfo();
    }
    
    String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
    return getResource(pathInContext);
  }

  protected Resource getWelcome(Resource directory) throws MalformedURLException, IOException {
    for (int i = 0; i < _welcomeFiles.length; i++) {
      Resource welcome = directory.addPath(_welcomeFiles[i]);
      if (welcome.exists() && !welcome.isDirectory())
        return welcome;
    }

    return null;
  }

  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    if (baseRequest.isHandled())
      return;

    boolean skipContentBody = false;

    if(!HttpMethods.GET.equals(request.getMethod())) {
      if(!HttpMethods.HEAD.equals(request.getMethod())) {
        //try another handler
        super.handle(target, baseRequest, request, response);
        return;
      }
      skipContentBody = true;
    }
    
    Resource resource = getResource(request);
    
    if (resource == null || !resource.exists()) {
      //no resource - try other handlers
      super.handle(target, baseRequest, request, response);
      return;
    }

    // We are going to serve something
    baseRequest.setHandled(true);

    if (resource.isDirectory()) {
      if(!request.getPathInfo().endsWith(URIUtil.SLASH)) {
        response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(request.getRequestURI(), URIUtil.SLASH)));
        return;
      }

      Resource welcome = getWelcome(resource);
      if(welcome != null && welcome.exists()) {
        resource = welcome;
      }
    }

    // set some headers
    long last_modified = resource.lastModified();
    if (last_modified > 0) {
      long if_modified = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
      if (if_modified > 0 && last_modified / 1000 <= if_modified / 1000) {
        response.setStatus(HttpStatus.NOT_MODIFIED_304);
        return;
      }
    }

    Buffer mime = _mimeTypes.getMimeByExtension(resource.toString());
    if(mime == null)
      mime = _mimeTypes.getMimeByExtension(request.getPathInfo());

    // set the headers
    doResponseHeaders(response, resource, mime != null ? mime.toString() : null);
    response.setDateHeader(HttpHeaders.LAST_MODIFIED, last_modified);
    if(skipContentBody)
      return;
    // Send the content
    OutputStream out = null;
    try {
      out = response.getOutputStream();
    } catch(IllegalStateException e) {
      out = new WriterOutputStream(response.getWriter());
    }

    // See if a short direct method can be used?
    if(out instanceof AbstractHttpConnection.Output) {
      // TODO file mapped buffers
      ((AbstractHttpConnection.Output)out).sendContent(resource.getInputStream());
    } else {
      // Write content normally
      resource.writeTo(out,0,resource.length());
    }
  }

  protected void doResponseHeaders(HttpServletResponse response, Resource resource, String mimeType) {
    if(mimeType != null)
      response.setContentType(mimeType);

    long length = resource.length();

    if(response instanceof Response) {
      HttpFields fields = ((Response)response).getHttpFields();

      if(length > 0)
        fields.putLongField(HttpHeaders.CONTENT_LENGTH_BUFFER, length);

      if (_cacheControl != null)
        fields.put(HttpHeaders.CACHE_CONTROL_BUFFER,_cacheControl);
    } else {
      if(length > 0)
        response.setHeader(HttpHeaders.CONTENT_LENGTH,Long.toString(length));

      if(_cacheControl != null)
        response.setHeader(HttpHeaders.CACHE_CONTROL,_cacheControl.toString());
    }
  }
}