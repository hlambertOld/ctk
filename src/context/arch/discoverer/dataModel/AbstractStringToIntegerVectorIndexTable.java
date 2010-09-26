/*
 * StringToIntegerVectorIndexTable.java
 *
 * Created on July 2, 2001, 3:12 PM
 */

package context.arch.discoverer.dataModel;

import context.arch.discoverer.ComponentDescription;

import java.util.Hashtable;
import java.util.Vector;

/**
 * This class allows to store in a Hashtable :
 *    key=String  => value=Vector of Integer
 *
 * @author  Agathe
 */
public abstract class AbstractStringToIntegerVectorIndexTable extends AbstractIndexTableImpl {

  /**
   * Creates new StringToIntegerVectorIndexTable 
   */
  public AbstractStringToIntegerVectorIndexTable (String IndexName) {
    super();
    name = IndexName;
  }

  /**
   *
   * This method allows to add just one value. So it must be overridden in 
   * other cases.
   *
   * @param componentDescription The ComponentDescription object
   * @param integer The index
   *
   */
  public void add (Object key, Object integer) {
    Vector v;
    // If The key already exists, we add the value in the vector
    String elementAsKey = ((String)getElementAsIndex(key)).toLowerCase();
    if ((v = (Vector)this.get (elementAsKey)) != null) {
      v.addElement ((Integer) integer);
    }
    else {
      v = new Vector();
      v.addElement ((Integer) integer);
      put (elementAsKey, v);
    }
  }

  /**
   * This method allows to remove the Integer value from the vector of Integer
   * corresponding to key.
   * This method allows to remove just one value. So it must be overridden in 
   * other cases.
   *
   * @param key
   * @param value
   */
  public void removeKey (Object key, Object value) {
    String elementAsKey = ((String)getElementAsIndex(key)).toLowerCase();
    Vector v = (Vector) this.get (elementAsKey);
    if (v != null){
      v.remove ((Integer) value);
      if (v.isEmpty ())
        this.remove (elementAsKey);
    }
  }

  
}//class end
