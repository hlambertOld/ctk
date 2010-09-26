/*
 * ClassnameIndexTable.java
 *
 * Created on July 3, 2001, 11:17 AM
 */

package context.arch.discoverer.dataModel;

import context.arch.discoverer.ComponentDescription;

/**
 *
 * @author  Agathe
 * @version 
 */
public class TypeIndexTable extends AbstractStringToIntegerVectorIndexTable {

  /**
   * Creates new ClassnameIndexTable 
   */
  public TypeIndexTable (String indexName) {
    super(indexName);
  }

  /**
   * 
   * @param key ComponentDescription
   * @return String The component type (Widget, Server, BaseObject, Interpreter)
   */
  public Object getElementAsIndex(Object componentDescription){
    return ((ComponentDescription) componentDescription).type;
  }
    
    
}
