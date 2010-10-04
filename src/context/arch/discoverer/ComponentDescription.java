/*
 * ComponentDescription.java
 *
 * Created on April 16, 2001, 11:27 AM
 */

package context.arch.discoverer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import context.arch.comm.DataObject;
import context.arch.service.helper.ServiceDescription;
import context.arch.storage.Attribute;
import context.arch.storage.AttributeNameValue;
import context.arch.storage.Attributes;
import context.arch.subscriber.Callback;
import context.arch.subscriber.Subscriber;
import context.arch.subscriber.Subscribers;
import context.arch.util.Error;

/**
 * This class allows to store a component's description
 *
 * Has changed Vectors into ArrayList
 * all server and widget att merged
 * all callback / service of widget and server merged
 *
 * @author  Agathe
 * @see context.arch.BaseObject
 */
public class ComponentDescription extends Object implements Cloneable {

  /**
   * The component id
   */
  public String id;
  
  /**
   * To component classname
   */
  public String classname;

  /**
   * The component hostname
   */
  public String hostname;
  
  /**
   * The component hostaddress
   */
  public String hostaddress;
  
  /**
   * The component location
   */
  public String location;
  
   /**
   * The component type : Discoverer.APPLICATION or Discoverer.WIDGET or Discoverer.SERVER
   * or Discoverer.INTERPRETER
   */
  public String type;
    
   /**
   * The component version
   */
  public String version;
  
  /**
   * The component port
   */
  public int port;
  
  /** 
   * ArrayLists that contain attributes, services, callbacks and subscribers 
   * <ul>
   * <il>constantAttributes : contains AttributeNameValue objects, the constant attributes. It 
   * stores both constant attributes of widget and server. For the server, the constant 
   * attributes are those of the widgets it subscribes to and its one. </il>
   * <il>nonConstantAttributes : contains Attribute objects, the non constant attribute 
   * names and the type. The same as above : it stores both non constant attributes of 
   * widget and server. For the server, the non constant attributes are those of the widgets it 
   * subscribes to and its one.</il>
   * 
   * <il>Callbacks : contains String objects, the widget callback names</il>
   * <il>Services : contains String objects, the widget services names</il>
   * <il>subscribers : contains String objects, the subscribers names</il>
   *
   * <il>inAttributes : contains Attribute objects, the interpreter incoming attribute names and the type</il>
   * <il>outAttributes : contains Attribute objects, the interpreter outgoing attribute names and the type</il>
   * </ul>
   */
  private TreeSet 
    constantAttributes,
    nonConstantAttributes,
    callbacks,
    services,
    subscribers,
    inAttributes,
    outAttributes;
  
  /**
   * The constructor that creates a new ComponentDescription with no parameters
   */
  public ComponentDescription(){
    constantAttributes = new TreeSet();
    nonConstantAttributes = new TreeSet();
    //serverConstantAttributes = new ArrayList();
    //serverNonConstantAttributes = new ArrayList();
    callbacks = new TreeSet();
    services = new TreeSet();
    subscribers = new TreeSet();
    inAttributes = new TreeSet();
    outAttributes = new TreeSet();
  }
  
