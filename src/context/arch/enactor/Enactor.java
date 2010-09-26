package context.arch.enactor;

import java.util.ArrayList;
import java.util.Iterator;

import context.arch.discoverer.ComponentDescription;
import context.arch.storage.Attribute;
import context.arch.storage.AttributeNameValue;
import context.arch.storage.Attributes;

/**
 * The Enactor component encapsulates application logic and simplifies
 * the acquisition of context data. Use it by making subclasses that
 * define their own EnactorReferences and EnactorParameters. To be useful,
 * an Enactor should have an EnactorSubscriptionManager set upon it. 
 * 
 * @author alann
 */
public abstract class Enactor {
  protected EnactorSubscriptionManager subscriptionManager;
  //we keep one listener and use a thread-safe multicaster
  protected EnactorListener enactorListener;
  private ArrayList enactorParameters = new ArrayList();
  private ArrayList enactorReferences = new ArrayList();

  protected void setSubscriptionManager(EnactorSubscriptionManager sm) {
    //TODO: if already set, remove widget descriptions from old sm
    subscriptionManager = sm;
  }
  
  public EnactorSubscriptionManager getSubscriptionManager() {
    return subscriptionManager;
  }

  protected boolean addEnactorParameter(EnactorParameter ep) {
  	ep.setEnactor(this);
    return enactorParameters.add(ep);
  }

  protected boolean removeEnactorParameter(EnactorParameter ep) {
  	ep.setEnactor(null);
    return enactorParameters.remove(ep);
  }

  public EnactorParameter getEnactorParameter(String name) {
    if (name != null) {
      Iterator i = enactorParameters.iterator();
      while (i.hasNext()) {
        EnactorParameter rp = (EnactorParameter) i.next();
        if (name.equals(rp.getName()))
          return rp;
      }
    }
    return null;
  }

  public Iterator getEnactorParameters() {
    return enactorParameters.iterator();
  }

  protected boolean addEnactorReference(EnactorReference er) {
    er.setEnactor(this);
    return enactorReferences.add(er);
  }

  protected boolean removeEnactorReference(EnactorReference er) {
  	er.setEnactor(null);
    return enactorReferences.remove(er);
  }

  public Iterator getEnactorReferences() {
    return enactorReferences.iterator();
  }

  //////////////////////
  // Begin Listener Code
  //////////////////////
    
  public void addListener(EnactorListener sml) {
    fireInitialAddEvents(sml);
    enactorListener = EnactorListenerMulticaster.add(enactorListener, sml);
  }
    
  public void removeListener(EnactorListener sml) {
    enactorListener = EnactorListenerMulticaster.remove(enactorListener, sml);
    fireFinalRemoveEvents(sml);
  }
  
  // fire methods that call listeners. Refer to EnactorListener class for documentation.
  protected final void fireComponentEvaluated(EnactorReference rwr, ComponentDescription cd) {
    fireComponentEvaluated(enactorListener, rwr, cd);
  }
  
  protected final void fireComponentAdded(EnactorReference rwr, String widgetSubId, ComponentDescription cd, Attributes paramAtts) {
    fireComponentAdded(enactorListener, rwr, widgetSubId, cd, paramAtts);
  }
  
  protected final void fireComponentRemoved(EnactorReference rwr, String widgetSubId, ComponentDescription cd, Attributes paramAtts) {
    fireComponentRemoved(enactorListener, rwr, widgetSubId, cd, paramAtts);
  }
  
  protected final void fireParameterValueChanged(EnactorParameter parameter, Attributes paramAtts, Object value) {
    fireParameterValueChanged(enactorListener, parameter, paramAtts, value);
  }
    
  protected void fireComponentEvaluated(EnactorListener listener, EnactorReference rwr, ComponentDescription cd) {
    if (listener != null) listener.componentEvaluated(rwr, cd);
  }
    
  protected void fireComponentAdded(EnactorListener listener, EnactorReference rwr, String widgetSubId, ComponentDescription cd, Attributes paramAtts) {
    if (listener != null) listener.componentAdded(rwr, cd, paramAtts);
  }
    
  protected void fireComponentRemoved(EnactorListener listener, EnactorReference rwr, String widgetSubId, ComponentDescription cd, Attributes paramAtts) {
    if (paramAtts == null) {
      paramAtts = new Attributes();
    }
    if (listener != null) listener.componentRemoved(rwr, cd, paramAtts);
  }
  
  protected void fireParameterValueChanged(EnactorListener listener, EnactorParameter parameter, Attributes paramAtts, Object value) {
    if (paramAtts == null) {
      paramAtts = new Attributes();
    }
    if (listener != null) listener.parameterValueChanged(parameter, paramAtts, value);
  }

  //semantics-preserving methods

  /**
   * fires add events to "catch up" listeners to the state of the enactor.
   * 
   * @param sml
   */
  private void fireInitialAddEvents(EnactorListener sml) {
    if (subscriptionManager != null) subscriptionManager.fireAddEventsForAll(this, sml);
  }
 
  /**
   * fires remove events to "catch up" listeners to the state of the enactor.
   * 
   * @param sml
   */
  private void fireFinalRemoveEvents(EnactorListener sml) {
    // TODO Auto-generated method stub
    
  }


  ////////////////////
  // End Listener Code
  ////////////////////

  /**
   * convenience method until we fix ComponentDescriptions to do this right...
   * 
   * TODO: modify CTK componentDescriptions so that we can access Attributes sanely
   */
  protected Object getAtt(String key, Iterator attributeIterator) {
    Object ret = null;
    if (key != null) {
      while(attributeIterator.hasNext()) {
        Attribute att = (Attribute) attributeIterator.next();
        if (key.equals(att.getName())) {
          ret = (att instanceof AttributeNameValue) ? ((AttributeNameValue)att).getValue() : null;
        }
      }
    }
    return ret;
  }
}
