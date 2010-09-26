package context.arch.widget;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import context.arch.BaseObject;
import context.arch.comm.DataObject;
import context.arch.comm.RequestObject;
import context.arch.comm.clients.DiscovererClient;
import context.arch.comm.clients.IndependentCommunication;
import context.arch.comm.language.EncodeException;
import context.arch.comm.language.InvalidEncoderException;
import context.arch.discoverer.Discoverer;
import context.arch.discoverer.querySystem.AbstractQueryItem;
import context.arch.service.Service;
import context.arch.service.Services;
import context.arch.service.helper.FunctionDescription;
import context.arch.service.helper.FunctionDescriptions;
import context.arch.service.helper.ServiceDescription;
import context.arch.service.helper.ServiceInput;
import context.arch.storage.Attribute;
import context.arch.storage.AttributeFunction;
import context.arch.storage.AttributeFunctions;
import context.arch.storage.Attributes;
import context.arch.storage.InvalidStorageException;
import context.arch.storage.Retrieval;
import context.arch.storage.RetrievalResults;
import context.arch.storage.Storage;
import context.arch.storage.StorageObject;
import context.arch.subscriber.AbstractSubscriber;
import context.arch.subscriber.Callback;
import context.arch.subscriber.Callbacks;
import context.arch.subscriber.Subscriber;
import context.arch.subscriber.Subscribers;
import context.arch.util.Constants;
import context.arch.util.Error;

/**
 * This class is the basic context widget, with attributes and
 * methods that should apply to all context widgets.
 *
 * @see context.arch.BaseObject
 * @author Anind
 */
public abstract class Widget extends BaseObject {
  
  /**
   * Debug flag. Set to true to see debug messages.
   */
  public static boolean DEBUG = false;
  
  /**
   * Tag for the class file being used by the widget
   */
  public static final String CLASS = "class";
  
  /**
   * The tag for the type of this object
   */
  public static final String WIDGET_TYPE = "widget";
  
  /**
   * Dummy version number. Subclasses should override this value.
   */
  public String VERSION_NUMBER = "UNDEFINED";
  
  /**
   * Default port for widgets to use
   */
  public static final int DEFAULT_PORT = 5000;
  
  /** Tag for version number. */
  public static final String VERSION = "version";
  
  /** Tag for the timestamp of widget data */
  public static final String TIMESTAMP = "timestamp";
  
  /**
   * Tag to indicate the widget should return the latest stored data
   */
  public static final String QUERY = "query";
  
  /**
   * Tag to indicate the reply to a QUERY message
   */
  public static final String QUERY_REPLY = "queryReply";
  
  /**
   * Tag to indicate the widget should get the latest data from the generator and return them
   */
  public static final String UPDATE_AND_QUERY = "updateAndQuery";
  
  /**
   * Tag to indicate the reply to an UPDATE_AND_QUERY message
   */
  public static final String UPDATE_AND_QUERY_REPLY = "updateAndQueryReply";
  
  /**
   * Tag to indicate the widget should return its list of attributes
   */
  public static final String QUERY_ATTRIBUTES = "queryAttributes";
  
  /**
   * Tag to indicate the widget should return its list of attributes
   */
  public static final String QUERY_CONSTANT_ATTRIBUTES = "queryConstantAttributes";
  
  /**
   * Tag to indicate the reply to a QUERY_ATTRIBUTES message
   */
  public static final String QUERY_ATTRIBUTES_REPLY = "queryAttributesReply";
  
  /**
   * Tag to indicate the reply to a QUERY_CONSTANT_ATTRIBUTES message
   */
  public static final String QUERY_CONSTANT_ATTRIBUTES_REPLY = "queryConstantAttributesReply";
  
  /**
   * Tag to indicate the widget should return its list of callbacks
   */
  public static final String QUERY_CALLBACKS = "queryCallbacks";
  
  /**
   * Tag to indicate the reply to a QUERY_CALLBACKS message
   */
  public static final String QUERY_CALLBACKS_REPLY = "queryCallbacksReply";
  
  /**
   * Tag to indicate the widget should return its list of services
   */
  public static final String QUERY_SERVICES = "queryServices";
  
  /**
   * Tag to indicate the reply to a QUERY_SERVICES message
   */
  public static final String QUERY_SERVICES_REPLY = "queryServicesReply";
  
  /**
   * Tag to indicate the widget should return its version number
   */
  public static final String QUERY_VERSION = "queryVersion";
  
  /**
   * Tag to indicate the reply to a QUERY_VERSION message
   */
  public static final String QUERY_VERSION_REPLY = "queryVersionReply";
  
  /**
   * Tag to indicate the widget should accept the given data
   */
  public static final String PUT_DATA = "putData";
  
  /**
   * Tag to indicate the reply to a PUT_DATA message
   */
  public static final String PUT_DATA_REPLY = "putDataReply";
  
  /**
   * Tag to indicate an update is being sent
   */
  public static final String UPDATE = "update";
  
  /**
   * Constant for the widget spacer
   */
  public static final String SPACER = Constants.SPACER;
  
  // The list of non constant attributes
  protected Attributes attributes;
  // The list of constant attributes
  protected Attributes constantAttributes;
  // The list of non constant attributes types
  protected Hashtable attributeTypes;
  // The list of constant attributes types
  protected Hashtable constantAttributesTypes;
  // The list of callbacks
  protected Callbacks callbacks;
  // The list of services
  protected Services services;
  protected long CurrentOffset = 0;
  
  /**Object to handle subscriptions to context data
   * @see context.arch.subscriber.Subscribers
   * @see context.arch.subscriber.Subscriber
   */
  public Subscribers subscribers;
  
  /**Object to keep track of storage
   * @see context.arch.storage.StorageObject
   */
  public StorageObject storage;
  
