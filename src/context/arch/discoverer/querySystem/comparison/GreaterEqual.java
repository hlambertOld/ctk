/*
 * GreaterEqual.java
 *
 * Created on July 5, 2001, 11:24 AM
 */

package context.arch.discoverer.querySystem.comparison;

/**
 *
 * @author  Agathe
 */
public class GreaterEqual extends AbstractComparison {

  public static final String GREATER_EQUAL = "grEq";
  
  /** Creates new GreaterEqual */
  public GreaterEqual () {
    super (GreaterEqual.GREATER_EQUAL);
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
    Equal e = new Equal();
    result = g.compare (o1, o2) ||
            e.compare (o1, o2);
    return result;
  }

}
