package context.arch.enactor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import context.arch.BaseObject;
import context.arch.InvalidMethodException;
import context.arch.MethodException;
import context.arch.comm.DataObject;
import context.arch.comm.clients.IndependentCommunication;
import context.arch.discoverer.ComponentDescription;
import context.arch.discoverer.Discoverer;
import context.arch.discoverer.componentDescription.TypeElement;
import context.arch.discoverer.querySystem.AbstractQueryItem;
import context.arch.discoverer.querySystem.ORQueryItem;
import context.arch.discoverer.querySystem.QueryItem;
import context.arch.handler.Handler;
import context.arch.interpreter.Interpreter;
import context.arch.enactor.WidgetIdRegistry.WidgetIdRegEntry;
import context.arch.enactor.WidgetReferenceRegistry.WidgetReferenceRegEntry;
import context.arch.enactor.WidgetSubscriptionRegistry.WidgetSubscriptionRegEntry;
import context.arch.storage.Attributes;
import context.arch.subscriber.ClientSideSubscriber;
import context.arch.subscriber.DiscovererSubscriber;
import context.arch.widget.Widget;

/**
 * 
 * This class manages CTK subscriptions on behalf of enactors. It generates 
 * discovery queries out of enactor references, and notifies enactors of any
 * new widgets that match those references. If multiple enactors utilize one
 * subscription manager, it tries to minimize the number of actual CTK 
 * subscriptions used for all enactors.
 * 
 * @author alann
 */
public class EnactorSubscriptionManager implements Handler {
  public static final int DEFAULT_PORT = 4324;
  
  private static final Logger LOGGER = Logger.getLogger(EnactorSubscriptionManager.class.getName());

  public EnactorSubscriptionManager() {
    this(DEFAULT_PORT);
  }
  
  public EnactorSubscriptionManager(int port) {
    LOGGER.info("starting SituationManager on port " + port);
    bo = new BaseObject(port);
    bo.setId(BaseObject.getId(EnactorSubscriptionManager.class.getName(),port));
    bo.findDiscoverer(false);
    init();
  }
  
  /**
   * Adds an enactor to the ESM. If a new enactor, its references will be processed
   * for subscriptions.
   * 
   * @param r enactor to be added.
   * @return <tt>true</tt> if rule was not already contained in the SM.
   */
  public boolean addEnactor(Enactor r) throws EnactorException {
    boolean b = enactors.add(r);
    r.setSubscriptionManager(this);
    
    Iterator i = r.getEnactorReferences();
    
    while (i.hasNext()) {
      EnactorReference wd = (EnactorReference) i.next();
      addEnactorReference(wd);
    }
    
    return b;
  }
  
  /**
   * Removes an enactor from the ESM, and removes all of its subscriptions.
   * 
   * @param r enactor to be removed.
   * @return <tt>true</tt> if enactor in the SM.
   */
  public boolean removeEnactor(Enactor r) throws EnactorException {
    boolean b = enactors.remove(r);
    r.setSubscriptionManager(null);
    
    Iterator i = r.getEnactorReferences();
    
    while (i.hasNext()) {
      EnactorReference wd = (EnactorReference) i.next();
      removeEnactorReference(wd);
    }
    
    
    return b;
  }

  /**
   * This method registers an EnactorReference with the ESM. The reference must
   * be part of a registered Enactor. Enactors can call this method if they add
   * new references to themselves at runtime.
   * 
   * @param rwr the EnactorReference to add.
   * @throws SituationException if the Enactor is not registered with the ESM.
   */
  protected void addEnactorReference(EnactorReference rwr) throws EnactorException {
    if (!enactors.contains(rwr.getEnactor())) throw new EnactorException("RuleWidgetReference must be part of a rule in the SituationManager; call addRule first");
    
    widgetReferences.put(rwr, new WidgetReferenceRegEntry());
    
    Iterator i = sendDiscovererAttributeQuery(rwr.getDescriptionQuery()).iterator();
    //subscribe to each component matching our rulewidgetreference
    while(i.hasNext()) {
      subscribe((ComponentDescription)i.next(), rwr);
    }
  }
  
