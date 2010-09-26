/*
 * Greater.java
 *
 * Created on July 5, 2001, 11:24 AM
 */

package context.arch.discoverer.querySystem.comparison;

/**
 *
 * @author  Agathe
 */
public class Greater extends AbstractComparison {

  public static final String GREATER = "gr";
  
  /** Creates new Greater */
  public Greater () {
    super (Greater.GREATER);
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
    
    // tests if o1 is Integer
    if (o1.getClass().toString().equals("class java.lang.Integer") ){
      try {
        Integer i1 = (Integer) o1;
        Integer i2 = Integer.valueOf (o2.toString ());
        result = i1.intValue () > i2.intValue ();
      }
      catch (ClassCastException cce) {}
      catch (NumberFormatException nfe) {}
    }
    // Tests if o1 is Float
    else if (o1.getClass().toString().equals("class java.lang.Float") ){
      try {
        Float f1 = (Float) o1;
        Float f2 = Float.valueOf (o2.toString ());
        result = f1.floatValue () > f2.floatValue ();
      }
      catch (ClassCastException cce) {}
      catch (NumberFormatException nfe) {}
    }
    // if o1 is Long
    else if (o1.getClass().toString().equals("class java.lang.Long")){
      try {
        Long l1 = (Long) o1;
        Long l2 = Long.valueOf (o2.toString ());
        result = l1.longValue () > l2.longValue ();
      }
      catch (ClassCastException cce) {}
      catch (NumberFormatException nfe) {}
    }
    // if o1 is Double
    else if (o1.getClass().toString().equals("class java.lang.Double")){
      try {
        Double d1 = (Double) o1;
        Double d2 = Double.valueOf (o2.toString ());
        System.out.println("Greater " + d1 + " " + d2);
        result = d1.doubleValue () > d2.doubleValue ();
      }
      catch (ClassCastException cce) {}
      catch(NumberFormatException nfe) {}
    }
    // else try to convert both objects into Double
    else {
      try {
        Double d1 = Double.valueOf (o1.toString ());
        Double d2 = Double.valueOf (o2.toString ());
        result = d1.doubleValue () > d2.doubleValue ();
      }
      catch (ClassCastException cce) {}
      catch(NumberFormatException nfe) {}
    }
    // If result is still false, try to convert all to double, and take care
    // of the "."
    if (!result){
      try {
        Double d1 = new Double ( (new Double(o1.toString ())).doubleValue () + 0.0);
        Double d2 = new Double ( (new Double(o2.toString ())).doubleValue () + 0.0);
        result = d1.doubleValue () > d2.doubleValue ();
      }
      catch (ClassCastException cce) {}
      catch (NumberFormatException nfe) {}
    }
    return result;
  }
  
  /********************************************/
  public static void main (String args[]){
    Double i1 = new Double(1540);
    Long d2 = new Long (1541);
    String s2 = "1541.0";
    String ss2 = "1541";
    String s3 = "1541.2";
    Double d3 = new Double (1541.2);
    
    
    System.out.println("i1 " + i1 + i1.getClass ());
    System.out.println("d2 " + d2 + d2.getClass ());
    
    Equal e = new Equal();
    Greater g = new Greater();
    Lower l = new Lower();
    LowerEqual le = new LowerEqual();
    Different d = new Different();
    
    System.out.println("i1==d2" + e.compare (i1,d2));
    System.out.println("e : " + e.compare (new Boolean(true), new Boolean(true)));
    
    
    System.out.println("d2 > i1" + g.compare (d2,i1));
    
    System.out.println("i1 > s2" + g.compare (i1, s2));
    System.out.println("s2 > d2" + g.compare (s2, d2));
    System.out.println("d2 == s2" + e.compare (d2,s2));
    System.out.println("s2==d2" + e.compare (s2, d2));
    System.out.println("s3 > s2 " + g.compare (s3, s2));
    System.out.println("d3 > s2 " + g.compare (d3,s2));
    System.out.println("s2 == ss2" + e.compare (s2, ss2));
    
    System.out.println("i1<d2" + l.compare (i1,d2));
    System.out.println("ss2<s3 " +l.compare (ss2,s3)); 

    System.out.println("s2<ss2" + l.compare (s2,ss2));
    System.out.println("s2!=ss2" + d.compare (s2, ss2));
  }
}
