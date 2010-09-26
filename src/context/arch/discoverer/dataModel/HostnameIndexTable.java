/*
 * ClassnameIndexTable.java
 *
 * Created on July 3, 2001, 11:17 AM
 */

package context.arch.discoverer.dataModel;

import context.arch.discoverer.ComponentDescription;

import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author  Agathe
 */
public class HostnameIndexTable extends AbstractStringToIntegerVectorIndexTable {

  /**
   * Creates new ClassnameIndexTable 
   */
  public HostnameIndexTable (String indexName) {
    super(indexName);
  }

  /**
   * Returns the relevant key that is stored : the hostname and hostaddress
   *
   * @param key ComponentDescription
   * @return ArrayList The component Hostname and Hostaddress
   */
  public Object getElementAsIndex(Object componentDescription){
    ArrayList array = new ArrayList();
    array.add (((ComponentDescription) componentDescription).hostname);
    array.add (((ComponentDescription) componentDescription).hostaddress);
    return array;
  }
    
  /**
   * Overrides the add method to put 2 keys value : the hostaddress and the
   * hostname
   *
   * @param componentDescription The ComponentDescription object
   * @param integer The index
   */
  public void add (Object componentDescription, Object integer) {
    Vector v;
    // If The key already exists, we add the value in the vector
    ArrayList list = (ArrayList) getElementAsIndex(componentDescription);
    for (int i = 0 ; i < list.size () ; i++){
      String elementAsKey = ((String) list.get (i)).toLowerCase();
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
    ArrayList list = (ArrayList) getElementAsIndex(componentDescription);
    for (int i = 0 ; i < list.size () ; i++){
      String elementAsKey = ((String) list.get (i)).toLowerCase();
      Vector v = (Vector) this.get (elementAsKey);
      if (v != null){
        v.remove ((Integer) integer);
        if (v.isEmpty ())
          this.remove (elementAsKey);
      }
    }
  }
  
}//class end
