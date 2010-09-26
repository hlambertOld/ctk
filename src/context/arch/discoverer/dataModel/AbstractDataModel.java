/*
 * AbstractDataModel.java
 *
 * Created on July 2, 2001, 3:21 PM
 */

package context.arch.discoverer.dataModel;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.Enumeration;

/**
 *
 * @author  Agathe
 */
public abstract class AbstractDataModel {

  /**
   * This hashtable contains the IndexTableIF object that are the index tables 
   * used in this data model.
   * key=string=name of the indexTable => value=IndexTableIF
   */
  protected Hashtable nameToIndexTableIF; 
  
  private int numberOfElement = 0;
    
  /**
   *
   */
  public AbstractDataModel(){
    nameToIndexTableIF = new Hashtable();
  }
  
  /**
   *
   */
  public IndexTableIF getIndexTableIFCorrespondingTo(String indexName){
    return (IndexTableIF) nameToIndexTableIF.get (indexName);
  }
    
  /**
   *
   */
  public abstract Object add(Object object);

  /**
   *
   */
  public abstract Object update(Object object);
  
  /**
   *
   */
  public abstract Object remove(Object object);
  
  /**
   *
   */
  public abstract Object getIndexOf(Object object);
  
  /**
   *
   */
  public abstract Object getObjectAt(Object object);
  
  /**
   *
   */
  public abstract Enumeration getIndexKeys();
  
  /**
   *
   */
  public abstract String toString();
  
  /**
   *
   */
  public int getNumberOfElements(){
    return this.numberOfElement;
  }
  
  /**
   *
   */
  public abstract Object getEmptyArray();
  
  public void incNbElements(){
    this.numberOfElement ++;
  }
  
  public void decNbElements(){
    this.numberOfElement --;
  }
  
  public int getNbElements(){
    return this.numberOfElement;
  }
}
