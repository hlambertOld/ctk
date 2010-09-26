package context.arch.server; 

import context.arch.comm.DataObject;
import context.arch.discoverer.Discoverer;
import context.arch.handler.Handler;
import context.arch.widget.Widget;
import context.arch.widget.WidgetHandle;
import context.arch.widget.WidgetHandles;
import context.arch.storage.AttributeNameValue;
import context.arch.storage.Attributes;
import context.arch.storage.Attribute;
import context.arch.subscriber.Callbacks;
import context.arch.subscriber.Callback;
import context.arch.service.Services;
import context.arch.service.InheritedService;
import context.arch.service.helper.ServiceDescriptions;
import context.arch.service.helper.ServiceDescription;
import context.arch.MethodException;
import context.arch.InvalidMethodException;
import context.arch.util.Error;

import context.arch.subscriber.ClientSideSubscriber;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

/**
 * This class is the basic context server, with attributes and
 * methods that should apply to all context servers.
 *
 * A server is a widget with added gathering and storage facilities.
 * Servers are attached to people (incl. groups), places and things.
 *
 * Basically, a server subscribes to a set of widgets, stores and
 * updates the attribute values from the widgets.
 *
 * A server has a "key" attribute that identifies the entity it is
 * attached to. For example, a user server's key may be USERNAME. The server
 * will only request and store information that pertains to a give value
 * of USERNAME.
 *
 * @see context.arch.widget.Widget
 */
public abstract class Server extends Widget implements Handler {

  /**
   * Debug flag. Set to true to see debug messages.
   */
  private static final boolean DEBUG = true;

  /**
   * The tag for the type of this object
   */
  public static final String SERVER_TYPE = "server";
  
  /**
   * Default port to use for communications.
   */
  public static final int DEFAULT_PORT = 6000;

  /**
   * The set of widgets this server is monitoring.
   */
  protected WidgetHandles widgets;	

  /**
   * The current port of the server
   */
  private int serverPort;
  
  /**
   *
   */
  private Attributes attributesCache;
  
  /**
   *
   */
  private Hashtable attributesTimes;
     
  /**
   * Constructor that sets up internal variables for maintaining
   * the list of server attributes and callbacks, and setting up
   * the Widget info.
   *
   * @param clientClass Class to use for client communications
   * @param serverClass Class to use for server communications
   * @param serverPort Port to use for server communications
   * @param encoderClass Class to use for communications encoding
   * @param decoderClass Class to use for communications decoding
   * @param storageClass Class to use for storage
   * @param id String to use for widget id and persistent storage 
   * @param widgets The set of widgets this server will subscribe to
   */
  public Server(String clientClass, String serverClass, int serverPort, String encoderClass,
                String decoderClass, String storageClass, String id,
                WidgetHandles widgets) {
    super(clientClass,serverClass,serverPort,encoderClass,decoderClass,storageClass,id);
    this.widgets = widgets;
    this.serverPort = serverPort;
    serverSetup();
  }    

  /**
   * Constructor that sets up internal variables for maintaining
   * the list of server attributes and callbacks, and setting up
   * the Widget info. This version takes a boolean to indicate whether the 
   * default storage class should be used or whether not storage should be 
   * provided.
   *
   * @param clientClass Class to use for client communications
   * @param serverClass Class to use for server communications
   * @param serverPort Port to use for server communications
   * @param encoderClass Class to use for communications encoding
   * @param decoderClass Class to use for communications decoding
   * @param storageFlag Flag to determine whether storage should be used or not
   * @param id String to use for widget id and persistent storage 
   * @param widgets The set of widgets this server will subscribe to
   */
  public Server(String clientClass, String serverClass, int serverPort, String encoderClass,
                String decoderClass, boolean storageFlag, String id,
                WidgetHandles widgets) {
    super(clientClass,serverClass,serverPort,encoderClass,decoderClass,storageFlag,id);
    this.widgets = widgets;
    this.serverPort = serverPort;
    serverSetup();
  }    

  /**
   * Constructor that sets up internal variables for maintaining
   * the list of server attributes and callbacks.  It takes a port
   * number as a parameter to indicate which port to listen for
   * messages/connections.
   *
   * @param port Port to listen to for incoming messages
   * @param id Widget id
   * @param widgets The set of widgets this server will subscribe to
   */
  public Server(int port, String id, WidgetHandles widgets) {
    this(null,null,port,null,null,null,id, widgets);
  }

