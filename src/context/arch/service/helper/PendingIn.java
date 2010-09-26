package context.arch.service.helper;

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * This class maintains a list of pending service requests, kept by a 
 * component making the service requests.
 *
 * @see context.arch.service.Service
 */
public class PendingIn extends Hashtable {

  /**
   * Basic empty constructor
   */
  public PendingIn() {
    super();
  }

  /**
   * Adds the given request id to the pending container.
   *
   * @param id Request id to make pending
   */
  public void addPending(String id) {
    put(id,id);
  }

  /**
   * Determines whether the given request id is in the pending container
   *
   * @param id Request id to look for
   * @return whether request is pending
   */
  public boolean isPending(String id) {
    return containsKey(id);
  }

  /**
   * Removes the given request id from the pending container
   *
   * @param id Request id to remove
   */
  public void removePending(String id) {
    remove(id);
  }

  /**
   * Returns the number of requests pending in the container
   *
   * return the number of requests pending in the container
   */
  public int numPending() {
    return size();
  }

}
