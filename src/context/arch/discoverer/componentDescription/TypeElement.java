/*
 * TypeElement.java
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
public class TypeElement extends AbstractDescriptionElement {
  
  public static final String TYPE_ELEMENT = "type";
  
  /** Creates new IdElement */
  public TypeElement () {
    super(TYPE_ELEMENT);
  }
  
  /** */
  public TypeElement (Object value){
    this();
    setValue(value);
  }
  
  /** */
  public void setValue(Object value){
    if (value instanceof String)
      super.setValue(value);
  }
  
  /**
   *
   */
  public Object extractElement(Object componentDescription){
    return (String)((ComponentDescription) componentDescription).type;
  }

  public boolean processQueryItem(Object componentDescription, AbstractComparison operator){
    return operator.compare(  ((ComponentDescription) componentDescription).type,
                              this.getValue ());
  }
  
}
