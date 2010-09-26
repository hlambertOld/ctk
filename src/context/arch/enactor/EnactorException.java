package context.arch.enactor;

/**
 * General Exception for Enactor package.
 * 
 * @author alann
 */
public class EnactorException extends Exception {
  public EnactorException() {}
  
  public EnactorException(String message) {
    super(message);
  }

  
  public EnactorException(String message, Throwable t) {
    super(message, t);
  }
}
