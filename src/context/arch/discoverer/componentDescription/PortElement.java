/*
 * PortElement.java
 *
 * Created on July 6, 2001, 8:39 AM
 */

package context.arch.discoverer.componentDescription;

import context.arch.discoverer.querySystem.comparison.AbstractComparison;
import context.arch.discoverer.ComponentDescription;

/**
 *
 * @author  Agathe
 */
public class PortElement extends AbstractDescriptionElement {
  
  public static final String PORT_ELEMENT = "port";
  
  /** Creates new IdElement */
  public PortElement () {
    super(PORT_ELEMENT);
  }
  
  /** */
  public PortElement (Object value){
    this();
    setValue(value);
  }
  
  /** */
  public void setValue(Object value){
    if (value instanceof String)
      super.setValue(value);
    else if (value instanceof Integer)
      super.setValue(value.toString ());
  }
  
  /**
   *
   */
  public Object extractElement(Object componentDescription){
    return Integer.toString(((ComponentDescription) componentDescription).port);
  }
  
  public boolean processQueryItem(Object componentDescription, AbstractComparison operator){
    ComponentDescription comp = (ComponentDescription) componentDescription;
    int port = comp.port;
    return operator.compare(  new Integer(port),
                              this.getValue ());
  }

}
