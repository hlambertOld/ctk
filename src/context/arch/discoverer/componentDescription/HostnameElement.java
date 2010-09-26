/*
 * HostnameElement.java
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
public class HostnameElement extends AbstractDescriptionElement {
  
  public static final String HOSTNAME_ELEMENT = "hostname";
  
  public static final String HOSTADDRESS_ELEMENT = "hostaddress";
  
  /** Creates new IdElement */
  public HostnameElement () {
    super(HOSTNAME_ELEMENT);
  }
  
  /** */
  public HostnameElement (Object value){
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
    return (String)((ComponentDescription) componentDescription).hostname;
  }
  
  public boolean processQueryItem(Object componentDescription, AbstractComparison operator){
    return ( operator.compare(  ((ComponentDescription) componentDescription).hostname,
                              this.getValue ())
             || operator.compare(  ((ComponentDescription) componentDescription).hostaddress,
                              this.getValue ()));
  }

}