  /**
   * This method allows to return the ComponentDescription version of a DataObject
   *
   * @param dataObject The DataObject containing the description of a context object
   * @return ComponentDescription The ComponentDescription version
   */
  public static ComponentDescription dataObjectToComponentDescription(DataObject dataObject){
    DataObject data = dataObject;
    
    if (data == null) return null;
    
    ComponentDescription comp = new ComponentDescription();
    
    // id
    if (data.getDataObject(Discoverer.REGISTERER_ID) != null){
      comp.setId( data.getDataObject(Discoverer.REGISTERER_ID) );
    }
    else if (data.getDataObject(Discoverer.COMPONENT_ID) != null){
      comp.setId( data.getDataObject(Discoverer.COMPONENT_ID) );
    }
    else if (data.getDataObject(Discoverer.ID) != null){
      comp.setId( data.getDataObject(Discoverer.ID) );
    }
    comp.setClassname (data.getDataObject(Discoverer.COMPONENT_CLASSNAME));
    comp.setHostname( data.getDataObject(Discoverer.HOSTNAME));
    comp.setHostaddress( data.getDataObject(Discoverer.HOSTADDRESS));
    comp.setPort (data.getDataObject(Discoverer.PORT));
    comp.setLocation(data.getDataObject(Discoverer.LOCATION_ADDRESS));
    comp.setVersion(data.getDataObject(Discoverer.VERSION));
    comp.setType( data.getDataObject(Discoverer.TYPE));

    DataObject doTemp;
    // Get constant attributes
    doTemp = data.getDataObject(Discoverer.CONSTANT_ATTRIBUTE_NAME_VALUES);
    comp.setConstantAttributes(doTemp);

    // Get non constant attributes
    doTemp = data.getDataObject(Discoverer.NON_CONSTANT_ATTRIBUTE_NAME_VALUES);
    comp.setNonConstantAttributes(doTemp);

    // Get subscribers
    doTemp = data.getDataObject(Subscribers.SUBSCRIBERS);
    comp.setSubscribers(doTemp);

     // Get widget callbacks
    doTemp = data.getDataObject(Discoverer.WIDGET_CALLBACKS);
    comp.setWidgetCallbacks(doTemp);
    
     // Get widget services
    doTemp = data.getDataObject(Discoverer.WIDGET_SERVICES);
    comp.setWidgetServices(doTemp);
    
    // Get server callbacks
    doTemp = data.getDataObject(Discoverer.SERVER_CALLBACKS);
    comp.setServerCallbacks(doTemp);
    
     // Get server services
    doTemp = data.getDataObject(Discoverer.SERVER_SERVICES);
    comp.setServerServices(doTemp);
    
    
    // Get incoming attributes
    doTemp = data.getDataObject(Discoverer.INCOMING_ATTRIBUTE_NAME_VALUES);
    comp.setInAttributes(doTemp);
    
    // Get outgoing attributes
    doTemp = data.getDataObject(Discoverer.OUTGOING_ATTRIBUTE_NAME_VALUES);
    comp.setOutAttributes(doTemp);
    return comp;
  }
  
    
  
  public DataObject toDataObject(){
    DataObject res;
    Vector v1 = new Vector();
    v1.addElement (new DataObject(Discoverer.COMPONENT_ID, id));
    v1.addElement (new DataObject(Discoverer.COMPONENT_CLASSNAME, classname));
    v1.addElement (new DataObject(Discoverer.HOSTNAME, hostname));
    v1.addElement (new DataObject(Discoverer.HOSTADDRESS, hostaddress));
    v1.addElement (new DataObject(Discoverer.PORT, new Integer(port).toString ()));
    v1.addElement (new DataObject(Discoverer.LOCATION_ADDRESS, location));
    v1.addElement (new DataObject(Discoverer.VERSION, version));
    v1.addElement (new DataObject(Discoverer.TYPE, type));
    
    Vector v = new Vector();
    Iterator it = this.constantAttributes.iterator ();
    Attributes atts = new Attributes();
    while (it.hasNext ())
      atts.addAttributeNameValue ((AttributeNameValue)it.next ());
    v.addElement (atts.toDataObject ());
    v1.addElement (new DataObject(Discoverer.CONSTANT_ATTRIBUTE_NAME_VALUES, v));  
    atts = null;
    
    it = null;
    it = this.nonConstantAttributes.iterator ();
    Attributes ncatts = new Attributes();
    v = null;
    v = new Vector();
    while (it.hasNext ()){
      Object o = it.next ();
      if (o instanceof Attribute){
        ncatts.addAttribute ((Attribute)o);
      }
      else if (o instanceof String){
        ncatts.addAttribute ((String) o);
      }
    }
    v.addElement (ncatts.toDataObject ());
    v1.addElement (new DataObject (Discoverer.NON_CONSTANT_ATTRIBUTE_NAME_VALUES, v));  
    ncatts = null;
    
    
    it = this.subscribers.iterator ();
    v = null;
    v = new Vector();
    while (it.hasNext ())
      v.addElement (new DataObject(Subscriber.SUBSCRIBER,(String)it.next ()));
    v1.addElement (new DataObject(Subscribers.SUBSCRIBERS, v));  
    
    it = this.callbacks.iterator ();
    v = null;
    v = new Vector();
    while (it.hasNext ())
      v.addElement (new DataObject(Callback.CALLBACK_NAME,(String)it.next ()));
    v1.addElement (new DataObject(Discoverer.WIDGET_CALLBACKS, v));  
    
    it = this.services.iterator ();
    v = null;
    v = new Vector();
    while (it.hasNext ())
      v.addElement (new DataObject(ServiceDescription.SERVICE_NAME, (String)it.next ()));
    v1.addElement (new DataObject (Discoverer.WIDGET_SERVICES, v));  
    
    it = this.inAttributes.iterator ();
    v = null;
    v = new Vector();
    while (it.hasNext ())
      v.addElement (((Attribute)it.next ()).toDataObject ());
    v1.addElement (new DataObject(Discoverer.INCOMING_ATTRIBUTE_NAME_VALUES, v));  
    

    it = this.outAttributes.iterator ();
    v = null;
    v = new Vector();
    while (it.hasNext ())
      v.addElement (((Attribute)it.next ()).toDataObject ());
    v1.addElement (new DataObject(Discoverer.OUTGOING_ATTRIBUTE_NAME_VALUES, v));  
   
    res = new DataObject(Discoverer.REGISTERER, v1);
    v1 = null;
    v = null;
    it = null;
    
    return res;
  
  }
  /**
   * Returns a printable version of ComponentDescription
   *
   * @return String The string version of the ComponentDescription
   */
  public String toString(){
    StringBuffer s = new StringBuffer();
    s.append("Id : "+id);
    s.append(" - Classname : "+classname);
    s.append(" - Hostname : "+hostname + " " + hostaddress);
    s.append(" - Port : "+port);
    s.append(" - Location : "+location);
    s.append(" - Type : "+ type);
    
    s.append(" - Constant Attribute : " + constantAttributes);
    //s.append(" - Constant Attribute Values : " + constantAttributeValues);
    //s.append(" - Constant Attribute NamesValues : " + constantAttributeNamesValues);
    s.append(" - Non Constant Attribute : " +nonConstantAttributes);
    s.append(" - Incoming attributes : " + inAttributes);
    s.append(" - Outgoing attributes : " + outAttributes);
    s.append(" - Callbacks : " + callbacks);
    s.append(" - Services : " + services);
    s.append(" - Subscribers : " + subscribers);
    
    return s.toString();
  }
    
