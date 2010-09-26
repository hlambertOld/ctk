/*
 * CallbackElement.java
 *
 * Created on July 6, 2001, 1:37 PM
 */

package context.arch.discoverer.componentDescription;

import context.arch.storage.Attribute;
import context.arch.discoverer.querySystem.comparison.AbstractComparison;
import context.arch.subscriber.Subscriber;
import context.arch.discoverer.ComponentDescription;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author  Agathe
 */
public class SubscriberElement extends AbstractDescriptionElement {

  public static final String SUBSCRIBER_ELEMENT = "subscriber";
  
  /** Creates new CallbackElement */
  public SubscriberElement () {
    super(SUBSCRIBER_ELEMENT);
  }
  
  /** */
  public SubscriberElement (Object subscriberOrName){
    this();
    setValue(subscriberOrName);
  }
  
 
  /** */
  public void setValue(Object subscriberOrName){
    if (subscriberOrName instanceof String) {
      super.setValue(subscriberOrName);
    }
    else if (subscriberOrName instanceof Subscriber){
      super.setValue(((Subscriber)subscriberOrName).getSubscriptionId ());
    }
  }
  
  /**
   *
   */
  public Object extractElement(Object componentDescription){
    return ((ComponentDescription) componentDescription).getSubscribers ();
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