  /**
   * removed EnactorReference from manager. This can be called at runtime by Enactors when they
   * change their internal structure to notify the manager, but the Enactor must be part
   * of the manager first by calling addRule. 
   * 
   * @param rwr the EnactorReference to be removed.
   */
  protected void removeEnactorReference(EnactorReference rwr) throws EnactorException {
    if (!enactors.contains(rwr.getEnactor())) throw new EnactorException("RuleWidgetReference must be part of a rule in the SituationManager; call addRule first");
    WidgetReferenceRegEntry wrre = widgetReferences.remove(rwr);
    if (wrre != null) {
      Iterator i = wrre.getWidgetSubscriptions().iterator();
      while (i.hasNext()) {
        String subId = (String) i.next();
        WidgetSubscriptionRegEntry wsre = widgetSubscriptions.get(subId);
        if (wsre != null) {
          wsre.removeWidgetReference(rwr);
          if (wsre.getWidgetReferences().size() == 0) {
            unsubscribe(subId);
          }
        }
      }
    }
  }
  
  /**
   * @param sml
   */
  protected void fireAddEventsForAll(Enactor e, EnactorListener sml) {
    Iterator subIds = widgetSubscriptions.getSubscriptionIds().iterator();
    while (subIds.hasNext()) {
      String subId = (String) subIds.next();
      WidgetSubscriptionRegEntry wre = widgetSubscriptions.get(subId);
      ComponentDescription cd = wre.getComponentDescription();
      Iterator refs = wre.getWidgetReferences().iterator();
      while (refs.hasNext()) {
        EnactorReference wr = (EnactorReference)refs.next();
        e.fireComponentAdded(sml,wr,subId,cd,null);
      }
    }
  }

  //////////////////////////////////////
  // Begin CTK External Interaction Code
  //////////////////////////////////////
  
  protected BaseObject getBaseObject() {
    return bo;
  }
  
  public DataObject executeWidgetService(String subscriptionId, String serviceName, String functionName, Attributes input) {
    WidgetSubscriptionRegEntry wre = widgetSubscriptions.get(subscriptionId);
    if (wre != null) {
      ComponentDescription cd = wre.getComponentDescription();
      return bo.executeSynchronousWidgetService(cd.hostname,cd.port,cd.id,serviceName,functionName,input);
    }
    return null;
  }
  
  public void handleIndependentReply(IndependentCommunication independentCommunication) {
    LOGGER.info("independent reply");
  }
  
  public DataObject handle(String subscriptionId, DataObject data) throws InvalidMethodException, MethodException {
    if (discoSub.getSubscriptionId().equals(subscriptionId)) {
      //make new ClientSubscriber object, subscribe to widget.
      //TODO: we must need to do extra checking on messages from the discoverer (e.g. deletions), figure it out
      handleNewComponent(ComponentDescription.dataObjectToComponentDescription(data));
    } else {
      ComponentDescription cd  = ComponentDescription.dataObjectToComponentDescription(data);
      WidgetSubscriptionRegEntry wre = widgetSubscriptions.get(subscriptionId);
      if (wre != null) {
        //put state into WSRegistry
        wre.setCurrentValues(cd);
        //lookup listeners by widget's description
        Iterator i = wre.getWidgetReferences().iterator();
        while (i.hasNext()) {
          EnactorReference wr = (EnactorReference)i.next();
          AbstractQueryItem aqi = wr.getDescriptionQuery();
          if (aqi != null && aqi.process(cd)) {
            wr.evaluateComponent(subscriptionId, cd);
          }
        }
      }
    }
    return null;
  }
  
  /**
   * subscribes to the CTK Discover with a query that retrieves information about all new widgets.
   *
   */
  protected void init() {
    AbstractQueryItem q = new ORQueryItem(new QueryItem(new TypeElement(Widget.WIDGET_TYPE)),new QueryItem(new TypeElement(Interpreter.INTERPRETER_TYPE)));
    //make a new discoverersubscriber, send subscription.
    discoSub = new DiscovererSubscriber(bo.getId(),bo.getHostName(),bo.getPort(),Discoverer.NEW_COMPONENT,q);
    //we want to receive full ComponentDescriptions 
    discoSub.setFullDescriptionResponse(true);
    bo.discovererSubscribe(this, discoSub);
  }
  
  /**
   * We see if any registered enactors (through references) are interested in this
   * component.  If not we do nothing.  If some enactor at a later date wants it, 
   * we'll get it again as a result of a query.
   * 
   * @param cd description of component to be registered
   */
  protected synchronized void handleNewComponent(ComponentDescription cd) {
    LOGGER.info("handling new component " + cd.id);
    Iterator i = widgetReferences.getWidgetReferences().iterator();
    while (i.hasNext()) {
      EnactorReference rwr = (EnactorReference) i.next();
      //TODO gather all descriptionQueries in the widget before subscribing
      if (rwr.getDescriptionQuery().process(cd)) {
        subscribe(cd,rwr);
      }
    }
  }
  