  public String toSmallString(){  
    StringBuffer s = new StringBuffer();
    s.append("Id : "+id);
    s.append(" - Classname : "+classname);
    s.append(" - Hostname : "+hostname + " " + hostaddress);
    s.append(" - Port : "+port);
    s.append(" - Location : "+location);
    s.append(" - Type : "+ type);
    return s.toString();
  }
  public String toSmallStringNL(){  
    StringBuffer s = new StringBuffer();
    s.append("Id : "+id);
    s.append("\n - Hostname : "+hostname + " " + hostaddress);
    s.append("\n - Port : "+port);
    s.append("\n - Type : "+ type);
    return s.toString();
  }
  
  /**
   * Returns a printable version of ComponentDescription with new lines
   *
   * @return String The string version of the ComponentDescription
   */
  public String toStringNL(){
    StringBuffer s = new StringBuffer();
    s.append("\n- Id : "+id);
    s.append("\n- Classname : "+classname);
    s.append("\n- Hostname : "+hostname + " " + hostaddress);
    s.append("\n- Port : "+port);
    s.append("\n- Location : "+location);
    s.append("\n- Type : "+ type);
    
    s.append("\n- Constant Attribute : " + constantAttributes);
    s.append("\n- Non Constant Attribute : " +nonConstantAttributes);
    s.append("\n - Incoming attributes : " + inAttributes);
    s.append("\n - Outgoing attributes : " + outAttributes);
    s.append("\n - Widget callbacks : " + callbacks);
    s.append("\n - Widget services : " + services);
    s.append("\n - Subscribers : " + subscribers);
    
    return s.toString();
  }
  
  /**
   * Adds the data parameter to the constant attributes
   *
   * @param data The name of the constant attribute
   * @see context.arch.storage.Attributes
   */
  public void setConstantAttribute (AttributeNameValue data){
    constantAttributes.add(data);
  }
  
