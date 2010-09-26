/*
 * ServiceElement.java
 *
 * Created on July 6, 2001, 1:37 PM
 */

package context.arch.discoverer.componentDescription;

import context.arch.storage.Attribute;
import context.arch.discoverer.querySystem.comparison.AbstractComparison;
import context.arch.service.Service;
import context.arch.discoverer.ComponentDescription;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author  Agathe
 */
public class ServiceElement extends AbstractDescriptionElement {

  public static final String SERVICE_ELEMENT = "service";
  
  /** Creates new ServiceElement */
  public ServiceElement () {
    super(SERVICE_ELEMENT);
  }
  
  /** */
  public ServiceElement (Object serviceOrName){
    this();
    setValue(serviceOrName);
  }
  
 
  /** */
  public void setValue(Object serviceOrName){
    if (serviceOrName instanceof String) {
      super.setValue(serviceOrName);
    }
    else if (serviceOrName instanceof Service){
      super.setValue(((Service)serviceOrName).getName());
    }
  }
  
  /**
   *
   */
  public Object extractElement(Object componentDescription){
    return ((ComponentDescription) componentDescription).getServices();
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
