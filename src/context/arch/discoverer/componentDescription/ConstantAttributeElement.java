/*
 * ConstantAttributeElement.java
 *
 * Created on July 6, 2001, 1:37 PM
 */

package context.arch.discoverer.componentDescription;

import context.arch.discoverer.ComponentDescription;

/**
 *
 * @author  Agathe
 */
public class ConstantAttributeElement extends AttributeElement {
  
  public static final String CONST_ATT_NAME_ELEMENT = "cstAttName";
  
  public static final String CONST_ATT_VALUE_ELEMENT = "cstAttValue";
  
  public static final String CONST_ATT_NAME_VALUE_ELEMENT = "cstAttNameValue";
  
  public ConstantAttributeElement(Object attributeNameValue){
    super(attributeNameValue);
  }
  
  public ConstantAttributeElement(Object stringName, Object stringValue){
    super(stringName,stringValue);
  }

  public ConstantAttributeElement(Object stringName, Object stringValue, String type){
    super(stringName,stringValue,type);
  }

  public String getNameElement() {
    return CONST_ATT_NAME_ELEMENT;
  }

  public String getValueElement() {
    return CONST_ATT_VALUE_ELEMENT;
  }

  public String getNameValueElement() {
    return CONST_ATT_NAME_VALUE_ELEMENT;
  }

  
  public Object extractElement(Object componentDescription){
    return ((ComponentDescription) componentDescription).getConstantAttributes();
  }

}//class end
