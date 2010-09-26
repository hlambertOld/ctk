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
public class NonConstantAttributeElement extends AttributeElement {
  public static final String NON_CONST_ATT_NAME_ELEMENT = "nonCstAttName";
  
  public static final String NON_CONST_ATT_VALUE_ELEMENT = "nonCstAttValue";
  
  public static final String NON_CONST_ATT_NAME_VALUE_ELEMENT = "nonCstAttNameValue";
  
  public NonConstantAttributeElement(Object attributeNameValue){
    super(attributeNameValue);
  }
  
  public NonConstantAttributeElement(Object stringName, Object stringValue){
    super(stringName,stringValue);
  }

  public NonConstantAttributeElement(Object stringName, Object stringValue, String type){
    super(stringName,stringValue,type);
  }

  public String getNameElement() {
    return NON_CONST_ATT_NAME_ELEMENT;
  }

  public String getValueElement() {
    return NON_CONST_ATT_VALUE_ELEMENT;
  }

  public String getNameValueElement() {
    return NON_CONST_ATT_NAME_VALUE_ELEMENT;
  }

  public Object extractElement(Object componentDescription){
    return ((ComponentDescription) componentDescription).getNonConstantAttributes();
  }

}//class end