  /**
   * Constructor that sets up internal variables for maintaining
   * the list of server attributes and callbacks.  It takes a port
   * number as a parameter to indicate which port to listen for
   * messages/connections.
   *
   * @param port Port to listen to for incoming messages
   * @param id Widget id
   * @param storageFlag Flag to determine whether storage should be used or not
   * @param widgets The set of widgets this server will subscribe to
   */
  public Server(int port, String id, boolean storageFlag, WidgetHandles widgets) {
    this(null,null,port,null,null,storageFlag,id, widgets);
  }
	
  /**
   * Constructor that sets up internal variables for maintaining
   * the list of server attributes and callbacks.  It takes the 
   * widget id as a parameter
   *
   * @param id ID of the widget
   * @param widgets The set of widgets this server will subscribe to
   */
  public Server(String id, WidgetHandles widgets) {
    this(null,null,-1,null,null,null,id, widgets);
  }

  /**
   * Constructor that sets up internal variables for maintaining
   * the list of server attributes and callbacks.  It takes the 
   * widget id as a parameter with storage functionality set to storageFlag
   *
   * @param id ID of the widget
   * @param widgets The set of widgets this server will subscribe to
   * @param storageFlag Flag to determine whether storage should be used or not
   */
  public Server(String id, boolean storageFlag, WidgetHandles widgets) {
    this(null,null,-1,null,null,storageFlag,id, widgets);
  }

  /**
   * Returns the type of the object
   * This method should be overridden
   *
   * @return String
   */
  public String getType(){
    return Server.SERVER_TYPE;
  }
  
  /** 
   * This method sets up the server for use.  This includes getting the 
   * attributes and services information from relevant widgets.
   *
   * Modification made by Agathe
   * @see #setAttributes()
   * @see #setServices()
   */
  protected void serverSetup() {
    attributes = initAttributes();
    constantAttributes = initConstantAttributes(); // added by Agathe
    attributesCache = new Attributes();
    attributeTypes = attributes.toTypesHashtable();
    // constantAttributeTypes = constantAttributes.toTypesHashtable(); // Added by agathe
    // ??
    for (int i=0; i<attributes.numAttributes(); i++) {
      Attribute att = attributes.getAttributeAt(i);
      attributesCache.addAttributeNameValue(att.getName(),att.getSubAttributes(),att.getType());
    }
    attributesTimes = new Hashtable();
    Long long1 = new Long(0);
    for (Enumeration e = attributeTypes.keys(); e.hasMoreElements();) {
      attributesTimes.put(e.nextElement(),long1);
    }
    if (storage != null) {
      storage.setAttributes(attributes,attributeTypes);
      // storage.setConstantAttributes(constantAttributes,cosntantAttributeTypes); // Added by agathe
    }
    services = initServices();
  }

  /**
   * This method sets the widgets that this server should subscribe to.
   * Either the constructor should include a widgets parameter or this
   * method should be called before startSubscriptions() is called
   *
   * @param widgets Handles of widgets to subscribe to
   */
  public void setWidgets(WidgetHandles widgets) {
    this.widgets = widgets;
  }

  /**
   * This method is called to subscribe to the widgets this server
   * is interested in.  The reason this is not part of the constructor
   * is that the individual server's conditions will not be set yet.
   * This should be called after a constructor sets the widget handles 
   * to use or after setWidgets() has been called.
   *
   * @see #setCallbacks()
   */
  public void startSubscriptions() {
    callbacks = initCallbacks();
  }

  /**
   * This method is called to aggregate the list of non cosntant attributes that the
   * widgets relevant to this server provide.
   * This should be called after a constructor sets the widget handles 
   * to use or after setWidgets() has been called.
   *
   * @return the server attributes
   */
  protected Attributes initAttributes() {
    // this protects us against the Widget constructor 
    // that calls us too early (we havent' got the widgets yet)
    // it's good practice anyway
    if (widgets == null) {
      return null;
    }

    Attributes atts = new Attributes();
    for (int i = 0; i < widgets.size (); i++) {
      WidgetHandle handle = widgets.getWidgetHandleAt (i);

      DataObject widgetAtts = getWidgetAttributes(handle.getHostName(), handle.getPort(), handle.getId());
      String error = new Error(widgetAtts).getError();
      if (error != null) {
        if (error.equals(Error.NO_ERROR)) {
          Attributes wAtts = new Attributes(widgetAtts);
          for (int j=0; j<wAtts.numAttributes(); j++) {
            Attribute wAtt = wAtts.getAttributeAt(j);
            if (atts.getAttribute(wAtt.getName()) == null) {
              atts.addAttribute(wAtt);
            }
          }
        }
      }
    }

    atts.addAttributes(setServerAttributes());

    return atts;
  }
  
