/*
 * BooleanQueryItem.java
 *
 * Created on July 5, 2001, 4:17 PM
 */

package context.arch.discoverer.querySystem;

import context.arch.discoverer.dataModel.AbstractDataModel;
import context.arch.comm.DataObject;

import java.util.HashMap;
import java.util.Vector;
import java.util.Enumeration;

/**
 *
 * @author  Agathe
 */
public abstract class AbstractBooleanQueryItem extends AbstractQueryItem {

  /** Tags for DataObject building */
  public static final String ABSTRACT_BOOLEAN_QUERY_ITEM = "abBoolQI";
  
  public static final String NAME = "name";
  
  public static final String LEFT = "left";
  
  public static final String RIGHT = "right";
  
  /** the left element : exists in all cases */
  protected AbstractQueryItem left;
  
  /** the right element : doesn't exist in all the cases*/
  protected AbstractQueryItem right;

  /** the name of this boolean condition object */
  protected String booleanCondition;
  
  /**
   * Creates new BooleanQueryItem. It corresponds to a node in a tree
   *
   * @param type The name of this element
   * @param left 
   */
  public AbstractBooleanQueryItem(String type, AbstractQueryItem left){
    this(type, left, null);
  }
  
  /** 
   * Creates new BooleanQueryItem. It corresponds to a node in a tree
   *
   * @param type The name of this element
   * @param left 
   * @param right
   */
  public AbstractBooleanQueryItem (String type, AbstractQueryItem left,AbstractQueryItem right) {
    this.booleanCondition = type;
    this.left = left;
    this.right = right;
  }
  
  /**
   * This method allows to process the boolean condition on the whole 
   * data model
   */
  public abstract Object process(AbstractDataModel dataModel);

  /**
   * Returns true if the component fits the query item
   * 
   * @param
   * @return boolean
   */
  public abstract boolean process(Object component);

  /** Returns the name of this element : the boolean condition name */
  public String getBooleanCondition(){
    return booleanCondition;
  }
  
  /**
   * Returns a printable version of this object
   */
  public String toString(){
    StringBuffer sb = new StringBuffer(getBooleanCondition ());
    sb.append ("\n\tleft= " + left);
    if (right != null)
      sb.append ("\n\tright= " + right);
    return sb.toString ();
  }
  
  /**
   * Return the DataObject version of this object
   *
   * @return DataObject
   */
  public DataObject toDataObject(){
    Vector v = new Vector();
    Vector l = new Vector();
    Vector r;
    
    v.add (new DataObject(NAME, getBooleanCondition ()));
    
    l.add (left.toDataObject());
    v.add (new DataObject(LEFT, l));
    
    if (right != null){
      r = new Vector();
      r.add (right.toDataObject());
      v.add (new DataObject (RIGHT, r));
    }
    return new DataObject(ABSTRACT_BOOLEAN_QUERY_ITEM, v);
  }

  /**
   * Takes a dataObject and returns an AbstracQueryItem object
   * 
   * @param data
   * @return
   */
  public static AbstractQueryItem fromDataObject(DataObject data) { 
    String name = (String) (data.getDataObject (NAME)).getValue().firstElement();
    AbstractQueryItem l = null, r = null;
    
    Vector children = data.getChildren ();
    Enumeration list = children.elements ();
    DataObject el;
    while (list.hasMoreElements ()){
      el = (DataObject) list.nextElement ();
      if (el.getName ().equals (LEFT)){
        l = AbstractQueryItem.fromDataObject ((DataObject)el.getValue ().firstElement ());
      }
      else if (el.getName ().equals (RIGHT)){
        r = AbstractQueryItem.fromDataObject ((DataObject)el.getValue ().firstElement ());
      }
    }
    return AbstractBooleanQueryItem.factory (name, l, r);
  }
  
  /**
   * Returns the right object corresponding to the specified name
   *
   * @param name The name of the object
   * @param left
   * @param right
   * return AbstractBooleanQueryItem
   */
  public static AbstractBooleanQueryItem factory(String name, AbstractQueryItem left, AbstractQueryItem right){
    if (name.equals (ANDQueryItem.AND_QUERY_ITEM)){
      return new ANDQueryItem(left, right);
    }
    else if (name.equals (ORQueryItem.OR_QUERY_ITEM)){
      return new ORQueryItem(left, right);
    }
    else if (name.equals (NOTQueryItem.NOT_QUERY_ITEM)){
      return new NOTQueryItem(left);
    }
    return null;
  }
  
}
