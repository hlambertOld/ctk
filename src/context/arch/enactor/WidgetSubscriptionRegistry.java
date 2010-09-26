package context.arch.enactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import context.arch.discoverer.ComponentDescription;
import context.arch.subscriber.ClientSideSubscriber;

/**
 * A registry used by the EnactorSubscriptionManager to track subscriptions to widgets.
 * 
 * @author alann
 */
class WidgetSubscriptionRegistry {
	public WidgetSubscriptionRegEntry get(String subId) {
		return (WidgetSubscriptionRegEntry) map.get(subId);
	}

	public WidgetSubscriptionRegEntry remove(String subId) {
		return (WidgetSubscriptionRegEntry) map.remove(subId);
	}

	public WidgetSubscriptionRegEntry put(String subId, WidgetSubscriptionRegEntry re) {
		return (WidgetSubscriptionRegEntry) map.put(subId, re);
	}

  public Set getSubscriptionIds() {
    return map.keySet();
  }
	private HashMap map = new HashMap();

	/**
	 * state that is useful for tracking widgets we are subscribed to.
	 * we could in principle regenerate a correct regkey from the
	 * component description every time we needed it, but by keeping one
	 * around we hopefully cache the expensive hashCode calculation.
	 * 
	 * note that while the ComponenentDescription inside the regkey will
	 * match 'signatures' with componentDescription, only the componentDescription
	 * completely describes this particular widget (i.e. has its hostname, port,...).
	 * In fact, it is best to try and make the descriptionRegKey be the _exact_
	 * object instance as the key in the descriptionRegistry, because then
	 * registry lookups will be extremely efficient (validating on '==' instead of '.equals').
	 * This is not a strict requirement, however.
	 * 
	 * @author alann
	 */
	static class WidgetSubscriptionRegEntry {
    public boolean addWidgetReference(EnactorReference rwr) {
      return widgetReferences.add(rwr);
    }
    
    public boolean removeWidgetReference(EnactorReference rwr) {
      return widgetReferences.remove(rwr);
    }
    
    public List getWidgetReferences() {
      return retWR; 
    }
    
    public ComponentDescription getComponentDescription() {
      return componentDescription;
    }
    
    public void setComponentDescription(ComponentDescription cd) {
      componentDescription = cd;
    }
    
    public ClientSideSubscriber getClientSideSubscriber() {
      return subInfo;
    }
    
    public void setClientSideSubscriber(ClientSideSubscriber css) {
      subInfo = css;
    }
    
    public ComponentDescription getCurrentValues() {
      return currentState;
    }
    
    public void setCurrentValues(ComponentDescription cd) {
      currentState = cd;
    }

    private ArrayList widgetReferences = new ArrayList();
    private List retWR = Collections.unmodifiableList(widgetReferences);    
		private ComponentDescription componentDescription, currentState;
		private ClientSideSubscriber subInfo;
	}

}
