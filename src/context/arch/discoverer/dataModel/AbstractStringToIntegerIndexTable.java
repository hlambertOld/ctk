/*
 * StringToIntegerIndexTable.java
 *
 * Created on July 2, 2001, 3:05 PM
 */

package context.arch.discoverer.dataModel;


import java.util.Hashtable;

/**
 * This hashtable associates :
 *    key=String => value=Integer
 *
 * @author  Agathe
 */
public abstract class AbstractStringToIntegerIndexTable extends AbstractIndexTableImpl {

  /** Creates new StringToIntegerIndexTable */
  public AbstractStringToIntegerIndexTable (String indexName) {
    super();
    name = indexName;
  }
  

  /**
   *
   */
  public void add (Object key, Object value) {
    String elementAsKey = ((String) getElementAsIndex(key)).toLowerCase();
    put(elementAsKey, (Integer) value);
  }
  
  /**
   * 
   */
  public void removeKey (Object key, Object value) {
    String elementAsKey = ((String) getElementAsIndex(key)).toLowerCase();
    this.remove (elementAsKey);
  }
  
  
}//class end
