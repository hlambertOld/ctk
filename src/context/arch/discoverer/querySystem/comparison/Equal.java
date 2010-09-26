/*
 * Equal.java
 *
 * Created on July 5, 2001, 11:24 AM
 */

package context.arch.discoverer.querySystem.comparison;

/**
 *
 * @author  Agathe
 */
public class Equal extends context.arch.discoverer.querySystem.comparison.AbstractComparison {

  public static final String EQUAL = "eq";
  
  /** Creates new Equal */
  public Equal () {
    super (Equal.EQUAL);
  }
  
  /**
   * Tests if 2 objects are equal.
   *
   * @param o1 The first object
   * @param o2 The second object
   * @return boolean The result of the comparison
   */
  public boolean compare (Object o1, Object o2){
    boolean result = false;
    //System.out.println("\n\nEqual compare " + o1 + " and " + o2);
    result = compare2(new String(o1.toString ()), o2)
            || compare2(new String(o2.toString ()),o1)
            || o1.equals (o2);
    //System.out.println("\nEqual result " + result);
    return result;
  }

  /**
   * This private method does an equal comparison, but assume that the first
   * object is a String and test the class of the second object
   */
  private boolean compare2 (Object o1, Object o2){
    boolean result = false;
    //Test if o1 is string
    if ((o1.getClass().toString()).equals("class java.lang.String")){
      //test if o2 is string
      if (o2.getClass().toString().equals("class java.lang.String") ){
        String s1 = (String) o1;
        String s2 = (String) o2;
        result = s1.equalsIgnoreCase (s2);
        if (!result){
          // try to convert both to double : for the case where o1=123.0 and o2=123
          try {
            Double d1 = new Double (Double.valueOf (o1.toString ()).doubleValue () + 0.0);
            Double d2 = new Double (Double.valueOf (o2.toString ()).doubleValue () + 0.0);;
            result = d1.doubleValue () == d2.doubleValue ();
          }
          catch (ClassCastException cce) {}
          catch (NumberFormatException nfe) {}
          }
      }
      // if o2 is Integer
      else if (o2.getClass().toString().equals("class java.lang.Integer") ){
        try {
          Integer i2 = (Integer) o2;
          Integer i1 = Integer.valueOf (o1.toString ());
          result = i2.intValue() == i1.intValue();
        }
        catch (ClassCastException cce) {}
        catch (NumberFormatException nfe) {}
      }
      // if o2 is Float
      else if (o2.getClass().toString().equals("class java.lang.Float")){
        try {
          Float f2 = (Float) o2;
          Float f1 = Float.valueOf (o1.toString ());
          result = f2.floatValue () == f1.floatValue ();
        }
        catch (ClassCastException cce) {}
        catch (NumberFormatException nfe) {}
      }
      // if o2 is Long
      else if (o2.getClass().toString().equals("class java.lang.Long")){
        try {
          Long l2 = (Long) o2;
          Long l1 = Long.valueOf (o1.toString ());
          result = l1.longValue () == l2.longValue ();
        }
        catch (ClassCastException cce) {}
        catch (NumberFormatException nfe) {}
      }
      // if o2 is Double
      else if (o2.getClass().toString().equals("class java.lang.Double")){
        try {
          Double d2 = (Double) o2;
          Double d1 = Double.valueOf (o1.toString ());
          d1 = new Double( d1.doubleValue () + 0.0);
          result = d1.doubleValue () == d2.doubleValue ();
        }
        catch (ClassCastException cce) {}
        catch (NumberFormatException nfe) {}
      }
    }
    return result;
  }
  
}//class end
