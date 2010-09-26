/*
 * AbstractSubscriber.java
 *
 * Created on September 6, 2001, 9:52 AM
 */

package context.arch.subscriber;

import context.arch.comm.DataObject;
import java.util.Vector;
import java.util.Hashtable;

/**
 * Interface for Subscriber classes that may be stored in a Subscribers object.
 * A subscriber is defined by: id+hostname+port. It specifies a callback name it 
 * subscribes to and a tag (subscriber tag as alias for the callback).
 *
 *
 * @author  Agathe
 */

public abstract class AbstractSubscriber {

  /**
   * Tag for a subscriber
   */
  public static final String SUBSCRIBER = "subscriber";

  /**
   * Tag for host machine of component 
   */
  public static final String HOSTNAME = "hostname";

  /**
   * Tag for port number of component
   */
  public static final String PORT = "port";

  /**
   * Tag for subscription id
   */
  public static final String SUBSCRIBER_ID = "subscriberId";
  
  /**
   * Tag for the client side BaseObject id
   */
  public static final String CLIENT_BASEOBJECT_ID = "clientBOId";

  /**
   * Tag to indicate message is a subscription reply
   */
  public static final String SUBSCRIPTION_REPLY = "subscriptionReply";

  /**
   * Tag for callback tag (on subscriber side)
   */
  public static final String CALLBACK_TAG = "callbackTag";

  /**
   * Tag for callback (on widget side)
   */
  public static final String CALLBACK_NAME = Callback.CALLBACK_NAME;

  /**
   * Tag to indicate message is a subscription callback
   */
  public static final String SUBSCRIPTION_CALLBACK = "subscriptionCallback";

  /**
   * Tag to indicate message is for adding a subscriber
   */
  public static final String ADD_SUBSCRIBER = "addSubscriber";

  /**
   * Tag to indicate message is for removing a subscriber
   */
  public static final String REMOVE_SUBSCRIBER = "removeSubscriber";

  /**
   * Tag to indicate message is the reply to a subscription callback message
   */
  public static final String SUBSCRIPTION_CALLBACK_REPLY = "subscriptionCallbackReply";

  /**
   * Tag for subscriber type
   */
  public static final String SUBSCRIBER_TYPE = "subType";
  
  /**
   * Maximum number of consecutive communication errors to be tolerated 
   */
  public static final int MAX_ERRORS = 5;

  private String subscriptionUniqueId;
  private String subscriberHostname;
  private int subscriberPort;
  private String subscriptionCallback;
  int errors;
  private String baseObjectId;
  
  /**
   * String identifying the type of the subscriber.
   * Existing types: general, discoverer
   */
  private String subscriberType;
  
  /**
   * Counter used to create unique id
   */
  private static int counterForUniqueId = 0;
  
  /**
   *
   */
  public AbstractSubscriber(String subscriberType){
    this.subscriberType = subscriberType;
  }
  
  /**
   * Basic constructor that creates a subscriber object from a DataObject.
   * The DataObject must contain a <SUBSCRIBER> tag
   *
   * @param data DataObject containing the subscriber info
   */
  public AbstractSubscriber (DataObject data) {
    //System.out.println("DATA => \n" +data);
    DataObject sub = data.getDataObject(SUBSCRIBER);
    
    subscriberHostname = (String) sub.getDataObject(HOSTNAME).getValue().firstElement();
    subscriberPort = new Integer(((String) sub.getDataObject(PORT).getValue().firstElement())).intValue();
    subscriptionCallback = (String) sub.getDataObject(CALLBACK_NAME).getValue().firstElement();
    
    // get the client baseobject id
    DataObject temp = sub.getDataObject (CLIENT_BASEOBJECT_ID);
    //System.out.println("\n\nSub id " + temp);
    if (temp != null) {
      System.out.println("1 " +  temp.getValue());
      //baseObjectId = (String) sub.getDataObject (CLIENT_BASEOBJECT_ID).getValue ().firstElement ();
      baseObjectId = temp.getValue ().firstElement ().toString();
      //System.out.println("\nBO Id="+baseObjectId);
    }
    temp = sub.getDataObject (SUBSCRIBER_ID);
    if (temp != null)
      subscriptionUniqueId = (String) temp.getValue().firstElement();
    errors = 0;
  }
  
