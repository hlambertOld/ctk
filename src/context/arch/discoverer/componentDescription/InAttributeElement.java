/*
 * InAttElement.java
 *
 * Created on July 6, 2001, 1:37 PM
 */

package context.arch.discoverer.componentDescription;

import context.arch.storage.Attribute;
import context.arch.discoverer.querySystem.comparison.AbstractComparison;
import context.arch.storage.Attribute;
import context.arch.discoverer.ComponentDescription;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author  Agathe
 */
public class InAttributeElement extends AbstractDescriptionElement {

  public static final String IN_ATT_ELEMENT = "inAtt";
  
  /** Creates new InAttElement */
  public InAttributeElement () {
    super(IN_ATT_ELEMENT);
  }
  
  /** */
  public InAttributeElement (Object inAttributeOrName){
    this();
    setValue(inAttributeOrName);
  }
  
 
  /** */
  public void setValue(Object inAttributeOrName){
    if (inAttributeOrName instanceof String) {
      // By default, set the value as a   
      super.setValue(inAttributeOrName);
    }
    else if (inAttributeOrName instanceof Attribute){
      super.setValue(((Attribute)inAttributeOrName).getName());
    }
  }
  
  /**
   *
   */
  public Object extractElement(Object componentDescription){
    return ((ComponentDescription) componentDescription).getInAttributes ();
  }
  
  /**
   *
   */
  public boolean processQueryItem(Object componentDescription, AbstractComparison operator){
    boolean result = false;
    
    Iterator list = ((Collection)extractElement(componentDescription)).iterator();
    while (list.hasNext()){
      result = operator.compare(list.next (), getValue());
      if (result) break;
    }
    return result;
  }
  
}//class end
