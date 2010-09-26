/*
 * AbstractComparison.java
 *
 * Created on July 5, 2001, 11:16 AM
 */

package context.arch.discoverer.querySystem.comparison;

import context.arch.comm.DataObject;

/**
 *
 * @author  Agathe
 */
public abstract class AbstractComparison {

  public static final String ABSTRACT_COMPARISON = "abComp";
  
  /**
   * The comparison type
   */
  private String comparisonName;
  
  /** 
   * Creates new AbstractComparison 
   */
  public AbstractComparison (String compName) {
    comparisonName = compName;
  }
  
  /**
   * Returns the comparison name
   *
   * return String
   */
  public String getComparisonName(){
    return comparisonName;
  }
  
  /**
   * Compares 2 objects
   *
   * @param o1 The first object
   * @param o2 The second object
   * @return boolean The result of the comparison
   */
  public abstract boolean compare(Object o1, Object o2);

  public String toString(){
    return getComparisonName();
  }
  
  public DataObject toDataObject(){
    return new DataObject(ABSTRACT_COMPARISON, getComparisonName ());
  }
  
  public static AbstractComparison fromDataObject(DataObject data){
    String name = (String) data.getValue ().firstElement ();
    return AbstractComparison.factory (name);
  }
  
  public static AbstractComparison factory(String name){
    if (name.equals (Equal.EQUAL)){
      return new Equal();
    }
    else if (name.equals (Different.DIFFERENT)){
      return new Different();
    }
    else if (name.equals (Greater.GREATER)){
      return new Greater();
    }
    else if (name.equals (GreaterEqual.GREATER_EQUAL)){
      return new GreaterEqual();
    }
    else if (name.equals (Lower.LOWER)){
      return new Lower();
    }
    else if (name.equals (LowerEqual.LOWER_EQUAL)){
      return new LowerEqual();
    }
    return null;
  }
  
}//class end
