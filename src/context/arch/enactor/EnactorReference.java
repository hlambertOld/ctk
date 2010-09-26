package context.arch.enactor;

import context.arch.discoverer.ComponentDescription;
import context.arch.discoverer.querySystem.AbstractQueryItem;

/**
 * Fully describes a widget via a set of attributes, and a set of conditions on
 * those attributes.  Any attribute conditioned on should be represented in
 * Attributes.
 * 
 * TODO: enforce data correspondence, possible change over to collections.
 * 
 * @author alann
 */
public abstract class EnactorReference {
  protected AbstractQueryItem descriptionQuery;
  protected Enactor enactor;

  public void setEnactor(Enactor r) {
    enactor = r;
  }
  
  public Enactor getEnactor() {
    return enactor;
  }
  
  public void setDescriptionQuery(AbstractQueryItem dq) {
    descriptionQuery = dq;
  }
  
  public AbstractQueryItem getDescriptionQuery() {
    return descriptionQuery;
  }
  
  /**
   * This method is called when a new batch of state data concerning a widget
   * should be evaluated by this EnactorReference.
   * 
   * @param widgetSubId the unique identifier for the widget subscription
   * @param widgetState the current state of the widget
   */
  public void evaluateComponent(String componentSubId, ComponentDescription widgetState) {
    enactor.fireComponentEvaluated(this,widgetState);
  }
    
  /**
   * Called whenever a widget satisfies the EnactorReference
   * descriptionQuery. A call to this method is advance notice that
   * evaluateWidget will be called with this widgetSubId. A EnactorReference
   * may override this method to provide more custom parameter Attributes to be
   * set up by the Enactor, if unsure just leave the third arg null.
   * 
   * @param widgetSubId
   */
  public void componentAdded(String widgetSubId, ComponentDescription widgetState) {
    enactor.fireComponentAdded(this, widgetSubId, widgetState, null);
  }

  /**
   * Called whenever a widget no longer satisfies the descriptionQuery.
   * 
   * @param widgetSubId
   */
  public void componentRemoved(String widgetSubId, ComponentDescription widgetState) {
    enactor.fireComponentRemoved(this, widgetSubId, widgetState, null);
  }
}
