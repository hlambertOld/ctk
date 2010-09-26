/*
 * LeaseWatcher.java
 *
 * Created on May 25, 2001, 2:03 PM
 */

package context.arch.discoverer.lease;

import context.arch.discoverer.Discoverer;

import java.util.Timer;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Calendar;

/**
 * This class is used to watch for all context components leases
 * each xx time, as defined by Lease.TIME_SLOT_MILLIS, and then send
 * to the LeasesKeeper object an
 * Enumeration of all component index whose lease expires.
 *
 * @author  Agathe
 */
public class LeasesWatcher {
  
  /**
   * The timer object that waits for Lease.TIME_SLOT_MILLIS minutes to start
   * the leases watching
   *
   * @see context.arch.discoverer.lease.Lease#TIME_SLOT_MILLIS
   */
  private Timer timer;
  
  /**
   * The LeasesKeeper object
   */
  protected LeasesKeeper keeper;
  
  /**
   * The vector containing the lease objects
   */
  protected Vector leases;
  
  
  
  
  /**
   *
   */
  protected void watchLeases(){
    System.out.println("\n\n\n\n-----LeasesWatcher <watchLeases> The timer has expired time= " + Calendar.getInstance().getTime());
    if (leases != null) {
      // Take the current date
      Calendar currentDate = Calendar.getInstance();
      // The result vector that will contain  index of the component to which
      // the discoverer has to send a lease end notification message
      ArrayList result = new ArrayList();
      
      // Checks all leases, if the lease expires, add the context component index to the vector
      Enumeration list = leases.elements();
      while (list.hasMoreElements()){
        Lease l = (Lease) list.nextElement();
        //Tests if the lease corresponds to an existing component description
        if (keeper.existingComponentDescription(l.getComponentIndex())){
          // this commented code is the original: we don't check the components before the end of the lease
          /*Calendar date = l.getEndDate();
          if (date.before (currentDate)){
            // Adds the index
            result.add (l.getComponentIndex());
          }*/
          Calendar date = l.getEndDate();
          // Adds the index
          result.add(l.getComponentIndex());
        }
      }
      if (! result.isEmpty()){
        // Sends the Enumeration of leases terms to the LeasesKeeper
        keeper.leaseEndNotificationTo(result);
      }
    }
  }
  
  /**
   * This method allows to copy the current leases into the object the
   * LeasesWatcher will watch.
   *
   * @param currentLeases The vector object containing the leases
   */
  synchronized protected void putLeases(Hashtable currentLeases){
    //System.out.println("LeasesWatcher <putLeases> " + currentLeases);
    leases = null;
    // Does a copy of the object
    if (currentLeases != null) {
      leases = new Vector(currentLeases.values());
    }
  }
  
}// class end