  /**
   * Constructor that sets up internal variables for maintaining
   * the list of widget attributes, callbacks, and services and setting up
   * the BaseObject info.
   *
   * TO COMPLETE : for storage use
   *
   * @param clientClass Class to use for client communications
   * @param serverClass Class to use for server communications
   * @param serverPort Port to use for server communications
   * @param encoderClass Class to use for communications encoding
   * @param decoderClass Class to use for communications decoding
   * @param storageClass Class to use for storage
   * @param id String to use for widget id and persistent storage
   * @see context.arch.storage.StorageObject
   */
  public Widget(String clientClass, String serverClass, int serverPort, String encoderClass,
  String decoderClass, String storageClass, String id) {
    super(clientClass,serverClass,serverPort,encoderClass,decoderClass);
    init(id);
  }
  
  /**
   * Constructor that sets up internal variables for maintaining
   * the list of widget attributes, callbacks, and services and setting up
   * the BaseObject info. This version takes a boolean to indicate whether the
   * default storage class should be used or whether no storage should be
   * provided.
   *
   * @param clientClass Class to use for client communications
   * @param serverClass Class to use for server communications
   * @param serverPort Port to use for server communications
   * @param encoderClass Class to use for communications encoding
   * @param decoderClass Class to use for communications decoding
   * @param storageFlag Flag to determine whether storage should be used or not
   * @param id String to use for widget id and persistent storage
   * @see context.arch.storage.StorageObject
   */
  public Widget(String clientClass, String serverClass, int serverPort, String encoderClass,
  String decoderClass, boolean storageFlag, String id) {
    super(clientClass,serverClass,serverPort,encoderClass,decoderClass);
    init(id);
  }
  
  private void init(String id) {
    setAttributes(initAttributes());
    setConstantAttributes(initConstantAttributes());
    setCallbacks(initCallbacks());
    setServices(initServices());
    setId(id);
    setSubscribers();
    getNewOffset();
  }
  
  /**
   * Constructor that sets up internal variables for maintaining
   * the list of widget attributes, callbacks and services.  It takes a port
   * number as a parameter to indicate which port to listen for
   * messages/connections, the id to use for the widget, and a flag to indicate
   * whether storage functionality should be turned on or off.
   *
   * @param port Port to listen to for incoming messages
   * @param id Widget id
   * @param storageFlag Boolean flag to indicate whether storage should be turned on
   */
  public Widget(int port, String id, boolean storageFlag) {
    this(null,null,port,null,null,storageFlag,id);
  }
  
  /**
   * Constructor that sets up internal variables for maintaining
   * the list of widget attributes, callbacks and services.  It takes a port
   * number as a parameter to indicate which port to listen for
   * messages/connections.
   *
   * @param port Port to listen to for incoming messages
   * @param id Widget id
   */
  public Widget(int port, String id) {
    this(null,null,port,null,null,null,id);
  }
  
  /**
   * Constructor that sets up internal variables for maintaining
   * the list of widget attributes, callbacks and services.  It takes
   * the id to use for the widget, and a flag to indicate
   * whether storage functionality should be turned on or off.
   *
   * @param id Widget id
   * @param storageFlag Boolean flag to indicate whether storage should be turned on
   */
  public Widget(String id, boolean storageFlag) {
    this(null,null,-1,null,null,storageFlag,id);
  }
  
  /**
   * Constructor that sets up internal variables for maintaining
   * the list of widget attributes, callbacks and services.  It takes the
   * widget id as a parameter
   *
   * @param id ID of the widget
   */
  public Widget(String id) {
    this(null,null,-1,null,null,null,id);
  }
  
  /**
   * Returns the type of the object
   * This method should be overridden
   *
   * @return String
   */
  public String getType(){
    return Widget.WIDGET_TYPE;
  }
  
  /**
   * Sets the attributes for the widget
   */
  protected abstract Attributes initAttributes();
  
  
  /**
   * Sets the callbacks for the widget
   */
  protected abstract Callbacks initCallbacks();
  
  /**
   * Sets the constant attributes for the widget
   */
  protected abstract Attributes initConstantAttributes();
  
  /**
   * Sets the services for the widget
   */
  protected abstract Services initServices();
  
  protected void setAttributes(Attributes atts) {
    attributes = atts;
    if (attributes != null) {
      attributeTypes = attributes.toTypesHashtable();
    } else {
      attributeTypes = null;
    }
  }
  
  protected void setConstantAttributes(Attributes atts) {
    constantAttributes = atts;
    if (constantAttributes != null) {
      constantAttributesTypes = constantAttributes.toTypesHashtable();
    }
  }
  
  protected void setCallbacks(Callbacks calls) {
    callbacks = calls;
  }
  
  protected void setServices(Services svcs) {
    services = svcs;
  }
  
  /**
   * Returns the attribute value with the given name
   *
   * @param name Name of the attribute to get
   */
  protected String getAttributeType(String name) {
    return (String)attributeTypes.get(name);
  }
  
  /**
   * Sets an attribute
   *
   * @param name Name of the attribute to set
   * @param type Type of the attribute
   */
  protected void setAttribute(String name, String type) {
    attributeTypes.put(name, type);
    attributes.addAttribute(name,type);
  }
  
  /**
   * Checks if the given attribute is an attribute of this widget
   *
   * @param name Name of the attribute to check
   */
  protected boolean isAttribute(String name) {
    return attributeTypes.containsKey(name);
  }
  
  /**
   * Checks if the given callback is a callback of this widget
   *
   * @param name Name of the callback to check
   * @return boolean True if name is a known callback name
   */
  protected boolean isCallback(String name) {
    // return callbacks.contains(name); -- this was a bug :)  DS, 10/30/1998
    if (callbacks.getCallback(name) == null) {
      return false;
    }
    return true;
  }
  
  /**
   * This is an empty method that should be overridden by objects
   * that subclass from this class.  It is intended to be called
   * by generator objects when new data has arrived for the purpose
   * of notifying the widget.
   *
   * @param event Name of the event occurring
   * @param data Object containing the relevant data
   */
  public void notify(String event, Object data) {
  }
  
