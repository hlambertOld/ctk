/*
 * DiscovererMediator.java
 *
 * Created on July 2, 2001, 4:00 PM
 */

package context.arch.discoverer;

import context.arch.discoverer.dataModel.AbstractDataModel;
import context.arch.discoverer.dataModel.DiscovererDataModel;
import context.arch.discoverer.ComponentDescription;
import context.arch.discoverer.lease.Lease;
import context.arch.discoverer.lease.LeasesKeeper;
import context.arch.util.Error;
import context.arch.discoverer.querySystem.AbstractQueryItem;
import context.arch.discoverer.querySystem.*;
import context.arch.discoverer.querySystem.comparison.*;
import context.arch.discoverer.componentDescription.*;
import context.arch.storage.AttributeNameValue;
import context.arch.storage.Attribute;
import context.arch.BaseObject;
import context.arch.comm.language.MessageHandler;
import context.arch.util.FileRead;
import context.arch.comm.clients.IndependentCommunication;
import context.arch.comm.RequestObject;
import context.arch.subscriber.DiscovererSubscriber;

import context.arch.comm.DataObject;
import context.arch.comm.language.EncodeException;
import context.arch.comm.language.DecodeException;
import context.arch.comm.language.InvalidEncoderException;
import context.arch.comm.language.InvalidDecoderException;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Hashtable;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * This mediator allows to handle the dialog between the discoverer and 
 * the DiscovererDataModel, the LeasesKeeper.
 * It is able to store components into the database, to update and remove them.
 * It handles queries and subscription queries.
 * It asks the discoverer to check the components' liveliness
 *
 * @author  Agathe
 */
public class DiscovererMediator {

  /**
   * The Discoverer object the DiscovererMediator works for
   */
  private Discoverer discoverer;
  
  /**
   * The message handler (the Discoverer object) used to decode and encode 
   * DataObject
   */
  private MessageHandler mh;
  
  /**
   * The database object where the DiscovererMediator stores the index tables
   */
  private AbstractDataModel dataModel;
  
  /**
   * The object containing a timer that triggers the check of components' 
   * liveliness
   */
  private LeasesKeeper leasesKeeper;
  
  /**
   * Boolean to keep a logfile
   */
  public boolean useLogFile = false;
  
  /**
   * Tags used for the log file
   */
  private final static String ENTRY_STRING = "entry:";
  private final static String ADD_COMP = "addComp:";
  private final static String REMOVE_COMP = "removeComp:";
  
  
  /** 
   * The default name for the Discoverer database log file
   */
  private String filename = "discoverer-database.log";
  
  /**
   * Creates new DiscovererMediator 
   *
   * @param discoverer The discoverer object
   * @param keppLogFile If true, the log file is updated
   */
  public DiscovererMediator (Discoverer discoverer, boolean keepLogFile) {
    dataModel = new DiscovererDataModel();
    this.discoverer = discoverer;
    mh = (MessageHandler) discoverer;
    useLogFile = keepLogFile;
    filename = this.discoverer.getId () + "-database.log";
    
    setRegisteredComponents ();
    
  }
  
  /**
   * This method allows to register a ComponentDescription object
   *
   * @param comp the ComponentDescription object
   * @param lease The lease specified for this ComponentDescripion object
   * @return Error
   */
  public Error add(ComponentDescription comp, Lease lease){
    Error error = new Error(Error.NO_ERROR);
    // Gets the existing index if it exists
    Integer existingCompIndex = (Integer) dataModel.getIndexOf(comp.id);
    discoverer.println("\n\n+++Mediator add id=" + comp.id + " found=" + existingCompIndex);
    // If it already exists : removes it
    if (existingCompIndex != null){
      dataModel.remove (existingCompIndex);
      if (useLogFile)
        removeFromLog (comp);
      leasesKeeper.removeLease (existingCompIndex);
    }
    // Now adds it
    Integer index = (Integer) dataModel.add(comp);
    if (useLogFile)
      addToLog (comp);
    
    // Registers the lease
    lease.setComponentIndex(index);
    leasesKeeper.addLease(lease);
    
    return error;
  }
  
