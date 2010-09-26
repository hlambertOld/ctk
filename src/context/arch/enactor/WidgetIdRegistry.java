package context.arch.enactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A registry used by the EnactorSubscriptionManager to track widgets.
 * 
 * @author newbergr
 */
public class WidgetIdRegistry {
  public WidgetIdRegEntry get(String widgetId) {
    return (WidgetIdRegEntry) map.get(widgetId);
  }

  public WidgetIdRegEntry remove(String widgetId) {
    return (WidgetIdRegEntry) map.remove(widgetId);
  }

  public WidgetIdRegEntry put(String widgetId, WidgetIdRegEntry re) {
    return (WidgetIdRegEntry) map.put(widgetId, re);
  }

  private HashMap map = new HashMap();

  static class WidgetIdRegEntry {
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
