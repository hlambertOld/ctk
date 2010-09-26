/*
 * IdElement.java
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
public class IdElement extends AbstractDescriptionElement {
  
  public static final String ID_ELEMENT = "id";
  
  /** Creates new IdElement */
  public IdElement () {
    super(ID_ELEMENT);
  }
  
  /** */
  public IdElement(Object value){
    this();
    setValue(value);
  }
  
  /** */
  public void setValue(Object value){
    if (value instanceof String)
      super.setValue((String)value);
  }
  
  /**
   *
   */
  public Object extractElement(Object componentDescription){
    return ((ComponentDescription) componentDescription).id;
  }

  public boolean processQueryItem(Object componentDescription, AbstractComparison operator){
    return operator.compare(  (String)extractElement(componentDescription),
                              this.getValue ());
    
  }
  
}
