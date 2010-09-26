package context.arch.subscriber;

import context.arch.comm.language.MessageHandler;
import context.arch.util.FileRead;
import context.arch.comm.language.DecodeException;
import context.arch.comm.language.InvalidDecoderException;
import context.arch.comm.language.EncodeException;
import context.arch.comm.language.InvalidEncoderException;
import context.arch.comm.DataObject;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.io.StringReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * This class maintains a list of subscribers, allows additions, removals and
 * updates to individual subscribers.
 *
 * Agathe: I have changed Subscribers and Subscriber to allow the addition of
 * the DiscovererSubscriber class. Subscriber and DiscovererSubscriber
 * implement an interface handled by Subscribers.
 *
 * Agathe: modify restart subscription
 *
 * @author Anind, Agathe
 * @see context.arch.subscriber.Subscriber
 */
public class Subscribers extends Vector {
  
  /** Debug flag */
  public static boolean DEBUG = false;
  /**
   * Tags written in the log file
   */
  private final static String ENTRY_STRING = "entry:";
  
  private final static String ADD_SUB_REG = "addSubR:";
  private final static String ADD_SUB_DISCO = "addSubD:";
  private final static String REMOVE_SUB_REG = "removeSubR:";
  private final static String REMOVE_SUB_DISCO = "removeSubD:";
  private final static String UPDATE_SUB_REG = "updateSubR:";
  private final static String UPDATE_SUB_DISCO = "updateSubD:";
  
  /**
   * Tag used in messages
   */
  public static final String SUBSCRIBERS = "subscribers";
  
  /** */
  private Hashtable hash;
  /** */
  private MessageHandler mh;
  /** */
//  private String filename;
  
  /** The id of the component */
  private String baseObjectId;
  
  /** This counter is incremented at each subscription so that we attribute
   * a unique id to the subscriber*/
  private static int counterForUniqueIds = 0;
  
  /**
   * Basic constructor that takes an object that implements the MessageHandler
   * interface and an id to create a logfile name from.
   */
  public Subscribers(MessageHandler mh,String id) {
    super(20);
    this.mh = mh;
    hash = new Hashtable(20);
    // The filename for the log file
//    filename = new String("/data/"+id+"-subscription.log");
    baseObjectId = id;
    restartSubscriptions();
  }
  
  /**
   * Adds a subscriber to the subscriber list
   *
   * @param sub Subscriber object to add
   */
  public synchronized void addSubscriber(AbstractSubscriber sub) {
    addSubscriber(sub,true);
  }
  
  /**
   * Adds a subscriber to the subscriber list
   *
   * @param sub Subscriber object to add
   * @param log Whether to log the subscribe or not
   */
  public synchronized void addSubscriber(AbstractSubscriber sub, boolean log) {
    //System.out.println("Subscribers <addSubscriber> class=" + sub.getClass().toString() );
    Subscriber existingSub = null;
    if ( sub instanceof Subscriber 
    && (existingSub = getSubscriber((Subscriber)sub)) != null ) {
      // Look if we have already this component as subscriber
      sub.setSubscriptionId(existingSub.getSubscriptionId());
      hash.remove(existingSub);
      hash.put(sub.getSubscriptionId(), sub);
    }
    else {
      // Updates the unique subscription id
      sub.setSubscriptionId(sub.getBaseObjectId()+"_"+baseObjectId+"_"+sub.getSubscriptionCallback()+"_"+this.getCounterForUniqueIds());
      if (hash.get(sub.getSubscriptionId()) != null) {
        removeSubscriber(sub);
      }
      addElement(sub);
      hash.put(sub.getSubscriptionId(),sub);
    }
    if (log) {
      if (sub instanceof DiscovererSubscriber)
        writeLog(ENTRY_STRING+ADD_SUB_DISCO, sub);
      else //if (sub instanceof Subscriber)
        writeLog(ENTRY_STRING+ADD_SUB_REG, sub);
    }
  }
  
