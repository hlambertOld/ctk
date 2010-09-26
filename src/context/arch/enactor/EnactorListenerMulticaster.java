package context.arch.enactor;

import context.arch.discoverer.ComponentDescription;
import context.arch.storage.Attributes;

/**
 * An event multicaster, for thread-safe enactor event broadcasting.
 * 
 * @author alann
 */
class EnactorListenerMulticaster implements EnactorListener {

	private final EnactorListener a, b;
	
	EnactorListenerMulticaster(EnactorListener a, EnactorListener b) {
		this.a = a; this.b = b;
	}

  static EnactorListener add(EnactorListener a, EnactorListener b) {
    return addInternal(a, b);
  }

  static EnactorListener remove(EnactorListener l, EnactorListener oldl) {
    return removeInternal(l, oldl);
  }

	private EnactorListener remove(EnactorListener oldl) {
    if (oldl == a)  return b;
    if (oldl == b)  return a;
    EnactorListener a2 = removeInternal(a, oldl);
    EnactorListener b2 = removeInternal(b, oldl);
    if (a2 == a && b2 == b) {
      return this;	// it's not here
    }
    return addInternal(a2, b2);
  }

  private static EnactorListener addInternal(EnactorListener a, EnactorListener b) {
    if (a == null)  return b;
    if (b == null)  return a;
    return new EnactorListenerMulticaster(a, b);
  }

  private static EnactorListener removeInternal(EnactorListener l, EnactorListener oldl) {
    if (l == oldl || l == null) {
      return null;
    } else if (l instanceof EnactorListenerMulticaster) {
      return ((EnactorListenerMulticaster)l).remove(oldl);
    } else {
      return l;   // it's not here
    }
  }
  
  public void componentEvaluated(EnactorReference rwr, ComponentDescription widgetDescription) {
    a.componentEvaluated(rwr, widgetDescription);
    b.componentEvaluated(rwr, widgetDescription);
  }

  public void componentAdded(EnactorReference rwr, ComponentDescription widgetDescription, Attributes paramAtts) {
    a.componentAdded(rwr, widgetDescription, paramAtts);
    b.componentAdded(rwr, widgetDescription, paramAtts);
  }

  public void componentRemoved(EnactorReference rwr, ComponentDescription widgetDescription, Attributes paramAtts) {
    a.componentRemoved(rwr, widgetDescription, paramAtts);
    b.componentRemoved(rwr, widgetDescription, paramAtts);
  }

  public void parameterValueChanged(EnactorParameter parameter, Attributes validAtts, Object value) {
    a.parameterValueChanged(parameter, validAtts, value);
    b.parameterValueChanged(parameter, validAtts, value);
  }

}
