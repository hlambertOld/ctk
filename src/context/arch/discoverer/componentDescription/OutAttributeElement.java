/*
 * OutAttElement.java
 *
 * Created on July 6, 2001, 1:37 PM
 */

package context.arch.discoverer.componentDescription;

import context.arch.storage.Attribute;
import context.arch.discoverer.querySystem.comparison.AbstractComparison;
import context.arch.subscriber.Callback;
import context.arch.discoverer.ComponentDescription;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author  Agathe
 */
public class OutAttributeElement extends AbstractDescriptionElement {

  public static final String OUT_ATT_ELEMENT = "outAtt";
  
  /** Creates new OutAttElement */
  public OutAttributeElement () {
    super(OUT_ATT_ELEMENT);
  }
  
  /** */
  public OutAttributeElement (Object outAttributeOrName){
    this();
    setValue(outAttributeOrName);
  }
  
 
  /** */
  public void setValue(Object outAttributeOrName){
    if (outAttributeOrName instanceof String) {
      super.setValue(outAttributeOrName);
    }
    else if (outAttributeOrName instanceof Attribute){
      super.setValue(((Attribute)outAttributeOrName).getName());
    }
  }
  
  /**
   *
   */
  public Object extractElement(Object componentDescription){
    return ((ComponentDescription) componentDescription).getOutAttributes ();
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
