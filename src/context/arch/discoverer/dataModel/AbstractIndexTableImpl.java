/*
 * IndexTableImpl.java
 *
 * Created on July 3, 2001, 10:33 AM
 */

package context.arch.discoverer.dataModel;

import java.util.Hashtable;

/**
 *
 * @author  Agathe
 */
public abstract class AbstractIndexTableImpl extends Hashtable implements IndexTableIF {

  /**
   *
   */
  protected String name;

  /**
   *
   */
  public AbstractIndexTableImpl (){
    super();
  }
  
  /**
   *
   */
  public abstract void add (Object key,Object value);
  
  /**
   *
   */
  public abstract void removeKey (Object key,Object value);
  
  /**
   *
   */
  public String getName (){
    return name;
  }
  
  /**
   *
   */
  public String toString (){
    StringBuffer sb = new StringBuffer(getName());
    sb.append (super.toString ());
    return sb.toString ();
  }
  
  /**
   * Returns the element of the object key that is used as the key of the 
   * indexTable.
   * That is the final class that chooses what element of the complex object
   * is stored as a key. 
   *
   * @param object The object from which the class extracts what is relevant for it.
   * @return Object
   */
  public abstract Object getElementAsIndex(Object object);
}
