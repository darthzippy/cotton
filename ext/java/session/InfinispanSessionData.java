package org.sam.cotton.session;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InfinispanSessionData implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 239756354963042417L;

  private final String _id;
  private long _accessed;
  private long _lastAccessed;
  private long _maxIdleMs;
  private long _cookieSet;
  private long _created;
  private Map _attributes;
  private long _expiryTime;

  public InfinispanSessionData(String sessionId) {
    _id = sessionId;
    _created = System.currentTimeMillis();
    _accessed = _created;
    _attributes = new ConcurrentHashMap();
  }

  public synchronized String getId() {
    return _id;
  }

  public synchronized long getCreated() {
    return _created;
  }

  protected synchronized void setCreated(long ms) {
    _created = ms;
  }

  public synchronized long getAccessed() {
    return _accessed;
  }

  protected synchronized void setAccessed(long ms) {
    _accessed = ms;
  }

  public synchronized void setMaxIdleMs(long ms) {
    _maxIdleMs = ms;
  }

  public synchronized long getMaxIdleMs() {
    return _maxIdleMs;
  }

  public synchronized void setLastAccessed(long ms) {
    _lastAccessed = ms;
  }

  public synchronized long getLastAccessed() {
    return _lastAccessed;
  }

  public void setCookieSet(long ms) {
    _cookieSet = ms;
  }

  public synchronized long getCookieSet() {
    return _cookieSet;
  }

  protected synchronized Map getAttributeMap() {
    return _attributes;
  }

  protected synchronized void setAttributeMap(ConcurrentHashMap map) {
    _attributes = map;
  }

  public synchronized void setExpiryTime(long time) {
    _expiryTime = time;
  }

  public synchronized long getExpiryTime() {
    return _expiryTime;
  }

  @Override
  public String toString() {
    return "Session id=" + _id + ",created=" + _created + ",accessed="
        + _accessed + ",lastAccessed=" + _lastAccessed + ",cookieSet="
        + _cookieSet;
  }
}
