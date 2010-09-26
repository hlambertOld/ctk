package context.arch.enactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A registry used by the EnactorSubscriptionManager to track references to widgets.
 * 
 * @author newbergr
 */
public class WidgetReferenceRegistry {
  public WidgetReferenceRegEntry get(EnactorReference rwr) {
    return (WidgetReferenceRegEntry) map.get(rwr);
  }

  public WidgetReferenceRegEntry remove(EnactorReference rwr) {
    return (WidgetReferenceRegEntry) map.remove(rwr);
  }

  public WidgetReferenceRegEntry put(EnactorReference rwr, WidgetReferenceRegEntry re) {
    return (WidgetReferenceRegEntry) map.put(rwr, re);
  }

  public Set getWidgetReferences() {
    return map.keySet();
  }

  private HashMap map = new HashMap();

  static class WidgetReferenceRegEntry {
    public boolean addWidgetSubscription(String subId) {
      return widgetSubscriptions.add(subId);
    }
    
    public boolean removeWidgetSubscription(String subId) {
      return widgetSubscriptions.remove(subId);
    }
    
    public List getWidgetSubscriptions() {
      return retWR; 
    }

    private ArrayList widgetSubscriptions = new ArrayList();
    private List retWR = Collections.unmodifiableList(widgetSubscriptions);        
  }
}