  /**
   * Updates a lease for a registered component
   *
   * @param compId The id of the component
   * @param lease The new Lease object
   * @return Error
   */
  public Error updateLease(String compId, Lease lease){
    Error err = new Error();
    // Get comp index
    Integer compIndex = (Integer)dataModel.getIndexOf (compId);
    lease.setComponentIndex (compIndex);
    // Updates the lease
    if (leasesKeeper.renewLease (lease))
      err.setError (Error.NO_ERROR);
    else
      err.setError (Lease.LEASE_ERROR);
    return err;
  }
  
  /**
   * Take a query and returns the corresponding components
   *
   * @param query The abstractquery object
   * @return DataObject Contains the component descriptions of the component 
   * matching the query
   */
  public DataObject search(AbstractQueryItem query){
    int[][] tab = (int[][]) rawSearch(query);
    //System.out.println("Mediator result of the search : " + QueryItem.arrayToString (tab));
    Vector content = new Vector();
    Error err = new Error();
    DataObject result = null;
    int counter = 0 ; // # of responses
    
    for (int i = 0 ; i < tab.length ; i++){
      if (tab[i][1] == 1) { // The component is selected
        int index = tab[i][0];
        //System.out.println("Number of the component selected " + index);
        ComponentDescription comp = this.getComponentDescription (new Integer(index));
        DataObject doComp = comp.getBasicDataObject();
        DataObject newContent = new DataObject(Discoverer.DISCOVERER_QUERY_REPLY_CONTENT, doComp.getChildren ());
        //System.out.println("\n\n a content = " + newContent);
        content.addElement (newContent);
        counter ++;
      }
    }
    Hashtable qAtts = new Hashtable();
    qAtts.put(Discoverer.QUERY_TOTAL_ANSWERS, Integer.toString (counter));
    result = new DataObject(Discoverer.DISCOVERER_QUERY_REPLY_CONTENTS, qAtts, content);
    return result;
  }
  
  /**
   * Gets an AbstractQueryObject and returns a array with for each index
   * of registered components a value yes/no to indicate if this component
   * fits the query or not
   *
   * TO DO : complete the search to remove the widget included in server: USEFUL??
   *
   * @param query The abstract query object
   * @return Object An int[][] array
   */
  public Object rawSearch(AbstractQueryItem query){
    // Result from the data Model
    Object result = query.process (dataModel);
    
    // Do some complementary process: for example, is a server has subscribed
    // to a widget.
    return result;
  }
  
  /**
   * Returns a printable version of this object: the database content, the
   * leases.
   *
   * @return String
   */
  public String toString(){
    StringBuffer sb = new StringBuffer("DiscovererMediator");
    sb.append (dataModel.toString ());
    sb.append ("\n\n");
    sb.append (leasesKeeper.toString ());
    return sb.toString ();
  }

  /**
   * Removes a component from the discoverer base
   *
   * @param stringOrInteger The component's index or id
   * @return Error
   */
  public Error remove(Object stringOrInteger){
    Error error = new Error(Error.NO_ERROR);
    Integer index = null;
    String compId;
    // Get the index of the component
    if (stringOrInteger instanceof Integer){
      index = (Integer) stringOrInteger;
    }
    else { 
      index = (Integer) dataModel.getIndexOf ( (String) stringOrInteger);
    }
    // Remove it
    Object removed = dataModel.remove (index);
    compId = ((ComponentDescription) removed).id;
    // Update the log file
    if (removed != null && useLogFile){
      removeFromLog ((ComponentDescription)removed);
    }
    // Remove the lease
    leasesKeeper.removeLease (index);
    // Remove from the subscriber
    Enumeration list = discoverer.subscribers.getSubscribers ();
    DiscovererSubscriber dSub;
    while (list.hasMoreElements ()){
      dSub = (DiscovererSubscriber) list.nextElement ();
      String subId = dSub.getSubscriptionId ();
      if (subId.startsWith (compId))
      discoverer.subscribers.removeSubscriber (dSub, true);
    }
    if (removed == null)
      error.setError (Error.INVALID_REQUEST_ERROR);
    return error;
  }
  
  /**
   * Update the description of a component and its lease
   *
   * @param component The component to update
   * @param lease The new Lease
   * @return Error 
   */
  public Error update(ComponentDescription component, Lease lease){
    Error error = new Error(Error.NO_ERROR);
    Integer index = (Integer) dataModel.update(component);
    // Update the log file
    if (useLogFile){
      removeFromLog (component);
      addToLog (component);
    }
    if (index == null)
      error.setError (Error.ERROR_CODE);
    if (lease != null){
      updateLease(index, lease);
    }
    return error;
  }
  
