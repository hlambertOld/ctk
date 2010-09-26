/*
 * CallbackElement.java
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
public class CallbackElement extends AbstractDescriptionElement {

  public static final String CALLBACK_ELEMENT = "callback";
  
  /** Creates new CallbackElement */
  public CallbackElement () {
    super(CALLBACK_ELEMENT);
  }
  
  /** */
  public CallbackElement (Object callbackName){
    this();
    setValue(callbackName);
  }
  
 
  /** */
  public void setValue(Object callbackName){
    if (callbackName instanceof String) {
      // By default, set the value as a   
      super.setValue((String)callbackName);
    }
    else if (callbackName instanceof Callback){
      super.setValue(((Callback)callbackName).getName());
    }
  }
  
  /**
   *
   */
  public Object extractElement(Object componentDescription){
    return ((ComponentDescription) componentDescription).getCallbacks();
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
    System.out.println("Callback res " + result);
    return result;
  }
  
}//class end
