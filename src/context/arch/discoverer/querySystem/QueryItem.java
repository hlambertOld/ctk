/*
 * QueryItem.java
 *
 * Created on July 5, 2001, 3:25 PM
 */

package context.arch.discoverer.querySystem;

import context.arch.discoverer.dataModel.AbstractDataModel;
import context.arch.discoverer.querySystem.comparison.AbstractComparison;
import context.arch.discoverer.querySystem.comparison.Equal;
import context.arch.discoverer.componentDescription.AbstractDescriptionElement;
import context.arch.discoverer.dataModel.IndexTableIF;
import context.arch.comm.DataObject;

import java.util.HashMap;
import java.util.Collection;
import java.util.Vector;
import java.util.Iterator;
import java.util.Enumeration;

/**
 *
 * @author  Agathe
 */
public class QueryItem extends AbstractQueryItem {

  public static final String QUERY_ITEM = "queryItem";
  
  /** The type of comparison : equal, greater, lower, different... */
  private AbstractComparison comparison;
  
  /** The object that specifies the type of description element and the value
   * wanted
   * i.e. : type = id
   * value = PersonNamePresence
   */
  private AbstractDescriptionElement elementToMatch;
  
  /** 
   * Creates new QueryItem 
   */
  public QueryItem (AbstractDescriptionElement element, AbstractComparison comparison) {
    this.elementToMatch = element;
    this.comparison = comparison;
  }
  
  /**
   *
   */
  public QueryItem (AbstractDescriptionElement element) {
    this(element, new Equal());
  }
  
  /**
   * This method allows to perform the query
   */
  public Object process(AbstractDataModel dataModel){
    //0 get an empty HashMap from the dataModel that has as key the element index
    // and as value : true or false
    int [][] array = (int [][]) dataModel.getEmptyArray ();
    //1 get the hashtable corresponding to elToMatch
    //System.out.println("\n\nGet index of " + elementToMatch.getElementName()); 
    IndexTableIF table = dataModel.getIndexTableIFCorrespondingTo (elementToMatch.getElementName ());
    //2 get the object corresponding to the searched value
    if (table != null) {
      // 2-1 If the comparison type is equal => get directly the value
      if (comparison.getComparisonName ().equals (Equal.EQUAL)){
        Object result = table.get(elementToMatch.getValue ());
        processResult(array, result);
      }//end 2-1
      
      // 2-2 Else handle the comparison type
      else {
        Enumeration keys = table.keys();
        String temp;
        String toMatch = (String) elementToMatch.getValue ();
        Object obj;
        while (keys.hasMoreElements ()){
          temp = (String) keys.nextElement ();
          if (comparison.compare(temp, toMatch)){
            obj = table.get(temp);
            processResult(array, obj);
          }
        }
      }// end 2-2
    }
    else {
      //System.out.println("QueryItem <process AbstractDataModel> type of the element to match = not recognized");
    }
    return array;
  }

  /**
   *
   */
  public boolean process(Object component){
    boolean result = false;
    //System.out.println("\n\n\nQueryItem <process component> element to match " + elementToMatch);
    result = elementToMatch.processQueryItem(component, comparison);
    //System.out.println("QueryItem final result " + result);
    return result;
  }
  
  /**
   *
   */
  private void processResult(int [][] arrayToFill, Object containsElements){
    if (containsElements != null){
      if (containsElements instanceof Integer){
        int el = ((Integer)containsElements).intValue();
        int index;
        if ( (index = indexOf(arrayToFill, el)) > -1 )
          arrayToFill[index][1] = 1;
      }
      else if (containsElements instanceof Collection){
        Collection c = (Collection) containsElements;
        Iterator list = c.iterator ();
        Integer temp;
        int index;
        while (list.hasNext ()){
          temp = (Integer) list.next ();
          if ( (index = indexOf(arrayToFill, temp.intValue())) > -1 )
            arrayToFill[index][1] = 1;
        }
      }
      else {
        //System.out.println("QueryItem <process AbstractDataModel> on the result from the data model");
      }
    }
  }
  
  /**
   *
   */
  public String toString(){
    StringBuffer sb = new StringBuffer("QueryItem : ");
    sb.append (elementToMatch.toString ());
    sb.append (" " +comparison.toString());
    return sb.toString ();
  }
  
  private int indexOf(int[][] table, int searchedElement){
    int res;
    for (int i = 0 ; i < table.length ; i++){
      if (table[i][0] == searchedElement)
        return i;
    }
    return -1;
  }
  
  /**
   *
   */
  public static AbstractQueryItem fromDataObject(DataObject data) { 
    DataObject doAbsDes = data.getDataObject (AbstractDescriptionElement.ABSTRACT_DESCRIPTION_ELEMENT);
    AbstractDescriptionElement abDes = AbstractDescriptionElement.fromDataObject (doAbsDes);
    
    DataObject doComp = data.getDataObject (AbstractComparison.ABSTRACT_COMPARISON);
    AbstractComparison comp = AbstractComparison.fromDataObject(doComp);
    return new QueryItem(abDes,comp);
  }
  
  /**
   *
   */
  public DataObject toDataObject(){
    DataObject absEl = elementToMatch.toDataObject ();
    DataObject comp = comparison.toDataObject ();
    Vector v = new Vector();
    v.add (absEl);
    v.add (comp);
    return new DataObject(QUERY_ITEM, v);
  }
}//class end
