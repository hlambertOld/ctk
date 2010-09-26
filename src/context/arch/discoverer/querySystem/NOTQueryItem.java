/*
 * NOTQueryItem.java
 *
 * Created on July 5, 2001, 4:22 PM
 */

package context.arch.discoverer.querySystem;

import context.arch.discoverer.dataModel.AbstractDataModel;

import java.util.HashMap;

/**
 *
 * @author  Agathe
 */
public class NOTQueryItem extends AbstractBooleanQueryItem{

  public static final String NOT_QUERY_ITEM = "notQueryItem";
  
  /** Creates new ANDQueryItem */
  public NOTQueryItem (AbstractQueryItem left) {
    super(NOT_QUERY_ITEM, left);
  }

  /**
   * Perform a NOT
   */
  public Object process(AbstractDataModel dataModel){
    int [][] leftResult = (int [][]) left.process (dataModel);
    
    int size = leftResult.length;
    for (int i = 0 ; i < size ; i ++){
      leftResult[i][1] = (leftResult[i][1] + 1) % 2 ;
    }
    
    return leftResult;
  }
  
  /**
   *
   */
  public boolean process(Object component){
    return ! left.process (component);
  }
}