  /**
   * This method is called when a remote component sends an UPDATE_AND_QUERY message.
   * It calls the widget's queryGenerator method to get the latest generator info,
   * and then stores it.
   */
  protected void updateWidgetInformation() {
    Attributes atts = queryGenerator();
    if (atts != null) {
      if (storage != null) {
        storage.store(atts);
      }
    }
  }
  
  /**
   * This abstract method is called when the widget wants to get the latest generator
   * info.
   *
   * @return AttributeNameValues containing the latest generator information
   */
  protected abstract Attributes queryGenerator();
  
  /**
   * This is an empty method that should be overridden by objects
   * that subclass from this class.  It is called when another component
   * tries to run a method on the widget, but it's not a query.
   *
   * @param data DataObject containing the data for the method
   * @param error String containing the incoming error value
   * @return DataObject containing the method results
   */
  protected DataObject runWidgetMethod(DataObject data, String error) {
    String name = data.getName();
    Error err = new Error(error);
    if (err.getError() == null) {
      err.setError(Error.UNKNOWN_METHOD_ERROR);
    }
    Vector v = new Vector();
    v.addElement(err.toDataObject());
    return new DataObject(data.getName(),v);
  }
  
  /**
   * This method is meant to handle any internal methods that the baseObject doesn't
   * handle.  In particular, this method handles the common details for query requests,
   * update and query requests, and version requests that each widget should provide.
   * If the method is not one of these queries, then it calls runWidgetMethod which each widget
   * should provide.
   *
   * @param data DataObject containing the method to run and parameters
   * @return DataObject containing the results of running the method
   * @see #QUERY
   * @see #QUERY_VERSION
   * @see #UPDATE_AND_QUERY
   */
  public DataObject runUserMethod(DataObject data) {
    debugprintln(DEBUG, "\nWidget runUserMethod " + data.getName ());
    DataObject widget = data.getDataObject(ID);
    String error = null;
    
    if (widget == null) {
      error = Error.INVALID_ID_ERROR;
    }
    else {
      String queryId = (String)(widget.getValue().firstElement());
      if (!queryId.equals(getId())) {
        error = Error.INVALID_ID_ERROR;
      }
    }
    
    String methodType = data.getName();
    if (methodType.equals(UPDATE_AND_QUERY)) {
      return queryWidget(data,true,error);
    }
    else if (methodType.equals(QUERY)) {
      return queryWidget(data,false,error);
    }
    else if (methodType.equals(QUERY_ATTRIBUTES)) {
      return queryAttributes(data,error);
    }
    else if (methodType.equals(QUERY_CONSTANT_ATTRIBUTES)) {
      return queryConstantAttributes(data,error);
    }
    else if (methodType.equals(QUERY_CALLBACKS)) {
      return queryCallbacks(data,error);
    }
    else if (methodType.equals(QUERY_SERVICES)) {
      return queryServices(data,error);
    }
    else if (methodType.equals(Subscriber.ADD_SUBSCRIBER)) {
      return addSubscriber(data,error);
    }
    else if (methodType.equals(Subscriber.REMOVE_SUBSCRIBER)) {
      return removeSubscriber(data,error);
    }
    else if (methodType.equals(StorageObject.RETRIEVE_DATA)) {
      return retrieveData(data,error);
    }
    else if (methodType.equals(PUT_DATA)) {
      return putData(data,error);
    }
    else if (methodType.equals(Service.SERVICE_REQUEST)) {
      return executeService(data,error);
    }
    else {
      return runWidgetMethod(data,error);
    }
  }
  
  /**
   * This method puts context data in a widget.  It is expected
   * that widgets will get data from a generator.  But for some
   * widgets, the generator will not use the context toolkit directly,
   * but may use a web CGI script, for example.  For this case, the
   * widget provides this method to collect the data and makes it available
   * to subscribers and for retrieval.
   *
   * @param data DataObject containing the context data to write
   * @param error String containing the incoming error value
   * @return DataObject containing the results of writing the data
   */
  protected DataObject putData(DataObject data, String error) {
    Vector v = new Vector();
    Error err = new Error(error);
    if (err.getError() == null) {
      DataObject callbackObj = data.getDataObject(Subscriber.CALLBACK_NAME);
      String callback = null;
      if (callbackObj != null) {
        callback = (String)callbackObj.getValue().firstElement();
      }
      Attributes atts = new Attributes(data);
      if ((atts == null) || (atts.numAttributes() == 0)) {
        err.setError(Error.INVALID_DATA_ERROR);
      }
      else if (callback != null) {
        Callback call = callbacks.getCallback(callback);
        if (call == null) {
          err.setError(Error.INVALID_CALLBACK_ERROR);
        }
        else {
          Attributes callAtts = call.getAttributes();
          boolean ok = true;
          if (callAtts.numAttributes() == atts.numAttributes()) {
            for (int i=0; i<callAtts.numAttributes(); i++) {
              Attribute callAtt = callAtts.getAttributeAt(i);
              if (atts.getAttributeNameValue(callAtt.getName()) == null) {
                ok = false;
              }
            }
          }
          if (!ok) {
            err.setError(Error.INVALID_ATTRIBUTE_ERROR);
          }
          else {
            setNonConstantAttributes(atts);
            sendToSubscribers(callback);
            store(atts);
            err.setError(Error.NO_ERROR);
          }
        }
      }
      else if (!canHandle(atts)) {
        err.setError(Error.INVALID_ATTRIBUTE_ERROR);
      }
      else {
        store(atts);
        err.setError(Error.NO_ERROR);
      }
    }
    v.addElement(err.toDataObject());
    return new DataObject(PUT_DATA_REPLY, v);
  }
  
