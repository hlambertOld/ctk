/*
 * AbstractQueryItem.java
 *
 * Created on July 5, 2001, 3:20 PM
 */

package context.arch.discoverer.querySystem;

import context.arch.discoverer.dataModel.AbstractDataModel;
import context.arch.comm.DataObject;

import java.util.HashMap;

/**
 * Design pattern : Composite
 * 
 * An AbstractQueryItem is the abstract element to create queries.
 * 
 * A query may contain QueryItem or AbstractBooleanQueryItem.
 * A QueryItem specifies the type of element searched, the value searched and
 * a type of comparison.
 * 
 * For example : 
 *    type of element = type
 *    value = widget
 *    comparison = equal
 * Another example :
 *    type of element = attribute
 *    value = username
 *    comparison = equal
 * 
 * A AbstractBooleanQueryItem is just a node that contains 1 or 2 other
 * AbstractQueryItem objects.
 *
 * A query is a tree. To process a query, the leaves of this tree returns an
 * array that contains for all existing components in the data model, if each
 * components suits the query or not (0 or 1).
 *
 * Example of query :
 * QueryItem q1 = new QueryItem(new IdElement("PersonNamePresence2_rigatoni_1655")); // by default Equal()
 * // IdElement implements IndexTableIF
 * QueryItem q2 = new QueryItem(new PortElement("1520"),new GreaterEqual());
 * ANDQueryItem and = new ANDQueryItem(q1, q2);
 * result = and.process(abstractDataModel); // abstractDataModel gets access to all IndexTableIF objects.
 *
 * There are 2 ways of processing a query :
 * - when the query is processed for all existing objects in the AbstractDataModel 
 * - when the query is processed only for one object (to handle the notification)
 *
 *
 *                      ------------------------------------------
 *                      |         AbstractQueryItem              |
 *                      ------------------------------------------
 *                      |    process(AbstractDataModel) : Object |
 *                      |    process(Object) : boolean           |
 *                      ------------------------------------------
 *                             /\                  /\
 *                              |                   |
 *    --------------------------------------        |
 *    |  QueryItem                         |        | 
 *    --------------------------------------        |
 *    |comparison : AbstractComparison     |        |
 *    |elToMatch : AbstractDescriptionElement|     -------------------------------
 *    --------------------------------------       |    AbstractBooleanQueryItem |
 *    |process(AbstractDataModel) : Object |       -------------------------------
 *    |process(Object) : boolean           |       |son : AbstractQueryItem      |
 *    --------------------------------------       |brother : AbstractQueryItem  |
 *                                                 -------------------------------
 *                                                            /\
 *                                                             |
 *                  ___________________________________________|________________________________________
 *                  |                                          |                                         |
 * -------------------------------------- -------------------------------------- --------------------------------------
 * |          ANDQueryItem              | |        ORQueryItem                 | |           NOTQueryItem             |                     
 * -------------------------------------- -------------------------------------- --------------------------------------
 * |process(AbstractDataModel) : Object | |process(AbstractDataModel) : Object | |process(AbstractDataModel) : Object |
 * |process(Object) : boolean           | |process(Object) : boolean           | |process(Object) : boolean           |
 * -------------------------------------  -------------------------------------  --------------------------------------
 *
 * @author  Agathe
 * @see context.arch.discoverer.dataModel
 */
public abstract class AbstractQueryItem {

  public static final String ABSTRACT_QUERY_ITEM = "abstractQueryItem";

  /**
   * This method allows to process an 2D array that contains in the first
   * column the index of the component stored in the data model, and in 
   * the second column a integer value : 0 or 1 for false or true.
   *
   * @param dataModel
   * @return Object
   */
  public abstract Object process(AbstractDataModel dataModel);
  
  /**
   * Returns true if a component fits this query
   *
   * @param component
   * @return boolean
   */
  public abstract boolean process(Object component);
  
  /**
   * Returns a printable version of this object
   *
   * @return String
   */
  public abstract String toString();

  /**
   * Returns a string displaying a complex object
   *
   * @param table
   * @return String
   */
  public static String arrayToString(Object table){
    if(table instanceof int[][]){
      int [][] tab = (int [][])table;
      StringBuffer sb = new StringBuffer();
      for (int i = 0 ; i < tab.length ; i++){
        sb.append ("\n" + tab[i][0] + " -> " + tab[i][1]);
      }
      return sb.toString ();
    }
    return "";
  }
  
  /**
   * Convert an DataObject into an AbstractQueryItem object
   * 
   * @param data
   * @return AbstractQueryItem
   */
  public static AbstractQueryItem fromDataObject(DataObject data) { 
    // May be a QueryItem or a AbstractBooleanQueryItem
    if (data != null){
      //System.out.println("AbstractQueryItem fromDataObject name="+data.getName ());
      String name = data.getName ();
      if (name.equals (QueryItem.QUERY_ITEM)){
        return QueryItem.fromDataObject (data);
      }
      else if (name.equals (AbstractBooleanQueryItem.ABSTRACT_BOOLEAN_QUERY_ITEM)){
        return AbstractBooleanQueryItem.fromDataObject (data);
      }
    }
    return null; 
  }
  
  /**
   * Returns a DataObject version of this object
   * 
   * @return DataObject
   */
  public abstract DataObject toDataObject();
  
}