  /**
   * Removes a subscriber from the subscriber list
   *
   * @param sub Subscriber object to remove
   * @return whether the removal was successful or not
   */
  public synchronized boolean removeSubscriber(AbstractSubscriber sub) {
    return removeSubscriber(sub,true);
  }
  
  /**
   * Removes a subscriber from the subscriber list
   *
   * @param sub Subscriber object to remove
   * @param log Whether to log the subscribe or not
   * @return whether the removal was successful or not
   */
  public synchronized boolean removeSubscriber(AbstractSubscriber sub, boolean log) {
    Object o = hash.get(sub.getSubscriptionId());
    AbstractSubscriber sub2 = (AbstractSubscriber) o;
    if (sub2 != null) {
      hash.remove(sub2.getSubscriptionId());
      this.remove(sub2);
      //System.out.println("Subscribers <removeSub> after remove: hash=" + hash  + " and this = " 
      //+ ((Vector)this).toString());
      if (log) {
        if (sub2 instanceof DiscovererSubscriber)
          writeLog(ENTRY_STRING+REMOVE_SUB_DISCO,sub2);
        else //if (sub instanceof DiscovererSubscriber)
          writeLog(ENTRY_STRING+REMOVE_SUB_REG,sub2);
      }
      return true;
    }
    return false;
  }
  
  /** Remove an AbstractSubscriber
   *
   * @param subToRemove
   */
  public boolean remove(AbstractSubscriber subToRemove){
    Enumeration list = this.elements();
    AbstractSubscriber sub = null;
    Object o = null;
    boolean found = false;
    while (list.hasMoreElements()){
       o = list.nextElement();
      sub = (AbstractSubscriber) o;
      if (subToRemove.getSubscriptionId().equalsIgnoreCase(sub.getSubscriptionId())) {
        found = true;
        break;
      }
    }
    if (found) {
      //System.out.println("Remove from vector");
      this.removeElement(o);
    }
    return found;
  }
  
  /**
   * Removes a subscriber from the subscriber list
   *
   * @param sub Subscriber object to remove
   * @return whether the removal was successful or not
   */
  public synchronized boolean removeSubscriber(String subId) {
    return removeSubscriber(subId,true);
  }
  
  /**
   * Removes a subscriber from the subscriber list
   *
   * @param sub Subscriber object to remove
   * @param log Whether to log the subscribe or not
   * @return whether the removal was successful or not
   */
  public synchronized boolean removeSubscriber(String subId, boolean log) {
    AbstractSubscriber sub2 = (AbstractSubscriber)hash.get(subId);
    if (sub2 != null) {
      return removeSubscriber(sub2);
    }
    return false;
  }
  
  /**
   * Updates a subscriber in the subscriber list.  The subscriber name is
   * retrieved from the subscriber object and the old subscriber entry with
   * this name is replaced by the given one.
   *
   * @param sub Subscriber object to update
   */
  public synchronized void updateSubscriber(AbstractSubscriber sub) {
    updateSubscriber(sub,true);
  }
  
  /**
   * Updates a subscriber in the subscriber list.  The subscriber name is
   * retrieved from the subscriber object and the old subscriber entry with
   * this name is replaced by the given one.
   *
   * @param sub Subscriber object to update
   * @param log Whether to log the subscribe or not
   */
  public synchronized void updateSubscriber(AbstractSubscriber sub, boolean log) {
    removeSubscriber((AbstractSubscriber)(hash.get(sub.getSubscriptionId())));
    addElement(sub);
    hash.put(sub.getSubscriptionId(), sub);
    if (log) {
      if (sub instanceof DiscovererSubscriber)
        writeLog(ENTRY_STRING+UPDATE_SUB_DISCO,sub);
      else //if (sub instanceof DiscovererSubscriber)
        writeLog(ENTRY_STRING+UPDATE_SUB_REG,sub);
    }
  }
  