  /**
   * This method is called to aggregate the list of constant attributes that the
   * widgets relevant to this server provide.
   * This should be called after a constructor sets the widget handles 
   * to use or after setWidgets() has been called.
   *
   * @return AttributeNameValues the server constant attributes
   */
  protected Attributes initConstantAttributes() {
    // this protects us against the Widget constructor 
    // that calls us too early (we havent' got the widgets yet)
    // it's good practice anyway
    if (widgets == null) {
      return null;
    }

    Attributes atts = new Attributes();
    for (int i = 0; i < widgets.size (); i++) {
      WidgetHandle handle = widgets.getWidgetHandleAt (i);

      DataObject widgetAtts = getWidgetConstantAttributes(handle.getHostName(), handle.getPort(), handle.getId());
      String error = new Error(widgetAtts).getError();
      if (error != null) {
        if (error.equals(Error.NO_ERROR)) {
          Attributes wAtts = new Attributes(widgetAtts);
          for (int j=0; j<wAtts.numAttributes(); j++) {
            AttributeNameValue wAtt = (AttributeNameValue) wAtts.getAttributeAt(j);
            if (atts.getAttributeNameValue(wAtt.getName()) == null) {
              atts.addAttributeNameValue(wAtt);
            }
          }
        }
      }
    }

    atts.addAttributes(setServerConstantAttributes());

    return atts;
  }
  
  /**
   * This abstract method set the non constant attributes for a server - those
   * that are specific to the server, and not contained in the widgets
   * it subscribes to.
   *
   * @return Attributes The Attributes
   */
  protected abstract Attributes setServerAttributes();
  
  /**
   * This abstract method set the constant attributes for a server - those
   * that are specific to the server, and not contained in the widgets
   * it subscribes to.
   *
   * @return Attributes The constant Attributes
   */
  protected abstract Attributes setServerConstantAttributes();
  

  /**
   * This method is called to aggregate the list of services that the
   * widgets relevant to this widget provide.  This allows the server
   * to act as a proxy to the individual widgets' services.
   * This should be called after a constructor sets the widget handles 
   * to use or after setWidgets() has been called.
   *
   * @return the server services
   */
  protected Services initServices() {
    if (widgets == null) {
      return null;
    }

    Services services = new Services();
    for (int i=0; i<widgets.size(); i++) {
      WidgetHandle handle = widgets.getWidgetHandleAt(i);
      DataObject widgetServices = getWidgetServices(handle.getHostName(), handle.getPort(), handle.getId());
      String error = new Error(widgetServices).getError();
      if (error != null) {
        if (error.equals(Error.NO_ERROR)) {
          ServiceDescriptions wServices = new ServiceDescriptions(widgetServices);
          for (int j=0; j<wServices.numServiceDescriptions(); j++) {
            ServiceDescription desc = wServices.getServiceDescriptionAt(j);
            services.addService(new InheritedService(this,getId(),desc.getName(),desc.getFunctionDescriptions(),
                                handle.getHostName(),Integer.toString(handle.getPort()),handle.getId()));
          }
        }
      }
    }
    services.addServices(setServerServices());

    return services;
  }

  /**
   * This abstract method set the services for a server - those
   * that are specific to the server, and not contained in the widgets
   * it subscribes to.
   *
   * @return Services specific to the server
   */
  protected abstract Services setServerServices();

  /**
   * This method is called to subscribe to the widgets this server
   * is interested in.  It allows the server to act as a proxy to
   * the callbacks provided by each individual widget.
   * This should be called after a constructor sets the widget handles 
   * to use or after setWidgets() has been called.
   *
   * @see #setCallbacks()
   */
  protected Callbacks initCallbacks() {
    // this protects us against the Widget constructor 
    // that calls us too early (we havent' got the widgets yet)
    // it's good practice anyway
    if (widgets == null) {
      return null;
    }

    Callbacks calls = new Callbacks();
    // For all widgets
    for (int i = 0; i < widgets.size (); i++) {
      WidgetHandle handle = widgets.getWidgetHandleAt (i);
      addWidgetCallbacksSubscription(handle, calls);
    }
    calls.addCallbacks(setServerCallbacks());

    return calls;
  }