  /**
   * This method queries the callbacks of a widget.
   *
   * @param query DataObject containing the query
   * @param error String containing the incoming error value
   * @return DataObject containing the results of the query
   */
  protected DataObject queryCallbacks(DataObject query, String error) {
    Vector v = new Vector();
    Error err = new Error(error);
    if (err.getError() == null) {
      if (callbacks == null) {
        err.setError(Error.EMPTY_RESULT_ERROR);
      }
      else {
        v.addElement(callbacks.toDataObject());
        err.setError(Error.NO_ERROR);
      }
    }
    v.addElement(err.toDataObject());
    return new DataObject(QUERY_CALLBACKS_REPLY, v);
  }
  
  /**
   * This method queries the attributes of a widget.
   *
   * @param query DataObject containing the query
   * @param error String containing the incoming error value
   * @return DataObject containing the results of the query
   */
  protected DataObject queryAttributes(DataObject query, String error) {
    Vector v = new Vector();
    Error err = new Error(error);
    if (err.getError() == null) {
      if (attributes == null) {
        err.setError(Error.EMPTY_RESULT_ERROR);
      }
      else {
        err.setError(Error.NO_ERROR);
        v.addElement(attributes.toDataObject());
      }
    }
    v.addElement(err.toDataObject());
    return new DataObject(QUERY_ATTRIBUTES_REPLY, v);
  }
  
  /**
   * This method queries the constant attributes of a widget.
   *
   * @param query DataObject containing the query
   * @param error String containing the incoming error value
   * @return DataObject containing the results of the query
   *
   * @author Agathe
   */
  protected DataObject queryConstantAttributes(DataObject query, String error) {
    Vector v = new Vector();
    Error err = new Error(error);
    if (err.getError() == null) {
      if (constantAttributes == null) {
        err.setError(Error.EMPTY_RESULT_ERROR);
      }
      else {
        err.setError(Error.NO_ERROR);
        v.addElement(constantAttributes.toDataObject());
      }
    }
    v.addElement(err.toDataObject());
    return new DataObject(QUERY_CONSTANT_ATTRIBUTES_REPLY, v);
  }
  /**
   * This method queries the services of a widget.
   *
   * @param query DataObject containing the query
   * @param error String containing the incoming error value
   * @return DataObject containing the results of the query
   */
  protected DataObject queryServices(DataObject query, String error) {
    Vector v = new Vector();
    Error err = new Error(error);
    if (err.getError() == null) {
      if (services == null) {
        err.setError(Error.EMPTY_RESULT_ERROR);
      }
      else {
        err.setError(Error.NO_ERROR);
        v.addElement(services.toDataObject());
      }
    }
    v.addElement(err.toDataObject());
    return new DataObject(QUERY_SERVICES_REPLY, v);
  }
  