  /**
   * Update a registered component lease
   *
   * @param stringOrInteger The component id or index
   * @param lease The new lease
   * @return Error
   */
  public Error updateLease(Object stringOrInteger, Lease lease){
    Error error = new Error(Error.NO_ERROR);
    Integer compIndex = (Integer)dataModel.getIndexOf (stringOrInteger);
    lease.setComponentIndex (compIndex);
    if (! leasesKeeper.renewLease (lease))
      error.setError (Error.ERROR_CODE);
    return error;
  }
  
  /**
   * Returns the ComponentDescription of a registered component
   *
   * @param stringOrIndex The component index or id
   * @return ComponentDescription
   */
  public ComponentDescription getComponentDescription(Object stringOrInteger){
    return (ComponentDescription)dataModel.getObjectAt (stringOrInteger);
  }
  
  /**
   * This method allows to send a lease end notification to each
   * component whose lease ends. The reply received by the component
   * either renews the lease or confirms it.
   * The list of components is sent back to the discoverer that take
   * care of sending a message to them
   *
   * @listOfComponents The components index
   */
  public void sendLeaseEndNotificationTo(ArrayList listOfComponents){
    discoverer.sendLeaseEndNotificationTo(listOfComponents);
  }
  
  /**
   * Return true if the component is registered by the discoverer
   *
   * @param stringOrInteger The component index or id
   * @return boolean
   */
  public boolean exists(Object stringOrInteger){
    if (stringOrInteger != null )
      return (dataModel.getIndexOf (stringOrInteger) != null);
    return false;
  }
  
  /**
   * Adds a component description to the log file
   *
   * @param comp The component description
   */
  public synchronized void addToLog(ComponentDescription comp) {
    writeLog(ENTRY_STRING+ADD_COMP, comp);
  }
  