  /**
   * This method return a Subscriber object based on the type specified in
   * the SUBSCRIBER tag.
   *
   * @param data DataObject containing the subscriber info
   */
  public static AbstractSubscriber dataObjectToAbstractSubscriber(DataObject data) {
    AbstractSubscriber newSub = null;
    DataObject sub = data.getDataObject(SUBSCRIBER);
    Hashtable h = sub.getAttributes ();
    String type;
    Object o;
    if ( h != null && (o = h.get(AbstractSubscriber.SUBSCRIBER_TYPE)) != null){
      type = (String) o;
      if (type.equals (Subscriber.GENERAL_TYPE)){
        newSub = new Subscriber(data);
      }
      else if (type.equals(DiscovererSubscriber.DISCOVERER_TYPE) || type.equals(ClientSideSubscriber.CLIENT_TYPE)){
        newSub = new DiscovererSubscriber(data);
      }
    }
    else {
      newSub = new Subscriber(data); // if not for the discoverer, it is for widget
    }
    
    return newSub;
  }
  
  /**
   * Returns the id of the subscriber
   *
   * @return the subscriber id
   */
  public String getSubscriptionId() {
    return subscriptionUniqueId;
  }

  /**
   * Sets the id of the subscriber
   *
   * @param id ID of the subscriber
   */
  public void setSubscriptionId(String id) {
    this.subscriptionUniqueId = id;
  }

  /**
   * Returns the name of the subscriber's host computer
   *
   * @return the host name of the subscriber
   */
  public String getSubscriberHostName() {
    return this.subscriberHostname;
  }

  /**
   * Sets the name of the subscriber's host computer
   *
   * @param hostname Name of the subscriber's host computer
   */
  public void setSubscriberHostname(String subHostname) {
    this.subscriberHostname = subHostname;
  }

  /**
   * Returns the port number to send info to
   *
   * @return the port number of the subscriber
   */
  public int getSubscriberPort() {
    return this.subscriberPort;
  }

  /**
   * Sets the port number to send info to
   *
   * @param subscriberPort Port number to send information to
   */
  public void setSubscriberPort(int subPort) {
    this.subscriberPort = subPort;
  }

  /**
   * Returns the subscriber callback that the subscriber registered
   *
   * @return the subscriptionCallback of the subscriber
   */
  public String getSubscriptionCallback() {
    return subscriptionCallback;
  }

  /**
   * Sets the subscriber callback that the subscriber wants to register for
   *
   * @param String Widget callback being registered for
   */
  public void setSubscriptionCallback(String subscriptionCallback) {
    this.subscriptionCallback = subscriptionCallback;
  }

  /**
   * Increment the error counter
   */
  public void addError() {
    errors++;
  }

  /**
   * Reset the error counter
   */
  public void resetErrors() {
    errors = 0;
  }

  /**
   * Returns the number of consecutive errors in trying to communicate with
   * this subscriber
   *
   * @return number of consecutive communications errors for this subscriber
   */
  public int getErrors() {
    return errors;
  }

  /**
   * This method converts the subscriber info to a DataObject
   *
   * @return Subscriber object converted to a <SUBSCRIBER> DataObject
   */
  public DataObject toDataObject() {    
    Vector v = new Vector();
    if (context.arch.widget.Widget.DEBUG) System.out.println("AbstractSubscriber <toDataObject> this= " + this.toString ());
    if (subscriptionUniqueId != null)
      v.addElement(new DataObject(SUBSCRIBER_ID, subscriptionUniqueId));
    if (baseObjectId != null){
      //v.addElement(new DataObject(BaseObject.ID, baseObjectId)); 
      v.addElement(new DataObject(CLIENT_BASEOBJECT_ID, baseObjectId));
    }
    v.addElement(new DataObject(HOSTNAME, subscriberHostname));
    v.addElement(new DataObject(PORT, Integer.toString(subscriberPort)));
    v.addElement(new DataObject(CALLBACK_NAME, subscriptionCallback));
    if (context.arch.widget.Widget.DEBUG) System.out.println("AbstractSubscriber <toDataObject>Vector= " + v);
    return new DataObject(SUBSCRIBER, v);
  }
  
  public String getBaseObjectId(){
    return this.baseObjectId;
  }
  
  protected void setBaseObjectId(String id){
    this.baseObjectId = id;
  }
  
  public String toString(){
    StringBuffer sb = new StringBuffer();
    sb.append ("unique SUB ID " + getSubscriptionId ());
    sb.append (" - baseObject id " + this.baseObjectId);
    sb.append (" - type sub " + this.subscriberType);
    sb.append (" - hostname " + getSubscriberHostName ());
    sb.append (" - port " + getSubscriberPort ());
    return sb.toString ();
  }
  
}

