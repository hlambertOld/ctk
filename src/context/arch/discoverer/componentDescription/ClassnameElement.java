/*
 * ClassnameElement.java
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
public class ClassnameElement extends AbstractDescriptionElement {
  
  public static final String CLASSNAME_ELEMENT = "classname";
  
  /** Creates new IdElement */
  public ClassnameElement () {
    super(CLASSNAME_ELEMENT);
  }
  
  /** */
  public ClassnameElement (Object value){
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
    return ((ComponentDescription) componentDescription).classname;
  }

  public boolean processQueryItem(Object componentDescription, AbstractComparison operator){
    return operator.compare(  ((ComponentDescription) componentDescription).classname,
                              this.getValue ());
  }
  
}