  /**
   * Subscribes to the widget described by the component description, and binds
   * it to the enactor reference. We use the registry to bind the reference to an
   * existing subscription if possible, before making a new CTK subscription.
   * 
   */
  protected void subscribe(ComponentDescription cd, EnactorReference rwr) {
    LOGGER.info("subscribing to widget " + cd.id);
    WidgetIdRegEntry wire = widgetIds.get(cd.id);
    String subscriptionId = null;
    if (wire == null) {
      //create subscription; do we set the subscriberID here???
      ClientSideSubscriber css = new ClientSideSubscriber(bo.getId(),bo.getHostName(),bo.getPort(),Widget.UPDATE,rwr.getDescriptionQuery(),null);
      bo.subscribeTo(this, cd.id, cd.hostname, cd.port, css);
      //put an entry in the widgetSubscriptions that will allow us to lookup the description & css later
      WidgetSubscriptionRegEntry wre = new WidgetSubscriptionRegEntry();
      wre.addWidgetReference(rwr);
      wre.setClientSideSubscriber(css);
      wre.setComponentDescription(cd);
      subscriptionId = css.getSubscriptionId(); 
      widgetSubscriptions.put(subscriptionId, wre);
      wire = new WidgetIdRegEntry();
      wire.addWidgetSubscription(subscriptionId);
      widgetIds.put(cd.id,wire);
      WidgetReferenceRegEntry wrre = widgetReferences.get(rwr);
      wrre.addWidgetSubscription(subscriptionId);
    } else {
      //for now we assume only one subscription per widget. This could change in the future.
      subscriptionId = (String) wire.getWidgetSubscriptions().get(0);
      updateWidgetSubscriptionCondition(subscriptionId);
    }
    //notify RuleWidgetReference
    rwr.componentAdded(subscriptionId, cd);
  }
  
  /**
   * Unsubscribes to the widget with the given subscription id, and notifies
   * all bound enactor references.
   */
  protected void unsubscribe(String subscriptionId) {
    LOGGER.info("unsubscribing to widget");
    WidgetSubscriptionRegEntry wre = widgetSubscriptions.remove(subscriptionId);
    if (wre != null) {
      //potentially tell all widgetreferences about unsubscription
      //remove entry from widgetIds
      ComponentDescription cd = wre.getComponentDescription();
      String widgetId = cd.id;
      WidgetIdRegEntry wire = widgetIds.get(widgetId);
      if (wire != null) {
        wire.removeWidgetSubscription(subscriptionId);
        if (wire.getWidgetSubscriptions().size() == 0) {
          widgetIds.remove(widgetId);
        }
      }
      bo.unsubscribeFrom(subscriptionId);
      //notify widgets of removal
      Iterator i = wre.getWidgetReferences().iterator();
      while(i.hasNext()) {
        EnactorReference rwr = (EnactorReference) i.next();
        rwr.componentRemoved(subscriptionId, cd);
      }
    }
  }
  
  /**
   * resubscribe to a currently subscribed widget with new conditions.
   */
  protected void updateWidgetSubscriptionCondition(String subscriptionId) {
    WidgetSubscriptionRegEntry wre = widgetSubscriptions.get(subscriptionId);
    if (wre != null) {
      ClientSideSubscriber css = wre.getClientSideSubscriber();
      ComponentDescription cd = wre.getComponentDescription();
      Iterator i = wre.getWidgetReferences().iterator();
      AbstractQueryItem q = null;
      while (i.hasNext()) {
        EnactorReference rwr = (EnactorReference) i.next();
        q = (q == null) ? rwr.getDescriptionQuery() : new ORQueryItem(q,rwr.getDescriptionQuery());
      }
      css.setCondition(q);
      //resubscribe using the same Subscriber: widget should take care of removing the old one
      bo.subscribeTo(this,cd.id,cd.hostname,cd.port,css);
    }
  }
  
  //TODO: this method is returning ComponentDescriptions that are empty of attributes, therefore useless!
  protected Collection sendDiscovererAttributeQuery(AbstractQueryItem q) {
    if (q != null)
      return bo.discovererQuery(q);
    else
      return Collections.EMPTY_SET;
  }
  
  ////////////////////////////////////
  // End CTK External Interaction Code
  ////////////////////////////////////
  
  
  private BaseObject bo;
  private HashSet enactors = new HashSet();

  //to be replace by an embedded database...
  private WidgetReferenceRegistry widgetReferences = new WidgetReferenceRegistry();
  private WidgetSubscriptionRegistry widgetSubscriptions = new WidgetSubscriptionRegistry();
  private WidgetIdRegistry widgetIds = new WidgetIdRegistry();
  
  private DiscovererSubscriber discoSub;
}
