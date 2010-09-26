/*
 * ClassnameIndexTable.java
 *
 * Created on July 3, 2001, 11:17 AM
 */

package context.arch.discoverer.dataModel;

import context.arch.discoverer.ComponentDescription;

/**
 * 
 * Stores the port as a String
 *
 * @author  Agathe
 */
public class PortIndexTable extends AbstractStringToIntegerVectorIndexTable {

  /**
   * Creates new ClassnameIndexTable 
   */
  public PortIndexTable (String indexName) {
    super(indexName);
  }

  /**
   * Returns the relevant key that is stored : the port number
   *
   * @param key ComponentDescription
   * @return String The component port
   */
  public Object getElementAsIndex(Object componentDescription){
    return Integer.toString (((ComponentDescription) componentDescription).port);
  }
    
    
}
