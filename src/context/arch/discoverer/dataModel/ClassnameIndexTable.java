/*
 * ClassnameIndexTable.java
 *
 * Created on July 3, 2001, 11:17 AM
 */

package context.arch.discoverer.dataModel;

import context.arch.discoverer.ComponentDescription;

/**
 *
 * @author  Administrator
 * @version 
 */
public class ClassnameIndexTable extends AbstractStringToIntegerVectorIndexTable {

  /**
   * Creates new ClassnameIndexTable 
   */
  public ClassnameIndexTable (String indexName) {
    super(indexName);
  }

  /**
   * 
   * @param key ComponentDescription
   * @return String The component classname
   */
  public Object getElementAsIndex(Object componentDescription){
    return ((ComponentDescription) componentDescription).classname;
  }
  
}
