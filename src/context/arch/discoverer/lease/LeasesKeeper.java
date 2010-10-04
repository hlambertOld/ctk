/*
 * LeasesKeeper.java
 *
 * Created on May 30, 2001, 10:53 AM
 */

package context.arch.discoverer.lease;

import context.arch.discoverer.DiscovererMediator;

import java.util.Timer;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;

/**
 * A component registers to the discoverer for a given period of time that is
 * defined with a lease.
 * That allows the discovery system to make sure a component is still available.
 * This class keeps all information about the context components leases that
 * are registered.
 * It contains a LeasesWatcher object that watches the end of leases each 
 * xx minutes as defined in the Lease class by
 * the constant Lease.TIME_SLOT_MILLIS.
 * If the LeaseKeeper detects the end of a lease, it sends it to the discoverer
 * that will send a checking message to the component. 
 *
 * @author  Agathe
 * @see context.arch.discoverer.lease.LeasesWatcher
 * @see context.arch.discoverer.Discoverer
 */
public class LeasesKeeper {

  /**
   * The discoverer object
   */
  protected DiscovererMediator mediator;

  /**
   * All leases, associates the component (Integer)index -> Lease object
   */
  protected Hashtable leases;
  
  /**
   * The watcher that triggers the leases examination
   */
  protected LeasesWatcher watcher;
  
  /** 
   * Creates new LeasesKeeper
   *
   * TO COMPLETE (if the discoverer restarts from a log file)
   *
   * @param discoverer The Discoverer object
   */
  public LeasesKeeper (DiscovererMediator mediator) {
    if (mediator != null){
      this.mediator = mediator;
    }
    watcher = new LeasesWatcher(this);
    leases = new Hashtable();
  }
  
  /**
   * Adds a Lease object and update the LeaseWatcher object
   *
   * @param lease The Lease object
   */
  public void addLease(Lease lease){
    lease.setStartDate(); 
    leases.put (lease.getComponentIndex (), lease);
    watcher.putLeases (leases);
  }

  /**
   * This method allows to send a list of leases that should end to the 
   * discoverer.
   *
   * @param listOfLeaseEnd The leases to send to the discoverer
   */
  public void leaseEndNotificationTo(ArrayList listOfLeaseEnd){
    //System.out.println("LeasesKeeper <leaseEndNotification> - lease=" + listOfLeaseEnd);
    mediator.sendLeaseEndNotificationTo(listOfLeaseEnd);
    //System.out.println("has sent it to the disco");
}

  /**
   * This method allows to remove a Lease corresponding to the index
   * 
   * @param indexToRemove The index of the ComponentDescription for which we
   * want to remove the lease
   * @return Lease The removed Lease object
   */
  public synchronized Lease removeLease(Integer indexToRemove){
    System.out.println("LeaseKeeper <removeLease>");
    Integer result = null;
    Lease l = null;
    if ((result = contains(indexToRemove)) != null){
      l = (Lease) leases.remove (result);
      System.out.println("LeasesKeeper <removeLease> new leases ="+leases);
      watcher.putLeases (leases);
    }
    return l;
  }
  
  /**
   * This method tests if an index of a ComponentDescription exists
   *
   * @param componentIndex The Integer to test
   * @return boolean True if this object contains componentIndex
   */
  public synchronized Integer contains(Integer componentIndex){
    Enumeration list = leases.keys ();
    while (list.hasMoreElements ()){
      Integer ind = (Integer) list.nextElement ();
      if (ind.equals (componentIndex)){
        return ind;
      }
    }
    return null;
  }
  
  /**
   * Returns a printable version of this object
   *
   * @return String
   */
  public String toString(){
    StringBuffer s = new StringBuffer("Leases :");
    if (! leases.isEmpty ()) {
      Enumeration list = leases.keys ();
      while (list.hasMoreElements ()) {
        Integer index = (Integer) list.nextElement ();
        Lease l = (Lease) leases.get (index);
        s.append ("\nIndex=" + index + " => " + l);
      }
    }
    return s.toString ();
  }
  
  /**
   * This method allows to renew an exiting lease
   *
   * @param index The index of the lease
   * @param renewal The new lease
   * @return boolean True if the lease has been updated for the given index
   */
  public synchronized boolean renewLease (Lease renewal){
    Object o = leases.remove (renewal.getComponentIndex ());
    if (o != null) {
      renewal.setStartDate ();
      leases.put (renewal.getComponentIndex (), renewal);
      watcher.putLeases (leases);
      return true;
    }
    // Return false cause lease not found
    return false;  
  }
  
  /**
   * Tests if an index corresponds to an existing ComponentDescription in the
   * discoverer
   *
   * @param componentDescriptionIndex The index of the object to test
   * @return boolean True if there is an existing component description with
   * this index in the discoverer
   */
  public boolean existingComponentDescription (Integer componentDescriptionIndex){
    return mediator.exists (componentDescriptionIndex);
  }
  
} // class end
