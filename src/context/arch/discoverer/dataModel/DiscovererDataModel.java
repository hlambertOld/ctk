/*
 * DiscovererDataModel.java
 *
 * Created on July 2, 2001, 3:27 PM
 */

package context.arch.discoverer.dataModel;

import java.util.Enumeration;
import java.util.Hashtable;

import context.arch.discoverer.ComponentDescription;
import context.arch.discoverer.componentDescription.CallbackElement;
import context.arch.discoverer.componentDescription.ClassnameElement;
import context.arch.discoverer.componentDescription.ConstantAttributeElement;
import context.arch.discoverer.componentDescription.HostnameElement;
import context.arch.discoverer.componentDescription.IdElement;
import context.arch.discoverer.componentDescription.InAttributeElement;
import context.arch.discoverer.componentDescription.NonConstantAttributeElement;
import context.arch.discoverer.componentDescription.OutAttributeElement;
import context.arch.discoverer.componentDescription.PortElement;
import context.arch.discoverer.componentDescription.ServiceElement;
import context.arch.discoverer.componentDescription.SubscriberElement;
import context.arch.discoverer.componentDescription.TypeElement;


/**
 * This class stores the ComponentDescriptions of the CTK object
 * that register to the discoverer.
 * It stores the complete ComponentDescription in a Hashtable and 
 * maintain a set of IndexTableIF that allows to search more quickly for
 * particular CTK objects. They are index on each of the description element.
 *
 * @author  Agathe
 */
public class DiscovererDataModel extends AbstractDataModel {

  
  public final String ID_INDEX = IdElement.ID_ELEMENT;
  public final String CLASSNAME_INDEX = ClassnameElement.CLASSNAME_ELEMENT;
  public final String HOSTNAME_INDEX = HostnameElement.HOSTNAME_ELEMENT;
  public final String TYPE_INDEX = TypeElement.TYPE_ELEMENT;
  public final String PORT_INDEX = PortElement.PORT_ELEMENT;
  //public final String LOCATION_INDEX = "location";
  // constant attributes
  public final String CST_ATT_NAME_VALUE_INDEX = ConstantAttributeElement.CONST_ATT_NAME_VALUE_ELEMENT;   // name+value
  public final String CST_ATT_NAME_INDEX = ConstantAttributeElement.CONST_ATT_NAME_ELEMENT;   //name
  public final String CST_ATT_VALUE_INDEX = ConstantAttributeElement.CONST_ATT_VALUE_ELEMENT;   //value
  // non constant attributes
  public final String NON_CST_ATT_INDEX = NonConstantAttributeElement.NON_CONST_ATT_NAME_ELEMENT;
  public final String CALLBACK_INDEX = CallbackElement.CALLBACK_ELEMENT;
  public final String SERVICE_INDEX = ServiceElement.SERVICE_ELEMENT;
  public final String SUBSCRIBER_INDEX = SubscriberElement.SUBSCRIBER_ELEMENT;
  public final String IN_ATT_INDEX = InAttributeElement.IN_ATT_ELEMENT;
  public final String OUT_ATT_INDEX = OutAttributeElement.OUT_ATT_ELEMENT;
  
  /** Stores the whole ComponentDescription objects 
   * key = Integer(index) => value=ComponentDescription */
  private Hashtable components;
  
  /** Field inherited from AbstractDataModel : stores the Hashtable */
  // protected HashMap nameToIndexTableIF;
  
  /**
   * The next index that is used to reference the next ComponentDescription to store
   */
  private int nextIndex = 0;
  