  /**
   * This method first sends a message to the widget specified by handle to get
   * the list of its callbacks, then the server subscribes to each of them. The
   * subscription is added to calls
   *
   * @param handle The widget to subscribe to
   * @param calls The subscription results are added to calls
   */
  private void addWidgetCallbacksSubscription(WidgetHandle handle, Callbacks calls){
    // Get all callbacks of each widget
      DataObject widgetCalls = getWidgetCallbacks(handle.getHostName(), handle.getPort(), handle.getId());
      String error = new Error(widgetCalls).getError();
      if (error != null) {
        if (error.equals(Error.NO_ERROR)) {
          Callbacks cbacks = new Callbacks(widgetCalls);
          // Subscribe to each callback received by the widget
          for (int j=0; j < cbacks.numCallbacks(); j++) {
            Callback call = cbacks.getCallbackAt(j);
            ClientSideSubscriber css = new ClientSideSubscriber(this.getId(), this.getHostName (), this.getPort (), call.getName (), null, handle.getAttributes());
            println("Server in <addWidgetCallbacksSubscription> subscribe to:" + css);
            Error done = subscribeTo((Handler)this, handle.getId(), handle.getHostName (), handle.getPort(), css);
            // The unique id identifying the subscription is in css
            //call.setName(handle.getId()+"_"+call.getName()); // before
            call.setName(css.getSubscriptionId ());
            
            calls.addCallback(call);
          }
        }
      }
  
  }
  
  /**
   * This method allows to subscribe to a new widget
   *
   * @param handle The widget to subscribe to
   * @author Agathe
   */
  protected void addCallback (WidgetHandle handle){
    // Add that to widgets
    widgets.addWidgetHandle (handle);
    
    Callbacks calls = new Callbacks();
    
    // Get the callbacks and subscribe
    addWidgetCallbacksSubscription (handle, calls);
    
    // Add the calls to callbacks
    callbacks.addCallbacks (calls);
    
    //Update the discoverer
    if (discoverer != null){
      this.discovererUpdate ();
    }
      
  }
  
  /**
   * This abstract method set the callbacks for a server - those
   * that are specific to the server, and not contained in the widgets
   * it subscribes to.
   *
   * @return Callbacks containing callbacks specific to the server
   */
  protected abstract Callbacks setServerCallbacks();

  /**
   * This method implements the handle method in the Handler interface.  
   * It handles the subscription information supplied by widgets this
   * server has subscribed to.
   *
   * @param callback The name of the widget callback (on the subscriber side) triggered
   * @param data DataObject containing the data for the widget callback 
   * @return DataObject containing any directives to the widget that created the callback
   * @exception context.arch.InvalidMethodException if the callback specified isn't recognized	
   * @exception context.arch.MethodException if the callback specified can't be handled successfully
   */
  public DataObject handle(String callback, DataObject data) throws InvalidMethodException, MethodException {
    if (callbacks.hasCallback(callback)) {
      Attributes attsObj = new Attributes(data);
      if (attsObj != null) {
        sendToSubscribers(callback);
        storeAttributeNameValues(attsObj);
      }
    }
    else {
      throw new InvalidMethodException(Error.UNKNOWN_CALLBACK_ERROR);
    }
    return null;
  }
  