  /**
   * Adds a set of attribute names to the constant attributes. Gets a 
   * DISCOVERER_CONSTANT_ATTRIBUTE_NAME_VALUES DataObject
   *
   * @param data The DataObject containing a set of constant attribute names
   * @return Error The error code
   * @see context.arch.storage.Attributes
   */
  public Error setConstantAttributes(DataObject data){
    Error err = new Error();
    if (data != null) {
      // Gets the ATTRIBUTE_NAME_VALUES data object's content
      DataObject doAtt = (DataObject) data.getChildren().firstElement();
      //Retrieve the attributeNameValues object
      Attributes atts = new Attributes(doAtt);
      // Gets each AttributeNameValue
      Enumeration listAttNV = atts.elements();
      while (listAttNV.hasMoreElements()){
        AttributeNameValue att = (AttributeNameValue) listAttNV.nextElement();
        setConstantAttribute(att);
      }  
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
  
    /**
   * Adds the data parameter to the non constant attributes
   *
   * @param data The non constant attribute
   * @see context.arch.storage.Attribute
   */
   public void setNonConstantAttribute (Attribute data){
     if (data != null ){
      nonConstantAttributes.add(data);
     }
    }

  /**
   * Adds a set of attribute names to the non constant attributes. Gets a 
   * DISCOVERER_NON_CONSTANT_ATTRIBUTE_NAME_VALUES DataObject
   *
   * @param data The DataObject containing a set of non constant attribute names
   * @return Error The error code
   * @see context.arch.storage.Attribute
   */
  public Error setNonConstantAttributes(DataObject data){
    Error err = new Error();
    if (data != null) {
      Vector vTemp = data.getChildren();
      if ( ! vTemp.isEmpty()) {
        DataObject doNCAtt = (DataObject) vTemp.firstElement();
        Attributes NCatts = new Attributes(doNCAtt);
        for(int i=0,n=NCatts.numAttributes();i<n;i++) {
          setNonConstantAttribute(NCatts.getAttributeAt(i));
        }   
      }
      // There is no error if the field is empty
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
  
 /**
 * Adds the data parameter to the incoming attributes
 *
 * @param data The incoming attribute
 * @see context.arch.storage.Attribute
 */
 public void setInAttribute (Attribute data){
   if (data != null){
    inAttributes.add(data);
   }
  }

  /**
   * Adds a set of incoming attribute names to the cincoming attributes. Gets a 
   * INCOMING_DISCOVERER_ATTRIBUTE_NAME_VALUES DataObject
   *
   * @param data The DataObject containing a set of incoming attribute names
   * @return Error The error code
   * @see context.arch.storage.Attribute
   */
  public Error setInAttributes(DataObject data){
    Error err = new Error();
    if (data != null) {
      Vector vTemp = data.getChildren();
      if ( ! vTemp.isEmpty()) {
        DataObject doInAtt = (DataObject) vTemp.firstElement();
        Vector Inatts = doInAtt.getChildren();
        Enumeration listInAtt = Inatts.elements();
        DataObject InattValue;
        while (listInAtt.hasMoreElements()){
          InattValue = (DataObject) listInAtt.nextElement();
          setInAttribute(new Attribute(InattValue));
        }   
      }
      // There is no error if the field is empty
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
  
   /**
   * Adds the data parameter to the outgoing attributes
   *
   * @param data The outgoing attribute
   * @see context.arch.storage.Attribute
   */
 public void setOutAttribute (Attribute data){
   if (data != null ){
     outAttributes.add(data);
   }
  }

  /**
   * Adds a set of outgoing attributes to the outgoing attributes. Gets a 
   * OUTGOING_DISCOVERER_CONSTANT_ATTRIBUTE_NAME_VALUES DataObject
   *
   * @param data The DataObject containing a set of outgoing attribute names
   * @return Error The error code
   * @see context.arch.storage.Attribute
   */
  public Error setOutAttributes(DataObject data){
    Error err = new Error();
    if (data != null) {
      Vector vTemp = data.getChildren();
      if ( ! vTemp.isEmpty()) {
        DataObject doAtt = (DataObject) vTemp.firstElement();
        Vector atts = doAtt.getChildren();
        Enumeration listAtt = atts.elements();
        DataObject attValue;
        while (listAtt.hasMoreElements()){
          attValue = (DataObject) listAtt.nextElement();
          setOutAttribute(new Attribute(attValue));
        }   
      }
      // There is no error if the field is empty
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
    
  
  /**
   * Adds a set of server constant attributes to the server attributes. Gets a 
   * Discoverer.SERVER_CONSTANT_ATTRIBUTES DataObject
   *
   * @param data The DataObject containing a set of server attribute names
   * @return Error The error code
   * @see context.arch.storage.Attribute
   */
  public Error setServerConstantAttributes(DataObject data){
    Error err = new Error();
    Vector vTemp;
    if ( (data != null) && ((vTemp = data.getChildren()) != null) ) {
      DataObject doAtt = (DataObject) vTemp.firstElement();
      Vector atts = doAtt.getChildren();
      Enumeration listAtt = atts.elements();
      DataObject attValue;
      while (listAtt.hasMoreElements()){
        attValue = (DataObject) listAtt.nextElement();
        setServerConstantAttribute(new AttributeNameValue(attValue));
      }  
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
  
    /**
   * Adds a set of server non constant attributes to the server attributes. Gets a 
   * Discoverer.SERVER_NON_CONSTANT_ATTRIBUTES DataObject
   *
   * @param data The DataObject containing a set of server attribute names
   * @return Error The error code
   * @see context.arch.storage.Attribute
   */
  public Error setServerNonConstantAttributes(DataObject data){
    Error err = new Error();
    Vector vTemp;
    if ( (data != null) && ((vTemp = data.getChildren()) != null) ) {
      DataObject doAtt = (DataObject) vTemp.firstElement();
      Vector atts = doAtt.getChildren();
      Enumeration listAtt = atts.elements();
      DataObject attValue;
      while (listAtt.hasMoreElements()){
        attValue = (DataObject) listAtt.nextElement();
        setServerNonConstantAttribute(new Attribute(attValue));
      }  
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
  
 /**
   * Adds the data parameter to the server constant attributes
   *
   * @param data The server constant attribute
   * @see context.arch.storage.Attribute
   */ 
 public void setServerConstantAttribute (Attribute data){
   if (data != null ){
    constantAttributes.add(data);
   }
 }
  
 /**
   * Adds the data parameter to the server non constant attributes
   *
   * @param data The servernopn constant attribute
   * @see context.arch.storage.Attribute
   */ 
 public void setServerNonConstantAttribute (Attribute data){
   if (data != null ){
    nonConstantAttributes.add(data);
   }
 }
 
 /**
   * Adds the data parameter to the subscriber attributes
   *
   * @param data The name of the subscriber attribute
   * @see context.arch.storage.Attribute
   */
  public void setSubscriber (String data){
    if (data != null && ! data.trim().equals("")){
      subscribers.add(data);
    }
  }
    
  /**
   * Adds a set of subscriber names to thesubscriber attributes. Gets a 
   * DISCOVERER_CONSTANT_ATTRIBUTE_NAME_VALUES DataObject
   *
   * @param data The DataObject containing a set of subscriber attribute names
   * @return Error The error code
   * @see context.arch.storage.Attribute
   */  
  public Error setSubscribers(DataObject data){
    Error err = new Error();
    Vector vTemp;
    if (data != null) {
      vTemp = data.getChildren();
      if (! vTemp.isEmpty()) {
        Enumeration listSub = vTemp.elements();
        DataObject subValue;
        while (listSub.hasMoreElements()){
          subValue = (DataObject) listSub.nextElement();
          if (! subValue.getValue().isEmpty()){
            setSubscriber((String) subValue.getValue().firstElement());
          }
        }
      }
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
  
    /**
   * Adds the data parameter to the server services
   *
   * @param data The name of the server services
   * @see context.arch.storage.Attribute
   */
    public void setServerService (String data){
      if (data != null && ! data.trim().equals("")){
        services.add(data);
      }
    }
    
    /**
   * Adds a set of server service names. Gets a 
   * SERVER_SERVICES DataObject
   *
   * @param data The DataObject containing a set of server services names
   * @return Error The error code
   * @see context.arch.storage.Attribute
   */
  public Error setServerServices(DataObject data){
    Error err = new Error();
    Vector vTemp;
    if (data != null) {
      vTemp = data.getChildren();
      if ( ! vTemp.isEmpty() ) {
        Enumeration list = vTemp.elements();
        DataObject value;
        while (list.hasMoreElements()){
          value = (DataObject) list.nextElement();
          if (! value.getValue().isEmpty()){
            setServerService((String) value.getValue().firstElement().toString());
          }
        }  
      }
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
    
    /**
   * Adds the data parameter to the widget callbacks
   *
   * @param data The name of the widget callbacks
   * @see context.arch.storage.Attribute
   */
    public void setWidgetCallback (String data){
      if (data != null && ! data.trim().equals("")){
        callbacks.add(data);
      }
    }
  
  /**
   * Adds a set of widget callback names to the callback. Gets a 
   * WIDGET_CALLBACKS DataObject
   *
   * @param data The DataObject containing a set of widget callback names
   * @return Error The error code
   * @see context.arch.storage.Attribute
   */
  public Error setWidgetCallbacks(DataObject data){
    Error err = new Error();
    Vector vTemp;
    if (data != null) {
      vTemp = data.getChildren();
      if ( ! vTemp.isEmpty() ) {
        Enumeration listCall = vTemp.elements();
        DataObject callValue;
        while (listCall.hasMoreElements()){
          callValue = (DataObject) listCall.nextElement();
          if (! callValue.getValue().isEmpty()){
            setWidgetCallback((String) callValue.getValue().firstElement());
          }
        }  
      }
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
  
   /**
   * Adds the data parameter to the widget services
   *
   * @param data The name of the widget services
   * @see context.arch.storage.Attribute
   */
    public void setWidgetService (String data){
      if (data != null && ! data.trim().equals("")){
        services.add(data);
      }
    }
    
  /**
   * Adds a set of service names. Gets a 
   * WIDGET_SERVICES DataObject
   *
   * @param data The DataObject containing a set of widget services names
   * @return Error The error code
   * @see context.arch.storage.Attribute
   */
  public Error setWidgetServices(DataObject data){
    Error err = new Error();
    Vector vTemp;
    if (data != null) {
      vTemp = data.getChildren();
      if ( ! vTemp.isEmpty() ) {
        Enumeration list = vTemp.elements();
        DataObject value;
        while (list.hasMoreElements()){
          value = (DataObject) list.nextElement();
          if (!value.getValue().isEmpty()){
            setWidgetService((String) value.getValue().firstElement());
          }
        }  
      }
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
  
  /**
   * Adds the data parameter to the server callbacks
   *
   * @param data The name of the server callbacks
   * @see context.arch.storage.Attribute
   */
    public void setServerCallback (String data){
      if (data != null && ! data.trim().equals("")){
        callbacks.add(data);
      }
    }
    
    /**
   * Adds a set of server callback names. Gets a 
   * SERVER_CALLBACKS DataObject
   *
   * @param data The DataObject containing a set of server callback names
   * @return Error The error code
   * @see context.arch.storage.Attribute
   */
  public Error setServerCallbacks(DataObject data){
    Error err = new Error();
    Vector vTemp;
    if (data != null) {
      vTemp = data.getChildren();
      if ( ! vTemp.isEmpty() ) {
        Enumeration list = vTemp.elements();
        DataObject value;
        while (list.hasMoreElements()){
          value = (DataObject) list.nextElement();
          if (! value.getValue().isEmpty()) {
            setServerCallback((String) value.getValue().firstElement().toString());
          }
        }  
      }
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
    
  /**
   * Sets the id. Gets an ID DataObject
   *
   * @param data The DataObject containing the id
   * @return Error The error code
   * @see context.arch.BaseObject#ID
   */
  public Error setId (DataObject data){
    Error err = new Error();  
    if (data != null && ! data.getValue().isEmpty() ) {
      id = ((String) data.getValue().firstElement());
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
    
  /**
   * Sets the classname. Gets a 
 * COMPONENT_CLASSNAME DataObject
   *
   * @param data The DataObject containing the classname
   * @return Error The error code
   * @see context.arch.discoverer.Discoverer
   */  
  public Error setClassname (DataObject data){
    Error err = new Error();  
    if (data != null && ! data.getValue().isEmpty()) {
      classname = ((String) data.getValue().firstElement());
      err.setError(Error.NO_ERROR);
    }
    else
      err.setError(Error.INVALID_DATA_ERROR);
    return err;
  }
    
  /**
   * Sets the component type. Gets a 
   * TYPE DataObject
   *
   * @param data The DataObject containing the type
   * @return Error The error code
   * @see context.arch.discoverer.Discoverer
   */ 
    public Error setType(DataObject data){
      Error err = new Error();  
      if (data != null && ! data.getValue().isEmpty()) {
        type =( (String) data.getValue().firstElement());
        err.setError(Error.NO_ERROR);
      }
      else
        err.setError(Error.INVALID_DATA_ERROR);
      return err;
    }
    
    /**
   * Sets the component hostname. Gets a 
   * HOSTNAME DataObject
   *
   * @param data The DataObject containing the hostname
   * @return Error The error code
   * @see context.arch.discoverer.Discoverer
   */ 
    public Error setHostname(DataObject data){
      Error err = new Error();  
      if (data != null && ! data.getValue().isEmpty()) {
        hostname = ((String) data.getValue().firstElement());
        err.setError(Error.NO_ERROR);
      }
      else
        err.setError(Error.INVALID_DATA_ERROR);
      return err;
    }
 
    /**
   * Sets the component hostaddress. Gets a 
   * HOSTADDRESS DataObject
   *
   * @param data The DataObject containing the hostaddress
   * @return Error The error code
   * @see context.arch.discoverer.Discoverer
   */ 
    public Error setHostaddress(DataObject data){
      Error err = new Error();  
      if (data != null && ! data.getValue().isEmpty()) {
        hostaddress = ((String) data.getValue().firstElement());
        err.setError(Error.NO_ERROR);
      }
      else
        err.setError(Error.INVALID_DATA_ERROR);
      return err;
    }
    
    /**
   * Sets the component port. Gets a 
   * PORT DataObject
   *
   * @param data The DataObject containing the port
   * @return Error The error code
   * @see context.arch.discoverer.Discoverer
   */ 
    public Error setPort(DataObject data){
      Error err = new Error();  
      if (data != null && ! data.getValue().isEmpty()){
        try {
          int i;
          i = Integer.parseInt((String) data.getValue().firstElement());
          port = i;
          err.setError(Error.NO_ERROR);
        }catch (NumberFormatException nfe) {
          System.out.println("Discoverer - addComponent - NumberFormatException " + nfe); 
          err.setError(Error.INVALID_DATA_ERROR);
        }
      }
      else
        err.setError(Error.INVALID_DATA_ERROR);
      return err;
    }
      
    /**
   * Sets the component location. Gets a 
   * LOCATION DataObject
   *
   * @param data The DataObject containing the location
   * @return Error The error code
   * @see context.arch.discoverer.Discoverer
   */ 
    public Error setLocation(DataObject data){
      Error err = new Error();  
      if (data != null && ! data.getValue().isEmpty()) {
        location = ((String) data.getValue().firstElement());
        err.setError(Error.NO_ERROR);
      }
      else
        err.setError(Error.INVALID_DATA_ERROR);
      return err;
    }
    
    /**
   * Sets the component version. Gets a 
   * VERSION DataObject
   *
   * @param data The DataObject containing the version
   * @return Error The error code
   * @see context.arch.discoverer.Discoverer
   */ 
    public Error setVersion(DataObject data){
      Error err = new Error();  
      if (data != null && ! data.getValue().isEmpty()) {
        version = ((String) data.getValue().firstElement());
        err.setError(Error.NO_ERROR);
      }
      else
        err.setError(Error.INVALID_DATA_ERROR);
      return err;
    }
  
  /**
   * This method allows to get the modified descriptions fields and update the component
   * Fields that can be modified : the non constant attributes (widget, server) and the subscribers, 
   * It gets a NON_CONSTANT_ATTRIBUTE_NAME_VALUES
   * The default update type is the add type (Discoverer.UPDATE_ADD_TYPE)
   *  
   * @param data The DataObject containing the modified fields
   * @return Error The error code
   * @see context.arch.comm.DataObject
   * @see context.arch.discoverer.Discoverer
   */
  public Error updateDescription(DataObject data){
    Error err = updateDescription(data, Discoverer.UPDATE_ADD_TYPE);
    return err;
  }
    
  /**
   * This method allows to get the modified descriptions fields and update the component
   * Fields that can be modified : the non constant attributes (widget, server) and the subscribers, 
   * It gets a NON_CONSTANT_ATTRIBUTE_NAME_VALUES
   *  
   * @param data The DataObject containing the modified fields
   * @return Error The error code
   * @see context.arch.comm.DataObject
   * @see context.arch.discoverer.Discoverer
   */
  public Error updateDescription(DataObject data, String updateType){
    Error error = new Error();
    
    if (data != null) { 
      if (updateType == null){
        updateType = Discoverer.UPDATE_ADD_TYPE;
      }
      //Test the widget non constant attributes
      DataObject nonCAtt = data.getDataObject(Discoverer.NON_CONSTANT_ATTRIBUTE_NAME_VALUES);
      if (updateType.equalsIgnoreCase(Discoverer.UPDATE_REPLACE_TYPE) && nonCAtt != null){
        nonConstantAttributes = null;
        nonConstantAttributes = new TreeSet();
      }
      setNonConstantAttributes(nonCAtt);
      
      // Test the subscribers
      DataObject subs = data.getDataObject(Subscribers.SUBSCRIBERS);
      if (updateType.equalsIgnoreCase(Discoverer.UPDATE_REPLACE_TYPE) && subs != null){
        subscribers = null;
        subscribers = new TreeSet();
      }
      setSubscribers(subs);
     
      error.setError(Error.NO_ERROR);
    }
    else
      error.setError(Error.INVALID_DATA_ERROR);
    return error ;
  }
  
  /**
   * Returns an enumeration of the constant attribute names
   *
   * @return Enumeration The list of constant attribute names
   *
  public Enumeration getConstantAttributeNames(){
    return constantAttributeNames.elements();
  }
   */
  
   /**
   * Returns an enumeration of the constant attribute values
   *
   * @return Enumeration The list of constant attribute values
   *
  public Enumeration getConstantAttributeValues(){
    return constantAttributeValues.elements();
  }
    */
  
  /**
   * Returns an enumeration of the constant attribute names&values
   *
   * @return Enumeration The list of constant attribute objects, as a list of Attribute objects
   */ 
  public SortedSet getConstantAttributes(){
    return constantAttributes;
  }
  
  /**
   * Returns an enumeration of the non constant attributes
   *
   * @return Collection The list of non constant attributes, as a list of Attribute objects
   */ 
  public SortedSet getNonConstantAttributes(){
    return nonConstantAttributes;
  }
  
  /**
   * Returns an enumeration of the all non constant attributes (non constant and constant)
   *
   * @return Enumeration The list of non constant attributes, as a list of Attribute objects
   */ 
  public SortedSet getAllAttributes(){
    SortedSet c = (SortedSet) nonConstantAttributes.clone ();
    c.addAll(constantAttributes);
    return c;
  }
  
  /**
   * Returns an enumeration of the incoming attributes
   *
   * @return Enumeration The list of incoming attributes, as a list of Attribute objects
   */ 
  public SortedSet getInAttributes(){
    return inAttributes;
  }
  
  /**
   * Returns an enumeration of the outgoing attributes
   *
   * @return Enumeration The list of outgoing attributes, as a list of Attribute objects
   */ 
  public SortedSet getOutAttributes(){
    return outAttributes;
  }
  
  /**
   * Returns an enumeration of the constant attributes names
   *
   * @return Enumeration The list of widget/server constant attributes names
   */
  public SortedSet getConstantAttributeNames(){
    TreeSet result = new TreeSet();
    Iterator listAtt = (this.getConstantAttributes()).iterator();
    while (listAtt.hasNext ()){
      AttributeNameValue att = (AttributeNameValue) listAtt.next();
      result.add(att.getName());
    }
    return result;
  }
  
  /**
   *
   */
  public SortedSet getConstantAttributeValues(){
    TreeSet result = new TreeSet();
    Iterator listAtt = (this.getConstantAttributes()).iterator();
    while (listAtt.hasNext ()){
      AttributeNameValue att = (AttributeNameValue) listAtt.next();
      result.add(att.getValue().toString());
    }
    return result;
  }
  
  /**
   *
   */
  public SortedSet getConstantAttributeNameValues(){
    TreeSet result = new TreeSet();
    Iterator listAtt = (this.getConstantAttributes()).iterator();
    while (listAtt.hasNext()){
      AttributeNameValue att = (AttributeNameValue) listAtt.next();
      result.add(att.getName().toString()+Discoverer.FIELD_SEPARATOR+att.getValue().toString());
    }
    return result;
  }
  
  /**
   * Returns an enumeration of the callbacks
   *
   * @return Enumeration The list of callbacks
   */ 
  public SortedSet getCallbacks(){
    return callbacks;
  }
  
  /**
   * Returns an enumeration of the widget callbacks,
   * but if the callback name contains an underscore,
   * then it returns just the last word.
   * For example : for widgetName_callbackName we just
   * return callbackName
   *
   * @return Enumeration The list of callbacks
   */ 
  public SortedSet getJustNameOfWidgetCallbacks(){
    TreeSet v = new TreeSet();
    Iterator list = getCallbacks().iterator();
    while (list.hasNext ()){
      String call = (String) list.next();
      String underscore = "_";
      int lastIndex = call.lastIndexOf ("_");
      try {
        String res = call.substring (lastIndex + 1, call.length ());
        v.add (res);
      }
      catch(IndexOutOfBoundsException ioobe) {
        System.out.println("ComponentDescription - getJustNameOfWidgetCallbacks - no underscore");
        v.add (call);
      }
    }
    return v;
  }
  
  /**
   * Returns an enumeration of the widget services
   *
   * @return Enumeration The list of widget services
   */ 
  public SortedSet getServices(){
    return services;
  }
  
  
  /**
   * Returns an enumeration of thesubscribers
   *
   * @return Enumeration The list of subscribers
   */ 
  public SortedSet getSubscribers(){
    return subscribers;
  }
    
  /**
   * This method allows to return a DataObject containing :
   *    - the component id
   *    - the component hostname
   *    - the component port
   *
   * @return DataObject The data object containing the information
   */
  public DataObject getBasicDataObject(){
    ArrayList v1 = new ArrayList();
    v1.add(new DataObject(Discoverer.ID, this.id));
    v1.add(new DataObject (Discoverer.HOSTNAME, this.hostname));
    v1.add(new DataObject (Discoverer.PORT, Integer.toString(this.port)));
    v1.add(new DataObject (Discoverer.TYPE, this.type));
    Vector v = new Vector((Collection)v1);
    return new DataObject(Discoverer.DISCOVERER_QUERY_REPLY_CONTENT, v);
  }
  
  /**
   * This method allows to compare 2 ComponentDescription objects.
   *
   * @param otherComponent The ComponentDescription to compare to
   * @return boolean The result of the comparison
   */
  public boolean equals(ComponentDescription otherComponent){
    boolean result = true;
    ComponentDescription c1 = this;
    ComponentDescription c2 = otherComponent;
    
    if (! c1.id.equals(c2.id) || ! c1.classname.equals(c2.classname) || ! c1.hostaddress.equals(c2.hostaddress)){
      result = false;
    }
    if (result){
      if ( ! c1.hostname.equals(c2.hostname) ||  ! c1.location.equals(c2.location) || ! c1.type.equals(c2.type) || ! c1.version.equals(c2.version)) {
        result = false;
      }
    } 
    if (result){
      if (! (c1.port == c2.port)){
        result = false;
      }
    }
    return result;
  }
  
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  
}// end of class
