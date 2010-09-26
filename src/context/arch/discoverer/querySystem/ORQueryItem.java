/*
 * ORQueryItem.java
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
public class ORQueryItem extends AbstractBooleanQueryItem{

  public static final String OR_QUERY_ITEM = "orQueryItem";
  
  /** Creates new ANDQueryItem */
  public ORQueryItem (AbstractQueryItem left,AbstractQueryItem right) {
    super(OR_QUERY_ITEM, left,  right);
  }

  /**
   * Perform an OR
   */
  public Object process(AbstractDataModel dataModel){
    int [][] leftResult = (int [][]) left.process (dataModel);
    int [][] rightResult = (int [][]) right.process (dataModel);
    
    int size = leftResult.length;
    for (int i = 0 ; i < size ; i ++){
      leftResult[i][1] = leftResult[i][1] | rightResult[i][1];
    }
    
    return leftResult;
  }
   
  /**
   *
   */
  public boolean process(Object component){
    boolean leftResult = left.process (component);
    boolean rightResult = right.process (component);
    return leftResult || rightResult;
  }
  
}//class end
