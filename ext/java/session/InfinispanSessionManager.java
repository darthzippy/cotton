package org.sam.cotton.session;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.session.AbstractSessionManager;
import org.eclipse.jetty.server.session.AbstractSession;
import org.eclipse.jetty.util.log.Log;
import org.infinispan.Cache;

public class InfinispanSessionManager extends AbstractSessionManager {

  protected Cache<String, InfinispanSessionData> cache;

  public InfinispanSessionManager() {
    super();
  }
  /**
   * add a Session to the session store
   */
  @Override
  protected void addSession(AbstractSession session) {

    InfinispanSessionManager.Session spanSession = ((InfinispanSessionManager.Session) session);

    if (Log.isDebugEnabled())
      Log.debug("addSession call " + spanSession.getClusterId());

    cache.put(spanSession.getClusterId(), spanSession._data);

  }
  /**
   * get the session by session id
   * @returns the session or NULL if no session exists
   */
  @Override
  public Session getSession(String idInCluster) {

    if (Log.isDebugEnabled())
      Log.debug("get Session from cache " + idInCluster);

    InfinispanSessionData data = (InfinispanSessionData) cache
        .get(idInCluster);

    if (data == null) {
      return null;
    } else {
      return new Session(data);
    }
  }

  /**
   * should return the session map
   * TODO: think about is it clever to do this in a cluster environment?
   */
  @Override
  public Map getSessionMap() {
    if (Log.isDebugEnabled())
      Log.debug("getSessionMap call - unsupported");
    return null;
  }
  /**
   * delete all sessions
   */
  @Override
  protected void invalidateSessions() {
    if (Log.isDebugEnabled())
      Log.debug("invalidateSessions  call");
    cache.clear();
  }
  /**
   * create a new session
   */
  @Override
  protected Session newSession(HttpServletRequest request) {
    if (Log.isDebugEnabled())
      Log.debug("new Session call");
    return new Session(this, request);
  }
  /**
   * remove a session
   */
  @Override
  protected boolean removeSession(String idInCluster) {
    if (Log.isDebugEnabled())
      Log.debug("removeSession call");
    cache.remove(idInCluster);
  }

  protected class Session extends AbstractSession implements Serializable {

    private static final long serialVersionUID = -7709121635752136930L;

    private final InfinispanSessionData _data;

    protected Session(AbstractSessionManager manager, HttpServletRequest request) {
      super(manager, request);
      if (Log.isDebugEnabled())
        Log.debug("Session constructor call");
      _data = new InfinispanSessionData(getClusterId());
      _data.setMaxIdleMs(_dftMaxIdleSecs * 1000);
      _data.setExpiryTime(_dftMaxIdleSecs < 0 ? 0 : (System.currentTimeMillis() + (_dftMaxIdleSecs * 1000)));
      _values = _data.getAttributeMap();
    }

    protected Session(InfinispanSessionData data) {
      super(data.getCreated(), data.getId());
      _data = data;
      _data.setMaxIdleMs(_dftMaxIdleSecs * 1000);
      _values = data.getAttributeMap();
    }

    @Override
    protected Map newAttributeMap() {
      if (Log.isDebugEnabled())
        Log.debug("newAttributeMap call");
      return _data.getAttributeMap();
    }

    @Override
    protected String getClusterId() {
      if (Log.isDebugEnabled())
        Log.debug("getclusterid: " + super.getClusterId());
      return super.getClusterId();
    }

    @Override
    protected void cookieSet() {
      super.cookieSet();
      if (Log.isDebugEnabled())
        Log.debug("cookieSet");
      _data.setCookieSet(_data.getAccessed());
    }

    /**
     * Entry to session.
     *
     * @see org.eclipse.jetty.server.session.AbstractSessionManager.Session#access(long)
     */
    @Override
    protected void access(long time) {
      if (Log.isDebugEnabled())
        Log.debug("access");
      super.access(time);
      _data.setLastAccessed(_data.getAccessed());
      _data.setAccessed(time);
      _data.setExpiryTime(_maxIdleMs < 0 ? 0 : (time + _maxIdleMs));
    }

    /**
     * Exit from session
     *
     * @see org.eclipse.jetty.server.session.AbstractSessionManager.Session#complete()
     */
    @Override
    protected void complete() {
      super.complete();
      if (Log.isDebugEnabled())
        Log.debug("complete " + _data);
      cache.replace(_clusterId, _data);
    }

    @Override
    protected void timeout() throws IllegalStateException {
      super.timeout();
      if (Log.isDebugEnabled())
        Log.debug("timeout");
      cache.remove(getClusterId());
    }
  }

  public Cache<String, InfinispanSessionData> getCache() {
    return cache;
  }

  public void setCache(Cache<String, InfinispanSessionData> cache) {
    this.cache = cache;
  }
}
