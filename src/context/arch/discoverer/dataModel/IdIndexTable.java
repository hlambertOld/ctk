/*
 * IdIndexTable.java
 *
 * Created on July 3, 2001, 11:08 AM
 */

package context.arch.discoverer.dataModel;

import context.arch.discoverer.ComponentDescription;
/**
 *
 * @author  Agathe
 */
public class IdIndexTable extends AbstractStringToIntegerIndexTable {

  /** 
   * Creates new IdIndexTable 
   */
  public IdIndexTable (String indexName) {
    super(indexName);
  }
  
  /**
   * 
   * @param key ComponentDescription
   * @return String The component id
   */
  public Object getElementAsIndex(Object componentDescription){
    return (String)((ComponentDescription) componentDescription).id;
  }
  

}