  /** 
   * Creates new DiscovererDataModel 
   */
  public DiscovererDataModel () {
    super();
    components = new Hashtable();
    
    // nameToIndexTableIF is inherited from the AbstractDataModel class
    // Here a put the index tables : the key is the name of the table
    nameToIndexTableIF.put(ID_INDEX, new IdIndexTable(ID_INDEX));
    
    nameToIndexTableIF.put(CLASSNAME_INDEX, new ClassnameIndexTable(CLASSNAME_INDEX));  //classname
    nameToIndexTableIF.put(HOSTNAME_INDEX, new HostnameIndexTable(HOSTNAME_INDEX));     // hostname
    nameToIndexTableIF.put(TYPE_INDEX, new TypeIndexTable(TYPE_INDEX));                 //type
    nameToIndexTableIF.put(PORT_INDEX, new PortIndexTable(PORT_INDEX));                 // port
    nameToIndexTableIF.put(CST_ATT_NAME_VALUE_INDEX, new CstAttributeIndexTable(CST_ATT_NAME_VALUE_INDEX));
    nameToIndexTableIF.put(CST_ATT_NAME_INDEX, new CstAttributeNameIndexTable(CST_ATT_NAME_INDEX));
    nameToIndexTableIF.put(CST_ATT_VALUE_INDEX, new CstAttributeValueIndexTable(CST_ATT_VALUE_INDEX));
    nameToIndexTableIF.put(NON_CST_ATT_INDEX, new NonCstAttributeIndexTable(NON_CST_ATT_INDEX));
    nameToIndexTableIF.put(CALLBACK_INDEX, new CallbackIndexTable(CALLBACK_INDEX));
    nameToIndexTableIF.put(SERVICE_INDEX, new ServiceIndexTable(SERVICE_INDEX));
    nameToIndexTableIF.put(SUBSCRIBER_INDEX, new SubscriberIndexTable(SUBSCRIBER_INDEX));
    nameToIndexTableIF.put(IN_ATT_INDEX, new InAttributeIndexTable(IN_ATT_INDEX));
    nameToIndexTableIF.put(OUT_ATT_INDEX, new OutAttributeIndexTable(OUT_ATT_INDEX));
    //nameToIndexTableIF.put(LOCATION_INDEX, new LocationIndexTable(LOCATION_INDEX));
    
  }

  /**
   * Adds the ComponentDescription object to the list of components, and 
   * updates the index tables based on the component description.
   *
   * @param componentDescription ComponentDescription object
   * @return Integer
   */
  public Object add(Object componentDescription){
    int index = this.nextIndex;
    Integer intIndex = new Integer(index);
    add(componentDescription, intIndex);
    this.nextIndex ++;
    return intIndex;
  }

  /**
   * This method adds a component into the general table containing all components,
   * and updates all index tables with this element.
   *
   * @param componentDescription The component to add
   * @param index The index used to index this component
   * @return The Integer object corresponding to the index of the added component; return null if there is an error
   */
  private Object add(Object componentDescription, Object index){
    ComponentDescription comp = (ComponentDescription) componentDescription;
    if (comp != null) {
      components.put((Integer)index, comp);
      addToIndexTableIF (comp,(Integer) index);
      incNbElements();
      return index;
    }
    else 
      return null;
  }
  
  /**
   * Update all hashtable (general and index tables) to add a new component
   *
   * @param componentDescription The new component to add
   * @return the Integer corresponding to the index of the component
   */
  public Object update(Object componentDescription){
    ComponentDescription comp = (ComponentDescription) componentDescription;
    Integer index = (Integer) getIndexOf (comp.id);
    remove (index);
    add(comp, index);
    return index;
  }
  
  
  /**
   * Return the index corresponding to the id of a component
   *
   * @param string String
   * @return Integer
   */
  public Object getIndexOf(Object stringOrInteger){
    if (stringOrInteger instanceof String)
      return (Integer)((IndexTableIF)nameToIndexTableIF.get (ID_INDEX)).get(((String)stringOrInteger).toLowerCase());
    else if (stringOrInteger instanceof Integer && (components.get ((Integer)stringOrInteger) != null))
      return stringOrInteger;
    return null;
  }
  
  /**
   *
   * @param integer Integer
   * @return Object (ComponentDescription)
   */
  public Object getObjectAt(Object stringOrInteger){
    Integer index = null;
    if (stringOrInteger instanceof String){
      index = (Integer) getIndexOf (stringOrInteger);
    }
    else if (stringOrInteger instanceof Integer){
      index = (Integer) stringOrInteger;
    }
    return components.get (index);
  }
  
  /**
   *
   */
  public Enumeration getIndexKeys(){
    return components.keys ();
  }

  
  /**
   * 
   * @param integer Integer
   * @return Object The removed object (ComponentDescription)
   */
  public Object remove (Object objectIndexOrId){
    Integer index = null;
    if (objectIndexOrId instanceof Integer) {
      index = (Integer) objectIndexOrId;
    }
    else if (objectIndexOrId instanceof String){
      index = (Integer) getIndexOf (objectIndexOrId);
    }
    if (index != null){
      // remove from the all component descriptions
      ComponentDescription removed = (ComponentDescription) components.remove (index);
      // remove from the index tables
      removeFromIndexTableIF(removed, index);
      decNbElements();
      return removed;
    }
    return null;
  
  }
  
