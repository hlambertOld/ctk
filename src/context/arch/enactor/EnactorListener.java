package context.arch.enactor;

import context.arch.discoverer.ComponentDescription;
import context.arch.storage.Attributes;


/**
 * A listener class for enactors. Any listener registered with an enactor gets
 * called whenever the relevent events occur with that enactor. Listeners can listen
 * to many enactors; the particular enactor responsible for calling an enactor can be
 * retrieved through EnactorReference or EnactorParameter arguments.
 * 
 * EnactorListeners currently do not receive componentAdded calls for all components for
 * which they might receive componentEvaluated calls, if they are added to an enactor after
 * a particular component was added. This may change if/when stricter semantics guaranteeing adds
 * before evaluations is implemented.
 * TODO: modify this comment when semantics are modified 
 * 
 * @author alann
 */
public interface EnactorListener {
  public static final String COMPONENT_EVALUATED = "componentEvaluated";
  
  public static final String COMPONENT_ADDED = "componentAdded";
  
  public static final String COMPONENT_REMOVED = "componentRemoved";
  
  public static final String PARAMETER_VALUE_CHANGED = "parameterValueChanged";
  
  public static final String ACTION_TRIGGERED = "actionTriggered";
  
  /**
   * called whenever a component is evaluated by an enactor. This typically occurs
   * when a component (that matches the description query of a reference) sends a
   * new CTK Callback. 
   * 
   * @param rwr the reference that evaluated the component. 
   * @param widgetDescription
   */
  public void componentEvaluated(EnactorReference rwr, ComponentDescription widgetDescription);

  /**
   * called when a new CTK component matches an enactor reference. If the enactor establishes any
   * parameter-related attributes for this component, they are passed along as well. 
   * 
   * TODO: make parameter attribute handling more clear, consistent
   * 
   * @param rwr the relevant enactor reference
   * @param widgetDescription the description of the added component
   * @param paramAtts initial values for any enactor parameters for this component
   */
  public void componentAdded(EnactorReference rwr, ComponentDescription widgetDescription, Attributes paramAtts);
  
  /**
   * called when a CTK component no longer matches an enactor reference.
   * 
   * TODO: make parameter attribute handling more clear, consistent
   * 
   * @param rwr the relevant enactor reference
   * @param widgetDescription the description of the removed component
   * @param paramAtts any enactor parameter values of the component
   */
  public void componentRemoved(EnactorReference rwr, ComponentDescription widgetDescription, Attributes paramAtts);
  
  /**
   * called when the value of an enactor parameter changes. A parameter change may occur relative to
   * particular attributes; those attributes are passed along.
   * 
   * TODO: modify after fixing attribute handling
   * 
   * @param parameter the enactor parameter
   * @param validAtts attributes for which the change occurs
   * @param value the new value of the changed parameter
   */
  public void parameterValueChanged(EnactorParameter parameter, Attributes validAtts, Object value);
}