  /**
   * This method runs a query on a widget, asking for either it's latest
   * acquired data (QUERY) or asking for the widget to acquire and return
   * new data (UPDATE_AND_QUERY)
   *
   * @param query DataObject containing the query request
   * @param update Whether or not to acquire new data
   * @param error String containing the incoming error value
   * @return DataObject containing the reply to the query
   */
  protected DataObject queryWidget(DataObject query, boolean update, String error) {
    debugprintln(DEBUG, "Widget queryWidget query:"+query.toString() + "\nerror:"+ error);
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
    if (err.getError() != null) {
      v.addElement(err.toDataObject());
      return result;
    }
    
    if (update) {
      updateWidgetInformation();
    }
    if (storage != null) {
      storage.flushStorage();
    }
    Attributes values = storage.retrieveLastAttributes();
    
    if (values != null) {
      Attributes subset = (Attributes) values.getSubset(atts);
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
    else {
      err.setError(Error.INVALID_DATA_ERROR);
    }
    v.addElement(err.toDataObject());
    debugprintln (DEBUG, "Widget queryWidget return:"+result.toString());
    return result;
  }
  
  /**
   * This method checks the list of attributes to ensure
   * that the widget contains these attributes.
   *
   * @param attributes Attributes object containing attributes to check
   * @return whether the list of attributes is valid
   */
  protected boolean canHandle(Attributes atts) {
    if (atts.numAttributes() > 0) {
      Attribute att = atts.getAttributeAt(0);
      if (!(att.getName().equals(Attributes.ALL))) {
        for (int i=0; i<atts.numAttributes(); i++) {
          att = atts.getAttributeAt(i);
          if (!isAttribute(att.getName())) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  /**
   * This method checks the list of attributes and conditions to ensure
   * that the widget contains these attributes.
   *
   * @param atts List of attributes to check
   * @param conds List of Conditions to check
   * @return whether the list of attributes and conditions is valid
   */
//  protected boolean canHandle(Attributes atts, Conditions conds) {
//    if (atts.numAttributes() > 0) {
//      Attribute att = atts.getAttributeAt(0);
//      if (!(att.getName().equals(Attributes.ALL))) {
//        for (int i=0; i<atts.numAttributes(); i++) {
//          att = atts.getAttributeAt(i);
//          if (!isAttribute(att.getName())) {
//            return false;
//          }
//        }
//      }
//    }
//    for (int i=0; i<conds.numConditions(); i++) {
//      if (!isAttribute(conds.getConditionAt(i).getAttribute())) {
//        return false;
//      }
//    }
//    return true;
//  }
  
  /**
   * This method checks the list of attributes and conditions to ensure
   * that the widget contains these attributes.
   *
   * @param atts List of attributes to check
   * @param conds List of Conditions to check
   * @return whether the list of attributes and conditions is valid
   */
  protected boolean canHandle(AttributeFunctions atts) {
    if (atts.numAttributeFunctions() > 0) {
      AttributeFunction att = atts.getAttributeFunctionAt(0);
      if (!(att.getName().equals(Attributes.ALL))) {
        for (int i=0; i<atts.numAttributeFunctions(); i++) {
          att = atts.getAttributeFunctionAt(i);
          if (!isAttribute(att.getName())) {
            return false;
          }
        }
      }
    }
//    for (int i=0; i<conds.numConditions(); i++) {
//      if (!isAttribute(conds.getConditionAt(i).getAttribute())) {
//        return false;
//      }
//    }
    return true;
  }
  
  /**
   * This method checks the list of attributes (in an AttributeNameValues object)
   * to ensure that the widget contains these attributes.
   *
   * @param atts List of attributes to check
   * @return whether the list of attributes and conditions is valid
   */
  /*
  protected boolean canHandle(Attributes atts) {
    if (atts.numAttributeNameValues() > 0) {
      AttributeNameValue att = atts.getAttributeNameValueAt(0);
      if (!(att.getName().equals(Attributes.ALL))) {
        for (int i=0; i<atts.numAttributeNameValues(); i++) {
          att = atts.getAttributeNameValueAt(i);
          if (!isAttribute(att.getName())) {
            return false;
          }
        }
      }
    }
    return true;
  }
  */
  
  /**
   * This method attempts to execute a widget service.
   *
   * @param request DataObject containing the service request
   * @param error String containing the incoming error value
   * @return DataObject containing the results of the service request
   */
  protected DataObject executeService(DataObject request, String error) {
    Vector v = new Vector();
    Error err = new Error(error);
    DataObject result;
    if (err.getError() == null) {
      ServiceInput si = new ServiceInput(request);
      if (si == null) {
        err.setError(Error.MISSING_PARAMETER_ERROR);
      }
      else if (!services.hasService(si.getServiceName())) {
        err.setError(Error.UNKNOWN_SERVICE_ERROR);
      }
      else {
        Service service = services.getService(si.getServiceName());
        FunctionDescriptions fds = service.getFunctionDescriptions();
        if (!fds.hasFunctionDescription(si.getFunctionName())) {
          err.setError(Error.UNKNOWN_FUNCTION_ERROR);
        }
        else {
          String timing = (String)request.getDataObject(FunctionDescription.FUNCTION_TIMING).getValue().firstElement();
          FunctionDescription fd = fds.getFunctionDescription(si.getFunctionName());
          if (!fd.getTiming().equals(timing)) {
            err.setError(Error.INVALID_TIMING_ERROR);
          }
          else {
            result = service.execute(si);
            err.setError(Error.NO_ERROR);
            v.addElement(result);
          }
        }
      }
    }
    v.addElement(err.toDataObject());
    return new DataObject(Service.SERVICE_REQUEST_REPLY,v);
  }
  
  /**
   * sets the current state of some set of NonConstantAttributes in the widget.
   * If subscribers or storage is enabled, we send the attributes out to
   * subscribers.
   * 
   * Note: does not enforce strong type checking. If an arbitrary attribute is
   * set, this widget will 'acquire' that attribute. It will probably not be
   * sent as a callback, however.
   * 
   * @param anv
   */
  public void setNonConstantAttributes(Attributes anv) {
    attributes.addAttributes(anv);
  }
  
  /**
   * This method should be called to send data to subscribers when a context
   * widget's callback is triggered.  It sends data only to those subscribers
   * that have subscribed to the specified callback.
   *
   * @author Agathe, to use independentCommunication
   *
   *
   * @param callbackTag Context widget callback that was triggered
   * @param atts AttributeNameValues to send to subscribers
   * @param data DataObject version of atts
   * @see BaseObject#userRequest(DataObject, String, String, int)
   * @see context.arch.subscriber.Subscribers
   */
  protected void sendToSubscribers(String callback) {
    debugprintln(DEBUG, "\n\nWidget <sendToSubscribers> callback=" + callback);
    Callback call = callbacks.getCallback(callback);
    if (call == null) return;
    // For each subscriber, see if the subscriber is interested
    debugprintln(DEBUG, "widget <sendToSubs> nb subs? " + subscribers.numSubscribers ());
    for (int i=0; i < subscribers.numSubscribers(); i++) {
      Subscriber sub = (Subscriber) subscribers.getSubscriberAt(i);
      DataObject result = null;      // callback reply
      // Check if the subscriber wants this callback
      debugprintln(DEBUG, "Widget <sendToSubs> test callback="+callback + " ?? equal to sub call=" + sub.getSubscriptionCallback ());
      if (callback.equals(sub.getSubscriptionCallback())) {
        // Checks if the subscriber has specified conditions
        if (dataValid(call, sub.getCondition())) {
          debugprintln(DEBUG, "Widget <sendToSubscribers> datavalid TRUE");
          Attributes callAtts = call.getAttributes();
          Attributes subAtts = attributes.getSubset(callAtts).getSubset(sub.getAttributes());
          Attributes constSubAtts = constantAttributes.getSubset(callAtts).getSubset(sub.getAttributes());
          //only process if we have attriute to return
          if (subAtts.numAttributes() == 0 && constSubAtts.numAttributes() == 0) {
            return;
          }
          DataObject subid = new DataObject(Subscriber.SUBSCRIBER_ID, sub.getSubscriptionId ());
          Vector v = new Vector();
          v.addElement(subid);
          DataObject compDescription = buildCallbackComponentDescription(subAtts,constSubAtts);
          v.addElement(compDescription);
          DataObject send = new DataObject(Subscriber.SUBSCRIPTION_CALLBACK, v);
          String host = sub.getSubscriberHostName();
          int port = new Integer(sub.getSubscriberPort()).intValue();
          // Agathe : change to use independentUserRequest
          try {
            result = null;
            // Create the independent comm object
            IndependentCommunication ic = new IndependentCommunication (new RequestObject(send, Subscriber.SUBSCRIPTION_CALLBACK, host, port));
            // Store the sub object to remove it if it does not exist anymore
            ic.setObjectToStore (sub);
            // Store some reference for this communication
            ic.setSenderClassId (Widget.WIDGET_TYPE+Subscriber.SUBSCRIPTION_CALLBACK);
            // Send the notification
            independentUserRequest (ic);
            sub.resetErrors();
          } catch (EncodeException ee) {
            System.out.println("Widget sendToSubscribers EncodeException: "+ee);
          } catch (InvalidEncoderException iee) {
            System.out.println("Widget sendToSubscribers InvalidEncoderException: "+iee);
          }
          // we pass the result on for processing
          // TBD: pass it on only if it's not an error message?
          try {
            processCallbackReply (result, sub);
          } catch (Exception e) {
            System.out.println ("Widget sendToSubscribers Exception during processCallbackReply: "+e);
          }
        }
        else
          debugprintln(DEBUG, "Widget <sendToSubscribers> datavalid FALSE");
      }
    }
  }
  
  /**
   * constructs an abbreviated ComponentDescription containing only the necessary information
   * for the callback.
   * 
   */
  private DataObject buildCallbackComponentDescription(Attributes nonConstantAtts, Attributes constantAtts) {
    Vector cdv = new Vector();
    

    cdv.addElement(new DataObject(Discoverer.REGISTERER_ID,getId()));

    Vector v = new Vector();
    v.addElement(constantAtts.toDataObject());
    cdv.addElement(new DataObject(Discoverer.CONSTANT_ATTRIBUTE_NAME_VALUES, v));
    
    v = new Vector();
    v.addElement(nonConstantAtts.toDataObject());
    cdv.addElement(new DataObject(Discoverer.NON_CONSTANT_ATTRIBUTE_NAME_VALUES, v));

    return new DataObject(Discoverer.REGISTERER, cdv);
  }
  
  /**
   * This private method checks that the given data falls within the given conditions.
   *
   * @param atts AttributeNameValues containing data to validate
   * @param conditions Conditions to validate against
   * @return whether the data falls within the given conditions
   */
  private boolean dataValid(Callback callback, AbstractQueryItem condition) {
    return (condition == null) ? true : condition.process(getComponentDescription());
  }
    
  /**
   * This method should be overriden to process the results of subscription callbacks.
   *
   * @param result DataObject containing the result
   * @param sub Subscriber that returned this reply
   */
  protected void processCallbackReply (DataObject result, Subscriber sub) {
  }
  
  /**
   * This method adds a subscriber to this object.  It calls
   * Subscribers.addSubscriber() if it can add the subscriber.  It returns a
   * DataObject containing the reply information, including any error information.
   *
   * Agathe: change the Subscriber calls to use AbstractSubscriber
   *
   * @param sub DataObject containing the subscription information
   * @param error String containing the incoming error value
   * @return DataObject with the reply to the subscription request
   * @see context.arch.subscriber.Subscribers#addSubscriber(String,String,int,String,String,Conditions,Attributes)
   */
  public DataObject addSubscriber(DataObject sub, String error) {
    Vector v = new Vector();
    Error err = new Error(error);
    debugprintln(DEBUG, "Widget <addSubscriber> " + sub);
    
    if (err.getError() == null) {
      Subscriber subscriber = (Subscriber) AbstractSubscriber.dataObjectToAbstractSubscriber (sub);
      
      debugprintln(DEBUG, "Widget <addSubscriber> has created sub=" + subscriber);
      debugprintln(DEBUG, "\nWidget <addSubscriber> Subscription callback=" + subscriber.getSubscriptionCallback ());
      //debugprintln("Widget <addSubscriber> Callbacks =" + this.callbacks);
        
      if (subscriber == null) {
        debugprintln(DEBUG, "Widget <addSubscriber> sub null");
        err.setError(Error.MISSING_PARAMETER_ERROR);
      }
      // Test if this widget may handle the specified attributes and conditions
      //TODO: add this validation code back in after query system updated --alann
//      else if (!canHandle(subscriber.getAttributes(),subscriber.getCondition()) {
//        debugprintln(DEBUG, "Widget <addSubscriber> cannot handle att");
//        err.setError(Error.INVALID_ATTRIBUTE_ERROR);
//      }
      // Test if this widget may handle the specified callback
      else if (!isCallback(subscriber.getSubscriptionCallback ())) {
        debugprintln(DEBUG, "Widget <addSubscriber> doesn't know the callback");
        err.setError(Error.INVALID_CALLBACK_ERROR);
      }
      // Add the subscriber
      else {
        debugprintln(DEBUG, "Widget <addSubscriber> has added it");
        
        subscribers.addSubscriber(subscriber);
        debugprintln(DEBUG, "Widget <addSubscriber> The sub is now " + subscriber);
        
        v.addElement(new DataObject(Subscriber.SUBSCRIBER_ID, subscriber.getSubscriptionId ()));
        err.setError(Error.NO_ERROR);
      }
    }
    
    // Send an update to the discoverer
    if (discoverer != null)
      discovererUpdate ();
    
    v.addElement(err.toDataObject());
    debugprintln(DEBUG, "Widget <addSubscriber> data to send back " + v);
    return new DataObject(Subscriber.SUBSCRIPTION_REPLY, v);
  }
  
  /**
   * This method removes a subscriber to this object.  It calls
   * Subscribers.removeSubscriber() if it can remove the subscriber.  It returns a
   * DataObject containing the reply information, including any error information.
   *
   * @param sub DataObject containing the subscription information
   * @param error String containing the incoming error value
   * @return DataObject with the reply to the subscription request
   * @see context.arch.subscriber.Subscribers#removeSubscriber(String, String, String, String, String)
   */
  public DataObject removeSubscriber(DataObject sub, String error) {
    Vector v = new Vector();
    Error err = new Error(error);
    if (err.getError() == null) {
      DataObject dobj = sub.getDataObject (AbstractSubscriber.SUBSCRIBER_ID);
      Vector vec;
      if (dobj != null && !(vec = dobj.getValue ()).isEmpty()){
        String subId;
        subId = (String) vec.firstElement ();
        boolean done = subscribers.removeSubscriber(subId);
        if (!done) {
          err.setError(Error.UNKNOWN_SUBSCRIBER_ERROR);
        }
        else {
          v.addElement(new DataObject(Subscriber.SUBSCRIBER_ID, subId));
          err.setError(Error.NO_ERROR);
        }
      }
    }
    
    // Send an update to the discoverer - start a thread for that
    DiscovererClient discoClient = new DiscovererClient(this,Discoverer.DISCOVERER_UPDATE,
    getSubscribersDescription(), Discoverer.UPDATE_REPLACE_TYPE);
    discoClient.start();
    
    v.addElement(err.toDataObject());
    return new DataObject(Subscriber.SUBSCRIPTION_REPLY, v);
  }
  
  /**
   * This method retrieves data from the widget's storage.  It returns a
   * DataObject containing the retrieved data information, including any error information.
   *
   * @param data DataObject containing the subscription information
   * @param error String containing the incoming error value
   * @return DataObject with the reply to the subscription request
   */
  protected DataObject retrieveData(DataObject data, String error) {
    debugprintln (DEBUG, "Widget retrieveData data :"+data + "\nerror:" + error);
    Vector v = new Vector();
    Error err = new Error(error);
    if (err.getError() == null) {
      Retrieval retrieval = new Retrieval(data);
      if (!canHandle(retrieval.getAttributeFunctions())) {
        err.setError(Error.INVALID_ATTRIBUTE_ERROR);
      }
      else {
        if (storage == null) {
          err.setError(Error.EMPTY_RESULT_ERROR);
        }
        else {
          RetrievalResults results = storage.retrieveAttributes(retrieval);
          if (results == null) {
            err.setError(Error.INVALID_REQUEST_ERROR);
          }
          else if (results.size() == 0) {
            err.setError(Error.EMPTY_RESULT_ERROR);
          }
          else {
            err.setError(Error.NO_ERROR);
          }
          if (results != null) {
            v.addElement(results.toDataObject());
          }
        }
      }
    }
    v.addElement(err.toDataObject());
    debugprintln(DEBUG, "Widget retrieve data return:"+v.toString());
    return new DataObject(StorageObject.RETRIEVE_DATA_REPLY, v);
  }
  
  /**
   * This stub method stores the data in the given DataObject
   *
   * @param data Data to store
   * @see context.arch.storage.StorageObject#store(DataObject)
   */
  protected void store(DataObject data) {
    debugprintln(DEBUG, "Widget <store (DO)>");
    if (storage != null) {
      storage.store(data);
    }
  }
  
  /**
   * This stub method stores the data in the given AttributeNameValues object
   *
   * @param data Data to store
   * @see context.arch.storage.StorageObject#store(AttributeNameValues)
   */
  protected void store(Attributes data) {
    debugprintln(DEBUG, "Widget <store(ANVS)>");
    if (storage != null) {
      storage.store(data);
    }
  }
  
  /**
   * This method creates a thread that retrieves a global time clock and determines
   * the offset between the local clock and the global clock. It checks this
   *
   * @return the offset between the global and local clocks
   * @see context.arch.widget.OffsetThread
   */
  protected void getNewOffset() {
    OffsetThread offset = new OffsetThread();
    CurrentOffset = offset.getCurrentOffset();
    offset = new OffsetThread(120);
  }
  
  /**
   * This method retrieves the offset between the local clock and a global clock
   * with no delay.
   *
   * @return the offset between the global and local clocks
   * @see context.arch.widget.OffsetThread
   */
  protected long getNewOffsetNoDelay() {
    OffsetThread offset = new OffsetThread();
    return offset.getCurrentOffset();
  }
  
  /**
   * This method returns the current time to use as a timestamp
   *
   * @return the current time, corrected using a global clock offset
   */
  protected Long getCurrentTime() {
    long temp = new Date().getTime();
    return new Long(temp + CurrentOffset);
  }
  
  /**
   * This method builds the widget description which contains the constant and
   * non constant attributes, the callbacks, the services, the subscribers
   * This method overloads the BaseObject's getUserDescription method
   *
   * @return DataObject The description of the widget
   * @see #getWidgetDescription()
   * @author Agathe
   */
  public DataObject getUserDescription(){
    // TO complete !!!!!
    DataObject result;
    
    // Get the non constant attributes
    DataObject doAtt_ = attributes.toDataObject();
    Vector vAtt = new Vector();
    vAtt.addElement(doAtt_);
    DataObject doAtt = new DataObject(Discoverer.NON_CONSTANT_ATTRIBUTE_NAME_VALUES, vAtt);
    
    // Get the constant attributes
    DataObject doCstAtt_ = constantAttributes.toDataObject();
    Vector vCstAtt = new Vector();
    vCstAtt.addElement(doCstAtt_);
    DataObject doCstAtt = new DataObject(Discoverer.CONSTANT_ATTRIBUTE_NAME_VALUES, vCstAtt);
    
    // Get the callbacks
    DataObject doCallbacks_ = callbacks.toDataObject();
    Vector vCall_ = doCallbacks_.getChildren();
    Vector vCall = new Vector();
    if ( ! vCall_.isEmpty()) {
      Enumeration eCall_ = vCall_.elements();
      DataObject element;
      while ( eCall_.hasMoreElements()){
        element = (DataObject) eCall_.nextElement();
        vCall.addElement(element.getDataObject(Callback.CALLBACK_NAME));
      }
    }
    DataObject doCallbacks = new DataObject(Discoverer.WIDGET_CALLBACKS, vCall);
    
    // Get the services
    DataObject doServices_ = services.toDataObject();
    Vector vSer_ = doServices_.getChildren();
    Vector vSer = new Vector();
    if ( ! vSer_.isEmpty()) {
      Enumeration eSer_ = vSer_.elements();
      DataObject element;
      while ( eSer_.hasMoreElements()){
        element = (DataObject) eSer_.nextElement();
        vSer.addElement(element.getDataObject(ServiceDescription.SERVICE_NAME));
      }
    }
    DataObject doServices = new DataObject(Discoverer.WIDGET_SERVICES, vSer);
    
    //Get the subscribers
    DataObject doSubs = getSubscribersDescription();
    
    Vector v = new Vector();
    v.addElement(doAtt);
    v.addElement(doCstAtt);
    v.addElement(doCallbacks);
    v.addElement(doServices);
    v.addElement(doSubs);
    
    // Get getWidgetDescription
    DataObject doDescrip = getWidgetDescription();
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
   * This method returns a DataObject containig the list of subscribers
   *
   * @return DataObject The list of subscribers
   * @author Agathe
   */
  public DataObject getSubscribersDescription(){
    // Get the subscribers
    Vector subs = new Vector();
    Enumeration listOfSubs = subscribers.getSubscribers ();
    AbstractSubscriber sub;
    while (listOfSubs.hasMoreElements ()){
      sub = (AbstractSubscriber) listOfSubs.nextElement ();
      subs.addElement (new DataObject(AbstractSubscriber.SUBSCRIBER_ID, sub.getBaseObjectId ()));
    }
    return new DataObject(Subscribers.SUBSCRIBERS, subs);
  }
  
  /**
   * This method returns the desciption specific to a widget.
   * By default, it returns the type of the object that is 'WIDGET' type
   * This method should be overloaded.
   *
   * @return DataObject The DataObject containing the description of the widget
   * @see #getUserDescription()
   * @author Agathe
   */
  public DataObject getWidgetDescription(){
    return null;
  }
  
  /**
   * This method is called when the widget is restarted. This method restarts
   * the subscriptions, that is, the widget creates the subscribers that are
   * described in its logfile. To each identified subscriber, the widget sends a
   * PING message to check their liveliness. The URL sent is WIDGET+SUBSCRIBERS+PING.
   * This PING is done through an independent connection (a thread handles the
   * communication), so the result of the PING is got in the widget.handleIndependentReply.
   *
   * @author Agathe
   */
  protected void setSubscribers(){
    debugprintln(DEBUG, "\n\nWidget <setSubscribers> ");
    // Get the subscribers retrieved from the log file
    Subscribers notCheckedSubs = new Subscribers(this, this.getId ());
    // Check them to be sure they are still alive
    Enumeration list = notCheckedSubs.getSubscribers ();
    AbstractSubscriber temp;
    int i=0;
    while (list.hasMoreElements ()){
      subscribers = notCheckedSubs;
      temp = (AbstractSubscriber) list.nextElement ();
      debugprintln(DEBUG, "widget <setSubs> send a PING for " + temp);
      IndependentCommunication indComm =
      new IndependentCommunication(
      new RequestObject(null, null, temp.getSubscriberHostName (),temp.getSubscriberPort (), temp.getSubscriptionId ()),
      true);
      indComm.setObjectToStore (temp);
      indComm.setSenderClassId (Widget.WIDGET_TYPE+Subscribers.SUBSCRIBERS+BaseObject.PING);
      pingComponent(indComm);
      i ++;
    }
    subscribers = notCheckedSubs;
    debugprintln(DEBUG, "End setSubscriber # subs to check= " + i);
  }
  
  /**
   * This method overrides the handleIndependentReply defined in the BaseObject
   * class.
   *
   * This method handles the reply to : <ul>
   *  <li>PING messages sent to subscriber to check out if they are still alive.
   *  This test is done when the widget is restarted and restarts the subscriptions
   *  based on its logfile. The subscribers are checked
   *  The url is WIDGET+SUBSCRIBERS+PING
   * <li> Notification messages sent to the subscribers. If the communication failed
   * (due to connection errors) the subscriber is removed from the widget and the
   * widget updates the discoverer
   * The url is WIDGET+SUBSCRIPTION_CALLBACK
   *
   * </ul>
   * If the url is not recognized, the message is sent to the
   * BaseObject.handlIndependentReply
   *
   * @param independentCommunication The object sent back by the thread
   * @author Agathe
   */
  
  public void handleIndependentReply(IndependentCommunication independentCommunication){
    debugprintln(DEBUG, "Widget <handleIndependentReply>");
    
    // Reply from the subscribers that are checked with a PING : WIDGET+SUBSCRIBERS+PING
    if (independentCommunication != null){
      String senderId= independentCommunication.getSenderClassId ();
      
      // The reply of a message sent to ping a subscriber from the widget
      if (senderId != null && senderId.equals(Widget.WIDGET_TYPE+Subscribers.SUBSCRIBERS+BaseObject.PING)) {
        independentCommunication.decodeReply (this);
        DataObject replyContent = independentCommunication.getDecodedReply ();
        debugprintln(DEBUG, "\nWidget <handleIndependentReply> Reply=" + replyContent + " - exceptions " + independentCommunication.getExceptions ());
        if (independentCommunication.getRequest ().getUrl ().equals (BaseObject.PING)){
          if ( ! independentCommunication.getExceptions ().isEmpty () // There are exceptions
          || replyContent == null) {
            debugprintln(DEBUG, "Widget <handleIndependentReply> removes subscriber");
            subscribers.removeSubscriber ((AbstractSubscriber)independentCommunication.getObjectToStore ());
            this.discovererUpdate ();
          }
        }
      }
      
      // The reply comes from a subscription notification
      else if (senderId != null && senderId.equals(Widget.WIDGET_TYPE+Subscriber.SUBSCRIPTION_CALLBACK)){
        if ( ! independentCommunication.getExceptions ().isEmpty ()){
          // If there are exception, remove the subscriber corresponding to that notification
          Subscriber sub = (Subscriber) independentCommunication.getObjectToStore ();
          debugprintln (DEBUG, "IndependentCommunication ERROR - remove the subscriber=" + sub);
          subscribers.removeSubscriber (sub);
          this.discovererUpdate ();
        }
      }
      
      // Else, asks to the super class
      else{
        super.handleIndependentReply (independentCommunication);
      }
    }
  }
  
  /** This method overrides the BaseObject setId(String) method so that
   * the baseobject id specified in the subscribers object be also updated.
   * @param id The id of this ctk component
   */
  public void setId(String id) {
    super.setId(id);
    if (this.subscribers != null)
      this.subscribers.setBaseObjectId(id);
  }
  
}