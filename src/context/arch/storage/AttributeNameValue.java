package context.arch.storage;

import context.arch.comm.DataObject;

import java.util.Hashtable;
import java.util.Vector;

/**
 * This class is a container for an attribute name, value and type.
 */
public class AttributeNameValue extends Attribute {

  protected Object value;

  /**
   * Tag for an attribute name/value pair
   */
  public static final String ATTRIBUTE_NAME_VALUE = "attributeNameValue";

  /**
   * Tag for an attribute name
   */
  public static final String ATTRIBUTE_NAME = "attributeName";

  /**
   * Tag for an attribute value
   */
  public static final String ATTRIBUTE_VALUE = "attributeValue";

  /**
   * Empty constructor
   */
  public AttributeNameValue() {
  }

  /**
   * Constructor that takes only a name
   *
   * @param name Name of attribute to store
   */
  public AttributeNameValue(String name) {
    this.name = name;
    value = null;
    type = null;
  }

  /**
   * Constructor that takes a name, value and type
   *
   * @param name Name of attribute to store
   * @param value Value of attribute to store
   * @param type Datatype of attribute to store
   */
  public AttributeNameValue(String name, Object value, String type) {
    this.name = name;
    this.value = value;
    this.type = type;
  }

  /**
   * Constructor that takes a DataObject as input.  The DataObject
   * must have <ATTRIBUTE_NAME_VALUE> as its top-level tag
   *
   * @param attribute DataObject containing the attribute info
   */
  public AttributeNameValue(DataObject attribute) {
    type = DEFAULT_TYPE;
    Hashtable hash = attribute.getAttributes();
    if (hash != null) {
      Object o = hash.get(ATTRIBUTE_TYPE);
      if (o != null) {
        type = (String)o;
      }
    }
    if (type.equals(STRUCT)) {
      value = new Attributes(attribute.getDataObject(Attributes.ATTRIBUTES));
    }
    else {
      Vector val = attribute.getDataObject(ATTRIBUTE_VALUE).getValue();
      value = val.firstElement();
    }
    name = (String)attribute.getDataObject(ATTRIBUTE_NAME).getValue().firstElement();
  }

  /**
   * Converts this object to a DataObject.
   *
   * @return AttributeNameValue object converted to an <ATTRIBUTE_NAME_VALUE> DataObject
   */
  public DataObject toDataObject() {
    if (type.equals(STRUCT)) {
      Vector u = new Vector();
      u.addElement(new DataObject(ATTRIBUTE_NAME,name));
      u.addElement(((Attributes)value).toDataObject());
      DataObject dobj = new DataObject(ATTRIBUTE_NAME_VALUE,u);
      Hashtable hash = new Hashtable();
      hash.put(ATTRIBUTE_TYPE,STRUCT);
      dobj.setAttributes(hash);
      return dobj;
    }
    else {
      Vector vec = new Vector();
      vec.addElement(new DataObject(ATTRIBUTE_NAME, name));
      Vector v = new Vector();
      v.addElement(String.valueOf(getValue()));
      vec.addElement(new DataObject(ATTRIBUTE_VALUE, v));
      DataObject dobj = new DataObject(ATTRIBUTE_NAME_VALUE, vec);
      Hashtable hash = new Hashtable();
      if (!(type.equals(STRING))) {
        hash.put(ATTRIBUTE_TYPE,type);
        dobj.setAttributes(hash);
      }
      return dobj;
    }
  }  

  /**
   * Sets the value of an attribute
   *
   * @param value Value of the attribute to store
   */
  public void setValue(Object value) {
    this.value = value;
  }

  /**
   * Returns the value of the stored attribute
   *
   * @return value of the stored attribute
   */
  public Object getValue() {
    return value;
  }

  /**
   * A printable version of this class.
   *
   * @return String version of this class
   */
  public String toString() {
    return new String("[name="+getName()+",value="+getValue()+",type="+getType()+"]");
  }
}
