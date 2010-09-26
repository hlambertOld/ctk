package context.arch.discoverer.componentDescription;

import context.arch.storage.Attribute;
import context.arch.storage.AttributeNameValue;
import context.arch.discoverer.querySystem.comparison.AbstractComparison;

import java.util.StringTokenizer;
import java.util.Collection;
import java.util.Iterator;

/**
 * 
 * 
 * @author newbergr
 */
public abstract class AttributeElement extends AbstractDescriptionElement {
  public static final String SEPARATOR = "+";//context.arch.discoverer.Discoverer.FIELD_SEPARATOR;
  
  protected String type;
  
  protected AttributeElement(Object attributeNameValue){
    setValue(attributeNameValue);
  }
  
  protected AttributeElement(Object stringName, Object stringValue){
    this(stringName, stringValue, null);
  }
  
  protected AttributeElement(Object stringName, Object stringValue, String type){
    if (stringName != null && stringValue != null){
      setAttributeNameValue(stringName, stringValue);
      elementName = getNameValueElement();
    }
    else if (stringName == null && stringValue != null){
      setAttributeValue(stringValue);
      elementName = getValueElement();
    }
    else if (stringName != null && stringValue == null){
      setAttributeName(stringName);
      elementName = getNameElement();
    }
    this.type = type;
  }

  public abstract String getNameElement();
  public abstract String getValueElement();
  public abstract String getNameValueElement();

  /** */
  public void setValue(Object attribute){
    if (attribute instanceof String) {
      // By default, set the value as a   
      //setAttributeValue(attributeNameValue);
      StringTokenizer st = new StringTokenizer((String)attribute, SEPARATOR, true);
      String first = st.nextToken();
      if (first.equals(SEPARATOR)){
        if (st.hasMoreTokens()){
          setAttributeValue(st.nextToken());
          elementName = getValueElement();
        }
      }
      else {
        if (st.hasMoreTokens()) {
          if (st.nextToken().equals(SEPARATOR)){
            setAttributeNameValue(first, st.nextToken());
            elementName = getNameValueElement();
          }
        }
        else {
          setAttributeName(first);
          elementName = getNameElement();
        }
      }
    }
    else if (attribute instanceof AttributeNameValue){
      setAttributeNameValue(((AttributeNameValue)attribute).getName(), ((AttributeNameValue)attribute).getValue());
    }
    else if (attribute instanceof Attribute) {
      setAttributeName(((Attribute)attribute).getName());
    }
  }
  
  public void setAttributeName(Object attName){
    super.setValue(attName.toString() );
  }
  
  public void setAttributeValue(Object attValue){
    super.setValue(SEPARATOR + attValue.toString ());
  }
  
  public void setAttributeNameValue(Object attName, Object attValue){
    super.setValue (attName.toString () + SEPARATOR + attValue.toString ());
  }
  
  private String getAttName(){
    StringTokenizer st = new StringTokenizer((String)getValue(),SEPARATOR, true);
    String name = null; 
    String temp = st.nextToken();
    if (!SEPARATOR.equals(temp)){
      name = temp;
    }
    return name;
  }
  
    private String getAttValue(){
    StringTokenizer st = new StringTokenizer((String)getValue(),SEPARATOR, true);
    String val = null;
    String temp = st.nextToken(); 
    if (temp.equals (SEPARATOR)){
      val = st.nextToken();
    }
    else {
      if (st.hasMoreTokens()) {
        st.nextToken(); // the separator
        val = st.nextToken();
      }
    }
    return val;
  }

  
  public boolean processQueryItem(Object componentDescription, AbstractComparison operator){
    boolean result = false;
    String name = getAttName(); 
    String val = getAttValue();
    // Compare the names
    if (val == null && name != null){
      Iterator list = ((Collection)extractElement(componentDescription)).iterator();
      while (list.hasNext()){
        Attribute a = (Attribute) list.next();
        result = operator.compare(a.getName(), name);
        if (result && (type == null || type.equals(a.getType()))) break;
      }
    }
    // compares the values
    else if(name == null && val != null){
      Iterator list = ((Collection)extractElement(componentDescription)).iterator();
      while (list.hasNext()){
        try {
          AttributeNameValue anv = (AttributeNameValue) list.next();
          result = operator.compare(anv.getValue(), val);
          if (result && (type == null || type.equals(anv.getType()))) break;
        } catch (ClassCastException cce) {
          //if this specific Attribute was not an AttributeNameValue, just skip it
          continue;
        }
      }
    }
    // compares names+values
    else if (name != null && val != null){
      Iterator list = ((Collection)extractElement(componentDescription)).iterator();
      while (list.hasNext ()){
        try {
          AttributeNameValue anv = (AttributeNameValue) list.next();
          result = anv.getName().equalsIgnoreCase(name) && operator.compare(anv.getValue(), val);
          if (result && (type == null || type.equals(anv.getType()))) break;
        } catch (ClassCastException cce) {
          //if this specific Attribute was not an AttributeNameValue, just skip it
          continue;
        }
      }
    }
    return result;
  }
  
  
  public String toString(){
    return "Name=" + this.getElementName () + " value=" + this.getValue ();
  }
  
}
