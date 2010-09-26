/*
 * Lower.java
 *
 * Created on July 5, 2001, 11:24 AM
 */

package context.arch.discoverer.querySystem.comparison;

/**
 *
 * @author  Agathe
 */
public class Lower extends AbstractComparison {

  public static final String LOWER = "low";
  
  /** Creates new Lower */
  public Lower () {
    super (Lower.LOWER);
  }
  
  /**
   * Compares 2 objects
   *
   * @param o1 The first object
   * @param o2 The second object
   * @return boolean The result of the comparison
   */
  public boolean compare (Object o1, Object o2){
    boolean result = false;
    Greater g = new Greater();
    result = g.compare (o2, o1);
    return result;
  }

}
