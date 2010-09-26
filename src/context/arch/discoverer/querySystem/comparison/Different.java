/*
 * Different.java
 *
 * Created on July 5, 2001, 11:24 AM
 */

package context.arch.discoverer.querySystem.comparison;

/**
 *
 * @author  Agathe
 */
public class Different extends AbstractComparison {

  public static final String DIFFERENT = "diff";
  
  /** Creates new Different */
  public Different () {
    super (Different.DIFFERENT);
  }
  
  /**
   * Compares 2 objects
   *
   * @param o1 The first object
   * @param o2 The second object
   * @return boolean The result of the comparison
   */
  public boolean compare (Object o1, Object o2){
    Equal e = new Equal();
    return ! e.compare (o1, o2);
  }

}