  /**
   * Writes something to the log file
   *
   * @param header The header to write
   * @param comp The component to write
   */
  private void writeLog(String header, ComponentDescription comp) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename,true));
      String out = new String(header+mh.encodeData(comp.toDataObject())+"\n");
      writer.write(out,0,out.length());
      writer.flush();
      writer.close();
    } catch (IOException ioe) {
        System.out.println("DiscovererDataModel writeLog() IO: "+ioe);
    } catch (EncodeException ee) {
        System.out.println("DiscovererDataModel writeLog() Encode: "+ee);
    } catch (InvalidEncoderException iee) {
        System.out.println("DiscovererDataModel writeLog() InvalidEncoder: "+iee);
    }
  }
  
  /**
   * Adds the information that a component is removed from the database into
   * the log file
   *
   * @param comp The component description to remove
   */
  public synchronized void removeFromLog(ComponentDescription comp){
    removeFromLog(ENTRY_STRING+REMOVE_COMP, comp);
  }
  
  /**
   * Adds the remove information into the log file
   *
   * @param header The header information
   * @param comp The component description removed
   */
  public void removeFromLog(String header, ComponentDescription comp){
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename,true));
      //System.out.println("\n\nDiscoDataModel <removeFromLog> comp " + comp);
      //System.out.println("\nto dataObj " + comp.toDataObject ());
      String out = new String(header+mh.encodeData(comp.toDataObject())+"\n");
      writer.write(out,0,out.length());
      writer.flush();
      writer.close();
    } catch (IOException ioe) {
        System.out.println("DiscovererDataModel removeFromLog() IO: "+ioe);
    } catch (EncodeException ee) {
        System.out.println("DiscovererDataModel removeFromLog() Encode: "+ee);
    } catch (InvalidEncoderException iee) {
        System.out.println("DiscovererDataModel removeFromLog() InvalidEncoder: "+iee);
    }
  }
  
  /**
   * Retrieve the information from the log file, check the liveliness of the
   * components and put them back into the database
   */
  private void setRegisteredComponents(){
    // Get the list of component to restart
    Object obj = restartRegistrations();
    if (obj != null){
      HashMap notCheckedComps = (HashMap) obj;
      // Check them to be sure they are still alive
      Iterator list = notCheckedComps.values ().iterator ();
      int i=0;
      ComponentDescription comp;
      // Uses an independent communication to send a PING
      while (list.hasNext ()){
        comp = (ComponentDescription) list.next ();
        IndependentCommunication indComm = new IndependentCommunication(
                                              new RequestObject(null,null, 
                                                  comp.hostname,comp.port,comp.id), true);
        indComm.setObjectToStore (comp);
        indComm.setSenderClassId (Discoverer.DISCOVERER+Discoverer.REGISTERER+BaseObject.PING);
        discoverer.pingComponent(indComm);
        i ++;
      }
    }
  }
  
  /**
   * Overrides the method that handle independent Reply. If the independent
   * communication has been sent by this class, this class handles it. Otherwise
   * the super class handleIndependeReply is called.
   *
   * Catches:
   * - Discoverer+Registerer+Ping message
   *
   * TO DO: restore the lease?? not useful because the next time the discoverer
   * will check the component, the component will send its lease
   *
   */
  public void handleIndependentReply (IndependentCommunication independentCommunication){
    System.out.println("\nThe discovererMediator gets the reply from the the element ");
    if (independentCommunication != null) {
      independentCommunication.decodeReply (discoverer);
      DataObject replyContent = independentCommunication.getDecodedReply ();
      System.out.println("\nDiscovererMediator <handleIndependentReply> Reply=" + replyContent + " - exceptions " + independentCommunication.getExceptions ());

      // For RESTART REGISTRATION
      if (independentCommunication.getSenderClassId ().equals (Discoverer.DISCOVERER+Discoverer.REGISTERER+BaseObject.PING)){
        if ( ! independentCommunication.getExceptions ().isEmpty () 
             || replyContent == null){
          System.out.println("DiscovererMediator <handleIndependentReply> comp does not answer " + ((ComponentDescription)(independentCommunication.getObjectToStore ())).id);
        }
        // Adds the comp into the database if the comp is alive
        else {
          System.out.println("DiscovererMediator <handleIndependentReply> add the comp" );
          ComponentDescription comp =  (ComponentDescription) independentCommunication.getObjectToStore ();
          this.add(comp, new Lease());
        }
      }
    }
  }
  
  /**
   * Retrieve a list of component to restart from the log file
   *
   * @return Object contains a HashMap object
   */
  private Object restartRegistrations() {
    String log = new FileRead(filename).read();
    int index = log.indexOf(ENTRY_STRING);
    HashMap hash = new HashMap();
    
    while (index != -1) {
      String entry1 = null; // contains the command
      int index2 = log.indexOf(ENTRY_STRING,index+1);
      if (index2 == -1) {
        entry1 = log.substring(index+ENTRY_STRING.length());
      }
      else {
        entry1 = log.substring(index+ENTRY_STRING.length(),index2);
      }
      try { // Test the message code : ADD, REMOVE and creates a new Component
            // object based on the log file
        if (entry1.indexOf(ADD_COMP) != -1) {
          index = entry1.indexOf(">");
          String entry = entry1.substring(index+1);
          ComponentDescription comp = ComponentDescription.dataObjectToComponentDescription (mh.decodeData(new StringReader(entry)));
          if (hash.containsKey (comp.id)){
            hash.remove (comp.id);
          }
          hash.put (comp.id, comp); 
        }
        else if (entry1.indexOf(REMOVE_COMP) != -1) {
          index = entry1.indexOf(">");
          String entry = entry1.substring(index+1);
          ComponentDescription comp = ComponentDescription.dataObjectToComponentDescription (mh.decodeData(new StringReader(entry)));
          hash.remove (comp.id); 
        }
      } catch (DecodeException de) {
          System.out.println("DiscovererDataModel Decode: "+de);
      } catch (InvalidDecoderException ide) {
          System.out.println("DiscovererDataModel InvalidDecoder: "+ide);
      }
      index = index2;
    }
    try {
      // Clear the content of the file
      FileWriter fw = new FileWriter(filename,false);
      fw.flush ();
      fw.close ();
    }
    catch (IOException ioe) {
      System.out.println("DiscovererDataModel writeLog() IO: "+ioe);
    }
    return hash;
  }
  
}//class end
