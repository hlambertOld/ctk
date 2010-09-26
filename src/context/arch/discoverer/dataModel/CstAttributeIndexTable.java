/*
 * CstAttributeIndexTable.java
 *
 * Created on July 3, 2001, 11:17 AM
 */

package context.arch.discoverer.dataModel;

import context.arch.discoverer.ComponentDescription;

import java.util.ArrayList;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Collection;

/**
 *
 * @author  Agathe
 */
public class CstAttributeIndexTable extends AbstractStringToIntegerVectorIndexTable {

  /**
   * Creates new CstAttributeIndexTable 
   */
  public CstAttributeIndexTable(String indexName) {
    super(indexName);
  }

  /**
   * Returns the relevant key that is stored : the constant attributes
   *
   * @param key ComponentDescription
   * @return collection of the constant attributes
   */
  public Object getElementAsIndex(Object componentDescription){
    return ((ComponentDescription)componentDescription).getConstantAttributeNameValues();
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
    Iterator list = ((Collection) getElementAsIndex(componentDescription)).iterator();
    
    while (list.hasNext()){
      String elementAsKey = ((String) list.next()).toLowerCase();
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
    
  /**
   * This method allows to remove ...
   *
   * @param key
   * @param value
   */
  public void removeKey (Object componentDescription, Object integer) {
   Iterator list = ((Collection) getElementAsIndex(componentDescription)).iterator ();
    while (list.hasNext ()){
      String elementAsKey = ((String) list.next()).toLowerCase();
      Vector v = (Vector) this.get (elementAsKey);
      if (v != null){
        v.remove ((Integer) integer);
        if (v.isEmpty ())
          this.remove (elementAsKey);
      }
    }
  }
  
}//class end