  /**
   *
   */
  public void addToIndexTableIF(ComponentDescription comp, Integer index){
    // Get the list of tables
    Enumeration listOfTables = nameToIndexTableIF.elements();
    IndexTableIF table = null;
    while (listOfTables.hasMoreElements ()){
      table = (IndexTableIF) listOfTables.nextElement ();
      table.add(comp, index);
    }
  }
  
  /**
   * This method allows to remove the reference to a component description 
   * from the IndexTableIF tables.
   * 
   */
  public void removeFromIndexTableIF(ComponentDescription comp, Integer index){
    // Get the list of tables
    Enumeration listOfTables = nameToIndexTableIF.elements();
    IndexTableIF table = null;
    while (listOfTables.hasMoreElements ()){
      table = (IndexTableIF) listOfTables.nextElement ();
      table.removeKey(comp, index);
    }
  }
  
  /**
   *
   */
  public String toString(){
    StringBuffer sb = new StringBuffer("DiscovererDataModel - ");
    sb.append ("Nb Elements=" + this.getNumberOfElements() + "\n\n");
    sb.append ("\n" + nameToIndexTableIF.get (ID_INDEX));
    sb.append ("\n\n" + nameToIndexTableIF.get ( CLASSNAME_INDEX));
    sb.append ("\n" + nameToIndexTableIF.get (HOSTNAME_INDEX ));
    sb.append ("\n" + nameToIndexTableIF.get ( TYPE_INDEX));
    sb.append ("\n" + nameToIndexTableIF.get ( PORT_INDEX));
    sb.append ("\n" + nameToIndexTableIF.get (CST_ATT_NAME_VALUE_INDEX));
    sb.append ("\n" + nameToIndexTableIF.get (CST_ATT_NAME_INDEX ));
    sb.append ("\n" + nameToIndexTableIF.get ( CST_ATT_VALUE_INDEX));
    sb.append ("\n" + nameToIndexTableIF.get (NON_CST_ATT_INDEX ));
    sb.append ("\n" + nameToIndexTableIF.get (CALLBACK_INDEX ));
    sb.append ("\n" + nameToIndexTableIF.get (SUBSCRIBER_INDEX ));
    sb.append ("\n" + nameToIndexTableIF.get (IN_ATT_INDEX ));
    sb.append ("\n" + nameToIndexTableIF.get (OUT_ATT_INDEX ));
    
    return sb.toString ();
  }
  
  /**
   * Returns a empty hashmap that contains as key the index of the currently
   * stored components, and as value, false.
   *
   * @return HashMap
   */
  public Object getEmptyArray(){
    int size = components.size ();
    // THAT
    //long l1 = System.currentTimeMillis();
    
    int [][] array = new int[size][2];
    Enumeration keys = components.keys ();
    for (int i = 0 ; i < size ; i ++ ){
      array[i][0] = ((Integer)keys.nextElement ()).intValue();
      array[i][1] = 0;
    }
    //long l2 = System.currentTimeMillis();
    return array;
    
    /*// OR
    if (size <1 ) size = 1;
    HashMap hash = new HashMap(size);
    Enumeration list2 = components.keys ();
    while (list2.hasMoreElements ()){
      hash.put (list2.nextElement (), new Boolean(false));
    }
    
    long l3 = System.currentTimeMillis();
    System.out.println("l1= " + l1 + " l2= " + l2 + " l3= " + l3); 
    System.out.println("first " + (l2-l1));
    System.out.println("second " + (l3-l2));
    
    return hash;
     */
  }
  

  
  public static void main (String args[]){
    AbstractDataModel data = new DiscovererDataModel();
    ComponentDescription comp = new ComponentDescription();
    comp.id = "agathe";
    comp.port = 1524;
    comp.classname = "battestini";
    comp.hostname = "orleans";
    comp.hostaddress = "120.32.36.39";
    comp.location = "AH";
    comp.type = "girl";
    
    System.out.println("comp = " + comp);
    
    data.add(comp);
    for (int i = 0 ; i < 100 ; i ++){
      data.add(comp);
    }
    
    
    System.out.println(data.toString());
  }
}// class end

