/*
 * NonCstAttributeIndexTable.java
 *
 * Created on July 3, 2001, 11:17 AM
 */

package context.arch.discoverer.dataModel;

import context.arch.discoverer.ComponentDescription;
import context.arch.storage.Attribute;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author  Agathe
 */
public class NonCstAttributeIndexTable extends AbstractStringToIntegerVectorIndexTable {

  /**
   * Creates new NonCstAttributeIndexTable 
   */
  public NonCstAttributeIndexTable (String indexName) {
    super(indexName);
  }

  /**
   * Returns the relevant key that is stored : the constant attributes
   *
   * @param key ComponentDescription
   * @return Collection of the constant attributes
   */
  public Object getElementAsIndex(Object componentDescription){
//    Collection c = ((ComponentDescription)componentDescription).getNonConstantAttributes();
//    c.addAll (((ComponentDescription)componentDescription).getConstantAttributeNames());
    Collection c = ((ComponentDescription)componentDescription).getAllAttributes();
    return c;
  }
    
  /**
   * Overrides the add method to 
   *
   * @param componentDescription The ComponentDescription object
   * @param integer The index
   */
  public void add (Object componentDescription, Object integer) {
    Vector v;
    // If The key already exists, we add the value in the vector
    Collection c = (Collection) getElementAsIndex(componentDescription);
    Iterator list = c.iterator ();
    Object o;
    Attribute key;
    String elementAsKey = null;
    while (list.hasNext ()){
      o = list.next ();
      if (o instanceof Attribute) {
        key = (Attribute) o;
        elementAsKey = ((String) key.getName ()).toLowerCase();
      }
      else if ( o instanceof String){
        elementAsKey = ((String) o).toLowerCase();
      }
      if (elementAsKey != null){
        if ((v = (Vector)this.get (elementAsKey)) != null) {
          v.addElement ((Integer) integer);
        }
        else {
          v = new Vector();
          v.addElement ((Integer) integer);
          this.put (elementAsKey, v);
        }
      }
    }
  }
    
  /**
   * This method allows to remove ...
   *
   * @param key
   * @param value
   */
  public void removeKey (Object componentDescription, Object integer) {
    Collection c = (Collection) getElementAsIndex(componentDescription);
    Iterator list = c.iterator ();
    Object o;
    Attribute key;
    String elementAsKey = null;
    while (list.hasNext ()){
      o = list.next ();
      if (o instanceof Attribute) {
        key = (Attribute) o;
        elementAsKey = ((String) key.getName ()).toLowerCase();
      }
      else if ( o instanceof String){
        elementAsKey = ((String) o).toLowerCase();
      }
      Vector v = (Vector) this.get (elementAsKey);
      if (v != null){
        v.remove ((Integer) integer);
        if (v.isEmpty ())
          this.remove (elementAsKey);
      }
    }
  }
  
}//class end
