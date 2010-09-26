/*
 * AbstractIndexTable.java
 *
 * Created on July 2, 2001, 2:55 PM
 */

package context.arch.discoverer.dataModel;

import java.util.Enumeration;

/**
 *
 *
 *
 *
 *   -------------------------------------
 *   |   IndexTableIF                    |
 *   -------------------------------------
 *   | add(key, value)
 *   | removeKey(key, value)
 *   | getName() : string
 *   | containsKey(key) :bool
 *   | get(key):obj
 *   -------------------------------------
 *
 *  
 *
 *
 *   -------------------------------------
 *   |    AbstractIndexTableImpl                    ------------------------
 *   -------------------------------------          | Hashtable            |
 *   | add(key, value)                              ------------------------
 *   | removeKey(key, value)
 *   | getName() : string
 *   | containsKey(key) :bool
 *   | get(key):obj
 *   -------------------------------------
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * @author  Agathe
 */

public interface IndexTableIF {

  /**
   *
   */
  public void add(Object key, Object value);
  
  /**
   *
   */
  public void removeKey(Object key, Object value);
  
  /**
   *
   */
  public String getName();
  
  /**
   *
   */
  public String toString();
  
  /**
   *
   */
  public boolean containsKey(Object object);
  
  /**
   *
   */
  public Object get(Object key);
  
  /**
   *
   */
  public Enumeration keys();
  
  /*
  abstract void removeValue(Object key);
  
  abstract Object search(Object key);

  */
  
  
}
