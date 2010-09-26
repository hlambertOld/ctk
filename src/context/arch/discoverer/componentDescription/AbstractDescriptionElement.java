/*
 * AbstractDescriptionElement.java
 *
 * Created on July 5, 2001, 3:36 PM
 */

package context.arch.discoverer.componentDescription;

import context.arch.discoverer.querySystem.comparison.AbstractComparison;
import context.arch.comm.DataObject;

import java.util.Vector;

/**
 *
 * @author  Agathe
 */
public abstract class AbstractDescriptionElement {
  
  /** Tag for DataObject version */
  public static final String ABSTRACT_DESCRIPTION_ELEMENT = "abDesElem";
  
  /** Tag for DataObject version */
  public static final String NAME = "abName";
  
  /** Tag for DataObject version */
  public static final String VALUE = "abValue";
  
  /** The name of this description element */
  protected String elementName;
  
  /** Value of this element */
  protected Object value;
  
  /** Creates new AbstractDescriptionElement */
  protected AbstractDescriptionElement (String elementName) {
    this (elementName, null);
  }
  
  /** Creates new AbstractDescriptionElement */
  protected AbstractDescriptionElement(String elementName, Object value){
    this.elementName = elementName;
    this.value = value;
  }

  protected AbstractDescriptionElement() {}

  /** Returns the name of this element*/
  public String getElementName(){
    return elementName;
  }
  
  /** Returns the value of this element */
  public Object getValue(){
    return value;
  }
  
  /** Sets the value of this element */
  public void setValue(Object value){
    if (value instanceof String)
      this.value = ((String)value).toLowerCase();
    else 
      this.value = value;
  }
  
  /**
   * Returns the element from the componentDescription corresponding to this 
   * description element
   *
   * @param componentDescription
   * @return Object
   */
  public abstract Object extractElement(Object componentDescription);
  
  /**
   * Returns true if the comparison between the value of this object and the value
   * of the corresponding field of the componentDescription returns true
   *
   * @param componentDescription The component to compare to this object
   * @param comparison The comparison element to use
   * @return boolean
   */
  public abstract boolean processQueryItem(Object componentDescription, AbstractComparison comparison);
  
  /** Returns a printable version */
  public String toString(){
    return getElementName() + " " + getValue();
  }
  
  /**
   * Returns the DataObject version
   */
  public DataObject toDataObject(){
    Vector v = new Vector();
    v.add (new DataObject (NAME, getElementName ()));
    v.add (new DataObject (VALUE, getValue ().toString ()));
    return new DataObject(ABSTRACT_DESCRIPTION_ELEMENT, v);
  }
  
  /**
   * Takes a DataObject and return an AbstractDescriptionElement object
   *
   * @param data
   * @return AbstractDescriptionElement
   */
  public static AbstractDescriptionElement fromDataObject(DataObject data){
    String name = (String) (data.getDataObject (NAME)).getValue ().firstElement ();
    Object value =  (data.getDataObject (VALUE)).getValue ().firstElement ();
    return AbstractDescriptionElement.factory (name, value);
  }
  
  /**
   * Returns the AbstracDescriptionElement corresponding to the specified name
   *
   * @param
   * @param
   * @return
   */
  public static AbstractDescriptionElement factory(String name, Object value){
    if (name.equals (IdElement.ID_ELEMENT)){
      return new IdElement(value);
    }
    else if (name.equals (PortElement.PORT_ELEMENT)){ 
      return new PortElement(value);
    }
    else if (name.equals (TypeElement.TYPE_ELEMENT)){ 
      return new TypeElement(value);
    }
    else if (name.equals (ClassnameElement.CLASSNAME_ELEMENT)){ 
      return new ClassnameElement(value);
    }
    else if (name.equals (HostnameElement.HOSTNAME_ELEMENT)
    || name.equals (HostnameElement.HOSTADDRESS_ELEMENT)){ 
      return new HostnameElement(value);
    }
    else if (name.equals (ConstantAttributeElement.CONST_ATT_NAME_ELEMENT)
    || name.equals (ConstantAttributeElement.CONST_ATT_VALUE_ELEMENT)
    || name.equals (ConstantAttributeElement.CONST_ATT_NAME_VALUE_ELEMENT)){ 
      return new ConstantAttributeElement(value);
    }
    else if (name.equals (NonConstantAttributeElement.NON_CONST_ATT_NAME_ELEMENT)
    || name.equals (NonConstantAttributeElement.NON_CONST_ATT_VALUE_ELEMENT)
    || name.equals (NonConstantAttributeElement.NON_CONST_ATT_NAME_VALUE_ELEMENT)){ 
      return new NonConstantAttributeElement(value);
    }
    else if (name.equals (ServiceElement.SERVICE_ELEMENT)){ 
      return new ServiceElement(value);
    }
    else if (name.equals (InAttributeElement.IN_ATT_ELEMENT)){ 
      return new InAttributeElement(value);
    }
    else if (name.equals (OutAttributeElement.OUT_ATT_ELEMENT)){ 
      return new OutAttributeElement(value);
    }
    else if (name.equals (SubscriberElement.SUBSCRIBER_ELEMENT)){ 
      return new SubscriberElement(value);
    }
    else if (name.equals (CallbackElement.CALLBACK_ELEMENT)){ 
      return new CallbackElement(value);
    }
    return null;
  }
  
  
}//class end