  /**
   * Returns the subscriber at the given index.  Do not assume that a given
   * subscriber's index will stay constant throughout its lifetime.  When
   * other subscribers are added and removed, a given subscriber's index
   * may change.
   *
   * @param index index value of the Subscriber object to retrieve
   */
  public synchronized AbstractSubscriber getSubscriberAt(int index) {
    return (AbstractSubscriber)(elementAt(index));
  }
  
  /**
   * Returns the subscriber with the given name.
   *
   * @param subscriptionId ID of the Subscriber object to retrieve
   */
  public synchronized AbstractSubscriber getSubscriber(String subscriptionId) {
    return (AbstractSubscriber)(hash.get(subscriptionId));
  }
  
  /** This method returns if possible the subscriber that corresponds to the arguments.
   *
   * @para subscriber
   * @return Subscriber
   */
  public synchronized Subscriber getSubscriber(Subscriber subscriber){
    
    if (subscriber == null)
      return null;
    
    
    System.out.println("subscriber = " + subscriber );
    
    Enumeration list = this.getSubscribers();
    Subscriber sub = null;
    
    boolean found = false;
    
    while (list.hasMoreElements()){
      sub = (Subscriber) list.nextElement();
      if (sub == null) continue;
      
      if (! sub.getBaseObjectId().equalsIgnoreCase(subscriber.getBaseObjectId())) continue;
      if (! sub.getSubscriberHostName().equalsIgnoreCase(subscriber.getSubscriberHostName())) continue;
      if ( sub.getSubscriberPort() != subscriber.getSubscriberPort()) continue;
      if (! sub.getSubscriptionCallback().equalsIgnoreCase(subscriber.getSubscriptionCallback())) continue;
      //TODO: repair this, not sure if QueryItems can be so easily compared --alann
//      if (! sub.getConditions().equals(subscriber.getConditions())) continue;
      if (! sub.getAttributes().equals(subscriber.getAttributes())) continue;
      
      // sub correspond to the subscriber we are looking for
      found = true;
      break;
    }
    
    if (found)
      return sub;
    else
      return null;
  }
  
  
  /**
   * Returns an enumeration containing all the subscribers in the list
   */
  public synchronized Enumeration getSubscribers() {
    return hash.elements();
  }
  
  /**
   * Returns an enumeration containing all the subscriber names in the list
   */
  public synchronized Enumeration getSubscriberNames() {
    return hash.keys();
  }
  
  /**
   * Returns the number of subscribers in the list
   */
  public synchronized int numSubscribers() {
    return size();
  }
  
  /**
   * Converts to a DataObject.
   *
   * @return
   */
  public DataObject toDataObject(){
    Vector v = new Vector();
    for (int i=0; i<numSubscribers(); i++) {
      v.addElement(getSubscriberAt(i).toDataObject());
    }
    return new DataObject(SUBSCRIBERS,v);
  }
  
  /** Return the number to use
   * @return int
   */
  private synchronized int getCounterForUniqueIds(){
    int res = counterForUniqueIds;
    counterForUniqueIds++;
    return res;
  }
  