  /**
   * This method runs a query on a widget, asking for either it's latest
   * acquired data (QUERY) or asking for the widget to acquire and return
   * new data (UPDATE_AND_QUERY).  Currently, it deals with QUERY and
   * UPDATE_AND_QUERY in exactly the same way.
   *
   * @param query DataObject containing the query request
   * @param update Whether or not to acquire new data
   * @param error String containing the incoming error value
   * @return DataObject containing the reply to the query
   */
  protected DataObject queryWidget(DataObject query, boolean update, String error) {
    DataObject result = null;
    Vector v = new Vector();
    if (update) {
      result = new DataObject(UPDATE_AND_QUERY_REPLY, v);
    }
    else {
      result = new DataObject(QUERY_REPLY, v);
    }

    Attributes atts = new Attributes(query);
    Error err = new Error(error);
    if (err.getError() == null) {
      if (atts == null) {
        err.setError(Error.MISSING_PARAMETER_ERROR);
      }
      else if (!canHandle(atts)) {
        err.setError(Error.INVALID_ATTRIBUTE_ERROR);
      }
    }

    if (err.getError() == null) {
      Attributes subset = attributesCache.getSubset(atts);
      if (subset.numAttributes() == 0) {
        err.setError(Error.INVALID_DATA_ERROR);
      }
      else {
        v.addElement(subset.toDataObject());
        if (subset.numAttributes() >= atts.numAttributes()) {
          err.setError(Error.NO_ERROR);
        }
        else {
          err.setError(Error.INCOMPLETE_DATA_ERROR);
        }
      }
    }
    v.addElement(err.toDataObject());
    return result;
  }

  /**
   * This method stores the attribute name values, both in persistent storage
   * and in local storage.
   *
   * @param atts AttributeNameValues to store
   */
  public void storeAttributeNameValues(Attributes atts) {
    store(atts);
    
    AttributeNameValue time = atts.getAttributeNameValue(TIMESTAMP);
    long timestamp = 0;
    if (time != null){
      Long l = new Long ((String) time.getValue () );
      if (l != null)
        timestamp = l.longValue();
      else {
        System.out.println("ERROR: in <storeAttributeNameValues> timestamp null");
      }
    }

    for (int i=0; i<atts.numAttributes(); i++) {
      AttributeNameValue attNew = (AttributeNameValue) atts.getAttributeAt(i);
      String attName = attNew.getName();
      Object o = attributesTimes.get (attName);
      long storedTime = -1;
      if (o != null)
        storedTime = ((Long)o).longValue();
      else 
        System.out.println("ERROR: in <storeAttributeNameValues> storedTime null");
      if (storedTime != -1 && storedTime <= timestamp) {
        AttributeNameValue attOld = attributesCache.getAttributeNameValue(attName);
        attOld.setValue(attNew.getValue());
      }
    }
  }
  
  
  
  /**
   * This method overloads the widget method
   * It returns the server specific description
   *
   * @return DataObject The information common to all servers
   */
  public DataObject getWidgetDescription(){
    DataObject result;
    
    // Get the server non constant attributes
    DataObject doAtt_ = setServerAttributes().toDataObject();
    println("server all att = " + doAtt_.toString());
    Vector vAtt = new Vector();
    vAtt.addElement(doAtt_);
    DataObject doAtt = new DataObject(Discoverer.SERVER_NON_CONSTANT_ATTRIBUTES, vAtt);
    
    // Get the server constant attributes
    DataObject doCstAtt_ = setServerConstantAttributes().toDataObject();
    println("server att = " + doCstAtt_.toString());
    Vector vCstAtt = new Vector();
    vCstAtt.addElement(doCstAtt_);
    DataObject doCstAtt = new DataObject(Discoverer.SERVER_CONSTANT_ATTRIBUTES, vCstAtt);
    
    // Get the server callbacks
    DataObject doCallbacks_ = setServerCallbacks().toDataObject();
    Vector vCall = new Vector();
    vCall.addElement(doCallbacks_);
    DataObject doCallbacks = new DataObject(Discoverer.SERVER_CALLBACKS, vCall);
    
    // Get the server services
    DataObject doServices_ = setServerServices().toDataObject();
    Vector vSer = new Vector();
    vSer.addElement(doServices_);
    DataObject doServices = new DataObject(Discoverer.SERVER_SERVICES, vSer);
    
    Vector v = new Vector();
    v.addElement(doAtt);
    v.addElement(doCstAtt);
    v.addElement(doCallbacks);
    v.addElement(doServices);
    
    // Get getWidgetDescription
    DataObject doDescrip = getServerDescription();
      if (doDescrip != null) {
        Vector vDescrip = doDescrip.getChildren();
        Enumeration e = vDescrip.elements();
        DataObject temp;
        while (e.hasMoreElements()){
          temp = (DataObject) e.nextElement();
          v.addElement(temp);
        }
      }
    
    result = new DataObject(Discoverer.TEMP_DEST, v);
    
    return result;
  
  
  }
  
  /**
   * Returns the server description that should be overloaded
   *
   */
  public DataObject getServerDescription (){
    return null;
  }
  
}