  /**
   * This method reads in the subscription log, restarts all the subscriptions
   * that were valid at the time of this object being shut down and writes
   * out the valid subscriptions to the log.  It deletes the old log and
   * creates a new one, so that it can clear out entries in the log for
   * corresponding unsubscribes and subscribes.
   */
  private void restartSubscriptions() {
//    String log = new FileRead(filename).read();
//    int index = log.indexOf(ENTRY_STRING);
//    
//    while (index != -1) {
//      String entry1 = null; // contains the command
//      int index2 = log.indexOf(ENTRY_STRING,index+1);
//      if (index2 == -1) {
//        entry1 = log.substring(index+ENTRY_STRING.length());
//      }
//      else {
//        entry1 = log.substring(index+ENTRY_STRING.length(),index2);
//      }
//      try { // Test the message code : ADD, REMOVE, UPDATE and creates a Subscriber
//        // object based on the log file
//        if (entry1.indexOf(ADD_SUB_REG) != -1) {
//          index = entry1.indexOf(">");
//          String entry = entry1.substring(index+1);
//          AbstractSubscriber sub = new Subscriber(mh.decodeData(new StringReader(entry)));
//          addSubscriber(sub,false);
//        }
//        else if (entry1.indexOf(REMOVE_SUB_REG) != -1) {
//          index = entry1.indexOf(">");
//          String entry = entry1.substring(index+1);
//          AbstractSubscriber sub = new Subscriber(mh.decodeData(new StringReader(entry)));
//          removeSubscriber(sub,false);
//        }
//        else if (entry1.indexOf(UPDATE_SUB_REG) != -1) {
//          index = entry1.indexOf(">");
//          String entry = entry1.substring(index+1);
//          AbstractSubscriber sub = new Subscriber(mh.decodeData(new StringReader(entry)));
//          updateSubscriber(sub,false);
//        }
//        // Discoverer subscribers
//        else if (entry1.indexOf(ADD_SUB_DISCO) != -1) {
//          index = entry1.indexOf(">");
//          String entry = entry1.substring(index+1);
//          AbstractSubscriber sub = new DiscovererSubscriber(mh.decodeData(new StringReader(entry)));
//          addSubscriber(sub,false);
//        }
//        else if (entry1.indexOf(REMOVE_SUB_DISCO) != -1) {
//          index = entry1.indexOf(">");
//          String entry = entry1.substring(index+1);
//          AbstractSubscriber sub = new DiscovererSubscriber(mh.decodeData(new StringReader(entry)));
//          removeSubscriber(sub,false);
//        }
//        else if (entry1.indexOf(UPDATE_SUB_DISCO) != -1) {
//          index = entry1.indexOf(">");
//          String entry = entry1.substring(index+1);
//          AbstractSubscriber sub = new DiscovererSubscriber(mh.decodeData(new StringReader(entry)));
//          updateSubscriber(sub,false);
//        }
//      } catch (DecodeException de) {
//        System.out.println("Subscribers Decode: "+de);
//      } catch (InvalidDecoderException ide) {
//        System.out.println("Subscribers InvalidDecoder: "+ide);
//      }
//      index = index2;
//    }
//    
//    try {
//      BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
      for (int i=0; i<numSubscribers(); i++) {
        AbstractSubscriber sub = getSubscriberAt(i);
        String header = "";
        if (sub instanceof DiscovererSubscriber)
          header = new String(ENTRY_STRING+ADD_SUB_DISCO);
        else // REGULAR
          header = new String(ENTRY_STRING+ADD_SUB_REG);
        writeLog(header,sub);}
//      }
//      writer.close();
//    } catch (IOException ioe) {
//      ioe.printStackTrace();
//    }
  }
  
  /**
   * This private method writes an entry to the logfile.
   *
   * @param header Header of the entry to append to the logfile
   * @param sub Subscriber information to put in the entry
   */
  private void writeLog(String header, AbstractSubscriber sub) {
      
  }
  
//  private void writeLog(String header, AbstractSubscriber sub) {
//    try {
//      BufferedWriter writer = new BufferedWriter(new FileWriter(filename,true));
//      String out = new String(header+mh.encodeData(sub.toDataObject())+"\n");
//      writer.write(out,0,out.length());
//      writer.flush();
//      writer.close();
//    } catch (IOException ioe) {
//      System.out.println("Subscribers writeLog() IO: "+ioe);
//    } catch (EncodeException ee) {
//      System.out.println("Subscribers writeLog() Encode: "+ee);
//    } catch (InvalidEncoderException iee) {
//      System.out.println("Subscribers writeLog() InvalidEncoder: "+iee);
//    }
//  }
  
  /** Set the BaseObject id used to attribute the unique id for subscribers
   * @param id
   */
  public void setBaseObjectId(String id){
    this.baseObjectId = id;
  }
  
  public static void main (String arg[]){
    String s = null;
    String s2 = "" + s;
    
    System.out.println(s2);
  }
}
